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
import cc.squirreljme.plugin.util.GradleJavaExecSpecFiller;
import cc.squirreljme.plugin.util.GuardedOutputStream;
import cc.squirreljme.plugin.util.JavaExecSpecFiller;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.SourceSet;
import org.gradle.process.ExecResult;

/**
 * Represents the type of virtual machine to run.
 *
 * @since 2020/08/06
 */
public enum VMType
	implements VMSpecifier
{
	/** Hosted virtual machine. */
	HOSTED("Hosted", "jar",
		":emulators:emulator-base")
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/05/16
		 */
		@Override
		public void dumpLibrary(Task __task, boolean __isTest,
			InputStream __in, OutputStream __out)
			throws IOException, NullPointerException
		{
			throw new RuntimeException(this.name() + " cannot be dumped.");
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2020/08/15
		 */
		@Override
		public void processLibrary(Task __task, boolean __isTest,
			InputStream __in, OutputStream __out)
			throws IOException, NullPointerException
		{
			if (__in == null || __out == null)
				throw new NullPointerException("NARG");
			
			// Is just pure copy of the JAR
			VMHelpers.copy(__in, __out);
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2020/08/15
		 */
		@Override
		public void spawnJvmArguments(Task __task, boolean __debugEligible,
			JavaExecSpecFiller __execSpec, String __mainClass,
			Map<String, String> __sysProps,
			Path[] __libPath, Path[] __classPath, String... __args)
			throws NullPointerException
		{
			if (__task == null || __execSpec == null || __mainClass == null ||
				__sysProps == null || __classPath == null || __args == null)
				throw new NullPointerException("NARG");
				
			// Add our selection of libraries into the hosted environment so in
			// the event the active libraries are needed, they are available.
			Map<String, String> sysProps = new LinkedHashMap<>(__sysProps);
			sysProps.put("squirreljme.hosted.libraries",
				VMHelpers.classpathAsString(__libPath));
			sysProps.put("squirreljme.hosted.classpath",
				VMHelpers.classpathAsString(__classPath));
			
			// Can we directly refer to the emulator library already?
			// Only if it has not already been given, doing it here will enable
			// every sub-process quick access to the library
			if (!sysProps.containsKey("squirreljme.emulator.libpath"))
			{
				Path emuLib = VMHelpers.findEmulatorLib(__task);
				if (emuLib != null && Files.exists(emuLib))
					sysProps.put("squirreljme.emulator.libpath",
						emuLib.toString());
			}
			
			// Bring in any system defined properties we want to truly set?
			VMType.__copySysProps(sysProps);
			
			// Start with the base emulator class path
			List<Object> classPath = new ArrayList<>();
			Set<Path> vmSupportPath = new LinkedHashSet<>();
			for (File file : VMHelpers.projectRuntimeClasspath(
				__task.getProject().project(this.emulatorProject)))
			{
				vmSupportPath.add(file.toPath());
				classPath.add(file);
			}
			
			// Add all of the emulator outputs
			for (File file : __task.getProject().project(this.emulatorProject)
				.getTasks().getByName("jar").getOutputs().getFiles())
				vmSupportPath.add(file.toPath());
			
			// Use all the supporting path
			classPath.addAll(vmSupportPath);
			
			// Append the target class path on top of this, as everything
			// will be running directly
			classPath.addAll(Arrays.asList(__classPath));
			
			// Add the VM classpath so it can be recreated if we need to spawn
			// additional tasks such as by the launcher
			sysProps.put("squirreljme.hosted.vm.supportpath",
				VMHelpers.classpathAsString(vmSupportPath));
			sysProps.put("squirreljme.hosted.vm.classpath",
				VMHelpers.classpathAsString(VMHelpers.resolvePath(classPath)));
			
			// Declare system properties that are all the originally defined
			// system properties
			for (Map.Entry<String, String> e : __sysProps.entrySet())
				sysProps.put("squirreljme.orig." + e.getKey(), e.getValue());
			
			// Debug
			__task.getLogger().debug("Hosted SupportPath: {}", vmSupportPath);
			__task.getLogger().debug("Hosted ClassPath: {}", classPath);
			
			// Is this eligible to be ran under a debugger?
			if (__debugEligible)
			{
				// Does this run have a debugger specified already?
				// Prevent double debugger claim which would cause one to fail
				// to listen and thus break debugging
				boolean hasDebug = false;
				for (String arg : __args)
					if (arg.startsWith("-Xjdwp:"))
					{
						hasDebug = true;
						break;
					}
				
				// Enable debugging for the spawned hosted environment
				// Use an alternative variable to allow for VMFactory to be
				// debugged rather than just the emulated environment.
				String xjdwpProp = System.getProperty("squirreljme.xjdwp");
				String jdwpProp = (xjdwpProp != null ? xjdwpProp :
					System.getProperty("squirreljme.jdwp"));
				if ((xjdwpProp != null && !xjdwpProp.isEmpty()) ||
					(!hasDebug && jdwpProp != null && !jdwpProp.isEmpty()))
				{
					// Figure the hostname/port split
					int lastCol = jdwpProp.lastIndexOf(':');
					if (lastCol >= 0)
					{
						// Split hostname and port
						String host = jdwpProp.substring(0, lastCol);
						int port = Integer.parseInt(
							jdwpProp.substring(lastCol + 1));
						
						// Listen on a given port?
						if (host.isEmpty())
							__execSpec.setJvmArgs(Arrays.asList(String.format(
								"-agentlib:jdwp=transport=dt_socket," +
								"server=y,suspend=y,address=%d", port)));
						
						// Connect to remote VM
						else
							__execSpec.setJvmArgs(Arrays.asList(String.format(
								"-agentlib:jdwp=transport=dt_socket," +
								"server=n," +
								"address=%s:%d,suspend=y," +
								"onuncaught=y", host, port)));
					}
				}
			}
			
			// Use the classpath we previously determined
			__execSpec.classpath(classPath);
			
			// Main class was the directly specified class, we do not
			// need to handle the standard VM factory launcher
			__execSpec.setMain(__mainClass);
			
			// Use the passed arguments directly
			__execSpec.setArgs(Arrays.asList(__args));
			
			// Any desired system properties
			__execSpec.systemProperties(sysProps);
		}
	},
	
	/** SpringCoat virtual machine. */
	SPRINGCOAT("SpringCoat", "jar",
		":emulators:springcoat-vm")
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/05/16
		 */
		@Override
		public void dumpLibrary(Task __task, boolean __isTest,
			InputStream __in, OutputStream __out)
			throws IOException, NullPointerException
		{
			throw new RuntimeException(this.name() + " cannot be dumped.");
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2020/08/15
		 */
		@Override
		public void processLibrary(Task __task, boolean __isTest,
			InputStream __in, OutputStream __out)
			throws IOException, NullPointerException
		{
			if (__in == null || __out == null)
				throw new NullPointerException("NARG");
			
			// Is just pure copy of the JAR
			VMHelpers.copy(__in, __out);
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2020/08/15
		 */
		@Override
		public void spawnJvmArguments(Task __task, boolean __debugEligible,
			JavaExecSpecFiller __execSpec, String __mainClass,
			Map<String, String> __sysProps,
			Path[] __libPath, Path[] __classPath, String... __args)
			throws NullPointerException
		{
			// Use a common handler to execute the VM as the VMs all have
			// the same entry point handlers and otherwise
			this.spawnVmViaFactory(__task, __debugEligible, __execSpec,
				__mainClass, __sysProps, __libPath, __classPath, __args);
		}
	},
	
	/** SummerCoat virtual machine. */
	SUMMERCOAT("SummerCoat", "sqc",
		":emulators:summercoat-vm")
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/05/16
		 */
		@Override
		public void dumpLibrary(Task __task, boolean __isTest,
			InputStream __in, OutputStream __out)
			throws IOException, NullPointerException
		{
			if (__task == null || __in == null || __out == null)
				throw new NullPointerException("NARG");
			
			// Run the specified command
			this.__aotCommand(__task, __in, __out,
				"dumpCompile", null);
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2020/08/15
		 */
		@Override
		public void processLibrary(Task __task, boolean __isTest,
			InputStream __in, OutputStream __out)
			throws NullPointerException
		{
			if (__task == null || __in == null || __out == null)
				throw new NullPointerException("NARG");
				
			// Need to access the config for ROM building
			SquirrelJMEPluginConfiguration config =
				SquirrelJMEPluginConfiguration
				.configuration(__task.getProject());
				
			// Potential extra arguments
			Collection<String> args = new ArrayList<>();
			
			// Is this a boot loader? This is never valid for tests as they
			// are just extra libraries, it does not make sense to have them
			// be loadable.
			if (!__isTest && config.isBootLoader)
				args.add("-boot");
				
			// Run the specified command
			this.__aotCommand(__task, __in, __out,
				"compile", args);
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2020/11/21
		 */
		@Override
		public Iterable<Task> processLibraryDependencies(
			VMExecutableTask __task)
			throws NullPointerException
		{
			Project project = __task.getProject().getRootProject()
				.project(":modules:aot-" +
					this.vmName(VMNameFormat.LOWERCASE));
			Project rootProject = project.getRootProject();
			
			// Make sure the AOT compiler is always up to date when this is
			// ran, otherwise things can be very weird if it is not updated
			// which would not be a good thing at all
			Collection<Task> rv = new LinkedList<>();
			for (ProjectAndTaskName task : VMHelpers.runClassTasks(project,
				SourceSet.MAIN_SOURCE_SET_NAME, VMType.HOSTED))
			{
				Project taskProject = rootProject.project(task.project);
				
				// Depends on all of the classes, not just the libraries, for
				// anything the AOT compiler uses. If the compiler changes we
				// need to make sure the updated compiler is used!
				rv.add(taskProject.getTasks().getByName("classes"));
				rv.add(taskProject.getTasks().getByName("jar"));
				
				// The library that makes up the task is important
				rv.add(taskProject.getTasks().getByName(task.task));
			}
			
			// Make sure the hosted environment is working since it needs to
			// be kept up to date as well
			for (Task task : new VMEmulatorDependencies(__task,
				VMType.HOSTED).call())
				rv.add(task);
			
			return rv;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2020/11/27
		 */
		@Override
		public void processRom(Task __task, OutputStream __out,
			Collection<Path> __libs)
			throws IOException, NullPointerException
		{
			if (__task == null || __out == null || __libs == null)
				throw new NullPointerException("NARG");
				
			// Setup arguments for compilation
			Collection<String> args = new ArrayList<>();
			
			// Put down paths to libraries to link together
			for (Path path : __libs)
				args.add(path.toString());
				
			// Run the specified command
			this.__aotCommand(__task, null, __out,
				"rom", args);
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2020/08/15
		 */
		@Override
		public void spawnJvmArguments(Task __task, boolean __debugEligible,
			JavaExecSpecFiller __execSpec, String __mainClass,
			Map<String, String> __sysProps,
			Path[] __libPath, Path[] __classPath, String... __args)
			throws NullPointerException
		{
			// Use a common handler to execute the VM as the VMs all have
			// the same entry point handlers and otherwise
			this.spawnVmViaFactory(__task, __debugEligible, __execSpec,
				__mainClass, __sysProps, __libPath, __classPath, __args);
		}
	},
	
	/* End. */
	;
	
	/** Prefix for system properties to appear in the VM. */
	private static final String _JVM_KEY_PREFIX =
		"squirreljme.sysprop.";
	
	/** The proper name of the VM. */
	public final String properName;
	
	/** The extension for the VM. */
	public final String extension;
	
	/** The project used for the emulator. */
	public final String emulatorProject;
	
	/**
	 * Returns the proper name of the virtual machine.
	 * 
	 * @param __properName The proper name of the VM.
	 * @param __extension The library extension.
	 * @param __emulatorProject The project used for the emulator.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/08/06
	 */
	VMType(String __properName, String __extension,
		String __emulatorProject)
		throws NullPointerException
	{
		if (__properName == null || __extension == null ||
			__emulatorProject == null)
			throw new NullPointerException("NARG");
		
		this.properName = __properName;
		this.extension = __extension;
		this.emulatorProject = __emulatorProject;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/08/16
	 */
	@Override
	public final String emulatorProject()
	{
		return this.emulatorProject;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/16
	 */
	@Override
	public boolean hasDumping()
	{
		return this == VMType.SUMMERCOAT;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/08/23
	 */
	@Override
	public final boolean hasRom()
	{
		return this == VMType.SUMMERCOAT;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/08/07
	 */
	@Override
	public String outputLibraryName(Project __project, String __sourceSet)
		throws NullPointerException
	{
		if (__project == null || __sourceSet == null)
			throw new NullPointerException("NARG");
		
		// The main library should never show its original source set
		if (SourceSet.MAIN_SOURCE_SET_NAME.equals(__sourceSet))
			return __project.getName() + "." + this.extension;
		
		// Otherwise include the source sets
		return __project.getName() + "-" + __sourceSet + "." + this.extension;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/11/27
	 */
	@Override
	public String outputRomName(String __sourceSet)
		throws NullPointerException
	{
		if (SourceSet.MAIN_SOURCE_SET_NAME.equals(__sourceSet))
			return "squirreljme." + this.extension;
		return "squirreljme-" + __sourceSet + "." + this.extension;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/11/21
	 */
	@Override
	public Iterable<Task> processLibraryDependencies(
		VMExecutableTask __task)
		throws NullPointerException
	{
		return Collections.emptyList();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/11/27
	 */
	@Override
	public void processRom(Task __task, OutputStream __out,
		Collection<Path> __libs)
		throws IOException, NullPointerException
	{
		throw new RuntimeException(this.name() + " is not ROM capable.");
	}
	
	/**
	 * Spawns a virtual machine using the standard {@code VmFactory} class.
	 * 
	 * @param __task The task being executed, may be used as context.
	 * @param __debugEligible Is this eligible to be ran under the debugger?
	 * @param __execSpec The execution specification.
	 * @param __mainClass The main class to execute.
	 * @param __sysProps The system properties to define.
	 * @param __libPath The library path to use.
	 * @param __classPath The class path of the execution target.
	 * @param __args Arguments to the started program.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/08/15
	 */
	public void spawnVmViaFactory(Task __task, boolean __debugEligible,
		JavaExecSpecFiller __execSpec, String __mainClass,
		Map<String, String> __sysProps, Path[] __libPath,
		Path[] __classPath, String[] __args)
		throws NullPointerException
	{
		if (__task == null || __execSpec == null || __mainClass == null ||
			__sysProps == null || __libPath == null || __classPath == null ||
			__args == null)
			throw new NullPointerException("NARG");
		
		// Copy system properties
		Map<String, String> sysProps = new LinkedHashMap<>(__sysProps);
		
		// Bring in any system defined properties we want to truly set?
		VMType.__copySysProps(sysProps);
		
		// Determine the class-path for the emulator
		Set<Path> vmClassPath = new LinkedHashSet<>();
		for (File file : VMHelpers.projectRuntimeClasspath(
			__task.getProject().project(this.emulatorProject)))
			vmClassPath.add(file.toPath());
		
		// Make sure the base emulator is available as well
		for (File file : VMHelpers.projectRuntimeClasspath(
			__task.getProject().findProject(":emulators:emulator-base")))
			vmClassPath.add(file.toPath());
		
		// Add all of the emulator outputs
		for (File file : __task.getProject().project(this.emulatorProject)
			.getTasks().getByName("jar").getOutputs().getFiles())
			vmClassPath.add(file.toPath());
		
		// Debug
		__task.getLogger().debug("VM ClassPath: {}", vmClassPath);
		
		// Build arguments to the VM
		Collection<String> vmArgs = new LinkedList<>();
		
		// Add emulator to launch
		vmArgs.add("-Xemulator:" + this.vmName(VMNameFormat.LOWERCASE));
		
		// Add library paths, suites that are available for consumption
		vmArgs.add("-Xlibraries:" + VMHelpers.classpathAsString(__libPath));
		
		// Enable JDWP debugging?
		if (__debugEligible)
		{
			String jdwpProp = System.getProperty("squirreljme.jdwp");
			if (jdwpProp != null)
				vmArgs.add("-Xjdwp:" + jdwpProp);
		}
		
		// Change threading model?
		String threadModel = System.getProperty("squirreljme.thread");
		if (threadModel != null)
			vmArgs.add("-Xthread:" + threadModel);
		
		// Determine where profiler snapshots are to go, try to use the
		// profiler directory for that
		Path profilerDir = ((__task instanceof VMExecutableTask) ?
			VMHelpers.profilerDir(__task.getProject(), this,
			((VMExecutableTask)__task).getSourceSet()).get() :
			__task.getProject().getBuildDir().toPath());
		
		// Use the main class name unless this is a test, so that they are
		// named better
		String profilerClass = (__mainClass.equals(
			VMHelpers.SINGLE_TEST_RUNNER) && __args.length > 0 ?
			__args[0] : __mainClass);
		vmArgs.add("-Xsnapshot:" + profilerDir.resolve(
			__task.getProject().getName() + "_" +
			profilerClass.replace('.', '-') + ".nps"));
		
		// Class path for the target program to launch
		vmArgs.add("-classpath");
		vmArgs.add(VMHelpers.classpathAsString(__classPath));
		
		// Any system properties
		for (Map.Entry<String, String> sysProp : sysProps.entrySet())
			vmArgs.add("-D" + sysProp.getKey() + "=" + sysProp.getValue());
		
		// Main class of the target to run
		vmArgs.add(__mainClass);
		
		// Any arguments to the target run
		vmArgs.addAll(Arrays.asList(__args));
		
		// Classpath used for execution
		Path[] classPath = vmClassPath.<Path>toArray(
			new Path[vmClassPath.size()]);
		
		// Launching is effectively the same as the hosted run but with the
		// VM here instead. System properties are passed through so that the
		// holding VM and the sub-VM share the same properties.
		VMType.HOSTED.spawnJvmArguments(__task, __debugEligible, __execSpec,
			"cc.squirreljme.emulator.vm.VMFactory", __sysProps,
			__libPath, classPath,
			vmArgs.<String>toArray(new String[vmArgs.size()]));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/08/07
	 */
	@Override
	public String vmName(VMNameFormat __format)
		throws NullPointerException
	{
		String properName = this.properName;
		switch (__format)
		{
			case LOWERCASE:
				return properName.toLowerCase();
				
			case CAMEL_CASE:
				return Character.toLowerCase(properName.charAt(0)) +
					properName.substring(1);
				
			case PROPER_NOUN:
			default:
				return properName;
		}
	}
	
	/**
	 * @param __task The task being run for.
	 * @param __in The input source (optional).
	 * @param __out The output source (optional).
	 * @param __command The name of the ROM.
	 * @param __args The arguments for the AOT command.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/05/16
	 */
	void __aotCommand(Task __task, InputStream __in, OutputStream __out,
		String __command, Iterable<String> __args)
		throws NullPointerException
	{
		if (__task == null || __command == null)
			throw new NullPointerException("NARG");
		
		// Class path is of the compiler target, it does not matter
		Path[] classPath = VMHelpers.runClassPath(__task.getProject()
			.getRootProject().project(":modules:aot-" +
				this.vmName(VMNameFormat.LOWERCASE)),
			SourceSet.MAIN_SOURCE_SET_NAME, VMType.HOSTED);
		
		// Setup arguments for compilation
		Collection<String> args = new ArrayList<>();
		
		// The engine to use
		args.add("-Xcompiler:" + this.vmName(VMNameFormat.LOWERCASE));
		
		// The name of this JAR
		args.add("-Xname:" + __task.getProject().getName());
		
		// Our run command and any additional arguments
		args.add(__command);
		if (__args != null)
			for (String arg : __args)
				args.add(arg);
		
		// Call the AOT backend
		ExecResult exitResult = __task.getProject().javaexec(__spec ->
			{
				// Figure out the arguments to the JVM, it does not matter
				// what the classpath is
				VMType.HOSTED.spawnJvmArguments(__task, false,
					new GradleJavaExecSpecFiller(__spec),
					"cc.squirreljme.jvm.aot.Main",
					Collections.emptyMap(),
					classPath,
					classPath,
					args.toArray(new String[args.size()]));
				
				// Use the error stream directory
				__spec.setErrorOutput(new GuardedOutputStream(System.err));
				
				// Use the given input
				if (__in != null)
					__spec.setStandardInput(__in);
				
				// Use output if specified
				if (__out != null)
					__spec.setStandardOutput(__out);
				
				// Ignore error states, let us handle it instead of Gradle
				// so we could handle multiple different exit codes.
				__spec.setIgnoreExitValue(true);
			});
		
		// Processing the library did not work?
		int code;
		if ((code = exitResult.getExitValue()) != 0)
			throw new RuntimeException(String.format(
				"Failed to run the AOT processor for %s. " +
				 "(exit code %d, %s %s).",
				__task.getName(), code, __command,
				(__args == null ? "" : __args)));
	}
	
	/**
	 * Copies system properties with the given prefix into the system
	 * properties for the VM.
	 * 
	 * @param __sysProps The system properties to copy into.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/03/08
	 */
	private static void __copySysProps(Map<String, String> __sysProps)
		throws NullPointerException
	{
		if (__sysProps == null)
			throw new NullPointerException("NARG");
		
		// Copy any that are set
		for (Map.Entry<Object, Object> prop :
			System.getProperties().entrySet())
		{
			// Only match certain keys
			String baseKey = Objects.toString(prop.getKey());
			if (!baseKey.startsWith(VMType._JVM_KEY_PREFIX))
				continue;
			
			// Add it in
			__sysProps.put(
				baseKey.substring(VMType._JVM_KEY_PREFIX.length()),
				Objects.toString(prop.getValue()));
		}
	}
}
