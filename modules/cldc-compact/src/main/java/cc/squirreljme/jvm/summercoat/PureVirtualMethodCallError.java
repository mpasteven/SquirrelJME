// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.summercoat;

/**
 * This is thrown when an attempt is made to call a purely native or abstract
 * method.
 *
 * @since 2021/01/30
 */
public class PureVirtualMethodCallError
	extends VirtualMachineError
{
	/**
	 * Initializes the error.
	 * 
	 * @param __m The message.
	 * @since 2021/01/30
	 */
	public PureVirtualMethodCallError(String __m)
	{
		super(__m);
	}
}
