/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

#include <stdio.h>
#include <stdarg.h>

#include "debug.h"

/** Debug buffer size for messages. */
#define DEBUG_BUF 512

sjme_returnNever sjme_todo(const char* message, ...)
{
	char buf[DEBUG_BUF];
	va_list args;
	
	/* Load message buffer. */
	va_start(args, message);
	vsnprintf(buf, DEBUG_BUF, message, args);
	va_end(args);
	
	/* Print output message. */
	fprintf(stderr, "TD: TODO Hit: %s\n", buf);
	
	/* Exit and stop. */
	exit(EXIT_FAILURE);
	
	/* These are totally not use. */
#pragma clang diagnostic push
#pragma ide diagnostic ignored "UnreachableCode"
#pragma ide diagnostic ignored "UnusedLocalVariable"
	{
		sjme_returnNever fail = {};
		return fail;
	}
#pragma clang diagnostic pop
}
