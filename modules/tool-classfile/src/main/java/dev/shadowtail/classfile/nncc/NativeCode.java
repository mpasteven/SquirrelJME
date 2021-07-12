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

import cc.squirreljme.runtime.cldc.util.UnmodifiableIterator;
import java.util.Iterator;
import net.multiphasicapps.classfile.ByteCode;

/**
 * This is similar to {@link ByteCode} except that it instead of using a
 * stack for intermediate Java operations, this instead uses registers. This
 * provides a more concise and easier to use format by virtual machines.
 *
 * @see ByteCode
 * @since 2019/03/09
 */
public final class NativeCode
	implements Iterable<NativeInstruction>
{
	/** The zero register. */
	public static final int ZERO_REGISTER =
		0;
	
	/** The return value register (two slots, 1 + 2). */
	public static final int RETURN_REGISTER =
		1;
	
	/** Second return register. */
	public static final int RETURN_TWO_REGISTER =
		2;
	
	/** The exception register. */
	public static final int EXCEPTION_REGISTER =
		3;
	
	/**
	 * The pointer containing static field data.
	 * 
	 * @deprecated Because static fields will be referred to by an instance
	 * of the class information rather than be within a single fixed size
	 * chunk of memory.
	 */
	@Deprecated
	public static final int STATIC_FIELD_REGISTER =
		4;
	
	/** Register which represents the current thread of execution. */
	public static final int THREAD_REGISTER =
		5;
	
	/** Base for local registers (locals start here). */
	public static final int LOCAL_REGISTER_BASE =
		6;
	
	/** The register containing the constant pool. */
	public static final int POOL_REGISTER =
		6;
	
	/**
	 * The register which contains the next pool pointer to use.
	 *
	 * @deprecated This is deprecated because
	 * {@link NativeInstructionType#INVOKE} is deprecated and pool pointers
	 * for invoke will be explicitly set or not used at all.
	 */
	@Deprecated
	public static final int NEXT_POOL_REGISTER =
		7;
	
	/** The register of the first argument. */
	public static final int ARGUMENT_REGISTER_BASE =
		8;
	
	/** Maximum number of registers possible to use. */
	public static final int MAX_REGISTERS =
		128;
	
	/** This bit XORed NEG bit == set, means memory access is volatile. */
	@Deprecated
	public static final int MEMORY_OFF_VOLATILE_BIT =
		0x80000000;
	
	/**
	 * This bit is just used to signal that the offset is negative. After the
	 * volatility has been determined, the volatile bit is set to the value
	 * of this bit then execution continues.
	 */
	public static final int MEMORY_OFF_NEG_BIT =
		0x40000000;
	
	/** Mask to access the offset value. */
	public static final int MEMORY_OFF_VALUE_MASK =
		0x7FFFFFFF;
	
	/** Instructions for this code. */
	private final NativeInstruction[] _instructions;
	
	/**
	 * Initializes the register code.
	 *
	 * @param __i The associated instructions.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/03/22
	 */
	public NativeCode(NativeInstruction[] __i)
		throws NullPointerException
	{
		__i = (__i == null ? new NativeInstruction[0] : __i.clone());
		for (NativeInstruction i : __i)
			if (i == null)
				throw new NullPointerException("NARG");
		
		this._instructions = __i;
	}
	
	/**
	 * Gets the instruction at this index.
	 *
	 * @param __dx The index to get.
	 * @return The register at this index.
	 * @since 2019/03/26
	 */
	public final NativeInstruction get(int __dx)
	{
		return this._instructions[__dx];
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/03/23
	 */
	@Override
	public final Iterator<NativeInstruction> iterator()
	{
		return UnmodifiableIterator.<NativeInstruction>of(
			this._instructions);
	}
	
	/**
	 * Returns the length of the register code.
	 *
	 * @return The register code length.
	 * @since 2019/03/26
	 */
	public final int length()
	{
		return this._instructions.length;
	}
}

