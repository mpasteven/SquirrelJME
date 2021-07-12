// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package javax.microedition.lcdui;

import javax.microedition.lcdui.game.Sprite;

/**
 * The class describes the interface that is used for drawing operations.
 *
 * When the clipping area is used, no pixels outside of it are drawn. This may
 * be used to draw special effects or have similar maskings.
 *
 * The anchor points {@link #BASELINE}, {@link #BOTTOM}, {@link #HCENTER},
 * {@link #LEFT}, {@link #RIGHT}, {@link #TOP}, and {@link #VCENTER} modify how
 * text and images are placed by allowing their placement positions to be
 * shifted accordingly.
 *
 * The default blending mode must be {@link #SRC_OVER}.
 *
 * An alpha value of {@code 255} means fully opaque (visible) while a value of
 * {@code 0} means transparent (invisible).
 *
 * @since 2017/02/09
 */
public abstract class Graphics
{
	/**
	 * This is the anchorpoint for the baseline of text. This is not valid for
	 * anything which is not text. The baseline is considered to be point where
	 * all letters rest on. The baseline is not the lowest point, so for
	 * letters such as {@code j} the baseline will be higher than the lowest
	 * point.
	 */
	public static final int BASELINE =
		64;
	
	/** The anchor point to position below the specified point. */
	public static final int BOTTOM =
		32;
	
	/** Dotted stroke line style. */
	public static final int DOTTED =
		1;
	
	/** The anchor point to position in the center horizontally. */
	public static final int HCENTER =
		1;
	
	/** The anchor point to position the item to the left. */
	public static final int LEFT =
		4;
	
	/** The anchor point to position the item on the right. */
	public static final int RIGHT =
		8;
	
	/** Solid stroke line style. */
	public static final int SOLID =
		0;
	
	/**
	 * A blending mode, the destination alpha becomes the source and as such
	 * the operation is a copy.
	 */
	public static final int SRC =
		1;
	
	/**
	 * A blending mode, the source alpha is a composited over the
	 * destination.
	 */
	public static final int SRC_OVER =
		0;
	
	/** The anchor point to position the item on the top. */
	public static final int TOP =
		16;
	
	/** The anchor point to position in the center vertically. */
	public static final int VCENTER =
		2;
	
	/**
	 * Base initialization of graphics sub-class.
	 *
	 * Note that extending this class is specific to SquirrelJME and that
	 * doing so will cause programs to only run on SquirrelJME.
	 *
	 * @since 2016/10/10
	 */
	protected Graphics()
	{
	}
	
	/**
	 * This reduces the clipping area of the drawing so that 
	 *
	 * This is only used to reduce the clipping area, to make it larger use
	 * {@link Graphics#setClip(int, int, int, int)}.
	 *
	 * @param __x The X coordinate of the clipping rectangle,
	 * will be translated.
	 * @param __y The Y coordinate of the clipping rectangle,
	 * will be translated.
	 * @param __w The width of the rectangle.
	 * @param __h The height of the rectangle.
	 * @since 2017/02/10
	 */
	public abstract void clipRect(int __x, int __y, int __w, int __h);
	
	/**
	 * This copies one region of the image to another region.
	 *
	 * Copying to a display device is not permitted because it may impact how
	 * double buffering is implemented, as such it is not supported.
	 *
	 * Pixels are copied directly and no alpha compositing is performed.
	 *
	 * If the source and destination overlap then it must be as if they did not
	 * overlap at all, this means that the destination will be an exact copy of
	 * the source.
	 *
	 * @param __sx The source X position, will be translated.
	 * @param __sy The source Y position, will be translated.
	 * @param __w The width to copy.
	 * @param __h The height to copy.
	 * @param __dx The destination X position, will be translated.
	 * @param __dy The destination Y position, will be translated.
	 * @param __anchor The anchor point of the destination.
	 * @throws IllegalArgumentException If the source region exceeds the size
	 * of the source image.
	 * @throws IllegalStateException If the destination is a display device.
	 * @since 2017/02/10
	 */
	public abstract void copyArea(int __sx, int __sy, int __w, int __h,
		int __dx, int __dy, int __anchor)
		throws IllegalArgumentException, IllegalStateException;
	
