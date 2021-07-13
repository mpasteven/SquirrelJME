// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.summercoat.lle;

import cc.squirreljme.jvm.Assembly;
import cc.squirreljme.jvm.mle.ObjectShelf;
import cc.squirreljme.jvm.mle.TypeShelf;
import cc.squirreljme.jvm.mle.brackets.JarPackageBracket;
import cc.squirreljme.jvm.mle.brackets.TypeBracket;
import cc.squirreljme.jvm.mle.exceptions.MLECallError;
import cc.squirreljme.jvm.summercoat.LogicHandler;
import cc.squirreljme.jvm.summercoat.constants.ClassProperty;
import cc.squirreljme.jvm.summercoat.constants.StaticClassProperty;
import cc.squirreljme.jvm.summercoat.constants.StaticVmAttribute;
import cc.squirreljme.runtime.cldc.debug.Debugging;

/**
 * SummerCoat implementation of {@link TypeShelf}, provides the shelf for
 * types that exist within the JVM.
 *
 * @since 2020/05/30
 */
@SuppressWarnings("unused")
public final class LLETypeShelf
{
	/**
	 * Not used.
	 * 
	 * @since 2020/06/30
	 */
	private LLETypeShelf()
	{
	}
	
	/**
	 * Returns the binary name of the given class.
	 *
	 * @param __type The type to get the binary name of.
	 * @return The binary name of this class.
	 * @throws MLECallError If the type is not valid.
	 * @since 2020/06/07
	 */
	public static String binaryName(TypeBracket __type)
		throws MLECallError
	{
		if (__type == null)
			throw new MLECallError("NARG");
		
		return (String)Assembly.pointerToObject(
			LogicHandler.typeGetProperty(__type,
				ClassProperty.MEMHANDLE_THIS_NAME_DESC));
	}
	
	/**
	 * Returns the name of the package the class is within.
	 *
	 * @param __type The type to get the binary package of.
	 * @return The binary name of the package the class is in.
	 * @since 2020/06/07
	 */
	public static String binaryPackageName(TypeBracket __type)
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Returns the type that is used within the given class.
	 *
	 * @param __cl The class to get the type of.
	 * @return The type of the given class.
	 * @since 2020/06/07
	 */
	public static TypeBracket classToType(Class<?> __cl)
	{
		if (__cl == null)
			throw new MLECallError("NARG");
		
		// Just read the class type information
		return Assembly.pointerToTypeBracket(Assembly.memHandleReadInt(__cl,
			LogicHandler.staticVmAttribute(
				StaticVmAttribute.OFFSETOF_CLASS_TYPEBRACKET_FIELD)));
	}
	
	/**
	 * Returns the component type of the class.
	 *
	 * @param __type The type to get the component of.
	 * @return The component type of the class.
	 * @throws MLECallError If {@code __type} is {@code null} or is not an
	 * array type.
	 * @since 2020/06/07
	 */
	public static TypeBracket component(TypeBracket __type)
		throws MLECallError
	{
		if (__type == null)
			throw new MLECallError("NARG");
		
		// {@squirreljme.error ZZ53 Not an array type.}
		if (!TypeShelf.isArray(__type))
			throw new MLECallError("ZZ53");
		
		return Assembly.pointerToTypeBracket(LogicHandler.typeGetProperty(
			__type, ClassProperty.TYPEBRACKET_COMPONENT));
	}
	
	/**
	 * Returns the root component of this type, this is essentially calling
	 * {@link #component(TypeBracket)} over until the type is no longer an
	 * array type.
	 *
	 * @param __type The type to get the root component of.
	 * @return The root component of the type.
	 * @since 2020/06/07
	 */
	public static TypeBracket componentRoot(TypeBracket __type)
	{
		if (__type == null)
			throw new MLECallError("NARG");
		
		// {@squirreljme.error ZZ53 Not an array type.}
		if (!TypeShelf.isArray(__type))
			throw new MLECallError("ZZ53");
		
		return Assembly.pointerToTypeBracket(LogicHandler.typeGetProperty(
			__type, ClassProperty.TYPEBRACKET_ROOT_COMPONENT));
	}
	
