// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.mini;

import dev.shadowtail.classfile.pool.DualClassRuntimePoolBuilder;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.multiphasicapps.io.ChunkSection;
import net.multiphasicapps.io.ChunkWriter;

/**
 * Stores temporary field data as it is processed.
 *
 * @since 2019/03/11
 */
final class __TempFields__
{
	/** The fields in the table. */
	final List<MinimizedField> _fields =
		new ArrayList<>();
	
	/** The number of fields in the table. */
	int _count;
	
	/** The current byte size of the field table. */
	int _bytes;
	
	/** The total number of objects in the table. */
	int _objects;
	
	/**
	 * Returns the byte representation of the data here.
	 *
	 * @param __pool The constant pool.
	 * @return The byte data representation.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/14
	 */
	public final byte[] getBytes(DualClassRuntimePoolBuilder __pool)
		throws NullPointerException
	{
		if (__pool == null)
			throw new NullPointerException("NARG");
		
		// The resulting table
		ChunkWriter output = new ChunkWriter();
		
		// Write
		try
		{
			// Table of contents data
			ChunkSection toc = output.addSection();
			
			// Actual table data
			ByteArrayOutputStream dbytes = new ByteArrayOutputStream();
			DataOutputStream ddos = new DataOutputStream(dbytes);
			
			// Write field information
			for (MinimizedField m : this._fields)
			{
				// 16-bytes
				toc.writeInt(m.flags);
				toc.writeUnsignedShortChecked(m.offset);
				toc.writeUnsignedShortChecked(m.size);
				toc.writeUnsignedShortChecked(
					__pool.add(false, m.name.toString()).index);
				toc.writeUnsignedShortChecked(
					__pool.add(false, m.type.className()).index);
				toc.writeUnsignedShortChecked((m.value == null ? 0 :
					__pool.add(false, m.value).index));
				toc.writeByte(m.datatype.ordinal());
				toc.writeByte(0);
			}
			
			// Write end of table
			toc.writeInt(0xFFFFFFFF);
			
			// Build output data
			return output.toByteArray();
		}
		
		// Should not occur
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}

