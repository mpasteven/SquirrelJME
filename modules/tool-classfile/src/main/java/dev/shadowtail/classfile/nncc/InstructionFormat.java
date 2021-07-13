// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.nncc;

import java.util.AbstractList;

/**
 * Represents the format that an instruction uses, this consists of
 * multiple {@link ArgumentFormat}s.
 *
 * @since 2021/06/10
 */
public final class InstructionFormat
	extends AbstractList<ArgumentFormat>
{
	/** The formats. */
	private final ArgumentFormat[] _formats;
	
	/**
	 * Initializes the instruction formats.
	 * 
	 * @param __formats The formats used.
	 * @since 2021/06/10
	 */
	public InstructionFormat(ArgumentFormat... __formats)
	{
		this._formats = (__formats == null ? new ArgumentFormat[0] :
			__formats.clone());
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/06/10
	 */
	@Override
	public ArgumentFormat get(int __dx)
	{
		return this._formats[__dx];
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/06/10
	 */
	@Override
	public int size()
	{
		return this._formats.length;
	}
}
