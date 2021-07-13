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

import cc.squirreljme.runtime.lcdui.common.CommonColors;
import cc.squirreljme.runtime.lcdui.common.TextStorage;

/**
 * This text class is one which handles all of the text metrics and drawing and
 * such. It handles different fonts, colors, and styles on a per character
 * basis and performs all the needed operations to support text drawing.
 *
 * The color defaults to {@code Display.getColor(Display.COLOR_FOREGROUND)}.
 *
 * @since 2018/11/29
 */
public class Text
{
	public static final int ALIGN_CENTER =
		1;
	
	public static final int ALIGN_DEFAULT =
		4;
	
	public static final int ALIGN_JUSTIFY =
		3;
	
	public static final int ALIGN_LEFT =
		0;
	
	public static final int ALIGN_RIGHT =
		2;
	
	public static final int DIRECTION_LTR =
		10;
	
	public static final int DIRECTION_NEUTRAL =
		12;
	
	public static final int DIRECTION_RTL =
		11;
	
	/** Storage for the text. */
	private final TextStorage _storage =
		new TextStorage();
	
	/** The width. */
	private int _width;
	
	/** The height. */
	private int _height;
	
	/** The default font. */
	private Font _defaultfont =
		Font.getDefaultFont();
	
	/** The default foreground color. */
	private int _defaultcolor =
		CommonColors.DEFAULT_TEXT_COLOR;
	
	/** The background color. */
	private int _backgroundcolor =
		CommonColors.DEFAULT_TEXT_BACKGROUND_COLOR;
	
	/** The alignment. */
	private int _alignment;
	
	/** The caret position. */
	private int _caret =
		-1;
	
	/** The required display height, is cached. */
	private int _requiredheight;
	
	/** The required line count, is cached. */
	private int _requiredlines;
	
	/** Space above each line. */
	private int _spaceabove;
	
	/** Space below each line. */
	private int _spacebelow;
	
	/** The alignment of each line. */
	private int _align;
	
	/** Indentation. */
	private int _indentation;
	
	/** The direction of the text. */
	private int _direction = Text.DIRECTION_NEUTRAL;
	
	/** Scrolling offset (scrolled up by n pixels). */
	private int _scrolloffset;
	
	/** The highlight index. */
	private int _highlightdx =
		-1;
	
	/** The highlight length. */
	private int _highlightlen =
		0;
	
	/** Does character placement have to be updated. */
	private boolean _dirty;
	
	/**
	 * Initializes the text with no width or height.
	 *
	 * @since 2018/11/29
	 */
	public Text()
	{
		this("", 0, 0);
	}
	
	/**
	 * Initializes the text with the given width and height.
	 *
	 * @param __c The text to use.
	 * @param __w The width.
	 * @param __h The height.
	 * @throws IllegalArgumentException If the width or height are negative.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/29
	 */
	public Text(String __c, int __w, int __h)
		throws IllegalArgumentException, NullPointerException
	{
		this(__c, 0, __c.length(), __w, __h);
	}
	
	/**
	 * Initializes the text with the given width and height.
	 *
	 * @param __c The text to use.
	 * @param __o The offset.
	 * @param __l The length.
	 * @param __w The width.
	 * @param __h The height.
	 * @throws IllegalArgumentException If the width or height are negative.
	 * @throws IndexOutOfBoundsException If the offset and/or length are
	 * negative or exceed the array bounds.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/29
	 */
	public Text(String __c, int __o, int __l, int __w, int __h)
		throws IllegalArgumentException, IndexOutOfBoundsException,
			NullPointerException
	{
		if (__c == null)
			throw new NullPointerException("NARG");
		if (__o < 0 || __l < 0 || (__o + __l) > __c.length())
			throw new IndexOutOfBoundsException("IOOB");
		
		// {@squirreljme.error EB2p The width and height cannot be negative
		// for text.}
		if (__w < 0 || __h < 0)
			throw new IllegalArgumentException("EB2p");
		
		// Set sizes first
		this._width = __w;
		this._height = __h;
		
		// Insert all of the text
		this.insert(0, __c.substring(__o, __o + __l));
	}
	
