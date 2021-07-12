/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

/**
 * Data types.
 * 
 * @since 2021/03/06
 */

#ifndef SQUIRRELJME_DATATYPE_H
#define SQUIRRELJME_DATATYPE_H

#include "sjmerc.h"

/* Anti-C++. */
#ifdef __cplusplus
#ifndef SJME_CXX_IS_EXTERNED
#define SJME_CXX_IS_EXTERNED
#define SJME_CXX_SQUIRRELJME_DATATYPE_H
extern "C"
{
#endif /* #ifdef SJME_CXX_IS_EXTERNED */
#endif /* #ifdef __cplusplus */

/*--------------------------------------------------------------------------*/

/**
 * The type of data to be written.
 * 
 * @since 2021/03/06
 */
typedef enum sjme_dataType
{
	/** Object. */
	SJME_DATATYPE_OBJECT = 0,
	
	/** Byte. */
	SJME_DATATYPE_BYTE = 1,
	
	/** Short. */
	SJME_DATATYPE_SHORT = 2,
	
	/** Character. */
	SJME_DATATYPE_CHARACTER = 3,
	
	/** Integer. */
	SJME_DATATYPE_INTEGER = 4,
	
	/** Float. */
	SJME_DATATYPE_FLOAT = 5,
	
	/** Long. */
	SJME_DATATYPE_LONG = 6,
	
	/** Double. */
	SJME_DATATYPE_DOUBLE = 7,
	
	/** The number of data types. */
	SJME_NUM_DATATYPES = 8,
} sjme_dataType;

/** Data type sizes. */
extern const sjme_jint sjme_dataTypeSize[SJME_NUM_DATATYPES];

/*--------------------------------------------------------------------------*/

/* Anti-C++. */
#ifdef __cplusplus
#ifdef SJME_CXX_SQUIRRELJME_DATATYPE_H
}
#undef SJME_CXX_SQUIRRELJME_DATATYPE_H
#undef SJME_CXX_IS_EXTERNED
#endif /* #ifdef SJME_CXX_SQUIRRELJME_DATATYPE_H */
#endif /* #ifdef __cplusplus */

#endif /* SQUIRRELJME_DATATYPE_H */
