// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.runtime.lcdui.gfx;

import cc.squirreljme.runtime.lcdui.font.SQFFont;
import javax.microedition.lcdui.Font;

/**
 * This represents the state of the current text drawing.
 *
 * This is used to reduce pressure on the text drawing code because it is
 * complex.
 *
 * @since 2019/12/15
 */
final class __TextState__
{
	/** Metrics. */
	public final int[] metrics =
		new int[4];
	
	/** Integer arguments. */
	public final int[] chint =
		new int[8];
	
	/** Object argumnets. */
	public final Object[] chobj =
		new Object[1];
	
	/** The old color. */
	public int oldcolor;
	
	/** Text width/height. */
	public int textw, texth;
	
	/** Text end X/Y. */
	public int tex, tey;
	
	/** The starting X clip. */
	public int clipsx;
	
	/** The starting Y clip. */
	public int clipsy;
	
	/** The ending X clip. */
	public int clipex;
	
	/** The ending Y clip. */
	public int clipey;
	
	/** The last font. */
	public Font lastfont;
	
	/** The font to use. */
	public SQFFont sqf;
	
	/** The font bitmap. */
	public byte[] bmp;
	
	/** The pixel height of the font. */
	public int pixelheight;
	
	/** Bits per scanline. */
	public int bitsperscan;
	
	/** Used background color. */
	public int bgcol;
	
	/** Does this have a background? */
	public boolean hasbg;
	
	/**
	 * Loads integer arguments.
	 *
	 * @param __a A.
	 * @param __b B.
	 * @param __c C.
	 * @param __d D.
	 * @param __e E.
	 * @param __f F.
	 * @return The integer array.
	 * @since 2019/12/15
	 */
	public final int[] loadIntArgs(int __a, int __b, int __c, int __d,
		int __e, int __f)
	{
		int[] chint = this.chint;
		
		chint[0] = __a;
		chint[1] = __b;
		chint[2] = __c;
		chint[3] = __d;
		chint[4] = __e;
		chint[5] = __f;
		
		return chint;
	}
	
	/**
	 * Loads integer arguments.
	 *
	 * @param __a A.
	 * @param __b B.
	 * @param __c C.
	 * @param __d D.
	 * @param __e E.
	 * @param __f F.
	 * @param __g G.
	 * @param __h H.
	 * @return The integer array.
	 * @since 2019/12/15
	 */
	public final int[] loadIntArgs(int __a, int __b, int __c, int __d,
		int __e, int __f, int __g, int __h)
	{
		int[] chint = this.chint;
		
		chint[0] = __a;
		chint[1] = __b;
		chint[2] = __c;
		chint[3] = __d;
		chint[4] = __e;
		chint[5] = __f;
		chint[6] = __g;
		chint[7] = __h;
		
		return chint;
	}
	
	/**
	 * Loads object array.
	 *
	 * @param __a A.
	 * @return The object array.
	 * @since 2019/12/15
	 */
	public final Object[] loadObject(Object __a)
	{
		Object[] chobj = this.chobj;
		
		chobj[0] = __a;
		
		return chobj;
	}
}

