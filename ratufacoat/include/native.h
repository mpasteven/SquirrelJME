/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

/**
 * Native interface.
 * 
 * @since 2021/02/28
 */

#ifndef SQUIRRELJME_NATIVE_H
#define SQUIRRELJME_NATIVE_H

#include "error.h"

/* Anti-C++. */
#ifdef __cplusplus
#ifndef SJME_CXX_IS_EXTERNED
#define SJME_CXX_IS_EXTERNED
#define SJME_CXX_SQUIRRELJME_NATIVE_H
extern "C"
{
#endif /* #ifdef SJME_CXX_IS_EXTERNED */
#endif /* #ifdef __cplusplus */

/*--------------------------------------------------------------------------*/

/**
 * Native functions available for the JVM to use.
 *
 * @since 2019/06/03
 */
typedef struct sjme_nativefuncs
{
	/** Current monotonic nano-seconds, returns low nanos. */
	sjme_jint (*nanotime)(sjme_jint* hi);
	
	/** Current system clock in Java time, returns low time. */
	sjme_jint (*millitime)(sjme_jint* hi);
	
	/** The filename to use for the native ROM. */
	sjme_nativefilename* (*nativeromfile)(void);
	
	/** Converts the Java char sequence to native filename. */
	sjme_nativefilename* (*nativefilename)(sjme_jint len, sjme_jchar* chars);
	
	/** Frees the specified filename. */
	void (*freefilename)(sjme_nativefilename* filename);
	
	/** Opens the specified file. */
	sjme_nativefile* (*fileopen)(sjme_nativefilename* filename,
		sjme_jint mode, sjme_error* error);
	
	/** Closes the specified file. */
	void (*fileclose)(sjme_nativefile* file, sjme_error* error);
	
	/** Returns the size of the file. */
	sjme_jint (*filesize)(sjme_nativefile* file, sjme_error* error);
	
	/** Reads part of a file. */
	sjme_jint (*fileread)(sjme_nativefile* file, void* dest, sjme_jint len,
		sjme_error* error);
	
	/** Writes single byte to standard output. */
	sjme_jint (*stdout_write)(sjme_jint b);
	
	/** Writes single byte to standard error. */
	sjme_jint (*stderr_write)(sjme_jint b);
	
	/** Obtains the framebuffer. */
	sjme_framebuffer* (*framebuffer)(void);
	
	/** Returns information on where to load optional JAR from. */
	sjme_jint (*optional_jar)(void** ptr, sjme_jint* size);
} sjme_nativefuncs;

/*--------------------------------------------------------------------------*/

/* Anti-C++. */
#ifdef __cplusplus
#ifdef SJME_CXX_SQUIRRELJME_NATIVE_H
}
#undef SJME_CXX_SQUIRRELJME_NATIVE_H
#undef SJME_CXX_IS_EXTERNED
#endif /* #ifdef SJME_CXX_SQUIRRELJME_NATIVE_H */
#endif /* #ifdef __cplusplus */

#endif /* SQUIRRELJME_NATIVE_H */
