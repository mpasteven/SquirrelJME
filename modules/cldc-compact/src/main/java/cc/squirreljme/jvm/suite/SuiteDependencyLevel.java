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
 * This represents the level of the dependency.
 *
 * @since 2017/02/22
 */
public enum SuiteDependencyLevel
{
	/** Required. */
	REQUIRED,
	
	/** Optional. */
	OPTIONAL,
	
	/** End. */
	;
	
	/**
	 * Is this an optional dependency level?
	 *
	 * @return {@code true} if this is an optional dependency level.
	 * @since 2017/11/22
	 */
	public boolean isOptional()
	{
		return this == SuiteDependencyLevel.OPTIONAL;
	}
	
	/**
	 * Is this an required dependency level?
	 *
	 * @return {@code true} if this is an required dependency level.
	 * @since 2017/11/22
	 */
	public boolean isRequired()
	{
		return this == SuiteDependencyLevel.REQUIRED;
	}
	
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
			case REQUIRED:	return "required";
			case OPTIONAL:	return "optional";
			default:
				throw new todo.OOPS();
		}
	}
	
	/**
	 * Returns the dependency level based on the input string.
	 *
	 * @param __s The input string to parse.
	 * @return The dependency level for the given string.
	 * @throws IllegalArgumentException If the level is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/02/22
	 */
	public static SuiteDependencyLevel of(String __s)
		throws IllegalArgumentException, NullPointerException
	{
		// Check
		if (__s == null)
			throw new NullPointerException("NARG");
		
		// Depends
		switch (__s.trim())
		{
			case "required":	return SuiteDependencyLevel.REQUIRED;
			case "optional":	return SuiteDependencyLevel.OPTIONAL;
			
				// {@squirreljme.error DG82 Invalid dependency level. (Level)}
			default:
				throw new IllegalArgumentException(String.format(
					"DG82 %s", __s));
		}
	}
}

