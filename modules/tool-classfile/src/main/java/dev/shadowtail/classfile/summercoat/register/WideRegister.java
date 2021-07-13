// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.summercoat.register;

import dev.shadowtail.classfile.nncc.NativeCode;

/**
 * Represents a wide register combination.
 *
 * @since 2020/11/28
 */
public final class WideRegister
	extends Register
{
	/** Wide return value. */
	public static final WideRegister WIDE_RETURN =
		new WideRegister(IntValueRegister.of(NativeCode.RETURN_REGISTER),
			IntValueRegister.of(NativeCode.RETURN_TWO_REGISTER));
	
	/** The high value. */
	public final IntValueRegister high;
	
	/** The low value. */
	public final IntValueRegister low;
	
	/**
	 * Initializes the wide register.
	 * 
	 * @param __lo The low register.
	 * @param __hi The high register.
	 * @throws IllegalArgumentException If the two registers are not adjacent.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/28
	 */
	public WideRegister(IntValueRegister __lo, IntValueRegister __hi)
		throws IllegalArgumentException, NullPointerException
	{
		super(__lo.register);
		
		if (__hi == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error JC4m High and low register are not adjacent.
		// (The low register; The high register)}
		if (__lo.register + 1 != __hi.register)
			throw new IllegalArgumentException(String.format("JC4m %s %s",
				__lo, __hi));
			
		this.high = __hi;
		this.low = __lo;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/11/28
	 */
	@Override
	public boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (!(__o instanceof WideRegister))
			return false;
		
		WideRegister o = (WideRegister)__o;
		return this.high.equals(o.high) &&
			this.low.equals(o.low);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/11/28
	 */
	@Override
	public int hashCode()
	{
		return this.high.hashCode() ^ this.low.hashCode();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/11/28
	 */
	@Override
	public String toString()
	{
		return String.format("LoHi[%s+%s]", this.low, this.high);
	}
	
	/**
	 * Creates a wide register from two registers.
	 * 
	 * @param __lo The low register.
	 * @param __hi The high register.
	 * @return The wide register.
	 * @since 2020/11/28
	 */
	public static WideRegister of(int __lo, int __hi)
	{
		return new WideRegister(IntValueRegister.of(__lo),
			IntValueRegister.of(__hi));
	}
}
