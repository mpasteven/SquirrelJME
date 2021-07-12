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

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * This contains the attributes for a single section within the manifest file.
 *
 * This class is immutable.
 *
 * @since 2016/05/20
 */
public final class JavaManifestAttributes
	extends AbstractMap<JavaManifestKey, String>
{
	/** The key value pairs. */
	private final Map<JavaManifestKey, String> _pairs;
	
	/**
	 * Initializes empty manifest attributes.
	 *
	 * @since 2018/02/10
	 */
	JavaManifestAttributes()
	{
		this._pairs = new HashMap<>();
	}
	
	/**
	 * Initializes the manifest attributes.
	 *
	 * @param __from The map to copy from.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/05/20
	 */
	JavaManifestAttributes(Map<JavaManifestKey, String> __from)
		throws NullPointerException
	{
		// Check
		if (__from == null)
			throw new NullPointerException("NARG");
		
		// Copy
		this._pairs = new LinkedHashMap<>(__from);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/05/20
	 */
	@Override
	public boolean containsKey(Object __o)
	{
		return this._pairs.containsKey(__o);
	}
	
	/**
	 * Checks whether the specified key has a value defined for it.
	 *
	 * @param __k The key to check.
	 * @return {@code true} if a value is defined.
	 * @since 2017/11/26
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
	 * @since 2017/11/26
	 */
	public boolean definesValue(String __k)
	{
		return this.containsKey(__k == null ? null : new JavaManifestKey(__k));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/05/20
	 */
	@Override
	public Set<Map.Entry<JavaManifestKey, String>> entrySet()
	{
		return this._pairs.entrySet();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/05/20
	 */
	@Override
	public String get(Object __o)
	{
		return this._pairs.get(__o);
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
	 * @since 2016/05/20
	 */
	@Override
	public int size()
	{
		return this._pairs.size();
	}
}

