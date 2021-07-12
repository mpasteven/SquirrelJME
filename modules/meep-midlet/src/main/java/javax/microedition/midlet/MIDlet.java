// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package javax.microedition.midlet;

import cc.squirreljme.jvm.manifest.JavaManifest;
import cc.squirreljme.jvm.manifest.JavaManifestKey;
import cc.squirreljme.jvm.mle.JarPackageShelf;
import cc.squirreljme.jvm.mle.brackets.JarPackageBracket;
import cc.squirreljme.runtime.cldc.Poking;
import cc.squirreljme.runtime.cldc.annotation.ApiDefinedDeprecated;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.runtime.midlet.ActiveMidlet;
import cc.squirreljme.runtime.midlet.CleanupHandler;
import java.io.IOException;
import java.io.InputStream;

public abstract class MIDlet
{
	/** This is the prefix used to override settings. */
	private static final String _APP_PROPERTY_OVERRIDE =
		"cc.squirreljme.runtime.midlet.override.";
	
	/** The cached manifest for obtaining properties. */
	private JavaManifest _manifest;
	
	/** Is there no manifest? */
	private boolean _noManifest;
	
	/**
	 * Initialize the MIDlet.
	 *
	 * @since 2017/02/08
	 */
	protected MIDlet()
	{
		// We might be on an emulator, so do some poking to make sure
		// everything is working okay
		Poking.poke();
		
		// Set the active midlet to this one
		ActiveMidlet.set(this);
	}
	
	/**
	 * Signals that the MIDlet is in the destruction state.
	 *
	 * @param __uc Is is unconditional?
	 * @throws MIDletStateChangeException If the destruction might stop.
	 * @since 2020/02/29
	 */
	protected abstract void destroyApp(boolean __uc)
		throws MIDletStateChangeException;
	
	/**
	 * This is the starting point for all MIDlets.
	 * 
	 * @throws MIDletStateChangeException If the MIDlet should not start.
	 * @since 2020/10/03
	 */
	protected abstract void startApp()
		throws MIDletStateChangeException;
	
	/**
	 * Checks if the given permission is valid.
	 *
	 * Do not use this to check permissions.
	 *
	 * @param __p The permission to check.
	 * @return {@code 0} if permission is denied, {@code 1} if permitted,
	 * and {@code -1} if unknown.
	 * @throws IllegalStateException If this is a MIDP 3.0 application.
	 * @since 2019/05/05
	 */
	@ApiDefinedDeprecated
	public final int checkPermission(String __p)
		throws IllegalStateException
	{
		// Ignore if null
		if (__p == null)
			return 1;
		
		// Not permitted on MIDP 3 or MEEP
		String profile = this.getAppProperty("microedition-profile");
		if (profile != null)
		{
			// Makes it easier to use
			profile = profile.toLowerCase();
			
			// {@squirreljme.error AD03 Cannot use check permission on
			// MIDP 3.0 or MEEP suite profiles.}
			if (profile.contains("midp-3") || profile.contains("meep"))
				throw new IllegalStateException("AD03");
		}
		
		// Do security check
		try
		{
			// If there is no security manager, just assume everything is
			// okay
			SecurityManager sm = System.getSecurityManager();
			if (sm == null)
				return 1;
			
			// Check it now
			sm.checkPermission(new RuntimePermission(__p));
			return 1;
		}
		catch (SecurityException e)
		{
			return 0;
		}
	}
	
