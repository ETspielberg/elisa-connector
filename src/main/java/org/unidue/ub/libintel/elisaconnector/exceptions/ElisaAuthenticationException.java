package org.unidue.ub.libintel.elisaconnector.exceptions;

public class ElisaAuthenticationException extends RuntimeException {

    public ElisaAuthenticationException(String errorMessage) {
        super(errorMessage);
    }
}
