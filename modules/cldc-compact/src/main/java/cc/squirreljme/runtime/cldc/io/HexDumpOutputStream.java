// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.runtime.cldc.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;

/**
 * This contains a stream which when given bytes hexadecimal bytes will
 * output to the given stream.
 *
 * Closing this stream does not close the dumping stream, it only closes the
 * piped stream if one is used.
 *
 * @since 2016/08/12
 */
public class HexDumpOutputStream
	extends OutputStream
{
	/** The number of columns to print. */
	private static final int _COLUMNS =
		16;
	
	/** Where to pipe data to. */
	protected final OutputStream pipe;
	
	/** Where to write characters to. */
	protected final Writer dump;
	
	/** The write queue. */
	private final byte[] _queue =
		new byte[HexDumpOutputStream._COLUMNS];
	
	/** The current write position. */
	private volatile int _at;
	
	/**
	 * Initializes dumping to the given stream.
	 *
	 * @param __dump The stream to dump to.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/07/12
	 */
	public HexDumpOutputStream(PrintStream __dump)
		throws NullPointerException
	{
		this(new PrintStreamWriter(__dump));
	}
	
	/**
	 * Initializes dumping to the given stream.
	 *
	 * @param __dump The stream to dump to.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/07/12
	 */
	public HexDumpOutputStream(Writer __dump)
		throws NullPointerException
	{
		// Check
		if (__dump == null)
			throw new NullPointerException("NARG");
		
		// Set
		this.pipe = null;
		this.dump = __dump;
	}
	
	/**
	 * Initializes dumping to the given stream and also copying them to
	 * another stream to act as a pipe.
	 *
	 * @param __pipe The stream to copy bytes to.
	 * @param __dump The stream to dump to.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/07/12
	 */
	public HexDumpOutputStream(OutputStream __pipe, PrintStream __dump)
		throws NullPointerException
	{
		this(__pipe, new PrintStreamWriter(__dump));
	}
	
	/**
	 * Initializes dumping to the given stream and also copying them to
	 * another stream to act as a pipe.
	 *
	 * @param __pipe The stream to copy bytes to.
	 * @param __dump The stream to dump to.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/07/12
	 */
	public HexDumpOutputStream(OutputStream __pipe, Writer __dump)
		throws NullPointerException
	{
		// Check
		if (__pipe == null || __dump == null)
			throw new NullPointerException("NARG");
		
		// Set
		this.pipe = __pipe;
		this.dump = __dump;
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * The stream to dump to is not closed.
	 *
	 * @since 2016/08/12
	 */
	@Override
	public void close()
		throws IOException
	{
		// Force printing of bytes
		this.__printLine();
		
		// Only close the pipe and not the dump
		Closeable c = this.pipe;
		if (c != null)
			c.close();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/08/12
	 */
	@Override
	public void flush()
		throws IOException
	{
		// Flush the forward pipe
		this.pipe.flush();
		
		// And the dump
		this.dump.flush();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/08/12
	 */
	@Override
	public void write(int __b)
		throws IOException
	{
		// Write a single byte
		int cols = HexDumpOutputStream._COLUMNS;
		byte b = (byte)__b;
		byte[] queue = this._queue;
		int at = this._at;
		try
		{
			// Write to the queue
			queue[at++] = b;
			this._at = at;
			
			// Send to the output
			OutputStream pipe = this.pipe;
			if (pipe != null)
				pipe.write(__b);
		}
		
		// Check if a row is to be printed
		finally
		{
			// Print entire row
			if (at == cols)
				this.__printLine();
		}
	}
	
	/**
	 * Prints a hexadecimal dumped line.
	 *
	 * @throws IOException If it could not be written.
	 * @since 2016/08/12
	 */
	private void __printLine()
		throws IOException
	{
		// Might not always work
		int cols = HexDumpOutputStream._COLUMNS;
		byte[] queue = this._queue;
		int at = this._at;
		try
		{
			Writer w = this.dump;
			
			// Print starting hex data
			for (int i = 0; i < cols; i++)
			{
				// Padding
				if (i > 0)
				{
					w.write(' ');
				
					// Extra space?
					if ((i & 3) == 0)
						w.write(' ');
				}
				
				// No data?
				if (i >= at)
				{
					w.write("  ");
					continue;
				}
				
				// Write both bytes
				int x = queue[i] & 0xFF;
				w.write(Character.forDigit(((x >>> 4) & 0xF), 16));
				w.write(Character.forDigit((x & 0xF), 16));
			}
			
			// Print ASCII version of it
			w.write("  |");
			for (int i = 0; i < cols; i++)
			{
				// No data?
				if (i >= at)
					break;	
				
				// Only print in the range of ASCII
				char c = (char)(queue[i] & 0xFF);
				if (c < ' ' || c > 0x7E)
					c = '.';
				
				w.write(c);
			}
			w.write('|');
			w.write('\n');
		}
	
		// Always clear the position
		finally
		{
			this._at = 0;
		}
	}
	
	/**
	 * Dumps the resulting input stream to the given stream.
	 *
	 * @param __dump The target stream.
	 * @param __in The stream to dump.
	 * @return If the dump completed without an {@link IOException}.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/021
	 */
	public static boolean dump(PrintStream __dump, InputStream __in)
		throws NullPointerException
	{
		if (__dump == null || __in == null)
			throw new NullPointerException("NARG");
		
		// Open dump output
		try (HexDumpOutputStream hdos = new HexDumpOutputStream(__dump))
		{
			byte[] buf = new byte[24];
			for (;;)
			{
				int rc = __in.read(buf);
				
				if (rc < 0)
					break;
				
				hdos.write(buf, 0, rc);
			}
		}
		
		// Failed
		catch (IOException e)
		{
			return false;
		}
		
		// Okay
		return true;
	}
	
	/**
	 * Dumps the resulting byte array to the given stream.
	 *
	 * @param __dump The target stream.
	 * @param __b The byte array to dump.
	 * @return If the dump completed without an {@link IOException}.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/03/26
	 */
	public static boolean dump(PrintStream __dump, byte[] __b)
		throws NullPointerException
	{
		if (__dump == null || __b == null)
			throw new NullPointerException("NARG");
		
		return HexDumpOutputStream.dump(__dump, __b, 0, __b.length);
	}	
	
	/**
	 * Dumps the resulting byte array to the given stream.
	 *
	 * @param __dump The target stream.
	 * @param __b The byte array to dump.
	 * @param __o The offset.
	 * @param __l The length.
	 * @return If the dump completed without an {@link IOException}.
	 * @throws IndexOutOfBoundsException If the offset and/or length are
	 * negative or exceed the array bounds.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/03/26
	 */
	public static boolean dump(PrintStream __dump, byte[] __b, int __o,
		int __l)
		throws IndexOutOfBoundsException, NullPointerException
	{
		if (__dump == null || __b == null)
			throw new NullPointerException("NARG");
		
		// Open dump output
		try (HexDumpOutputStream hdos = new HexDumpOutputStream(__dump))
		{
			hdos.write(__b, __o, __l);
		}
		
		// Failed
		catch (IOException e)
		{
			return false;
		}
		
		// Okay
		return true;
	}
}

