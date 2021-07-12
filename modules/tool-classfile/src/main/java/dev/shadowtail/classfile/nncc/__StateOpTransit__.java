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
 * State operation transits.
 *
 * @since 2021/01/19
 */
final class __StateOpTransit__
{
	/** The state operations and the desired target. */
	final StateOperationsAndTarget _stateOpAndTarget;
	
	/** Target data. */
	final __EData__ _eData;
	
	/**
	 * Initializes the state operation transit.
	 * 
	 * @param __stateOpAndTarget The state operation and the target.
	 * @param __eData The target data.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/19
	 */
	__StateOpTransit__(StateOperationsAndTarget __stateOpAndTarget,
		__EData__ __eData)
		throws NullPointerException
	{
		if (__stateOpAndTarget == null || __eData == null)
			throw new NullPointerException("NARG");
		
		this._stateOpAndTarget = __stateOpAndTarget;
		this._eData = __eData;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/01/19
	 */
	@Override
	public final String toString()
	{
		return this._stateOpAndTarget + " -> " + this._eData;
	}
}
