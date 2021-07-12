/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

/**
 * RetroArch Audio and MIDI Support.
 * 
 * @since 2021/02/27
 */

#include "lrlocal.h"

/** Set audio sample callback. */
void retro_set_audio_sample(retro_audio_sample_t cb)
{
	g_libRetroState->audioSampleFunc = cb;
}

/** Set audio sample batching. */
void retro_set_audio_sample_batch(retro_audio_sample_batch_t cb)
{
	g_libRetroState->audioSampleBatchFunc = cb;
}
