// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.launch;

import cc.squirreljme.jvm.mle.brackets.JarPackageBracket;
import cc.squirreljme.jvm.suite.DependencyInfo;
import cc.squirreljme.jvm.suite.EntryPoint;
import cc.squirreljme.jvm.suite.SuiteInfo;

/**
 * Represents a single application that can be launched.
 *
 * @since 2020/12/28
 */
public final class JavaApplication
	extends Application
{
	/** Manifest property for appearing on the launcher. */
	private static final String _NO_LAUNCHER =
		"X-SquirrelJME-NoLauncher";
	
	/** The suite information. */
	protected final SuiteInfo info;
	
	/** The entry point used. */
	protected final EntryPoint entryPoint;
	
	/**
	 * Initializes the application.
	 * 
	 * @param __info The JAR information.
	 * @param __jar The JAR to reference.
	 * @param __libs The lazy library initializer.
	 * @param __entryPoint The entry point used.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/29
	 */
	JavaApplication(SuiteInfo __info, JarPackageBracket __jar,
		__Libraries__ __libs, EntryPoint __entryPoint)
		throws NullPointerException
	{
		super(__jar, __libs);
		
		if (__info == null || __jar == null || __libs == null ||
			__entryPoint == null)
			throw new NullPointerException("NARG");
		
		this.info = __info;
		this.entryPoint = __entryPoint;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/12/29
	 */
	@Override
	public String displayName()
	{
		EntryPoint entry = this.entryPoint;
		String result = (entry.isMidlet() ? entry.name() :
			this.info.manifest().getMainAttributes()
				.getValue("MIDlet-Name"));
		
		return (result == null ? entry.entryPoint() : result);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/12/29
	 */
	@Override
	public EntryPoint entryPoint()
	{
		return this.entryPoint;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/12/29
	 */
	@Override
	public boolean isNoLauncher()
	{
		return this.info.manifest().getMainAttributes()
			.definesValue(JavaApplication._NO_LAUNCHER);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/06/13
	 */
	@Override
	public DependencyInfo loaderDependencies()
	{
		return this.info.dependencies();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/06/13
	 */
	@Override
	public String[] loaderEntryArgs()
	{
		EntryPoint entry = this.entryPoint;
		if (entry.isMidlet())
			return new String[]{entry.entryPoint()};
		return new String[0];
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/06/13
	 */
	@Override
	public String loaderEntryClass()
	{
		EntryPoint entry = this.entryPoint;
		if (entry.isMidlet())
			return "javax.microedition.midlet.__MainHandler__";
		return entry.entryPoint();
	}
}
