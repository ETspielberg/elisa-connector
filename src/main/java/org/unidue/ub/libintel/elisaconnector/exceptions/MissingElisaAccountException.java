package org.unidue.ub.libintel.elisaconnector.exceptions;

public class MissingElisaAccountException extends RuntimeException {

    public MissingElisaAccountException(String errorMessage) {
        super(errorMessage);
    }
}
