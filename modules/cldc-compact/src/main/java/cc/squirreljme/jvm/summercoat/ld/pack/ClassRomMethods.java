// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.summercoat.ld.pack;

import cc.squirreljme.runtime.cldc.debug.Debugging;

/**
 * Methods that exist within a {@link ClassRom}.
 *
 * @since 2021/07/11
 */
public final class ClassRomMethods
{
	/**
	 * Returns the method by its method index.
	 * 
	 * @param __dx The index of the method.
	 * @return The ROM method.
	 * @throws IndexOutOfBoundsException If this is not a valid method index.
	 * @since 2021/07/12
	 */
	public ClassRomMethod get(int __dx)
		throws IndexOutOfBoundsException
	{
		throw Debugging.todo();
	}
}
