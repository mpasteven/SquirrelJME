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

import cc.squirreljme.runtime.cldc.util.SortedTreeSet;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This represents the bitmap from within a PCF file.
 *
 * @since 2018/11/28
 */
public final class PCFBitmap
{
	/** The format. */
	public final int format;
	
	/** The number of glyphs. */
	public final int numglyphs;
	
	/** The glyph map. */
	public final Map<Integer, PCFGlyphMap> glyphmaps;
	
	/**
	 * Initializes the bitmap data.
	 *
	 * @param __format The format used.
	 * @param __numglyphs The glyphs used.
	 * @param __offsets The offsets.
	 * @param __data The data.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/28
	 */
	public PCFBitmap(int __format, int __numglyphs, int[] __offsets,
		byte[] __data)
		throws NullPointerException
	{
		if (__offsets == null || __data == null)
			throw new NullPointerException("NARG");
		
		this.format = __format;
		this.numglyphs = __numglyphs;
		
		// This class is used to sort all of the offsets but still keep the
		// original indexes
		final class __Index__
			implements Comparable<__Index__>
		{
			/** The offset. */
			public final int offset;
			
			/** The index. */
			public final int index;
			
			/**
			 * Initializes the index.
			 *
			 * @param __o The offset.
			 * @param __i The index.
			 * @since 2018/11/29
			 */
			__Index__(int __o, int __i)
			{
				this.offset = __o;
				this.index = __i;
			}
			
			/**
			 * {@inheritDoc}
			 * @since 2018/11/29
			 */
			@Override
			public int compareTo(__Index__ __o)
			{
				// Sort by the offset first, but it is possible that some
				// glyphs might share the same offset, so do not lose them!
				int rv = this.offset - __o.offset;
				if (rv != 0)
					return rv;
				return this.index - __o.index;
			}
		}
		
		// Go through all the offsets and determine all of the bounds
		Set<__Index__> index = new SortedTreeSet<>();
		for (int i = 0; i < this.numglyphs; i++)
			index.add(new __Index__(__offsets[i], i));
		
		// Need to determine the bounds for every glyph to extract the data
		// for it, so mark when an index ends and such. So go through
		// and mark where a new glyph starts.
		int datalen = __data.length;
		boolean[] boops = new boolean[datalen];
		for (__Index__ i : index)
			boops[i.offset] = true;
		
		// Build the bitmaps for each map of glyphs so they can be converted
		// accordingly
		Map<Integer, PCFGlyphMap> glyphmaps = new HashMap<>();
		for (__Index__ i : index)
		{
			// Start with a dynamic set of copied byte data
			List<Byte> bm = new ArrayList<>();
			
			// Add all the bits that make up the glyph data there
			for (int o = i.offset; o < datalen;)
			{
				// Add this byte always
				bm.add(__data[o++]);
				
				// Since we are at the next index check for the end or if
				// it signaled a new glyph here
				if (o >= datalen || boops[o])
					break;
			}
			
			// Store the glyph data
			glyphmaps.put(i.index, new PCFGlyphMap(bm));
		}
		
		this.glyphmaps = glyphmaps;
	}
	
	/**
	 * Reads the bitmap data.
	 *
	 * @param __dis The input stream.
	 * @return The resulting bitmap.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/28
	 */
	public static final PCFBitmap read(DataInputStream __dis)
		throws IOException, NullPointerException
	{
		if (__dis == null)
			throw new NullPointerException("NARG");
		
		// Format
		int format = Integer.reverseBytes(__dis.readInt());
		
		// Read the glyph offsets
		int numglyphs = __dis.readInt();
		int[] offsets = new int[numglyphs];
		for (int i = 0; i < numglyphs; i++)
			offsets[i] = __dis.readInt();
		
		// Read all four bitmap sizes
		int[] bitmapsizes = new int[4];
		for (int i = 0; i < 4; i++)
			bitmapsizes[i] = __dis.readInt();
		
		// The bytes depends on the format
		byte[] data = new byte[bitmapsizes[format & 3]];
		__dis.readFully(data);
		
		// Build bitmap
		return new PCFBitmap(format, numglyphs, offsets, data);
	}
}

