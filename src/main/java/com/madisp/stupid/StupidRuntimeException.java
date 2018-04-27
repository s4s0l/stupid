package com.madisp.stupid;

/**
 * @author Marcin Wielgus
 */
public class StupidRuntimeException extends Exception {
    public StupidRuntimeException(String message) {
        super(message);
    }

    public StupidRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
