// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.plugin.multivm;

import java.util.Iterator;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskContainer;

/**
 * This is used to initialize the Gradle tasks for projects accordingly.
 *
 * @since 2020/08/07
 */
public final class TaskInitialization
{
	/** Source sets that are used. */
	private static final String[] _SOURCE_SETS =
		new String[]{SourceSet.MAIN_SOURCE_SET_NAME,
			SourceSet.TEST_SOURCE_SET_NAME};
	
	/**
	 * Not used.
	 * 
	 * @since 2020/08/07
	 */
	private TaskInitialization()
	{
	}
	
	/**
	 * Initializes the project for the tasks and such 
	 * 
	 * @param __project The project to initialize for.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/08/07
	 */
	public static void initialize(Project __project)
		throws NullPointerException
	{
		if (__project == null)
			throw new NullPointerException("NARG");
		
		// Initialize or both main classes and such
		for (String sourceSet : TaskInitialization._SOURCE_SETS)
			TaskInitialization.initialize(__project, sourceSet);
		
		// Disable the test task, since it is non-functional
		__project.getTasks().replace("test", DefunctTestTask.class);
		
		Task check = __project.getTasks().getByName("check");
		for (Iterator<Object> it = check.getDependsOn().iterator();
			it.hasNext();)
		{
			// Get the root item, if a provider of one
			Object item = it.next();
			if (item instanceof Provider)
				item = ((Provider<?>)item).get();
			
			// Only consider tasks
			if (!(item instanceof Task))
				continue;
			
			// Remove the test task, since we do not want it to run here
			if ("test".equals(((Task)item).getName()))
				it.remove();
		}
	}
	
	/**
	 * Initializes the source set for the given project.
	 * 
	 * @param __project The project to initialize for.
	 * @param __sourceSet The source set to be initialized.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/08/07
	 */
	public static void initialize(Project __project, String __sourceSet)
		throws NullPointerException
	{
		if (__project == null || __sourceSet == null)
			throw new NullPointerException("NARG");
		
		// Initialize for each VM
		for (VMType vmType : VMType.values())
			TaskInitialization.initialize(__project, __sourceSet, vmType);
	}
	
	/**
	 * Initializes the virtual machine for the given project's sourceset.
	 * 
	 * @param __project The project to initialize for.
	 * @param __sourceSet The source set.
	 * @param __vmType The virtual machine type.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/08/07
	 */
	public static void initialize(Project __project, String __sourceSet,
		VMSpecifier __vmType)
		throws NullPointerException
	{
		if (__project == null || __sourceSet == null || __vmType == null)
			throw new NullPointerException("NARG");
		
		// Everything will be working on these tasks
		TaskContainer tasks = __project.getTasks();
		
		// Library that needs to be constructed so execution happens properly
		VMLibraryTask libTask = tasks.create(
			TaskInitialization.task("lib", __sourceSet, __vmType),
			VMLibraryTask.class, __sourceSet, __vmType);
		
		// Is dumping available?
		if (__vmType.hasDumping())
			tasks.create(
				TaskInitialization.task("dump", __sourceSet, __vmType),
				VMDumpLibraryTask.class, __sourceSet, __vmType, libTask);
		
		// Running the target
		if (__sourceSet.equals(SourceSet.MAIN_SOURCE_SET_NAME))
			tasks.create(
				TaskInitialization.task("run", __sourceSet, __vmType),
				VMRunTask.class, __sourceSet, __vmType, libTask);
		
		// Testing the target
		else if (__sourceSet.equals(SourceSet.TEST_SOURCE_SET_NAME))
			tasks.create(
				TaskInitialization.task("test", __sourceSet, __vmType),
				VMTestTask.class, __sourceSet, __vmType, libTask);
	}
	
	/**
	 * Initializes the full-suite run which selects every API and library
	 * module available, along with allowing an external 3rd library classpath
	 * launching.
	 * 
	 * @param __project The root project.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/10/17
	 */
	public static void initializeFullSuiteTask(Project __project)
		throws NullPointerException
	{
		if (__project == null)
			throw new NullPointerException("NARG");
		
		for (VMType vmType : VMType.values())
			TaskInitialization.initializeFullSuiteTask(__project, vmType);
	}
	
	/**
	 * Initializes the full-suite run which selects every API and library
	 * module available, along with allowing an external 3rd library classpath
	 * launching.
	 * 
	 * @param __project The root project.
	 * @param __vmType The virtual machine type.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/10/17
	 */
	private static void initializeFullSuiteTask(Project __project,
		VMType __vmType)
		throws NullPointerException
	{
		if (__project == null || __vmType == null)
			throw new NullPointerException("NARG");
		
		__project.getTasks().create(
			TaskInitialization.task("full", "", __vmType),
			VMFullSuite.class, __vmType);
	}
	
	/**
	 * Initializes ROM tasks for the given base project.
	 * 
	 * @param __project The root project.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/08/23
	 */
	public static void romTasks(Project __project)
		throws NullPointerException
	{
		if (__project == null)
			throw new NullPointerException("NARG");
			
		// Initialize or both main classes and such
		for (String sourceSet : TaskInitialization._SOURCE_SETS)
			for (VMType vmType : VMType.values())
				TaskInitialization.romTasks(__project, sourceSet, vmType);
	}
	
	/**
	 * Initializes ROM tasks for the given base project.
	 * 
	 * @param __project The root project.
	 * @param __sourceSet The source set to use.
	 * @param __vmType The virtual machine type used.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/08/23
	 */
	private static void romTasks(Project __project, String __sourceSet,
		VMType __vmType)
		throws NullPointerException
	{
		if (__project == null || __sourceSet == null || __vmType == null)
			throw new NullPointerException("NARG");
			
		// Everything will be working on these tasks
		TaskContainer tasks = __project.getTasks();
		
		// Does the VM utilize ROMs?
		if (__vmType.hasRom())
			tasks.create(
				TaskInitialization.task("rom", __sourceSet, __vmType),
				VMRomTask.class, __sourceSet, __vmType);
	}
	
	/**
	 * Builds a name for a task.
	 * 
	 * @param __name The task name.
	 * @param __sourceSet The source set for the task base.
	 * @param __vmType The type of virtual machine used.
	 * @return A string representing the task.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/08/07
	 */
	public static String task(String __name, String __sourceSet,
		VMSpecifier __vmType)
		throws NullPointerException
	{
		if (__name == null || __sourceSet == null || __vmType == null)
			throw new NullPointerException("NARG");
		
		// If this is the main source set, never include the source set as
		// it becomes implied. Additionally if the name and the source set
		// are the same, reduce the confusion so there is no "testTestHosted".
		if (__sourceSet.equals(SourceSet.MAIN_SOURCE_SET_NAME) ||
			__sourceSet.equals(__name) || __sourceSet.isEmpty())
			return __name + __vmType.vmName(VMNameFormat.PROPER_NOUN);
		
		// Otherwise include it
		return __name +
			Character.toUpperCase(__sourceSet.charAt(0)) +
			__sourceSet.substring(1) +
			__vmType.vmName(VMNameFormat.PROPER_NOUN);
	}
}
