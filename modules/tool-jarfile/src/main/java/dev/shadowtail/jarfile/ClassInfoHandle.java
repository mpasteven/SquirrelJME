// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.jarfile;

import cc.squirreljme.jvm.summercoat.constants.ClassProperty;
import cc.squirreljme.jvm.summercoat.constants.MemHandleKind;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * This is a handle that represents class information.
 *
 * @since 2020/12/20
 */
public class ClassInfoHandle
	extends PropertyListHandle
{
	/** The class this refers to. */
	protected final Reference<ClassState> stateRef;
	
	/**
	 * Initializes the base memory handle.
	 *
	 * @param __id The memory handle ID.
	 * @param __memActions The memory actions used.
	 * @param __cl The class to refer to.
	 * @param __baseSize The base size of the array.
	 * @throws IllegalArgumentException If the memory handle does not have the
	 * correct security bits specified.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/20
	 */
	ClassInfoHandle(int __id, MemActions __memActions, ClassState __cl,
		int __baseSize)
		throws IllegalArgumentException, NullPointerException
	{
		super(MemHandleKind.CLASS_INFO, __id, __memActions, __baseSize,
			ClassProperty.NUM_RUNTIME_PROPERTIES);
		
		if (__cl == null)
			throw new NullPointerException("NARG");
		
		this.stateRef = new WeakReference<>(__cl);
	}
	
	/**
	 * Returns the class state.
	 * 
	 * @return The class state.
	 * @throws IllegalStateException If the class was garbage collected.
	 * @since 2021/01/10
	 */
	public final ClassState classState()
		throws IllegalStateException
	{
		// Check if GCed
		ClassState rv = this.stateRef.get();
		if (rv == null)
			throw new IllegalStateException("GCGC");
		
		return rv;
	}
}
