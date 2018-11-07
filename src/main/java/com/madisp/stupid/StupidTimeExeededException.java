package com.madisp.stupid;

/**
 * @author Marcin Wielgus
 */
public class StupidTimeExeededException extends StupidRuntimeException {
    public StupidTimeExeededException(String message) {
        super(message);
    }

    public StupidTimeExeededException(String message, Throwable cause) {
        super(message, cause);
    }
}
