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
 * This represents a handle for the constant pool of a class.
 *
 * @since 2020/12/19
 */
public final class PoolHandle
	extends ListValueHandle
{
	/**
	 * Initializes the base memory handle.
	 *
	 * @param __id The memory handle ID.
	 * @throws IllegalArgumentException If the memory handle does not have the
	 * correct security bits specified.
	 * @since 2020/12/19
	 */
	PoolHandle(int __id)
		throws IllegalArgumentException
	{
		super(__id);
	}
}
