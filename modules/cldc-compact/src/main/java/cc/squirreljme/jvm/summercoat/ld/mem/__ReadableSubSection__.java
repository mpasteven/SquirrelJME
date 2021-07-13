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
 * Readable sub-section of memory.
 *
 * @since 2021/04/03
 */
class __ReadableSubSection__
	implements ReadableMemory
{
	/** Readable memory. */
	protected final ReadableMemory readable;
	
	/** The base memory address. */
	protected final long base;
	
	/** The length of memory to access. */
	protected final long length;
	
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
	__ReadableSubSection__(ReadableMemory __basis, long __base,
		long __len)
		throws IndexOutOfBoundsException, NullPointerException
	{
		if (__basis == null)
			throw new NullPointerException("NARG");
		if (__base < 0 || __len < 0)
			throw new IndexOutOfBoundsException("IOOB");
		
		this.readable = __basis;
		this.base = __base;
		this.length = __len;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/03
	 */
	@Override
	public final long absoluteAddress(long __addr)
		throws MemoryAccessException, NotRealMemoryException
	{
		// {@squirreljme.error ZZ4s Address is out of range of the section.}
		if (__addr < 0 || __addr >= this.length)
			throw new MemoryAccessException(__addr, "ZZ4s");
		
		return this.readable.absoluteAddress(this.base + __addr);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/03
	 */
	@Override
	public int memReadByte(long __addr)
	{
		return this.readable.memReadByte(this.__check(__addr, 1));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/03
	 */
	@Override
	public void memReadBytes(long __addr, byte[] __b, int __o, int __l)
		throws IndexOutOfBoundsException, NullPointerException
	{
		if (__b == null)
			throw new NullPointerException("NARG");
		if (__o < 0 || __l < 0 || (__o + __l) > __b.length)
			throw new IndexOutOfBoundsException("IOOB");
		
		// Nothing to read?
		if (__l == 0)
			return;
		
		// Perform the actual read
		this.readable.memReadBytes(this.__check(__addr, __l),
			__b, __o, __l);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/03
	 */
	@Override
	public MemHandleReference memReadHandle(long __addr)
	{
		return new MemHandleReference(this.memReadInt(__addr));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/03
	 */
	@Override
	public int memReadInt(long __addr)
	{
		return this.readable.memReadInt(this.__check(__addr, 4));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/03
	 */
	@Override
	public long memReadLong(long __addr)
	{
		return this.readable.memReadLong(this.__check(__addr, 8));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/03
	 */
	@Override
	public int memReadShort(long __addr)
	{
		return this.readable.memReadShort(this.__check(__addr, 2));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/03
	 */
	@Override
	public final long memRegionSize()
	{
		return this.length;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/03
	 */
	@Override
	public ReadableMemory subSection(long __base, long __len)
		throws MemoryAccessException
	{
		// Refers to ourself?
		if (__base == 0 && __len == this.memRegionSize())
			return this;
		
		// {@squirreljme.error ZZ4q Sub-section would be out of range of
		// this memory region. (The base address; The length)}
		if (__base < 0 || __len < 0 || (__base + __len) > this.memRegionSize())
			throw new MemoryAccessException(__base,
				"ZZ4q " + __base + " " + __len);
		
		return this.readable.subSection(this.base + __base, __len);
	}
	
	/**
	 * Checks that the read is valid.
	 * 
	 * @param __addr The address to read from.
	 * @param __len The number of bytes to read.
	 * @return The address to truly read from.
	 * @since 2021/05/16
	 */
	private long __check(long __addr, int __len)
	{
		// {@squirreljme.error ZZ5a Access out of bounds. (The address;
		// The requested length; The memory length)}
		if (__addr < 0 || __len < 0 || __addr + __len > this.length)
			throw new MemoryAccessException(__addr, String.format(
				"ZZ5a %d + %d in %d", __addr, __len, this.length));
		
		return this.base + __addr;
	}
}
