// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.nncc;

import dev.shadowtail.classfile.xlate.ExceptionHandlerTransition;

/**
 * Information for a transiting exception handler.
 *
 * @since 2021/01/19
 */
final class __TransitingExceptionHandler__
{
	/** The code transition happening. */
	final ExceptionHandlerTransition _transition;
	
	/** The data for the exception itself. */
	final __EData__ _eData;
	
	/**
	 * Initializes the transition to an exception handler.
	 * 
	 * @param __transition The transition used.
	 * @param __data The exception target data.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/19
	 */
	__TransitingExceptionHandler__(ExceptionHandlerTransition __transition,
		__EData__ __data)
		throws NullPointerException
	{
		if (__transition == null || __data == null)
			throw new NullPointerException("NARG");
		
		this._transition = __transition;
		this._eData = __data;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/01/19
	 */
	@Override
	public final String toString()
	{
		return this._transition + " -> " + this._eData;
	}
}
