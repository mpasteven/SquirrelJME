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
import cc.squirreljme.plugin.util.FileLocation;
import cc.squirreljme.plugin.util.TestDetection;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Pattern;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.SourceSet;
import org.gradle.jvm.tasks.Jar;

/**
 * Helpers for the multi-VM handlers.
 *
 * @since 2020/08/07
 */
@SuppressWarnings("OverlyComplexClass")
public final class VMHelpers
{
	/** The class used for single test runs. */
	public static final String SINGLE_TEST_RUNNER =
		"net.multiphasicapps.tac.MainSingleRunner";
	
	/** Main configurations. */
	private static final String[] _MAIN_CONFIGS =
		new String[]{"api", "implementation"};
	
	/** Test configurations. */
	private static final String[] _TEST_CONFIGS =
		new String[]{"testImplementation", "testImplementation"};
	
	/** Declaration of multi-test parameters. */
	private static final String _MULTI_PARAMETERS_KEY =
		"multi-parameters";
	
	/* Copy buffer size. */
	public static final int COPY_BUFFER =
		4096;
	
	/**
	 * Not used.
	 * 
	 * @since 2020/08/07
	 */
	private VMHelpers()
	{
	}
	
	/**
	 * Returns a collection of the tests that are available.
	 * 
	 * @param __project The project to check.
	 * @param __sourceSet The source set for the project.
	 * @return The list of available tests.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/08/07
	 */
	public static Map<String, CandidateTestFiles> availableTests(
		Project __project, String __sourceSet)
		throws NullPointerException
	{
		if (__project == null || __sourceSet == null)
			throw new NullPointerException("NARG");
		
		// Mappings of both source and expected files
		Set<String> names = new TreeSet<>();
		Map<String, FileLocation> sources = new HashMap<>();
		Map<String, FileLocation> expects = new HashMap<>();
		
		// Scan through every file and match sources and expected tests
		for (FileLocation file :
			TestDetection.sourceSetFiles(__project, __sourceSet))
		{
			// If this is a MIME encoded file, normalize the name so it does
			// not include the mime extension as that is removed at JAR build
			// time
			Path normalized;
			if ("__mime".equals(VMHelpers.getExtension(file.relative)))
				normalized = VMHelpers.stripExtension(file.relative);
			else
				normalized = file.relative;
			
			// Determine the name of the class, used to filter if this is
			// a valid test or not at a later stage
			String testName = VMHelpers.pathToString('.',
					VMHelpers.stripExtension(normalized));
			
			// Always add the test now, since this could be a super class
			// of a test but not something that should be called
			names.add(testName);
			
			// Determine how this file is to be handled
			switch (VMHelpers.getExtension(normalized))
			{
					// Executable Classes
				case "class":
				case "java":
				case "j":
					sources.put(testName, file);
					break;
				
					// Test expectations
				case "in":
					expects.put(testName, file);
					break;
			}
		}
		
		// Setup full set of possible candidates
		Map<String, CandidateTestFiles> fullSet = new TreeMap<>();
		for (String testName : names)
		{
			// May be an abstract test?
			FileLocation source = sources.get(testName);
			if (source == null)
				continue;
			
			// Store it
			fullSet.put(testName,
				new CandidateTestFiles(source, expects.get(testName)));
		}
		
		// Map tests and candidate sets to normal candidates
		Map<String, CandidateTestFiles> result = new TreeMap<>();
		for (Map.Entry<String, CandidateTestFiles> entry : fullSet.entrySet())
		{
			String testName = entry.getKey();
			
			// Ignore if this does not match the expected name form
			if (!TestDetection.isTest(testName))
				continue;
			
			// Load the expected results and see if there multi-parameters
			Collection<String> multiParams = VMHelpers.__parseMultiParams(
				VMHelpers.__loadExpectedResults(testName, fullSet));
			
			// Single test, has no parameters
			CandidateTestFiles candidate = entry.getValue();
			if (multiParams == null || multiParams.isEmpty())
				result.put(testName, candidate);
			
			// Otherwise signify all the parameters within
			else
				for (String multiParam : multiParams)
					result.put(testName + "@" + multiParam, candidate);
		}
		
		return Collections.unmodifiableMap(result);
	}
	
