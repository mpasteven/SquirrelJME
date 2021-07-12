// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.runtime.cldc.util;

import java.util.List;
import java.util.RandomAccess;

/**
 * General utilities for collections.
 *
 * @since 2021/02/25
 */
public final class CollectionUtils
{
	/**
	 * Not used.
	 * 
	 * @since 2021/02/25
	 */
	private CollectionUtils()
	{
	}
	
	/**
	 * Wraps the given character list as an integer list.
	 * 
	 * @param __chars The character list to wrap.
	 * @return The resultant integer list.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/02/25
	 */
	public static List<Integer> asIntegerList(List<Character> __chars)
		throws NullPointerException
	{
		if (__chars == null)
			throw new NullPointerException("NARG");
		
		if (__chars instanceof RandomAccess)
			return new __CharacterIntegerListRandom__(__chars);
		return new __CharacterIntegerListSequential__(__chars);
	}
}
