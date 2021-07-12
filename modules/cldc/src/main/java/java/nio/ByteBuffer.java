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
public abstract class ByteBuffer
	extends Buffer
	implements Comparable<ByteBuffer>
{
	ByteBuffer()
	{
		throw new todo.TODO();
	}
	
	@Completion(CompletionState.NOTHING)
	public abstract FloatBuffer asFloatBuffer();
	
	@Completion(CompletionState.NOTHING)
	public abstract IntBuffer asIntBuffer();
	
	@Completion(CompletionState.NOTHING)
	public abstract ShortBuffer asShortBuffer();
	
	@Completion(CompletionState.NOTHING)
	public abstract byte get();
	
	@Completion(CompletionState.NOTHING)
	public abstract byte get(int __a);
	
	@Completion(CompletionState.NOTHING)
	public abstract float getFloat();
	
	@Completion(CompletionState.NOTHING)
	public abstract float getFloat(int __a);
	
	@Completion(CompletionState.NOTHING)
	public abstract int getInt();
	
	@Completion(CompletionState.NOTHING)
	public abstract int getInt(int __a);
	
	@Completion(CompletionState.NOTHING)
	public abstract short getShort();
	
	@Completion(CompletionState.NOTHING)
	public abstract short getShort(int __a);
	
	@SuppressWarnings({"AbstractMethodOverridesAbstractMethod", "override"})
	@Completion(CompletionState.NOTHING)
	public abstract boolean isDirect();
	
	@Completion(CompletionState.NOTHING)
	public abstract ByteBuffer put(byte __a);
	
	@Completion(CompletionState.NOTHING)
	public abstract ByteBuffer put(int __a, byte __b);
	
	@Completion(CompletionState.NOTHING)
	public abstract ByteBuffer putFloat(float __a);
	
	@Completion(CompletionState.NOTHING)
	public abstract ByteBuffer putFloat(int __a, float __b);
	
	@Completion(CompletionState.NOTHING)
	public abstract ByteBuffer putInt(int __a);
	
	@Completion(CompletionState.NOTHING)
	public abstract ByteBuffer putInt(int __a, int __b);
	
	@Completion(CompletionState.NOTHING)
	public abstract ByteBuffer putShort(short __a);
	
	@Completion(CompletionState.NOTHING)
	public abstract ByteBuffer putShort(int __a, short __b);
	
	@Completion(CompletionState.NOTHING)
	public abstract ByteBuffer slice();
	
	@SuppressWarnings({"override"})
	@Completion(CompletionState.NOTHING)
	public final byte[] array()
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
	public int compareTo(ByteBuffer __a)
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
	public ByteBuffer get(byte[] __a, int __b, int __c)
	{
		throw new todo.TODO();
	}
	
	@Completion(CompletionState.NOTHING)
	public ByteBuffer get(byte[] __a)
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
	public final ByteOrder order()
	{
		throw new todo.TODO();
	}
	
	@Completion(CompletionState.NOTHING)
	public final ByteBuffer order(ByteOrder __a)
	{
		throw new todo.TODO();
	}
	
	@Completion(CompletionState.NOTHING)
	public ByteBuffer put(ByteBuffer __a)
	{
		throw new todo.TODO();
	}
	
	@Completion(CompletionState.NOTHING)
	public ByteBuffer put(byte[] __a, int __b, int __c)
	{
		throw new todo.TODO();
	}
	
	@Completion(CompletionState.NOTHING)
	public final ByteBuffer put(byte[] __a)
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
	public static ByteBuffer allocateDirect(int __a)
	{
		throw new todo.TODO();
	}
	
	@Completion(CompletionState.NOTHING)
	public static ByteBuffer wrap(byte[] __a, int __b, int __c)
	{
		throw new todo.TODO();
	}
	
	@Completion(CompletionState.NOTHING)
	public static ByteBuffer wrap(byte[] __a)
	{
		throw new todo.TODO();
	}
}

