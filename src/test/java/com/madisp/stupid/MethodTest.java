package com.madisp.stupid;

import org.junit.Test;

import static org.junit.Assert.*;

public class MethodTest extends BaseExpressionTest {
    public Foobar foobar = new Foobar();

    public String foo() {
        return "foo";
    }

    public String bar() {
        return "bar";
    }

    public Foobar foobar() {
        return foobar;
    }

    public String fmt(String formatString, Object... args) {
        return String.format(formatString, args);
    }

    public String identity(String arg) {
        return arg;
    }

    public double sqrt(double arg) {
        return Math.sqrt(arg);
    }

    @Test
    public void testSimpleMethods() throws StupidRuntimeException {
        assertEquals(bar().getClass(), eval("bar()").getClass());
        assertEquals(foo(), eval("foo()"));
        assertEquals(bar(), eval("bar()"));
    }

    @Test
    public void testJavaMethods() throws StupidRuntimeException {
        assertEquals(foo(), eval("foo().toString()"));
        assertEquals(bar().startsWith("foo"), eval("bar().startsWith('foo')"));
        assertEquals("foobar".hashCode(), eval("'foobar'.hashCode()"));
    }

    @Test
    public void testNestedMethods() throws StupidRuntimeException {
        assertEquals(foobar(), foobar);
        assertEquals(foobar(), eval("foobar()"));
        assertEquals(foobar, eval("foobar"));
        assertEquals(foobar().foobar(), eval("foobar().foobar()"));
        assertEquals(foobar().foobar().equals(foo() + bar()), eval("foobar().foobar().equals(foo()+bar())"));
    }

    @Test
    public void testVarargs() throws StupidRuntimeException {
        assertEquals(fmt("asdf"), eval("fmt('asdf')"));
        assertEquals(
                fmt("asdf %d %s %f", 15, foobar.foobar(), 2.3d),
                eval("fmt('asdf %d %s %f', 15, foobar.foobar(), 2.3d)"));
    }

    @Test(expected = StupidRuntimeException.class)
    public void testMissingMethod() throws StupidRuntimeException {
        eval("baz()");
        eval("baz().foo()");
    }

    @Test(expected = StupidRuntimeException.class)
    public void testMissingMethod2() throws StupidRuntimeException {
        eval("baz().foo()");
    }

    @Test
    public void testPassingNull() throws StupidRuntimeException {
        assertNull(eval("identity(null)"));
        assertEquals(identity(null), eval("identity(null)"));
    }

    @Test
    public void testPromotingArgs() throws StupidRuntimeException {
        assertNotNull(eval("sqrt(3)"));
        assertEquals(Double.class, eval("sqrt(3)").getClass());
        assertEquals(sqrt(3), eval("sqrt(3)"));
    }

    public static class Foobar {
        public String foobar() {
            return "foobar";
        }
    }
}
