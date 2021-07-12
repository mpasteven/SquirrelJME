// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.jarfile;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Table of contents used for JARs and PACKfiles.
 *
 * @param <H> The header type used.
 * @since 2020/12/13
 */
public final class TableOfContents<H>
{
	/** The type this header is for. */
	protected final Class<H> forType;
	
	/** The field count. */
	protected final int count;
	
	/** The field span. */
	protected final int span;
	
	/** The field data. */
	private final int[] _fields;
	
	/**
	 * Initializes the base Table of Contents
	 * 
	 * @param __for The class this is for.
	 * @param __count The count used.
	 * @param __span The span used.
	 * @param __fields The field data.
	 * @throws IllegalArgumentException If these are not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/13
	 */
	public TableOfContents(Class<H> __for, int __count, int __span,
		int... __fields)
		throws IllegalArgumentException, NullPointerException
	{
		if (__for == null || __fields == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error BC0a Negative count or span. (The count;
		// The span)}
		if (__count < 0 || __span < 0)
			throw new IllegalArgumentException(String.format("BC0a %d %d",
				__count, __span));
		
		// {@squirreljme.error BC0h Incorrectly sized fields array.
		// (The count; The span; The field array length)}
		if ((__count * __span) != __fields.length)
			throw new IllegalArgumentException(String.format("BC0h %d %d %d",
				__count, __span, __fields.length));
		
		this.forType = __for;
		this.count = __count;
		this.span = __span;
		this._fields = __fields.clone();
	}
	
	/**
	 * Reads the value of the given table of contents property.
	 * 
	 * @param __dx The index to read from.
	 * @param __prop The property to read.
	 * @return The value of the given property.
	 * @throws IllegalArgumentException If the property or property is
	 * not valid.
	 * @throws IndexOutOfBoundsException If the index is not valid.
	 * @since 2020/12/13
	 */
	public final int get(int __dx, int __prop)
		throws IllegalArgumentException, IndexOutOfBoundsException
	{
		if (__dx < 0 || __dx > this.count)
			throw new IndexOutOfBoundsException("IOOB i" + __dx);
			
		int span = this.span;
		if (__prop < 0 || __prop >= span)
			throw new IndexOutOfBoundsException("IOOB p" + __prop);
		
		return this._fields[(__dx * span) + __prop];
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/12/13
	 */
	@Override
	public final String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append('{');
		for (int dx = 0, count = this.count; dx < count; dx++)
		{
			if (dx > 0)
				sb.append(", ");
			
			sb.append('[');
			for (int p = 0, span = this.span; p < span; p++)
			{
				if (p > 0)
					sb.append(", ");
				
				sb.append(this.get(dx, p));
			}
			sb.append(']');
		}
		sb.append('}');
		
		return sb.toString();
	}
	
	/**
	 * Decodes the pack file table of contents.
	 * 
	 * @param __cl The class type used.
	 * @param __in The stream to read from.
	 * @return The read table of contents.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/09
	 */
	public static <H> TableOfContents<H> decode(Class<H> __cl,
		InputStream __in)
		throws IOException, NullPointerException
	{
		if (__cl == null || __in == null)
			throw new NullPointerException("NARG");
		
		@SuppressWarnings("resource")
		DataInputStream in = new DataInputStream(__in);
		
		// Read the count and span of entries
		int count = in.readUnsignedShort(); 
		int span = in.readUnsignedShort();
		
		// All entries have the same fixed number of fields
		int numFields = count * span;
		int[] fields = new int[numFields];
		
		// Read all of them in
		for (int i = 0; i < numFields; i++)
			fields[i] = in.readInt(); 
		
		return new TableOfContents<H>(__cl, count, span, fields);
	}
}
