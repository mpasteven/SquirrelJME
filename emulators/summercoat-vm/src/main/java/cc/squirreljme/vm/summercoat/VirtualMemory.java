// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.summercoat;

import cc.squirreljme.jvm.summercoat.ld.mem.AbstractWritableMemory;
import cc.squirreljme.jvm.summercoat.ld.mem.Memory;
import cc.squirreljme.jvm.summercoat.ld.mem.MemoryAccessException;
import cc.squirreljme.jvm.summercoat.ld.mem.NotRealMemoryException;
import cc.squirreljme.jvm.summercoat.ld.mem.ReadableMemory;
import cc.squirreljme.jvm.summercoat.ld.mem.WritableMemory;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains the entirety of virtual memory, this includes access
 * to the various on-demand minification of suites and classes.
 *
 * @since 2019/04/21
 */
public final class VirtualMemory
	extends AbstractWritableMemory
{
	/** Memory map. */
	private final List<Memory> _memories =
		new ArrayList<>();
	
	/** Active cache map. */
	private volatile Memory[] _cache =
		new Memory[0];
	
	/**
	 * Maps the given region of memory.
	 *
	 * @param __mem The region to map.
	 * @since 2019/04/21
	 */
	public final void mapRegion(Memory __mem)
		throws NullPointerException
	{
		if (__mem == null)
			throw new NullPointerException("NARG");
		
		// Add memory
		List<Memory> memories = this._memories;
		synchronized (memories)
		{
			memories.add(__mem);
			
			// Redo cache
			this._cache = memories.<Memory>toArray(
				new Memory[memories.size()]);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/03
	 */
	@Override
	public long absoluteAddress(long __addr)
		throws MemoryAccessException, NotRealMemoryException
	{
		return __addr;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 */
	@Override
	public final int memReadByte(long __addr)
	{
		// Find memory to read from
		Memory[] cache = this._cache;
		for (Memory c : cache)
		{
			long cbase = c.absoluteAddress(0);
			long csize = c.memRegionSize();
			long vaddr = __addr - cbase;
			
			if (vaddr >= 0 && vaddr < csize)
				return ((ReadableMemory)c).memReadByte(vaddr);
		}
		
		// Unmapped address
		throw new VMMemoryAccessException(String.format(
			"Unmapped Address %#010x", __addr));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 * @return
	 */
	@Override
	public final long memRegionSize()
	{
		return Memory.MAX_32BIT;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 */
	@Override
	public final void memWriteByte(long __addr, int __v)
	{
		// Find memory to write to
		Memory[] cache = this._cache;
		for (Memory c : cache)
		{
			long cbase = c.absoluteAddress(0),
				csize = c.memRegionSize(),
				vaddr = __addr - cbase;
			
			if (vaddr >= 0 && vaddr < csize)
			{
				if (c instanceof WritableMemory)
				{
					((WritableMemory)c).memWriteByte(vaddr, __v);
					return;
				}
			}
		}
		
		// {@squirreljme.error AE0j Invalid write to unmapped or non-writable
		// memory! (The address; The value to write)}
		throw new VMMemoryAccessException(
			String.format("AE0j %08x %d", __addr, __v));
	}
}

