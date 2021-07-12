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
 * This represents the name of an API.
 *
 * @since 2017/11/30
 */
public final class APIName
	implements Comparable<APIName>
{
	/** The name of the API. */
	protected final String string;
	
	/**
	 * Initializes the API name from the given string.
	 *
	 * @param __n The name of the API.
	 * @throws InvalidSuiteException If the suite is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/30
	 */
	public APIName(String __n)
		throws InvalidSuiteException, NullPointerException
	{
		if (__n == null)
			throw new NullPointerException("NARG");
		
		// Force all APIs to be uppercase
		__n = StringUtils.toUpperCaseNoLocale(__n);
		
		// {@squirreljme.error DG01 An illegal character was
		// specified in the API name. (The API name)}
		if (StringUtils.firstIndex("\0\r\n:;", __n) >= 0)
			throw new InvalidSuiteException(String.format("DG01 %s", __n));
		
		// {@squirreljme.error DG02 API name cannot be blank.}
		if (__n.length() <= 0)
			throw new InvalidSuiteException("DG02");
		
		this.string = __n;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/11/30
	 */
	@Override
	public int compareTo(APIName __o)
	{
		return this.string.compareTo(__o.string);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/11/30
	 */
	@Override
	public boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (!(__o instanceof APIName))
			return false;
		
		return this.string.equals(((APIName)__o).string);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/11/30
	 */
	@Override
	public int hashCode()
	{
		return this.string.hashCode();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/11/30
	 */
	@Override
	public String toString()
	{
		return this.string;
	}
}

