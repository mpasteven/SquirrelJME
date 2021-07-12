// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.nncc;

import cc.squirreljme.runtime.cldc.debug.Debugging;
import dev.shadowtail.classfile.pool.DualClassRuntimePool;
import dev.shadowtail.classfile.xlate.CompareType;
import dev.shadowtail.classfile.xlate.DataType;
import dev.shadowtail.classfile.xlate.MathType;
import dev.shadowtail.classfile.xlate.StackJavaType;
import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.multiphasicapps.collections.UnmodifiableList;

/**
 * This represents a single instruction.
 *
 * @since 2019/03/22
 */
public final class NativeInstruction
{
	/** Cache of operation formats. */
	@SuppressWarnings("CheckForOutOfMemoryOnLargeArrayAllocation")
	private static final InstructionFormat[] _FORMAT_CACHE =
		new InstructionFormat[NativeInstructionType.MAX_INSTRUCTIONS];
	
	/** The operation. */
	protected final int op;
	
	/** The arguments. */
	private final UnmodifiableList<Object> _args;
	
	/** String form. */
	private Reference<String> _string;
	
	/** Hash. */
	private int _hash;
	
	/**
	 * Initializes the temporary instruction.
	 *
	 * @param __op The operation.
	 * @param __args The arguments.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/03/22
	 */
	public NativeInstruction(int __op, Object... __args)
		throws NullPointerException
	{
		this.op = __op;
		this._args = UnmodifiableList.of(Arrays.asList((__args =
			(__args == null ? new Object[0] : __args.clone()))));
		
		for (Object o : __args)
			if (o == null)
				throw new NullPointerException("NARG");
	}
	
	/**
	 * Initializes the temporary instruction.
	 *
	 * @param __op The operation.
	 * @param __args The arguments.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/03/22
	 */
	public NativeInstruction(int __op, Collection<Object> __args)
		throws NullPointerException
	{
		this(__op, __args.<Object>toArray(new Object[__args.size()]));
	}
	
	/**
	 * Returns the argument for the given index.
	 *
	 * @param __i The index of the argument.
	 * @return The argument.
	 * @throws IndexOutOfBoundsException If the argument index is not
	 * within bounds.
	 * @since 2019/03/24
	 */
	public Object argument(int __i)
		throws IndexOutOfBoundsException
	{
		return this._args.get(__i);
	}
	
	/**
	 * Returns the argument for the given index.
	 *
	 * @param <T> The type of argument to get.
	 * @param __i The index of the argument.
	 * @param __cl The class to cast to.
	 * @return The argument as the given class.
	 * @throws ClassCastException If the class is not of the given type.
	 * @throws IndexOutOfBoundsException If the argument index is not
	 * within bounds.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/03/23
	 */
	public <T> T argument(int __i, Class<T> __cl)
		throws ClassCastException, IndexOutOfBoundsException,
			NullPointerException
	{
		// Check
		if (__cl == null)
			throw new NullPointerException("NARG");
		
		return __cl.cast(this._args.get(__i));
	}
	
	/**
	 * The number of arguments the instruction takes.
	 *
	 * @return The argument count.
	 * @since 2019/03/23
	 */
	public final int argumentCount()
	{
		return this._args.size();
	}
	
	/**
	 * Returns the argument format for the instruction.
	 *
	 * @return The argument format.
	 * @since 2018/04/16
	 */
	public final InstructionFormat argumentFormat()
	{
		return NativeInstruction.argumentFormat(this.op);
	}
	
	/**
	 * Returns all of the arguments.
	 *
	 * @return The arguments.
	 * @since 2019/04/03
	 */
	public final List<Object> arguments()
	{
		return this._args;
	}
	
