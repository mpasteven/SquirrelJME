/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

/**
 * Save state and freezing support.
 * 
 * @since 2021/02/27
 */

#include "lrlocal.h"

/** Serialize? */
bool retro_serialize(void* data, size_t size)
{
	return false;
}

/** Serialize size? */
size_t retro_serialize_size(void)
{
	return 0;
}

/** Unserialize? */
bool retro_unserialize(const void* data, size_t size)
{
	return false;
}
