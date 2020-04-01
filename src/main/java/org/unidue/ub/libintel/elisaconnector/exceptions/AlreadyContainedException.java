package org.unidue.ub.libintel.elisaconnector.exceptions;

/**
 * thrown if the the item is already on the memory list in elise
 */
public class AlreadyContainedException extends ElisaException {

    public AlreadyContainedException(String errorMessage) {
        super(errorMessage);
    }
}
