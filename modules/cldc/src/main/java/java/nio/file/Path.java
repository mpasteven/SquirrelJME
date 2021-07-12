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
public interface Path
	extends Comparable<Path>, Iterable<Path>
{
	@Completion(CompletionState.NOTHING)
	boolean endsWith(Path __a);
	
	@Completion(CompletionState.NOTHING)
	boolean endsWith(String __a);
	
	@Completion(CompletionState.NOTHING)
	Path getFileName();
	
	@Completion(CompletionState.NOTHING)
	FileSystem getFileSystem();
	
	@Completion(CompletionState.NOTHING)
	Path getName(int __a);
	
	@Completion(CompletionState.NOTHING)
	int getNameCount();
	
	@Completion(CompletionState.NOTHING)
	Path getParent();
	
	@Completion(CompletionState.NOTHING)
	Path getRoot();
	
	@Completion(CompletionState.NOTHING)
	boolean isAbsolute();
	
	@Completion(CompletionState.NOTHING)
	Path normalize();
	
	@Completion(CompletionState.NOTHING)
	Path relativize(Path __a);
	
	@Completion(CompletionState.NOTHING)
	Path resolve(Path __a);
	
	@Completion(CompletionState.NOTHING)
	Path resolve(String __a);
	
	@Completion(CompletionState.NOTHING)
	Path resolveSibling(Path __a);
	
	@Completion(CompletionState.NOTHING)
	Path resolveSibling(String __a);
	
	@Completion(CompletionState.NOTHING)
	boolean startsWith(Path __a);
	
	@Completion(CompletionState.NOTHING)
	boolean startsWith(String __a);
	
	@Completion(CompletionState.NOTHING)
	Path subpath(int __a, int __b);
	
	@Completion(CompletionState.NOTHING)
	Path toAbsolutePath();
	
	@Completion(CompletionState.NOTHING)
	Path toRealPath(LinkOption... __a)
		throws IOException;
}

