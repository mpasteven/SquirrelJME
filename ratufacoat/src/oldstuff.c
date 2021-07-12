/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// --------------------------------------------------------------------------*/

/**
 * Virtual memory functions (deprecated).
 *
 * @since 2019/06/25
 */

#include "sjmerc.h"
#include "oldstuff.h"
#include "memory.h"

/** Base of virtual memory. */
#define SJME_VIRTUAL_MEM_BASE SJME_JINT_C(1048576)

/** Rounding of virtual memory. */
#define SJME_VIRTUAL_MEM_MASK SJME_JINT_C(1023)

/** Virtual memory information. */
struct sjme_vmem
{
	/** The number of mappings. */
	sjme_jint count;
	
	/** The next address for allocations. */
	sjme_jint nextaddr;
	
	/** Mappings. */
	sjme_vmemmap** maps;
};

sjme_vmem* sjme_vmmnew(sjme_error* error)
{
	sjme_vmem* rv;
	
	/* Try to allocate space. */
	rv = sjme_malloc(sizeof(*rv));
	if (rv == NULL)
	{
		sjme_setError(error, SJME_ERROR_NO_MEMORY, sizeof(*rv));
		
		return NULL;
	}
	
	/* Initialize. */
	rv->nextaddr = SJME_VIRTUAL_MEM_BASE;
	
	return rv;
}

sjme_vmemmap* sjme_vmmmap(sjme_vmem* vmem, sjme_jint at, void* ptr,
	sjme_jint size, sjme_error* error)
{
	sjme_vmemmap* rv;
	sjme_vmemmap** newmaps;
	sjme_jint i;
	
	(void)at;
	
	/* Invalid argument. */
	if (vmem == NULL || ptr == NULL || size <= 0)
	{
		sjme_setError(error, SJME_ERROR_INVALIDARG, 0);
		
		return NULL;
	}
	
	/* Allocate return value. */
	rv = sjme_malloc(sizeof(*rv));
	newmaps = sjme_malloc(sizeof(*newmaps) * (vmem->count + 1));
	if (rv == NULL)
	{
		sjme_setError(error, SJME_ERROR_NO_MEMORY, sizeof(*rv));
		
		sjme_free(rv);
		sjme_free(newmaps);
		
		return NULL;
	}
	
	/* Copy and set new mappings. */
	for (i = 0; i < vmem->count; i++)
		newmaps[i] = vmem->maps[i];
	newmaps[vmem->count] = rv;
	
	/* Setup mapping. */
	rv->realptr = (uintptr_t)ptr;
	rv->fakeptr = vmem->nextaddr;
	rv->size = size;
	
	/* Store the mappings (remember to free the old ones!). */
	vmem->nextaddr = (vmem->nextaddr + size + SJME_VIRTUAL_MEM_MASK) &
		(~SJME_VIRTUAL_MEM_MASK);
	vmem->count = vmem->count + 1;
	sjme_free(vmem->maps);
	vmem->maps = newmaps;
	
	return rv;
}

void* sjme_vmmresolve(sjme_vmem* vmem, sjme_vmemptr ptr, sjme_jint off,
	sjme_error* error)
{
	sjme_jint i, n, moff;
	sjme_vmemmap* map;
	sjme_vmemptr optr;
	uintptr_t mp;
	
	/* Invalid argument. */
	if (vmem == NULL)
	{
		sjme_setError(error, SJME_ERROR_INVALIDARG, 0);
		
		return NULL;
	}
	
	/* Get true pointer position. */
	optr = ptr + off;
	
	/* Search for the mapping. */
	for (i = 0, n = vmem->count; i < n; i++)
	{
		/* Get mapping. */
		map = vmem->maps[i];
		
		/* Calculate the mapping offset. */
		moff = optr - map->fakeptr;
		
		/* Is in range? */
		if (moff >= 0 && moff < map->size)
		{
			/* If this mapping is banked, make sure it is mapped. */
			if (map->bank != NULL)
			{
				/* Load the bank into memory. */
				mp = map->bank(&moff);
				
				/* Return from the base mapping using the returned offset. */
				return (void*)(mp + moff);
			}
			
			/* Otherwise, use direct memory access. */
			return (void*)(map->realptr + moff);
		}
	}
	
	/* Address is not valid. */
	sjme_setError(error, SJME_ERROR_BADADDRESS, optr);
	return NULL;
}

