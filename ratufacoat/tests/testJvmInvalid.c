/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

#include "tests.h"
#include "jvm.h"
#include "builtin.h"

/**
 * Tests if the JVM tries to be initialized with invalid data.
 * 
 * @since 2021/03/06 
 */
SJME_TEST_PROTOTYPE(testJvmInvalid)
{
	sjme_jvm* jvm;
	sjme_jvmoptions options;
	
	/* Setup options, use invalid ROM data here but specify it. */
	memset(&options, 0, sizeof(options));
	options.romData = malloc(1);
	options.romSize = 1;
	
	/* Missing options. */
	jvm = NULL;
	sjme_clearError(&shim->error);
	if (!sjme_jvmNew(&jvm, NULL, shim->nativeFunctions, &shim->error))
		return FAIL_TEST(1);
	
	/* Error not set? */
	if (!sjme_hasError(&shim->error))
		return FAIL_TEST(2);
	
	/* Missing JVM output. */
	jvm = NULL;
	sjme_clearError(&shim->error);
	if (!sjme_jvmNew(NULL, &options, shim->nativeFunctions,
		&shim->error))
		return FAIL_TEST(3);
	
	/* Error not set? */
	if (!sjme_hasError(&shim->error))
		return FAIL_TEST(4);
	
	/* Missing Native functions. */
	jvm = NULL;
	sjme_clearError(&shim->error);
	if (!sjme_jvmNew(&jvm, &options, NULL, &shim->error))
		return FAIL_TEST(5);
	
	/* Error not set? */
	if (!sjme_hasError(&shim->error))
		return FAIL_TEST(6);
	
	/* If we have no built-in ROM, we cannot actually test this par. */
	if (sjme_builtInRomSize == 0)
		return PASS_TEST();
	
	/* Missing ROM Size. */
	jvm = NULL;
	options.romData = sjme_builtInRomData;
	options.romSize = 0;
	sjme_clearError(&shim->error);
	if (!sjme_jvmNew(&jvm, &options, shim->nativeFunctions, &shim->error))
		return FAIL_TEST(7);
	
	/* Error not set? */
	if (!sjme_hasError(&shim->error))
		return FAIL_TEST(8);
	
	/* Missing ROM data. */
	jvm = NULL;
	options.romData = NULL;
	options.romSize = sjme_builtInRomSize;
	sjme_clearError(&shim->error);
	if (!sjme_jvmNew(&jvm, &options, shim->nativeFunctions, &shim->error))
		return FAIL_TEST(9);
	
	/* Error not set? */
	if (!sjme_hasError(&shim->error))
		return FAIL_TEST(10);
	
	/* Missing both? */
	jvm = NULL;
	options.romData = NULL;
	options.romSize = 0;
	sjme_clearError(&shim->error);
	if (!sjme_jvmNew(&jvm, &options, shim->nativeFunctions, &shim->error))
		return FAIL_TEST(11);
	
	/* Error not set? */
	if (!sjme_hasError(&shim->error))
		return FAIL_TEST(12);
	
	return PASS_TEST();
}
