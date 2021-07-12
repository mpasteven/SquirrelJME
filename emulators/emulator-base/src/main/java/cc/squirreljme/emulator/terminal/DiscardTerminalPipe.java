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

/**
 * This is a terminal pipe which just discards output.
 *
 * @since 2020/07/06
 */
public final class DiscardTerminalPipe
	implements TerminalPipe
{
	/** Is this closed? */
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
				throw new IOException("Discard pipe closed.");
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
		throw new MLECallWouldFail("Cannot read a discard pipe.");
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
				throw new IOException("Discard pipe closed.");
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
				throw new IOException("Discard pipe closed.");
		}
	}
}
