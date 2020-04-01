package org.unidue.ub.libintel.elisaconnector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * generates standardized log messages to be captured by the elk stack
 */
public class LogService {

    private final static Logger log = LoggerFactory.getLogger(LogService.class);

    private final static String logText = "subject: '%s', isbn: '%s', action: '%s', elisa: '%s', type: '%s'";

    /**
     * @param subject the subject area the book was assigned to
     * @param isbn the isbn of the book
     * @param action the action taken
     * @param elisa the status of the elisa submission
     * @param type the type of purchase request (user, lecturer,...)
     */
    public static void logElisa(String subject, String isbn, String action, String elisa, String type) {
        log.info(String.format(logText, subject, isbn, action, elisa, type));
    }


}
