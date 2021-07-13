// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.jarfile;

import dev.shadowtail.classfile.mini.MinimizedMethod;

/**
 * This is used to refer to methods for VTable and interface lookup.
 *
 * @since 2021/01/30
 */
public final class MethodBinder
{
	/** The class this is for. */
	protected final ClassState inClass;
	
	/** The method this is for. */
	protected final MinimizedMethod method;
	
	/** VTable for this method bind. */
	protected final VTableMethod vTable;
	
	/** Is this in the same package? */
	protected final boolean inSamePackage;
	
	/**
	 * Initializes the method binder.
	 * 
	 * @param __inClass The class the method is in.
	 * @param __method The referenced method.
	 * @param __vTable The VTable reference for this method.
	 * @param __inSamePackage Is this in the same package?
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/30
	 */
	public MethodBinder(ClassState __inClass, MinimizedMethod __method,
		VTableMethod __vTable, boolean __inSamePackage)
		throws NullPointerException
	{
		if (__inClass == null || __method == null || __vTable == null)
			throw new NullPointerException("NARG");
		
		this.inClass = __inClass;
		this.method = __method;
		this.vTable = __vTable;
		this.inSamePackage = __inSamePackage;
	}
	
	/**
	 * Is this an abstract method?
	 * 
	 * @return If this is an abstract method.
	 * @since 2021/01/30
	 */
	protected boolean isAbstract()
	{
		return this.method.flags().isAbstract();
	}
	
	/**
	 * Is the containing class abstract?
	 * 
	 * @return If the containing class is abstract.
	 * @since 2021/01/30
	 */
	protected boolean isClassAbstract()
	{
		return this.inClass.classFile.flags().isAbstract();
	}
	
	/**
	 * Is this a native method?
	 * 
	 * @return If this is a native method.
	 * @since 2021/01/30
	 */
	protected boolean isNative()
	{
		return this.method.flags().isNative();
	}
	
	/**
	 * Is this a package private method?
	 * 
	 * @return If this is a package private method.
	 * @since 2021/01/30
	 */
	protected boolean isPackagePrivate()
	{
		return this.method.flags().isPackagePrivate();
	}
	
	/**
	 * Is this a private method?
	 * 
	 * @return If this is a private method.
	 * @since 2021/01/30
	 */
	public boolean isPrivate()
	{
		return this.method.flags().isPrivate();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/06
	 */
	@Override
	public final String toString()
	{
		return String.format("MB[%s:%s]=%s", this.inClass.thisName,
			this.method.nameAndType(), this.vTable);
	}
}
