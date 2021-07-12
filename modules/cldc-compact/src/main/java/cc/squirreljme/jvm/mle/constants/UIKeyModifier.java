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
 * Modifier keys for when key and mouse events occur.
 *
 * @since 2020/08/02
 */
public interface UIKeyModifier
{
	/** Alt key modifier. */
	int MODIFIER_ALT =
		0x10000;
	
	/** Shift key modifier. */
	int MODIFIER_SHIFT =
		0x20000;
	
	/** Control (Ctrl) key modifier. */
	int MODIFIER_CTRL =
		0x40000;
	
	/** Left Command Button. (SquirrelJME). */
	int MODIFIER_LEFT_COMMAND =
		0x80000;
	
	/** Right Command button. (SquirrelJME). */
	int MODIFIER_RIGHT_COMMAND =
		0x100000;
	
	/** Command key modifier. */
	int MODIFIER_COMMAND =
		0x400000;
		
	/** The Function key. */
	int MODIFIER_FUNCTION =
		0x800000;
	
	/** Left and right commands, used for middle command. */
	int MODIFIER_LEFT_RIGHT_COMMANDS =
		UIKeyModifier.MODIFIER_LEFT_COMMAND |
		UIKeyModifier.MODIFIER_RIGHT_COMMAND;
		
	/** Mask specifically for the limits of J2ME. */
	int J2ME_MASK =
		UIKeyModifier.MODIFIER_ALT |
		UIKeyModifier.MODIFIER_FUNCTION | UIKeyModifier.MODIFIER_COMMAND |
		UIKeyModifier.MODIFIER_CTRL | UIKeyModifier.MODIFIER_SHIFT;
	
	/** Mask for all the modifier keys. */
	int MASK =
		UIKeyModifier.J2ME_MASK |
		UIKeyModifier.MODIFIER_LEFT_RIGHT_COMMANDS;
}
