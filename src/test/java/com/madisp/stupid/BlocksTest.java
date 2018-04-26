package com.madisp.stupid;

import org.junit.Test;

import static org.junit.Assert.*;

public class BlocksTest extends BaseExpressionTest {
	private boolean run = false;

	private Runnable runnable = () -> run = true;

	private void run(Runnable run) {
		run.run();
	}

	@Test
	public void testSimpleBlock() {
		assertNotNull(eval("{|x| x * x }"));
		assertEquals(Block.class, eval("{|x| x * x }").getClass());
		assertEquals(4, eval("{|x| x * x}.(2)"));
		assertEquals(4, eval("{|x| x * x;x;}.(4)"));
	}

	@Test
	public void testSingleMethodApply() {
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

	// TODO commented out for now, I don't know if we want to have blocks to SAM's yet.
//	@Test
//	public void testBlockToSingleMethodInterface() throws Exception {
//		assertEquals(false, run);
//		eval("run(runnable)");
//		assertEquals(true, run);
//		run = false;
//
//		assertEquals(false, run);
//		eval("run({run = true})");
//		assertEquals(true, run);
//		run = false;
//	}
}
