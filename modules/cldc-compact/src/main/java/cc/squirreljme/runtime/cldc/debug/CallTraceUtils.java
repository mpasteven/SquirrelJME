// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.runtime.cldc.debug;

import cc.squirreljme.jvm.mle.DebugShelf;
import cc.squirreljme.jvm.mle.brackets.TracePointBracket;
import cc.squirreljme.runtime.cldc.lang.LineEndingUtils;
import java.io.IOException;

/**
 * Utilities to use for printing call traces and other related methods.
 *
 * SquirrelJME uses compactified traces which are smaller and easier to read
 * accordingly.
 *
 * @since 2020/06/11
 */
public final class CallTraceUtils
{
	/**
	 * Not used.
	 *
	 * @since 2020/06/11
	 */
	private CallTraceUtils()
	{
	}
	
	/**
	 * Prints the given stack trace.
	 *
	 * @param __out The stream to write to.
	 * @param __toss The throwable to print.
	 * @param __indentLevel The indentation level to use.
	 * @return If trace printing failed with an {@link IOException}.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/06/11
	 */
	public static boolean printStackTrace(Appendable __out, Throwable __toss,
		int __indentLevel)
		throws NullPointerException
	{
		if (__out == null)
			throw new NullPointerException("NARG");
		
		return CallTraceUtils.printStackTrace(__out, __toss.toString(),
			DebugShelf.getThrowableTrace(__toss), __toss.getCause(),
			__toss.getSuppressed(), __indentLevel);
	}
	
	/**
	 * Prints the given stack trace to the output, resolving all of the entries
	 * before printing.
	 *
	 * @param __out The stream to write to.
	 * @param __message Exception message.
	 * @param __trace The trace to be printed.
	 * @param __cause The cause of this exception.
	 * @param __suppressed Suppressed exceptions.
	 * @param __indentLevel The indentation level to use.
	 * @return If trace printing failed with an {@link IOException}.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/06/11
	 */
	public static boolean printStackTrace(Appendable __out, String __message,
		TracePointBracket[] __trace, Throwable __cause,
		Throwable[] __suppressed, int __indentLevel)
		throws NullPointerException
	{
		if (__out == null)
			throw new NullPointerException("NARG");
		
		return CallTraceUtils.printStackTrace(__out, __message,
			CallTraceUtils.resolveAll(__trace), __cause, __suppressed,
			__indentLevel);
	}
	
