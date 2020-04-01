package org.unidue.ub.libintel.elisaconnector;

import org.thymeleaf.context.Context;
import org.unidue.ub.libintel.elisaconnector.model.RequestData;
import org.unidue.ub.libintel.elisaconnector.model.RequestDataLecturer;
import org.unidue.ub.libintel.elisaconnector.model.RequestDataUser;

public class utils {

    public static Context setGeneralVariables(RequestData requestData) {
        Context context = new Context();
        context.setVariable("isbn", requestData.isbn);
        context.setVariable("title", requestData.title);
        context.setVariable("contributor", requestData.contributor);
        context.setVariable("edition", requestData.edition);
        context.setVariable("publisher", requestData.publisher);
        context.setVariable("price", requestData.price);
        context.setVariable("year", requestData.year);
        context.setVariable("subjectarea", requestData.subjectarea);
        context.setVariable("comment", requestData.comment);
        context.setVariable("name", requestData.name);
        context.setVariable("libraryaccountNumber", requestData.libraryaccountNumber);
        context.setVariable("emailAddress", requestData.emailAddress);
        context.setVariable("ebookDesired", requestData.ebookDesired);
        return context;
    }

    public static void setLecturerSpecificVariables(Context context, RequestDataLecturer requestDataLecturer) {
        context.setVariable("source", requestDataLecturer.source);
        context.setVariable("directToStock", requestDataLecturer.directToStock);
        context.setVariable("personalAccount", requestDataLecturer.personalAccount);
        context.setVariable("happAccount", requestDataLecturer.happAccount);
        context.setVariable("semAppAccount", requestDataLecturer.semAppAccount);
        context.setVariable("semAppNumber", requestDataLecturer.semAppNumber);
        context.setVariable("number", requestDataLecturer.number);
    }

    public static void setUserSpecificVariables(Context context, RequestDataUser requestDataUser) {
        context.setVariable("source", requestDataUser.source);
        context.setVariable("response", requestDataUser.response);
        context.setVariable("essen", requestDataUser.essen);
        context.setVariable("duisburg", requestDataUser.duisburg);
        context.setVariable("requestPlace", requestDataUser.requestPlace);
    }
}
