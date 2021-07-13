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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This contains the annotation table which stores all of the annotation
 * information. No visibility information is stored because that is not needed
 * in SquirrelJME either by the run-time or the compiler.
 *
 * @since 2018/05/15
 */
public final class AnnotationTable
	implements Iterable<Annotation>
{
	/** The annotations which have been declared. */
	private final Map<ClassName, Annotation> _annotations;
	
	/**
	 * Initializes the annotation table.
	 *
	 * @param __e The input annotations.
	 * @throws InvalidClassFormatException If the class is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/05/15
	 */
	public AnnotationTable(Annotation... __e)
		throws InvalidClassFormatException, NullPointerException
	{
		this(Arrays.<Annotation>asList((__e != null ? __e :
			new Annotation[0])));
	}
	
	/**
	 * Initializes the annotation table.
	 *
	 * @param __e The input annotations.
	 * @throws InvalidClassFormatException If the class is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/05/15
	 */
	public AnnotationTable(Iterable<Annotation> __e)
		throws InvalidClassFormatException, NullPointerException
	{
		if (__e == null)
			throw new NullPointerException("NARG");
		
		Map<ClassName, Annotation> rv = new LinkedHashMap<>();
		for (Annotation e : __e)
		{
			// {@squirreljme.error JC1w Duplicate annotation declared. (The
			// declared annotation)}
			ClassName name = e.type();
			if (rv.containsKey(name))
				throw new InvalidClassFormatException(String.format("JC1w %s",
					name));
			
			rv.put(name, e);
		}
		
		this._annotations = rv;
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
	 * @since 2018/05/16
	 */
	@Override
	public final Iterator<Annotation> iterator()
	{
		return UnmodifiableIterator.<Annotation>of(
			this._annotations.values());
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
	 * Decodes the visible and invisible annotations that may exist for a
	 * class, field, or method.
	 *
	 * @param __pool The constant pool.
	 * @param __attrs The input attributes.
	 * @return The parsed annotation values.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/05/15
	 */
	public static AnnotationTable parse(Pool __pool,
		AttributeTable __attrs)
		throws IOException, NullPointerException
	{
		if (__pool == null || __attrs == null)
			throw new NullPointerException("NARG");
		
		// Parse visible then invisible annotations
		List<Annotation> rv = new ArrayList<>();
		for (int z = 0; z < 2; z++)
			try (DataInputStream di = __attrs.open((z == 0 ?
				"RuntimeVisibleAnnotations" : "RuntimeInvisibleAnnotations")))
			{
				if (di != null)
				{
					int n = di.readUnsignedShort();
					for (int i = 0; i < n; i++)
						rv.add(Annotation.parse(__pool, di));
				}
			}
		
		return new AnnotationTable(rv);
	}
}

