package com.madisp.stupid.expr;

import com.madisp.stupid.ExecContext;
import com.madisp.stupid.Expression;
import com.madisp.stupid.ExpressionVisitor;
import com.madisp.stupid.StupidRuntimeException;

/**
 * Get a variable, either for getting a value or assigning to it.
 * Usage in stupid: {@code var}, or {@code obj.var}.
 */
public class VarExpression implements Expression {
    private final Expression base;
    private final String identifier;

    public VarExpression(Expression base, String identifier) {
        this.base = base;
        this.identifier = identifier;
    }

    @Override
    public Object value(ExecContext ctx) throws StupidRuntimeException {
        Object root = base == null ? null : base.value(ctx);
        if (base != null && root == null) {
            return null; // null.something always yields null
        }
        try {
            return ctx.getFieldValue(root, identifier);
        } catch (NoSuchFieldException nsfe) {
            return null;
        }
    }

    @Override
    public Expression[] children() {
        return new Expression[]{base};
    }

    @Override
    public Object acceptVisitor(ExpressionVisitor visitor, Object value) {
        return visitor.onVarExpression(value, this);
    }
}
