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

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * This represents a suite version.
 *
 * @since 2016/10/12
 */
public final class SuiteVersion
	implements Comparable<SuiteVersion>
{
	/** The minimum version number. */
	public static final SuiteVersion MIN_VERSION =
		new SuiteVersion(0, 0, 0);
	
	/** The maximum version number. */
	public static final SuiteVersion MAX_VERSION =
		new SuiteVersion(99, 99, 99);
	
	/** The major version. */
	protected final int major;
	
	/** The minor version. */
	protected final int minor;
	
	/** The release version. */
	protected final int release;
	
	/** The string representation. */
	private Reference<String> _string;
	
	/**
	 * Initializes the version.
	 *
	 * @param __v The value to parse.
	 * @throws InvalidSuiteException If there are too many or too little
	 * version fields, they contain illegal charactes, or have an out of range
	 * value.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/10/12
	 */
	public SuiteVersion(String __v)
		throws InvalidSuiteException, NullPointerException
	{
		this(SuiteVersion.__decodeVersion(__v));
	}
	
	/**
	 * Initializes a Midlet version number from the specified array of
	 * integer values.
	 *
	 * @param __v The version triplet, up to the first three elements are
	 * used by the version number.
	 * @throws InvalidSuiteException If the version number has an out of
	 * range value.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/10/12
	 */
	public SuiteVersion(int[] __v)
		throws InvalidSuiteException, NullPointerException
	{
		this((__v.length > 0 ? __v[0] : 0),
			(__v.length > 1 ? __v[1] : 0),
			(__v.length > 2 ? __v[2] : 0));
	}
	
	/**
	 * Decodes the midlet version, optionally allowing it to a reverse
	 * operation of the {@link #hashCode()} method.
	 *
	 * @param __hash If {@code true} then the value to decode is treated as
	 * the hash code returned by this class.
	 * @param __maj If {@code __hash} is {@code true} then this is the hash
	 * code of a SuiteVersion, otherwise it is the major version number.
	 * @throws InvalidSuiteException If the version number has an out of
	 * range value.
	 * @since 2016/10/13
	 */
	public SuiteVersion(boolean __hash, int __maj)
		throws InvalidSuiteException
	{
		this((__hash ? __maj / 10000 : __maj),
			(__hash ? (__maj / 100) % 100 : 0),
			(__hash ? __maj % 100 : 0));
	}
	
	/**
	 * Initializes the version.
	 *
	 * @param __maj The major version.
	 * @throws IllegalArgumentException If any value is out of range.
	 * @since 2016/10/12
	 */
	public SuiteVersion(int __maj)
	{
		this(__maj, 0, 0);
	}
	
	/**
	 * Initializes the version.
	 *
	 * @param __maj The major version.
	 * @param __min The minor version.
	 * @throws IllegalArgumentException If any value is out of range.
	 * @since 2016/10/12
	 */
	public SuiteVersion(int __maj, int __min)
	{
		this(__maj, __min, 0);
	}
	
	/**
	 * Initializes the version.
	 *
	 * @param __maj The major version.
	 * @param __min The minor version.
	 * @param __rel The release version.
	 * @throws InvalidSuiteException If any value is out of range.
	 * @since 2016/10/12
	 */
	public SuiteVersion(int __maj, int __min, int __rel)
		throws InvalidSuiteException
	{
		// {@squirreljme.error DG0i Input version number is out of range, only
		// 0 through 99 are valid. (The major version; The minor version; The
		// release version)}
		if (__maj < 0 || __maj > 99 || __min < 0 || __min > 99 ||
			__rel < 0 || __rel > 99)
			throw new InvalidSuiteException(String.format("AR0i %d %d %d",
				__maj, __min, __rel));
		
		// Set
		this.major = __maj;
		this.minor = __min;
		this.release = __rel;
	}
	
	/**
	 * Checks if this version at least the specified verison.
	 *
	 * @param __v The version to check against.
	 * @return {@code true} if this version is at least the other.
	 * @throws NullPointerException On nul arguments.
	 */
	public boolean atLeast(SuiteVersion __v)
		throws NullPointerException
	{
		// Check
		if (__v == null)
			throw new NullPointerException("NARG");
		
		// Can compare the hashcodes
		return this.hashCode() >= __v.hashCode();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/10/12
	 */
	@Override
	public int compareTo(SuiteVersion __o)
	{
		// Major first
		int amaj = this.major, bmaj = __o.major;
		int rv = amaj - bmaj;
		if (rv != 0)
			return rv;
		
		// Then minor
		int amin = this.minor, bmin = __o.minor;
		rv = amin - bmin;
		if (rv != 0)
			return rv;
		
		// Then release
		int arel = this.release, brel = __o.release;
		rv = arel - brel;
		if (rv != 0)
			return rv;
		
		// The same
		return 0;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/10/12
	 */
	@Override
	public boolean equals(Object __o)
	{
		// Check
		if (!(__o instanceof SuiteVersion))
			return false;
		
		// Cast
		SuiteVersion o = (SuiteVersion)__o;
		return this.major == o.major &&
			this.minor == o.minor &&
			this.release == o.release;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/10/12
	 */
	@Override
	public int hashCode()
	{
		return (this.major * 10000) +
			(this.minor * 100) +
			this.release;
	}
	
	/**
	 * Returns the major version.
	 *
	 * @return The major version.
	 * @since 2017/02/22
	 */
	public int major()
	{
		return this.major;
	}
	
	/**
	 * Returns the minor version.
	 *
	 * @return The minor version.
	 * @since 2017/02/22
	 */
	public int minor()
	{
		return this.minor;
	}
	
	/**
	 * Returns the release version.
	 *
	 * @return The release version.
	 * @since 2017/02/22
	 */
	public int release()
	{
		return this.release;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/10/12
	 */
	@Override
	public String toString()
	{
		// Get
		Reference<String> ref = this._string;
		String rv;
		
		// Cache?
		if (ref == null || null == (rv = ref.get()))
			this._string = new WeakReference<>((rv = this.major + "." +
				this.minor + "." + this.release));
		
		// Return it
		return rv;
	}
	
	/**
	 * Decodes the string based version number
	 *
	 * @param __v The input string.
	 * @return The version tuplet.
	 * @throws InvalidSuiteException If the input is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/10/12
	 */
	private static final int[] __decodeVersion(String __v)
		throws InvalidSuiteException, NullPointerException
	{
		// Check
		if (__v == null)
			throw new NullPointerException("NARG");
		
		// Trim whitespace
		__v = __v.trim();
		
		// Output array
		int[] rv = new int[3];
		
		// Parse the input value
		int n = __v.length(), at = 0;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i <= n; i++)
		{
			int c = (i == n ? -1 : __v.charAt(i));
			
			// Decimal point? or end
			if (c == '.' || c == -1)
			{
				rv[at++] = Integer.parseInt(sb.toString(), 10);
				
				// {@squirreljme.error DG0j Too many version fields in the
				// specified string. (The input string)}
				if (c != -1 && at >= 4)
					throw new InvalidSuiteException(String.format("AR0j %s",
						__v));
				
				// Clear
				sb.setLength(0);
			}
			
			// Add to string
			else if (c >= '0' && c <= '9')
				sb.append((char)c);
			
			// {@squirreljme.error DG0k An illegal character is in the
			// version string. (The input string; The illegal character)}
			else
				throw new InvalidSuiteException(String.format("AR0k %s %04x",
					__v, c));
		}
		
		// Return it
		return rv;
	}
}

