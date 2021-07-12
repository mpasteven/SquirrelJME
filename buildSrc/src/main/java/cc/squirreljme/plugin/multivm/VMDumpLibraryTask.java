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
import java.nio.file.Paths;
import javax.inject.Inject;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.provider.Provider;
import org.gradle.jvm.tasks.Jar;

/**
 * This is used to dump the output compilation result of a library.
 *
 * @since 2021/05/16
 */
public class VMDumpLibraryTask
	extends DefaultTask
	implements VMExecutableTask
{
	/** The source set used. */
	public final String sourceSet;
	
	/** The virtual machine type. */
	public final VMSpecifier vmType;
	
	/**
	 * Initializes the library dumping task.
	 * 
	 * @param __sourceSet The source set used.
	 * @param __vmType The virtual machine type.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/05/16
	 */
	@Inject
	public VMDumpLibraryTask(String __sourceSet,
		VMSpecifier __vmType, VMLibraryTask __libTask)
		throws NullPointerException
	{
		if (__sourceSet == null || __vmType == null)
			throw new NullPointerException("NARG");
			
		Project project = this.getProject();
		Jar baseJar = VMHelpers.jarTask(project, __sourceSet);
		
		// These are used at the build stage
		this.sourceSet = __sourceSet;
		this.vmType = __vmType;
		
		// Set details of this task
		this.setGroup("squirreljme");
		this.setDescription("Dumps the compiled library for debugging.");
		
		// We need to build the library before we can dump it
		this.dependsOn(baseJar,
			__libTask);
			
		// The input is the output of the library task, which is a glob
		Provider<File> file = this.getProject().provider(
			() -> __libTask.getOutputs().getFiles().getSingleFile());
		this.getInputs().file(file);
		
		// The output depends on the task and its source set
		this.getOutputs().file(this.getProject().provider(
			() -> Paths.get(file.get() + ".yml").toFile()));
		this.getOutputs().upToDateWhen(
			new VMLibraryTaskUpToDate(this.vmType));
		
		// Performs the action of the task
		this.doLast(new VMDumpLibraryTaskAction(__sourceSet, __vmType));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/16
	 */
	@Override
	public String getSourceSet()
	{
		return this.sourceSet;
	}
}
