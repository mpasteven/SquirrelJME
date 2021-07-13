// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.runtime.launcher.ui;

import cc.squirreljme.jvm.launch.Application;
import cc.squirreljme.jvm.launch.SuiteScanListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;

/**
 * This listens for when suites have been detected by the scanner.
 *
 * @since 2020/12/29
 */
final class __ProgressListener__
	implements SuiteScanListener
{
	/** The suites that have been listed. */
	protected final Collection<Application> listedSuites;
	
	/** The program list. */
	protected final List programList;
	
	/**
	 * Initializes the progress listener.
	 * 
	 * @param __programList The program list used.
	 * @param __listedSuites The suites that are available.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/29
	 */
	public __ProgressListener__(List __programList,
		Collection<Application> __listedSuites)
		throws NullPointerException
	{
		if (__programList == null || __listedSuites == null)
			throw new NullPointerException("NARG");
		
		this.programList = __programList;
		this.listedSuites = __listedSuites;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/12/29
	 */
	@Override
	public void scanned(Application __app, int __dx, int __total)
	{
		// Do nothing if this is not to appear on the launcher
		if (__app.isNoLauncher())
			return;
		
		Collection<Application> listedSuites = this.listedSuites;
		List programList = this.programList;
		
		// Update title to reflect this discovery
		programList.setTitle(String.format(
			"Querying Suites (Found %d of %d)...", __dx + 1, __total));
		
		// Remember this suite
		listedSuites.add(__app);
		
		// Try to load the image for the application
		Image icon = null;
		try (InputStream iconData = __app.iconStream())
		{
			if (iconData != null)
				icon = Image.createImage(iconData);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		// Add entry to the list
		programList.append(__app.displayName(), icon);
	}
}
