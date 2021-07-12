/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

/**
 * SquirrelJME JVM Types.
 * 
 * @since 2021/04/03
 */

#ifndef BUILDVM_SJMERC_H
#define BUILDVM_SJMERC_H

/* Anti-C++. */
#ifdef __cplusplus
#ifndef SJME_CXX_IS_EXTERNED
#define SJME_CXX_IS_EXTERNED
#define SJME_CXX_BUILDVM_SJMERC_H
extern "C"
{
#endif /* #ifdef SJME_CXX_IS_EXTERNED */
#endif /* #ifdef __cplusplus */

/*--------------------------------------------------------------------------*/

/*--------------------------------------------------------------------------*/

/* Anti-C++. */
#ifdef __cplusplus
#ifdef SJME_CXX_BUILDVM_SJMERC_H
}
#undef SJME_CXX_BUILDVM_SJMERC_H
#undef SJME_CXX_IS_EXTERNED
#endif /* #ifdef SJME_CXX_BUILDVM_SJMERC_H */
#endif /* #ifdef __cplusplus */

#endif /* BUILDVM_SJMERC_H */
