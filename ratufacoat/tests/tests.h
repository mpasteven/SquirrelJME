/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// --------------------------------------------------------------------------*/

#ifndef SQUIRRELJME_TESTS_H
#define SQUIRRELJME_TESTS_H

#include <stdlib.h>

#include "sjmerc.h"
#include "native.h"

/**
 * Testing shim structure.
 * 
 * @since 2021/03/04
 */
typedef struct sjme_testShim
{
	/** Error state. */
	sjme_error error;
	
	/** Native function support. */
	sjme_nativefuncs* nativeFunctions;
} sjme_testShim;

/**
 * Macro for prototyping tests.
 *
 * @param name The name of the test to wrap.
 */
#define SJME_TEST_PROTOTYPE(name) sjme_jint name(sjme_testShim* shim)

/** Pass test. */
#define PASS_TEST() SJME_JINT_C(0)

/** Fail test. */
#define FAIL_TEST(n) ((SJME_JINT_C(n)) != 0 ? (SJME_JINT_C(n)) : -1)

/** Skip test. */
#define SKIP_TEST() SJME_JINT_C(-65535)

/* Available tests. */
SJME_TEST_PROTOTYPE(testAtomic);
SJME_TEST_PROTOTYPE(testJvmInit);
SJME_TEST_PROTOTYPE(testJvmInvalid);
SJME_TEST_PROTOTYPE(testMemHandleAccess);
SJME_TEST_PROTOTYPE(testMemHandleCycle);
SJME_TEST_PROTOTYPE(testMemHandleEndian);
SJME_TEST_PROTOTYPE(testMemHandleInit);
SJME_TEST_PROTOTYPE(testMemHandleInvalid);
SJME_TEST_PROTOTYPE(testMemHandleMany);
SJME_TEST_PROTOTYPE(testNothing);
SJME_TEST_PROTOTYPE(testOpCodes);
SJME_TEST_PROTOTYPE(testRandom);
SJME_TEST_PROTOTYPE(testSkipped);

#endif /* SQUIRRELJME_TESTS_H */
