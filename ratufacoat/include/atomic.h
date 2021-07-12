/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

/**
 * Atomics which are needed for thread safety.
 * 
 * @since 2021/03/06
 */

#ifndef SQUIRRELJME_ATOMIC_H
#define SQUIRRELJME_ATOMIC_H

/* Which atomic set is being used? */
#if defined(__STDC_VERSION__) && __STDC_VERSION__ >= 201112L && \
	!defined(__STDC_NO_ATOMICS__)
	#include <stdatomic.h>

	#define SJME_ATOMIC_C11
#elif defined(_WIN32) || defined(__WIN32__) || defined(__WIN32) || \
	defined(_WINDOWS)
	#define SJME_ATOMIC_WIN32 
#else
	#error No atomic available
#endif

#include "sjmerc.h"
#include "error.h"

/* Anti-C++. */
#ifdef __cplusplus
#ifndef SJME_CXX_IS_EXTERNED
#define SJME_CXX_IS_EXTERNED
#define SJME_CXX_SQUIRRELJME_ATOMIC_H
extern "C"
{
#endif /* #ifdef SJME_CXX_IS_EXTERNED */
#endif /* #ifdef __cplusplus */

/*--------------------------------------------------------------------------*/

/**
 * Atomic integer.
 * 
 * @since 2021/03/06
 */
typedef struct sjme_atomicInt sjme_atomicInt;

#if defined(SJME_ATOMIC_C11)
	struct sjme_atomicInt
	{
		/** Atomic value. */
		_Atomic sjme_jint value;
	};
#elif defined(SJME_ATOMIC_WIN32)
	struct sjme_atomicInt
	{
		/** Atomic value. */
		volatile sjme_jint value;
	};
#else
	#error No sjme_atomicInt
#endif

/**
 * Gets the value of the atomic.
 * 
 * @param atomic The atomic to read from.
 * @return The read value.
 * @since 2021/03/06
 */
sjme_jint sjme_atomicGet(sjme_atomicInt* atomic);

/**
 * Sets the given atomic value.
 * 
 * @param atomic The atomic to set. 
 * @param value The value to set.
 * @since 2021/03/06
 */
void sjme_atomicSet(sjme_atomicInt* atomic, sjme_jint value);

/**
 * Atomically reads the value then adds into the atomic.
 * 
 * @param atomic The atomic to add to.
 * @param add The value to add.
 * @return The value before adding.
 * @since 2021/03/06
 */
sjme_jint sjme_atomicGetAndAdd(sjme_atomicInt* atomic, sjme_jint add);

/**
 * Sets the value of the given atomic provided the check value is a match,
 * it then returns the old value atomically.
 * 
 * @param atomic The atomic to check and set.
 * @param check The value to check against, which if equal will set @c set.
 * @param set The value to set if @c check is equal.
 * @return If @c check matched and the atomic is set.
 * @since 2021/03/06
 */
sjme_jboolean sjme_atomicCompareAndSet(sjme_atomicInt* atomic,
	sjme_jint check, sjme_jint set);

/*--------------------------------------------------------------------------*/

/* Anti-C++. */
#ifdef __cplusplus
#ifdef SJME_CXX_SQUIRRELJME_ATOMIC_H
}
#undef SJME_CXX_SQUIRRELJME_ATOMIC_H
#undef SJME_CXX_IS_EXTERNED
#endif /* #ifdef SJME_CXX_SQUIRRELJME_ATOMIC_H */
#endif /* #ifdef __cplusplus */

#endif /* SQUIRRELJME_ATOMIC_H */
