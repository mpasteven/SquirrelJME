// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.collections;

import java.util.ArrayList;

/**
 * This is based on top of {@link ArrayList} and is the same interface except
 * that it is {@link AutoCloseable} and on close will attempt to close all
 * elements within the list. This throws {@link RuntimeException} if closing
 * failed.
 *
 * @since 2021/06/19
 */
public class AutoCloseableList<T extends AutoCloseable>
	extends ArrayList<T>
	implements AutoCloseable
{
	/**
	 * {@inheritDoc}
	 * @since 2021/06/19
	 */
	@Override
	public void close()
	{
		int count = 0;
		Exception defer = null;
		for (int i = 0, n = this.size(); i < n; i++)
			try
			{
				T v = this.get(i);
				if (v != null)
					v.close();
			}
			catch (Exception e)
			{
				if (defer == null)
					defer = e;
				count++;
			}
		
		// {@squirreljme.error AC06 There was at least one exception which
		// occurred while closing the list. (The number of exceptions thrown)}
		if (defer != null)
			throw new RuntimeException(String.format("AC06 %d", count), defer);
	}
	
	/**
	 * Adds the specified item to the list and returns it.
	 *
	 * @param __t The element to add.
	 * @return {@code __t}.
	 * @since 2021/06/19
	 */
	public final T addThis(T __t)
	{
		this.add(__t);
		return __t;
	}
	
	/**
	 * Adds the specified item to the list and returns it.
	 *
	 * @param <E> The class to cast to.
	 * @param __t The element to add.
	 * @param __cl The class to cast to on return.
	 * @return {@code __t} cast to {@code E}.
	 * @throws NullPointerException If no class was specified.
	 * @since 2021/06/19
	 */
	public final <E extends T> E addThis(E __t, Class<E> __cl)
		throws NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException("NARG");
		
		this.add(__t);
		return __cl.cast(__t);
	}
}

