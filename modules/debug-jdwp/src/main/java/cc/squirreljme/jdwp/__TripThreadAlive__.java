// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jdwp;

import cc.squirreljme.jdwp.trips.JDWPTripThread;
import cc.squirreljme.jdwp.views.JDWPViewFrame;
import cc.squirreljme.jdwp.views.JDWPViewThread;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import java.lang.ref.Reference;

/**
 * Trip when a thread changes state or has an action or otherwise.
 *
 * @since 2021/04/11
 */
final class __TripThreadAlive__
	extends __TripBase__
	implements JDWPTripThread
{
	/**
	 * Initializes the trip.
	 * 
	 * @param __controller The controller.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/04/11
	 */
	__TripThreadAlive__(Reference<JDWPController> __controller)
		throws NullPointerException
	{
		super(__controller);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/11
	 */
	@Override
	public void alive(Object __thread, boolean __isAlive)
	{
		JDWPController controller = this.__controller();
		JDWPState state = controller.state;
		
		// Register this thread for later use
		state.items.put(__thread);
		
		// Forward generic event
		controller.signal(__thread, (__isAlive ? EventKind.THREAD_START :
			EventKind.THREAD_DEATH), __thread);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/28
	 */
	@Override
	public void step(Object __which, JDWPStepTracker __stepTracker)
	{
		JDWPController controller = this.__controller();
		JDWPViewThread viewThread = controller.viewThread();
		JDWPViewFrame viewFrame = controller.viewFrame();
		JDWPState state = controller.state;
		
		// Register this thread for later use
		state.items.put(__which);
		
		// Signal the step, if no events are found we likely are no longer
		// going to step so just stop
		JDWPStepTracker stepTracker = viewThread.stepTracker(__which);
		if (!controller.signal(__which, EventKind.SINGLE_STEP, stepTracker))
			stepTracker.clear();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/18
	 */
	@Override
	public void suspension(Object __thread, boolean __isSuspended)
	{
		// There are currently no triggers on this at all
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/07/05
	 */
	@Override
	public void unconditionalBreakpoint(Object __thread)
	{
		if (JDWPController._DEBUG)
			Debugging.debugNote("UNCONDITIONAL BREAKPOINT!");
		
		JDWPController controller = this.__controller();
		
		// Make sure this thread is registered
		controller.state.items.put(__thread);
		
		// Send the signal
		controller.signal(__thread, EventKind.UNCONDITIONAL_BREAKPOINT);
	}
}
