/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

#include "debug.h"
#include "handles.h"
#include "memory.h"
#include "random.h"

/** Initial maximum handle count. */
#define SJME_INIT_COUNT 64

/** Minimum possible mask. */
#define SJME_MINIMAL_MASK 63

/** Random allocation tries. */
#define SJME_RANDOM_TRIES 7

/**
 * Storage for all memory handles.
 * 
 * @since 2021/02/27
 */
struct sjme_memHandles
{
	/** Allocated handle array (resized as needed). */
	sjme_memHandle** handles;
	
	/** The handle array size. */
	sjme_jint numHandles;
	
	/** Random number for the handle ID. */
	sjme_randomState random;
	
	/** Used number of handles. */
	sjme_jint usedHandles;
	
	/** Statistics. */
	sjme_memHandleStats stats;
};

/**
 * Structure for a single memory handle.
 * 
 * @since 2021/02/27
 */
struct sjme_memHandle
{
	/** The identifier of the handle. */
	sjme_jint id;
	
	/** The mask used to determine the position in the slotting table. */
	sjme_jint slotMask;
	
	/** The kind of this handle. */
	sjme_memHandleKind kind;
	
	/** The reference count of the handle. */
	sjme_jint refCount;
	
	/** The length of this handle. */
	sjme_jint length;
	
	/** Dangling data for handles. */
	sjme_jbyte bytes[];
};

sjme_returnFail sjme_memHandlesInit(sjme_memHandles** out, sjme_error* error)
{
	sjme_memHandles* rv = NULL;
	sjme_memHandle** handles = NULL;
	sjme_jlong seed;
	
	/* Cannot be null. */
	if (out == NULL)
	{
		sjme_setError(error, SJME_ERROR_NULLARGS, 0);
		return SJME_RETURN_FAIL;
	}
	
	/* Allocate, check if it was actually done. */
	rv = sjme_malloc(sizeof(*rv));
	if (rv == NULL)
	{
		sjme_setError(error, SJME_ERROR_NO_MEMORY, sizeof(*rv));
		return SJME_RETURN_FAIL;
	}
	
	/* Seed the RNG for generated upper values. */
	seed.hi = SJME_POINTER_TO_JINT((void*)rv);
	seed.lo = SJME_POINTER_TO_JINT((void*)out);
	if (sjme_randomSeed(&rv->random, seed, error))
	{
		sjme_free(rv);
		
		if (!sjme_hasError(error))
			sjme_setError(error, SJME_ERROR_COULD_NOT_SEED,
				sizeof(sjme_memHandle*) * SJME_INIT_COUNT);
		return SJME_RETURN_FAIL;
	}
	
	/* Allocate storage for all handles. */
	handles = sjme_malloc(sizeof(sjme_memHandle*) * SJME_INIT_COUNT);
	if (handles == NULL)
	{
		sjme_free(rv);
		
		sjme_setError(error, SJME_ERROR_NO_MEMORY,
			sizeof(sjme_memHandle*) * SJME_INIT_COUNT);
		return SJME_RETURN_FAIL;
	}
	
	/* Figure out where this handle goes in the handle list. */
	
	/* Set where handles are contained. */
	rv->handles = handles;
	rv->numHandles = SJME_INIT_COUNT;
	
	/* It worked! So return the pointer. */
	*out = rv;
	return SJME_RETURN_SUCCESS;
}

sjme_returnFail sjme_memHandlesDestroy(sjme_memHandles* handles,
	sjme_error* error)
{
	sjme_jint i;
	
	/* Cannot be null. */
	if (handles == NULL)
	{
		sjme_setError(error, SJME_ERROR_NULLARGS, 0);
		return SJME_RETURN_FAIL;
	}
	
	/* Delete all existing handles. */
	for (i = 0; i < handles->numHandles; i++)
		if (handles->handles[i] != NULL)
			if (sjme_memHandleDelete(handles, handles->handles[i], error))
			{
				if (!sjme_hasError(error))
					sjme_setError(error,
						SJME_ERROR_DESTROY_FAIL, 0);
				return SJME_RETURN_FAIL;
			}
	
	/* Clear handles array. */
	sjme_free(handles->handles);
	
	/* Wipe and then clear handles. */
	memset(handles, 0, sizeof(*handles));
	sjme_free(handles);
	
	/* Success! */
	return SJME_RETURN_SUCCESS;
}

