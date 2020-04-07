package org.unidue.ub.libintel.elisaconnector.controller;

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
import org.unidue.ub.libintel.elisaconnector.model.elisa.CreateListRequest;
import org.unidue.ub.libintel.elisaconnector.model.elisa.Title;
import org.unidue.ub.libintel.elisaconnector.model.elisa.TitleData;
import org.unidue.ub.libintel.elisaconnector.service.*;

import static org.unidue.ub.libintel.elisaconnector.service.LogService.logElisa;

/**
 * provides endpoints to wrap the ELi:SA API.
 */
@Controller
public class ElisaController {

    private final Logger log = LoggerFactory.getLogger(ElisaController.class);

    // the standard email address to send mails if elisa submission does not work
    @Value("${libintel.eavs.email.default}")
    private String defaultEavEmail;

    // the email address to send mails when electronic media arte requested
    @Value("${libintel.ebooks.email.default}")
    private String defaultEbookEmail;

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
        log.debug(requestValidation);

        String requestType = requestData.getClass().getSimpleName();
        if (requestType.contains("RequestData"))
            requestType = requestType.replace("RequestData", "");

        log.debug("ebook desired: " + requestData.ebookDesired);

        // if no subject is given, send the default email
        if (requestValidation.equals("no.subjectarea")) {
            // set subjectarea to 'keine Angabe' for the mail generation
            requestData.subjectarea = "keine Angabe";

            // send either ebook mail or print mail
            if (requestData.ebookDesired)
                mailSenderService.sendEbookMail(requestData, defaultEbookEmail);
            else
                mailSenderService.sendEavMail(requestData, defaultEavEmail, "Es wurde kein Fach angegeben");

            // prepare strings for log message
            String action = requestData.ebookDesired ? "default ebook email sent" : "default email sent";
            String isbnMessage = requestData.isbn.isEmpty() ? "no isbn given" : requestData.isbn;

            // write log message for statistical purposes
            logElisa("no subject given", isbnMessage, action, "not possible", requestType);
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
                String action = requestData.ebookDesired ? "default ebook email sent" : "default email sent";
                if (requestData.ebookDesired)
                    mailSenderService.sendEbookMail(requestData, defaultEbookEmail);
                else
                    mailSenderService.sendEavMail(requestData, defaultEavEmail, "Es wurde keine ISBN angegeben");
                logElisa(requestData.subjectarea, "no isbn given", action, "not possible", requestType);
                return ResponseEntity.ok().body("Please provide an ISBN");
            } else {
                if (requestData.ebookDesired) {
                    log.debug("received Request to obtain ISBN " + requestData.isbn + "as ebook");
                    mailSenderService.sendEbookMail(requestData, defaultEbookEmail);
                    logElisa(requestData.subjectarea, requestData.isbn, "ebook mail sent", "not applicable", requestType);
                    return ResponseEntity.ok().body("ebook mail sent");
                } else
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
                    logElisa(requestData.subjectarea, requestData.isbn, "default email sent", "no account available", requestType);
                    log.debug("could not retrieve the userID for subjectArea " + notationgroupname);
                    return ResponseEntity.ok().body("could not retreive Elisa account id");
                } catch (Exception e) {
                    mailSenderService.sendEavMail(requestData, defaultEavEmail, "Der zuständige ELi:SA Account konnte nicht gefunden werden");
                    log.debug("could not retrieve the userID for subjectArea " + notationgroupname);
                    log.debug("the following error occurred: ", e);
                    logElisa(requestData.subjectarea, requestData.isbn, "default email sent", "no account available", requestType);
                    return ResponseEntity.ok().body("could not retreive Elisa account id");
                }
                String userID = elisaData.getElisaUserId();
                if (userID == null || "".equals(userID)) {
                    log.debug("found no user id for subject area " + notationgroupname);
                    mailSenderService.sendEavMail(requestData, defaultEavEmail, "Der zuständige ELi:SA Account konnte nicht gefunden werden");
                    logElisa(requestData.subjectarea, requestData.isbn, "default email sent", "elisa account incomplete", requestType);
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
                        logElisa(requestData.subjectarea, requestData.isbn, "submitted to elisa", "success", requestType);
                        mailSenderService.sendNotificationMail(requestData, elisaData.getElisaUserId(), elisaData.getElisaName());
                        return ResponseEntity.ok().build();
                    }
                    else {
                        logElisa(requestData.subjectarea, requestData.isbn, "eav mail sent", "title not in elisa", requestType);
                        mailSenderService.sendEavMail(requestData, defaultEavEmail, "Der Titel ist nicht in ELi:SA enthalten.");
                        return ResponseEntity.badRequest().build();
                    }

                } catch (AlreadyContainedException ace) {
                    log.debug("title already on list.");
                    mailSenderService.sendAlreadyContainedMail(requestData, userID, elisaData.getElisaName());
                    logElisa(requestData.subjectarea, requestData.isbn, "already contained email sent", "already on list", requestType);
                    return ResponseEntity.ok().body("title already on list.");
                } catch (ElisaAuthenticationException eae) {
                    log.error("elisa authentication failed. Reason: " + eae.getMessage());
                    mailSenderService.sendEavMail(requestData, defaultEavEmail, "Die ELi:SA -Authentifizierung ist fehlgeschlagen");
                    logElisa(requestData.subjectarea, requestData.isbn, "default email sent", "authentication failed", requestType);
                    return ResponseEntity.ok().body("no token received");
                } catch (InvalidIsbnException iie) {
                    log.error("isbn with errors. Reason: " + iie.getMessage());
                    mailSenderService.sendEavMail(requestData, defaultEavEmail, "Die ISBN konnte nicht in ELi:SA gefunden werden.");
                    logElisa(requestData.subjectarea, requestData.isbn, "default email sent", "isbn with errors", requestType);
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
