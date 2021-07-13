// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.aot.summercoat;

import cc.squirreljme.jvm.summercoat.constants.JarProperty;
import cc.squirreljme.jvm.summercoat.ld.pack.HeaderStruct;
import cc.squirreljme.jvm.summercoat.ld.pack.JarRom;
import dev.shadowtail.classfile.mini.DualPoolEncoder;
import dev.shadowtail.classfile.mini.MinimizedClassFile;
import dev.shadowtail.classfile.mini.MinimizedClassHeader;
import dev.shadowtail.classfile.mini.MinimizedField;
import dev.shadowtail.classfile.mini.MinimizedMethod;
import dev.shadowtail.classfile.nncc.NativeInstruction;
import dev.shadowtail.classfile.nncc.NativeInstructionType;
import dev.shadowtail.classfile.pool.BasicPool;
import dev.shadowtail.classfile.pool.DualClassRuntimePool;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Iterator;
import net.multiphasicapps.classfile.ClassNames;
import net.multiphasicapps.classfile.InstructionMnemonics;
import net.multiphasicapps.io.Base64Alphabet;
import net.multiphasicapps.io.Base64Encoder;

/**
 * This is a dumper for SummerCoat's minimized class file format.
 *
 * @since 2021/05/21
 */
public final class ClassDumper
{
	/** The size of each line, used for base64 representation. */
	private static final int _LINE_SIZE =
		76;
	
	/** The size of tabs. */
	private static final int _TAB_SIZE =
		3;
	
	/** Minimum line size. */
	private static final int _MIN_LINE_SIZE =
		32;
	
	/** The JAR Rom. */
	protected final JarRom jarRom;
	
	/** The name of the JAR. */
	protected final String jarName;
	
	/** The stream to dump to. */
	protected final PrintStream out;
	
	/**
	 * Initializes the class dumper.
	 * 
	 * @param __jarRom The ROM to be dumped.
	 * @param __name The name of the JAR.
	 * @param __out Where to dump to.
	 * @since 2021/05/21
	 */
	public ClassDumper(JarRom __jarRom, String __name, PrintStream __out)
		throws NullPointerException
	{
		if (__jarRom == null)
			throw new NullPointerException("NARG");
		
		this.jarRom = __jarRom;
		this.jarName = __name;
		this.out = __out;
	}
	
	/**
	 * Dumps the given JAR.
	 * 
	 * @param __inGlob The raw ROM data.
	 * @throws IOException If the JAR could not be read.
	 * @since 2021/05/21
	 */
	public void dump(byte[] __inGlob)
		throws IOException
	{
		// Basic header
		this.__print(0, "# SquirrelJME Compiled JarROM Dump");
		this.__print(0, "dumpVersion", "%d", 1);
		
		// What is this JAR called?
		this.__print(0, "jarName", this.jarName);
		
		// The JAR to be dumped
		JarRom jar = this.jarRom;
		
		// Dump the JAR header
		HeaderStruct header = jar.header();
		this.__dumpJarHeader(header);
		
		// Decode the dual pool
		DualClassRuntimePool dualPool = DualPoolEncoder.decode(__inGlob,
			header.getInteger(JarProperty.OFFSET_STATIC_POOL),
				header.getInteger(JarProperty.SIZE_STATIC_POOL),
			header.getInteger(JarProperty.OFFSET_RUNTIME_POOL),
				header.getInteger(JarProperty.SIZE_RUNTIME_POOL));
		
		// Dump the pool
		/*this.__dumpPool(0, dualPool);*/
		
		// Write out entries
		this.__print(0, "entries", "");
		
		// Dump each individual resource
		String[] entries = jar.entries();
		for (int i = 0, n = entries.length; i < n; i++)
			try (InputStream entryIn = jar.openResourceAsStream(i)) 
			{
				// Ignore directories!
				if (entries[i].endsWith("/"))
					continue;
				
				// Header key
				this.__print(1, "- " + entries[i], "");
				
				// If not a class file, dump raw byte data
				if (!entries[i].endsWith(".class"))
					this.__dumpResource(2, entryIn);
				
				// Otherwise decode as a class
				else
					this.__dumpClass(2,
						MinimizedClassFile.decode(entryIn, dualPool));
			}
	}
	
