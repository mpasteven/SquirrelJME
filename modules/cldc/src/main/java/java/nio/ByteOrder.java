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

/**
 * This represents the byte order that data may be in.
 *
 * @since 2016/02/27
 */
@Standard
public final class ByteOrder
{
	/** Big endian byte order. */
	@Completion(CompletionState.COMPLETE)
	public static final ByteOrder BIG_ENDIAN =
		new ByteOrder("BIG_ENDIAN");
	
	/** Little endian byte order. */
	@Completion(CompletionState.COMPLETE)
	public static final ByteOrder LITTLE_ENDIAN =
		new ByteOrder("LITTLE_ENDIAN");
	
	/** The string representing the byte order. */
	private final String _string;
	
	/**
	 * Initializes the byte order.
	 *
	 * @param __str String pertaining to the order.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/02/27
	 */
	private ByteOrder(String __str)
		throws NullPointerException
	{
		// Check
		if (__str == null)
			throw new NullPointerException();
		
		// Set
		this._string = __str;
	}
	
	/**
	 * Returns the string representing the byte order, either
	 * {@code BIG_ENDIAN} or {@code LITTLE_ENDIAN}.
	 *
	 * @return The byte order's string.
	 * @since 2016/02/27
	 */
	@Override
	@Completion(CompletionState.COMPLETE)
	public String toString()
	{
		return this._string;
	}
	
	@Completion(CompletionState.NOTHING)
	public static ByteOrder nativeOrder()
	{
		throw new todo.TODO();
	}
}

