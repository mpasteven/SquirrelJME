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
import java.util.Set;

@Standard
public abstract class FileSystem
	implements Closeable
{
	/**
	 * Base constructor.
	 *
	 * @since 2019/12/22
	 */
	protected FileSystem()
	{
	}
	
	@Completion(CompletionState.NOTHING)
	public abstract Iterable<FileStore> getFileStores();
	
	@Completion(CompletionState.NOTHING)
	public abstract Path getPath(String __a, String... __b);
	
	@Completion(CompletionState.NOTHING)
	public abstract Iterable<Path> getRootDirectories();
	
	@Completion(CompletionState.NOTHING)
	public abstract String getSeparator();
	
	@Completion(CompletionState.NOTHING)
	public abstract boolean isOpen();
	
	@Completion(CompletionState.NOTHING)
	public abstract boolean isReadOnly();
	
	@Completion(CompletionState.NOTHING)
	public abstract Set<String> supportedFileAttributeViews();
}

