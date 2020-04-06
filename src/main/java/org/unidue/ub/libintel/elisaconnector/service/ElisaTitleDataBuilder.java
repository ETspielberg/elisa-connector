package org.unidue.ub.libintel.elisaconnector.service;

import org.unidue.ub.libintel.elisaconnector.model.RequestData;
import org.unidue.ub.libintel.elisaconnector.model.RequestDataLecturer;
import org.unidue.ub.libintel.elisaconnector.model.RequestDataUser;
import org.unidue.ub.libintel.elisaconnector.model.elisa.TitleData;

/**
 * creates the title data for the elisa list creation request from the given request data
 */
public class ElisaTitleDataBuilder {

    /**
     * takes a request data object and returns the title data object to be submitted to elisa
     * @param requestData the basic request data object or an extension (lecturer, user)
     * @return the title data object in the elisa format
     */
    public static TitleData fromRequestData(RequestData requestData){
        String className = requestData.getClass().getSimpleName();
        TitleData titleData = new TitleData(requestData.isbn);
        switch(className) {
            case "RequestDataUser": {
                addRequestDataUser(titleData, (RequestDataUser) requestData);
                break;
            }
            case "RequestDataLecturer": {
                addRequestDataLecturer(titleData, (RequestDataLecturer) requestData);
                break;
            }
        }
        return titleData;

    }


    /**
     * takes a RequestDataUser object and adds the specific fields to the providedTitleData object.
     * @param requestData the request data as obtained from the web form
     */
    private static void addRequestDataUser(TitleData titleData, RequestDataUser requestData) {
        // prepare the library note
        String note = titleData.getNotiz();
        if (requestData.essen) {
            note = "E??:1, " + note;
        }
        if (requestData.duisburg) {
            if (requestData.essen)
                note += "" + note;
            note += "D??:1,  , " + note;
        }
        if (requestData.libraryaccountNumber != null) {
            if (!requestData.libraryaccountNumber.isEmpty()) {
                note += "VM " + requestData.libraryaccountNumber;
                if (requestData.requestPlace != null) {
                    if (!requestData.requestPlace.isEmpty())
                        note += " in " + requestData.requestPlace;
                }
            }
        }
        titleData.setNotiz(note);
    }


    /**
     * takes a RequestDataLecturer object and adds the specific fields to the providedTitleData object.
     * @param requestData the request data as obtained from the web form
     */
    private static void addRequestDataLecturer(TitleData titleData, RequestDataLecturer requestData) {
        String intern = "VM für ";
        if (requestData.personalAccount) {
            intern += " persönlichen Ausweis ";

        }
        if (requestData.happAccount) {
            intern += " Handapparat ";
        }
        if (requestData.semAppAccount) {
            intern += " Semesterapparat ";
        }
        intern += " erwünscht.";
        if (requestData.number > 1){
            intern += " Bitte " + requestData.number + " Exemplare bestellen.";
        }
        titleData.setNotizIntern(intern);
        if (requestData.libraryaccountNumber != null) {
            if (!requestData.libraryaccountNumber.isEmpty()) {
                titleData.setNotiz("VM " + requestData.libraryaccountNumber);
            }
        }
    }
}
