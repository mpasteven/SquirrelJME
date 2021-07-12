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
import cc.squirreljme.jvm.mle.constants.UIItemType;
import cc.squirreljme.jvm.mle.constants.UIKeyEventType;
import cc.squirreljme.jvm.mle.constants.UIWidgetProperty;
import cc.squirreljme.jvm.mle.exceptions.MLECallError;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 * Button.
 *
 * @since 2020/07/18
 */
public class SwingItemButton
	extends SwingItem
	implements ActionListener
{
	/** The button. */
	private final JButton button;
	
	/**
	 * Initializes the item.
	 * 
	 * @since 2020/07/18
	 */
	public SwingItemButton()
	{
		super(UIItemType.BUTTON);
		
		JButton button = new JButton();
		this.button = button;
		
		// To detect button presses
		button.addActionListener(this);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/09/27
	 */
	@Override
	public void actionPerformed(ActionEvent __e)
	{
		SwingForm form = this._form;
		if (form == null)
			return;
		
		UIFormCallback callback = form.callback();
		if (callback == null)
			return;
		
		// Fake this as a key being pressed on this item
		callback.eventKey(form, this, UIKeyEventType.COMMAND_ACTIVATED,
			0, 0);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/18
	 */
	@Override
	public JButton component()
	{
		return this.button;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/18
	 */
	@Override
	public void deletePost()
	{
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/18
	 */
	@Override
	public void property(int __intProp, int __sub, int __newValue)
		throws MLECallError
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/18
	 */
	@Override
	public void property(int __strProp, int __sub, String __newValue)
	{
		switch (__strProp)
		{
			case UIWidgetProperty.STRING_LABEL:
				this.button.setText(__newValue);
				break;
			
			default:
				throw new MLECallError("Unknown property: " + __strProp);
		}
	}
}
