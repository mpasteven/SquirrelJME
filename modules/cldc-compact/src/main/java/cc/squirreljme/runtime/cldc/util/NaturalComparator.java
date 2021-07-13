// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.runtime.cldc.util;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Comparator;

/**
 * This is used to compare two values using their natural comparison.
 *
 * @since 2016/09/06
 */
public final class NaturalComparator<V>
	implements Comparator<V>
{
	/** The single instance. */
	private static Reference<NaturalComparator> _REF;
	
	/**
	 * Only one is ever needed.
	 *
	 * @since 2017/11/30
	 */
	public NaturalComparator()
	{
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/09/06
	 */
	@Override
	@SuppressWarnings({"unchecked"})
	public int compare(V __a, V __b)
	{
		// Consider two nulls to be equal
		boolean na = (__a == null), nb = (__b == null);
		if (na && nb)
			return 0;
		
		// Nulls before non-null
		else if (na && !nb)
			return -1;
		else if (!na && nb)
			return 1;
		
		// Use standard comparison
		return ((Comparable<V>)__a).compareTo(__b);
	}
	
	/**
	 * Returns the natural comparator instance.
	 *
	 * @param <V> The type of value to compare.
	 * @return The instance of the natural comparator.
	 * @since 2016/09/06
	 */
	@SuppressWarnings({"unchecked"})
	public static final <V> NaturalComparator<V> instance()
	{
		Reference<NaturalComparator> ref = NaturalComparator._REF;
		NaturalComparator rv;
		
		// Cache?
		if (ref == null || null == (rv = ref.get()))
			NaturalComparator._REF = new WeakReference<>((rv = new NaturalComparator()));
		
		// Return it
		return (NaturalComparator<V>)rv;
	}
}

