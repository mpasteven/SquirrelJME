/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

/**
 * Software Math Support.
 * 
 * @since 2021/02/27
 */

#ifndef SQUIRRELJME_SOFTMATH_H
#define SQUIRRELJME_SOFTMATH_H

#include "sjmerc.h"

/* Anti-C++. */
#ifdef __cplusplus
#ifndef SJME_CXX_IS_EXTERNED
#define SJME_CXX_IS_EXTERNED
#define SJME_CXX_SQUIRRELJME_SOFTMATH_H
extern "C"
{
#endif /* #ifdef SJME_CXX_IS_EXTERNED */
#endif /* #ifdef __cplusplus */

/*--------------------------------------------------------------------------*/

/**
 * Long return value result
 *
 * @since 2019/12/07
 */
typedef struct sjme_jlong
{
#if defined(SJME_BIG_ENDIAN)
	/** High. */
	sjme_jint hi;
	
	/** Low. */
	sjme_jint lo;
#else
	/** Low. */
	sjme_jint lo;
	
	/** High. */
	sjme_jint hi;
#endif
} sjme_jlong;

/**
 * Division result.
 *
 * @since 2019/06/20
 */
typedef struct sjme_jint_div
{
	/** Quotient. */
	sjme_jint quot;

	/** Remainder. */
	sjme_jint rem;
} sjme_jint_div;

/**
 * Adds the given long value, fractional.
 * 
 * @param a A.
 * @param bLo B, low bits.
 * @param bHi B, high bits.
 * @return The resultant long.
 * @since 2021/03/07
 */
sjme_jlong sjme_addLongF(sjme_jlong a, sjme_jint bLo, sjme_jint bHi);

/**
 * Performs software division.
 * 
 * @param anum The numerator.
 * @param aden The denominator.
 * @return The result of the operation.
 * @since 2021/02/27
 */
sjme_jint_div sjme_divInt(sjme_jint anum, sjme_jint aden);

/**
 * Multiplies the given long value.
 * 
 * @param a A.
 * @param bLo B, low bits.
 * @param bHi B, high bits.
 * @return The resultant long.
 * @since 2021/03/07
 */
sjme_jlong sjme_mulLong(sjme_jlong a, sjme_jlong b);

/**
 * Multiplies the given long value, fractional.
 * 
 * @param a A.
 * @param bLo B, low bits.
 * @param bHi B, high bits.
 * @return The resultant long.
 * @since 2021/03/07
 */
sjme_jlong sjme_mulLongF(sjme_jlong a, sjme_jint bLo, sjme_jint bHi);

/**
 * Unsigned shift right.
 * 
 * @param val The value to shift. 
 * @param sh The shift by amount.
 * @return The unsigned right shift value.
 * @since 2021/03/07
 */
sjme_jint sjme_ushrInt(sjme_jint val, sjme_jint sh);

/*--------------------------------------------------------------------------*/

/* Anti-C++. */
#ifdef __cplusplus
#ifdef SJME_CXX_SQUIRRELJME_SOFTMATH_H
}
#undef SJME_CXX_SQUIRRELJME_SOFTMATH_H
#undef SJME_CXX_IS_EXTERNED
#endif /* #ifdef SJME_CXX_SQUIRRELJME_SOFTMATH_H */
#endif /* #ifdef __cplusplus */

#endif /* SQUIRRELJME_SOFTMATH_H */