	/**
	 * Returns the cache directory of the project.
	 * 
	 * @param __project The project to get the cache directory of.
	 * @param __vmType The virtual machine being used.
	 * @param __sourceSet The source set for the library, as there might be
	 * duplicates between them potentially.
	 * @return The path provider to the project cache directory.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/08/15
	 */
	public static Provider<Path> cacheDir(Project __project,
		VMSpecifier __vmType, String __sourceSet)
		throws NullPointerException
	{
		if (__project == null || __vmType == null)
			throw new NullPointerException("NARG");
		
		return __project.provider(() -> __project.getBuildDir().toPath()
			.resolve("squirreljme").resolve("vm-" + __sourceSet + "-" +
				__vmType.vmName(VMNameFormat.LOWERCASE)));
	}
	
	/**
	 * Returns the class path as a string.
	 *
	 * @param __paths Class paths.
	 * @return The class path as a string.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/08/21
	 */
	public static String classpathAsString(Path... __paths)
		throws NullPointerException
	{
		if (__paths == null)
			throw new NullPointerException("NARG");
		
		return VMHelpers.classpathAsString(Arrays.asList(__paths));
	}
	
	/**
	 * Returns the class path as a string.
	 *
	 * @param __paths Class paths.
	 * @return The class path as a string.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/02/29
	 */
	public static String classpathAsString(Iterable<Path> __paths)
		throws NullPointerException
	{
		if (__paths == null)
			throw new NullPointerException("NARG");
		
		StringBuilder sb = new StringBuilder();
		
		for (Path path : __paths)
		{
			if (sb.length() > 0)
				sb.append(File.pathSeparatorChar);
			sb.append(path);
		}
		
		return sb.toString();
	}
	
	/**
	 * Decodes the classpath string.
	 * 
	 * @param __string The string to decode.
	 * @return The decoded path.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/10/17
	 */
	public static Path[] classpathDecode(String __string)
		throws NullPointerException
	{
		if (__string == null)
			throw new NullPointerException("NARG");
		
		Collection<Path> result = new LinkedList<>();
		for (String split : __string.split(Pattern.quote(File.pathSeparator)))
			result.add(Paths.get(split));
		
		return result.<Path>toArray(new Path[result.size()]);
	}
	
	/**
	 * Copies from the input into the output.
	 * 
	 * @param __in The input.
	 * @param __out The output.
	 * @throws IOException On read/write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/08/15
	 */
	public static void copy(InputStream __in, OutputStream __out)
		throws IOException, NullPointerException
	{
		if (__in == null || __out == null)
			throw new NullPointerException("NARG");
		
		byte[] buf = new byte[VMHelpers.COPY_BUFFER];
		for (;;)
		{
			int rc = __in.read(buf);
			
			if (rc < 0)
				return;
			
			__out.write(buf, 0, rc);
		}
	}
	
	/**
	 * Attempts to find the emulator library so that can be loaded directly
	 * instead of being extracted by each test process, if possible.
	 * 
	 * @param __task The task running under.
	 * @return The path to the emulator library.
	 * @since 2020/12/01
	 */
	@SuppressWarnings("ConstantConditions")
	public static Path findEmulatorLib(Task __task)
		throws NullPointerException
	{
		if (__task == null)
			throw new NullPointerException("NARG");
		
		// Figure out what the library is called
		String libName = System.mapLibraryName("emulator-base");
		
		// We need to look through the emulator base tasks to determine
		// the library to select
		Project emuBase = __task.getProject().getRootProject()
			.findProject(":emulators:emulator-base");
		
		// Is this valid?
		Object raw = emuBase.getExtensions().getExtraProperties()
			.get("libPathBase");
		if (!(raw instanceof Path))
			return null;
		
		// Library is here?
		return ((Path)raw).resolve(
			System.mapLibraryName("emulator-base")).toAbsolutePath();
	}
	
