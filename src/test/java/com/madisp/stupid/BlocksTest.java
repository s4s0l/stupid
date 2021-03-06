package com.madisp.stupid;

import com.madisp.stupid.context.FixedMethodContext;
import com.madisp.stupid.context.NamedBlockContext;
import com.madisp.stupid.context.VarContext;
import com.madisp.stupid.validation.MethodFinder;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.madisp.stupid.context.VarContext.Type.CREATE_ON_SET_OR_GET;
import static org.junit.Assert.*;

public class BlocksTest extends BaseExpressionTest {
    private boolean run = false;

    public Runnable runnable = () -> run = true;

    private void run(Runnable run) {
        run.run();
    }

    @Test
    public void testSimpleBlock() throws StupidRuntimeException {
        assertNotNull(eval("{|x| x * x }"));
        assertEquals(Block.class, eval("{|x| x * x }").getClass());
        assertEquals(4L, eval("{|x| x * x}.(2)"));
        assertEquals(4L, eval("{|x| x * x;x;}.(4)"));

    }

    @Test
    public void testComments() throws Exception {
        assertEquals(4L, eval("{|x| //asdadasd\nx;}.(4)"));
        assertEquals("//test", eval("{|x| //asdadasd\n'//test';}.(4)"));
        assertEquals(4L, eval("{|x| x;//asdasd\n}.(4)"));
    }

    @Test
    public void testSingleMethodApply() throws StupidRuntimeException {
        assertFalse(run);
        run(runnable);
        assertTrue(run);
        run = false;

        assertFalse(false);
        eval("runnable.()");
        assertTrue(run);
        // restore run
        run = false;
    }


    @Test
    public void testBlockCallFromVar() throws StupidRuntimeException {
        Map<String, Object> vars = new HashMap<>();
        ctx.pushExecContext(new VarContext(CREATE_ON_SET_OR_GET, vars));
        eval("fun = {|x| x * x}");
        assertEquals(4L, eval("fun.(2)"));
        ctx.popExecContext();
    }

    @Test
    public void testBlockCallFromVarAsFunction() throws StupidRuntimeException {
        Map<String, Object> vars = new HashMap<>();
        ctx.pushExecContext(new VarContext(CREATE_ON_SET_OR_GET, vars));
        eval("fun = {|x| x * x}");
        assertEquals(4L, eval("fun(2)"));
        ctx.popExecContext();
    }

    @Test
    public void testBlockCallFromVarAsFunctionNested() throws StupidRuntimeException {
        Map<String, Object> vars = new HashMap<>();
        ctx.pushExecContext(new VarContext(CREATE_ON_SET_OR_GET, vars));
        eval("fun.x = {|x| x * x}");
        assertEquals(4L, eval("fun.x(2)"));
        ctx.popExecContext();
    }

    @Test(expected = StupidRuntimeException.class)
    public void testBlockCallFromVarDoesNotWorkWhenVarIsNotABlock() throws StupidRuntimeException {
        Map<String, Object> vars = new HashMap<>();
        ctx.pushExecContext(new VarContext(CREATE_ON_SET_OR_GET, vars));
        eval("fun.x = 1");
        try {
            assertEquals(4L, eval("fun.x(2)"));
        } finally {
            ctx.popExecContext();
        }
    }


    @Test
    public void testBlockCallFromNamedBlockContext() throws StupidRuntimeException {
        NamedBlockContext blocks = new NamedBlockContext();
        this.ctx.pushExecContext(blocks);
        blocks.addBlock("xxx", (Block) eval("{|x| x * x}"));
        assertEquals(4L, eval("xxx(2)"));
        assertEquals(4L, eval("xxx.(2)"));
        blocks.addBlock("second", "|x| x + x");
        Block block = blocks.addBlock("third", "|x| second(x) + x");
        assertEquals(
                MethodFinder.findRequiredMethods(block).iterator().next()
                , new MethodSignature("second", 1, true));

        Set<MethodSignature> supportedSignatures = blocks.getSupportedSignatures();
        assertTrue(supportedSignatures.contains(new MethodSignature("second", 1, true)));
        assertTrue(supportedSignatures.contains(new MethodSignature("third", 1, true)));
        assertTrue(supportedSignatures.contains(new MethodSignature("xxx", 1, true)));

        assertEquals(6L, eval("second(3)"));
        assertEquals("AAA", eval("third('A')"));

        ctx.popExecContext();
    }


    @Test
    public void supportingSignatures() throws StupidRuntimeException {
        NamedBlockContext blocks = new NamedBlockContext();
        FixedMethodContext fmc = new FixedMethodContext(
                FixedMethodContext.root("noarg", () -> "1"));
        this.ctx.pushExecContext(blocks);
        this.ctx.pushExecContext(fmc);
        blocks.addBlock("xxx", (Block) eval("{|x| x * x}"));
        blocks.addBlock("second", "|x| x + x");
        blocks.addBlock("third", "|x| second(x) + x");
        Set<MethodSignature> supportedSignatures = ctx.getSupportedSignatures();
        assertTrue(supportedSignatures.contains(new MethodSignature("second", 1, true)));
        assertTrue(supportedSignatures.contains(new MethodSignature("third", 1, true)));
        assertTrue(supportedSignatures.contains(new MethodSignature("xxx", 1, true)));
        assertTrue(supportedSignatures.contains(new MethodSignature("noarg", 0, true)));
        assertEquals("111", eval("third(noarg())"));
        ctx.popExecContext();
    }


}
