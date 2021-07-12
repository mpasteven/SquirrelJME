// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package java.nio;

import cc.squirreljme.completion.Completion;
import cc.squirreljme.completion.CompletionState;
import cc.squirreljme.completion.Standard;

@Standard
public abstract class ShortBuffer
	extends Buffer
	implements Comparable<ShortBuffer>
{
	ShortBuffer()
	{
		throw new todo.TODO();
	}
	
	@Completion(CompletionState.NOTHING)
	public abstract short get();
	
	@Completion(CompletionState.NOTHING)
	public abstract short get(int __a);
	
	@SuppressWarnings({"AbstractMethodOverridesAbstractMethod", "override"})
	@Completion(CompletionState.NOTHING)
	public abstract boolean isDirect();
	
	@Completion(CompletionState.NOTHING)
	public abstract ByteOrder order();
	
	@Completion(CompletionState.NOTHING)
	public abstract ShortBuffer put(short __a);
	
	@Completion(CompletionState.NOTHING)
	public abstract ShortBuffer put(int __a, short __b);
	
	@Completion(CompletionState.NOTHING)
	public abstract ShortBuffer slice();
	
	@SuppressWarnings({"override"})
	@Completion(CompletionState.NOTHING)
	public final short[] array()
	{
		throw new todo.TODO();
	}
	
	@SuppressWarnings({"override"})
	@Completion(CompletionState.NOTHING)
	public final int arrayOffset()
	{
		throw new todo.TODO();
	}
	
	@Override
	@Completion(CompletionState.NOTHING)
	public int compareTo(ShortBuffer __a)
	{
		throw new todo.TODO();
	}
	
	@Override
	@Completion(CompletionState.NOTHING)
	public boolean equals(Object __a)
	{
		throw new todo.TODO();
	}
	
	@Completion(CompletionState.NOTHING)
	public ShortBuffer get(short[] __a, int __b, int __c)
	{
		throw new todo.TODO();
	}
	
	@Completion(CompletionState.NOTHING)
	public ShortBuffer get(short[] __a)
	{
		throw new todo.TODO();
	}
	
	@SuppressWarnings({"override"})
	@Completion(CompletionState.NOTHING)
	public final boolean hasArray()
	{
		throw new todo.TODO();
	}
	
	@Override
	@Completion(CompletionState.NOTHING)
	public int hashCode()
	{
		throw new todo.TODO();
	}
	
	@Completion(CompletionState.NOTHING)
	public ShortBuffer put(ShortBuffer __a)
	{
		throw new todo.TODO();
	}
	
	@Completion(CompletionState.NOTHING)
	public ShortBuffer put(short[] __a, int __b, int __c)
	{
		throw new todo.TODO();
	}
	
	@Completion(CompletionState.NOTHING)
	public final ShortBuffer put(short[] __a)
	{
		throw new todo.TODO();
	}
	
	@Override
	@Completion(CompletionState.NOTHING)
	public String toString()
	{
		throw new todo.TODO();
	}
	
	@Completion(CompletionState.NOTHING)
	public static ShortBuffer wrap(short[] __a, int __b, int __c)
	{
		throw new todo.TODO();
	}
	
	@Completion(CompletionState.NOTHING)
	public static ShortBuffer wrap(short[] __a)
	{
		throw new todo.TODO();
	}
}


