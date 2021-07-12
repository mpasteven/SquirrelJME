/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

#include <windows.h>
#include <stdlib.h>
#include <stdio.h>

int main(int argc, char** argv)
{
	int i;
	
	fprintf(stderr, "Args %d\r\n", argc);
	for (i = 0; i < argc; i++)
		fprintf(stderr, "Arg[%d]: %s\r\n", i, argv[i]);
	fflush(stderr);
	
	return EXIT_FAILURE;
}

