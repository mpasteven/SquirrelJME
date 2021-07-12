// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package java.util;

import cc.squirreljme.jvm.mle.ObjectShelf;
import cc.squirreljme.jvm.mle.TypeShelf;
import cc.squirreljme.runtime.cldc.util.ByteIntegerArray;
import cc.squirreljme.runtime.cldc.util.CharacterIntegerArray;
import cc.squirreljme.runtime.cldc.util.DoubleArrayList;
import cc.squirreljme.runtime.cldc.util.FloatArrayList;
import cc.squirreljme.runtime.cldc.util.IntegerArrays;
import cc.squirreljme.runtime.cldc.util.IntegerIntegerArray;
import cc.squirreljme.runtime.cldc.util.LongArrayList;
import cc.squirreljme.runtime.cldc.util.ShellSort;
import cc.squirreljme.runtime.cldc.util.ShortIntegerArray;

/**
 * This class contains utility methods which operate on arrays.
 *
 * {@squirreljme.error ZZ2f The sort range exceeds the array bounds.}
 * {@squirreljme.error ZZ2g The from index exceeds the to index.}
 *
 * @since 2016/09/30
 */
public class Arrays
{
	/**
	 * Not used.
	 *
	 * @since 2018/11/04
	 */
	private Arrays()
	{
	}
	
	/**
	 * Wraps the specified array allowing access to its data as a fixed size
	 * list. The returned {@link List} will have {@link RandomAccess}
	 * implemented.
	 *
	 * @param <T> The type of values contained within the array.
	 * @param __a The array to wrap.
	 * @return The specified array wrapped in a {@link List}.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/08/31
	 */
	@SuppressWarnings({"unchecked"})
	public static <T> List<T> asList(T... __a)
		throws NullPointerException
	{
		// Check
		if (__a == null)
			throw new NullPointerException("NARG");
		
		// Wrap it
		return new __ArraysList__<T>(__a);
	}
	
	public static int binarySearch(long[] __a, long __b)
	{
		throw new todo.TODO();
	}
	
	public static int binarySearch(long[] __a, int __b, int __c, long __d)
	{
		throw new todo.TODO();
	}
	
	/**
	 * Searches the given sorted array for the given element.
	 *
	 * @param __a The sorted array to search.
	 * @param __key The key to locate.
	 * @return The index of the given key or {@code (-(insertion point) - 1)}
	 * indicating where the element would be found.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/10/28
	 */
	public static int binarySearch(int[] __a, int __key)
		throws NullPointerException
	{
		if (__a == null)
			throw new NullPointerException("NARG");
		
		return Arrays.binarySearch(__a, 0, __a.length, __key);
	}
	
	/**
	 * Searches the given sorted array for the given element.
	 *
	 * @param __a The sorted array to search.
	 * @param __from The from index.
	 * @param __to The to index.
	 * @param __key The key to locate.
	 * @return The index of the given key or {@code (-(insertion point) - 1)}
	 * indicating where the element would be found.
	 * @throws ArrayIndexOutOfBoundsException If the from or to index exceed
	 * the bounds of the array.
	 * @throws IllegalArgumentException If the from index is higher than the
	 * to index.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/10/28
	 */
	public static int binarySearch(int[] __a, int __from, int __to, int __key)
		throws ArrayIndexOutOfBoundsException, IllegalArgumentException,
			NullPointerException
	{
		return IntegerArrays.binarySearch(new IntegerIntegerArray(__a),
			__from, __to, __key);
	}
	
	/**
	 * Searches the given sorted array for the given element.
	 *
	 * @param __a The sorted array to search.
	 * @param __key The key to locate.
	 * @return The index of the given key or {@code (-(insertion point) - 1)}
	 * indicating where the element would be found.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/05/09
	 */
	public static int binarySearch(short[] __a, short __key)
		throws NullPointerException
	{
		if (__a == null)
			throw new NullPointerException("NARG");
		
		return Arrays.binarySearch(__a, 0, __a.length, __key);
	}
	
	/**
	 * Searches the given sorted array for the given element.
	 *
	 * @param __a The sorted array to search.
	 * @param __from The from index.
	 * @param __to The to index.
	 * @param __key The key to locate.
	 * @return The index of the given key or {@code (-(insertion point) - 1)}
	 * indicating where the element would be found.
	 * @throws ArrayIndexOutOfBoundsException If the from or to index exceed
	 * the bounds of the array.
	 * @throws IllegalArgumentException If the from index is higher than the
	 * to index.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/05/09
	 */
	public static int binarySearch(short[] __a, int __from, int __to,
		short __key)
		throws ArrayIndexOutOfBoundsException, IllegalArgumentException,
			NullPointerException
	{
		return IntegerArrays.binarySearch(new ShortIntegerArray(__a),
			__from, __to, __key);
	}
	