	/**
	 * This draws the outer edge of the ellipse from the given angles using
	 * the color, alpha, and stroke style.
	 *
	 * The coordinates are treated as if they were in a rectangular region. As
	 * such the center of the ellipse to draw the outline of is in the center
	 * of the specified rectangle.
	 *
	 * Note that no lines are drawn to the center point, so the shape does not
	 * result in a pie slice.
	 *
	 * The angles are in degrees and visually the angles match those of the
	 * unit circle correctly transformed to the output surface. As such, zero
	 * degrees has the point of {@code (__w, __h / 2)}, that is it points to
	 * the right. An angle at 45 degrees will always point to the top right
	 * corner.
	 *
	 * If the width or height are zero, then nothing is drawn. The arc will
	 * cover an area of {@code __w + 1} and {@code __h + 1}.
	 *
	 * @param __x The X position of the upper left corner, will be translated.
	 * @param __y The Y position of the upper left corner, will be translated.
	 * @param __w The width of the arc.
	 * @param __h The height of the arc.
	 * @param __sa The starting angle in degrees, 
	 * @param __aa The offset from the starting angle, negative values indicate
	 * clockwise direction while positive values are counter clockwise.
	 * @since 2017/02/10
	 */ 
	public abstract void drawArc(int __x, int __y, int __w, int __h, int __sa,
		int __aa);
	
	public abstract void drawARGB16(short[] __data, int __off, int __scanlen,
		int __x, int __y, int __w, int __h)
		throws NullPointerException;
	
	public abstract void drawChar(char __s, int __x, int __y, int __anchor);
	
	public abstract void drawChars(char[] __s, int __o, int __l, int __x,
		int __y, int __anchor)
		throws NullPointerException;
	
	/**
	 * Draws the specified image.
	 *
	 * If this graphics object draws onto the source image then the result is
	 * undefined, {@link #copyArea(int, int, int, int, int, int, int)} should
	 * be used instead.
	 *
	 * @param __i The source image.
	 * @param __x The X position to draw at, is translated.
	 * @param __y The Y position to draw at, is translated.
	 * @param __anchor The anchor point of the image.
	 * @throws IllegalArgumentException If the anchor point is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/02/11
	 */
	public abstract void drawImage(Image __i, int __x, int __y, int __anchor)
		throws IllegalArgumentException, NullPointerException;
	
	/**
	 * Draws a line using the current color and stroke style.
	 *
	 * @param __x1 Starting X position.
	 * @param __y1 Starting Y position.
	 * @param __x2 Ending X position.
	 * @param __y2 Ending Y position.
	 * @since 2017/02/11
	 */
	public abstract void drawLine(int __x1, int __y1, int __x2, int __y2);
	
	public abstract void drawRGB(int[] __data, int __off, int __scanlen,
		int __x, int __y, int __w, int __h, boolean __alpha)
		throws NullPointerException;
	
	public abstract void drawRGB16(short[] __data, int __off, int __scanlen,
		int __x, int __y, int __w, int __h)
		throws NullPointerException;
	
	public abstract void drawRect(int __x, int __y, int __w, int __h);
	
	/**
	 * Same as {@code drawRegion(__src, __xsrc __ysrc, __wsrc, __hsrc, __trans,
	 * __xdest, __ydest, __anch, __wsrc, __hsrc);}.
	 *
	 * @param __src The source image.
	 * @param __xsrc The source X position.
	 * @param __ysrc The source Y position.
	 * @param __wsrc The width of the source region.
	 * @param __hsrc The height of the source region.
	 * @param __trans Sprite translation and/or rotation, see {@link Sprite}.
	 * @param __xdest The destination X position, is translated..
	 * @param __ydest The destination Y position, is translated..
	 * @param __anch The anchor point.
	 * @throws IllegalArgumentException If the source is the destination
	 * image; the source region exceeds the image bounds; the sprite
	 * transformation is not valid; The anchor is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/02/11 
	 */
	public abstract void drawRegion(Image __src, int __xsrc, int __ysrc,
		int __wsrc, int __hsrc, int __trans, int __xdest, int __ydest,
		int __anch)
		throws IllegalArgumentException, NullPointerException;
	
	/**
	 * Draws the specified region of the given image with potential scaling
	 * and transformations.
	 *
	 * @param __src The source image.
	 * @param __xsrc The source X position.
	 * @param __ysrc The source Y position.
	 * @param __wsrc The width of the source region.
	 * @param __hsrc The height of the source region.
	 * @param __trans Sprite translation and/or rotation, see {@link Sprite}.
	 * @param __xdest The destination X position, is translated..
	 * @param __ydest The destination Y position, is translated..
	 * @param __anch The anchor point.
	 * @param __wdest The destination width.
	 * @param __hdest The destination height.
	 * @throws IllegalArgumentException If the source is the destination
	 * image; the source region exceeds the image bounds; the sprite
	 * transformation is not valid; The anchor is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/02/11 
	 */
	public abstract void drawRegion(Image __src, int __xsrc, int __ysrc,
		int __wsrc, int __hsrc, int __trans, int __xdest, int __ydest,
		int __anch, int __wdest, int __hdest)
		throws IllegalArgumentException, NullPointerException;
	
	public abstract void drawRoundRect(int __x, int __y, int __w, int __h,
		int __aw, int __ah);
	
