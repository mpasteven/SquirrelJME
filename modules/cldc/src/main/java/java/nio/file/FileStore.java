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
import java.io.IOException;

@Standard
public abstract class FileStore
{
	protected FileStore()
	{
		throw new todo.TODO();
	}
	
	@Completion(CompletionState.NOTHING)
	public abstract Object getAttribute(String __a)
		throws IOException;
	
	@Completion(CompletionState.NOTHING)
	public abstract long getTotalSpace()
		throws IOException;
	
	@Completion(CompletionState.NOTHING)
	public abstract long getUnallocatedSpace()
		throws IOException;
	
	@Completion(CompletionState.NOTHING)
	public abstract long getUsableSpace()
		throws IOException;
	
	@Completion(CompletionState.NOTHING)
	public abstract boolean isReadOnly();
	
	@Completion(CompletionState.NOTHING)
	public abstract String name();
	
	@Completion(CompletionState.NOTHING)
	public abstract boolean supportsFileAttributeView(String __a);
	
	@Completion(CompletionState.NOTHING)
	public abstract String type();
}


