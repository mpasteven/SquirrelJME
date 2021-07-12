// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.summercoat;

/**
 * Cache for the native CPU, used to speed up potential operations.
 *
 * @since 2021/02/14
 */
public final class CPUCache
{
	/** The size of the method cache. */
	public static final int ICACHE_SIZE =
		4096;
	
	/** Spill over protection for the cache. */
	public static final int METHOD_CACHE_SPILL =
		1024;
	
	/** The argument cache size. */
	public static final int ACACHE_SIZE =
		12;
	
	/** The instruction cache. */
	public final byte[] iCache =
		new byte[CPUCache.ICACHE_SIZE];
	
	/** Raw argument cache. */
	public final int[] argRawCache =
		new int[CPUCache.ACACHE_SIZE];
	
	/** Value argument cache. */
	public final int[] argValCache =
		new int[CPUCache.ACACHE_SIZE];
	
	/** The current CPU Frame. */
	public CPUFrame nowFrame;
}
