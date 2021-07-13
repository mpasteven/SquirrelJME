// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.jarfile;

import java.util.NoSuchElementException;
import net.multiphasicapps.io.ChunkFuture;

/**
 * Represents a plain list value handle, which is the same as an integer
 * array but is not one.
 *
 * @since 2020/12/21
 */
public class ListValueHandle
	extends MemHandle
{
	/** The number of items in this list. */
	protected final int count;
	
	/** The base size of the list. */
	protected final int baseSize;
	
	/**
	 * Initializes the base list handle.
	 *
	 * @param __id The memory handle ID.
	 * @param __memActions The memory actions used.
	 * @param __baseSize The base size of the list.
	 * @param __count The size of the list.
	 * @throws IllegalArgumentException If the memory handle does not have the
	 * correct security bits specified or if the count is negative.
	 * @since 2020/12/21
	 */
	ListValueHandle(int __kind, int __id, MemActions __memActions,
		int __baseSize, int __count)
		throws IllegalArgumentException
	{
		super(__kind, __id, __memActions, __baseSize + (4 * __count));
		
		// {@squirreljme.error BC03 Negative list size. (The count}}
		if (__count < 0)
			throw new IllegalArgumentException("BC03 " + __count);
		
		this.baseSize = __baseSize;
		this.count = __count;
		
		// Store the length here
		this.memActions.write(this,
			MemoryType.INTEGER, this.__offset(-1), __count);
	}
	
	/**
	 * Returns the value at the given index.
	 * 
	 * @param __i The index to get.
	 * @return The value at the given index.
	 * @throws IllegalStateException If this is not of the given type.
	 * @throws IndexOutOfBoundsException If the index is not valid.
	 * @throws NoSuchElementException If no value was written.
	 * @since 2020/12/21
	 */
	public int getInteger(int __i)
		throws IllegalStateException, IndexOutOfBoundsException,
			NoSuchElementException
	{
		if (__i < 0 || __i >= this.count)
			throw new IndexOutOfBoundsException("IOOB");
		
		// The value stored may be null
		Integer rv = this.memActions.<Integer>read(Integer.class,
			this, MemoryType.INTEGER, this.__offset(__i));
		
		// {@squirreljme.error BC0e No value was stored in the list.}
		if (rv == null)
			throw new NoSuchElementException("BC0e");
		
		return rv;
	}
	
	/**
	 * Returns the memory handle at the given index.
	 * 
	 * @param __i The index to get.
	 * @return The value at the given index.
	 * @throws IllegalStateException If this is not of the given type.
	 * @throws IndexOutOfBoundsException If the index is not valid.
	 * @throws NoSuchElementException If no value was written.
	 * @since 2021/01/30
	 */
	public MemHandle getMemHandle(int __i)
		throws IllegalStateException, IndexOutOfBoundsException,
			NoSuchElementException
	{
		if (__i < 0 || __i >= this.count)
			throw new IndexOutOfBoundsException("IOOB");
		
		// The value stored may be null
		MemHandle rv = this.memActions.<MemHandle>read(MemHandle.class,
			this, MemoryType.INTEGER, this.__offset(__i));
		
		// {@squirreljme.error BC0t No value was stored in the list.}
		if (rv == null)
			throw new NoSuchElementException("BC0t");
		
		return rv;
	}
	
	/**
	 * Sets the entry at the given index.
	 * 
	 * @param __i The index to set.
	 * @param __iVal The handle to set it to.
	 * @throws IndexOutOfBoundsException If the index is out of bounds.
	 * @since 2020/12/19
	 */
	public void set(int __i, int __iVal)
		throws IndexOutOfBoundsException, NullPointerException
	{
		if (__i < 0 || __i >= this.count)
			throw new IndexOutOfBoundsException("IOOB " + __i);
		
		this.memActions.write(this,
			MemoryType.INTEGER, this.__offset(__i), __iVal);
	}
	
	/**
	 * Sets the entry at the given index.
	 * 
	 * @param __i The index to set.
	 * @param __handle The handle to set it to.
	 * @throws IndexOutOfBoundsException If the index is out of bounds.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/19
	 */
	public void set(int __i, MemHandle __handle)
		throws IndexOutOfBoundsException, NullPointerException
	{
		if (__handle == null)
			throw new NullPointerException("NARG");
		
		if (__i < 0 || __i >= this.count)
			throw new IndexOutOfBoundsException("IOOB " + __i);
		
		this.memActions.write(this,
			MemoryType.INTEGER, this.__offset(__i), __handle);
	}
	
	/**
	 * Sets the entry at the given index.
	 * 
	 * @param __i The index to set.
	 * @param __future The future to set it to.
	 * @throws IndexOutOfBoundsException If the index is out of bounds.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/10
	 */
	public void set(int __i, ChunkFuture __future)
		throws IndexOutOfBoundsException, NullPointerException
	{
		if (__future == null)
			throw new NullPointerException("NARG");
		
		if (__i < 0 || __i >= this.count)
			throw new IndexOutOfBoundsException("IOOB " + __i);
		
		this.memActions.write(this,
			MemoryType.INTEGER, this.__offset(__i), __future);
	}
	
	/**
	 * Sets the entry at the given index.
	 * 
	 * @param __i The index to set.
	 * @param __bjp The Boot Jar Pointer to set it to.
	 * @throws IndexOutOfBoundsException If the index is out of bounds.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/10
	 */
	public void set(int __i, HasBootJarPointer __bjp)
		throws IndexOutOfBoundsException, NullPointerException
	{
		if (__bjp == null)
			throw new NullPointerException("NARG");
		
		if (__i < 0 || __i >= this.count)
			throw new IndexOutOfBoundsException("IOOB " + __i);
		
		this.memActions.write(this,
			MemoryType.INTEGER, this.__offset(__i), __bjp);
	}
	
	/**
	 * Calculates the offset to the object.
	 * 
	 * @param __dx The index to get.
	 * @return The offset to the value.
	 * @since 2021/01/20
	 */
	private int __offset(int __dx)
	{
		return this.baseSize + (__dx * 4);
	}
}
