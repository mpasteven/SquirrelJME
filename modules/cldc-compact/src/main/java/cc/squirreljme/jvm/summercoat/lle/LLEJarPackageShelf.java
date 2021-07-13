// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.summercoat.lle;

import cc.squirreljme.jvm.Assembly;
import cc.squirreljme.jvm.mle.DebugShelf;
import cc.squirreljme.jvm.mle.JarPackageShelf;
import cc.squirreljme.jvm.mle.brackets.JarPackageBracket;
import cc.squirreljme.jvm.mle.constants.VerboseDebugFlag;
import cc.squirreljme.jvm.mle.exceptions.MLECallError;
import cc.squirreljme.jvm.summercoat.SystemCall;
import cc.squirreljme.jvm.summercoat.constants.RuntimeVmAttribute;
import cc.squirreljme.jvm.summercoat.ld.pack.JarRom;
import cc.squirreljme.jvm.summercoat.ld.pack.PackRom;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import java.io.InputStream;

/**
 * This allows access to the library class path and resources.
 *
 * @since 2020/06/07
 */
public final class LLEJarPackageShelf
{
	/** The Pack ROM, cached. */
	private static volatile PackRom _packRom;
	
	/** The libraries which are available, cached. */
	private static volatile JarPackageBracket[] _libraries;
	
	/**
	 * Returns the classpath of the current program.
	 *
	 * @return The classpath of the current program.
	 * @since 2020/06/07
	 */
	public static JarPackageBracket[] classPath()
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Checks if the given brackets refer to the same JAR.
	 * 
	 * @param __a The first JAR.
	 * @param __b The second JAR.
	 * @return If these are equal or not.
	 * @throws MLECallError If either argument is {@code null}.
	 * @since 2020/07/02
	 */
	public static boolean equals(
		JarPackageBracket __a, JarPackageBracket __b)
		throws MLECallError
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Returns the libraries which are available to the virtual machine.
	 * 
	 * @return The libraries that are currently available.
	 * @see JarPackageShelf#libraries()
	 * @since 2020/10/31
	 */
	public static JarPackageBracket[] libraries()
	{
		// Restore from the cache
		JarPackageBracket[] rv = LLEJarPackageShelf._libraries;
		if (rv != null)
			return rv.clone();
		
		// Load the available libraries
		rv = LLEJarPackageShelf.__packRom().libraries();
		
		// Cache for later and use this
		LLEJarPackageShelf._libraries = rv;
		return rv.clone();
	}
	
	/**
	 * Returns the path to the given JAR.
	 * 
	 * Note that this may or may not be a physical path, it could be a
	 * representation of the JAR file in string form.
	 * 
	 * @param __jar The JAR to get the path of.
	 * @return The path of the given JAR.
	 * @throws MLECallError If the JAR is not valid.
	 * @since 2020/10/31
	 */
	public static String libraryPath(JarPackageBracket __jar)
		throws MLECallError
	{
		if (__jar == null)
			throw new MLECallError("NARG");
		
		// The string representation is the JAR name
		return LLEJarPackageShelf.__rom(__jar).toString();
	}
	
	/**
	 * Opens the resource from the input stream.
	 *
	 * @param __jar The JAR to open.
	 * @param __rc The resource to load from the given JAR.
	 * @return Input stream to the resource, may be {@code null} if it does
	 * not exist.
	 * @throws MLECallError If the JAR is not valid or the resource was not
	 * specified.
	 * @since 2020/06/07
	 */
	public static InputStream openResource(JarPackageBracket __jar,
		String __rc)
		throws MLECallError
	{
		if (__jar == null || __rc == null)
			throw new MLECallError("NARG");
		
		return LLEJarPackageShelf.__rom(__jar).openResourceAsStream(__rc);
	}
	
	/**
	 * Returns the cached pack ROM or initializes a new one if needed.
	 * 
	 * @return The pack ROM.
	 * @since 2021/02/09
	 */
	private static PackRom __packRom()
	{
		// Has this been cached already?
		PackRom rv = LLEJarPackageShelf._packRom;
		if (rv != null)
			return rv;
		
		// Where is the ROM located?
		long romAddr = Assembly.longPack(
			SystemCall.runtimeVmAttribute(RuntimeVmAttribute.ROM_ADDR_LOW),
			SystemCall.runtimeVmAttribute(RuntimeVmAttribute.ROM_ADDR_HIGH));
		
		// Verbosity on methods
		DebugShelf.verbose(VerboseDebugFlag.METHOD_ENTRY);
		
		// Debug
		Debugging.debugNote("ROM is at %#08x", romAddr);
		
		// Create a new manager
		return PackRom.load(romAddr);
	}
	
	/**
	 * Returns the JAR ROM.
	 * 
	 * @param __jar The JAR to get from.
	 * @return The mapped JAR.
	 * @throws MLECallError If this is not a correct Jar.
	 * @since 2021/04/06
	 */
	private static JarRom __rom(JarPackageBracket __jar)
		throws MLECallError
	{
		// {@squirreljme.error ZZ54 Not the right JAR type.}
		if (!(__jar instanceof JarRom))
			throw new MLECallError("ZZ54");
		
		return (JarRom)__jar;
	}
}
