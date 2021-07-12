// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.plugin.multivm;

import java.io.File;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.gradle.api.Task;
import org.gradle.api.specs.Spec;

/**
 * This is a check to determine if a task is up to date or not for the library
 * compiler.
 *
 * @since 2020/11/28
 */
public class VMLibraryTaskUpToDate
	implements Spec<Task>
{
	/** The virtual machine to target. */
	protected final VMSpecifier vmType;
	
	/**
	 * Initializes the task dependencies.
	 * 
	 * @param __vmType The virtual machine to target.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/28
	 */
	public VMLibraryTaskUpToDate(VMSpecifier __vmType)
		throws NullPointerException
	{
		if (__vmType == null)
			throw new NullPointerException("NARG");
		
		this.vmType = __vmType;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/11/28
	 */
	@Override
	public boolean isSatisfiedBy(Task __task)
	{
		// Get the times the output was last changed
		Set<Instant> taskOuts = new HashSet<>(); 
		for (File f : __task.getOutputs().getFiles().getFiles())
			taskOuts.add(Instant.ofEpochMilli(f.lastModified()));
		
		// Determine if any part of the compiler was not considered up-to-date
		for (Task dep : this.vmType.processLibraryDependencies(
			(VMExecutableTask)__task))
		{
			for (File f : dep.getOutputs().getFiles().getFiles())
			{
				Instant fileTime = Instant.ofEpochMilli(f.lastModified());
				
				// One of the JARs the compiler uses is newer than the JAR we
				// made, so this is not up to date!
				for (Instant taskOut : taskOuts)
					if (fileTime.compareTo(taskOut) > 0)
						return false;
			}
		}
		
		// Considered up to date
		return true;
	}
}
