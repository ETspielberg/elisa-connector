package unidue.ub.elisaconnector.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

public class AuthenticationRequest {

    private String callerID;

    private String timestamp;

    private String hash;

    public AuthenticationRequest(String callerID, String secret) {
        this.callerID = callerID;
        this.timestamp = Instant.now().toString();
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat formatterHash = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        this.timestamp = formatter.format(today);
        String toEncode = callerID + formatterHash.format(today) + secret;
        this.hash = DigestUtils.md5DigestAsHex(toEncode.getBytes());
    }

    public String getCallerID() {
        return callerID;
    }

    public void setCallerID(String callerID) {
        this.callerID = callerID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void calclulateHash(String secret) {
        this.hash = callerID + timestamp.toString() + secret;
    }

    public String toString() {
        return "{callerID: " + callerID + ", timestamp: " + timestamp.toString() + ", hash: " + hash + "}";
    }
}
