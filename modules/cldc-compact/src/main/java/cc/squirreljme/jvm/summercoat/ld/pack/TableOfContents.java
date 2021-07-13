// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.summercoat.ld.pack;

/**
 * This represents a table of contents which is used to provide a mapping
 * of repeating keys to values, for each various entry.
 *
 * @since 2021/02/22
 */
public interface TableOfContents
{
	/**
	 * Returns the number of entries in the table.
	 * 
	 * @return The number of entries in the table.
	 * @since 2021/02/22
	 */
	int count();
	
	/**
	 * Gets the given table of contents entry for the given index.
	 * 
	 * @param __dx The index to get.
	 * @param __prop The property to get.
	 * @return The value of the given index.
	 * @throws IndexOutOfBoundsException If the index and/or property are not
	 * in range.
	 * @since 2021/02/22
	 */
	int get(int __dx, int __prop)
		throws IndexOutOfBoundsException;
}
