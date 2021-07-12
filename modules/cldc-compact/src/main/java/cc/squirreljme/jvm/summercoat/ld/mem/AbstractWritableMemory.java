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
import cc.squirreljme.runtime.cldc.debug.Debugging;

/**
 * This class is used to provide simple writing for types other than integers
 * into memory.
 *
 * @since 2019/04/21
 */
@SuppressWarnings("MagicNumber")
public abstract class AbstractWritableMemory
	extends AbstractReadableMemory
	implements WritableMemory
{
	/**
	 * Uses big endian for the memory.
	 * 
	 * @since 2021/02/14
	 */
	public AbstractWritableMemory()
	{
	}
	
	/**
	 * Uses the specific byte order for the memory.
	 * 
	 * @param __byteOrder The {@link ByteOrderType} used.
	 * @throws IllegalArgumentException If the byte order is not valid.
	 * @since 2021/02/14
	 */
	public AbstractWritableMemory(int __byteOrder)
		throws IllegalArgumentException
	{
		super(__byteOrder);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 */
	@Override
	public void memWriteBytes(long __addr, byte[] __b, int __o, int __l)
	{
		if (__b == null)
			throw new NullPointerException("NARG");
		if (__o < 0 || __l < 0 || (__o + __l) > __b.length)
			throw new IndexOutOfBoundsException("IOOB");
		
		for (int i = 0; i < __l; i++)
			this.memWriteByte(__addr++, __b[__o++] & 0xFF);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/01/17
	 */
	@Override
	public void memWriteHandle(long __addr, MemHandleReference __v)
	{
		this.memWriteInt(__addr, (__v == null ? 0 : __v.id));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 */
	@Override
	public void memWriteInt(long __addr, int __v)
	{
		switch (this.byteOrder)
		{
			case ByteOrderType.BIG_ENDIAN:
				this.memWriteByte(__addr++, __v >>> 24);
				this.memWriteByte(__addr++, __v >>> 16);
				this.memWriteByte(__addr++, __v >>> 8);
				this.memWriteByte(__addr, __v);
				break;
			
			default:
				throw Debugging.oops();
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/01/17
	 */
	@Override
	public void memWriteLong(long __addr, long __v)
	{
		switch (this.byteOrder)
		{
			case ByteOrderType.BIG_ENDIAN:
				this.memWriteByte(__addr++, (byte)(__v >>> 56));
				this.memWriteByte(__addr++, (byte)(__v >>> 48));
				this.memWriteByte(__addr++, (byte)(__v >>> 40));
				this.memWriteByte(__addr++, (byte)(__v >>> 32));
				this.memWriteByte(__addr++, (byte)(__v >>> 24));
				this.memWriteByte(__addr++, (byte)(__v >>> 16));
				this.memWriteByte(__addr++, (byte)(__v >>> 8));
				this.memWriteByte(__addr, (byte)(__v));
				break;
			
			default:
				throw Debugging.oops();
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 */
	@Override
	public void memWriteShort(long __addr, int __v)
	{
		switch (this.byteOrder)
		{
			case ByteOrderType.BIG_ENDIAN:
				this.memWriteByte(__addr++, __v >>> 8);
				this.memWriteByte(__addr, __v);
				break;
			
			default:
				throw Debugging.oops();
		}
	}
	
	/**
	 * Opens an output stream for this memory stream.
	 * 
	 * @return The output stream.
	 * @since 2021/02/19
	 */
	public final WritableMemoryOutputStream outputStream()
	{
		return new WritableMemoryOutputStream(this);
	}
	
	/**
	 * Opens an output stream for this memory stream.
	 * 
	 * @param __addr The address to read from.
	 * @param __len The length to use.
	 * @return The output stream.
	 * @since 2021/02/19
	 */
	public final WritableMemoryOutputStream outputStream(long __addr,
		long __len)
	{
		return new WritableMemoryOutputStream(this, __addr, __len);
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
		
		// {@squirreljme.error ZZ4d Sub-section would be out of range of
		// this memory region. (The base address; The length)}
		if (__base < 0 || __len < 0 || (__base + __len) > this.memRegionSize())
			throw new MemoryAccessException(__base,
				"ZZ4d " + __base + " " + __len);
		
		return new __WritableSubSection__(this, __base, __len);
	}
}