	/**
	 * Searches the given sorted array for the given element.
	 *
	 * @param __a The sorted array to search.
	 * @param __key The key to locate.
	 * @return The index of the given key or {@code (-(insertion point) - 1)}
	 * indicating where the element would be found.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/10/28
	 */
	public static int binarySearch(char[] __a, char __key)
		throws NullPointerException
	{
		if (__a == null)
			throw new NullPointerException("NARG");
		
		return Arrays.binarySearch(__a, 0, __a.length, __key);
	}
	
	/**
	 * Searches the given sorted array for the given element.
	 *
	 * @param __a The sorted array to search.
	 * @param __from The from index.
	 * @param __to The to index.
	 * @param __key The key to locate.
	 * @return The index of the given key or {@code (-(insertion point) - 1)}
	 * indicating where the element would be found.
	 * @throws ArrayIndexOutOfBoundsException If the from or to index exceed
	 * the bounds of the array.
	 * @throws IllegalArgumentException If the from index is higher than the
	 * to index.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/10/28
	 */
	public static int binarySearch(char[] __a, int __from, int __to,
		char __key)
		throws ArrayIndexOutOfBoundsException, IllegalArgumentException,
			NullPointerException
	{
		return IntegerArrays.binarySearch(new CharacterIntegerArray(__a),
			__from, __to, __key);
	}
	
	/**
	 * Searches the given sorted array for the given element.
	 *
	 * @param __a The sorted array to search.
	 * @param __key The key to locate.
	 * @return The index of the given key or {@code (-(insertion point) - 1)}
	 * indicating where the element would be found.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/05/09
	 */
	public static int binarySearch(byte[] __a, byte __key)
		throws NullPointerException
	{
		if (__a == null)
			throw new NullPointerException("NARG");
		
		return Arrays.binarySearch(__a, 0, __a.length, __key);
	}
	
	/**
	 * Searches the given sorted array for the given element.
	 *
	 * @param __a The sorted array to search.
	 * @param __from The from index.
	 * @param __to The to index.
	 * @param __key The key to locate.
	 * @return The index of the given key or {@code (-(insertion point) - 1)}
	 * indicating where the element would be found.
	 * @throws ArrayIndexOutOfBoundsException If the from or to index exceed
	 * the bounds of the array.
	 * @throws IllegalArgumentException If the from index is higher than the
	 * to index.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/05/09
	 */
	public static int binarySearch(byte[] __a, int __from, int __to,
		byte __key)
		throws ArrayIndexOutOfBoundsException, IllegalArgumentException,
			NullPointerException
	{
		return IntegerArrays.binarySearch(new ByteIntegerArray(__a),
			__from, __to, __key);
	}
	
	public static int binarySearch(double[] __a, double __b)
	{
		throw new todo.TODO();
	}
	
	public static int binarySearch(double[] __a, int __b, int __c, double __d)
	{
		throw new todo.TODO();
	}
	
	public static int binarySearch(float[] __a, float __b)
	{
		throw new todo.TODO();
	}
	
	public static int binarySearch(float[] __a, int __b, int __c, float __d)
	{
		throw new todo.TODO();
	}
	
	public static int binarySearch(Object[] __a, Object __b)
	{
		throw new todo.TODO();
	}
	
	public static int binarySearch(Object[] __a, int __b, int __c, Object __d)
	{
		throw new todo.TODO();
	}
	
	public static <T> int binarySearch(T[] __a, T __b, Comparator<? super T>
		__c)
	{
		throw new todo.TODO();
	}
	
	public static <T> int binarySearch(T[] __a, int __b, int __c, T __d,
		Comparator<? super T> __e)
	{
		throw new todo.TODO();
	}
	
	/**
	 * Returns a copy of the given array but using the specified type.
	 *
	 * @param <T> The resulting type of the array to use.
	 * @param __src The source array.
	 * @param __newLen The new length of the array.
	 * @return The copy of the array with the new length and type.
	 * @throws ArrayStoreException If an element being copied from the source
	 * array is not compatible with the destination array.
	 * @throws NegativeArraySizeException If the new length is negative.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/04
	 */
	@SuppressWarnings({"unchecked"})
	public static <T> T[] copyOf(T[] __src, int __newLen)
		throws NegativeArraySizeException, NullPointerException
	{
		return Arrays.<T, T>copyOf(__src, __newLen,
			(Class<T[]>)__src.getClass());
	}
	