	/**
	 * Obtains the value of a property for the current application. Properties
	 * are defined in the application descriptor along with the manifest file
	 * of the JAR.
	 *
	 * @param __p The property to obtain.
	 * @return The value of the given property or {@code null} if it is not
	 * defined.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/09/07
	 */
	public final String getAppProperty(String __p)
		throws NullPointerException
	{
		// Check
		if (__p == null)
			throw new NullPointerException("NARG");
		
		// Debug
		Debugging.debugNote("getAppProperty(%s)", __p);
		
		// Overridden property?
		JavaManifestKey key = new JavaManifestKey(__p);
		String val = System.getProperty(MIDlet._APP_PROPERTY_OVERRIDE + key);
		if (val != null)
			return val.trim();
		
		// If there is not manifest, ignore this step
		if (!this._noManifest)
		{
			// Lookup JAR manifest
			JavaManifest manifest = this._manifest;
			if (manifest == null)
			{
				// Determine what our JAR is
				JarPackageBracket[] classPath =
					JarPackageShelf.classPath();
				JarPackageBracket ourJar = classPath[classPath.length - 1];
				
				// Some application properties are inside of the manifest so
				// check that
				try (InputStream is = JarPackageShelf.openResource(
					ourJar, "META-INF/MANIFEST.MF"))
				{
					// Not found, force failure
					if (is == null)
						throw new IOException();
					
					// Load it
					manifest = new JavaManifest(is);
					this._manifest = manifest;
					
					Debugging.debugNote("Attr: %s", manifest.getMainAttributes());
				}
				
				// Does not exist or failed to read
				catch (IOException e)
				{
					this._noManifest = true;
					manifest = null;
				}
			}
			
			// Try to get key value
			if (manifest != null)
			{
				String rv = manifest.getMainAttributes().getValue(
					new JavaManifestKey(__p));
				if (rv != null)
					return rv.trim();
			}
		}
		
		// Key not found or no manifest
		return null;
	}
	
	public final MIDletIdentity getMIDletIdentity()
	{
		throw Debugging.todo();
	}
	
	public final long getSplashScreenTime()
	{
		throw Debugging.todo();
	}
	
	public final boolean isSelectedScreenSaver()
	{
		throw Debugging.todo();
	}
	
	/**
	 * Used by the application to notify the MIDlet that it has entered the
	 * destroyed state and resources should be cleaned up and such. When this
	 * is called the program will be terminated.
	 *
	 * @since 2019/04/15
	 */
	public final void notifyDestroyed()
	{
		Debugging.debugNote("Notification of destruction");
		
		// Run all cleanup handlers
		CleanupHandler.runAll();
		
		// Kill the program
		System.exit(0);
	}
	
	/**
	 * Notifies that the application should be paused now.
	 *
	 * This does nothing on SquirrelJME.
	 *
	 * @since 2017/02/08
	 */
	@ApiDefinedDeprecated
	public final void notifyPaused()
	{
	}
	
	/**
	 * This is code which should be called before the application is paused.
	 * 
	 * This does nothing on SquirrelJME.
	 *
	 * @since 2017/02/08
	 */
	@ApiDefinedDeprecated
	public void pauseApp()
	{
	}
	
	/**
	 * Requests that the system perform the given request.
	 * 
	 * @param __url The URL to request on the native system.
	 * @return This wil return {@code true} if the MIDlet must exit first
	 * before the request can be handled.
	 * @throws Exception Will throw
	 * {@code javax.microedition.io.ConnectionNotFoundException} if this
	 * connection type is not supported.
	 * @since 2020/07/02
	 */
	public final boolean platformRequest(String __url)
		throws Exception
	{
		// Games from Konami require this to return true even though that means
		// the application should terminate after this point. This is handled
		// and does not thrown an exception on bad requests.
		// Returning false here will cause the games to not work.
		if ("hjoja".equals(__url))
			return true;
		
		// Debug
		todo.DEBUG.note("Platform request: %s", __url);
		
		throw new todo.TODO();
	}
	
	/**
	 * This is called when an application was resumed.
	 * 
	 * On SquirrelJME this has no effect.
	 * 
	 * @since 2020/07/03
	 */
	@ApiDefinedDeprecated
	public final void resumeRequest()
	{
	}
	
	public static String getAppProperty(String __name, String __vend,
		String __attrname, String __attrdelim)
		throws NullPointerException
	{
		if (__attrname == null)
			throw new NullPointerException("NARG");
		
		throw new todo.TODO();
	}
}

