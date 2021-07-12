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
import cc.squirreljme.jvm.CallStackItem;
import cc.squirreljme.jvm.mle.brackets.TracePointBracket;
import cc.squirreljme.jvm.mle.constants.VerboseDebugFlag;
import cc.squirreljme.jvm.mle.exceptions.MLECallError;
import cc.squirreljme.jvm.summercoat.LogicHandler;
import cc.squirreljme.jvm.summercoat.SummerCoatUtil;
import cc.squirreljme.jvm.summercoat.SystemCall;
import cc.squirreljme.jvm.summercoat.constants.StaticVmAttribute;

/**
 * This is the shelf used for accessing the debugging features of SquirrelJME
 * along with miscellaneous debugging utilities.
 *
 * @since 2020/06/11
 */
@SuppressWarnings("unused")
public final class LLEDebugShelf
{
	/**
	 * Not used.
	 *
	 * @since 2020/06/11
	 */
	private LLEDebugShelf()
	{
	}
	
	/**
	 * Returns the trace that is part of the given throwable.
	 *
	 * @param __t The throwable to gets it's trace of.
	 * @return The trace that was within the given throwable.
	 * @since 2020/06/11
	 */
	public static TracePointBracket[] getThrowableTrace(Throwable __t)
	{
		if (__t == null)
			throw new MLECallError("NARG");
		
		// This is just a field read
		return (TracePointBracket[])Assembly.memHandleReadObject(__t,
			LogicHandler.staticVmAttribute(
			StaticVmAttribute.OFFSETOF_THROWABLE_TRACE_FIELD));
	}
	
	/**
	 * Resolves the address from the given point.
	 *
	 * @param __point The point to resolve.
	 * @return The address.
	 * @since 2020/06/16
	 */
	public static long pointAddress(TracePointBracket __point)
	{
		if (__point == null)
			throw new MLECallError("NARG");
		
		return ((__LLETracePoint__)__point).pcAddr;
	}
	
	/**
	 * Resolves the class from the given point.
	 *
	 * @param __point The point to resolve.
	 * @return The class.
	 * @since 2020/06/16
	 */
	public static String pointClass(TracePointBracket __point)
	{
		if (__point == null)
			throw new MLECallError("NARG");
		
		return SummerCoatUtil.loadString(
			((__LLETracePoint__)__point).classNameP);
	}
	
	/**
	 * Resolves the file from the given point.
	 *
	 * @param __point The point to resolve.
	 * @return The file.
	 * @since 2020/06/16
	 */
	public static String pointFile(TracePointBracket __point)
	{
		if (__point == null)
			throw new MLECallError("NARG");
		
		return SummerCoatUtil.loadString(
			((__LLETracePoint__)__point).sourceFileP);
	}
	
	/**
	 * Resolves the Java address from the given point.
	 *
	 * @param __point The point to resolve.
	 * @return The Java address.
	 * @since 2020/06/16
	 */
	public static int pointJavaAddress(TracePointBracket __point)
	{
		if (__point == null)
			throw new MLECallError("NARG");
		
		return ((__LLETracePoint__)__point).javaPcAddr;
	}
	
	/**
	 * Resolves the Java operation from the given point.
	 *
	 * @param __point The point to resolve.
	 * @return The Java operation.
	 * @since 2020/06/16
	 */
	public static int pointJavaOperation(TracePointBracket __point)
	{
		if (__point == null)
			throw new MLECallError("NARG");
		
		return ((__LLETracePoint__)__point).javaOperation;
	}
	
	/**
	 * Resolves the line from the given point.
	 *
	 * @param __point The point to resolve.
	 * @return The line.
	 * @since 2020/06/16
	 */
	public static int pointLine(TracePointBracket __point)
	{
		if (__point == null)
			throw new MLECallError("NARG");
		
		return ((__LLETracePoint__)__point).sourceLine;
	}
	
	/**
	 * Resolves the method name from the given point.
	 *
	 * @param __point The point to resolve.
	 * @return The method name.
	 * @since 2020/06/16
	 */
	public static String pointMethodName(TracePointBracket __point)
	{
		if (__point == null)
			throw new MLECallError("NARG");
		
		return SummerCoatUtil.loadString(
			((__LLETracePoint__)__point).methodNameP);
	}
	
	/**
	 * Resolves the method type from the given point.
	 *
	 * @param __point The point to resolve.
	 * @return The method type.
	 * @since 2020/06/16
	 */
	public static String pointMethodType(TracePointBracket __point)
	{
		if (__point == null)
			throw new MLECallError("NARG");
		
		return SummerCoatUtil.loadString(
			((__LLETracePoint__)__point).methodTypeP);
	}
	
	/**
	 * Traces the entire stack, the top-most trace point in the stack is always
	 * first.
	 *
	 * @return The stack trace.
	 * @since 2020/06/11
	 */
	public static TracePointBracket[] traceStack()
	{
		// How many frames are on the stack? Skip the first two because it is
		// this method and the other one
		int frameCount = SystemCall.callStackHeight() - 2;
		TracePointBracket[] rv = new TracePointBracket[frameCount];
		
		// Load in each individual frame
		for (int i = 0; i < frameCount; i++)
			rv[i] = LLEDebugShelf.__loadFrame(i + 2);
		
		return rv;
	}
	
	/**
	 * Starts performing verbose virtual machine outputs for debugging.
	 * 
	 * This method may or may not have an actual effect.
	 * 
	 * When the calling method exits, verbosity should be terminated although
	 * it is not required. This is so that outer calls do not cause verbosity
	 * to happen.
	 * 
	 * @param __flags The {@link VerboseDebugFlag}.
	 * @return An integer to be passed to
	 * {@link cc.squirreljme.jvm.mle.DebugShelf#verboseStop(int)}.
	 * @since 2020/07/11
	 */
	public static int verbose(int __flags)
	{
		return SystemCall.verbose(-1, __flags, 0);
	}
	
	/**
	 * Stops performing verbosity output.
	 * 
	 * @param __code The previous verbosity state.
	 * @since 2020/07/11
	 */
	public static void verboseStop(int __code)
	{
		SystemCall.verbose(-1, 0, __code);
	}
	
	/**
	 * Loads the given frame.
	 * 
	 * @param __f The frame to load.
	 * @return The trace point for the given frame.
	 * @since 2021/04/03
	 */
	private static TracePointBracket __loadFrame(int __f)
	{
		return new __LLETracePoint__(
			SystemCall.callStackItem(__f, CallStackItem.CLASS_NAME),
			SystemCall.callStackItem(__f, CallStackItem.METHOD_NAME),
			SystemCall.callStackItem(__f, CallStackItem.METHOD_TYPE),
			SystemCall.callStackItem(__f, CallStackItem.SOURCE_FILE),
			(int)SystemCall.callStackItem(__f, CallStackItem.SOURCE_LINE),
			SystemCall.callStackItem(__f, CallStackItem.PC_ADDRESS),
			(int)SystemCall.callStackItem(__f, CallStackItem.JAVA_OPERATION),
			(int)SystemCall.callStackItem(__f, CallStackItem.JAVA_PC_ADDRESS),
			(int)SystemCall.callStackItem(__f, CallStackItem.TASK_ID));
	}
}
