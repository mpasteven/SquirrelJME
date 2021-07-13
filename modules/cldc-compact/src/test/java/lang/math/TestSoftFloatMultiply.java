// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package lang.math;

import cc.squirreljme.jvm.SoftFloat;
import cc.squirreljme.jvm.SoftInteger;
import java.util.Random;
import net.multiphasicapps.tac.TestRunnable;

/**
 * Tests {@code float} multiplication using software math.
 *
 * @since 2021/04/10
 */
public class TestSoftFloatMultiply
	extends TestRunnable
{
	/** Name of the fixed values. */
	private static final String[] _FIX_NAME =
		new String[]{"min", "max", "nrm",
			"nif", "pif", "nan",
			"zer", "zen",
			"one",
			"neg"};
	
	/** Fixed values to multiply. */
	private static final float[] _FIX_VAL =
		new float[]{Float.MIN_VALUE, Float.MAX_VALUE, Float.MIN_NORMAL,
			Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NaN,
			Float.intBitsToFloat(0), Float.intBitsToFloat(Integer.MIN_VALUE),
			Float.intBitsToFloat(1065353216),
			Float.intBitsToFloat(-1082130432)};
	
	/** The count. */
	private static final int _COUNT =
		384;
	
	/** Small count. */
	private static final int _SMALL =
		64;
	
	/** Negative small count. */
	private static final int _NEG_SMALL =
		128;
	
	/** The seed used. */
	private static final long _SEED =
		0xCAFEBABE_DEADBEEFL;
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/10
	 */
	@Override
	public void test()
		throws Throwable
	{
		// Calculate the fixed values first
		for (int a = 0, n = TestSoftFloatMultiply._FIX_VAL.length; a < n; a++)
			for (int b = 0; b < n; b++)
				this.secondary(String.format("fx%s%s",
						TestSoftFloatMultiply._FIX_NAME[a],
						TestSoftFloatMultiply._FIX_NAME[b]),
					TestSoftFloatMultiply.format(TestSoftFloatMultiply.mul(
						TestSoftFloatMultiply._FIX_VAL[a],
						TestSoftFloatMultiply._FIX_VAL[b])));
		
		// Generate random seed for randomized values
		Random rand = new Random(TestSoftFloatMultiply._SEED);
		
		// Work with many values
		for (int i = 0; i < TestSoftFloatMultiply._COUNT; i++)
		{
			// Get two values to multiply together
			int ia = (i < TestSoftFloatMultiply._SMALL ? i :
				(i < TestSoftFloatMultiply._NEG_SMALL ?
					-(i - TestSoftFloatMultiply._SMALL) :
					rand.nextInt()));
			int ib = (i < TestSoftFloatMultiply._SMALL ?
					i + TestSoftFloatMultiply._SMALL :
				(i < TestSoftFloatMultiply._NEG_SMALL ? -i :
					rand.nextInt())) * (((i >> 2) & 1) == 0 ? -1 : 1);
				
			// Convert to float
			float fa = SoftInteger.toFloat(ia);
			float fb = SoftInteger.toFloat(ib);
			
			// Perform the math
			float fc = TestSoftFloatMultiply.mul(fa, fb);
			
			// Store the result
			this.secondary(String.format("v%03dT%012dq%012d", i, ia, ib)
					.replace('-', 'n'),
				TestSoftFloatMultiply.format(fc));
		}
	}
	
	/**
	 * Formats the floating point value.
	 * 
	 * @param __v The value to format.
	 * @return The formatted value.
	 * @since 2021/04/10
	 */
	private static String format(float __v)
	{
		return String.format("0x%08x", Float.floatToRawIntBits(__v));
	}
	
	/**
	 * Multiplies A and B using software.
	 * 
	 * @param __a A
	 * @param __b B.
	 * @return The result.
	 * @since 2021/04/10
	 */
	private static float mul(float __a, float __b)
	{
		return SoftFloat.mul(Float.floatToRawIntBits(__a),
			Float.floatToRawIntBits(__b));
	}
}
