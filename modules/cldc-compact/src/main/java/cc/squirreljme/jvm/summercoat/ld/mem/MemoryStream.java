// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.summercoat.ld.mem;

/**
 * A stream of memory access, used to calculate the address and otherwise.
 *
 * @since 2021/02/14
 */
public interface MemoryStream
{
	/**
	 * Returns the address the stream is located at.
	 * 
	 * @return The address the stream is located at.
	 * @since 2021/02/14
	 */
	long address();
	
	/**
	 * Returns the address the stream is located at relative to the start of
	 * the stream.
	 * 
	 * @return The relative stream offset.
	 * @since 2021/02/14
	 */
	int offset();
}
