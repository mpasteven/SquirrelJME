// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.emulator.uiform;

import cc.squirreljme.jvm.mle.callbacks.UIFormCallback;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Handles window display events.
 *
 * @since 2020/09/13
 */
public class HandleDisplayWindowEvent
	implements WindowListener
{
	/** The display to call to. */
	public final SwingDisplay display;
	
	/**
	 * Initializes the event handler.
	 * 
	 * @param __display The display to call into.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/09/13
	 */
	public HandleDisplayWindowEvent(SwingDisplay __display)
		throws NullPointerException
	{
		if (__display == null)
			throw new NullPointerException("NARG");
		
		this.display = __display;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/09/13
	 */
	@Override
	public void windowOpened(WindowEvent __e)
	{
		// Ignored
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/09/13
	 */
	@Override
	public void windowClosing(WindowEvent __e)
	{
		SwingForm current = this.display.current();
		if (current != null)
		{
			UIFormCallback callback = current.callback();
			if (callback != null)
				callback.exitRequest(current);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/09/13
	 */
	@Override
	public void windowClosed(WindowEvent __e)
	{
		// Ignored
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/09/13
	 */
	@Override
	public void windowIconified(WindowEvent __e)
	{
		// Ignored
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/09/13
	 */
	@Override
	public void windowDeiconified(WindowEvent __e)
	{
		// Ignored
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/09/13
	 */
	@Override
	public void windowActivated(WindowEvent __e)
	{
		// Ignored
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/09/13
	 */
	@Override
	public void windowDeactivated(WindowEvent __e)
	{
		// Ignored
	}
}
