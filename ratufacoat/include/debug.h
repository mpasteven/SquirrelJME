/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

/**
 * Anything to do with debugging.
 * 
 * @since 2021/02/27
 */

#ifndef SQUIRRELJME_DEBUG_H
#define SQUIRRELJME_DEBUG_H

#include "sjmerc.h"

/* Anti-C++. */
#ifdef __cplusplus
#ifndef SJME_CXX_IS_EXTERNED
#define SJME_CXX_IS_EXTERNED
#define SJME_CXX_SQUIRRELJME_DEBUG_H
extern "C"
{
#endif /* #ifdef SJME_CXX_IS_EXTERNED */
#endif /* #ifdef __cplusplus */

/*--------------------------------------------------------------------------*/

/**
 * Indicates a To-Do and then terminates the program.
 * 
 * @param message The @c printf style message.
 * @param ... Any @c printf style arguments.
 * @return Never returns.
 * @since 2021/02/28 
 */
sjme_returnNever sjme_todo(const char* message, ...);

/*--------------------------------------------------------------------------*/

/* Anti-C++. */
#ifdef __cplusplus
#ifdef SJME_CXX_SQUIRRELJME_DEBUG_H
}
#undef SJME_CXX_SQUIRRELJME_DEBUG_H
#undef SJME_CXX_IS_EXTERNED
#endif /* #ifdef SJME_CXX_SQUIRRELJME_DEBUG_H */
#endif /* #ifdef __cplusplus */

#endif /* SQUIRRELJME_DEBUG_H */
