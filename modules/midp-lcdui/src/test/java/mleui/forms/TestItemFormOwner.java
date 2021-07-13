// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package mleui.forms;

import cc.squirreljme.jvm.mle.brackets.UIDisplayBracket;
import cc.squirreljme.jvm.mle.brackets.UIFormBracket;
import cc.squirreljme.jvm.mle.brackets.UIItemBracket;
import cc.squirreljme.jvm.mle.constants.UIItemType;
import cc.squirreljme.runtime.lcdui.mle.UIBackend;

/**
 * Tests that the item on the form is the item of the given form.
 *
 * @since 2021/01/03
 */
public class TestItemFormOwner
	extends BaseUIForm
{
	/**
	 * {@inheritDoc}
	 * @since 2021/01/03
	 */
	@Override
	protected void test(UIBackend __backend, UIDisplayBracket __display,
		UIFormBracket __form)
		throws Throwable
	{
		UIItemBracket item = __backend.itemNew(UIItemType.BUTTON);
		
		// Should not be on the form
		this.secondary("first-null",
			null == __backend.itemForm(item));
		
		// Placing it on the form, it should point to that form
		__backend.formItemPosition(__form, item, 0);
		this.secondary("set-matches",
			__backend.equals(__form, __backend.itemForm(item)));
		
		// Replacing the item should make it go away
		UIItemBracket rep = __backend.itemNew(UIItemType.LABEL);
		__backend.formItemPosition(__form, rep, 0);
		this.secondary("replace-null",
			null == __backend.itemForm(item));
	}
}