	/**
	 * Returns a copy of the given array but using the specified type.
	 *
	 * @param <T> The resulting type of the array to use.
	 * @param <U> The input array type.
	 * @param __src The source array.
	 * @param __newLen The new length of the array.
	 * @param __newType The type type.
	 * @return The copy of the array with the new length and type.
	 * @throws ArrayStoreException If an element being copied from the source
	 * array is not compatible with the destination array.
	 * @throws NegativeArraySizeException If the new length is negative.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/04
	 */
	public static <T, U> T[] copyOf(U[] __src, int __newLen,
		Class<? extends T[]> __newType)
		throws ArrayStoreException, NegativeArraySizeException,
			NullPointerException
	{
		if (__src == null || __newType == null)
			throw new NullPointerException("NARG");
		if (__newLen < 0)
			throw new NegativeArraySizeException("NASE");
		
		// Allocate array in the target type
		T[] rv = ObjectShelf.<T[]>arrayNew(TypeShelf.classToType(__newType),
			__newLen);
		
		System.arraycopy(__src, 0,
			rv, 0, Math.min(__newLen, __src.length));
			
		return rv;
	}
	
	/**
	 * Returns a new copy of the given array of the given length.
	 *
	 * @param __src The array to copy.
	 * @param __newLen The new length of the array.
	 * @return The copied array.
	 * @throws NegativeArraySizeException If the new length is negative.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/06
	 */
	public static byte[] copyOf(byte[] __src, int __newLen)
		throws NegativeArraySizeException, NullPointerException
	{
		if (__src == null)
			throw new NullPointerException("NARG");
		if (__newLen < 0)
			throw new NegativeArraySizeException("NASE");
		
		byte[] rv = new byte[__newLen];
		
		ObjectShelf.arrayCopy(__src, 0, rv, 0,
			Math.min(__newLen, __src.length));
		
		return rv;
	}
	
	/**
	 * Returns a new copy of the given array of the given length.
	 *
	 * @param __src The array to copy.
	 * @param __newLen The new length of the array.
	 * @return The copied array.
	 * @throws NegativeArraySizeException If the new length is negative.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/06
	 */
	public static short[] copyOf(short[] __src, int __newLen)
		throws NegativeArraySizeException, NullPointerException
	{
		if (__src == null)
			throw new NullPointerException("NARG");
		if (__newLen < 0)
			throw new NegativeArraySizeException("NASE");
		
		short[] rv = new short[__newLen];
		
		ObjectShelf.arrayCopy(__src, 0, rv, 0,
			Math.min(__newLen, __src.length));
		
		return rv;
	}
	
	/**
	 * Returns a new copy of the given array of the given length.
	 *
	 * @param __src The array to copy.
	 * @param __newLen The new length of the array.
	 * @return The copied array.
	 * @throws NegativeArraySizeException If the new length is negative.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/06
	 */
	public static int[] copyOf(int[] __src, int __newLen)
		throws NegativeArraySizeException, NullPointerException
	{
		if (__src == null)
			throw new NullPointerException("NARG");
		if (__newLen < 0)
			throw new NegativeArraySizeException("NASE");
		
		int[] rv = new int[__newLen];
		
		ObjectShelf.arrayCopy(__src, 0, rv, 0,
			Math.min(__newLen, __src.length));
		
		return rv;
	}
	
	/**
	 * Returns a new copy of the given array of the given length.
	 *
	 * @param __src The array to copy.
	 * @param __newLen The new length of the array.
	 * @return The copied array.
	 * @throws NegativeArraySizeException If the new length is negative.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/06
	 */
	public static long[] copyOf(long[] __src, int __newLen)
		throws NegativeArraySizeException, NullPointerException
	{
		if (__src == null)
			throw new NullPointerException("NARG");
		if (__newLen < 0)
			throw new NegativeArraySizeException("NASE");
		
		long[] rv = new long[__newLen];
		
		ObjectShelf.arrayCopy(__src, 0, rv, 0,
			Math.min(__newLen, __src.length));
		
		return rv;
	}
	
	/**
	 * Returns a new copy of the given array of the given length.
	 *
	 * @param __src The array to copy.
	 * @param __newLen The new length of the array.
	 * @return The copied array.
	 * @throws NegativeArraySizeException If the new length is negative.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/06
	 */
	public static char[] copyOf(char[] __src, int __newLen)
		throws NegativeArraySizeException, NullPointerException
	{
		if (__src == null)
			throw new NullPointerException("NARG");
		if (__newLen < 0)
			throw new NegativeArraySizeException("NASE");
		
		char[] rv = new char[__newLen];
		
		ObjectShelf.arrayCopy(__src, 0, rv, 0,
			Math.min(__newLen, __src.length));
		
		return rv;
	}
	
	/**
	 * Returns a new copy of the given array of the given length.
	 *
	 * @param __src The array to copy.
	 * @param __newLen The new length of the array.
	 * @return The copied array.
	 * @throws NegativeArraySizeException If the new length is negative.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/06
	 */
	public static float[] copyOf(float[] __src, int __newLen)
		throws NegativeArraySizeException, NullPointerException
	{
		if (__src == null)
			throw new NullPointerException("NARG");
		if (__newLen < 0)
			throw new NegativeArraySizeException("NASE");
		
		float[] rv = new float[__newLen];
		
		ObjectShelf.arrayCopy(__src, 0, rv, 0,
			Math.min(__newLen, __src.length));
		
		return rv;
	}
	
