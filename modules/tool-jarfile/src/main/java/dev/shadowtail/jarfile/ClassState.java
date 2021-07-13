// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.jarfile;

import cc.squirreljme.jvm.summercoat.constants.QuickCastCheckType;
import dev.shadowtail.classfile.mini.MinimizedClassFile;
import java.io.IOException;
import java.util.List;
import net.multiphasicapps.classfile.ClassName;
import net.multiphasicapps.classfile.ClassNames;
import net.multiphasicapps.classfile.MethodNameAndType;

/**
 * This contains the state of a single class.
 *
 * @since 2020/12/16
 */
public final class ClassState
{
	/** The class data. */
	protected final MinimizedClassFile classFile;
	
	/** The name of this class. */
	protected final ClassName thisName;
	
	/** All possible interfaces for the class. */
	ClassNames _allInterfaces;
	
	/** This is a handle for class information. */
	ClassInfoHandle _classInfoHandle;
	
	/** The memory handle that is used for the pool. */
	PoolHandle _poolMemHandle;
	
	/** The super class. */
	ClassState _superClass;
	
	/** The class chain. */
	List<ClassState> _classChain;
	
	/** Virtual method binds. */
	List<MethodBinder> _virtualBinds;
	
	/** Static method binds. */
	List<MethodBinder> _staticBinds;
	
	/** Interface methods. */
	List<MethodNameAndType> _interfaceMethods;
	
	/**
	 * Initializes the base empty class state.
	 * 
	 * @param __thisName The name of this class.
	 * @param __classFile The class file data.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/16
	 */
	ClassState(ClassName __thisName, MinimizedClassFile __classFile)
		throws NullPointerException
	{
		if (__thisName == null || __classFile == null)
			throw new NullPointerException("NARG");
		
		this.thisName = __thisName;
		this.classFile = __classFile;
	}
	
	/**
	 * Checks if the given class can be assigned to this one, the check is
	 * in the same order as {code instanceOf Object} that is
	 * {@code b.getClass().isAssignableFrom(a.getClass()) == (a instanceof b)}
	 * and {@code (Class<B>)a} does not throw {@link ClassCastException}.
	 * 
	 * This has the same logic as {@link Class#isAssignableFrom(Class)}. 
	 * 
	 * @param __state The state to get from, as needed.
	 * @param __b The other class.
	 * @return One of the {@link QuickCastCheckType}.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/31
	 */
	protected int isAssignableFrom(BootState __state, ClassState __b)
		throws IOException, NullPointerException
	{
		if (__state == null || __b == null)
			throw new NullPointerException("NARG");
		
		if (true)
			return QuickCastCheckType.UNKNOWN;
		
		// Any conversion from object is unknown
		if (__b.thisName.isObjectClass())
			return QuickCastCheckType.UNKNOWN;
		
		// Anything to object is valid
		if (this.thisName.isObjectClass())
			return QuickCastCheckType.COMPATIBLE;
		
		// Find matching other class
		for (ClassState at = __b; at != null; at = at._superClass)
		{
			// If the super class is missing, try to load it
			if (at._superClass == null)
				__state.loadClass(at.thisName);
			
			// Is this a match?
			if (this == at)
				return QuickCastCheckType.COMPATIBLE;
		}
		
		return QuickCastCheckType.UNKNOWN;
		
		/*
		// Check all interfaces now
		for (
		
		if (true)
			throw Debugging.todo();
		
		// Is the same exact class or casting to object?
		if (this == __b || __b.thisName.isObjectClass())
			return QuickCastCheckType.COMPATIBLE;
		
		// If we are the object class, it will never be known if we can
		// cast to any other type
		if (this.thisName.isObjectClass())
			return QuickCastCheckType.UNKNOWN;
		
		// Either side is an array type
		if (this.thisName.isArray() || __b.thisName.isArray())
		{
			// Dimensional mismatch, conversion is not possible
			if (this.thisName.dimensions() != __b.thisName.dimensions())
			{
				// If B is an array and the root component type is an object,
				// we can cast an array to an object array as long as the
				// number of dimensions is less than our own.
				if (__b.thisName.isArray() &&
					__b.thisName.rootComponentType().isObjectClass())
					if (__b.thisName.dimensions() < this.thisName.dimensions())
						return QuickCastCheckType.COMPATIBLE;
				
				return QuickCastCheckType.NOT_COMPATIBLE;
			}
			
			// We can cast other arrays to other arrays with a compatible
			// component type
			return __state.loadClass(this.thisName.componentType())
				.isAssignableFrom(__state, __state.loadClass(__b.thisName));
		}
		
		// Find matching super class
		for (ClassState at = this; at != null; at = at._superClass)
		{
			// If the super class is missing, try to load it
			if (at._superClass == null)
				__state.loadClass(at.thisName);
			
			// Is this a potential match?
			if (this == at)
				return QuickCastCheckType.COMPATIBLE;
		}
		
		// If the other class is an interface, check to see if one of our
		// own interfaces. If it is then we can cast to it.
		if (__b.classFile.flags().isInterface())
			for (ClassName our : __state.allInterfaces(this.thisName))
				if (__state.loadClass(our) == __b)
					return QuickCastCheckType.COMPATIBLE;
		
		// If we are an interface we will never quite know if we can cast to
		// any other object since while two objects may share an interface
		// they might not be remotely compatible types
		if (this.classFile.flags().isInterface())
			return QuickCastCheckType.UNKNOWN;
		
		// Look for type compatibility on the other side, if our source is
		// a super class of the other class... then it is possible that
		// conversion may pass or fail
		for (ClassState at = __b; at != null; at = at._superClass)
		{
			// If the super class is missing, try to load it
			if (at._superClass == null)
				__state.loadClass(at.thisName);
			
			// Is this a match?
			if (__b == at)
				return QuickCastCheckType.UNKNOWN; 
		}
		
		// Not compatible
		return QuickCastCheckType.NOT_COMPATIBLE;*/
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/01/24
	 */
	@Override
	public String toString()
	{
		return "ClassState:" + this.thisName;
	}
}
