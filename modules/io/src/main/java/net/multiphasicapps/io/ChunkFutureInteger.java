// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.io;

/**
 * Future value storage with {@link ChunkWriter}.
 *
 * @since 2020/11/29
 */
public final class ChunkFutureInteger
	implements ChunkFuture
{
	/** The value to use. */
	private volatile int _value;
	
	/**
	 * Initializes the future to zero.
	 * 
	 * @since 2020/12/06
	 */
	public ChunkFutureInteger()
	{
	}
	
	/**
	 * Initializes the future to the given value.
	 * 
	 * @param __value The value.
	 * @since 2020/12/06
	 */
	public ChunkFutureInteger(int __value)
	{
		this._value = __value;
	}
	
	/**
	 * Returns the stored value.
	 * 
	 * @return The value.
	 * @since 2020/11/29
	 */
	@Override
	public final int get()
	{
		synchronized (this)
		{
			return this._value;
		} 
	}
	
	/**
	 * Sets the stored value.
	 * 
	 * @param __v The value to set.
	 * @since 2020/11/29
	 */
	public final void set(int __v)
	{
		synchronized (this)
		{
			this._value = __v;
		} 
	}
}
