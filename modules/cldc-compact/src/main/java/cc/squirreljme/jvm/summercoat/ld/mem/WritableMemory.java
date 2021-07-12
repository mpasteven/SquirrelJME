// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.summercoat.ld.mem;

/**
 * This is used to provide an interface for reading memory.
 *
 * @since 2019/04/21
 */
public interface WritableMemory
	extends ReadableMemory
{
	/**
	 * Writes a value to memory.
	 *
	 * @param __addr The address to write to.
	 * @param __v The value to write.
	 * @since 2019/04/21
	 */
	void memWriteByte(long __addr, int __v);
	
	/**
	 * Writes multiple bytes to memory
	 *
	 * @param __addr The address to write to.
	 * @param __b The input bytes.
	 * @param __o The offset.
	 * @param __l The length.
	 * @throws IndexOutOfBoundsException If the offset and/or length are
	 * negative or exceed the array bounds.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/21
	 */
	void memWriteBytes(long __addr, byte[] __b, int __o, int __l);
	
	/**
	 * Writes the memory handle to the address.
	 * 
	 * @param __addr The address to write to.
	 * @param __v The value to write.
	 * @since 2021/01/17
	 */
	void memWriteHandle(long __addr, MemHandleReference __v);
	
	/**
	 * Writes a value to memory.
	 *
	 * @param __addr The address to write to.
	 * @param __v The value to write.
	 * @since 2019/04/21
	 */
	void memWriteInt(long __addr, int __v);
	
	/**
	 * Writes a value to memory.
	 *
	 * @param __addr The address to write to.
	 * @param __v The value to write.
	 * @since 2021/01/17
	 */
	void memWriteLong(long __addr, long __v);
	
	/**
	 * Writes a value to memory.
	 *
	 * @param __addr The address to write to.
	 * @param __v The value to write.
	 * @since 2019/04/21
	 */
	void memWriteShort(long __addr, int __v);
	
	/**
	 * Returns a sub-section of memory.
	 * 
	 * @param __base The base address of memory.
	 * @param __len The length of memory.
	 * @return The subsection of memory.
	 * @throws MemoryAccessException If the address and length are not valid.
	 * @since 2021/04/03
	 */
	@Override
	WritableMemory subSection(long __base, long __len)
		throws MemoryAccessException;
}

