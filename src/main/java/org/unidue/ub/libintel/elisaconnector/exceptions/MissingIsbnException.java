package org.unidue.ub.libintel.elisaconnector.exceptions;

public class MissingIsbnException extends RuntimeException {

    public MissingIsbnException(String errorMessage) {
        super(errorMessage);
    }

    public MissingIsbnException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
