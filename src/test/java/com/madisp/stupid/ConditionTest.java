package com.madisp.stupid;

import com.madisp.stupid.context.VarContext;
import org.junit.Test;

import java.util.HashMap;

import static com.madisp.stupid.context.VarContext.Type.CREATE_ON_SET_OR_GET;
import static org.junit.Assert.assertEquals;

public class ConditionTest extends BaseExpressionTest {
    @Test
    public void testIf() throws StupidRuntimeException {
        ctx.pushExecContext(new VarContext(CREATE_ON_SET_OR_GET, new HashMap<>()));
        assertEquals(Boolean.class, eval("if (false) { 1 }").getClass());
        assertEquals(false, eval("if (false) { 1 }"));
        assertEquals(1L, eval("if (true) { 1 }"));
        assertEquals(1L, eval("if (true) { a=1;a; }"));
        assertEquals(1L, eval("if (1==1) { a=1;a; }"));
        assertEquals(2L, eval("x = if (1==1) { a=1;a=a+1;a; } else {b=24;b}; x;"));
        assertEquals(2L, eval("x = if (1==1) { a=1;a=a+1;a } else {b=23;b}; x;"));
        ctx.popExecContext();
    }

    @Test
    public void testIfElse() throws StupidRuntimeException {
        ctx.pushExecContext(new VarContext(CREATE_ON_SET_OR_GET, new HashMap<>()));
        assertEquals(Boolean.class, eval("if (false) { 1 } else {true}").getClass());
        assertEquals(true, eval("if (false) { 1 } else {true;}"));
        assertEquals(2L, eval("if (false) { 1 } else {2} "));
        assertEquals(2L, eval("if (false) {22}else{ a=2;a; }"));
        assertEquals(2L, eval("if (false) {22}else{ a=2;a }"));
        assertEquals(2L, eval("x = if (1==3) { a=1;a; } else {b=2;b}"));
        assertEquals(2L, eval("x = if (1==3) { a=1;a; } else {b=2;b}; x"));
        ctx.popExecContext();
    }

}
