// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) 2013-2016 Steven Gawroriski <steven@multiphasicapps.net>
//     Copyright (C) 2013-2016 Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// For more information see license.mkd.
// ---------------------------------------------------------------------------

package net.multiphasicapps.io.datadequestream;

import java.io.IOException;
import java.io.OutputStream;
import net.multiphasicapps.util.datadeque.ByteDeque;

/**
 * This is used to wrap a {@link ByteDeque} and is used to append to the
 * given queue.
 *
 * Closing has no effect.
 *
 * This class is not thread safe.
 *
 * @since 2016/08/09
 */
public class ByteDequeOutputStream
	extends OutputStream
{
	/** The output queue. */
	protected final ByteDeque deque;
	
	/**
	 * Initializes the byte deque output stream using an automatically
	 * create deque.
	 *
	 * @since 2016/07/09
	 */
	public ByteDequeOutputStream()
	{
		this(new ByteDeque());
	}
	
	/**
	 * This wraps the given {@link ByteDeque} and appends an output bytes
	 * into it.
	 *
	 * @param __bd The byte deque to append into.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/08/09
	 */
	public ByteDequeOutputStream(ByteDeque __bd)
		throws NullPointerException
	{
		// Check
		if (__bd == null)
			throw new NullPointerException("NARG");
		
		// Set
		this.deque = __bd;
	}
	
	/**
	 * This has no effect.
	 *
	 * @since 2016/08/09
	 */
	@Override
	public void close()
	{
		// Does nothing
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/08/09
	 */
	@Override
	public void write(int __b)
	{
		this.deque.addLast((byte)__b);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/08/09
	 */
	@Override
	public void write(byte[] __b, int __o, int __l)
	{
		this.deque.addLast(__b, __o, __l);
	}
}