sjme_returnFail sjme_memHandlesStats(sjme_memHandles* handles,
	sjme_memHandleStats* out, sjme_error* error)
{
	if (handles == NULL || out == NULL)
	{
		sjme_setError(error, SJME_ERROR_NULLARGS, 0);
		return SJME_RETURN_FAIL;
	}
	
	/* Copy over. */
	memmove(out, &handles->stats, sizeof(sjme_memHandleStats));
	
	return SJME_RETURN_SUCCESS;
}

sjme_returnFail sjme_memHandleNew(sjme_memHandles* handles,
	sjme_memHandle** out, sjme_memHandleKind kind, sjme_jint size,
	sjme_error* error)
{
	sjme_memHandle** newHandles;
	sjme_memHandle** tray;
	sjme_memHandle* rv = NULL;
	sjme_memHandle* check;
	sjme_jint randId, trySlot, tryId, scanSlot, mask, topMask, randTry;
	
	/* Cannot be null. */
	if (handles == NULL || out == NULL)
	{
		sjme_setError(error, SJME_ERROR_NULLARGS, 0);
		return SJME_RETURN_FAIL;
	}
	
	/* Check size. */
	if (size < 0)
	{
		sjme_setError(error, SJME_ERROR_NEGATIVE_SIZE, size);
		return SJME_RETURN_FAIL;
	}
	
	/* Check kind. */
	if (kind <= SJME_MEMHANDLE_KIND_UNDEFINED ||
		kind >= SJME_MEMHANDLE_KIND_NUM_KINDS)
	{
		sjme_setError(error, SJME_ERROR_INVALID_MEMHANDLE_KIND, kind);
		return SJME_RETURN_FAIL;
	}
	
	/* Try to seed a random ID for placement. */
	if (sjme_randomNextInt(&handles->random, &randId, error))
	{
		sjme_setError(error, SJME_ERROR_COULD_NOT_SEED, 0);
		return SJME_RETURN_FAIL;
	}
	
	/* Attempt to allocate the handle. */
	rv = sjme_malloc(sizeof(*rv) + size);
	if (rv == NULL)
	{
		sjme_setError(error, SJME_ERROR_NO_MEMORY, sizeof(*rv));
		return SJME_RETURN_FAIL;
	}
	
	/* Try to determine if this can fit directly in a slot, without a scan. */
	trySlot = randId & (handles->numHandles - 1);
	while (handles->handles[trySlot] != NULL)
	{
		/* Keep track of this loop. */
		handles->stats.reRollCount++;
		
		/* If the majority of our handles are taken up then the chance of */
		/* randomly grabbing a new one will be more difficult so just give */
		/* up here. */
		if (handles->usedHandles > ((handles->numHandles >> 1) +
			(handles->numHandles >> 2)))
			trySlot = handles->numHandles;
		
		/* Otherwise first pick at random, then do a linear scan. */
		else
		{
			/* Try a bunch of tries to place this in a still random slot. */
			for (randTry = 0; randTry < SJME_RANDOM_TRIES; randTry++)
			{
				/* Make another random ID and try to fit it in here. */
				if (sjme_randomNextInt(&handles->random, &randId, error))
				{
					sjme_setError(error, SJME_ERROR_COULD_NOT_SEED,
						randTry);
					return SJME_RETURN_FAIL;
				}
				
				/* Is this random ID valid? */
				trySlot = randId & (handles->numHandles - 1);
				if (handles->handles[trySlot] == NULL)
					break;
			}
			
			/* A random attempt worked! */
			if (randTry < SJME_RANDOM_TRIES)
			{
				handles->stats.reRollHit++;
				break;
			}
			
			/* Counts as a miss. */
			handles->stats.reRollMiss++;
			
			/* Find the first free slot. */
			for (trySlot = 0; trySlot < handles->numHandles; trySlot++)
				if (handles->handles[trySlot] == NULL)
					break;
		}
		
		/* No slot found? Grow the space. */
		if (trySlot == handles->numHandles)
		{
			/* Allocate more space to store the handles. */
			newHandles = sjme_realloc(handles->handles, 
				sizeof(sjme_memHandle*) * (handles->numHandles << 1));
			if (newHandles == NULL)
			{
				sjme_free(rv);
				
				sjme_setError(error, SJME_ERROR_NO_MEMORY,
					sizeof(sjme_memHandle*) *
					(handles->numHandles << 1));
				return SJME_RETURN_FAIL;
			}
			
			/* Use new handle data. */
			handles->handles = newHandles;
			handles->numHandles <<= 1;
			
			/* Keep statistics. */
			handles->stats.growCount++;
			
			/* Try again! */
			continue;
		}
		
		/* Stop trying. */
		break;
	}
	
	/* Make sure the ID is unique. */
	/* Note that since the lowest set of bits is masked out of the actual */
	/* slot, we can just go down the masks and see if there is a collision! */
	/* Linear scanning will take a very long forever the more and more */
	/* handles exist. */
	/* ex: BOOP 0x97e1dda4 -> 23972:0x97e1dda4 */
	/* ex: BOOP 0xdb8936c4 -> 5828:0xdb8936c4 */
	/* ex: BOOP 0xcf07b907 -> 47367:0xcf07b907 */
	topMask = handles->numHandles - 1;
	tryId = (randId & (~topMask)) | trySlot;
	tray = handles->handles;
	for (mask = topMask; mask >= SJME_MINIMAL_MASK; mask >>= 1)
	{
		/* Try this slot. */
		scanSlot = tryId & mask;
		
		/* Is there an ID collision? */
		check = tray[scanSlot];
		if (check != NULL && check->id == tryId)
		{
			/* Keep track of this. */
			handles->stats.downMaskMiss++;
			
			/* Grab another ID. */
			if (sjme_randomNextInt(&handles->random, &randId, error))
			{
				sjme_free(rv);
				
				sjme_setError(error, SJME_ERROR_COULD_NOT_SEED, tryId);
				return SJME_RETURN_FAIL;
			}
			
			/* Calculate a new ID number. */
			tryId = (randId & (~topMask)) | trySlot;
			
			/* Go back to the start, shift up since after-loop shift-down. */
			mask = topMask << 1;
		}
	}
	
	/* Store in this slot. */
	handles->handles[trySlot] = rv;
	
	/* Count up. */
	handles->usedHandles++;
	handles->stats.numNew++;
	
	/* Set properties for the handle. */
	rv->id = tryId;
	rv->kind = kind;
	rv->length = size;
	rv->slotMask = handles->numHandles - 1;
	rv->refCount = 1;		/* Prevent instant GC. */
	
	/* Use this one. */
	*out = rv;
	return SJME_RETURN_SUCCESS;
}

