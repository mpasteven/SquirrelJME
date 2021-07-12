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
import java.io.InputStream;
import java.io.OutputStream;

@Standard
public final class Channels
{
	private Channels()
	{
		throw new todo.TODO();
	}
	
	@Completion(CompletionState.NOTHING)
	public static ReadableByteChannel newChannel(InputStream __a)
	{
		throw new todo.TODO();
	}
	
	@Completion(CompletionState.NOTHING)
	public static WritableByteChannel newChannel(OutputStream __a)
	{
		throw new todo.TODO();
	}
	
	@Completion(CompletionState.NOTHING)
	public static InputStream newInputStream(ReadableByteChannel __a)
	{
		throw new todo.TODO();
	}
	
	@Completion(CompletionState.NOTHING)
	public static OutputStream newOutputStream(WritableByteChannel __a)
	{
		throw new todo.TODO();
	}
}

