/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

/**
 * Software Math.
 * 
 * @since 2021/02/27
 */

#include "debug.h"
#include "sjmerc.h"
#include "softmath.h"

/** Unsigned shift right masks. */
static const sjme_jint sjme_ushrIntMask[32] =
	{
		SJME_JINT_C(0x00000000),
		SJME_JINT_C(0x00000001),
		SJME_JINT_C(0x00000003),
		SJME_JINT_C(0x00000007),
		SJME_JINT_C(0x0000000F),
		SJME_JINT_C(0x0000001F),
		SJME_JINT_C(0x0000003F),
		SJME_JINT_C(0x0000007F),
		SJME_JINT_C(0x000000FF),
		SJME_JINT_C(0x000001FF),
		SJME_JINT_C(0x000003FF),
		SJME_JINT_C(0x000007FF),
		SJME_JINT_C(0x00000FFF),
		SJME_JINT_C(0x00001FFF),
		SJME_JINT_C(0x00003FFF),
		SJME_JINT_C(0x00007FFF),
		SJME_JINT_C(0x0000FFFF),
		SJME_JINT_C(0x0001FFFF),
		SJME_JINT_C(0x0003FFFF),
		SJME_JINT_C(0x0007FFFF),
		SJME_JINT_C(0x000FFFFF),
		SJME_JINT_C(0x001FFFFF),
		SJME_JINT_C(0x003FFFFF),
		SJME_JINT_C(0x007FFFFF),
		SJME_JINT_C(0x00FFFFFF),
		SJME_JINT_C(0x01FFFFFF),
		SJME_JINT_C(0x03FFFFFF),
		SJME_JINT_C(0x07FFFFFF),
		SJME_JINT_C(0x0FFFFFFF),
		SJME_JINT_C(0x1FFFFFFF),
		SJME_JINT_C(0x3FFFFFFF),
	};

sjme_jlong sjme_addLongF(sjme_jlong a, sjme_jint bLo, sjme_jint bHi)
{
	sjme_jlong c;
	
	/* Add the higher/lower parts */
	c.hi = a.hi + bHi;
	c.lo = a.lo + bLo;
	
	/* If the low addition carried a bit over, then set that bit in the */
	/* high part */
	if ((c.lo + SJME_JINT_C(0x80000000)) <
		(a.lo + SJME_JINT_C(0x80000000)))
		c.hi++;
	
	/* Return result */
	return c;
}

sjme_jint_div sjme_divInt(sjme_jint anum, sjme_jint aden)
{
	/* From Wikipedia (http://en.wikipedia.org/wiki/Division_%28digital%29) */
	/* if D == 0 then throw DivisionByZeroException end*/
	/* Q := 0 # initialize quotient and remainder to Zero  */
	/* R := 0                                              */
	/* for i = n-1...0 do  # " where n is no of bits "     */
	/*   R := R << 1       # left-shift R by 1 bit         */
	/*   R(0) := N(i)      # set the least-significant bit */
	/*              # of R equal to bit i of the numerator */
	/*   if R >= D then                                    */
	/*     R = R - D                                       */
	/*     Q(i) := 1                                       */
	/*   end                                               */
	/* end                                                 */
	sjme_jint_div rv = {0, 0};
	struct
	{
		sjme_juint quot;
		sjme_juint rem;
	} interm = {0, 0};
	sjme_juint i;
	sjme_jbyte isneg;
	
	/* Disallow division by zero */
	if (aden == 0)
		return rv;
	
	/* Negative? */
	isneg = 0;
	if ((anum < 0 && aden >= 0) || (anum >= 0 && aden < 0))
		isneg |= 1;
	
	/* Force Positive */
	anum = (anum < 0 ? -anum : anum);
	aden = (aden < 0 ? -aden : aden);
	
	/* Perform Math */
	for (i = SJME_JUINT_C(31);; i--)
	{
		interm.rem <<= SJME_JUINT_C(1);
		interm.rem &= SJME_JUINT_C(0xFFFFFFFE);
		interm.rem |= (((sjme_juint)anum) >> i) & SJME_JUINT_C(1);
		
		if (interm.rem >= (sjme_juint)aden)
		{
			interm.rem -= (sjme_juint)aden;
			interm.quot |= (SJME_JUINT_C(1) << i);
		}
		
		if (i == 0)
			break;
	}
	
	/* Restore Integers */
	rv.quot = interm.quot;
	rv.rem = interm.rem;
	
	/* Make Negative */
	if (isneg & 1)
		rv.quot = -rv.quot;
	
	/* Return */
	return rv;
}

sjme_jlong sjme_mulLong(sjme_jlong a, sjme_jlong b)
{
	/* TODO: Replace this with 32-bit version? */
	sjme_jlong rv;
	int64_t aa = (((int64_t)a.hi) << 32) |
		(((int64_t)a.lo) & UINT64_C(0xFFFFFFFF));
	int64_t bb = (((int64_t)b.hi) << 32) |
		(((int64_t)b.lo) & UINT64_C(0xFFFFFFFF));
	int64_t cc = aa * bb;
	
	rv.hi = (sjme_jint)(cc >> 32);
	rv.lo = (sjme_jint)(cc);
	return rv;
}

sjme_jlong sjme_mulLongF(sjme_jlong a, sjme_jint bLo, sjme_jint bHi)
{
	sjme_jlong b;
	
	b.hi = bHi;
	b.lo = bLo;
	
	return sjme_mulLong(a, b);
}

sjme_jint sjme_ushrInt(sjme_jint val, sjme_jint sh)
{
	sh &= 31;
	return (val >> sh) & sjme_ushrIntMask[sh];
}
