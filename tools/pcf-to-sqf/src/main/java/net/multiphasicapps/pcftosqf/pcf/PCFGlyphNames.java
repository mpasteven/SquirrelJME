// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.pcftosqf.pcf;

import cc.squirreljme.runtime.cldc.util.SortedTreeMap;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.multiphasicapps.collections.UnmodifiableMap;

/**
 * Glyph name table.
 *
 * @since 2018/11/28
 */
public final class PCFGlyphNames
{
	/** The format. */
	public final int format;
	
	/** The glyph names. */
	public final Map<Integer, String> names;
	
	/**
	 * Initializes the glyph names.
	 *
	 * @param __format The format.
	 * @param __names The names.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/28
	 */
	public PCFGlyphNames(int __format, Map<Integer, String> __names)
		throws NullPointerException
	{
		if (__names == null)
			throw new NullPointerException("NARG");
		
		this.format = __format;
		this.names = UnmodifiableMap.<Integer, String>of(
			new SortedTreeMap<>(__names));
	}
	
	/**
	 * Reads the glyph name data.
	 *
	 * @param __dis The input stream.
	 * @return The resulting glyph names.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/28
	 */
	public static final PCFGlyphNames read(DataInputStream __dis)
		throws IOException, NullPointerException
	{
		if (__dis == null)
			throw new NullPointerException("NARG");
		
		// Format
		int format = Integer.reverseBytes(__dis.readInt());
		
		// Read the string offsets
		int numglyphs = __dis.readInt();
		int[] offsets = new int[numglyphs];
		for (int i = 0; i < numglyphs; i++)
			offsets[i] = __dis.readInt();
		
		// Read the string table
		int stringsize = __dis.readInt();
		byte[] strings = new byte[stringsize];
		__dis.readFully(strings);
		
		// Map strings to indexes
		Map<Integer, String> names = new HashMap<>();
		for (int i = 0; i < numglyphs; i++)
			names.put(i, PCFProperties.__readString(strings, offsets[i]));
		
		// Build
		return new PCFGlyphNames(format, names);
	}
}

