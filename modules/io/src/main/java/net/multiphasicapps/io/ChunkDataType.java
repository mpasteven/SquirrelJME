// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.io;

/**
 * The type of data to write in.
 *
 * @since 2019/08/17
 */
public enum ChunkDataType
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
	
	/** The number of bytes used for the data type. */
	public final int numBytes;
	
	/**
	 * Initializes the data type information.
	 * 
	 * @param __numBytes The number of bytes to use.
	 * @since 2020/12/04
	 */
	ChunkDataType(int __numBytes)
	{
		this.numBytes = __numBytes;
	}
}

