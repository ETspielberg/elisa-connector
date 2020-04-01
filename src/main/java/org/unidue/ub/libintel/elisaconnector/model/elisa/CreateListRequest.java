package org.unidue.ub.libintel.elisaconnector.model.elisa;

import java.util.ArrayList;
import java.util.List;

/**
 * Request object to create list item in ELi:SA. the token needs to be set to the value obtained from the authentication
 * request.
 */
public class CreateListRequest {

    private String userID;

    private String token;

    private String notepadName;

    /**
     *
     * @param userID the elisa id of the account in which the list shall be created
     * @param notepadName the name of the memory list the item shall be added
     */
    public CreateListRequest(String userID, String notepadName) {
        this.userID = userID;
        this.notepadName = notepadName;
    }

    private List<Title> titleList  = new ArrayList<>();

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNotepadName() {
        return notepadName;
    }

    public void setNotepadName(String notepadName) {
        this.notepadName = notepadName;
    }

    public List<Title> getTitleList() {
        return titleList;
    }

    public void setTitleList(List<Title> titleList) {
        this.titleList = titleList;
    }

    /**
     * adds a Title to the list of titles.
     * @param title the Title object to be added
     */
    public void addTitle(Title title) {
        this.titleList.add(title);
    }

}
