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
import cc.squirreljme.jvm.mle.TypeShelf;
import cc.squirreljme.jvm.mle.brackets.TypeBracket;
import cc.squirreljme.jvm.mle.constants.MonitorResultType;
import cc.squirreljme.jvm.mle.exceptions.MLECallError;
import cc.squirreljme.jvm.summercoat.LogicHandler;
import cc.squirreljme.jvm.summercoat.SystemCall;
import cc.squirreljme.jvm.summercoat.constants.ClassProperty;
import cc.squirreljme.jvm.summercoat.constants.StaticClassProperty;
import cc.squirreljme.jvm.summercoat.constants.StaticVmAttribute;
import cc.squirreljme.runtime.cldc.debug.Debugging;

/**
 * This shelf supports object anything that has to do with objects.
 *
 * @since 2020/06/09
 */
public final class LLEObjectShelf
{
	/**
	 * Not used.
	 *
	 * @since 2020/06/09
	 */
	private LLEObjectShelf()
	{
	}
	
	/**
	 * Checks if the given object can be stored in the specified array.
	 * 
	 * @param __array The array to check.
	 * @param __value The value to check
	 * @return If the value can be stored in the given array.
	 * @throws MLECallError If given type is not an array or {@code __array}
	 * is {@code null}.
	 * @since 2021/02/07
	 */
	public static boolean arrayCheckStore(int __array, int __value)
		throws MLECallError
	{
		if (__array == 0)
			throw new MLECallError("NARG");
		
		// Get the type that the array is
		int arrayType = LLETypeShelf.objectType(__array);
		
		// {@squirreljme.error ZZ4o Object is not an array.}
		if (LogicHandler.typeGetProperty(arrayType,
			StaticClassProperty.NUM_DIMENSIONS) <= 0)
			throw new MLECallError("ZZ4o");
		
		// Storing null is always valid
		if (__value == 0)
			return true;
		
		// The component type of the array must be compatible with the
		// target type
		int targetType = LLETypeShelf.objectType(__value);
		return LLETypeShelf.isAssignableFrom(
			LogicHandler.typeGetProperty(arrayType,
				ClassProperty.TYPEBRACKET_COMPONENT),
			targetType);
	}
	
	/**
	 * Checks if the given object can be stored in the specified array.
	 * 
	 * @param __array The array to check.
	 * @param __value The value to check
	 * @return If the value can be stored in the given array.
	 * @throws MLECallError If given type is not an array or {@code __array}
	 * is {@code null}.
	 * @since 2021/02/07
	 */
	public static boolean arrayCheckStore(Object __array, Object __value)
		throws MLECallError
	{
		return LLEObjectShelf.arrayCheckStore(
			Assembly.objectToPointer(__array),
			Assembly.objectToPointer(__value));
	}
	
	/**
	 * Copies the given arrays. If the source and destination are the same
	 * array, the copy operation will not collide with itself.
	 * 
	 * @param __src The source array.
	 * @param __srcOff The source offset.
	 * @param __dest The destination array.
	 * @param __destOff The destination offset.
	 * @param __len The elements to copy.
	 * @since 2020/06/22
	 */
	public static void arrayCopy(boolean[] __src, int __srcOff,
		boolean[] __dest, int __destOff, int __len)
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Copies the given arrays. If the source and destination are the same
	 * array, the copy operation will not collide with itself.
	 * 
	 * @param __src The source array.
	 * @param __srcOff The source offset.
	 * @param __dest The destination array.
	 * @param __destOff The destination offset.
	 * @param __len The elements to copy.
	 * @since 2020/06/22
	 */
	public static void arrayCopy(byte[] __src, int __srcOff,
		byte[] __dest, int __destOff, int __len)
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Copies the given arrays. If the source and destination are the same
	 * array, the copy operation will not collide with itself.
	 * 
	 * @param __src The source array.
	 * @param __srcOff The source offset.
	 * @param __dest The destination array.
	 * @param __destOff The destination offset.
	 * @param __len The elements to copy.
	 * @since 2020/06/22
	 */
	public static void arrayCopy(short[] __src, int __srcOff,
		short[] __dest, int __destOff, int __len)
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Copies the given arrays. If the source and destination are the same
	 * array, the copy operation will not collide with itself.
	 * 
	 * @param __src The source array.
	 * @param __srcOff The source offset.
	 * @param __dest The destination array.
	 * @param __destOff The destination offset.
	 * @param __len The elements to copy.
	 * @since 2020/06/22
	 */
	public static void arrayCopy(char[] __src, int __srcOff,
		char[] __dest, int __destOff, int __len)
	{
		int arrayBase = SystemCall.arrayAllocationBase();
		SystemCall.memHandleMove(__src, arrayBase + (2 * __srcOff),
			__dest, arrayBase + (2 * __destOff), (2 * __len));
	}
	
