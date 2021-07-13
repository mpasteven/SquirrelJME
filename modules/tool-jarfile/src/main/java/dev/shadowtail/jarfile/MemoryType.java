// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.jarfile;

import cc.squirreljme.runtime.cldc.debug.Debugging;
import dev.shadowtail.classfile.xlate.DataType;
import net.multiphasicapps.io.ChunkDataType;

/**
 * The memory type used.
 *
 * @since 2021/01/10
 */
public enum MemoryType
{
	/** Byte. */
	BYTE(1),
	
	/** Short. */
	SHORT(2),
	
	/** Integer. */
	INTEGER(4),
	
	/** Long. */
	LONG(8),
	
	/* End. */
	;
	
	/** The number of bytes that make up the type. */
	public final int byteCount;
	
	/** The last address to read off. */
	public final int lastOffset;
	
	/**
	 * Initializes the constant.
	 * 
	 * @param __size The size of the type.
	 * @since 2021/01/10
	 */
	MemoryType(int __size)
	{
		this.byteCount = __size;
		this.lastOffset = __size - 1;
	}
	
	/**
	 * Returns the chunk data type used for this data.
	 * 
	 * @return The chunk data type used.
	 * @since 2021/01/17
	 */
	protected ChunkDataType toChunkDataType()
	{
		switch (this)
		{
			case BYTE:		return ChunkDataType.BYTE;
			case SHORT:		return ChunkDataType.SHORT;
			case INTEGER:	return ChunkDataType.INTEGER;
			case LONG:		return ChunkDataType.LONG;
			
			default:
				throw Debugging.oops(this);
		}
	}
	
	/**
	 * Returns the memory type that is used to represent a value.
	 * 
	 * @param __dataType The data type.
	 * @return The memory type of the data type.
	 * @since 2021/01/12
	 */
	protected static MemoryType of(DataType __dataType)
	{
		switch (__dataType)
		{
			case BYTE:		return MemoryType.BYTE;
			
			case CHARACTER:
			case SHORT:		return MemoryType.SHORT;
			
			case OBJECT:
			case INTEGER:
			case FLOAT:		return MemoryType.INTEGER;
			
			case LONG:
			case DOUBLE:	return MemoryType.LONG;
			
			default:
				throw Debugging.oops();
		}
	}
}
