// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.jarfile;

/**
 * Something which has a BootJAR Pointer.
 *
 * @since 2021/04/08
 */
public interface HasBootJarPointer
{
	/**
	 * Returns the pointer.
	 * 
	 * @return The pointer.
	 * @since 2021/04/08
	 */
	BootJarPointer pointer();
}
