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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class is used to store and contain the basic constant pool which
 * consists of values, indexes, and parts.
 *
 * This class is thread safe.
 *
 * @since 2019/07/15
 */
public final class BasicPoolBuilder
	implements Iterable<BasicPoolEntry>
{
	/** Entries which exist in the constant pool. */
	protected final Map<Object, BasicPoolEntry> entries =
		new LinkedHashMap<>();
	
	/*
	  Base pool initialization.
	 
	  @since 2019/07/15
	 */
	{
		// The first entry of the constant pool is always null!
		this.entries.put(null, new BasicPoolEntry(0, null, new int[0]));
	}
	
	/**
	 * Adds a basic entry.
	 *
	 * @param __v The value.
	 * @param __parts The parts to use.
	 * @return The entry for this value.
	 * @throws IllegalStateException If the entry already exists within
	 * the pool.
	 * @since 2019/07/15
	 */
	public final BasicPoolEntry add(Object __v, int... __parts)
		throws IllegalStateException
	{
		Map<Object, BasicPoolEntry> entries = this.entries;
		
		// Lock on self, since there could be multiple processing of pool
		// entries in the future
		synchronized (this)
		{
			// {@squirreljme.error JC4c The specified entry already exists
			// within the pool. (The entry being added)}
			if (entries.containsKey(__v))
				throw new IllegalStateException("JC4c " + __v);
			
			// Create, store, and use the new entry
			BasicPoolEntry rv;
			entries.put(__v, (rv = new BasicPoolEntry(
				entries.size(), __v, __parts)));
			return rv;
		}
	}
	
	/**
	 * Adds an already existing basic entry to this pool.
	 *
	 * @param __e The entry to add.
	 * @return The resulting basic entry which may be {@code __e} or another
	 * entry.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/08/25
	 */
	public final BasicPoolEntry addEntry(BasicPoolEntry __e)
		throws NullPointerException
	{
		if (__e == null)
			throw new NullPointerException("NARG");
		
		throw Debugging.todo();
	}
	
	/**
	 * Adds an internal entry or returns the pre-existing value if it is
	 * already in the pool.
	 *
	 * @param __v The value.
	 * @param __parts The parts to use.
	 * @return The entry for this value.
	 * @since 2019/07/15
	 */
	public final BasicPoolEntry addOrGet(Object __v, int... __parts)
	{
		synchronized (this)
		{
			// If the entry already exists use it
			BasicPoolEntry rv = this.getByValue(__v);
			if (rv != null)
				return rv;
			
			// Otherwise add it
			return this.add(__v, __parts);
		}
	}
	
	/**
	 * Searches the constant pool and returns the entry at the given index.
	 *
	 * @param __dx The index to get.
	 * @return The entry at the given index or {@code null} if there is none.
	 * @since 2019/07/15
	 */
	public final BasicPoolEntry getByIndex(int __dx)
	{
		// Find matching index
		Map<Object, BasicPoolEntry> entries = this.entries;
		synchronized (this)
		{
			for (BasicPoolEntry e : entries.values())
				if (e.index == __dx)
					return e;
		}
		
		// Not found
		return null;
	}
	
	/**
	 * Gets the given entry by the key.
	 *
	 * @param __v The value to get.
	 * @return The entry for the value or {@code null} if it is not in the
	 * pool.
	 * @since 2019/07/15
	 */
	public final BasicPoolEntry getByValue(Object __v)
	{
		synchronized (this)
		{
			return this.entries.get(__v);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/07/17
	 */
	@Override
	public final Iterator<BasicPoolEntry> iterator()
	{
		// Get elements
		BasicPoolEntry[] elems;
		synchronized (this)
		{
			Map<Object, BasicPoolEntry> entries = this.entries;
			elems = entries.values().toArray(
				new BasicPoolEntry[entries.size()]);
		}
		
		// Iterate over
		return UnmodifiableIterator.<BasicPoolEntry>of(elems);
	}
	
	/**
	 * Returns the size of the constant pool.
	 *
	 * @return The pool size.
	 * @since 2019/07/15
	 */
	public final int size()
	{
		synchronized (this)
		{
			return this.entries.size();
		}
	}
}

