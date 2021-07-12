/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

#include "datatype.h"

const sjme_jint sjme_dataTypeSize[SJME_NUM_DATATYPES] =
	{
		4, /* SJME_DATATYPE_OBJECT */
		1, /* SJME_DATATYPE_BYTE */
		2, /* SJME_DATATYPE_SHORT */
		2, /* SJME_DATATYPE_CHARACTER */
		4, /* SJME_DATATYPE_INTEGER */
		4, /* SJME_DATATYPE_FLOAT */
		8, /* SJME_DATATYPE_LONG */
		8, /* SJME_DATATYPE_DOUBLE */
	};