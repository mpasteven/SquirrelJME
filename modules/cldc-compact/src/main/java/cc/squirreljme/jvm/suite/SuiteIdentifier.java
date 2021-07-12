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
 * This is the identity for a midlet suite which contains a name, vendor,
 * and version.
 *
 * @since 2016/10/12
 */
public final class SuiteIdentifier
	implements Comparable<SuiteIdentifier>
{
	/** The suite name. */
	protected final SuiteName name;
	
	/** The suite vendor. */
	protected final SuiteVendor vendor;
	
	/** The suite version. */
	protected final SuiteVersion version;
	
	/** String representation. */
	private Reference<String> _string;
	
	/**
	 * Initializes the suite identifier.
	 *
	 * @param __name The name of the suite.
	 * @param __ven The vendor of the suite.
	 * @param __ver The version of the suite.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/02/22
	 */
	public SuiteIdentifier(SuiteName __name, SuiteVendor __ven,
		SuiteVersion __ver)
		throws NullPointerException
	{
		this(__ven, __name, __ver);
	}
	
	/**
	 * Initializes the suite identifier.
	 *
	 * @param __ven The vendor of the suite.
	 * @param __name The name of the suite.
	 * @param __ver The version of the suite.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/10/12
	 */
	public SuiteIdentifier(SuiteVendor __ven, SuiteName __name,
		SuiteVersion __ver)
		throws NullPointerException
	{
		// Check
		if (__ven == null || __name == null || __ver == null)
			throw new NullPointerException("NARG");
		
		// Set
		this.name = __name;
		this.vendor = __ven;
		this.version = __ver;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/10/12
	 */
	@Override
	public int compareTo(SuiteIdentifier __o)
	{
		if (this == __o)
			return 0;
		
		// Compare name
		int rv = this.name.compareTo(__o.name);
		if (rv != 0)
			return rv;
		
		// Then vendor
		rv = this.vendor.compareTo(__o.vendor);
		if (rv != 0)
			return rv;
		
		// Then the version last
		return this.version.compareTo(__o.version);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/10/12
	 */
	@Override
	public boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		// Check
		if (!(__o instanceof SuiteIdentifier))
			return false;
		
		return 0 == (this.compareTo((SuiteIdentifier)__o));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/10/12
	 */
	@Override
	public int hashCode()
	{
		return this.name.hashCode() ^ this.vendor.hashCode() ^
			this.version.hashCode();
	}
	
	/**
	 * Returns the suite name.
	 *
	 * @return The suite name.
	 * @since 2016/10/12
	 */
	public SuiteName name()
	{
		return this.name;
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
			this._string = new WeakReference<>((rv = this.vendor + ";" +
				this.name + ";" + this.version));
		
		// Return it
		return rv;
	}
	
	/**
	 * Returns the suite vendor.
	 *
	 * @return The suite vendor.
	 * @since 2016/10/12
	 */
	public SuiteVendor vendor()
	{
		return this.vendor;
	}
	
	/**
	 * Returns the suite version.
	 *
	 * @return The suite version.
	 * @since 2016/10/12
	 */
	public SuiteVersion version()
	{
		return this.version;
	}
}

