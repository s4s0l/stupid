package com.madisp.stupid.expr;

import com.madisp.stupid.ExecContext;
import com.madisp.stupid.Expression;
import com.madisp.stupid.StupidRuntimeException;

/**
 * The classic ternary expression, e.g. expr ? trueValue : falseValue.
 * I know everybody hates it but it has been proven useful from time to time.
 * <p>
 * In stupid: {@code expr ? trueValue : falseValue}
 */
public class TernaryExpression implements Expression {
    private final Expression expression, trueValue, falseValue;

    public TernaryExpression(Expression expression, Expression trueValue, Expression falseValue) {
        this.expression = expression;
        this.trueValue = trueValue;
        this.falseValue = falseValue;
    }

    @Override
    public Object value(ExecContext ctx) throws StupidRuntimeException {
        return ctx.getConverter().toBool(ctx.dereference(expression))
                ? ctx.dereference(trueValue) : ctx.dereference(falseValue);
    }

    @Override
    public Expression[] children() {
        return new Expression[]{expression, trueValue, falseValue};
    }
}
