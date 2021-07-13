// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.summercoat.ld.mem;

import cc.squirreljme.jvm.mle.constants.ByteOrderType;

/**
 * Interface used to describe any memory that has a byte order.
 *
 * @since 2021/02/14
 */
public interface HasByteOrder
{
	/**
	 * Returns the {@link ByteOrderType} of the memory.
	 * 
	 * @return The {@link ByteOrderType} of the memory.
	 * @since 2021/02/14
	 */
	int byteOrder();
}
