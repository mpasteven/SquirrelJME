// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package util;

import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.runtime.cldc.util.IntegerArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.multiphasicapps.tac.TestRunnable;

/**
 * Tests sorting a large number of items, determined by random.
 *
 * @since 2021/07/12
 */
public class TestSortingLots
	extends TestRunnable
{
	/** The number of items to test. */
	public static final int NUM_ITEMS =
		4321;
	
	/**
	 * {@inheritDoc}
	 * @since 2021/07/12
	 */
	@SuppressWarnings("CheckForOutOfMemoryOnLargeArrayAllocation")
	@Override
	public void test()
		throws Throwable
	{
		// Use random seed, but not a fixed one so this has unique data
		// every time!
		Random rand = new Random(System.currentTimeMillis() ^
			System.nanoTime());
		
		// Test with primitive integers first
		int[] ints = new int[TestSortingLots.NUM_ITEMS];
		for (int i = 0; i < TestSortingLots.NUM_ITEMS; i++)
			ints[i] = rand.nextInt();
		
		// Do sort!
		long intsStart = System.nanoTime(); 
		Arrays.sort(ints);
		Debugging.debugNote("ints: %d in %dms",
			TestSortingLots.NUM_ITEMS,
			(System.nanoTime() - intsStart) / 1_000_000L);
		
		// Make sure everything is sorted
		this.secondary("ints",
			TestSortingLots.isSorted(new IntegerArrayList(ints)));
		
		// Then with integer objects
		Integer[] objects = new Integer[TestSortingLots.NUM_ITEMS];
		for (int i = 0; i < TestSortingLots.NUM_ITEMS; i++)
			objects[i] = rand.nextInt();
		
		// Do sort!
		long objectsStart = System.nanoTime();
		Arrays.sort(objects);
		Debugging.debugNote("objects: %d in %dms",
			TestSortingLots.NUM_ITEMS,
			(System.nanoTime() - objectsStart) / 1_000_000L);
		
		// Make sure is sorted!
		this.secondary("objects",
			TestSortingLots.isSorted(Arrays.asList(objects)));
	}
	
	/**
	 * Checks if this list is sorted.
	 * 
	 * @param __list The list to check. 
	 * @return If this is sorted.
	 * @since 2021/07/12
	 */
	public static boolean isSorted(List<Integer> __list)
	{
		// Each later number must be bigger than the earlier number
		int last = Integer.MIN_VALUE;
		for (int i = 0, n = __list.size(); i < n; i++)
		{
			int now = __list.get(i);
			
			if (now < last)
				return false;
			
			last = now;
		}
		
		// Must be sorted
		return true;
	}
}
