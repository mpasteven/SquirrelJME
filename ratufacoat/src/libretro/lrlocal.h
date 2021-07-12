/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

/**
 * Local RetroArch Support.
 * 
 * @since 2021/02/27
 */

#ifndef SQUIRRELJME_LRLOCAL_H
#define SQUIRRELJME_LRLOCAL_H

#include <libretro.h>

/* Anti-C++. */
#ifdef __cplusplus
#ifndef SJME_CXX_IS_EXTERNED
#define SJME_CXX_IS_EXTERNED
#define SJME_CXX_SQUIRRELJME_LRLOCAL_H
extern "C"
{
#endif /* #ifdef SJME_CXX_IS_EXTERNED */
#endif /* #ifdef __cplusplus */

/*--------------------------------------------------------------------------*/

/**
 * The held state for RetroArch.
 * 
 * @since 2021/02/27
 */
typedef struct sjme_libRetroState
{
	/** Input polling callback. */
	retro_input_poll_t inputPollFunc;
	
	/** Input state callback. */
	retro_input_state_t inputStateFunc;
	
	/** Audio callback. */
	retro_audio_sample_batch_t audioSampleBatchFunc;
	
	/** Audio sample callback. */
	retro_audio_sample_t audioSampleFunc;
} sjme_libRetroState;

/** The global RetroArch State. */
extern sjme_libRetroState* g_libRetroState;

/*--------------------------------------------------------------------------*/

/* Anti-C++. */
#ifdef __cplusplus
#ifdef SJME_CXX_SQUIRRELJME_LRLOCAL_H
}
#undef SJME_CXX_SQUIRRELJME_LRLOCAL_H
#undef SJME_CXX_IS_EXTERNED
#endif /* #ifdef SJME_CXX_SQUIRRELJME_LRLOCAL_H */
#endif /* #ifdef __cplusplus */

#endif /* SQUIRRELJME_LRLOCAL_H */
