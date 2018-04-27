package com.madisp.stupid.expr;

import com.madisp.stupid.ExecContext;
import com.madisp.stupid.Expression;
import com.madisp.stupid.StupidRuntimeException;

/**
 * The unary boolean not operator.
 *
 * Usage in stupid: {@code !expr}
 */
public class NotExpression implements Expression<Boolean> {
	private final Expression expr;

	public NotExpression(Expression expr) {
		this.expr = expr;
	}

	@Override
    public Boolean value(ExecContext ctx) throws StupidRuntimeException {
		return !ctx.getConverter().toBool(ctx.dereference(expr));
	}

	@Override
	public Expression[] children() {
		return new Expression[] { expr };
	}
}
