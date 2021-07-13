// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.suite;

import cc.squirreljme.runtime.cldc.util.StringUtils;

/**
 * This represents the vendor of a midlet suite.
 *
 * @since 2016/10/12
 */
public final class SuiteVendor
	implements Comparable<SuiteVendor>
{
	/** String value. */
	protected final String string;
	
	/**
	 * Initializes the suite vendor.
	 *
	 * @param __v The value to parse.
	 * @throws InvalidSuiteException If the input is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/10/12
	 */
	public SuiteVendor(String __v)
		throws InvalidSuiteException, NullPointerException
	{
		// Check
		if (__v == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error DG0h An illegal character was
		// specified in the midlet suite vendor. (The midlet suite
		// vendor)}
		if (StringUtils.firstIndex("\0\r\n:;", __v) >= 0)
			throw new InvalidSuiteException(String.format("AD0d %s", __v));
		
		this.string = __v;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/10/12
	 */
	@Override
	public int compareTo(SuiteVendor __o)
	{
		return this.string.compareTo(__o.string);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/10/12
	 */
	@Override
	public boolean equals(Object __o)
	{
		// Check
		if (!(__o instanceof SuiteVendor))
			return false;
		
		return this.string.equals(((SuiteVendor)__o).string);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/10/12
	 */
	@Override
	public int hashCode()
	{
		return this.string.hashCode();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/10/12
	 */
	@Override
	public String toString()
	{
		return this.string;
	}
}

