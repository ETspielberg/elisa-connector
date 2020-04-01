package org.unidue.ub.libintel.elisaconnector.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.unidue.ub.libintel.elisaconnector.model.RequestDataLecturer;

import static org.unidue.ub.libintel.elisaconnector.utils.*;

/**
 * creates the necessary mails for purchase requests from lecturers
 */
@Service
public class LecturerMailCreationService implements MailCreationService<RequestDataLecturer> {

    private TemplateEngine templateEngine;

    /**
     * construcotr based autowiring to the thymeleaf template engine
     * @param templateEngine the thymeleaf template engine bean
     */
    @Autowired
    public LecturerMailCreationService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }


    /**
     * generates the mail contents in html format for a eav mail send, if the elisa submission was not successful.
     * @param reason the reason, why the elisa submission was unsuccessful
     * @param requestDataLecturer the purchase request data submitted by the lecturer
     * @return the content of the mail in html format
     */
    @Override
    public String buildEavMail(String reason, RequestDataLecturer requestDataLecturer) {
        Context context = setGeneralVariables(requestDataLecturer);
        setLecturerSpecificVariables(context, requestDataLecturer);
        context.setVariable("reason", reason);
        context.setVariable("mailType", "lecturer");
        return templateEngine.process("eavMailTemplate", context);
    }

    /**
     * generates the mail contents in html format for a mail send, if the title was already on an elisa memory list.
     * @param name the name of the subject librarian to be addressed
     * @param requestDataLecturer the purchase request data submitted by the lecturer
     * @return the content of the mail in html format
     */
    @Override
    public String buildAlreadyContainedMal(String name, RequestDataLecturer requestDataLecturer) {
        Context context = setGeneralVariables(requestDataLecturer);
        setLecturerSpecificVariables(context, requestDataLecturer);
        context.setVariable("to", name);
        context.setVariable("mailType", "lecturer");
        return templateEngine.process("alreadyContainedMailTemplate", context);
    }

    /**
     * @param name the name of the subject librarian to be addressed
     * @param requestDataLecturer the purchase request data submitted by the lecturer
     * @return the content of the mail in html format
     */
    @Override
    public String buildNotificationMail(String name, RequestDataLecturer requestDataLecturer) {
        Context context = setGeneralVariables(requestDataLecturer);
        setLecturerSpecificVariables(context, requestDataLecturer);
        context.setVariable("to", name);
        context.setVariable("mailType", "lecturer");
        return templateEngine.process("notificationMailTemplate", context);
    }

    /**
     * @param requestDataLecturer the purchase request data submitted by the lecturer
     * @return the content of the mail in html format
     */
    @Override
    public String buildEbookMail(RequestDataLecturer requestDataLecturer) {
        Context context = setGeneralVariables(requestDataLecturer);
        setLecturerSpecificVariables(context, requestDataLecturer);
        context.setVariable("mailType", "lecturer");
        return templateEngine.process("ebookMailTemplate", context);
    }
}
