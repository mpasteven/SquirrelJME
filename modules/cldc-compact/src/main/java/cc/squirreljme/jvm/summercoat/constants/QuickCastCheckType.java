// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.summercoat.constants;

/**
 * The quick cast check type.
 *
 * @since 2021/01/31
 */
public interface QuickCastCheckType
{
	/** Not yet known. */
	byte UNKNOWN =
		0;
	
	/** Calculated to not be compatible. */
	byte NOT_COMPATIBLE =
		-1;
	
	/** Calculated to be compatible. */
	byte COMPATIBLE =
		1;
}
