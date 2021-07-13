// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.launch;

import cc.squirreljme.jvm.mle.brackets.JarPackageBracket;
import cc.squirreljme.jvm.suite.DependencyInfo;
import cc.squirreljme.jvm.suite.MatchResult;
import cc.squirreljme.jvm.suite.SuiteInfo;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Lazily loaded library handling, for later dependency handling.
 *
 * @since 2020/12/29
 */
final class __Libraries__
{
	/** The available libraries. */
	private final List<Library> _libs =
		new LinkedList<>();
	
	/**
	 * Returns the suites which match the given dependency set.
	 *
	 * @param __set The set of dependencies to get.
	 * @param __opt If {@code true} include optional dependencies.
	 * @return Libraries which satisfy the given dependencies.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/31
	 */
	public Library[] matchDependencies(DependencyInfo __set, boolean __opt)
		throws NullPointerException
	{
		if (__set == null)
			throw new NullPointerException("NARG");
		
		// Extra libraries to obtain
		Library[] from;
		synchronized (this)
		{
			List<Library> libs = this._libs;
			from = libs.<Library>toArray(new Library[libs.size()]);
		}
		
		// Perform the lookup with these specific libraries
		return __Libraries__.matchDependencies(__set, __opt, from);
	}
	
	/**
	 * Registers a library for later dependency handling.
	 * 
	 * @param __info The JAR information.
	 * @param __jar The JAR itself.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/29
	 */
	final void __register(SuiteInfo __info, JarPackageBracket __jar)
		throws NullPointerException
	{
		if (__info == null || __jar == null)
			throw new NullPointerException("NARG");
		
		synchronized (this)
		{
			this._libs.add(new Library(__info, __jar));
		}
	}
	
	/**
	 * Returns the suites which match the given dependency set.
	 *
	 * @param __set The set of dependencies to get.
	 * @param __opt If {@code true} include optional dependencies.
	 * @param __from From which libraries do we take from?
	 * @return Libraries which satisfy the given dependencies.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/31
	 */
	public static Library[] matchDependencies(DependencyInfo __set,
		boolean __opt, Library... __from)
		throws NullPointerException
	{
		if (__set == null)
			throw new NullPointerException("NARG");
		
		// Clear all optionals if they are not included
		if (!__opt)
			__set = __set.noOptionals();
		
		// No dependencies to search for
		if (__set.isEmpty())
			return new Library[0];
		
		// The returning set
		Set<Library> rv = new LinkedHashSet<>();
		
		// Go through all binaries and attempt to match
		for (Library lib : __from)
		{
			// Determine if this library matches one of dependencies we are
			// looking for
			MatchResult result = __set.match(lib.provided());
			if (!result.hasMatches())
				continue;
			
			// Recursively go down
			Collections.addAll(rv, __Libraries__.matchDependencies(
				lib.dependencies(), false, __from));
			
			// Use this as a dependency
			rv.add(lib);
			
			// Use remaining unmatched set
			__set = result.unmatched();
			
			// If the set was emptied then it will never have any more matches
			if (__set.isEmpty())
				break;
		}
		
		// {@squirreljme.error DG0p Could not locate the suite which
		// satisfies the given dependency. (The dependency to look for)}
		if (rv.isEmpty())
			throw new RuntimeException(
				String.format("DG0p %s", __set));
		
		return rv.<Library>toArray(new Library[rv.size()]);
	}
}
