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
 * This interface is used to represent anything which provides memory which
 * can be read.
 *
 * @since 2019/04/21
 */
public interface ReadableMemory
	extends Memory
{
	/**
	 * Reads the memory at the specified address.
	 *
	 * @param __addr The address to read from.
	 * @return The read value.
	 * @since 2019/04/21
	 */
	int memReadByte(long __addr);
	
	/**
	 * Bulk read of memory bytes.
	 *
	 * @param __addr The address to read from.
	 * @param __b The output bytes.
	 * @param __o The offset.
	 * @param __l The length.
	 * @throws IndexOutOfBoundsException If the offset and/or length are
	 * negative or exceed the array bounds.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/21
	 */
	void memReadBytes(long __addr, byte[] __b, int __o, int __l)
		throws IndexOutOfBoundsException, NullPointerException;
	
	/**
	 * Reads the memory handle from the address.
	 * 
	 * @param __addr The address to read from.
	 * @return The memory handle reference.
	 * @since 2021/01/17
	 */
	MemHandleReference memReadHandle(long __addr);
	
	/**
	 * Reads the memory at the specified address.
	 *
	 * @param __addr The address to read from.
	 * @return The read value.
	 * @since 2019/04/21
	 */
	int memReadInt(long __addr);
	
	/**
	 * Reads the memory at the specified address.
	 *
	 * @param __addr The address to read from.
	 * @return The read value.
	 * @since 2021/01/17
	 */
	long memReadLong(long __addr);
	
	/**
	 * Reads the memory at the specified address.
	 *
	 * @param __addr The address to read from.
	 * @return The read value.
	 * @since 2019/04/21
	 */
	int memReadShort(long __addr);
	
	/**
	 * Returns a sub-section of memory.
	 * 
	 * @param __base The base address of memory.
	 * @param __len The length of memory.
	 * @return The subsection of memory.
	 * @throws MemoryAccessException If the address and length are not valid.
	 * @since 2021/04/03
	 */
	ReadableMemory subSection(long __base, long __len)
		throws MemoryAccessException;
}

