// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.mle;

import cc.squirreljme.jvm.mle.brackets.TypeBracket;
import cc.squirreljme.jvm.mle.constants.MonitorResultType;
import cc.squirreljme.jvm.mle.exceptions.MLECallError;

/**
 * This shelf supports object anything that has to do with objects.
 *
 * @since 2020/06/09
 */
public final class ObjectShelf
{
	/**
	 * Not used.
	 *
	 * @since 2020/06/09
	 */
	private ObjectShelf()
	{
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
	public static native void arrayCopy(boolean[] __src, int __srcOff,
		boolean[] __dest, int __destOff, int __len);
	
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
	public static native void arrayCopy(byte[] __src, int __srcOff,
		byte[] __dest, int __destOff, int __len);
	
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
	public static native void arrayCopy(short[] __src, int __srcOff,
		short[] __dest, int __destOff, int __len);
	
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
	public static native void arrayCopy(char[] __src, int __srcOff,
		char[] __dest, int __destOff, int __len);
	
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
	public static native void arrayCopy(int[] __src, int __srcOff,
		int[] __dest, int __destOff, int __len);
	
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
	public static native void arrayCopy(long[] __src, int __srcOff,
		long[] __dest, int __destOff, int __len);
	
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
	public static native void arrayCopy(float[] __src, int __srcOff,
		float[] __dest, int __destOff, int __len);
	
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
	public static native void arrayCopy(double[] __src, int __srcOff,
		double[] __dest, int __destOff, int __len);
	
	/**
	 * Returns the length of the array if this object is an array.
	 *
	 * @param __object The object to get the length of.
	 * @return The length of the array or a negative value if this is not an
	 * array.
	 * @since 2020/06/09
	 */
	public static native int arrayLength(Object __object);
	
	/**
	 * Allocates a new array.
	 *
	 * @param <T> The resultant type of the array.
	 * @param __type The type to allocate the array for.
	 * @param __len The length of the array.
	 * @return The newly allocated array as the given object.
	 * @since 2020/06/09
	 */
	public static native <T> T arrayNew(TypeBracket __type, int __len);
	
	/**
	 * Checks if the given thread holds the lock on the given method.
	 *
	 * @param __javaThread The Java thread to check if it holds the lock.
	 * @param __o The object to check.
	 * @return If the given thread holds the lock.
	 * @since 2020/06/17
	 */
	public static native boolean holdsLock(Thread __javaThread, Object __o);
	
	/**
	 * Returns the identity hashcode of the object.
	 *
	 * @param __o The object to get the hashcode of.
	 * @return The identity hashcode of the object.
	 * @since 2020/06/18
	 */
	public static native int identityHashCode(Object __o);
	
	/**
	 * Creates a new instance of the given type, this has the same effect
	 * as calling
	 * {@link ObjectShelf#newInstance(TypeBracket, TypeBracket[], Object...)}
	 * with {@code newInstance(__type, new TypeBracket[0], new Object[0])};
	 *
	 * @param __type The type to instantiate.
	 * @return The newly created object or {@code null} if there was no
	 * memory left.
	 * @since 2020/06/17
	 */
	public static native Object newInstance(TypeBracket __type);
	
	/**
	 * Creates a new instance of the given object, calling the specific
	 * constructor.
	 * 
	 * @param __type The type to make a new instance of.
	 * @param __argTypes The types that the arguments make up 
	 * @param __args The arguments to the constructor.
	 * @return The newly created object or {@code null} if there was no
	 * memory left.
	 * @throws MLECallError If any argument is null (except for values within
	 * {@code __args} as a null argument may be valid, if an argument type
	 * is null, if a passed argument is not null and not an instance of the
	 * type, if the constructor does not exist, or if the argument count does
	 * not match the argument types count.
	 * @since 2020/07/14
	 */
	public static native Object newInstance(TypeBracket __type,
		TypeBracket[] __argTypes, Object... __args);
	
	/**
	 * Notifies the monitors holding onto this object.
	 * 
	 * @param __object The object to signal.
	 * @param __all Notify all threads?
	 * @return The {@link MonitorResultType}.
	 * @since 2020/06/22
	 */
	public static native int notify(Object __object, boolean __all);
	
	/**
	 * Waits on the given monitor.
	 * 
	 * @param __object The object to wait on.
	 * @param __ms The milliseconds to wait.
	 * @param __ns The nanoseconds to wait.
	 * @return The {@link MonitorResultType}.
	 * @since 2020/06/22
	 */
	public static native int wait(Object __object, long __ms, int __ns);
}
