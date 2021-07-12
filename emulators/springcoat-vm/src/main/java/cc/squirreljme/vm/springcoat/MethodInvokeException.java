// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.springcoat;

import cc.squirreljme.runtime.cldc.debug.CallTraceElement;
import cc.squirreljme.runtime.cldc.debug.CallTraceUtils;
import java.io.PrintStream;

/**
 * This wraps an exception that was thrown within an invoked method.
 *
 * @since 2020/09/15
 */
public class MethodInvokeException
	extends SpringException
{
	/** The exception thrown. */
	public final SpringObject exception;
	
	/** The stack trace of this invocation. */
	private final CallTraceElement[] _stackTrace;
	
	/**
	 * Initializes the invoked exception.
	 *
	 * @param __message The message.
	 * @param __exception The exception that was tossed.
	 * @param __stackTrace The stack trace for this call.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/09/15
	 */
	public MethodInvokeException(String __message, SpringObject __exception,
		CallTraceElement[] __stackTrace)
		throws NullPointerException
	{
		super(__message);
		
		if (__exception == null)
			throw new NullPointerException("NARG");
		
		this.exception = __exception;
		this._stackTrace = __stackTrace;
	}
	
	/**
	 * Prints the virtual machine trace.
	 * 
	 * @param __out The stream to print to.
	 * @since 2021/02/07
	 */
	protected void printVmTrace(PrintStream __out)
	{
		CallTraceUtils.printStackTrace(__out,
			this.getMessage(), this._stackTrace, null,
			null, 0);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/09/15
	 */
	@Override
	public final String toString()
	{
		return this.exception.toString();
	}
	
	/**
	 * Returns the virtual machine trace.
	 * 
	 * @return The virtual machine trace.
	 * @since 2021/02/07
	 */
	public final CallTraceElement[] vmTrace()
	{
		return this._stackTrace.clone();
	}
}
