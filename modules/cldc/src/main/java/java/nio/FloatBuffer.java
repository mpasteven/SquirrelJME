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
public abstract class FloatBuffer
	extends Buffer
	implements Comparable<FloatBuffer>
{
	FloatBuffer()
	{
		throw new todo.TODO();
	}
	
	@Completion(CompletionState.NOTHING)
	public abstract float get();
	
	@Completion(CompletionState.NOTHING)
	public abstract float get(int __a);
	
	@SuppressWarnings({"AbstractMethodOverridesAbstractMethod", "override"})
	@Completion(CompletionState.NOTHING)
	public abstract boolean isDirect();
	
	@Completion(CompletionState.NOTHING)
	public abstract ByteOrder order();
	
	@Completion(CompletionState.NOTHING)
	public abstract FloatBuffer put(float __a);
	
	@Completion(CompletionState.NOTHING)
	public abstract FloatBuffer put(int __a, float __b);
	
	@Completion(CompletionState.NOTHING)
	public abstract FloatBuffer slice();
	
	@SuppressWarnings({"override"})
	@Completion(CompletionState.NOTHING)
	public final float[] array()
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
	public int compareTo(FloatBuffer __a)
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
	public FloatBuffer get(float[] __a, int __b, int __c)
	{
		throw new todo.TODO();
	}
	
	@Completion(CompletionState.NOTHING)
	public FloatBuffer get(float[] __a)
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
	public FloatBuffer put(FloatBuffer __a)
	{
		throw new todo.TODO();
	}
	
	@Completion(CompletionState.NOTHING)
	public FloatBuffer put(float[] __a, int __b, int __c)
	{
		throw new todo.TODO();
	}
	
	@Completion(CompletionState.NOTHING)
	public final FloatBuffer put(float[] __a)
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
	public static FloatBuffer wrap(float[] __a, int __b, int __c)
	{
		throw new todo.TODO();
	}
	
	@Completion(CompletionState.NOTHING)
	public static FloatBuffer wrap(float[] __a)
	{
		throw new todo.TODO();
	}
}


