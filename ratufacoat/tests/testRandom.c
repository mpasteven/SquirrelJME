/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

#include "tests.h"
#include "random.h"

/** Random number of cycles. */
#define RANDOM_CYCLES 32

/** Matching booleans. */
static const sjme_jboolean matchBool[RANDOM_CYCLES] =
	{
		sjme_true, sjme_true, sjme_true, sjme_false, sjme_false, sjme_true,
		sjme_true, sjme_true, sjme_true, sjme_false, sjme_false, sjme_true,
		sjme_false, sjme_true, sjme_true, sjme_false, sjme_true, sjme_false,
		sjme_false, sjme_false, sjme_true, sjme_false, sjme_false, sjme_true,
		sjme_true, sjme_true, sjme_false, sjme_true, sjme_false, sjme_false,
		sjme_true, sjme_true
	};

/** Values to match against. */
static const sjme_jint matchInt[RANDOM_CYCLES] =
	{
		SJME_JINT_C(-128266353), SJME_JINT_C(1948586741),
		SJME_JINT_C(-737493750), SJME_JINT_C(-1691956655),
		SJME_JINT_C(981813466), SJME_JINT_C(-162976284),
		SJME_JINT_C(914154380), SJME_JINT_C(453943797),
		SJME_JINT_C(-1545361761), SJME_JINT_C(1742258951),
		SJME_JINT_C(1884219158), SJME_JINT_C(1240009219),
		SJME_JINT_C(-150729253), SJME_JINT_C(-103157653),
		SJME_JINT_C(1955413213), SJME_JINT_C(1734636865),
		SJME_JINT_C(1214260749), SJME_JINT_C(1428227275),
		SJME_JINT_C(1515069861), SJME_JINT_C(622848097),
		SJME_JINT_C(425202528), SJME_JINT_C(1696159965),
		SJME_JINT_C(1491573386), SJME_JINT_C(990471538),
		SJME_JINT_C(-2068721), SJME_JINT_C(-1104025112),
		SJME_JINT_C(-813810180), SJME_JINT_C(-1351411048),
		SJME_JINT_C(-2056461820), SJME_JINT_C(367036826),
		SJME_JINT_C(717818122), SJME_JINT_C(801130495)
	};

/**
 * Tests that random works properly.
 * 
 * @since 2021/03/07
 */
SJME_TEST_PROTOTYPE(testRandom)
{
	sjme_randomState random;
	sjme_jlong seed;
	sjme_jboolean jboolean;
	sjme_jint jint;
	
	/* Seed the RNG. */
	seed.hi = SJME_JINT_C(0xFFFFFFFF);
	seed.lo = SJME_JINT_C(0xCAFEBABE);
	if (sjme_randomSeed(&random, seed, &shim->error))
		return FAIL_TEST(1);
	
	/* Check booleans. */
	for (int i = 0; i < RANDOM_CYCLES; i++)
	{
		if (sjme_randomNextBoolean(&random, &jboolean, &shim->error))
			return FAIL_TEST(100 + i);
		
		if (matchBool[i] != jboolean)
		{
			fprintf(stderr, "%i: want=%d != got=%d\n",
				i, matchBool[i], jboolean);
			return FAIL_TEST(200 + i);
		}
	}
	
	/* Check integers. */
	for (int i = 0; i < RANDOM_CYCLES; i++)
	{
		if (sjme_randomNextInt(&random, &jint, &shim->error))
			return FAIL_TEST(300 + i);
		
		if (matchInt[i] != jint)
		{
			fprintf(stderr, "%i: want=%d != got=%d\n",
				i, matchInt[i], jint);
			return FAIL_TEST(400 + i);
		}
	}
	
	return PASS_TEST();
}
