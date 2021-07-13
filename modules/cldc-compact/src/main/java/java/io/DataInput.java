// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package java.io;

public interface DataInput
{
	boolean readBoolean()
		throws IOException;
	
	byte readByte()
		throws IOException;
	
	char readChar()
		throws IOException;
	
	double readDouble()
		throws IOException;
	
	float readFloat()
		throws IOException;
	
	void readFully(byte[] __b)
		throws IOException;
	
	void readFully(byte[] __b, int __o, int __l)
		throws IOException;
	
	int readInt()
		throws IOException;
	
	long readLong()
		throws IOException;
	
	short readShort()
		throws IOException;
	
	String readUTF()
		throws IOException;
	
	int readUnsignedByte()
		throws IOException;
	
	int readUnsignedShort()
		throws IOException;
	
	int skipBytes(int __n)
		throws IOException;
}

