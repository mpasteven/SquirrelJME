// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.summercoat.lle;

import cc.squirreljme.jvm.mle.brackets.TracePointBracket;

/**
 * Trace point information.
 *
 * @since 2021/04/03
 */
final class __LLETracePoint__
	implements TracePointBracket
{
	/** The address of the point. */
	public final long pcAddr;
	
	/** The Java Address. */
	public final int javaPcAddr;
	
	/** The Java operation. */
	public final int javaOperation;
	
	/** The source line. */
	public final int sourceLine;
	
	/** The class name pointer. */
	public final long classNameP;
	
	/** The method name pointer. */
	public final long methodNameP;
	
	/** The method type pointer. */
	public final long methodTypeP;
	
	/** The source file pointer. */
	public final long sourceFileP;
	
	/** The task identifier. */
	public final int taskId;
	
	/**
	 * Initializes the trace point.
	 * 
	 * @param __classNameP The class name.
	 * @param __methodNameP The method name.
	 * @param __methodTypeP The method type.
	 * @param __sourceFileP The source file.
	 * @param __sourceLine The source line.
	 * @param __pcAddr The PC address.
	 * @param __javaOperation The Java Operation.
	 * @param __javaPcAddr The Java PC address.
	 * @param __taskId The task identifier.
	 * @since 2021/04/03
	 */
	__LLETracePoint__(long __classNameP, long __methodNameP,
		long __methodTypeP, long __sourceFileP, int __sourceLine,
		long __pcAddr, int __javaOperation, int __javaPcAddr, int __taskId)
	{
		this.classNameP = __classNameP;
		this.methodNameP = __methodNameP;
		this.methodTypeP = __methodTypeP;
		this.sourceFileP = __sourceFileP;
		this.sourceLine = __sourceLine;
		this.pcAddr = __pcAddr;
		this.javaOperation = __javaOperation;
		this.javaPcAddr = __javaPcAddr;
		this.taskId = __taskId;
	}
}
