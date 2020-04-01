package org.unidue.ub.libintel.elisaconnector.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.unidue.ub.libintel.elisaconnector.model.RequestDataLecturer;

import static org.unidue.ub.libintel.elisaconnector.utils.*;

@Service
public class LecturerMailCreationService implements MailCreationService<RequestDataLecturer> {

    private TemplateEngine templateEngine;

    @Autowired
    public LecturerMailCreationService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }


    @Override
    public String buildEavMail(String reason, RequestDataLecturer requestDataLecturer) {
        Context context = setGeneralVariables(requestDataLecturer);
        setLecturerSpecificVariables(context, requestDataLecturer);
        context.setVariable("reason", reason);
        context.setVariable("mailType", "lecturer");
        return templateEngine.process("eavMailTemplate", context);
    }

    @Override
    public String buildAlreadyContainedMal(String name, RequestDataLecturer requestDataLecturer) {
        Context context = setGeneralVariables(requestDataLecturer);
        setLecturerSpecificVariables(context, requestDataLecturer);
        context.setVariable("to", name);
        context.setVariable("mailType", "lecturer");
        return templateEngine.process("alreadyContainedMailTemplate", context);
    }

    @Override
    public String buildNotificationMail(String name, RequestDataLecturer requestDataLecturer) {
        Context context = setGeneralVariables(requestDataLecturer);
        setLecturerSpecificVariables(context, requestDataLecturer);
        context.setVariable("to", name);
        context.setVariable("mailType", "lecturer");
        return templateEngine.process("notificationMailTemplate", context);
    }

    @Override
    public String buildEbookMail(String reason, RequestDataLecturer requestDataLecturer) {
        Context context = setGeneralVariables(requestDataLecturer);
        setLecturerSpecificVariables(context, requestDataLecturer);
        context.setVariable("reason", reason);
        context.setVariable("mailType", "lecturer");
        return templateEngine.process("ebookMailTemplate", context);
    }
}
