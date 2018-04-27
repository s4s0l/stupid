package com.madisp.stupid.expr;

import com.madisp.stupid.ExecContext;
import com.madisp.stupid.Expression;
import com.madisp.stupid.StupidRuntimeException;

/**
 * @author Marcin Wielgus
 */
public class ConditionExpression implements Expression {
    private final Expression expression, trueValue, falseValue;

    public ConditionExpression(Expression expression, Expression trueValue, Expression falseValue) {
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
