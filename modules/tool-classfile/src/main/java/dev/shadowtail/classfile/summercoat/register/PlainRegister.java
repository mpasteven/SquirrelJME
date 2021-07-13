// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.summercoat.register;

/**
 * This is a plain register which is completely untyped and otherwise.
 *
 * @since 2020/11/24
 */
public final class PlainRegister
	extends Register
{
	/**
	 * Initializes the plain register.
	 *
	 * @param __register The register to get.
	 * @since 2020/11/24
	 */
	public PlainRegister(int __register)
	{
		super(__register);
	}
	
	/**
	 * Uses the same register index but represents it as a typed register
	 * with the given type.
	 * 
	 * @param <T> The type of register.
	 * @param __cl The class to cast to.
	 * @return The register except that it is typed.
	 * @throws NullPointerException If no class was specified.
	 * @since 2020/11/24
	 */
	public final <T> TypedRegister<T> asType(Class<T> __cl)
		throws NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException("NARG");
		
		return new TypedRegister<>(__cl, this.register);
	}
	
	/**
	 * Creates a register reference.
	 * 
	 * @param __r The register to set.
	 * @return The resultant plain register.
	 * @since 2021/01/19
	 */
	public static PlainRegister of(int __r)
	{
		return new PlainRegister(__r);
	}
}
