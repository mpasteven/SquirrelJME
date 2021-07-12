// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.summercoat.ld.mem;

import cc.squirreljme.jvm.mle.constants.ByteOrderType;

/**
 * This is a region of memory which is backed by a byte array, this may or
 * may not be writable.
 *
 * @since 2019/04/21
 */
public final class ByteArrayMemory
	extends AbstractWritableMemory
{
	/** The offset to this address. */
	protected final int offset;
	
	/** The size of the byte array. */
	protected final int size;
	
	/** Offset into the byte array. */
	protected final int arrayOff;
	
	/** Allow writing into this memory? */
	protected final boolean allowWrites;
	
	/** The backing byte array. */
	private final byte[] _bytes;
	
	/**
	 * Initializes the byte array memory.
	 *
	 * @param __mo The memory offset.
	 * @param __b The memory bytes.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/21
	 */
	public ByteArrayMemory(int __mo, byte[] __b)
		throws NullPointerException
	{
		this(__mo, __b, 0, __b.length);
	}
	
	/**
	 * Initializes the byte array memory.
	 *
	 * @param __mo The memory offset.
	 * @param __b The memory bytes.
	 * @param __allowWrites Allow writing to this memory.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/02/14
	 */
	public ByteArrayMemory(int __mo, byte[] __b, boolean __allowWrites)
		throws NullPointerException
	{
		this(__mo, __b, 0, __b.length, __allowWrites);
	}
	
	/**
	 * Initializes the byte array memory.
	 *
	 * @param __mo The memory offset.
	 * @param __b The memory bytes.
	 * @param __o The array offset.
	 * @param __l The number of bytes to access.
	 * @throws IndexOutOfBoundsException If the byte array offset and/or
	 * length exceeds the array bounds.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/21
	 */
	public ByteArrayMemory(int __mo, byte[] __b, int __o, int __l)
		throws IndexOutOfBoundsException, NullPointerException
	{
		this(__mo, __b, __o, __l, false);
	}
	
	/**
	 * Initializes the byte array memory.
	 *
	 * @param __mo The memory offset.
	 * @param __b The memory bytes.
	 * @param __o The array offset.
	 * @param __l The number of bytes to access.
	 * @param __allowWrites Allow writing to this memory.
	 * @throws IndexOutOfBoundsException If the byte array offset and/or
	 * length exceeds the array bounds.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/02/14
	 */
	public ByteArrayMemory(int __mo,
		byte[] __b, int __o, int __l, boolean __allowWrites)
		throws IndexOutOfBoundsException, NullPointerException
	{
		super(ByteOrderType.BIG_ENDIAN);
		
		if (__b == null)
			throw new NullPointerException("NARG");
		if (__o < 0 || __l < 0 || (__o + __l) > __b.length)
			throw new IndexOutOfBoundsException("IOOB");
		
		this.offset = __mo;
		this._bytes = __b;
		this.arrayOff = __o;
		this.size = __l;
		this.allowWrites = __allowWrites;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 */
	@Override
	public int memReadByte(long __addr)
	{
		// Check if this access is valid or not
		this.__check(__addr, false, 1);
		
		return this._bytes[(int)(this.arrayOff + __addr)] & 0xFF;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 */
	@Override
	public void memReadBytes(long __addr, byte[] __b, int __o, int __l)
		throws IndexOutOfBoundsException, NullPointerException
	{
		if (__b == null)
			throw new NullPointerException("NARG");
		if (__o < 0 || __l < 0 || (__o + __l) > __b.length)
			throw new IndexOutOfBoundsException("IOOB");
		
		// Check read
		this.__check(__addr, false, __l);
		
		// Copy data quickly using acceleration!
		System.arraycopy(this._bytes, (int)(this.arrayOff + __addr),
			__b, __o, __l);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 */
	@SuppressWarnings("MagicNumber")
	@Override
	public int memReadInt(long __addr)
	{
		// Check if this access is valid or not
		this.__check(__addr, false, 4);
		
		byte[] bytes = this._bytes;
		int rp = (int)(this.arrayOff + __addr);
		return ((bytes[rp++] & 0xFF) << 24) |
			((bytes[rp++] & 0xFF) << 16) |
			((bytes[rp++] & 0xFF) << 8) |
			(bytes[rp] & 0xFF);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 */
	@SuppressWarnings("MagicNumber")
	@Override
	public int memReadShort(long __addr)
	{
		// Check if this access is valid or not
		this.__check(__addr, false, 2);
		
		byte[] bytes = this._bytes;
		int rp = (int)(this.arrayOff + __addr);
		return (short)(((bytes[rp++] & 0xFF) << 8) |
			(bytes[rp] & 0xFF));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 * @return
	 */
	@Override
	public final long memRegionSize()
	{
		return this.size;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 */
	@Override
	public void memWriteByte(long __addr, int __v)
	{
		// Check if this access is valid or not
		this.__check(__addr, true, 1);
		
		int wp = (int)(this.arrayOff + __addr);
		this._bytes[wp] = (byte)(__v);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/14
	 */
	@Override
	public void memWriteBytes(long __addr, byte[] __b, int __o, int __l)
		throws IndexOutOfBoundsException, NullPointerException
	{
		if (__b == null)
			throw new NullPointerException("NARG");
		if (__o < 0 || __l < 0 || (__o + __l) > __b.length)
			throw new IndexOutOfBoundsException("IOOB");
		
		// Check read
		this.__check(__addr, true, __l);
		
		// Copy data quickly using acceleration!
		System.arraycopy(__b, __o,
			this._bytes, (int)(this.arrayOff + __addr), __l);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 */
	@SuppressWarnings("MagicNumber")
	@Override
	public final void memWriteInt(long __addr, int __v)
	{
		// Check if this access is valid or not
		this.__check(__addr, true, 4);
		
		byte[] bytes = this._bytes;
		int wp = (int)(this.arrayOff + __addr);
		bytes[wp++] = (byte)(__v >>> 24);
		bytes[wp++] = (byte)(__v >>> 16);
		bytes[wp++] = (byte)(__v >>> 8);
		bytes[wp] = (byte)(__v);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 */
	@Override
	public final void memWriteShort(long __addr, int __v)
	{
		// Check if this access is valid or not
		this.__check(__addr, true, 2);
		
		byte[] bytes = this._bytes;
		int wp = (int)(this.arrayOff + __addr);
		bytes[wp++] = (byte)(__v >>> 8);
		bytes[wp] = (byte)(__v);
	}
	
	/**
	 * Checks if the given address can be read from or written to.
	 * 
	 * @param __addr The address to write to.
	 * @param __write If this is being written to.
	 * @param __len The number of bytes to write.
	 * @throws MemoryAccessException If the memory access is invalid.
	 * @since 2021/02/14
	 */
	private void __check(long __addr, boolean __write, int __len)
		throws MemoryAccessException
	{
		// Not able to write to ROM memory areas
		if (__write && !this.allowWrites)
			throw new MemoryAccessException(__addr);
		
		// {@squirreljme.error ZZ58 Memory access out of bounds. (The address;
		// the length; the size.})
		if (__addr < 0 || __addr > Integer.MAX_VALUE ||
			(__addr + __len) > this.size)
			throw new MemoryAccessException(__addr, 
				String.format("ZZ58 %d + %d in %d",
					__addr, __len, this.size));
		
		// {@squirreljme.error ZZ57 Unaligned byte array access.}
		if ((__addr % __len) != 0)
			throw new MemoryAccessException(__addr, "ZZ57");
	}
}

