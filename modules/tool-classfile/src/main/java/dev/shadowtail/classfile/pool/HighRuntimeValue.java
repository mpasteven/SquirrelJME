// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.pool;

/**
 * High long value stored.
 *
 * @since 2021/04/07
 */
public final class HighRuntimeValue
{
	/** The attachment. */
	public final Object attachment;
	
	/**
	 * Initializes the high long value.
	 * 
	 * @param __attachment The attachment.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/04/07
	 */
	public HighRuntimeValue(Object __attachment)
		throws NullPointerException
	{
		if (__attachment == null)
			throw new NullPointerException("NARG");
		
		this.attachment = __attachment;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/07
	 */
	@Override
	public boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (!(__o instanceof HighRuntimeValue))
			return false;
		
		return this.attachment.equals(((HighRuntimeValue)__o).attachment);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/07
	 */
	@Override
	public int hashCode()
	{
		return this.attachment.hashCode();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/07
	 */
	@Override
	public String toString()
	{
		return "HighLong:" + this.attachment;
	}
}
