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

#define HANDLE_SIZE 128

/**
 * Test that invalid actions on handles fail.
 * 
 * @since 2021/02/28 
 */
SJME_TEST_PROTOTYPE(testMemHandleInvalid)
{
	sjme_memHandles* handles = NULL;
	sjme_memHandle* handle = NULL;
	
	/* No output handles. */
	sjme_clearError(&shim->error);
	if (!sjme_memHandlesInit(NULL, &shim->error))
		return FAIL_TEST(1);
	
	/* Error not set? */
	if (!sjme_hasError(&shim->error))
		return FAIL_TEST(2);
	
	/* Initialize handles. */
	sjme_clearError(&shim->error);
	if (sjme_memHandlesInit(&handles, &shim->error))
		return FAIL_TEST(3);
	
	/* Negative size. */
	sjme_clearError(&shim->error);
	if (!sjme_memHandleNew(handles, &handle,
		SJME_MEMHANDLE_KIND_OBJECT_INSTANCE, -127, &shim->error))
		return FAIL_TEST(5);
	
	/* Error not set? */
	if (!sjme_hasError(&shim->error))
		return FAIL_TEST(6);
	
	/* Invalid handle (low). */
	sjme_clearError(&shim->error);
	if (!sjme_memHandleNew(handles, &handle,
		SJME_MEMHANDLE_KIND_UNDEFINED, HANDLE_SIZE, &shim->error))
		return FAIL_TEST(7);
	
	/* Error not set? */
	if (!sjme_hasError(&shim->error))
		return FAIL_TEST(8);
	
	/* Invalid handle (high). */
	sjme_clearError(&shim->error);
	if (!sjme_memHandleNew(handles, &handle,
		SJME_MEMHANDLE_KIND_NUM_KINDS, HANDLE_SIZE, &shim->error))
		return FAIL_TEST(9);
	
	/* Error not set? */
	if (!sjme_hasError(&shim->error))
		return FAIL_TEST(10);
	
	/* Delete null handle? */
	sjme_clearError(&shim->error);
	if (!sjme_memHandleDelete(handles, NULL, &shim->error))
		return FAIL_TEST(11);
	
	/* Error not set? */
	if (!sjme_hasError(&shim->error))
		return FAIL_TEST(12);
	
	/* Setup actual handle for testing. */
	sjme_clearError(&shim->error);
	if (sjme_memHandleNew(handles, &handle,
		SJME_MEMHANDLE_KIND_OBJECT_INSTANCE, HANDLE_SIZE,
			&shim->error))
		return FAIL_TEST(13);
	
	/* Delete valid handle with no handle placement? */
	sjme_clearError(&shim->error);
	if (!sjme_memHandleDelete(NULL, handle, &shim->error))
		return FAIL_TEST(14);
	
	/* Error not set? */
	if (!sjme_hasError(&shim->error))
		return FAIL_TEST(15);
	
	/* Delete valid handle for cleanup */
	sjme_clearError(&shim->error);
	if (sjme_memHandleDelete(handles, handle, &shim->error))
		return FAIL_TEST(16);
	
	/* Then immediately destroy them. */
	sjme_clearError(&shim->error);
	if (sjme_memHandlesDestroy(handles, &shim->error))
		return FAIL_TEST(17);
	
	return PASS_TEST();
}
