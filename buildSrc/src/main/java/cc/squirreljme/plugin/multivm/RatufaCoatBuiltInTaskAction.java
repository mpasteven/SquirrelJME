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
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import org.gradle.api.Action;
import org.gradle.api.Task;

/**
 * This is the action that generates the built-in ROM for RatufaCoat.
 *
 * @since 2021/02/25
 */
public class RatufaCoatBuiltInTaskAction
	implements Action<Task>
{
	/** The column limit. */
	private static final int _COLS =
		40;
	
	/** Compact writing table, for the quickest and smallest writes. */
	private static final byte[][] _COMPACT =
		new byte[256][];
	
	/** The longest length. */
	private static final int _LONGEST;
	
	/** The source set used. */
	public final String sourceSet;
	
	/** The virtual machine creating for. */
	protected final VMSpecifier vmType;
	
	static
	{
		// Calculate the sequence of bytes which result in the smallest for
		// a given type in C
		byte[][] compact = RatufaCoatBuiltInTaskAction._COMPACT;
		for (int i = 0; i < 256; i++)
		{
			// Find the best one
			String least = null;
			for (String maybe : Arrays.<String>asList(
				Integer.toString(i),
				"0" + Integer.toOctalString(i),
				"0x" + Integer.toHexString(i)))
				if (least == null || maybe.length() < least.length())
					least = maybe;
			
			// Use this one, but pre-encode it
			compact[i] = least.getBytes(StandardCharsets.UTF_8);
		}
		
		// Go through and calculate the longest sequence
		int longest = Integer.MIN_VALUE;
		for (byte[] buf : compact)
			longest = Math.max(longest, buf.length);
		_LONGEST = longest;
	}
	
	/**
	 * Initializes the task.
	 * 
	 * @param __vmType The VM to make a ROM for.
	 * @param __sourceSet The source set.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/02/25
	 */
	public RatufaCoatBuiltInTaskAction(VMSpecifier __vmType,
		String __sourceSet)
		throws NullPointerException
	{
		if (__vmType == null || __sourceSet == null)
			throw new NullPointerException("NARG");
		
		this.vmType = __vmType;
		this.sourceSet = __sourceSet;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/25
	 */
	@Override
	public void execute(Task __task)
	{
		// Where is the ROM going?
		Path output = __task.getOutputs().getFiles().getSingleFile().toPath();
		
		// This could fail to write
		Path tempFile = null;
		try
		{
			// Setup temporary file
			tempFile = Files.createTempFile("builtin", ".c");
			
			// Generate C code from this
			try (PrintStream out = new PrintStream(
				Files.newOutputStream(tempFile,
					StandardOpenOption.WRITE, StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING),
				false, "utf-8"))
			{
				// Load in C header
				try (InputStream in = RatufaCoatBuiltInTaskAction.class
					.getResourceAsStream("header.h"))
				{
					byte[] buf = new byte[8192];
					for (;;)
					{
						int rc = in.read(buf);
						
						// EOF?
						if (rc < 0)
							break;
						
						out.write(buf, 0, rc);
					}
				}
				
				// Only when built-in is enabled
				out.println();
				out.println("#if defined(SQUIRRELJME_HAS_BUILTIN)");
				out.println();
				
				// Declare the type
				out.println("const sjme_ubyte sjme_builtInRomData[] = {");
				
				// Read in the entire ROM file since this is much faster
				Path inputRom = __task.getInputs()
					.getFiles().getSingleFile().toPath();
				byte[] romData = Files.readAllBytes(inputRom);
				long romDate = Files.getLastModifiedTime(inputRom).toMillis();
				
				// Get ROM Unique Identifier (ROM Sum)
				byte[] romDigest = MessageDigest.getInstance("SHA-256")
					.digest(romData);
				
				// Determine file size for progress metering
				int romSize = romData.length;
				
				// Starting time for timing
				long startNs = System.nanoTime();
				
				// Output data buffer
				int chunkySize = 0;
				int chunkyLimit = 131072;
				byte[] chunkyBuf = new byte[chunkyLimit + 128];
				
				// Write C ROM data
				byte[][] compact = RatufaCoatBuiltInTaskAction._COMPACT;
				for (int i = 0, col = 0, commaBit = romSize -1;
					i < romSize; i++)
				{
					// This could take awhile!
					if (chunkySize > chunkyLimit)
					{
						// This could be a slow process, so allow it to be
						// stopped! Do not check all the time since this
						// may go quickly.
						if (Thread.currentThread().isInterrupted())
							throw new IOException("Conversion interrupted.");
						
						// Dump the output chunk and reset it
						out.write(chunkyBuf, 0, chunkySize);
						chunkySize = 0;
						
						// Nanoseconds per progress point
						long currentNanos = (System.nanoTime() -
							startNs);
						long nanosPerIndex = currentNanos / i;
						long estTotalNanos = nanosPerIndex * romSize;
						
						// Print progress dot
						System.err.printf("Processed %d of %d bytes " +
							"(%d seconds remaining)...%n",
							i, romSize,
							(estTotalNanos - currentNanos) / 1_000_000_000L);
						System.err.flush();
					}
					
					// Write the digit down
					for (byte b : compact[romData[i] & 0xFF])
						chunkyBuf[chunkySize++] = b;
					
					// Do not add a final comma
					if (i < commaBit)
						chunkyBuf[chunkySize++] = ',';
					
					// Put extra bytes on new line
					if ((++col) >= RatufaCoatBuiltInTaskAction._COLS)
					{
						chunkyBuf[chunkySize++] = '\n';
						col = 0;
					}
				}
				
				// Dump the chunks if there is anything left
				if (chunkySize > 0)
					out.write(chunkyBuf, 0, chunkySize);
				
				// End the type
				out.println("};");
				
				// Write ROM file size
				out.printf("const sjme_jint sjme_builtInRomSize = %d;",
					romData.length);
				out.println();
				
				// Write ROM ID
				out.printf("const sjme_jbyte sjme_builtInRomId[] = " +
				 	"{%s};",
				 	Arrays.toString(romDigest)
				 		.replace('[', ' ')
				 		.replace(']', ' '));
				out.println();
				
				// Write ROM ID Length
				out.printf("const sjme_jint sjme_builtInRomIdLen = " +
				 	"SJME_JINT_C(%d);",
				 	romDigest.length);
				out.println();
				
				// Write ROM date
				out.printf("const sjme_jint sjme_builtInRomDate[] = " +
					"{SJME_JINT_C(%d), SJME_JINT_C(%d)};",
					(int)(romDate >>> 32), (int)romDate);
				out.println();
				
				// End Only when built-in is enabled
				out.println();
				out.println("#endif");
				out.println();
				
				// Flush the output before closed
				out.flush();
			}
			
			// It worked, so place it in the output
			Files.createDirectories(output.getParent());
			Files.move(tempFile, output, StandardCopyOption.REPLACE_EXISTING);
		}
		
		// It did fail to write
		catch (IOException | NoSuchAlgorithmException e)
		{
			throw new RuntimeException("Could not build ROM: " + output, e);
		}
		
		// Try to clear the temporary file if it exists still
		finally
		{
			if (tempFile != null)
				try
				{
					Files.delete(tempFile);
				}
				catch (IOException ignored)
				{
				}
		}
	}
}
