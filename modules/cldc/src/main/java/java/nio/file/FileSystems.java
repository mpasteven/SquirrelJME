// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package java.nio.file;

import cc.squirreljme.completion.Completion;
import cc.squirreljme.completion.CompletionState;
import cc.squirreljme.completion.Standard;
import cc.squirreljme.runtime.cldc.full.NullFileSystem;

/**
 * This contains a static method which is used to obtain the default filesystem
 * which is used by SquirrelJME's native filesystem interface.
 *
 * @since 2017/06/12
 */
@Standard
public final class FileSystems
{
	/**
	 * Not used.
	 *
	 * @since 2017/06/12
	 */
	private FileSystems()
	{
	}
	
	/**
	 * This returns the default filesystem which is used by the system to
	 * access the filesystem.
	 *
	 * @return The system's default filesystem.
	 * @since 2017/06/12
	 */
	@Completion(CompletionState.NOTHING)
	public static FileSystem getDefault()
	{
		todo.DEBUG.note("Implement FileSystem support!");
		return NullFileSystem.INSTANCE;
	}
}

