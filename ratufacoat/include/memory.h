/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

/**
 * Native memory functions.
 * 
 * @since 2021/02/28
 */

#ifndef SQUIRRELJME_MEMORY_H
#define SQUIRRELJME_MEMORY_H

#include "sjmerc.h"
#include "datatype.h"

/* Anti-C++. */
#ifdef __cplusplus
#ifndef SJME_CXX_IS_EXTERNED
#define SJME_CXX_IS_EXTERNED
#define SJME_CXX_SQUIRRELJME_MEMORY_H
extern "C"
{
#endif /* #ifdef SJME_CXX_IS_EXTERNED */
#endif /* #ifdef __cplusplus */

/*--------------------------------------------------------------------------*/

/**
 * Allocates the given number of bytes.
 *
 * @param size The number of bytes to allocate or @c NULL if that failed.
 * @since 2019/06/07
 */
void* sjme_malloc(sjme_jint size);

/**
 * Re-allocates the given pointer.
 *
 * @param ptr The pointer to change in size.
 * @param size The number of bytes to allocate or @c NULL if that failed.
 * @since 2021/03/08
 */
void* sjme_realloc(void* ptr, sjme_jint size);

/**
 * Frees the given pointer.
 *
 * @param p The pointer to free.
 * @since 2019/06/07
 */
void sjme_free(void* p);

/*--------------------------------------------------------------------------*/

/* Anti-C++. */
#ifdef __cplusplus
#ifdef SJME_CXX_SQUIRRELJME_MEMORY_H
}
#undef SJME_CXX_SQUIRRELJME_MEMORY_H
#undef SJME_CXX_IS_EXTERNED
#endif /* #ifdef SJME_CXX_SQUIRRELJME_MEMORY_H */
#endif /* #ifdef __cplusplus */

#endif /* SQUIRRELJME_MEMORY_H */
