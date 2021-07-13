// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.plugin.multivm;

import javax.inject.Inject;
import org.gradle.api.Task;
import org.gradle.api.tasks.testing.Test;

/**
 * This represents a test task which is defunct, it relies on another task
 * for testing.
 *
 * @since 2020/10/07
 */
public class DefunctTestTask
	extends Test
{
	/**
	 * Initializes the test.
	 * 
	 * @since 2020/10/07
	 */
	@Inject
	public DefunctTestTask()
	{
		// Set details of this task
		this.setGroup("defunct");
		this.setDescription("Defunct test task, relies on another test task.");
		
		// Always runs
		this.onlyIf(new AlwaysTrue());
		
		// Make sure the task fails as quickly as possibles
		this.doFirst(this::action);
	}
	
	/**
	 * This just fails the task.
	 * 
	 * @param __task The running task.
	 * @since 2020/10/07
	 */
	private void action(Task __task)
	{
		throw new RuntimeException("The `test` task is defunct, " +
			"the task `testHosted` must be used instead. Failing.");
	}
}
