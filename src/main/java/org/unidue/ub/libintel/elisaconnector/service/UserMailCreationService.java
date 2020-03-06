package org.unidue.ub.libintel.elisaconnector.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.unidue.ub.libintel.elisaconnector.model.RequestDataUser;

import static org.unidue.ub.libintel.elisaconnector.utils.*;

@Service
public class UserMailCreationService implements MailCreationService<RequestDataUser> {

    private TemplateEngine templateEngine;

    @Autowired
    public  UserMailCreationService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }


    @Override
    public String buildEavMail(String reason, RequestDataUser requestDataUser) {
        Context context = setGeneralVariables(requestDataUser);
        setUserSpecificVariables(context, requestDataUser);
        context.setVariable("reason", reason);
        context.setVariable("mailType", "user");
        return templateEngine.process("eavMailTemplate", context);
    }

    @Override
    public String buildAlreadyContainedMal(String name, RequestDataUser requestDataUser) {
        Context context = setGeneralVariables(requestDataUser);
        setUserSpecificVariables(context, requestDataUser);
        context.setVariable("to", name);
        context.setVariable("mailType", "user");
        return templateEngine.process("alreadyContainedMailTemplate", context);
    }

    @Override
    public String buildNotificationMail(String name, RequestDataUser requestDataUser) {
        Context context = setGeneralVariables(requestDataUser);
        setUserSpecificVariables(context, requestDataUser);
        context.setVariable("to", name);
        context.setVariable("mailType", "user");
        return templateEngine.process("notificationMailTemplate", context);
    }
}
