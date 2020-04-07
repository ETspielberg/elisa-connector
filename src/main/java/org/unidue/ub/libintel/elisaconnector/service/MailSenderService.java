package org.unidue.ub.libintel.elisaconnector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.elisaconnector.model.RequestData;
import org.unidue.ub.libintel.elisaconnector.model.RequestDataLecturer;
import org.unidue.ub.libintel.elisaconnector.model.RequestDataUser;

/**
 * handles the sending of mails
 */
@Service
public class MailSenderService {

    // the email address to appear as "from"
    @Value("${libintel.eavs.email.from}")
    private String eEavEmailFrom;

    private final JavaMailSender emailSender;

    private final UserMailCreationService userMailCreationService;

    private final LecturerMailCreationService lecturerMailCreationService;

    private static final Logger log = LoggerFactory.getLogger(MailSenderService.class);


    /**
     * constructor based autowiring of email sender and the appropriate mail creation services.
     * @param emailSender the mail sender service as instantiated from the config properties
     * @param userMailCreationService the mail creation service handling the mails for purchase requests from users
     * @param lecturerMailCreationService the mail creation service handling the mails for purchase requests from lecturers
     */
    MailSenderService(
            // error on unknown bean for emailSender can be ignored, emailSender bean is created from configuration properties
            JavaMailSender emailSender,
            UserMailCreationService userMailCreationService,
            LecturerMailCreationService lecturerMailCreationService
    ) {
        this.emailSender = emailSender;
        this.userMailCreationService = userMailCreationService;
        this.lecturerMailCreationService = lecturerMailCreationService;
    }

    /**
     * sends the mail, if an ebook is requested
     * @param requestData the purchase request data
     * @param to the recipient of the email
     */
    public void sendEbookMail(RequestData requestData, String to) {
        String requestType = requestData.getClass().getSimpleName();
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(eEavEmailFrom);
            messageHelper.setTo(to);
            String text;
            switch (requestType) {
                case "RequestDataUser": {
                    MailBuilder<RequestDataUser> mailBuilder = new MailBuilder<>(userMailCreationService);
                    text = mailBuilder.buildEbookMail((RequestDataUser) requestData);
                    messageHelper.setSubject("neuer E-Book-Vorschlag eines Studierenden/Externen");
                    break;
                }
                default: {
                    MailBuilder<RequestDataLecturer> mailBuilder = new MailBuilder<>(lecturerMailCreationService);
                    text = mailBuilder.buildEbookMail((RequestDataLecturer) requestData);
                    messageHelper.setSubject("neuer E-Book-Vorschlag eines Lehrenden");
                    break;
                }
            }

            messageHelper.setText(text, true);
        };
        emailSender.send(messagePreparator);
        log.debug("sent email to " + to);
    }

    /**
     * sends the notification mail, that a new title has been successfully submitted to elisa
     * @param requestData the purchase request data
     * @param to the email address of the recipient
     * @param name the name of the recipient
     */
    public void sendNotificationMail(RequestData requestData, String to, String name) {
        String requestType = requestData.getClass().getSimpleName();
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(eEavEmailFrom);
            messageHelper.setTo(to);
            String text;
            switch (requestType) {
                case "RequestDataUser": {
                    MailBuilder<RequestDataUser> mailBuilder = new MailBuilder<>(userMailCreationService);
                    text = mailBuilder.buildNotificationMail(name, (RequestDataUser) requestData);
                    messageHelper.setSubject("neuer Anschaffungsvorschlag eines Studierenden/Externen in ELi:SA");
                    break;
                }
                default: {
                    MailBuilder<RequestDataLecturer> mailBuilder = new MailBuilder<>(lecturerMailCreationService);
                    text = mailBuilder.buildNotificationMail(name, (RequestDataLecturer) requestData);
                    messageHelper.setSubject("neuer Anschaffungsvorschlag eines Lehrenden in ELi:SA");
                    break;
                }
            }

            messageHelper.setText(text, true);
        };
        emailSender.send(messagePreparator);
        log.debug("sent email to " + to);
    }

    /**
     * sends the eav mail if the title could not be submitted to elisa
     * @param requestData the purchase request data
     * @param to the email address of the recipient
     * @param reason the reason why the title could not be submitted to elisa
     */
    public void sendEavMail(RequestData requestData, String to, String reason) {
        String requestType = requestData.getClass().getSimpleName();
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(eEavEmailFrom);
            messageHelper.setTo(to);
            String text;
            switch (requestType) {
                case "RequestDataUser": {
                    MailBuilder<RequestDataUser> mailBuilder = new MailBuilder<>(userMailCreationService);
                    text = mailBuilder.buildEavMail(reason, (RequestDataUser) requestData);
                    messageHelper.setSubject("Anschaffungsvorschlag eines Studierenden/Externen");
                    break;
                }
                default: {
                    MailBuilder<RequestDataLecturer> mailBuilder = new MailBuilder<>(lecturerMailCreationService);
                    text = mailBuilder.buildEavMail(reason, (RequestDataLecturer) requestData);
                    messageHelper.setSubject("Anschaffungsvorschlag eines Lehrenden");
                    break;
                }
            }
            messageHelper.setText(text, true);
        };
        emailSender.send(messagePreparator);
        log.debug("sent email to " + to);
    }

    /**
     * sends a mail, that a title was requested, which is already on an elisa memory list
     * @param requestData the purchase request data
     * @param to the email address of the recipient
     * @param name  the name of the recipient
     */
    public void sendAlreadyContainedMail(RequestData requestData, String to, String name) {
        String requestType = requestData.getClass().getSimpleName();
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(eEavEmailFrom);
            messageHelper.setTo(to);
            String text;
            switch (requestType) {
                case "UserRequestData": {
                    MailBuilder<RequestDataLecturer> mailBuilder = new MailBuilder<>(lecturerMailCreationService);
                    text = mailBuilder.buildAlreadyContainedMal(name, (RequestDataLecturer) requestData);
                    messageHelper.setSubject("Anschaffungsvorschlag eines Studierenden/Externen in ELi:SA");
                    break;
                }
                default: {
                    MailBuilder<RequestDataUser> mailBuilder = new MailBuilder<>(userMailCreationService);
                    text = mailBuilder.buildAlreadyContainedMal(name, (RequestDataUser) requestData);
                    messageHelper.setSubject("Anschaffungsvorschlag eines Lehrenden in ELi:SA");
                }
            }

            messageHelper.setText(text, true);
        };
        emailSender.send(messagePreparator);
        log.debug("sent email to " + to);
    }
}