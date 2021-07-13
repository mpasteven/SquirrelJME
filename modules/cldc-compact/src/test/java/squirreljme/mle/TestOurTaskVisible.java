// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package squirreljme.mle;

import cc.squirreljme.jvm.mle.TaskShelf;
import cc.squirreljme.jvm.mle.brackets.TaskBracket;
import net.multiphasicapps.tac.TestBoolean;

/**
 * Tests that our own task appears in the active list.
 *
 * @since 2020/07/09
 */
public class TestOurTaskVisible
	extends TestBoolean
{
	/**
	 * {@inheritDoc}
	 * @since 2020/07/09
	 */
	@Override
	public boolean test()
	{
		TaskBracket current = TaskShelf.current();
		TaskBracket[] tasks = TaskShelf.active();
		
		// Find our own matching task
		for (TaskBracket __task : tasks)
			if (TaskShelf.equals(current, __task))
				return true;
		
		return false;
	}
}
