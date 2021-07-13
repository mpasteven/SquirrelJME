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
import cc.squirreljme.jvm.mle.ThreadShelf;
import cc.squirreljme.jvm.mle.brackets.TaskBracket;
import cc.squirreljme.jvm.mle.brackets.VMThreadBracket;
import net.multiphasicapps.tac.TestRunnable;

/**
 * Tests that there are matches between the threads and tasks.
 *
 * @since 2021/05/08
 */
public class TestCurrentThreadMatch
	extends TestRunnable
{
	/**
	 * {@inheritDoc}
	 * @since 2021/05/08
	 */
	@Override
	public void test()
		throws Throwable
	{
		Thread javaThread = ThreadShelf.currentJavaThread();
		VMThreadBracket vmThread = ThreadShelf.currentVMThread();
		TaskBracket task = TaskShelf.current();
		
		this.secondary("vmthread-java",
			javaThread == ThreadShelf.toJavaThread(vmThread));
		
		this.secondary("java-vmthread",
			ThreadShelf.equals(vmThread, ThreadShelf.toVMThread(javaThread)));
		
		this.secondary("vmthread-task",
			TaskShelf.equals(task, ThreadShelf.vmThreadTask(vmThread)));
	}
}
