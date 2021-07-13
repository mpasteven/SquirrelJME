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
import java.io.OutputStream;

/**
 * This is a terminal pipe which prints to a real output stream, it does still
 * prevent the target stream from being closed.
 *
 * @since 2020/07/06
 */
public final class RealTerminalPipe
	implements TerminalPipe
{
	/** The stream to write to. */
	protected final OutputStream out;
	
	/** Is this closed? */
	private boolean _closed;
	
	/**
	 * Initializes the real pipe.
	 * 
	 * @param __out The real pipe to target.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/07/06
	 */
	public RealTerminalPipe(OutputStream __out)
		throws NullPointerException
	{
		if (__out == null)
			throw new NullPointerException("NARG");
		
		this.out = __out;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/06
	 */
	@Override
	public final void close()
		throws IOException
	{
		synchronized (this)
		{
			// Have close go first so that the output is closed
			try
			{
				this.out.close();
			}
			
			// Always mark as closed regardless of failure or not
			finally
			{
				this._closed = true;
			}
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
				throw new IOException("Real pipe closed.");
			
			this.out.flush();
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
		throw new MLECallWouldFail("Cannot read a real pipe.");
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
				throw new IOException("Real pipe closed.");
			
			this.out.write(__b);
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
				throw new IOException("Real pipe closed.");
			
			this.out.write(__b, __o, __l);
		}
	}
}
