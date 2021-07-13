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
 * A memory handle that represents a chunk.
 *
 * @since 2021/01/10
 */
public class ChunkMemHandle
	extends MemHandle
{
	/**
	 * Initializes the base memory handle.
	 *
	 * @param __kind The {@link MemHandleKind}.
	 * @param __id The memory handle ID.
	 * @param __memActions The memory actions that are used.
	 * @param __bytes The number of bytes the handle consumes.
	 * @throws IllegalArgumentException If the memory handle does not have the
	 * correct security bits specified or if the byte size is negative.
	 * @since 2021/01/10
	 */
	ChunkMemHandle(int __kind, int __id, MemActions __memActions, int __bytes)
		throws IllegalArgumentException, NullPointerException
	{
		super(__kind, __id, __memActions, __bytes);
	}
	
	/**
	 * Reads the given value from the memory chunk.
	 * 
	 * @param <V> The class type to read.
	 * @param __cl The class type to read.
	 * @param __off The offset into the handle.
	 * @param __type The type of value to read.
	 * @return The read value.
	 * @since 2021/01/12
	 */
	protected <V> V read(Class<V> __cl, int __off, MemoryType __type)
		throws NullPointerException
	{
		if (__type == null)
			throw new NullPointerException("NARG");
		
		return this.memActions.<V>read(__cl, this, __type, __off);
	}
	
	/**
	 * Writes the given value of the given type at the given offset.
	 * 
	 * @param __off The offset of the type.
	 * @param __type The type of value to store.
	 * @param __v The value to store.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/12
	 */
	protected void write(int __off, MemoryType __type, Object __v)
		throws NullPointerException
	{
		if (__type == null || __v == null)
			throw new NullPointerException("NARG");
		
		this.memActions.write(this, __type, __off, __v);
	}
}
