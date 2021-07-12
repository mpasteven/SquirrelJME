// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package squirreljme.summercoat;

import cc.squirreljme.runtime.cldc.debug.Debugging;
import net.multiphasicapps.tac.TestRunnable;

/**
 * This tests that the system time can be obtained without error.
 *
 * @since 2019/05/23
 */
public class TestSystemTime
	extends TestRunnable
{
	/**
	 * {@inheritDoc}
	 * @since 2019/05/23
	 */
	@Override
	public void test()
	{
		throw Debugging.todo();
		/*
		Assembly.sysCall(SystemCallIndex.TIME_MILLI_WALL);
		this.secondary("mw", Assembly.sysCallV(SystemCallIndex.ERROR_GET,
			SystemCallIndex.TIME_MILLI_WALL));
		
		Assembly.sysCall(SystemCallIndex.TIME_NANO_MONO);
		this.secondary("nm", Assembly.sysCallV(SystemCallIndex.ERROR_GET,
			SystemCallIndex.TIME_NANO_MONO));
		 */
	}
}

