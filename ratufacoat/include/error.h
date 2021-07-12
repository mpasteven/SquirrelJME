/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

/**
 * Error States and otherwise.
 * 
 * @since 2021/02/28
 */

#ifndef SQUIRRELJME_ERROR_H
#define SQUIRRELJME_ERROR_H

#include "sjmerc.h"

/* Anti-C++. */
#ifdef __cplusplus
#ifndef SJME_CXX_IS_EXTERNED
#define SJME_CXX_IS_EXTERNED
#define SJME_CXX_SQUIRRELJME_ERROR_H
extern "C"
{
#endif /* #ifdef SJME_CXX_IS_EXTERNED */
#endif /* #ifdef __cplusplus */

/*--------------------------------------------------------------------------*/

/**
 * The potential error codes that can happen.
 * 
 * @since 2021/02/28
 */
typedef enum sjme_errorCode
{
	/** No error. */
	SJME_ERROR_NONE,
	
	/** Unknown error. */
	SJME_ERROR_UNKNOWN,
	
	/** File does not exist. */
	SJME_ERROR_NOSUCHFILE,
	
	/** Invalid argument. */
	SJME_ERROR_INVALIDARG,
	
	/** End of file reached. */
	SJME_ERROR_ENDOFFILE,
	
	/** No memory available. */
	SJME_ERROR_NO_MEMORY,
	
	/** No native ROM file specified. */
	SJME_ERROR_NONATIVEROM,
	
	/** No support for files. */
	SJME_ERROR_NOFILES,
	
	/** Invalid ROM magic number. */
	SJME_ERROR_INVALIDROMMAGIC,
	
	/** Invalid JAR magic number. */
	SJME_ERROR_INVALIDJARMAGIC,
	
	/** Invalid end of BootRAM. */
	SJME_ERROR_INVALIDBOOTRAMEND,
	
	/** Invalid BootRAM seed. */
	SJME_ERROR_INVALIDBOOTRAMSEED,
	
	/** CPU hit breakpoint. */
	SJME_ERROR_CPUBREAKPOINT,
	
	/** Cannot write Java values. */
	SJME_ERROR_NOJAVAWRITE,
	
	/** Read error. */
	SJME_ERROR_READERROR,
	
	/** Early end of file reached. */
	SJME_ERROR_EARLYEOF,
	
	/** Virtual machine not ready. */
	SJME_ERROR_JVMNOTREADY,
	
	/** The virtual machine has exited, supervisor boot okay. */
	SJME_ERROR_JVMEXIT_SUV_OKAY,
	
	/** The virtual machine has exited, the supervisor did not flag! */
	SJME_ERROR_JVMEXIT_SUV_FAIL,
	
	/** Thread returned at the top-most frame and not through a system call. */
	SJME_ERROR_THREADRETURN,
	
	/** Bad memory access. */
	SJME_ERROR_BADADDRESS,
	
	/** Invalid CPU operation. */
	SJME_ERROR_INVALIDOP,
	
	/** Could not initialize the VMM. */
	SJME_ERROR_VMMNEWFAIL,
	
	/** Invalid size. */
	SJME_ERROR_INVALIDSIZE,
	
	/** Address resolution error. */
	SJME_ERROR_ADDRRESFAIL,
	
	/** Invalid memory type. */
	SJME_ERROR_INVALIDMEMTYPE,
	
	/** Register value overflowed. */
	SJME_ERROR_REGISTEROVERFLOW,
	
	/** Could not map address. */
	SJME_ERROR_VMMMAPFAIL,
	
	/** Null arguments. */
	SJME_ERROR_NULLARGS,
	
	/** Negative size. */
	SJME_ERROR_NEGATIVE_SIZE,
	
	/** Invalid memory handle kind. */
	SJME_ERROR_INVALID_MEMHANDLE_KIND,
	
	/** Out of bounds. */
	SJME_ERROR_OUT_OF_BOUNDS,
	
	/** Could not seed the RNG. */
	SJME_ERROR_COULD_NOT_SEED,
	
	/** Destruction failed. */
	SJME_ERROR_DESTROY_FAIL,
	
	/** Invalid memory handle. */
	SJME_ERROR_INVALID_HANDLE,
	
	/** Invalid data type. */
	SJME_ERROR_INVALID_DATATYPE,
	
	/** Invalid boot RAM. */
	SJME_ERROR_INVALID_BOOTRAM,
} sjme_errorCode; 

/** This represents an error. */
typedef struct sjme_error
{
	/** Error code. */
	sjme_errorCode code;
	
	/** The value of it. */
	sjme_jint value;
} sjme_error;

/**
 * Clears the error.
 * 
 * @param error The error to clear.
 * @since 2021/03/04
 */
void sjme_clearError(sjme_error* error);

/**
 * Checks if an error is present.
 * 
 * @param error The error to check. 
 * @return If there is an error present or not.
 * @since 2021/03/04
 */
sjme_returnFail sjme_hasError(sjme_error* error);

/**
 * Sets the error code.
 *
 * @param error The error to set.
 * @param code The error code.
 * @param value The error value.
 * @since 2019/06/25
 */
void sjme_setError(sjme_error* error, sjme_errorCode code, sjme_jint value);

/*--------------------------------------------------------------------------*/

/* Anti-C++. */
#ifdef __cplusplus
#ifdef SJME_CXX_SQUIRRELJME_ERROR_H
}
#undef SJME_CXX_SQUIRRELJME_ERROR_H
#undef SJME_CXX_IS_EXTERNED
#endif /* #ifdef SJME_CXX_SQUIRRELJME_ERROR_H */
#endif /* #ifdef __cplusplus */

#endif /* SQUIRRELJME_ERROR_H */
