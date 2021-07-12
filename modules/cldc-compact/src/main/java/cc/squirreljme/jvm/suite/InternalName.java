// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.suite;

/**
 * This represents an internal project name which is used by SquirrelJME.
 *
 * @since 2017/12/31
 */
public final class InternalName
	implements MarkedProvided
{
	/** The project name. */
	protected final String name;
	
	/**
	 * Initializes the internal name.
	 *
	 * @param __n The name to use.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/31
	 */
	public InternalName(String __n)
		throws NullPointerException
	{
		if (__n == null)
			throw new NullPointerException("NARG");
		
		this.name = __n;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/31
	 */
	@Override
	public boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (!(__o instanceof InternalName))
			return false;
		
		return this.name.equals(((InternalName)__o).name);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/31
	 */
	@Override
	public int hashCode()
	{
		return this.name.hashCode();
	}
	
	/**
	 * Returns the internal name.
	 *
	 * @return The internal name.
	 * @since 2017/12/31
	 */
	public String name()
	{
		return this.name;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/31
	 */
	@Override
	public String toString()
	{
		return this.name;
	}
}

