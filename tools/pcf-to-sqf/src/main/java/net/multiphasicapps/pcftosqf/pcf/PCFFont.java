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
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.multiphasicapps.collections.UnmodifiableList;

/**
 * This class is capable of reading PCF formatted fonts and reading all of
 * the information from it.
 *
 * Information on the font is at these locations, looking them up on
 * archive.org:
 * * https://fontforge.github.io/en-US/documentation/reference/pcf-format/
 * * http://myhome.hananet.net:80/~bumchul/xfont/pcf.txt
 * * http://www.tsg.ne.jp:80/GANA/S/pcf2bdf/pcf.pdf
 *
 * @since 2018/11/25
 */
public class PCFFont
{
	/** Properties. */
	public final PCFProperties properties;
	
	/** Accelerators. */
	public final PCFAccelerators accelerators;
	
	/** Metrics. */
	public final List<PCFMetric> metrics;
	
	/** Bitmap. */
	public final PCFBitmap bitmap;
	
	/** Encoding. */
	public final PCFEncoding encoding;
	
	/** Scalable widths. */
	public final PCFScalableWidths scalablewidths;
	
	/** The glyph names. */
	public final PCFGlyphNames glyphnames;
	
	/**
	 * Initializes the font.
	 *
	 * @param __properties 
	 * @param __accelerators 
	 * @param __metrics 
	 * @param __bitmap 
	 * @param __encoding 
	 * @param __scalablewidths 
	 * @param __glyphnames 
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/28
	 */
	public PCFFont(PCFProperties __properties, PCFAccelerators __accelerators,
		PCFMetric[] __metrics, PCFBitmap __bitmap, PCFEncoding __encoding,
		PCFScalableWidths __scalablewidths, PCFGlyphNames __glyphnames)
		throws NullPointerException
	{
		if (__properties == null || __accelerators == null ||
			__metrics == null || __bitmap == null || __encoding == null ||
			__scalablewidths == null || __glyphnames == null)
			throw new NullPointerException("NARG");
		
		this.properties = __properties;
		this.accelerators = __accelerators;
		this.metrics = UnmodifiableList.<PCFMetric>of(
			Arrays.<PCFMetric>asList(__metrics.clone()));
		this.bitmap = __bitmap;
		this.encoding = __encoding;
		this.scalablewidths = __scalablewidths;
		this.glyphnames = __glyphnames;
	}
	
	/**
	 * Reads the given stream for PCF font information.
	 *
	 * @param __in The stream to read from.
	 * @return The decoded font information.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/25
	 */
	public static final PCFFont read(InputStream __in)
		throws IOException, NullPointerException
	{
		if (__in == null)
			throw new NullPointerException("NARG");
		
		// Need to read in the data!
		DataInputStream dos = new DataInputStream(__in);
		
		// {@squirreljme.error AP01 Invalid PCF magic number.}
		int magic;
		if ((magic = dos.readInt()) != 0x01666370)
			throw new IOException(String.format("AP01 %08x", magic));
		
		// Read each table entry, since they have offsets into the file they
		// could be in any random order which would be bad
		Set<PCFTableEntry> tables = new SortedTreeSet<>();
		int numtables = Integer.reverseBytes(dos.readInt());
		for (int i = 0; i < numtables; i++)
			tables.add(new PCFTableEntry(
				Integer.reverseBytes(dos.readInt()),
				Integer.reverseBytes(dos.readInt()),
				Integer.reverseBytes(dos.readInt()),
				Integer.reverseBytes(dos.readInt())));
		
		// Determine the base position of the read pointer
		int readptr = 8 + (numtables * 16);
		
		// Debug
		todo.DEBUG.note("Table: %s, ended at %d", tables, readptr);
		
		// Parsed fields
		PCFProperties pcfp = null;
		PCFAccelerators pcfaccel = null;
		PCFMetric[] metrics = null;
		PCFBitmap bitmap = null;
		PCFEncoding encoding = null;
		PCFScalableWidths scalablewidths = null;
		PCFGlyphNames glyphnames = null;
		
		// Go through all table entries and parse them, they will be sorted
		// by their offset and handled as such
		boolean waseofing = false;
		for (PCFTableEntry te : tables)
		{
			// {@squirreljme.error AP02 Expected EOF to occur on the last
			// entry, this likely means the file was truncated more than
			// what was expected.}
			if (waseofing)
				throw new IOException("AP02");
			
			// Skip bytes needed to reach the destination
			int skippy = te.offset - readptr;
			if (skippy > 0)
				dos.skipBytes(skippy);
			
			// {@squirreljme.error AP03 Negative skip distance.}
			else if (skippy < 0)
				throw new IOException("AP03");
			
			// Debug
			todo.DEBUG.note("Read entry %s", te);
			
			// Read in data that makes up this section, but the entries could
			// be clipped short and have a size larger than the file. So just
			// assume zero padding is used and hope it works
			int tesize;
			byte[] data = new byte[(tesize = te.size)];
			for (int i = 0; i < tesize; i++)
			{
				int rc = dos.read();
				
				// Make sure this is the last entry on EOF!
				if (rc < 0)
				{
					waseofing = true;
					break;
				}
				
				data[i] = (byte)rc;
			}
			
			// Handle the data in the section
			switch (te.type)
			{
					// Properties
				case 1:
					try (DataInputStream dis = new DataInputStream(
						new ByteArrayInputStream(data)))
					{
						pcfp = PCFProperties.read(dis);
					}
					break;
					
					// Accelerators
				case 2:
					try (DataInputStream dis = new DataInputStream(
						new ByteArrayInputStream(data)))
					{
						pcfaccel = PCFAccelerators.read(dis);
					}
					break;
					
					// Metrics
				case 4:
					try (DataInputStream dis = new DataInputStream(
						new ByteArrayInputStream(data)))
					{
						metrics = PCFMetric.readMetrics(dis);
					}
					break;
					
					// Bitmaps
				case 8:
					try (DataInputStream dis = new DataInputStream(
						new ByteArrayInputStream(data)))
					{
						bitmap = PCFBitmap.read(dis);
					}
					break;
					
					// Ink Metrics (NOT USED???)
				case 16:
					if (true)
						throw new todo.TODO();
					break;
					
					// BDF Encodings
				case 32:
					try (DataInputStream dis = new DataInputStream(
						new ByteArrayInputStream(data)))
					{
						encoding = PCFEncoding.read(dis);
					}
					break;
					
					// SWidths
				case 64:
					try (DataInputStream dis = new DataInputStream(
						new ByteArrayInputStream(data)))
					{
						scalablewidths = PCFScalableWidths.read(dis);
					}
					break;
					
					// Glyph Names
				case 128:
					try (DataInputStream dis = new DataInputStream(
						new ByteArrayInputStream(data)))
					{
						glyphnames = PCFGlyphNames.read(dis); 
					}
					break;
					
					// BDF Accelerators
				case 256:
					// Debug
					todo.DEBUG.note("Ignoring BDF Accelerators");
					break;
					
					// {@squirreljme.error AP04 Unknown PCF type. (The type)}
				default:
					throw new IOException("AP04 " + te.type);
			}
			
			// Set pointer for next run
			readptr += te.size;
		}
		
		// Build font
		return new PCFFont(pcfp, pcfaccel, metrics, bitmap,
			encoding, scalablewidths, glyphnames);
	}
}