	/**
	 * Deletes the specified text.
	 *
	 * @param __i The index to start deletion at.
	 * @param __l The number of characters to delete.
	 * @throws IndexOutOfBoundsException If the index is out of bounds.
	 * @since 2018/12/02
	 */
	public void delete(int __i, int __l)
		throws IndexOutOfBoundsException
	{
		// Perform the delete
		this._storage.delete(__i, __l);
		
		// Deleting nothing?
		if (__l <= 0)
			return;
		
		// Mark dirty
		this._dirty = true;
		
		// Adjust the caret?
		int caret = this._caret;
		if (caret > __i)
			this._caret = caret - Math.max(__l - (caret - __i), __l);
	}
	
	/**
	 * Returns the alignment of the text.
	 *
	 * @return The text alignment.
	 * @since 2018/12/02
	 */
	public int getAlignment()
	{
		return this._alignment;
	}
	
	/**
	 * Returns the background color.
	 *
	 * @return The background color.
	 * @since 2018/12/02
	 */
	public int getBackgroundColor()
	{
		return this._backgroundcolor;
	}
	
	/**
	 * Returns the caret position.
	 *
	 * @return The caret position or {@code -1} if it is not used.
	 * @since 2018/12/02
	 */
	public int getCaret()
	{
		return this._caret;
	}
	
	/**
	 * Returns the extents of the given character.
	 *
	 * @param __i The character to get.
	 * @param __ext The extents of the character: x, y, width, height.
	 * @throws IllegalArgumentException If the extend array has a length less
	 * than four.
	 * @throws IndexOutOfBoundsException If the character index is out of
	 * bounds.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/12/01
	 */
	public void getCharExtent(int __i, int[] __ext)
		throws IllegalArgumentException, IndexOutOfBoundsException,
			NullPointerException
	{
		if (__ext == null)
			throw new NullPointerException("NARG");
			
		// Update
		if (this._dirty)
			this.__undirty();
		
		// Need to extract the character and font to determine the width and
		// the height of it
		TextStorage storage = this._storage;
		
		// Exceeds storage size?
		if (__i >= storage.size)
			throw new IndexOutOfBoundsException("IOOB");
		
		// Default font?
		Font font = storage.font[__i];
		if (font == null)
			font = this._defaultfont;
		
		// Set extents, the width and height come from the character data
		try
		{
			__ext[0] = storage.x[__i];
			__ext[1] = storage.y[__i];
			__ext[2] = font.charWidth(storage.chars[__i]);
			__ext[3] = font.getHeight();
		}
		catch (IndexOutOfBoundsException e)
		{
			// {@squirreljme.error EB2q Extent array length must at least
			// be 4.}
			if (__ext.length < 4)
				throw new IllegalArgumentException("EB2q");
		}
	}
	
	public int getCharIndex(int __x, int __y)
	{
		throw new todo.TODO();
	}
	
	/**
	 * Returns the default font.
	 *
	 * @return The default font.
	 * @since 2018/12/01
	 */
	public Font getFont()
	{
		return this._defaultfont;
	}
	
	/**
	 * Returns the font which is used by the given character.
	 *
	 * @param __i The index of the character to get.
	 * @return The font for the given index.
	 * @throws IndexOutOfBoundsException If the index it outside of bounds.
	 * @since 2018/12/01
	 */
	public Font getFont(int __i)
		throws IndexOutOfBoundsException
	{
		TextStorage storage = this._storage;
		
		// Exceeds storage size?
		if (__i >= storage.size)
			throw new IndexOutOfBoundsException("IOOB");
		
		// Only if a font is set
		Font rv = storage.font[__i];
		if (rv != null)
			return rv;
		return this._defaultfont;
	}
	
