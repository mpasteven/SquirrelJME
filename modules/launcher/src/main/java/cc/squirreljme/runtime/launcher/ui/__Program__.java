// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.runtime.launcher.ui;

import cc.squirreljme.jvm.manifest.JavaManifest;
import cc.squirreljme.jvm.mle.JarPackageShelf;
import cc.squirreljme.jvm.mle.brackets.JarPackageBracket;
import cc.squirreljme.jvm.suite.EntryPoint;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Image;

/**
 * Stores the program information which is mapped to what is displayed.
 *
 * @since 2018/11/16
 */
@Deprecated
final class __Program__
{
	/** The JAR used. */
	protected final JarPackageBracket jar;
	
	/** The entry point used. */
	protected final EntryPoint entry;
	
	/** The display name of this suite. */
	protected final String displayName;
	
	/** The currently active task. */
	final __ActiveTask__ _activeTask;
	
	/** The icon to show for this program. */
	Image _icon;
	
	/** Was an icon loaded? */
	boolean _loadedIcon;
	
	/**
	 * Initializes the program.
	 * 
	 * @param __jar The JAR the program is in.
	 * @param __man The manifest of the JAR.
	 * @param __activeTask The active task.
	 * @param __entry The entry point.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/17
	 */
	public __Program__(JarPackageBracket __jar, JavaManifest __man,
		__ActiveTask__ __activeTask, EntryPoint __entry)
		throws NullPointerException
	{
		if (__jar == null || __man == null || __activeTask == null ||
			__entry == null)
			throw new NullPointerException("NARG");
		
		this.jar = __jar;
		this._activeTask = __activeTask;
		this.entry = __entry;
		
		String midName = __man.getMainAttributes().getValue("MIDlet-Name");
		this.displayName = (__entry.isMidlet() ? __entry.name() : midName);
	}
	
	/**
	 * The display image for this suite.
	 *
	 * @return The display image.
	 * @since 2018/11/16
	 */
	public final Image displayImage()
	{
		// Image already known?
		Image rv = this._icon;
		if (rv != null || this._loadedIcon)
			return rv;
		
		// No image is here at all
		String iconRc = this.entry.imageResource();
		if (iconRc == null || iconRc.isEmpty())
			return null;
		
		// Load image from JAR resource
		try (InputStream in = JarPackageShelf.openResource(this.jar, iconRc))
		{
			// No resource exists
			if (in == null)
				return null;
			
			// Load image data
			rv = Image.createImage(in);
		}
		
		// Not a valid image, ignore
		catch (IOException e)
		{
			rv = null;
			
			// Failed, so maybe attempt to debug why?
			e.printStackTrace();
		}
		
		// Cache and use it, flag that an icon was loaded so it does not
		// constantly try to load icons for programs
		this._loadedIcon = true;
		this._icon = rv;
		return rv;
	}
	
	/**
	 * The display name for this suite.
	 *
	 * @return The display name.
	 * @since 2018/11/16
	 */
	public final String displayName()
	{
		return this.displayName;
	}
}

