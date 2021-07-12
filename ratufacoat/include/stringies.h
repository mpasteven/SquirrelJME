/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// --------------------------------------------------------------------------*/

#ifndef SQUIRRELJME_STRINGIES_H
#define SQUIRRELJME_STRINGIES_H

#include "error.h"

/**
 * Returns a description of the given JVM error.
 *
 * @param error The error to describe.
 * @param destMessage The destination buffer to contain the bytes
 * @param destLen The length of the described error, this is taken as the
 * buffer input initially.
 * @since 2020/08/09
 */
void sjme_describeJvmError(sjme_error* error,
	sjme_jbyte* destMessage, sjme_jint* destLen);

#endif //SQUIRRELJME_STRINGIES_H
