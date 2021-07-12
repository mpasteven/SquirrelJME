/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// --------------------------------------------------------------------------*/

/**
 * SquirrelJME RatufaCoat Header.
 *
 * @since 2019/06/02
 */

/** Header guard. */
#ifndef SJME_hGRATUFACOATSJMERCHSJMERCH
#define SJME_hGRATUFACOATSJMERCHSJMERCH

/** Standard Includes. */
#include <stddef.h>
#include <limits.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>

/* Versioning identifier. */
#if !defined(SQUIRRELJME_VERSION)
	/** Version string. */
	#define SQUIRRELJME_VERSION "0.3.0"
#endif

/* Version ID. */
#if !defined(SQUIRRELJME_VERSION_ID)
	/** Version ID. */
	#define SQUIRRELJME_VERSION_ID "unknown:"__DATE__""__TIME__
#endif

/** Is this a 64-bit system? */
#if !defined(SJME_BITS)
	#if defined(_LP64) || defined(__LP64__) || defined(__x86_64__) || \
		defined(_M_X64) || defined(_M_AMD64) || defined(__aarch64__) || \
		defined(__ia64__) || defined(__ia64) || defined(_M_IA64) || \
		defined(__itanium__) || defined(__powerpc64__) || \
		defined(__ppc64__) || defined(_ARCH_PPC64) || defined(_ARCH_PPC64)
		#define SJME_BITS 64
	#else
		#define SJME_BITS 32
	#endif
#endif

/** Possibly detect endianess. */
#if !defined(SJME_BIG_ENDIAN) && !defined(SJME_LITTLE_ENDIAN)
	/** Defined by the system? */
	#if !defined(SJME_BIG_ENDIAN)
		#if defined(MSB_FIRST) || \
			(defined(WORDS_BIGENDIAN) && WORDS_BIGENDIAN != 0)
			#define SJME_BIG_ENDIAN
		#endif
	#endif
	
	/** Just set little endian if no endianess was defined */
	#if !defined(SJME_BIG_ENDIAN) && !defined(SJME_LITTLE_ENDIAN)
		#define SJME_LITTLE_ENDIAN
	#endif
	
	/** If both are defined, just set big endian. */
	#if defined(SJME_BIG_ENDIAN) && defined(SJME_LITTLE_ENDIAN)
		#undef SJME_LITTLE_ENDIAN
	#endif
#endif

/** Linux. */
#if defined(__linux__) || defined(__gnu_linux__)
	#define SJME_IS_LINUX 1
#endif

/** DOS? */
#if defined(MSDOS) || defined(_MSDOS) || defined(__MSDOS__) || defined(__DOS__)
	#define SJME_IS_DOS 1
	
	#include <malloc.h>
#endif

/** C99 includes. */
#if (defined(__STDC_VERSION__) && __STDC_VERSION__ >= 199901L) || \
	(defined(__WATCOMC__) && __WATCOMC__ >= 1270) || \
	(defined(_MSC_VER) && _MSC_VER >= 1600) || \
	(defined(__GNUC__) && __GNUC__ >= 4) || \
	(defined(PSP) || defined(PS2))
	#include <stdint.h>

/** Old Microsoft. */
#elif defined(_MSC_VER) && _MSC_VER < 1600
	typedef signed __int8 int8_t;
	typedef signed __int16 int16_t;
	typedef signed __int32 int32_t;
	
	typedef unsigned __int8 uint8_t;
	typedef unsigned __int16 uint16_t;
	typedef unsigned __int32 uint32_t;
	
	#define INT8_C(x) x
	#define INT16_C(x) x
	#define INT32_C(x) x
	
	#define UINT8_C(x) x##U
	#define UINT16_C(x) x##U
	#define UINT32_C(x) x##U

/** Palm OS. */
#elif defined(__palmos__)
	#include <PalmTypes.h>
	
	typedef Int8 int8_t;
	typedef Int16 int16_t;
	typedef Int32 int32_t;
	
	typedef UInt8 uint8_t;
	typedef UInt16 uint16_t;
	typedef UInt32 uint32_t;
	
	typedef int32_t intptr_t;
	typedef uint32_t uintptr_t;
	
	#define INT8_C(x) x
	#define INT16_C(x) x
	#define INT32_C(x) x##L
	
	#define UINT8_C(x) x##U
	#define UINT16_C(x) x##U
	#define UINT32_C(x) x##UL
	
	#define SIZE_MAX UINT32_C(0xFFFFFFFF)

