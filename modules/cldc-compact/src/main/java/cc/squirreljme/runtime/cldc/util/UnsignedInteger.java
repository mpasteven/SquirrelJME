// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.runtime.cldc.util;

/**
 * This class manages a boxed like representation of unsigned integers.
 *
 * @since 2018/11/03
 */
public final class UnsignedInteger
	extends Number
	implements Comparable<UnsignedInteger>
{
	/** The stored value. */
	protected final int value;
	
	/**
	 * Initializes the unsigned integer.
	 *
	 * @param __v The value to use.
	 * @since 2018/11/03
	 */
	public UnsignedInteger(int __v)
	{
		throw new todo.TODO();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/11/03
	 */
	@Override
	public final int compareTo(UnsignedInteger __i)
	{
		return UnsignedInteger.compareUnsigned(this.value, __i.value);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/11/03
	 */
	@Override
	public final double doubleValue()
	{
		throw new todo.TODO();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/11/03
	 */
	@Override
	public final float floatValue()
	{
		throw new todo.TODO();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/11/03
	 */
	@Override
	public final boolean equals(Object __o)
	{
		throw new todo.TODO();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/11/03
	 */
	@Override
	public final int hashCode()
	{
		throw new todo.TODO();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/11/03
	 */
	@Override
	public final int intValue()
	{
		throw new todo.TODO();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/11/03
	 */
	@Override
	public final long longValue()
	{
		throw new todo.TODO();
	}
	
	/**
	 * Compares two signed values.
	 *
	 * @param __a The first value.
	 * @param __b The second value.
	 * @return The resulting comparison.
	 * @since 2018/11/03
	 */
	public static final int compareSigned(int __a, int __b)
	{
		if (__a == __b)
			return 0;
		else if (__a < __b)
			return -1;
		else
			return 1;
	}
	
	/**
	 * Compares a signed and unsigned value.
	 *
	 * @param __a The first value.
	 * @param __ub The second value.
	 * @return The resulting comparison.
	 * @since 2018/11/03
	 */
	public static final int compareSignedUnsigned(int __a, int __ub)
	{
		throw new todo.TODO();
	}
	
	/**
	 * Compares two unsigned values.
	 *
	 * @param __a The first value.
	 * @param __b The second value.
	 * @return The resulting comparison, {@code 0} if equal or a
	 * positive/negative value.
	 * @since 2018/11/03
	 */
	@SuppressWarnings("MagicNumber")
	public static int compareUnsigned(int __a, int __b)
	{
		// Quick shortcut if they are the same number
		if (__a == __b)
			return 0;
		
		// Check if there is a sign bit
		boolean na = ((__a & 0x8000_0000) != 0),
			nb = ((__b & 0x8000_0000) != 0);
		
		// Comparison is the same as signed integers if the sign is the same
		// 0x80000000 == -2147483648
		// 0xFFFFFFFF == -1
		if (na == nb)
			return UnsignedInteger.compareSigned(__a, __b);
		
		// Only A has sign bit, it is always greater
		else if (na && !nb)
			return 1;
		
		// Only B has sign bit, it is always lesser
		else
			return -1;
	}
}

