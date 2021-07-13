// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.suite;

/**
 * This class contains the results of a dependency match.
 *
 * @since 2017/11/30
 */
public final class MatchResult
{
	/** The matched results. */
	protected final DependencyInfo matched;
	
	/** The unmatched results. */
	protected final DependencyInfo unmatched;
	
	/**
	 * Initializes the match result.
	 *
	 * @param __matched The matched dependencies.
	 * @param __unmatched The unmatched dependencies.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/31
	 */
	public MatchResult(DependencyInfo __matched, DependencyInfo __unmatched)
		throws NullPointerException
	{
		if (__matched == null || __unmatched == null)
			throw new NullPointerException("NARG");
		
		this.matched = __matched;
		this.unmatched = __unmatched;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/31
	 */
	@Override
	public boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (!(__o instanceof MatchResult))
			return false;
		
		MatchResult o = (MatchResult)__o;
		return this.matched.equals(o.matched) &&
			this.unmatched.equals(o.unmatched);
	}
	
	/**
	 * Returns {@code true} if there have been matches in the result.
	 *
	 * @return Has there been any matches?
	 * @since 2017/11/30
	 */
	public final boolean hasMatches()
	{
		return !this.matched.isEmpty();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/31
	 */
	@Override
	public final int hashCode()
	{
		return this.matched.hashCode() ^
			this.unmatched.hashCode();
	}
	
	/**
	 * Returns the dependency information which contains every dependency
	 * which has been matched.
	 *
	 * @return The dependency information containing only matched items.
	 * @since 2017/11/30
	 */
	public final DependencyInfo matched()
	{
		return this.matched;
	}
	
	/**
	 * Returns the dependency information which contains every dependency
	 * which has yet to be matched.
	 *
	 * @return The dependency information containing only unmatched items.
	 * @since 2017/11/30
	 */
	public final DependencyInfo unmatched()
	{
		return this.unmatched;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/31
	 */
	@Override
	public final String toString()
	{
		throw new todo.TODO();
	}
}

