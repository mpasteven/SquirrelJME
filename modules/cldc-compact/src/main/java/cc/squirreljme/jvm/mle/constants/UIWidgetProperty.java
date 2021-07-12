// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.mle.constants;

import cc.squirreljme.jvm.mle.brackets.UIItemBracket;

/**
 * Integer and String properties for {@link UIItemBracket}.
 *
 * @since 2020/07/19
 */
public interface UIWidgetProperty
{
	/** Null property. */
	byte NULL =
		0;
	
	/** The label of the item. */
	byte STRING_LABEL =
		1;
	
	/** Widget width. */
	byte INT_WIDTH =
		2;
	
	/** Widget height. */
	byte INT_HEIGHT =
		3;
	
	/** Signals that a repaint should happen. */
	byte INT_SIGNAL_REPAINT =
		4;
	
	/** X position. */
	byte INT_X_POSITION =
		5;
	
	/** Y position. */
	byte INT_Y_POSITION =
		6;
	
	/** Is this shown? */
	byte INT_IS_SHOWN =
		7;
	
	/** Width and height in old+new. */
	byte INT_WIDTH_AND_HEIGHT =
		8;
	
	/** The type of {@link UIListType} to use. */
	byte INT_LIST_TYPE =
		9;
	
	/** The number of elements to appear on a list. */
	byte INT_NUM_ELEMENTS =
		10;
	
	/** Enable or get enabled for a given list item */
	byte INT_LIST_ITEM_DISABLED =
		11;
	
	/**
	 * Select or get select for a given list item. If the locking code is not
	 * the same, this is likely from another sequence.
	 * 
	 * @squirreljme.uiwidgetparam 1 The locking code, to check which list
	 * selection update sequence this is in.
	 * @squirreljme.uiwidgetparam 2 The selection state of the item.
	 */
	byte INT_LIST_ITEM_SELECTED =
		12;
	
	/** The dimension of the drawn icon image ({@code DxD}). */
	byte INT_LIST_ITEM_ICON_DIMENSION =
		13;
	
	/** The label for a list item. */
	byte STRING_LIST_ITEM_LABEL =
		14; 
	
	/** The ID for a list entry, to detect changes. */
	byte INT_LIST_ITEM_ID_CODE =
		15;
	
	/** The font to use for the item ({@code pxsize|style|name}). */
	byte INT_LIST_ITEM_FONT =
		16;
	
	/** The {@link UIItemType} this item is. */
	byte INT_UIITEM_TYPE =
		17;
	
	/**
	 * Select or get select for a given list item.
	 * 
	 * @squirreljme.uiwidgetparam 1 The current locking code this is intended
	 * for, for new codes this should be zero.
	 * @squirreljme.uiwidgetparam 2 The new locking code, zero clears.
	 */
	byte INT_UPDATE_LIST_SELECTION_LOCK =
		18;
	
	/** The form title. */
	byte STRING_FORM_TITLE =
		19;
	
	/** The number of properties. */
	byte NUM_PROPERTIES =
		20;
}