	/**
	 * Copies the given arrays. If the source and destination are the same
	 * array, the copy operation will not collide with itself.
	 * 
	 * @param __src The source array.
	 * @param __srcOff The source offset.
	 * @param __dest The destination array.
	 * @param __destOff The destination offset.
	 * @param __len The elements to copy.
	 * @since 2020/06/22
	 */
	public static void arrayCopy(int[] __src, int __srcOff,
		int[] __dest, int __destOff, int __len)
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Copies the given arrays. If the source and destination are the same
	 * array, the copy operation will not collide with itself.
	 * 
	 * @param __src The source array.
	 * @param __srcOff The source offset.
	 * @param __dest The destination array.
	 * @param __destOff The destination offset.
	 * @param __len The elements to copy.
	 * @since 2020/06/22
	 */
	public static void arrayCopy(long[] __src, int __srcOff,
		long[] __dest, int __destOff, int __len)
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Copies the given arrays. If the source and destination are the same
	 * array, the copy operation will not collide with itself.
	 * 
	 * @param __src The source array.
	 * @param __srcOff The source offset.
	 * @param __dest The destination array.
	 * @param __destOff The destination offset.
	 * @param __len The elements to copy.
	 * @since 2020/06/22
	 */
	public static void arrayCopy(float[] __src, int __srcOff,
		float[] __dest, int __destOff, int __len)
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Copies the given arrays. If the source and destination are the same
	 * array, the copy operation will not collide with itself.
	 * 
	 * @param __src The source array.
	 * @param __srcOff The source offset.
	 * @param __dest The destination array.
	 * @param __destOff The destination offset.
	 * @param __len The elements to copy.
	 * @since 2020/06/22
	 */
	public static void arrayCopy(double[] __src, int __srcOff,
		double[] __dest, int __destOff, int __len)
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Returns the length of the array if this object is an array.
	 *
	 * @param __object The object to get the length of.
	 * @return The length of the array or a negative value if this is not an
	 * array.
	 * @since 2020/06/09
	 */
	public static int arrayLength(Object __object)
	{
		if (__object == null)
			throw new MLECallError("NARG");
		
		// If not an array, return a negative value from it
		TypeBracket type = TypeShelf.objectType(__object);
		if (!TypeShelf.isArray(type))
			return -1;
		
		// Directly read the length attribute
		return Assembly.memHandleReadInt(__object,
			LogicHandler.staticVmAttribute(
				StaticVmAttribute.OFFSETOF_ARRAY_LENGTH_FIELD));
	}
	
	/**
	 * Allocates a new array.
	 *
	 * @param <T> The resultant type of the array.
	 * @param __type The type to allocate the array for.
	 * @param __len The length of the array.
	 * @return The newly allocated array as the given object.
	 * @since 2020/06/09
	 */
	@SuppressWarnings("unchecked")
	public static <T> T arrayNew(TypeBracket __type, int __len)
	{
		if (__type == null)
			throw new MLECallError("NARG");
		
		if (__len < 0)
			throw new NegativeArraySizeException("" + __type);
		
		// Determine how large the object needs to be
		int allocBase = LogicHandler.typeGetProperty(__type,
			ClassProperty.SIZE_ALLOCATION);
		int allocSize = allocBase +
			(__len * LogicHandler.typeGetProperty(__type,
				ClassProperty.INT_COMPONENT_CELL_SIZE));
		
		// Allocate the object
		Object rv = LLEObjectShelf.__allocObject(__type, allocSize);
		
		// Set the length of the array
		Assembly.memHandleWriteInt(rv, LogicHandler.staticVmAttribute(
			StaticVmAttribute.OFFSETOF_ARRAY_LENGTH_FIELD), __len);
		
		return (T)rv;
	}
	
