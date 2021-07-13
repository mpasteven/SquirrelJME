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
 * This is thrown when the given memory handle is not valid.
 *
 * @since 2021/05/06
 */
public class InvalidMemoryHandleException
	extends VMMemoryAccessException
{
	/** The invalid handle. */
	public final int handleId;
	
	/**
	 * Initializes the exception.
	 * 
	 * @param __id The memory handle ID.
	 * @param __m The message.
	 * @since 2021/05/06
	 */
	public InvalidMemoryHandleException(int __id, String __m)
	{
		super(__m);
		
		this.handleId = __id;
	}
}