	/**
	 * Returns a new copy of the given array of the given length.
	 *
	 * @param __src The array to copy.
	 * @param __newLen The new length of the array.
	 * @return The copied array.
	 * @throws NegativeArraySizeException If the new length is negative.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/06
	 */
	public static double[] copyOf(double[] __src, int __newLen)
		throws NegativeArraySizeException, NullPointerException
	{
		if (__src == null)
			throw new NullPointerException("NARG");
		if (__newLen < 0)
			throw new NegativeArraySizeException("NASE");
		
		double[] rv = new double[__newLen];
		
		ObjectShelf.arrayCopy(__src, 0, rv, 0,
			Math.min(__newLen, __src.length));
		
		return rv;
	}
	
	/**
	 * Returns a new copy of the given array of the given length.
	 *
	 * @param __src The array to copy.
	 * @param __newLen The new length of the array.
	 * @return The copied array.
	 * @throws NegativeArraySizeException If the new length is negative.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/06
	 */
	public static boolean[] copyOf(boolean[] __src, int __newLen)
		throws NegativeArraySizeException, NullPointerException
	{
		if (__src == null)
			throw new NullPointerException("NARG");
		if (__newLen < 0)
			throw new NegativeArraySizeException("NASE");
		
		boolean[] rv = new boolean[__newLen];
		
		ObjectShelf.arrayCopy(__src, 0, rv, 0,
			Math.min(__newLen, __src.length));
		
		return rv;
	}
	
	/**
	 * Checks if both arrays are equal to each other.
	 *
	 * @param __a The first array.
	 * @param __b The second array.
	 * @return If the two arrays are equal.
	 * @since 2019/01/24
	 */
	public static boolean equals(long[] __a, long[] __b)
	{
		// Same reference or both null is equal
		if (__a == __b)
			return true;
		
		// Either one is null
		if (__a == null || __b == null)
			return false;
		
		// Differing lengths?
		int na = __a.length,
			nb = __b.length;
		if (na != nb)
			return false;
		
		for (int i = 0; i < na; i++)
			if (__a[i] != __b[i])
				return false;
		
		// No mismatches
		return true;
	}
	
	/**
	 * Checks if both arrays are equal to each other.
	 *
	 * @param __a The first array.
	 * @param __b The second array.
	 * @return If the two arrays are equal.
	 * @since 2019/01/24
	 */
	public static boolean equals(int[] __a, int[] __b)
	{
		// Same reference or both null is equal
		if (__a == __b)
			return true;
		
		// Either one is null
		if (__a == null || __b == null)
			return false;
		
		// Differing lengths?
		int na = __a.length,
			nb = __b.length;
		if (na != nb)
			return false;
		
		for (int i = 0; i < na; i++)
			if (__a[i] != __b[i])
				return false;
		
		// No mismatches
		return true;
	}
	
	/**
	 * Checks if both arrays are equal to each other.
	 *
	 * @param __a The first array.
	 * @param __b The second array.
	 * @return If the two arrays are equal.
	 * @since 2019/01/24
	 */
	public static boolean equals(short[] __a, short[] __b)
	{
		// Same reference or both null is equal
		if (__a == __b)
			return true;
		
		// Either one is null
		if (__a == null || __b == null)
			return false;
		
		// Differing lengths?
		int na = __a.length,
			nb = __b.length;
		if (na != nb)
			return false;
		
		for (int i = 0; i < na; i++)
			if (__a[i] != __b[i])
				return false;
		
		// No mismatches
		return true;
	}
	
	/**
	 * Checks if both arrays are equal to each other.
	 *
	 * @param __a The first array.
	 * @param __b The second array.
	 * @return If the two arrays are equal.
	 * @since 2019/01/24
	 */
	public static boolean equals(char[] __a, char[] __b)
	{
		// Same reference or both null is equal
		if (__a == __b)
			return true;
		
		// Either one is null
		if (__a == null || __b == null)
			return false;
		
		// Differing lengths?
		int na = __a.length,
			nb = __b.length;
		if (na != nb)
			return false;
		
		for (int i = 0; i < na; i++)
			if (__a[i] != __b[i])
				return false;
		
		// No mismatches
		return true;
	}
	
	/**
	 * Checks if both arrays are equal to each other.
	 *
	 * @param __a The first array.
	 * @param __b The second array.
	 * @return If the two arrays are equal.
	 * @since 2019/01/24
	 */
	public static boolean equals(byte[] __a, byte[] __b)
	{
		// Same reference or both null is equal
		if (__a == __b)
			return true;
		
		// Either one is null
		if (__a == null || __b == null)
			return false;
		
		// Differing lengths?
		int na = __a.length,
			nb = __b.length;
		if (na != nb)
			return false;
		
		for (int i = 0; i < na; i++)
			if (__a[i] != __b[i])
				return false;
		
		// No mismatches
		return true;
	}
	
