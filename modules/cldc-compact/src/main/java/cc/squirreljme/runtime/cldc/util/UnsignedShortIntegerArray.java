// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.runtime.cldc.util;

/**
 * Wraps a short array to provide unsigned integer access to it.
 *
 * @see ShortIntegerArray
 * @since 2021/07/12
 */
public final class UnsignedShortIntegerArray
	implements IntegerArray
{
	/** The backed array. */
	protected final short[] array;
	
	/**
	 * Initializes the array wrapper.
	 *
	 * @param __a The array to wrap.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/07/12
	 */
	public UnsignedShortIntegerArray(short[] __a)
		throws NullPointerException
	{
		if (__a == null)
			throw new NullPointerException("NARG");
		
		this.array = __a;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/07/12
	 */
	@SuppressWarnings("MagicNumber")
	@Override
	public final int get(int __i)
	{
		return this.array[__i] & 0xFFFF;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/07/12
	 */
	@Override
	public final void set(int __i, int __v)
	{
		this.array[__i] = (short)__v;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/07/12
	 */
	@Override
	public final int size()
	{
		return this.array.length;
	}
}