/** Guessed otherwise. */
#else
	#if defined(SCHAR_MAX) && SCHAR_MAX == 127
		typedef signed char int8_t;
		
		#define INT8_C(x) x
	#endif
	
	#if defined(INT_MAX) && INT_MAX == 32767
		typedef signed int int16_t;
		typedef unsigned int uint16_t;
		
		#define INT16_C(x) x
		#define UINT16_C(x) x##U
	#elif defined(SHORT_MAX) && SHORT_MAX == 32767
		typedef signed short int16_t;
		typedef unsigned short uint16_t;
		
		#define INT16_C(x) x
		#define UINT16_C(x) x##U
	#endif
	
	#if defined(INT_MAX) && INT_MAX > 32767
		typedef signed int int32_t;
		typedef unsigned int uint32_t;
		
		#define INT32_C(x) x
	#elif defined(LONG_MAX) && LONG_MAX > 32767
		typedef signed long int32_t;
		typedef unsigned long uint32_t;
		
		#define INT32_C(x) x##L
		#define UINT32_C(x) x##UL
	#endif
#endif

/* Anti-C++. */
#ifdef _cplusplus
#ifndef SJME_CXX_IS_EXTERNED
#define SJME_CXX_IS_EXTERNED
#define SJME_cXRATUFACOATSJMERCHSJMERCH
extern "C"
{
#endif /* #ifdef SJME_CXX_IS_EXTERNED */
#endif /* #ifdef __cplusplus */

/*--------------------------------------------------------------------------*/

/** {@code byte} type. */
typedef int8_t sjme_jbyte;

/** Unsigned {@code byte} type. */
typedef uint8_t sjme_ubyte;

/** {@code short} type. */
typedef int16_t sjme_jshort;

/** {@code char} type. */
typedef uint16_t sjme_jchar;

/** {@code int} type. */
typedef int32_t sjme_jint;

/** Unsigned {@code int} type. */
typedef uint32_t sjme_juint;

/** Boolean type. */
typedef enum sjme_jboolean
{
	/** False. */
	sjme_false = 0,
	
	/** True. */
	sjme_true = 1,
} sjme_jboolean;

/** Constant value macros. */
#define SJME_JBYTE_C(x) INT8_C(x)
#define SJME_JSHORT_C(x) INT16_C(x)
#define SJME_JCHAR_C(x) UINT16_C(x)
#define SJME_JINT_C(x) INT32_C(x)
#define SJME_JUINT_C(x) UINT32_C(x)

/** Maximum values. */
#define SJME_JINT_MAX_VALUE INT32_C(0x7FFFFFFF)

/** Unsigned short mask. */
#define SJME_JINT_USHORT_MASK INT32_C(0xFFFF)

/** Pointer conversion. */
#define SJME_POINTER_TO_JINT(x) ((sjme_jint)((uintptr_t)(x)))
#define SJME_JINT_TO_POINTER(x) ((void*)((uintptr_t)((sjme_jint)(x))))

#define SJME_POINTER_TO_JMEM(x) (((uintptr_t)(x)))
#define SJME_JMEM_TO_POINTER(x) ((void*)((uintptr_t)((x))))

/** Pointer math. */
#define SJME_POINTER_OFFSET(p, o) SJME_JINT_TO_POINTER( \
	SJME_POINTER_TO_JINT(p) + ((sjme_jint)(o)))

/** Pointer math, no precision loss. */
#define SJME_POINTER_OFFSET_LONG(p, o) SJME_JMEM_TO_POINTER( \
	SJME_POINTER_TO_JMEM(p) + ((intptr_t)(o)))

/** Standard C format for arguments. */
#define SJME_JVMARG_FORMAT_STDC SJME_JINT_C(1)

/** Open file for reading. */
#define SJME_OPENMODE_READ SJME_JINT_C(1)

/** Open file for read and writing. */
#define SJME_OPENMODE_READWRITE SJME_JINT_C(2)

