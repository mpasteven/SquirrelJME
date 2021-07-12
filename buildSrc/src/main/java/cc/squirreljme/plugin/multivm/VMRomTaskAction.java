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
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashSet;
import java.util.Set;
import org.gradle.api.Action;
import org.gradle.api.Task;

/**
 * This performs the actual work that is needed to build the ROM.
 *
 * @since 2020/08/23
 */
public class VMRomTaskAction
	implements Action<Task>
{
	/** The source set used. */
	protected final String sourceSet;
	
	/** The virtual machine to generate for. */
	protected final VMSpecifier vmType;
	
	/**
	 * Initializes the task.
	 * 
	 * @param __sourceSet The source set used.
	 * @param __vmType The VM to make a ROM for.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/08/23
	 */
	public VMRomTaskAction(String __sourceSet,
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
	 * @since 2020/08/23
	 */
	@Override
	public void execute(Task __task)
		throws NullPointerException
	{
		String sourceSet = this.sourceSet;
		VMSpecifier vmType = this.vmType;
		
		Path tempFile = null;
		try
		{
			// We need somewhere safe to store the file
			tempFile = Files.createTempFile(
				this.vmType.vmName(VMNameFormat.LOWERCASE), sourceSet);
			
			// Get all of the libraries to translate
			Set<Path> libPaths = new LinkedHashSet<>();
			for (VMLibraryTask task : VMRomDependencies.libraries(__task,
				sourceSet, vmType))
				for (File f : task.getOutputs().getFiles().getFiles())
					libPaths.add(f.toPath());
			
			// Setup output file for writing
			try (OutputStream out = Files.newOutputStream(tempFile,
				StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING,
				StandardOpenOption.CREATE))
			{
				this.vmType.processRom(__task, out, libPaths);
			}
			
			// Move the file over
			Files.move(tempFile,
				__task.getOutputs().getFiles().getSingleFile().toPath(),
				StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException e)
		{
			throw new RuntimeException("I/O Error linking ROM for " +
				__task.getProject().getName(), e);
		}
		
		// Always try to cleanup the temporary file
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
