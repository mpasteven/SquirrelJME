// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.runtime.cldc.util;

import java.util.Comparator;
import java.util.List;

/**
 * This class contains utilities used for {@link IntegerArray}.
 *
 * @since 2018/10/28
 */
public final class IntegerArrays
{
	/**
	 * Not used.
	 *
	 * @since 2018/10/28
	 */
	private IntegerArrays()
	{
	}
	
	/**
	 * Searches the given sorted array for the given element.
	 *
	 * @param __a The sorted array to search.
	 * @param __from The from index.
	 * @param __to The to index.
	 * @param __key The key to locate.
	 * @return The index of the given key or {@code (-(insertion point) - 1)}
	 * indicating where the element would be found.
	 * @throws ArrayIndexOutOfBoundsException If the from or to index exceed
	 * the bounds of the array.
	 * @throws IllegalArgumentException If the from index is higher than the
	 * to index.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/10/28
	 */
	public static int binarySearch(IntegerArray __a, int __from, int __to,
		int __key)
		throws ArrayIndexOutOfBoundsException, IllegalArgumentException,
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
		
		// Empty array, will always be at the 0th index
		int len = __from - __to;
		if (len == 0)
			return -1;
		
		// Array has a single element, so only check that
		else if (len == 1)
		{
			int pv = __a.get(__from);
			
			// Is same
			if (pv == __key)
				return __from;
			
			// Value is either the 0th or 1st element
			if (pv < __key)
				return -1;
			else
				return -2;
		}
		
		// Use the same index
		__to -= 1;
		
		// Search for element at the pivot first, stop if the from and to are
		// at the same points
		while (__from <= __to)
		{
			// Calculate the pivot and use its value
			int p = __from + (((__to - __from) + 1) >> 1),
				pv = __a.get(p);
			
			// Left of pivot?
			if (__key < pv)
				__to = p - 1;
			
			// Right of pivot?
			else if (__key > pv)
				__from = p + 1;
			
			// Match
			else
				return p;
		}
		
		return (-__from) - 1;
	}
	
	/**
	 * Sorts the specified array.
	 *
	 * @param __a The array to sort.
	 * @param __from The source array.
	 * @param __to The destination array.
	 * @throws ArrayIndexOutOfBoundsException If the from and/or to index
	 * exceed the array bounds.
	 * @throws IllegalArgumentException If the from index is greater than to
	 * index.
	 * @throws NullPointerException If no array was specified.
	 * @see ShellSort#sort(List, int, int, Comparator) 
	 * @since 2018/10/28
	 */
	public static void sort(IntegerArray __a, int __from, int __to)
		throws ArrayIndexOutOfBoundsException, IllegalArgumentException,
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
		
		// Pointless sort?
		int n = __to - __from;
		if (n == 0 || n == 1)
			return;
		
		// If only two values are being sorted, it is a simple swap check
		if (n == 2)
		{
			IntegerArrays.__sortTwo(__a, __from);
			return;
		}
		
		// Work down from the highest gap to the lowest
		for (int gap : ShellSort.gaps(n))
		{
			// Gapped insertion sort
			for (int i = gap; i < n; i++)
			{
				// Use this to make a hole
				int temp = __a.get(__from + i);
				
				// Shift earlier gap elements down
				int j;
				for (j = i; j >= gap &&
						__a.get(__from + (j - gap)) > temp; j -= gap)
					__a.set(__from + j, __a.get(__from + (j - gap)));
				
				// Put in the correct position
				__a.set(__from + j, temp);
			}
		}
	}
	
	/**
	 * Sorts only two items.
	 * 
	 * @param __a The array to sort.
	 * @param __from The source array.
	 * @since 2021/07/12
	 */
	private static void __sortTwo(IntegerArray __a, int __from)
	{
		int ib = __from + 1;
		
		// Get both values
		int a = __a.get(__from);
		int b = __a.get(ib);
		
		// If the second is lower than the first, we need to swap
		if (b < a)
		{
			__a.set(__from, b);
			__a.set(ib, a);
		}
	}
}

