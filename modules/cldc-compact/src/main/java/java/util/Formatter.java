// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package java.util;

import cc.squirreljme.runtime.cldc.annotation.ImplementationNote;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;

/**
 * This class implements C-style {@code printf()} formatted for text, it is
 * inspired by the C language but it does not match it exactly and this is
 * far more strict in what it requires.
 *
 * Format specifiers start with the {@code '%'} character which defines a
 * sequence accordingly. The format specifiers are in the following format:
 * {@code %[argument_index$][flags][width][.precision]conversion}. Any fields
 * within brackets are optional. For any specifiers which do not conform to
 * arguments, their format is {@code %[flags][width]conversion}.
 *
 * {@code argument_index$} specifies which argument to take the value from,
 * this allows one to use an alternative order. If this is not specified then
 * the order is implied based on the argument order for any specifiers which
 * do not use an argument index. This means that:
 * {@code format("%7$d %d %6$d %d %% %d", 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)} will
 * print {@code "7 1 6 2 % 3"}.
 *
 * {@code flags} specifies special flags which are used to modify how the
 * printing is done. The following flags are used:
 *
 *  - {@code '-'} -- Left justified
 *    (general, char, int, float, date).
 *  - {@code '#'} -- Conversion dependent alternate form
 *    (general, int (only: o, x, X), float).
 *  - {@code '+'} -- Include a sign always
 *    (int, float).
 *  - {@code ' '} -- Include leading space for positive values
 *    (int, float).
 *  - {@code '0'} -- Zero padding
 *    (int, float).
 *  - {@code ','} -- Use locale specific grouping separators
 *    (int (only: d), float).
 *  - {@code '('} -- Negative values are enclosed in parenthesis
 *    (int, float).
 *
 * {@code width} is a positive decimal integer which specifies the minimum
 * amount of space the text will take up, it will be aligned accordingly
 * depending on the flags used.
 *
 * {@code precision} is a positive decimal integer which specified a limit
 * on the output, this depends on the format specifier.
 *
 * {@code conversion} is the symbol which determines how the input is to be
 * formatted.
 *
 * @since 2018/09/23
 */
