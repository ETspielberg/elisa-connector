package org.unidue.ub.libintel.elisaconnector.exceptions;

public class AlreadyContainedException extends RuntimeException {

    public AlreadyContainedException(String errorMessage) {
        super(errorMessage);
    }
}
