package com.madisp.stupid.expr;

import com.madisp.stupid.ExecContext;
import com.madisp.stupid.Expression;
import com.madisp.stupid.ExpressionVisitor;
import com.madisp.stupid.StupidRuntimeException;

/**
 * Assign a value to a field.
 * Usage in stupid: {@code var = expr}
 */
public class AssignExpression implements Expression {
    private final Expression base;
    private final String identifier;
    private final Expression value;

    public AssignExpression(Expression base, String identifier, Expression value) {
        this.base = base;
        this.identifier = identifier;
        this.value = value;
    }

    @Override
    public Object value(ExecContext ctx) throws StupidRuntimeException {
        Object root = base == null ? null : base.value(ctx);
        if (base != null && root == null) {
            return null; // null.something always yields null
        }
        try {
            return ctx.setFieldValue(root, identifier, ctx.dereference(value));
        } catch (NoSuchFieldException nsfe) {
            return null;
        }
    }

    @Override
    public Expression[] children() {
        return new Expression[]{base, value};
    }

    @Override
    public Object acceptVisitor(ExpressionVisitor visitor, Object value) {
        return visitor.onAssign(value, this);
    }
}
