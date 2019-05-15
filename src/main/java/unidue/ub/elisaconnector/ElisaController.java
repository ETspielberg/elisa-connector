package unidue.ub.elisaconnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import unidue.ub.elisaconnector.client.ElisaClient;
import unidue.ub.elisaconnector.client.SubjectClient;
import unidue.ub.elisaconnector.model.*;
import unidue.ub.elisaconnector.service.MailContentBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * provides endpoints to wrap the ELi:SA API.
 */
@Controller
public class ElisaController {

    private final Logger log = LoggerFactory.getLogger(ElisaController.class);

    // the caller id assigned by the hbz
    @Value("${libintel.elisa.callerid}")
    private String callerID;

    // the secret provided by the hbz
    @Value("${libintel.elisa.secret}")
    private String secret;

    // the standard email address to send mails if elisa submission does not work
    @Value("${libintel.eavs.email.default}")
    private String defaultEavEmail;

    // the email address to appear as "from"
    @Value("${libintel.eavs.email.from}")
    private String eEavEmailFrom;

    // standard elisa user getting the notepads if no subject librarian can be chosen
    @Value("${libintel.elisa.userid.default}")
    private String defaultElisaUserid;

    private final JavaMailSender emailSender;

    private final ElisaClient elisaClient;

    private final SubjectClient subjectClient;

    private final MailContentBuilder mailContentBuilder;

    /**
     * constructor injection of beans
     *
     * @param elisaClient        the ELi:SA Feign client
     * @param subjectClient      tje subject Feign client
     * @param emailSender        the email sender
     * @param mailContentBuilder the thymeleaf template email builder
     */
    @Autowired
    public ElisaController(ElisaClient elisaClient, SubjectClient subjectClient, JavaMailSender emailSender, MailContentBuilder mailContentBuilder) {
        this.elisaClient = elisaClient;
        this.subjectClient = subjectClient;
        // error on unknown bean for emailSender can be ignored, emailSender bean is created from configuration properties
        this.emailSender = emailSender;
        this.mailContentBuilder = mailContentBuilder;
    }

