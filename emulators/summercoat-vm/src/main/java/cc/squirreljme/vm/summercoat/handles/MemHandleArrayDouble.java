// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.summercoat.handles;

import cc.squirreljme.jvm.summercoat.constants.MemHandleKind;
import cc.squirreljme.runtime.cldc.debug.Debugging;

/**
 * Double array.
 *
 * @since 2021/01/17
 */
public class MemHandleArrayDouble
	extends MemHandleArray
{
	/** The array values. */
	protected final double[] values;
	
	/**
	 * Initializes a new handle.
	 *
	 * @param __id The identifier for this handle.
	 * @param __base The base array size.
	 * @param __array The array used.
	 * @throws IllegalArgumentException If the kind is not valid or the
	 * requested size is negative.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/17
	 */
	public MemHandleArrayDouble(int __id, int __base, double... __array)
		throws IllegalArgumentException, NullPointerException
	{
		super(__id, MemHandleKind.DOUBLE_ARRAY,
			__base, 8, __array.length);
		
		this.values = __array;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/14
	 */
	@Override
	public long memReadLong(long __addr)
	{
		if (super.checkBase(__addr))
			return super.memReadLong(__addr);
		
		return Double.doubleToRawLongBits(this.values[super.calcCell(__addr)]);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/01/17
	 */
	@Override
	public void memWriteLong(long __addr, long __v)
	{
		if (super.checkBase(__addr))
			super.memWriteLong(__addr, __v);
		else
			this.values[super.calcCell(__addr)] = Double.longBitsToDouble(__v);
	}
}
