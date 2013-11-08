package com.madisp.stupid.expr;

import com.madisp.stupid.ExecContext;
import com.madisp.stupid.Value;

public class PlusExpression implements Value {
	private final Value left, right;

	public PlusExpression(Value left, Value right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public Object value(ExecContext ctx) {
		Object l = ctx.deref(left);
		Object r = ctx.deref(right);
		if (l instanceof String || r instanceof String) {
			return ctx.toString(l) + ctx.toString(r);
		} else if (l instanceof Double || r instanceof Double) {
			return ctx.toDouble(l) + ctx.toDouble(r);
		}
		return ctx.toInt(l) + ctx.toInt(r);
	}
}
