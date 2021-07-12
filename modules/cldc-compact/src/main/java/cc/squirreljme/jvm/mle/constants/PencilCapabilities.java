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
 * Capabilities for the hardware accelerated pencil graphics drawing.
 * 
 * This interface contains bit-fields.
 *
 * @since 2020/09/25
 */
public interface PencilCapabilities
{
	/**
	 * Minimum capabilities required by the implementation, this includes all
	 * of that state operations such as: transforms, clips, colors, styles,
	 * and otherwise.
	 * 
	 * Capable of the following:
	 * - {@code clipRect}. 
	 * - {@code getAlpha}.
	 * - {@code getAlphaColor}.
	 * - {@code getBlendingMode}.
	 * - {@code getBlueComponent}.
	 * - {@code getClipHeight}.
	 * - {@code getClipWidth}.
	 * - {@code getClipX}.
	 * - {@code getClipY}.
	 * - {@code getColor}.
	 * - {@code getDisplayColor}.
	 * - {@code getGrayScale}.
	 * - {@code getGreenComponent}.
	 * - {@code getRedComponent}.
	 * - {@code getStrokeStyle}.
	 * - {@code getTranslateX}.
	 * - {@code getTranslateY}.
	 * - {@code setAlpha}.
	 * - {@code setAlphaColor}.
	 * - {@code setBlendingMode}.
	 * - {@code setClip}.
	 * - {@code setColor}.
	 * - {@code setGrayScale}.
	 * - {@code setStrokeStyle}.
	 * - {@code translate}.
	 */
	byte MINIMUM =
		0x1;
	
	/**
	 * Capable of the following:
	 * - {@code copyArea} noblend.
	 */
	byte COPY_AREA_NOBLEND =
		0x02;
	
	/**
	 * Capable of the following:
	 * - {@code drawARGB16} noblend. 
	 * - {@code drawRGB16} noblend.
	 */
	byte DRAW_XRGB16_SIMPLE_NOBLEND =
		0x04;
	
	/**
	 * Capable of the following:
	 * - {@code drawRGB} noblend. 
	 */
	byte DRAW_XRGB32_SIMPLE_NOBLEND =
		0x08;
	
	/**
	 * Capable of the following:
	 * - {@code drawImage} noblend. 
	 * - {@code drawRegion} noblend.
	 */
	byte DRAW_XRGB16_REGION_NOBLEND =
		0x10;
	
	/**
	 * Capable of the following:
	 * - {@code drawImage} noblend. 
	 * - {@code drawRegion} noblend.
	 */
	byte DRAW_XRGB32_REGION_NOBLEND =
		0x20;
	
	/**
	 * Capable of the following:
	 * - {@code drawArc} solid. 
	 */
	byte DRAW_ARC_SOLID_NOBLEND =
		0x40;
	
	/**
	 * Capable of the following:
	 * - {@code drawArc} dotted. 
	 */
	short DRAW_ARC_DOT_NOBLEND =
		0x80;
	
	/**
	 * Capable of the following:
	 * - {@code drawLine} solid. 
	 */
	short DRAW_LINE_SOLID_NOBLEND =
		0x100;
	
	/**
	 * Capable of the following:
	 * - {@code drawLine} dotted. 
	 */
	short DRAW_LINE_DOT_NOBLEND =
		0x200;
	
	/**
	 * Capable of the following:
	 * - {@code drawRect} solid. 
	 */
	short DRAW_RECT_SOLID_NOBLEND =
		0x400;
	
	/**
	 * Capable of the following:
	 * - {@code drawRect} dotted. 
	 */
	short DRAW_RECT_DOT_NOBLEND =
		0x800;
	
	/**
	 * Capable of the following:
	 * - {@code drawRoundRect} solid. 
	 */
	short DRAW_ROUNDRECT_SOLID_NOBLEND =
		0x1000;
	
	/**
	 * Capable of the following:
	 * - {@code drawRoundRect} dotted. 
	 */
	short DRAW_ROUNDRECT_DOT_NOBLEND =
		0x2000;
	
