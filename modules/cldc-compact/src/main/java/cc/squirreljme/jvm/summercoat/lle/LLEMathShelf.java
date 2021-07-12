// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.summercoat.lle;

import cc.squirreljme.jvm.Assembly;
import cc.squirreljme.runtime.cldc.debug.Debugging;

/**
 * This contains various math functions.
 *
 * @since 2020/06/18
 */
public final class LLEMathShelf
{
	/**
	 * Not used.
	 *
	 * @since 2020/06/18
	 */
	private LLEMathShelf()
	{
	}
	
	/**
	 * Extracts the raw bits of the given value.
	 *
	 * @param __v The value to extract.
	 * @return The raw bits.
	 * @since 2020/06/18
	 */
	public static long rawDoubleToLong(double __v)
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Extracts the raw bits of the given value.
	 *
	 * @param __v The value to extract.
	 * @return The raw bits.
	 * @since 2020/06/18
	 */
	public static int rawFloatToInt(float __v)
	{
		return Assembly.floatToRawIntBits(__v);
	}
	
	/**
	 * Composes the value from the given bits
	 *
	 * @param __b The raw bits.
	 * @return The value.
	 * @since 2020/06/18
	 */
	public static float rawIntToFloat(int __b)
	{
		return Assembly.intBitsToFloat(__b);
	}
	
	/**
	 * Composes the value from the given bits
	 *
	 * @param __b The raw bits.
	 * @return The value.
	 * @since 2020/06/18
	 */
	public static double rawLongToDouble(long __b)
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
}
