// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package lang;

import java.util.Arrays;
import java.util.Objects;
import net.multiphasicapps.tac.TestConsumer;

/**
 * Tests that {@link System#arraycopy(Object, int, Object, int, int)} works
 * properly.
 *
 * @since 2020/11/15
 */
public class TestSystemArrayCopy
	extends TestConsumer<String>
{
	/** Base array size. */
	public static final int BASE_SIZE =
		16;
	
	/** The half array size. */
	public static final int HALF_SIZE = 
		TestSystemArrayCopy.BASE_SIZE >> 1;
	
	/** The quarter array size. */
	public static final int QUARTER_SIZE = 
		TestSystemArrayCopy.BASE_SIZE >> 2;
	
	/**
	 * {@inheritDoc}
	 * @since 2020/11/15
	 */
	@SuppressWarnings("SuspiciousSystemArraycopy")
	@Override
	public void test(String __s)
		throws Throwable
	{
		Object a = TestSystemArrayCopy.allocate(__s,
			TestSystemArrayCopy.BASE_SIZE, 0);
		
		// The two full arrays should be identical
		Object fullCopy = TestSystemArrayCopy.allocate(__s,
			TestSystemArrayCopy.BASE_SIZE, TestSystemArrayCopy.BASE_SIZE);
		System.arraycopy(a, 0,
			fullCopy, 0, TestSystemArrayCopy.BASE_SIZE);
		
		this.secondary("full", TestSystemArrayCopy.equals(a, fullCopy));
		
		// Overlapping low to high
		System.arraycopy(a, TestSystemArrayCopy.QUARTER_SIZE - 1,
			a, TestSystemArrayCopy.HALF_SIZE,
			TestSystemArrayCopy.HALF_SIZE);
		
		this.secondary("sum-lth",
			TestSystemArrayCopy.sum(a, TestSystemArrayCopy.BASE_SIZE));
		this.secondary("hash-lth",
			TestSystemArrayCopy.hash(a, TestSystemArrayCopy.BASE_SIZE));
			
		// Overlapping high to low
		System.arraycopy(fullCopy, TestSystemArrayCopy.HALF_SIZE - 1,
			fullCopy, TestSystemArrayCopy.QUARTER_SIZE,
			TestSystemArrayCopy.HALF_SIZE);
		
		this.secondary("sum-htl",
			TestSystemArrayCopy.sum(fullCopy, TestSystemArrayCopy.BASE_SIZE));
		this.secondary("hash-htl",
			TestSystemArrayCopy.hash(fullCopy, TestSystemArrayCopy.BASE_SIZE));
	}
	
	/**
	 * Allocates an array of the given type.
	 * 
	 * @param __type The type of array to allocate.
	 * @param __size The size of the array.
	 * @param __base Base value.
	 * @return The allocated array.
	 * @throws IllegalArgumentException If the type is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/15
	 */
	private static Object allocate(String __type, int __size, int __base)
		throws IllegalArgumentException, NullPointerException
	{
		switch (__type)
		{
			case "BOOLEAN":
				boolean[] za = new boolean[__size];
				for (int i = 0; i < __size; i++)
					za[i] = (((i + __base) & 1) != 0);
				return za;
				
			case "BYTE":
				byte[] ba = new byte[__size];
				for (int i = 0; i < __size; i++)
					ba[i] = (byte)(i + __base);
				return ba;
				
			case "SHORT":
				short[] sa = new short[__size];
				for (int i = 0; i < __size; i++)
					sa[i] = (short)(i + __base);
				return sa;
			
			case "CHAR":
				char[] ca = new char[__size];
				for (int i = 0; i < __size; i++)
					ca[i] = (char)(i + __base);
				return ca;
			
			case "INT":
				int[] ia = new int[__size];
				for (int i = 0; i < __size; i++)
					ia[i] = (i + __base);
				return ia;
			
			case "LONG":
				long[] la = new long[__size];
				for (int i = 0; i < __size; i++)
					la[i] = (i + __base);
				return la;
			
			case "FLOAT":
				float[] fa = new float[__size];
				for (int i = 0; i < __size; i++)
					fa[i] = (i + __base);
				return fa;
			
			case "DOUBLE":
				double[] da = new double[__size];
				for (int i = 0; i < __size; i++)
					da[i] = (i + __base);
				return da;
			
			case "OBJECT":
				Object[] oa = new Object[__size];
				for (int i = 0; i < __size; i++)
					oa[i] = (i + __base);
				return oa;
			
			default:
				throw new IllegalArgumentException(__type);
		}
	}
	
	/**
	 * Checks if both arrays are equal
	 * 
	 * @param __a A.
	 * @param __b B.
	 * @return If the arrays are equal.
	 * @since 2020/11/15
	 */
	private static Object equals(Object __a, Object __b)
	{
		if (__a instanceof boolean[])
			return Arrays.equals((boolean[])__a, (boolean[])__b);
		else if (__a instanceof byte[])
			return Arrays.equals((byte[])__a, (byte[])__b);
		else if (__a instanceof short[])
			return Arrays.equals((short[])__a, (short[])__b);
		else if (__a instanceof char[])
			return Arrays.equals((char[])__a, (char[])__b);
		else if (__a instanceof int[])
			return Arrays.equals((int[])__a, (int[])__b);
		else if (__a instanceof long[])
			return Arrays.equals((long[])__a, (long[])__b);
		else if (__a instanceof float[])
			return Arrays.equals((float[])__a, (float[])__b);
		else if (__a instanceof double[])
			return Arrays.equals((double[])__a, (double[])__b);
		else
			return Arrays.equals((Object[])__a, (Object[])__b);
	}
	
	/**
	 * Returns the value at the given index
	 * 
	 * @param __a The array.
	 * @param __dx The index to get.
	 * @return The value at the index.
	 * @since 2020/11/15
	 */
	private static int get(Object __a, int __dx)
	{
		if (__a instanceof boolean[])
			return (((boolean[])__a)[__dx] ? __dx : -1);
		else if (__a instanceof byte[])
			return ((byte[])__a)[__dx];
		else if (__a instanceof short[])
			return ((short[])__a)[__dx];
		else if (__a instanceof char[])
			return ((char[])__a)[__dx];
		else if (__a instanceof int[])
			return ((int[])__a)[__dx];
		else if (__a instanceof long[])
			return (int)((long[])__a)[__dx];
		else if (__a instanceof float[])
			return (int)((float[])__a)[__dx];
		else if (__a instanceof double[])
			return (int)((double[])__a)[__dx];
		else
			return Objects.hashCode(((Object[])__a)[__dx]);
	}
	
	/**
	 * Returns the hash of the array.
	 * 
	 * @param __a The array.
	 * @param __len The length of the array.
	 * @return The sum.
	 * @since 2020/11/15
	 */
	public static Object hash(Object __a, int __len)
	{
		int rv = 0;
		
		for (int i = 0; i < __len; i++)
			rv = 31 * rv + TestSystemArrayCopy.get(__a, i);
		
		return rv;
	}
	
	/**
	 * Returns the sum of the array.
	 * 
	 * @param __a The array.
	 * @param __len The length of the array.
	 * @return The sum.
	 * @since 2020/11/15
	 */
	public static Object sum(Object __a, int __len)
	{
		int sum = 0;
		
		for (int i = 0; i < __len; i++)
			sum = TestSystemArrayCopy.get(__a, i);
		
		return sum;	
	}
}
