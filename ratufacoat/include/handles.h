/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

/**
 * Memory handle support, used to have potentially loosely bound regions
 * of memory.
 * 
 * @since 2021/02/27
 */

#ifndef SQUIRRELJME_HANDLES_H
#define SQUIRRELJME_HANDLES_H

#include "sjmerc.h"
#include "error.h"
#include "datatype.h"
#include "softmath.h"

/* Anti-C++. */
#ifdef __cplusplus
#ifndef SJME_CXX_IS_EXTERNED
#define SJME_CXX_IS_EXTERNED
#define SJME_CXX_SQUIRRELJME_HANDLES_H
extern "C"
{
#endif /* #ifdef SJME_CXX_IS_EXTERNED */
#endif /* #ifdef __cplusplus */

/*--------------------------------------------------------------------------*/

/**
 * Memory handle statistics.
 * 
 * @since 2021/03/08
 */
typedef struct sjme_memHandleStats
{
	/** Successes after a re-roll. */
	sjme_jint reRollHit;
	
	/** Fails after a re-roll. */
	sjme_jint reRollMiss;
	
	/** Number of times the re-roll loop ran. */
	sjme_jint reRollCount;
	
	/** Number of times handles grew. */
	sjme_jint growCount;
	
	/** Number of allocated. */
	sjme_jint numNew;
	
	/** Number of deallocated. */
	sjme_jint numDelete;
	
	/** Number of times the down mask made a collision. */
	sjme_jint downMaskMiss;
} sjme_memHandleStats;

/** Storage for all memory handles. */
typedef struct sjme_memHandles sjme_memHandles;

/** A single memory handle. */
typedef struct sjme_memHandle sjme_memHandle;

/**
 * The kind of memory handle the object is.
 * 
 * @since 2021/03/06
 */
typedef enum sjme_memHandleKind
{
	/** Undefined. */
	SJME_MEMHANDLE_KIND_UNDEFINED,
	
	/** Static field storage. */
	SJME_MEMHANDLE_KIND_STATIC_FIELD_DATA,
	
	/** Class information. */
	SJME_MEMHANDLE_KIND_CLASS_INFO,
	
	/** A list of classes. */
	SJME_MEMHANDLE_KIND_CLASS_INFO_LIST,
	
	/** The constant pool. */
	SJME_MEMHANDLE_KIND_POOL,
	
	/** Instance of an object. */
	SJME_MEMHANDLE_KIND_OBJECT_INSTANCE,
	
	/** Virtual method VTable. */
	SJME_MEMHANDLE_KIND_VIRTUAL_VTABLE,
	
	/** Interface method VTable. */
	SJME_MEMHANDLE_KIND_INTERFACE_VTABLE,
	
	/** Boolean array (really byte). */
	SJME_MEMHANDLE_KIND_BOOLEAN_ARRAY,
	
	/** Boolean/byte array. */
	SJME_MEMHANDLE_KIND_BYTE_ARRAY,
	
	/** Short array. */
	SJME_MEMHANDLE_KIND_SHORT_ARRAY,
	
	/** Character array. */
	SJME_MEMHANDLE_KIND_CHARACTER_ARRAY,
	
	/** Integer array. */
	SJME_MEMHANDLE_KIND_INTEGER_ARRAY,
	
	/** Long array. */
	SJME_MEMHANDLE_KIND_LONG_ARRAY,
	
	/** Float array. */
	SJME_MEMHANDLE_KIND_FLOAT_ARRAY,
	
	/** Double array. */
	SJME_MEMHANDLE_KIND_DOUBLE_ARRAY,
	
	/** Object (memory handle) array. */
	SJME_MEMHANDLE_KIND_OBJECT_ARRAY,
	
	/** Static virtual Machine Attributes. */
	SJME_MEMHANDLE_KIND_STATIC_VM_ATTRIBUTES,
	
	/** Quick cast information. */
	SJME_MEMHANDLE_KIND_QUICK_CAST_CHECK,
	
	/** Interface I2XTable. */
	SJME_MEMHANDLE_KIND_I2X_TABLE,
	
	/** Interface XTable. */
	SJME_MEMHANDLE_KIND_INTERFACE_XTABLE,
	
	/** The number of kinds used. */
	SJME_MEMHANDLE_KIND_NUM_KINDS
} sjme_memHandleKind;

/**
 * Initializes the state of memory handles.
 * 
 * @param out The output where handles will go.
 * @param error The error state, if any.
 * @return The failure state.
 * @since 2021/02/27
 */
sjme_returnFail sjme_memHandlesInit(sjme_memHandles** out, sjme_error* error);

/**
 * Destroys and deallocates the state of memory handles.
 * 
 * @param handles The handles to destroy.
 * @param error The error state.
 * @return If this failed or not.
 * @since 2021/02/28
 */
sjme_returnFail sjme_memHandlesDestroy(sjme_memHandles* handles,
	sjme_error* error);

/**
 * Returns the handle statistics.
 * 
 * @param handles The handles to get for.
 * @param out The target statistics.
 * @param error The error state.
 * @return If this failed or not.
 * @since 2021/03/08
 */
sjme_returnFail sjme_memHandlesStats(sjme_memHandles* handles,
	sjme_memHandleStats* out, sjme_error* error);

/**
 * Initializes a new memory 
 * 
 * @param handles The handle state.
 * @param out The output handle.
 * @param kind The kind of handle to allocate.
 * @param size The size of the handle.
 * @param error The error state.
 * @return If this failed or not.
 * @since 2021/02/28
 */
sjme_returnFail sjme_memHandleNew(sjme_memHandles* handles,
	sjme_memHandle** out, sjme_memHandleKind kind, sjme_jint size,
	sjme_error* error);

/**
 * Deletes the given memory handle.
 * 
 * @param handles The handle storage.
 * @param handle The handle to clear.
 * @param error The output error state.
 * @return If destroying the mem handle worked or not.
 * @since 2021/03/06 
 */
sjme_returnFail sjme_memHandleDelete(sjme_memHandles* handles,
	sjme_memHandle* handle, sjme_error* error);
	
/**
 * Check if the given range sequence is in range for a memory handle.
 * 
 * @param handle The memory handle.
 * @param offset The offset into the handle.
 * @param length The number of bytes to check.
 * @param error The output error state.
 * @return If the access would be out of bounds.
 * @since 2021/01/24
 */
sjme_returnFail sjme_memHandleInBounds(sjme_memHandle* handle, 
	sjme_jint offset, sjme_jint length, sjme_error* error);

/**
 * Accesses a memory handle
 * 
 * @param handle The handle to read/write from.
 * @param write Writing to the handle?
 * @param type The type of data to read/write.
 * @param inOut The input/output.
 * @param offset The offset into the handle.
 * @param error The failure state.
 * @return If this failed or not.
 * @since 2021/03/06
 */
sjme_returnFail sjme_memHandleAccess(sjme_memHandle* handle,
	sjme_jboolean write, sjme_dataType type, sjme_jint* inOut,
	sjme_jint offset, sjme_error* error);

/**
 * Accesses a memory handle using wide data.
 * 
 * @param handle The handle to read/write from.
 * @param write Writing to the handle?
 * @param type The type of data to read/write.
 * @param inOut The input/output.
 * @param offset The offset into the handle.
 * @param error The failure state.
 * @return If this failed or not.
 * @since 2021/03/06
 */
sjme_returnFail sjme_memHandleAccessWide(sjme_memHandle* handle,
	sjme_jboolean write, sjme_dataType type, sjme_jlong* inOut,
	sjme_jint offset, sjme_error* error);

/*--------------------------------------------------------------------------*/

/* Anti-C++. */
#ifdef __cplusplus
#ifdef SJME_CXX_SQUIRRELJME_HANDLES_H
}
#undef SJME_CXX_SQUIRRELJME_HANDLES_H
#undef SJME_CXX_IS_EXTERNED
#endif /* #ifdef SJME_CXX_SQUIRRELJME_HANDLES_H */
#endif /* #ifdef __cplusplus */

#endif /* SQUIRRELJME_HANDLES_H */