	/**
	 * Same as {@code drawSubstring(__s, 0, __s.length(), __x, __y, __anchor)}.
	 *
	 * @param __s The string to draw.
	 * @param __x The X position, will be translated.
	 * @param __y The Y position, will be translated.
	 * @param __anchor The anchor point.
	 * @throws NullPointerException If no string was specified.
	 * @since 2017/02/10
	 */
	public abstract void drawString(String __s, int __x, int __y,
		int __anchor)
		throws NullPointerException;
	
	public abstract void drawSubstring(String __s, int __o, int __l, int __x,
		int __y, int __anchor)
		throws NullPointerException, StringIndexOutOfBoundsException;
	
	public abstract void drawText(Text __t, int __x, int __y);
	
	/**
	 * This draws the filled slice of an ellipse (like a pie slice) from the
	 * given angles using the color, alpha, and stroke style.
	 *
	 * Unlike {@link #drawArc(int, int, int, int, int, int)}, the width and
	 * height are not increased by a single pixel.
	 *
	 * Otherwise, this follows the same set of rules as
	 * {@link #drawArc(int, int, int, int, int, int)}.
	 *
	 * @param __x The X position of the upper left corner, will be translated.
	 * @param __y The Y position of the upper left corner, will be translated.
	 * @param __w The width of the arc.
	 * @param __h The height of the arc.
	 * @param __sa The starting angle in degrees, 
	 * @param __aa The offset from the starting angle, negative values indicate
	 * clockwise direction while positive values are counter clockwise.
	 * @see #drawArc(int, int, int, int, int, int)
	 * @since 2017/02/10
	 */
	public abstract void fillArc(int __x, int __y, int __w, int __h, int __sa,
		int __aa);
	
	public abstract void fillRect(int __x, int __y, int __w, int __h);
	
	public abstract void fillRoundRect(int __x, int __y, int __w, int __h,
		int __aw, int __ah);
	
	public abstract void fillTriangle(int __x1, int __y1, int __x2, int __y2,
		int __x3, int __y3);
	
	/**
	 * Returns the alpha component.
	 *
	 * @return The alpha in the range of {@code [0, 255]}.
	 * @since 2017/02/10
	 */
	public abstract int getAlpha();
	
	/**
	 * Returns the color along with the alpha color. 
	 *
	 * @return The color in the form of {@code @0xAARRGGBB}.
	 * @since 2017/02/10
	 */
	public abstract int getAlphaColor();
	
	/**
	 * Returns the blending mode.
	 *
	 * @return The current blending mode.
	 * @since 2017/02/10
	 */
	public abstract int getBlendingMode();
	
	/**
	 * Returns the blue component.
	 *
	 * @return The color in the range of {@code [0, 255]}.
	 * @since 2017/02/10
	 */
	public abstract int getBlueComponent();
	
	/**
	 * This returns the height of the clipping area.
	 *
	 * @return The clipping area height.
	 * @since 2017/02/10
	 */
	public abstract int getClipHeight();
	
	/**
	 * This returns the width of the clipping area.
	 *
	 * @return The clipping area width.
	 * @since 2017/02/10
	 */
	public abstract int getClipWidth();
	
	/**
	 * This returns the transformed X position of the clipping rectangle.
	 *
	 * @return The clipping area X coordinate, which is transformed.
	 * @since 2017/02/10
	 */
	public abstract int getClipX();
	
	/**
	 * This returns the transformed Y position of the clipping rectangle.
	 *
	 * @return The clipping area Y coordinate, which is transformed.
	 * @since 2017/02/10
	 */
	public abstract int getClipY();
	
	/**
	 * Returns the current color which has been set for drawing.
	 *
	 * @return The color in the form of {@code 0x00RRGGBB}, the upper bits
	 * for alpha will always be zero.
	 * @since 2017/02/10
	 */
	public abstract int getColor();
	
	/**
	 * This returns the actual color that would be drawn onto the given
	 * display if it were set.
	 *
	 * @param __rgb The color to use, the format is {@code 0xRRGGBB}.
	 * @return The color that will actually be drawn on the display.
	 * @since 2017/02/09
	 */
	public abstract int getDisplayColor(int __rgb);
	
	/**
	 * Returns the current font that is used for drawing characters.
	 *
	 * @return The current font that is used.
	 * @since 2017/02/10
	 */
	public abstract Font getFont();
	
	/**
	 * Returns the grayscale color component. If the current set color is not
	 * grayscale then it is unspecified how the grayscale color is derived (it
	 * may be a simple average or derived from display specific brightness
	 * values).
	 *
	 * @return The color in the range of {@code [0, 255]}.
	 * @since 2017/02/10
	 */
	public abstract int getGrayScale();
	
