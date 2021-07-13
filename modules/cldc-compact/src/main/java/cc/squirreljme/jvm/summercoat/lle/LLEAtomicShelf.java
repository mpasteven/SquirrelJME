// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.summercoat.lle;

import cc.squirreljme.jvm.Assembly;
import cc.squirreljme.runtime.cldc.debug.Debugging;

/**
 * This shelf provides helpers for atomic operations.
 *
 * @since 2020/05/30
 */
public final class LLEAtomicShelf
{
	/**
	 * Not used.
	 *
	 * @since 2020/05/30
	 */
	private LLEAtomicShelf()
	{
	}
	
	/**
	 * Lock for garbage collection operations.
	 *
	 * @return The locking key if locked, otherwise {@code 0} when busy.
	 * @since 2020/05/30
	 */
	public static int gcLock()
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Unlocks the garbage collector provided that the previous locking key
	 * matches.
	 *
	 * @param __key The key that was previously used to lock the garbage
	 * collector.
	 * @since 2020/05/30
	 */
	public static void gcUnlock(int __key)
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * For constant lock spins, this provides a consistent means of counting
	 * and then potentially fire-spinning or resting depending on the
	 * situation. This may cause a thread to preempt.
	 *
	 * On cooperatively tasked systems, this may switch to another thread.
	 *
	 * @param __count The number of times the lock has spun.
	 * @since 2020/05/30
	 */
	public static void spinLock(int __count)
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Returns a "unique" atomic tick value. The value returned should be
	 * unique enough but the actual value that is returned should not be used
	 * in any way for comparisons other than equality.
	 *
	 * @return The tick value, note that it should not be used for comparisons
	 * other than equality.
	 * @since 2020/05/03
	 */
	public static int tick()
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
}
