// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.plugin.multivm;

import cc.squirreljme.plugin.SquirrelJMEPluginConfiguration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.Callable;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.SourceSet;

/**
 * Dependencies used for the full SquirrelJME suite.
 *
 * @since 2020/10/17
 */
public class VMFullSuiteDepends
	implements Callable<Iterable<Task>>
{
	/** The task to execute for. */
	protected final Task task;
	
	/** The source set used. */
	protected final String sourceSet;
	
	/** The virtual machine creating for. */
	protected final VMSpecifier vmType;
	
	/**
	 * Initializes the dependency grabber.
	 * 
	 * @param __task The task to run off.
	 * @param __vmType The virtual machine type.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/10/17
	 */
	public VMFullSuiteDepends(Task __task, String __sourceSet,
		VMSpecifier __vmType)
		throws NullPointerException
	{
		if (__task == null || __sourceSet == null || __vmType == null)
			throw new NullPointerException("NARG");
		
		this.task = __task;
		this.sourceSet = __sourceSet;
		this.vmType = __vmType;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/10/17
	 */
	@Override
	public Iterable<Task> call()
	{
		Project root = this.task.getProject().getRootProject();
		Collection<Task> tasks = new LinkedHashSet<>();
		
		// We need the emulator to be built and working before we can actually
		// run our full suite accordingly
		Task emulJar = root.project(this.vmType.emulatorProject()).getTasks()
			.findByName("jar");
		if (emulJar != null)
			tasks.add(emulJar);
		
		// Which source sets should be used
		List<String> sourceSets;
		if (!this.sourceSet.equals(SourceSet.MAIN_SOURCE_SET_NAME))
			sourceSets = Arrays.asList(SourceSet.MAIN_SOURCE_SET_NAME,
				this.sourceSet);
		else
			sourceSets = Collections.singletonList(this.sourceSet);
		
		// Go through every single project, and try to use it as a dependency
		for (Project project : root.getAllprojects())
		{
			// Ignore non-SquirrelJME projects
			SquirrelJMEPluginConfiguration config =
				SquirrelJMEPluginConfiguration.configurationOrNull(project);
			if (config == null)
				continue;
			
			// Include whatever libraries are needed for the source set
			for (String sourceSet : sourceSets)
			{
				// Find the associated library task
				Task libTask = project.getTasks().findByName(TaskInitialization
					.task("lib", sourceSet,
						this.vmType));
				if (libTask == null)
					continue;
				
				// Use all of their dependencies, if not yet added
				for (VMLibraryTask subDep : new VMRunDependencies(
					(VMExecutableTask)libTask, sourceSet,
					this.vmType).call())
					tasks.add(subDep);
			}
		}
		
		return Collections.unmodifiableCollection(tasks);
	}
}