	/**
	 * Returns the number of dimensions this type has.
	 *
	 * @param __type The type to get the dimension count for.
	 * @return The number of dimensions for the type, this will be {@code 0}
	 * if this is not an array.
	 * @throws MLECallError If {@code __type} is {@code null}.
	 * @since 2020/07/06
	 */
	public static int dimensions(TypeBracket __type)
		throws MLECallError
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Gets the enumeration values for the given type.
	 * 
	 * @param __type The type to get the enumerated values from.
	 * @return The enumeration values for the given type.
	 * @throws MLECallError If {@code __type} is {@code null} or is not an
	 * enumeration.
	 * @since 2020/06/28
	 */
	@SuppressWarnings("rawtypes")
	public static Enum[] enumValues(TypeBracket __type)
		throws MLECallError
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Since types are virtual objects that do not have a true base off
	 * {@link Object}, this method is used to compare two types with each
	 * other.
	 *
	 * @param __a The first type.
	 * @param __b The second type.
	 * @return If these two types are the same.
	 * @throws MLECallError If either are {@code null}.
	 * @since 2020/06/04
	 */
	public static boolean equals(TypeBracket __a, TypeBracket __b)
		throws MLECallError
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Finds a type by it's name.
	 *
	 * @param __name The name of the type.
	 * @return The type bracket for the type or {@code null} if none was
	 * found.
	 * @since 2020/06/02
	 */
	public static TypeBracket findType(String __name)
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Initializes the given class.
	 * 
	 * @param __type The class info to initialize.
	 * @throws MLECallError If the class is {@code null}.
	 * @since 2020/11/28
	 */
	public static void initClass(TypeBracket __type)
		throws MLECallError
	{
		if (__type == null)
			throw new MLECallError("NARG");
		
		// If the class is already initialized, no point in doing it again
		if (LLETypeShelf.isClassInit(__type))
			return;
		
		// TODO: Protect initClass() for multiple threads
		Debugging.todoNote("Protected initClass(%s)",
			TypeShelf.binaryName(__type));
		
		// Set as initialized _BEFORE_ we do the actual static method call
		// because we are going to recurse into super classes and we do not
		// want to end up in a class initialization loop!
		LogicHandler.typeSetProperty(__type,
			ClassProperty.BOOLEAN_IS_INITIALIZED, 1);
		
		// Initialize the super class first before we do this one
		TypeBracket superType = LLETypeShelf.superClass(__type);
		if (superType != null)
			LLETypeShelf.initClass(superType);
		
		// If there is a static initializer, call it since we will need to
		// setup any static fields and otherwise
		long clInitFp = LogicHandler.typeGetPropertyLong(__type,
			ClassProperty.FUNCPTR_CLINIT_LO);
		if (clInitFp != 0)
		{
			Assembly.breakpoint();
			throw Debugging.todo();
		}
	}
	
	/**
	 * Returns the JAR that the type is within.
	 *
	 * @param __type The type to get the JAR of.
	 * @return The JAR that the type is within or {@code null} if this type
	 * is not within any JAR.
	 * @since 2020/06/07
	 */
	public static JarPackageBracket inJar(TypeBracket __type)
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Returns the interfaces of the type.
	 *
	 * @param __type The type to get the interfaces of.
	 * @return The interfaces of the class.
	 * @throws MLECallError If {@code __type} is null. 
	 * @since 2020/06/07
	 */
	public static TypeBracket[] interfaces(TypeBracket __type)
		throws MLECallError
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Returns true if this type is an array.
	 *
	 * @param __type The type to check.
	 * @return If this is an array.
	 * @throws MLECallError If {@code __type} is {@code null}.
	 * @since 2020/06/07
	 */
	public static boolean isArray(TypeBracket __type)
		throws MLECallError
	{
		if (__type == null)
			throw new MLECallError("NARG");
		
		return LogicHandler.typeGetProperty(__type,
			StaticClassProperty.NUM_DIMENSIONS) > 0;
	}
	
