package org.unidue.ub.libintel.elisaconnector.exceptions;

/**
 * thrown if the authentication in elisa fails
 */
public class ElisaAuthenticationException extends ElisaException {

    public ElisaAuthenticationException(String errorMessage) {
        super(errorMessage);
    }
}
