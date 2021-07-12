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
#include "datatype.h"

/** Size of the test handle. */
#define HANDLE_SIZE 8

/** Filler value. */
#define FILLER_NUMBER SJME_JINT_C(0xA1A2AFA0)

/** High filler value. */
#define FILLER_NUMBER_HIGH SJME_JINT_C(0xB3B4BEBD)

/** Magic number for read. */
#define MAGIC_NUMBER SJME_JINT_C(0xC5C6CDCB)

/** High magic number. */
#define MAGIC_NUMBER_HIGH SJME_JINT_C(0xD7D8DAD9)

/** Narrow test types. */
static const sjme_dataType testTypesNarrow[] = {
	SJME_DATATYPE_BYTE, SJME_DATATYPE_CHARACTER,
	SJME_DATATYPE_SHORT, SJME_DATATYPE_INTEGER,
	SJME_DATATYPE_FLOAT, SJME_DATATYPE_OBJECT, -1};
	
/** Mask for the test types. */
static const sjme_jint testTypesNarrowValue[] = {
	0xFFFFFFCB, 0x0000CDCB,
	0xFFFFCDCB, MAGIC_NUMBER,
	MAGIC_NUMBER, MAGIC_NUMBER,
	-1};

/** Long test types. */
static const enum sjme_dataType testTypesWide[] = {
	SJME_DATATYPE_LONG, SJME_DATATYPE_DOUBLE, -1};

/**
 * Tests accessing memory handles.
 * 
 * @since 2021/03/06 
 */
