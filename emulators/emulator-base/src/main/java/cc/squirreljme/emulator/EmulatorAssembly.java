// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.emulator;

/**
 * This contains the implementation of some system calls in the event that the
 * JNI emulation layer does not have a C-based implementation of a method.
 *
 * @since 2020/02/26
 */
public final class EmulatorAssembly
{
	/** The thread contexts, storing thread specific information. */
	private static final ThreadLocal<EmulatorThreadContext> _CONTEXT =
		new ThreadLocal<>();
	
	/**
	 * Not used.
	 *
	 * @since 2020/02/26
	 */
	private EmulatorAssembly()
	{
	}
	
	/**
	 * Handles system calls in Java.
	 *
	 * @param __si System call index.
	 * @param __a Argument.
	 * @param __b Argument.
	 * @param __c Argument.
	 * @param __d Argument.
	 * @param __e Argument.
	 * @param __f Argument.
	 * @param __g Argument.
	 * @param __h Argument.
	 * @return The result.
	 * @since 2020/02/26
	 */
	public static long systemCall(short __si, int __a, int __b,
		int __c, int __d, int __e, int __f, int __g, int __h)
	{
		throw new Error("Invalid Emulator System Call");
	}
	
	/**
	 * Returns the current thread context.
	 *
	 * @return The thread context.
	 * @since 2020/02/26
	 */
	public static EmulatorThreadContext threadContext()
	{
		// Has this been created already?
		EmulatorThreadContext rv = EmulatorAssembly._CONTEXT.get();
		if (rv != null)
			return rv;
		
		// Does not exist, needs to be created
		synchronized (EmulatorAssembly.class)
		{
			// Check again
			rv = EmulatorAssembly._CONTEXT.get();
			if (rv != null)
				return rv;
			
			EmulatorAssembly._CONTEXT.set((rv = new EmulatorThreadContext()));
			return rv;
		}
	}
}
