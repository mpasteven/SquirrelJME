// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.runtime.cldc.lang;

import cc.squirreljme.jvm.mle.ObjectShelf;
import cc.squirreljme.jvm.mle.TypeShelf;

/**
 * This method contains the code which is able to initialize multi-dimensional
 * arrays and perform other array related tasks.
 *
 * @since 2018/11/03
 */
public final class ArrayUtils
{
	/** Boolean array. */
	public static final byte ARRAY_BOOLEAN =
		1;
	
	/** Byte array. */
	public static final byte ARRAY_BYTE =
		2;
	
	/** Short array. */
	public static final byte ARRAY_SHORT =
		3;
	
	/** Character array. */
	public static final byte ARRAY_CHARACTER =
		4;
	
	/** Integer array. */
	public static final byte ARRAY_INTEGER =
		5;
	
	/** Long array. */
	public static final byte ARRAY_LONG =
		6;
	
	/** Float array. */
	public static final byte ARRAY_FLOAT =
		7;
	
	/** Double array. */
	public static final byte ARRAY_DOUBLE =
		8;
	
	/** Object array. */
	public static final byte ARRAY_OBJECT =
		9;
	
	/**
	 * Not used.
	 *
	 * @since 2018/11/03
	 */
	private ArrayUtils()
	{
	}
	
	/**
	 * Sets the value in the array.
	 *
	 * @param __a The array.
	 * @param __dx The index to set.
	 * @param __v The value to store.
	 * @throws ArrayIndexOutOfBoundsException If the index is out of bounds.
	 * @throws ClassCastException If the wrong class is used.
	 * @throws IllegalArgumentException If the argument is invalid.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/12/13
	 */
	public static void arraySet(Object __a, int __dx, Object __v)
		throws ArrayIndexOutOfBoundsException, ClassCastException,
			IllegalArgumentException, NullPointerException
	{
		ArrayUtils.arraySet(ArrayUtils.arrayType(__a), __a, __dx, __v);
	}
	
	/**
	 * Sets the value in the array.
	 *
	 * @param __type The type of array used.
	 * @param __a The array.
	 * @param __dx The index to set.
	 * @param __v The value to store.
	 * @throws ArrayIndexOutOfBoundsException If the index is out of bounds.
	 * @throws ClassCastException If the wrong class is used.
	 * @throws IllegalArgumentException If the argument is invalid.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/12/13
	 */
	public static void arraySet(int __type, Object __a, int __dx,
		Object __v)
		throws ArrayIndexOutOfBoundsException, ClassCastException,
			IllegalArgumentException, NullPointerException
	{
		if (__a == null)
			throw new NullPointerException("NARG");
		
		// Depends on the type
		switch (__type)
		{
			case ArrayUtils.ARRAY_BOOLEAN:
				((boolean[])__a)[__dx] = (Boolean)__v;
				break;
				
			case ArrayUtils.ARRAY_BYTE:
				((byte[])__a)[__dx] = (Byte)__v;
				break;
				
			case ArrayUtils.ARRAY_SHORT:
				((short[])__a)[__dx] = (Short)__v;
				break;
				
			case ArrayUtils.ARRAY_CHARACTER:
				((char[])__a)[__dx] = (Character)__v;
				break;
				
			case ArrayUtils.ARRAY_INTEGER:
				((int[])__a)[__dx] = (Integer)__v;
				break;
				
			case ArrayUtils.ARRAY_LONG:
				((long[])__a)[__dx] = (Long)__v;
				break;
				
			case ArrayUtils.ARRAY_FLOAT:
				((float[])__a)[__dx] = (Float)__v;
				break;
				
			case ArrayUtils.ARRAY_DOUBLE:
				((double[])__a)[__dx] = (Double)__v;
				break;
				
			case ArrayUtils.ARRAY_OBJECT:
				((Object[])__a)[__dx] = __v;
				break;
			
				// {@squirreljme.error ZZ0c Invalid array type.}
			default:
				throw new IllegalArgumentException("ZZ0c");
		}
	}
	
