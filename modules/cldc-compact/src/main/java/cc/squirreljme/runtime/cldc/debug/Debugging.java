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
import cc.squirreljme.jvm.mle.RuntimeShelf;
import cc.squirreljme.jvm.mle.TerminalShelf;
import cc.squirreljme.jvm.mle.ThreadShelf;
import cc.squirreljme.jvm.mle.brackets.TracePointBracket;
import cc.squirreljme.jvm.mle.constants.StandardPipeType;
import cc.squirreljme.jvm.mle.constants.VMType;
import cc.squirreljme.runtime.cldc.io.ConsoleOutputStream;
import cc.squirreljme.runtime.cldc.lang.LineEndingUtils;
import java.io.PrintStream;

/**
 * This class contains all of the static methods which are for writing debug
 * output and failing accordingly on incomplete code. All the methods here for
 * the most part do not touch the standard {@link System#err} and
 * {@link System#out} (which use {@link PrintStream}), as in the event those
 * have a bug or otherwise issues can occur. The methods here should be
 * reliable enough on their own to convey a message accordingly.
 *
 * @since 2020/03/21
 */
public final class Debugging
{
	/** Only bytes up to this value are permitted in the output. */
	private static final int _BYTE_LIMIT =
		0x7E;
	
	/** Exit status for TODOs. */
	private static final int _TODO_EXIT_STATUS =
		63;
	
	/** This will be set when TODOs are tripped, to prevent infinite loops. */
	@SuppressWarnings("StaticVariableMayNotBeInitialized")
	private static volatile boolean _tripped;
	
	/**
	 * Not used.
	 *
	 * @since 2020/03/21
	 */
	private Debugging()
	{
	}
	
	/**
	 * Emits a debugging note.
	 *
	 * @param __fmt The format.
	 * @since 2020/05/13
	 */
	public static void debugNote(String __fmt)
	{
		Debugging.__format('D', 'B', __fmt, (Object[])null);
	}
	
	/**
	 * Emits a debugging note.
	 *
	 * @param __fmt The format.
	 * @param __args The arguments to the string.
	 * @since 2020/03/27
	 */
	public static void debugNote(String __fmt, Object... __args)
	{
		Debugging.__format('D', 'B', __fmt, __args);
	}
	
	/**
	 * Emits a notice
	 *
	 * @param __fmt The format.
	 * @param __args The arguments to the string.
	 * @since 2021/01/18
	 */
	public static void notice(String __fmt, Object... __args)
	{
		Debugging.__format('\0', '\0', __fmt, __args);
	}
	
	/**
	 * Emits an oops error.
	 *
	 * @return The generated error.
	 * @since 2020/12/31
	 */
	public static Error oops()
	{
		return Debugging.todo();
	}
	
	/**
	 * Emits an oops error.
	 *
	 * @param __args Argument to the error.
	 * @return The generated error.
	 * @since 2020/03/22
	 */
	public static Error oops(Object... __args)
	{
		return Debugging.todo(__args);
	}
	
	/**
	 * Prints the given character.
	 *
	 * @param __c The character to print.
	 * @since 2020/05/07
	 */
	@SuppressWarnings({"SameParameterValue"})
	public static void print(char __c)
	{
		Debugging.print(__c, '\0');
	}
	
	/**
	 * Prints the given characters.
	 *
	 * @param __c The character to print.
	 * @param __d Second character to print.
	 * @since 2020/05/07
	 */
	@SuppressWarnings("FeatureEnvy")
	public static void print(char __c, char __d)
	{
		// If we are on standard Java SE, use the System.err for output
		if (RuntimeShelf.vmType() == VMType.JAVA_SE)
		{
			System.err.print(__c);
			if (__d > 0)
				System.err.print(__d);
			
			return;
		}
		
		// Use standard SquirrelJME output
		TerminalShelf.write(StandardPipeType.STDERR,
			(__c > Debugging._BYTE_LIMIT ? '?' : __c));
		
		if (__d > 0)
			TerminalShelf.write(StandardPipeType.STDERR,
				(__d > Debugging._BYTE_LIMIT ? '?' : __d));
	}
	
	/**
	 * Emits a To-Do error.
	 *
	 * @return The generated error.
	 * @since 2020/03/21
	 */
	public static Error todo()
	{
		return Debugging.todo((Object[])null);
	}
	
