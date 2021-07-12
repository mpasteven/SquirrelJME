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

/**
 * This represents an identifier which acts as a name of a fragment of a class
 * or a member of a class.
 *
 * @since 2017/06/12
 */
public abstract class Identifier
{
	/** The string which makes up the identifier. */
	protected final String string;
	
	/**
	 * Initializes the identifier.
	 *
	 * @param __n The input identifier to decode.
	 * @throws InvalidClassFormatException If it is not a valid identifier.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/06/12
	 */
	Identifier(String __n)
		throws InvalidClassFormatException, NullPointerException
	{
		// Check
		if (__n == null)
			throw new NullPointerException("NARG");
		
		// Set
		this.string = __n;
		
		// Check characters
		for (int i = 0, n = __n.length(); i < n; i++)
		{
			char c = __n.charAt(i);
			
			// {@squirreljme.error JC2x The specified identifier contains an
			// invalid character. (The identifier)}
			if (c == '.' || c == ';' || c == '[' || c == '/')
				throw new InvalidClassFormatException(
					String.format("JC2x %s", __n));
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/06/12
	 */
	@Override
	public boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		// Check
		if (!(__o instanceof Identifier))
			return false;
		
		return this.string.equals(((Identifier)__o).string);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/06/12
	 */
	@Override
	public int hashCode()
	{
		return this.string.hashCode();
	}
	
	/**
	 * Returns the identifier.
	 *
	 * @return The identifier.
	 * @since 2017/10/02
	 */
	public final String identifier()
	{
		return this.string;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/06/12
	 */
	@Override
	public String toString()
	{
		return this.string;
	}
}

