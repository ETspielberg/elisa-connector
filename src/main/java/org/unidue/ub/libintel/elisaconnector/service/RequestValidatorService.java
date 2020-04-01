package org.unidue.ub.libintel.elisaconnector.service;

import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.elisaconnector.model.RequestData;

import java.util.regex.Pattern;

/**
 * validates a submitted purchase request
 */
@Service
public class RequestValidatorService {

    // ISBN regular expression test for ISBN
    private final static Pattern patternISBN = Pattern.compile("^(97([89]))?\\d{9}(\\d|X)$");

    RequestValidatorService() {}

    /**
     * validates given request data. returns a string message depicting the status of the request.
     * @param requestData the request data to be validated
     * @return 'no.subjectarea', 'no.isbn', 'valid.isbn', 'invalid.isbn'
     */
    public String validate(RequestData requestData) {
        if (requestData.subjectarea == null || requestData.subjectarea.equals("kA") || requestData.subjectarea.trim().equals(""))
            return "no.subjectarea";
        if (requestData.isbn == null || requestData.isbn.isEmpty())
            return "no.isbn";
        if (requestData.isbn.contains("-"))
            requestData.isbn = requestData.isbn.replace("-", "");
        requestData.isbn = requestData.isbn.replace("-", "").strip();
        if (requestData.isbn.contains(" "))
            requestData.isbn = requestData.isbn.replace(" ", "");
        if (patternISBN.matcher(requestData.isbn).find())
            return "valid.isbn";
        else
            return "invalid.isbn";
    }
}