	/**
	 * Capable of the following:
	 * - {@code fillArc} noblend. 
	 */
	short FILL_ARC_NOBLEND =
		0x4000;
	
	/**
	 * Capable of the following:
	 * - {@code fillRect} noblend. 
	 */
	int FILL_RECT_NOBLEND =
		0x8000;
	
	/**
	 * Capable of the following:
	 * - {@code fillRoundRect} noblend. 
	 */
	int FILL_ROUNDRECT_NOBLEND =
		0x10000;
	
	/**
	 * Capable of the following:
	 * - {@code fillTriangle} noblend. 
	 */
	int FILL_TRIANGLE_NOBLEND =
		0x20000;
	
	/**
	 * Capable of the following:
	 * - {@code drawChar} noblend.
	 * - {@code drawChars} noblend.
	 * - {@code drawString} noblend.
	 * - {@code drawSubstring} noblend.
	 * - {@code drawText} noblend.
	 * - {@code getFont} noblend.
	 * - {@code setFont} noblend.
	 */
	int FONT_TEXT_NOBLEND =
		0x40000;
	
	/**
	 * Capable of the following:
	 * - {@code copyArea} blended.
	 */
	int COPY_AREA_BLEND =
		0x80000;
	
	/**
	 * Capable of the following:
	 * - {@code drawARGB16} blended. 
	 * - {@code drawRGB16} blended.
	 */
	int DRAW_XRGB16_SIMPLE_BLEND =
		0x100000;
	
	/**
	 * Capable of the following:
	 * - {@code drawRGB} blended. 
	 */
	int DRAW_XRGB32_SIMPLE_BLEND =
		0x200000;
	
	/**
	 * Capable of the following:
	 * - {@code drawImage} blended. 
	 * - {@code drawRegion} blended.
	 */
	int DRAW_XRGB16_REGION_BLEND =
		0x400000;
	
	/**
	 * Capable of the following:
	 * - {@code drawImage} blended. 
	 * - {@code drawRegion} blended.
	 */
	int DRAW_XRGB32_REGION_BLEND =
		0x800000;
	
	/**
	 * Capable of the following:
	 * - {@code drawArc} solid. 
	 */
	int DRAW_ARC_SOLID_BLEND =
		0x1000000;
	
	/**
	 * Capable of the following:
	 * - {@code drawArc} dotted. 
	 */
	int DRAW_ARC_DOT_BLEND =
		0x2000000;
	
	/**
	 * Capable of the following:
	 * - {@code drawLine} solid. 
	 */
	int DRAW_LINE_SOLID_BLEND =
		0x4000000;
	
	/**
	 * Capable of the following:
	 * - {@code drawLine} dotted. 
	 */
	int DRAW_LINE_DOT_BLEND =
		0x8000000;
	
	/**
	 * Capable of the following:
	 * - {@code drawRect} solid blended. 
	 */
	int DRAW_RECT_SOLID_BLEND =
		0x10000000;
	
	/**
	 * Capable of the following:
	 * - {@code drawRect} dotted blended. 
	 */
	int DRAW_RECT_DOT_BLEND =
		0x20000000;
	
	/**
	 * Capable of the following:
	 * - {@code drawRoundRect} solid blended. 
	 */
	int DRAW_ROUNDRECT_SOLID_BLEND =
		0x40000000;
	
	/**
	 * Capable of the following:
	 * - {@code drawRoundRect} dotted blended. 
	 */
	long DRAW_ROUNDRECT_DOT_BLEND =
		0x80000000;
	
	/**
	 * Capable of the following:
	 * - {@code fillArc} blended. 
	 */
	long FILL_ARC_BLEND =
		0x100000000L;
	
	/**
	 * Capable of the following:
	 * - {@code fillRect} blended. 
	 */
	long FILL_RECT_BLEND =
		0x200000000L;
	
