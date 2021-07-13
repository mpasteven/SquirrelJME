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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.gradle.api.Task;
import org.gradle.api.specs.Spec;

/**
 * This is used to check if a test is considered invalid because one of the
 * inputs is no longer up to date.
 *
 * @since 2021/02/23
 */
public class VMRunUpToDateWhen
	implements Spec<Task>
{
	/** The source set working under. */
	protected final String sourceSet;
	
	/** The virtual machine type. */
	protected final VMSpecifier vmType;
	
	/**
	 * Initializes the handler.
	 * 
	 * @param __sourceSet The source set.
	 * @param __vmType The virtual machine this is created for.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/02/23
	 */
	public VMRunUpToDateWhen(String __sourceSet, VMSpecifier __vmType)
		throws NullPointerException
	{
		if (__sourceSet == null || __vmType == null)
			throw new NullPointerException("NARG");
		
		this.sourceSet = __sourceSet;
		this.vmType = __vmType;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/23
	 */
	@Override
	public boolean isSatisfiedBy(Task __task)
	{
		// Get the times the output was last changed
		Set<Instant> taskOuts = new HashSet<>(); 
		for (File f : __task.getOutputs().getFiles().getFiles())
			taskOuts.add(Instant.ofEpochMilli(f.lastModified()));
		
		// Determine if any of our parent dependencies were out of date
		for (Path dep : VMHelpers.runClassPath((VMExecutableTask)__task,
			this.sourceSet, this.vmType))
		{
			Instant fileTime;
			try
			{
				fileTime = Files.getLastModifiedTime(dep).toInstant();
			}
			catch (IOException ignored)
			{
				continue;
			}
			
			// One of our runtime libraries is newer than our own outputs
			for (Instant taskOut : taskOuts)
				if (fileTime.compareTo(taskOut) > 0)
					return false;
		}
		
		// Considered up to date
		return true;
	}
}
