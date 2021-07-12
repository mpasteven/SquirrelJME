/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

/**
 * Opcode decoding and otherwise.
 * 
 * @since 2021/02/28
 */

#ifndef SQUIRRELJME_OPCODE_H
#define SQUIRRELJME_OPCODE_H

#include "error.h"

/* Anti-C++. */
#ifdef __cplusplus
#ifndef SJME_CXX_IS_EXTERNED
#define SJME_CXX_IS_EXTERNED
#define SJME_CXX_SQUIRRELJME_OPCODE_H
extern "C"
{
#endif /* #ifdef SJME_CXX_IS_EXTERNED */
#endif /* #ifdef __cplusplus */

/*--------------------------------------------------------------------------*/

/** Encoding mask. */
#define SJME_ENC_MASK UINT8_C(0xF0)

/** Math, R=RR, Integer. */
#define SJME_ENC_MATH_REG_INT UINT8_C(0x00)

/** Comparison mask. */
#define SJME_ENC_COMPARE_MASK UINT8_C(0x07)

/** Int comparison, then maybe jump. */
#define SJME_ENC_IF_ICMP UINT8_C(0x10)

/** Math type mask. */
#define SJME_ENC_MATH_MASK UINT8_C(0x0F)

/** Mask for Java order mode. */
#define SJME_ENC_MEMORY_JAVA_MASK UINT8_C(0x10)

/** Memory access, offset is in register. */
#define SJME_ENC_MEMORY_OFF_REG UINT8_C(0x20)

/** Memory access to big endian Java format, offset is in register. */
#define SJME_ENC_MEMORY_OFF_REG_JAVA UINT8_C(0x30)

/** Math, R=RC, Integer. */
#define SJME_ENC_MATH_CONST_INT UINT8_C(0x80)

/** Memory access, offset is a constant. */
#define SJME_ENC_MEMORY_OFF_ICONST UINT8_C(0xA0)

/** Memory access to big endian Java format, offset is a constant. */
#define SJME_ENC_MEMORY_OFF_ICONST_JAVA UINT8_C(0xB0)

/** Special. */
#define SJME_ENC_SPECIAL_A UINT8_C(0xE0)

/** Special.*/
#define SJME_ENC_SPECIAL_B UINT8_C(0xF0)

/** If equal to constant. */
#define SJME_OP_IFEQ_CONST UINT8_C(0xE6)

/** Debug entry to method. */
#define SJME_OP_DEBUG_ENTRY UINT8_C(0xE8)

/** Debug exit from method. */
#define SJME_OP_DEBUG_EXIT UINT8_C(0xE9)

/** Debug single point in method. */
#define SJME_OP_DEBUG_POINT UINT8_C(0xEA)

/** Return. */
#define SJME_OP_RETURN UINT8_C(0xF3)

/** Store to pool, note that at code gen time this is aliased. */
#define SJME_OP_STORE_POOL UINT8_C(0xF4)

/** Store to integer array. */
#define SJME_OP_STORE_TO_INTARRAY UINT8_C(0xF5)

/** Invoke. */
#define SJME_OP_INVOKE UINT8_C(0xF7)

/** Copy value in register. */
#define SJME_OP_COPY UINT8_C(0xF8)

/** Atomically decrements a memory address and gets the value. */
#define SJME_OP_ATOMIC_INT_DECREMENT_AND_GET UINT8_C(0xF9)

/** Atomically increments a memory address. */
#define SJME_OP_ATOMIC_INT_INCREMENT UINT8_C(0xFA)

/** System call. */
#define SJME_OP_SYSTEM_CALL UINT8_C(0xFB)

/** Atomically checks, gets, and sets (if matched) the value. */
#define SJME_OP_ATOMIC_COMPARE_GET_AND_SET UINT8_C(0xFC)

/** Load from pool, note that at code gen time this is aliased. */
#define SJME_OP_LOAD_POOL UINT8_C(0xFD)

/** Load from integer array. */
#define SJME_OP_LOAD_FROM_INTARRAY UINT8_C(0xFE)

/** Compare and exchange. */
#define SJME_OP_BREAKPOINT UINT8_C(0xFF)

/**
 * Decodes an integer value from operations which could be unaligned.
 *
 * @param vmem Virtual memory.
 * @param ptr The pointer to read from.
 * @param error Error flag.
 * @return The resulting read value.
 * @since 2019/06/16
 */
sjme_jint sjme_opdecodejint(sjme_vmem* vmem, sjme_vmemptr* ptr,
	sjme_error* error);

/**
 * Decodes a short value from operations which could be unaligned.
 *
 * @param vmem Virtual memory.
 * @param ptr The pointer to read from.
 * @param error Error flag.
 * @return The resulting read value.
 * @since 2019/06/16
 */
sjme_jint sjme_opdecodejshort(sjme_vmem* vmem, sjme_vmemptr* ptr,
	sjme_error* error);
	
/**
 * Decodes a variable unsigned int operation argument.
 *
 * @param vmem Virtual memory.
 * @param ptr The pointer to read from.
 * @param error Error flag.
 * @return The resulting decoded value.
 * @since 2019/06/09
 */
sjme_jint sjme_opdecodeui(sjme_vmem* vmem, sjme_vmemptr* ptr,
	sjme_error* error);
	
/**
 * Decodes register from the virtual machine.
 *
 * @param vmem Virtual memory.
 * @param ptr The pointer to read from.
 * @param error Error flag.
 * @return The resulting register value.
 * @since 2019/06/25
 */
sjme_jint sjme_opdecodereg(sjme_vmem* vmem, sjme_vmemptr* ptr,
	sjme_error* error);

/**
 * Decodes a relative jump offset.
 *
 * @param vmem Virtual memory.
 * @param ptr The pointer to read from.
 * @param error Error flag.
 * @return The resulting relative jump.
 * @since 2019/06/13
 */
sjme_jint sjme_opdecodejmp(sjme_vmem* vmem, sjme_vmemptr* ptr,
	sjme_error* error);
	
/*--------------------------------------------------------------------------*/

/* Anti-C++. */
#ifdef __cplusplus
#ifdef SJME_CXX_SQUIRRELJME_OPCODE_H
}
#undef SJME_CXX_SQUIRRELJME_OPCODE_H
#undef SJME_CXX_IS_EXTERNED
#endif /* #ifdef SJME_CXX_SQUIRRELJME_OPCODE_H */
#endif /* #ifdef __cplusplus */

#endif /* SQUIRRELJME_OPCODE_H */
