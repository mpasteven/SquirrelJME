// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package javax.microedition.io.file;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import javax.microedition.io.StreamConnection;

public interface FileConnection
	extends StreamConnection
{
	long availableSize();
	
	boolean canRead();
	
	boolean canWrite();
	
	void create()
		throws IOException;
	
	void delete()
		throws IOException;
	
	long directorySize(boolean __a)
		throws IOException;
	
	boolean exists();
	
	long fileSize()
		throws IOException;
	
	String getName();
	
	String getPath();
	
	String getURL();
	
	boolean isDirectory();
	
	boolean isHidden();
	
	boolean isOpen();
	
	long lastModified();
	
	Enumeration list()
		throws IOException;
	
	Enumeration list(String __a, boolean __b)
		throws IOException;
	
	void mkdir()
		throws IOException;
	
	OutputStream openOutputStream(long __a)
		throws IOException;
	
	void rename(String __a)
		throws IOException;
	
	void setFileConnection(String __a)
		throws IOException;
	
	void setHidden(boolean __a)
		throws IOException;
	
	void setReadable(boolean __a)
		throws IOException;
	
	void setWritable(boolean __a)
		throws IOException;
	
	long totalSize();
	
	void truncate(long __a)
		throws IOException;
	
	long usedSize();
}


