// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.summercoat.lle;

import cc.squirreljme.jvm.Assembly;
import cc.squirreljme.jvm.mle.brackets.RefLinkBracket;
import cc.squirreljme.runtime.cldc.debug.Debugging;

/**
 * This provides the interface for references which are used to weakly refer
 * to them, so that they may be collected or act as a cache.
 *
 * @see RefLinkBracket
 * @since 2020/05/30
 */
public final class LLEReferenceShelf
{
	/**
	 * Not used.
	 *
	 * @since 2020/05/30
	 */
	private LLEReferenceShelf()
	{
	}
	
	/**
	 * Deletes the reference link, freeing any associated memory.
	 *
	 * @param __link The link to delete.
	 * @since 2020/05/30
	 */
	public static void deleteLink(RefLinkBracket __link)
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Returns the link after the specified one.
	 *
	 * @param __link The link.
	 * @return The next link or {@code null} if there is none.
	 * @since 2020/05/30
	 */
	public static RefLinkBracket linkGetNext(RefLinkBracket __link)
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Gets the object this points to.
	 *
	 * @param __link The link to get the object of.
	 * @return The object that this points to, or {@code null} if there
	 * is no pointed object.
	 * @since 2020/05/30
	 */
	public static Object linkGetObject(RefLinkBracket __link)
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Returns the link before the specified one.
	 *
	 * @param __link The link.
	 * @return The previous link or {@code null} if there is none.
	 * @since 2020/05/30
	 */
	public static RefLinkBracket linkGetPrev(RefLinkBracket __link)
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Sets the link that is after this one
	 *
	 * @param __link The link.
	 * @param __next The new link to set, may be {@code null} to clear.
	 * @since 2020/05/30
	 */
	public static void linkSetNext(RefLinkBracket __link,
		RefLinkBracket __next)
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Sets the object that this points to.
	 *
	 * @param __link The link to be given the object.
	 * @param __v The object to set to, may be {@code null}.
	 * @since 2020/05/30
	 */
	public static void linkSetObject(RefLinkBracket __link, Object __v)
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Sets the link that is before this one.
	 *
	 * @param __link The link.
	 * @param __prev The new link to set, may be {@code null} to clear.
	 * @since 2020/05/30
	 */
	public static void linkSetPrev(RefLinkBracket __link,
		RefLinkBracket __prev)
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Creates a new reference link.
	 *
	 * @return The newly created reference link.
	 * @since 2020/05/30
	 */
	public static RefLinkBracket newLink()
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Gets the link of an object.
	 *
	 * @param __o The object to get the link of.
	 * @return The link of the object or {@code null} if there is none.
	 * @since 2020/05/30
	 */
	public static RefLinkBracket objectGet(Object __o)
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Sets the link of the object.
	 *
	 * @param __o The object to set the link of.
	 * @param __link The link to set to it.
	 * @since 2020/05/30
	 */
	public static void objectSet(Object __o, RefLinkBracket __link)
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
}
