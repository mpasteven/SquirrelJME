// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.summercoat.ld.mem;

import cc.squirreljme.jvm.Assembly;
import cc.squirreljme.jvm.mle.RuntimeShelf;
import cc.squirreljme.jvm.mle.constants.ByteOrderType;
import cc.squirreljme.jvm.summercoat.lle.LLERuntimeShelf;
import cc.squirreljme.runtime.cldc.debug.Debugging;

/**
 * Access to real memory within the system, this can access a region of memory.
 *
 * @since 2021/02/14
 */
public final class RealMemory
	extends AbstractWritableMemory
{
	/** The native byte order. */
	private static final int _NATIVE_ORDER =
		RuntimeShelf.byteOrder();
	
	/** The base address. */
	private final long baseAddr;
	
	/** The length. */
	private final int length;
	
	/**
	 * Initializes the real memory accessor.
	 * 
	 * @param __baseAddr The base address.
	 * @param __len The number of bytes to access.
	 * @since 2021/02/14
	 */
	public RealMemory(long __baseAddr, int __len)
	{
		// Use the byte order of the system so it matches properly for
		// read/write operations
		this(__baseAddr, __len, LLERuntimeShelf.byteOrder());
	}
	
	/**
	 * Initializes the real memory accessor.
	 * 
	 * @param __baseAddr The base address.
	 * @param __len The number of bytes to access.
	 * @param __byteOrder The {@link ByteOrderType} to use for access.
	 * @since 2021/02/14
	 */
	public RealMemory(long __baseAddr, int __len, int __byteOrder)
	{
		super(__byteOrder);
		
		this.baseAddr = __baseAddr;
		this.length = __len;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/03
	 */
	@Override
	public long absoluteAddress(long __addr)
		throws MemoryAccessException, NotRealMemoryException
	{
		// Should be able to read a single byte from this region
		return this.__check(__addr, 1);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/14
	 */
	@SuppressWarnings("MagicNumber")
	@Override
	public int memReadByte(long __addr)
	{
		return Assembly.memReadByte(
			this.__check(__addr, 1), 0) & 0xFF;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/14
	 */
	@Override
	public MemHandleReference memReadHandle(long __addr)
	{
		int rv = this.memReadInt(__addr);
		return (rv == 0 ? null : new MemHandleReference(rv));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/14
	 */
	@Override
	public int memReadInt(long __addr)
	{
		return this.__valueInt(
			Assembly.memReadInt(this.__check(__addr, 4), 0));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/14
	 */
	@Override
	public long memReadLong(long __addr)
	{
		return this.__valueLong(
			Assembly.memReadLong(this.__check(__addr, 8), 0));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/14
	 */
	@SuppressWarnings("MagicNumber")
	@Override
	public int memReadShort(long __addr)
	{
		return this.__valueShort((short)Assembly.memReadShort(
			this.__check(__addr, 2), 0)) & 0xFFFF;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/14
	 */
	@Override
	public long memRegionSize()
	{
		return this.length;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/14
	 */
	@Override
	public void memWriteByte(long __addr, int __v)
	{
		Assembly.memWriteByte(this.__check(__addr, 1), 0, __v);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/14
	 */
	@Override
	public void memWriteHandle(long __addr, MemHandleReference __v)
	{
		this.memWriteInt(__addr, (__v == null ? 0 : __v.id));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/14
	 */
	@Override
	public void memWriteInt(long __addr, int __v)
	{
		Assembly.memWriteInt(this.__check(__addr, 4), 0,
			this.__valueInt(__v));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/14
	 */
	@Override
	public void memWriteLong(long __addr, long __v)
	{
		Assembly.memWriteLong(this.__check(__addr, 8), 0,
			this.__valueLong(__v));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/14
	 */
	@Override
	public void memWriteShort(long __addr, int __v)
	{
		Assembly.memWriteShort(this.__check(__addr, 2), 0,
			this.__valueShort((short)__v));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/03
	 */
	@Override
	public RealMemory subSection(long __base, long __len)
		throws MemoryAccessException
	{
		// Refers to ourself?
		if (__base == 0 && __len == this.length)
			return this;
		
		// {@squirreljme.error ZZ4t Sub-section would be out of range.}
		if (__base < 0 || __len < 0 || (__base + __len) > this.length ||
			__len > Integer.MAX_VALUE)
			throw new MemoryAccessException(__base, "ZZ4t");
		
		// Just sub-divides real memory
		return new RealMemory(this.baseAddr + __base, (int)__len,
			this.byteOrder);
	}
	
	/**
	 * Checks the address to determine if it is valid.
	 * 
	 * @param __addr The address used.
	 * @param __width The width used.
	 * @return The actual address to read from.
	 * @throws IllegalArgumentException If the width is zero or negative.
	 * @throws MemoryAccessException If the memory could not be read.
	 * @since 2021/02/18
	 */
	private long __check(long __addr, int __width)
	{
		// The width cannot be negative!
		if (__width <= 0)
			throw new IllegalArgumentException("INEG");
		
		// Out of bounds access?
		if (__addr < 0 || (__addr + __width) > this.length)
			throw new MemoryAccessException(__addr);
		
		return this.baseAddr + __addr;
	}
	
	/**
	 * Returns the value correctly byte swapped.
	 * 
	 * @param __v The value to normalize.
	 * @return The normalized value.
	 * @since 2021/02/18
	 */
	private int __valueInt(int __v)
	{
		if (this.byteOrder != RealMemory._NATIVE_ORDER)
			return Integer.reverseBytes(__v);
		return __v;
	}
	
	/**
	 * Returns the value correctly byte swapped.
	 * 
	 * @param __v The value to normalize.
	 * @return The normalized value.
	 * @since 2021/02/18
	 */
	private long __valueLong(long __v)
	{
		if (this.byteOrder != RealMemory._NATIVE_ORDER)
			return Long.reverseBytes(__v);
		return __v;
	}
	
	/**
	 * Returns the value correctly byte swapped.
	 * 
	 * @param __v The value to normalize.
	 * @return The normalized value.
	 * @since 2021/02/18
	 */
	private short __valueShort(short __v)
	{
		if (this.byteOrder != RealMemory._NATIVE_ORDER)
			return Short.reverseBytes(__v);
		return __v;
	}
}
