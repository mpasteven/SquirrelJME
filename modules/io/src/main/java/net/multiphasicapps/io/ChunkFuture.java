// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.io;

/**
 * This represents a future chunk.
 *
 * @since 2020/11/29
 */
public interface ChunkFuture
{
	/**
	 * Obtains the value from this future.
	 * 
	 * @return The value of this future.
	 * @since 2020/11/29
	 */
	int get();
}
