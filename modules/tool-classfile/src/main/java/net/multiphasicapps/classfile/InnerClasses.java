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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * This contains the table of inner class references.
 *
 * @since 2018/05/15
 */
public final class InnerClasses
	implements Iterable<InnerClass>
{
	/** The declared inner classes. */
	private final InnerClass[] _inners;
	
	/**
	 * Initializes the inner classes.
	 *
	 * @param __i The inner classes.
	 * @throws InvalidClassFormatException If the inner classes are not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/05/16
	 */
	public InnerClasses(InnerClass... __i)
		throws InvalidClassFormatException, NullPointerException
	{
		this(Arrays.<InnerClass>asList((__i != null ? __i :
			new InnerClass[0])));
	}
	
	/**
	 * Initializes the inner classes.
	 *
	 * @param __i The inner classes.
	 * @throws InvalidClassFormatException If the inner classes are not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/05/16
	 */
	public InnerClasses(Iterable<InnerClass> __i)
		throws InvalidClassFormatException, NullPointerException
	{
		if (__i == null)
			throw new NullPointerException("NARG");
		
		List<InnerClass> rv = new ArrayList<>();
		for (InnerClass i : __i)
		{
			if (i == null)
				throw new NullPointerException("NARG");
			
			rv.add(i);
		}
		
		this._inners = rv.<InnerClass>toArray(new InnerClass[rv.size()]);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/05/15
	 */
	@Override
	public final boolean equals(Object __o)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/05/15
	 */
	@Override
	public final int hashCode()
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/06/16
	 */
	@Override
	public final Iterator<InnerClass> iterator()
	{
		return UnmodifiableIterator.<InnerClass>of(this._inners);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/05/15
	 */
	@Override
	public final String toString()
	{
		throw Debugging.todo();
	}
	
	/**
	 * Parses the information which pertains to the inner classes.
	 *
	 * @param __pool The constant pool.
	 * @param __attrs The input attributes.
	 * @return The parsed inner class information.
	 * @throws InvalidClassFormatException If the inner classes are not
	 * correct.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/05/15
	 */
	public static InnerClasses parse(Pool __pool,
		AttributeTable __attrs)
		throws InvalidClassFormatException, IOException, NullPointerException
	{
		if (__pool == null || __attrs == null)
			throw new NullPointerException("NARG");
		
		// Only if the attribute exists
		Attribute attr = __attrs.get("InnerClasses");
		if (attr == null)
			return new InnerClasses();
		
		List<InnerClass> rv = new ArrayList<>();
		try (DataInputStream in = attr.open())
		{
			// Read each one
			int n = in.readUnsignedShort();
			for (int i = 0; i < n; i++)
			{
				ClassName innerclass = __pool.<ClassName>require(
					ClassName.class, in.readUnsignedShort());
				ClassName outerclass = __pool.<ClassName>get(
					ClassName.class, in.readUnsignedShort());
				
				UTFConstantEntry rawname = __pool.<UTFConstantEntry>get(
					UTFConstantEntry.class, in.readUnsignedShort());
				ClassIdentifier name = (rawname == null ? null :
					new ClassIdentifier(rawname.toString()));
				
				InnerClassFlags flags = new InnerClassFlags(
					in.readUnsignedShort());
				
				// Add them
				if (outerclass == null && name == null)
					rv.add(new InnerClass(innerclass, flags));
				else
				{
					rv.add(new InnerClass(innerclass, outerclass,
						name, flags));
				}
			}
		}
		
		return new InnerClasses(rv);
	}
}

