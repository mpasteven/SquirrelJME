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
 * This is thrown when the given memory is not mappable to real memory in
 * any way.
 *
 * @since 2021/04/03
 */
public class NotRealMemoryException
	extends MemoryAccessException
{
	/**
	 * Initializes the exception.
	 * 
	 * @param __addr The address.
	 * @since 2021/04/03
	 */
	public NotRealMemoryException(long __addr)
	{
		super(__addr);
	}
	
	/**
	 * Initializes the exception.
	 * 
	 * @param __addr The address.
	 * @param __m The message.
	 * @since 2021/04/03
	 */
	public NotRealMemoryException(long __addr, String __m)
	{
		super(__addr, __m);
	}
	
	/**
	 * Initializes the exception.
	 * 
	 * @param __addr The address.
	 * @param __c The cause.
	 * @since 2021/04/03
	 */
	public NotRealMemoryException(long __addr, Throwable __c)
	{
		super(__addr, __c);
	}
}
