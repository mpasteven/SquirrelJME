// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.jarfile;

/**
 * Represents a single method within the VTable.
 *
 * @since 2021/01/27
 */
public class VTableMethod
{
	/** Execution address of the method. */
	public final BootJarPointer execAddr;
	
	/** The pool used for the method. */
	public final PoolHandle poolHandle;
	
	/**
	 * Initializes the VTable method.
	 * 
	 * @param __execAddr The execution address.
	 * @param __poolHandle The pool handler.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/27
	 */
	public VTableMethod(BootJarPointer __execAddr, PoolHandle __poolHandle)
		throws NullPointerException
	{
		if (__execAddr == null || __poolHandle == null)
			throw new NullPointerException("NARG");
		
		this.execAddr = __execAddr;
		this.poolHandle = __poolHandle;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/06
	 */
	@Override
	public final String toString()
	{
		return String.format("VTableMethod[%s %s]",
			this.execAddr, this.poolHandle);
	}
}
