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
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * This is an input stream which can read from readable memory.
 * 
 * For anything from {@link DataInput} the proper type based methods are used,
 * as such if there is a specific byte order that is used it will be
 * derived from this.
 *
 * @since 2019/04/21
 */
public final class ReadableMemoryInputStream
	extends InputStream
	implements DataInput, MemoryStream
{
	/** The base read address. */
	protected final long address;
	
	/** The number of bytes that can be read. */
	protected final long length;
	
	/** The input memory. */
	protected final ReadableMemory memory;
	
	/** The current read offset. */
	private int _at;
	
	/**
	 * Initializes the input read memory, the byte order is big endian.
	 * 
	 * @param __mem The memory to read from.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/09
	 */
	public ReadableMemoryInputStream(ReadableMemory __mem)
		throws NullPointerException
	{
		// The address is zero and not __mem.memRegionOffset() because this
		// address is relative to the piece of memory and not the address
		// of that memory
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
	 * @since 2019/04/21
	 */
	public ReadableMemoryInputStream(ReadableMemory __mem,
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
	 * @since 2019/04/21
	 */
	@Override
	public final int available()
	{
		return (int)Math.max(Integer.MAX_VALUE, this.length - this._at);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 */
	@Override
	public final void close()
	{
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 */
	@SuppressWarnings("MagicNumber")
	@Override
	public final int read()
	{
		// EOF?
		int at = this._at;
		if (at >= this.length)
			return -1;
		
		// Read
		this._at = at + 1;
		return this.memory.memReadByte(this.address + at) & 0xFF;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 */
	@Override
	public final int read(byte[] __b)
		throws NullPointerException
	{
		return this.read(__b, 0, __b.length);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 */
	@Override
	public final int read(byte[] __b, int __o, int __l)
		throws IndexOutOfBoundsException, NullPointerException
	{
		if (__b == null)
			throw new NullPointerException("NARG");
		if (__o < 0 || __l < 0 || (__o + __l) > __b.length)
			throw new IndexOutOfBoundsException("IOOB");
		
		// Used to check bounds
		int at = this._at;
		long length = this.length;
		long address = this.address;
		
		// Is already at EOF
		if (at >= length)
			return -1;
		
		// Read in all bytes
		ReadableMemory memory = this.memory;
		long from = address + at;
		for (int i = 0; i < __l; i++)
		{
			// EOF?
			if (at >= length)
			{
				this._at = at;
				return (i == 0 ? -1 : i);
			}
			
			// Read in
			__b[__o++] = (byte)memory.memReadByte(from++);
			at++;
		}
		
		// Count
		this._at = at;
		return __l;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/09
	 */
	@Override
	public boolean readBoolean()
		throws IOException
	{
		return this.readByte() != 0;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/09
	 */
	@Override
	public byte readByte()
		throws IOException
	{
		return (byte)this.memory.memReadByte(this.__checkIncr(1));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/09
	 */
	@Override
	public char readChar()
		throws IOException
	{
		return (char)this.readShort();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/09
	 */
	@Override
	public double readDouble()
		throws IOException
	{
		return Double.longBitsToDouble(this.readLong());
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/09
	 */
	@Override
	public float readFloat()
		throws IOException
	{
		return Float.intBitsToFloat(this.readInt());
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/09
	 */
	@Override
	public void readFully(byte[] __b)
		throws IOException
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/09
	 */
	@Override
	public void readFully(byte[] __b, int __o, int __l)
		throws IOException
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/09
	 */
	@Override
	public int readInt()
		throws IOException
	{
		return this.memory.memReadInt(this.__checkIncr(4));
	}
	
	/**
	 * This method is not in CLDC, do not use!
	 * 
	 * @deprecated Not in CLDC, do not use!
	 * @since 2021/02/09
	 */
	@Deprecated
	@SuppressWarnings({"override", "ConstantConditions"})
	public String readLine()
		throws IOException
	{
		if (false)
			throw new IOException();
		throw Debugging.oops();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/09
	 */
	@Override
	public long readLong()
		throws IOException
	{
		return this.memory.memReadLong(this.__checkIncr(8));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/09
	 */
	@Override
	public short readShort()
		throws IOException
	{
		return (short)this.memory.memReadShort(this.__checkIncr(2));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/09
	 */
	@Override
	public String readUTF()
		throws IOException
	{
		return DataInputStream.readUTF(this);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/09
	 */
	@SuppressWarnings("MagicNumber")
	@Override
	public int readUnsignedByte()
		throws IOException
	{
		return this.readByte() & 0xFF;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/09
	 */
	@SuppressWarnings("MagicNumber")
	@Override
	public int readUnsignedShort()
		throws IOException
	{
		return this.readShort() & 0xFFFF;
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
	 * @since 2021/02/09
	 */
	@Override
	public int skipBytes(int __n)
		throws IOException
	{
		throw Debugging.todo();
	}
	
	/**
	 * Checks for a read which would overflow and if it is successful will
	 * increment accordingly for the next address.
	 * 
	 * @param __len The number of bytes to check on read.
	 * @return The address to read from.
	 * @throws IllegalArgumentException If the length is zero or negative.
	 * @throws EOFException If the end of file is reached.
	 * @since 2021/02/14
	 */
	private long __checkIncr(int __len)
		throws IllegalArgumentException, EOFException
	{
		if (__len <= 0)
			throw new IllegalArgumentException("INEG");
		
		// {@squirreljme.error ZZ3z Reached end of memory stream.
		// (The position; The length of the stream; The length checked)}
		int at = this._at;
		if (at + (__len - 1) > this.length)
			throw new EOFException(String.format("ZZ3z %d %d %d",
				at, this.length, __len));
		
		// Increment and use the old address
		this._at = at + __len;
		return this.address + at;
	}
}

