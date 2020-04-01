package org.unidue.ub.libintel.elisaconnector.exceptions;

/**
 * thrown, if elisamarks the isbn as invalid
 */
public class InvalidIsbnException extends ElisaException {

    public InvalidIsbnException(String message) {
        super(message);
    }
}
