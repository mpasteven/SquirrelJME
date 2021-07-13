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
 * Wraps a character list as an integer list.
 *
 * @since 2021/02/25
 */
final class __CharacterIntegerListRandom__
	extends AbstractList<Integer>
	implements RandomAccess
{
	/** The backing character array. */
	protected final List<Character> chars;
	
	/**
	 * Initializes the list wrapper.
	 * 
	 * @param __chars The list to wrap.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/02/25
	 */
	__CharacterIntegerListRandom__(List<Character> __chars)
		throws NullPointerException
	{
		if (__chars == null)
			throw new NullPointerException("NARG");
		
		this.chars = __chars;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/25
	 */
	@Override
	public void add(int __dx, Integer __v)
	{
		this.chars.add(__dx, (__v == null ? null : (char)((int)__v)));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/25
	 */
	@Override
	public Integer get(int __dx)
	{
		Character rv = this.chars.get(__dx);
		return (rv == null ? null : (int)rv);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/25
	 */
	@Override
	public Integer remove(int __dx)
	{
		Character rv = this.chars.remove(__dx);
		return (rv == null ? null : (int)rv);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/25
	 */
	@Override
	public Integer set(int __dx, Integer __v)
	{
		Character rv = this.chars.set(__dx,
			(__v == null ? null : (char)((int)__v)));
		return (rv == null ? null : (int)rv);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/25
	 */
	@Override
	public int size()
	{
		return this.chars.size();
	}
}
