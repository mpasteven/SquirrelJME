// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

import dev.shadowtail.classfile.mini.Minimizer;
import java.io.InputStream;
import net.multiphasicapps.classfile.ClassFile;
import net.multiphasicapps.tac.TestRunnable;
import net.multiphasicapps.tac.UntestableException;

/**
 * Tests that minimizing is performed properly and loading minimized classes
 * works as well.
 *
 * @since 2019/03/10
 */
public class TestMinimizedLoad
	extends TestRunnable
{
	/**
	 * {@inheritDoc}
	 * @since 2019/03/10
	 */
	@Override
	public void test()
		throws Throwable
	{
		if (true)
			throw new UntestableException("Already tested by another."); 
		
		for (String x : TestClassLoad.CLASS_LIST)
			try (InputStream in = TestClassLoad.class.getResourceAsStream(x))
			{
				Minimizer.minimize(ClassFile.decode(in));
			}
	}
}