	/**
	 * Returns the type of array the object is.
	 *
	 * @param __a The array type.
	 * @return The type of array this is.
	 * @throws IllegalArgumentException If not an array.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/12/13
	 */
	public static int arrayType(Object __a)
		throws IllegalArgumentException, NullPointerException
	{
		if (__a == null)
			throw new NullPointerException("NARG");
		
		if (__a instanceof Object[])
			return ArrayUtils.ARRAY_OBJECT;
		else if (__a instanceof boolean[])
			return ArrayUtils.ARRAY_BOOLEAN;
		else if (__a instanceof byte[])
			return ArrayUtils.ARRAY_BYTE;
		else if (__a instanceof short[])
			return ArrayUtils.ARRAY_SHORT;
		else if (__a instanceof char[])
			return ArrayUtils.ARRAY_CHARACTER;
		else if (__a instanceof int[])
			return ArrayUtils.ARRAY_INTEGER;
		else if (__a instanceof long[])
			return ArrayUtils.ARRAY_LONG;
		else if (__a instanceof float[])
			return ArrayUtils.ARRAY_FLOAT;
		else if (__a instanceof double[])
			return ArrayUtils.ARRAY_DOUBLE;
		
		// {@squirreljme.error ZZ0d Invalid array type. (The type)}
		throw new IllegalArgumentException("ZZ0d " + __a.getClass().getName());
	}
	
	/**
	 * Allocates a new multi-dimensional array.
	 *
	 * @param __type The type with minimum dimension sizes specified.
	 * @param __skip The initial number of dimensions to skip in the initial
	 * array.
	 * @param __a Length of dimension.
	 * @return The allocated multi-dimensional array.
	 * @throws NegativeArraySizeException If an allocated array would be
	 * of a negative size.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/05/04
	 */
	public static Object multiANewArray(Class<?> __type, int __skip,
		int __a)
		throws NegativeArraySizeException, NullPointerException
	{
		return ArrayUtils.multiANewArray(__type, __skip,
			new int[]{__a});
	}
	
	/**
	 * Allocates a new multi-dimensional array.
	 *
	 * @param __type The type with minimum dimension sizes specified.
	 * @param __skip The initial number of dimensions to skip in the initial
	 * array.
	 * @param __a Length of dimension.
	 * @param __b Length of dimension.
	 * @return The allocated multi-dimensional array.
	 * @throws NegativeArraySizeException If an allocated array would be
	 * of a negative size.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/05/04
	 */
	public static Object multiANewArray(Class<?> __type, int __skip,
		int __a, int __b)
		throws NegativeArraySizeException, NullPointerException
	{
		return ArrayUtils.multiANewArray(__type, __skip,
			new int[]{__a, __b});
	}
	
	/**
	 * Allocates a new multi-dimensional array.
	 *
	 * @param __type The type with minimum dimension sizes specified.
	 * @param __skip The initial number of dimensions to skip in the initial
	 * array.
	 * @param __a Length of dimension.
	 * @param __b Length of dimension.
	 * @param __c Length of dimension.
	 * @return The allocated multi-dimensional array.
	 * @throws NegativeArraySizeException If an allocated array would be
	 * of a negative size.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/05/04
	 */
	public static Object multiANewArray(Class<?> __type, int __skip,
		int __a, int __b, int __c)
		throws NegativeArraySizeException, NullPointerException
	{
		return ArrayUtils.multiANewArray(__type, __skip,
			new int[]{__a, __b, __c});
	}
	
	/**
	 * Allocates a new multi-dimensional array.
	 *
	 * @param __type The type with minimum dimension sizes specified.
	 * @param __skip The initial number of dimensions to skip in the initial
	 * array.
	 * @param __a Length of dimension.
	 * @param __b Length of dimension.
	 * @param __c Length of dimension.
	 * @param __d Length of dimension.
	 * @return The allocated multi-dimensional array.
	 * @throws NegativeArraySizeException If an allocated array would be
	 * of a negative size.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/05/04
	 */
	public static Object multiANewArray(Class<?> __type, int __skip,
		int __a, int __b, int __c, int __d)
		throws NegativeArraySizeException, NullPointerException
	{
		return ArrayUtils.multiANewArray(__type, __skip,
			new int[]{__a, __b, __c, __d});
	}
	
