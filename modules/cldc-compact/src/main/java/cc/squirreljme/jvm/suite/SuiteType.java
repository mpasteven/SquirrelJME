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
import cc.squirreljme.jvm.manifest.JavaManifestKey;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * This represents the type of suite that a suite may be.
 *
 * @since 2017/12/04
 */
public enum SuiteType
{
	/** MIDlet. */
	MIDLET("MIDlet"),
	
	/** LIBlet. */
	LIBLET("LIBlet"),
	
	/** An API. */
	SQUIRRELJME_API("X-SquirrelJME-API"),
	
	/** End. */
	;
	
	/** The used prefix. */
	protected final String prefix;
	
	/** Manifest description key. */
	private Reference<JavaManifestKey> _description;
	
	/** Manifest name key. */
	private Reference<JavaManifestKey> _name;
	
	/** Manifest vendor key. */
	private Reference<JavaManifestKey> _vendor;
	
	/** Manifest version key. */
	private Reference<JavaManifestKey> _version;
	
	/**
	 * Initializes the type.
	 *
	 * @param __p The prefix which is used.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/04
	 */
	SuiteType(String __p)
		throws NullPointerException
	{
		if (__p == null)
			throw new NullPointerException("NARG");
		
		this.prefix = __p;
	}
	
	/**
	 * Returns the key which is used to refer to dependencies.
	 *
	 * @param __i The index of the dependency to get.
	 * @return The key for the given dependency index.
	 * @since 2017/12/05
	 */
	public JavaManifestKey dependencyKey(int __i)
	{
		// {@squirreljme.error DG0f Cannot have a zero or negative dependency
		// index.}
		if (__i <= 0)
			throw new IllegalArgumentException("AR0f");
		
		return new JavaManifestKey(this.prefix + "-Dependency-" + __i);
	}
	
	/**
	 * Returns the manifest key used for the description.
	 *
	 * @return The description manifest key.
	 * @since 2017/12/04
	 */
	public JavaManifestKey descriptionKey()
	{
		Reference<JavaManifestKey> ref = this._description;
		JavaManifestKey rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._description = new WeakReference<>(
				(rv = new JavaManifestKey(this.prefix + "-Description")));
		
		return rv;
	}
	
	/**
	 * Returns the manifest key used for the name.
	 *
	 * @return The name manifest key.
	 * @since 2017/12/04
	 */
	public JavaManifestKey nameKey()
	{
		Reference<JavaManifestKey> ref = this._name;
		JavaManifestKey rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._name = new WeakReference<>(
				(rv = new JavaManifestKey(this.prefix + "-Name")));
		
		return rv;
	}
	
	/**
	 * Returns the manifest key used for the vendor.
	 *
	 * @return The vendor manifest key.
	 * @since 2017/12/04
	 */
	public JavaManifestKey vendorKey()
	{
		Reference<JavaManifestKey> ref = this._vendor;
		JavaManifestKey rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._vendor = new WeakReference<>(
				(rv = new JavaManifestKey(this.prefix + "-Vendor")));
		
		return rv;
	}
	
	/**
	 * Returns the manifest key used for the version.
	 *
	 * @return The version manifest key.
	 * @since 2017/12/04
	 */
	public JavaManifestKey versionKey()
	{
		Reference<JavaManifestKey> ref = this._version;
		JavaManifestKey rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._version = new WeakReference<>(
				(rv = new JavaManifestKey(this.prefix + "-Version")));
		
		return rv;
	}
	
	/**
	 * Returns the suite type for the given manifest.
	 *
	 * @param __man The manifest to get the type from.
	 * @return The type of suite from the given manifest.
	 * @throws InvalidSuiteException If the type is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/04
	 */
	public static final SuiteType ofManifest(JavaManifest __man)
		throws InvalidSuiteException, NullPointerException
	{
		if (__man == null)
			throw new NullPointerException("NARG");
		
		JavaManifestAttributes attr = __man.getMainAttributes();
		for (SuiteType t : SuiteType.values())
			if (attr.definesValue(t.nameKey()))
				return t;
		
		// {@squirreljme.error DG0g Could not obtain the type of suite from
		// the input manifest.}
		throw new InvalidSuiteException("AR0g");
	}
}