SJME_TEST_PROTOTYPE(testMemHandleAccess)
{
	sjme_memHandles* handles = NULL;
	sjme_memHandle* handle = NULL;
	sjme_jint readIn, writeOut;
	sjme_jlong readInL, writeOutL;
	int i;
	
	/* Initialize handles. */
	if (sjme_memHandlesInit(&handles, &shim->error))
		return FAIL_TEST(1);
	
	/* Setup a handle. */
	if (sjme_memHandleNew(handles, &handle,
		SJME_MEMHANDLE_KIND_OBJECT_INSTANCE, HANDLE_SIZE,
		&shim->error))
		return FAIL_TEST(2);
	
	/* In bounds. */
	if (sjme_memHandleInBounds(handle, 0, HANDLE_SIZE, &shim->error))
		return FAIL_TEST(3);
	
	/* Negative offset. */
	sjme_clearError(&shim->error);
	if (!sjme_memHandleInBounds(handle, -1, HANDLE_SIZE, &shim->error))
		return FAIL_TEST(4);
	
	/* Missing error? */
	if (!sjme_hasError(&shim->error))
		return FAIL_TEST(5);
	
	/* Negative size. */
	sjme_clearError(&shim->error);
	if (!sjme_memHandleInBounds(handle, 0, -1, &shim->error))
		return FAIL_TEST(6);
		
	/* Missing error? */
	if (!sjme_hasError(&shim->error))
		return FAIL_TEST(7);
	
	/* Off right side. */
	sjme_clearError(&shim->error);
	if (!sjme_memHandleInBounds(handle, 4, HANDLE_SIZE * 2,
		&shim->error))
		return FAIL_TEST(8);
		
	/* Missing error? */
	if (!sjme_hasError(&shim->error))
		return FAIL_TEST(9);
	
	/* Clear error for future writes. */
	sjme_clearError(&shim->error);
	
	/* Read/Write/Read tests for readIn. */
	for (i = 0; testTypesNarrow[i] != -1; i++)
	{
		/* Read in initial value, which should be zero! */
		readIn = FILLER_NUMBER;
		if (sjme_memHandleAccess(handle, sjme_false, testTypesNarrow[i],
			&readIn, 0, &shim->error))
			return FAIL_TEST(100 + i);
		
		/* This value MUST be zero. */
		if (readIn != 0)
			return FAIL_TEST(110 + i);
		
		/* Write full value. */
		writeOut = MAGIC_NUMBER;
		if (sjme_memHandleAccess(handle, sjme_true, testTypesNarrow[i],
			&writeOut, 0, &shim->error))
			return FAIL_TEST(120 + i);
		
		/* Read the value again. */
		readIn = FILLER_NUMBER;
		if (sjme_memHandleAccess(handle, sjme_false, testTypesNarrow[i],
			&readIn, 0, &shim->error))
			return FAIL_TEST(130 + i);
		
		/* Whatever value is read, must match what is expected. */
		if (readIn != testTypesNarrowValue[i])
			return FAIL_TEST(140 + i);
		
		/* Write zero back in. */
		writeOut = 0;
		if (sjme_memHandleAccess(handle, sjme_true, testTypesNarrow[i],
			&writeOut, 0, &shim->error))
			return FAIL_TEST(150 + i);
	}
	
	/* Read/Write/Read tests for readIn (wide). */
	for (i = 0; testTypesWide[i] != -1; i++)
	{
		/* Read in initial value, which should be zero! */
		readInL.hi = FILLER_NUMBER_HIGH;
		readInL.lo = FILLER_NUMBER;
		if (sjme_memHandleAccessWide(handle, sjme_false, testTypesWide[i],
			&readInL, 0, &shim->error))
			return FAIL_TEST(200 + i);
		
		/* This value MUST be zero. */
		if (readInL.hi != 0 || readInL.lo != 0)
			return FAIL_TEST(210 + i);
		
		/* Write full value. */
		writeOutL.hi = MAGIC_NUMBER_HIGH;
		writeOutL.lo = MAGIC_NUMBER;
		if (sjme_memHandleAccessWide(handle, sjme_true, testTypesWide[i],
			&writeOutL, 0, &shim->error))
			return FAIL_TEST(220 + i);
		
		/* Read the value again. */
		readInL.hi = FILLER_NUMBER_HIGH;
		readInL.lo = FILLER_NUMBER;
		if (sjme_memHandleAccessWide(handle, sjme_false, testTypesWide[i],
			&readInL, 0, &shim->error))
			return FAIL_TEST(230 + i);
		
		/* Must be equal values. */
		if (readInL.hi != MAGIC_NUMBER_HIGH &&
			readInL.lo != MAGIC_NUMBER)
		{
			fprintf(stderr, "0x%08x:%08x != 0x%08x:%08x\n",
				readInL.hi, readInL.lo, MAGIC_NUMBER_HIGH, MAGIC_NUMBER);
			fflush(stderr);
			
			return FAIL_TEST(240 + i);
		}
		
		/* Write zero back in. */
		writeOutL.hi = 0;
		writeOutL.lo = 0;
		if (sjme_memHandleAccessWide(handle, sjme_true, testTypesWide[i],
			&writeOutL, 0, &shim->error))
			return FAIL_TEST(250 + i);
	}
	
	/* Test using narrow types with wide. */
	for (i = 0; testTypesNarrow[i] != -1; i++)
	{
		/* Using the wrong method. */
		sjme_clearError(&shim->error);
		if (!sjme_memHandleAccessWide(handle, sjme_false,
			testTypesNarrow[i], &readInL, 0, &shim->error))
			return FAIL_TEST(300 + i);
			
		/* Missing error? */
		if (!sjme_hasError(&shim->error))
			return FAIL_TEST(310 + i);
	}
	
	/* Test using wide types with narrow. */
	for (i = 0; testTypesWide[i] != -1; i++)
	{
		/* Using the wrong method. */
		sjme_clearError(&shim->error);
		if (!sjme_memHandleAccess(handle, sjme_false,
			testTypesWide[i], &readIn, 0, &shim->error))
			return FAIL_TEST(400 + i);
			
		/* Missing error? */
		if (!sjme_hasError(&shim->error))
			return FAIL_TEST(410 + i);
	}
	
	/* Then immediately destroy them. */
	if (sjme_memHandlesDestroy(handles, &shim->error))
		return FAIL_TEST(10);
	
	return PASS_TEST();
}