	/**
	 * Capable of the following:
	 * - {@code fillRoundRect} blended. 
	 */
	long FILL_ROUNDRECT_BLEND =
		0x400000000L;
	
	/**
	 * Capable of the following:
	 * - {@code fillTriangle} blended. 
	 */
	long FILL_TRIANGLE_BLEND =
		0x800000000L;
	
	/**
	 * Capable of the following:
	 * - {@code drawChar} blended.
	 * - {@code drawChars} blended.
	 * - {@code drawString} blended.
	 * - {@code drawSubstring} blended.
	 * - {@code drawText} blended.
	 * - {@code getFont} blended.
	 * - {@code setFont} blended.
	 */
	long FONT_TEXT_BLEND =
		0x1000000000L;
	
	/** All capabilities that do not use blending, may be used as a mask. */
	long ALL_NOBLEND =
		PencilCapabilities.MINIMUM |
		PencilCapabilities.COPY_AREA_NOBLEND |
		PencilCapabilities.DRAW_ARC_DOT_NOBLEND |
		PencilCapabilities.DRAW_ARC_SOLID_NOBLEND |
		PencilCapabilities.DRAW_LINE_DOT_NOBLEND |
		PencilCapabilities.DRAW_LINE_SOLID_NOBLEND |
		PencilCapabilities.DRAW_RECT_DOT_NOBLEND |
		PencilCapabilities.DRAW_RECT_SOLID_NOBLEND |
		PencilCapabilities.DRAW_ROUNDRECT_DOT_NOBLEND |
		PencilCapabilities.DRAW_ROUNDRECT_SOLID_NOBLEND |
		PencilCapabilities.DRAW_XRGB16_REGION_NOBLEND |
		PencilCapabilities.DRAW_XRGB16_SIMPLE_NOBLEND |
		PencilCapabilities.DRAW_XRGB32_REGION_NOBLEND |
		PencilCapabilities.DRAW_XRGB32_SIMPLE_NOBLEND |
		PencilCapabilities.FILL_ARC_NOBLEND |
		PencilCapabilities.FILL_RECT_NOBLEND |
		PencilCapabilities.FILL_ROUNDRECT_NOBLEND |
		PencilCapabilities.FILL_TRIANGLE_NOBLEND |
		PencilCapabilities.FONT_TEXT_NOBLEND;
	
	/** All capabilities that use blending, may be used as a mask. */
	long ALL_BLEND =
		PencilCapabilities.MINIMUM |
		PencilCapabilities.COPY_AREA_BLEND |
		PencilCapabilities.DRAW_ARC_DOT_BLEND |
		PencilCapabilities.DRAW_ARC_SOLID_BLEND |
		PencilCapabilities.DRAW_LINE_DOT_BLEND |
		PencilCapabilities.DRAW_LINE_SOLID_BLEND |
		PencilCapabilities.DRAW_RECT_DOT_BLEND |
		PencilCapabilities.DRAW_RECT_SOLID_BLEND |
		PencilCapabilities.DRAW_ROUNDRECT_DOT_BLEND |
		PencilCapabilities.DRAW_ROUNDRECT_SOLID_BLEND |
		PencilCapabilities.DRAW_XRGB16_REGION_BLEND |
		PencilCapabilities.DRAW_XRGB16_SIMPLE_BLEND |
		PencilCapabilities.DRAW_XRGB32_REGION_BLEND |
		PencilCapabilities.DRAW_XRGB32_SIMPLE_BLEND |
		PencilCapabilities.FILL_ARC_BLEND |
		PencilCapabilities.FILL_RECT_BLEND |
		PencilCapabilities.FILL_ROUNDRECT_BLEND |
		PencilCapabilities.FILL_TRIANGLE_BLEND |
		PencilCapabilities.FONT_TEXT_BLEND;
	
	/** Every possible capability, may be used as a mask. */
	long ALL =
		PencilCapabilities.ALL_NOBLEND |
		PencilCapabilities.ALL_BLEND; 
}