    /**
     * endppoint to receive the data from the web form as json object.
     *
     * @param requestData the submission from the form (JSON-encoded)
     * @return a status message
     */
    @PostMapping("/receiveEav")
    public ResponseEntity<?> receiveEav(@RequestBody RequestData requestData) {
        // ISBN regular expression test for ISBN
        Pattern patternISBN = Pattern.compile("^(97([89]))?\\d{9}(\\d|X)$");
        String notationgroupname;
        if (requestData.isbn.contains("-"))
            requestData.isbn = requestData.isbn.replace("-", "");
        if (requestData.subjectarea.equals("kA")) {
            requestData.subjectarea = "keine Angabe";
            log.warn("no subject given");
            sendMail(defaultEavEmail, requestData, "Es wurde kein Fach angegeben");
            return ResponseEntity.ok().body("Please provide a subject");
        } else {
            notationgroupname = requestData.subjectarea;
            Notationgroup notationgroup;
            try {
                notationgroup = subjectClient.getNotationgroupById(notationgroupname);
                requestData.subjectarea = notationgroup.description;
            } catch (Exception e) {
                log.warn("could not obtain subject description", e);
            }

        }
        // if it is ISBN, try to upload it to elisa
        if (patternISBN.matcher(requestData.isbn).find()) {
            log.info("received Request to send ISBN " + requestData.isbn + "to ELi:SA");

            // try to get the corresponding user account from the settings backend. If no user id can be obtained, send the email
            ElisaData elisaData;
            try {
                List<ElisaData> allData = subjectClient.getElisaAccountForSubject(notationgroupname);

                if (allData.size() == 0) {
                    sendMail(defaultEavEmail, requestData, "Der zuständige ELi:SA Account konnte nicht gefunden werden");
                    log.warn("could not retrieve the userID for subjectArea " + notationgroupname);
                    return ResponseEntity.ok().body("could not retreive Elisa account id");
                }
                Collections.sort(allData);
                elisaData = determineElisaData(allData);
            } catch (Exception e) {
                sendMail(defaultEavEmail, requestData, "Der zuständige ELi:SA Account konnte nicht gefunden werden");
                log.warn("could not retrieve the userID for subjectArea " + notationgroupname);
                log.warn("the following error occurred: " + e);
                return ResponseEntity.ok().body("could not retreive Elisa account id");
            }
            String userID = elisaData.getElisaUserId();
            if (userID == null || "".equals(userID)) {
                log.info("found no user id for subject area " + notationgroupname);
                sendMail(defaultEavEmail, requestData, "Der zuständige ELi:SA Account konnte nicht gefunden werden");
                return ResponseEntity.ok().body("could not retreive Elisa account id");
            }

            log.info("setting elisa id to " + userID);

            // prepare isbns for sending to elisa
            CreateListRequest createListRequest = new CreateListRequest(userID, "Anschaffungsvorschlag");

            TitleData requestedTitle = new TitleData(requestData.isbn);

            // prepare the intern note with the data about the user and his comments. Add the email for notification.
            String noteIntern = "Vorschlag von " + requestData.name + " (" + requestData.emailAddress + ")";
            if (requestData.response) {
                noteIntern += "\n Bitte den Nutzer über Kaufentscheidung benachrichtigen.";
            }
            if (!"".equals(requestData.comment))
                noteIntern += "\n Kommentar: " + requestData.comment;
            if (!"".equals(requestData.source))
                noteIntern += "\n Literaturangebe von: " + requestData.source;

            requestedTitle.setNotizIntern(noteIntern);

            // prepare the library note
            String note = "";
            if (requestData.essen) {
                note += "E??:1, ";
            }
            if (requestData.duisburg) {
                if (requestData.essen)
                    note += "";
                note += "D??:1,  , ";
            }
            if (requestData.libraryaccountNumber != null) {
                if (!requestData.libraryaccountNumber.isEmpty()) {
                    note += "VM " + requestData.libraryaccountNumber;
                    if (requestData.name != null) {
                        if (!requestData.name.isEmpty())
                            note += " (" + requestData.name + ")";
                    }
                    if (requestData.requestPlace != null) {
                        if (!requestData.requestPlace.isEmpty())
                            note += " in " + requestData.requestPlace;
                    }
                }
            }
            requestedTitle.setNotiz(note);

            // prepare the creation request
            createListRequest.addTitle(new Title(requestedTitle));

            //prepare the authentication request
            AuthenticationRequest authenticationRequest = new AuthenticationRequest(callerID, secret);
            try {
                // perform authentication
                AuthenticationResponse authenticationResponse = elisaClient.getToken(authenticationRequest);
                // if successful try to create list
                if (authenticationResponse.getErrorcode() == 0) {
                    createListRequest.setToken(authenticationResponse.getToken());
                    CreateListResponse createListResponse = elisaClient.createList(createListRequest);
                    log.info("response from elisa: " + createListResponse.getErrorcode() + "(" + createListResponse.getErrorMessage() + ")");
                    if (createListResponse.getErrorcode() == 0) {
                        log.info("successfully created elisa entry");
                        sendNotificationMail(userID, elisaData.getElisaName());
                        return ResponseEntity.ok("List created");
                    } else if (createListResponse.getErrorcode() == 4) {
                        log.info("title already on list.");
                        sendAlreadyContainedMail(userID, requestData, elisaData.getElisaName());
                        return ResponseEntity.ok().body(createListResponse.getErrorMessage());
                    } else {
                        // if creation fails, send email to standard address
                        sendMail(defaultEavEmail, requestData, "ELi:SA-Antwort: " + createListResponse.getErrorMessage());
                        log.warn("could not create list. Reason: " + createListResponse.getErrorMessage());
                        return ResponseEntity.ok().body(createListResponse.getErrorMessage());
                    }
                    // if authentication fails, send email to standard address
                } else {
                    log.error("elisa authentication failed. Reason: " + authenticationResponse.getErrorMessage());
                    sendMail(defaultEavEmail, requestData, "Die ELi:SA -Authentifizierung ist fehlgeschlagen");
                    return ResponseEntity.ok().body("no token received");
                }

            } catch (Exception e) {
                log.error("could not connect to elisa API");
                log.error("the following error occurred: " + e);
                sendMail(defaultEavEmail, requestData, "ELi:SA API nicht erreichbar");
                return ResponseEntity.ok().body("could not connect to elisa API");
            }
        } else {
            log.warn("no isbn given");
            sendMail(defaultEavEmail, requestData, "Es wurde keine ISBN angegeben");
            return ResponseEntity.ok().body("Please provide an ISBN");
        }
    }

