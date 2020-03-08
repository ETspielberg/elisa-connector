package org.unidue.ub.libintel.elisaconnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.unidue.ub.libintel.elisaconnector.client.SubjectClient;
import org.unidue.ub.libintel.elisaconnector.exceptions.AlreadyContainedException;
import org.unidue.ub.libintel.elisaconnector.exceptions.ElisaAuthenticationException;
import org.unidue.ub.libintel.elisaconnector.exceptions.InvalidIsbnException;
import org.unidue.ub.libintel.elisaconnector.exceptions.MissingElisaAccountException;
import org.unidue.ub.libintel.elisaconnector.model.*;
import org.unidue.ub.libintel.elisaconnector.service.*;

/**
 * provides endpoints to wrap the ELi:SA API.
 */
@Controller
public class ElisaController {

    private final Logger log = LoggerFactory.getLogger(ElisaController.class);

    // the standard email address to send mails if elisa submission does not work
    @Value("${libintel.eavs.email.default}")
    private String defaultEavEmail;

    private final MailSenderService mailSenderService;

    private final ElisaService elisaService;

    private final SubjectClient subjectClient;

    private final ElisaAccountService elisaAccountService;

    private final RequestValidatorService requestValidatorService;

    /**
     * constructor injection of beans
     *
     * @param mailSenderService       responsible for composing and sending the emails
     * @param subjectClient           the subject Feign client
     * @param requestValidatorService validates the request and checks for correct ISBNs
     * @param elisaAccountService     retrieves the appropriate account
     * @param elisaService            connects to elisa and submits the item list
     */
    @Autowired
    public ElisaController(SubjectClient subjectClient,
                           ElisaService elisaService,
                           MailSenderService mailSenderService,
                           RequestValidatorService requestValidatorService,
                           ElisaAccountService elisaAccountService) {
        this.subjectClient = subjectClient;
        this.mailSenderService = mailSenderService;
        this.elisaService = elisaService;
        this.requestValidatorService = requestValidatorService;
        this.elisaAccountService = elisaAccountService;
    }

    /**
     * endppoint to receive the general data from the web form as json object.
     *
     * @param requestDataLecturer the submission from the form (JSON-encoded) with the fields applicable to lecturers
     * @return a status message
     */
    @PostMapping("/receiveEavLecturer")
    public ResponseEntity<?> receiveEavLecturer(@RequestBody RequestDataLecturer requestDataLecturer) {
        return receiveEav(requestDataLecturer);
    }

    /**
     * endppoint to receive the general data from the web form as json object.
     *
     * @param requestDataUser the submission from the form (JSON-encoded) with the fields applicable to users
     * @return a status message
     */
    @PostMapping("/receiveEavUser")
    public ResponseEntity<?> receiveEavUser(@RequestBody RequestDataUser requestDataUser) {
        return receiveEav(requestDataUser);
    }

