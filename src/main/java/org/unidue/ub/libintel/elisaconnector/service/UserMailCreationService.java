package org.unidue.ub.libintel.elisaconnector.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.unidue.ub.libintel.elisaconnector.model.RequestDataUser;

import static org.unidue.ub.libintel.elisaconnector.utils.*;

/**
 * creates the necessary mails for purchase requests from users
 */
@Service
public class UserMailCreationService implements MailCreationService<RequestDataUser> {

    private TemplateEngine templateEngine;

    /**
     * construcotr based autowiring to the thymeleaf template engine
     * @param templateEngine the thymeleaf templating engine
     */
    @Autowired
    public  UserMailCreationService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }


    /**
     * @param reason the reason, why the elisa submission was unsuccessful
     * @param requestDataUser the purchase request data submitted by the user
     * @return the content of the mail in html format
     */
    @Override
    public String buildEavMail(String reason, RequestDataUser requestDataUser) {
        Context context = setGeneralVariables(requestDataUser);
        setUserSpecificVariables(context, requestDataUser);
        context.setVariable("reason", reason);
        context.setVariable("mailType", "user");
        return templateEngine.process("eavMailTemplate", context);
    }

    /**
     * @param requestDataUser the purchase request data submitted by the user
     * @return the content of the mail in html format
     */
    @Override
    public String buildEbookMail(RequestDataUser requestDataUser) {
        Context context = setGeneralVariables(requestDataUser);
        setUserSpecificVariables(context, requestDataUser);
        context.setVariable("mailType", "user");
        return templateEngine.process("ebookMailTemplate", context);
    }

    /**
     * @param name the name of the subject librarian to be addressed
     * @param requestDataUser the purchase request data submitted by the user
     * @return the content of the mail in html format
     */
    @Override
    public String buildAlreadyContainedMal(String name, RequestDataUser requestDataUser) {
        Context context = setGeneralVariables(requestDataUser);
        setUserSpecificVariables(context, requestDataUser);
        context.setVariable("to", name);
        context.setVariable("mailType", "user");
        return templateEngine.process("alreadyContainedMailTemplate", context);
    }

    /**
     * @param name the name of the subject librarian to be addressed
     * @param requestDataUser the purchase request data submitted by the user
     * @return the content of the mail in html format
     */
    @Override
    public String buildNotificationMail(String name, RequestDataUser requestDataUser) {
        Context context = setGeneralVariables(requestDataUser);
        setUserSpecificVariables(context, requestDataUser);
        context.setVariable("to", name);
        context.setVariable("mailType", "user");
        return templateEngine.process("notificationMailTemplate", context);
    }
}
