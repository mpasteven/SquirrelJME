// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.jarfile;

import cc.squirreljme.jvm.summercoat.constants.BootstrapConstants;

/**
 * Represents a memory handle.
 *
 * @since 2020/12/16
 */
public abstract class MemHandle
{
	/** The kind of handle this is. */
	protected final int kind;
	
	/** The memory handle ID. */
	public final int id;
	
	/** The number of bytes used. */
	protected final int byteSize;
	
	/** The memory actions used. */
	protected final MemActions memActions;
	
	/** The reference count of this handle. */
	int _refCount;
	
	/** Array size, if this is one. */
	int _arraySize =
		-1;
	
	/**
	 * Initializes the base memory handle.
	 * 
	 * @param __kind The kind of handle this is.
	 * @param __id The memory handle ID.
	 * @param __memActions The memory actions that are used.
	 * @param __bytes The number of bytes the handle consumes.
	 * @throws IllegalArgumentException If the memory handle does not have the
	 * correct security bits specified or if the byte size is negative.
	 * @since 2020/12/16
	 */
	MemHandle(int __kind, int __id, MemActions __memActions, int __bytes)
		throws IllegalArgumentException, NullPointerException
	{
		if (__memActions == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error BC01 Invalid memory handle security bit IDs.
		// (The handle)}
		if ((__id & BootstrapConstants.HANDLE_SECURITY_MASK) !=
			BootstrapConstants.HANDLE_SECURITY_BITS)
			throw new IllegalArgumentException(
				"BC01 0b" + Integer.toString(__id, 2));
		
		// {@squirreljme.error BC04 Cannot create memory handle with a negative
		// size. (The number of bytes used)}
		if (__bytes < 0)
			throw new IllegalArgumentException("BC04 " + __bytes);
		
		this.kind = __kind;
		this.id = __id;
		this.memActions = __memActions;
		this.byteSize = __bytes;
	}
}