	/**
	 * Performs the same logic as {@link Class#isAssignableFrom(Class)}, 
	 * checks if the given class can be assigned to this one. The check is
	 * in the same order as {@code instanceof Object} that is
	 * {@code b.getClass().isAssignableFrom(a.getClass()) == (a instanceof b)}
	 * and {@code (Class<B>)a} does not throw {@link ClassCastException}.
	 * 
	 * @param __this The basis class
	 * @param __other The target class which is checked for assignment.
	 * @return If the given 
	 * @throws MLECallError On null arguments.
	 * @since 2021/02/07
	 */
	public static boolean isAssignableFrom(int __this, int __other)
		throws MLECallError
	{
		if (__this == 0 || __other == 0)
			throw new MLECallError("NARG");
			
		// Check current and super classes for the class information
		for (int at = __other; at != 0;
			at = LogicHandler.typeGetProperty(at,
				ClassProperty.TYPEBRACKET_SUPER))
			if (at == __this)
				return true;
		
		// If not yet found, try all of the interfaces
		int allInts = LogicHandler.typeGetProperty(__other,
			ClassProperty.TYPEBRACKET_ALL_INTERFACECLASSES);
		for (int i = 0, n = LogicHandler.listLength(allInts); i < n; i++)
			if (LogicHandler.listRead(allInts, i) == __this)
				return true;
		
		// Casting from one type to an array class?
		int ourDims = LogicHandler.typeGetProperty(__this,
			StaticClassProperty.NUM_DIMENSIONS);
		int otherDims = LogicHandler.typeGetProperty(__other,
			StaticClassProperty.NUM_DIMENSIONS);
		if (ourDims > 0 || otherDims > 0)
		{
			// Since we are doing arrays, any array that has a compatible
			// root component can be casted into. So this adjusts the logic
			// accordingly
			if (ourDims == otherDims)
				return LLETypeShelf.isAssignableFrom(
					LogicHandler.typeGetProperty(__this,
						ClassProperty.TYPEBRACKET_ROOT_COMPONENT),
					LogicHandler.typeGetProperty(__other,
						ClassProperty.TYPEBRACKET_ROOT_COMPONENT));
			
			// Are we casting from Foo[][]... to Object[]... or Object...?
			// We can lose dimensions but we cannot gain them
			return (0 != LogicHandler.typeGetProperty(__this,
				StaticClassProperty.BOOLEAN_ROOT_IS_OBJECT)) &&
				ourDims < otherDims;
		}
		
		// Is not an instance
		return false;
	}
	
	/**
	 * Performs the same logic as {@link Class#isAssignableFrom(Class)}, 
	 * checks if the given class can be assigned to this one. The check is
	 * in the same order as {@code instanceof Object} that is
	 * {@code b.getClass().isAssignableFrom(a.getClass()) == (a instanceof b)}
	 * and {@code (Class<B>)a} does not throw {@link ClassCastException}.
	 * 
	 * @param __this The basis class
	 * @param __other The target class which is checked for assignment.
	 * @return If the given 
	 * @throws MLECallError On null arguments.
	 * @since 2021/02/07
	 */
	public static boolean isAssignableFrom(TypeBracket __this,
		TypeBracket __other)
		throws MLECallError
	{
		return LLETypeShelf.isAssignableFrom(
			Assembly.objectToPointer(__this),
			Assembly.objectToPointer(__other));
	}
	
	/**
	 * Checks if the given class is initialized.
	 * 
	 * @param __type The class info to initialize.
	 * @return If the class is initialized.
	 * @throws MLECallError If the type is not valid.
	 * @since 2021/01/20
	 */
	public static boolean isClassInit(TypeBracket __type)
		throws MLECallError
	{
		if (__type == null)
			throw new MLECallError("NARG");
		
		// If this is an array, we just care if the root class is initialized
		// since arrays are for the most part synthetic
		if (LLETypeShelf.isArray(__type))
			return LLETypeShelf.isClassInit(Assembly.pointerToTypeBracket(
				LogicHandler.typeGetProperty(__type,
					ClassProperty.TYPEBRACKET_ROOT_COMPONENT)));
		
		// Debug
		Debugging.debugNote("isClassInit(%s)?",
			TypeShelf.binaryName(__type));
		
		// Otherwise check if the type is initialized
		return LogicHandler.typeGetProperty(__type,
			ClassProperty.BOOLEAN_IS_INITIALIZED) != 0;
	}
	
	/**
	 * Checks if this is an enumeration.
	 * 
	 * @param __type The type to check.
	 * @return If this is an enumeration.
	 * @throws MLECallError If {@code __type} is {@code null}.
	 * @since 2020/06/28
	 */
	public static boolean isEnum(TypeBracket __type)
		throws MLECallError
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Returns true if this type is an interface.
	 *
	 * @param __type The type to check.
	 * @return If this is an interface.
	 * @throws MLECallError If {@code __type} is {@code null}.
	 * @since 2020/06/07
	 */
	public static boolean isInterface(TypeBracket __type)
		throws MLECallError
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Returns true if this is a primitive type.
	 *
	 * @param __type The type to check.
	 * @return If this is a primitive.
	 * @throws MLECallError If {@code __type} is {@code null}.
	 * @since 2020/06/07
	 */
	public static boolean isPrimitive(TypeBracket __type)
		throws MLECallError
	{
		if (__type == null)
			throw new MLECallError("NARG");
		
		// Does this class say it is a primitive type?
		return LogicHandler.typeGetProperty(__type,
			StaticClassProperty.BOOLEAN_IS_PRIMITIVE) != 0;
	}
	
	/**
	 * Returns the type of the given object.
	 *
	 * @param __o The object to get the type of.
	 * @return The type of the given object.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/06/02
	 */
	public static int objectType(int __o)
	{
		if (__o == 0)
			throw new NullPointerException("NARG");
			
		// Directly read the type
		return Assembly.memHandleReadInt(__o, LogicHandler.staticVmAttribute(
			StaticVmAttribute.OFFSETOF_OBJECT_TYPE_FIELD));
	}
	
