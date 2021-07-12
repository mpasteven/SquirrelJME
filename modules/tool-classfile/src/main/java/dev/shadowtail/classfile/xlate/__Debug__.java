// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.xlate;

/**
 * Stores debug enabled flag.
 *
 * @since 2019/04/16
 */
final class __Debug__
{
	/** Debug enabled? */
	public static final boolean ENABLED;
	
	/*
	  Gets the debug flag.
	 
	  @since 2019/04/16
	 */
	static
	{
		// Get property
		boolean en = false;
		try
		{
			// {@squirreljme.property dev.shadowtail.classfile.xlate.debug=bool
			// Sets whether the massive amounts of debug test should be
			// printed in all the translation codes.}
			en = Boolean.getBoolean("dev.shadowtail.classfile.xlate.debug");
		}
		catch (SecurityException e)
		{
		}
		
		// Set
		ENABLED = en;
	}
}

