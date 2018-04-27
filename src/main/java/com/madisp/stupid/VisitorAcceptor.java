package com.madisp.stupid;

/**
 * @author Marcin Wielgus
 */
public interface VisitorAcceptor {
    <V> V acceptVisitor(ExpressionVisitor<V> visitor, V value);
}
