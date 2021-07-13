// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package util;

import cc.squirreljme.runtime.cldc.debug.Debugging;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.multiphasicapps.tac.TestRunnable;

/**
 * Tests that sorting is stable, as required by Java.
 *
 * @since 2021/07/12
 */
public class TestStableSort
	extends TestRunnable
{
	/** The number of items to sort. */
	public static final int COUNT =
		128;
	
	/**
	 * {@inheritDoc}
	 * @since 2021/07/12
	 */
	@SuppressWarnings("RedundantCollectionOperation")
	@Override
	public void test()
		throws Throwable
	{
		Random random = new Random(0xDEAD_BEEFL);
		
		// Setup a bunch of items
		List<Item> original = new ArrayList<>(TestStableSort.COUNT);
		Set<Integer> taken = new HashSet<>();
		for (int i = 0; i < TestStableSort.COUNT; i++)
		{
			// We could put extra values in order but that would not be
			// a good test since it would be very linear and not random.
			int extra;
			for (;;)
			{
				// Make every value in here unique!
				extra = random.nextInt(TestStableSort.COUNT);
				if (taken.add(extra))
					break;
			}
			
			// Add the item to be sorted
			original.add(random.nextInt(1 + i),
				new Item(i / 3, extra));
		}
		
		// Make as an array
		Item[] array = original.toArray(new Item[original.size()]);
		Item[] list = array.clone();
		
		// Sort the lists
		Arrays.sort(array);
		Collections.sort(Arrays.asList(list));
		
		// These must be equal
		this.secondary("equal", Arrays.equals(array, list));
		
		// Extra insurance to make sure everything is as expected
		this.secondary("sorted",
			TestStableSort.isSorted(Arrays.asList(array)));
		
		// Extract all the extra values, which should be in a specific order
		int[] extra = new int[TestStableSort.COUNT];
		for (int i = 0; i < TestStableSort.COUNT; i++)
			extra[i] = array[i].extra;
		
		// Report these values
		this.secondary("extra", extra);
	}
	
	
	/**
	 * Checks if this list is sorted.
	 * 
	 * @param __list The list to check. 
	 * @return If this is sorted.
	 * @since 2021/07/12
	 */
	public static boolean isSorted(List<Item> __list)
	{
		// Each later number must be bigger than the earlier number
		Item last = null;
		for (int i = 0, n = __list.size(); i < n; i++)
		{
			Item now = __list.get(i);
			
			if (last != null && now.compareTo(last) < 0)
				return false;
			
			last = now;
		}
		
		// Must be sorted
		return true;
	}
	
	/**
	 * The items to be check.
	 * 
	 * @since 2021/07/12
	 */
	public static class Item
		implements Comparable<Item>
	{
		/** The sorted value. */
		public final int sorted;
		
		/** The extra value. */
		public final int extra;
		
		/**
		 * Initializes the item.
		 * 
		 * @param __sorted The sorted item.
		 * @param __extra The extra item.
		 * @since 2021/07/12
		 */
		public Item(int __sorted, int __extra)
		{
			this.sorted = __sorted;
			this.extra = __extra;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2021/07/12
		 */
		@Override
		public int compareTo(Item __o)
		{
			return __o.sorted - this.sorted;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2021/07/12
		 */
		@Override
		public boolean equals(Object __o)
		{
			if (this == __o)
				return true;
			
			if (!(__o instanceof Item))
				return false;
			
			Item o = (Item)__o;
			return this.sorted == o.sorted &&
				this.extra == o.extra;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2021/07/12
		 */
		@Override
		public int hashCode()
		{
			return this.sorted ^ (~this.extra);
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2021/07/12
		 */
		@Override
		public String toString()
		{
			return String.format("[%d|%d]", this.sorted, this.extra);
		}
	}
}
