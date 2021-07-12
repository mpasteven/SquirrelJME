// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.xlate;

/**
 * Represents a soft register.
 *
 * @since 2021/06/19
 */
public final class SoftRegister
{
	/** Is this temporary. */
	public final boolean isTemporary;
	
	/** The register this points to. */
	public final int register;
	
	/**
	 * Initializes the soft register.
	 * 
	 * @param __isTemporary Is this temporary?
	 * @param __register The register to add.
	 * @since 2021/06/19
	 */
	public SoftRegister(boolean __isTemporary, int __register)
	{
		this.isTemporary = __isTemporary;
		this.register = __register;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/06/19
	 */
	@Override
	public boolean equals(Object __o)
	{
		if (__o == this)
			return true;
		
		if (!(__o instanceof SoftRegister))
			return false;
		
		SoftRegister o = (SoftRegister)__o;
		return this.isTemporary == o.isTemporary &&
			this.register == o.register;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/06/19
	 */
	@Override
	public int hashCode()
	{
		return this.register ^
			(this.isTemporary ? -1 : 0);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/06/19
	 */
	@Override
	public String toString()
	{
		return (this.isTemporary ? "T" : "R") + this.register;
	}
	
	/**
	 * Initializes the soft register.
	 * 
	 * @param __isTemporary Is this temporary?
	 * @param __register The register to add.
	 * @since 2021/06/19
	 */
	public static SoftRegister of(boolean __isTemporary, int __register)
	{
		return new SoftRegister(__isTemporary, __register);
	}
}