	/**
	 * Returns the type of the given object.
	 *
	 * @param __o The object to get the type of.
	 * @return The type of the given object.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/06/02
	 */
	public static TypeBracket objectType(Object __o)
	{
		return Assembly.pointerToTypeBracket(LLETypeShelf.objectType(
			Assembly.objectToPointer(__o)));
	}
	
	/**
	 * Returns the run-time name of a type as it would be if
	 * {@link Class#getName()} were called.
	 *
	 * @param __type The type to get the name of.
	 * @return The runtime name of the class.
	 * @throws MLECallError If {@code __type} is {@code null}.
	 * @since 2020/06/07
	 */
	public static String runtimeName(TypeBracket __type)
		throws MLECallError
	{
		// {@squirreljme.error ZZ4x No type specified.}
		if (__type == null)
			throw new MLECallError("ZZ4x");
		
		return (String)Assembly.pointerToObject(
			LogicHandler.typeGetProperty(__type,
			ClassProperty.MEMHANDLE_THIS_NAME_RUNTIME));
	}
	
	/**
	 * Returns the super class of this class.
	 *
	 * @param __type The super class of this type.
	 * @return Return the super class or {@code null} on null arguments.
	 * @throws MLECallError If {@code __type} is {@code null}.
	 * @since 2020/06/07
	 */
	public static TypeBracket superClass(TypeBracket __type)
		throws MLECallError
	{
		if (__type == null)
			throw new MLECallError("NARG");
		
		return Assembly.pointerToTypeBracket(
			LogicHandler.typeGetProperty(__type,
				ClassProperty.TYPEBRACKET_SUPER));
	}
	
	/**
	 * Returns the type holder for the {@code boolean} primitive type.
	 *
	 * @return The type for the primitive type.
	 * @since 2020/05/30
	 */
	public static TypeBracket typeOfBoolean()
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Returns the type holder for the {@code byte} primitive type.
	 *
	 * @return The type for the primitive type.
	 * @since 2020/05/30
	 */
	public static TypeBracket typeOfByte()
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Returns the type holder for the {@code short} primitive type.
	 *
	 * @return The type for the primitive type.
	 * @since 2020/05/30
	 */
	public static TypeBracket typeOfShort()
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Returns the type holder for the {@code char} primitive type.
	 *
	 * @return The type for the primitive type.
	 * @since 2020/05/30
	 */
	public static TypeBracket typeOfCharacter()
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Returns the type holder for the {@code int} primitive type.
	 *
	 * @return The type for the primitive type.
	 * @since 2020/05/30
	 */
	public static TypeBracket typeOfInteger()
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Returns the type holder for the {@code long} primitive type.
	 *
	 * @return The type for the primitive type.
	 * @since 2020/05/30
	 */
	public static TypeBracket typeOfLong()
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Returns the type holder for the {@code float} primitive type.
	 *
	 * @return The type for the primitive type.
	 * @since 2020/05/30
	 */
	public static TypeBracket typeOfFloat()
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Returns the type holder for the {@code double} primitive type.
	 *
	 * @return The type for the primitive type.
	 * @since 2020/05/30
	 */
	public static TypeBracket typeOfDouble()
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Gets the class type of the given bracket.
	 *
	 * @param <T> Ignored.
	 * @param __type The type to get the class object of.
	 * @return The class type for the given type.
	 * @since 2020/05/30
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> typeToClass(TypeBracket __type)
	{
		// {@squirreljme.error ZZ73 No type specified.}
		if (__type == null)
			throw new MLECallError("ZZ73");
		
		// TODO: Atomically protect this
		Debugging.todoNote("Protect typeToClass()!");
		
		// Is a property already valid here?
		int rawP = LogicHandler.typeGetProperty(__type,
			ClassProperty.MEMHANDLE_LANG_CLASS_INSTANCE);
		if (rawP != 0)
			return (Class<T>)Assembly.pointerToObject(rawP);
		
		// Create new class instance to fill int
		Object classObj = ObjectShelf.newInstance(
			Assembly.pointerToTypeBracket(LogicHandler.staticVmAttribute(
			StaticVmAttribute.TYPEBRACKET_CLASS)));
		
		// TODO: Initialize class
		Debugging.todoNote("Initialize Class!");
		
		// Store for later and use this one
		LogicHandler.typeSetProperty(__type,
			ClassProperty.MEMHANDLE_LANG_CLASS_INSTANCE,
			Assembly.objectToPointer(classObj));
		return (Class<T>)classObj;
	}
}
