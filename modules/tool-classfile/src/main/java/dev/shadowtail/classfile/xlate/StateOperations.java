// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.xlate;

import cc.squirreljme.runtime.cldc.util.UnmodifiableIterator;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * This represents multiple state operations.
 *
 * @since 2019/04/12
 */
public final class StateOperations
	implements Iterable<StateOperation>
{
	/** The operations. */
	private final StateOperation[] _ops;
	
	/** The hash. */
	private int _hash;
	
	/** String form. */
	private Reference<String> _string;
	
	/**
	 * Initializes the state operations.
	 *
	 * @param __ops The operations.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/12
	 */
	public StateOperations(StateOperation... __ops)
		throws NullPointerException
	{
		for (StateOperation x : (__ops = (__ops == null ?
			new StateOperation[0] : __ops.clone())))
			if (x == null)
				throw new NullPointerException("NARG");
		
		this._ops = __ops;
	}
	
	/**
	 * Initializes the state operations.
	 *
	 * @param __ops The operations.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/12
	 */
	public StateOperations(Iterable<StateOperation> __ops)
		throws NullPointerException
	{
		if (__ops == null)
			throw new NullPointerException("NARG");
		
		List<StateOperation> out = new ArrayList<>();
		for (StateOperation x : __ops)
			if (x == null)
				throw new NullPointerException("NARG");
			else
				out.add(x);
		
		this._ops = out.<StateOperation>toArray(
			new StateOperation[out.size()]);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/12
	 */
	@Override
	public final boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (!(__o instanceof StateOperations))
			return false;
		
		if (this.hashCode() != __o.hashCode())
			return false;
		
		return Arrays.equals(this._ops, ((StateOperations)__o)._ops);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/12
	 */
	@Override
	public final int hashCode()
	{
		int rv = this._hash;
		if (rv == 0)
			this._hash = (rv = Arrays.asList(this._ops).hashCode());
		return rv;
	}
	
	/**
	 * Returns if this is empty is empty or not.
	 *
	 * @return If this is empty.
	 * @since 2019/04/12
	 */
	public final boolean isEmpty()
	{
		return this._ops.length <= 0;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/12
	 */
	@Override
	public final Iterator<StateOperation> iterator()
	{
		return UnmodifiableIterator.<StateOperation>of(this._ops);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/12
	 */
	@Override
	public final String toString()
	{
		Reference<String> ref = this._string;
		String rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._string = new WeakReference<>((rv =
				Arrays.asList(this._ops).toString()));
		
		return rv;
	}
}

