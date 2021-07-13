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

/**
 * This represents the type of the dependency that is to be included.
 *
 * @since 2017/02/22
 */
public enum SuiteDependencyType
{
	/** Liblet. */
	LIBLET,
	
	/** Standard. */
	STANDARD,
	
	/** Service. */
	SERVICE,
	
	/** Proprietary. */
	PROPRIETARY,
	
	/** End. */
	;
	
	/**
	 * {@inheritDoc}
	 * @since 2017/02/22
	 */
	@Override
	public String toString()
	{
		// Convert string
		switch (this)
		{
			case LIBLET:		return "liblet";
			case STANDARD:		return "standard";
			case SERVICE:		return "service";
			case PROPRIETARY:	return "proprietary";
			
			default:
				throw new todo.OOPS();
		}
	}
	
	/**
	 * Returns the dependency type based on the input string.
	 *
	 * @param __s The input string to parse.
	 * @return The dependency type for the given string.
	 * @throws InvalidSuiteException If the dependency type is not
	 * valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/02/22
	 */
	public static SuiteDependencyType of(String __s)
		throws InvalidSuiteException, NullPointerException
	{
		// Check
		if (__s == null)
			throw new NullPointerException("NARG");
		
		// Depends
		switch (__s.trim())
		{
			case "liblet":		return SuiteDependencyType.LIBLET;
			case "standard":	return SuiteDependencyType.STANDARD;
			case "service":		return SuiteDependencyType.SERVICE;
			case "proprietary":	return SuiteDependencyType.PROPRIETARY;
			
				// {@squirreljme.error DG0a The specified string is not a valid
				// dependency type. (The input string)}
			default:
				throw new InvalidSuiteException(
					String.format("AR0a %s", __s));
		}
	}
}

