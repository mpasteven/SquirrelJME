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

import cc.squirreljme.jvm.manifest.JavaManifestAttributes;
import cc.squirreljme.runtime.cldc.util.StringUtils;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This contains the information which specifies all of the dependencies which
 * and application depends on.
 *
 * @since 2017/11/30
 */
public final class DependencyInfo
{
	/** The dependencies. */
	private final Set<MarkedDependency> _depends;
	
	/** String representation. */
	private Reference<String> _string;
	
	/**
	 * Initializes the dependency information.
	 *
	 * @param __deps The dependencies to depend on.
	 * @since 2017/12/31
	 */
	public DependencyInfo(MarkedDependency... __deps)
	{
		this(Arrays.<MarkedDependency>asList((__deps == null ?
			new MarkedDependency[0] : __deps)));
	}
	
	/**
	 * Initializes the dependency information.
	 *
	 * @param __deps The dependencies to use.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/31
	 */
	public DependencyInfo(Collection<MarkedDependency> __deps)
		throws NullPointerException
	{
		if (__deps == null)
			throw new NullPointerException("NARG");
		
		Set<MarkedDependency> depends = new LinkedHashSet<>();
		for (MarkedDependency d : __deps)
			if (d == null)
				throw new NullPointerException("NARG");
			else
				depends.add(d);
		this._depends = depends;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/31
	 */
	@Override
	public final boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (!(__o instanceof DependencyInfo))
			return false;
		
		return this._depends.equals(((DependencyInfo)__o)._depends);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/31
	 */
	@Override
	public final int hashCode()
	{
		return this._depends.hashCode();
	}
	
	/**
	 * Checks if the dependency information is empty.
	 *
	 * @return If the dependency info is empty.
	 * @since 2017/11/30
	 */
	public final boolean isEmpty()
	{
		return this._depends.isEmpty();
	}
	
	/**
	 * Matches this dependency information to see if any of the provided
	 * fields would statisfy the dependencies that are needed.
	 *
	 * @param __prov The provided information.
	 * @return The result of the match.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/30
	 */
	public final MatchResult match(ProvidedInfo __prov)
		throws NullPointerException
	{
		if (__prov == null)
			throw new NullPointerException("NARG");
		
		// Remove matching dependencies from the input while keeping the
		// matching ones
		Set<MarkedDependency> depends = new LinkedHashSet<>(this._depends),
			matched = new LinkedHashSet<>();
		for (MarkedProvided p : __prov)
		{
			for (Iterator<MarkedDependency> it = depends.iterator();
				it.hasNext();)
			{
				MarkedDependency d = it.next();
				
				if (d.matchesProvided(p))
				{
					matched.add(d);
					it.remove();
				}
			}
		}
		
		return new MatchResult(new DependencyInfo(matched),
			new DependencyInfo(depends));
	}
	
	/**
	 * Returns dependency information which contains no optional
	 * dependencies.
	 *
	 * @return Dependency information with no optionals.
	 * @since 2017/11/30
	 */
	public final DependencyInfo noOptionals()
	{
		// Ignore if there are no dependencies
		Set<MarkedDependency> depends = this._depends;
		if (depends.isEmpty())
			return this;
		
		// Include only required dependencies
		Set<MarkedDependency> instead = new LinkedHashSet<>();
		for (MarkedDependency md : depends)
			if (!md.isOptional())
				instead.add(md);
		
		// There were no optional dependencies
		if (depends.size() == instead.size())
			return this;
		return new DependencyInfo(instead);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/31
	 */
	@Override
	public final String toString()
	{
		Reference<String> ref = this._string;
		String rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._string = new WeakReference<>((rv = "Dependencies:" +
				this._depends));
		
		return rv;
	}
	
	/**
	 * Parses the given suite information and returns the dependency
	 * information.
	 *
	 * @param __info The suite information to parse.
	 * @return The parsed dependency information.
	 * @throws InvalidSuiteException If the dependencies is not correct.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/20
	 */
	public static final DependencyInfo of(SuiteInfo __info)
		throws InvalidSuiteException, NullPointerException
	{
		if (__info == null)
			throw new NullPointerException("NARG");
		
		Set<MarkedDependency> depends = new LinkedHashSet<>();
		JavaManifestAttributes attr = __info.manifest().getMainAttributes();
		String value;
		
		// The CLDC library to use
		value = attr.getValue("microedition-configuration");
		if (value != null)
		{
			value = value.trim();
			
			// Old software may rely on using 1.0 values when that is illegal
			if (value.equals("1.0"))
				depends.add(new Configuration("CLDC-1.0"));
			
			// Decode otherwise
			else
				depends.add(new Configuration(value.trim()));
		}
		
		// Profiles needed to run
		value = attr.getValue("microedition-profile");
		if (value != null)
			for (String s : StringUtils.basicSplit(" \t", value))
			{
				// Old software may rely on using the version directly
				if (s.equals("2.0"))
					depends.add(new Profile("MIDP-2.0"));
				
				// Decode otherwise
				else
					depends.add(new Profile(s));
			}
		
		// Parse entries in sequential order
		SuiteType type = __info.type();
		for (int i = 1; i >= 1; i++)
		{
			// Stop if no more values are read
			value = attr.getValue(type.dependencyKey(i));
			if (value == null)
				break;
			
			// Decode dependency
			depends.add(new SuiteDependency(value));
		}
		
		// Build
		return new DependencyInfo(depends);
	}
}

