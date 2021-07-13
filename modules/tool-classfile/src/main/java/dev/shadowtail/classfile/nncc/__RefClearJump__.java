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
 * Holder for reference clear jumps
 *
 * @since 2021/01/19
 */
final class __RefClearJump__
{
	/** The enqueue used and the label. */
	final EnqueueAndLabel _enqueueAndLabel;
	
	/** Debug data. */
	final __EData__ _eData;
	
	/**
	 * Initializes the reference clear jump.
	 * 
	 * @param __enqueueAndLabel The enqueue and label used.
	 * @param __eData The extra data.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/19
	 */
	__RefClearJump__(EnqueueAndLabel __enqueueAndLabel, __EData__ __eData)
		throws NullPointerException
	{
		if (__enqueueAndLabel == null || __eData == null)
			throw new NullPointerException("NARG");
		
		this._enqueueAndLabel = __enqueueAndLabel;
		this._eData = __eData;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/01/19
	 */
	@Override
	public final String toString()
	{
		return this._enqueueAndLabel + " -> " + this._eData;
	}
}
