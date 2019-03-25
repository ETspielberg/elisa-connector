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
    private String defaultEavEamil;

    // standard elisa user getting the notepads if no subject librarian can be chosen
    @Value("${libintel.elisa.userid.default}")
    private String defaultElisaUserid;

    private final JavaMailSender emailSender;

    private final ElisaClient elisaClient;

    private final SubjectClient subjectClient;

    private final MailContentBuilder mailContentBuilder;

    /**
     *  constructor injection of beans
     * @param elisaClient the ELi:SA Feign client
     * @param subjectClient tje subject Feign client
     * @param emailSender the email sender
     * @param mailContentBuilder the thymeleaf template email builder
     */
    @Autowired
    public ElisaController(ElisaClient elisaClient, SubjectClient subjectClient, JavaMailSender emailSender, MailContentBuilder mailContentBuilder) {
        this.elisaClient = elisaClient;
        this.subjectClient = subjectClient;
        this.emailSender = emailSender;
        this.mailContentBuilder = mailContentBuilder;
    }

    /**
     * endppoint to receive the data from the web form as json object.
     * @param requestData the submission from the form (JSON-encoded)
     * @return a status message
     */
    @PostMapping("/receiveEav")
    public ResponseEntity<?> receiveEav(@RequestBody RequestData requestData) {
        // ISBN regular expression test for ISBN
        Pattern patternISBN = Pattern.compile("^(97([89]))?\\d{9}(\\d|X)$");
        if (requestData.isbn.contains("-"))
            requestData.isbn = requestData.isbn.replace("-", "");

        // if it is ISBN, try to upload it to elisa
        if (patternISBN.matcher(requestData.isbn).find()) {
            log.info("received Request to send ISBN " + requestData.isbn + "to ELi:SA");

            // try to get the corresponding user account from the settings backend. If no user id can be obtained, send the email
            String userID;
            try {
                userID = subjectClient.getElisaAccount(requestData.subjectarea);
            } catch (Exception e) {
                sendMail(defaultEavEamil, requestData, "Der zuständige ELi:SA account konnte nicht gefunden werden");
                log.warn("could not retrieve the userID for subjectArea " + requestData.subjectarea);
                return ResponseEntity.ok().body("could not retreive Elisa account id");
            }
            if (userID == null)
                return ResponseEntity.ok().body("could not retreive Elisa account id");

            log.info("setting elisa id to " + userID);

            // prepare isbns for sending to elisa
            CreateListRequest createListRequest = new CreateListRequest(userID, "Anschaffungsvorschlag");

            TitleData requestedTitle = new TitleData(requestData.isbn);

            // prepare the intern note with the data about the user and his comments. Add the email for notification.
            String noteIntern = requestData.name + " (" + requestData.emailAddress + "): " + requestData.comment + "\n Literaturangebe von: " + requestData.source;
            if (requestData.response) {
                noteIntern += "\n Bitte den Nutzer benachrichtigen.";
            }
            requestedTitle.setNotizIntern(noteIntern);

            // prepare the library note
            String note = "";
            if (requestData.essen) {
                note += "E :1, ";
            }
            if (requestData.duisburg) {
                if (requestData.essen)
                    note += "";
                note += "D :1,  , ";
            }
            note += "VM für " + requestData.libraryaccountNumber + " (" + requestData.name + ") in " + requestData.requestPlace;
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
                    if (createListResponse.getErrorcode() == 0) {
                        log.info("successfully create elisa list");
                        sendNotificationMail(userID, "FachreferentIn");
                        return ResponseEntity.ok("List created");
                    } else if (createListResponse.getErrorcode() == 4) {
                        sendAlreadyContainedMail(userID, requestData, "FachreferentIn");
                        return ResponseEntity.ok().body(createListResponse.getErrorMessage());
                    } else {
                        // if creation fails, send email to standard address
                        sendMail(defaultEavEamil, requestData, "ELi:SA-Antwort: " + createListResponse.getErrorMessage());
                        log.warn("could not create list. Reason: " + createListResponse.getErrorMessage() );
                        return ResponseEntity.ok().body(createListResponse.getErrorMessage());
                    }
                    // if authentication fails, send email to standard address
                } else {
                    log.error("elisa authentication failed. Reason: " + authenticationResponse.getErrorMessage());
                    sendMail(defaultEavEamil, requestData, "Die ELi:SA -Authentifizierung ist fehlgeschlagen");
                    return ResponseEntity.ok().body("no token received");
                }

            } catch (Exception e) {
                log.error("could not connect to elisa API");
                sendMail(defaultEavEamil, requestData, "ELi:SA API nicht erreichbar");
                return ResponseEntity.ok().body("could not connect to elisa API");
            }
        } else {
            log.warn("no isbn given");
            sendMail(defaultEavEamil, requestData, "Es wurde keine ISBN angegeben");
            return ResponseEntity.ok().body("Please provide an ISBN");
        }
    }

    /**
     * endppoint to receive the data from the web form as form fields.
     * @param isbn the isbn of the desired title
     * @param title the title
     * @param contributor the author or editor
     * @param edition  the edition
     * @param publisher the publisher
     * @param year the year of publication
     * @param price the price
     * @param subjectarea the subjectarea this book belongs to
     * @param source the source of the bibliographic information (lecture etc.)
     * @param comment a field for comments
     * @param name the name of the requestor
     * @param libraryaccountNumber the user id of the requestor
     * @param emailAddress the email address of the requestor
     * @param response boolean, true if the requestor wants to be notified about the purchase decision
     * @param essen boolean, are items wanted for Essen?
     * @param duisburg boolean, are items wanted for Duisburg?
     * @param requestPlace if a request is desired, where should it be (Essen or Duisburg)?
     * @return
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
     * @param titles List of titles to be send to ELi:SA (JSON encoded)
     * @param userID the userID whose notepad the items shall be attached
     * @param notepadName the name of the notapad the items shall be stored in
     * @return status message
     */
    @PostMapping("/sendToElisa")
    public ResponseEntity<?> sendToElisa(
            @RequestBody List<Title> titles,
            @RequestBody String userID,
            @RequestBody String notepadName) {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(callerID, secret);
        AuthenticationResponse authenticationResponse = elisaClient.getToken(authenticationRequest);
        if (authenticationResponse.getErrorcode() != 0)
            return ResponseEntity.badRequest().body(authenticationResponse.getErrorMessage());
        CreateListRequest createListRequest = new CreateListRequest(userID, notepadName);
        createListRequest.setToken(authenticationResponse.getToken());
        createListRequest.setTitleList(titles);
        CreateListResponse createListResponse = elisaClient.createList(createListRequest);
        if (createListResponse.getErrorcode() == 0)
            return ResponseEntity.accepted().build();
        else if (createListResponse.getErrorcode() == 10 || createListResponse.getErrorcode() == 11)
            return ResponseEntity.badRequest().body(createListResponse);
        return ResponseEntity.badRequest().body(createListResponse.getErrorMessage());
    }

    private void sendMail(String to, RequestData requestData, String reason) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("eike.spielberg@uni-due.de");
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
            messageHelper.setFrom("eike.spielberg@uni-due.de");
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
            messageHelper.setFrom("eike.spielberg@uni-due.de");
            messageHelper.setTo(to);
            String text = mailContentBuilder.buildAlreadyContained(requestData, name);
            messageHelper.setText(text, true);
            messageHelper.setSubject("Anschaffungsvorschlag");
        };
        emailSender.send(messagePreparator);
        log.info("sent email to " + to);
    }
}
