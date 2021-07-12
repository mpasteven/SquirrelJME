/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

/**
 * Video and terminal support.
 * 
 * @since 2021/02/27
 */

#ifndef SQUIRRELJME_VIDEO_H
#define SQUIRRELJME_VIDEO_H

#include "sjmerc.h"

/* Anti-C++. */
#ifdef __cplusplus
#ifndef SJME_CXX_IS_EXTERNED
#define SJME_CXX_IS_EXTERNED
#define SJME_CXX_SQUIRRELJME_VIDEO_H
extern "C"
{
#endif /* #ifdef SJME_CXX_IS_EXTERNED */
#endif /* #ifdef __cplusplus */

/*--------------------------------------------------------------------------*/

/**
 * The pixel formats that are available for use.
 * 
 * @since 2021/03/08
 */
typedef enum sjme_pixelFormat
{
	/** 32-bit RGBA ({@code int}) [Java ME Standard]. */
	SJME_PIXEL_FORMAT_INT_RGBA8888 = 0,
	
	/** 32-bit RGB ({@code int}) [Java ME Standard]. */
	SJME_PIXEL_FORMAT_INT_RGB888 = 1,
	
	/** 16-bit RGBA4444. ({@code short}) [Java ME Standard]. */
	SJME_PIXEL_FORMAT_SHORT_RGBA4444 = 2,
	
	/** 16-bit RGB565. ({@code short}) [Java ME Standard]. */
	SJME_PIXEL_FORMAT_SHORT_RGB565 = 3,
	
	/** 16-bit RGB555. ({@code short}). */
	SJME_PIXEL_FORMAT_SHORT_RGB555 = 4,
	
	/** 16-bit ABGR1555. ({@code short}) [PlayStation 2]. */
	SJME_PIXEL_FORMAT_SHORT_ABGR1555 = 5,
	
	/** 65536 Colors ({@code short}). */
	SJME_PIXEL_FORMAT_SHORT_INDEXED65536 = 6,
	
	/** 256 Colors ({@code byte}). */
	SJME_PIXEL_FORMAT_BYTE_INDEXED256 = 7,
	
	/** Packed 16 colors (4-bit). ({@code packed byte}) */
	SJME_PIXEL_FORMAT_PACKED_INDEXED4 = 8,
	
	/** Packed 4 Colors (2-bit). ({@code packed byte}) */
	SJME_PIXEL_FORMAT_PACKED_INDEXED2 = 9,
	
	/** Packed 2 colors (1-bit). ({@code packed byte}) */
	SJME_PIXEL_FORMAT_PACKED_INDEXED1 = 10,
	
	/** The number of pixel formats. */
	SJME_PIXEL_FORMAT_NUM_PIXEL_FORMATS = 11,
} sjme_pixelFormat;

/*--------------------------------------------------------------------------*/

/* Anti-C++. */
#ifdef __cplusplus
#ifdef SJME_CXX_SQUIRRELJME_VIDEO_H
}
#undef SJME_CXX_SQUIRRELJME_VIDEO_H
#undef SJME_CXX_IS_EXTERNED
#endif /* #ifdef SJME_CXX_SQUIRRELJME_VIDEO_H */
#endif /* #ifdef __cplusplus */

#endif /* SQUIRRELJME_VIDEO_H */
