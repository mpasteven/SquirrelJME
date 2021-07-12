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

/** Handle test size. */
#define HANDLE_SIZE 128

/**
 * Tests a full cycle for memory handles, everything about them.
 * 
 * @since 2021/02/28 
 */
SJME_TEST_PROTOTYPE(testMemHandleCycle)
{
	sjme_memHandles* handles = NULL;
	sjme_memHandle* handle = NULL;
	
	/* Initialize handles. */
	if (sjme_memHandlesInit(&handles, &shim->error))
		return FAIL_TEST(1);
	
	/* Create a new handle. */
	if (sjme_memHandleNew(handles, &handle,
		SJME_MEMHANDLE_KIND_OBJECT_INSTANCE, HANDLE_SIZE,
		&shim->error))
		return FAIL_TEST(2);
	
	/* Then immediately destroy them. */
	if (sjme_memHandlesDestroy(handles, &shim->error))
		return FAIL_TEST(3);
	
	return PASS_TEST();
}
