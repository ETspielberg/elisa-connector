package unidue.ub.elisaconnector.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import unidue.ub.elisaconnector.model.RequestData;

@Service
public class MailContentBuilder {

    private TemplateEngine templateEngine;

    @Autowired
    public MailContentBuilder(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String build(RequestData requestData, String reason) {
        Context context = new Context();
        context.setVariable("reason", reason);
        context.setVariable("isbn", requestData.isbn);
        context.setVariable("title", requestData.title);
        context.setVariable("contributor", requestData.contributor);
        context.setVariable("edition", requestData.edition);
        context.setVariable("publisher", requestData.publisher);
        context.setVariable("price", requestData.price);
        context.setVariable("year", requestData.year);
        context.setVariable("subjectarea", requestData.subjectarea);
        context.setVariable("source", requestData.source);
        context.setVariable("comment", requestData.comment);
        context.setVariable("name", requestData.name);
        context.setVariable("libraryaccountNumber", requestData.libraryaccountNumber);
        context.setVariable("emailAddress", requestData.emailAddress);
        if (requestData.response)
            context.setVariable("response", "Ja");
        else
            context.setVariable("response", "Nein");
        if (requestData.essen)
            context.setVariable("essen", "Ja");
        else
            context.setVariable("essen", "");
        if (requestData.duisburg)
            context.setVariable("duisburg", "Ja");
        else
            context.setVariable("duisburg", "");
        context.setVariable("requestPlace", requestData.requestPlace);
        return templateEngine.process("mailTemplate", context);
    }

    public String buildNotification(String name) {
        Context context = new Context();
        context.setVariable("reason", name);
        return templateEngine.process("notificationMailTemplate", context);
    }
}