	/**
	 * Checks if the given thread holds the lock on the given method.
	 *
	 * @param __javaThread The Java thread to check if it holds the lock.
	 * @param __o The object to check.
	 * @return If the given thread holds the lock.
	 * @since 2020/06/17
	 */
	public static boolean holdsLock(Thread __javaThread, Object __o)
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Returns the identity hashcode of the object.
	 *
	 * @param __o The object to get the hashcode of.
	 * @return The identity hashcode of the object.
	 * @since 2020/06/18
	 */
	public static int identityHashCode(Object __o)
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Checks if this object is an array.
	 * 
	 * @param __object The object to check.
	 * @return If this object is an array.
	 * @since 2021/04/07
	 */
	public static boolean isArray(Object __object)
	{
		if (__object == null)
			throw new MLECallError("NARG");
			
		return LLEObjectShelf.arrayLength(__object) > 0;
	}
	
	/**
	 * Checks if this object is an instance of the given type.
	 * 
	 * @param __o The object to check.
	 * @param __type The type it may be.
	 * @return If this object is an instance of the given type.
	 * @throws MLECallError If {@code __type} is null.
	 * @since 2021/02/07
	 */
	public static boolean isInstance(int __o, int __type)
		throws MLECallError
	{
		if (__type == 0)
			throw new MLECallError("NARG");
		
		// Null objects are never an instance of anything
		if (__o == 0)
			return false;
		
		// Perform assignment check
		return LLETypeShelf.isAssignableFrom(
			__type, LLETypeShelf.objectType(__o));
	}
	
	/**
	 * Checks if this object is an instance of the given type.
	 * 
	 * @param __o The object to check.
	 * @param __type The type it may be.
	 * @return If this object is an instance of the given type.
	 * @throws MLECallError If {@code __type} is null.
	 * @since 2021/02/07
	 */
	public static boolean isInstance(Object __o, TypeBracket __type)
		throws MLECallError
	{
		return LLEObjectShelf.isInstance(Assembly.objectToPointer(__o),
			Assembly.objectToPointer(__type));
	}
	
	/**
	 * Creates a new instance of the given type.
	 *
	 * @param __type The type to instantiate.
	 * @return The newly created object or {@code null} if there was no
	 * memory left.
	 * @since 2020/06/17
	 */
	public static Object newInstance(TypeBracket __type)
	{
		if (__type == null)
			throw new MLECallError("NARG");
		
		// {@squirreljme.error ZZ4j Class has no allocated size?}
		int allocSize = LogicHandler.typeGetProperty(__type,
			ClassProperty.SIZE_ALLOCATION);
		if (allocSize <= 0)
			throw new MLECallError("ZZ4j");
		
		// Allocate the object
		return LLEObjectShelf.__allocObject(__type, allocSize);
	}
	
	/**
	 * Notifies the monitors holding onto this object.
	 * 
	 * @param __object The object to signal.
	 * @param __all Notify all threads?
	 * @return The {@link MonitorResultType}.
	 * @since 2020/06/22
	 */
	public static int notify(Object __object, boolean __all)
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Waits on the given monitor.
	 * 
	 * @param __object The object to wait on.
	 * @param __ms The milliseconds to wait.
	 * @param __ns The nanoseconds to wait.
	 * @return The {@link MonitorResultType}.
	 * @since 2020/06/22
	 */
	public static int wait(Object __object, long __ms, int __ns)
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Allocates an object and seeds initial information regarding it.
	 * 
	 * @param __info The class information.
	 * @param __allocSize The allocation size of the object.
	 * @return The allocated object.
	 * @throws NullPointerException On null arguments.
	 * @throws OutOfMemoryError If not enough memory is available.
	 * @since 2021/01/23
	 */
	private static Object __allocObject(TypeBracket __info,
		int __allocSize)
		throws NullPointerException, OutOfMemoryError
	{
		if (__info == null)
			throw new NullPointerException("NARG");
		
		// This represents the kind of handle that gets allocated
		int memHandleKind = LogicHandler.typeGetProperty(__info,
			ClassProperty.INT_MEMHANDLE_KIND);
		
		// Attempt to allocate a handle
		int rv = SystemCall.memHandleNew(memHandleKind, __allocSize);
		if (rv == 0)
		{
			// Attempt garbage collection
			System.gc();
			
			// Try again, but fail if it still fails
			rv = SystemCall.memHandleNew(memHandleKind, __allocSize);
			if (rv == 0)
				throw new OutOfMemoryError();
		}
		
		// Set the object type information
		Assembly.memHandleWriteObject(rv, LogicHandler.staticVmAttribute(
			StaticVmAttribute.OFFSETOF_OBJECT_TYPE_FIELD), __info);
		
		// Convert to represented object before returning.
		return Assembly.pointerToObject(rv);
	}
}
