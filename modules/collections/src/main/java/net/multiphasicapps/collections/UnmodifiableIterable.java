// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.collections;

import cc.squirreljme.runtime.cldc.util.UnmodifiableIterator;
import java.util.Iterator;

/**
 * Wraps an iterable which makes it not able to be modified.
 *
 * @param <T> The type to iterate through.
 * @since 2021/04/25
 */
public final class UnmodifiableIterable<T>
	implements Iterable<T>
{
	/** The iterable used. */
	private final Iterable<T> iterable;
	
	/**
	 * Initializes the unmodifiable iterable.
	 * 
	 * @param __it The iterable used.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/04/25
	 */
	UnmodifiableIterable(Iterable<T> __it)
		throws NullPointerException
	{
		if (__it == null)
			throw new NullPointerException("NARG");
		
		this.iterable = __it;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/25
	 */
	@Override
	public Iterator<T> iterator()
	{
		return UnmodifiableIterator.<T>of(this.iterable);
	}
	
	/**
	 * Returns an unmodifiable iterable from the given one.
	 * 
	 * @param <T> The type used.
	 * @param __it The iterator to wrap.
	 * @return The iterable but it cannot be modified.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/04/25
	 */
	public static <T> Iterable<T> of(Iterable<T> __it)
		throws NullPointerException
	{
		if (__it == null)
			throw new NullPointerException("NARG");
		
		return new UnmodifiableIterable<>(__it);
	}
}