	/**
	 * Returns the green component.
	 *
	 * @return The color in the range of {@code [0, 255]}.
	 * @since 2017/02/10
	 */
	public abstract int getGreenComponent();
	
	/**
	 * Returns the red component.
	 *
	 * @return The color in the range of {@code [0, 255]}.
	 * @since 2017/02/10
	 */
	public abstract int getRedComponent();
	
	/**
	 * Returns the current stroke style for lines which are drawn.
	 *
	 * @return The current stroke style.
	 * @since 2017/02/10
	 */
	public abstract int getStrokeStyle();
	
	/**
	 * Returns the X coordinate of the translated coordinate system.
	 *
	 * @return The X coordinate of the translated coordinate system.
	 * @since 2017/02/10
	 */
	public abstract int getTranslateX();
	
	/**
	 * Returns the Y coordinate of the translated coordinate system.
	 *
	 * @return The Y coordinate of the translated coordinate system.
	 * @since 2017/02/10
	 */
	public abstract int getTranslateY();
	
	/**
	 * Sets the alpha value to use for drawing.
	 *
	 * @param __a The alpha value to use.
	 * @throws IllegalArgumentException If the value is not in the range of
	 * {@code [0, 255]}.
	 * @since 2017/02/10
	 */
	public abstract void setAlpha(int __a)
		throws IllegalArgumentException;
	
	/**
	 * Sets the alpha color to draw with along with the color to use.
	 *
	 * @param __argb The color in the form of {@code 0xAARRGGBB}.
	 * @since 2017/02/10
	 */
	public abstract void setAlphaColor(int __argb);
	
	/**
	 * Sets the color and alpha value to use for drawing.
	 *
	 * @param __a The alpha value.
	 * @param __r The red value.
	 * @param __g The green value.
	 * @param __b The blue value.
	 * @throws IllegalArgumentException If any component is not within the
	 * range of {@code [0, 255]}.
	 * @since 2017/02/09
	 */
	public abstract void setAlphaColor(int __a, int __r, int __g, int __b)
		throws IllegalArgumentException;
	
	/**
	 * Sets the blending mode of the drawing operations.
	 *
	 * @param __m The mode of drawing to use.
	 * @throws IllegalArgumentException If the mode is not valid.
	 * @since 2017/02/10
	 */
	public abstract void setBlendingMode(int __m)
		throws IllegalArgumentException;
	
	/**
	 * Sets the new clipping area of the destination image. The previous
	 * clipping area is replaced.
	 *
	 * @param __x The X coordinate, will be translated.
	 * @param __y The Y coordinate, will be translated.
	 * @param __w The width.
	 * @param __h The height.
	 * @since 2017/02/10
	 */
	public abstract void setClip(int __x, int __y, int __w, int __h);
	
	/**
	 * Sets the combined RGB value to use for drawing.
	 *
	 * @param __rgb The color to use, the format is {@code 0xRRGGBB}.
	 * @since 2017/02/09
	 */
	public abstract void setColor(int __rgb);
	
	/**
	 * Sets the color to use for drawing.
	 *
	 * @param __r The red value.
	 * @param __g The green value.
	 * @param __b The blue value.
	 * @throws IllegalArgumentException If any component is not within the
	 * range of {@code [0, 255]}.
	 * @since 2017/02/09
	 */
	public abstract void setColor(int __r, int __g, int __b)
		throws IllegalArgumentException;
	
	/**
	 * Sets the font to use for drawing operations.
	 *
	 * @param __font The font to use for drawing, if {@code null} then the
	 * default font is used.
	 * @since 2017/02/09
	 */
	public abstract void setFont(Font __font);
	
	/**
	 * Sets a grayscale color which has all the red, green, and blue
	 * components set as the same value.
	 *
	 * @param __v The value to use for the color.
	 * @since 2017/02/09
	 */
	public abstract void setGrayScale(int __v);
	
	/**
	 * Sets the stroke style to use for lines.
	 *
	 * @param __a The stroke style, either {@link #SOLID} or {@link #DOTTED}.
	 * @throws IllegalArgumentException If the stroke is not valid.
	 * @since 2017/02/09
	 */
	public abstract void setStrokeStyle(int __a)
		throws IllegalArgumentException;
	
	/**
	 * Translates all coordinates so that they are offset by the given
	 * values, a previous translation is translated by the given coordinates.
	 *
	 * To set the absolute position of the translation the following may be
	 * performed:
	 * {@code translate(ax - getTranslateX(), ay - getTranslateY())}.
	 *
	 * The clipping area, if set, will not be transformed.
	 *
	 * @param __x The X value to use for the new origin.
	 * @param __y The Y value to use for the new origin.
	 * @since 2017/02/09
	 */
	public abstract void translate(int __x, int __y);
}


