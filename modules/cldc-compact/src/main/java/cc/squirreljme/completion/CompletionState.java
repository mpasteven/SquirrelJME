// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.completion;

/**
 * This represents the state of completion.
 *
 * @since 2020/12/25
 */
public enum CompletionState
{
	/** No work or otherwise has been performed. */
	NOTHING,
	
	/** Has been started but not enough to be partially complete. */
	STARTED,
	
	/** Partially completed. */
	PARTIAL,
	
	/** Mostly completed. */
	MOSTLY,
	
	/** Complete. */
	COMPLETE,
	
	/* End. */
	;
}
