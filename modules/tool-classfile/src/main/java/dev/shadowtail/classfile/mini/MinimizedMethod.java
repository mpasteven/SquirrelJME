// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.mini;

import dev.shadowtail.classfile.pool.DualClassRuntimePool;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import net.multiphasicapps.classfile.InvalidClassFormatException;
import net.multiphasicapps.classfile.MethodDescriptor;
import net.multiphasicapps.classfile.MethodFlags;
import net.multiphasicapps.classfile.MethodName;
import net.multiphasicapps.classfile.MethodNameAndType;

/**
 * This represents a method which has been minimized.
 *
 * @since 2019/03/14
 */
public final class MinimizedMethod
{
	/** Flags that are used for the method. */
	protected final int flags;
	
	/** The index of this method in the instance table. */
	protected final int instanceIndex;
	
	/** The name of the method. */
	protected final MethodName name;
	
	/** The type of the method. */
	protected final MethodDescriptor type;
	
	/** The code offset. */
	protected final int codeOffset;
	
	/** Internal method index. */
	protected final int methodOrderIndex;
	
	/** Translated method code. */
	final byte[] _code;
	
	/** Method flags. */
	private Reference<MethodFlags> _flags;
	
	/** Name and type. */
	private Reference<MethodNameAndType> _nat;
	
	/** String representation. */
	private Reference<String> _string;
	
	/**
	 * Initializes the minimized method.
	 *
	 * @param __f The method flags.
	 * @param __o Index in the method table for instances.
	 * @param __n The method name.
	 * @param __t The method type.
	 * @param __tc Transcoded instructions.
	 * @param __co The code offset.
	 * @param __methodOrderIndex The method index.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/03/14
	 */
	public MinimizedMethod(int __f, int __o, MethodName __n,
		MethodDescriptor __t, byte[] __tc, int __co,
		int __methodOrderIndex)
		throws NullPointerException
	{
		if (__n == null || __t == null)
			throw new NullPointerException("NARG");
		
		this.flags = __f;
		this.instanceIndex = __o;
		this.name = __n;
		this.type = __t;
		this._code = (__tc == null ? new byte[0] : __tc.clone());
		this.codeOffset = __co;
		this.methodOrderIndex = __methodOrderIndex;
	}
	
	/**
	 * Returns the method code.
	 *
	 * @return The method code.
	 * @since 2019/04/19
	 */
	public final byte[] code()
	{
		return this._code.clone();
	}
	
	/**
	 * Returns the code offset.
	 *
	 * @return The code offset.
	 * @since 2021/07/12
	 */
	public int codeOffset()
	{
		return this.codeOffset;
	}
	
	/**
	 * Returns the raw flag bits.
	 *
	 * @return The raw flag bits.
	 * @since 2021/07/12
	 */
	public int flagBits()
	{
		return this.flags;
	}
	
	/**
	 * Returns the flags for this method.
	 *
	 * @return The flags for the method.
	 * @since 2019/04/17
	 */
	public final MethodFlags flags()
	{
		Reference<MethodFlags> ref = this._flags;
		MethodFlags rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._flags = new WeakReference<>((rv =
				new MethodFlags(this.flagBits())));
		
		return rv;
	}
	
	/**
	 * Returns the index where this appears in tables.
	 * 
	 * @return The instance index.
	 * @since 2021/07/12
	 */
	public int instanceIndex()
	{
		return this.instanceIndex;
	}
	
	/**
	 * Returns the method order index.
	 * 
	 * @return The method order index.
	 * @since 2021/07/12
	 */
	public int methodOrderIndex()
	{
		return this.methodOrderIndex;
	}
	
	/**
	 * Returns the method name.
	 * 
	 * @return The method name.
	 * @since 2021/07/12
	 */
	public MethodName name()
	{
		return this.name;
	}
	
	/**
	 * Returns the name and type for this method.
	 *
	 * @return The name and type for this method.
	 * @since 2019/05/26
	 */
	public final MethodNameAndType nameAndType()
	{
		Reference<MethodNameAndType> ref = this._nat;
		MethodNameAndType rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._nat = new WeakReference<>(
				(rv = new MethodNameAndType(this.name(), this.type())));
		
		return rv;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/05/24
	 */
	@Override
	public final String toString()
	{
		Reference<String> ref = this._string;
		String rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._string = new WeakReference<>((rv = String.format(
				"%s:%s{flags=%x, dx=%d, co=%d}", this.name(),
				this.type(), this.flagBits(), this.instanceIndex(),
				this.codeOffset())));
		
		return rv;
	}
	
	/**
	 * Returns the method type.
	 *
	 * @return The method type.
	 * @since 2021/07/12
	 */
	public MethodDescriptor type()
	{
		return this.type;
	}
	
	/**
	 * Decodes the method data.
	 *
	 * @param __n The number of methods.
	 * @param __p The constant pool.
	 * @param __b Input data.
	 * @param __o Offset into array.
	 * @param __l Length of data rea.
	 * @return The decoded methods.
	 * @throws IndexOutOfBoundsException If the offset and/or length are
	 * negative or exceed the array bounds.
	 * @throws InvalidClassFormatException If the methods could not be decoded.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/17
	 */
	public static MinimizedMethod[] decodeMethods(int __n,
		DualClassRuntimePool __p, byte[] __b, int __o, int __l)
		throws IndexOutOfBoundsException, InvalidClassFormatException,
			NullPointerException
	{
		if (__b == null || __p == null)
			throw new NullPointerException("NARG");
		if (__o < 0 || __l < 0 || (__o + __l) > __b.length)
			throw new IndexOutOfBoundsException("IOOB");
		
		// Setup return value
		MinimizedMethod[] rv = new MinimizedMethod[__n];
		
		// Read method data
		try (DataInputStream dis = new DataInputStream(
			new ByteArrayInputStream(__b, __o, __l)))
		{
			for (int i = 0; i < __n; i++)
			{
				// Read in method information
				int flags = dis.readInt(),
					offset = dis.readUnsignedShort();
				MethodName name = new MethodName(
					__p.getByIndex(false, dis.readUnsignedShort()).
						<String>value(String.class));
				MethodDescriptor type =
					__p.getByIndex(false, dis.readUnsignedShort()).
						<MethodDescriptor>value(MethodDescriptor.class);
				int offcode = dis.readInt();
				int lencode = dis.readInt();
				int methodIndex = dis.readUnsignedShort();
				
				// Read code?
				byte[] code = null;
				if (offcode > 0)
				{
					code = new byte[lencode];
					System.arraycopy(__b, __o + offcode,
						code, 0, lencode);
				}
				
				// Build method
				rv[i] = new MinimizedMethod(flags, offset, name, type,
					code, offcode, methodIndex);
			}
		}
		
		// {@squirreljme.error JC06 Could not read method data.
		// (Count; Method data size)}
		catch (ClassCastException|IOException|IndexOutOfBoundsException e)
		{
			throw new InvalidClassFormatException(String.format(
				"JC06 %d %d", __n, __l), e);
		}
		
		return rv;
	}
}

