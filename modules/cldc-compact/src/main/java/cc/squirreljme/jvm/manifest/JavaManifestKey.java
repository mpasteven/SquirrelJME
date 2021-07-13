// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.manifest;

/**
 * This represents a key which is used in a manifest, it is case insensitive
 * when it comes to ASCII values.
 *
 * @since 2016/05/29
 */
public final class JavaManifestKey
{
	/** The used string. */
	protected final String string;
	
	/** The actual input string. */
	protected final String inputString;
	
	/**
	 * Initializes the manifest key using the given string.
	 *
	 * @param __s The string to use for the manifest key.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/05/29
	 */
	public JavaManifestKey(String __s)
		throws NullPointerException
	{
		// Check
		if (__s == null)
			throw new NullPointerException("NARG");
		
		// Lower-case all letters
		StringBuilder sb = new StringBuilder();
		for (int i = 0, n = __s.length(); i < n; i++)
			sb.append(JavaManifestKey.__toLower(__s.charAt(i)));
		this.string = sb.toString();
		
		// Remember input string for case purposes
		this.inputString = __s;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/05/29
	 */
	@Override
	public boolean equals(Object __o)
	{
		// Is another key?
		if (__o instanceof JavaManifestKey)
			return this.__equals(((JavaManifestKey)__o).string);
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/05/29
	 */
	@Override
	public int hashCode()
	{
		return this.string.hashCode();
	}
	
	/**
	 * Returns the input string which was passed to this key, this string
	 * cannot be used for comparative purposes and it intended to be used
	 * for case matching in the output manifest.
	 *
	 * @return The input string.
	 * @since 2017/11/26
	 */
	public String inputString()
	{
		return this.inputString;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/05/29
	 */
	@Override
	public String toString()
	{
		return this.string;
	}
	
	/**
	 * Compares two strings checking for case insensitivity in the basic
	 * ASCII range.
	 *
	 * @param __b The other string to compare against.
	 * @return {@code true} if the strings are equal.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/05/29
	 */
	private boolean __equals(String __b)
		throws NullPointerException
	{
		// Check
		if (__b == null)
			throw new NullPointerException("NARG");
		
		// Cache
		String a = this.string;
		
		// Get their lengths
		int na = a.length(),
			nb = __b.length();
		
		// Would not be equal
		if (na != nb)
			return false;
		
		// Check characters
		for (int i = 0; i < na; i++)
			if (JavaManifestKey.__toLower(a.charAt(i)) != JavaManifestKey
				.__toLower(__b.charAt(i)))
				return false;
		
		// Matches
		return true;
	}
	
	/**
	 * Converts the specified character to lower case.
	 *
	 * @param __c The character to lower case.
	 * @return The lowercased character or {@code __c} if it cannot be
	 * lowercased.
	 * @since 2016/05/29
	 */
	private static char __toLower(char __c)
	{
		if (__c >= 'A' && __c <= 'Z')
			return (char)('a' + (__c - 'A'));
		return __c;
	}
}

