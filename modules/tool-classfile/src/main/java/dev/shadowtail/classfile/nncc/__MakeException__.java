// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.nncc;

/**
 * A generator for created exceptions.
 *
 * @since 2021/01/19
 */
final class __MakeException__
{
	/** The class and label used. */
	final ClassAndLabel _classAndLabel;
	
	/** Target data. */
	final __EData__ _eData;
	
	/**
	 * Initializes the make exception.
	 * 
	 * @param __classAndLabel The class and label.
	 * @param __eData The target data.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/19
	 */
	__MakeException__(ClassAndLabel __classAndLabel, __EData__ __eData)
		throws NullPointerException
	{
		if (__classAndLabel == null || __eData == null)
			throw new NullPointerException("NARG");
		
		this._classAndLabel = __classAndLabel;
		this._eData = __eData;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/01/19
	 */
	@Override
	public final String toString()
	{
		return this._classAndLabel + " -> " + this._eData;
	}
}
