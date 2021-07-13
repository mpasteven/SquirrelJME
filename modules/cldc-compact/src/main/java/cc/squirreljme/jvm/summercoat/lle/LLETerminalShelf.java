// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.summercoat.lle;

import cc.squirreljme.jvm.Assembly;
import cc.squirreljme.jvm.mle.TerminalShelf;
import cc.squirreljme.jvm.mle.constants.PipeErrorType;
import cc.squirreljme.jvm.mle.constants.StandardPipeType;
import cc.squirreljme.jvm.mle.exceptions.MLECallError;
import cc.squirreljme.jvm.summercoat.SystemCall;
import cc.squirreljme.runtime.cldc.debug.Debugging;

/**
 * Low-level {@link TerminalShelf}.
 *
 * @since 2020/06/14
 */
public final class LLETerminalShelf
{
	/**
	 * Not used.
	 *
	 * @since 2002/06/14
	 */
	private LLETerminalShelf()
	{
	}
	
	/**
	 * Returns the number of available bytes for reading, if it is known.
	 * 
	 * @param __fd The {@link StandardPipeType} to close.
	 * @return The number of bytes ready for immediate reading, will be
	 * zero if there are none. For errors one of {@link PipeErrorType}.
	 * @throws MLECallError If {@code __fd} is not valid.
	 * @since 2020/11/22
	 */
	public static int available(int __fd)
		throws MLECallError
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Closes the output of the current process.
	 * 
	 * @param __fd The {@link StandardPipeType} to close.
	 * @return One of {@link PipeErrorType}.
	 * @throws MLECallError If {@code __fd} is not valid.
	 * @since 2020/07/02
	 */
	public static int close(int __fd)
		throws MLECallError
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Flushes the stream.
	 *
	 * @param __fd The {@link StandardPipeType} to flush.
	 * @return One of {@link PipeErrorType}.
	 * @throws MLECallError If {@code __fd} is not valid.
	 * @since 2018/12/08
	 */
	public static int flush(int __fd)
		throws MLECallError
	{
		// {@squirreljme.error ZZ4y Could flush the pipe.}
		if (SystemCall.pdFlush(
			LLETerminalShelf.__pipeOfFd(__fd)) != PipeErrorType.NO_ERROR)
			throw new MLECallError("ZZ4y");
		
		return PipeErrorType.NO_ERROR;
	}
	
	/**
	 * Reads from the given pipe into the output buffer.
	 *
	 * @param __fd The {@link StandardPipeType} to read from.
	 * @param __b The bytes to read into.
	 * @param __o The offset.
	 * @param __l The length.
	 * @return One of {@link PipeErrorType} or the number of read bytes.
	 * @throws MLECallError If {@code __fd} is not valid, the offset and/or
	 * length are negative or exceed the buffer size, or {@code __b} is
	 * {@code null}.
	 * @since 2018/12/05
	 */
	public static int read(int __fd, byte[] __b, int __o, int __l)
		throws MLECallError
	{
		Assembly.breakpoint();
		throw Debugging.todo();
	}
	
	/**
	 * Writes the character to the console output.
	 *
	 * @param __fd The {@link StandardPipeType} to write to.
	 * @param __c The byte to write, only the lowest 8-bits are used.
	 * @return One of {@link PipeErrorType} or {@code 1} on success.
	 * @throws MLECallError If {@code __fd} is not valid.
	 * @since 2018/09/21
	 */
	public static int write(int __fd, int __c)
		throws MLECallError
	{
		// {@squirreljme.error ZZ4h Could not write single byte to pipe.}
		if (SystemCall.pdWriteByte(
			LLETerminalShelf.__pipeOfFd(__fd), __c) <= 0)
			throw new MLECallError("ZZ4h");
		
		return 1;
	}
	
	/**
	 * Writes the given bytes to the console output.
	 *
	 * @param __fd The {@link StandardPipeType} to write to.
	 * @param __b The bytes to write.
	 * @param __o The offset.
	 * @param __l The length.
	 * @return One of {@link PipeErrorType} or {@code __l} on success.
	 * @throws MLECallError If {@code __fd} is not valid, the offset and/or
	 * length are negative or exceed the buffer size, or {@code __b} is
	 * {@code null}.
	 * @since 2018/12/05
	 */
	public static int write(int __fd, byte[] __b, int __o, int __l)
		throws MLECallError
	{
		// {@squirreljme.error ZZ1i Invalid write arguments.}
		if (__b == null || __o < 0 || __l < 0 || (__o + __l) > __b.length)
			throw new MLECallError("ZZ1i");
		
		// Where are the bytes going?
		int scFd = LLETerminalShelf.__pipeOfFd(__fd);
		
		// Write all bytes to the output
		// {@squirreljme.error ZZ4i Could not write multiple bytes to pipe.}
		for (int i = 0; i < __l; i++)
			if (SystemCall.pdWriteByte(scFd, __b[__o + i]) <= 0)
				throw new MLECallError("ZZ4i");
		
		return __l;
	}
	
	/**
	 * Returns the pipe used for the file descriptor.
	 * 
	 * @param __fd The {@link StandardPipeType}.
	 * @return The standard pipe ID for SummerCoat's pipes.
	 * @throws MLECallError If the pipe is not valid.
	 * @since 2020/11/28
	 */
	private static int __pipeOfFd(int __fd)
		throws MLECallError
	{
		switch (__fd)
		{
			case StandardPipeType.STDIN:
				return SystemCall.pdOfStdIn();
				
			case StandardPipeType.STDOUT:
				return SystemCall.pdOfStdOut();
				
			case StandardPipeType.STDERR:
				return SystemCall.pdOfStdErr();
			
				// {@squirreljme.error ZZ4g Could not map a standard pipe
				// descriptor. (The file descriptor)}
			default:
				throw new MLECallError("ZZ4g " + __fd);
		}
	}
}
