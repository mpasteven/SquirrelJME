// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.io;

import java.lang.ref.WeakReference;

/**
 * Not Described.
 *
 * @since 2020/12/05
 */
public class ChunkFutureChunk
	implements ChunkFuture
{
	/** The chunk to refer to. */
	private final WeakReference<ChunkWriter> chunk;
	
	/**
	 * Initializes the future chunk reference.
	 * 
	 * @param __chunk The chunk to reference.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/05
	 */
	public ChunkFutureChunk(ChunkWriter __chunk)
		throws NullPointerException
	{
		if (__chunk == null)
			throw new NullPointerException("NARG"); 
		
		this.chunk = new WeakReference<>(__chunk);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/12/05
	 */
	@Override
	public int get()
	{
		// {@squirreljme.error BD06 The chunk was garbage collected.}
		ChunkWriter chunk = this.chunk.get();
		if (chunk == null)
			throw new IllegalStateException("BD06");
		
		return chunk.fileSize();
	}
}
