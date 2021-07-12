// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package security;

import cc.squirreljme.runtime.cldc.util.SortedTreeMap;
import java.security.Permission;
import java.util.Map;
import net.multiphasicapps.tac.TestRunnable;

/**
 * Tests that implied permissions are correct.
 *
 * @since 2020/07/09
 */
public class TestImpliedPermission
	extends TestRunnable
{
	/** The name to use for keys. */
	@SuppressWarnings("SpellCheckingInspection")
	private static final String[] _NAMES =
		new String[]{"squirrel", "squirrel*", "squirrel.*", "squirrel.cute",
			"squirrel.adorable", "sq*", "*quirrel", "squ*rrel", "cookie",
			"a.b.*", "a.b.c", "*"};
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/09
	 */
	@Override
	public void test()
	{
		// Build permission mapping of every possibility!
		Map<String, Permission> permissions = new SortedTreeMap<>();
		for (int i = 0, n = TestImpliedPermission._NAMES.length; i < n; i++)
		{
			String name = TestImpliedPermission._NAMES[i];
			
			permissions.put("n-" + i + "-" + name,
				new __NameOnly__(name));
			permissions.put("s-" + i + "-" + name,
				new __SubNameOnly__(name));
		}
		
		// The number of actual permissions to check against
		int num = permissions.size();
		
		// Match all of the permissions together
		for (Map.Entry<String, Permission> ka : permissions.entrySet())
		{
			Permission kav = ka.getValue();
			boolean[] implies = new boolean[num];
			
			int at = 0;
			for (Map.Entry<String, Permission> kb : permissions.entrySet())
				implies[at++] = kav.implies(kb.getValue());
			
			this.secondary(ka.getKey(), implies);
		}
	}
}
