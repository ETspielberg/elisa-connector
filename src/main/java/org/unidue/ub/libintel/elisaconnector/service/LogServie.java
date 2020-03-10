package org.unidue.ub.libintel.elisaconnector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogServie {

    private final static Logger log = LoggerFactory.getLogger(LogServie.class);

    private final static String logText = "subject: '%s', isbn: '%s', action: '%s', elisa: '%s', type: '%s'";

    public static void logElisa(String subject, String isbn, String action, String elisa, String type) {
        log.info(String.format(logText, subject, isbn, action, elisa, type));
    }


}
