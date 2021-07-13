// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.xlate;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import net.multiphasicapps.classfile.ExceptionHandlerTable;

/**
 * This contains the information needed to transition into exception handlers.
 *
 * @since 2019/04/13
 */
public final class ExceptionHandlerTransition
{
	/** The operations to perform when the entry is being handled. */
	public final StateOperations handled;
	
	/** Cleanup for when there are no handlers and everything is tossed up. */
	public final JavaStackEnqueueList nothandled;
	
	/** The exception table. */
	public final ExceptionHandlerTable table;
	
	/** Hash code. */
	private int _hash;
	
	/** String form. */
	private Reference<String> _string;
	
	/**
	 * Initializes the information.
	 *
	 * @param __h The state operations for getting the stack working properly.
	 * @param __q The tossup enqueue table when nothing is handled.
	 * @param __t The exception handler table.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/13
	 */
	public ExceptionHandlerTransition(StateOperations __h,
		JavaStackEnqueueList __q, ExceptionHandlerTable __t)
		throws NullPointerException
	{
		if (__h == null || __q == null || __t == null)
			throw new NullPointerException("NARG");
		
		this.handled = __h;
		this.nothandled = __q;
		this.table = __t;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/13
	 */
	@Override
	public final boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (this.hashCode() != __o.hashCode())
			return false;
		
		if (!(__o instanceof ExceptionHandlerTransition))
			return false;
		
		ExceptionHandlerTransition o = (ExceptionHandlerTransition)__o;
		return this.handled.equals(o.handled) &&
			this.nothandled.equals(o.nothandled) &&
			this.table.equals(o.table);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/13
	 */
	@Override
	public final int hashCode()
	{
		int rv = this._hash;
		if (rv == 0)
			this._hash = (rv = this.handled.hashCode() ^
				this.nothandled.hashCode() ^ this.table.hashCode());
		return rv;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/13
	 */
	@Override
	public final String toString()
	{
		String rv;
		
		Reference<String> ref = this._string;
		if (ref == null || null == (rv = ref.get()))
			this._string = new WeakReference<>((rv = String.format(
				"{handled=%s, notHandled=%s, table=%s",
				this.handled, this.nothandled, this.table)));
		
		return rv;
	}
}

