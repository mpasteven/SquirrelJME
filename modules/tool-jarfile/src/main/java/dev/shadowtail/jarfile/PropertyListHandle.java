// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.jarfile;

import cc.squirreljme.jvm.summercoat.constants.MemHandleKind;

/**
 * This is a handle which represents a list of properties.
 *
 * @since 2020/12/20
 */
public class PropertyListHandle
	extends ListValueHandle
{
	/**
	 * Initializes the base property list handle.
	 *
	 * @param __kind The {@link MemHandleKind}.
	 * @param __id The memory handle ID.
	 * @param __memActions The actions to use.
	 * @param __baseSize The base size of the list.
	 * @param __count The size of the list.
	 * @throws IllegalArgumentException If the memory handle does not have the
	 * correct security bits specified or if the count is negative.
	 * @since 2020/12/20
	 */
	PropertyListHandle(int __kind, int __id, MemActions __memActions,
		int __baseSize, int __count)
		throws IllegalArgumentException
	{
		super(__kind, __id, __memActions, __baseSize, __count);
	}
}
