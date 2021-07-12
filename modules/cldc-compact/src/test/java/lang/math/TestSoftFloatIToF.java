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
 * Test soft floating point int to float.
 *
 * @since 2021/04/08
 */
public class TestSoftFloatIToF
	extends TestRunnable
{
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
	 *
	 * @since 2021/04/08
	 */
	@Override
	public void test()
	{
		Random rand = new Random(TestSoftFloatIToF._SEED);
		
		// Do massive conversions
		for (int i = 0; i < TestSoftFloatIToF._COUNT; i++)
		{
			int iVal = (i < TestSoftFloatIToF._SMALL ? i :
				(i < TestSoftFloatIToF._NEG_SMALL ?
					-(i - TestSoftFloatIToF._SMALL) :
					rand.nextInt()));
				
			float fVal = SoftInteger.toFloat(iVal);
			//float fVal = (float)iVal;
			
			int xVal = SoftFloat.toInteger(Float.floatToRawIntBits(fVal));
			//int xVal = (int)fVal;
			
			this.secondary(String.format("v%03dT%012d", i, iVal)
					.replace('-', 'n'),
				String.format("0x%08x.0x%08x",
					Float.floatToRawIntBits(fVal), xVal));
		}
	}
}
