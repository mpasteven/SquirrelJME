/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

/**
 * RetroArch GamePad and Controller Support.
 * 
 * @since 2021/02/27
 */

#include "lrlocal.h"

/** Sets controller port device. */
void retro_set_controller_port_device(unsigned port, unsigned device)
{
}

/**
 * Set input polling callback.
 * 
 * @param cb The callback for input polling.
 * @since 2021/02/27
 */
void retro_set_input_poll(retro_input_poll_t cb)
{	
	g_libRetroState->inputPollFunc = cb;
}

/**
 * Set input state callback.
 * 
 * @param cb The callback for input state.
 * @since 2021/02/27
 */
void retro_set_input_state(retro_input_state_t cb)
{
	g_libRetroState->inputStateFunc = cb;
}
