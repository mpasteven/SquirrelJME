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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * This class contains static methods which can be used for manipulating
 * strings.
 *
 * @since 2017/11/23
 */
public final class StringUtils
{
	/**
	 * Not used.
	 *
	 * @since 2017/11/23
	 */
	private StringUtils()
	{
	}
	
	/**
	 * Splits the given string using the specified delimeters and outputs it
	 * to the given collection.
	 *
	 * @param __delim The delimeters to use.
	 * @param __s The string to split.
	 * @param __out The collection to place split strings into.
	 * @return {@code __out}
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/23
	 */
	public static final Collection<String> basicSplit(char[] __delim,
		String __s, Collection<String> __out)
		throws NullPointerException
	{
		if (__delim == null || __s == null || __out == null)
			throw new NullPointerException("NARG");
		
		// Parse string
		boolean dows = true;
		int lastdelim = -2;
		for (int i = 0, n = __s.length(), mark = 0; i <= n; i++)
		{
			// -1 is a special delimeter for the end of string because
			// otherwise if the string does not end in a delimeter it will not
			// be found
			int c = (i == n ? -1 : __s.charAt(i));
			
			// Is this a delimeter
			if (c == lastdelim || c == -1 || StringUtils.__indexOf(__delim, (char)c) >= 0)
			{
				// Remember last delimeter for potential speed
				lastdelim = c;
				
				// If reading delimeters, clear flag and mark
				// to remember the current index
				if (dows)
				{
					dows = false;
					mark = i;
				}
				
				// Otherwise end of sequence, generate string
				else
				{
					// Split out
					__out.add(__s.substring(mark, i));
					
					// Switch to handling delimeters
					dows = true;
				}
			}
			
			// If reading delimeters, clear flag and mark
			// to remember the current index, is not delimeters
			// here
			else if (dows)
			{
				dows = false;
				mark = i;
			}
		}
		
		// Return output always
		return __out;
	}
	
	/**
	 * Splits the given string using the specified delimeters and outputs it
	 * to the given collection.
	 *
	 * @param __delim The delimeters to use.
	 * @param __s The string to split.
	 * @param __out The collection to place split strings into.
	 * @return {@code __out}
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/23
	 */
	public static final Collection<String> basicSplit(String __delim,
		String __s, Collection<String> __out)
		throws NullPointerException
	{
		if (__delim == null || __s == null || __out == null)
			throw new NullPointerException("NARG");
		
		return StringUtils.basicSplit(__delim.toCharArray(), __s, __out);
	}
	
	/**
	 * Splits the given string using the specified delimeter.
	 *
	 * @param __delim The delimeter to use.
	 * @param __s The string to split.
	 * @return The split string.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/12/23
	 */
	public static final String[] basicSplit(char __delim, String __s)
	{
		return StringUtils.basicSplit(new char[]{__delim}, __s);
	}
	
	/**
	 * Splits the given string using the specified delimeters.
	 *
	 * @param __delim The delimeters to use.
	 * @param __s The string to split.
	 * @return The split sequence of strings.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/23
	 */
	public static final String[] basicSplit(char[] __delim, String __s)
		throws NullPointerException
	{
		if (__delim == null || __s == null)
			throw new NullPointerException("NARG");
		
		Collection<String> rv = StringUtils.basicSplit(__delim, __s,
			new ArrayList<String>());
		return rv.<String>toArray(new String[rv.size()]);
	}
	
	/**
	 * Splits the given string using the specified delimeters.
	 *
	 * @param __delim The delimeters to use.
	 * @param __s The string to split.
	 * @return The split sequence of strings.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/23
	 */
	public static final String[] basicSplit(String __delim, String __s)
		throws NullPointerException
	{
		if (__delim == null || __s == null)
			throw new NullPointerException("NARG");
		
		return StringUtils.basicSplit(__delim.toCharArray(), __s);
	}
	
	/**
	 * Splits the specified string using the given delimeter and returns all
	 * of the fields which are contained within, any leading and trailing
	 * whitespace is trimmed.
	 *
	 * @param __delim The delimeter to split fields by.
	 * @param __s The string to split.
	 * @return An array containing all of the fields.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/30
	 */
	public static final String[] fieldSplitAndTrim(char __delim, String __s)
		throws NullPointerException
	{
		if (__s == null)
			throw new NullPointerException("NARG");
		
		// Split and trim
		String[] rv = StringUtils.fieldSplit(__delim, __s);
		for (int i = 0, n = rv.length; i < n; i++)
			rv[i] = rv[i].trim();
		return rv;
	}
	
