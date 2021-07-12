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
 * An error with a system call.
 *
 * @since 2021/01/24
 */
public class VMSystemCallException
	extends VMException
{
	/** The error code. */
	public final int error;
	
	/**
	 * Initializes the exception.
	 * 
	 * @param __error The error code.
	 * @since 2021/01/24
	 */
	public VMSystemCallException(int __error)
	{
		this.error = __error;
	}
}