	/**
	 * Dumps the given class.
	 * 
	 * @param __indent The indentation.
	 * @param __class The class to dump.
	 * @throws IOException On read/write errors.
	 * @since 2021/05/16
	 */
	private void __dumpClass(int __indent, MinimizedClassFile __class)
		throws IOException
	{
		PrintStream __out = this.out; 
		
		// Print some basic class details
		this.__print(__indent, "thisName", "%s",
			__class.thisName());
		this.__print(__indent, "superName", "%s",
			__class.superName());
		
		// Print flags
		this.__print(__indent, "flags", "");
		this.__printList(__indent + 1, __class.flags());
		
		// Print interfaces
		ClassNames iNames = __class.interfaceNames();
		if (!iNames.isEmpty())
		{
			this.__print(__indent, "interfaceNames", "");
			this.__printList(__indent + 1, iNames);
		}
		
		// Dump header
		this.__dumpHeader(__indent, __class.header, __out);
		
		// Dump fields
		this.__dumpFields(__indent, "fieldStatic",
			__class.fields(true));
		this.__dumpFields(__indent, "fieldInstance",
			__class.fields(false));
		
		// Dump methods
		this.__dumpMethods(__indent, __class.pool, "methodStatic",
			__class.methods(true));
		this.__dumpMethods(__indent, __class.pool, "methodInstance",
			__class.methods(false));
	}
	
	/**
	 * Dumps the given field.
	 * 
	 * @param __indent The indentation.
	 * @param __f The field to dump.
	 * @since 2021/05/16
	 */
	private void __dumpField(int __indent, MinimizedField __f)
	{
		// Key
		this.__print(__indent, String.format("- %s %s", __f.name, __f.type),
			"");
		
		// Flags
		this.__print(__indent + 1, "flags", "");
		this.__printList(__indent + 2, __f.flags());
		
		// Other properties
		this.__print(__indent + 1, "type", "%s",
			__f.datatype);
		this.__print(__indent + 1, "value", "%s",
			__f.value);
		this.__print(__indent + 1, "size", "%d",
			__f.size);
		this.__print(__indent + 1, "offset", "%d",
			__f.offset);
	}
	
	/**
	 * Dumps the given fields.
	 * 
	 * @param __indent The indentation.
	 * @param __key The key for this group.
	 * @param __fields The fields to dump.
	 * @since 2021/05/29
	 */
	private void __dumpFields(int __indent, String __key,
		MinimizedField... __fields)
	{
		if (__fields == null || __fields.length == 0)
			return;
		
		this.__print(__indent, __key, "");
		for (MinimizedField f : __fields)
			this.__dumpField(__indent + 1, f);
	}
	
	/**
	 * Dumps the class file header.
	 * 
	 * @param __indent The indentation.
	 * @param __header The header.
	 * @param __out Where to dump to.
	 * @since 2021/05/16
	 */
	private void __dumpHeader(int __indent, MinimizedClassHeader __header,
		PrintStream __out)
	{
		this.__print(__indent, "classProperties", "");
		for (int i = 0, n = __header.numProperties(); i < n; i++)
			this.__print(__indent + 1,
				__Utils__.classPropertyToString(i),
				"%#010x", __header.get(i));
	}
	
	/**
	 * Dumps the JAR header.
	 * 
	 * @param __header The header to dump.
	 * @since 2021/05/16
	 */
	private void __dumpJarHeader(HeaderStruct __header)
	{
		this.__print(0, "jarProperties", "");
		for (int i = 0, n = __header.numProperties(); i < n; i++)
			this.__print(1,
				__Utils__.jarPropertyToString(i),
				"%#010x", __header.getInteger(i));
	}
	
