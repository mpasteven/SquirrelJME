// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.runtime.launcher.ui;

import cc.squirreljme.jvm.mle.constants.UIPixelFormat;
import cc.squirreljme.runtime.cldc.SquirrelJME;
import cc.squirreljme.runtime.lcdui.mle.PencilGraphics;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/**
 * This is the splash screen for the launcher which always shows.
 *
 * @since 2019/05/19
 */
public final class SplashScreen
	extends Canvas
	implements Runnable
{
	/** The copyright string. */
	public static final String COPYRIGHT =
		"https://squirreljme.cc/\n" +
		"(C) 2013-2021 Stephanie Gawroriski\n" +
		"Licensed under the GPLv3!\nDonate to me on Patreon:\n" +
		"*** https://www.patreon.com/SquirrelJME! ***";
	
	/** The splash image width. */
	public static final int WIDTH =
		240;
	
	/** The splash image height. */
	public static final int HEIGHT =
		320;
	
	/** The image data to draw. */
	volatile int[] _image;
	
	/**
	 * Initializes the splash screen with a precached image.
	 *
	 * @param __sw The screen width.
	 * @param __sh The screen height.
	 * @since 2019/05/19
	 */
	public SplashScreen(int __sw, int __sh)
	{
		// Set a nice title!
		this.setTitle("SquirrelJME is Loading!");
		
		// Full-screen mode for the entire image
		this.setFullScreenMode(true);
		
		// Load the launcher image in a background thread so the splash screen
		// can still display the copyright notice
		new Thread(this, "LauncherImageLoader").start();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/05/19
	 */
	@Override
	public final void paint(Graphics __g)
	{
		// Draw the raw image data, is the fastest
		int[] image = this._image;
		if (image != null)
			__g.drawRGB(image, 0, SplashScreen.WIDTH, 0, 0,
				SplashScreen.WIDTH, SplashScreen.HEIGHT, false);
		
		// The image is not fully loaded yet, so draw the copyright at least
		else
			SplashScreen.__copyright(__g, true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/06/29
	 */
	@Override
	public final void run()
	{
		// Image is completely operated with using raw data
		int np = SplashScreen.WIDTH * SplashScreen.HEIGHT;
		int[] image = new int[np];
		
		// Load splash image onto the data
		try (InputStream in = SplashScreen.class.
			getResourceAsStream("splash.raw"))
		{
			// If it exists, use it
			if (in != null)
			{
				// Input raw pixels
				int nr = SplashScreen.WIDTH * SplashScreen.HEIGHT * 3;
				byte[] raw = new byte[nr];
				
				// Read in raw data
				for (int read = 0; read < nr;)
				{
					int rc = in.read(raw, read, nr - read);
					
					if (rc < 0)
						break;
					
					read += rc;
				}
				
				// Translate RGB byte pixels to RGB int pixels
				for (int o = 0, i = 0; o < np; o++)
					image[o] = ((raw[i++] & 0xFF) << 16) |
						((raw[i++] & 0xFF) << 8) |
						(raw[i++] & 0xFF);
			}
		}
		catch (IOException e)
		{
		}
		
		// Text will be drawn using the advanced graphics since it can
		// operate on integer buffers directly
		Graphics g = PencilGraphics.hardwareGraphics(
			UIPixelFormat.INT_RGB888, 
			SplashScreen.WIDTH, SplashScreen.HEIGHT,
			image, 0, null, 0, 0,
			SplashScreen.WIDTH, SplashScreen.HEIGHT);
		
		// Draw copyright at the bottom
		SplashScreen.__copyright(g, false);
		
		// Use this image
		this._image = image;
		
		// Perform a repaint before we return so the better looking splash
		// screen is used
		this.repaint();
	}
	
	/**
	 * Draws the copyright text onto the given graphics.
	 *
	 * @param __g The graphics to draw onto.
	 * @param __swip Should "SquirrelJME Loading..." be drawn?
	 * @throws NullPointerException On null arguments.
	 * @since 2019/06/29
	 */
	private static void __copyright(Graphics __g, boolean __swip)
		throws NullPointerException
	{
		if (__g == null)
			throw new NullPointerException("NARG");
		
		// Draw version number
		__g.setFont(Font.getFont("sansserif", 0, 16));
		__g.drawString(SquirrelJME.RUNTIME_VERSION, 238, 48,
			Graphics.RIGHT | Graphics.TOP);
		
		// Draw loading and the SquirrelJME string?
		if (__swip)
		{
			// SquirrelJME String
			__g.drawString("SquirrelJME", 2, 2,
				Graphics.TOP | Graphics.LEFT);
			
			// Loading...
			__g.drawString("Loading...", 120, 160,
				Graphics.VCENTER | Graphics.HCENTER);
		}
		
		// Set properties
		__g.setFont(Font.getFont("sansserif", 0, 12));
		
		// Draw a black drop-shadow, this makes the lighter front easier to
		// see regardless of what is on the background
		__g.setColor(0x000000);
		__g.drawString(SplashScreen.COPYRIGHT, 1, 318,
			Graphics.BOTTOM | Graphics.LEFT);
		__g.drawString(SplashScreen.COPYRIGHT, 3, 318,
			Graphics.BOTTOM | Graphics.LEFT);
		__g.drawString(SplashScreen.COPYRIGHT, 2, 317,
			Graphics.BOTTOM | Graphics.LEFT);
		__g.drawString(SplashScreen.COPYRIGHT, 2, 319,
			Graphics.BOTTOM | Graphics.LEFT);
		
		// Then draw the frontal white text
		__g.setColor(0xFFFFFF);
		__g.drawString(SplashScreen.COPYRIGHT, 2, 318,
			Graphics.BOTTOM | Graphics.LEFT);
	}
}

