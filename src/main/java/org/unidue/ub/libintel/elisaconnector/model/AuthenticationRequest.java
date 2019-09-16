package org.unidue.ub.libintel.elisaconnector.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.util.DigestUtils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AuthenticationRequest {

    private String callerID;

    private String timestamp;

    private String hash;

    /**
     * Request object to authentificate at ELi:SA. The timestamps and hash are calculated by default.
     *
     * @param callerID the caller ID provided by the hbz
     * @param secret the secrtet provided by the hbz
     */
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
}
