// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package squirreljme.mle;

import cc.squirreljme.jvm.mle.DebugShelf;
import cc.squirreljme.jvm.mle.TaskShelf;
import cc.squirreljme.jvm.mle.ThreadShelf;
import cc.squirreljme.jvm.mle.brackets.TracePointBracket;
import cc.squirreljme.runtime.cldc.debug.CallTraceUtils;
import java.util.Arrays;
import java.util.Objects;
import net.multiphasicapps.tac.TestSupplier;

/**
 * Tests that trace store and get is the same.
 *
 * @since 2020/07/08
 */
public class TestTraceStoreGet
	extends TestSupplier<Boolean>
{
	/** The message to use. */
	public static final String MESSAGE =
		"Squirrels are cute!";
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/08
	 */
	@Override
	public Boolean test()
		throws Throwable
	{
		// Get a trace that SquirrelJME would make since that is the only
		// trace that is acceptable
		TracePointBracket[] trace = DebugShelf.traceStack();
		
		// Set the trace
		ThreadShelf.setTrace(TestTraceStoreGet.MESSAGE, trace);
		
		String[] getMessage = new String[1];
		TracePointBracket[] getTrace = TaskShelf.getTrace(
			TaskShelf.current(), getMessage);
		
		// Set the message and check if both resolved traces equal the same
		this.secondary("samemessage", Objects.equals(
			TestTraceStoreGet.MESSAGE, getMessage[0]));
		return Arrays.equals(CallTraceUtils.resolveAll(trace),
			CallTraceUtils.resolveAll(getTrace));
	}
}
