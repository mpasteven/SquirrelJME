// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.runtime.cldc.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This is an iterator where elements cannot be removed and where it iterates
 * over an array.
 *
 * @param <T> The type to use.
 * @since 2018/05/13
 */
public final class UnmodifiableArrayIterator<T>
	implements Iterator<T>
{
	/** The element limit. */
	protected final int limit;
	
	/** The source elements, cleared when empty. */
	private T[] _source;
	
	/** The current element. */
	private int _at;
	
	/**
	 * Initializes the iterator.
	 *
	 * @param __a The input array.
	 * @param __o The offset into the array.
	 * @param __l The number of elements to read.
	 * @throws ArrayIndexOutOfBoundsException If the offset and/or length are
	 * negative or exceed the array bounds.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/05/13
	 */
	UnmodifiableArrayIterator(T[] __a, int __o, int __l)
		throws ArrayIndexOutOfBoundsException, NullPointerException
	{
		if (__a == null)
			throw new NullPointerException("NARG");
		if (__o < 0 || __l < 0 || (__o + __l) > __a.length)
			throw new ArrayIndexOutOfBoundsException("IOOB");
		
		this.limit = __o + __l;
		this._source = __a;
		this._at = __o;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2108/05/13
	 */
	@Override
	public final boolean hasNext()
	{	
		return (this._at < this.limit);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2108/05/13
	 */
	@Override
	public final T next()
		throws NoSuchElementException
	{
		// Is at the end?
		int at = this._at;
		if (at >= this.limit)
		{
			this._source = null;
			throw new NoSuchElementException("NSEE");
		}
		
		this._at = at + 1;
		return this._source[at];
	}
	
	/**
	 * {@inheritDoc}
	 * @throws UnsupportedOperationException Always.
	 * @since 2108/05/13
	 */
	@Override
	public final void remove()
		throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException("RORO");
	}
	
	/**
	 * Wraps the given array.
	 *
	 * @param <T> The iterator type.
	 * @param __a The array to wrap.
	 * @return The iterator for the array.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/05/13
	 */
	@SuppressWarnings({"unchecked"})
	public static <T> Iterator<T> of(T... __a)
		throws NullPointerException
	{
		if (__a == null)
			throw new NullPointerException("NARG");
		
		return new UnmodifiableArrayIterator<T>(__a, 0, __a.length);
	}
	
	/**
	 * Wraps the given array.
	 *
	 * @param <T> The iterator type.
	 * @param __a The input array.
	 * @param __o The offset into the array.
	 * @param __l The number of elements to read.
	 * @throws ArrayIndexOutOfBoundsException If the offset and/or length are
	 * negative or exceed the array bounds.
	 * @return The iterator for the array.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/05/13
	 */
	public static <T> Iterator<T> of(T[] __a, int __o, int __l)
		throws ArrayIndexOutOfBoundsException, NullPointerException
	{
		if (__a == null)
			throw new NullPointerException("NARG");
		if (__o < 0 || __l < 0 || (__o + __l) > __a.length)
			throw new ArrayIndexOutOfBoundsException("IOOB");
		
		return new UnmodifiableArrayIterator<T>(__a, __o, __l);
	}
}

