// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.runtime.cldc.util;

import cc.squirreljme.runtime.cldc.debug.Debugging;
import java.util.ListIterator;

/**
 * A list iterator which wraps a {@code ListIterator<Character>} and then
 * provides access to it.
 *
 * @since 2021/02/25
 */
public class __CharacterIntegerListIterator__
	implements ListIterator<Integer>
{
	/** The iterator to base off. */
	private final ListIterator<Character> iterator;
	
	/**
	 * Creates a wrapped iterator.
	 * 
	 * @param __iterator The iterator to use.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/02/25
	 */
	public __CharacterIntegerListIterator__(
		ListIterator<Character> __iterator)
		throws NullPointerException
	{
		if (__iterator == null)
			throw new NullPointerException("NARG");
		
		this.iterator = __iterator;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/25
	 */
	@Override
	public void add(Integer __v)
	{
		this.iterator.add((__v == null ? null : (char)((int)__v)));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/25
	 */
	@Override
	public boolean hasNext()
	{
		return this.iterator.hasNext();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/25
	 */
	@Override
	public boolean hasPrevious()
	{
		return this.iterator.hasPrevious();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/25
	 */
	@Override
	public Integer next()
	{
		Character rv = this.iterator.next();
		return (rv == null ? null : (int)rv);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/25
	 */
	@Override
	public int nextIndex()
	{
		return this.iterator.nextIndex();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/25
	 */
	@Override
	public Integer previous()
	{
		Character rv = this.iterator.previous();
		return (rv == null ? null : (int)rv);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/25
	 */
	@Override
	public int previousIndex()
	{
		return this.iterator.previousIndex();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/25
	 */
	@Override
	public void remove()
	{
		this.iterator.remove();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/25
	 */
	@Override
	public void set(Integer __v)
	{
		this.iterator.set((__v == null ? null : (char)((int)__v)));
	}
}
