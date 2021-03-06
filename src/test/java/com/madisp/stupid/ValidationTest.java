package com.madisp.stupid;

import com.madisp.stupid.context.FixedMethodContext;
import com.madisp.stupid.context.StackContext;
import com.madisp.stupid.context.VarContext;
import com.madisp.stupid.validation.MethodFinder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class ValidationTest {
    private StackContext ctx = new StackContext();
    private ExpressionFactory builder = new ExpressionFactory();
    private FixedMethodContext fmc = new FixedMethodContext(
            FixedMethodContext.root("noarg", () -> "1"),
            FixedMethodContext.root("onearg", ValidationTest::oneArg),
            FixedMethodContext.root("twoarg", (a, b) -> "3" + a + b),
            FixedMethodContext.object("toUpperCase", a -> a.toString().toUpperCase()),
            FixedMethodContext.object("append", (o, a) -> o.toString() + a.toString())
    );

    private static String oneArg(Object x) {
        return "2" + x;
    }

    private Object eval(String expr) throws NoSuchMethodException, StupidRuntimeException {
        Value e = builder.parseExpression(expr);
        Set<MethodSignature> requiredMethods = MethodFinder.findRequiredMethods(e);
        requiredMethods.removeAll(fmc.getSupportedSignatures());
        if (!requiredMethods.isEmpty()) {
            throw new NoSuchMethodException();
        }
        return ctx.dereference(e);
    }

    @Before
    public void setUp() {
        ctx.pushExecContext(fmc);
    }

    @After
    public void tearDown() {
        ctx.popExecContext();
    }

    @Test
    public void fixedRootCalls() throws Exception {
        assertEquals("1", eval("noarg()"));
        assertEquals("2a", eval("onearg('a')"));
        assertEquals("21", eval("onearg(1)"));
        assertEquals("3ab", eval("twoarg('a', 'b')"));
    }

    @Test
    public void fixedNonRootCalls() throws Exception {
        Map<String, Object> vars = new HashMap<>();
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