	/**
	 * Returns the default foreground color.
	 *
	 * @return The default foreground color.
	 * @since 2018/12/02
	 */
	public int getForegroundColor()
	{
		return this._defaultcolor;
	}
	
	/**
	 * Gets the foreground color for a character.
	 *
	 * @param __i The character to get.
	 * @return The color for that character.
	 * @since 2018/12/01
	 */
	public int getForegroundColor(int __i)
		throws IndexOutOfBoundsException
	{
		TextStorage storage = this._storage;
		
		// Exceeds storage size?
		if (__i >= storage.size)
			throw new IndexOutOfBoundsException("IOOB");
		
		// Zero means that the default color is to be used
		int rv = storage.color[__i];
		if (rv != 0)
			return rv;
		return this._defaultcolor;
	}
	
	/**
	 * Returns the height of the text.
	 *
	 * @return The height of the text.
	 * @since 2018/12/01
	 */
	public int getHeight()
	{
		return this._height;
	}
	
	/**
	 * Returns the highlight index.
	 *
	 * @return The highlight index or {@code -1} if none.
	 * @since 2018/12/02
	 */
	public int getHighlightIndex()
	{
		return this._highlightdx;
	}
	
	/**
	 * Returns the length of highlighted characters.
	 *
	 * @return The number of characters highlighted, {@code 0} if none.
	 * @since 2018/12/02
	 */
	public int getHighlightLength()
	{
		return this._highlightlen;
	}
	
	/**
	 * Returns the current indentation.
	 *
	 * @return The indentation.
	 * @since 2018/12/02
	 */
	public int getIndent()
	{
		return this._indentation;
	}
	
	/**
	 * Returns the initial direction.
	 *
	 * @return The initial direction.
	 * @since 2018/12/02
	 */
	public int getInitialDirection()
	{
		return this._direction;
	}
	
	/**
	 * Returns the height which is required to completely display all of the
	 * text within.
	 *
	 * @return The required height to draw.
	 * @since 2018/12/01
	 */
	public int getRequiredHeight()
	{
		// Update
		if (this._dirty)
			this.__undirty();
		
		return this._requiredheight;
	}
	
	/**
	 * Returns the number of lines which are required to display all of the
	 * text within.
	 *
	 * @return The number of lines which are required.
	 * @since 2018/12/01
	 */
	public int getRequiredLineCount()
	{
		// Update
		if (this._dirty)
			this.__undirty();
		
		return this._requiredlines;
	}
	
	/**
	 * Returns the scroll offset.
	 *
	 * @return The scroll offset.
	 * @since 2018/12/02
	 */
	public int getScrollOffset()
	{
		return this._scrolloffset;
	}
	
	/**
	 * Returns the space above each line.
	 *
	 * @return The space above.
	 * @since 2018/12/02
	 */
	public int getSpaceAbove()
	{
		return this._spaceabove;
	}
	
	/**
	 * Returns the space below each line.
	 *
	 * @return The space below.
	 * @since 2018/12/02
	 */
	public int getSpaceBelow()
	{
		return this._spacebelow;
	}
	
	/**
	 * Returns the text contained within this object.
	 *
	 * @param __i The starting index.
	 * @param __l The length.
	 * @return The string for the text.
	 * @throws IndexOutOfBoundsException If the index and/or length exceed
	 * the text bounds.
	 * @since 2018/12/01
	 */
	public String getText(int __i, int __l)
		throws IndexOutOfBoundsException
	{
		TextStorage storage = this._storage;
		
		// {@squirreljme.error EB2r Text outside of bounds. (The starting
		// index; The ending index; The size of the text)}
		int size = storage.size;
		if (__i < 0 || __l < 0 || (__i + __l) > size)
			throw new IndexOutOfBoundsException(
				String.format("EB2r %d %d %d", __i, __l, size));
		
		// Create string from it
		return new String(storage.chars, __i, __l);
	}
	
	/**
	 * Returns the length of this text object.
	 *
	 * @return The length of the text object.
	 * @since 2018/12/01
	 */
	public int getTextLength()
	{
		return this._storage.size;
	}
	
