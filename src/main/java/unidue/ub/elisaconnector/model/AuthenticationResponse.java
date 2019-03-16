package unidue.ub.elisaconnector.model;

public class AuthenticationResponse {

    private int errorcode;

    private String token;

    public int getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(int errorcode) {
        this.errorcode = errorcode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getErrorMessage() {
        switch (errorcode) {
            case 0: return "Kein Fehler";
            case 1: return "Fehler bei der Authentifizierung";
            case 2: return "Zeitstempel zu alt ( > 30 Minuten)";
        }
        return "Kein Fehler";
    }
}
