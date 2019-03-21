package unidue.ub.elisaconnector.model;

import java.util.List;

public class CreateListResponse {

    private int errorcode;

    private List<String> isbnWithError;

    public int getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(int errorcode) {
        this.errorcode = errorcode;
    }

    public List<String> getIsbnWithError() {
        return isbnWithError;
    }

    public void setIsbnWithError(List<String> isbnWithError) {
        this.isbnWithError = isbnWithError;
    }

    /**
     * returns a speaking version of the error code
     * @return errorMessage the error message related to the error code
     */
    public String getErrorMessage() {
        switch (errorcode) {
            case 0: return "kein Fehler";
            case 1: return "Session abgelaufen";
            case 2: return "Nutzer nicht gefunden";
            case 3: return "Titelinformationen sind falsch formatiert";
            case 4: return "Titel wurde bereits verarbeitet";
            case 10: return "Mindestens ein Titel beinhaltet einen Fehler";
            case 11: return "Titel nicht gefunden";
            case 12: return "Feld \"notiz\": Feldlänge überschritten oder invalid UTF-8";
            case 13: return "Feld \"notiz_intern\" Feldlänge überschritten oder invalid UTF-8";
        }
        return "kein Fehler";
    }
}
