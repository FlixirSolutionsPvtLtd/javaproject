package com.alinote.api.exception;

public class ServicesException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1599040697882447612L;

    public ServicesException(String message) {
        super(message);
    }

    public ServicesException(int message) {
        super(Integer.toString(message));
    }
}
