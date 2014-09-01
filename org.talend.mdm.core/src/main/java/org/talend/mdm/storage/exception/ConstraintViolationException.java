package org.talend.mdm.storage.exception;

/**
 *
 */
public class ConstraintViolationException extends RuntimeException {
    public ConstraintViolationException(Exception cause) {
        super(cause);
    }
}
