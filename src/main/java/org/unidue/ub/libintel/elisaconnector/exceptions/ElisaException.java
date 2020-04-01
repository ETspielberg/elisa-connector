package org.unidue.ub.libintel.elisaconnector.exceptions;

/**
 * thrown, when a general problem with elisa occurs
 */
public class ElisaException extends RuntimeException {

    public ElisaException(String errorMessage) {
        super(errorMessage);
    }
}
