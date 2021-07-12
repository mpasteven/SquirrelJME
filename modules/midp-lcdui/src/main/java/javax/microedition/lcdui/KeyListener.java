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

/**
 * This is the key listener interface which is used for a slightly more
 * advanced means of listening for and responding to key events.
 *
 * Unlike {@link Canvas} and {@link CustomItem}, the key code that is passed
 * to these methods will be modified by the key modifiers (this means that
 * if shift is held down then letters will be uppercase).
 *
 * @since 2017/02/12
 */
public interface KeyListener
{
	/** Alt key modifier. */
	int MODIFIER_ALT =
		65536;
		
	/** Function (Fn/Chr) key modifier. */
	int MODIFIER_CHR =
		8388608;
	
	/** Command key modifier. */
	int MODIFIER_COMMAND =
		4194304;
	
	/** Ctrl key modifier. */
	int MODIFIER_CTRL =
		262144;
	
	/** Mask for all the modifier keys. */
	int MODIFIER_MASK =
		13041664;
	
	/** Shift key modifier. */
	int MODIFIER_SHIFT =
		131072;
	
	/**
	 * Called when a key is pressed.
	 *
	 * @param __kc The key code.
	 * @param __km The modifiers to the key.
	 * @since 2017/02/12
	 */
	void keyPressed(int __kc, int __km);
	
	/**
	 * Called when a key is released.
	 *
	 * @param __kc The key code.
	 * @param __km The modifiers to the key.
	 * @since 2017/02/12
	 */
	void keyReleased(int __kc, int __km);
	
	/**
	 * Called when a key is repeated.
	 *
	 * @param __kc The key code.
	 * @param __km The modifiers to the key.
	 * @since 2017/02/12
	 */
	void keyRepeated(int __kc, int __km);
}

