package com.madisp.stupid;

/**
 * @author Marcin Wielgus
 */
public interface Operators {

    Object and(Object left, Object right);

    Object or(Object left, Object right);

    Object not(Object o);
}