	/**
	 * Obtains the given argument as a byte.
	 *
	 * @param __i The argument to get.
	 * @return The value of the argument.
	 * @throws ClassCastException If the given argument is not an number.
	 * @throws IndexOutOfBoundsException If the index is not within the
	 * bounds of the instruction arguments.
	 * @since 2019/04/03
	 */
	public byte byteArgument(int __i)
		throws ClassCastException, IndexOutOfBoundsException
	{
		return this.<Number>argument(__i, Number.class).byteValue();
	}
	
	/**
	 * Returns the encoding of this instruction.
	 *
	 * @return The instruction encoding.
	 * @since 2019/03/27
	 */
	public final int encoding()
	{
		return NativeInstruction.encoding(this.op);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/11
	 */
	@Override
	public final boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (!(__o instanceof NativeInstruction))
			return false;
		
		NativeInstruction o = (NativeInstruction)__o;
		return this.hashCode() == o.hashCode() &&
			this.op == o.op && this._args.equals(o._args);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/11
	 */
	@Override
	public final int hashCode()
	{
		int rv = this._hash;
		if (rv == 0)
			this._hash = (rv = (this.op ^
				Arrays.asList(this._args).hashCode()));
		return rv;
	}
	
	/**
	 * Obtains the given argument as an integer.
	 *
	 * @param __i The argument to get.
	 * @return The value of the argument.
	 * @throws ClassCastException If the given argument is not an number.
	 * @throws IndexOutOfBoundsException If the index is not within the
	 * bounds of the instruction arguments.
	 * @since 2018/03/23
	 */
	public int intArgument(int __i)
		throws ClassCastException, IndexOutOfBoundsException
	{
		return this.<Number>argument(__i, Number.class).intValue();
	}
	
	/**
	 * Returns the operation this instruction performs.
	 *
	 * @return The operation performed.
	 * @since 2019/03/23
	 */
	public final int operation()
	{
		return this.op;
	}
	
	/**
	 * Obtains the given argument as a short.
	 *
	 * @param __i The argument to get.
	 * @return The value of the argument.
	 * @throws ClassCastException If the given argument is not a number.
	 * @throws IndexOutOfBoundsException If the index is not within the
	 * bounds of the instruction arguments.
	 * @since 2018/03/23
	 */
	public short shortArgument(int __i)
		throws ClassCastException, IndexOutOfBoundsException
	{
		return this.<Number>argument(__i, Number.class).shortValue();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/03/22
	 */
	@Override
	public final String toString()
	{
		Reference<String> ref = this._string;
		String rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._string = new WeakReference<>((rv =
				NativeInstruction.mnemonic(this.op) + ":" +
				Arrays.asList(this._args)));
		
		return rv;
	}
	
	/**
	 * Returns the argument count of the operation.
	 *
	 * @param __op The operation to get the argument count of.
	 * @return The number of used arguments.
	 * @throws InvalidInstructionException If the encoding is not known.
	 * @since 2019/03/27
	 */
	public static int argumentCount(int __op)
		throws InvalidInstructionException
	{
		// Depends on the encoding
		switch (NativeInstruction.encoding(__op))
		{
			case NativeInstructionType.BREAKPOINT:
			case NativeInstructionType.DEBUG_EXIT:
			case NativeInstructionType.RETURN:
				return 0;
				
			case NativeInstructionType.MEM_HANDLE_COUNT_UP:
				return 1;
			
			case NativeInstructionType.ATOMIC_INT_INCREMENT:
			case NativeInstructionType.BREAKPOINT_MARKED:
			case NativeInstructionType.PING:
			case NativeInstructionType.COPY:
			case NativeInstructionType.INVOKE:
			case NativeInstructionType.LOAD_POOL:
			case NativeInstructionType.SYSTEM_CALL:
			case NativeInstructionType.INVOKE_POINTER_ONLY:
			case NativeInstructionType.MEM_HANDLE_COUNT_DOWN:
				return 2;
					
			case NativeInstructionType.ATOMIC_INT_DECREMENT_AND_GET:
			case NativeInstructionType.DEBUG_POINT:
			case NativeInstructionType.IF_ICMP:
			case NativeInstructionType.IFEQ_CONST:
			case NativeInstructionType.MATH_REG_INT:
			case NativeInstructionType.MATH_CONST_INT:
			case NativeInstructionType.INVOKE_POINTER_AND_POOL:
				return 3;
				
			case NativeInstructionType.MEM_HANDLE_OFF_REG:
			case NativeInstructionType.MEM_HANDLE_OFF_ICONST:
				if (DataType.of(__op & DataType.MASK).isWide())
					return 4;
				return 3;
			
			case NativeInstructionType.MEMORY_OFF_REG:
			case NativeInstructionType.MEMORY_OFF_ICONST:
				if (DataType.of(__op & DataType.MASK).isWide())
					return 5;
				return 4;
				
			case NativeInstructionType.DEBUG_ENTRY:
				return 5;
			
			case NativeInstructionType.ATOMIC_COMPARE_GET_AND_SET:
				return 5;
				
				// {@squirreljme.error JC10 Unknown instruction argument
				// count.}
			default:
				throw new InvalidInstructionException("JC10 " + __op);
		}
	}
	/**
	 * Returns the argument format for the instruction.
	 *
	 * @param __op The operation to get the encoding of.
	 * @return The argument format.
	 * @since 2021/06/10
	 */
	public static InstructionFormat argumentFormat(int __op)
	{
		// Will be checking the format cache
		InstructionFormat[] formatCache = NativeInstruction._FORMAT_CACHE;
		
		// {@squirreljme.error JC4z Invalid operation. (The operation)}
		if (__op < 0 || __op >= NativeInstructionType.MAX_INSTRUCTIONS)
			throw new InvalidInstructionException("JC4z " +
				NativeInstruction.mnemonic(__op));
		
		// Already been cached?
		InstructionFormat rv = formatCache[__op];
		if (rv != null)
			return rv;
		
		// Generate and cache for later
		rv = NativeInstruction.__argumentFormat(__op);
		formatCache[__op] = rv;
		
		return rv;
	}
	
	/**
	 * Decodes the given instruction.
	 * 
	 * @param __dualPool The dual pool to use for 
	 * @param __in The stream to read from.
	 * @return The decoded instruction.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/06/15
	 */
	public static NativeInstruction decode(DualClassRuntimePool __dualPool,
		DataInput __in)
		throws IOException, NullPointerException
	{
		if (__in == null)
			throw new NullPointerException("NARG");
		
		// Decode and determine the instruction format
		int op = __in.readUnsignedByte();
		InstructionFormat format = NativeInstruction.argumentFormat(op);
		
		// Read all instruction arguments
		Object[] args = new Object[format.size()];
		for (int i = 0, n = format.size(); i < n; i++)
		{
			Object v;
			switch (format.get(i))
			{
				case INT32:
					v = __in.readInt();
					break;
					
				case INT64:
					v = __in.readLong();
					break;
					
				case FLOAT32:
					v = __in.readFloat();
					break;
					
				case FLOAT64:
					v = __in.readDouble();
					break;
					
				case REGLIST:
					int len = __in.readUnsignedByte();
					if ((len & 0x80) != 0)
					{
						// Read more length
						len = ((len & 0x7F) << 8) | __in.readUnsignedByte();
						
						// Read in values
						int[] vals = new int[len];
						for (int j = 0; j < len; j++)
							vals[j] = __in.readUnsignedShort();
						
						v = new RegisterList(vals);
					}
					else
					{
						// Read in values
						int[] vals = new int[len];
						for (int j = 0; j < len; j++)
							vals[j] = __in.readUnsignedByte();
						
						v = new RegisterList(vals);
					}
					break;
				
				case VUINT:
				case VUREG:
				case VPOOL:
				case VJUMP:
					// Read variable width data
					int data = __in.readUnsignedByte();
					if ((data & 0x80) != 0)
						data = ((data & 0x7F) << 8) | __in.readUnsignedByte();
					
					if (format.get(i) == ArgumentFormat.VPOOL)
						v = __dualPool.runtimePool().byIndex(data);
					else
						v = data;
					break;
					
				default:
					throw Debugging.oops();
			}
			
			// Store
			args[i] = v; 
		}
		
		// Build instruction
		return new NativeInstruction(op, args);
	}
	
	/**
	 * Returns the encoding of the given instruction.
	 *
	 * @param __op The operation to get the encoding of.
	 * @return The encoding for the given operation.
	 * @since 2019/03/24
	 */
	public static int encoding(int __op)
	{
		// Special operations all use unique encodings so just return their
		// opcode, while all of the other operations use one of the pre-defined
		// encodings.
		int upper = __op & 0xF0;
		if (upper == NativeInstructionType.SPECIAL_A ||
			upper == NativeInstructionType.SPECIAL_B)
			return __op;
		
		// Plain
		return upper;
	}
	
	/**
	 * Returns the mnemonic of the given operation.
	 *
	 * @param __op The operation to get.
	 * @return The mnemonic for the operation.
	 * @since 2019/04/07
	 */
	public static String mnemonic(int __op)
	{
		switch (NativeInstruction.encoding(__op))
		{
			case NativeInstructionType.MATH_REG_INT:
			case NativeInstructionType.MATH_CONST_INT:
				return StackJavaType.of((__op >> 4) & 0x3).name() +
					"_" +
					MathType.of(__op & 0x0F).name() +
					"_" +
					(((__op & 0x80) != 0) ? "CONST" : "REG");
			
			case NativeInstructionType.IF_ICMP:
				{
					CompareType ct = CompareType.of(__op & 0x07);
					if (ct == CompareType.TRUE)
						return "GOTO";
					else if (ct == CompareType.FALSE)
						return "NOP";
					else
						return "IF_ICMP_" + ct.name();
				}
				
			case NativeInstructionType.MEMORY_OFF_REG:
			case NativeInstructionType.MEMORY_OFF_ICONST:
				return "MEM_" +
					(((__op & 0x08) != 0) ? "LOAD" : "STORE") +
					"_" +
					DataType.of(__op & 0x07).name() +
					"_" +
					(((__op & 0x80) != 0) ? "ICONST" : "REG");
			
			case NativeInstructionType.MEM_HANDLE_OFF_REG:
			case NativeInstructionType.MEM_HANDLE_OFF_ICONST:
				return "MEM_HANDLE_OFF_" +
					(((__op & 0x08) != 0) ? "LOAD" : "STORE") +
					"_" +
					DataType.of(__op & 0x07).name() +
					"_" +
					(((__op & 0x80) != 0) ? "ICONST" : "REG") +
					"_JAVA";
				
			case NativeInstructionType.ATOMIC_INT_DECREMENT_AND_GET:
				return "ATOMIC_INT_DECREMENT_AND_GET";
			case NativeInstructionType.ATOMIC_INT_INCREMENT:
				return "ATOMIC_INT_INCREMENT";
			case NativeInstructionType.BREAKPOINT:		return "BREAKPOINT";
			case NativeInstructionType.BREAKPOINT_MARKED:
				return "BREAKPOINT_MARKED";
			case NativeInstructionType.PING:			return "PING";
			case NativeInstructionType.COPY:			return "COPY";
			case NativeInstructionType.DEBUG_ENTRY:		return "DEBUG_ENTRY";
			case NativeInstructionType.DEBUG_EXIT:		return "DEBUG_EXIT";
			case NativeInstructionType.DEBUG_POINT:		return "DEBUG_POINT";
			case NativeInstructionType.IFEQ_CONST:		return "IFEQ_CONST";
			case NativeInstructionType.INVOKE:			return "INVOKE";
			case NativeInstructionType.LOAD_POOL:		return "LOAD_POOL";
			case NativeInstructionType.RETURN:			return "RETURN";
			case NativeInstructionType.SYSTEM_CALL:		return "SYSTEM_CALL";
			
			case NativeInstructionType.INVOKE_POINTER_ONLY:
				return "INVOKE_POINTER_ONLY";
			
			case NativeInstructionType.INVOKE_POINTER_AND_POOL:
				return "INVOKE_POINTER_AND_POOL";
			
			case NativeInstructionType.MEM_HANDLE_COUNT_DOWN:
				return "MEM_HANDLE_COUNT_DOWN";
				
			case NativeInstructionType.MEM_HANDLE_COUNT_UP:
				return "MEM_HANDLE_COUNT_UP";
			
			default:
				return String.format("UNKNOWN_%02x", __op);
		}
	}
	
	/**
	 * Returns the argument format for the instruction.
	 *
	 * @param __op The operation to get the encoding of.
	 * @return The argument format.
	 * @since 2018/04/16
	 */
	private static InstructionFormat __argumentFormat(int __op)
	{
		switch (NativeInstruction.encoding(__op))
		{
				// []
			case NativeInstructionType.BREAKPOINT:
			case NativeInstructionType.DEBUG_EXIT:
			case NativeInstructionType.RETURN:
				return ArgumentFormat.of();
				
				// [r16]
			case NativeInstructionType.MEM_HANDLE_COUNT_UP:
				return ArgumentFormat.of(
					ArgumentFormat.VUREG);
				
				// [i16, p16]
			case NativeInstructionType.BREAKPOINT_MARKED:
			case NativeInstructionType.PING:
				return ArgumentFormat.of(
					ArgumentFormat.VUINT,
					ArgumentFormat.VPOOL);
				
				// [r16, reglist]
			case NativeInstructionType.SYSTEM_CALL:
			case NativeInstructionType.INVOKE:
			case NativeInstructionType.INVOKE_POINTER_ONLY:
				return ArgumentFormat.of(
					ArgumentFormat.VUREG,
					ArgumentFormat.REGLIST);
				
				// [r16, r16]
			case NativeInstructionType.COPY:
			case NativeInstructionType.MEM_HANDLE_COUNT_DOWN:
				return ArgumentFormat.of(
					ArgumentFormat.VUREG,
					ArgumentFormat.VUREG);
				
				// [r16, u16]
			case NativeInstructionType.ATOMIC_INT_INCREMENT:
				return ArgumentFormat.of(
					ArgumentFormat.VUREG,
					ArgumentFormat.VUINT);
				
				// [p16, r16]
			case NativeInstructionType.LOAD_POOL:
				return ArgumentFormat.of(
					ArgumentFormat.VPOOL,
					ArgumentFormat.VUREG);
					
				// [r16, r16, reglist]
			case NativeInstructionType.INVOKE_POINTER_AND_POOL:
				return ArgumentFormat.of(
					ArgumentFormat.VUREG,
					ArgumentFormat.VUREG,
					ArgumentFormat.REGLIST);
					
				// [p16, p16, p16, p16]
			case NativeInstructionType.DEBUG_ENTRY:
				return ArgumentFormat.of(
					ArgumentFormat.VPOOL,
					ArgumentFormat.VPOOL,
					ArgumentFormat.VPOOL,
					ArgumentFormat.VPOOL,
					ArgumentFormat.VUINT);
				
				// [u16, u16, u16]
			case NativeInstructionType.DEBUG_POINT:
				return ArgumentFormat.of(
					ArgumentFormat.VUINT,
					ArgumentFormat.VUINT,
					ArgumentFormat.VUINT);
				
				// [r16, r16, u16]
			case NativeInstructionType.ATOMIC_INT_DECREMENT_AND_GET:
				return ArgumentFormat.of(
					ArgumentFormat.VUREG,
					ArgumentFormat.VUREG,
					ArgumentFormat.VUINT);
				
				// [r16, r16, r16]
			case NativeInstructionType.MATH_REG_INT:
				return ArgumentFormat.of(
					ArgumentFormat.VUREG,
					ArgumentFormat.VUREG,
					ArgumentFormat.VUREG);
					
				// [r16 [+r16], r16+r16, i32]
			case NativeInstructionType.MEMORY_OFF_REG:
				if (DataType.of(__op & DataType.MASK).isWide())
					return ArgumentFormat.of(
						ArgumentFormat.VUREG,
						ArgumentFormat.VUREG,
						ArgumentFormat.VUREG,
						ArgumentFormat.VUREG,
						ArgumentFormat.VUREG);
				return ArgumentFormat.of(
					ArgumentFormat.VUREG,
					ArgumentFormat.VUREG,
					ArgumentFormat.VUREG,
					ArgumentFormat.VUREG);
				
				// [r16 [+r16], r16+r16, i32]
			case NativeInstructionType.MEMORY_OFF_ICONST:
				if (DataType.of(__op & DataType.MASK).isWide())
					return ArgumentFormat.of(
						ArgumentFormat.VUREG,
						ArgumentFormat.VUREG,
						ArgumentFormat.VUREG,
						ArgumentFormat.VUREG,
						ArgumentFormat.INT32);
				return ArgumentFormat.of(
					ArgumentFormat.VUREG,
					ArgumentFormat.VUREG,
					ArgumentFormat.VUREG,
					ArgumentFormat.INT32);
					
				// [r16 [+r16], r16, r16]
			case NativeInstructionType.MEM_HANDLE_OFF_REG:
				if (DataType.of(__op & DataType.MASK).isWide())
					return ArgumentFormat.of(
						ArgumentFormat.VUREG,
						ArgumentFormat.VUREG,
						ArgumentFormat.VUREG,
						ArgumentFormat.VUREG);
				return ArgumentFormat.of(
					ArgumentFormat.VUREG,
					ArgumentFormat.VUREG,
					ArgumentFormat.VUREG);
				
				// [r16 [+r16], r16, i32]
			case NativeInstructionType.MEM_HANDLE_OFF_ICONST:
				if (DataType.of(__op & DataType.MASK).isWide())
					return ArgumentFormat.of(
						ArgumentFormat.VUREG,
						ArgumentFormat.VUREG,
						ArgumentFormat.VUREG,
						ArgumentFormat.INT32);
				return ArgumentFormat.of(
					ArgumentFormat.VUREG,
					ArgumentFormat.VUREG,
					ArgumentFormat.INT32);
			
				// [r16, i32, r16]
			case NativeInstructionType.MATH_CONST_INT:
				return ArgumentFormat.of(
					ArgumentFormat.VUREG,
					ArgumentFormat.INT32,
					ArgumentFormat.VUREG);
				
				// [r16, r16, j16]
			case NativeInstructionType.IF_ICMP:
				return ArgumentFormat.of(
					ArgumentFormat.VUREG,
					ArgumentFormat.VUREG,
					ArgumentFormat.VJUMP);
			
				// [r16, i32, j16]
			case NativeInstructionType.IFEQ_CONST:
				return ArgumentFormat.of(
					ArgumentFormat.VUREG,
					ArgumentFormat.INT32,
					ArgumentFormat.VJUMP);
			
			// [r16 (check), r16 (get), r16 (set), r16 (addr), u16 (off)]
			case NativeInstructionType.ATOMIC_COMPARE_GET_AND_SET:
				return ArgumentFormat.of(
					ArgumentFormat.VUREG,
					ArgumentFormat.VUREG,
					ArgumentFormat.VUREG,
					ArgumentFormat.VUREG,
					ArgumentFormat.VUINT);
		}
		
		// {@squirreljme.error JC11 Invalid operation. (The operation)}
		throw new InvalidInstructionException("JC11 " +
			NativeInstruction.mnemonic(__op));
	}
}

