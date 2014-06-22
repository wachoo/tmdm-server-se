package com.amalto.core.util;


public class EntityNotFoundException extends XtentisException {

    private static final long serialVersionUID = -5541352366654335876L;

    public EntityNotFoundException() {
        super();
    }

    /**
     * @param message
     */
    public EntityNotFoundException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public EntityNotFoundException(Throwable cause) {
        super(cause);
    }
}