@ImplementationNote("")
public final class Formatter
	implements Closeable
{
	/** The newline sequence. */
	private static final String _NEWLINE;
	
	/** The appendable to write to. */
	private final Appendable _out;
	
	/** Has this been closed? */
	private volatile boolean _closed;
	
	/** The IOException if any was generated. */
	private IOException _ioe;
	
	/**
	 * Cache the line separator which is derived from the system properties.
	 *
	 * @since 2018/10/10
	 */
	static
	{
		String nl;
		try
		{
			nl = System.getProperty("line.separator");
		}
		catch (SecurityException e)
		{
			nl = "\n";
		}
		
		_NEWLINE = nl;
	}
	
	/**
	 * Initializes a formatter which uses an internal {@link StringBuilder}.
	 *
	 * @since 2018/09/23
	 */
	public Formatter()
	{
		this._out = new StringBuilder();
	}
	
	/**
	 * Initializes the formatter writing to the given output.
	 *
	 * @param __a The output.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/09/23
	 */
	public Formatter(Appendable __a)
		throws NullPointerException
	{
		if (__a == null)
			throw new NullPointerException("NARG");
		
		this._out = __a;
	}
	
	/**
	 * Initializes the formatter writing to the given output.
	 *
	 * @param __ps The output.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/09/23
	 */
	public Formatter(PrintStream __ps)
		throws NullPointerException
	{
		this((Appendable)__ps);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/09/23
	 */
	@Override
	public void close()
	{
		// No effect?
		if (this._closed)
			return;
		
		// Set closed
		this._closed = true;
		
		// Can only be done if it is closeable
		Appendable out = this._out;
		if (out instanceof Closeable)
			try
			{
				((Closeable)out).close();
			}
			catch (IOException e)
			{
				this._ioe = e;
			}
	}
	
	/**
	 * Flushes the output formatter, if it is flushable.
	 *
	 * @throws IllegalStateException If this formatter was closed.
	 * @since 2018/09/23
	 */
	public void flush()
		throws IllegalStateException
	{
		// {@squirreljme.error ZZ2i The formatter has been closed.}
		if (this._closed)
			throw new IllegalStateException("ZZ2i");
		
		// Java ME has no Flushable interface so only certain classes have
		// the flush method
		try
		{
			Appendable out = this._out;
			if (out instanceof OutputStream)
				((OutputStream)out).flush();
			else if (out instanceof Writer)
				((Writer)out).flush();
		}
		
		// {@squirreljme.error ZZ2j Could not flush the formatter.}
		catch (IOException e)
		{
			throw new RuntimeException("ZZ2j", e);
		}
	}
	
	/**
	 * Prints formatted text to the output.
	 *
	 * @param __fmt The format specified.
	 * @param __args The input arguments.
	 * @return {@code this}.
	 * @throws IllegalArgumentException If the format specifiers are not
	 * valid.
	 * @throws IllegalStateException If this formatter has been closed.
	 * @throws NullPointerException If no format was specified.
	 * @since 2018/09/23
	 */
	public Formatter format(String __fmt, Object... __args)
		throws IllegalArgumentException, IllegalStateException,
			NullPointerException
	{
		if (__fmt == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error ZZ2k This formatter has been closed.}
		if (this._closed)
			throw new IllegalStateException("ZZ2k");
		
		// Force this to exist
		if (__args == null)
			__args = new Object[0];
		
		// Global state needed for argument handling.
		__PrintFGlobal__ pg = new __PrintFGlobal__(__args);
		
		// Writing to the appendable may cause an exception to occur
		try
		{
			// Process input characters
			Appendable out = this._out;
			for (int i = 0, n = __fmt.length(), next = 0; i < n; i = next)
			{
				char c = __fmt.charAt(i);
				
				// Just a normal character
				if (c != '%')
				{
					out.append(c);
					
					// Just skip the single character
					next = i + 1;;
					continue;
				}
				
				// It is simpler to handle the parsing of the specifier in
				// another method due to loops and variables
				__PrintFState__ pf = new __PrintFState__(pg);
				next = this.__specifier(pf, i, __fmt);
				
				// Handle output of the specifier
				this.__output(out, pf);
			}
		}
		
		// Catch any exception
		catch (IOException e)
		{
			this._ioe = e;
		}
		
		return this;
	}
	
	/**
	 * Returns the last {@link IOException} that was thrown by the output.
	 *
	 * @return The last exception thrown or {@code null} if one was never
	 * thrown.
	 * @since 2018/09/23
	 */
	public IOException ioException()
	{
		return this._ioe;
	}
	
	/**
	 * Returns the output.
	 *
	 * @return The output.
	 * @throws IllegalStateException If this formatter was closed.
	 * @since 2018/09/23
	 */
	public Appendable out()
		throws IllegalStateException
	{
		// {@squirreljme.error ZZ2l The formatter has been closed.}
		if (this._closed)
			throw new IllegalStateException("ZZ2l");
		
		return this._out;
	}
	
	/**
	 * This performs {@code this.out().toString()} and returns that result, as
	 * such the value here might not match what was written into the string.
	 *
	 * @return The string from the output.
	 * @throws IllegalStateException If this formatter was closed.
	 * @since 2018/09/23
	 */
	@Override
	public String toString()
		throws IllegalStateException
	{
		// {@squirreljme.error ZZ2m The formatter has been closed.}
		if (this._closed)
			throw new IllegalStateException("ZZ2m");
		
		return this._out.toString();
	}
	
	/**
	 * Formats integer number in decimal format.
	 *
	 * @param __pf The state used.
	 * @param __n The number used.
	 * @return The formatted integer number.
	 * @throws NullPointerException If no state is specified.
	 * @since 2018/11/03
	 */
	private static String __formatDecimalInt(__PrintFState__ __pf, Number __n)
		throws NullPointerException
	{
		if (__pf == null)
			throw new NullPointerException("NARG");
		
		// Null is used.
		if (__n == null)
			return null;
		
		// {@squirreljme.error ZZ3s Decimal integers cannot have an
		// alternative form.}
		if (__pf.__hasFlag(__PrintFFlag__.ALTERNATIVE_FORM))
			throw new IllegalArgumentException("ZZ3s");
		
		// Generate base number
		long value = __n.longValue();
		boolean neg = (value < 0);
		StringBuilder base = new StringBuilder(Long.toString(value));
		
		// Use local digit grouping
		if (__pf.__hasFlag(__PrintFFlag__.LOCALE_GROUPING))
			throw new todo.TODO();
		
		// Negative values can have flags (note negatives are always signed)
		boolean signed = neg;
		if (neg)
		{
			// Use parenthesis for negative instead
			if (__pf.__hasFlag(__PrintFFlag__.NEGATIVE_PARENTHESIS))
			{
				// Replace the sign with open parenthesis
				base.setCharAt(0, '(');
				
				// And add parenthesis to the end
				base.append(')');
			}
		}
		
		// Positive values have some flags
		else
		{
			// Always use the sign
			if (signed |= __pf.__hasFlag(__PrintFFlag__.ALWAYS_SIGNED))
				base.insert(0, '+');
			
			// Space for positive value
			else if (signed |= __pf.__hasFlag(
				__PrintFFlag__.SPACE_FOR_POSITIVE))
				base.insert(0, ' ');
		}
		
		// Padding with zero? Remember if there is a sign in place
		if (__pf.__hasFlag(__PrintFFlag__.ZERO_PADDED))
			for (int w = __pf.__width(), idx = (signed ? 1 : 0);
				base.length() < w;)
				base.insert(idx, '0');
		
		// Use this result
		return base.toString();
	}
	
	/**
	 * Formats other unsigned value.
	 *
	 * @param __pf Printing state.
	 * @param __n The number to print.
	 * @param __base The number's base.
	 * @throws 
	 */
	private static String __formatOtherUnsignedInt(__PrintFState__ __pf,
		Number __n, int __base)
		throws NullPointerException
	{
		if (__pf == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error ZZ3t Invalid flag specified for unsigned
		// formatted value.}
		if (__pf.__hasFlag(__PrintFFlag__.LOCALE_GROUPING) ||
			__pf.__hasFlag(__PrintFFlag__.NEGATIVE_PARENTHESIS) ||
			__pf.__hasFlag(__PrintFFlag__.ALWAYS_SIGNED) ||
			__pf.__hasFlag(__PrintFFlag__.SPACE_FOR_POSITIVE))
			throw new IllegalArgumentException("ZZ3t");
		
		// Get the long value of the given value, because it is limited to
		// the precision (byte:-2 becomes 0xFE)
		long input;
		if (__n instanceof Byte)
			input = __n.byteValue() & 0xFFL;
		else if (__n instanceof Short)
			input = __n.shortValue() & 0xFFFFL;
		else if (__n instanceof Integer)
			input = __n.intValue() & 0xFFFFFFFFL;
		else
			input = __n.longValue();
		
		// Bit shift and mask used for the value
		int shift, mask;
		switch (__pf._conv)
		{
			case OCTAL_INTEGER:
				mask = 0x7;
				shift = 3;
				break;
			
			default:
			case HEXADECIMAL_INTEGER:
				mask = 0xF;
				shift = 4;
				break;
		}
		
		// Build the output number, constantly shift it until it is zero
		StringBuilder out = new StringBuilder();
		for (long rest = input; rest != 0; rest >>>= shift)
		{
			// Mask out the value
			int val = (int)(rest & mask);
			
			// Add the character for this digit
			out.append(Character.forDigit(val, __base));
		}
		
		// Pad the remaining space with zero
		if (__pf.__hasFlag(__PrintFFlag__.ZERO_PADDED))
			for (int i = out.length(), n = __pf.__width(); i < n; i++)
				out.append('0');
		
		// Place in the alternative form somewhere
		if (__pf.__hasFlag(__PrintFFlag__.ALTERNATIVE_FORM))
		{
			// The length of the string
			int len = out.length();
			
			// Hex
			if (__base == 16)
			{
				// If the last two characters are zero, set the one before
				// last to X
				if (len >= 2 && out.charAt(len - 1) == '0' &&
					out.charAt(len - 2) == '0')
					out.setCharAt(len - 2, 'x');
				
				// If the last character is zero, then make it x and append
				// zero
				else if (len >= 1 && out.charAt(len - 1) == '0')
				{
					out.setCharAt(len - 1, 'x');
					out.append('0');
				}
				
				// Otherwise append 0x (reversed)
				else
				{
					out.append('x');
					out.append('0');
				}
			}
			
			// Octal
			else if (__base == 8)
			{
				// If the last character is not zero, then add zero to the end
				if (len < 1 && out.charAt(len - 1) != '0')
					out.append('0');
			}
		}
		
		// Reverse string because it is in the other order
		out.reverse();
		
		// Use it
		return out.toString();
	}
	
	/**
	 * Outputs to the state to the appendable.
	 *
	 * @param __out The output appendable.
	 * @param __pf The standard state.
	 * @throws IllegalArgumentException If the format is not valid.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/09/28
	 */
	private static void __output(Appendable __out, __PrintFState__ __pf)
		throws IllegalArgumentException, IOException, NullPointerException
	{
		if (__out == null || __pf == null)
			throw new NullPointerException("NARG");
			
		__PrintFConversion__ conv = __pf._conv;
		__PrintFCategory__ cat = conv.__category();
		
		// Depends on the conversion
		String append;
		switch (conv)
		{
				// Newline sequence
			case NEWLINE:
				append = Formatter._NEWLINE;
				break;
				
				// Boolean
			case BOOLEAN:
				append = __pf.<Boolean>__argument(Boolean.class,
					Boolean.FALSE).toString();
				break;
				
				// Single character
			case CHARACTER:
				Character cha = __pf.<Character>__argument(Character.class);
				append = (cha == null ? "null" : cha.toString());
				break;
				
				// Octal 
			case OCTAL_INTEGER:
				append = Formatter.__formatOtherUnsignedInt(__pf,
					__pf.<Number>__argument(Number.class), 8);
				break;
			
				// Decimal Integer
			case DECIMAL_INTEGER:
				append = Formatter.__formatDecimalInt(__pf,
					__pf.<Number>__argument(Number.class));
				break;
				
				// Hexadecimal Integer
			case HEXADECIMAL_INTEGER:
				append = Formatter.__formatOtherUnsignedInt(__pf,
					__pf.<Number>__argument(Number.class), 16);
				break;
			
				// Simple string conversion
			case STRING:
				append = __pf.<Object>__argument(Object.class, "null").
					toString();
				break;
			
				// {@squirreljme.error ZZ2n Unimplemented conversion.
				// (The conversion)}
			default:
				throw new todo.TODO("ZZ2n " + conv);
		}
		
		// Convert to uppercase
		if (__pf._upper)
			append = append.toUpperCase();
		
		// General items may be reduced by the precision, at most
		if (cat == __PrintFCategory__.GENERAL)
		{
			int precision = __pf._precision;
			if (precision >= 0)
				append = append.substring(0,
					Math.min(precision, append.length()));
		}
		
		// Width is either higher of the input width or the string length
		int strlen = append.length(),
			width = Math.max(strlen, __pf._width),
			padding = width - strlen;
		
		// Right justification spaces?
		boolean isleft = __pf.__isLeftJustified();
		if (!isleft)
			for (int i = 0; i < padding; i++)
				__out.append(' ');
		
		// Send the string in
		__out.append(append);
		
		// Left justification spaces?
		if (isleft)
			for (int i = 0; i < padding; i++)
				__out.append(' ');
	}
	
	/**
	 * Parses the specifier in the input format.
	 *
	 * This parses the following, just returning that information:
	 * {@code %[argument_index$][flags][width][.precision]conversion}.
	 *
	 * @param __pf The PrintF state.
	 * @param __base The base character index.
	 * @param __fmt The format specifiers.
	 * @return The index where the format specifier ends, this is so the
	 * calling loop can properly set its counter.
	 * @throws IllegalArgumentException If the format specifiers are not
	 * correct.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/09/23
	 */
	private static int __specifier(__PrintFState__ __pf, int __base,
		String __fmt)
		throws IllegalArgumentException, NullPointerException
	{
		if (__pf == null || __fmt == null)
			throw new NullPointerException("NARG");
		
		// Length of the format
		int fmtlen = __fmt.length();
		
		// The specifier could be too short! So handle these cases and make
		// sure the exception of the right type
		int at = __base + 1;
		try
		{
			char c = __fmt.charAt(at);
			
			// The first might be a number specifying the argument index to use
			// Note that zero is NOT included because it would be parsed as
			// a flag, additionally argument indexes are 1-based!
			if (c >= '1' && c <= '9')
			{
				// Read the entire number
				int base = at;
				for (;;)
				{
					c = __fmt.charAt(at);
					if (c >= '0' && c <= '9')
						at++;
					else
						break;
				}
				
				// Parse value
				int val = Integer.valueOf(__fmt.substring(base, at));
				
				// If there is a dollar sign, this is going to be the
				// argument index
				if (__fmt.charAt(at) == '$')
				{
					__pf.__setArgumentIndex(val);
					
					// Skip the dollar
					at++;
				}
				
				// Otherwise this is the width
				else
					__pf.__setWidth(val);
			}
			
			// Parse flags, but only if no width was specified
			if (!__pf.__hasWidth())
				for (c = __fmt.charAt(at); __pf.__setFlag(c);
					at++, c = __fmt.charAt(at))
					;
			
			// Parse width
			c = __fmt.charAt(at);
			if (!__pf.__hasWidth() && c >= '1' && c <= '9')
			{
				// Read the entire number
				int base = at;
				for (;;)
				{
					c = __fmt.charAt(at);
					if (c >= '0' && c <= '9')
						at++;
					else
						break;
				}
				
				// Set width
				__pf.__setWidth(Integer.valueOf(__fmt.substring(base, at)));
			}
			
			// Parse precision
			c = __fmt.charAt(at);
			if (c == '.')
				throw new todo.TODO();
			
			// Parse the conversion
			c = __fmt.charAt(at);
			if (c == 't' || c == 'T')
			{
				__pf.__setConversion(c, __fmt.charAt(at + 1));
				at++; 
			}
			else
				__pf.__setConversion(c, -1);
			
			// Skip conversion character
			at++;
		}
		
		// {@squirreljme.error ZZ2o Could not parse the format specifier
		// properly. (The string with the specifier sequence)}
		catch (IndexOutOfBoundsException|NumberFormatException e)
		{
			throw new IllegalArgumentException("ZZ2o " +
				__fmt.substring(__base, Math.min(at, __fmt.length())), e);
		}
		
		// Return the new ending index, after everything was parsed
		return at;
	}
}

