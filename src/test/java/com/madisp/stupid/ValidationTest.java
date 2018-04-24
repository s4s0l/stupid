package com.madisp.stupid;

import com.madisp.stupid.context.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ValidationTest {
    private StackContext ctx = new StackContext();
    private ExpressionFactory builder = new ExpressionFactory();
    private FixedMethodContext fmc = new FixedMethodContext(
            FixedMethodContext.root("noarg", () -> "1"),
            FixedMethodContext.root("onearg", a -> "2" + a),
            FixedMethodContext.root("twoarg", (a, b) -> "3" + a + b),
            FixedMethodContext.object("toUpperCase", a -> a.toString().toUpperCase()),
            FixedMethodContext.object("append", (o, a) -> o.toString() + a.toString())
    );
    private ValidationContext vc = new ComposedValidationContext(fmc);

    private Object eval(String expr) throws NoSuchFieldException, NoSuchMethodException {
        Value e = builder.parseExpression(expr);
        e.validate(fmc);
        return ctx.dereference(e);
    }

    @Before
    public void setUp() throws Exception {
        ctx.pushExecContext(fmc);
    }

    @After
    public void tearDown() throws Exception {
        ctx.popExecContext();
    }

    @Test
    public void fixedRootCalls() throws Exception {
        assertEquals("1", eval("noarg()"));
        assertEquals("2a", eval("onearg('a')"));
        assertEquals("3ab", eval("twoarg('a', 'b')"));
    }

    @Test
    public void fixedNonRootCalls() throws Exception {
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("foo", "value");

        ctx.pushExecContext(new VarContext(vars));
        assertEquals("VALUE", eval("foo.toUpperCase()"));
        assertEquals("values", eval("foo.append('s')"));
        ctx.popExecContext();
    }


    @Test(expected = NoSuchMethodException.class)
    public void missingMethod1() throws Exception {
        eval("noargz()");
    }

    @Test(expected = NoSuchMethodException.class)
    public void missingMethodArgCount() throws Exception {
        eval("noarg(1)");
    }

    @Test(expected = NoSuchMethodException.class)
    public void missingMethodObject() throws Exception {
        eval("foo.noargz()");
    }

    @Test(expected = NoSuchMethodException.class)
    public void missingMethodArgCountOnObj() throws Exception {
        eval("foo.toUpperCase(1)");
    }

}
