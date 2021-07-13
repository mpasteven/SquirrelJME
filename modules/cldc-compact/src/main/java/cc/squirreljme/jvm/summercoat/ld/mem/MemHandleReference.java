// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.summercoat.ld.mem;

/**
 * Reference to memory handle.
 *
 * @since 2021/01/17
 */
public final class MemHandleReference
{
	/** The ID. */
	public final int id;
	
	/**
	 * Initializes the reference to the memory handle.
	 * 
	 * @param __id The identifier.
	 * @since 2021/01/17
	 */
	public MemHandleReference(int __id)
	{
		this.id = __id;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/01/17
	 */
	@Override
	public int hashCode()
	{
		return this.id;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/01/17
	 */
	@Override
	public boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (!(__o instanceof MemHandleReference))
			return true;
		
		return this.id == ((MemHandleReference)__o).id;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/01/17
	 */
	@Override
	public String toString()
	{
		return String.format("MemHandle#%08x", this.id);
	}
}
