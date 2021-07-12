/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

#include "tests.h"
#include "builtin.h"
#include "jvm.h"

/**
 * Tests that the JVM initializes properly, requires built-in ROM.
 * 
 * @since 2021/03/04 
 */
SJME_TEST_PROTOTYPE(testJvmInit)
{
	sjme_jvmoptions options;
	sjme_jvm* jvm;
	
	/* If we have no built-in ROM, we cannot actually test this. */
	if (sjme_builtInRomSize == 0)
		return SKIP_TEST();
	
	/* Setup options. */
	memset(&options, 0, sizeof(options));
	options.romData = sjme_builtInRomData;
	options.romSize = sjme_builtInRomSize;
	
	/* Try to create the JVM. */
	jvm = NULL;
	sjme_clearError(&shim->error);
	if (sjme_jvmNew(&jvm, &options, shim->nativeFunctions, &shim->error))
		return FAIL_TEST(1);
	
	/* Was still null or has an error? */
	if (jvm == NULL || sjme_hasError(&shim->error))
		return FAIL_TEST(2);
	
	/* Try to destroy the JVM. */
	sjme_clearError(&shim->error);
	if (sjme_jvmDestroy(jvm, &shim->error))
		return FAIL_TEST(3);
	
	/** Has an error? */
	if (sjme_hasError(&shim->error))
		return FAIL_TEST(4);
	
	return PASS_TEST();
}
