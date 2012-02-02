package com.amalto.core.util;


public class ValidateException extends Exception {

    private static final long serialVersionUID = 1L;

    public ValidateException() {
        super();
    }

    /**
     * @param message
     */
    public ValidateException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public ValidateException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public ValidateException(Throwable cause) {
        super(cause);
    }
}