    /**
     * endppoint to receive the data from the web form as form fields.
     *
     * @param isbn                 the isbn of the desired title
     * @param title                the title
     * @param contributor          the author or editor
     * @param edition              the edition
     * @param publisher            the publisher
     * @param year                 the year of publication
     * @param price                the price
     * @param subjectarea          the subjectarea this book belongs to
     * @param source               the source of the bibliographic information (lecture etc.)
     * @param comment              a field for comments
     * @param name                 the name of the requestor
     * @param libraryaccountNumber the user id of the requestor
     * @param emailAddress         the email address of the requestor
     * @param response             boolean, true if the requestor wants to be notified about the purchase decision
     * @param essen                boolean, are items wanted for Essen?
     * @param duisburg             boolean, are items wanted for Duisburg?
     * @param requestPlace         if a request is desired, where should it be (Essen or Duisburg)?
     * @return returns status code 200 if entry was created, else forwards the error message from elisa
     */
    // endpoint to process the data from the html form
    @PostMapping("/sendEav")
    public ResponseEntity<?> receiveEav(@RequestParam String isbn,
                                        @RequestParam String title,
                                        @RequestParam String contributor,
                                        @RequestParam String edition,
                                        @RequestParam String publisher,
                                        @RequestParam String year,
                                        @RequestParam String price,
                                        @RequestParam String subjectarea,
                                        @RequestParam String source,
                                        @RequestParam String comment,
                                        @RequestParam String name,
                                        @RequestParam String libraryaccountNumber,
                                        @RequestParam String emailAddress,
                                        @RequestParam boolean response,
                                        @RequestParam boolean essen,
                                        @RequestParam boolean duisburg,
                                        @RequestParam String requestPlace) {
        // for an easier handling store all parameters in the POJO RequestData
        RequestData requestData = new RequestData(isbn, title, contributor, edition, publisher, year, price,
                subjectarea, source, comment, name, libraryaccountNumber, emailAddress, response, essen, duisburg, requestPlace);
        return receiveEav(requestData);

    }

    /**
     * direkt wrapper for the ELi:SA API. handles authentication etc. credentials remain stored in the secured config server.
     *
     * @param protokollToElisaRequest      A container object holding the list of titles to be send to ELi:SA, the notepad name and the elisa user ID
     * @return status message
     */
    @PostMapping("/sendToElisa")
    public ResponseEntity<?> sendToElisa(
            @RequestBody ProtokollToElisaRequest protokollToElisaRequest) {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(callerID, secret);
        AuthenticationResponse authenticationResponse = elisaClient.getToken(authenticationRequest);
        if (authenticationResponse.getErrorcode() != 0)
            return ResponseEntity.badRequest().body(authenticationResponse.getErrorMessage());
        CreateListRequest createListRequest = new CreateListRequest(protokollToElisaRequest.getUserID(), protokollToElisaRequest.getNotepadName());
        log.info("creating elisa request for user " + protokollToElisaRequest.getUserID() + " and notepad " +  protokollToElisaRequest.getNotepadName());
        createListRequest.setToken(authenticationResponse.getToken());
        createListRequest.setTitleList(Arrays.asList(protokollToElisaRequest.getTitles()));
        CreateListResponse createListResponse = elisaClient.createList(createListRequest);
        if (createListResponse.getErrorcode() == 0)
            return ResponseEntity.accepted().build();
        return ResponseEntity.badRequest().body(createListResponse.getErrorMessage());
    }

    private void sendMail(String to, RequestData requestData, String reason) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(eEavEmailFrom);
            messageHelper.setTo(to);
            String text = mailContentBuilder.build(requestData, reason);
            messageHelper.setText(text, true);
            messageHelper.setSubject("Anschaffungsvorschlag");
        };
        emailSender.send(messagePreparator);
        log.info("sent email to " + to);
    }

    private void sendNotificationMail(String to, String name) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(eEavEmailFrom);
            messageHelper.setTo(to);
            String text = mailContentBuilder.buildNotification(name);
            messageHelper.setText(text, true);
            messageHelper.setSubject("neuer Anschaffungsvorschlag in Elisa");
        };
        emailSender.send(messagePreparator);
        log.info("sent email to " + to);
    }

    private void sendAlreadyContainedMail(String to, RequestData requestData, String name) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(eEavEmailFrom);
            messageHelper.setTo(to);
            String text = mailContentBuilder.buildAlreadyContained(requestData, name);
            messageHelper.setText(text, true);
            messageHelper.setSubject("Anschaffungsvorschlag");
        };
        emailSender.send(messagePreparator);
        log.info("sent email to " + to);
    }

    private ElisaData determineElisaData(List<ElisaData> availableProfiles) {
        Collections.sort(availableProfiles);
        // TODO: check for out-of-office status, select first one, second one, depending on result
        return availableProfiles.get(0);
    }
}
