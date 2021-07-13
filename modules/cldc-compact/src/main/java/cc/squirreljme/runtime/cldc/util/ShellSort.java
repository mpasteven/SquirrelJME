// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.runtime.cldc.util;

import cc.squirreljme.runtime.cldc.debug.Debugging;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * Shell sorting algorithm.
 *
 * @since 2019/05/10
 */
public class ShellSort
{
	/** Gaps used in shell sort, used as a base to determine the gap size. */
	private static final int[] _GAPS =
		new int[]{1750, 701, 301, 132, 57, 23, 10, 4, 1};
	
	/** Tiniest gap. */
	private static final int[] _TINY =
		new int[]{1};
	
	/**
	 * Returns the best set of gaps to use for the list.
	 * 
	 * @param __n The number of items in the list.
	 * @return The set of gaps to use.
	 * @throws IllegalArgumentException If the length is negative.
	 * @since 2021/07/02
	 */
	public static int[] gaps(int __n)
		throws IllegalArgumentException
	{
		// {@squirreljme.error ZZ5e Request of gaps with a negative list
		// size.}
		if (__n < 0)
			throw new IllegalArgumentException("ZZ5e");
		
		// If the length is very short, just use the smallest GAP there is
		if (__n < 4)
			return ShellSort._TINY;
		
		// Otherwise find the set of gaps that would not be useless here
		int[] gaps = ShellSort._GAPS;
		int n = gaps.length;
		
		// Go through and find the gap sequence that reduces the chance of
		// pointless runs.
		for (int i = 0; i < n; i++)
			if (__n > gaps[i])
			{
				// If we are a really big list, then we just use all the gap
				// sets we already know about
				if (i == 0)
					return gaps;
				
				// Java ME 8 does not have Arrays.copyOfRange() so we need to
				// do the same here
				int subLen = n - i;
				int[] rv = new int[subLen];
				
				// Take the final elements and use those
				System.arraycopy(gaps, i,
					rv, 0, subLen);
				
				return rv;
			}
		
		// Technically this point should never be reached, but if it does
		// then the tiniest value is used
		return ShellSort._TINY;
	}
	
	/**
	 * Sorts the specified collection.
	 *
	 * @param <T> The type to sort.
	 * @param __a The collection to sort.
	 * @param __from The from index.
	 * @param __to The to index.
	 * @param __comp The comparator to use.
	 * @throws IndexOutOfBoundsException If the from or to index are
	 * outside of bounds.
	 * @throws IllegalArgumentException If the from address is greater than
	 * the to address.
	 * @throws NullPointerException If no collection was specified.
	 * @see IntegerArrays#sort(IntegerArray, int, int) 
	 * @since 2019/05/09
	 */
	public static <T> void sort(List<T> __a,
		int __from, int __to, Comparator<? super T> __comp)
		throws IndexOutOfBoundsException, IllegalArgumentException,
			NullPointerException
	{
		// Check
		if (__a == null)
			throw new NullPointerException("NARG");
		int an = __a.size();
		if (__from < 0 || __to > an)
			throw new ArrayIndexOutOfBoundsException("IOOB");
		if (__from > __to)
			throw new IllegalArgumentException("IOOB");
		
		// Use natural comparator?
		if (__comp == null)
			__comp = NaturalComparator.<T>instance();
		
		// Pointless sort?
		int n = __to - __from;
		if (n == 0 || n == 1)
			return;
		
		// If the list is not random access, then there will be a great
		// penalty sorting it and this may result in quadratic performance loss
		if (!(__a instanceof RandomAccess))
		{
			ShellSort.__nonRandom(__a, __from, __comp, n);
			return;
		}
		
		// If only two values are being sorted, it is a simple swap check
		if (n == 2)
		{
			ShellSort.__sortTwo(__a, __from, __comp);
			return;
		}
		
		// Keep storage for indexes, this is used for an additional comparison
		// to force sorts to be stable. Although it costs some extra memory
		// it will try to use the most compact array possible.
		IntegerArray index = ShellSort.__indexStore(n);
		
		// Work down from the highest gap to the lowest
		for (int gap : ShellSort.gaps(n))
		{
			// Gapped insertion sort
			for (int i = gap; i < n; i++)
			{
				// Use this to make a hole
				int holeFrom = __from + i;
				T temp = __a.get(holeFrom);
				int tempDx = index.get(holeFrom);
				
				// Shift earlier gap elements down
				int j;
				for (j = i; j >= gap; j -= gap)
				{
					int from = __from + (j - gap);
					int to = __from + j;
					
					// This forces the sort to be stable by also taking into
					// account the index of the entry if the values are equal
					int comp = __comp.compare(__a.get(from), temp);
					if (comp == 0)
						comp = index.get(from) - tempDx;
					
					// Is the other value higher?
					if (comp > 0)
					{
						__a.set(to, __a.get(from));
						index.set(to, index.get(from));
					}
					else 
						break;
				}
				
				// Put in the correct position
				int holeTo = __from + j;
				__a.set(holeTo, temp);
				index.set(holeTo, tempDx);
			}
		}
	}
	
	/**
	 * Generates storage for storing values up to a given amount
	 * 
	 * @param __n The number of elements to store.
	 * @return A wrapped integer array to store the sorted items.
	 * @since 2021/07/12
	 */
	@SuppressWarnings("MagicNumber")
	private static IntegerArray __indexStore(int __n)
	{
		// Create an array that can store indexes for everything
		IntegerArray rv;
		if (__n < 256)
			rv = new UnsignedByteIntegerArray(new byte[__n]);
		else if (__n < 65536)
			rv = new UnsignedShortIntegerArray(new short[__n]);
		else
			rv = new IntegerIntegerArray(new int[__n]);
		
		// Now fill all the values up, to remember the original indexes
		for (int i = 0; i < __n; i++)
			rv.set(i, i);
		
		return rv;
	}
	
	/**
	 * For non-random access, performs the actual sort algorithm by copying
	 * everything to a temporary array first.
	 * 
	 * @param <T> The type of value to sort.
	 * @param __list The list to sort.
	 * @param __from Which element to sort from.
	 * @param __comp The comparator to use.
	 * @param __n The number of items to sort.
	 * @since 2021/07/12
	 */
	private static <T> void __nonRandom(List<T> __list, int __from,
		Comparator<? super T> __comp, int __n)
	{
		// Setup duplicate
		List<T> dup = new ArrayList<>(__n);
		
		// Copy values using source iterator (less CPU intensive)
		ListIterator<T> it = __list.listIterator(__from);
		for (int o = 0; o < __n; o++)
			dup.add(it.next());
		
		// Sort this array
		ShellSort.<T>sort(dup, 0, __n, __comp);
		
		// Our iterator should still be good, so we can go backwards
		// setting all the values in it
		for (int i = __n - 1; i >= 0; i--)
		{
			it.previous();
			it.set(dup.get(i));
		}
	}
	
	/**
	 * Sort of only two items.
	 * 
	 * @param <T> The type of value to sort.
	 * @param __list The list to sort.
	 * @param __from Which element to sort from.
	 * @param __comp The comparator to use.
	 * @since 2021/07/12
	 */
	private static <T> void __sortTwo(List<T> __list, int __from,
		Comparator<? super T> __comp)
	{
		int ia = __from;
		int ib = __from + 1;
		
		// Get both values
		T a = __list.get(ia);
		T b = __list.get(ib);
		
		// If the second is lower than the first, we need to swap
		if (__comp.compare(b, a) < 0)
		{
			__list.set(ia, b);
			__list.set(ib, a);
		}
	}
}

