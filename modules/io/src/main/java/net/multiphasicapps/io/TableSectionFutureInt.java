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
 * Future value storage with {@link TableSectionOutputStream}.
 *
 * @since 2020/11/29
 */
public final class TableSectionFutureInt
{
	/** The value to use. */
	private volatile int _value;
	
	/**
	 * Returns the stored value.
	 * 
	 * @return The value.
	 * @since 2020/11/29
	 */
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