	/**
	 * Returns the width of this text.
	 *
	 * @return The text width.
	 * @since 2018/12/01
	 */
	public int getWidth()
	{
		return this._width;
	}
	
	/**
	 * Inserts the given string at the position.
	 *
	 * @param __i The index to insert at, the index is always forced within
	 * the bounds of the buffer (negative values are inserted at zero and
	 * positions greater than the size are inserted at the end).
	 * @param __s The stirng to index.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/30
	 */
	public void insert(int __i, String __s)
		throws NullPointerException
	{
		if (__s == null)
			throw new NullPointerException("NARG");
		
		// Ignore adding empty strings as there is no purpose to it
		if (__s.isEmpty())
			return;
		
		// The index is always in the bounds of the storage
		TextStorage storage = this._storage;
		if (__i < 0)
			__i = 0;
		else if (__i > storage.size)
			__i = storage.size;
		
		// Insert space to store the characters
		int sn = __s.length();
		storage.insert(__i, sn);
		
		// Set character data here
		char[] chars = storage.chars;
		for (int i = 0; i < sn; i++)
			chars[__i++] = __s.charAt(i);
		
		// Adjust the caret?
		int caret = this._caret;
		if (caret >= 0 && __i < caret)
			this._caret = caret + sn;
		
		// Becomes dirty
		this._dirty = true;
	}
	
	public int lastRenderedIndex()
	{
		throw new todo.TODO();
	}
	
	public void moveCaret(int __nl)
	{
		throw new todo.TODO();
	}
	
	/**
	 * Sets the alignment of the text.
	 *
	 * @param __a If the alignment is not valid.
	 * @throws IllegalArgumentException If the alignment is not valid.
	 * @since 2018/12/02
	 */
	public void setAlignment(int __a)
		throws IllegalArgumentException
	{
		// {@squirreljme.error EB2s Invalid alignment. (The alignment)}
		if (__a != Text.ALIGN_LEFT && __a != Text.ALIGN_CENTER && __a != Text.ALIGN_RIGHT &&
			__a != Text.ALIGN_JUSTIFY && __a != Text.ALIGN_DEFAULT)
			throw new IllegalArgumentException("EB2s " + __a);
		
		this._alignment = __a;
	}
	
	/**
	 * Sets the background color.
	 *
	 * @param __argb The color to use.
	 * @since 2018/12/02
	 */
	public void setBackgroundColor(int __argb)
	{
		this._backgroundcolor = __argb;
	}
	
	/**
	 * Sets the position of the caret.
	 *
	 * @param __i The position to use, {@code -1} clears the caret
	 * @throws IndexOutOfBoundsException If the caret is outside of the
	 * text bounds.
	 * @since 2018/12/02
	 */
	public void setCaret(int __i)
		throws IndexOutOfBoundsException
	{
		// Clear it
		if (__i == -1)
		{
			this._caret = -1;
			return;
		}
		
		throw new todo.TODO();
	}
	
	/**
	 * Sets the default font to use when no font has been specified.
	 *
	 * @param __f The default font to use.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/30
	 */
	public void setFont(Font __f)
		throws NullPointerException
	{
		if (__f == null)
			throw new NullPointerException("NARG");
		
		// If this is the same font, just ignore it
		Font olddefaultfont = this._defaultfont;
		if (__f.equals(olddefaultfont))
			return;
		
		// Changing the font becomes dirty since we need to measure the
		// metrics again
		this._defaultfont = __f;
		this._dirty = true;
	}
	
	/**
	 * Sets the font at the given positions.
	 *
	 * @param __f The font to set, {@code null} clears.
	 * @param __i The index.
	 * @param __l The length.
	 * @throws IndexOutOfBoundsException If the given range is out of bounds.
	 * @since 2018/12/02
	 */
	public void setFont(Font __f, int __i, int __l)
	{
		TextStorage storage = this._storage;
		
		// Exceeds storage size?
		int size = storage.size;
		if (__i < 0 || __l < 0 || __i >= size || (__i + __l) > size)
			throw new IndexOutOfBoundsException("IOOB");
		
		// Set
		Font[] font = storage.font;
		for (int i = 0; i < __l; i++)
			font[__i++] = __f;
		
		// Is dirty now
		this._dirty = true;
	}
	
