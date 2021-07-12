// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.summercoat;

import cc.squirreljme.jvm.Assembly;
import cc.squirreljme.jvm.summercoat.constants.MemHandleKind;
import cc.squirreljme.runtime.cldc.util.NaturalComparator;
import java.util.Objects;

/**
 * General utilities for SummerCoat.
 *
 * @since 2021/01/17
 */
public final class SummerCoatUtil
{
	/**
	 * Not used.
	 * 
	 * @since 2021/01/17
	 */
	private SummerCoatUtil()
	{
	}
	
	/**
	 * Is this an array kind of {@link MemHandleKind}.
	 * 
	 * @param __kind The {@link MemHandleKind} to check.
	 * @return If this is an array kind.
	 * @throws IllegalArgumentException If the kind is not valid.
	 * @since 2021/01/17
	 */
	public static boolean isArrayKind(int __kind)
		throws IllegalArgumentException
	{
		// {@squirreljme.error ZZ4l Invalid memory handle kind. (The kind)}
		if (__kind <= 0 || __kind >= MemHandleKind.NUM_KINDS)
			throw new IllegalArgumentException("ZZ4l " + __kind);
		
		switch (__kind)
		{
			case MemHandleKind.BOOLEAN_ARRAY:
			case MemHandleKind.BYTE_ARRAY:
			case MemHandleKind.SHORT_ARRAY:
			case MemHandleKind.CHARACTER_ARRAY:
			case MemHandleKind.INTEGER_ARRAY:
			case MemHandleKind.LONG_ARRAY:
			case MemHandleKind.FLOAT_ARRAY:
			case MemHandleKind.DOUBLE_ARRAY:
			case MemHandleKind.OBJECT_ARRAY:
				return true;
		}
		
		return false;
	}
	
	/**
	 * Loads a string from the given address.
	 * 
	 * @param __utfP The pointer to load from, if {@code 0} then {@code null}
	 * will be returned.
	 * @return The read string or {@code null} if {@code 0}.
	 * @since 2021/04/03
	 */
	public static String loadString(long __utfP)
	{
		if (__utfP == 0)
			return null;
		
		// How long is this string?
		int maxChars = Assembly.memReadShort(__utfP, 0) & 0xFFFF;
		char[] chars = new char[maxChars];
		
		// Read all bits of the string, handle chars as needed
		int numChars = 0;
		for (int i = 0, subChar = 0; i < maxChars; i++)
		{
			int a = Assembly.memReadByte(__utfP, 2 + i) & 0xFF;
			
			// Still in sub-char sequence?
			if (subChar > 0)
			{
				// Shift the higher bits in and place in the lower sequence
				chars[numChars] <<= 6;
				chars[numChars] |= (a & 0x3F);
				
				// Done with this char? Move to the next one
				if ((--subChar) == 0)
					numChars++;
				continue;
			}
			
			// Three byte
			if ((a & 0xF0) == 0xE0)
				subChar = 2;
			
			// Two byte
			else if ((a & 0xC0) == 0x80)
				subChar = 1;
			
			// Single byte
			else
				chars[numChars++] = (char)a;
		}
		
		return new String(chars, 0, numChars);
	}
	
	/**
	 * Compares the two strings.
	 * 
	 * @param __utfP The first string.
	 * @param __s The second string.
	 * @return The comparison result.
	 * @since 2021/04/07
	 */
	public static int strCmp(long __utfP, String __s)
	{
		return Objects.compare(
			SummerCoatUtil.loadString(__utfP), __s,
			new NaturalComparator<String>());
	}
	
	/**
	 * Compares the two strings.
	 * 
	 * @param __s The first string.
	 * @param __utfP The second string.
	 * @return The comparison result.
	 * @since 2021/04/07
	 */
	public static int strCmp(String __s, long __utfP)
	{
		return Objects.compare(
			__s, SummerCoatUtil.loadString(__utfP),
			new NaturalComparator<String>());
	}
}
