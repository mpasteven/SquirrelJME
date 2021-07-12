// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.launch;

import cc.squirreljme.jvm.manifest.JavaManifest;
import cc.squirreljme.jvm.mle.JarPackageShelf;
import cc.squirreljme.jvm.mle.brackets.JarPackageBracket;
import cc.squirreljme.jvm.mle.exceptions.MLECallError;
import cc.squirreljme.jvm.suite.EntryPoint;
import cc.squirreljme.jvm.suite.EntryPoints;
import cc.squirreljme.jvm.suite.InvalidSuiteException;
import cc.squirreljme.jvm.suite.SuiteInfo;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This is a scanner which can read all of the application groups that are
 * available.
 *
 * @since 2020/12/28
 */
public final class SuiteScanner
{
	/** Data resource name. */
	private static final String _DATA_RESOURCE =
		"$DATA$";
	
	/**
	 * Not used.
	 * 
	 * @since 2020/12/28
	 */
	private SuiteScanner()
	{
	}
	
	/**
	 * Scans all of the available suites and returns imformation that is needed
	 * for them to properly launch.
	 * 
	 * @return The state of scanned suites.
	 * @since 2020/12/28
	 */
	public static AvailableSuites scanSuites()
	{
		return SuiteScanner.scanSuites(null);
	}
	
	/**
	 * Scans all of the available suites and returns imformation that is needed
	 * for them to properly launch.
	 * 
	 * @param __listener The listener for suites as they are scanned, used to
	 * indicate progress.
	 * @return The state of scanned suites.
	 * @since 2020/12/29
	 */
	public static AvailableSuites scanSuites(SuiteScanListener __listener)
	{
		// Get all of the available libraries
		JarPackageBracket[] jars = JarPackageShelf.libraries();
		int numJars = jars.length;
		
		// Load the names of all JARs and map to brackets, this is later used
		// for i-mode lookup.
		Map<String, JarPackageBracket> nameToJar = new HashMap<>();
		for (JarPackageBracket jar : jars)
		{
			String name = JarPackageShelf.libraryPath(jar);
			if (name != null)
				nameToJar.put(name, jar);
		}
		
		// Libraries are lazily handled on launching
		__Libraries__ libs = new __Libraries__();
		
		// Applications which should be loaded
		List<Application> result = new LinkedList<>();
		
		// Locate programs via the library path
		for (int i = 0, n = jars.length; i < n; i++)
		{
			JarPackageBracket jar = jars[i];
			
			// Ignore non-JARs
			String libPath = JarPackageShelf.libraryPath(jar);
			if (!libPath.endsWith(".jar") && !libPath.endsWith(".JAR"))
				continue;
			
			// Debug
			Debugging.debugNote("Checking %s...", libPath);
			
			// Scan for multiple application types, since it is very possible
			// for an application to be in hybrid form (such as MIDP/i-mode)
			SuiteScanner.__scanJava(__listener, numJars, libs,
				result, i, jar);
			SuiteScanner.__scanIMode(__listener, numJars, libs, nameToJar,
				result, i, jar);
		}
		
		// Finalize suite list
		return new AvailableSuites(libs, result
			.<Application>toArray(new Application[result.size()]));
	}
	
	/**
	 * Scans for an i-mode application and loads them.
	 * 
	 * @param __listener The listener used for load events.
	 * @param __nameToJar The name to JAR mapping, used to find the JAM file.
	 * @param __result The target applications.
	 * @param __jarDx The JAR index.
	 * @param __jar The JAR to check.
	 * @since 2021/06/013
	 */
	private static void __scanIMode(SuiteScanListener __listener,
		int __numJars, __Libraries__ __libs,
		Map<String, JarPackageBracket> __nameToJar,
		List<Application> __result,
		int __jarDx, JarPackageBracket __jar)
	{
		// Try to determine what our JAM would be called
		String jarName = JarPackageShelf.libraryPath(__jar);
		String jamName;
		if (jarName.endsWith(".jar"))
			jamName = jarName.substring(0, jarName.length() - 4) + ".jam";
		else if (jarName.endsWith(".JAR"))
			jamName = jarName.substring(0, jarName.length() - 4) + ".JAM";
		else
			jamName = jarName + ".jam";
		
		// If there is no JAM file, this cannot be an i-mode application
		JarPackageBracket jam = __nameToJar.get(jamName);
		if (jam == null)
			return;
		
		// Load the ADF/JAM descriptor that describes this application
		Map<String, String> adfProps = new LinkedHashMap<>();
		try (InputStream jamIn = JarPackageShelf.openResource(jam,
			SuiteScanner._DATA_RESOURCE))
		{
			// Missing? Cannot be an i-mode application
			if (jamIn == null)
				return;
			
			// Load in the JAM (Is encoded in Japanese)
			try (BufferedReader jamBr = new BufferedReader(
				new InputStreamReader(jamIn, "shift-jis")))
			{
				for (;;)
				{
					// EOF?
					String ln = jamBr.readLine();
					if (ln == null)
						break;
					
					// No equal sign, ignore
					int eq = ln.indexOf('=');
					if (eq < 0)
						continue;
					
					// Split into key and value form
					String key = ln.substring(0, eq).trim();
					String val = ln.substring(eq + 1).trim();
					
					// Store into if the key is valid
					if (!key.isEmpty())
						adfProps.put(key, val);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return;
		} 
			
		// Load application
		try
		{
			Application app = new IModeApplication(__jar, __libs, adfProps);
			__result.add(app);
			
			// Indicate that it was scanned
			if (__listener != null)
				__listener.scanned(app, __jarDx, __numJars);
		}
		catch (InvalidSuiteException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Scans for Java applications in the given JAR.
	 * 
	 * @param __listener The listener used.
	 * @param __numJars The number of JARs.
	 * @param __libs The available support libraries.
	 * @param __result Where applications go.
	 * @param __jarDx The Jar Index.
	 * @param __jar The JAR to check.
	 * @since 2021/06/13
	 */
	private static void __scanJava(SuiteScanListener __listener,
		int __numJars, __Libraries__ __libs, List<Application> __result,
		int __jarDx, JarPackageBracket __jar)
	{
		// Try to read the manifest from the given JAR and process the
		// suite information
		SuiteInfo info;
		JavaManifest man;
		try (InputStream rc = JarPackageShelf.openResource(__jar,
			"META-INF/MANIFEST.MF"))
		{
			// If no manifest exists, might not be a JAR
			if (rc == null)
				return;
			
			man = new JavaManifest(rc);
			info = new SuiteInfo(man);
		}
		
		// Prevent bad JARs and files from messing things up
		catch (IOException|InvalidSuiteException|MLECallError e)
		{
			e.printStackTrace();
			return;
		}
		
		switch (info.type())
		{
			// Handle library
			case LIBLET:
			case SQUIRRELJME_API:
				__libs.__register(info, __jar);
				return;
			
			// Handle application
			case MIDLET:
				// Setup application information for all possible entry
				// points
				for (EntryPoint e : new EntryPoints(man))
				{
					// Load application
					JavaApplication app = new JavaApplication(info, __jar, __libs, e);
					__result.add(app);
					
					// Indicate that it was scanned
					if (__listener != null)
						__listener.scanned(app, __jarDx, __numJars);
				}
				return;
			
			// Unknown?
			default:
				throw Debugging.oops();
		}
	}
}
