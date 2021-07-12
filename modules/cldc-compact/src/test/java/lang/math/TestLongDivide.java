// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package lang.math;

import java.util.Random;
import net.multiphasicapps.tac.TestRunnable;

/**
 * Tests that division with {@code long} is working since that uses
 * software math for SummerCoat.
 *
 * @since 2021/02/20
 */
public class TestLongDivide
	extends TestRunnable
{
	/** Counts. */
	public static final int COUNT =
		128;
	
	/** Half count. */
	public static final int HALF_COUNT =
		TestLongDivide.COUNT / 2;
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/20
	 */
	@Override
	public void test()
		throws Throwable
	{
		Random random = new Random(0xCAFEBABE_DEADBEEFL);
		
		// Quotient and remainder results
		long[] quo = new long[TestLongDivide.COUNT];
		long[] rem = new long[TestLongDivide.COUNT];
		
		// Constant division!
		for (int i = 0; i < TestLongDivide.COUNT; i++)
		{
			long num = random.nextLong();
			
			// Do not get a zero value because we test division by zero
			// elsewhere...
			long den = 0L;
			while (den == 0L)
				den = (i < TestLongDivide.HALF_COUNT ?
					1 + random.nextInt(1 + i) : random.nextLong());
			
			quo[i] = num / den;
			rem[i] = num % den;
		}
		
		// Store record
		this.secondary("quo", quo);
		this.secondary("rem", rem);
	}
}
