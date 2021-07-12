// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.pool;

import cc.squirreljme.jvm.mle.brackets.TypeBracket;
import net.multiphasicapps.classfile.ClassName;

/**
 * This represents a pointer to the {@link TypeBracket} of a given class.
 *
 * @since 2019/09/07
 */
public final class TypeBracketPointer
{
	/** The class name to load for. */
	public final ClassName name;
	
	/**
	 * Initializes the pointer.
	 *
	 * @param __cl The class name.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/09/07
	 */
	public TypeBracketPointer(ClassName __cl)
		throws NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException("NARG");
		
		this.name = __cl;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/09/07
	 */
	@Override
	public final boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (!(__o instanceof TypeBracketPointer))
			return false;
		
		return this.name.equals(((TypeBracketPointer)__o).name);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/09/07
	 */
	@Override
	public final int hashCode()
	{
		return this.name.hashCode();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/09/07
	 */
	@Override
	public final String toString()
	{
		return this.name.toString();
	}
}

