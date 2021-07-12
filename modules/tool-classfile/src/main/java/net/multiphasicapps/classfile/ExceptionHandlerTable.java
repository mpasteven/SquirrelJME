// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.classfile;

import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.runtime.cldc.util.UnmodifiableIterator;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This represents a every exception that exists within a method.
 *
 * @since 2017/02/09
 */
public final class ExceptionHandlerTable
	implements Iterable<ExceptionHandler>
{
	/** The exception handler table. */
	private final ExceptionHandler[] _table;
	
	/** String representation. */
	private Reference<String> _string;
	
	/**
	 * Initializes the exception handler table.
	 *
	 * @param __t The entries to handle.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/10/09
	 */
	ExceptionHandlerTable(ExceptionHandler... __t)
		throws NullPointerException
	{
		if (__t == null)
			throw new NullPointerException("NARG");
		
		// Clone entries
		__t = __t.clone();
		for (ExceptionHandler e : __t)
			if (e == null)
				throw new NullPointerException("NARG");
		
		// Set
		this._table = __t;
	}
	
	/**
	 * Initializes the exception handler table.
	 *
	 * @param __t The entries to handle.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/03/22
	 */
	ExceptionHandlerTable(Collection<ExceptionHandler> __t)
		throws NullPointerException
	{
		if (__t == null)
			throw new NullPointerException("NARG");
		
		// Copy array
		ExceptionHandler[] t = __t.<ExceptionHandler>toArray(
			new ExceptionHandler[__t.size()]);
		for (ExceptionHandler e : t)
			if (e == null)
				throw new NullPointerException("NARG");
		
		this._table = t;
	}
	
	/**
	 * Return all of the exception handlers which apply to the given address.
	 *
	 * @param __pc The address to use.
	 * @return An array containing the exceptions which handle for the given
	 * address.
	 * @since 2018/10/13
	 */
	public final ExceptionHandler[] at(int __pc)
	{
		List<ExceptionHandler> rv = new ArrayList<>();
		
		// Add any handlers which are in range
		for (ExceptionHandler e : this._table)
			if (e.inRange(__pc))
				rv.add(e);
		
		return rv.<ExceptionHandler>toArray(new ExceptionHandler[rv.size()]);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/03/21
	 */
	@Override
	public final boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (!(__o instanceof ExceptionHandlerTable))
			return false;
		
		// The exception handler table has a strict order
		return Arrays.equals(this._table, ((ExceptionHandlerTable)__o)._table);
	}
	
	/**
	 * Returns the exception handler at the given index.
	 *
	 * @return The exception handler at the given index.
	 * @since 2017/02/09
	 */
	public final ExceptionHandler get(int __i)
	{
		return this._table[__i];
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/03/21
	 */
	@Override
	public final int hashCode()
	{
		// Just add up all of the entries quickly
		int rv = 0;
		for (ExceptionHandler e : this._table)
			rv += e.hashCode();
		return rv;
	}
	
	/**
	 * Is this exception table empty?
	 *
	 * @return If the table is empty.
	 * @since 2019/03/22
	 */
	public final boolean isEmpty()
	{
		return this._table.length == 0;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/03/22
	 */
	@Override
	public final Iterator<ExceptionHandler> iterator()
	{
		return UnmodifiableIterator.<ExceptionHandler>of(this._table);
	}
	
	/**
	 * Returns a map of all the ranges that exist within the exception table
	 * that are unique to each other.
	 *
	 * @return A mapping of ranges and table entries.
	 * @since 2019/03/21
	 */
	public final Map<InstructionAddressRange, ExceptionHandlerTable>
		mappedUniqueRanges()
	{
		Map<InstructionAddressRange, ExceptionHandlerTable> rv =
			new LinkedHashMap<>();
		
		// Start of range
		int rangestart = 0;
		ExceptionHandlerTable current = this.tableAt(0);
		
		// Go through all addresses in range
		// We include the end address because after that point there would
		// be no exception handlers after that point. This forces a range
		// to be specified where anything after this end point has no handlers
		int ma = this.maximumEndAddress();
		for (int i = 1; i <= ma; i++)
		{
			// Get table at this address
			ExceptionHandlerTable now = this.tableAt(i);
			
			// If the table is different, then a new range is used
			if (!current.equals(now))
			{
				// Store the range
				rv.put(new InstructionAddressRange(rangestart, i), current);
				
				// Set for next scan
				rangestart = i;
				current = now;
			}
		}
		
		// Always store the final range
		rv.put(new InstructionAddressRange(rangestart, ma), current);
		
		return rv;
	}
	
	/**
	 * Returns the maximum end address that is used in the table.
	 *
	 * @return The highest end address used in the table.
	 * @since 2019/03/21
	 */
	public final int maximumEndAddress()
	{
		int rv = 0;
		for (ExceptionHandler e : this._table)
		{
			int epc = e.endpc;
			if (epc > rv)
				rv = epc;
		}
		return rv;
	}
	
	/**
	 * Returns the maximum used start address in the table.
	 *
	 * @return The maximum start address.
	 * @since 2019/04/04
	 */
	public final int maximumStartAddress()
	{
		int rv = 0;
		for (ExceptionHandler e : this._table)
		{
			int spc = e.startpc;
			if (spc > rv)
				rv = spc;
		}
		return rv;
	}
	
	/**
	 * Returns the instruction range.
	 *
	 * @return The instruction range.
	 * @since 2019/03/21
	 */
	public final InstructionAddressRange range()
	{
		throw Debugging.todo();
	}
	
	/**
	 * Returns the number of exception handlers that exist.
	 *
	 * @return The total number of exception handlers.
	 * @since 2017/02/09
	 */
	public final int size()
	{
		return this._table.length;
	}
	
	/**
	 * Returns an exception handler table for the given address.
	 *
	 * @param __pc The address to get an exception handler table from.
	 * @return A table representing only the exceptions at a given address.
	 * @since 2019/03/21
	 */
	public final ExceptionHandlerTable tableAt(int __pc)
	{
		return new ExceptionHandlerTable(this.at(__pc));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/13
	 */
	@Override
	public final String toString()
	{
		Reference<String> ref = this._string;
		String rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._string = new WeakReference<>(
				(rv = Arrays.asList(this._table).toString()));
		
		return rv;
	}
	
	/**
	 * Parses the exception handler table.
	 *
	 * @param __is The input stream to read exceptions from.
	 * @param __pool The constant pool for class names.
	 * @param __len The length of the method in byte codes.
	 * @throws InvalidClassFormatException If the exception table is not valid.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/02/09
	 */
	public static ExceptionHandlerTable decode(DataInputStream __is,
		Pool __pool, int __len)
		throws InvalidClassFormatException, IOException, NullPointerException
	{
		// Check
		if (__is == null || __pool == null)
			throw new NullPointerException("NARG");
		
		// Read exception table count
		int n = __is.readUnsignedShort();
		ExceptionHandler[] table = new ExceptionHandler[n];
		for (int i = 0; i < n; i++)
		{
			// Read values
			int spc = __is.readUnsignedShort();
			int epc = __is.readUnsignedShort();
			int hpc = __is.readUnsignedShort();
			ClassName type = __pool.<ClassName>get(ClassName.class,
				__is.readUnsignedShort());
			
			// {@squirreljme.error JC2n Address is outside of the bounds of the
			// method. (The start address; The end address; The handler
			// address; The code length)}
			if (spc >= __len || epc > __len || hpc >= __len)
				throw new InvalidClassFormatException(String.format(
					"JC2n %d %d %d %d", spc, epc, hpc, __len));
			
			// Setup exception
			table[i] = new ExceptionHandler(spc, epc, hpc, type);
		}
		
		// Setup
		return new ExceptionHandlerTable(table);
	}
}