	/**
	 * Allocates a new multi-dimensional array.
	 *
	 * @param __type The type with minimum dimension sizes specified.
	 * @param __skip The initial number of dimensions to skip in the initial
	 * array.
	 * @param __a Length of dimension.
	 * @param __b Length of dimension.
	 * @param __c Length of dimension.
	 * @param __d Length of dimension.
	 * @param __e Length of dimension.
	 * @return The allocated multi-dimensional array.
	 * @throws NegativeArraySizeException If an allocated array would be
	 * of a negative size.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/05/04
	 */
	public static Object multiANewArray(Class<?> __type, int __skip,
		int __a, int __b, int __c, int __d, int __e)
		throws NegativeArraySizeException, NullPointerException
	{
		return ArrayUtils.multiANewArray(__type, __skip,
			new int[]{__a, __b, __c, __d, __e});
	}
	
	/**
	 * Allocates a new multi-dimensional array.
	 *
	 * @param __type The type with minimum dimension sizes specified.
	 * @param __skip The initial number of dimensions to skip in the initial
	 * array.
	 * @param __a Length of dimension.
	 * @param __b Length of dimension.
	 * @param __c Length of dimension.
	 * @param __d Length of dimension.
	 * @param __e Length of dimension.
	 * @param __f Length of dimension.
	 * @return The allocated multi-dimensional array.
	 * @throws NegativeArraySizeException If an allocated array would be
	 * of a negative size.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/05/04
	 */
	public static Object multiANewArray(Class<?> __type, int __skip,
		int __a, int __b, int __c, int __d, int __e, int __f)
		throws NegativeArraySizeException, NullPointerException
	{
		return ArrayUtils.multiANewArray(__type, __skip,
			new int[]{__a, __b, __c, __d, __e, __f});
	}
	
	/**
	 * Allocates a new multi-dimensional array.
	 *
	 * @param __type The type with minimum dimension sizes specified.
	 * @param __skip The initial number of dimensions to skip in the initial
	 * array.
	 * @param __a Length of dimension.
	 * @param __b Length of dimension.
	 * @param __c Length of dimension.
	 * @param __d Length of dimension.
	 * @param __e Length of dimension.
	 * @param __f Length of dimension.
	 * @param __g Length of dimension.
	 * @return The allocated multi-dimensional array.
	 * @throws NegativeArraySizeException If an allocated array would be
	 * of a negative size.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/05/04
	 */
	public static Object multiANewArray(Class<?> __type, int __skip,
		int __a, int __b, int __c, int __d, int __e, int __f, int __g)
		throws NegativeArraySizeException, NullPointerException
	{
		return ArrayUtils.multiANewArray(__type, __skip,
			new int[]{__a, __b, __c, __d, __e, __f, __g});
	}
	
	/**
	 * Allocates a new multi-dimensional array.
	 *
	 * @param __type The type with minimum dimension sizes specified.
	 * @param __skip The initial number of dimensions to skip in the initial
	 * array.
	 * @param __a Length of dimension.
	 * @param __b Length of dimension.
	 * @param __c Length of dimension.
	 * @param __d Length of dimension.
	 * @param __e Length of dimension.
	 * @param __f Length of dimension.
	 * @param __g Length of dimension.
	 * @param __h Length of dimension.
	 * @return The allocated multi-dimensional array.
	 * @throws NegativeArraySizeException If an allocated array would be
	 * of a negative size.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/05/04
	 */
	public static Object multiANewArray(Class<?> __type, int __skip,
		int __a, int __b, int __c, int __d, int __e, int __f, int __g,
		int __h)
		throws NegativeArraySizeException, NullPointerException
	{
		return ArrayUtils.multiANewArray(__type, __skip,
			new int[]{__a, __b, __c, __d, __e, __f, __g, __h});
	}
	