	/**
	 * Sets the default foreground color to use when drawing.
	 *
	 * @param __argb The color to use.
	 * @since 2018/12/01
	 */
	public void setForegroundColor(int __argb)
	{
		// This does not dirty anything because it is just a color change,
		// nothing needs to be recomputed
		this._defaultcolor = __argb;
	}
	
	/**
	 * Sets the color at the given positions.
	 *
	 * @param __argb The ARGB color, zero removes the color.
	 * @param __i The index.
	 * @param __l The length.
	 * @throws IndexOutOfBoundsException If the given range is out of bounds.
	 * @since 2018/12/02
	 */
	public void setForegroundColor(int __argb, int __i, int __l)
	{
		TextStorage storage = this._storage;
		
		// Exceeds storage size?
		int size = storage.size;
		if (__i < 0 || __l < 0 || __i >= size || (__i + __l) > size)
			throw new IndexOutOfBoundsException("IOOB");
		
		// Set
		int[] color = storage.color;
		for (int i = 0; i < __l; i++)
			color[__i++] = __argb;
		
		// Is dirty now
		this._dirty = true;
	}
	
	/**
	 * Sets the height of the text.
	 *
	 * @param __h The new height.
	 * @throws IllegalArgumentException If the height is negative.
	 * @since 2018/12/01
	 */
	public void setHeight(int __h)
		throws IllegalArgumentException
	{
		// {@squirreljme.error EB2t The height of a font cannot be
		// negative.}
		if (__h < 0)
			throw new IllegalArgumentException("EB2t");
		
		// Just set the height, we do not need to clear the dirty bit because
		// as long as the requiredheight is still within the height the text
		// fits
		this._height = __h;
	}
	
	/**
	 * Sets the highlight position.
	 *
	 * @param __i The starting index, {@code -1} clears the highlight.
	 * @param __l The number of characters to highlight.
	 * @throws IndexOutOfBoundsException If the index and/or length are out
	 * of bounds.
	 */
	public void setHighlight(int __i, int __l)
		throws IndexOutOfBoundsException
	{
		// Clear it
		if (__i == -1)
		{
			this._highlightdx = -1;
			this._highlightlen = 0;
			return;
		}
		
		throw new todo.TODO();
	}
	
	/**
	 * Sets the indentation.
	 *
	 * @param __i The indentation.
	 * @since 2018/12/02
	 */
	public void setIndent(int __i)
	{
		this._indentation = __i;
	}
	
	/**
	 * Sets the initial direction.
	 *
	 * @param __dir The initial direction.
	 * @throws IllegalArgumentException If the direction is not valid.
	 * @since 2018/12/02
	 */
	public void setInitialDirection(int __dir)
		throws IllegalArgumentException
	{
		// {@squirreljme.error EB2u The direction to use. (The direction)}
		if (__dir != Text.DIRECTION_LTR && __dir != Text.DIRECTION_RTL &&
			__dir != Text.DIRECTION_NEUTRAL)
			throw new IllegalArgumentException("EB2u " + __dir);
		
		this._direction = __dir;
	}
	
	/**
	 * Sets the scroll offset.
	 *
	 * @param __o The offset.
	 * @since 2018/12/02
	 */
	public void setScrollOffset(int __o)
	{
		this._scrolloffset = __o;
	}
	
	/**
	 * Sets the space above each line.
	 *
	 * @param __sa The space above in pixels.
	 * @since 2018/12/02
	 */
	public void setSpaceAbove(int __sa)
	{
		this._spaceabove = __sa;
	}
	
