// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package squirreljme.compilerbug;

/**
 * Represents a single link in the linked list. This is just a basic
 * structure like object with public fields for simple access.
 *
 * @since 2018/10/29
 */
final class __Link__
{
	/** The previous link. */
	__Link__ _prev;
	
	/** The next link. */
	__Link__ _next;
	
	/** The value to store. */
	Object _value;
	
	/**
	 * Initializes the new link and links into the chain.
	 *
	 * @param __prev The previous link to link in.
	 * @param __v The value to use.
	 * @param __next The next link to link in.
	 * @since 2018/10/29
	 */
	__Link__(__Link__ __prev, Object __v, __Link__ __next)
	{
		// Set value first
		this._value = __v;
		
		// Link into previous chain
		this._prev = __prev;
		if (__prev != null)
			__prev._next = this;
		
		// Link into next chain
		this._next = __next;
		if (__next != null)
			__next._prev = this;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/06/16
	 */
	@Override
	public int hashCode()
	{
		int rv = 0;
		
		if (this._prev != null)
			rv |= 1;
		if (this._next != null)
			rv |= 2;
		if (this._value != null)
			rv |= 4;
		
		return rv;
	}
}
