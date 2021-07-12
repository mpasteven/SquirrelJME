// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.nncc;

import dev.shadowtail.classfile.xlate.JavaStackEnqueueList;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * This contains an enqueue and a label.
 *
 * @since 2019/04/24
 */
public final class EnqueueAndLabel
{
	/** The enqueue. */
	public final JavaStackEnqueueList enqueue;
	
	/** Label to exception point. */
	public final NativeCodeLabel label;
	
	/** String form. */
	private Reference<String> _string;
	
	/** Hash. */
	private int _hash;
	
	/**
	 * Initializes the class.
	 *
	 * @param __n The enqueue used.
	 * @param __l The label to jump to.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/24
	 */
	public EnqueueAndLabel(JavaStackEnqueueList __n, NativeCodeLabel __l)
		throws NullPointerException
	{
		if (__n == null || __l == null)
			throw new NullPointerException("NARG");
		
		this.enqueue = __n;
		this.label = __l;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/24
	 */
	@Override
	public boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (this.hashCode() != __o.hashCode())
			return false;
		
		if (!(__o instanceof EnqueueAndLabel))
			return false;
		
		EnqueueAndLabel o = (EnqueueAndLabel)__o;
		return this.enqueue.equals(o.enqueue) &&
			this.label.equals(o.label);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/24
	 */
	@Override
	public final int hashCode()
	{
		int rv = this._hash;
		if (rv == 0)
			this._hash = (rv = this.enqueue.hashCode() ^
				this.label.hashCode());
		return rv;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/24
	 */
	@Override
	public final String toString()
	{
		String rv;
		
		Reference<String> ref = this._string;
		if (ref == null || null == (rv = ref.get()))
			this._string = new WeakReference<>((rv = String.format(
				"{enqueue=%s, label=%s",
				this.enqueue, this.label)));
		
		return rv;
	}
}

