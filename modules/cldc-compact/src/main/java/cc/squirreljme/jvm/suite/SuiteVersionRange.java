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
 * This is used to handle version ranges that may be used for dependencies.
 *
 * Version ranges are inclusive.
 *
 * @since 2017/02/22
 */
public final class SuiteVersionRange
	implements Comparable<SuiteVersionRange>
{
	/** Any version. */
	public static final SuiteVersionRange ANY_VERSION =
		new SuiteVersionRange(SuiteVersion.MIN_VERSION,
			SuiteVersion.MAX_VERSION);
	
	/** The starting range, inclusive. */
	protected final SuiteVersion from;
	
	/** Tne ending range, inclusive. */
	protected final SuiteVersion to;
	
	/** String representation. */
	private Reference<String> _string;
	
	/**
	 * Initializes the range inclusively between the two given versions.
	 *
	 * @param __from The source version.
	 * @param __to The destination version.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/02/22
	 */
	public SuiteVersionRange(SuiteVersion __from, SuiteVersion __to)
		throws NullPointerException
	{
		// Check
		if (__from == null || __to == null)
			throw new NullPointerException("NARG");
		
		// Make sure from is always first
		if (__from.compareTo(__to) <= 0)
		{
			this.from = __from;
			this.to = __to;
		}
		
		// Swapped
		else
		{
			this.from = __to;
			this.to = __from;
		}
	}
	
	/**
	 * Parses the version range that is specified in the dependency of JAR
	 * files.
	 *
	 * @param __s The string to parse.
	 * @throws InvalidSuiteException If the range is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/02/22
	 */
	public SuiteVersionRange(String __s)
		throws InvalidSuiteException, NullPointerException
	{
		// Check
		if (__s == null)
			throw new NullPointerException("NARG");
		
		// Trim
		__s = __s.trim();
		
		// {@squirreljme.error DG0l The version range cannot be blank.}
		int sl = __s.length();
		if (sl <= 0)
			throw new IllegalArgumentException("AR0l");
		
		// Get the last character
		char lc = __s.charAt(__s.length() - 1);
		
		// All versions following this.
		if (lc == '+')
		{
			this.from = new SuiteVersion(__s.substring(0, sl - 1));
			this.to = new SuiteVersion(99, 99, 99);
		}
		
		// All versions in the group
		else if (lc == '*')
		{
			// Get the last dot, if any
			int ld = __s.lastIndexOf('.');
			if (ld < 0)
			{
				// Any version, does not matter
				if (sl == 1)
				{
					this.from = new SuiteVersion(0);
					this.to = new SuiteVersion(99, 99, 99);
				}
				
				// {@squirreljme.error DG0m Major only wildcard versions must
				// be a single asterisk. (The input string)}
				else
					throw new InvalidSuiteException(String.format("AR0m %s",
						__s));
			}
			
			// Parse otherwise, just count the number of dots to determine
			// how deep it goes
			else
			{
				// {@squirreljme.error DG0n The last dot in a wildcard must be
				// before the asterisk. (The input string)}
				if (ld != sl - 1)
					throw new InvalidSuiteException(String.format("AR0n %s",
						__s));
				
				// Source range is simple
				SuiteVersion ver = new SuiteVersion(
					__s.substring(0, sl - 2));
				this.from = ver;
				
				// Count dots, determines major/minor
				int numdots = 0;
				for (int i = 0; i < sl; i++)
					if (__s.charAt(i) == '.')
						numdots++;
				
				// minor and release wildcard
				if (numdots == 1)
					this.to = new SuiteVersion(ver.major(), 99, 99);
				
				// release ranged wildcard
				else if (numdots == 2)
					this.to = new SuiteVersion(ver.major(), ver.minor(), 99);
				
				// {@squirreljme.error DG0o There are too many decimal points
				// in the wildcard version string. (The input string)}
				else
					throw new InvalidSuiteException(String.format("AR0o %s",
						__s));
			}
		}
		
		// Only this version
		else
		{
			SuiteVersion ver = new SuiteVersion(__s);
			this.from = ver;
			this.to = ver;
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/11/30
	 */
	@Override
	public int compareTo(SuiteVersionRange __o)
	{
		// From version is always first
		int rv = this.from.compareTo(__o.from);
		if (rv != 0)
			return rv;
		
		return this.to.compareTo(__o.to);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/02/22
	 */
	@Override
	public boolean equals(Object __o)
	{
		// Check
		if (!(__o instanceof SuiteVersionRange))
			return false;
		
		// Compare
		SuiteVersionRange o = (SuiteVersionRange)__o;
		return this.from.equals(o.from) && this.to.equals(o.to);
	}
	
	/**
	 * Returns the start of the range
	 *
	 * @return The range start.
	 * @since 2017/02/22
	 */
	public SuiteVersion from()
	{
		return this.from;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/02/22
	 */
	@Override
	public int hashCode()
	{
		return this.to.hashCode() ^ (~this.from.hashCode());
	}
	
	/**
	 * Checks whether the specified version is in range.
	 *
	 * @param __v The version to check.
	 * @return {@code true} if it is in the range.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/02/22
	 */
	public boolean inRange(SuiteVersion __v)
		throws NullPointerException
	{
		// Check
		if (__v == null)
			throw new NullPointerException("NARG");
		
		return __v.compareTo(this.from) >= 0 &&
			__v.compareTo(this.to) <= 0;
	}
	
	/**
	 * Checks whether the given version is within range of the other version.
	 *
	 * @param __r The other version range to check.
	 * @return If the other version range shares all or part of its range
	 * with this range.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/27
	 */
	public boolean inRange(SuiteVersionRange __r)
		throws NullPointerException
	{
		if (__r == null)
			throw new NullPointerException("NARG");
		
		throw new todo.TODO();
	}
	
	/**
	 * Returns the end of the range.
	 *
	 * @return The range end.
	 * @since 2017/02/22
	 */
	public SuiteVersion to()
	{
		return this.to;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/02/22
	 */
	@Override
	public String toString()
	{
		// Get
		Reference<String> ref = this._string;
		String rv;
		
		// Cache?
		if (ref == null || null == (rv = ref.get()))
		{
			// Slowly build version
			StringBuilder sb = new StringBuilder();
			SuiteVersion from = this.from;
			SuiteVersion to = this.to;
			
			// Get all values
			int amaj = from.major(),
				amin = from.minor(),
				arel = from.release(),
				bmaj = to.major(),
				bmin = to.minor(),
				brel = to.release();
			
			// Pure wildcard
			if (amaj == 0 && amin == 0 && arel == 0 &&
				bmaj == 99 && bmin == 99 && brel == 99)
				sb.append('*');
			
			// Exact, subwildcard, or any following
			else
			{
				// Add major version
				sb.append(amaj);
				sb.append('.');
				
				// Wild card minor and release
				if (amin == 0 && arel == 0 && bmin == 99 && brel == 99)
					sb.append('*');
				
				// Not wild
				else
				{
					// Add version
					sb.append(amin);
					sb.append('.');
					
					// Wild card release
					if (arel == 0 && brel == 99)
						sb.append('*');
					
					// Would be exact (or plus)
					else
						sb.append(arel);
				}
			
				// Will be all versions following
				if (bmaj == 99 && bmin == 99 && brel == 99)
					sb.append('+');
			}
			
			// Store
			this._string = new WeakReference<>((rv = sb.toString()));
		}
		
		return rv;
	}
	
	/**
	 * Returns a version which at most implements the given version.
	 *
	 * @param __v The version.
	 * @return The resulting version range.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/26
	 */
	public static final SuiteVersionRange atMost(SuiteVersion __v)
		throws NullPointerException
	{
		if (__v == null)
			throw new NullPointerException("NARG");
		
		return new SuiteVersionRange(SuiteVersion.MIN_VERSION, __v);
	}
	
	/**
	 * Returns a version which exactly implements the given version.
	 *
	 * @param __v The version.
	 * @return The resulting version range.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/26
	 */
	public static final SuiteVersionRange exactly(SuiteVersion __v)
		throws NullPointerException
	{
		if (__v == null)
			throw new NullPointerException("NARG");
		
		return new SuiteVersionRange(__v, __v);
	}
}

