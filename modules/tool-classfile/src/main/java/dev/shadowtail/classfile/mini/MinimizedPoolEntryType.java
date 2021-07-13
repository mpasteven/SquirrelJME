// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.mini;

import dev.shadowtail.classfile.pool.AccessedField;
import dev.shadowtail.classfile.pool.ClassNameHash;
import dev.shadowtail.classfile.pool.ClassPool;
import dev.shadowtail.classfile.pool.HighRuntimeValue;
import dev.shadowtail.classfile.pool.InvokeXTable;
import dev.shadowtail.classfile.pool.InvokedMethod;
import dev.shadowtail.classfile.pool.NotedString;
import dev.shadowtail.classfile.pool.NullPoolEntry;
import dev.shadowtail.classfile.pool.QuickCastCheck;
import dev.shadowtail.classfile.pool.TypeBracketPointer;
import dev.shadowtail.classfile.pool.UsedString;
import dev.shadowtail.classfile.summercoat.pool.InterfaceClassName;
import net.multiphasicapps.classfile.ClassName;
import net.multiphasicapps.classfile.ClassNames;
import net.multiphasicapps.classfile.MethodDescriptor;

/**
 * This represents the entry type in the pool.
 * 
 * The order is significant as it is used to identify the types used.
 *
 * @since 2019/04/14
 */
public enum MinimizedPoolEntryType
{
	/** Nothing. */
	NULL(true, NullPoolEntry.class),
	
	/** String. */
	STRING(false, String.class),
	
	/** Name of class. */
	CLASS_NAME(false, ClassName.class),
	
	/** Class names (used for interfaces). */
	CLASS_NAMES(false, ClassNames.class),
	
	/** The constant run-time pool for a given class. */
	CLASS_POOL(true, ClassPool.class),
	
	/** Accessed Field. */
	ACCESSED_FIELD(true, AccessedField.class),
	
	/** Invoked Method. */
	INVOKED_METHOD(true, InvokedMethod.class),
	
	/** Method Descriptor. */
	METHOD_DESCRIPTOR(false, MethodDescriptor.class),
	
	/** Integer. */
	INTEGER(false, Integer.class),
	
	/** Float. */
	FLOAT(false, Float.class),
	
	/** Long. */
	LONG(false, Long.class),
	
	/** Double. */
	DOUBLE(false, Double.class),
	
	/** A plain string that was used. */
	USED_STRING(true, UsedString.class),
	
	/** The invocation table for a given class's invocation kind. */
	INVOKE_XTABLE(true, InvokeXTable.class),
	
	/** An invokable interface class, used for interface binding. */
	INTERFACE_CLASS(true, InterfaceClassName.class),
	
	/** Class information. */
	TYPE_BRACKET_POINTER(true, TypeBracketPointer.class),
	
	/** A string which has been noted for debug purposes. */
	NOTED_STRING(true, NotedString.class),
	
	/** Check for quick casting, to avoid doing an involved instance check. */
	QUICK_CAST_CHECK(true, QuickCastCheck.class),
	
	/** The hash code for a class. */
	CLASS_NAME_HASH(true, ClassNameHash.class),
	
	/** Place holder for high long values. */
	HIGH_RUNTIME_VALUE(true, HighRuntimeValue.class),
	
	/* End. */
	;
	
	/** Static values. */
	private static final MinimizedPoolEntryType[] _VALUES =
		MinimizedPoolEntryType.values();
	
	/** Classes used. */
	private final Class<?>[] _classes;
	
	/** Is this a run-time entry? */
	protected final boolean isRunTime;
	
	/**
	 * Initializes the enumeration.
	 * 
	 * @param __isRt Is this for run-time?
	 * @param __classes Classes that represent this type.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/24
	 */
	MinimizedPoolEntryType(boolean __isRt, Class<?>... __classes)
		throws NullPointerException
	{
		if (__classes == null)
			throw new NullPointerException("NARG");
		
		this.isRunTime = __isRt;
		this._classes = __classes;
	}
	
	/**
	 * Checks if this is a compatible class.
	 * 
	 * @param __cl The class to check.
	 * @return If this is a compatible class.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/28
	 */
	public final boolean isClass(Class<?> __cl)
		throws NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException("NARG");
		
		for (Class<?> cl : this._classes)
			if (__cl == cl)
				return true;
		
		return false;
	}
	
	/**
	 * Can this be in the runtime pool?
	 *
	 * @return If this can be in the runtime pool.
	 * @since 2019/07/20
	 */
	public final boolean isRuntime()
	{
		return this == MinimizedPoolEntryType.NULL || this.isRunTime;
	}
	
	/**
	 * Can this be in the static pool?
	 *
	 * @return If this can be in the static pool.
	 * @since 2019/07/20
	 */
	public final boolean isStatic()
	{
		// This is the opposite of run-time
		return this == MinimizedPoolEntryType.NULL || !this.isRunTime;
	}
	
	/**
	 * Returns the type for the specified index.
	 *
	 * @param __i The index to get.
	 * @return The type for this index.
	 * @throws IllegalArgumentException If the type is not valid.
	 * @since 2019/04/17
	 */
	public static MinimizedPoolEntryType of(int __i)
		throws IllegalArgumentException
	{
		for (MinimizedPoolEntryType e : MinimizedPoolEntryType._VALUES)
			if (e.ordinal() == __i)
				return e;
		
		// {@squirreljme.error JC0e Unknown pool type. (The type)}
		throw new IllegalArgumentException("JC0e " + __i);
	}
	
	/**
	 * Returns the entry type that is used for the specified class.
	 *
	 * @param __cl The class to check.
	 * @return The entry type for the class.
	 * @throws IllegalArgumentException If the class is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/14
	 */
	public static MinimizedPoolEntryType ofClass(Class<?> __cl)
		throws IllegalArgumentException, NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException("NARG");
		
		// Try to find the given class
		for (MinimizedPoolEntryType e : MinimizedPoolEntryType._VALUES)
			for (Class<?> classy : e._classes)
				if (__cl == classy)
					return e;
		
		// {@squirreljme.error JC0f Class does not map to a pool entry
		// type. (The class)}
		throw new IllegalArgumentException("JC0f " + __cl);
	}
}

