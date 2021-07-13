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

import java.io.Closeable;
import java.util.Set;

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
	
	public abstract Iterable<FileStore> getFileStores();
	
	public abstract Path getPath(String __a, String... __b);
	
	public abstract Iterable<Path> getRootDirectories();
	
	public abstract String getSeparator();
	
	public abstract boolean isOpen();
	
	public abstract boolean isReadOnly();
	
	public abstract Set<String> supportedFileAttributeViews();
}