	/**
	 * Emits a To-Do error.
	 *
	 * @param __args Arguments to the error.
	 * @return The generated error.
	 * @since 2020/03/21
	 */
	@SuppressWarnings("StaticVariableUsedBeforeInitialization")
	public static Error todo(Object... __args)
	{
		// Only trip this once! In the event this trips twice, just shortcut
		// with an exception otherwise
		if (Debugging._tripped)
		{
			// There was a To-Do on this To-Do, so need to report it instead
			// of just exiting!
			Debugging.todoNote("TODO TRIPPED IN TODO HANDLER: ");
			
			// Toss up and see what happens here
			return new Error("Recursive TODO");
		}
		Debugging._tripped = true;
		
		// This try is here so that in event this fails or throws another
		// exception, we always terminal no matter what
		boolean stackTracePrinted = false;
		try
		{
			// Print a very visible banner to not hide this information
			Debugging.todoNote(
				"*****************************************");
			Debugging.todoNote("INCOMPLETE CODE HAS BEEN REACHED: ");
			
			// If running on Java SE use it's method of printing traces
			// because the SquirrelJME trace support may be missing
			if (RuntimeShelf.vmType() == VMType.JAVA_SE)
			{
				new Throwable("INCOMPLETE CODE").printStackTrace(System.err);
			}
			
			// Use SquirrelJME's method
			else
			{
				// Print the stack trace first like this so it does not
				// possibly get trashed
				TracePointBracket[] trace = DebugShelf.traceStack();
				CallTraceUtils.printStackTrace(
					new ConsoleOutputStream(StandardPipeType.STDERR),
					"INCOMPLETE CODE", trace,
					null, null, 0);
					
				// Report the To-Do trace so it is known to another program
				ThreadShelf.setTrace("INCOMPLETE CODE", trace);
			}
			stackTracePrinted = true;
			
			// Print all arguments passed afterwards, just in case
			if (__args != null)
			{
				Debugging.todoNote(
					"-----------------------------------------");
				
				int n = __args.length;
				for (int i = 0; i < n; i++)
					try
					{
						Debugging.todoNote("%d: %s", i, __args[i]);
					}
					catch (Throwable ignored)
					{
						// Just drop everything here, so we can try to print
						// as much as we can
					}
			}
			
			Debugging.todoNote(
				"*****************************************");
		}
		
		// In the event this happens, we can report it
		catch (Throwable t)
		{
			Debugging.todoNote("THROWABLE TOSSED IN TODO HANDLER!");
			
			// Report if we could not print the trace!
			if (!stackTracePrinted)
				Debugging.todoNote("COULD NOT PRINT STACK TRACE!");
			
			// Try to report what the throwable was
			try
			{
				// Report on it
				Debugging.todoNote("THROWABLE WAS...");
				Debugging.todoNote("    CLASS: %s", t.getClass());
				Debugging.todoNote("    MESSG: %s", t.getMessage());
				
				// Try to print the trace
				CallTraceUtils.printStackTrace(
					new ConsoleOutputStream(StandardPipeType.STDERR),
					t, 0);
			}
			
			// This might occur on the native system
			catch (LinkageError error)
			{
				Debugging.todoNote("WAS LINKAGE ERROR?");
				
				// Print the trace of the error and try to find the root
				// cause of it
				try
				{
					error.printStackTrace(System.err);
				}
				
				// Could not print that either
				catch (Throwable ignored)
				{
					// Report that this happened though 
					Debugging.todoNote("COULD NOT PRINT LINK TRACE!");
				}
			}
			
			// This is a point where everything is so wrong we cannot
			// do anything at all
			catch (Throwable ignored)
			{
				// Report that this happened though 
				Debugging.todoNote("COULD NOT PRINT BACKUP TRACE!");
			}
		}
		
		// Always try to exit at the end of the call, in the event another
		// exception is thrown
		finally
		{
			// Just exit directly so there is no way to continue, if we can
			try
			{
				System.exit(Debugging._TODO_EXIT_STATUS);
			}
			catch (SecurityException ignored)
			{
				// However just ignore this case if we cannot truly exit here
			}
		}
		
		// Throw normal error here
		throw new Error("TODO");
	}
	
	/**
	 * Emits a To-Do note.
	 *
	 * @param __fmt Format string.
	 * @since 2020/05/13
	 */
	public static void todoNote(String __fmt)
	{
		Debugging.__format('T', 'D', __fmt, (Object[])null);
	}
	
	/**
	 * Emits a To-Do note.
	 *
	 * @param __fmt Format string.
	 * @param __args Arguments.
	 * @since 2020/03/31
	 */
	public static void todoNote(String __fmt, Object... __args)
	{
		Debugging.__format('T', 'D', __fmt, __args);
	}
	
	/**
	 * Returns a To-Do for an object.
	 *
	 * @param <T> The type.
	 * @param __args The calling arguments.
	 * @return Never returns.
	 * @since 2020/04/09
	 */
	public static <T> T todoObject(Object... __args)
	{
		throw Debugging.todo(__args);
	}
	