	/**
	 * Dumps the method code.
	 *
	 * @param __indent The indentation.
	 * @param __m The method to dump.
	 * @param __dualPool The dual constant pool.
	 * @throws IOException On read/write errors. 
	 * @since 2021/05/16
	 */
	private void __dumpMethod(int __indent, MinimizedMethod __m,
		DualClassRuntimePool __dualPool)
		throws IOException
	{
		// Key
		this.__print(__indent, String.format("- %s %s", __m.name(),
			__m.type()),
			"");
		
		// Flags
		this.__print(__indent + 1, "flags", "");
		this.__printList(__indent + 2, __m.flags());
		
		// Other properties
		this.__print(__indent + 1, "index", "%d", __m.instanceIndex());
			
		// Is there code to be dumped?
		byte[] code = __m.code();
		if (code != null && code.length > 0)
			try (DataInputStream in = new DataInputStream(
				new ByteArrayInputStream(code)))
			{
				this.__printCode(__indent + 1, in, __dualPool);
				//this.__printBinary(__indent + 1, "data", in);
			}
	}
	
	/**
	 * Dumps the given methods.
	 * 
	 * @param __indent The indentation.
	 * @param __dualPool The dual class pool.
	 * @param __key The key.
	 * @param __methods The methods to dump.
	 * @throws IOException On read/write errors.
	 * @since 2021/05/29
	 */
	private void __dumpMethods(int __indent, DualClassRuntimePool __dualPool,
		String __key, MinimizedMethod... __methods)
		throws IOException
	{
		if (__methods == null || __methods.length == 0)
			return;
		
		this.__print(__indent, __key, "");
		for (MinimizedMethod m : __methods)
			this.__dumpMethod(__indent + 1, m, __dualPool);
	}
	
	/**
	 * Dumps the dual pool.
	 * 
	 * @param __indent The indentation.
	 * @param __dualPool The dual pool to dump.
	 * @since 2021/05/30
	 */
	private void __dumpPool(int __indent, DualClassRuntimePool __dualPool)
	{
		this.__dumpPool(__indent, "static", __dualPool.classPool());
		this.__dumpPool(__indent, "runtime", __dualPool.runtimePool());
	}
	
	/**
	 * Dumps the single pool.
	 * 
	 * @param __indent The indentation.
	 * @param __pool The pool to dump.
	 * @since 2021/05/30
	 */
	private void __dumpPool(int __indent, String __key, BasicPool __pool)
	{
		this.__print(__indent, __key, "");
		this.__printList(__indent + 1, __pool);
	}
	
	/**
	 * Dumps the given resource.
	 * 
	 * @param __indent The indentation.
	 * @param __entryIn The entry data to write.
	 * @throws IOException On read errors.
	 * @since 2021/05/22
	 */
	private void __dumpResource(int __indent, InputStream __entryIn)
		throws IOException
	{
		this.__printBinary(__indent, "data", __entryIn);
	}
	
	/**
	 * Prints a line to the output.
	 * 
	 * @param __indent The indentation level.
	 * @param __key The key to print.
	 * @since 2021/05/22
	 */
	private void __print(int __indent, String __key)
	{
		this.__print(__indent, __key, null, (Object[])null);
	}
	
	/**
	 * Prints a line to the output.
	 * 
	 * @param __indent The indentation level.
	 * @param __key The key to print.
	 * @param __value The format string.
	 * @since 2021/05/21
	 */
	private void __print(int __indent, String __key, String __value)
	{
		this.__print(__indent, __key, __value, (Object[])null);
	}
	
	/**
	 * Prints a line to the output.
	 * 
	 * @param __indent The indentation level.
	 * @param __key The key to print.
	 * @param __value The format string.
	 * @param __args The arguments to the format.
	 * @since 2021/05/21
	 */
	private void __print(int __indent, String __key, String __value,
		Object... __args)
	{
		this.__printPrefix(__indent, __key, __value != null,
			__value != null && !__value.isEmpty());
		
		// Print value line
		PrintStream out = this.out;
		if (__value != null && !__value.isEmpty())
		{
			if (__args == null || __args.length == 0)
				out.print(__value);
			else
				out.printf(__value, __args);
		}
		
		// End the line
		out.println();
	}
	
