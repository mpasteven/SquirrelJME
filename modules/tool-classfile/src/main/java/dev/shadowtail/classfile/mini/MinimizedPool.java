// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.mini;

import cc.squirreljme.runtime.cldc.debug.Debugging;
import dev.shadowtail.classfile.pool.AccessedField;
import dev.shadowtail.classfile.pool.ClassPool;
import dev.shadowtail.classfile.pool.FieldAccessTime;
import dev.shadowtail.classfile.pool.FieldAccessType;
import dev.shadowtail.classfile.pool.InvokeType;
import dev.shadowtail.classfile.pool.InvokeXTable;
import dev.shadowtail.classfile.pool.InvokedMethod;
import dev.shadowtail.classfile.pool.UsedString;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import net.multiphasicapps.classfile.ClassName;
import net.multiphasicapps.classfile.ClassNames;
import net.multiphasicapps.classfile.FieldName;
import net.multiphasicapps.classfile.FieldReference;
import net.multiphasicapps.classfile.InvalidClassFormatException;
import net.multiphasicapps.classfile.MethodDescriptor;
import net.multiphasicapps.classfile.MethodHandle;
import net.multiphasicapps.classfile.MethodName;

/**
 * This represents the minimized constant pool.
 *
 * @since 2019/04/16
 */
@Deprecated
public final class MinimizedPool
{
	/** Pool value offsets. */
	private final int[] _offsets;
	
	/** Entry types. */
	private final byte[] _types;
	
	/** Parts. */
	private final int[][] _parts;
	
	/** Values. */
	final Object[] _values;
	
	/**
	 * Initializes the minimized pool.
	 *
	 * @param __os Offsets to the individual pool entries.
	 * @param __ts Types.
	 * @param __ps Parts.
	 * @param __vs Values.
	 * @throws IllegalArgumentException If the arrays are of a different size.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/17
	 */
	public MinimizedPool(int[] __os, byte[] __ts, int[][] __ps, Object[] __vs)
		throws IllegalArgumentException, NullPointerException
	{
		if (__os == null || __ts == null || __ps == null || __vs == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error JC07  Input arrays are of a different length.
		// (The expected length)}
		int n = __ts.length;
		if (__os.length != n || __ps.length != n || __vs.length != n)
			throw new IllegalArgumentException("JC07 " + n);
		
		// Defensive copy each
		__os = __os.clone();
		__ts = __ts.clone();
		__ps = __ps.clone();
		__vs = __vs.clone();
		
		// Clone all elements in the parts
		for (int i = 0; i < n; i++)
		{
			int[] p = __ps[i];
			
			if (p == null)
				throw new NullPointerException("NARG");
			
			__ps[i] = p.clone();
		}
		
		// Check values for null, skip entry 0
		for (int i = 1; i < n; i++)
			if (__vs[i] == null)
				throw new NullPointerException("NARG");
		
		// Set
		this._offsets = __os;
		this._types = __ts;
		this._parts = __ps;
		this._values = __vs;
	}
	
	/**
	 * Gets the specified pool entry.
	 *
	 * @param __i The entry to get.
	 * @since 2019/04/17
	 */
	public final Object get(int __i)
	{
		return this._values[__i];
	}
	
	/**
	 * Gets the specified pool entry.
	 *
	 * @param <V> The type to get.
	 * @param __i The index of the entry.
	 * @param __cl The type to get.
	 * @return The pool entry value.
	 * @throws ClassCastException If the class is not compatible.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/17
	 */
	public final <V> V get(int __i, Class<V> __cl)
		throws ClassCastException, NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException("NARG");
		
		return __cl.cast(this._values[__i]);
	}
	
	/**
	 * Returns the offset to the value of the entry within the pool.
	 *
	 * @param __i The index.
	 * @return The offset.
	 * @since 2019/05/01
	 */
	public final int offset(int __i)
	{
		return this._offsets[__i];
	}
	
	/**
	 * Returns the part at the given index.
	 *
	 * @param __i The pool entry to get.
	 * @param __p The part index.
	 * @return The part for this index.
	 * @since 2019/04/22
	 */
	public final int part(int __i, int __p)
	{
		return this._parts[__i][__p];
	}
	