sjme_returnFail sjme_memHandleDelete(sjme_memHandles* handles,
	sjme_memHandle* handle, sjme_error* error)
{
	sjme_jint slot;
	
	/* Cannot be null. */
	if (handles == NULL || handle == NULL)
	{
		sjme_setError(error, SJME_ERROR_NULLARGS, 0);
		return SJME_RETURN_FAIL;
	}
	
	/* Determine the slot for the handle. */
	slot = handle->id & handle->slotMask;
	
	/* Make sure this handle is really in this slot. */
	if (slot < 0 || slot >= handles->numHandles ||
		handles->handles[slot] == NULL ||
		handles->handles[slot]->id != handle->id ||
		handles->handles[slot] != handle)
	{
		sjme_setError(error, SJME_ERROR_INVALID_HANDLE, handle->id);
		return SJME_RETURN_FAIL;
	}
	
	/* Clear the slot. */
	handles->handles[slot] = NULL;
	
	/* Count down. */
	handles->usedHandles--;
	handles->stats.numDelete++;
	
	/* Destroy the handle data. */
	memset(handle, 0, sizeof(*handle) + handle->length);
	sjme_free(handle);
	
	return SJME_RETURN_SUCCESS;
}

sjme_returnFail sjme_memHandleInBounds(sjme_memHandle* handle, 
	sjme_jint offset, sjme_jint length, sjme_error* error)
{
	/* Cannot be null. */
	if (handle == NULL)
	{
		sjme_setError(error, SJME_ERROR_NULLARGS, 0);
		return SJME_RETURN_FAIL;
	}
	
	/* Check offset and length. */
	if (offset < 0 || length < 0 || (offset + length) > handle->length)
	{
		sjme_setError(error, SJME_ERROR_OUT_OF_BOUNDS, offset);
		return SJME_RETURN_FAIL;
	}
	
	/* Is okay. */
	return SJME_RETURN_SUCCESS;
}

