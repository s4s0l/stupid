package com.madisp.stupid;

import com.madisp.stupid.context.NamedBlockContext;
import com.madisp.stupid.context.TimeTrackingDereferencer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TimeTrackingTest extends BaseExpressionTest {
    private long maxTimeMillis = 1500;
    private TimeTrackingDereferencer tt = new TimeTrackingDereferencer(maxTimeMillis * 1000 * 1000, 100);

    @Override
    public void setUp() throws StupidRuntimeException {
        super.setUp();
        ctx.setDereferencer(tt);
        NamedBlockContext blocks = new NamedBlockContext();
        this.ctx.pushExecContext(blocks);
        blocks.addBlock("loop", (Block) eval("{|x| loop(x)}"));
        blocks.addBlock("long", (Block) eval("{|x| if(x>100) {100} else {long(x+1)}}"));
    }

    @Test(expected = StupidTimeExeededException.class)
    public void methodLoopWillCauseATimeExceededException() throws StupidRuntimeException {
        tt.resetTimer();
        assertEquals("???", eval("loop(1)"));
    }

    @Test
    public void longEvaluationShouldNotTimeout() throws StupidRuntimeException {
        tt.resetTimer();
        assertEquals(100, eval("long(1)"));
    }
}