	/**
	 * Returns the number of used parts.
	 *
	 * @param __i The index.
	 * @return The number of used parts.
	 * @since 2019/04/27
	 */
	public final int partCount(int __i)
	{
		return this._parts[__i].length;
	}
	
	/**
	 * Returns all of the available parts.
	 *
	 * @param __i The index.
	 * @return The parts for this index.
	 * @since 2019/04/30
	 */
	public final int[] parts(int __i)
	{
		return this._parts[__i].clone();
	}
	
	/**
	 * Returns the number of entries in the pool.
	 *
	 * @return The number of pool entries.
	 * @since 2019/04/17
	 */
	public final int size()
	{
		return this._types.length;
	}
	
	/**
	 * Returns the constant pool type.
	 *
	 * @param __i The index of the entry.
	 * @return The pool type.
	 * @since 2019/04/17
	 */
	public final MinimizedPoolEntryType type(int __i)
	{
		return MinimizedPoolEntryType.of(this._types[__i]);
	}
	
	/**
	 * Decodes the minimized constant pool.
	 *
	 * @param __n Number of entries in the pool.
	 * @param __is The bytes to decode from.
	 * @param __o The offset into the array.
	 * @param __l The length of the array.
	 * @return The resulting minimized pool.
	 * @throws IndexOutOfBoundsException If the offset and/or length are
	 * negative or exceed the array bounds.
	 * @throws InvalidClassFormatException If the class is not formatted
	 * correctly.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/16
	 */
	@Deprecated
	public static MinimizedPool decode(int __n, byte[] __is, int __o,
		int __l)
		throws IndexOutOfBoundsException, InvalidClassFormatException,
			NullPointerException
	{
		if (__is == null)
			throw new NullPointerException("NARG");
		if (__o < 0 || __l < 0 || (__o + __l) > __is.length)
			throw new IndexOutOfBoundsException("IOOB");
		
		// Types and offsets
		byte[] types = new byte[__n];
		int[] offsets = new int[__n];
		
		// Read the type and offset table
		try (DataInputStream dis = new DataInputStream(
			new ByteArrayInputStream(__is, __o, __l)))
		{
			// Read type table
			for (int i = 0; i < __n; i++)
				types[i] = dis.readByte();
			
			// {@squirreljme.error JC08 First pool entry type is not zero.
			// (The type that was read)}
			if (types[0] != 0)
				throw new InvalidClassFormatException("JC08 " + types[0]);
			
			// {@squirreljme.error JC09 Pool uneven padding byte was not
			// 255. (The value it was; The following byte)}
			if ((__n & 1) != 0)
			{
				int val;
				if ((val = dis.readUnsignedByte()) != 0xFF)
					throw new InvalidClassFormatException("JC09 " + val + " " +
						dis.read());
			}
			
			// Read offsets into the structure
			for (int i = 0; i < __n; i++)
				offsets[i] = dis.readUnsignedShort();
		}
		
		// {@squirreljme.error JC0a Invalid read of pool data.}
		catch (IOException e)
		{
			throw new InvalidClassFormatException("JC0a", e);
		}
		
		// Output pool entry types, values, and parts
		int[][] parts = new int[__n][];
		Object[] values = new Object[__n];
		
		// Zero int for empty parts
		int[] nopart = new int[0];
		
		// Re-build individual pool entries
		Object[] entries = new Object[__n];
		for (int i = 0; i < __n; i++)
		{
			// Get type and pointer
			int rtype = types[i],
				ptr = offsets[i];
			
			// Is this wide?
			boolean iswide;
			if ((iswide = ((rtype & 0x80) != 0)))
			{
				rtype &= 0x7F;
				
				// Re-adjust type array since we use this for the type list
				types[i] = (byte)rtype;
			}
			
			// Read info
			int[] part = null;
			Object v = null;
			
			// It is easier to handle this as a stream of bytes
			try (DataInputStream dis = new DataInputStream(
				new ByteArrayInputStream(__is, __o + ptr, __l - ptr)))
			{
				// Depends on the type
				MinimizedPoolEntryType type = MinimizedPoolEntryType.of(rtype);
				switch (type)
				{
						// Null is nothing, so do not bother
					case NULL:
						// {@squirreljme.error JC0b NULL pool entry is only
						// permitted as the first type, the class file is
						// corrupt.}
						if (i != 0)
							throw new InvalidClassFormatException("JC0b");
						break;
						
						// Strings
					case STRING:
						part = new int[]{
								dis.readUnsignedShort(),
								dis.readUnsignedShort(),
							};
						v = dis.readUTF();
						break;
						
						// Integer
					case INTEGER:
						v = dis.readInt();
						break;
						
						// Float
					case FLOAT:
						v = Float.intBitsToFloat(dis.readInt());
						break;
						
						// Types which consist of parts
					case CLASS_NAME:
					case CLASS_NAMES:
					case CLASS_POOL:
					case ACCESSED_FIELD:
					case INVOKED_METHOD:
					case METHOD_DESCRIPTOR:
					case LONG:
					case DOUBLE:
					case USED_STRING:
					case INVOKE_XTABLE:
						// Wide parts
						if (iswide)
						{
							// Read length
							int np = dis.readUnsignedShort();
							
							// Read values
							part = new int[np];
							for (int j = 0; j < np; j++)
								part[j] = dis.readUnsignedShort();
						}
						
						// Narrow parts
						else
						{
							// Read length
							int np = dis.readUnsignedByte();
							
							// Read values
							part = new int[np];
							for (int j = 0; j < np; j++)
								part[j] = dis.readUnsignedByte();
						}
						
						// Now that the parts were read, we can build the
						// actual value
						switch (type)
						{
								// Class name
							case CLASS_NAME:
								v = new ClassName((String)values[part[0]]);
								break;
								
								// Names of classes
							case CLASS_NAMES:
								{
									// Number of class names
									int ncn = part.length;
									
									// Read class names
									ClassName[] names = new ClassName[ncn];
									for (int j = 0; j < ncn; j++)
										names[j] = (ClassName)values[part[j]];
									
									// Build names
									v = new ClassNames(names);
								}
								break;
							
								// The constant pool of a class
							case CLASS_POOL:
								v = new ClassPool((ClassName)values[part[0]]);
								break;
								
								// Field which was accessed
							case ACCESSED_FIELD:
								v = new AccessedField(
									FieldAccessTime.of(part[0]),
									FieldAccessType.of(part[1]),
									new FieldReference(
										(ClassName)values[part[2]],
										new FieldName((String)values[part[3]]),
										((ClassName)values[part[4]]).field()));
								break;
								
								// Invoked method
							case INVOKED_METHOD:
								v = new InvokedMethod(
									InvokeType.of(part[0]),
									new MethodHandle(
										(ClassName)values[part[1]],
										new MethodName(
											(String)values[part[2]]),
										(MethodDescriptor)values[part[3]]));
								break;
							
								// Method descriptor
							case METHOD_DESCRIPTOR:
								v = new MethodDescriptor(
									(String)values[part[0]]);
								break;
								
								// The method index
							case INVOKE_XTABLE:
								v = new InvokeXTable(
									InvokeType.of(part[0]),
									(ClassName)values[part[1]]);
								break;
								
								// Long
							case LONG:
								v = (((long)(((Integer)values[part[0]])
										<< 0)) | ((Integer)values[part[1]]) &
											0xFFFFFFFFL);
								break;
								
								// Double
							case DOUBLE:
								v = Double.longBitsToDouble(
									(((long)(((Integer)values[part[0]])
										<< 0)) | ((Integer)values[part[1]]) &
											0xFFFFFFFFL));
								break;
								
								// Used string
							case USED_STRING:
								v = new UsedString((String)values[part[0]]);
								break;
								
							default:
								throw Debugging.oops(type.name());
						}
						break;
					
					default:
						throw Debugging.oops(type.name());
				}
			}
			
			// {@squirreljme.error JC0c Invalid read of pool data.}
			catch (IllegalArgumentException|IOException e)
			{
				throw new InvalidClassFormatException("JC0c", e);
			}
			
			// Set data
			parts[i] = (part == null ? nopart : part);
			values[i] = v;
		}
		
		// Build
		return new MinimizedPool(offsets, types, parts, values);
	}
}

