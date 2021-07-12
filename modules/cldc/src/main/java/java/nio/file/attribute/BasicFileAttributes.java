// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package java.nio.file.attribute;

import cc.squirreljme.completion.Completion;
import cc.squirreljme.completion.CompletionState;
import cc.squirreljme.completion.Standard;

@Standard
public interface BasicFileAttributes
{
	@Completion(CompletionState.NOTHING)
	FileTime creationTime();
	
	@Completion(CompletionState.NOTHING)
	boolean isDirectory();
	
	@Completion(CompletionState.NOTHING)
	boolean isOther();
	
	@Completion(CompletionState.NOTHING)
	boolean isRegularFile();
	
	@Completion(CompletionState.NOTHING)
	boolean isSymbolicLink();
	
	@Completion(CompletionState.NOTHING)
	FileTime lastAccessTime();
	
	@Completion(CompletionState.NOTHING)
	FileTime lastModifiedTime();
	
	@Completion(CompletionState.NOTHING)
	long size();
}


