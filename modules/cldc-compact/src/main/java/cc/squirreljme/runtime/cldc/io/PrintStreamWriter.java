// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) 2013-2016 Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) 2013-2016 Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// For more information see license.mkd.
// ---------------------------------------------------------------------------

package cc.squirreljme.runtime.cldc.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;

/**
 * This is a class which takes input characters and writes to the given
 * print stream.
 *
 * This class is thread safe.
 *
 * @since 2016/08/12
 */
public class PrintStreamWriter
	extends Writer
	implements Closeable
{
	/** The stream to write to. */
	protected final PrintStream output;
	
	/**
	 * Initializes the wrapped print stream writer.
	 *
	 * @param __ps The stream to write to.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/08/12
	 */
	public PrintStreamWriter(PrintStream __ps)
		throws NullPointerException
	{
		// Check
		if (__ps == null)
			throw new NullPointerException("NARG");
		
		// Set
		this.output = __ps;
	}
	
	/**
	 * Initializes the wrapped print stream writer and using the given lock.
	 *
	 * @param __lock The lock to use.
	 * @param __ps The stream to write to.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/08/12
	 */
	public PrintStreamWriter(Object __lock, PrintStream __ps)
		throws NullPointerException
	{
		super(__lock);
		
		// Check
		if (__ps == null)
			throw new NullPointerException("NARG");
		
		// Set
		this.output = __ps;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/08/12
	 */
	@Override
	public void close()
		throws IOException
	{
		// Lock
		PrintStream output = this.output;
		synchronized (this.lock)
		{
			output.close();
			this.__checkError();
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/08/12
	 */
	@Override
	public void flush()
		throws IOException
	{
		// Lock
		PrintStream output = this.output;
		synchronized (this.lock)
		{
			output.flush();
			this.__checkError();
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/08/12
	 */
	@Override
	public void write(int __c)
		throws IOException
	{
		// Lock
		PrintStream output = this.output;
		synchronized (this.lock)
		{
			output.print((char)__c);
			this.__checkError();
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/08/12
	 */
	@Override
	public void write(char[] __c, int __o, int __l)
		throws IndexOutOfBoundsException, IOException, NullPointerException
	{
		// Check
		if (__c == null)
			throw new NullPointerException("NARG");
		int n = __c.length;
		int end = __o + __l;
		if (__o < 0 || __l < 0 || end > n)
			throw new IndexOutOfBoundsException("IOOB");
	
		// Lock
		PrintStream output = this.output;
		synchronized (this.lock)
		{
			// Print characters
			for (int i = __o; i < end; i++)
				output.print((char)__c[i]);
			
			// Check
			this.__checkError();
		}
	}
	
	/**
	 * Checks if the given stream has reported an error.
	 *
	 * @throws IOException If the stream has entered the error state.
	 * @since 2016/08/12
	 */
	private void __checkError()
		throws IOException
	{
		// {@squirreljme.error BD1n The underlying stream has entered the
		// error state.}
		if (this.output.checkError())
			throw new IOException("BD1n");
	}
}

