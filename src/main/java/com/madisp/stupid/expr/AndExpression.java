package com.madisp.stupid.expr;

import com.madisp.stupid.ExecContext;
import com.madisp.stupid.Expression;
import com.madisp.stupid.StupidRuntimeException;

/**
 * The boolean and operator.
 * Usage in stupid: {@code expr and expr}
 */
public class AndExpression implements Expression<Object> {
    private final Expression left, right;

    public AndExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Object value(ExecContext ctx) throws StupidRuntimeException {
        return ctx.getOperators().and(left.value(ctx), right.value(ctx));
    }

    @Override
    public Expression[] children() {
        return new Expression[]{left, right};
    }
}
