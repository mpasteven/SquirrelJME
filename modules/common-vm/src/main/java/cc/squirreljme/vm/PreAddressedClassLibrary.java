// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm;

import cc.squirreljme.runtime.cldc.debug.Debugging;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * This is a class library that is pre-addressed at a fixed location.
 *
 * @since 2020/12/13
 */
public final class PreAddressedClassLibrary
	implements VMClassLibrary
{
	/** The offset to the library. */
	public final int offset;
	
	/** The size of the library. */
	public final int size;
	
	/** The name of the library. */
	protected final String name;
	
	/**
	 * Initializes the pre-addressed class library.
	 * 
	 * @param __offset The offset to the class library.
	 * @param __size The size of the library.
	 * @param __name The name of the library.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/13
	 */
	public PreAddressedClassLibrary(int __offset, int __size, String __name)
		throws NullPointerException
	{
		if (__name == null)
			throw new NullPointerException("NARG");
		
		this.offset = __offset;
		this.size = __size;
		this.name = __name;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/12/13
	 */
	@Override
	public String[] listResources()
	{
		// {@squirreljme.error AK03 Pre-addressed class libraries do not
		// support lookup.}
		throw new IllegalStateException("AK03");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/12/13
	 */
	@Override
	public String name()
	{
		return this.name;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/06/14
	 */
	@Override
	public Path path()
	{
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/12/13
	 */
	@Override
	public InputStream resourceAsStream(String __rc)
		throws IOException, NullPointerException
	{
		// {@squirreljme.error AK02 Pre-addressed class libraries do not
		// support lookup.}
		throw new IllegalStateException("AK02");
	}
}
