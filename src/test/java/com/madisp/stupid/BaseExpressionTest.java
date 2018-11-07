package com.madisp.stupid;

import com.madisp.stupid.context.ReflectionContext;
import com.madisp.stupid.context.StackContext;
import org.junit.After;
import org.junit.Before;

public abstract class BaseExpressionTest {
	protected StackContext ctx = new StackContext();
	protected ExpressionFactory builder = new ExpressionFactory();

	protected Object eval(String expr) throws StupidRuntimeException {
		Value e = builder.parseExpression(expr);
		return ctx.dereference(e);
	}

	@Before
	public void setUp() throws StupidRuntimeException {
		ctx.pushExecContext(new ReflectionContext(this));
	}

	@After
	public void tearDown() {
		ctx.popExecContext();
	}
}
