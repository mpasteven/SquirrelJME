/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// --------------------------------------------------------------------------*/

/**
 * Native memory functions.
 *
 * @since 2019/06/25
 */

/* Palm OS Functions. */
#if defined(__palmos__)
	#include <MemoryMgr.h>
	#include <MemGlue.h>
#else
	#include <string.h>
#endif

#include "sjmerc.h"
#include "memory.h"

void* sjme_malloc(sjme_jint size)
{
	void* rv;
	
	/* These will never allocate. */
	if (size <= 0)
		return NULL;
	
	/* Round size and include extra 4-bytes for size storage. */
	size = ((size + SJME_JINT_C(3)) & (~SJME_JINT_C(3))) +
		SJME_JINT_C(4);
	
	/* Exceeds maximum permitted allocation size? */
	if (sizeof(sjme_jint) > sizeof(size_t) && size > (sjme_jint)SIZE_MAX)
		return NULL;
	
#if defined(__palmos__)
	/* Palm OS, use glue to allow greater than 64K. */
	rv = MemGluePtrNew(size);
#else
	/* Use standard C function otherwise. */
	rv = calloc(1, size);
#endif

	/* Did not allocate? */
	if (rv == NULL)
		return NULL;
		
#if defined(__palmos__)
	/* Clear memory on Palm OS. */
	MemSet(rv, size, 0);
#else
	/* Clear values to zero. */
	memset(rv, 0, size);
#endif
	
	/* Store the size into this memory block for later free. */
	*((sjme_jint*)rv) = size;
	
	/* Return the adjusted pointer. */
	return SJME_POINTER_OFFSET_LONG(rv, 4);
}

void* sjme_realloc(void* ptr, sjme_jint size)
{
	void* rv;
	sjme_jint oldSize;
	
	/* These will never allocate. */
	if (size <= 0)
	{
		/* Free pointer? */
		if (ptr != NULL)
			sjme_free(ptr);
		
		return NULL;
	}
	
	/* Allocate new pointer, keep old pointer if this failed. */
	rv = sjme_malloc(size);
	if (rv == NULL)
		return NULL;
	
	/* Copy old data over? */
	if (ptr != NULL)
	{
		/* Get the old size, to copy the data around. */
		oldSize = *((sjme_jint*)(SJME_POINTER_OFFSET_LONG(ptr, -4)));
		
		/* Only copy the smaller of size. */
		memmove(rv, ptr, (size > oldSize ? oldSize : size));
		
		/* Free the old pointer. */
		sjme_free(ptr);
	}
	
	/* Return the new pointer. */
	return rv;
}

void sjme_free(void* p)
{
	void* baseP;
	
	/* Ignore null pointers. */
	if (p == NULL)
		return;
	
	/* Base pointer which is size shifted. */
	baseP = SJME_POINTER_OFFSET_LONG(p, -4);
	
#if defined(__palmos__)
	/* Use Palm OS free. */
	MemPtrFree(baseP);
#else
	/* Use Standard C free. */
	free(baseP);
#endif
}
