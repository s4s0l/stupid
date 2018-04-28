package com.madisp.stupid;

/**
 * @author Marcin Wielgus
 */
public interface OperatorCapable<T> {

    Object and(T other);

    Object or(T other);

    Object not();
}
