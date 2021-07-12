// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.plugin.tasks;

import cc.squirreljme.plugin.multivm.CandidateTestFiles;
import cc.squirreljme.plugin.multivm.VMHelpers;
import cc.squirreljme.plugin.util.FileLocation;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.LinkedList;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetOutput;
import org.gradle.language.jvm.tasks.ProcessResources;

/**
 * This generates the list of tests that are available.
 *
 * @since 2020/02/28
 */
public class GenerateTestsListTask
	extends DefaultTask
{
	/** The service list path. */
	public static final Path SERVICE_LIST_PATH =
		Paths.get("META-INF", "services",
			"net.multiphasicapps.tac.TestInterface");
	
	/**
	 * Initializes the task.
	 *
	 * @param __processTask The resource task.
	 * @since 2020/02/28
	 */
	@Inject
	public GenerateTestsListTask(ProcessResources __processTask)
	{
		// Set details of this task
		this.setGroup("squirreljme");
		this.setDescription("Builds the list of tests which are available.");
		
		// Configure inputs and outputs
		Project project = this.getProject();
		this.getInputs().files(
			project.provider(this::__taskInputsAsFileCollection));
		this.getOutputs().files(
			project.provider(this::__taskOutputsAsFileCollection));
		
		// Only run in this condition
		this.onlyIf(this::__onlyIf);
		
		// Perform our task action
		this.doLast(new Action<Task>() {
				@Override
				public void execute(Task __task)
				{
					GenerateTestsListTask.this.__doLast(__task);
				}
			});
		
		// The process task depends on this task
		__processTask.dependsOn(this);
	}
	
	/**
	 * Runs the actual task.
	 *
	 * @param __task The task to run for.
	 * @since 2020/02/28
	 */
	private void __doLast(Task __task)
	{
		Path outputFile = this.__taskOutput();
		
		// Create list
		try
		{
			// Make sure the output directory exists
			Files.createDirectories(outputFile.getParent());
			
			// Write lines to service file
			try (PrintStream out = new PrintStream(Files.newOutputStream(
				outputFile, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING,
				StandardOpenOption.WRITE)))
			{
				// Every available test becomes the name of every test
				for (String name : VMHelpers.availableTests(this.getProject(),
					SourceSet.TEST_SOURCE_SET_NAME).keySet())
					out.println(name);
				
				// Make sure the output is written
				out.flush();
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Only run the task if certain conditions are met.
	 *
	 * @param __task The task to run.
	 * @return The result if it should run or not.
	 * @since 2020/02/28
	 */
	private boolean __onlyIf(Task __task)
	{
		// There is no point in running this, if nothing can be done
		return !this.__taskInputsAsFileCollection().isEmpty();
	}
	
	/**
	 * Returns the output path.
	 *
	 * @return The output path.
	 * @since 2020/02/28
	 */
	private Path __outputPath()
	{
		// We might need to set an output potentially
		Project project = this.getProject();
		SourceSetOutput outSet = this.getProject().getConvention().
			getPlugin(JavaPluginConvention.class).
			getSourceSets().getByName(SourceSet.TEST_SOURCE_SET_NAME).
			getOutput();
			
		// The base output directory
		File outDirFile = outSet.getResourcesDir();
		if (outDirFile == null)
			outSet.setResourcesDir((outDirFile = project.getBuildDir().
			toPath().resolve("test-list-resource").toFile()));
		
		return outDirFile.toPath();
	}
	
	/**
	 * Gets the inputs for this task.
	 *
	 * @return The task inputs.
	 * @since 2020/02/28
	 */
	private Iterable<FileLocation> __taskInputs()
	{
		Collection<FileLocation> inputs = new LinkedList<>();
		
		for (CandidateTestFiles file : VMHelpers.availableTests(
			this.getProject(), SourceSet.TEST_SOURCE_SET_NAME).values())
		{
			if (file.sourceCode != null)
				inputs.add(file.sourceCode);
			
			if (file.expectedResult != null)
				inputs.add(file.expectedResult);
		}
		
		return inputs;
	}
	
	/**
	 * Gets the inputs for the task.
	 *
	 * @return The task inputs.
	 * @since 2020/02/28
	 */
	private FileCollection __taskInputsAsFileCollection()
	{
		Collection<File> result = new LinkedList<>();
		for (FileLocation file : this.__taskInputs())
			result.add(file.absolute.toFile());
		
		return this.getProject().files(result);
	}
	
	/**
	 * Returns the output of this task.
	 *
	 * @return The task output.
	 * @since 2020/02/28
	 */
	private Path __taskOutput()
	{
		return this.__outputPath().resolve(
			GenerateTestsListTask.SERVICE_LIST_PATH);
	}
	
	/**
	 * Gets the outputs for the task.
	 *
	 * @return The task outputs.
	 * @since 2020/02/28
	 */
	private FileCollection __taskOutputsAsFileCollection()
	{
		// There is just a single output file
		return this.getProject().files(this.__taskOutput().toFile());
	}
}