	/**
	 * Checks if both arrays are equal to each other.
	 *
	 * @param __a The first array.
	 * @param __b The second array.
	 * @return If the two arrays are equal.
	 * @since 2019/01/24
	 */
	public static boolean equals(boolean[] __a, boolean[] __b)
	{
		// Same reference or both null is equal
		if (__a == __b)
			return true;
		
		// Either one is null
		if (__a == null || __b == null)
			return false;
		
		// Differing lengths?
		int na = __a.length,
			nb = __b.length;
		if (na != nb)
			return false;
		
		for (int i = 0; i < na; i++)
			if (__a[i] != __b[i])
				return false;
		
		// No mismatches
		return true;
	}
	
	/**
	 * Checks if both arrays are equal to each other.
	 *
	 * @param __a The first array.
	 * @param __b The second array.
	 * @return If the two arrays are equal.
	 * @since 2019/01/24
	 */
	public static boolean equals(double[] __a, double[] __b)
	{
		// Same reference or both null is equal
		if (__a == __b)
			return true;
		
		// Either one is null
		if (__a == null || __b == null)
			return false;
		
		// Differing lengths?
		int na = __a.length,
			nb = __b.length;
		if (na != nb)
			return false;
		
		for (int i = 0; i < na; i++)
			if (__a[i] != __b[i])
				return false;
		
		// No mismatches
		return true;
	}
	
	/**
	 * Checks if both arrays are equal to each other.
	 *
	 * @param __a The first array.
	 * @param __b The second array.
	 * @return If the two arrays are equal.
	 * @since 2019/01/24
	 */
	public static boolean equals(float[] __a, float[] __b)
	{
		// Same reference or both null is equal
		if (__a == __b)
			return true;
		
		// Either one is null
		if (__a == null || __b == null)
			return false;
		
		// Differing lengths?
		int na = __a.length,
			nb = __b.length;
		if (na != nb)
			return false;
		
		for (int i = 0; i < na; i++)
			if (__a[i] != __b[i])
				return false;
		
		// No mismatches
		return true;
	}
	
	/**
	 * Checks if both arrays are equal to each other.
	 *
	 * @param __a The first array.
	 * @param __b The second array.
	 * @return If the two arrays are equal.
	 * @since 2019/01/24
	 */
	public static boolean equals(Object[] __a, Object[] __b)
	{
		// Same reference or both null is equal
		if (__a == __b)
			return true;
		
		// Either one is null
		if (__a == null || __b == null)
			return false;
		
		// Differing lengths?
		int na = __a.length,
			nb = __b.length;
		if (na != nb)
			return false;
		
		for (int i = 0; i < na; i++)
			if (!Objects.equals(__a[i], __b[i]))
				return false;
		
		// No mismatches
		return true;
	}
	
	/**
	 * Fills the array with the given value.
	 *
	 * @param __a The array to fill.
	 * @param __v The value to store.
	 * @throws NullPointerException If the array is null.
	 * @since 2020/01/01
	 */
	public static void fill(long[] __a, long __v)
		throws NullPointerException
	{
		if (__a == null)
			throw new NullPointerException("NARG");
		
		for (int i = 0, n = __a.length; i < n; i++)
			__a[i] = __v;
	}
	
	/**
	 * Fills the array with the given value.
	 *
	 * @param __a The array to fill.
	 * @param __v The value to store.
	 * @throws NullPointerException If the array is null.
	 * @since 2020/01/01
	 */
	public static void fill(int[] __a, int __v)
		throws NullPointerException
	{
		if (__a == null)
			throw new NullPointerException("NARG");
		
		for (int i = 0, n = __a.length; i < n; i++)
			__a[i] = __v;
	}
	
	/**
	 * Fills the array with the given value.
	 *
	 * @param __a The array to fill.
	 * @param __v The value to store.
	 * @throws NullPointerException If the array is null.
	 * @since 2020/01/01
	 */
	public static void fill(short[] __a, short __v)
		throws NullPointerException
	{
		if (__a == null)
			throw new NullPointerException("NARG");
		
		for (int i = 0, n = __a.length; i < n; i++)
			__a[i] = __v;
	}
	
	/**
	 * Fills the array with the given value.
	 *
	 * @param __a The array to fill.
	 * @param __v The value to store.
	 * @throws NullPointerException If the array is null.
	 * @since 2020/01/01
	 */
	public static void fill(char[] __a, char __v)
		throws NullPointerException
	{
		if (__a == null)
			throw new NullPointerException("NARG");
		
		for (int i = 0, n = __a.length; i < n; i++)
			__a[i] = __v;
	}
	
