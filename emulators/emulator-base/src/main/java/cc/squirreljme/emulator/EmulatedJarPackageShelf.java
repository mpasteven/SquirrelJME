// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.emulator;

import cc.squirreljme.jvm.mle.JarPackageShelf;
import cc.squirreljme.jvm.mle.brackets.JarPackageBracket;
import cc.squirreljme.jvm.mle.exceptions.MLECallError;
import cc.squirreljme.vm.DataContainerLibrary;
import cc.squirreljme.vm.JarClassLibrary;
import cc.squirreljme.vm.VMClassLibrary;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Handlers for {@link JarPackageShelf}.
 *
 * @since 2020/10/31
 */
public final class EmulatedJarPackageShelf
{
	/** Cache of our own classpath. */
	private static JarPackageBracket[] _CLASSPATH_CACHE;
	
	/** Cache of loaded libraries. */
	private static JarPackageBracket[] _LIB_CACHE;
	
	/**
	 * Returns our classpath.
	 * 
	 * @return Our classpath.
	 * @since 2021/06/24
	 */
	public static JarPackageBracket[] classPath()
	{
		// Use single cache for it, if available
		JarPackageBracket[] rv = EmulatedJarPackageShelf._CLASSPATH_CACHE;
		if (rv != null)
			return rv.clone();
		
		// Use the system property to know our true classpath
		String paths = System.getProperty(EmulatedTaskShelf.RUN_CLASSPATH);
		if (paths != null)
			rv = EmulatedJarPackageShelf.__loadPaths(paths);
		else
			rv = new JarPackageBracket[0];
			
		// Store cache for later usages
		EmulatedJarPackageShelf._CLASSPATH_CACHE = rv;
		return rv.clone();
	}
	
	/**
	 * Returns the libraries which are available to the virtual machine.
	 * 
	 * @return The libraries that are currently available.
	 * @since 2020/10/31
	 */
	public static JarPackageBracket[] libraries()
	{
		// Use single cache for it, if available
		JarPackageBracket[] rv = EmulatedJarPackageShelf._LIB_CACHE;
		if (rv != null)
			return rv.clone();
		
		// For hosted VMs, the libraries are stored in a system property so
		// that they can be accessed.
		String paths = System.getProperty(
			EmulatedTaskShelf.AVAILABLE_LIBRARIES);
		if (paths != null)
			rv = EmulatedJarPackageShelf.__loadPaths(paths);
		else
			rv = new JarPackageBracket[0];
		
		// Store cache for later usages
		EmulatedJarPackageShelf._LIB_CACHE = rv;
		return rv.clone();
	}
	
	/**
	 * Returns the path to the given JAR
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
			throw new MLECallError("No JAR.");
		
		Path path = ((EmulatedJarPackageBracket)__jar).vmLib.path();
		return Objects.toString(path, null);
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
	 * @since 2020/10/31
	 */
	public static InputStream openResource(JarPackageBracket __jar,
		String __rc)
		throws MLECallError
	{
		if (__jar == null || __rc == null)
			throw new MLECallError("No JAR or resource.");
		
		return ((EmulatedJarPackageBracket)__jar).openResource(__rc);
	}
	
	/**
	 * Loads paths from the given JAR set.
	 * 
	 * @param __paths The paths to load.
	 * @return Loaded JAR brackets for the given paths.
	 * @since 2021/06/24
	 */
	private static JarPackageBracket[] __loadPaths(String __paths)
	{
		List<JarPackageBracket> fill = new ArrayList<>();
		for (int at = 0, next;; at = next + 1)
		{
			// Get the segment from this section
			next = __paths.indexOf(File.pathSeparatorChar, at + 1);
			String segment = (next < 0 ? __paths.substring(at) :
				__paths.substring(at, next));
			Path segPath = Paths.get(segment);
			
			// Are we filling a JAR or filling random file data?
			VMClassLibrary vmLib;
			if (JarClassLibrary.isJar(segment))
				vmLib = new JarClassLibrary(segPath);
			else
				vmLib = new DataContainerLibrary(segPath);
			
			// Wrap class library container
			fill.add(new EmulatedJarPackageBracket(vmLib));
			
			// Processing no more
			if (next < 0)
				break;
		}
		
		// Store them all
		return fill.toArray(new JarPackageBracket[fill.size()]);
	}
}
