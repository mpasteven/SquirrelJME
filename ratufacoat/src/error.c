/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

#include "error.h"

void sjme_clearError(sjme_error* error)
{
	if (error != NULL)
	{
		error->code = SJME_ERROR_NONE;
		error->value = 0;
	}
}

sjme_returnFail sjme_hasError(sjme_error* error)
{
	if (error != NULL && error->code != SJME_ERROR_NONE)
		return SJME_RETURN_FAIL;
	return SJME_RETURN_SUCCESS;
}

void sjme_setError(sjme_error* error, sjme_errorCode code, sjme_jint value)
{
	if (error != NULL)
	{
		error->code = code;
		error->value = value;
	}
}
