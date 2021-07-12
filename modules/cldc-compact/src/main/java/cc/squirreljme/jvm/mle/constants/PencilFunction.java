// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.mle.constants;

/**
 * The graphics function used for {@link PencilCapabilities}, may be used to
 * simplify the determination of which drawing method to use.
 *
 * @since 2021/01/04
 */
public interface PencilFunction
{
	/**
	 * Function for the following:
	 * - {@code copyArea}.
	 */
	byte FUNC_COPY_AREA =
		1;
	
	/**
	 * Function for the following:
	 * - {@code drawARGB16}. 
	 * - {@code drawRGB16}.
	 */
	byte FUNC_DRAW_XRGB16_SIMPLE =
		2;
	
	/**
	 * Function for the following:
	 * - {@code drawRGB}. 
	 */
	byte FUNC_DRAW_XRGB32_SIMPLE =
		4;
	
	/**
	 * Function for the following:
	 * - {@code drawImage}. 
	 * - {@code drawRegion}.
	 */
	byte FUNC_DRAW_XRGB16_REGION =
		8;
	
	/**
	 * Function for the following:
	 * - {@code drawImage}. 
	 * - {@code drawRegion}.
	 */
	byte FUNC_DRAW_XRGB32_REGION =
		16;
	
	/**
	 * Function for the following:
	 * - {@code drawArc}. 
	 */
	byte FUNC_DRAW_ARC =
		32;
	
	/**
	 * Function for the following:
	 * - {@code drawLine}. 
	 */
	byte FUNC_DRAW_LINE =
		64;
	
	/**
	 * Function for the following:
	 * - {@code drawRect}. 
	 */
	short FUNC_DRAW_RECT =
		128;
	
	/**
	 * Function for the following:
	 * - {@code drawRoundRect}. 
	 */
	short FUNC_DRAW_ROUNDRECT =
		256;
	
	/**
	 * Function for the following:
	 * - {@code fillArc}. 
	 */
	short FUNC_FILL_ARC =
		512;
	
	/**
	 * Function for the following:
	 * - {@code fillRect}. 
	 */
	short FUNC_FILL_RECT =
		1024;
	
	/**
	 * Function for the following:
	 * - {@code fillRoundRect}. 
	 */
	short FUNC_FILL_ROUNDRECT =
		2048;
	
	/**
	 * Function for the following:
	 * - {@code fillTriangle}. 
	 */
	short FUNC_FILL_TRIANGLE =
		4096;
	
	/**
	 * Function for the following:
	 * - {@code drawChar}.
	 * - {@code drawChars}.
	 * - {@code drawString}.
	 * - {@code drawSubstring}.
	 * - {@code drawText}.
	 * - {@code getFont}.
	 * - {@code setFont}.
	 */
	short FUNC_FONT_TEXT =
		8192;
}
