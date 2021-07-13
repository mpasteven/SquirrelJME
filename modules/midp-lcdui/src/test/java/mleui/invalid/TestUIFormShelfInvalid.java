// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package mleui.invalid;

import cc.squirreljme.jvm.mle.brackets.UIDisplayBracket;
import cc.squirreljme.jvm.mle.brackets.UIFormBracket;
import cc.squirreljme.jvm.mle.brackets.UIItemBracket;
import cc.squirreljme.jvm.mle.callbacks.UIDisplayCallback;
import cc.squirreljme.jvm.mle.callbacks.UIFormCallback;
import cc.squirreljme.jvm.mle.constants.UIItemPosition;
import cc.squirreljme.jvm.mle.constants.UIItemType;
import cc.squirreljme.jvm.mle.constants.UIMetricType;
import cc.squirreljme.jvm.mle.exceptions.MLECallError;
import cc.squirreljme.runtime.lcdui.mle.UIBackend;
import mleui.BaseBackend;
import net.multiphasicapps.tac.UntestableException;

/**
 * Tests whether {@link UIBackend} fails as it should with invalid values.
 *
 * @since 2020/06/30
 */
public class TestUIFormShelfInvalid
	extends BaseBackend
{
	@Override
	public void test(UIBackend __backend, UIDisplayBracket __display)
		throws Throwable
	{
		// Call sub-test methods and make sure they fail
		int callCount = 0;
		int errorCount = 0;
		for (int i = 0;; i++)
		{
			// Perform the call
			try
			{
				callCount++;
				
				// Run the test and stop if this is the end
				if (this.test(__backend, i))
				{
					callCount--;
					break;
				}
				
				// Send invalid secondary to flag that something is wrong
				this.secondary("not-thrown-" + i, i);
			}
			
			// Caught exception that we want
			catch (MLECallError ignored)
			{
				errorCount++;
			}
		}
		
		// Report the count
		this.secondary("calls", callCount);
		this.secondary("errors", errorCount);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/06/22
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public boolean test(UIBackend __backend, int __index)
		throws MLECallError
	{
		// Check to see if forms are actually supported, if not then we cannot
		// check if it is invalid
		if (0 == __backend.metric(UIMetricType.UIFORMS_SUPPORTED))
			throw new UntestableException("UIForms Not Supported!");
		
		UIItemBracket fake;
		switch (__index)
		{
			case 0:
				__backend.metric(-1);
				break;
			
			case 1:
				__backend.metric(UIMetricType.NUM_METRICS);
				break;
			
			case 2:
				__backend.equals((UIDisplayBracket)null, null);
				break;
			
			case 3:
				__backend.equals((UIFormBracket)null, null);
				break;
			
			case 4:
				__backend.equals((UIItemBracket)null, null);
				break;
			
			case 5:
				__backend.displayCurrent(null);
				break;
			
			case 6:
				__backend.displayShow(null, null);
				break;
			
			case 7:
				__backend.callback(null, (UIDisplayCallback)null);
				break;
			
			case 8:
				__backend.formDelete(null);
				break;
			
			case 9:
				__backend.callback(null, (UIFormCallback)null);
				break;
			
			case 10:
				__backend.itemNew(-1);
				break;
			
			case 11:
				__backend.itemNew(UIItemType.NUM_TYPES);
				break;
			
			case 12:
				__backend.itemDelete(null);
				break;
			
			case 13:
				fake = __backend.itemNew(UIItemType.BUTTON);
				__backend.itemDelete(fake);
				__backend.itemDelete(fake);
				break;
			
			case 14:
				__backend.formItemCount(null);
				break;
			
			case 15:
				__backend.formItemRemove(null, 0);
				break;
			
			case 16:
				__backend.formItemRemove(__backend.formNew(), 0);
				break;
			
			case 17:
				__backend.formItemRemove(__backend.formNew(),
					UIItemPosition.MIN_VALUE - 1);
				break;
			
			case 18:
				__backend.formItemAtPosition(__backend.formNew(),
					UIItemPosition.MIN_VALUE - 1);
				break;
			
			case 19:
				__backend.formItemAtPosition(__backend.formNew(),
					0);
				break;
			
			case 20:
				__backend.formItemPosition(__backend.formNew(),
					null, -1);
				break;
			
			case 21:
				__backend.formItemPosition(__backend.formNew(),
					__backend.itemNew(UIItemType.BUTTON), 1);
				break;
			
			case 22:
				__backend.formItemPosition(__backend.formNew(),
					__backend.itemNew(UIItemType.BUTTON),
					UIItemPosition.MIN_VALUE - 1);
				break;
			
			case 23:
				__backend.formItemRemove(__backend.formNew(),
					UIItemPosition.BODY);
				break;
			
			case 24:
				__backend.itemForm(null);
				break;
			
			default:
				return true;
		}
		
		return false;
	}
}
