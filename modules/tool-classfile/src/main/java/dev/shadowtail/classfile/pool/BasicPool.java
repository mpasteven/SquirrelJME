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
import cc.squirreljme.runtime.cldc.util.UnmodifiableIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This represents a basic constant pool.
 *
 * @since 2019/07/17
 */
public final class BasicPool
	implements Iterable<BasicPoolEntry>
{
	/** Entries which exist in the constant pool. */
	private final Map<Object, BasicPoolEntry> _entries;
	
	/** Linear entries within the pool. */
	private final List<BasicPoolEntry> _linear;
	
	/**
	 * Input for the basic pool.
	 *
	 * @param __it The input entries.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/09/11
	 */
	public BasicPool(BasicPoolEntry... __it)
		throws NullPointerException
	{
		this(Arrays.<BasicPoolEntry>asList(__it));
	}
	
	/**
	 * Input for the basic pool.
	 *
	 * @param __it The input entries.
	 * @throws IllegalArgumentException If a pool entry was duplicated.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/09/07
	 */
	public BasicPool(Iterable<BasicPoolEntry> __it)
		throws IllegalArgumentException, NullPointerException
	{
		if (__it == null)
			throw new NullPointerException("NARG");
		
		// Initial guessed size?
		int igs = ((__it instanceof Collection) ?
			((Collection)__it).size() : 16);
			
		// Where the entries go
		List<BasicPoolEntry> linear = new ArrayList<>(igs);
		Map<Object, BasicPoolEntry> entries = new LinkedHashMap<>();
		
		// Process entries
		for (BasicPoolEntry e : __it)
		{
			if (e == null)
				throw new NullPointerException("NARG");
			
			// Just add to the end of the list
			linear.add(e);
			
			// {@squirreljme.error JC4k Duplicated pool entry. (The entry)}
			if (null != entries.put(e.value, e))
				throw new IllegalArgumentException("JC4k " + e);
		}
		
		// Set
		this._entries = entries;
		this._linear = linear;
	}
	
	/**
	 * Gets the pool entry by index.
	 *
	 * @param __i The index to get.
	 * @return The index of the given entry.
	 * @throws IndexOutOfBoundsException If the entry is not within bounds.
	 * @since 2019/09/11
	 */
	public final BasicPoolEntry byIndex(int __i)
		throws IndexOutOfBoundsException
	{
		return this._linear.get(__i);
	}
	
	/**
	 * Gets the pool value by index.
	 *
	 * @param <T> The class type.
	 * @param __cl The class type.
	 * @param __i The index to get.
	 * @return The index of the given entry.
	 * @throws IndexOutOfBoundsException If the entry is not within bounds.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/09/11
	 */
	public final <T> T byIndex(Class<T> __cl, int __i)
		throws ClassCastException, IndexOutOfBoundsException,
			NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException("NARG");
		
		return this.byIndex(__i).<T>value(__cl);
	}
	
	/**
	 * Gets the pool entry by value.
	 *
	 * @param __v The value to get.
	 * @return The entry or {@code null} if it was not found.
	 * @since 2019/09/11
	 */
	public final BasicPoolEntry byValue(Object __v)
		throws IndexOutOfBoundsException
	{
		return this._entries.get(__v);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/30
	 */
	@Override
	public Iterator<BasicPoolEntry> iterator()
	{
		return UnmodifiableIterator.<BasicPoolEntry>of(
			this._linear.iterator());
	}
	
	/**
	 * Returns the size of the pool.
	 *
	 * @return The pool size.
	 * @since 2019/09/11
	 */
	public final int size()
	{
		return this._entries.size();
	}
}

