// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.jarfile;

import cc.squirreljme.jvm.summercoat.constants.BootstrapConstants;
import cc.squirreljme.runtime.cldc.debug.Debugging;

/**
 * Represents the B value for the Boot Jar Pointer.
 *
 * Used with {@link BootstrapConstants#ACTION_BOOTJARP_B}.
 * 
 * The low value is {@link BootJarPointer}.
 *
 * @since 2021/04/08
 */
public final class HighBootJarPointer
	implements HasBootJarPointer
{
	/** The pointer to refer to. */
	public final BootJarPointer pointer;
	
	/**
	 * Initializes the high variant of the pointer.
	 * 
	 * @param __pointer The pointer used.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/04/08
	 */
	public HighBootJarPointer(BootJarPointer __pointer)
		throws NullPointerException
	{
		if (__pointer == null)
			throw new NullPointerException("NARG");
		
		this.pointer = __pointer;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/08
	 */
	@Override
	public boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (!(__o instanceof HighBootJarPointer))
			return false;
		
		return this.pointer.equals(((HighBootJarPointer)__o).pointer);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/08
	 */
	@Override
	public int hashCode()
	{
		return this.pointer.hashCode();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/08
	 */
	@Override
	public BootJarPointer pointer()
	{
		return this.pointer;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/08
	 */
	@Override
	public String toString()
	{
		return "High:" + this.pointer;
	}
}
