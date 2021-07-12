// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.io;

import cc.squirreljme.runtime.cldc.debug.Debugging;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Represents a future section.
 *
 * @since 2020/12/04
 */
public class ChunkFutureSection
	implements ChunkFuture
{
	/** The kind of section this is. */
	protected final ChunkFutureSectionKind kind;
	
	/** The section this refers to. */
	protected final Reference<ChunkSection> section;
	
	/** The offset for the section. */
	protected final int offset;
	
	/**
	 * Initializes a future section reference.
	 * 
	 * @param __kind The kind of value to use.
	 * @param __section The section to reference.
	 * @param __off The offset to use.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/04
	 */
	public ChunkFutureSection(ChunkFutureSectionKind __kind,
		ChunkSection __section, int __off)
		throws NullPointerException
	{
		if (__kind == null || __section == null)
			throw new NullPointerException("NARG");
		
		this.kind = __kind;
		this.section = new WeakReference<>(__section);
		this.offset = __off;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/12/04
	 */
	@Override
	public int get()
	{
		// {@squirreljme.error BD07 Section was garbage collected.}
		ChunkSection section = this.section.get();
		if (section == null)
			throw new IllegalStateException("BD07");
		
		switch (this.kind)
		{
			case ADDRESS:
				return section._writeAddr + this.offset;
			
			case SIZE:
				return section._writeSize + this.offset;
			
			default:
				throw Debugging.oops();
		}
	}
}
