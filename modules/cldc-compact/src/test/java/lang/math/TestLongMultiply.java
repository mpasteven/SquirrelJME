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
 * Tests long multiplication.
 *
 * @since 2021/04/05
 */
public class TestLongMultiply
	extends TestRunnable
{
	/** Counts. */
	public static final int COUNT =
		128;
	
	/** Half count. */
	public static final int HALF_COUNT =
		TestLongMultiply.COUNT / 2;
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/20
	 */
	@SuppressWarnings({"CheckForOutOfMemoryOnLargeArrayAllocation", 
		"NumericOverflow"})
	@Override
	public void test()
		throws Throwable
	{
		Random random = new Random(0xCAFEBABE_DEADBEEFL);
		
		// Maximal values
		this.secondary("minmin", Long.MIN_VALUE * Long.MIN_VALUE);
		this.secondary("minmax", Long.MIN_VALUE * Long.MAX_VALUE);
		this.secondary("maxmin", Long.MAX_VALUE * Long.MIN_VALUE);
		this.secondary("maxmax", Long.MAX_VALUE * Long.MAX_VALUE);
		
		// Results
		long[] incr = new long[TestLongMultiply.COUNT];
		long[] negi = new long[TestLongMultiply.COUNT];
		long[] rand = new long[TestLongMultiply.COUNT];
		
		// Constant division!
		for (int i = 0; i < TestLongMultiply.COUNT; i++)
		{
			long a = random.nextLong();
			
			// For certain values get fixed values, otherwise very random
			// ones
			long b = (i < TestLongMultiply.HALF_COUNT ?
				1 + random.nextInt(1 + i) : random.nextLong());
				
			// Negative values?
			boolean nega = (i & 1) != 0;
			boolean negb = ((i >> 1) & 1) != 0;
			
			incr[i] = (long)i * (TestLongMultiply.COUNT + i);
			negi[i] = (long)(nega ? -i : i) *
				((TestLongMultiply.COUNT + i) * (negb ? -1 : 1));
			rand[i] = a * b;
		}
		
		// Store results
		this.secondary("incr", incr);
		this.secondary("negi", negi);
		this.secondary("rand", rand);
	}
}
