/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// --------------------------------------------------------------------------*/

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "builtin.h"
#include "tests.h"
#include "stringies.h"

/**
 * Exit code for termination from tests
 * 
 * @a https://cmake.org/cmake/help/latest/prop_test/SKIP_RETURN_CODE.html#prop_test:SKIP_RETURN_CODE
 * @since 2021/03/04
 */
#define CTEST_EXIT_SKIPPED 42

sjme_jint shimStdErrWrite(sjme_jint b)
{
	sjme_jbyte v;
	
	/* Write. */
	v = (sjme_jbyte)b;
	fwrite(&v, 1, 1, stderr);
	
	/* EOF? */
	if (feof(stderr))
		return SJME_JINT_C(0);
	
	/* Error? */
	if (ferror(stderr))
		return SJME_JINT_C(-1);
	
	return SJME_JINT_C(1);
}

/**
 * Creates a new shim.
 * 
 * @return The new shim.
 * @since 2021/03/04 
 */
sjme_testShim* shimNew()
{
	sjme_testShim* rv;
	
	/* Allocate shim. */
	rv = malloc(sizeof(*rv));
	memset(rv, 0, sizeof(*rv));
	
	/* Setup native methods. */
	rv->nativeFunctions = malloc(sizeof(*rv->nativeFunctions));
	memset(rv->nativeFunctions, 0, sizeof(*rv->nativeFunctions));
	
	/* Link native methods. */
	rv->nativeFunctions->stderr_write = shimStdErrWrite;
	
	/* Use this shim. */
	return rv;
}

/**
 * Destroys an existing shim.
 * 
 * @param shim The shim to destroy.
 * @since 2021/03/04 
 */
void shimDestroy(sjme_testShim* shim)
{
	free(shim->nativeFunctions);
	free(shim);
}

/**
 * Single test information.
 * 
 * @since 2020/08/12
 */
typedef struct sjme_singleTest
{
	const char* name;
	int (*function)(sjme_testShim* shim);
} sjme_singleTest;

/** Macro for making a test. */
#define SJME_TEST(name) {#name, name}

/** Test table. */
static const sjme_singleTest sjme_singleTests[] =
{
	SJME_TEST(testAtomic),
	SJME_TEST(testJvmInit),
	SJME_TEST(testJvmInvalid),
	SJME_TEST(testMemHandleAccess),
	SJME_TEST(testMemHandleCycle),
	SJME_TEST(testMemHandleEndian),
	SJME_TEST(testMemHandleInit),
	SJME_TEST(testMemHandleInvalid),
	SJME_TEST(testMemHandleMany),
	SJME_TEST(testNothing),
	SJME_TEST(testOpCodes),
	SJME_TEST(testRandom),
	SJME_TEST(testSkipped),
	
	/* End of tests. */
	{NULL, NULL}
};

/**
 * Main entry point.
 * 
 * @param argc Argument count.
 * @param argv Arguments.
 * @return @a EXIT_SUCCESS on success, otherwise @a EXIT_FAILURE.
 * @since 2020/08/12
 */
int main(int argc, char** argv)
{
#define ERROR_BUF_LEN 512
	int allTests, execTest, currentExitCode, ranTests, result, skippedTests;
	sjme_testShim* shim;
	const sjme_singleTest* test;
	sjme_jbyte errorBuf[ERROR_BUF_LEN];
	sjme_jint errorBufLen;
	
	/* General test report. */
	fprintf(stderr, "Testing SquirrelJME %s\n",
		SQUIRRELJME_VERSION" ("SQUIRRELJME_VERSION_ID")");
	fprintf(stderr, "Built-In ROM is %s\n",
		(sjme_builtInRomSize == 0 ? "Unavailable" : "Available"));
	fflush(stderr);
	
	/* Running all tests? */
	if (argc == 1 || argv[0] == NULL)
		allTests = 1;
	else
		allTests = 0;
	
	/* Assume all pass initially. */
	currentExitCode = EXIT_SUCCESS;
	
	/* Used to determine how to exit running. */
	ranTests = 0;
	skippedTests = 0;
	
	/* Go through the test table and find the named tests if applicable. */
	for (test = &sjme_singleTests[0]; test->name != NULL; test++)
	{
		/* Determine if this is a test to run */
		execTest = allTests;
		if (!allTests)
			for (int i = 1; i < argc; i++)
				if (strcmp(argv[i], test->name) == 0)
				{
					execTest = 1;
					break;
				}
		
		/* Execute the test. */
		if (execTest)
		{
			/* Indicate a message that it is running. */
			fprintf(stderr, "Running test %s...\n", test->name);
			fflush(stderr);
			
			/* Setup shim. */
			shim = shimNew();
			if (shim == NULL)
			{
				fprintf(stderr, "Failed to create shim!\n");
				fflush(stderr);
				
				return EXIT_FAILURE;
			}
			
			/* Run it, if it fails, then signal. Zero is success. */
			ranTests++;
			result = test->function(shim);
			if (result != 0)
			{
				/* Count any skipped tests, used to later check all results. */
				if (result == SKIP_TEST())
					skippedTests++;
				
				/* Any failed test will mark as a failure. */
				else
					currentExitCode = EXIT_FAILURE;
			}
			
			/* Note exit code. */
			fprintf(stderr, "Test %s result: ", test->name);
			fprintf(stderr, "%s ", (result == 0 ? "PASS" :
				(result == SKIP_TEST() ? "SKIP" : "FAIL")));
			fprintf(stderr, "%d (%#x)\n", result, result);
			fflush(stderr);
			
			/* Note error code, if any. */
			if (sjme_hasError(&shim->error))
			{
				/* Reset error buffer. */
				memset(errorBuf, 0, sizeof(errorBuf));
				errorBufLen = ERROR_BUF_LEN;
				
				/* Describe it. */
				sjme_describeJvmError(&shim->error, errorBuf, &errorBufLen);
				
				/* Print it. */
				fprintf(stderr, "Test %s error: %s\n",
					test->name, errorBuf);
				fflush(stderr);
			}
			
			/* Destroy shim. */
			shimDestroy(shim);
		}
	}
	
	/* Specified a specific test but was not ran, so fail. */
	if (!allTests && ranTests <= 0)
	{
		fprintf(stderr, "Did not run any SquirrelJME Tests...\n");
		return EXIT_FAILURE;
	}
		
	/* If every single test was skipped, mark as such. */
	if (skippedTests == ranTests)
		return CTEST_EXIT_SKIPPED;
	
	/* And the code if all tests ran. */
	return currentExitCode;
}
