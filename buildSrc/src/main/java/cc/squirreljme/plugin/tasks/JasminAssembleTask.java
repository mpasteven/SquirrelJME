// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.plugin.tasks;

import jasmin.ClassFile;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import javax.inject.Inject;
import org.gradle.api.Task;
import org.gradle.language.jvm.tasks.ProcessResources;

/**
 * Task for the assembly of Jasmin sources.
 *
 * @since 2020/04/04
 */
public class JasminAssembleTask
	extends AbstractResourceTask
{
	/** The extension for Jasmin files. */
	public static final String EXTENSION =
		".j";
	
	/**
	 * Initializes the task.
	 *
	 * @param __sourceSet The source set to use.
	 * @param __prTask The process resources task.
	 * @since 2020/04/04
	 */
	@Inject
	public JasminAssembleTask(String __sourceSet, ProcessResources __prTask)
	{
		super(JasminAssembleTask.EXTENSION, ".class",
			__sourceSet, __prTask);
		
		// Set details of this task
		this.setGroup("squirreljme");
		this.setDescription("Assembles Jasmin Assembly files.");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/04/04
	 */
	@Override
	protected void doTaskAction(Task __task)
	{
		// Process all outputs
		for (__Output__ output : this.taskOutputs())
			try (ByteArrayOutputStream target = new ByteArrayOutputStream())
			{
				// Assemble input file
				try (InputStream in = Files.newInputStream(
					output.input.absolute, StandardOpenOption.READ))
				{
					// Assemble source
					ClassFile jasClass = new ClassFile();
					try
					{
						jasClass.readJasmin(new BufferedInputStream(in),
							output.input.relative.getFileName().toString(),
							true);
					}
					
					// This could fail
					catch (jas.jasError e)
					{
						throw new RuntimeException(String.format(
							"Error assembling: %s (%d errors): %s",
							output.input.absolute, jasClass.errorCount(),
							e.getMessage()));
					}
					
					// Failed to assemble?
					if (jasClass.errorCount() > 0)
						throw new RuntimeException(String.format(
							"Error assembling: %s (%d errors)",
							output.input.absolute, jasClass.errorCount()));
					
					// Write class file
					jasClass.write(target);
				}
				
				// Write to output file
				try (OutputStream out = Files.newOutputStream(output.output,
					StandardOpenOption.WRITE, StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING))
				{
					target.writeTo(out);
					out.flush();
				}
			}
			
			// Fallback to catch any other failures
			catch (Exception e)
			{
				if (e instanceof RuntimeException)
					throw (RuntimeException)e;
				
				throw new RuntimeException(String.format(
					"Could not assemble %s: %s", output.input.absolute,
					e.getMessage()), e);
			}
	}
}
