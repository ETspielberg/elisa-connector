package unidue.ub.elisaconnector.model;

import java.util.ArrayList;
import java.util.List;

public class CreateListRequest {

    private String userID;

    private String token;

    private String notepadName;

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

    public void addTitle(Title title) {
        this.titleList.add(title);
    }

}
