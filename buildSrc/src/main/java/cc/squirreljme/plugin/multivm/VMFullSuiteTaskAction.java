// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.plugin.multivm;

import cc.squirreljme.plugin.util.GradleJavaExecSpecFiller;
import cc.squirreljme.plugin.util.GuardedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.stream.Stream;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.SourceSet;
import org.gradle.process.ExecResult;

/**
 * This is the action for running the full entire suite in the emulator.
 *
 * @since 2020/10/17
 */
public class VMFullSuiteTaskAction
	implements Action<Task>
{
	/** The additional libraries to load. */
	public static final String LIBRARIES_PROPERTY =
		"full.libraries";
	
	/** The source set used. */
	public final String sourceSet;
	
	/** The virtual machine creating for. */
	protected final VMSpecifier vmType;
	
	/**
	 * Initializes the task.
	 * 
	 * @param __sourceSet The source set.
	 * @param __vmType The VM to make a ROM for.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/10/17
	 */
	public VMFullSuiteTaskAction(String __sourceSet, VMSpecifier __vmType)
		throws NullPointerException
	{
		if (__vmType == null || __sourceSet == null)
			throw new NullPointerException("NARG");
		
		this.vmType = __vmType;
		this.sourceSet = __sourceSet;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/10/17
	 */
	@Override
	public void execute(Task __task)
	{
		Project root = __task.getProject().getRootProject();
		
		// We need all of the libraries to load and to be available
		Collection<Path> libPath = new LinkedHashSet<>();
		for (Task dep : __task.getTaskDependencies().getDependencies(__task))
		{
			//System.err.printf("Task: %s %s%n", dep, dep.getClass());
			
			// Load executable library tasks from our own VM
			if (dep instanceof VMExecutableTask)
				for (File file : dep.getOutputs().getFiles())
					libPath.add(file.toPath());
		}
		
		// Additional items onto the library set?
		String exLib = System.getProperty(
			VMFullSuiteTaskAction.LIBRARIES_PROPERTY);
		if (exLib != null)
			for (Path p : VMHelpers.classpathDecode(exLib))
			{
				// Add contents of a given directory
				if (Files.isDirectory(p))
					try (Stream<Path> s = Files.list(p))
					{
						libPath.addAll(Arrays.asList(s.toArray(Path[]::new)));
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				
				// Add single path
				else
					libPath.add(p);
			}
		
		// Determine the initial classpath of the launcher, which is always
		// ran first
		Collection<Path> classPath = new LinkedHashSet<>();
		classPath.addAll(Arrays
			.asList(VMHelpers.runClassPath((VMExecutableTask)root.project(
				":modules:launcher").getTasks().getByName(TaskInitialization
					.task("lib", SourceSet.MAIN_SOURCE_SET_NAME,
					 this.vmType)),
				SourceSet.MAIN_SOURCE_SET_NAME, this.vmType)));
		
		// Debug these, just to ensure they work
		__task.getLogger().debug("LibPath: {}", libPath);
		__task.getLogger().debug("Classpath: {}", classPath);
		
		// Run the virtual machine with everything
		ExecResult exitResult = __task.getProject().javaexec(__spec ->
			{
				// Use filled JVM arguments
				this.vmType.spawnJvmArguments(__task, false,
					new GradleJavaExecSpecFiller(__spec),
					"javax.microedition.midlet.__MainHandler__",
					new LinkedHashMap<String, String>(),
					libPath.<Path>toArray(new Path[libPath.size()]),
					classPath.<Path>toArray(new Path[classPath.size()]),
					"cc.squirreljme.runtime.launcher.ui.MidletMain");
				
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
