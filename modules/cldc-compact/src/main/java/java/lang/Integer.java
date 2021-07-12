// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package java.lang;

import cc.squirreljme.jvm.mle.RuntimeShelf;
import cc.squirreljme.jvm.mle.TypeShelf;
import cc.squirreljme.jvm.mle.constants.MemoryProfileType;
import cc.squirreljme.runtime.cldc.annotation.ImplementationNote;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * This represents a boxed {@code int} value.
 *
 * @since 2019/05/11
 */
public final class Integer
	extends Number
	implements Comparable<Integer>
{
	/** The maximum value. */
	public static final int MAX_VALUE =
		2147483647;
	
	/** The minimum value. */
	public static final int MIN_VALUE =
		-2147483648;
	
	/** The number of bits this uses. */
	public static final int SIZE =
		32;
	
	/** The class type representing the primitive type. */
	public static final Class<Integer> TYPE =
		TypeShelf.<Integer>typeToClass(TypeShelf.typeOfInteger());
	
	/** The value of this integer. */
	private final int _value;
	
	/** The string representation of this value. */
	private Reference<String> _string;
	
	/**
	 * Initializes the integer with the given value.
	 *
	 * @param __v The value to use.
	 * @since 2018/09/23
	 */
	public Integer(int __v)
	{
		this._value = __v;
	}
	
	/**
	 * Parses the given string and initializes the integer value, it is
	 * parsed the same as {@code Integer.parseInt(__v, 10)}.
	 *
	 * @param __v The value to parse.
	 * @throws NullPointerException On null arguments.
	 * @throws NumberFormatException If the number is not valid.
	 * @since 2019/05/11
	 */
	public Integer(String __v)
		throws NullPointerException, NumberFormatException
	{
		if (__v == null)
			throw new NullPointerException("NARG");
		
		this._value = Integer.parseInt(__v, 10);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/05/11
	 */
	@Override
	public byte byteValue()
	{
		return (byte)this._value;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/05/10
	 */
	@Override
	public int compareTo(Integer __b)
	{
		int a = this._value,
			b = __b;
		
		if (a == b)
			return 0;
		else if (a < b)
			return -1;
		else
			return 1;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/05/11
	 */
	@Override
	public double doubleValue()
	{
		return (double)this._value;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/11/04
	 */
	@Override
	public boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (!(__o instanceof Integer))
			return false;
		
		return this._value == ((Integer)__o)._value;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/05/11
	 */
	@Override
	public float floatValue()
	{
		return (float)this._value;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/11/02
	 */
	@Override
	public int hashCode()
	{
		return this._value;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/12
	 */
	@Override
	public int intValue()
	{
		return this._value;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/11/03
	 */
	@Override
	public long longValue()
	{
		return this._value;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/05/11
	 */
	@Override
	public short shortValue()
	{
		return (short)this._value;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/09/23
	 */
	@Override
	public String toString()
	{
		// Normal memory profile?
		if (RuntimeShelf.memoryProfile() >= MemoryProfileType.NORMAL)
			return Integer.toString(this._value, 10);
		
		// Try to reduce memory by caching this
		Reference<String> ref = this._string;
		String rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._string = new WeakReference<>(
				(rv = Integer.toString(this._value)));
		
		return rv;
	}
	
	/**
	 * Returns the number of bits which are in the value, this is the
	 * population count.
	 *
	 * @param __v The value to count.
	 * @return The number of bits set in the value.
	 * @since 2018/11/11
	 */
	@SuppressWarnings("MagicNumber")
	public static int bitCount(int __v)
	{
		__v = __v - ((__v >>> 1) & 0x55555555);
		__v = (__v & 0x33333333) + ((__v >>> 2) & 0x33333333);
		return ((__v + (__v >>> 4) & 0xF0F0F0F) * 0x1010101) >>> 24;
	}
	
	/**
	 * Decodes the input string for an integer value.
	 *
	 * There is an optional sign: {@code -} or {@code +}.
	 *
	 * Then the number may be prefixed by:
	 * {@code 0x}, {@code 0X}, or {@code #} for hexadecimal,
	 * {@code 0} for octal. If there is no prefix then the number is treated
	 * as decimal.
	 *
	 * @param __s The input string to decode.
	 * @return The decoded integer.
	 * @throws NullPointerException On null arguments.
	 * @throws NumberFormatException If the string is of an incorrect value.
	 * @since 2018/11/11
	 */
	public static Integer decode(String __s)
		throws NullPointerException, NumberFormatException
	{
		if (__s == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error ZZ16 Cannot decode an empty string.}
		if (__s.isEmpty())
			throw new NumberFormatException("ZZ16");
		
		// It may be changed!
		String orig = __s;
		
		// Check for sign, assume positive otherwise
		char sign = __s.charAt(0);
		if (sign != '-' && sign != '+')
			sign = '+';
		
		// Remove the sign
		else
			__s = __s.substring(1);
		
		// Which number format?
		int radix;
		if (__s.startsWith("0x") || __s.startsWith("0X"))
		{
			radix = 16;
			__s = __s.substring(2);
		}
		else if (__s.startsWith("#"))
		{
			radix = 16;
			__s = __s.substring(1);
		}
		else if (__s.startsWith("0"))
		{
			radix = 8;
			__s = __s.substring(1);
		}
		else
			radix = 10;
		
		// {@squirreljme.error ZZ17 Misplaced sign. (The input string)}
		if (__s.startsWith("-") || __s.startsWith("+"))
			throw new NumberFormatException("ZZ2p " + orig);
		
		// Decode value with radix
		try
		{
			return Integer.parseInt(sign + __s, radix);
		}
		
		// {@squirreljme.error ZZ18 Could not parse number. (The input string)}
		catch (NumberFormatException e)
		{
			RuntimeException t = new NumberFormatException("ZZ18 " + orig);
			t.initCause(e);
			throw t;
		}
	}
	
	/**
	 * Obtains the integer value of a system property.
	 *
	 * @param __key The system property to get.
	 * @return The value or {@code null} if it is not an integer.
	 * @throws NumberFormatException If the property does not contain a
	 * valid number.
	 * @throws SecurityException If access to the property is denied.
	 * @since 2018/11/11
	 */
	public static Integer getInteger(String __key)
		throws NumberFormatException, SecurityException
	{
		return Integer.getInteger(__key, null);
	}
	
	/**
	 * Obtains the integer value of a system property.
	 *
	 * @param __key The system property to get.
	 * @param __def The default value.
	 * @return The value or {@code null} if it is not an integer.
	 * @throws NumberFormatException If the property does not contain a
	 * valid number.
	 * @throws SecurityException If access to the property is denied.
	 * @since 2018/11/11
	 */
	public static Integer getInteger(String __key, int __def)
		throws NumberFormatException, SecurityException
	{
		Integer rv = Integer.getInteger(__key, null);
		if (rv == null)
			return __def;
		return rv;
	}
	
	/**
	 * Obtains the integer value of a system property.
	 *
	 * @param __key The system property to get.
	 * @param __def The default value.
	 * @return The value or {@code null} if it is not an integer.
	 * @throws NumberFormatException If the property does not contain a
	 * valid number.
	 * @throws SecurityException If access to the property is denied.
	 * @since 2018/11/11
	 */
	public static Integer getInteger(String __key, Integer __def)
		throws NumberFormatException, SecurityException
	{
		// If there is no property, use the default
		String prop = System.getProperty(__key);
		if (prop == null)
			return __def;
		
		// Otherwise decode the value
		return Integer.decode(prop);
	}
	
	/**
	 * Returns the highest one bit of the given number.
	 * 
	 * @param __v The value to get.
	 * @return The highest one bit of the given number.
	 * @since 2020/10/29
	 */
	@SuppressWarnings({"DuplicatedCode", "MagicNumber"})
	public static int highestOneBit(int __v)
	{
		__v = __v | (__v >>> 1);
		__v = __v | (__v >>> 2);
		__v = __v | (__v >>> 4);
		__v = __v | (__v >>> 8);
		__v = __v | (__v >>> 16);
		
		return __v - (__v >>> 1);
	}
	
	/**
	 * Returns the single one bit for the given value.
	 * 
	 * @param __v The value to get the lowest single bit of.
	 * @return The lowest single bit of the given integer.
	 * @since 2020/10/29
	 */
	@SuppressWarnings({"DuplicatedCode", "MagicNumber"})
	public static int lowestOneBit(int __v)
	{
		__v = __v | (__v << 1);
		__v = __v | (__v << 2);
		__v = __v | (__v << 4);
		__v = __v | (__v << 8);
		__v = __v | (__v << 16);
		
		return __v - (__v << 1);
	}
	
	/**
	 * Returns the number of leading zeros in the bit representation.
	 *
	 * @param __v The value to process.
	 * @return The number of leading zeros.
	 * @since 2019/04/14
	 */
	@SuppressWarnings({"MagicNumber", "DuplicatedCode"})
	public static int numberOfLeadingZeros(int __v)
	{
		// From https://stackoverflow.com/a/23857066/11286149
		__v = __v | (__v >>> 1);
		__v = __v | (__v >>> 2);
		__v = __v | (__v >>> 4);
		__v = __v | (__v >>> 8);
		__v = __v | (__v >>> 16);
		
		return Integer.bitCount(~__v);
	}
	
	/**
	 * Returns the number of zeros which trail on the right side.
	 *
	 * @param __v The value to check.
	 * @return The number of trailing zeros.
	 * @since 2018/11/11
	 */
	@SuppressWarnings("MagicNumber")
	public static int numberOfTrailingZeros(int __v)
	{
		// c will be the number of zero bits on the right
		int c = 32;
		__v &= -__v;
		
		if ((__v) != 0)
			c--;
		
		if ((__v & 0x0000FFFF) != 0)
			c -= 16;
			
		if ((__v & 0x00FF00FF) != 0)
			c -= 8;
		
		if ((__v & 0x0F0F0F0F) != 0)
			c -= 4;
		
		if ((__v & 0x33333333) != 0)
			c -= 2;
		
		if ((__v & 0x55555555) != 0)
			c -= 1;
		
		return c;
	}
	
	/**
	 * Returns the value of the specified string using the given radix.
	 *
	 * @param __v The String to decode.
	 * @param __r The radix to use.
	 * @return The resulting integer.
	 * @throws NumberFormatException If the string is not valid or the radix
	 * is outside of the valid bounds.
	 * @since 2018/10/12
	 */
	public static int parseInt(String __v, int __r)
		throws NumberFormatException
	{
		// {@squirreljme.error ZZ19 The radix is out of bounds. (The radix)}
		if (__r < Character.MIN_RADIX || __r > Character.MAX_RADIX)
			throw new NumberFormatException("ZZ19 " + __r);
			
		if (__v == null)
			throw new NumberFormatException("ZZ1a");
		
		// {@squirreljme.error ZZ1a String is null or has zero length.}
		int n = __v.length();
		if (n <= 0)
			throw new NumberFormatException("ZZ1a");
		
		// Detect sign
		boolean neg;
		boolean signed = false;
		char c = __v.charAt(0);
		if ((neg = (c == '-')) || c == '+')
			signed = true;
		
		// Read all digits
		int rv = 0;
		for (int i = (signed ? 1 : 0); i < n; i++)
		{
			// Read character
			c = __v.charAt(i);
			
			// Convert to digit
			int dig = Character.digit(c, __r);
			
			// {@squirreljme.error ZZ1b Character out of range of radix.
			// (The input string; The out of range character)}
			if (dig < 0)
				throw new NumberFormatException("ZZ1b " + __v + " " + c);
			
			// {@squirreljme.error ZZ1c Input integer out of range of 32-bit
			// integer. (The input string)}
			int prod = rv * __r;
			if (rv != 0 && (neg ? (prod > rv) : (prod < rv)))
				throw new NumberFormatException("ZZ1c " + __v);
			
			// Add up
			if (neg)
				rv = prod - dig;
			else
				rv = prod + dig;
		}
		
		return rv;
	}
	
	/**
	 * Returns the value of the specified string.
	 *
	 * @param __v The String to decode.
	 * @return The parsed integer.
	 * @throws NumberFormatException If the string is not valid.
	 * @since 2018/10/12
	 */
	public static int parseInt(String __v)
		throws NumberFormatException
	{
		return Integer.parseInt(__v, 10);
	}
	
	/**
	 * Reverses all of the bits in the given integer.
	 *
	 * @param __i The input value.
	 * @return The integer but with the bits reversed.
	 * @since 2018/11/11
	 */
	@SuppressWarnings("MagicNumber")
	@ImplementationNote("Taken from " +
		"<http://aggregate.org/MAGIC/#Bit%20Reversal>.")
	public static int reverse(int __i)
	{
		__i = (((__i & 0xAAAAAAAA) >>> 1) | ((__i & 0x55555555) << 1));
		__i = (((__i & 0xCCCCCCCC) >>> 2) | ((__i & 0x33333333) << 2));
		__i = (((__i & 0xF0F0F0F0) >>> 4) | ((__i & 0x0F0F0F0F) << 4));
		__i = (((__i & 0xFF00FF00) >>> 8) | ((__i & 0x00FF00FF) << 8));
		
		return ((__i >>> 16) | (__i << 16));
	}
	
	/**
	 * Reverses the given bytes.
	 * 
	 * @param __i The integer to reverse.
	 * @return The reversed bytes.
	 * @since 2021/02/03
	 */
	public static int reverseBytes(int __i)
	{
		// 0xAABBCCDD -> 0xBBAADDCC
		__i = (((__i & 0xFF00FF00) >>> 8) | ((__i & 0x00FF00FF) << 8));
		
		// 0xBBAADDCC -> 0xDDCCBBAA
		return (__i >>> 16) | (__i << 16);
	}
	
	public static int rotateLeft(int __i, int __d)
	{
		throw new todo.TODO();
	}
	
	public static int rotateRight(int __i, int __d)
	{
		throw new todo.TODO();
	}
	
	/**
	 * Returns the sign of the given integer value, this will be {@code -1}
	 * for negative values, {@code 0} for zero, and {@code 1} for positive
	 * values.
	 *
	 * @param __v The value to get the sign for.
	 * @return The sign of the given value.
	 * @since 2019/05/11
	 */
	public static int signum(int __v)
	{
		if (__v < 0)
			return -1;
		else if (__v > 1)
			return 1;
		else
			return 0;
	}
	
	public static String toBinaryString(int __a)
	{
		throw new todo.TODO();
	}
	
	public static String toHexString(int __a)
	{
		throw new todo.TODO();
	}
	
	public static String toOctalString(int __a)
	{
		throw new todo.TODO();
	}
	
	/**
	 * Converts the value to a string using the given radix.
	 *
	 * @param __v The input value.
	 * @param __r The radix of the string, if it exceeds the maximum
	 * permitted radix specified in {@link Character} then this is set to 10.
	 * @return The resulting string.
	 * @since 2018/09/23
	 */
	public static String toString(int __v, int __r)
	{
		// This is effectively the same code as the long version, so to reduce
		// duplication just forward along. This way we only need to consider
		// one version
		return Long.toString(__v, __r);
	}
	
	/**
	 * Calls {@link Integer#toString(int, int)} with a radix of 10.
	 *
	 * @param __v The input value.
	 * @return The resulting string.
	 * @since 2018/09/23
	 */
	public static String toString(int __v)
	{
		return Integer.toString(__v, 10);
	}
	
	/**
	 * Returns the value of the specified string using the given radix.
	 *
	 * @param __v The String to decode.
	 * @param __r The radix to use.
	 * @return The boxed value.
	 * @throws NumberFormatException If the string is not valid or the radix
	 * is outside of the valid bounds.
	 * @since 2018/10/12
	 */
	public static Integer valueOf(String __v, int __r)
		throws NumberFormatException
	{
		return Integer.parseInt(__v, __r);
	}
	
	/**
	 * Returns the value of the specified string.
	 *
	 * @param __v The String to decode.
	 * @return The boxed value.
	 * @throws NumberFormatException If the string is not valid.
	 * @since 2018/10/12
	 */
	public static Integer valueOf(String __v)
		throws NumberFormatException
	{
		return Integer.parseInt(__v, 10);
	}
	
	/**
	 * Returns a instance of the given value, this may be cached.
	 *
	 * @param __v The value to box.
	 * @return The boxed value.
	 * @since 2018/09/23
	 */
	@ImplementationNote("This is not cached.")
	public static Integer valueOf(int __v)
	{
		return new Integer(__v);
	}
}

