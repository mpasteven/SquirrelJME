/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

/**
 * Old stuff that will be going away eventually.
 * 
 * @since 2021/02/28
 */

#ifndef SQUIRRELJME_OLDSTUFF_H
#define SQUIRRELJME_OLDSTUFF_H

#include "sjmerc.h"
#include "error.h"

/* Anti-C++. */
#ifdef __cplusplus
#ifndef SJME_CXX_IS_EXTERNED
#define SJME_CXX_IS_EXTERNED
#define SJME_CXX_SQUIRRELJME_OLDSTUFF_H
extern "C"
{
#endif /* #ifdef SJME_CXX_IS_EXTERNED */
#endif /* #ifdef __cplusplus */

/*--------------------------------------------------------------------------*/

/**
 * Creates a new virtual memory manager.
 *
 * @param error The error state.
 * @return The virtual memory manager.
 * @since 2019/06/25
 */
sjme_vmem* sjme_vmmnew(sjme_error* error);

/**
 * Virtually maps the given region of memory.
 *
 * @param vmem The virtual memory to map to.
 * @param at The address to map to, if possible to map there.
 * @param ptr The region to map.
 * @param size The number of bytes to map.
 * @param error The error state.
 * @return The memory mapping information, returns @code NULL @endcode on
 * error.
 * @since 2019/06/25
 */
sjme_vmemmap* sjme_vmmmap(sjme_vmem* vmem, sjme_jint at, void* ptr,
	sjme_jint size, sjme_error* error);

/**
 * Resolves the given memory pointer.
 *
 * @param vmem The virtual memory.
 * @param ptr The pointer to resolve.
 * @param off The offset.
 * @param error The error.
 * @since 2019/06/27
 */
void* sjme_vmmresolve(sjme_vmem* vmem, sjme_vmemptr ptr, sjme_jint off,
	sjme_error* error);

/**
 * Convert size to Java type.
 *
 * @param type The input size.
 * @param error The error state.
 * @return The resulting type.
 * @since 2019/06/25
 */
sjme_jint sjme_vmmsizetojavatype(sjme_jint size, sjme_error* error);

/**
 * Convert size to type.
 *
 * @param type The input size.
 * @param error The error state.
 * @return The resulting type.
 * @since 2019/06/25
 */
sjme_jint sjme_vmmsizetotype(sjme_jint size, sjme_error* error);

/**
 * Reads from virtual memory.
 *
 * @param vmem Virtual memory.
 * @param type The type to utilize.
 * @param ptr The pointer address.
 * @param off The offset.
 * @param error The error state.
 * @return The read value.
 * @since 2019/06/25
 */
sjme_jint sjme_vmmread(sjme_vmem* vmem, sjme_jint type, sjme_vmemptr ptr,
	sjme_jint off, sjme_error* error);

/**
 * Reads from virtual memory.
 *
 * @param vmem Virtual memory.
 * @param type The type to utilize.
 * @param ptr The pointer address, is incremented accordingly.
 * @param error The error state.
 * @return The read value.
 * @since 2019/06/25
 */
sjme_jint sjme_vmmreadp(sjme_vmem* vmem, sjme_jint type, sjme_vmemptr* ptr,
	sjme_error* error);

/**
 * Write to virtual memory.
 *
 * @param vmem Virtual memory.
 * @param type The type to utilize.
 * @param ptr The pointer address.
 * @param off The offset.
 * @param val The value to write.
 * @param error The error state.
 * @since 2019/06/25
 */
void sjme_vmmwrite(sjme_vmem* vmem, sjme_jint type, sjme_vmemptr ptr,
	sjme_jint off, sjme_jint val, sjme_error* error);

/**
 * Write to virtual memory.
 *
 * @param vmem Virtual memory.
 * @param type The type to utilize.
 * @param ptr The pointer address, is incremented accordingly.
 * @param val The value to write.
 * @param error The error state.
 * @since 2019/06/25
 */
void sjme_vmmwritep(sjme_vmem* vmem, sjme_jint type, sjme_vmemptr* ptr,
	sjme_jint val, sjme_error* error);

/**
 * Atomically reads, checks, and then sets the value.
 *
 * @param vmem Virtual memory.
 * @param check The check value.
 * @param set The set value.
 * @param ptr The pointer address.
 * @param off The offset.
 * @param error The error state.
 * @return The value which was read.
 * @since 2019/07/01
 */
sjme_jint sjme_vmmatomicintcheckgetandset(sjme_vmem* vmem, sjme_jint check,
	sjme_jint set, sjme_vmemptr ptr, sjme_jint off, sjme_error* error);

/**
 * Atomically increments and integer and then gets its value.
 *
 * @param vmem Virtual memory.
 * @param ptr The pointer address.
 * @param off The offset.
 * @param add The value to add.
 * @param error The error state.
 * @return The value after the addition.
 * @since 2019/06/25
 */
sjme_jint sjme_vmmatomicintaddandget(sjme_vmem* vmem,
	sjme_vmemptr ptr, sjme_jint off, sjme_jint add, sjme_error* error);

/*--------------------------------------------------------------------------*/

/* Anti-C++. */
#ifdef __cplusplus
#ifdef SJME_CXX_SQUIRRELJME_OLDSTUFF_H
}
#undef SJME_CXX_SQUIRRELJME_OLDSTUFF_H
#undef SJME_CXX_IS_EXTERNED
#endif /* #ifdef SJME_CXX_SQUIRRELJME_OLDSTUFF_H */
#endif /* #ifdef __cplusplus */

#endif /* SQUIRRELJME_OLDSTUFF_H */