	/**
	 * Allocates a new multi-dimensional array.
	 *
	 * @param __type The type with minimum dimension sizes specified.
	 * @param __skip The initial number of dimensions to skip in the initial
	 * array.
	 * @param __a Length of dimension.
	 * @param __b Length of dimension.
	 * @param __c Length of dimension.
	 * @param __d Length of dimension.
	 * @param __e Length of dimension.
	 * @param __f Length of dimension.
	 * @param __g Length of dimension.
	 * @param __h Length of dimension.
	 * @param __i Length of dimension.
	 * @return The allocated multi-dimensional array.
	 * @throws NegativeArraySizeException If an allocated array would be
	 * of a negative size.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/05/04
	 */
	public static Object multiANewArray(Class<?> __type, int __skip,
		int __a, int __b, int __c, int __d, int __e, int __f, int __g,
		int __h, int __i)
		throws NegativeArraySizeException, NullPointerException
	{
		return ArrayUtils.multiANewArray(__type, __skip,
			new int[]{__a, __b, __c, __d, __e, __f, __g, __h, __i});
	}
	
	/**
	 * Allocates a new multi-dimensional array.
	 *
	 * @param __type The type with minimum dimension sizes specified.
	 * @param __skip The initial number of dimensions to skip in the initial
	 * array.
	 * @param __a Length of dimension.
	 * @param __b Length of dimension.
	 * @param __c Length of dimension.
	 * @param __d Length of dimension.
	 * @param __e Length of dimension.
	 * @param __f Length of dimension.
	 * @param __g Length of dimension.
	 * @param __h Length of dimension.
	 * @param __i Length of dimension.
	 * @param __j Length of dimension.
	 * @return The allocated multi-dimensional array.
	 * @throws NegativeArraySizeException If an allocated array would be
	 * of a negative size.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/05/04
	 */
	public static Object multiANewArray(Class<?> __type, int __skip,
		int __a, int __b, int __c, int __d, int __e, int __f, int __g,
		int __h, int __i, int __j)
		throws NegativeArraySizeException, NullPointerException
	{
		return ArrayUtils.multiANewArray(__type, __skip,
			new int[]{__a, __b, __c, __d, __e, __f, __g, __h, __i, __j});
	}
	
	/**
	 * Allocates a new multi-dimensional array.
	 *
	 * @param __type The type with minimum dimension sizes specified.
	 * @param __skip The initial number of dimensions to skip in the initial
	 * array.
	 * @param __dims The dimensions and the number of them to use.
	 * @return The allocated multi-dimensional array.
	 * @throws NegativeArraySizeException If an allocated array would be
	 * of a negative size.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/03
	 */
	public static Object multiANewArray(Class<?> __type, int __skip,
		int[] __dims)
		throws NegativeArraySizeException, NullPointerException
	{
		if (__type == null || __dims == null)
			throw new NullPointerException("NARG");
		
		// Count the number of dimensions represented in the type
		String typename = __type.getName();
		int typeDims = 0;
		while (typename.charAt(typeDims) == '[')
			typeDims++;
		
		// {@squirreljme.error ZZ0e Negative number of dimensions available
		// or input type is not correct for the array type.}
		int dims = __dims.length - __skip;
		if (__skip < 0 || dims <= 0 || typeDims < dims)
			throw new IllegalArgumentException("ZZ0e");
		
		// Allocate array of this type
		int numElem = __dims[__skip];
		Object rv = ObjectShelf.arrayNew(
			TypeShelf.classToType(__type), numElem);
		
		// Need to determine the type for setting
		int type = ArrayUtils.arrayType(rv);
		
		// The array has more dimensions which must be set
		if (dims > 1)
		{
			// Remove a single brace from the class to cut down its dimensions
			// Class names use dot forms, so refer to classes using the class
			// handler.
			Class<?> subtype;
			try
			{
				subtype = Class.forName(typename.substring(1));
			}
			
			// {@squirreljme.error ZZ0f Could not find the sub-type for
			// multi-dimensional array.}
			catch (ClassNotFoundException e)
			{
				throw new Error("ZZ0f", e);
			}
			
			// Skipping ahead by one
			int nextSkip = __skip + 1;
			
			// Allocate
			for (int i = 0; i < numElem; i++)
				ArrayUtils.arraySet(type, rv, i,
					ArrayUtils.multiANewArray(subtype, nextSkip, __dims));
		}
		
		return rv;
	}
}

