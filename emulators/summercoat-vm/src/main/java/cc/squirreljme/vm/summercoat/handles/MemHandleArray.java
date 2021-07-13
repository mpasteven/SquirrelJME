// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.summercoat.handles;

import cc.squirreljme.emulator.vm.VMException;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.vm.summercoat.MemHandle;
import cc.squirreljme.vm.summercoat.VMMemoryAccessException;

/**
 * A memory handle that is an array.
 *
 * @since 2021/01/17
 */
public abstract class MemHandleArray
	extends MemHandle
{
	/** The length of the array. */
	public final int length;
	
	/** The size of each cell in the array. */
	protected final int cellSize;
	
	/**
	 * Initializes a new handle.
	 *
	 * @param __id The identifier for this handle.
	 * @param __kind The kind of memory handle to allocate.
	 * @param __base The array base
	 * @param __cellSize The size of each cell in the array.
	 * @param __len The array length.
	 * @throws IllegalArgumentException If the kind is not valid or the
	 * requested size is negative.
	 * @since 2021/01/17
	 */
	public MemHandleArray(int __id, int __kind, int __base, int __cellSize,
		int __len)
		throws IllegalArgumentException
	{
		super(__id, __kind, __base + (__cellSize * __len), __base);
		
		if (__len < 0)
			throw new IllegalArgumentException("Negative length: " + __len);
		
		this.length = __len;
		this.cellSize = __cellSize;
	}
	
	/**
	 * Calculates the index cell for the array access.
	 * 
	 * @param __addr The address to read for.
	 * @return The index of the entry.
	 * @throws VMMemoryAccessException If the cell would be invalid.
	 * @since 2021/02/14
	 */
	protected final int calcCell(long __addr)
		throws VMMemoryAccessException
	{
		// Make sure the index is valid
		long relBase = __addr - super.rawSize;
		if (relBase < 0)
			throw new VMMemoryAccessException("Cannot be an index: " + __addr);
		
		// To properly access the array it must be within bounds!
		int cellSize = this.cellSize;
		if ((relBase % cellSize) != 0)
			throw new VMMemoryAccessException(
				String.format("Unaligned cell access: %d (%d)",
					relBase, __addr));
		
		// Determine the base cell, to check against
		long cell = relBase / cellSize;
		if (cell < 0 || cell > Integer.MAX_VALUE)
			throw new VMMemoryAccessException(
				String.format("Invalid cell access: %d (%d, %d)",
					cell, relBase, __addr));
		
		// The index is here just as usual
		return (int)(relBase / cellSize);
	}
	
	/**
	 * Determines if this is in the base area before the array data, this will
	 * fail on invalid accesses.
	 * 
	 * @param __addr The address to check.
	 * @return if this is writing into the lower base of the handle.
	 * @throws VMMemoryAccessException If the memory access is not valid.
	 * @since 2021/02/14
	 */
	protected final boolean checkBase(long __addr)
		throws VMMemoryAccessException
	{
		int cellSize = this.cellSize;
		if (__addr < 0 || (__addr + cellSize) > Integer.MAX_VALUE)
			throw new VMMemoryAccessException(String.format(
				"Invalid memory access: %d", __addr)); 
		
		// would this read into part of the array?
		int rawSize = this.rawSize;
		if (__addr < rawSize && __addr + cellSize > rawSize)
			throw new VMMemoryAccessException(String.format(
				"Invalid memory access: %d", __addr));
		
		return __addr < rawSize;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/01/17
	 */
	@SuppressWarnings("MagicNumber")
	@Override
	protected void specialWriteBytes(int __addr, byte[] __b, int __o, int __l)
		throws IndexOutOfBoundsException, NullPointerException
	{
		if (__b == null)
			throw new NullPointerException("NARG");
		if (__o < 0 || __l < 0 || (__o + __l) > __b.length)
			throw new IndexOutOfBoundsException("IOOB");
		
		// Check if our cell is valid or not
		int relAddr = __addr - super.rawSize;
		int cellSize = this.cellSize;
		if ((relAddr % cellSize) != 0)
			throw new VMMemoryAccessException(String.format(
				"Invalid cell read %d @ %#x",
				cellSize, relAddr));
		
		// Are we not reading full cells?
		if ((__l % cellSize) != 0)
			throw new VMMemoryAccessException(String.format(
				"Invalid cell read %d / %d",
				cellSize, __l));
		
		// Determine the cell we are in
		int inCell = relAddr / cellSize;
		if (inCell < 0 || inCell >= this.length)
			throw new VMException("Out of bounds: " + inCell);
		
		// Depending on the cell size, forward accordingly
		int numCells = __l / cellSize;
		for (int cell = 0, addr = __addr, off = __o;
			cell < numCells; cell++, addr += cellSize, off += cellSize)
			switch (cellSize)
			{
				case 1:
					this.memWriteByte(addr,
						__b[off]);
					break;
					
				case 2:
					this.memWriteShort(addr,
						((__b[off] & 0xFF) << 8) |
						(__b[off + 1] & 0xFF));
					break;
					
				case 4:
					this.memWriteInt(addr,
						((__b[off] & 0xFF) << 24) |
						((__b[off + 1] & 0xFF) << 16) |
						((__b[off + 2] & 0xFF) << 8) |
						(__b[off + 3] & 0xFF));
					break;
					
				case 8:
					this.memWriteLong(addr,
						((__b[off] & 0xFFL) << 56) |
						((__b[off + 1] & 0xFFL) << 48) |
						((__b[off + 2] & 0xFFL) << 40) |
						((__b[off + 3] & 0xFFL) << 32) |
						((__b[off + 4] & 0xFFL) << 24) |
						((__b[off + 5] & 0xFF) << 16) |
						((__b[off + 6] & 0xFF) << 8) |
						(__b[off + 7] & 0xFF));
					break;
				
				default:
					throw Debugging.oops();
			}
	}
}
