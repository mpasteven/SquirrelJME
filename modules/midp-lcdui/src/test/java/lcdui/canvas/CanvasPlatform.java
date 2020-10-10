// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package lcdui.canvas;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

/**
 * A platform for testing canvases.
 *
 * @since 2020/07/26
 */
public class CanvasPlatform
	extends Canvas
{
	/** The time of the last repaint. */
	private long _lastRepaint;
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/26
	 */
	@Override
	protected void paint(Graphics __g)
	{
		// Set paint as having happened
		synchronized (this)
		{
			// Debug
			System.err.println("Repaint happened...");
			
			this._lastRepaint = System.currentTimeMillis();
		}
		
		// Will need these to fill the canvas with something
		int width = this.getWidth();
		int height = this.getHeight();
		
		// Draw an X
		__g.drawLine(0, 0, width, height);
		__g.drawLine(0, height, width, 0);
	}
	
	/**
	 * Queries the last repaint time.
	 * 
	 * @return The last repaint time.
	 * @since 2020/07/27
	 */
	public final long queryLastRepaint()
	{
		synchronized (this)
		{
			return this._lastRepaint;
		}
	}
}