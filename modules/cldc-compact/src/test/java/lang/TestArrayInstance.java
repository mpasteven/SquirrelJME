// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package lang;

import net.multiphasicapps.tac.TestRunnable;

/**
 * Tests array instances and such.
 *
 * @since 2021/02/07
 */
public class TestArrayInstance
	extends TestRunnable
{
	/**
	 * {@inheritDoc}
	 * @since 2021/02/07
	 */
	@SuppressWarnings({"ConstantConditions", "RedundantCast"})
	@Override
	public void test()
		throws Throwable
	{
		super.secondary("same",
			(Object)new Integer[0] instanceof Integer[]);
		super.secondary("different",
			(Object)new Long[0] instanceof Integer[]);
			
		super.secondary("super-compatible",
			(Object)new Integer[0] instanceof Number[]);
		super.secondary("super-incompatible",
			(Object)new String[0] instanceof Number[]);
		
		// Baseline
		
		super.secondary("lower-dims-not-object",
			(Object)new Integer[0] instanceof Integer[][]);
		super.secondary("higher-dims-not-object",
			(Object)new Integer[0][0] instanceof Integer[]);
		
		super.secondary("same-dims-non-to-object",
			(Object)new Object[0][0] instanceof Integer[][]);
		super.secondary("lower-dims-non-to-object",
			(Object)new Object[0] instanceof Integer[][]);
		super.secondary("lower-dims-non-to-pure-object",
			(Object)new Object() instanceof Integer[][]);
		
		super.secondary("higher-dims-to-object",
			(Object)new Object[0][0] instanceof Object[]);
		
		// Inverted
		
		super.secondary("lower-dims-not-object-invert",
			(Object)new Integer[0][0] instanceof Integer[]);
		super.secondary("higher-dims-not-object-invert",
			(Object)new Integer[0] instanceof Integer[][]);
		
		super.secondary("same-dims-non-to-object-invert",
			(Object)new Integer[0][0] instanceof Object[][]);
		super.secondary("lower-dims-non-to-object-invert",
			(Object)new Integer[0][0] instanceof Object[]);
		super.secondary("lower-dims-non-to-pure-object-invert",
			(Object)new Integer[0][0] instanceof Object);
			
		super.secondary("higher-dims-to-object-invert",
			(Object)new Object[0] instanceof Object[][]);
	}
}
