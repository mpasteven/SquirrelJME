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
import cc.squirreljme.plugin.swm.JavaMEMidlet;
import cc.squirreljme.plugin.util.GradleJavaExecSpecFiller;
import cc.squirreljme.plugin.util.GuardedOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.gradle.api.Action;
import org.gradle.api.Task;
import org.gradle.process.ExecResult;

/**
 * Runs the program within the virtual machine.
 *
 * @since 2020/08/07
 */
public class VMRunTaskAction
	implements Action<Task>
{
	/** The source set used. */
	protected final String sourceSet;
	
	/** The virtual machine type. */
	protected final VMSpecifier vmType;
	
	/**
	 * Initializes the task action.
	 * 
	 * @param __sourceSet The source set to use.
	 * @param __vmType The virtual machine type.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/08/16
	 */
	public VMRunTaskAction(String __sourceSet,
		VMSpecifier __vmType)
		throws NullPointerException
	{
		if (__sourceSet == null || __vmType == null)
			throw new NullPointerException("NARG");
		
		this.sourceSet = __sourceSet;
		this.vmType = __vmType;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/08/07
	 */
	@Override
	public void execute(Task __task)
	{
		// Need this to get the program details
		SquirrelJMEPluginConfiguration config =
			SquirrelJMEPluginConfiguration.configuration(__task.getProject());
			
		// Gather the class path to use for target execution, this is all the
		// SquirrelJME modules this depends on
		VMSpecifier vmType = this.vmType;
		Path[] classPath = VMHelpers.runClassPath(
			(VMExecutableTask)__task, this.sourceSet, vmType);
		
		// Debug
		__task.getLogger().debug("Classpath: {}", Arrays.asList(classPath));
		
		// Determine the main entry class or MIDlet to use
		JavaMEMidlet midlet = JavaMEMidlet.find(config.midlets);
		String mainClass = VMHelpers.mainClass(config, midlet);
		
		// Debug
		__task.getLogger().debug("MIDlet: {}", midlet);
		__task.getLogger().debug("MainClass: {}", mainClass);
		
		// If executing a MIDlet, then the single main argument is the actual
		// name of the MIDlet to execute
		List<String> args = new ArrayList<>();
		if (midlet != null)
			args.add(midlet.mainClass);
		
		// Debug
		__task.getLogger().debug("Target Working Dir: {}",
			System.getProperty("user.dir"));
		
		// Execute the virtual machine, if the exit status is non-zero then
		// the task execution will be considered as a failure
		ExecResult exitResult = __task.getProject().javaexec(__spec ->
			{
				// Use filled JVM arguments
				vmType.spawnJvmArguments(__task, true,
					new GradleJavaExecSpecFiller(__spec), mainClass,
					Collections.<String, String>emptyMap(),
					classPath, classPath,
					args.<String>toArray(new String[args.size()]));
				
				// Use these streams directly
				__spec.setStandardOutput(new GuardedOutputStream(System.out));
				__spec.setErrorOutput(new GuardedOutputStream(System.err));
			});
		
		// Did the task fail?
		int exitValue = exitResult.getExitValue();
		if (exitValue != 0)
			throw new RuntimeException("Task exited with: " + exitValue);
	}
}
