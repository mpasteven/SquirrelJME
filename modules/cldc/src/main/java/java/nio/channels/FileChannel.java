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

import cc.squirreljme.completion.Completion;
import cc.squirreljme.completion.CompletionState;
import cc.squirreljme.completion.Standard;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Set;

@Standard
public abstract class FileChannel
	implements SeekableByteChannel
{
	protected FileChannel()
	{
		throw new todo.TODO();
	}
	
	@Completion(CompletionState.NOTHING)
	public abstract void force(boolean __a)
		throws IOException;
	
	@Override
	@Completion(CompletionState.NOTHING)
	public abstract FileChannel position(long __a)
		throws IOException;
	
	@Completion(CompletionState.NOTHING)
	public abstract int read(ByteBuffer __a, long __b)
		throws IOException;
	
	@Override
	@Completion(CompletionState.NOTHING)
	public abstract FileChannel truncate(long __a)
		throws IOException;
	
	@Completion(CompletionState.NOTHING)
	public abstract int write(ByteBuffer __a, long __b)
		throws IOException;
	
	@Completion(CompletionState.NOTHING)
	public static FileChannel open(Path __a, Set<? extends OpenOption> __b,
		FileAttribute<?>... __c)
		throws IOException
	{
		if (false)
			throw new IOException();
		throw new todo.TODO();
	}
	
	@Completion(CompletionState.NOTHING)
	public static FileChannel open(Path __a, OpenOption... __b)
		throws IOException
	{
		if (false)
			throw new IOException();
		throw new todo.TODO();
	}
}

