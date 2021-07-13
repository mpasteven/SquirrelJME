// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.launch;

import cc.squirreljme.jvm.mle.JarPackageShelf;
import cc.squirreljme.jvm.mle.TaskShelf;
import cc.squirreljme.jvm.mle.brackets.JarPackageBracket;
import cc.squirreljme.jvm.mle.brackets.TaskBracket;
import cc.squirreljme.jvm.mle.constants.TaskPipeRedirectType;
import cc.squirreljme.jvm.suite.DependencyInfo;
import cc.squirreljme.jvm.suite.EntryPoint;
import java.io.InputStream;
import java.util.Objects;

/**
 * Base application support which is able to know about and launch other
 * applications.
 *
 * @since 2021/06/13
 */
public abstract class Application
{
	/** The JAR this references. */
	protected final JarPackageBracket jar;
	
	/** The library information. */
	private final __Libraries__ _libraries;
	
	/**
	 * The application to load.
	 * 
	 * @param __jar The JAR used.
	 * @param __libs The libraries to map.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/06/13
	 */
	Application(JarPackageBracket __jar, __Libraries__ __libs)
		throws NullPointerException
	{
		if (__jar == null || __libs == null)
			throw new NullPointerException("NARG");
		
		this.jar = __jar;
		this._libraries = __libs;
	}
	
	/**
	 * Returns the display name of the application.
	 * 
	 * @return The display name of the application.
	 * @since 2020/12/29
	 */
	public abstract String displayName();
	
	/**
	 * Returns the entry point of the task.
	 * 
	 * @return The entry point that represents this application.
	 * @since 2020/12/29
	 */
	public abstract EntryPoint entryPoint();
	
	/**
	 * Returns the dependencies needed for loading
	 * 
	 * @return Dependencies needed for loading.
	 * @since 2021/06/13
	 */
	public abstract DependencyInfo loaderDependencies();
	
	/**
	 * Returns the loader entry point arguments.
	 * 
	 * @return Entry point arguments for loading.
	 * @since 2021/06/13
	 */
	public abstract String[] loaderEntryArgs();
	
	/**
	 * Returns the loader entry class.
	 * 
	 * @return The load entry class, the class which is responsible for
	 * starting the application.
	 * @since 2021/06/13
	 */
	public abstract String loaderEntryClass();
	
	/**
	 * Returns the stream to the application icon data.
	 * 
	 * @return The stream for the application data or {@code null} if there is
	 * no icon.
	 * @since 2020/12/29
	 */
	public final InputStream iconStream()
	{
		String imgRc = this.entryPoint().imageResource();
		if (imgRc != null)
			return JarPackageShelf.openResource(this.jar, imgRc);
		
		return null;
	}
	
	/**
	 * Indicates that this should not appear on the launcher.
	 * 
	 * @return If this should not appear on the launcher.
	 * @since 2020/12/29
	 */
	public boolean isNoLauncher()
	{
		return false;
	}
	
	/**
	 * Launches the specified task.
	 * 
	 * @return The bracket for the task.
	 * @since 2020/12/29
	 */
	public final TaskBracket launch()
	{
		// Find libraries to base off
		Library[] libraries = this._libraries.matchDependencies(
			this.loaderDependencies(), true);
		int numLibs = libraries.length;
		
		// Determine the class path used from this
		JarPackageBracket[] classPath = new JarPackageBracket[numLibs + 1];
		for (int i = 0; i < numLibs; i++)
			classPath[i] = libraries[i].jar;
		classPath[numLibs] = this.jar;
		
		// Have the task launch itself
		return TaskShelf.start(classPath,
			this.loaderEntryClass(),
			this.loaderEntryArgs(),
			new String[0],
			TaskPipeRedirectType.TERMINAL,
			TaskPipeRedirectType.TERMINAL);
	}
	
	/**
	 * Returns the SquirrelJME name of the application.
	 * 
	 * @return The SquirrelJME name of the application.
	 * @since 2020/12/29
	 */
	public final String squirrelJMEName()
	{
		String fromName = Objects.toString(this.displayName(),
			Objects.toString(this.entryPoint().name(),
				this.entryPoint().entryPoint()));
		
		StringBuilder sb = new StringBuilder(fromName.length());
		for (int i = 0, n = fromName.length(); i < n; i++)
		{
			char c = fromName.charAt(i);
			
			if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) 
				sb.append(c);
			else if (c >= 'A' && c <= 'Z')
				sb.append((char)('a' + (c - 'A')));
		}
		
		// If blank, put something here at least
		if (sb.length() <= 0)
			sb.append(fromName.hashCode());
		
		return sb.toString();
	}
}
