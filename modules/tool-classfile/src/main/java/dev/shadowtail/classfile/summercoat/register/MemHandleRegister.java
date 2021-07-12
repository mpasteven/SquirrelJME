// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.summercoat.register;

import dev.shadowtail.classfile.nncc.NativeCode;

/**
 * Represents a register that represents a memory handle (likely an object).
 *
 * @since 2020/11/28
 */
public final class MemHandleRegister
	extends Register
{
	/** Return register. */
	public static final MemHandleRegister RETURN =
		MemHandleRegister.of(NativeCode.RETURN_REGISTER);
	
	/** The always null reference register. */
	public static final MemHandleRegister NULL =
		MemHandleRegister.of(NativeCode.ZERO_REGISTER);
	
	/** Exception register. */
	public static final MemHandleRegister EXCEPTION =
		MemHandleRegister.of(NativeCode.EXCEPTION_REGISTER);
	
	/**
	 * Initializes the basic register.
	 *
	 * @param __register The register to get.
	 * @since 2020/11/28
	 */
	public MemHandleRegister(int __register)
	{
		super(__register);
	}
	
	/**
	 * Sets up a memory handle register.
	 * 
	 * @param __register The register to use.
	 * @return The memory handle register.
	 * @since 2020/11/28
	 */
	public static MemHandleRegister of(int __register)
	{
		return new MemHandleRegister(__register);
	}
}
