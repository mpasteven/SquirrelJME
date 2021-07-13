// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.pool;

import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.runtime.cldc.util.ShortArrayList;
import dev.shadowtail.classfile.mini.MinimizedPoolEntryType;
import java.util.Objects;

/**
 * Represents an entry within the constant pool.
 *
 * @since 2019/07/15
 */
public final class BasicPoolEntry
{
	/** The index of this entry. */
	public final int index;
	
	/** Offset of this entry. */
	public final int offset;
	
	/** The value. */
	public final Object value;
	
	/** The parts. */
	private final short[] _parts;
	
	/** Cached hashcode. */
	private int _hash;
	
	/**
	 * Initializes a new entry.
	 *
	 * @param __dx The entry index.
	 * @param __v The value.
	 * @param __parts The parts.
	 * @since 2019/09/11
	 */
	public BasicPoolEntry(int __dx, Object __v, short[] __parts)
	{
		this.index = __dx;
		this.value = __v;
		this.offset = -1;
		this._parts = (__parts == null ? new short[0] : __parts.clone());
	}
	
	/**
	 * Initializes a new entry.
	 *
	 * @param __dx The entry index.
	 * @param __v The value.
	 * @param __parts The parts.
	 * @since 2019/07/15
	 */
	public BasicPoolEntry(int __dx, Object __v, int[] __parts)
	{
		this(__dx, __v, __parts, -1);
	}
	
	/**
	 * Initializes a new entry.
	 *
	 * @param __dx The entry index.
	 * @param __v The value.
	 * @param __parts The parts.
	 * @param __eoff The entry offset.
	 * @since 2019/09/14
	 */
	public BasicPoolEntry(int __dx, Object __v, int[] __parts, int __eoff)
	{
		this.index = __dx;
		this.value = __v;
		this.offset = __eoff;
		
		// Reduce parts to shorts
		int n = (__parts == null ? 0 : __parts.length);
		short[] parts = new short[n];
		for (int i = 0; i < n; i++)
			parts[i] = (short)__parts[i];
		this._parts = parts;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/08/25
	 */
	@Override
	public final boolean equals(Object __o)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/08/25
	 */
	@Override
	public final int hashCode()
	{
		int rv = this._hash;
		if (rv != 0)
			return rv;
		
		rv = this.index ^
			this.offset ^
			Objects.hashCode(this.value) ^
			ShortArrayList.asList(this._parts).hashCode();
		
		this._hash = rv;
		return rv;
	}
	
	/**
	 * Returns a single part value.
	 *
	 * @param __i The part index.
	 * @return The resulting part.
	 * @throws IndexOutOfBoundsException If the part index is not within
	 * bounds.
	 * @since 2019/09/14
	 */
	public final int part(int __i)
	{
		return this._parts[__i] & 0xFFFF;
	}
	
	/**
	 * Returns the parts used.
	 *
	 * @return The used parts.
	 * @since 2019/07/15
	 */
	public final short[] parts()
	{
		return this._parts.clone();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/01/13
	 */
	@Override
	public String toString()
	{
		return String.format("#%d(%s):%s",
			this.index, this.type(), this.value);
	}
	
	/**
	 * Returns the type of entry that this is.
	 *
	 * @return The entry type.
	 * @since 2019/09/14
	 */
	public final MinimizedPoolEntryType type()
	{
		Object value = this.value;
		if (value == null)
			return MinimizedPoolEntryType.NULL;
		return MinimizedPoolEntryType.ofClass(value.getClass());
	}
	
	/**
	 * Returns the value of this entry.
	 *
	 * @param <T> The type to use.
	 * @param __cl The class of this type.
	 * @return The resulting type.
	 * @throws ClassCastException If the class type is not correct.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/09/01
	 */
	public final <T> T value(Class<T> __cl)
		throws ClassCastException, NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException("NARG");
		
		return __cl.cast(this.value);
	}
}

