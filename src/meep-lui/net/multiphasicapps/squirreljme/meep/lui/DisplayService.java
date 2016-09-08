// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) 2013-2016 Steven Gawroriski <steven@multiphasicapps.net>
//     Copyright (C) 2013-2016 Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// For more information see license.mkd.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirreljme.meep.lui;

/**
 * This is a service which implements the actual support for displays. This
 * base class is intended to make implementing LUI displays much easier by
 * wrapping a line based format with unspecified attributes.
 *
 * @since 2016/09/07
 */
public abstract class DisplayService
{
	/** The current display cells. */
	private volatile DisplayCells _cells;
	
	/**
	 * Returns the current set of display cells.
	 *
	 * @return The display cells.
	 * @since 2016/09/08
	 */
	public final DisplayCells displayCells()
	{
		return this._cells;
	}
	
	/**
	 * Sets the size of the output display.
	 *
	 * @param __c The number of columns.
	 * @param __r The number of rows.
	 * @throws IndexOutOfBoundsException If either is zero or negative.
	 * @since 2016/09/08
	 */
	protected final void setDisplaySize(int __c, int __r)
		throws IndexOutOfBoundsException
	{
		this._cells = new DisplayCells(__c, __r);
	}
}

