// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.plugin.multivm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.function.Function;
import org.gradle.api.Action;
import org.gradle.api.Task;
import org.gradle.api.tasks.SourceSet;

/**
 * Performs the action of building the virtual machine.
 *
 * @since 2020/08/07
 */
public class VMLibraryTaskAction
	implements Action<Task>
{
	/** The source set used. */
	public final String sourceSet;
	
	/** The virtual machine type. */
	public final VMSpecifier vmType;
	
	/**
	 * Initializes the task action.
	 * 
	 * @param __sourceSet The source set.
	 * @param __vmType The virtual machine type.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/08/15
	 */
	public VMLibraryTaskAction(String __sourceSet,
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
		VMLibraryTaskAction.execute(__task, this.vmType, this.sourceSet,
			this.vmType::processLibrary);
	}
	
	/**
	 * Performs a library like action.
	 * 
	 * @param __task The task calling from.
	 * @param __vmType The virtual machine type.
	 * @param __sourceSet The source set used.
	 * @param __func The function to use.
	 * @since 2021/05/16
	 */
	public static void execute(Task __task, VMSpecifier __vmType,
		String __sourceSet, VMLibraryExecuteFunction __func)
	{
		// Open the input library for processing
		Path tempFile = null;
		try (InputStream in = Files.newInputStream(__task.getInputs()
			.getFiles().getSingleFile().toPath(), StandardOpenOption.READ))
		{
			// Where shall this go?
			tempFile = Files.createTempFile(
				__vmType.vmName(VMNameFormat.LOWERCASE), __sourceSet);
			
			// Setup output file for writing
			try (OutputStream out = Files.newOutputStream(tempFile,
				StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING,
				StandardOpenOption.CREATE))
			{
				__func.function(__task,
					SourceSet.TEST_SOURCE_SET_NAME.equals(__sourceSet),
					in, out);
			}
			
			// Move the file over
			Files.move(tempFile,
				__task.getOutputs().getFiles().getSingleFile().toPath(),
				StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException e)
		{
			throw new RuntimeException("I/O Error processing input for " +
				__task.getProject().getName(), e);
		}
		
		// Ensure the temporary file is cleaned up on failure
		finally
		{
			if (tempFile != null)
				try
				{
					Files.deleteIfExists(tempFile);
				}
				catch (IOException ignored)
				{
				}
		}
	}
}
