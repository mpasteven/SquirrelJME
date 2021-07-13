// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.tac;

import cc.squirreljme.runtime.cldc.util.BooleanArrayList;
import cc.squirreljme.runtime.cldc.util.ByteArrayList;
import cc.squirreljme.runtime.cldc.util.CharacterArrayList;
import cc.squirreljme.runtime.cldc.util.CollectionUtils;
import cc.squirreljme.runtime.cldc.util.IntegerArrayList;
import cc.squirreljme.runtime.cldc.util.LongArrayList;
import cc.squirreljme.runtime.cldc.util.ShortArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class contains serializers of data.
 *
 * @since 2019/01/20
 */
public final class DataSerialization
{
	/**
	 * Encodes key value.
	 * 
	 * This is the opposite of
	 * {@link DataDeserialization#decodeKey(String)}. 
	 *
	 * @param __key The key to encode.
	 * @return The resulting key.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/05/09
	 */
	public static String encodeKey(String __key)
		throws NullPointerException
	{
		if (__key == null)
			throw new NullPointerException("NARG");
			
		// Encode the key to allow for valid characters
		int n;
		StringBuilder sb = new StringBuilder((n = __key.length()));
		for (int i = 0; i < n; i++)
		{
			char c = __key.charAt(i);
			
			// Possibly re-encoded?
			boolean enc = false;
			if (c >= 'A' && c <= 'Z')
				c = Character.toLowerCase(c);
			
			// Re-encode?
			char redo = 0;
			switch (c)
			{
				case '-':	redo = '-'; break;
				case '!':	redo = 'x'; break;
				case '"':	redo = 'q'; break;
				case '#':	redo = 'h'; break;
				case '$':	redo = 'm'; break;
				case '%':	redo = 'c'; break;
				case '&':	redo = 'e'; break;
				case '*':	redo = 's'; break;
				case '+':	redo = 'p'; break;
				case '.':	redo = 'd'; break;
				case '/':	redo = 'l'; break;
				case ':':	redo = 'o'; break;
				case '?':	redo = 'u'; break;
				case '@':	redo = 'a'; break;
				case '^':	redo = 'r'; break;
				case '|':	redo = 'i'; break;
				case '~':	redo = 't'; break;
			}
			
			// Was this value being re-encoded?
			if (redo != 0)
			{
				sb.append('-');
				sb.append(redo);
			}
			else
				sb.append(c);
		}
		
		// Use the encoded key
		return sb.toString();
	}
	
