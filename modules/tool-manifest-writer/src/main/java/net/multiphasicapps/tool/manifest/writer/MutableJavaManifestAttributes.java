// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.tool.manifest.writer;

import cc.squirreljme.jvm.manifest.JavaManifestAttributes;
import cc.squirreljme.jvm.manifest.JavaManifestKey;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * This is a mutable set of attributes which exist within a manifest.
 *
 * @since 2016/09/19
 */
public class MutableJavaManifestAttributes
	extends AbstractMap<JavaManifestKey, String>
{
	/** The manifest values. */
	protected final Map<JavaManifestKey, String> values =
		new LinkedHashMap<>();
	
	/**
	 * Initializes empty manifest attributes.
	 *
	 * @since 2017/11/19
	 */
	public MutableJavaManifestAttributes()
	{
	}
	
	/**
	 * Initializes the attributes with a copy of the other attributes.
	 *
	 * @param __a The attributes to copy from.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/19
	 */
	public MutableJavaManifestAttributes(JavaManifestAttributes __a)
		throws NullPointerException
	{
		if (__a == null)
			throw new NullPointerException("NARG");
		
		this.values.putAll(__a);
	}
	
	/**
	 * Checks whether the specified key has a value defined for it.
	 *
	 * @param __k The key to check.
	 * @return {@code true} if a value is defined.
	 * @since 2017/12/04
	 */
	public boolean definesValue(JavaManifestKey __k)
	{
		return this.containsKey(__k);
	}
	
	/**
	 * Checks whether the specified key has a value defined for it.
	 *
	 * @param __k The key to check.
	 * @return {@code true} if a value is defined.
	 * @since 2017/12/04
	 */
	public boolean definesValue(String __k)
	{
		return this.containsKey(__k == null ? null : new JavaManifestKey(__k));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/09/19
	 */
	@Override
	public final Set<Map.Entry<JavaManifestKey, String>> entrySet()
	{
		return this.values.entrySet();
	}
	
	/**
	 * Returns the value used by the given key.
	 *
	 * @param __k The key to get the value for.
	 * @return The value for the given key or {@code null} if not found.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/04
	 */
	public String getValue(JavaManifestKey __k)
	{
		if (__k == null)
			throw new NullPointerException("NARG");
		
		return this.get(__k);
	}
	
	/**
	 * Returns the value used by the given key.
	 *
	 * @param __s The key to get the value for.
	 * @return The value for the given key or {@code null} if not found.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/10/21
	 */
	public String getValue(String __s)
		throws NullPointerException
	{
		// Check
		if (__s == null)
			throw new NullPointerException("NARG");
		
		// Find it
		return this.get(new JavaManifestKey(__s));
	}
	
	/**
	 * Returns the value in the attributes or the specified value if it is not
	 * set.
	 *
	 * @param __k The key to get.
	 * @param __dv The default value to use.
	 * @return The value for the given key or {@code __dv}.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/03/18
	 */
	public String getValue(JavaManifestKey __k, String __dv)
		throws NullPointerException
	{
		if (__k == null)
			throw new NullPointerException("NARG");
		
		String rv = this.getValue(__k);
		if (rv == null)
			return __dv;
		return rv;
	}
	
	/**
	 * Returns the value in the attributes or the specified value if it is not
	 * set.
	 *
	 * @param __k The key to get.
	 * @param __dv The default value to use.
	 * @return The value for the given key or {@code __dv}.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/03/18
	 */
	public String getValue(String __k, String __dv)
		throws NullPointerException
	{
		if (__k == null)
			throw new NullPointerException("NARG");
		
		String rv = this.getValue(__k);
		if (rv == null)
			return __dv;
		return rv;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/09/19
	 */
	@Override
	public String put(JavaManifestKey __k, String __v)
		throws NullPointerException
	{
		// Check
		if (__k == null || __v == null)
			throw new NullPointerException("NARG");
		
		// Forward
		return this.values.put(__k, __v);
	}
	
	/**
	 * Places the specified key with the given value into the manifest.
	 *
	 * @param __k The key to use.
	 * @param __v The value for that key.
	 * @return The old value.
	 * @since 2016/12/27
	 */
	public String putValue(String __k, String __v)
	{
		// Check
		if (__k == null || __v == null)
			throw new NullPointerException("NARG");
		
		// Forward
		return this.put(new JavaManifestKey(__k), __v);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/09/29
	 */
	@Override
	public String remove(Object __k)
	{
		return this.values.remove(__k);
	}
}

