// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package lcdui.list;

import java.util.Random;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;

/**
 * Tests adding and removing items.
 *
 * @since 2020/11/03
 */
public class TestItemAddRemove
	extends BaseList
{
	/**
	 * {@inheritDoc}
	 * @since 2020/11/03
	 */
	@Override
	protected void test(Display __display, List __list, int __type,
		String __typeName)
	{
		// Setup some baseline items for the list
		Random rand = new Random(1234);
		ListItem a = ListItem.random(rand);
		ListItem b = ListItem.random(rand);
		
		// Appending a single item into the list first
		__list.append("a", Image.createImage(1, 1));
		
		this.secondary("append-size", __list.size());
		
		// Store all properties it to double-check if it follows properly
		a.into(__list, 0);
		
		// Adding to the start, the list should be b, a
		__list.insert(0, "b", Image.createImage(2, 2));
		
		// Should be the same item here
		this.secondary("insert-size", __list.size());
		this.secondary("insert-a",
			a.equals(ListItem.of(__list, 1)));
		this.secondary("insert-nota",
			a.equals(ListItem.of(__list, 0)));
		
		// Write B's info, to ensure that it does not mess anything else up
		b.into(__list, 0);
		
		// Delete B, which should keep a untouched
		__list.delete(0);
		
		this.secondary("delete-size", __list.size());
		this.secondary("delete-a",
			a.equals(ListItem.of(__list, 0)));
		
		// Clear out everything!
		__list.deleteAll();
		
		this.secondary("clear-size", __list.size());
	}
}