sjme_jint sjme_vmmsizetojavatype(sjme_jint size, sjme_error* error)
{
	/* Convert. */
	switch (size)
	{
		case 1:
			return SJME_VMMTYPE_BYTE;
			
		case 2:
			return SJME_VMMTYPE_JAVASHORT;
			
		case 4:
			return SJME_VMMTYPE_JAVAINTEGER;
	}
	
	/* Not valid. */
	sjme_setError(error, SJME_ERROR_INVALIDSIZE, size);
	return 0;
}

sjme_jint sjme_vmmsizetotype(sjme_jint size, sjme_error* error)
{
	/* Convert. */
	switch (size)
	{
		case 1:
			return SJME_VMMTYPE_BYTE;
			
		case 2:
			return SJME_VMMTYPE_SHORT;
			
		case 4:
			return SJME_VMMTYPE_INTEGER;
	}
	
	/* Not valid. */
	sjme_setError(error, SJME_ERROR_INVALIDSIZE, size);
	return 0;
}

sjme_jint sjme_vmmread(sjme_vmem* vmem, sjme_jint type, sjme_vmemptr ptr,
	sjme_jint off, sjme_error* error)
{
	void* realptr;
	sjme_error xerror;
	
	/* Invalid argument? */
	if (vmem == NULL)
	{
		sjme_setError(error, SJME_ERROR_INVALIDARG, 0);
		
		return 0;
	}
	
	/* Need error. */
	if (error == NULL)
	{
		memset(&xerror, 0, sizeof(xerror));
		error = &xerror;
	}
	
	/* Resolve address. */
	realptr = sjme_vmmresolve(vmem, ptr, off, error);
	if (realptr == NULL)
	{
		if (error->code == SJME_ERROR_NONE)
			sjme_setError(error, SJME_ERROR_ADDRRESFAIL, ptr + off);
		
		return 0;
	}
	
	/* Depends on the type. */
	switch (type)
	{
		case SJME_VMMTYPE_BYTE:
			return *((sjme_jbyte*)realptr);

#if defined(SJME_BIG_ENDIAN)
		case SJME_VMMTYPE_SHORT:
		case SJME_VMMTYPE_JAVASHORT:
			return *((sjme_jshort*)realptr);
#else
		case SJME_VMMTYPE_SHORT:
			return *((sjme_jshort*)realptr);
			
		case SJME_VMMTYPE_JAVASHORT:
			type = *((sjme_jshort*)realptr);
			type = (type & SJME_JINT_C(0xFFFF0000)) |
				(((type << SJME_JINT_C(8)) & SJME_JINT_C(0xFF00)) |
				((type >> SJME_JINT_C(8)) & SJME_JINT_C(0x00FF)));
			return type;
#endif

#if defined(SJME_BIG_ENDIAN)
		case SJME_VMMTYPE_INTEGER:
		case SJME_VMMTYPE_JAVAINTEGER:
			return *((sjme_jint*)realptr);
#else
		case SJME_VMMTYPE_INTEGER:
			return *((sjme_jint*)realptr);
		
		case SJME_VMMTYPE_JAVAINTEGER:
			type = *((sjme_jint*)realptr);
			type = (((type >> SJME_JINT_C(24)) & SJME_JINT_C(0x000000FF)) |
				((type >> SJME_JINT_C(8)) & SJME_JINT_C(0x0000FF00)) |
				((type << SJME_JINT_C(8)) & SJME_JINT_C(0x00FF0000)) |
				((type << SJME_JINT_C(24)) & SJME_JINT_C(0xFF000000)));
			return type;
#endif
			
		default:
			sjme_setError(error, SJME_ERROR_INVALIDMEMTYPE, type);
			return 0;
	}
}

sjme_jint sjme_vmmreadp(sjme_vmem* vmem, sjme_jint type, sjme_vmemptr* ptr,
	sjme_error* error)
{
	sjme_jint rv;
	
	/* Read value. */
	rv = sjme_vmmread(vmem, type, *ptr, 0, error);
	
	/* Increment pointer. */
	switch (type)
	{
		case SJME_VMMTYPE_BYTE:
			*ptr = *ptr + 1;
			break;
			
		case SJME_VMMTYPE_SHORT:
		case SJME_VMMTYPE_JAVASHORT:
			*ptr = *ptr + 2;
			break;
			
		case SJME_VMMTYPE_INTEGER:
		case SJME_VMMTYPE_JAVAINTEGER:
			*ptr = *ptr + 4;
			break;
	}
	
	/* Return value. */
	return rv;
}

