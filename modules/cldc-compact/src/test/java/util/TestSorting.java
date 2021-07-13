// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.multiphasicapps.tac.TestRunnable;

/**
 * Tests that arrays can be sorted.
 *
 * @since 2019/05/09
 */
public class TestSorting
	extends TestRunnable
{
	/** Random seed. */
	public static final int SEED =
		123456789;
	
	/** The number of elements to sort through. */
	private static final int[] _COUNTS =
		new int[]{1, 2, 3, 4, 8, 13, 26};
	
	/**
	 * {@inheritDoc}
	 * @since 2019/05/09
	 */
	@Override
	public void test()
	{
		for (int c = 0, cn = TestSorting._COUNTS.length; c < cn; c++)
		{
			Random rand;
			
			int count = TestSorting._COUNTS[c];
			String cid = String.format("%02d", count);
		
			// Byte
			rand = new Random(TestSorting.SEED);
			byte[] ab = new byte[count];
			for (int i = 0; i < count; i++)
				ab[i] = (byte)rand.nextInt();
			Arrays.sort(ab);
			this.secondary("arraybyte" + cid, ab);
			
			// Short
			rand = new Random(TestSorting.SEED);
			short[] as = new short[count];
			for (int i = 0; i < count; i++)
				as[i] = (short)rand.nextInt();
			Arrays.sort(as);
			this.secondary("arrayshort" + cid, as);
			
			// Char
			rand = new Random(TestSorting.SEED);
			char[] ac = new char[count];
			for (int i = 0; i < count; i++)
				ac[i] = (char)rand.nextInt();
			Arrays.sort(ac);
			this.secondary("arraychar" + cid, ac);
			
			// Integer
			rand = new Random(TestSorting.SEED);
			int[] ai = new int[count];
			for (int i = 0; i < count; i++)
				ai[i] = rand.nextInt();
			Arrays.sort(ai);
			this.secondary("arrayint" + cid, ai);
			
			// Object (just integers)
			rand = new Random(TestSorting.SEED);
			Integer[] ao = new Integer[count];
			for (int i = 0; i < count; i++)
				ao[i] = rand.nextInt();
			Arrays.sort(ao);
			this.secondary("arrayobject" + cid, ao);
			
			// Array List
			rand = new Random(TestSorting.SEED);
			List<Integer> la = new ArrayList<>(count);
			for (int i = 0; i < count; i++)
				la.add(rand.nextInt());
			Collections.sort(la);
			this.secondary("listarray" + cid,
				la.<Integer>toArray(new Integer[count]));
			
			// Linked List
			rand = new Random(TestSorting.SEED);
			List<Integer> ll = new LinkedList<>();
			for (int i = 0; i < count; i++)
				ll.add(rand.nextInt());
			Collections.sort(ll);
			this.secondary("listlinked" + cid,
				ll.<Integer>toArray(new Integer[count]));
		}
	}
}

