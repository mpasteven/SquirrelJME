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

/** The size of the handle to test. */
#define HANDLE_SIZE 8

/** Magic number for read. */
#define MAGIC_NUMBER SJME_JINT_C(0xC5C6CDCB)

/** High magic number. */
#define MAGIC_NUMBER_HIGH SJME_JINT_C(0xD7D8DAD9)

/** Desired left value. */
#if defined(SJME_BIG_ENDIAN)
	#define DESIRED_LEFT MAGIC_NUMBER_HIGH
#else
	#define DESIRED_LEFT MAGIC_NUMBER
#endif

/** Desired right value. */
#if defined(SJME_BIG_ENDIAN)
	#define DESIRED_RIGHT MAGIC_NUMBER
#else
	#define DESIRED_RIGHT MAGIC_NUMBER_HIGH
#endif

/** The left most byte. */
#if defined(SJME_BIG_ENDIAN)
	#define LEFT_MOST_BYTE SJME_JINT_C(0xFFFFFFD7)
#else
	#define LEFT_MOST_BYTE SJME_JINT_C(0xFFFFFFCB)
#endif

/**
 * Tests that the endianess writing is correct.
 * 
 * @since 2021/03/06
 */
SJME_TEST_PROTOTYPE(testMemHandleEndian)
{
	sjme_memHandles* handles = NULL;
	sjme_memHandle* handle = NULL;
	sjme_jint readIn;
	sjme_jlong writeOutL;
	
	/* Initialize handles. */
	if (sjme_memHandlesInit(&handles, &shim->error))
		return FAIL_TEST(1);
	
	/* Setup a handle. */
	if (sjme_memHandleNew(handles, &handle,
		SJME_MEMHANDLE_KIND_OBJECT_INSTANCE, HANDLE_SIZE,
		&shim->error))
		return FAIL_TEST(2);
	
	/* Write some 64-bit data. */
	writeOutL.hi = MAGIC_NUMBER_HIGH;
	writeOutL.lo = MAGIC_NUMBER;
	if (sjme_memHandleAccessWide(handle, sjme_true,
		SJME_DATATYPE_LONG, &writeOutL, 0, &shim->error))
		return FAIL_TEST(3);
	
	/* Read left 32-bit. */
	readIn = 0;
	if (sjme_memHandleAccess(handle, sjme_false,
		SJME_DATATYPE_INTEGER, &readIn, 0, &shim->error))
		return FAIL_TEST(4);
	
	/* Check left value */
	if (DESIRED_LEFT != readIn)
		return FAIL_TEST(5);
	
	/* Read right 32-bit. */
	readIn = 0;
	if (sjme_memHandleAccess(handle, sjme_false,
		SJME_DATATYPE_INTEGER, &readIn, 4, &shim->error))
		return FAIL_TEST(6);
	
	/* Check right value */
	if (DESIRED_RIGHT != readIn)
		return FAIL_TEST(7);
	
	/* Read single byte to see if it correctly matches the system. */
	readIn = 0;
	if (sjme_memHandleAccess(handle, sjme_false,
		SJME_DATATYPE_BYTE, &readIn, 0, &shim->error))
		return FAIL_TEST(8);
	
	/* Must be this value. */
	if (LEFT_MOST_BYTE != readIn)
	{
		fprintf(stderr, "Wanted %#x, but was %#x\n",
			LEFT_MOST_BYTE, readIn);
		fflush(stderr);
		
		return FAIL_TEST(9);
	}
	
	/* Then immediately destroy them. */
	if (sjme_memHandlesDestroy(handles, &shim->error))
		return FAIL_TEST(10);
	
	return PASS_TEST();
}
