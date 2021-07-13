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

import cc.squirreljme.jvm.mle.TypeShelf;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public final class Short
	extends Number
	implements Comparable<Short>
{
	public static final short MAX_VALUE =
		32767;
	
	public static final short MIN_VALUE =
		-32768;
	
	public static final int SIZE =
		16;
	
	/** The class representing the primitive type. */
	public static final Class<Short> TYPE =
		TypeShelf.<Short>typeToClass(TypeShelf.typeOfShort());
	
	/** The value of this integer. */
	private final short _value;
	
	/** The string representation of this value. */
	private Reference<String> _string;
	
	/**
	 * Wraps the boxed value.
	 *
	 * @param __v The value to wrap.
	 * @since 2018/11/14
	 */
	public Short(short __v)
	{
		this._value = __v;
	}
	
	public Short(String __a)
		throws NumberFormatException
	{
		if (false)
			throw new NumberFormatException();
		throw new todo.TODO();
	}
	
	@Override
	public byte byteValue()
	{
		throw new todo.TODO();
	}
	
	@Override
	public int compareTo(Short __a)
	{
		throw new todo.TODO();
	}
	
	@Override
	public double doubleValue()
	{
		throw new todo.TODO();
	}
	
	@Override
	public boolean equals(Object __a)
	{
		throw new todo.TODO();
	}
	
	@Override
	public float floatValue()
	{
		throw new todo.TODO();
	}
	
	@Override
	public int hashCode()
	{
		throw new todo.TODO();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/25
	 */
	@Override
	public int intValue()
	{
		return this._value;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/25
	 */
	@Override
	public long longValue()
	{
		return this._value;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/25
	 */
	@Override
	public short shortValue()
	{
		return this._value;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/11/14
	 */
	@Override
	public String toString()
	{
		Reference<String> ref = this._string;
		String rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._string = new WeakReference<>(
				(rv = Short.toString(this._value)));
		
		return rv;
	}
	
	public static Short decode(String __a)
		throws NumberFormatException
	{
		if (false)
			throw new NumberFormatException();
		throw new todo.TODO();
	}
	
	public static short parseShort(String __a, int __b)
		throws NumberFormatException
	{
		if (false)
			throw new NumberFormatException();
		throw new todo.TODO();
	}
	
	public static short parseShort(String __a)
		throws NumberFormatException
	{
		if (false)
			throw new NumberFormatException();
		throw new todo.TODO();
	}
	
	/**
	 * Reverses the given bytes.
	 * 
	 * @param __i The value to reverse.
	 * @return The reversed value.
	 * @since 2021/02/18
	 */
	public static short reverseBytes(short __i)
	{
		// 0xBBAA -> 0xAABB
		return (short)((__i >>> 8) | (__i << 8));
	}
	
	/**
	 * Returns the string representation of the short value.
	 *
	 * @param __v The value to represent.
	 * @return The string representation of it.
	 * @since 2018/11/14
	 */
	public static String toString(short __v)
	{
		return Integer.toString(__v, 10);
	}
	
	public static Short valueOf(String __a, int __b)
		throws NumberFormatException
	{
		if (false)
			throw new NumberFormatException();
		throw new todo.TODO();
	}
	
	public static Short valueOf(String __a)
		throws NumberFormatException
	{
		if (false)
			throw new NumberFormatException();
		throw new todo.TODO();
	}
	
	/**
	 * Boxes the given short value.
	 *
	 * @param __v The value to box.
	 * @return The boxed value.
	 * @since 2018/11/14
	 */
	public static Short valueOf(short __v)
	{
		return new Short(__v);
	}
}

