/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

#include "tests.h"

/**
 * Tests that skipping works, this should always result in a skip.
 * 
 * @since 2021/03/04
 */
SJME_TEST_PROTOTYPE(testSkipped)
{
	return SKIP_TEST();
}
