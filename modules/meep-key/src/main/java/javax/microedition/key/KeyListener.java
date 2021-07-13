// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package javax.microedition.key;

public interface KeyListener
{
	int DOWN =
		6;
		
	int KEY_BACKSPACE =
		8;
		
	int KEY_DELETE =
		127;
		
	int KEY_DOWN =
		-2;
		
	int KEY_ENTER =
		10;
		
	int KEY_ESCAPE =
		27;
		
	int KEY_LEFT =
		-3;
		
	int KEY_NUM0 =
		48;
		
	int KEY_NUM1 =
		49;
		
	int KEY_NUM2 =
		50;
		
	int KEY_NUM3 =
		51;
		
	int KEY_NUM4 =
		52;
		
	int KEY_NUM5 =
		53;
		
	int KEY_NUM6 =
		54;
		
	int KEY_NUM7 =
		55;
		
	int KEY_NUM8 =
		56;
		
	int KEY_NUM9 =
		57;
		
	int KEY_POUND =
		35;
		
	int KEY_RIGHT =
		-4;
		
	int KEY_SELECT =
		-5;
		
	int KEY_SPACE =
		32;
		
	int KEY_STAR =
		42;
		
	int KEY_TAB =
		9;
		
	int KEY_UP =
		-1;
		
	int LEFT =
		2;
	
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
		
	int RIGHT =
		5;
		
	int UP =
		1;

	void keyPressed(InputDevice __dev, int __code, int __mod);

	void keyReleased(InputDevice __dev, int __code, int __mod);

	void keyRepeated(InputDevice __dev, int __code, int __mod);
}

