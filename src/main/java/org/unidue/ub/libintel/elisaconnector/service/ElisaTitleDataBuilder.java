package org.unidue.ub.libintel.elisaconnector.service;

import org.unidue.ub.libintel.elisaconnector.model.RequestData;
import org.unidue.ub.libintel.elisaconnector.model.TitleData;

public class ElisaTitleDataBuilder {

    /**
     * takes a RequestData object and generates a TitleData object from it, which can be submitted to elisa.
     * @param requestData the request data as obtained from the web form
     * @return the data to be submitted to elisa
     */
    public static TitleData fromRequestData(RequestData requestData) {
        TitleData titleData = new TitleData(requestData.isbn);

        // prepare the library note
        String note = "";
        if (requestData.essen) {
            note += "E??:1, ";
        }
        if (requestData.duisburg) {
            if (requestData.essen)
                note += "";
            note += "D??:1,  , ";
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
        return titleData;
    }
}
