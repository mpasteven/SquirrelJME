// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.aot;

import cc.squirreljme.runtime.cldc.Poking;
import cc.squirreljme.vm.JarClassLibrary;
import cc.squirreljme.vm.SummerCoatJarLibrary;
import cc.squirreljme.vm.VMClassLibrary;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.ServiceLoader;
import net.multiphasicapps.zip.streamreader.ZipStreamEntry;
import net.multiphasicapps.zip.streamreader.ZipStreamReader;

/**
 * Main entry point for the AOT compilation backend.
 *
 * @since 2020/11/21
 */
public class Main
{
	/**
	 * Finds the given backend.
	 * 
	 * @param __compiler The compiler to select.
	 * @return The backend.
	 * @throws IllegalArgumentException If the backend is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/22
	 */
	private static Backend findBackend(String __compiler)
		throws IllegalArgumentException, NullPointerException
	{
		if (__compiler == null)
			throw new NullPointerException("NARG");
		
		// Find matching backend with the given name
		for (Backend backend : ServiceLoader.load(Backend.class))
			if (__compiler.equals(backend.name()))
				return backend;
		
		// {@squirreljme.error AE04 The given compiler does not exist.
		// (The compiler)}
		throw new IllegalArgumentException("AE04 " + __compiler);
	}
	
	/**
	 * Main entry point for the compiler interface.
	 * 
	 * @param __args Arguments to the main class.
	 * @throws IOException On read errors.
	 * @since 2020/11/21
	 */
	public static void main(String... __args)
		throws IOException
	{
		// Make sure the VM stuff is alive here
		Poking.poke();
		
		// Push all arguments to the queue
		Deque<String> args = new LinkedList<>(Arrays.asList(__args));
		
		// Selection commands
		String compiler = null;
		String name = "undefined";
		String mode = null;
		
		// Parse input arguments
		while (!args.isEmpty())
		{
			String arg = args.removeFirst();
			
			// Compiler to select
			if (arg.startsWith("-Xcompiler:"))
				compiler = arg.substring("-Xcompiler:".length());
			
			// The name of the JAR being compiled
			else if (arg.startsWith("-Xname:"))
				name = arg.substring("-Xname:".length());
			
			// End of switches
			else if (!arg.startsWith("-"))
			{
				mode = arg;
				break;
			}
			
			// {@squirreljme.error AE01 Unknown argument. (The argument)}
			else
				throw new IllegalArgumentException("AE01 " + arg);
		}
		
		// {@squirreljme.error AE03 Mode was not specified.}
		if (mode == null)
			throw new IllegalArgumentException("AE03");
		
		// Find the backend to use
		Backend backend = Main.findBackend(compiler);
		
		// Use explicit input/output
		try (InputStream in = new StandardInputStream();
			OutputStream out = System.out)
		{
			// Which mode should occur?
			switch (mode)
			{
					// Compile code
				case "compile":
					Main.mainCompile(backend, in, out, name, args);
					break;
					
					// Dump the result of "compile"
				case "dumpCompile":
					Main.dumpCompile(backend, in, out, name);
					break;
					
					// Link multiple libraries into one
				case "rom":
					Main.mainRom(backend, out, args);
					break;
				
				// {@squirreljme.error AE02 Unknown mode. (The mode)}
				default:
					throw new IllegalArgumentException("AE02 " + mode);
			}
		}
	}
	
