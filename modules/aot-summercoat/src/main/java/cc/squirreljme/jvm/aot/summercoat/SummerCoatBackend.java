// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.aot.summercoat;

import cc.squirreljme.jvm.aot.Backend;
import cc.squirreljme.jvm.aot.CompileSettings;
import cc.squirreljme.jvm.aot.LinkGlob;
import cc.squirreljme.jvm.aot.RomSettings;
import cc.squirreljme.jvm.summercoat.constants.JarProperty;
import cc.squirreljme.jvm.summercoat.ld.mem.ByteArrayMemory;
import cc.squirreljme.jvm.summercoat.ld.pack.HeaderStruct;
import cc.squirreljme.jvm.summercoat.ld.pack.JarRom;
import cc.squirreljme.runtime.cldc.io.HexDumpOutputStream;
import cc.squirreljme.vm.SummerCoatJarLibrary;
import cc.squirreljme.vm.VMClassLibrary;
import dev.shadowtail.classfile.mini.DualPoolEncoder;
import dev.shadowtail.classfile.mini.MinimizedClassFile;
import dev.shadowtail.classfile.mini.MinimizedClassHeader;
import dev.shadowtail.classfile.mini.MinimizedField;
import dev.shadowtail.classfile.mini.MinimizedMethod;
import dev.shadowtail.classfile.pool.DualClassRuntimePool;
import dev.shadowtail.packfile.PackMinimizer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;

/**
 * This is the backend for SummerCoat.
 *
 * @since 2020/11/22
 */
public class SummerCoatBackend
	implements Backend
{
	/**
	 * {@inheritDoc}
	 * @since 2020/11/22
	 */
	@Override
	public void compileClass(CompileSettings __settings, LinkGlob __glob,
		String __name, InputStream __in, OutputStream __out)
		throws IOException, NullPointerException
	{
		if (__settings == null || __glob == null || __name == null ||
			__in == null || __out == null)
			throw new NullPointerException("NARG");
		
		// Copy from the source to the destination
		byte[] buf = new byte[4096];
		for (;;)
		{
			int rc = __in.read(buf);
			
			if (rc < 0)
				break;
			
			__out.write(buf, 0, rc);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/16
	 */
	@Override
	public void dumpGlob(byte[] __inGlob, String __name, PrintStream __out)
		throws IOException, NullPointerException
	{
		// Load JAR
		ClassDumper dumper = new ClassDumper(new JarRom(0, __name,
			new ByteArrayMemory(0, __inGlob, false)),
			__name, __out);
		
		// Perform the dumping
		dumper.dump(__inGlob);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/11/22
	 */
	@Override
	public LinkGlob linkGlob(CompileSettings __settings, String __name,
		OutputStream __out)
		throws IOException, NullPointerException
	{
		if (__settings == null || __name == null || __out == null)
			throw new NullPointerException("NARG");
		
		return new SummerCoatLinkGlob(__settings, __name, __out);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/11/22
	 */
	@Override
	public String name()
	{
		return "summercoat";
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/11/27
	 */
	@Override
	public void rom(RomSettings __settings, OutputStream __out,
		VMClassLibrary... __libs)
		throws IOException, NullPointerException
	{
		if (__settings == null || __out == null || __libs == null)
			throw new NullPointerException("NARG");
		
		// Try to find the boot library
		String bootLib = null;
		for (VMClassLibrary lib : __libs)
		{
			String name = lib.name();
			
			// Determine the base name
			int lastPath = Math.max(name.lastIndexOf('/'),
				name.lastIndexOf('\\'));
			name = (lastPath >= 0 ? name.substring(lastPath + 1) : name);
			
			// Clip off extension
			if (SummerCoatJarLibrary.isSqc(name) ||
				name.endsWith(".jar") || name.endsWith(".JAR"))
				name = name.substring(0, name.length() - 4);
			
			// Is this cldc-compact?
			if (name.equals("cldc-compact"))
				bootLib = lib.name();
		}
		
		// {@squirreljme.error AA01 Could not find the boot library.
		// (The available libraries)}
		if (bootLib == null)
			throw new IllegalArgumentException(
				"AA01 " + Arrays.asList(__libs));
		
		// Call the minimizer directly, just use a base library for the main
		// boot since it does not matter as the bootstrap should find the
		// launcher or the correct program to load rather than having it
		// baked into the ROM
		PackMinimizer.minimize(__out, bootLib, __libs);
	}
}