	/**
	 * Prints formatted text to the console output.
	 *
	 * @param __cha First grouping character.
	 * @param __chb Second grouping character.
	 * @param __format Format string.
	 * @param __args Arguments.
	 * @since 2020/05/07
	 */
	@SuppressWarnings({"StaticVariableUsedBeforeInitialization"})
	private static void __format(char __cha, char __chb, String __format,
		Object... __args)
	{
		// Print otherwise
		try
		{
			// Print header marker, but only if it is used
			if (__cha != '\0' && __chb != '\0')
			{
				Debugging.print(__cha, __chb);
				Debugging.print(':', ' ');
			}
			
			// The specifier to print along with the field index
			boolean specifier = false,
				hasArgIndex = false,
				firstChar = false,
				usePrefix = false,
				zeroPadding = false;
			int argIndex = 0,
				baseArg = 0,
				width = -1,
				argCount = (__args == null ? 0 : __args.length);
			
			// Print down the format
			for (int i = 0, n = __format.length(); i < n; i++)
			{
				char c = __format.charAt(i);
				
				// Printing a specifier
				if (specifier)
				{
					// Ignore flags
					if (c == '-' || c == '+' ||
						c == ' ' || c == ',' || c == '(')
						continue;
					
					// Ignore precision
					else if (c == '.')
						continue;
					
					// Zero padded?
					else if (firstChar && c == '0')
						zeroPadding = true;
					
					// Prefix flag?
					else if (c == '#')
						usePrefix = true;
					
					// Could be width or argument index position
					else if ((c >= '1' && c <= '9') ||
						(!firstChar && c == '0'))
					{
						if (hasArgIndex)
						{
							if (width < 0)
								width = 0;
							width = (width * 10) + (c - '0');
							
							// If the width is still zero, then this is the
							// zero padding flag
							if (width == 0)
							{
								zeroPadding = true;
								width = -1;
							}
						}
						else
							argIndex = (argIndex * 10) + (c - '0');
					}
					
					// Is argument index
					else if (!hasArgIndex && c == '$')
						hasArgIndex = true;
					
					// Percent or newline
					else if (c == '%' || c == 'n')
					{
						if (c == '%')
							Debugging.print('%');
						else
							Debugging.__printLine();
						
						// Stop
						specifier = false;
					}
					
					// A type to be printed, probably
					else
					{
						// If no argument index was flagged, then this is
						// likely the width
						if (!hasArgIndex && argIndex > 0)
							width = argIndex;
						
						// Select argument to print
						int choice = (hasArgIndex ? argIndex : baseArg++);
						Object value = (choice < 0 || choice >= argCount ?
							null : __args[choice]);
						
						// Print its value
						if (value == null)
						{
							Debugging.print('n', 'u');
							Debugging.print('l', 'l');
						}
						
						// A string printed value
						else
						{
							String string;
							
							// Hex sequence
							if (c == 'x' || c == 'X')
							{
								string = (usePrefix ? "0x" : "") +
									Long.toString(((Number)value).longValue(),
										16);
								
								if (c == 'X')
									string = string.toUpperCase();
							}
							
							// Octal
							else if (c == 'o')
								string = Long.toString(
									((Number)value).longValue(), 8);
								
							// Assume string
							else
								string = value.toString();
							
							// Print left padding?
							int strLen = string.length(),
								pad = width - strLen;
							while ((pad--) > 0)
								if (zeroPadding)
									Debugging.print('0');
								else
									Debugging.print(' ');
							
							// Print actual string
							for (int j = 0; j < strLen; j++)
								Debugging.print(string.charAt(j));
						}
						
						// Stop and reset
						specifier = false;
						hasArgIndex = false;
						usePrefix = false;
						zeroPadding = false;
						width = -1;
						argIndex = -1;
					}
					
					// No longer will be the first character
					if (firstChar)
						firstChar = false;
				}
				
				// Format specifier?
				else if (c == '%')
				{
					specifier = true;
					firstChar = true;
					argIndex = 0;
				}
				
				// Plain character?
				else
					Debugging.print(c);
			}
			
			// End of line
			Debugging.__printLine();
		}
		
		// Hopefully this does not happen but just in case, we want to catch
		// any exceptions that are tossed while printing format strings
		catch (Throwable t)
		{
			// Indicate this has occurred
			Debugging.print('X');
			Debugging.print('X');
			
			// End of line
			Debugging.__printLine();
		}
	}
	
	/**
	 * Prints the given character.
	 *
	 * @param __c The character to print.
	 * @since 2020/05/07
	 */
	@SuppressWarnings({"SameParameterValue"})
	private static void __print(char __c)
	{
		Debugging.__print(__c, '\0');
	}
	
	/**
	 * Prints the given characters.
	 *
	 * @param __c The character to print.
	 * @param __d Second character to print.
	 * @since 2020/05/07
	 */
	@SuppressWarnings("FeatureEnvy")
	private static void __print(char __c, char __d)
	{
		// If we are on standard Java SE, use the System.err for output
		if (RuntimeShelf.vmType() == VMType.JAVA_SE)
		{
			System.err.print(__c);
			if (__d > 0)
				System.err.print(__d);
			System.err.flush();
			
			return;
		}
		
		// Use standard SquirrelJME output
		TerminalShelf.write(StandardPipeType.STDERR,
			(__c > Debugging._BYTE_LIMIT ? '?' : __c));
		
		if (__d > 0)
			TerminalShelf.write(StandardPipeType.STDERR,
				(__d > Debugging._BYTE_LIMIT ? '?' : __d));
	}
	
	/**
	 * Prints end of line.
	 *
	 * @since 2020/05/07
	 */
	private static void __printLine()
	{
		int lineType = RuntimeShelf.lineEnding();
		for (int i = 0;; i++)
		{
			char c = LineEndingUtils.toChar(lineType, i);
			
			if (c == 0)
				break;
			
			Debugging.print(c, '\0');
		}
	}
}
