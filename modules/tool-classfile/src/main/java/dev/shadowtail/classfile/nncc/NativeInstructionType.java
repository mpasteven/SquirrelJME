// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.nncc;

/**
 * This represents a type of native instruction. Virtually all instructions
 * except for the special instructions are composed of groups.
 *
 * @since 2019/04/06
 */
public interface NativeInstructionType
{
	/**
	 * Math, R=RR, Integer.
	 * {@code iiiiMMMM, r3 = r1 ? r2}.
	 */
	short MATH_REG_INT =
		0x00;
	
	/**
	 * Int comparison, then maybe jump.
	 * {@code iiiiRccc, if (r1 ? r2) jump->j3}.
	 */
	short IF_ICMP =
		0x10;
	
	/**
	 * Memory access, offset is in register.
	 * {@code iiiiLddd, L=T load r1 = *(r2 + r3), L=F set *(r2 + r3) = r1}.
	 */
	short MEMORY_OFF_REG =
		0x20;
	
	/**
	 * Memory access to a memory handle (register).
	 * {@code iiiiLddd, L=T load r1 = *(r2 + r3), L=F set *(r2 + r3) = r1}.
	 */
	short MEM_HANDLE_OFF_REG =
		0x40;
	
	/**
	 * Math, R=RC, Integer.
	 * {@code iiiiMMMM, r3 = r1 ? c2}.
	 */
	short MATH_CONST_INT =
		0x80;
	
	/**
	 * Memory access, offset is a constant.
	 * {@code iiiiLddd, L=T load r1 = *(r2 + r3), L=F set *(r2 + r3) = r1}.
	 */
	short MEMORY_OFF_ICONST =
		0xA0;
	
	/**
	 * Memory access to a memory handle (constant value).
	 * {@code iiiiLddd, L=T load r1 = *(r2 + r3), L=F set *(r2 + r3) = r1}.
	 */
	short MEM_HANDLE_OFF_ICONST =
		0xC0;
	
	/**
	 * Special.
	 * {@code iiiixxxx}.
	 */
	short SPECIAL_A =
		0xE0;
	
	/**
	 * Special.
	 * {@code iiiixxxx}.
	 */
	short SPECIAL_B =
		0xF0;
	
	/** If equal to constant. */
	short IFEQ_CONST =
		0xE6;
	
	/** Debug entry to method. */
	short DEBUG_ENTRY =
		0xE8;
	
	/** Debug exit from method. */
	short DEBUG_EXIT =
		0xE9;
	
	/** Debug single point in method. */
	short DEBUG_POINT =
		0xEA;
		
	/**
	 * Breakpoint that is marked.
	 */
	short BREAKPOINT_MARKED =
		0xEB;
	
	/** Ping. */
	short PING =
		0xEC;
	
	/** Count memory handle down. */
	short MEM_HANDLE_COUNT_DOWN =
		0xED;
	
	/** Count memory handle up. */
	short MEM_HANDLE_COUNT_UP =
		0xEE;
	
	/** Invoke only a given pointer. */
	short INVOKE_POINTER_ONLY =
		0xEF;
	
	/** Invoke for pool and pointer. */
	short INVOKE_POINTER_AND_POOL =
		0xF0;
	
	/**
	 * Return. 
	 * {@code iiiixxxx, return}.
	 */
	short RETURN =
		0xF3;
	
	/**
	 * Invoke. 
	 * {@code iiiixxxx}.
	 * 
	 * @deprecated This instruction will use the deprecated
	 * {@link NativeCode#NEXT_POOL_REGISTER} and copy it to the pool register
	 * accordingly, use {@link NativeInstructionType#INVOKE_POINTER_ONLY} or
	 * {@link NativeInstructionType#INVOKE_POINTER_AND_POOL} instead.
	 */
	@Deprecated
	short INVOKE =
		0xF7;
	
	/** Copy value in register. */
	short COPY =
		0xF8;
	
	/** Atomically decrements a memory address and gets the value. */
	short ATOMIC_INT_DECREMENT_AND_GET =
		0xF9;
	
	/** Atomically increments a memory address. */
	short ATOMIC_INT_INCREMENT =
		0xFA;
	
	/** System call, used for extra functionality. */
	short SYSTEM_CALL =
		0xFB;
	
	/** Atomic compare and set. */
	short ATOMIC_COMPARE_GET_AND_SET =
		0xFC;
	
	/**
	 * Load from pool, note that at code gen time this is aliased.
	 * {@code iiiixxxx}.
	 * 
	 * @deprecated This will eventually become a special Object-esque that
	 * is completely code dependent.
	 */
	@Deprecated
	short LOAD_POOL =
		0xFD;
	
	/**
	 * Breakpoint.
	 */
	short BREAKPOINT =
		0xFF;
	
	/** Maximum instructions. */
	short MAX_INSTRUCTIONS =
		256;
}

