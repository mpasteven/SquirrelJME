// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.summercoat.ld.mem;

import cc.squirreljme.runtime.cldc.util.NaturalComparator;
import java.util.Objects;

/**
 * This contains the various memory utilities.
 *
 * @since 2021/05/16
 */
public final class MemoryUtils
{
	/**
	 * Not used.
	 * 
	 * @since 2021/05/16
	 */
	private MemoryUtils()
	{
	}
	
	/**
	 * Loads the given UTF string from the given memory.
	 * 
	 * @param __mem The memory to read from.
	 * @param __addr The address into the memory.
	 * @return The loaded UTF string.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/05/16
	 */
	public static String loadString(ReadableMemory __mem, long __addr)
		throws NullPointerException
	{
		if (__mem == null)
			throw new NullPointerException("NARG");
		
		// How long is this string?
		int maxChars = __mem.memReadShort(__addr) & 0xFFFF;
		char[] chars = new char[maxChars];
		
		// Read all bits of the string, handle chars as needed
		int numChars = 0;
		for (int i = 0, subChar = 0; i < maxChars; i++)
		{
			int a = __mem.memReadByte(__addr + 2 + i) & 0xFF;
			
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
	 * @param __s The first string.
	 * @param __addr The second string.
	 * @return The comparison result.
	 * @since 2021/05/16
	 */
	public static int strCmp(String __s,
		ReadableMemory __data, long __addr)
	{
		return Objects.compare(
			__s, MemoryUtils.loadString(__data, __addr),
			new NaturalComparator<String>());
	}
}