	/**
	 * Sets the space below each line.
	 *
	 * @param __sb The space below in pixels.
	 * @since 2018/12/02
	 */
	public void setSpaceBelow(int __sb)
	{
		this._spacebelow = __sb;
	}
	
	/**
	 * Sets the width of this text to the specified width.
	 *
	 * @param __w The width to set.
	 * @throws IllegalArgumentException If the width is negative.
	 * @since 2018/11/30
	 */
	public void setWidth(int __w)
		throws IllegalArgumentException
	{
		// {@squirreljme.error EB2v Cannot set the width to a negative value.}
		if (__w < 0)
			throw new IllegalArgumentException("EB2v");
		
		// Ignore on no changes
		int oldwidth = this._width;
		if (__w == oldwidth)
			return;
		
		// Set and mark dirty
		this._width = __w;
		this._dirty = true;
	}
	
	/**
	 * Returns whether all of the text fits within the bounds of the box.
	 *
	 * @return If all of the text fits within the bounds of the box.
	 * @since 2018/12/01
	 */
	public boolean textFits()
	{
		// Update
		if (this._dirty)
			this.__undirty();
		
		// The text will fit if the height needed to display everything is at
		// or below the height of the actual box. This is to make height
		// changes not require a recalculate.
		return this._requiredheight <= this._height;
	}
	
