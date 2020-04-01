package org.unidue.ub.libintel.elisaconnector.model;

import org.unidue.ub.libintel.elisaconnector.model.elisa.Title;

/**
 * POJO for the information obtained from the protokoll in order to be added to a memory list in elisa, that is the user
 * id of the account in elisa, the memory list name and the the list of title data to be submitted to the list.
 */
public class ProtokollToElisaRequest {

    private String notepadName;

    private String userID;

    private Title[] titles;

    public ProtokollToElisaRequest() {
    }

    public String getNotepadName() {
        return notepadName;
    }

    public void setNotepadName(String notepadName) {
        this.notepadName = notepadName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Title[] getTitles() {
        return titles;
    }

    public void setTitles(Title[] titles) {
        this.titles = titles;
    }
}