	/**
	 * Fills the array with the given value.
	 *
	 * @param __a The array to fill.
	 * @param __v The value to store.
	 * @throws NullPointerException If the array is null.
	 * @since 2020/01/01
	 */
	public static void fill(byte[] __a, byte __v)
		throws NullPointerException
	{
		if (__a == null)
			throw new NullPointerException("NARG");
		
		for (int i = 0, n = __a.length; i < n; i++)
			__a[i] = __v;
	}
	
	/**
	 * Fills the array with the given value.
	 *
	 * @param __a The array to fill.
	 * @param __v The value to store.
	 * @throws NullPointerException If the array is null.
	 * @since 2020/01/01
	 */
	public static void fill(boolean[] __a, boolean __v)
		throws NullPointerException
	{
		if (__a == null)
			throw new NullPointerException("NARG");
		
		for (int i = 0, n = __a.length; i < n; i++)
			__a[i] = __v;
	}
	
	/**
	 * Fills the array with the given value.
	 *
	 * @param __a The array to fill.
	 * @param __v The value to store.
	 * @throws NullPointerException If the array is null.
	 * @since 2020/01/01
	 */
	public static void fill(double[] __a, double __v)
		throws NullPointerException
	{
		if (__a == null)
			throw new NullPointerException("NARG");
		
		for (int i = 0, n = __a.length; i < n; i++)
			__a[i] = __v;
	}
	
	/**
	 * Fills the array with the given value.
	 *
	 * @param __a The array to fill.
	 * @param __v The value to store.
	 * @throws NullPointerException If the array is null.
	 * @since 2020/01/01
	 */
	public static void fill(float[] __a, float __v)
		throws NullPointerException
	{
		if (__a == null)
			throw new NullPointerException("NARG");
		
		for (int i = 0, n = __a.length; i < n; i++)
			__a[i] = __v;
	}
	
	/**
	 * Fills the array with the given value.
	 *
	 * @param __a The array to fill.
	 * @param __v The value to store.
	 * @throws NullPointerException If the array is null.
	 * @since 2020/01/01
	 */
	public static void fill(Object[] __a, Object __v)
		throws NullPointerException
	{
		if (__a == null)
			throw new NullPointerException("NARG");
		
		for (int i = 0, n = __a.length; i < n; i++)
			__a[i] = __v;
	}
	
	/**
	 * Sorts the specified array.
	 *
	 * @param __a The array to sort.
	 * @throws NullPointerException If no array was specified.
	 * @since 2016/09/30
	 */
	public static void sort(int[] __a)
		throws NullPointerException
	{
		// Check
		if (__a == null)
			throw new NullPointerException("NARG");
		
		Arrays.sort(__a, 0, __a.length);
	}
	
	/**
	 * Sorts the specified array.
	 *
	 * @param __a The array to sort.
	 * @param __from The from index.
	 * @param __to The to index.
	 * @throws ArrayIndexOutOfBoundsException If the from or to index are
	 * outside of bounds.
	 * @throws IllegalArgumentException If the from address is greater than
	 * the to address.
	 * @throws NullPointerException If no array was specified.
	 * @since 2019/05/09
	 */
	public static void sort(int[] __a, int __from, int __to)
		throws ArrayIndexOutOfBoundsException, IllegalArgumentException,
			NullPointerException
	{
		IntegerArrays.sort(new IntegerIntegerArray(__a), __from, __to);
	}
	
	/**
	 * Sorts the specified array.
	 *
	 * @param __a The array to sort.
	 * @throws NullPointerException If no array was specified.
	 * @since 2016/09/30
	 */
	public static void sort(long[] __a)
		throws NullPointerException
	{
		// Check
		if (__a == null)
			throw new NullPointerException("NARG");	
		
		Arrays.sort(__a, 0, __a.length);
	}
	
	/**
	 * Sorts the specified array.
	 *
	 * @param __a The array to sort.
	 * @param __from The from index.
	 * @param __to The to index.
	 * @throws ArrayIndexOutOfBoundsException If the from or to index are
	 * outside of bounds.
	 * @throws IllegalArgumentException If the from address is greater than
	 * the to address.
	 * @throws NullPointerException If no array was specified.
	 * @since 2020/01/01
	 */
	public static void sort(long[] __a, int __from, int __to)
		throws ArrayIndexOutOfBoundsException, IllegalArgumentException,
			NullPointerException
	{
		// Check
		if (__a == null)
			throw new NullPointerException("NARG");
		int an = __a.length;
		if (__from < 0 || __to > an)
			throw new ArrayIndexOutOfBoundsException("ZZ04");
		if (__from > __to)
			throw new IllegalArgumentException("ZZ2g");
		
		// Pointless sort?
		if (__from == __to)
			return;
		
		// Non-common sort, use a helper wrapper
		ShellSort.<Long>sort(new LongArrayList(__a), __from, __to, null);
	}
	
