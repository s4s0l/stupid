package com.madisp.stupid.context;

import com.madisp.stupid.Converter;
import com.madisp.stupid.OperatorCapable;
import com.madisp.stupid.Operators;

import java.util.function.Supplier;

/**
 * @author Marcin Wielgus
 */
public class DefaultOperators implements Operators {
    private final Supplier<Converter> converter;

    public DefaultOperators(Supplier<Converter> converter) {
        this.converter = converter;
    }

    @Override
    public Object and(Object left, Object right) {
        if (left != null && right != null && left.getClass().equals(right.getClass()) && left instanceof OperatorCapable) {
            return ((OperatorCapable) left).and(right);
        }
        return getConverter().toBool(left) && getConverter().toBool(right);
    }

    @Override
    public Object or(Object left, Object right) {
        if (left != null && right != null && left.getClass().equals(right.getClass()) && left instanceof OperatorCapable) {
            return ((OperatorCapable) left).or(right);
        }
        if (getConverter().toBool(left)) {
            return left;
        } else {
            return right;
        }
    }

    public Converter getConverter() {
        return converter.get();
    }

    @Override
    public Object not(Object o) {
        if (o instanceof OperatorCapable) {
            return ((OperatorCapable) o).not();
        }
        return !getConverter().toBool(o);
    }
}