	/**
	 * Undirties the text within and calculates all of the needed bounds and
	 * metrics for each character.
	 *
	 * @since 2018/12/01
	 */
	private final void __undirty()
	{
		if (!this._dirty)
			return;
			
		// Undirty state
		__TextUndirtyState__ ts = new __TextUndirtyState__();
		
		// Using this global stuff
		ts.defaultfont = this._defaultfont;
		ts.width = this._width;
		ts.spaceabove = this._spaceabove;
		ts.spacebelow = this._spacebelow;
		ts.align = this._align;
		ts.indentation = this._indentation;
		ts.direction = this._direction;
		ts.scrolloffset = this._scrolloffset;
		
		// If the direction is neutral, this just becomes the locale default
		// For now just treat it as LTR
		if (ts.direction == Text.DIRECTION_NEUTRAL)
			ts.direction = Text.DIRECTION_LTR;
		
		// Are we going right to left?
		boolean dortl = (ts.direction == Text.DIRECTION_RTL);
		
		// Will use this storage stuff
		TextStorage storage = this._storage;
		char[] chars = storage.chars;
		Font[] font = storage.font;
		short[] cx = storage.x;
		short[] cy = storage.y;
		
		// The starting Y position is 
		// The starting X and Y position is always zero, when other alignments
		// and such are used they are calculated when the line ends
		// X is offset by the indentation and Y is offset by the scrolling
		int y = -ts.scrolloffset + ts.spaceabove,
			nexty = y;
		
		// X starts with indentation, but that might be modified in right
		// to left mode
		int x = (dortl ? ts.width : ts.indentation),
			startx = x;
		
		// Cache parameters of font
		Font lastfont = null;
		int fontheight = 0;
		int fontascent = 0;
		int fontdescent = 0;
		
		// For the end of line calculator, these are the indexes which are
		// used for each character
		int linedxstart = 0;
		int linedxend = 0;
		
		// Redo handling of the current character, this will happen if
		// the line overflows
		boolean redo = false;
		
		// Go through and calculate every character line by line, carefully
		// handling alignment and justification
		// The line height is calculated so that if different fonts of
		// different sizes are on the same line, they all are on the baseline
		int linecount = 0;
		int linemaxheight = 0;
		int linemaxascent = 0;
		int linemaxdescent = 0;
		for (int i = 0, n = storage.size; i <= n; i++)
		{
			// Since we need to handle line indentation, justification and
			// otherwise we need a flag to know when the next line was hit
			// to calculate
			// But the last character always has line stuff done to handle the
			// final alignment/directions and such
			// For now only the X positions are considered because
			boolean donextline = (i == n),
				wasnewlinech = false;
			if (!donextline)
			{
				// Need the character and font here, for metrics
				char ch = chars[i];
				Font f = font[i];
				if (f == null)
					f = ts.defaultfont;
				
				// Font has changed?
				if (lastfont != f)
				{
					// Cache parameters
					fontheight = f.getHeight();
					fontascent = f.getAscent();
					fontdescent = f.getDescent();
					
					// Keep track so we do not need to update every time
					lastfont = f;
					
					// Properties of the line changed due to the font?
					// Cache them and check here accordingly
					if (fontascent > linemaxascent)
						linemaxascent = fontascent;
					if (fontheight > linemaxheight)
						linemaxheight = fontheight;
					if (fontdescent > linemaxdescent)
						linemaxdescent = fontdescent;
				}
				
				// Ignore carriage returns
				if (ch == '\r')
					continue;
				
				// Newlines do go to the next line, if a newline was detected
				// then the X position will be set to indentation, otherwise
				// zero.
				else if (ch == '\n')
				{
					donextline = true;
					wasnewlinech = true;
					
					// Clear a redo
					redo = false;
				}
				
				// Draw every other character
				else
				{
					// Get the properties of this character
					int chw = f.charWidth(ch);
					
					// Calculate draw position and the next X position
					// accordingly
					int dx, nx;
					if (dortl)
					{
						dx = x - chw;
						nx = dx;
					}
					else
					{
						dx = x;
						nx = x + chw;
					}
					
					// Character is still within the bounds?
					// Additionally if the X coordinate is at the start and
					// cannot even fit in the width just force it to be
					// placed
					if ((nx >= 0 && nx <= ts.width) ||
						(x <= startx && x + chw <= ts.width))
					{
						// Store current X position, this may change due to
						// right to left mode
						cx[i] = (short)dx;
						
						// Next character will be here
						x = nx;
						
						// Store the ascent of the character in the Y slot,
						// this is later used at end of line handler to
						// correctly place each character on the baseline
						cy[i] = (short)fontascent;
						
						// Clear a redo
						redo = false;
					}
					
					// Character exceeds the bounds, need to redo and handle
					// end of line
					else
					{
						// Redo should not be triggered twice, so just
						// clear it and never bother again
						if (redo)
						{
							redo = false;
							continue;
						}
						
						donextline = true;
						redo = true;
					}
				}
			}
			
			// End of line reached, handle alignment, justification, etc.
			// Perform any position updates as needed
			if (donextline)
			{
				// The line ends on this index (this is either strlen, a
				// newline, or a character placed on a newline so this is
				// always going to be exclusive)
				linedxend = i;
				
				// The next Y position is going to be the max font height
				// for this line.
				// An extra space above is only added if this was a newline,
				// so that way the next line has the actual space above
				nexty = y + linemaxheight + ts.spacebelow +
					(wasnewlinech ? ts.spaceabove : 0);
				
				// Calculate the correct Y position for each character
				for (int q = linedxstart; q < linedxend; q++)
				{
					// Get the original ascent of the character
					int origascent = cy[q];
					
					// The Y position is just the difference in space between
					// the line's max ascent and the character's actual ascent
					cy[q] = (short)(y + (linemaxascent - origascent));
				}
				
				// Handle non-default alignments
				if ((dortl && ts.align != Text.ALIGN_RIGHT) ||
					(!dortl && ts.align != Text.ALIGN_LEFT))
				{
					throw new todo.TODO();
				}
				
				// A line was here so the line count goes up
				linecount++;
				
				// If redoing this character, decrement i so that way it
				// negates the loop increment
				if (redo)
					i--;
				
				// The new start index
				linedxstart = i + 1;
				
				// Set the previously calculated Y position
				y = nexty;
				
				// Move X back
				x = startx;
			}
		}
		
		// Update other needed parameters
		// The required height is our nexty because either at the end or
		// a newline this will always be set
		this._requiredheight = nexty;
		this._requiredlines = linecount;
			
		// Has been updated, no longer dirty
		this._dirty = false;
	}
}

