// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package lcdui.list;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.List;

/**
 * Tests the selection of items and otherwise.
 *
 * @since 2020/11/03
 */
public class TestSelections
	extends BaseList
{
	/**
	 * {@inheritDoc}
	 * @since 2020/11/03
	 */
	@Override
	protected void test(Display __display, List __list, int __type,
		String __typeName)
		throws Throwable
	{
		// Flags for checking/setting
		boolean[] flags = new boolean[TestSelectionCommands.NUM_ITEMS];
		
		// Add some list items
		for (int i = 0; i < TestSelectionCommands.NUM_ITEMS; i++)
			__list.append(Character.toString((char)('a' + i)), null);
		
		// Starting selection
		__list.getSelectedFlags(flags);
		this.secondary("start", flags);
		
		// Sequential set
		for (int i = 0; i < TestSelectionCommands.NUM_ITEMS; i++)
		{
			__list.setSelectedIndex(i, true);
			
			__list.getSelectedFlags(flags);
			this.secondary("seqset-" + i, flags);
		}
		
		// Sequential clear
		for (int i = 0; i < TestSelectionCommands.NUM_ITEMS; i++)
		{
			__list.setSelectedIndex(i, false);
			
			__list.getSelectedFlags(flags);
			this.secondary("seqclear-" + i, flags);
		}
		
		// Even/Odd
		for (int i = 0; i < TestSelectionCommands.NUM_ITEMS; i++)
			flags[i] = ((i % 2) == 0);
		__list.setSelectedFlags(flags);
		
		__list.getSelectedFlags(flags);
		this.secondary("evenodd", flags);
		
		// Nothing
		for (int i = 0; i < TestSelectionCommands.NUM_ITEMS; i++)
			flags[i] = false;
		__list.setSelectedFlags(flags);
		
		__list.getSelectedFlags(flags);
		this.secondary("none", flags);
	}
}