	/**
	 * Sorts the specified array.
	 *
	 * @param __a The array to sort.
	 * @throws NullPointerException If no array was specified.
	 * @since 2016/09/30
	 */
	public static void sort(short[] __a)
		throws NullPointerException
	{
		// Check
		if (__a == null)
			throw new NullPointerException("NARG");
		
		Arrays.sort(__a, 0, __a.length);
	}
	
	/**
	 * Sorts the specified array.
	 *
	 * @param __a The array to sort.
	 * @param __from The from index.
	 * @param __to The to index.
	 * @throws ArrayIndexOutOfBoundsException If the from or to index are
	 * outside of bounds.
	 * @throws IllegalArgumentException If the from address is greater than
	 * the to address.
	 * @throws NullPointerException If no array was specified.
	 * @since 2019/05/09
	 */
	public static void sort(short[] __a, int __from, int __to)
		throws ArrayIndexOutOfBoundsException, IllegalArgumentException,
			NullPointerException
	{
		// Use common sorting code
		IntegerArrays.sort(new ShortIntegerArray(__a), __from, __to);
	}
	
	/**
	 * Sorts the specified array.
	 *
	 * @param __a The array to sort.
	 * @throws NullPointerException If no array was specified.
	 * @since 2016/09/30
	 */
	public static void sort(char[] __a)
		throws NullPointerException
	{
		// Check
		if (__a == null)
			throw new NullPointerException("NARG");
		
		Arrays.sort(__a, 0, __a.length);
	}
	
	/**
	 * Sorts the specified array.
	 *
	 * @param __a The array to sort.
	 * @param __from The source array.
	 * @param __to The destination array.
	 * @throws ArrayIndexOutOfBoundsException If the from and/or to index
	 * exceed the array bounds.
	 * @throws IllegalArgumentException If the from index is greater than to
	 * index.
	 * @throws NullPointerException If no array was specified.
	 * @since 2018/10/28
	 */
	public static void sort(char[] __a, int __from, int __to)
		throws ArrayIndexOutOfBoundsException, IllegalArgumentException,
			NullPointerException
	{
		// Use common sorting code
		IntegerArrays.sort(new CharacterIntegerArray(__a), __from, __to);
	}
	
	/**
	 * Sorts the specified array.
	 *
	 * @param __a The array to sort.
	 * @throws NullPointerException If no array was specified.
	 * @since 2016/09/30
	 */
	public static void sort(byte[] __a)
		throws NullPointerException
	{
		// Check
		if (__a == null)
			throw new NullPointerException("NARG");
		
		Arrays.sort(__a, 0, __a.length);
	}
	
	/**
	 * Sorts the specified array.
	 *
	 * @param __a The array to sort.
	 * @param __from The from index.
	 * @param __to The to index.
	 * @throws ArrayIndexOutOfBoundsException If the from or to index are
	 * outside of bounds.
	 * @throws IllegalArgumentException If the from address is greater than
	 * the to address.
	 * @throws NullPointerException If no array was specified.
	 * @since 2019/05/09
	 */
	public static void sort(byte[] __a, int __from, int __to)
		throws ArrayIndexOutOfBoundsException, IllegalArgumentException,
			NullPointerException
	{
		// Use common sorting code
		IntegerArrays.sort(new ByteIntegerArray(__a), __from, __to);
	}
	
	/**
	 * Sorts the specified array.
	 *
	 * @param __a The array to sort.
	 * @throws NullPointerException If no array was specified.
	 * @since 2016/09/30
	 */
	public static void sort(float[] __a)
		throws NullPointerException
	{
		// Check
		if (__a == null)
			throw new NullPointerException("NARG");
		
		Arrays.sort(__a, 0, __a.length);
	}
	
	/**
	 * Sorts the specified array.
	 *
	 * @param __a The array to sort.
	 * @param __from The from index.
	 * @param __to The to index.
	 * @throws ArrayIndexOutOfBoundsException If the from or to index are
	 * outside of bounds.
	 * @throws IllegalArgumentException If the from address is greater than
	 * the to address.
	 * @throws NullPointerException If no array was specified.
	 * @since 2020/01/01
	 */
	public static void sort(float[] __a, int __from, int __to)
		throws ArrayIndexOutOfBoundsException, IllegalArgumentException,
			NullPointerException
	{
		// Check
		if (__a == null)
			throw new NullPointerException("NARG");
		int an = __a.length;
		if (__from < 0 || __to > an)
			throw new ArrayIndexOutOfBoundsException("ZZ04");
		if (__from > __to)
			throw new IllegalArgumentException("ZZ2g");
		
		// Pointless sort?
		if (__from == __to)
			return;
		
		// Non-common sort, use a helper wrapper
		ShellSort.<Float>sort(new FloatArrayList(__a), __from, __to, null);
	}
	
