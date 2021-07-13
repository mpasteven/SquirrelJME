// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package squirreljme.util;

import cc.squirreljme.runtime.cldc.util.ShellSort;
import net.multiphasicapps.tac.TestRunnable;

/**
 * Test that the {@link ShellSort} gaps make sense.
 *
 * @since 2021/07/12
 */
public class TestShellSortGaps
	extends TestRunnable
{
	/** Gaps used in shell sort, used as a base to determine the gap size. */
	private static final int[] _GAPS =
		new int[]{1750, 701, 301, 132, 57, 23, 10, 4, 1};
	
	/**
	 * {@inheritDoc}
	 * @since 2021/07/12
	 */
	@Override
	public void test()
		throws Throwable
	{
		int[] gaps = TestShellSortGaps._GAPS;
		for (int i = 0, n = gaps.length; i < n; i++)
		{
			int len = gaps[i];
			
			for (int d = -1; d <= 1; d++)
				this.secondary(String.format("%04d", len + d),
					ShellSort.gaps(len + d));
		}
	}
}
