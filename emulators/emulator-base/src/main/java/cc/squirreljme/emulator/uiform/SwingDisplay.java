// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.emulator.uiform;

import cc.squirreljme.jvm.mle.brackets.UIDisplayBracket;
import cc.squirreljme.jvm.mle.exceptions.MLECallError;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * Represents a Swing Display.
 *
 * @since 2020/07/01
 */
public final class SwingDisplay
	implements UIDisplayBracket
{
	/** The screen instance. */
	@SuppressWarnings({"StaticVariableMayNotBeInitialized", 
		"NonConstantFieldWithUpperCaseName"})
	private static SwingDisplay _DISPLAY;
	
	/** The frame that makes up this display. */
	protected final JFrame frame;
	
	/** The current frame visible on the form. */
	private SwingForm _current;
	
	static
	{
		try
		{
			// Greatly optimizes speed
			JFrame.setDefaultLookAndFeelDecorated(true);
		}
		catch (Throwable ignored)
		{
		}
	}
	
	/**
	 * Initializes the display.
	 * 
	 * @since 2020/07/01
	 */
	public SwingDisplay()
	{
		JFrame frame = new JFrame();
		
		// Branding!
		frame.setTitle("SquirrelJME");
		
		// Use a basic layout for the form
		frame.setLayout(new BorderLayout());
		
		// We will be handling window closing, so disable the button and
		// implement the close logic ourselves
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		// Add window listener to handle close events and resizing
		frame.addWindowListener(new HandleDisplayWindowEvent(this));
		
		this.frame = frame;
	}
	
	/**
	 * Returns the current form being displayed.
	 * 
	 * @return The current form.
	 * @since 2020/07/19
	 */
	public SwingForm current()
	{
		synchronized (this)
		{
			return this._current;
		}
	}
	
	/**
	 * Shows the given form on the display.
	 * 
	 * @param __form The form to show.
	 * @since 2020/07/01
	 */
	@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
	public void show(SwingForm __form)
		throws MLECallError
	{
		synchronized (this)
		{
			JFrame frame = this.frame;
			
			// The form must not be associated with a display
			if (__form != null)
				synchronized (__form)
				{
					if (__form._display != null)
						throw new MLECallError(
							"Associated with display.");
				}
			
			// Do nothing if this is already current
			SwingForm current = this._current;
			if (current == __form)
				return;
			
			// Remove previous form if there is one
			if (current != null)
				synchronized (current)
				{
					// Snip it out
					frame.remove(current.formPanel);
					
					// And clear the linking
					current._display = null;
				}
			
			// Hide the current form
			if (__form == null)
			{
				frame.setVisible(false);
			}
			
			// Make our form visible
			else
			{
				// Remove all components from the frame and make our form the
				// only one
				frame.setLayout(new BorderLayout());
				frame.add(__form.formPanel, BorderLayout.CENTER);
				
				// Set an appropriate title
				if (__form._nextTitle != null)
					frame.setTitle(__form._nextTitle);
				
				// Minimize space used
				frame.pack();
				
				// If the frame was not already visible then just center it on
				// screen 
				boolean alreadyVisible = frame.isVisible();
				if (!alreadyVisible)
					frame.setLocationRelativeTo(null);
				
				// Now show it
				frame.setVisible(true);
				
				// Currently displayed form
				synchronized (__form)
				{
					this._current = __form;
					__form._display = this;
				}
			}
		}
		
		// Tell the form it was made current
		if (__form != null)
			__form.madeCurrent();
	}
	
	/**
	 * Returns the display instance.
	 * 
	 * @return The instance of the display.
	 * @since 2020/07/01
	 */
	@SuppressWarnings("StaticVariableUsedBeforeInitialization")
	public static SwingDisplay getInstance()
	{
		// Already cached?
		SwingDisplay rv = SwingDisplay._DISPLAY;
		if (rv != null)
			return rv;
		
		// Create it?
		rv = new SwingDisplay();
		SwingDisplay._DISPLAY = rv;
		
		return rv;
	}
}