/** Open file for read and writing, create new file or truncate existing. */
#define SJME_OPENMODE_READWRITETRUNCATE SJME_JINT_C(3)

/** VMM Type: Byte. */
#define SJME_VMMTYPE_BYTE SJME_JINT_C(-1)

/** VMM Type: Short. */
#define SJME_VMMTYPE_SHORT SJME_JINT_C(-2)

/** VMM Type: Integer. */
#define SJME_VMMTYPE_INTEGER SJME_JINT_C(-3)

/** VMM Type: Java Short. */
#define SJME_VMMTYPE_JAVASHORT SJME_JINT_C(-4)

/** VMM Type: Java Integer. */
#define SJME_VMMTYPE_JAVAINTEGER SJME_JINT_C(-5)

/**
 * Marker that indicates that a method returns failure.
 *
 * Will be one of @code SJME_RETURN_SUCCESS @endcode or
 * @code SJME_RETURN_FAIL @endcode.
 * 
 * @since 2021/02/27
 */
typedef enum sjme_returnFail
{
	/** Method success. @typedef sjme_returnFail. */
	SJME_RETURN_SUCCESS = 0,
	
	/** Method failure. @typedef sjme_returnFail. */
	SJME_RETURN_FAIL = 1,
} sjme_returnFail;

/**
 * Marker indicating that the method never returns.
 * 
 * @since 2021/02/28
 */
typedef struct sjme_returnNever
{
} sjme_returnNever;

/**
 * Virtual memory information.
 *
 * @since 2019/06/25
 */
typedef struct sjme_vmem sjme_vmem;

/**
 * Virtual memory pointer.
 *
 * @since 2019/06/25
 */
typedef sjme_jint sjme_vmemptr;

/**
 * Virtual memory mapping.
 *
 * @since 2019/06/25
 */
typedef struct sjme_vmemmap
{
	/** The real memory pointer. */
	uintptr_t realptr;
	
	/** The fake memory pointer. */
	sjme_jint fakeptr;
	
	/** The memory region size. */
	sjme_jint size;
	
	/** Banked access function. */
	uintptr_t (*bank)(sjme_jint* offset);
} sjme_vmemmap;

/**
 * Represents the framebuffer for SquirrelJME.
 *
 * @since 2019/06/20
 */
typedef struct sjme_framebuffer
{
	/** Video pixels. */
	sjme_jint* pixels;
	
	/** Screen width. */
	sjme_jint width;
	
	/** Screen height. */
	sjme_jint height;
	
	/** Scanline length. */
	sjme_jint scanlen;
	
	/** Scanline length in bytes. */
	sjme_jint scanlenbytes;
	
	/** The number of bits per pixel. */
	sjme_jint bitsperpixel;
	
	/** The number of available pixels. */
	sjme_jint numpixels;
	
	/** The pixel format used. */
	sjme_jint format;
	
	/** Flush the framebuffer. */
	void (*flush)(void);
	
	/** The frame buffer virtual memory. */
	sjme_vmemmap* framebuffer;
	
	/** Console X position. */
	sjme_jint conx;
	
	/** Console Y position. */
	sjme_jint cony;
	
	/** Console width. */
	sjme_jint conw;
	
	/** Console height. */
	sjme_jint conh;
} sjme_framebuffer;

/**
 * This represents the name of a file in native form, system dependent.
 *
 * @since 2019/06/08
 */
typedef struct sjme_nativefilename sjme_nativefilename;

/**
 * Represents an open file.
 *
 * @since 2019/06/08
 */
typedef struct sjme_nativefile sjme_nativefile;

/*--------------------------------------------------------------------------*/

/* Anti-C++. */
#ifdef __cplusplus
#ifdef SJME_cXRATUFACOATSJMERCHSJMERCH
}
#undef SJME_cXRATUFACOATSJMERCHSJMERCH
#undef SJME_CXX_IS_EXTERNED
#endif /** #ifdef SJME_cXRATUFACOATSJMERCHSJMERCH */
#endif /* #ifdef __cplusplus */

/** Header guard. */
#endif /* #ifndef SJME_hGRATUFACOATSJMERCHSJMERCH */

