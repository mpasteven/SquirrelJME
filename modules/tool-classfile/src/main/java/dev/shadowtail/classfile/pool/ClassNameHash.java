// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.pool;

import net.multiphasicapps.classfile.ClassName;

/**
 * Contains the hash for a class.
 *
 * @since 2021/02/04
 */
public final class ClassNameHash
{
	/** The class name to hash. */
	protected final ClassName className;
	
	/**
	 * Initializes the class name hash.
	 * 
	 * @param __className The class name.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/02/04
	 */
	public ClassNameHash(ClassName __className)
		throws NullPointerException
	{
		if (__className == null)
			throw new NullPointerException("NARG");
		
		this.className = __className;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/04
	 */
	@Override
	public int hashCode()
	{
		return this.className.hashCode();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/04
	 */
	@Override
	public boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (!(__o instanceof ClassNameHash))
			return false;
		
		return this.className.equals(((ClassNameHash)__o).className);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/04
	 */
	@Override
	public String toString()
	{
		return String.format("ClassNameHash:%s:%08x",
			this.className, this.hashCode());
	}
}
