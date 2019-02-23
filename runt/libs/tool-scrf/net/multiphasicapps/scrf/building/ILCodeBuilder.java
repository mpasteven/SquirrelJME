// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.scrf.building;

import net.multiphasicapps.scrf.CodeLocation;
import net.multiphasicapps.scrf.RegisterLocation;

/**
 * This class is used to store the generated instructions for the intermediate
 * language code.
 *
 * @since 2019/02/17
 */
public final class ILCodeBuilder
{
	/**
	 * Adds a copy from one location to another.
	 *
	 * @param __from The source to copy from.
	 * @param __to The destination to copy to.
	 * @return The location of the added instruction.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/02/23
	 */
	public final CodeLocation addCopy(RegisterLocation __from,
		RegisterLocation __to)
		throws NullPointerException
	{
		if (__from == null || __to == null)
			throw new NullPointerException("NARG");
		
		throw new todo.TODO();
	}
}

