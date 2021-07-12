/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

#include "atomic.h"

#if defined(SJME_ATOMIC_WIN32)
	#include <windows.h>
#endif

sjme_jint sjme_atomicGet(sjme_atomicInt* atomic)
{
#if defined(SJME_ATOMIC_C11)
	return atomic_load(&atomic->value);
#else
	return sjme_atomicGetAndAdd(atomic, 0);
#endif
}

void sjme_atomicSet(sjme_atomicInt* atomic, sjme_jint value)
{
#if defined(SJME_ATOMIC_C11)
	atomic_store(&atomic->value, value);
#elif defined(SJME_ATOMIC_WIN32)
	InterlockedExchange((volatile long*)&atomic->value, value);
#else
	#error No sjme_atomicSet
#endif
}

sjme_jint sjme_atomicGetAndAdd(sjme_atomicInt* atomic, sjme_jint add)
{
#if defined(SJME_ATOMIC_C11)
	return atomic_fetch_add(&atomic->value, add);
#elif defined(SJME_ATOMIC_WIN32)
	/* This performs an add and get, however to do a get and add we need */
	/* to subtract what we just added to get the original value. */
	return InterlockedAdd((volatile long*)&atomic->value, add) - add;
#else
	#error No sjme_atomicGetAndAdd
#endif
}

sjme_jboolean sjme_atomicCompareAndSet(sjme_atomicInt* atomic,
	sjme_jint check, sjme_jint set)
{
#if defined(SJME_ATOMIC_C11)
	if (atomic_compare_exchange_strong(&atomic->value, &check, set))
		return sjme_true;
	return sjme_false;
#elif defined(SJME_ATOMIC_WIN32)
	LONG was;
	
	/* Returns the value that was stored here. */
	was = InterlockedCompareExchange((volatile long*)&atomic->value,
		set, check);
	
	if (was == check)
		return sjme_true;
	return sjme_false;
#else
	#error No sjme_atomicCompareAndSet
#endif
}

