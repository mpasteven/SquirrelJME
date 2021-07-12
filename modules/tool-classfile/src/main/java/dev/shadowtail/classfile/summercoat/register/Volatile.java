// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.summercoat.register;

import dev.shadowtail.classfile.nncc.VolatileRegisterStack;

/**
 * This represents a volatile register.
 *
 * @since 2020/11/24
 */
public final class Volatile<R extends Register>
	implements AutoCloseable
{
	/** The register used. */
	public final R register;
	
	/** The register stack owning this. */
	private final VolatileRegisterStack _stack;
	
	/** Has this been closed? */
	private volatile boolean _closed;
	
	/**
	 * Initializes the volatile register.
	 * 
	 * @param __vrs The stack used to create this.
	 * @param __reg The register that is used.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/24
	 */
	public Volatile(VolatileRegisterStack __vrs, R __reg)
		throws NullPointerException
	{
		if (__vrs == null || __reg == null)
			throw new NullPointerException("NARG");
		
		this.register = __reg;
		this._stack = __vrs;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/11/24
	 */
	@Override
	public void close()
	{
		// Do nothing if already closed
		if (this._closed)
			return;
		
		// These only get closed once!
		this._closed = true;
		
		// Remove high and low parts
		R register = this.register;
		if (register instanceof WideRegister)
			this._stack.removeUnmanaged(
				((WideRegister)register).high.register);
		this._stack.removeUnmanaged(register.register);
	}
}
