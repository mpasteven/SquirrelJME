// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.aot;

import cc.squirreljme.vm.VMClassLibrary;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ServiceLoader;

/**
 * This is the base class for the backend interface what is available for
 * compilers and otherwise.
 * 
 * Accessed through {@link ServiceLoader}.
 *
 * @since 2020/11/22
 */
public interface Backend
{
	/**
	 * Performs compilation of a single class.
	 * 
	 * @param __settings The settings for compilation.
	 * @param __glob The compilation glob if this is needed by the source
	 * compilation step.
	 * @param __name The name of the class being compiled.
	 * @param __in The input stream.
	 * @param __out The output stream.
	 * @throws IOException On read/write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/22
	 */
	void compileClass(CompileSettings __settings, LinkGlob __glob,
		String __name, InputStream __in, OutputStream __out)
		throws IOException, NullPointerException;
	
	/**
	 * Dumps the glob that was compiled to a text based format for
	 * decompilation or otherwise.
	 * 
	 * @param __inGlob The input glob to dump.
	 * @param __name The name of the glob.
	 * @param __out Where the output goes.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/05/16
	 */
	void dumpGlob(byte[] __inGlob, String __name, PrintStream __out)
		throws IOException, NullPointerException;
	
	/**
	 * Creates a glob that is used for linking compiled classes together.
	 * 
	 * @param __settings The settings for compilation.
	 * @param __name The name of the glob.
	 * @param __out The destination output.
	 * @return The glob for linking.
	 * @throws IOException On any read/write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/22
	 */
	LinkGlob linkGlob(CompileSettings __settings, String __name,
		OutputStream __out)
		throws IOException, NullPointerException;
	
	/**
	 * Returns the name of the backend.
	 * 
	 * @return The name of the backend.
	 * @since 2020/11/22
	 */
	String name();
	
	/**
	 * Links together the ROM.
	 * 
	 * @param __settings ROM settings.
	 * @param __out The stream to write to.
	 * @param __libs The libraries to combined.
	 * @throws IOException On read/write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/27
	 */
	void rom(RomSettings __settings, OutputStream __out,
		VMClassLibrary... __libs)
		throws IOException, NullPointerException;
}