void sjme_vmmwrite(sjme_vmem* vmem, sjme_jint type, sjme_vmemptr ptr,
	sjme_jint off, sjme_jint val, sjme_error* error)
{
	void* realptr;
	sjme_error xerror;
	
	/* Invalid argument? */
	if (vmem == NULL)
	{
		sjme_setError(error, SJME_ERROR_INVALIDARG, 0);
		
		return;
	}
	
	/* Need error. */
	if (error == NULL)
	{
		memset(&xerror, 0, sizeof(xerror));
		error = &xerror;
	}
	
	/* Resolve address. */
	realptr = sjme_vmmresolve(vmem, ptr, off, error);
	if (realptr == NULL)
	{
		if (error->code == SJME_ERROR_NONE)
			sjme_setError(error, SJME_ERROR_ADDRRESFAIL, ptr + off);
		
		return;
	}
	
	/* Depends on the type. */
	switch (type)
	{
		case SJME_VMMTYPE_BYTE:
			*((sjme_jbyte*)realptr) = (sjme_jbyte)val;
			return;
		
		case SJME_VMMTYPE_JAVASHORT:
#if defined(SJME_LITTLE_ENDIAN)
			val = (val & SJME_JINT_C(0xFFFF0000)) |
				(((val << SJME_JINT_C(8)) & SJME_JINT_C(0xFF00)) |
				((val >> SJME_JINT_C(8)) & SJME_JINT_C(0x00FF)));
#endif
		case SJME_VMMTYPE_SHORT:
			*((sjme_jshort*)realptr) = (sjme_jshort)val;
			return;
		
		case SJME_VMMTYPE_JAVAINTEGER:
#if defined(SJME_LITTLE_ENDIAN)
			val = (((val >> SJME_JINT_C(24)) & SJME_JINT_C(0x000000FF)) |
				((val >> SJME_JINT_C(8)) & SJME_JINT_C(0x0000FF00)) |
				((val << SJME_JINT_C(8)) & SJME_JINT_C(0x00FF0000)) |
				((val << SJME_JINT_C(24)) & SJME_JINT_C(0xFF000000)));
#endif
		case SJME_VMMTYPE_INTEGER:
			*((sjme_jint*)realptr) = (sjme_jint)val;
			return;
			
		default:
			sjme_setError(error, SJME_ERROR_INVALIDMEMTYPE, type);
			return;
	}
}

void sjme_vmmwritep(sjme_vmem* vmem, sjme_jint type, sjme_vmemptr* ptr,
	sjme_jint val, sjme_error* error)
{
	sjme_jint rv;
	
	/* Write value. */
	sjme_vmmwrite(vmem, type, *ptr, 0, val, error);
	
	/* Increment pointer. */
	switch (type)
	{
		case SJME_VMMTYPE_BYTE:
			*ptr = *ptr + 1;
			break;
			
		case SJME_VMMTYPE_SHORT:
		case SJME_VMMTYPE_JAVASHORT:
			*ptr = *ptr + 2;
			break;
			
		case SJME_VMMTYPE_INTEGER:
		case SJME_VMMTYPE_JAVAINTEGER:
			*ptr = *ptr + 4;
			break;
	}
}

sjme_jint sjme_vmmatomicintcheckgetandset(sjme_vmem* vmem, sjme_jint check,
	sjme_jint set, sjme_vmemptr ptr, sjme_jint off, sjme_error* error)
{
	sjme_jint rv;
	
	/* Invalid argument? */
	if (vmem == NULL)
	{
		sjme_setError(error, SJME_ERROR_INVALIDARG, 0);
		
		return 0;
	}
	
	/* Read current value. */
	rv = sjme_vmmread(vmem, SJME_VMMTYPE_INTEGER, ptr, off, error);
	
	/* If value is the same, set it. */
	if (rv == check)
		sjme_vmmwrite(vmem, SJME_VMMTYPE_INTEGER, ptr, off, set, error);
	
	/* Return the value. */
	return rv;
}

sjme_jint sjme_vmmatomicintaddandget(sjme_vmem* vmem,
	sjme_vmemptr ptr, sjme_jint off, sjme_jint add, sjme_error* error)
{
	sjme_jint rv;
	
	/* Invalid argument? */
	if (vmem == NULL)
	{
		sjme_setError(error, SJME_ERROR_INVALIDARG, 0);
		
		return 0;
	}
	
	/* Read current value. */
	rv = sjme_vmmread(vmem, SJME_VMMTYPE_INTEGER, ptr, off, error);
	
	/* Add value. */
	rv += add;
	
	/* Store new value. */
	sjme_vmmwrite(vmem, SJME_VMMTYPE_INTEGER, ptr, off, rv, error);
	
	/* Return new value. */
	return rv;
}
