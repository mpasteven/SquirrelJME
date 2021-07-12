// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.summercoat;

import cc.squirreljme.emulator.vm.VMException;
import cc.squirreljme.jvm.summercoat.SummerCoatUtil;
import cc.squirreljme.jvm.summercoat.constants.MemHandleKind;
import cc.squirreljme.jvm.summercoat.ld.mem.MemHandleReference;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.vm.summercoat.handles.MemHandleArray;
import cc.squirreljme.vm.summercoat.handles.MemHandleArrayBoolean;
import cc.squirreljme.vm.summercoat.handles.MemHandleArrayByte;
import cc.squirreljme.vm.summercoat.handles.MemHandleArrayCharacter;
import cc.squirreljme.vm.summercoat.handles.MemHandleArrayDouble;
import cc.squirreljme.vm.summercoat.handles.MemHandleArrayFloat;
import cc.squirreljme.vm.summercoat.handles.MemHandleArrayInteger;
import cc.squirreljme.vm.summercoat.handles.MemHandleArrayLong;
import cc.squirreljme.vm.summercoat.handles.MemHandleArrayMemHandle;
import cc.squirreljme.vm.summercoat.handles.MemHandleArrayShort;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Manages memory handles.
 *
 * @since 2020/11/28
 */
public final class MemHandleManager
{
	/** Reserved handle, not used. */
	private static final MemHandle _RESERVED =
		new MemHandle(0, 1, 1, 1);
	
	/** Memory handles that exist. */
	private final Map<Integer, MemHandle> _handles =
		new ConcurrentSkipListMap<>();
	
	/** The handle ID generator. */
	private final Random _random =
		new Random(0x12345678);
	
	/**
	 * Allocates the given handle.
	 * 
	 * @param __kind The kind of memory handle to allocate.
	 * @param __byteSize The number of bytes to allocate.
	 * @return The allocated handle.
	 * @throws IllegalArgumentException If the kind is not valid or the
	 * requested size is negative.
	 * @since 2021/01/17
	 */
	public MemHandle alloc(int __kind, int __byteSize)
		throws IllegalArgumentException
	{
		if (__kind <= 0 || __kind >= MemHandleKind.NUM_KINDS)
			throw new IllegalArgumentException("Invalid kind: " + __kind);
		if (__byteSize < 0)
			throw new IllegalArgumentException("Negative allocation size.");
		
		// Reserve an ID
		int id = this.__reserve();
		
		// Create handle with this ID
		MemHandle rv = new MemHandle(id, __kind, __byteSize, __byteSize);
		
		// Store it and use it
		this._handles.put(id, rv);
		return rv;
	}
	
	/**
	 * Allocates the given handle.
	 * 
	 * @param __kind The kind of memory handle to allocate.
	 * @param __base The base array size.
	 * @param __len The array length.
	 * @return The allocated handle.
	 * @throws IllegalArgumentException If the kind is not valid or the
	 * requested size is negative.
	 * @since 2021/01/17
	 */
	public MemHandleArray allocArray(int __kind, int __base, int __len)
		throws IllegalArgumentException
	{
		if (!SummerCoatUtil.isArrayKind(__kind))
			throw new IllegalArgumentException("Not an array: " + __kind);
		if (__len < 0)
			throw new IllegalArgumentException("Negative allocation size.");
		
		// Determine storage for the array
		Object storage;
		switch (__kind)
		{
			case MemHandleKind.BOOLEAN_ARRAY:
				storage = new boolean[__len];
				break;
				
			case MemHandleKind.BYTE_ARRAY:
				storage = new byte[__len];
				break;
				
			case MemHandleKind.SHORT_ARRAY:
				storage = new short[__len];
				break;
				
			case MemHandleKind.CHARACTER_ARRAY:
				storage = new char[__len];
				break;
				
			case MemHandleKind.INTEGER_ARRAY:
				storage = new int[__len];
				break;
				
			case MemHandleKind.LONG_ARRAY:
				storage = new long[__len];
				break;
				
			case MemHandleKind.FLOAT_ARRAY:
				storage = new float[__len];
				break;
				
			case MemHandleKind.DOUBLE_ARRAY:
				storage = new double[__len];
				break;
				
			case MemHandleKind.OBJECT_ARRAY:
				storage = new MemHandle[__len];
				break;
			
			default:
				throw Debugging.oops(__kind);
		}
		
		// Wrap the given array
		return this.wrapArray(__base, storage);
	}
	
