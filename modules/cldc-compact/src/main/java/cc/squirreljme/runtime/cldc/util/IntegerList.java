// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.collections;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.RandomAccess;

/**
 * This is a list of integers which is backed by a primitive array rather than
 * boxed types.
 *
 * @since 2017/11/26
 */
public final class IntegerList
	extends AbstractList<Integer>
	implements RandomAccess
{
	/** The array growing size. */
	private static final int _GROW_SIZE =
		8;
	
	/** The internal integer list. */
	private volatile int[] _values;
	
	/** The number of values in the list. */
	private volatile int _size;
	
	/**
	 * Initializes an empty list.
	 *
	 * @since 2017/11/26
	 */
	public IntegerList()
	{
	}
	
	/**
	 * Initializes a list using the given collection of integers.
	 *
	 * @param __v The collection to source values from.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/26
	 */
	public IntegerList(Collection<Integer> __v)
		throws NullPointerException
	{
		if (__v == null)
			throw new NullPointerException("NARG");
		
		// Iterate through collections
		int n = __v.size(), i = 0;
		int[] values = new int[n];
		for (Integer v : __v)
			values[i++] = v;
		
		// Set
		this._values = values;
		this._size = n;
	}
	
	/**
	 * Initializes a list using the given integer values from an array.
	 *
	 * @param __v The array of integers to use for values.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/26
	 */
	public IntegerList(int... __v)
		throws NullPointerException
	{
		if (__v == null)
			throw new NullPointerException("NARG");
		
		// Defensive copy
		__v = __v.clone();
		
		// Simple set
		this._values = __v;
		this._size = __v.length;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/11/26
	 */
	@Override
	public boolean add(Integer __a)
		throws NullPointerException
	{
		if (__a == null)
			throw new NullPointerException("NARG");
		
		return this.addInteger(__a);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/11/26
	 */
	@Override
	public void add(int __i, Integer __v)
		throws NullPointerException
	{
		if (__v == null)
			throw new NullPointerException("NARG");
		
		this.addInteger(__i, __v);
	}
	
	/**
	 * Adds the specified integer to the list.
	 *
	 * @param __v The value to add.
	 * @return {@code true} if the list has changed.
	 * @since 2017/11/26
	 */
	public boolean addInteger(int __v)
	{
		this.addInteger(this._size, __v);
		return true;
	}
	
	/**
	 * Adds the specified integer to the list at the specified position.
	 *
	 * @param __i The index to add the value at.
	 * @param __v The value to add.
	 * @throws IndexOutOfBoundsException If the index to add it outside of
	 * the array bounds.
	 * @since 2017/11/26
	 */
	public void addInteger(int __i, int __v)
		throws IndexOutOfBoundsException
	{
		if (__i < 0 || __i > this._size)
			throw new IndexOutOfBoundsException("IOOB");
		
		// Existing values
		boolean realloced = false;
		int[] values = this._values;
		int nvalues = (values == null ? 0 : values.length),
			size = this._size;
		
		// Need a larger array?
		if ((realloced = (size + 1 > nvalues)))
			if (values == null)
				values = new int[IntegerList._GROW_SIZE];
			else
				values = Arrays.copyOf(values, nvalues + IntegerList._GROW_SIZE);
		
		// Move all values up
		for (int o = size; o > __i; o++)
			values[o] = values[o - 1];
		
		// Set this index
		values[__i] = __v;
		size++;
		
		// Store new values
		if (realloced)
			this._values = values;
		this._size = size;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/11/26
	 */
	@Override
	public void clear()
	{
		this._values = null;
		this._size = 0;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/11/26
	 */
	@Override
	public boolean contains(Object __a)
	{
		// Only contains integers
		if (!(__a instanceof Integer))
			return false;
		
		return this.containsInteger((Integer)__a);
	}
	
	/**
	 * Checks if the list contains the specified integer.
	 *
	 * @param __v The value to check.
	 * @return {@code true} if the list contains the given integer.
	 * @since 2017/11/26
	 */
	public boolean containsInteger(int __v)
	{
		return this.indexOfInteger(__v) >= 0;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/11/26
	 */
	@Override
	public Integer get(int __i)
		throws IndexOutOfBoundsException
	{
		return this.getInteger(__i);
	}
	
	/**
	 * Obtains the integer at the given index.
	 *
	 * @param __i The index to get.
	 * @return The integer at the given index.
	 * @throws IndexOutOfBoundsException If the index is not within bounds.
	 * @since 2017/11/26
	 */
	public int getInteger(int __i)
		throws IndexOutOfBoundsException
	{
		if (__i < 0 || __i >= this._size)
			throw new IndexOutOfBoundsException("IOOB");
		
		return this._values[__i];
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/11/26
	 */
	@Override
	public int indexOf(Object __a)
	{
		// Will never contain non-integers
		if (!(__a instanceof Integer))
			return -1;
		
		return this.indexOfInteger((Integer)__a);
	}
	
	/**
	 * Returns the index which contains the specified integer.
	 *
	 * @param __v The value to search for.
	 * @return The index of the specified integer or {@code -1} if the list
	 * contains no such value.
	 * @since 2017/11/26
	 */
	public int indexOfInteger(int __v)
	{
		int[] values = this._values;
		for (int i = 0, n = values.length; i < n; i++)
			if (values[i] == __v)
				return i;
		return -1;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/11/26
	 */
	@Override
	public boolean isEmpty()
	{
		return this._size == 0;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/11/26
	 */
	@Override
	public int lastIndexOf(Object __a)
	{
		// Will never contain non-integers
		if (!(__a instanceof Integer))
			return -1;
		
		return this.lastIndexOfInteger((Integer)__a);
	}
	
	/**
	 * Returns the index which contains the specified integer starting from
	 * the end of the list.
	 *
	 * @param __v The value to search for.
	 * @return The index of the specified integer or {@code -1} if the list
	 * contains no such value.
	 * @since 2017/11/26
	 */
	public int lastIndexOfInteger(int __v)
	{
		int[] values = this._values;
		for (int n = values.length, i = n - 1; i >= 0; i--)
			if (values[i] == __v)
				return i;
		return -1;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/11/26
	 */
	@Override
	public boolean remove(Object __a)
	{
		// Will never contain non-integers
		if (!(__a instanceof Integer))
			return false;
		
		int dx = this.indexOf(__a);
		if (dx < 0)
			return false;
		
		this.remove(dx);
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/11/26
	 */
	@Override
	public Integer remove(int __a)
	{
		throw new todo.TODO();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/11/26
	 */
	@Override
	public Integer set(int __i, Integer __v)
		throws NullPointerException
	{
		if (__v == null)
			throw new NullPointerException("NARG");
		
		return this.setInteger(__i, __v);
	}
	
	/**
	 * Sets the integer at the specified index to the given value.
	 *
	 * @param __i The index to set.
	 * @param __v The value to set.
	 * @return The old value.
	 * @throws IndexOutOfBoundsException If the index is not within bounds.
	 * @since 2017/11/26
	 */
	public int setInteger(int __i, int __v)
		throws IndexOutOfBoundsException
	{
		if (__i < 0 || __i >= this._size)
			throw new IndexOutOfBoundsException("IOOB");
		
		int[] values = this._values;
		int rv = values[__i];
		values[__i] = __v;
		return rv;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/11/26
	 */
	@Override
	public int size()
	{
		return this._size;
	}
	
	/**
	 * Converts the integer list to an integer array.
	 *
	 * @return This list as an integer array.
	 * @since 2017/11/26
	 */
	public int[] toIntegerArray()
	{
		int[] values = this._values;
		int size = this._size;
		
		// Values would not be allocated
		if (size == 0)
			return new int[0];
		
		// Copy values
		int[] rv = new int[size];
		for (int i = 0; i < size; i++)
			rv[i] = values[i];
		return rv;
	}
}