	/**
	 * Dumps the result of the compilation to a readable text format used
	 * for debugging.
	 * 
	 * @param __backend The backend to use.
	 * @param __inGlob The input glob.
	 * @param __out Where to write the output.
	 * @param __name The name of the Glob.
	 * @throws IOException On read/write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/05/16
	 */
	private static void dumpCompile(Backend __backend, InputStream __inGlob,
		OutputStream __out, String __name)
		throws IOException, NullPointerException
	{
		if (__backend == null || __inGlob == null || __out == null ||
			__name == null)
			throw new NullPointerException("NARG");
		
		// Read in the entire contents of the data
		byte[] dump;
		try (InputStream in = __inGlob;
			ByteArrayOutputStream baos = new ByteArrayOutputStream(
				Math.max(4096, __inGlob.available())))
		{
			// Load in a copy
			byte[] buf = new byte[8192];
			for (;;)
			{
				int rc = in.read(buf);
				
				// EOF?
				if (rc < 0)
					break;
				
				baos.write(buf, 0, rc);
			}
			
			// Write output
			dump = baos.toByteArray();
		}
		
		// Dump the output
		try (PrintStream out = new PrintStream(__out, true))
		{
			__backend.dumpGlob(dump, __name, out);
		}
	}
	
	/**
	 * Handles the main compilation stage.
	 * 
	 * @param __backend The backend to use.
	 * @param __inZip The input stream of the input ZIP.
	 * @param __outGlob The output stream of the Glob.
	 * @param __name The name of the library.
	 * @param __args The arguments to use.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/22
	 */
	private static void mainCompile(Backend __backend, InputStream __inZip,
		OutputStream __outGlob, String __name, Deque<String> __args)
		throws IOException, NullPointerException
	{
		if (__backend == null || __name == null || __args == null ||
			__inZip == null || __outGlob == null)
			throw new NullPointerException("NARG");
		
		// Parse compilation arguments
		CompileSettings settings = CompileSettings.parse(__args);
		
		// Setup glob for final linking
		LinkGlob glob = __backend.linkGlob(settings, __name, __outGlob);
		
		// Read input JAR and perform inline compilation
		try (InputStream in = __inZip;
			ZipStreamReader zip = new ZipStreamReader(in))
		{
			// Process JAR entries and compile them into individual class
			// fragments
			for (;;)
				try (ZipStreamEntry entry = zip.nextEntry())
				{
					// No more entries to process
					if (entry == null)
						break;
					
					// Only compile classes
					String name = entry.name();
					if (!name.endsWith(".class"))
					{
						// Link in the resource as-is however
						glob.join(name, true, entry);
						
						continue;
					}
					
					// Perform class compilation
					try (ByteArrayOutputStream classBytes =
						new ByteArrayOutputStream())
					{
						// Perform compilation
						__backend.compileClass(settings, glob,
							name.substring(0,
								name.length() - ".class".length()),
							entry, classBytes);
						
						// Link in the resultant object
						try (InputStream bain = new ByteArrayInputStream(
							classBytes.toByteArray()))
						{
							glob.join(name, false, bain);
						}
					} 
				}
			
			// Linking stage is finished
			glob.finish();
		}
	}
	
	/**
	 * Links the ROM together as one.
	 * 
	 * @param __backend The backend to use.
	 * @param __out Where the resultant ROM is to be written.
	 * @param __args The arguments to the ROM linking.
	 * @throws IOException On read/write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/27
	 */
	public static void mainRom(Backend __backend, OutputStream __out,
		Deque<String> __args)
		throws IOException, NullPointerException
	{
		if (__backend == null || __args == null || __out == null)
			throw new NullPointerException("NARG");
		
		// Parse rom arguments
		RomSettings settings = RomSettings.parse(__args);
		
		// Load all libraries
		Collection<VMClassLibrary> libs = new LinkedList<>();
		while (!__args.isEmpty())
		{
			String arg = __args.removeFirst();
			
			// Determine the correct kind of library to load
			VMClassLibrary lib;
			if (SummerCoatJarLibrary.isSqc(arg))
				lib = new SummerCoatJarLibrary(Paths.get(arg));
			else
				lib = JarClassLibrary.of(Paths.get(arg));
			
			libs.add(lib);
		}
		
		// {@squirreljme.error AE08 No libraries specified to link together.}
		if (libs.isEmpty())
			throw new IllegalArgumentException("AE08");
		
		// Perform combined linking
		__backend.rom(settings, __out,
			libs.toArray(new VMClassLibrary[libs.size()]));
	}
}
