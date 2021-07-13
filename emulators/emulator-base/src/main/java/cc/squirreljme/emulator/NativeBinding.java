// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.emulator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * This class manages the native bindings.
 *
 * @since 2020/02/25
 */
public final class NativeBinding
{
	/** Property for pre-loading libraries. */
	public static final String LIB_PRELOAD =
		"squirreljme.emulator.libpath";
	
	static
	{
		long loadNs = System.nanoTime();
		try
		{
			// Try to use a preloaded library, otherwise load it in
			Path libFile = NativeBinding.__checkPreload();
			if (libFile == null)
				libFile = NativeBinding.__libFromResources();
				
			// Debug
			System.err.printf("Java Version: %s%n",
				System.getProperty("java.version"));
			System.err.printf("Java Over-Layer: Loading %s...%n", libFile);
			
			// Try loading the library now
			System.load(libFile.toString());
			
			// Debug
			System.err.printf("Java Over-Layer: Binding methods...%n");
			
			// Bind methods
			if (NativeBinding.__bindMethods() != 0)
				throw new RuntimeException("Could not bind methods!");
			
			// Debug
			System.err.printf("Java Over-Layer: Methods bound!%n");
		}
		catch (IOException e)
		{
			throw new RuntimeException("Could not load library.", e);
		}
		
		// Track execution time
		finally
		{
			System.err.printf("Java Over-Layer: Loading took %dms%n",
				(System.nanoTime() - loadNs) / 1_000_000L);
		}
	}
	
	/**
	 * Binds methods accordingly.
	 *
	 * @since 2020/02/26
	 */
	private static native int __bindMethods();
	
	/**
	 * Checks to see if the preloaded library is available.
	 * 
	 * @return The path to the library or {@code null} if not preloaded.
	 * @since 2020/12/01
	 */
	private static Path __checkPreload()
	{
		String libProp = System.getProperty(NativeBinding.LIB_PRELOAD);
		if (libProp == null)
			return null;
		
		Path path = Paths.get(libProp);
		if (Files.exists(path))
			return path;
		return null;
	}
	
	/**
	 * Tries to load the library from resources.
	 * 
	 * @return The loaded library.
	 * @throws IOException On read/write errors.
	 * @since 2020/12/01
	 */
	private static Path __libFromResources()
		throws IOException
	{
		// Find the library to load
		String libName = System.mapLibraryName("emulator-base");
		
		// Debug
		System.err.printf("Java Over-Layer: Locating %s...%n", libName);
		
		// Timing for extraction
		long startNs = System.nanoTime();
		
		// Copy resource to the output
		Path tempDir = null,
			libFile = null;
		try (InputStream in = NativeBinding.class.
			getResourceAsStream("/" + libName))
		{
			if (in == null)
				throw new RuntimeException(String.format(
					"Library %s not found in resource.", libName));
			
			// Store the library as a given file
			tempDir = Files.createTempDirectory("squirreljme-lib");
			libFile = tempDir.resolve(libName);
			
			// Debug
			System.err.printf("Java Over-Layer: Extracting %s...%n", libName);
			
			// Write to the disk as we can only load there
			try (OutputStream out = Files.newOutputStream(libFile,
					StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING,
					StandardOpenOption.WRITE))
			{
				// Store here
				byte[] buf = new byte[262144];
				for (;;)
				{
					int rc = in.read(buf);
					
					if (rc < 0)
						break;
					
					out.write(buf, 0, rc);
				}
				
				// Make sure it is on the disk
				out.flush();
			}
			
			// Attempt cleanup at shutdown.
			Runtime.getRuntime().addShutdownHook(
				new PathCleanup(libFile, tempDir));
		
			// Debug
			System.err.printf("Java Over-Layer: Extracted to %s...%n",
				libFile);
			
			// Use this path
			return libFile;
		}
		catch (IOException e)
		{
			// Try to clear the file
			try
			{
				if (libFile != null)
					Files.delete(libFile);
				
				if (tempDir != null)
					Files.delete(tempDir);
			}
			catch (IOException f)
			{
				e.addSuppressed(f);
			}
			
			throw new IOException("Could not copy native library.", e);
		}
		
		// Track execution time
		finally
		{
			System.err.printf("Java Over-Layer: Extraction took %dms%n",
				(System.nanoTime() - startNs) / 1_000_000L);
		}
	}
}
