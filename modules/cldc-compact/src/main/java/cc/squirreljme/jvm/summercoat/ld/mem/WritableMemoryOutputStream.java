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
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This is an output stream which can directly write to memory areas.
 * 
 * For anything from {@link DataOutput} the proper type based methods are used,
 * as such if there is a specific byte order that is used it will be
 * derived from this.
 *
 * @since 2019/06/14
 */
public final class WritableMemoryOutputStream
	extends OutputStream
	implements DataOutput, MemoryStream
{
	/** The base write address. */
	protected final long address;
	
	/** The number of bytes that can be written. */
	protected final long length;
	
	/** The output memory. */
	protected final WritableMemory memory;
	
	/** The current write offset. */
	private int _at;
	
	/**
	 * Initializes the stream, the byte order is big endian.
	 *
	 * @param __mem The memory.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/02/09
	 */
	public WritableMemoryOutputStream(WritableMemory __mem)
		throws NullPointerException
	{
		this(__mem, 0, __mem.memRegionSize());
	}
	
	/**
	 * Initializes the stream, the byte order is big endian.
	 *
	 * @param __mem The memory.
	 * @param __addr The start address.
	 * @param __len The length.
	 * @throws IllegalArgumentException If the length is negative.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/06/14
	 */
	public WritableMemoryOutputStream(WritableMemory __mem,
		long __addr, long __len)
		throws IllegalArgumentException, NullPointerException
	{
		if (__mem == null)
			throw new NullPointerException("NARG");
		if (__len < 0)
			throw new IllegalArgumentException("NEGV");
		
		this.memory = __mem;
		this.address = __addr;
		this.length = __len;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/04
	 * @return
	 */
	@Override
	public long address()
	{
		return this.address + this._at;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/04
	 */
	@Override
	public int offset()
	{
		return this._at;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/06/14
	 */
	@Override
	public final void close()
	{
		// Does nothing
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/06/14
	 */
	@Override
	public final void flush()
	{
		// Does nothing
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/06/14
	 */
	@Override
	public final void write(int __b)
		throws IOException
	{
		// Check for end of memory
		int at = this._at;
		this.__check(1);
		
		// Write
		this.memory.memWriteByte(this.address + at, __b);
		this._at = at + 1;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/06/14
	 */
	@Override
	public final void write(byte[] __b)
		throws IOException, NullPointerException
	{
		this.write(__b, 0, __b.length);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/06/14
	 */
	@Override
	public final void write(byte[] __b, int __o, int __l)
		throws IOException, NullPointerException
	{
		if (__b == null)
			throw new NullPointerException("NARG");
		if (__o < 0 || __l < 0 || (__o + __l) > __b.length)
			throw new IndexOutOfBoundsException("IOOB");
		
		// Check for end of memory
		int at = this._at;
		this.__check(1);
		
		// Do not write past the bounds
		long left = this.length - at;
		if (__l > left)
			__l = (int)left;
		
		// Write to memory
		this.memory.memWriteBytes(this.address + at, __b, __o, __l);
		
		// Increase pointer
		this._at = at + __l;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/09
	 */
	@Override
	public void writeBoolean(boolean __v)
		throws IOException
	{
		this.writeByte((__v ? 1 : 0));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/09
	 */
	@Override
	public void writeByte(int __v)
		throws IOException
	{
		this.memory.memWriteByte(this.__check(1), __v);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/09
	 */
	@Override
	public void writeBytes(String __v)
		throws IOException
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/09
	 */
	@Override
	public void writeChar(int __v)
		throws IOException
	{
		this.writeShort((short)__v);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/09
	 */
	@Override
	public void writeChars(String __v)
		throws IOException
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/09
	 */
	@Override
	public void writeDouble(double __v)
		throws IOException
	{
		this.writeLong(Double.doubleToRawLongBits(__v));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/09
	 */
	@Override
	public void writeFloat(float __v)
		throws IOException
	{
		this.writeInt(Float.floatToRawIntBits(__v));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/09
	 */
	@Override
	public void writeInt(int __v)
		throws IOException
	{
		this.memory.memWriteInt(this.__check(4), __v);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/09
	 */
	@Override
	public void writeLong(long __v)
		throws IOException
	{
		this.memory.memWriteLong(this.__check(8), __v);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/09
	 */
	@Override
	public void writeShort(int __v)
		throws IOException
	{
		this.memory.memWriteShort(this.__check(2), __v);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/09
	 */
	@Override
	public void writeUTF(String __v)
		throws IOException
	{
		throw Debugging.todo();
	}
	
	/**
	 * Checks for a write which would overflow.
	 * 
	 * @param __len The number of bytes to check on write.
	 * @return The address to read from.
	 * @throws IllegalArgumentException If the length is zero or negative.
	 * @throws EOFException If the end of file is reached.
	 * @since 2021/02/14
	 */
	private long __check(int __len)
		throws IllegalArgumentException, EOFException
	{
		if (__len <= 0)
			throw new IllegalArgumentException("INEG");
		
		// {@squirreljme.error ZZ41 Reached end of memory stream.
		// (The position; The length of the stream; The length checked)}
		int at = this._at;
		if (at + (__len - 1) > this.length)
			throw new EOFException(String.format("ZZ41 %d %d %d",
				at, this.length, __len));
		
		return this.address + at;
	}
}

