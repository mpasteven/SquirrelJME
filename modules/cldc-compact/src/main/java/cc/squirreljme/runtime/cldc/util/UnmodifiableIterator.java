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
 * This creates an unmodifiable iterator over an existing iterator.
 *
 * @param <T> The type to use.
 * @since 2018/05/13
 */
public final class UnmodifiableIterator<T>
	implements Iterator<T>
{
	/** The source iterator. */
	protected final Iterator<T> source;
	
	/**
	 * Initializes the iterator.
	 *
	 * @param __it The source iterator.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/05/13
	 */
	UnmodifiableIterator(Iterator<T> __it)
		throws NullPointerException
	{
		if (__it == null)
			throw new NullPointerException("NARG");
		
		this.source = __it;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2108/05/13
	 */
	@Override
	public final boolean hasNext()
	{
		return this.source.hasNext();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2108/05/13
	 */
	@Override
	public final T next()
		throws NoSuchElementException
	{
		return this.source.next();
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
	 * Wraps the given iterable.
	 *
	 * @param <T> The iterator type.
	 * @param __i The iterable to wrap.
	 * @return The wrapped iterator.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/05/13
	 */
	public static <T> Iterator<T> of(Iterable<T> __i)
		throws NullPointerException
	{
		if (__i == null)
			throw new NullPointerException("NARG");
		
		return new UnmodifiableIterator<T>(__i.iterator());
	}
	
	/**
	 * Wraps the given iterator.
	 *
	 * @param <T> The iterator type.
	 * @param __i The iterator to wrap.
	 * @return The wrapped iterator.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/05/13
	 */
	public static <T> Iterator<T> of(Iterator<T> __i)
		throws NullPointerException
	{
		if (__i == null)
			throw new NullPointerException("NARG");
		
		return new UnmodifiableIterator<T>(__i);
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

