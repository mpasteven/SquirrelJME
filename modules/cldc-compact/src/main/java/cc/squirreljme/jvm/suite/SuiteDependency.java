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

import cc.squirreljme.runtime.cldc.util.NaturalComparator;
import cc.squirreljme.runtime.cldc.util.StringUtils;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * This represents a dependency that a LIBlet or MIDlet may depend on.
 *
 * @since 2017/02/22
 */
public final class SuiteDependency
	implements Comparable<SuiteDependency>, MarkedDependency
{
	/** The dependency type. */
	protected final SuiteDependencyType type;
	
	/** The dependency level. */
	protected final SuiteDependencyLevel level;
	
	/** The name. */
	protected final SuiteName name;
	
	/** The vendor. */
	protected final SuiteVendor vendor;
	
	/** The version range. */
	protected final SuiteVersionRange version;
	
	/** String representation. */
	private Reference<String> _string;
	
	/**
	 * Initializes the dependency which is parsed from the given input string.
	 *
	 * @param __s The string to parse.
	 * @throws InvalidSuiteException If the string is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/02/22
	 */
	public SuiteDependency(String __s)
		throws InvalidSuiteException, NullPointerException
	{
		// Check
		if (__s == null)
			throw new NullPointerException("NARG");
		
		// Trim whitespace
		__s = __s.trim();
		
		// Extract all semicolon positions
		int[] sc = StringUtils.multipleIndexOf(';', __s);
		
		// {@squirreljme.error DG06 Expected four semi-colons in the
		// dependency field. (The input dependency)}
		if (sc.length != 4)
			throw new InvalidSuiteException(String.format(
				"AR06 %s", __s));
		
		// Split fields
		String intype = __s.substring(0, sc[0]).trim(),
			inlevel = __s.substring(sc[0] + 1, sc[1]).trim(),
			inname = __s.substring(sc[1] + 1, sc[2]).trim(),
			invendor = __s.substring(sc[2] + 1, sc[3]).trim(),
			inversion = __s.substring(sc[3] + 1).trim();
		
		// Required fields
		SuiteDependencyType type;
		this.type = (type = SuiteDependencyType.of(intype));
		this.level = SuiteDependencyLevel.of(inlevel);
		
		// Optional fields
		SuiteName name;
		SuiteVendor vendor;
		SuiteVersionRange version;
		this.name = (name = (inname.isEmpty() ? null :
			new SuiteName(inname)));
		this.vendor = (vendor = (invendor.isEmpty() ? null :
			new SuiteVendor(invendor)));
		this.version = (version = (inversion.isEmpty() ? null :
			new SuiteVersionRange(inversion)));
		
		// Check
		SuiteDependency.__check(type, this.level, name, vendor, version);
	}
	
	/**
	 * Initializes the depedency with the given type, level, and where the
	 * remainder of the dependencies are parsed from the specified string.
	 *
	 * @param __type The type of dependency this is.
	 * @param __level The level of the dependency.
	 * @param __s The string to decode for the remainder of the dependency.
	 * @throws InvalidSuiteException If the input parameters are not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/26
	 */
	public SuiteDependency(SuiteDependencyType __type,
		SuiteDependencyLevel __level, String __s)
		throws InvalidSuiteException, NullPointerException
	{
		if (__type == null || __level == null || __s == null)
			throw new NullPointerException("NARG");
		
		// Trim whitespace
		__s = __s.trim();
		
		// Extract all semicolon positions
		int[] sc = StringUtils.multipleIndexOf(';', __s);
		
		// {@squirreljme.error DG07 Expected two semi-colons in the
		// dependency field. (The input dependency)}
		if (sc.length != 2)
			throw new InvalidSuiteException(String.format(
				"AR07 %s", __s));
		
		// Split fields
		String inname = __s.substring(0, sc[0]).trim(),
			invendor = __s.substring(sc[0] + 1, sc[1]).trim(),
			inversion = __s.substring(sc[1] + 1).trim();
		
		// Parse areas fields
		SuiteName name;
		SuiteVendor vendor;
		SuiteVersionRange version;
		this.name = (name = (inname.isEmpty() ? null :
			new SuiteName(inname)));
		this.vendor = (vendor = (invendor.isEmpty() ? null :
			new SuiteVendor(invendor)));
		this.version = (version = (inversion.isEmpty() ? null :
			new SuiteVersionRange(inversion)));
		
		SuiteDependency.__check(__type, __level, name, vendor, version);
		
		// Set
		this.type = __type;
		this.level = __level;
	}
	
	/**
	 * Initializes the dependency using the given parameters.
	 *
	 * @param __type The type of dependency this is.
	 * @param __level The level of the dependency.
	 * @param __name The name.
	 * @param __vendor The vendor.
	 * @param __version The version.
	 * @throws InvalidSuiteException If the input parameters are not valid.
	 * @throws NullPointerException If no type and/or name were specified.
	 * @since 2017/11/26
	 */
	public SuiteDependency(SuiteDependencyType __type,
		SuiteDependencyLevel __level, SuiteName __name,
		SuiteVendor __vendor, SuiteVersionRange __version)
		throws InvalidSuiteException, NullPointerException
	{
		if (__type == null || __level == null)
			throw new NullPointerException("NARG");
		
		SuiteDependency.__check(__type, __level, __name, __vendor, __version);
		
		// Set
		this.type = __type;
		this.level = __level;
		this.name = __name;
		this.vendor = __vendor;
		this.version = __version;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/11/30
	 */
	@Override
	public final int compareTo(SuiteDependency __d)
		throws NullPointerException
	{
		if (__d == null)
			throw new NullPointerException("NARG");
		
		// Type first
		int rv = this.type.compareTo(__d.type);
		if (rv != 0)
			return rv;
			
		// Optionality
		rv = this.level.compareTo(__d.level);
		if (rv != 0)
			return rv;
		
		// Name
		rv = Objects.<SuiteName>compare(this.name, __d.name,
			NaturalComparator.<SuiteName>instance());
		if (rv != 0)
			return rv;
		
		// Vendor
		rv = Objects.<SuiteVendor>compare(this.vendor, __d.vendor,
			NaturalComparator.<SuiteVendor>instance());
		if (rv != 0)
			return rv;
		
		// Version
		return Objects.<SuiteVersionRange>compare(this.version, __d.version,
			NaturalComparator.<SuiteVersionRange>instance());
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/02/22
	 */
	@Override
	public boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		// Check
		if (!(__o instanceof SuiteDependency))
			return false;
		
		// Compare
		SuiteDependency o = (SuiteDependency)__o;
		return this.type.equals(o.type) &&
			this.level.equals(o.level) &&
			Objects.equals(this.name, o.name) &&
			Objects.equals(this.vendor, o.vendor) &&
			Objects.equals(this.version, o.version);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/02/22
	 */
	@Override
	public int hashCode()
	{
		return this.type.hashCode() ^
			this.level.hashCode() ^
			Objects.hashCode(this.name) ^
			Objects.hashCode(this.vendor) ^
			Objects.hashCode(this.version);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/11/22
	 */
	@Override
	public boolean isOptional()
	{
		return this.level.isOptional();
	}
	
	/**
	 * Is this a required dependency?
	 *
	 * @return {@code true} if this is a required dependency.
	 * @since 2017/11/22
	 */
	public boolean isRequired()
	{
		return this.level.isRequired();
	}
	
	/**
	 * Returns the dependency level.
	 *
	 * @return The dependency level.
	 * @since 2017/02/22
	 */
	public SuiteDependencyLevel level()
	{
		return this.level;
	}
	
	/**
	 * Returns the dependency name.
	 *
	 * @return The dependency name, may be {@code null}.
	 * @since 2017/02/22
	 */
	public SuiteName name()
	{
		return this.name;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/31
	 */
	@Override
	public boolean matchesProvided(MarkedProvided __mp)
		throws NullPointerException
	{
		if (__mp == null)
			throw new NullPointerException("NARG");
		
		SuiteDependencyType type = this.type;
		Class<?> mpclass = __mp.getClass();
		SuiteName name = this.name;
		SuiteVendor vendor = this.vendor;
		SuiteVersionRange version = this.version;
		
		// Depends
		switch (type)
		{
				// Proprietary match
			case PROPRIETARY:
				// Potential internal project name
				if (__mp instanceof InternalName)
				{
					String myname = name.toString();
					
					// Needs at sign
					int dxat;
					if ((dxat = myname.indexOf('@')) < 0)
						return false;
					
					// Prefix must be project reference
					if (!myname.substring(0, dxat).
						equals("squirreljme.project"))
						return false;
					
					// Otherwise the project name must match
					return myname.substring(dxat + 1).
						equals(((InternalName)__mp).name());
				}
				
				// No matches
				return false;
				
				// Library
			case LIBLET:
				// Typed suite information
				if (__mp instanceof TypedSuite)
				{
					TypedSuite other = (TypedSuite)__mp;
					SuiteIdentifier ident = other.suite();
					
					// Only match other libraries
					if (other.type() != SuiteType.LIBLET)
						return false;
					
					// Must match name
					if (!name.equals(ident.name()))
						return false;
					
					// Match vendor if specified
					if (vendor != null)
						if (!vendor.equals(ident.vendor()))
							return false;
					
					// Check if version in range if specified
					if (version != null)
						if (!version.inRange(ident.version()))
							return false;
					
					// Is okay!
					return true;
				}
				
				// Unknown
				else
					return false;
					
				// Standard
			case STANDARD:
				if (__mp instanceof Standard)
				{
					Standard other = (Standard)__mp;
					
					// Must match name
					if (!name.equals(other.name()))
						return false;
					
					// Match vendor if specified
					if (vendor != null)
						if (!vendor.equals(other.vendor()))
							return false;
					
					// Check if version in range if specified
					if (version != null)
						if (!version.inRange(other.version()))
							return false;
					
					// Matches!
					return true;
				}
				
				// Not a standard
				else
					return false;
				
				// {@squirreljme.error DG08 Illegal dependency check.
				// (The dependency type; The target class)}
			default:
				throw new RuntimeException(String.format("AR08 %s %s",
					type, mpclass));
		}
	}
	
	/**
	 * Returns a dependency which is the same as this one except that it is
	 * required.
	 *
	 * @return This dependency but required.
	 * @since 2017/11/26
	 */
	public SuiteDependency toRequired()
	{
		if (this.isRequired())
			return this;
		return new SuiteDependency(this.type, SuiteDependencyLevel.REQUIRED,
			this.name, this.vendor, this.version);
	}
	
	/**
	 * Returns a dependency which is the same as this one except that it is
	 * optional.
	 *
	 * @return This dependency but optional.
	 * @since 2017/11/26
	 */
	public SuiteDependency toOptional()
	{
		if (this.isOptional())
			return this;
		return new SuiteDependency(this.type, SuiteDependencyLevel.OPTIONAL,
			this.name, this.vendor, this.version);
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
			// These are optional
			SuiteName name = this.name;
			SuiteVendor vendor = this.vendor;
			SuiteVersionRange version = this.version;
			
			// Generate
			this._string = new WeakReference<>((rv = String.format(
				"%s;%s;%s;%s;%s", this.type, this.level,
				(name == null ? "" : name),
				(vendor == null ? "" : vendor),
				(version == null ? "" : version))));
		}
		
		// Return it
		return rv;
	}
	
	/**
	 * Returns the dependency type.
	 *
	 * @return The dependency type.
	 * @since 2017/02/22
	 */
	public SuiteDependencyType type()
	{
		return this.type;
	}
	
	/**
	 * Returns the dependency vendor.
	 *
	 * @return The dependency vendor, may be {@code null}.
	 * @since 2017/02/22
	 */
	public SuiteVendor vendor()
	{
		return this.vendor;
	}
	
	/**
	 * Returns the dependency version.
	 *
	 * @return The dependency version, may be {@code null}.
	 * @since 2017/02/22
	 */
	public SuiteVersionRange version()
	{
		return this.version;
	}
	
	/**
	 * Checks whether the provided parameters are correct.
	 *
	 * @param __type The type of dependency this is.
	 * @param __level The level of the dependency.
	 * @param __name The name.
	 * @param __vendor The vendor.
	 * @param __version The version.
	 * @throws InvalidSuiteException If the input parameters are not valid.
	 * @since 2017/11/26
	 */
	private static final void __check(SuiteDependencyType __type,
		SuiteDependencyLevel __level, SuiteName __name,
		SuiteVendor __vendor, SuiteVersionRange __version)
		throws InvalidSuiteException
	{
		// {@squirreljme.error DG09 Dependencies on LIBlets must have the
		// name, vendor, and version set. (The type; The level; The name;
		// The vendor; The version)}
		if (__type == SuiteDependencyType.LIBLET && (__name == null ||
			__vendor == null || __version == null))
			throw new InvalidSuiteException(
				String.format("AR09 %s %s %s %s %s", __type, __level, __name,
					__vendor, __version));
	}
}

