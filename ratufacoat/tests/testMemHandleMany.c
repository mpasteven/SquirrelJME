/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

#include "tests.h"
#include "handles.h"

/** The number of handles to allocate. */
#define HANDLE_COUNT 524289 /*262145*/

/** The size of each handle. */
#define HANDLE_SIZE 128

/**
 * Tests an allocation of a large number of handles.
 * 
 * @since 2021/03/08
 */
SJME_TEST_PROTOTYPE(testMemHandleMany)
{
	sjme_memHandles* handles = NULL;
	sjme_memHandle* handle = NULL;
	sjme_jint i;
	sjme_memHandleStats stats;
	
	/* Initialize handles. */
	if (sjme_memHandlesInit(&handles, &shim->error))
		return FAIL_TEST(1);
	
	/* Create lots of new handles. */
	for (i = 0; i < HANDLE_COUNT; i++)
	{
		/* Progress note. */
		if ((i & 4095) == 0)
		{
			fprintf(stderr, "At %d of %d...\n", i, HANDLE_COUNT);
			fflush(stderr);
		}
		
		if (sjme_memHandleNew(handles, &handle,
			SJME_MEMHANDLE_KIND_OBJECT_INSTANCE, HANDLE_SIZE,
			&shim->error))
		{
			/* Stop if we ran out of memory! */
			if (shim->error.code == SJME_ERROR_NO_MEMORY)
				break;
			
			return FAIL_TEST(10000 + i);
		}
	}
	
	/* Read stats. */
	if (sjme_memHandlesStats(handles, &stats, &shim->error))
		return FAIL_TEST(2);
	
	/* Then immediately destroy them all, this frees everything. */
	if (sjme_memHandlesDestroy(handles, &shim->error))
		return FAIL_TEST(3);
	
	/** Print a bunch of stats. */
	fprintf(stderr, "reRollHit   : %d\n", stats.reRollHit);
	fprintf(stderr, "reRollMiss  : %d\n", stats.reRollMiss);
	fprintf(stderr, "reRollCount : %d\n", stats.reRollCount);
	fprintf(stderr, "growCount   : %d\n", stats.growCount);
	fprintf(stderr, "numNew      : %d\n", stats.numNew);
	fprintf(stderr, "numDelete   : %d\n", stats.numDelete);
	fprintf(stderr, "downMaskMiss: %d\n", stats.downMaskMiss);
	fflush(stderr);
	
	return PASS_TEST();
}