    /**
     * endppoint to receive the general data from the web form as json object.
     *
     * @param requestData the submission from the form (JSON-encoded)
     * @return a status message
     */
    @PostMapping("/receiveEav")
    public ResponseEntity<?> receiveEav(@RequestBody RequestData requestData) {
        // validate the request
        String requestValidation = requestValidatorService.validate(requestData);
        log.info(requestValidation);

        // if no subject is given, send the default email
        if (requestValidation.equals("no.subjectarea")) {
            requestData.subjectarea = "keine Angabe";
            mailSenderService.sendEavMail(requestData, defaultEavEmail, "Es wurde kein Fach angegeben");
            if (requestData.isbn.isEmpty())
                log.info("subject: 'no subject given', isbn: 'no isbn given', action: 'default email sent', elisa: 'not possible'");
            else
                log.info("subject: 'no subject given', isbn: " + requestData.isbn + ", action: 'default email sent', elisa: 'not possible'");
            return ResponseEntity.ok().body("Please provide a subject");
        } else {
            // if a subject code is given, store the code in an intermediate variable, retrieve the notation group from
            //  the repository, and replace the subject code by the speaking name.
            String notationgroupname = requestData.subjectarea;
            Notationgroup notationgroup;
            try {
                notationgroup = subjectClient.getNotationgroupById(notationgroupname);
                requestData.subjectarea = notationgroup.description;
            } catch (Exception e) {
                log.debug("could not obtain subject description", e);
            }

            // if no isbn is given send the default mail with the speaking name of the subject to default address
            if (requestValidation.equals("no.isbn") || requestValidation.equals("invalid.isbn")) {
                log.debug("no isbn given");
                mailSenderService.sendEavMail(requestData, defaultEavEmail, "Es wurde keine ISBN angegeben");
                log.info("subject: '" + requestData.subjectarea + "', isbn: 'no isbn given', action: 'default email sent', elisa: 'no account available'");
                return ResponseEntity.ok().body("Please provide an ISBN");
            } else {
                log.debug("received Request to send ISBN " + requestData.isbn + "to ELi:SA");

                // process requests with isbns and subject areas present
                // remove hyphens if present
                if (requestData.isbn.contains("-"))
                    requestData.isbn = requestData.isbn.replace("-", "");
                // retrieve the active Elisa account for the subject
                ElisaData elisaData;
                try {
                    elisaData = this.elisaAccountService.getActiveElisaDataForSubject(notationgroupname);
                } catch (MissingElisaAccountException meae) {
                    mailSenderService.sendEavMail(requestData, defaultEavEmail, "Zu diesem Fach konnte kein ELi:SA Account gefunden werden.");
                    log.info("subject: '" + requestData.subjectarea + "', isbn: '" + requestData.isbn + "', action: 'default email sent', elisa: 'no account available'");
                    log.debug("could not retrieve the userID for subjectArea " + notationgroupname);
                    return ResponseEntity.ok().body("could not retreive Elisa account id");
                } catch (Exception e) {
                    mailSenderService.sendEavMail(requestData, defaultEavEmail, "Der zuständige ELi:SA Account konnte nicht gefunden werden");
                    log.debug("could not retrieve the userID for subjectArea " + notationgroupname);
                    log.debug("the following error occurred: ", e);
                    log.info("subject: '" + requestData.subjectarea + "', isbn: '" + requestData.isbn + "', action: 'default email sent', elisa: 'no account available'");
                    return ResponseEntity.ok().body("could not retreive Elisa account id");
                }
                String userID = elisaData.getElisaUserId();
                if (userID == null || "".equals(userID)) {
                    log.debug("found no user id for subject area " + notationgroupname);
                    mailSenderService.sendEavMail(requestData, defaultEavEmail, "Der zuständige ELi:SA Account konnte nicht gefunden werden");
                    log.info("subject: '" + requestData.subjectarea + "', isbn: '" + requestData.isbn + "', action: 'default email sent', elisa: 'elisa accounts incomplete'");
                    return ResponseEntity.ok().body("could not retreive Elisa account id");
                }
                log.debug("setting elisa id to " + userID);
                // prepare isbns for sending to elisa
                CreateListRequest createListRequest = new CreateListRequest(userID, "Anschaffungsvorschlag");

                // build the title data (isbn, note, and note intern) from the request data)
                TitleData requestedTitle = ElisaTitleDataBuilder.fromRequestData(requestData);

                // prepare the creation request
                createListRequest.addTitle(new Title(requestedTitle));

                try {
                    boolean successful = elisaService.sendToElisa(createListRequest);
                    if (successful) {
                        log.info("subject: '" + requestData.subjectarea + "', isbn: '" + requestData.isbn + "', action: 'submitted to elisa', elisa: 'success'");
                        mailSenderService.sendNotificationMail(requestData, elisaData.getElisaUserId(), elisaData.getElisaName());
                        return ResponseEntity.ok().build();
                    }
                    else {
                        log.info("subject: '" + requestData.subjectarea + "', isbn: '" + requestData.isbn + "', action: 'eav mail sent', elisa: 'title not in elisa'");
                        mailSenderService.sendEavMail(requestData, defaultEavEmail, "Der Titel ist nicht in ELi:SA enthalten.");
                        return ResponseEntity.badRequest().build();
                    }

                } catch (AlreadyContainedException ace) {
                    log.debug("title already on list.");
                    mailSenderService.sendAlreadyContainedMail(requestData, userID, elisaData.getElisaName());
                    log.info("subject: '" + requestData.subjectarea + "', isbn: '" + requestData.isbn + "', action: 'already contained email sent', elisa: 'already on list'");
                    return ResponseEntity.ok().body("title already on list.");
                } catch (ElisaAuthenticationException eae) {
                    log.error("elisa authentication failed. Reason: " + eae.getMessage());
                    mailSenderService.sendEavMail(requestData, defaultEavEmail, "Die ELi:SA -Authentifizierung ist fehlgeschlagen");
                    log.info("subject: '" + requestData.subjectarea + "', isbn: '" + requestData.isbn + "', action: 'default email sent', elisa: 'authentication failed'");
                    return ResponseEntity.ok().body("no token received");
                } catch (InvalidIsbnException iie) {
                    log.error("isbn with errors. Reason: " + iie.getMessage());
                    mailSenderService.sendEavMail(requestData, defaultEavEmail, "Die ISBN konnte nciht in ELi:SA gefunden werden.");
                    log.info("subject: '" + requestData.subjectarea + "', isbn: '" + requestData.isbn + "', action: 'default email sent', elisa: 'isbn with errors'");
                    return ResponseEntity.ok().body("isbn with errors");
                }
            }
        }
    }


    /**
     * direct wrapper for the ELi:SA API. handles authentication etc. credentials remain stored in the secured config server.
     *
     * @param protokollToElisaRequest A container object holding the list of titles to be send to ELi:SA, the notepad name and the elisa user ID
     * @return status message
     */
    @PostMapping("/sendToElisa")
    public ResponseEntity<?> sendToElisa(
            @RequestBody ProtokollToElisaRequest protokollToElisaRequest) {
        CreateListRequest createListRequest = new CreateListRequest(protokollToElisaRequest.getUserID(), protokollToElisaRequest.getNotepadName());
        boolean successful = elisaService.sendToElisa(createListRequest);
        if (successful)
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.badRequest().build();

    }
}
