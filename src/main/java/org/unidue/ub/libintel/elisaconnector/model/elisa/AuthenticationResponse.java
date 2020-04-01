package org.unidue.ub.libintel.elisaconnector.model.elisa;

/**
 * Response object to authentication call to ELi:SA. contains the token to be used for further requests and possibly
 * the error messages if the authentication request failed.
 */
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

    /**
     * returns a speaking version of the error code
     * @return errorMessage the error message related to the error code
     */
    public String getErrorMessage() {
        switch (errorcode) {
            case 0: return "Kein Fehler";
            case 1: return "Fehler bei der Authentifizierung";
            case 2: return "Zeitstempel zu alt ( > 30 Minuten)";
        }
        return "Kein Fehler";
    }
}