	/**
	 * Prints the given stack trace to the output.
	 *
	 * @param __out The stream to write to.
	 * @param __message The exception message.
	 * @param __trace The trace to be printed.
	 * @param __cause The cause of this exception.
	 * @param __suppressed The suppressed exceptions.
	 * @param __indentLevel The indentation level to use.
	 * @return If trace printing failed with an {@link IOException}.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/06/11
	 */
	public static boolean printStackTrace(Appendable __out, String __message,
		CallTraceElement[] __trace, Throwable __cause,
		Throwable[] __suppressed, int __indentLevel)
		throws NullPointerException
	{
		if (__out == null)
			throw new NullPointerException("NARG");
		
		// We do not want any IOExceptions to cause stack trace printing to
		// fail, since this could potentially lead to a very nasty infinite
		// exception loop
		int lockStep = 0;
		try
		{
			// If there is no actual trace then just print that this is the
			// case and stop printing right away
			if (__trace == null)
			{
				__out.append("<No stack trace>");
				return false;
			}
			
			// Print the initial message of the exception
			CallTraceUtils.__appendIndent(__out, __indentLevel);
			__out.append("EXCEPTION ");
			
			// Count step
			lockStep++;
			
			// And that message could be blank!
			if (__message != null)
				__out.append(__message);
			LineEndingUtils.append(__out);
			
			// Set sub-indentation level
			int subLevel = __indentLevel + 1;
			
			// Count step
			lockStep++;
			
			// Print each element in the trace, the start of the trace is
			// always the top-most entry
			String lastClass = "<Unknown>";
			for (CallTraceElement current : __trace)
			{
				// Get the current class to detect if it has changed
				String nowClass = current.className();
				if (nowClass == null)
					nowClass = "<Unknown>";
				
				// If the class changed, specify the class which the trace
				// point resides in now
				if (!nowClass.equals(lastClass))
				{
					CallTraceUtils.__appendIndent(__out, subLevel);
					
					__out.append(" IN ");
					__out.append(current.toClassHeaderString());
					
					LineEndingUtils.append(__out);
					
					// If we stay in the same class, then we will not print
					// this header again
					lastClass = nowClass;
				}
				
				CallTraceUtils.__appendIndent(__out, subLevel);
				
				__out.append("- ");
				__out.append(current.toAtLineString());
				
				LineEndingUtils.append(__out);
				
				// Count step
				lockStep++;
			}
			
			// Count step
			lockStep++;
			
			// Print the exception cause, if any
			if (__cause != null)
			{
				// Starting sequence
				CallTraceUtils.__appendIndent(__out, subLevel);
				__out.append("CAUSED BY:");
				LineEndingUtils.append(__out);
				
				// Recursively enter the printing for this
				CallTraceUtils.printStackTrace(__out, __cause,
					subLevel + 1);
			}
			
			// Count step
			lockStep++;
			
			// Then print any suppressed exceptions
			if (__suppressed != null)
				for (Throwable __throwable : __suppressed)
				{
					// Starting sequence
					CallTraceUtils.__appendIndent(__out, subLevel);
					__out.append("SUPPRESSED:");
					LineEndingUtils.append(__out);
					
					// Recursively enter the printing for this
					CallTraceUtils.printStackTrace(__out, __throwable,
						subLevel + 1);
				}
		}
		
		// Ignore these completely, should hopefully not happen if the
		// Appendable we are writing to is a PrintStream or StringBuilder.
		catch (Throwable e)
		{
			// Add indication that there was a double fault here
			try
			{
				CallTraceUtils.__appendIndent(__out, __indentLevel);
				__out.append("FAULT IN printStackTrace()!");
				LineEndingUtils.append(__out);
			}
			
			// Give up
			catch (Throwable ignored)
			{
				try
				{
					__out.append('%');
					__out.append((char)('0' + lockStep));
					__out.append('%');
				}
				catch (Throwable ignored2)
				{
				}
			}
			
			// Just report that this happened
			return true;
		}
		
		// All other cases means no exception was thrown
		return false;
	}
	
	/**
	 * Resolves a single trace element.
	 *
	 * @param __point The single trace point to resolve.
	 * @return The resolved trace point.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/06/11
	 */
	public static CallTraceElement resolve(TracePointBracket __point)
		throws NullPointerException
	{
		if (__point == null)
			throw new NullPointerException("NARG");
		
		return new CallTraceElement(
			DebugShelf.pointClass(__point),
			DebugShelf.pointMethodName(__point),
			DebugShelf.pointMethodType(__point),
			DebugShelf.pointAddress(__point),
			DebugShelf.pointFile(__point),
			DebugShelf.pointLine(__point),
			DebugShelf.pointJavaOperation(__point),
			DebugShelf.pointJavaAddress(__point));
	}
	
	/**
	 * Resolves all entries in the stack trace.
	 *
	 * @param __trace The trace to resolve.
	 * @return The entire resolved trace.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/06/11
	 */
	public static CallTraceElement[] resolveAll(TracePointBracket[] __trace)
		throws NullPointerException
	{
		if (__trace == null)
			throw new NullPointerException("NARG");
		
		int n = __trace.length;
		CallTraceElement[] rv = new CallTraceElement[n];
		
		// Resolve each one individually
		for (int i = 0; i < n; i++)
			rv[i] = CallTraceUtils.resolve(__trace[i]);
		
		return rv;
	}
	
	/**
	 * Appends an indentation sequence to the output.
	 *
	 * @param __out The output source.
	 * @param __indentLevel The indentation level to use.
	 * @throws IOException If outputting caused an exception.
	 * @since 2020/06/11
	 */
	private static void __appendIndent(Appendable __out, int __indentLevel)
		throws IOException
	{
		// Doing nothing
		if (__indentLevel == 0)
			return;
		
		// Base space indent that is always there
		__out.append(' ');
		__out.append(' ');
		
		// Print bars for indentation level
		for (int i = 0; i < __indentLevel; i++)
			__out.append('|');
	}
}
