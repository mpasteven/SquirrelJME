// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.runtime.cldc.util;

import cc.squirreljme.runtime.cldc.debug.Debugging;
import java.util.AbstractSequentialList;
import java.util.List;
import java.util.ListIterator;

/**
 * A character list as an integer list, sequentially.
 *
 * @since 2021/02/25
 */
final class __CharacterIntegerListSequential__
	extends AbstractSequentialList<Integer>
{
	/** The backing character array. */
	protected final List<Character> chars;
	
	/**
	 * Initializes the list wrapper.
	 * 
	 * @param __chars The list to wrap.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/02/25
	 */
	__CharacterIntegerListSequential__(List<Character> __chars)
		throws NullPointerException
	{
		if (__chars == null)
			throw new NullPointerException("NARG");
		
		this.chars = __chars;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/25
	 */
	@Override
	public ListIterator<Integer> listIterator(int __dx)
	{
		return new __CharacterIntegerListIterator__(
			this.chars.listIterator(__dx));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/25
	 */
	@Override
	public int size()
	{
		return this.chars.size();
	}
}
