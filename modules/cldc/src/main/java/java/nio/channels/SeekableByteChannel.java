// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package java.nio.channels;

import java.io.IOException;

public interface SeekableByteChannel
	extends ByteChannel
{
	long position()
		throws IOException;
	
	SeekableByteChannel position(long __a)
		throws IOException;
	
	long size()
		throws IOException;
	
	SeekableByteChannel truncate(long __a)
		throws IOException;
	
}