	/**
	 * Splits the specified string using the given delimeter and returns all
	 * of the fields which are contained within. Extra whitespace within
	 * fields are not trimmed.
	 *
	 * @param __delim The delimiter to split fields by.
	 * @param __s The string to split.
	 * @return An array containing all of the fields.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/30
	 */
	public static String[] fieldSplit(char __delim, String __s)
		throws NullPointerException
	{
		if (__s == null)
			throw new NullPointerException("NARG");
		
		CharSequence[] xrv = CharSequenceUtils.fieldSplit(__delim, __s);
		
		int n = xrv.length;
		String[] rv = new String[n];
		for (int i = 0; i < n; i++)
			rv[i] = xrv[i].toString();
		
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
	public static final int firstIndex(char __c, String __s)
		throws NullPointerException
	{
		return CharSequenceUtils.firstIndex(__c, __s);
	}
	
	/**
	 * Searches the given string for the first occurrence of the specified
	 * characters.
	 *
	 * @param __c The characters to locate.
	 * @param __s The string to look inside.
	 * @return The index of the first occurrence.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/30
	 */
	public static final int firstIndex(char[] __c, String __s)
		throws NullPointerException
	{
		return CharSequenceUtils.firstIndex(__c, __s);
	}
	
	/**
	 * Searches the given string for the first occurrence of the specified
	 * characters.
	 *
	 * @param __c The characters to locate.
	 * @param __s The string to look inside.
	 * @return The index of the first occurrence.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/30
	 */
	public static final int firstIndex(String __c, String __s)
		throws NullPointerException
	{
		return CharSequenceUtils.firstIndex(__c, __s);
	}
	
	/**
	 * Searches the given string for the first occurrence of the specified
	 * characters. This assumes that the character set has already been
	 * sorted.
	 *
	 * @param __c The characters to locate, this is required to be sorted.
	 * @param __s The string to look inside.
	 * @return The index of the first occurrence.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/30
	 */
	public static final int firstIndexSorted(char[] __c, String __s)
		throws NullPointerException
	{
		return CharSequenceUtils.firstIndexSorted(__c, __s);
	}
	
	/**
	 * Returns an array containing all of the indexes that the specified
	 * character appears in the given string.
	 *
	 * @param __c The character to get the indexes for.
	 * @param __s The string to check in.
	 * @return An array containing the array indexes for the given character,
	 * if there are none then the array will be empty.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/26
	 */
	public static final int[] multipleIndexOf(char __c, String __s)
		throws NullPointerException
	{
		return CharSequenceUtils.multipleIndexOf(__c, __s);
	}
	
	/**
	 * Converts the specified string to lowercase ignoring locale, this uses
	 * {@link Character#toLowerCase(char)}.
	 *
	 * @param __s The string to convert.
	 * @return The lowercased string.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/30
	 */
	public static final String toLowerCaseNoLocale(String __s)
		throws NullPointerException
	{
		if (__s == null)
			throw new NullPointerException("NARG");
		
		int n = __s.length();
		StringBuilder sb = new StringBuilder(n);
		for (int i = 0; i < n; i++)
			sb.append(Character.toLowerCase(__s.charAt(i)));
		return sb.toString();
	}
	
	/**
	 * Converts the specified string to uppercase ignoring locale, this uses
	 * {@link Character#toUpperCase(char)}.
	 *
	 * @param __s The string to convert.
	 * @return The uppercased string.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/30
	 */
	public static final String toUpperCaseNoLocale(String __s)
		throws NullPointerException
	{
		if (__s == null)
			throw new NullPointerException("NARG");
		
		int n = __s.length();
		StringBuilder sb = new StringBuilder(n);
		for (int i = 0; i < n; i++)
			sb.append(Character.toUpperCase(__s.charAt(i)));
		return sb.toString();
	}
	
	/**
	 * Searches the input array to see if the given character is within the
	 * array.
	 *
	 * @param __a The array to check.
	 * @param __c The character to find in the array.
	 * @return The index of the character or {@code -1} if it was not found.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/23
	 */
	private static final int __indexOf(char[] __a, char __c)
		throws NullPointerException
	{
		if (__a == null)
			throw new NullPointerException("NARG");
		
		for (int i = 0, n = __a.length; i < n; i++)
			if (__c == __a[i])
				return i;
		return -1;
	}
}

