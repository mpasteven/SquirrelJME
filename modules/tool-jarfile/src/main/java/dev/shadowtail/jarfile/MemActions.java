// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.jarfile;

import cc.squirreljme.runtime.cldc.util.SortedTreeMap;
import java.util.Map;

/**
 * This class contains the state of memory actions, that is everything that
 * has modified or set memory specifically.
 * 
 * There are actions which act upon memory handles and set a specific value
 * or otherwise within them. This is to the bootloader is able to reconstruct
 * the pre-boot state and determine the best way to represent whatever is
 * written.
 *
 * @since 2020/12/22
 */
public final class MemActions
{
	/** Actions that are being performed. */
	private final Map<__MemHandleRef__, MemHandleActions> _actions =
		new SortedTreeMap<>();
	
	/**
	 * Returns the actions for the given handle.
	 * 
	 * @param __memHandle The handle to get.
	 * @return The actions for this single memory handle.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/10
	 */
	protected MemHandleActions get(MemHandle __memHandle)
		throws NullPointerException
	{
		if (__memHandle == null)
			throw new NullPointerException("NARG");
		
		Map<__MemHandleRef__, MemHandleActions> actions = this._actions;
		__MemHandleRef__ ref = new __MemHandleRef__(__memHandle);
		
		MemHandleActions rv = actions.get(ref);
		if (rv != null)
			return rv;
		
		actions.put(ref, (rv = new MemHandleActions(__memHandle.byteSize)));
		return rv;
	}
	/**
	 * Returns the actions for the given handle, if any.
	 * 
	 * @param __memHandle The handle to get.
	 * @return The actions for this single memory handle or {@code null} if
	 * none were created.
	 * @since 2021/01/13
	 */
	protected MemHandleActions optional(MemHandle __memHandle)
		throws NullPointerException
	{
		if (__memHandle == null)
			throw new NullPointerException("NARG");
		
		return this._actions.get(new __MemHandleRef__(__memHandle));
	}
	
	/**
	 * Reads from the given handle.
	 * 
	 * @param <V> The type to read.
	 * @param __cl The type to read.
	 * @param __memHandle The handle to read from.
	 * @param __type The type of value read.
	 * @param __off The offset of the value.
	 * @return The read value.
	 * @throws ClassCastException If the class is not the expected class.
	 * @throws IndexOutOfBoundsException If the offset is out of bounds.
	 * @since 2021/01/10
	 */
	protected <V> V read(Class<V> __cl, MemHandle __memHandle,
		MemoryType __type, int __off)
		throws ClassCastException, IndexOutOfBoundsException
	{
		if (__memHandle == null || __type == null || __cl == null)
			throw new NullPointerException("NARG");
		if (__off < 0 || __off >= __memHandle.byteSize)
			throw new IndexOutOfBoundsException("NARG");
		
		return this.get(__memHandle).read(__cl, __type, __off);
	}
	
	/**
	 * Writes the given value into the given offset of a memory handle.
	 * 
	 * @param __memHandle The memory handle to store in.
	 * @param __type The type of value rea.d
	 * @param __off The offset to place.
	 * @param __oVal The value to store.
	 * @throws IndexOutOfBoundsException If the offset is out of bounds.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/10
	 */
	public void write(MemHandle __memHandle, MemoryType __type, int __off,
		Object __oVal)
		throws IndexOutOfBoundsException, NullPointerException
	{
		if (__memHandle == null || __type == null || __oVal == null)
			throw new NullPointerException("NARG");
		
		this.get(__memHandle).write(__type, __off, __oVal);
	}
}
