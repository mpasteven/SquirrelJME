// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.summercoat;

import cc.squirreljme.emulator.vm.VMException;

/**
 * Memory access exception.
 *
 * @since 2021/01/17
 */
public class VMMemoryAccessException
	extends VMException
{
	/**
	 * Initializes the exception.
	 * 
	 * @param __s The message.
	 * @since 2021/01/17
	 */
	public VMMemoryAccessException(String __s)
	{
		super(__s);
	}
}
