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
import cc.squirreljme.runtime.cldc.util.UnmodifiableIterator;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This contains all of the information for dependencies which are provided
 * by an application or library.
 *
 * @since 2017/11/30
 */
public final class ProvidedInfo
	implements Iterable<MarkedProvided>
{
	/** Provided fields. */
	private final MarkedProvided[] _provided;
	
	/** String representation. */
	private Reference<String> _string;
	
	/**
	 * Initializes the provided dependency info.
	 *
	 * @param __provs The provided set.
	 * @since 2017/12/31
	 */
	public ProvidedInfo(MarkedProvided... __provs)
	{
		// Defensive copy
		__provs = (__provs == null ? new MarkedProvided[0] : __provs.clone());
		
		// Check for nulls
		for (MarkedProvided o : __provs)
			if (o == null)
				throw new NullPointerException("NARG");
		
		this._provided = __provs;
	}
	
	/**
	 * Initializes the provided dependency info.
	 *
	 * @param __provs The provided set.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/31
	 */
	public ProvidedInfo(Collection<MarkedProvided> __provs)
		throws NullPointerException
	{
		this(__provs.<MarkedProvided>toArray(
			new MarkedProvided[__provs.size()]));
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
		
		if (!(__o instanceof ProvidedInfo))
			return false;
		
		return Arrays.equals(this._provided, ((ProvidedInfo)__o)._provided);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/31
	 */
	@Override
	public final int hashCode()
	{
		return this._provided.hashCode();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/01/31
	 */
	@Override
	public Iterator<MarkedProvided> iterator()
	{
		return UnmodifiableIterator.of(this._provided);
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
			this._string = new WeakReference<>(
				(rv = Arrays.asList(this._provided).toString()));
		
		return rv;
	}
	
	/**
	 * Parses the given suite information and returns all of the provided
	 * resolutions for dependencies which are specified in the manifest.
	 *
	 * @param __info The suite information to parse.
	 * @return The parsed provided resolution information.
	 * @throws InvalidSuiteException If the manifest is not correct.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/20
	 */
	public static ProvidedInfo of(SuiteInfo __info)
		throws InvalidSuiteException, NullPointerException
	{
		if (__info == null)
			throw new NullPointerException("NARG");
		
		Set<MarkedProvided> provided = new LinkedHashSet<>();
		JavaManifestAttributes attr = __info.manifest().getMainAttributes();
		String value;
		
		// Defined configurations
		value = attr.getValue("X-SquirrelJME-DefinedConfigurations");
		if (value != null)
			for (String s : StringUtils.basicSplit(" \t", value))
				provided.add(new Configuration(s));
		
		// Defined profiles
		value = attr.getValue("X-SquirrelJME-DefinedProfiles");
		if (value != null)
			for (String s : StringUtils.basicSplit(" \t", value))
				provided.add(new Profile(s));
		
		// Defined standards
		value = attr.getValue("X-SquirrelJME-DefinedStandards");
		if (value != null)
			for (String s : StringUtils.basicSplit(",", value))
				provided.add(new Standard(s.trim()));
		
		// Has internal project name?
		String internalname = attr.getValue(
			"X-SquirrelJME-InternalProjectName");
		if (internalname != null)
			provided.add(new InternalName(internalname));
		
		// This provides a library or otherwise
		provided.add(new TypedSuite(__info.type(), __info.suite()));
		
		// Initialize
		return new ProvidedInfo(provided);
	}
}

