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

    /**
     *  generate a mail body to be send, if no entry in ELi:SA could be created
     * @param requestData the data from the web form
     * @param reason the reason, why no entry in ELi:SA could be created.
     * @return the mail body in html.
     */
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

    /**
     *  generate a mail body to be send, to notify the subject librarian, that an item has been submitted, which is already on the list
     * @param requestData the data from the web form
     * @param name name of the subject librarian
     * @return the mail body in html.
     */
    public String buildAlreadyContained(RequestData requestData, String name) {
        Context context = new Context();
        context.setVariable("receiver", name);
        context.setVariable("isbn", requestData.isbn);
        context.setVariable("title", requestData.title);
        context.setVariable("contributor", requestData.contributor);
        context.setVariable("comment", requestData.comment);
        context.setVariable("source", requestData.source);
        context.setVariable("name", requestData.name);
        context.setVariable("libraryaccountNumber", requestData.libraryaccountNumber);
        context.setVariable("emailAddress", requestData.emailAddress);
        context.setVariable("response", requestData.response);
        context.setVariable("essen", requestData.essen);
        context.setVariable("duisburg", requestData.duisburg);
        context.setVariable("requestPlace", requestData.requestPlace);
        return templateEngine.process("alreadyContainedEmailTemplate", context);
    }

    /**
     *  generate a mail body to be send, to notify the
     * @param name name of the subject librarian
     * @return the mail body in html.
     */
    public String buildNotification(String name, RequestData requestData) {
        Context context = new Context();
        context.setVariable("to", name);
        context.setVariable("title", requestData.title);
        context.setVariable("contributor", requestData.contributor);
        context.setVariable("name", requestData.name);
        context.setVariable("emailAddress", requestData.emailAddress);
        context.setVariable("comment", requestData.comment);
        context.setVariable("source", requestData.source);
        context.setVariable("subject", requestData.subjectarea);
        if (requestData.response)
            context.setVariable("response", "Ja");
        else
            context.setVariable("response", "Nein");
        return templateEngine.process("notificationMailTemplate", context);
    }
}
