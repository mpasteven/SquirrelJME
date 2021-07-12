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

import cc.squirreljme.completion.Completion;
import cc.squirreljme.completion.CompletionState;
import cc.squirreljme.completion.Standard;

/**
 * This indicates that the given path is not valid.
 *
 * @since 2019/12/22
 */
@Standard
public class InvalidPathException
	extends IllegalArgumentException
{
	/** The input. */
	private final transient String _input;
	
	/** The reason. */
	private final transient String _reason;
	
	/** The index. */
	private final transient int _index;
	
	/**
	 * Initializes the exception.
	 *
	 * @param __in The input string.
	 * @param __reason The reason for the failure.
	 * @param __index The index of the failure, {@code -1} if not known.
	 * @throws IllegalArgumentException If the index is less than {@code -1}.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/12/22
	 */
	@Completion(CompletionState.COMPLETE)
	public InvalidPathException(String __in, String __reason, int __index)
		throws IllegalArgumentException, NullPointerException
	{
		if (__in == null || __reason == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error ZY04 Invalid path index specified for
		// exception. (Index)}
		if (__index < -1)
			throw new IllegalArgumentException("ZY04 " + __index);
		
		this._input = __in;
		this._reason = __reason;
		this._index = __index;
	}
	
	/**
	 * Initializes the exception.
	 *
	 * @param __in The input string.
	 * @param __reason The reason for the failure.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/12/22
	 */
	@Completion(CompletionState.COMPLETE)
	public InvalidPathException(String __in, String __reason)
		throws NullPointerException
	{
		this(__in, __reason, -1);
	}
	
	/**
	 * Returns the index where the error occurred.
	 *
	 * @return The index where the error occurred, or {@code -1} if it is not
	 * known.
	 * @since 2019/12/22
	 */
	@Completion(CompletionState.COMPLETE)
	public int getIndex()
	{
		return this._index;
	}
	
	/**
	 * Returns the input path.
	 *
	 * @return The input path.
	 * @since 2019/12/22
	 */
	@Completion(CompletionState.COMPLETE)
	public String getInput()
	{
		return this._input;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/12/22
	 */
	@Override
	@Completion(CompletionState.COMPLETE)
	public String getMessage()
	{
		// This is specifically specified by the documentation
		int index = this._index;
		return this._reason + ": " + this._input +
			(index > -1 ? " at index " + index : "");
	}
	
	/**
	 * Returns the reason the path is not valid.
	 *
	 * @return The reason the path is not valid.
	 * @since 2019/12/22
	 */
	@Completion(CompletionState.COMPLETE)
	public String getReason()
	{
		return this._reason;
	}
}

