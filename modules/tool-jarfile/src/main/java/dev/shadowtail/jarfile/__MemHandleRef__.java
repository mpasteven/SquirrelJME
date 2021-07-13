// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.jarfile;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Reference to a memory handle.
 *
 * @since 2021/01/10
 */
final class __MemHandleRef__
	implements Comparable<__MemHandleRef__>
{
	/** The ID for this handle. */
	protected final int id;
	
	/** The handle this points to. */
	protected final Reference<MemHandle> ref;
	
	/**
	 * Initializes the memory handle reference.
	 * 
	 * @param __memHandle The memory handle to refer to.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/10
	 */
	__MemHandleRef__(MemHandle __memHandle)
		throws NullPointerException
	{
		if (__memHandle == null)
			throw new NullPointerException("NARG");
		
		this.id = __memHandle.id;
		this.ref = new WeakReference<>(__memHandle);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/01/10
	 */
	@Override
	public int compareTo(__MemHandleRef__ __o)
	{
		return this.id - __o.id;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/01/10
	 */
	@Override
	public boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (!(__o instanceof __MemHandleRef__))
			return false;
		
		return this.id == ((__MemHandleRef__)__o).id;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/01/10
	 */
	@Override
	public int hashCode()
	{
		return this.id;
	}
}
