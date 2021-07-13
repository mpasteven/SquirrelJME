// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.summercoat.ld.mem;

import cc.squirreljme.runtime.cldc.debug.Debugging;

/**
 * A writable sub-section of memory.
 *
 * @since 2021/04/03
 */
class __WritableSubSection__
	extends __ReadableSubSection__
	implements WritableMemory
{
	/** Writable memory. */
	protected final WritableMemory writable;
	
	/**
	 * Initializes the sub-section.
	 * 
	 * @param __basis The memory to access.
	 * @param __base The base address.
	 * @param __len The length of the region to access.
	 * @throws IndexOutOfBoundsException If the base and/or length are
	 * negative.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/04/03
	 */
	__WritableSubSection__(WritableMemory __basis, long __base,
		long __len)
	{
		super(__basis, __base, __len);
		
		this.writable = __basis;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/03
	 */
	@Override
	public void memWriteByte(long __addr, int __v)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/03
	 */
	@Override
	public void memWriteBytes(long __addr, byte[] __b, int __o, int __l)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/03
	 */
	@Override
	public void memWriteHandle(long __addr, MemHandleReference __v)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/03
	 */
	@Override
	public void memWriteInt(long __addr, int __v)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/03
	 */
	@Override
	public void memWriteLong(long __addr, long __v)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/03
	 */
	@Override
	public void memWriteShort(long __addr, int __v)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/03
	 */
	@Override
	public WritableMemory subSection(long __base, long __len)
		throws MemoryAccessException
	{
		// Refers to ourself?
		if (__base == 0 && __len == this.memRegionSize())
			return this;
		
		// {@squirreljme.error ZZ4r Sub-section would be out of range of
		// this memory region. (The base address; The length)}
		if (__base < 0 || __len < 0 || (__base + __len) > this.memRegionSize())
			throw new MemoryAccessException(__base,
				"ZZ4r " + __base + " " + __len);
		
		return this.writable.subSection(this.base + __base, __len);
	}
}