	/**
	 * Gets the extension from the given path.
	 * 
	 * @param __path The path to get from.
	 * @return The file extension.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/09/06
	 */
	public static String getExtension(Path __path)
		throws NullPointerException
	{
		if (__path == null)
			throw new NullPointerException("NARG");
		
		// Does this file even have an extension to it?
		String fileName = __path.getFileName().toString();
		int ld = fileName.lastIndexOf('.');
		if (ld < 0)
			return "";
		
		// Otherwise extract it
		return fileName.substring(ld + 1);
	}
	
	/**
	 * Returns the task that creates the JAR.
	 * 
	 * @param __project The project to get from.
	 * @param __sourceSet The source set.
	 * @return The jar task used, or {@code null} if not found.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/08/07
	 */
	public static Jar jarTask(Project __project, String __sourceSet)
		throws NullPointerException
	{
		if (__project == null || __sourceSet == null)
			throw new NullPointerException("NARG");
		
		switch (__sourceSet)
		{
			case SourceSet.MAIN_SOURCE_SET_NAME:
				return (Jar)__project.getTasks().getByName("jar");
			
			case SourceSet.TEST_SOURCE_SET_NAME:
				return (Jar)__project.getTasks().getByName("testJar");
			
			default:
				throw new IllegalStateException("Unknown sourceSet: " +
					__sourceSet);
		}
	}
	
	/**
	 * Returns the main class to execute.
	 *
	 * @param __cfg The configuration.
	 * @param __midlet The MIDlet to be ran.
	 * @return The main class.
	 * @throws NullPointerException If {@code __cfg} is {@code null}.
	 * @since 2020/03/06
	 */
	public static String mainClass(SquirrelJMEPluginConfiguration __cfg,
		JavaMEMidlet __midlet)
		throws NullPointerException
	{
		if (__cfg == null)
			throw new NullPointerException("NARG");
		
		// We either run the MIDlet or we do not
		return (__midlet != null ?
			"javax.microedition.midlet.__MainHandler__" :
			Objects.requireNonNull(__cfg.mainClass,
			"No main class in project."));
	}
	
	/**
	 * Converts the given path to a String using the delimiter.
	 * 
	 * @param __delim The delimiter.
	 * @param __path The path to convert.
	 * @return The path as a string.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/08/30
	 */
	public static String pathToString(char __delim, Path __path)
		throws NullPointerException
	{
		if (__path == null)
			throw new NullPointerException("NARG");
		
		// If the path is of only a single element (or none) then it is just
		// whatever the string form of the path is
		int n = __path.getNameCount();
		if (n <= 1)
			return __path.toString();
		
		// Build together the string
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n; i++)
		{
			if (i > 0)
				sb.append(__delim);
			sb.append(__path.getName(i).toString());
		}
		
