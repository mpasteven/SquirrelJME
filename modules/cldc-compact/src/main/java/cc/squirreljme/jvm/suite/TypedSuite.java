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
 * This represents a suite with an associated type.
 *
 * @since 2017/12/30
 */
public final class TypedSuite
	implements MarkedProvided
{
	/** The suite type. */
	protected final SuiteType type;
	
	/** The suite. */
	protected final SuiteIdentifier suite;
	
	/** String representation. */
	private Reference<String> _string;
	
	/**
	 * Initializes the typed suite.
	 *
	 * @param __type The type of suite.
	 * @param __suite The suite information.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/30
	 */
	public TypedSuite(SuiteType __type, SuiteIdentifier __suite)
		throws NullPointerException
	{
		if (__type == null || __suite == null)
			throw new NullPointerException("NARG");
		
		this.type = __type;
		this.suite = __suite;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/30
	 */
	@Override
	public boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (!(__o instanceof TypedSuite))
			return false;
		
		TypedSuite o = (TypedSuite)__o;
		return this.type.equals(o.type) &&
			this.suite.equals(o.suite);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/30
	 */
	@Override
	public int hashCode()
	{
		return this.type.hashCode() ^
			this.suite.hashCode();
	}
	
	/**
	 * Returns the suite.
	 *
	 * @return The suite.
	 * @since 2017/12/30
	 */
	public SuiteIdentifier suite()
	{
		return this.suite;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/30
	 */
	@Override
	public String toString()
	{
		Reference<String> ref = this._string;
		String rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._string = new WeakReference<>(
				(rv = this.type + " " + this.suite));
		
		return rv;
	}
	
	/**
	 * Returns the type.
	 *
	 * @return The type.
	 * @since 2017/12/30
	 */
	public SuiteType type()
	{
		return this.type;
	}
}

