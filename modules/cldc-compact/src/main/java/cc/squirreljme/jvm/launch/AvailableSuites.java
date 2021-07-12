// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.launch;

/**
 * This contains the list of suites and libraries.
 *
 * @since 2020/12/28
 */
public final class AvailableSuites
{
	/** The applications available. */
	private final Application[] _apps;
	
	/** The lazy loaded library set. */
	private final __Libraries__ _libraries;
	
	/**
	 * Initializes the available suites.
	 * 
	 * @param __libs The libraries to use for lazy initialization.
	 * @param __apps The applications that are available.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/29
	 */
	AvailableSuites(__Libraries__ __libs, Application... __apps)
		throws NullPointerException
	{
		if (__libs == null || __apps == null)
			throw new NullPointerException("NARG");
		
		this._libraries = __libs;
		this._apps = __apps.clone();
	}
}
