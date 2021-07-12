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
import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

@Standard
public interface DirectoryStream<T>
	extends Closeable, Iterable<T>
{
	@Standard
	interface Filter<T>
	{
		@Completion(CompletionState.NOTHING)
		boolean accept(T __a)
			throws IOException;
	}
}

