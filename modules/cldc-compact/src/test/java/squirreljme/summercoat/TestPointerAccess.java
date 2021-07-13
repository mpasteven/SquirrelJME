// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package squirreljme.summercoat;

import cc.squirreljme.jvm.Assembly;
import net.multiphasicapps.tac.TestRunnable;

/**
 * This tests that pointer access makes sense.
 *
 * @since 2019/12/21
 */
public class TestPointerAccess
	extends TestRunnable
{
	/**
	 * {@inheritDoc}
	 * @since 2019/12/21
	 */
	@Override
	public void test()
	{
		// Create a new object which we will get a pointer of
		Object object = new Object();
		
		// This object cannot be null
		int pointer = Assembly.objectToPointer(object);
		this.secondary("otpnotnull", pointer != 0); 
		
		// Reversing this operation should return the original object
		Object from = Assembly.pointerToObject(pointer);
		this.secondary("sameobject", object == from);
		
		// The zero pointer must be null
		Object ozero = Assembly.pointerToObject(0);
		this.secondary("zeroisnull", ozero == null);
		
		// The null object must be zero
		long pnull = Assembly.objectToPointer(null);
		this.secondary("nulliszero", pnull == 0L);
	}
}