		return sb.toString();
	}
	
	/**
	 * Returns the directory where profiler snapshots go.
	 * 
	 * @param __project The project to get the cache directory of.
	 * @param __vmType The virtual machine being used.
	 * @param __sourceSet The source set for the library, as there might be
	 * duplicates between them potentially.
	 * @return The path provider to the profiler snapshot directory.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/09/06
	 */
	public static Provider<Path> profilerDir(Project __project,
		VMSpecifier __vmType, String __sourceSet)
		throws NullPointerException
	{
		if (__project == null || __vmType == null)
			throw new NullPointerException("NARG");
		
		return __project.provider(() -> VMHelpers.cacheDir(
			__project, __vmType, __sourceSet).get().resolve("nps"));
	}
	
	/**
	 * Returns the project classpath.
	 *
	 * @param __project The project.
	 * @return The class path.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/02/29
	 */
	public static Iterable<File> projectRuntimeClasspath(Project __project)
		throws NullPointerException
	{
		if (__project == null)
			throw new NullPointerException("No project specified.");
		
		return __project.getConfigurations().
			getByName("runtimeClasspath").getFiles();
	}
	
	/**
	 * Reads all of the bytes from the stream.
	 * 
	 * @param __in The stream to read from.
	 * @return All of the read bytes.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/09/07
	 */
	public static byte[] readAll(InputStream __in)
		throws IOException, NullPointerException
	{
		if (__in == null)
			throw new NullPointerException("NARG");
		
		try (ByteArrayOutputStream out = new ByteArrayOutputStream(4096))
		{
			byte[] buf = new byte[4096];
			for (;;)
			{
				int rc = __in.read(buf);
				
				if (rc < 0)
					return out.toByteArray();
				
				out.write(buf, 0, rc);
			}
		}
	}
	
	/**
	 * Resolves path elements as needed to determine where a file is.
	 * 
	 * @param __in The input to resolve.
	 * @return The path of the given object.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/27
	 */
	public static Iterable<Path> resolvePath(Object __in)
		throws NullPointerException
	{
		if (__in == null)
			throw new NullPointerException("NARG");
		
		// Direct file paths
		if (__in instanceof Path)
			return Collections.singleton((Path)__in);
		else if (__in instanceof File)
			return Collections.singleton(((File)__in).toPath());
		
		// A produced value
		else if (__in instanceof Callable)
			try
			{
				return VMHelpers.resolvePath(
					((Callable<?>)__in).call());
			}
			catch (Exception e)
			{
				if (e instanceof RuntimeException)
					throw (RuntimeException)e;
				
				throw new RuntimeException("Could not run Callable.", e);
			}
		
		// A supplied value
		else if (__in instanceof Supplier)
			return VMHelpers.resolvePath(
					((Supplier<?>)__in).get());
		
		// An iterable sequence
		else if (__in instanceof Iterable)
		{
			List<Path> result = new ArrayList<>();
			
			// Process each one
			for (Object obj : (Iterable<?>)__in)
				for (Path sub : VMHelpers.resolvePath(obj))
					result.add(sub);
			
			return result;
		}
		
		// Unknown
		else
			throw new RuntimeException(String.format(
				"Unknown input path type %s", __in.getClass()));			
	}
	
	/**
	 * Resolves tasks from the projects and tasks.
	 * 
	 * @param <T> The class to resolve as.
	 * @param __class The class to resolve as.
	 * @param __project The project to latch onto for lookup.
	 * @param __in The input project and task names.
	 * @return An iterable which has the projects resolved.
	 * @throws NullPointerException On null arguments.
	 */
	public static <T extends Task> Iterable<T> resolveProjectTasks(
		Class<T> __class, Project __project, Iterable<ProjectAndTaskName> __in)
		throws NullPointerException
	{
		if (__project == null || __in == null)
			throw new NullPointerException("NARG");
		
		Collection<T> result = new LinkedList<>();
		
		// Map projects and tasks back into tasks
		for (ProjectAndTaskName depend : __in)
			result.add(__class.cast(__project.project(depend.project)
				.getTasks().getByName(depend.task)));
		
		return Collections.unmodifiableCollection(result);
	}
	
	/**
	 * Returns the path of the all the JARs which make up the classpath for
	 * running an executable.
	 * 
	 * @param __task The task to get for.
	 * @param __sourceSet The source set used.
	 * @param __vmType The virtual machine type.
	 * @return An array of paths containing the class path of execution.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/21
	 */
	public static Path[] runClassPath(Task __task,
		String __sourceSet, VMSpecifier __vmType)
		throws NullPointerException
	{
		return VMHelpers.runClassPath(__task.getProject(),
			__sourceSet, __vmType);
	}
	
	/**
	 * Returns the path of the all the JARs which make up the classpath for
	 * running an executable.
	 * 
	 * @param __project The project to get for.
	 * @param __sourceSet The source set used.
	 * @param __vmType The virtual machine type.
	 * @return An array of paths containing the class path of execution.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/08/20
	 */
	public static Path[] runClassPath(Project __project,
		String __sourceSet, VMSpecifier __vmType)
		throws NullPointerException
	{
		if (__project == null || __sourceSet == null || __vmType == null)
			throw new NullPointerException("NARG");
		
		// Get tasks that are used for dependency running
		Iterable<VMLibraryTask> tasks =
			VMHelpers.<VMLibraryTask>resolveProjectTasks(
				VMLibraryTask.class, __project, VMHelpers
					.runClassTasks(__project, __sourceSet, __vmType));
		
		// Get the outputs of these, as they will be used. Ensure the order is
		// kept otherwise execution may be non-deterministic and could break.
		Set<Path> classPath = new LinkedHashSet<>();
		for (VMLibraryTask vmLib : tasks)
			classPath.add(vmLib.getOutputs().getFiles().getSingleFile()
				.toPath());
		
		return classPath.toArray(new Path[classPath.size()]);
	}
	
	/**
	 * Returns the task dependencies to get outputs from that would be
	 * considered a part of the project's class path used at execution time.
	 * 
	 * @param __project The task to get from.
	 * @param __sourceSet The source set used.
	 * @param __vmType The virtual machine information.
	 * @return The direct run dependencies for the task.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/08/15
	 */
	public static Collection<ProjectAndTaskName> runClassTasks(
		Project __project, String __sourceSet,
		VMSpecifier __vmType)
		throws NullPointerException
	{
		return VMHelpers.runClassTasks(__project, __sourceSet, __vmType,
			null);
	}
	
	/**
	 * Returns the task dependencies to get outputs from that would be
	 * considered a part of the project's class path used at execution time.
	 * 
	 * @param __project The task to get from.
	 * @param __sourceSet The source set used.
	 * @param __vmType The virtual machine information.
	 * @param __did Projects that have been processed.
	 * @return The direct run dependencies for the task.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/08/15
	 */
	public static Collection<ProjectAndTaskName> runClassTasks(
		Project __project, String __sourceSet,
		VMSpecifier __vmType, Set<ProjectAndTaskName> __did)
		throws NullPointerException
	{
		if (__project == null || __sourceSet == null || __vmType == null)
			throw new NullPointerException("NARG");
		
		// Make sure this is always set
		if (__did == null)
			__did = new HashSet<>();
		
		// If this process was already processed, ignore it
		ProjectAndTaskName selfProjectTask = ProjectAndTaskName.of(__project,
			TaskInitialization.task("lib", __sourceSet, __vmType));
		if (__did.contains(selfProjectTask))
			return Collections.emptySet();
		
		Set<ProjectAndTaskName> result = new LinkedHashSet<>();
		
		// If we are testing then we depend on the main TAC library, otherwise
		// we will not be able to do any actual testing
		boolean isTesting = __sourceSet.equals(SourceSet.TEST_SOURCE_SET_NAME);
		if (isTesting)
		{
			// Depend on TAC
			result.addAll(VMHelpers.runClassTasks(
				__project.findProject(":modules:tac"),
				SourceSet.MAIN_SOURCE_SET_NAME, __vmType, __did));
			
			// Depend on our main project as we will be testing it
			result.addAll(VMHelpers.runClassTasks(__project,
				SourceSet.MAIN_SOURCE_SET_NAME, __vmType, __did));
		}
		
		// Go through the configurations to yank in the dependencies as needed
		for (String config : (isTesting ? VMHelpers._TEST_CONFIGS :
			VMHelpers._MAIN_CONFIGS))
		{
			// The configuration may be optional
			Configuration foundConfig = __project.getConfigurations()
				.findByName(config);
			if (foundConfig == null)
				continue;
			
			// Handle dependencies
			for (Dependency depend : foundConfig.getDependencies())
			{
				// Only consider projects
				if (!(depend instanceof ProjectDependency))
					continue;
				
				Project sub = ((ProjectDependency)depend)
					.getDependencyProject();
				
				// Only consider SquirrelJME projects
				SquirrelJMEPluginConfiguration squirreljmeConf =
					SquirrelJMEPluginConfiguration.configurationOrNull(sub);
				if (squirreljmeConf == null)
					continue;
				
				// Otherwise, handle the dependencies
				result.addAll(VMHelpers.runClassTasks(sub, 
					SourceSet.MAIN_SOURCE_SET_NAME, __vmType, __did));
			}
		}
		
		// Finally add our own library for usages
		result.add(selfProjectTask);
		
		// Ignore our own project
		__did.add(selfProjectTask);
		
		// Debug as needed
		__project.getLogger().debug("Run Depends: {}", result);
		
		return Collections.unmodifiableCollection(result);
	}
	
	/**
	 * Returns all of the tests to run.
	 * 
	 * @param __project The project to check.
	 * @param __sourceSet The source set to check.
	 * @return All of the tests that should be ran.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/08/30
	 */
	public static Map<String, CandidateTestFiles> runningTests(
		Project __project, String __sourceSet)
		throws NullPointerException
	{
		Map<String, CandidateTestFiles> available =
			VMHelpers.availableTests(__project, __sourceSet);
		
		// If specifying a single test to run only specify that
		String singleTest = System.getProperty(
			VMTestTask.SINGLE_TEST_PROPERTY);
		if (singleTest != null)
		{
			// We need to check every test, since we may have multi-parameters
			// to consider
			Map<String, CandidateTestFiles> singles = new LinkedHashMap<>();
			for (Map.Entry<String, CandidateTestFiles> e : available
				.entrySet())
				if (VMHelpers.__isSingleTest(e.getKey(), singleTest))
					singles.put(e.getKey(), e.getValue());
			
			// If we found at least one test then we can test those, there may
			// be multiple ones due to multi-parameters
			if (!singles.isEmpty())
				return Collections.unmodifiableMap(singles);
			
			// If the test has no matching file, then just ignore it
			__project.getLogger().warn("Could not find test {}, ignoring.",
				singleTest);
		}
		
		// Is only valid if there is at least one test
		return available;
	}
	
	/**
	 * Strips the extension from the path.
	 * 
	 * @param __path The path to strip the extension from.
	 * @return The input path with the extension stripped.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/08/30
	 */
	public static Path stripExtension(Path __path)
		throws NullPointerException
	{
		if (__path == null)
			throw new NullPointerException("NARG");
		
		// If there is no extension part then nothing has to be done
		String fileName = __path.getFileName().toString();
		int lastDot = fileName.lastIndexOf('.');
		if (lastDot < 0)
			return __path;
		
		// The "renamed" file is in the same parent directory
		return __path.resolveSibling(fileName.substring(0, lastDot));
	}
	
	/**
	 * Returns the directory where test results go.
	 * 
	 * @param __project The project to get the cache directory of.
	 * @param __vmType The virtual machine being used.
	 * @param __sourceSet The source set for the library, as there might be
	 * duplicates between them potentially.
	 * @return The path provider to the test result directory.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/09/06
	 */
	public static Provider<Path> testResultXmlDir(Project __project,
		VMSpecifier __vmType, String __sourceSet)
		throws NullPointerException
	{
		if (__project == null || __vmType == null)
			throw new NullPointerException("NARG");
		
		return __project.provider(() -> VMHelpers.cacheDir(
			__project, __vmType, __sourceSet).get().resolve("junit"));
	}
	
	/**
	 * Returns the test result XML file name.
	 * 
	 * @param __testName The test name.
	 * @return The name of the XML to use.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/09/06
	 */
	public static String testResultXmlName(String __testName)
		throws NullPointerException
	{
		if (__testName == null)
			throw new NullPointerException("NARG");
		
		return "TEST-" + __testName + ".xml";
	}
	
	/**
	 * Returns the directory where test results go.
	 * 
	 * @param __project The project to get the cache directory of.
	 * @param __vmType The virtual machine being used.
	 * @param __sourceSet The source set for the library, as there might be
	 * duplicates between them potentially.
	 * @return The path provider to the test result directory.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/26
	 */
	public static Provider<Path> testResultsCsvDir(Project __project,
		VMSpecifier __vmType, String __sourceSet)
		throws NullPointerException
	{
		if (__project == null || __vmType == null)
			throw new NullPointerException("NARG");
		
		return __project.provider(() -> VMHelpers.cacheDir(
			__project, __vmType, __sourceSet).get().resolve("csv"));
	}
	
	/**
	 * Returns the name for the CSV file.
	 * 
	 * @param __project The project this falls under.
	 * @return The path of the CSV results file.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/26
	 */
	public static Path testResultsCsvName(Project __project)
		throws NullPointerException
	{
		if (__project == null)
			throw new NullPointerException("NARG");
		
		return Paths.get("RESULTS-" + __project.getName() + ".csv");
	}
	
	/**
	 * Checks if this is the single test to run, depending if multi-parameters
	 * are used or not.
	 * 
	 * @param __key The key to check.
	 * @param __singleTest The single test that was requested.
	 * @return If this is the matching single test.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/10/11
	 */
	private static boolean __isSingleTest(String __key, String __singleTest)
		throws NullPointerException
	{
		if (__key == null || __singleTest == null)
			throw new NullPointerException("NARG");
		
		// If the test does not have a multi-parameter match it exactly.
		// However if we requested a specific multi-parameter then match that
		// as well.
		// Convert slashes to dots as well for binary name usage
		int la = __key.indexOf('@');
		if (la < 0 || __singleTest.indexOf('@') >= 0)
			return __key.equals(__singleTest) ||
				__key.equals(__singleTest.replace('/', '.'));
		
		// Only match by the basename, if multi-parameter assume all of them
		// But also convert all slashes to dots in the event binary names
		// are used.
		return __key.substring(0, la).equals(__singleTest) ||
			__key.substring(0, la).equals(
				__singleTest.replace('/', '.'));
	}
	
	/**
	 * Loads the expected results for a test.
	 * 
	 * @param __testName  The test being parsed.
	 * @param __candidates The candidates available, used for super classes.
	 * @return The expected results.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/10/09
	 */
	private static Manifest __loadExpectedResults(String __testName,
		Map<String, CandidateTestFiles> __candidates)
		throws NullPointerException
	{
		if (__testName == null || __candidates == null)
			throw new NullPointerException("NARG");
		
		// If there is no candidate for this test, always just return a
		// blank manifest to be parsed
		CandidateTestFiles candidate = __candidates.get(__testName);
		if (candidate == null)
			return new Manifest(); 
		
		// Load information gleaned from the source code
		__SourceInfo__ info;
		try (InputStream in = Files.newInputStream(
			candidate.sourceCode.absolute, StandardOpenOption.READ))
		{
			if ("j".equals(VMHelpers.getExtension(
				candidate.sourceCode.absolute)))
				info = __SourceInfo__.loadJasmin(in);
			else
				info = __SourceInfo__.loadJava(in);
		}
		catch (IOException e)
		{
			throw new RuntimeException("Could not parse source: " +
				__testName, e);
		}
		
		// Read the current manifest
		Manifest over;
		if (candidate.expectedResult == null)
			over = new Manifest();
		else
			try (InputStream in = Files.newInputStream(
				candidate.expectedResult.absolute, StandardOpenOption.READ))
			{
				over = new Manifest(in);
			}
			catch (IOException e)
			{
				throw new RuntimeException("Could not parse manifest: " +
					__testName, e);
			}
		
		// If there is no super class there is no need to even try reading or
		// merging any of them
		if (info.superClass == null)
			return over;
		
		// Load the manifest that belongs to the super class if that is
		// possible, a blank will be used if missing
		Manifest under = VMHelpers.__loadExpectedResults(info.superClass,
			__candidates);
		
		// Add the underlying manifest, provided it does not replace anything
		// on the higher level
		Attributes overAttr = over.getMainAttributes();
		for (Map.Entry<Object, Object> e : under.getMainAttributes()
			.entrySet())
			overAttr.putIfAbsent(e.getKey(), e.getValue());
		
		return over;
	}
	
	/**
	 * Parses multi-parameters from test results.
	 * 
	 * @param __expected The expected results to parse.
	 * @return The multi-parameters, if any.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/10/09
	 */
	private static Collection<String> __parseMultiParams(
		Manifest __expected)
		throws NullPointerException
	{
		if (__expected == null)
			throw new NullPointerException("NARG");
		
		// Get the possible parameter values
		String value = __expected.getMainAttributes()
			.getValue(VMHelpers._MULTI_PARAMETERS_KEY);
		if (value == null)
			return null;
		
		// Split fields, if there are zero then there are no parameters...
		// however for at least one there might be disable parameters so always
		// accept these as tests are expected these
		String[] splice = value.split("[ \t]");
		if (splice.length <= 0)
			return null;
		
		return Arrays.asList(splice);
	}
}