sjme_returnFail sjme_memHandleAccess(sjme_memHandle* handle,
	sjme_jboolean write, sjme_dataType type, sjme_jint* inOut,
	sjme_jint offset, sjme_error* error)
{
	/* Cannot be null. */
	if (handle == NULL || inOut == NULL)
	{
		sjme_setError(error, SJME_ERROR_NULLARGS, 0);
		return SJME_RETURN_FAIL;
	}
	
	/* Must be a valid data type. */
	if (type != SJME_DATATYPE_OBJECT &&
		type != SJME_DATATYPE_BYTE &&
		type != SJME_DATATYPE_SHORT &&
		type != SJME_DATATYPE_CHARACTER &&
		type != SJME_DATATYPE_INTEGER &&
		type != SJME_DATATYPE_FLOAT)
	{
		sjme_setError(error, SJME_ERROR_INVALID_DATATYPE, type);
		return SJME_RETURN_FAIL;
	}
	
	/* Check bounds of the handle. */
	if (sjme_memHandleInBounds(handle, offset, sjme_dataTypeSize[type], error))
	{
		if (!sjme_hasError(error))
			sjme_setError(error, SJME_ERROR_OUT_OF_BOUNDS, offset);
		return SJME_RETURN_FAIL;
	}
	
	/* Read the value here. */
	switch (type)
	{
		case SJME_DATATYPE_BYTE:
			if (write)
				handle->bytes[offset] = *inOut;
			else
				*inOut = handle->bytes[offset];
			break;
		
		case SJME_DATATYPE_SHORT:
			if (write)
				*((sjme_jshort*)&handle->bytes[offset]) = *inOut;
			else
				*inOut = *((sjme_jshort*)&handle->bytes[offset]);
			break;
		
		case SJME_DATATYPE_CHARACTER:
			if (write)
				*((sjme_jshort*)&handle->bytes[offset]) = *inOut;
			else
				*inOut = (*((sjme_jshort*)&handle->bytes[offset])) &
					SJME_JINT_C(0xFFFF);
			break;
		
		case SJME_DATATYPE_OBJECT:
		case SJME_DATATYPE_INTEGER:
		case SJME_DATATYPE_FLOAT:
			if (write)
				*((sjme_jint*)&handle->bytes[offset]) = *inOut;
			else
				*inOut = *((sjme_jint*)&handle->bytes[offset]);
			break;
	}
	
	// Success
	return SJME_RETURN_SUCCESS;
}

sjme_returnFail sjme_memHandleAccessWide(sjme_memHandle* handle,
	sjme_jboolean write, sjme_dataType type, sjme_jlong* inOut,
	sjme_jint offset, sjme_error* error)
{
	/* Cannot be null. */
	if (handle == NULL || inOut == NULL)
	{
		sjme_setError(error, SJME_ERROR_NULLARGS, 0);
		return SJME_RETURN_FAIL;
	}
	
	/* Must be a valid data type. */
	if (type != SJME_DATATYPE_LONG &&
		type != SJME_DATATYPE_DOUBLE)
	{
		sjme_setError(error, SJME_ERROR_INVALID_DATATYPE, type);
		return SJME_RETURN_FAIL;
	}
	
	/* Check bounds of the handle. */
	if (sjme_memHandleInBounds(handle, offset, sjme_dataTypeSize[type], error))
	{
		if (!sjme_hasError(error))
			sjme_setError(error, SJME_ERROR_OUT_OF_BOUNDS, offset);
		return SJME_RETURN_FAIL;
	}

#if defined(SJME_BIG_ENDIAN)
	if (write)
	{
		*((sjme_jint*)&handle->bytes[offset]) = inOut->hi;
		*((sjme_jint*)&handle->bytes[offset + 4]) = inOut->lo;
	}
	else
	{
		inOut->hi = *((sjme_jint*)&handle->bytes[offset]);
		inOut->lo = *((sjme_jint*)&handle->bytes[offset + 4]);
	}
#else
	if (write)
	{
		*((sjme_jint*)&handle->bytes[offset]) = inOut->lo;
		*((sjme_jint*)&handle->bytes[offset + 4]) = inOut->hi;
	}
	else
	{
		inOut->lo = *((sjme_jint*)&handle->bytes[offset]);
		inOut->hi = *((sjme_jint*)&handle->bytes[offset + 4]);
	}
#endif
	
	// Success
	return SJME_RETURN_SUCCESS;
}

