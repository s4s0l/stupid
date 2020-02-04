package com.madisp.stupid.expr;

import com.madisp.stupid.ExecContext;
import com.madisp.stupid.Expression;
import com.madisp.stupid.StupidRuntimeException;

/**
 * The arithmetic minus expression. If any of the arguments is a double then
 * both of the arguments are converted to double. Otherwise an Integer is implied.
 * Note that this class is used for both less than and larger than operations (as the
 * only difference is the order of operands).
 * <p>
 * Usage in stupid: {@code expr < expr}
 */
public class ComparisonWithEqExpression implements Expression<Boolean> {
    private Expression left, right;

    public ComparisonWithEqExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Boolean value(ExecContext ctx) throws StupidRuntimeException {
        Object leftValue = ctx.dereference(left);
        Object rightValue = ctx.dereference(right);
        if (leftValue instanceof Double || rightValue instanceof Double) {
            return ctx.getConverter().toDouble(leftValue) <= ctx.getConverter().toDouble(rightValue);
        } else {
            return ctx.getConverter().toLong(leftValue) <= ctx.getConverter().toLong(rightValue);
        }
    }

    @Override
    public Expression[] children() {
        return new Expression[]{left, right};
    }
}
