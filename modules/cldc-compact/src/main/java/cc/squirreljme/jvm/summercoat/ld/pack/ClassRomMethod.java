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
 * Represents a method within the ROM.
 *
 * @since 2021/07/12
 */
public final class ClassRomMethod
{
	/**
	 * Returns the code length of the given method.
	 * 
	 * @return The code length of the given method, will return {@code -1}
	 * if this method has no code ({@code abstract} or {@code native}).
	 * @since 2021/07/12
	 */
	public final int codeLength()
	{
		throw Debugging.todo();
	}
	
	/**
	 * Returns the name of the method.
	 * 
	 * @return The name of the method.
	 * @since 2021/07/12
	 */
	public final String name()
	{
		throw Debugging.todo();
	}
	
	/**
	 * Returns the method signature.
	 * 
	 * @return The method signature.
	 * @since 2021/07/12
	 */
	public final String signature()
	{
		throw Debugging.todo();
	}
}
