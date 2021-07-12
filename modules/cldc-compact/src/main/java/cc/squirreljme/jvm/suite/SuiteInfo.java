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

import cc.squirreljme.jvm.manifest.JavaManifest;
import cc.squirreljme.jvm.manifest.JavaManifestAttributes;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * This contains all of the information which is provided by a suite.
 *
 * @since 2017/11/30
 */
public final class SuiteInfo
{
	/** The manifest for this suite. */
	protected final JavaManifest manifest;
	
	/** The type of suite this is. */
	protected final SuiteType type;
	
	/** The suite name. */
	protected final SuiteName name;
	
	/** The suite vendor. */
	protected final SuiteVendor vendor;
	
	/** The suite version. */
	protected final SuiteVersion version;
	
	/** Required dependency information. */
	private Reference<DependencyInfo> _dependencies;
	
	/** Provided dependency information. */
	private Reference<ProvidedInfo> _provided;
	
	/** Suite cache. */
	private Reference<SuiteIdentifier> _suite;
	
	/**
	 * Initializes the suite information.
	 *
	 * @param __man The manifest making up the suite information.
	 * @throws InvalidSuiteException If the suite information is not correct.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/04
	 */
	public SuiteInfo(JavaManifest __man)
		throws InvalidSuiteException, NullPointerException
	{
		if (__man == null)
			throw new NullPointerException("NARG");
		
		this.manifest = __man;
		JavaManifestAttributes attr = __man.getMainAttributes();
		
		// First determine the type
		SuiteType type = SuiteType.ofManifest(__man);
		this.type = type;
		
		// {@squirreljme.error DG0b No suite name was specified.}
		SuiteName name = new SuiteName(
			Objects.<String>requireNonNull(attr.getValue(type.nameKey()),
			"AR0b"));
		this.name = name;
		
		// {@squirreljme.error DG0c No suite vendor was specified.}
		SuiteVendor vendor = new SuiteVendor(
			Objects.<String>requireNonNull(attr.getValue(type.vendorKey()),
			"AR0c"));
		this.vendor = vendor;
		
		// {@squirreljme.error DG0d No suite version was specified.}
		SuiteVersion version = new SuiteVersion(
			Objects.<String>requireNonNull(attr.getValue(type.versionKey()),
			"AR0d"));
		this.version = version;
	}
	
	/**
	 * Return the dependencies which are required by this suite.
	 *
	 * @return The dependencies required by this suite.
	 * @since 2017/12/04
	 */
	public final DependencyInfo dependencies()
	{
		Reference<DependencyInfo> ref = this._dependencies;
		DependencyInfo rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._dependencies = new WeakReference<>(
				(rv = DependencyInfo.of(this)));
		
		return rv;
	}
	
	/**
	 * Returns the manifest used for this suite.
	 *
	 * @return The manifest suite.
	 * @since 2017/12/05
	 */
	public final JavaManifest manifest()
	{
		return this.manifest;
	}
	
	/**
	 * Returns the suite name.
	 *
	 * @return The name.
	 * @since 2017/12/31
	 */
	public final SuiteName name()
	{
		return this.name;
	}
	
	/**
	 * Returns the dependencies provided by this suite.
	 *
	 * @return The provided dependencies for this suite.
	 * @since 2017/12/04
	 */
	public final ProvidedInfo provided()
	{
		Reference<ProvidedInfo> ref = this._provided;
		ProvidedInfo rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._provided = new WeakReference<>(
				(rv = ProvidedInfo.of(this)));
		
		return rv;
	}
	
	/**
	 * Returns the suite for this.
	 *
	 * @return The suite.
	 * @since 2017/12/05
	 */
	public final SuiteIdentifier suite()
	{
		Reference<SuiteIdentifier> ref = this._suite;
		SuiteIdentifier rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._suite = new WeakReference<>((rv = new SuiteIdentifier(
				this.name, this.vendor, this.version)));
		
		return rv;
	}
	
	/**
	 * Returns the type of suite this is.
	 *
	 * @return The type of suite.
	 * @since 2017/12/04
	 */
	public final SuiteType type()
	{
		return this.type;
	}
	
	/**
	 * Returns the suite vendor.
	 *
	 * @return The vendor.
	 * @since 2017/12/31
	 */
	public final SuiteVendor vendor()
	{
		return this.vendor;
	}
	
	/**
	 * Returns the suite version.
	 *
	 * @return The version.
	 * @since 2017/12/31
	 */
	public final SuiteVersion version()
	{
		return this.version;
	}
}

