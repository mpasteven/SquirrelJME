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
 * This is the invocation table for a given class, it refers to either static
 * or virtual invocations for a given method.
 *
 * @since 2021/01/30
 */
public final class InvokeXTable
{
	/** The invocation type. */
	public final InvokeType invokeType;
	
	/** The target class. */
	public final ClassName targetClass;
	
	/**
	 * Initializes the XTable reference.
	 * 
	 * @param __type The type of invoke to get.
	 * @param __target The target class.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/30
	 */
	public InvokeXTable(InvokeType __type, ClassName __target)
		throws NullPointerException
	{
		if (__type == null || __target == null)
			throw new NullPointerException("NARG");
		
		this.invokeType = __type;
		this.targetClass = __target;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/01/30
	 */
	@Override
	public int hashCode()
	{
		return this.invokeType.hashCode() ^
			this.targetClass.hashCode();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/01/30
	 */
	@Override
	public boolean equals(Object __o)
	{
		if (__o == this)
			return true;
		
		if (!(__o instanceof InvokeXTable))
			return false;
		
		InvokeXTable o = (InvokeXTable)__o;
		return this.invokeType == o.invokeType &&
			this.targetClass.equals(o.targetClass);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/01/30
	 */
	@Override
	public String toString()
	{
		return String.format("XTable:%s[%s]",
			this.invokeType, this.targetClass);
	}
}
