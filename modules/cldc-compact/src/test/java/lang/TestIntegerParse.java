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
 * Tests that integer parsing is working.
 *
 * @since 2020/10/09
 */
public class TestIntegerParse
	extends TestRunnable
{
	/**
	 * {@inheritDoc}
	 * @since 2020/10/09
	 */
	@Override
	public void test()
	{
		this.secondary("imin10",
			Integer.parseInt("-2147483648", 10));
		this.secondary("imax10",
			Integer.parseInt("2147483647", 10));
		
		this.secondary("imin10s",
			Integer.toString(Integer.MIN_VALUE, 10));
		this.secondary("imax10s",
			Integer.toString(Integer.MAX_VALUE, 10));
			
		this.secondary("lmin10",
			Long.parseLong("-9223372036854775808", 10));
		this.secondary("lmax10",
			Long.parseLong("9223372036854775807", 10));
		
		// Try to convert the minimum and maximum long values for every base
		for (int i = Character.MIN_RADIX; i <= Character.MAX_RADIX; i++)
		{
			this.secondary("lmin" + i + "s",
				Long.toString(Long.MIN_VALUE, i));
			this.secondary("lmax" + i + "s",
				Long.toString(Long.MAX_VALUE, i));
		}
	}
}
