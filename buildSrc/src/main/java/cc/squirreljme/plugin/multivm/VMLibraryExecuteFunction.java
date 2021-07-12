// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.plugin.multivm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.gradle.api.Task;

/**
 * Not Described.
 *
 * @since 2021/05/16
 */
@FunctionalInterface
public interface VMLibraryExecuteFunction
{
	/**
	 * Performs the function as required.
	 * 
	 * @param __task The task used.
	 * @param __isTest Is this a test?
	 * @param __in The input stream.
	 * @param __out The output stream.
	 * @throws IOException Any read/write errors.
	 * @since 2021/05/16
	 */
	void function(Task __task, boolean __isTest, InputStream __in,
		OutputStream __out)
		throws IOException;
}
