// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.classfile;

import cc.squirreljme.runtime.cldc.util.SortedTreeMap;
import cc.squirreljme.runtime.cldc.util.UnmodifiableIterator;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import net.multiphasicapps.collections.UnmodifiableMap;

/**
 * This is the stack map table which is used for verification purposes.
 *
 * @since 2017/10/09
 */
public final class StackMapTable
	implements Iterable<Map.Entry<Integer, StackMapTableState>>
{
	/** Stack map states. */
	private final Map<Integer, StackMapTableState> _states;
	
	/** String form. */
	private Reference<String> _string;
	
	/**
	 * Initializes the stack map table.
	 *
	 * @param __s The input states.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/10/16
	 */
	StackMapTable(Map<Integer, StackMapTableState> __s)
		throws NullPointerException
	{
		if (__s == null)
			throw new NullPointerException("NARG");
		
		this._states = new SortedTreeMap<>(__s);
	}
	
	/**
	 * Returns the stack map table state for the given entry.
	 *
	 * @param __a The address to get.
	 * @return The state for the given address or {@code null} if it there is
	 * no entry.
	 * @since 2017/10/16
	 */
	public StackMapTableState get(int __a)
	{
		return this._states.get(__a);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/16
	 */
	@Override
	public final Iterator<Map.Entry<Integer, StackMapTableState>> iterator()
	{
		return UnmodifiableIterator.<Map.Entry<Integer, StackMapTableState>>of(
			UnmodifiableMap.<Integer, StackMapTableState>of(
				this._states).entrySet().iterator());
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/01/21
	 */
	@Override
	public final String toString()
	{
		Reference<String> ref = this._string;
		String rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._string = new WeakReference<>((rv = this._states.toString()));
		
		return rv;
	}
}

