package org.unidue.ub.libintel.elisaconnector.exceptions;

/**
 * thrown if no elisa account could be found within the system
 */
public class MissingElisaAccountException extends RuntimeException {

    public MissingElisaAccountException(String errorMessage) {
        super(errorMessage);
    }
}