	/**
	 * Encodes the given string to a manifest safe format.
	 * 
	 * This is the opposite of
	 * {@link DataDeserialization#decodeString(String)}. 
	 *
	 * @param __s The string to encode.
	 * @return The encoded string, {@code null} has a special value.
	 * @since 2018/10/06
	 */
	public static String encodeString(String __s)
	{
		// Special value for null strings
		if (__s == null)
			return "\\NULL";
		
		// Encode characters to normalize them
		StringBuilder sb = new StringBuilder(__s.length());
		for (int i = 0, n = __s.length(); i < n; i++)
		{
			char c = __s.charAt(i);
			
			// Is the character to be translated?
			boolean escape = false;
			switch (c)
			{
					// Just escape these
				case '\\':
				case '"':
					escape = true;
					break;
					
					// Make spaces just in the form of an underline to make
					// them more spacey but easier to see
				case ' ':
					escape = true;
					c = '_';
					break;
					
					// Newline
				case '\n':
					escape = true;
					c = 'n';
					break;
					
					// Carriage return
				case '\r':
					escape = true;
					c = 'r';
					break;
					
					// Tab
				case '\t':
					escape = true;
					c = 't';
					break;
					
					// Opening brace
				case '{':
					escape = true;
					c = '(';
					break;
					
					// Closing brace
				case '}':
					escape = true;
					c = ')';
					break;
					
					// Delete
				case 0x7F:
					escape = true;
					c = 'd';
					break;
				
				case 0x00:
				case 0x01:
				case 0x02:
				case 0x03:
				case 0x04:
				case 0x05:
				case 0x06:
				case 0x07:
				case 0x08:
				case 0x0b:
				case 0x0c:
				case 0x0e:
				case 0x1f:
				case 0x10:
				case 0x11:
				case 0x12:
				case 0x13:
				case 0x14:
				case 0x15:
				case 0x16:
				case 0x17:
				case 0x18:
				case 0x19:
				case 0x1a:
				case 0x1b:
				case 0x1c:
				case 0x1d:
				case 0x1e:
					escape = true;
					c = (char)((c < 10 ? '0' + c : 'A' + (c - 10)));
					break;
				
					// Not changed
				default:
					break;
			}
			
			// Character is out of range?
			if (c >= 0x7F)
				sb.append(String.format("\\@%04x", (int)c));
			
			// Append printable
			else
			{
				if (escape)
					sb.append('\\');
				sb.append(c);
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * Serialize the given object to a string.
	 *
	 * @param __o The value to serialize.
	 * @return The resulting serialization.
	 * @since 2019/01/20
	 */
	public static String serialize(Object __o)
	{
		// Null
		if (__o == null)
			return "null";
		
		// No result generated
		else if (__o instanceof __NoResult__)
			return "NoResult";
		
		// Undefined
		else if (__o instanceof __UndefinedResult__)
			return "UndefinedResult";
		
		// Exception was thrown
		else if (__o instanceof __ExceptionThrown__)
			return "ExceptionThrown";
		
		// No exception was thrown
		else if (__o instanceof __NoExceptionThrown__)
			return "NoExceptionThrown";
		
		// Boolean values
		else if (__o instanceof Boolean)
			return __o.toString();
		
		// String
		else if (__o instanceof String)
			return "string:" + DataSerialization.encodeString((String)__o);
		
		// Byte
		else if (__o instanceof Byte)
			return "byte:" + __o;
		
		// Short
		else if (__o instanceof Short)
			return "short:" + __o;
		
		// Character
		else if (__o instanceof Character)
			return "char:" + (int)((Character)__o).charValue();
		
		// Integer
		else if (__o instanceof Integer)
			return "int:" + __o;
		
		// Long
		else if (__o instanceof Long)
			return "long:" + __o;
		
		// Boolean array
		else if ((__o instanceof boolean[]) || (__o instanceof Boolean[]))
		{
			List<Boolean> list = ((__o instanceof boolean[]) ?
				BooleanArrayList.asList((boolean[])__o) :
				Arrays.asList((Boolean[])__o));
			
			StringBuilder sb = new StringBuilder(
				String.format("boolean[%d]:", list.size()));
			for (Boolean v : list)
				sb.append((v ? 'T' : 'f'));
			
			return sb.toString();
		}
		
		// Boxed Number Array
		else if ((__o instanceof byte[]) || (__o instanceof Byte[]) ||
			(__o instanceof short[]) || (__o instanceof Short[]) ||
			(__o instanceof int[]) || (__o instanceof Integer[]) ||
			(__o instanceof long[]) || (__o instanceof Long[]) ||
			(__o instanceof char[]) || (__o instanceof Character[]))
		{
			// Which wrapper and key is used?
			List<? extends Number> list;
			String key;
			
			// Boxed character array (since not a number)
			if (__o instanceof Character[])
			{
				key = "char*";
				list = CollectionUtils.asIntegerList(
					Arrays.asList((Character[])__o));
			}
			
			// Determine how these values are accessed
			else if (__o instanceof Number[])
			{
				// These are all the same!
				list = Arrays.asList((Number[])__o);
				
				// The key varies
				if (__o instanceof Byte[])
					key = "byte*";
				else if (__o instanceof Short[])
					key = "short*";
				else if (__o instanceof Integer[])
					key = "int*";
				else
					key = "long*";
			}
			else if (__o instanceof char[])
			{
				key = "char";
				list = CollectionUtils.asIntegerList(
					new CharacterArrayList((char[])__o));
			}
			else if (__o instanceof byte[])
			{
				key = "byte";
				list = new ByteArrayList((byte[])__o);
			}
			else if (__o instanceof short[])
			{
				key = "short";
				list = new ShortArrayList((short[])__o);
			}
			else if (__o instanceof int[])
			{
				key = "int";
				list = new IntegerArrayList((int[])__o);
			}
			else
			{
				key = "long";
				list = new LongArrayList((long[])__o);
			}
			
			// Print values
			int n = list.size();
			StringBuilder sb = new StringBuilder(
				String.format("%s[%d]:", key, n));
			
			for (int i = 0; i < n; i++)
			{
				if (i > 0)
					sb.append(",");
				
				Number val = list.get(i);
				sb.append((val == null ? "null" : val.longValue()));
			}
			
			return sb.toString();
		}
		
		// String array
		else if (__o instanceof String[])
		{
			// Print values
			String[] a = (String[])__o;
			int n = a.length;
			StringBuilder sb = new StringBuilder(
				String.format("string[%d]:", n));
			for (int i = 0; i < n; i++)
			{
				if (i > 0)
					sb.append(",");
				sb.append(DataSerialization.encodeString(a[i]));
			}
			return sb.toString();
		}
		
		// Throwable, meta data is used
		else if (__o instanceof Throwable)
		{
			Throwable t = (Throwable)__o;
			
			StringBuilder sb = new StringBuilder("throwable:");
			
			// The last package to shorten the classes
			String lastpkg = "java.lang.";
			
			// Throwables may be of multiple class types and it usually is
			// expected that they are some base class. For example a class
			// can thrown some FooIndexOutOfBoundsException which is based
			// off IndexOutOfBoundsException, if the result expects the
			// base class then it must still pass. So store the entire class
			// tree.
			boolean comma = false;
			for (Class<?> x = t.getClass(); x != null && x != Object.class;
				x = x.getSuperclass())
			{
				// Clip off the package if it matches
				String clname = x.getName();
				if (clname.startsWith(lastpkg))
					clname = clname.substring(lastpkg.length());
				
				// Otherwise remember the package used
				else
				{
					int ld = clname.lastIndexOf('.');
					if (ld >= 0)
						lastpkg = clname.substring(0, ld + 1);
					
					// Maybe default package?
					else
						lastpkg = "";
				}
				
				// Split to encode multiple classes
				if (comma)
					sb.append(',');
				comma = true;
				
				// Append class name here
				sb.append(clname);
			}
			
			// For debug purposes, encode the message information
			String msg = t.getMessage();
			if (msg != null)
			{
				sb.append(':');
				sb.append(DataSerialization.encodeString(msg));
			}
			
			// Now metadata is included for this
			return sb.toString();
		}
		
		// Unrepresented object, just use its string representation in an
		// encoded form
		else
			return "other:" + __o.getClass().getName() + ":" +
				DataSerialization.encodeString(__o.toString());
	}
}

