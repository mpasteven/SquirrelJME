// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.runtime.cldc.util;

import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;

/**
 * Provides a list view of a {@code byte} array.
 *
 * @since 2020/07/11
 */
public class ByteArrayList
	extends AbstractList<Byte>
	implements RandomAccess
{
	/** The backing array. */
	protected final byte[] array;
	
	/** The offset. */
	protected final int offset;
	
	/** The cached size. */
	protected final int size;
	
	/**
	 * Initializes the long array view.
	 *
	 * @param __a The array to wrap.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/07/11
	 */
	public ByteArrayList(byte[] __a)
		throws NullPointerException
	{
		this(__a, 0, __a.length);
	}
	
	/**
	 * Initializes the long array view.
	 *
	 * @param __a The array to wrap.
	 * @param __o The offset.
	 * @param __l The length.
	 * @throws IndexOutOfBoundsException If the offset and/or length are
	 * negative or exceed the array bounds.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/07/11
	 */
	public ByteArrayList(byte[] __a, int __o, int __l)
		throws IndexOutOfBoundsException, NullPointerException
	{
		if (__a == null)
			throw new NullPointerException("NARG");
		if (__o < 0 || __l < 0 || (__o + __l) > __a.length)
			throw new IndexOutOfBoundsException("IOOB");
		
		this.array = __a;
		this.offset = __o;
		this.size = __l;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/11
	 */
	@Override
	public Byte get(int __i)
	{
		if (__i < 0 || __i >= this.size)
			throw new IndexOutOfBoundsException("IOOB");
		
		return this.array[this.offset + __i];
	}
	
	/**
	 * Sets the value of the given index,
	 *
	 * @param __i The index to set.
	 * @param __v The value to set.
	 * @return The old value.
	 * @throws IndexOutOfBoundsException If the index is not within bounds.
	 * @since 2020/07/11
	 */
	public byte set(int __i, byte __v)
		throws IndexOutOfBoundsException
	{
		return this.set(__i, Byte.valueOf(__v));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/11
	 */
	@Override
	public Byte set(int __i, Byte __v)
		throws IndexOutOfBoundsException, NullPointerException
	{
		if (__v == null)
			throw new NullPointerException("NARG");
		if (__i < 0 || __i >= this.size)
			throw new IndexOutOfBoundsException("IOOB");
		
		// The true index to access
		int trueDx = this.offset + __i;
		
		// Get old value
		byte[] array = this.array;
		byte rv = array[trueDx];
		
		// Set new value
		array[trueDx] = __v;
		
		// Return the old value
		return rv;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/11
	 */
	@Override
	public int size()
	{
		return this.size;
	}
	
	/**
	 * Returns the boxed list type of the given primitive array.
	 *
	 * @param __array The array to wrap.
	 * @return The boxed list type.
	 * @since 2020/07/11
	 */
	public static List<Byte> asList(byte... __array)
	{
		return new ByteArrayList(__array);
	}
}

