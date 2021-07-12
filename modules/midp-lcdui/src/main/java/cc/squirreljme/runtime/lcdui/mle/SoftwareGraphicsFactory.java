// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.runtime.lcdui.mle;

import cc.squirreljme.jvm.mle.constants.UIPixelFormat;
import cc.squirreljme.runtime.lcdui.gfx.AdvancedGraphics;
import javax.microedition.lcdui.Graphics;

/**
 * This factory is responsible for creating software level graphics renderers
 * that can draw within an array pixel buffer.
 *
 * @since 2020/09/25
 */
public final class SoftwareGraphicsFactory
{
	/**
	 * Not used.
	 * 
	 * @since 2020/09/25
	 */
	private SoftwareGraphicsFactory()
	{
	}
	
	/**
	 * Creates a graphics which can draw into the given pixel buffer using
	 * purely software operations.
	 * 
	 * @param __pf The {@link UIPixelFormat} used for the draw.
	 * @param __bw The buffer width.
	 * @param __bh The buffer height.
	 * @param __buf The target buffer to draw to, this is cast to the correct
	 * buffer format.
	 * @param __offset The offset to the start of the buffer.
	 * @param __pal The color palette, may be {@code null}. 
	 * @param __sx Starting surface X coordinate.
	 * @param __sy Starting surface Y coordinate.
	 * @param __sw Surface width.
	 * @param __sh Surface height.
	 * @return A software graphics implementation.
	 * @throws IllegalArgumentException If the pixel format is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/09/25
	 */
	public static Graphics softwareGraphics(int __pf, int __bw,
		int __bh, Object __buf, int __offset, int[] __pal, int __sx, int __sy,
		int __sw, int __sh)
		throws IllegalArgumentException, NullPointerException
	{
		if (__buf == null)
			throw new NullPointerException("NARG");
		
		switch (__pf)
		{
				// 32-bit RGB(A)
			case UIPixelFormat.INT_RGB888:
			case UIPixelFormat.INT_RGBA8888:
				return new AdvancedGraphics(
					(int[])__buf,
					(__pf == UIPixelFormat.INT_RGBA8888),
					null,
					__sw, __sh,
					__bw,
					__offset,
					__sx, __sy);
			
			// {@squirreljme.error EB3f Unsupported software pixel format.
			// (The pixel format)}
			default:
				throw new IllegalArgumentException("EB3f " + __pf);
		}
	}
}
