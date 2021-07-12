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
 * Contains the point for futures.
 *
 * @since 2020/12/05
 */
final class __FuturePoint__
{
	/** The future address. */
	protected final int address;
	
	/** The data type. */
	protected final ChunkDataType type;
	
	/** The value type. */
	protected final ChunkFuture value;
	
	/**
	 * Initializes the future point.
	 * 
	 * @param __addr The address to write at.
	 * @param __dt The chunk data type.
	 * @param __val The value to be written.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/05
	 */
	public __FuturePoint__(int __addr, ChunkDataType __dt, ChunkFuture __val)
		throws NullPointerException
	{
		if (__dt == null || __val == null)
			throw new NullPointerException("NARG");
		
		this.address = __addr;
		this.type = __dt;
		this.value = __val;
	}
}
