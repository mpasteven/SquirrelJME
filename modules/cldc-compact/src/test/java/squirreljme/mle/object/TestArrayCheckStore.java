// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package squirreljme.mle.object;

import cc.squirreljme.jvm.mle.ObjectShelf;
import cc.squirreljme.jvm.mle.RuntimeShelf;
import cc.squirreljme.jvm.mle.constants.VMType;
import cc.squirreljme.jvm.mle.exceptions.MLECallError;
import net.multiphasicapps.tac.TestRunnable;
import net.multiphasicapps.tac.UntestableException;

/**
 * Tests that {@link ObjectShelf#arrayCheckStore(Object, Object)} is
 * implemented correctly.
 *
 * @since 2021/02/07
 */
public class TestArrayCheckStore
	extends TestRunnable
{
	/**
	 * {@inheritDoc}
	 * @since 2021/02/07
	 */
	@Override
	public void test()
		throws Throwable
	{
		// Ignore for Java SE
		if (RuntimeShelf.vmType() == VMType.JAVA_SE)
			throw new UntestableException();
		
		super.secondary("same",
			ObjectShelf.arrayCheckStore(new Integer[0], 2));
		super.secondary("different",
			ObjectShelf.arrayCheckStore(new Integer[0], 3L));
			
		super.secondary("super-compatible",
			ObjectShelf.arrayCheckStore(new Number[0], 4));
		super.secondary("super-incompatible",
			ObjectShelf.arrayCheckStore(new Number[0], "5"));
		
		// Baseline
		
		super.secondary("lower-dims-not-object",
			ObjectShelf.arrayCheckStore(new Integer[0][0], 6));
		super.secondary("higher-dims-not-object",
			ObjectShelf.arrayCheckStore(new Integer[0], new Integer[0]));
		
		super.secondary("same-dims-non-to-object",
			ObjectShelf.arrayCheckStore(new Integer[0][0], new Object[0]));
		super.secondary("lower-dims-non-to-object",
			ObjectShelf.arrayCheckStore(new Integer[0][0], new Object()));
		
		super.secondary("higher-dims-to-object",
			ObjectShelf.arrayCheckStore(new Object[0], new Object[0]));
		
		// Inverted
		
		super.secondary("lower-dims-not-object-invert",
			ObjectShelf.arrayCheckStore(new Integer[0], new Integer[0]));
		super.secondary("higher-dims-not-object-invert",
			ObjectShelf.arrayCheckStore(new Integer[0][0], 12));
		
		super.secondary("same-dims-non-to-object-invert",
			ObjectShelf.arrayCheckStore(new Object[0][0], new Integer[0]));
		super.secondary("lower-dims-non-to-object-invert",
			ObjectShelf.arrayCheckStore(new Object[0], new Integer[0]));
			
		super.secondary("higher-dims-to-object-invert",
			ObjectShelf.arrayCheckStore(new Object[0][0], new Object()));
			
		try
		{
			ObjectShelf.arrayCheckStore(new Object(), new Integer[0]);
		}
		catch (MLECallError ignored)
		{
			super.secondary("lower-dims-non-to-pure-object-invert",
				true);
		}
	}
}
