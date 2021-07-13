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
import cc.squirreljme.jvm.suite.Configuration;
import cc.squirreljme.jvm.suite.DependencyInfo;
import cc.squirreljme.jvm.suite.EntryPoint;
import cc.squirreljme.jvm.suite.InvalidSuiteException;
import cc.squirreljme.jvm.suite.Profile;
import java.util.Map;
import java.util.Objects;

/**
 * Describes an i-mode application and how to launch it.
 *
 * @since 2021/06/13
 */
public class IModeApplication
	extends Application
{
	/** Boot class. */
	private static final String _BOOT_CLASS =
		"com.nttdocomo.ui.__AppLaunch__";
	
	/** The application name. */
	private static final String _APP_NAME =
		"AppName";
	
	/** The application launch class. */
	private static final String _APP_CLASS =
		"AppClass";
	
	/** Application parameters. */
	private static final String _APP_PARAMS =
		"AppParam";
	
	/** The configuration to use. */
	private static final String _CONFIGURATION_VER =
		"Configurationver";
	
	/** KVM Version, same as {@link #_CONFIGURATION_VER}. */
	private static final String _KVM_VER =
		"KvmVer";
	
	/** Profile version (DoJa 2.0+). */
	private static final String _PROFILE_VER =
		"ProfileVer";
	
	/** Application icon. */
	private static final String _APP_ICON =
		"AppIcon";
	
	/** ADF Properties. */
	private final Map<String, String> _adfProps;
	
	/**
	 * The application to load.
	 *
	 * @param __jar The JAR used.
	 * @param __libs The libraries to map.
	 * @param __adfProps Properties for the ADF/JAM.
	 * @throws InvalidSuiteException If this suite is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/06/13
	 */
	IModeApplication(JarPackageBracket __jar, __Libraries__ __libs,
		Map<String, String> __adfProps)
		throws InvalidSuiteException, NullPointerException
	{
		super(__jar, __libs);
		
		this._adfProps = __adfProps;
		
		if (!__adfProps.containsKey(IModeApplication._APP_NAME) ||
			!__adfProps.containsKey(IModeApplication._APP_CLASS))
			throw new InvalidSuiteException();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/06/13
	 */
	@Override
	public String displayName()
	{
		return this._adfProps.get("AppName");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/06/13
	 */
	@Override
	public EntryPoint entryPoint()
	{
		return new EntryPoint(this.displayName(),
			this._adfProps.get(IModeApplication._APP_CLASS),
			this._adfProps.get(IModeApplication._APP_ICON),
			false); 
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/06/13
	 */
	@Override
	public DependencyInfo loaderDependencies()
	{
		// This determines which library set to load
		String config = Objects.toString(
			this._adfProps.get(IModeApplication._CONFIGURATION_VER),
			this._adfProps.get(IModeApplication._KVM_VER));
		String profile = this._adfProps.get(IModeApplication._PROFILE_VER);
		
		// Default to old stuff
		if (config == null || config.isEmpty())
			config = "CLDC-1.0";
		if (profile == null || profile.isEmpty())
			profile = "DoJa-1.0";
		
		return new DependencyInfo(new Configuration(config),
			new Profile(profile));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/06/13
	 */
	@Override
	public String[] loaderEntryArgs()
	{
		EntryPoint entry = this.entryPoint();
		String args = this._adfProps.get(IModeApplication._APP_PARAMS);
		
		if (args == null)
			return new String[]{entry.entryPoint()};
		return new String[]{entry.entryPoint(), args};
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/06/13
	 */
	@Override
	public String loaderEntryClass()
	{
		// Always use the application helper
		return IModeApplication._BOOT_CLASS;
	}
}
