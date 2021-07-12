// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package lang.math;

import cc.squirreljme.jvm.SoftLong;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import java.util.Random;
import net.multiphasicapps.tac.TestRunnable;

/**
 * Tests long multiplication using software math.
 *
 * @since 2021/04/05
 */
public class TestLongMultiplySoft
	extends TestRunnable
{
	/** Counts. */
	public static final int COUNT =
		128;
	
	/** Half count. */
	public static final int HALF_COUNT =
		TestLongMultiplySoft.COUNT / 2;
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/20
	 */
	@SuppressWarnings("CheckForOutOfMemoryOnLargeArrayAllocation")
	@Override
	public void test()
		throws Throwable
	{
		Random random = new Random(0xCAFEBABE_DEADBEEFL);
		
		// Maximal values
		this.secondary("minmin",
			TestLongMultiplySoft.mul(Long.MIN_VALUE, Long.MIN_VALUE));
		this.secondary("minmax",
			TestLongMultiplySoft.mul(Long.MIN_VALUE, Long.MAX_VALUE));
		this.secondary("maxmin",
			TestLongMultiplySoft.mul(Long.MAX_VALUE, Long.MIN_VALUE));
		this.secondary("maxmax",
			TestLongMultiplySoft.mul(Long.MAX_VALUE, Long.MAX_VALUE));
		
		// Results
		long[] incr = new long[TestLongMultiplySoft.COUNT];
		long[] negi = new long[TestLongMultiplySoft.COUNT];
		long[] rand = new long[TestLongMultiplySoft.COUNT];
		
		// Constant division!
		for (int i = 0; i < TestLongMultiplySoft.COUNT; i++)
		{
			long a = random.nextLong();
			
			// For certain values get fixed values, otherwise very random
			// ones
			long b = (i < TestLongMultiplySoft.HALF_COUNT ?
				1 + random.nextInt(1 + i) : random.nextLong());
				
			// Negative values?
			boolean nega = (i & 1) != 0;
			boolean negb = ((i >> 1) & 1) != 0;
			
			incr[i] = TestLongMultiplySoft.mul(i,
				 (TestLongMultiplySoft.COUNT + i));
			negi[i] = TestLongMultiplySoft.mul(nega ? -i : i,
				((TestLongMultiplySoft.COUNT + i) * (negb ? -1 : 1)));
			rand[i] = TestLongMultiplySoft.mul(a, b);
		}
		
		// Store results
		this.secondary("incr", incr);
		this.secondary("negi", negi);
		this.secondary("rand", rand);
	}
	
	/**
	 * Multiplies A and B using software.
	 * 
	 * @param __a A
	 * @param __b B.
	 * @return The result.
	 * @since 2021/04/05
	 */
	private static long mul(long __a, long __b)
	{
		int ah = (int)(__a >>> 32);
		int al = (int)__a;
		int bh = (int)(__b >>> 32);
		int bl = (int)__b;
		
		long rv = SoftLong.mul(al, ah, bl, bh);
		
		long shouldBe = __a * __b;
		if (shouldBe != rv)
			Debugging.debugNote("%c %d * %d = %d (%d) : %x [%x]",
				(shouldBe == rv ? '=' : '!'),
				__a, __b,
				rv, shouldBe,
				rv, shouldBe);
		
		return rv;
	}
}
