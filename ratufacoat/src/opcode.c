/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

#include "sjmerc.h"
#include "opcode.h"
#include "error.h"
#include "oldstuff.h"
#include "cpu.h"

sjme_jint sjme_opdecodejint(sjme_vmem* vmem, sjme_vmemptr* ptr,
	sjme_error* error)
{
	sjme_jint rv;
	
	/* Read all values. */
	rv = (sjme_vmmreadp(vmem, SJME_VMMTYPE_BYTE, ptr, error) &
		SJME_JINT_C(0xFF)) << 24;
	rv |= (sjme_vmmreadp(vmem, SJME_VMMTYPE_BYTE, ptr, error) &
		SJME_JINT_C(0xFF)) << 16;
	rv |= (sjme_vmmreadp(vmem, SJME_VMMTYPE_BYTE, ptr, error) &
		SJME_JINT_C(0xFF)) << 8;
	rv |= (sjme_vmmreadp(vmem, SJME_VMMTYPE_BYTE, ptr, error) &
		SJME_JINT_C(0xFF));
	
	return rv;
}

sjme_jint sjme_opdecodejshort(sjme_vmem* vmem, sjme_vmemptr* ptr,
	sjme_error* error)
{
	sjme_jint rv;
	
	/* Read all values. */
	rv = (sjme_vmmreadp(vmem, SJME_VMMTYPE_BYTE, ptr, error) &
		SJME_JINT_C(0xFF)) << 8;
	rv |= (sjme_vmmreadp(vmem, SJME_VMMTYPE_BYTE, ptr, error) &
		SJME_JINT_C(0xFF));
	
	/* Sign extend? */
	if (rv & SJME_JINT_C(0x8000))
		rv |= SJME_JINT_C(0xFFFF0000);
	
	return rv;
}

sjme_jint sjme_opdecodeui(sjme_vmem* vmem, sjme_vmemptr* ptr,
	sjme_error* error)
{
	sjme_jint rv;
	
	/* Read single byte value from pointer. */
	rv = (sjme_vmmreadp(vmem, SJME_VMMTYPE_BYTE, ptr, error) &
		SJME_JINT_C(0xFF));
	
	/* Encoded as a 15-bit value? */
	if ((rv & SJME_JINT_C(0x80)) != 0)
	{
		rv = (rv & SJME_JINT_C(0x7F)) << SJME_JINT_C(8);
		rv |= (sjme_vmmreadp(vmem, SJME_VMMTYPE_BYTE, ptr, error) &
			SJME_JINT_C(0xFF));
	}
	
	/* Use read value. */
	return rv;
}

sjme_jint sjme_opdecodereg(sjme_vmem* vmem, sjme_vmemptr* ptr,
	sjme_error* error)
{
	sjme_jint rv;
	
	/* Decode register. */
	rv = sjme_opdecodeui(vmem, ptr, error);
	
	/* Keep within register bound. */
	if (rv < 0 || rv >= SJME_MAX_REGISTERS)
	{
		sjme_setError(error, SJME_ERROR_REGISTEROVERFLOW, rv);
		
		return 0;
	}
	
	/* Return it. */
	return rv;
}

sjme_jint sjme_opdecodejmp(sjme_vmem* vmem, sjme_vmemptr* ptr,
	sjme_error* error)
{
	sjme_jint rv;
	
	/* Decode value. */
	rv = sjme_opdecodeui(vmem, ptr, error);
	
	/* Negative branch? */
	if ((rv & SJME_JINT_C(0x00004000)) != 0)
		return rv | SJME_JINT_C(0xFFFF8000);
	return rv;
}
