// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.emulator.terminal;

import cc.squirreljme.emulator.MLECallWouldFail;
import java.io.IOException;
import net.multiphasicapps.io.ByteDeque;

/**
 * This is a terminal pipe which manages its own buffer for output, it is
 * used for the output of sub-tasks to be allowed to be read by another task.
 *
 * @since 2020/07/06
 */
public final class BufferTerminalPipe
	implements TerminalPipe
{
	/** The backing double-ended byte queue for buffer data. */
	protected final ByteDeque deque =
		new ByteDeque();
	
	/** Has this been closed? */
	private boolean _closed;
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/06
	 */
	@Override
	public final void close()
	{
		synchronized (this)
		{
			// Mark as closed
			this._closed = true;
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/06
	 */
	@Override
	public final void flush()
		throws IOException
	{
		synchronized (this)
		{
			if (this._closed)
				throw new IOException("Cannot flush closed buffer pipe.");
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/06
	 */
	@Override
	public final int read(byte[] __b, int __o, int __l)
		throws MLECallWouldFail
	{
		if (__b == null || __o < 0 || __l < 0 || (__o + __l) > __b.length)
			throw new MLECallWouldFail("Null or out of bounds argument.");
		
		synchronized (this)
		{
			int rv = this.deque.removeFirst(__b, __o, __l);
			
			// Check if a full end of file has been reached
			if (rv == 0 && this._closed && this.deque.isEmpty())
				return -1;
			return rv;
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/06
	 */
	@Override
	public final void write(int __b)
		throws IOException
	{
		synchronized (this)
		{
			if (this._closed)
				throw new IOException("Cannot write to closed buffer pipe.");
			
			this.deque.addLast((byte)__b);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/06
	 */
	@Override
	public final void write(byte[] __b, int __o, int __l)
		throws IOException, MLECallWouldFail
	{
		if (__b == null || __o < 0 || __l < 0 || (__o + __l) > __b.length)
			throw new MLECallWouldFail("Null or out of bounds argument.");
		
		synchronized (this)
		{
			if (this._closed)
				throw new IOException("Cannot write to closed buffer pipe.");
				
			this.deque.addLast(__b, __o, __l);
		}
	}
}
