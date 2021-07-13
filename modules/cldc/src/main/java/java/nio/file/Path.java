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

import java.io.IOException;

public interface Path
	extends Comparable<Path>, Iterable<Path>
{
	
	boolean endsWith(Path __a);
	
	boolean endsWith(String __a);
	
	@Override
	boolean equals(Object __a);
	
	Path getFileName();
	
	FileSystem getFileSystem();
	
	Path getName(int __a);
	
	int getNameCount();
	
	Path getParent();
	
	Path getRoot();
	
	@Override
	int hashCode();
	
	boolean isAbsolute();
	
	Path normalize();
	
	Path relativize(Path __a);
	
	Path resolve(Path __a);
	
	Path resolve(String __a);
	
	Path resolveSibling(Path __a);
	
	Path resolveSibling(String __a);
	
	boolean startsWith(Path __a);
	
	boolean startsWith(String __a);
	
	Path subpath(int __a, int __b);
	
	Path toAbsolutePath();
	
	Path toRealPath(LinkOption... __a)
		throws IOException;
	
	@Override
	String toString();
}

