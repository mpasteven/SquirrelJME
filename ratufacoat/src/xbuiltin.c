/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

#include "builtin.h"

/*
 * If the ROM is not built-in, then initialize these to nothing.
 * 
 * This is used for the tests so that they do not need to know about the
 * macros or otherwise from the library.
 */
#if !defined(SQUIRRELJME_HAS_BUILTIN)
const sjme_jbyte sjme_builtInRomId[] = {};
const sjme_jint sjme_builtInRomIdLen = 0;
const sjme_jint sjme_builtInRomDate[] = {0, 0};
const sjme_ubyte sjme_builtInRomData[] = {};
const sjme_jint sjme_builtInRomSize = 0;
#endif