	/**
	 * Returns the given memory handle.
	 * 
	 * @param __id The identifier of the handle.
	 * @return The memory handle for the given item.
	 * @throws InvalidMemoryHandleException If the memory handle does not
	 * exist.
	 * @since 2021/05/08
	 */
	public final MemHandle get(MemHandleReference __id)
		throws InvalidMemoryHandleException
	{
		return this.get((__id == null ? 0 : __id.id));
	}
	
	/**
	 * Returns the given memory handle.
	 * 
	 * @param __id The identifier of the handle.
	 * @return The memory handle for the given item.
	 * @throws InvalidMemoryHandleException If the memory handle does not
	 * exist.
	 * @since 2020/11/28
	 */
	public final MemHandle get(int __id)
		throws InvalidMemoryHandleException
	{
		MemHandle rv = this._handles.get(__id);
		if (rv == null)
			throw new InvalidMemoryHandleException(__id,
				String.format("No handle for 0x%x", __id));
		
		return rv;
	}
	
	/**
	 * Wraps the given array into a handle.
	 * 
	 * @param __base The base array size.
	 * @param __array The array to wrap.
	 * @return The allocated handle.
	 * @throws IllegalArgumentException If the array base is negative or this
	 * is not an array type.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/17
	 */
	public MemHandleArray wrapArray(int __base, Object __array)
		throws IllegalArgumentException
	{
		if (__array == null)
			throw new NullPointerException("NARG");
		
		// Reserve an ID that identifies this handle
		int id = this.__reserve();
		
		// Create wrapped class for the handle
		MemHandleArray rv;
		if (__array instanceof boolean[])
			rv = new MemHandleArrayBoolean(id, __base, (boolean[])__array);
		else if (__array instanceof byte[])
			rv = new MemHandleArrayByte(id, __base, (byte[])__array);
		else if (__array instanceof short[])
			rv = new MemHandleArrayShort(id, __base, (short[])__array);
		else if (__array instanceof char[])
			rv = new MemHandleArrayCharacter(id, __base, (char[])__array);
		else if (__array instanceof int[])
			rv = new MemHandleArrayInteger(id, __base, (int[])__array);
		else if (__array instanceof long[])
			rv = new MemHandleArrayLong(id, __base, (long[])__array);
		else if (__array instanceof float[])
			rv = new MemHandleArrayFloat(id, __base, (float[])__array);
		else if (__array instanceof double[])
			rv = new MemHandleArrayDouble(id, __base, (double[])__array);
		else if (__array instanceof MemHandle[])
			rv = new MemHandleArrayMemHandle(id, __base,
				this, (MemHandle[])__array);
		
		// Do not know how to wrap this
		else
			throw Debugging.oops(__array.getClass());
			
		// Store it and use it
		this._handles.put(id, rv);
		return rv;
	}
	
	/**
	 * Reserves an ID and returns it, this may possibly reduce handle
	 * contention along with allowing allocations to happen across multiple
	 * threads.
	 * 
	 * @return The reserved ID.
	 * @since 2021/01/17
	 */
	private int __reserve()
	{
		Map<Integer, MemHandle> handles = this._handles;
		Random random = this._random;
		
		synchronized (this)
		{
			// Find a unique ID number
			int id;
			do
			{
				id = random.nextInt() & 0x3D_7FFFFF;
			} while (handles.containsKey(id));
			
			// Reserve it for future usage
			handles.put(id, MemHandleManager._RESERVED);
			return id;
		}
	}
}
