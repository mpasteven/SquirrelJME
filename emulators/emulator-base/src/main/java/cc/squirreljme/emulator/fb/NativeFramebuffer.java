// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.emulator.fb;

import cc.squirreljme.emulator.EmulatorThreadContext;
import cc.squirreljme.jvm.Assembly;
import cc.squirreljme.jvm.Framebuffer;
import cc.squirreljme.jvm.SystemCallError;
import cc.squirreljme.jvm.SystemCallIndex;

/**
 * Contains the support for the framebuffer.
 *
 * @since 2020/02/29
 */
public final class NativeFramebuffer
{
	/** Framebuffer instance. */
	private static NativeFramebuffer _FRAMEBUFFER =
		null;
	
	/** The framebuffer title. */
	protected String title;
	
	/**
	 * Performs system call with the framebuffer.
	 *
	 * @param __ctx The emulator context.
	 * @param __func The function to perform.
	 * @param __a A.
	 * @param __b B.
	 * @param __c C.
	 * @param __d D.
	 * @param __e E.
	 * @param __f F.
	 * @param __g G.
	 * @return The result of the system call.
	 * @since 2020/02/29
	 */
	public final long systemCall(EmulatorThreadContext __ctx, int __func,
		int __a, int __b, int __c, int __d, int __e, int __f, int __g)
	{
		// Depends on the function
		switch (__func)
		{
				// The format of the framebuffer
			case Framebuffer.CONTROL_FORMAT:
				return Framebuffer.FORMAT_INTEGER_RGB888;
			
				// The capabilities of the framebuffer
			case Framebuffer.CONTROL_GET_CAPABILITIES:
				return Framebuffer.CAPABILITY_COLOR |
					Framebuffer.CAPABILITY_IPC_EVENTS |
					Framebuffer.CAPABILITY_KEYBOARD |
					Framebuffer.CAPABILITY_TOUCH;
			
				// Set title of the framebuffer
			case Framebuffer.CONTROL_SET_TITLE:
				this.title = String.valueOf((char[])Assembly.pointerToObject(
					__a));
				return 0;
			
			default:
				// So it is more easily known
				System.err.printf("FB?: %d(%d, %d, %d, %d, %d, %d, %d)\n",
					__func, __a, __b, __c, __d, __e, __f, __g);
				
				// Return as not supported
				__ctx.setError(SystemCallIndex.FRAMEBUFFER,
					SystemCallError.UNSUPPORTED_SYSTEM_CALL);
				return 0;
		}
	}
	
	/**
	 * Returns instance of the framebuffer.
	 *
	 * @return The framebuffer instance.
	 * @since 2020/02/29
	 */
	public static NativeFramebuffer getInstance()
	{
		NativeFramebuffer rv = NativeFramebuffer._FRAMEBUFFER;
		
		if (rv == null)
			synchronized (NativeFramebuffer.class)
			{
				rv = NativeFramebuffer._FRAMEBUFFER;
				if (rv == null)
				{
					rv = new NativeFramebuffer();
					NativeFramebuffer._FRAMEBUFFER = rv;
				}
			}
		
		return rv;
	}
}