	/**
	 * Sorts the specified array.
	 *
	 * @param __a The array to sort.
	 * @throws NullPointerException If no array was specified.
	 * @since 2016/09/30
	 */
	public static void sort(double[] __a)
		throws NullPointerException
	{
		// Check
		if (__a == null)
			throw new NullPointerException("NARG");
		
		Arrays.sort(__a, 0, __a.length);
	}
	
	/**
	 * Sorts the specified array.
	 *
	 * @param __a The array to sort.
	 * @param __from The from index.
	 * @param __to The to index.
	 * @throws ArrayIndexOutOfBoundsException If the from or to index are
	 * outside of bounds.
	 * @throws IllegalArgumentException If the from address is greater than
	 * the to address.
	 * @throws NullPointerException If no array was specified.
	 * @since 2020/01/01
	 */
	public static void sort(double[] __a, int __from, int __to)
		throws ArrayIndexOutOfBoundsException, IllegalArgumentException,
			NullPointerException
	{
		// Check
		if (__a == null)
			throw new NullPointerException("NARG");
		int an = __a.length;
		if (__from < 0 || __to > an)
			throw new ArrayIndexOutOfBoundsException("ZZ04");
		if (__from > __to)
			throw new IllegalArgumentException("ZZ2g");
		
		// Pointless sort?
		if (__from == __to)
			return;
		
		// Non-common sort, use a helper wrapper
		ShellSort.<Double>sort(new DoubleArrayList(__a), __from, __to, null);
	}
	
	/**
	 * Sorts the specified array using the natural {@link Comparator}.
	 *
	 * @param __a The array to sort.
	 * @throws NullPointerException If no array was specified.
	 * @since 2016/09/30
	 */
	public static void sort(Object[] __a)
		throws NullPointerException
	{
		// Check
		if (__a == null)
			throw new NullPointerException("NARG");	
		
		Arrays.<Object>sort(__a, 0, __a.length, null);
	}
	
	/**
	 * Sorts the specified array.
	 *
	 * @param __a The array to sort.
	 * @param __from The from index.
	 * @param __to The to index.
	 * @throws ArrayIndexOutOfBoundsException If the from or to index are
	 * outside of bounds.
	 * @throws IllegalArgumentException If the from address is greater than
	 * the to address.
	 * @throws NullPointerException If no array was specified.
	 * @since 2019/05/09
	 */
	public static void sort(Object[] __a, int __from, int __to)
		throws ArrayIndexOutOfBoundsException, IllegalArgumentException,
			NullPointerException
	{
		Arrays.<Object>sort(__a, __from, __to, null);
	}
	
	/**
	 * Sorts the specified array.
	 *
	 * @param <T> The type to sort.
	 * @param __a The array to sort.
	 * @param __comp The comparator to use.
	 * @throws ArrayIndexOutOfBoundsException If the from or to index are
	 * outside of bounds.
	 * @throws IllegalArgumentException If the from address is greater than
	 * the to address.
	 * @throws NullPointerException If no array was specified.
	 * @since 2019/05/09
	 */
	public static <T> void sort(T[] __a, Comparator<? super T> __comp)
		throws NullPointerException
	{
		// Check
		if (__a == null)
			throw new NullPointerException("NARG");
		
		Arrays.<T>sort(__a, 0, __a.length, __comp);
	}
	
	/**
	 * Sorts the specified array.
	 *
	 * @param <T> The type to sort.
	 * @param __a The array to sort.
	 * @param __from The from index.
	 * @param __to The to index.
	 * @param __comp The comparator to use.
	 * @throws ArrayIndexOutOfBoundsException If the from or to index are
	 * outside of bounds.
	 * @throws IllegalArgumentException If the from address is greater than
	 * the to address.
	 * @throws NullPointerException If no array was specified.
	 * @since 2019/05/09
	 */
	public static <T> void sort(T[] __a, int __from, int __to,
		Comparator<? super T> __comp)
		throws ArrayIndexOutOfBoundsException, IllegalArgumentException,
			NullPointerException
	{
		if (__a == null)
			throw new NullPointerException("NARG");
		
		// The thrown exception may need to be remapped
		try
		{
			ShellSort.<T>sort(Arrays.<T>asList(__a), __from, __to, __comp);
		}
		
		// {@squirreljme.error ZZ2h Out of bounds access when sorting array.}
		catch (IndexOutOfBoundsException e)
		{
			if (e instanceof ArrayIndexOutOfBoundsException)
				throw e;
			
			RuntimeException t = new ArrayIndexOutOfBoundsException("ZZ2h");
			t.initCause(e);
			throw t;
		}
	}
}

