// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.plugin.util;

import java.util.Arrays;

/**
 * This contains utilities which operate on character sequences.
 *
 * @since 2017/11/30
 */
public final class CharSequenceUtils
{
	/**
	 * Not used.
	 *
	 * @since 2017/11/30
	 */
	private CharSequenceUtils()
	{
	}
	
	/**
	 * Splits the specified character sequence using the given delimeter and
	 * returns all of the fields which are contained within. Extra whitespace
	 * within fields are not trimmed.
	 *
	 * @param __delim The delimeter to split fields by.
	 * @param __s The sequence to split.
	 * @return An array containing all of the fields.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/30
	 */
	public static CharSequence[] fieldSplit(char __delim,
		CharSequence __s)
		throws NullPointerException
	{
		if (__s == null)
			throw new NullPointerException("NARG");
		
		// Get all indexes of that given character
		int[] ind = CharSequenceUtils.multipleIndexOf(__delim, __s);
		int delCount = ind.length;
		
		int n = delCount + 1;
		CharSequence[] rv = new CharSequence[n];
		for (int l = -1, r = 0, i = 0; i < n; i++, l++, r++)
			rv[i] = __s.subSequence((l >= 0 ? ind[l] + 1 : 0),
				(r < delCount ? ind[r] : __s.length()));
		
		return rv;
	}
	
	/**
	 * Searches the given sequence for the first occurrence of the specified
	 * character.
	 *
	 * @param __c The character to locate.
	 * @param __s The sequence to look inside.
	 * @return The index of the first occurrence.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/30
	 */
	public static final int firstIndex(char __c, CharSequence __s)
		throws NullPointerException
	{
		if (__s == null)
			throw new NullPointerException("NARG");
		
		for (int i = 0, n = __s.length(); i < n; i++)
			if (__c == __s.charAt(i))
				return i;
		return -1;
	}
	
	/**
	 * Searches the given sequence for the first occurrence of the specified
	 * characters.
	 *
	 * @param __c The characters to locate.
	 * @param __s The sequence to look inside.
	 * @return The index of the first occurrence.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/30
	 */
	public static final int firstIndex(char[] __c, CharSequence __s)
		throws NullPointerException
	{
		if (__c == null || __s == null)
			throw new NullPointerException("NARG");
		
		// For optimization sort the input array to find characters faster
		__c = __c.clone();
		Arrays.sort(__c);
		
		// Forward to one which assumes sorted input
		return CharSequenceUtils.firstIndexSorted(__c, __s);
	}
	
	/**
	 * Searches the given sequence for the first occurrence of the specified
	 * characters.
	 *
	 * @param __c The characters to locate.
	 * @param __s The sequence to look inside.
	 * @return The index of the first occurrence.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/30
	 */
	public static final int firstIndex(String __c, CharSequence __s)
		throws NullPointerException
	{
		if (__c == null || __s == null)
			throw new NullPointerException("NARG");
		
		return CharSequenceUtils.firstIndex(__c.toCharArray(), __s);
	}
	
	/**
	 * Searches the given sequence for the first occurrence of the specified
	 * characters. This assumes that the character set has already been
	 * sorted.
	 *
	 * @param __c The characters to locate, this is required to be sorted.
	 * @param __s The sequence to look inside.
	 * @return The index of the first occurrence.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/30
	 */
	public static final int firstIndexSorted(char[] __c, CharSequence __s)
		throws NullPointerException
	{
		if (__c == null || __s == null)
			throw new NullPointerException("NARG");
		
		// Go through ever character
		for (int i = 0, n = __s.length(), y = __c.length; i < n; i++)
		{
			// Use binary search because it is faster than checking each
			// and every element
			char c = __s.charAt(i);
			if (Arrays.binarySearch(__c, c) >= 0)
				return i;
		}
		
		// Not found
		return -1;
	}
	
	/**
	 * Returns an array containing all of the indexes that the specified
	 * character appears in the given sequence.
	 *
	 * @param __c The character to get the indexes for.
	 * @param __s The sequence to check in.
	 * @return An array containing the array indexes for the given character,
	 * if there are none then the array will be empty.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/26
	 */
	public static final int[] multipleIndexOf(char __c, CharSequence __s)
		throws NullPointerException
	{
		if (__s == null)
			throw new NullPointerException("NARG");
		
		IntegerList list = new IntegerList();
		
		// Find every character index
		for (int i = 0, n = __s.length(), lastdx = 0; i < n; i++)
		{
			char c = __s.charAt(i);
			
			// Add index to list if found
			if (c == __c)
				list.addInteger(i);
		}
		
		// Finish
		return list.toIntegerArray();
	}
}

