// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jdwp.trips;

import cc.squirreljme.jdwp.JDWPStepTracker;

/**
 * A trip on a thread.
 *
 * @since 2021/04/11
 */
public interface JDWPTripThread
	extends JDWPTrip
{
	/**
	 * Indicates that the given thread is alive or death.
	 * 
	 * @param __thread The thread state being changed.
	 * @param __isAlive Is this thread alive?
	 * @since 2021/04/11
	 */
	void alive(Object __thread, boolean __isAlive);
	
	/**
	 * Checks and performs stepping for the given thread.
	 * 
	 * @param __which Which thread is being stepped?
	 * @param __stepTracker The step tracker which is used for this event.
	 * @since 2021/04/28
	 */
	void step(Object __which, JDWPStepTracker __stepTracker);
	
	/**
	 * Indicates on thread suspension.
	 * 
	 * @param __thread The thread being changed.
	 * @param __isSuspended Is this suspended?
	 * @since 2021/04/18
	 */
	void suspension(Object __thread, boolean __isSuspended);
	
	/**
	 * Causes an unconditional breakpoint to fire, forcing execution for this
	 * thread to stop. This can be used to debug in the event of fatal
	 * debugging errors and otherwise.
	 * 
	 * @param __thread The thread to unconditionally breakpoint in.
	 * @since 2021/07/05
	 */
	void unconditionalBreakpoint(Object __thread);
}
