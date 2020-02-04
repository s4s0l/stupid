package com.madisp.stupid.expr;

import com.madisp.stupid.ExecContext;
import com.madisp.stupid.Expression;
import com.madisp.stupid.StupidRuntimeException;

/**
 * The unary minus (negation) operator.
 * <p>
 * Usage in stupid: {@code -expr}
 */
public class NegateExpression implements Expression {
    private final Expression expr;

    public NegateExpression(Expression expr) {
        this.expr = expr;
    }

    @Override
    public Object value(ExecContext ctx) throws StupidRuntimeException {
        Object value = ctx.dereference(expr);
        if (value instanceof Integer) {
            return -((Integer) value).longValue();
        } else if (value instanceof Long) {
            return -((Long) value);
        } else if (value instanceof Double) {
            return 0d - ((Double) value);
        }
        return 0L;
    }

    @Override
    public Expression[] children() {
        return new Expression[]{expr};
    }
}
