// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.pool;

import net.multiphasicapps.classfile.ClassName;

/**
 * Quick casting check.
 *
 * @since 2021/01/31
 */
public final class QuickCastCheck
{
	/** The class coming from. */
	public final ClassName from;
	
	/** The class going to. */
	public final ClassName to;
	
	/**
	 * Initialize the quick class cast check.
	 * 
	 * @param __from The source class.
	 * @param __to The class being cast to.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/31
	 */
	public QuickCastCheck(ClassName __from, ClassName __to)
		throws NullPointerException
	{
		if (__from == null || __to == null)
			throw new NullPointerException("NARG");
		
		this.from = __from;
		this.to = __to;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/01/31
	 */
	@Override
	public int hashCode()
	{
		return this.from.hashCode() ^ this.to.hashCode();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/01/31
	 */
	@Override
	public boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (!(__o instanceof QuickCastCheck))
			return false;
		
		QuickCastCheck o = (QuickCastCheck)__o;
		return this.from.equals(o.from) &&
			this.to.equals(o.to);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/01/31
	 */
	@Override
	public String toString()
	{
		return String.format("QuickCastCheck:[%s -> %s]", this.from, this.to);
	}
}
