package com.madisp.stupid;

import com.madisp.stupid.context.FixedMethodContext;
import com.madisp.stupid.context.VarContext;
import org.junit.Test;

import java.util.HashMap;
import java.util.Objects;

import static org.junit.Assert.assertEquals;

public class OperatorsTest extends BaseExpressionTest {
    @Test
    public void testPlus() throws Exception {
        assertEquals(Long.class, eval("2 + 3").getClass());
        assertEquals(2L + 3L, eval("2 + 3"));
        assertEquals(Double.class, eval("2 + 3d").getClass());
        assertEquals(2L + 3d, (Double) eval("2 + 3d"), 0.0001d);
        assertEquals(2d + 3L, (Double) eval("2d + 3"), 0.0001d);
        assertEquals(1.5d + 1.5d, (Double) eval("1.5 + 1.5d"), 0.0001d);
        assertEquals("foo" + "bar", eval("'foo' + 'bar'"));
        assertEquals("foo" + "bar" + "foobar", eval("'foo' + 'bar' + 'foobar'"));
        assertEquals("foo" + 1, eval("'foo' + 1"));
        assertEquals(1L + "foo", eval("1 + 'foo'"));
    }

    @Test
    public void testMinus() throws Exception {
        assertEquals(Long.class, eval("2 - 3").getClass());
        assertEquals(2L - 3L, eval("2 - 3"));
        assertEquals(Double.class, eval("2 - 3d").getClass());
        assertEquals(2L - 3d, (Double) eval("2 - 3d"), 0.0001d);
        assertEquals(2d - 3L, (Double) eval("2d - 3"), 0.0001d);
        assertEquals(1.5d - 1.5d, (Double) eval("1.5 - 1.5d"), 0.0001d);
    }

    @Test
    public void testMultiplication() throws Exception {
        assertEquals(Long.class, eval("2 * 3").getClass());
        assertEquals(2L * 3L, eval("2 * 3"));
        assertEquals(Double.class, eval("2 * 3d").getClass());
        assertEquals(2L * 3d, (Double) eval("2 * 3d"), 0.0001d);
        assertEquals(2d * 3L, (Double) eval("2d * 3"), 0.0001d);
    }

    @Test
    public void testDivision() throws Exception {
        assertEquals(Long.class, eval("2 / 3").getClass());
        assertEquals(0L, eval("2 / 3"));
        assertEquals(3L, eval("7 / 2"));
        assertEquals(3L, eval("9 / 3"));
        assertEquals(Double.class, eval("2 / 3d").getClass());
        assertEquals(2L / 3d, (Double) eval("2 / 3d"), 0.0001d);
        assertEquals(2L / 3d, (Double) eval("2d / 3"), 0.0001d);
    }

    @Test
    public void testNegation() throws Exception {
        assertEquals(Long.class, eval("-2").getClass());
        assertEquals(-2L, eval("-2"));
        assertEquals(Double.class, eval("-2d").getClass());
        assertEquals(-2d, (Double) eval("-2d"), 0.0001d);
        assertEquals(0L, eval("-'asdf'")); // -(non-numeric) yields 0
    }

    @Test
    public void testArithmeticPrecedence() throws Exception {
        assertEquals(Long.class, eval("3+5*2").getClass());
        assertEquals(3L + 5L * 2L, eval("3+5*2"));
        assertEquals((3L + 5L) * 2L, eval("(3+5)*2"));
    }

    @Test
    public void testAnd() throws Exception {
        assertEquals(Boolean.class, eval("false and false").getClass());
        assertEquals(false && false, eval("false and false"));
        assertEquals(false && true, eval("false and true"));
        assertEquals(true && false, eval("true and false"));
        assertEquals(true && true, eval("true and true"));
    }

    @Test
    public void testOr() throws Exception {
        assertEquals(Boolean.class, eval("false or false").getClass());
        assertEquals(false || false, eval("false or false"));
        assertEquals(false || true, eval("false or true"));
        assertEquals(true || false, eval("true or false"));
        assertEquals(true || true, eval("true or true"));
    }

    @Test
    public void testBooleanNegation() throws Exception {
        assertEquals(Boolean.class, eval("!false").getClass());
        assertEquals(!true, eval("!true"));
        assertEquals(!false, eval("!false"));
    }

    @Test
    public void testBooleanPrecedence() throws Exception {
        assertEquals(Boolean.class, eval("false and false or true").getClass());
        assertEquals(false && false || true, eval("false and false or true"));
        assertEquals(true || false && false, eval("true or false and false"));
        assertEquals((true || false) && false, eval("(true or false) and false"));
        assertEquals(false && (false || true), eval("false and (false or true)"));
        assertEquals((true || false) && !false, eval("(true or false) and !false"));
        ctx.pushExecContext(new VarContext(VarContext.Type.CREATE_ON_SET_OR_GET,new HashMap<>()));
        assertEquals(true, eval("v1=1==1;v2=2==2;v1 and v2"));
        ctx.popExecContext();
    }

    @Test
    public void testTernary() throws Exception {
        assertEquals(false, eval("true ? false : true"));
        assertEquals(true, eval("false ? false : true"));
    }

    @Test
    public void testCustomOperators() throws Exception {
        ctx.pushExecContext(new FixedMethodContext(
                FixedMethodContext.root("bits", (Object a) -> new Bits(ctx.getConverter().toLong(a)))));

        //operators test

        assertEquals(new Bits(-2), eval("! bits(1)"));
        assertEquals(new Bits(2), eval("bits(6) and bits(3)"));
        assertEquals(new Bits(7), eval("bits(6) or bits(3)"));
        assertEquals(true, eval("bits(6) and true"));
        assertEquals(false, eval("bits(0) and true"));
        assertEquals(new Bits(1), eval("bits(1) or false"));
        assertEquals(new Bits(1), eval("false or bits(1)"));
        assertEquals(true, eval("bits(0) or true"));
        assertEquals(false, eval("bits(0) or false"));

        //converters test
        assertEquals(2L, eval("bits(0) ? 1 : 2"));
        assertEquals(1L, eval("bits(1) ? 1 : 2"));
        assertEquals("110:)", eval("bits(6) + ':)'"));
        assertEquals(7L, eval("bits(6) + 1"));
        assertEquals(12L, eval("bits(6) + bits(6)")); //todo: add other operators


        ctx.popExecContext();
    }

    private static class Bits implements Convertable, OperatorCapable<Bits> {
        private final long value;

        public Bits(long value) {
            this.value = value;
        }

        @Override
        public boolean toBool() {
            return value != 0;
        }

        @Override
        public long toLong() {
            return value;
        }

        @Override
        public double toDouble() {
            return value;
        }

        @Override
        public String toStupidString() {
            return Long.toString(value, 2);
        }

        @Override
        public Object and(Bits other) {
            return new Bits(value & other.value);
        }

        @Override
        public Object or(Bits other) {
            return new Bits(value | other.value);
        }

        @Override
        public Object not() {
            return new Bits(~value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Bits bits = (Bits) o;
            return value == bits.value;
        }

        @Override
        public int hashCode() {

            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "Bits{" +
                    "value=" + value +
                    '}';
        }
    }


}
