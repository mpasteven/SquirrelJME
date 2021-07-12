/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// --------------------------------------------------------------------------*/

/**
 * BootRAM Support.
 * 
 * @since 2021/02/26
 */

#ifndef SQUIRRELJME_BOOTROM_H
#define SQUIRRELJME_BOOTROM_H

#include "sjmerc.h"
#include "error.h"
#include "jvm.h"

/* Anti-C++. */
#ifdef __cplusplus
#ifndef SJME_CXX_IS_EXTERNED
#define SJME_CXX_IS_EXTERNED
#define SJME_cXRATUFACOATSJMONHSJMECONH
extern "C"
{
#endif /* #ifdef SJME_CXX_IS_EXTERNED */
#endif /* #ifdef __cplusplus */

/*--------------------------------------------------------------------------*/

/** Version used for classes and otherwise. */
#define SJME_CLASSINFO_CLASS_VERSION_20201129 1

/** The magic number for the Pack ROM (squirreljme.sqc). */
#define SJME_CLASSINFO_PACK_MAGIC_NUMBER 1

/**
 * Loads the BootROM into the given JVM.
 * 
 * @param jvm The virtual machine to load for.
 * @param error The error state, if an error happens.
 * @since 2021/02/27
 */
sjme_returnFail sjme_loadBootRom(sjme_jvm* jvm, sjme_error* error);

/*--------------------------------------------------------------------------*/

/* Anti-C++. */
#ifdef __cplusplus
#ifdef SJME_cXRATUFACOATSJMONHSJMECONH
}
#undef SJME_cXRATUFACOATSJMONHSJMECONH
#undef SJME_CXX_IS_EXTERNED
#endif /** #ifdef SJME_cXRATUFACOATSJMONHSJMECONH */
#endif /* #ifdef __cplusplus */

#endif /* SQUIRRELJME_BOOTROM_H */
