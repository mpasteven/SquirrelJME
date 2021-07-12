// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.emulator.uiform;

import cc.squirreljme.jvm.mle.constants.UIItemType;
import cc.squirreljme.jvm.mle.constants.UISpecialCode;
import cc.squirreljme.jvm.mle.constants.UIWidgetProperty;
import cc.squirreljme.jvm.mle.exceptions.MLECallError;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import javax.swing.JPanel;

/**
 * Standard canvas item.
 *
 * @since 2020/07/18
 */
public class SwingItemCanvas
	extends SwingItem
{
	/** The panel being drawn on. */
	protected final __PaintingPanel__ panel;
	
	/** Repainting coordinates. */
	private final int[] _repaint =
		new int[4];
	
	/**
	 * Initializes the item.
	 * 
	 * @since 2020/07/18
	 */
	public SwingItemCanvas()
	{
		super(UIItemType.CANVAS);
		
		__PaintingPanel__ panel = new __PaintingPanel__(this);
		this.panel = panel;
		
		panel.addComponentListener(new HandleComponentEvents(this));
		panel.addKeyListener(new HandleKeyEvents(this));
		
		// Allow this to be focused so it can have key events within
		panel.setFocusable(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/18
	 */
	@Override
	public JPanel component()
	{
		return this.panel;
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
	 * @since 2020/09/21
	 */
	@Override
	public void property(int __intProp, int __sub, int __val)
		throws MLECallError
	{
		switch (__intProp)
		{
				// Request repaint of canvas
			case UIWidgetProperty.INT_SIGNAL_REPAINT:
				int[] repaint = this._repaint;
				switch (__val & UISpecialCode.REPAINT_KEY_MASK)
				{
					case UISpecialCode.REPAINT_EXECUTE:
						this.panel.repaint(repaint[0], repaint[1],
							repaint[2], repaint[3]);
						break;
					
					case UISpecialCode.REPAINT_KEY_X:
						repaint[0] = __val & UISpecialCode.REPAINT_VALUE_MASK;
						break;
					
					case UISpecialCode.REPAINT_KEY_Y:
						repaint[1] = __val & UISpecialCode.REPAINT_VALUE_MASK;
						break;
					
					case UISpecialCode.REPAINT_KEY_WIDTH:
						repaint[2] = __val & UISpecialCode.REPAINT_VALUE_MASK;
						break;
					
					case UISpecialCode.REPAINT_KEY_HEIGHT:
						repaint[3] = __val & UISpecialCode.REPAINT_VALUE_MASK;
						break;
					
					default:
						throw new MLECallError(
							"Bad repaint signal: " + __val);
				}
				break;
			
			default:
				throw new MLECallError("Unknown property: " + __intProp);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/09/21
	 */
	@Override
	public void property(int __strProp, int __sub, String __newValue)
	{
		throw Debugging.todo();
	}
}
