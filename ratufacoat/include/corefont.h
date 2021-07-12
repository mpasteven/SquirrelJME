/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// --------------------------------------------------------------------------*/

/**
 * Built-In SQF Font.
 *
 * @since 2019/10/06
 */

/** Header guard. */
#ifndef SJME_hGRATUFACOATSJMFHSJMEBSQFH
#define SJME_hGRATUFACOATSJMFHSJMEBSQFH

#include "sjmerc.h"

/* Anti-C++. */
#ifdef _cplusplus
#ifndef SJME_CXX_IS_EXTERNED
#define SJME_CXX_IS_EXTERNED
#define SJME_cXRATUFACOATSJMFHSJMEBSQFH
extern "C"
{
#endif /* #ifdef SJME_CXX_IS_EXTERNED */
#endif /* #ifdef __cplusplus */

/*--------------------------------------------------------------------------*/

/**
 * SQF Font information.
 *
 * @since 2019/06/20
 */
typedef struct sjme_sqf
{
	/** The pixel height of the font. */
	sjme_jint pixelheight;
	
	/** The ascent of the font. */
	sjme_jint ascent;
	
	/** The descent of the font. */
	sjme_jint descent;
	
	/** The bytes per scanline. */
	sjme_jint bytesperscan;
	
	/** Widths for each character. */
	const sjme_jbyte* charwidths;
	
	/** Which characters are valid? */
	const sjme_jbyte* isvalidchar;
	
	/** Which characters make up the bitmap? */
	const sjme_jbyte* charbmp;
} sjme_sqf;

/** Upper shift value mask, since shifting off the type is undefined. */
extern const sjme_jint sjme_sh_umask[33];

/** Lower shift value mask, since shifting off the type is undefined. */
extern const sjme_jint sjme_sh_lmask[33];

/** Bit mask for font drawing. */
extern const sjme_jint sjme_drawcharbitmask[];

/** SQF Character Widths. */
extern const sjme_jbyte sjme_fontcharwidths[];

/** SQF Character validity. */
extern const sjme_jbyte sjme_fontisvalidchar[];

/** SQF Character Bitmaps. */
extern const sjme_jbyte sjme_fontcharbmp[];

/** ROM load failure message. */
extern const sjme_jbyte sjme_romfailmessage[];

/** Size of the ROM fail message. */
extern const sjme_jint sjme_romfailmessageSizeOf;

/** BootRAM failed to load. */
extern const sjme_jbyte sjme_bootfailmessage[];

/** Size of BootRAM failed message. */
extern const sjme_jint sjme_bootfailmessageSizeOf;

/** SQF Defined Font. */
extern const sjme_sqf sjme_font;

/*--------------------------------------------------------------------------*/

/* Anti-C++. */
#ifdef __cplusplus
#ifdef SJME_cXRATUFACOATSJMFHSJMEBSQFH
}
#undef SJME_cXRATUFACOATSJMFHSJMEBSQFH
#undef SJME_CXX_IS_EXTERNED
#endif /** #ifdef SJME_cXRATUFACOATSJMFHSJMEBSQFH */
#endif /* #ifdef __cplusplus */

/** Header guard. */
#endif /* #ifndef SJME_hGRATUFACOATSJMFHSJMEBSQFH */

