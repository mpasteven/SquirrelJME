// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package lang.interfaces;

/**
 * Implements A and B through super interface.
 *
 * @since 2021/02/14
 */
public class ImplementsAB
	implements InterfaceAB
{
	/**
	 * {@inheritDoc}
	 * @since 2021/02/14
	 */
	@Override
	public int methodA()
	{
		return 83;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/14
	 */
	@Override
	public int methodB()
	{
		return 172;
	}
}