	/**
	 * Prints binary data.
	 * 
	 * @param __indent The indentation.
	 * @param __key The key to print.
	 * @param __in The data to print.
	 * @throws IOException On read errors.
	 * @since 2021/05/22
	 */
	private void __printBinary(int __indent, String __key, InputStream __in)
		throws IOException
	{
		this.__printPrefix(__indent, __key,
			true, true);
		
		// Determine the line size to use, always keep a minimum!
		int lineSize = Math.max(ClassDumper._MIN_LINE_SIZE,
			(ClassDumper._LINE_SIZE - (__indent * ClassDumper._TAB_SIZE)) + 1);
			
		// Print encoded binary data
		PrintStream out = this.out;
		try (Base64Encoder in = new Base64Encoder(__in, Base64Alphabet.BASIC))
		{
			// Print binary coding prefix
			out.print("!binary |");
			out.println();
			
			// Write encoded characters
			int col = 0;
			for (;;)
			{
				// Which character is next?
				int c = in.read();
				if (c < 0)
					break;
				
				// Print indentation first?
				if ((col++) == 0)
					this.__printPrefix(__indent, " ",
						false, false);
				
				// Print this character
				out.print((char)c);
				
				// Go to next row?
				if (col >= lineSize)
				{
					out.println();
					col = 0;
				}
			}
		}
		
		// End the line
		out.println();
	}
	
	/**
	 * Prints the given SummerCoat code out.
	 * 
	 * @param __indent The indentation.
	 * @param __code The code to print.
	 * @param __dualPool The dual pool.
	 * @throws IOException On read/write errors.
	 * @since 2021/06/10
	 */
	private void __printCode(int __indent, DataInputStream __code,
		DualClassRuntimePool __dualPool)
		throws IOException
	{
		// Print header
		this.__print(__indent, "code", "");
		
		// Print each individual instruction
		for (int baseIndent = __indent + 1;;)
			try
			{
				NativeInstruction instruction =
					NativeInstruction.decode(__dualPool, __code);
				
				// Handle Java Debug Point specially
				String instStr;
				if (instruction.operation() ==
					NativeInstructionType.DEBUG_POINT)
				{
					// Read these parameters
					int sourceLine = instruction.intArgument(0);
					int javaOp = instruction.intArgument(1);
					int javaAddr = instruction.intArgument(2);
					
					// Build string
					instStr = String.format("*** Java :%d %s@%d ***",
						sourceLine,
						InstructionMnemonics.toString(javaOp),
						javaAddr);
				}
				else
				{
					// Quote up strings
					instStr = instruction.toString();
					if (instStr.indexOf('"') >= 0)
					{
						StringBuilder sb = new StringBuilder();
						for (int i = 0, n = instStr.length(); i < n; i++)
						{
							char c = instStr.charAt(i);
							if (c == '"')
								sb.append('\\');
							sb.append(c);
						}
						
						instStr = sb.toString();
					}
				}
				
				this.__print(baseIndent, "- \"" + instStr + "\"");
			}
			catch (EOFException ignored)
			{
				break;
			}
	}
	
	/**
	 * Prints the given list items.
	 * 
	 * @param __indent The indentation.
	 * @param __items The items to print.
	 * @since 2021/05/29
	 */
	private void __printList(int __indent, Iterable<?> __items)
	{
		for (Iterator<?> it = __items.iterator(); it.hasNext();)
			this.__print(__indent, "- " + it.next());
	}
	
	/**
	 * Prints the prefix.
	 * 
	 * @param __indent The indentation.
	 * @param __key The key to print.
	 * @param __hasValue Does this have a value?
	 * @param __valueNotEmpty Is this value not empty?
	 * @since 2021/05/22
	 */
	private void __printPrefix(int __indent, String __key, boolean __hasValue,
		boolean __valueNotEmpty)
	{
		// Print indentation
		PrintStream out = this.out;
		for (int i = 0; i < __indent; i++)
			out.print("  ");
		
		// Print the key?
		if (__key != null)
		{
			out.print(__key);
			
			// If there is also a value, there needs to be a divider
			if (__hasValue)
			{
				out.print(':');
				if (__valueNotEmpty)
					out.print(' ');
			}
		}
	}
}
