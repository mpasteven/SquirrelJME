/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

#include "bootrom.h"
#include "debug.h"

sjme_returnFail sjme_loadBootRom(sjme_jvm* jvm, sjme_error* error)
{
	sjme_todo("sjme_loadBootRom(%p, %p)", jvm, error);

#if 0
	sjme_vmemptr rp;
	sjme_vmemptr bootjar;
	sjme_jint bootoff, i, n, seedop, seedaddr, seedvalh, seedvall, seedsize;
	sjme_jint bootjaroff, vrambase, vrombase, qq;
	sjme_cpu* cpu;
	sjme_error xerror;
	
	/* Invalid arguments. */
	if (jvm == NULL)
	{
		sjme_seterror(error, SJME_ERROR_INVALIDARG, 0);
		
		return 0;
	}
	
	/* Force error to be set. */
	if (error == NULL)
	{
		memset(&xerror, 0, sizeof(xerror));
		error = &xerror;
	}
	
	/* Determine the address the VM sees for some memory types. */
	vrambase = jvm->ram->fakeptr;
	vrombase = jvm->rom->fakeptr;
	
	/* Set initial CPU (the first). */
	cpu = &jvm->threads[0];
	cpu->threadstate = SJME_THREAD_STATE_RUNNING;
	
	/* Set boot pointer to start of ROM. */
	rp = jvm->rom->fakeptr;
	
	/* Check ROM magic number. */
	if ((qq = sjme_vmmreadp(sjme_jvmVMem(jvm), SJME_VMMTYPE_JAVAINTEGER, &rp, error))
		!= SJME_ROM_MAGIC_NUMBER)
	{
		sjme_seterror(error, SJME_ERROR_INVALIDROMMAGIC, qq);
		
		return 0;
	}
	
	/* Ignore numjars, tocoffset, bootjarindex. */
	sjme_vmmreadp(sjme_jvmVMem(jvm), SJME_VMMTYPE_JAVAINTEGER, &rp, error);
	sjme_vmmreadp(sjme_jvmVMem(jvm), SJME_VMMTYPE_JAVAINTEGER, &rp, error);
	sjme_vmmreadp(sjme_jvmVMem(jvm), SJME_VMMTYPE_JAVAINTEGER, &rp, error);
	
	/* Read and calculate BootJAR position. */
	bootjaroff = sjme_vmmreadp(sjme_jvmVMem(jvm), SJME_VMMTYPE_JAVAINTEGER, &rp,
		error);
	rp = bootjar = vrombase + bootjaroff;
	
	/* Check JAR magic number. */
	if ((qq = sjme_vmmreadp(sjme_jvmVMem(jvm), SJME_VMMTYPE_JAVAINTEGER, &rp, error))
		!= SJME_JAR_MAGIC_NUMBER)
	{
		sjme_seterror(error, SJME_ERROR_INVALIDROMMAGIC, qq);
		
		return 0;
	}
	
	/* Ignore numrc, tocoffset, manifestoff, manifestlen. */
	sjme_vmmreadp(sjme_jvmVMem(jvm), SJME_VMMTYPE_JAVAINTEGER, &rp, error);
	sjme_vmmreadp(sjme_jvmVMem(jvm), SJME_VMMTYPE_JAVAINTEGER, &rp, error);
	sjme_vmmreadp(sjme_jvmVMem(jvm), SJME_VMMTYPE_JAVAINTEGER, &rp, error);
	sjme_vmmreadp(sjme_jvmVMem(jvm), SJME_VMMTYPE_JAVAINTEGER, &rp, error);
	
	/* Read boot offset for later. */
	bootoff = sjme_vmmreadp(sjme_jvmVMem(jvm), SJME_VMMTYPE_JAVAINTEGER, &rp, error);
	
	/* Ignore bootsize. */
	sjme_vmmreadp(sjme_jvmVMem(jvm), SJME_VMMTYPE_JAVAINTEGER, &rp, error);
	
	/* Seed initial CPU state. */
	cpu->state.r[SJME_POOL_REGISTER] = vrambase + sjme_vmmreadp(sjme_jvmVMem(jvm),
		SJME_VMMTYPE_JAVAINTEGER, &rp, error);
	cpu->state.r[SJME_STATIC_FIELD_REGISTER] = vrambase +
		sjme_vmmreadp(sjme_jvmVMem(jvm), SJME_VMMTYPE_JAVAINTEGER, &rp, error);
	cpu->state.pc = (bootjar + sjme_vmmreadp(sjme_jvmVMem(jvm),
		SJME_VMMTYPE_JAVAINTEGER, &rp, error));
	
	/* Load system call handler information. */
	sjme_vmmwrite(sjme_jvmVMem(jvm), SJME_VMMTYPE_JAVAINTEGER, jvm->syscallsfp, 0,
		vrambase + sjme_vmmreadp(sjme_jvmVMem(jvm),
			SJME_VMMTYPE_JAVAINTEGER, &rp, error), error);
	sjme_vmmwrite(sjme_jvmVMem(jvm), SJME_VMMTYPE_JAVAINTEGER, jvm->syscallcode, 0,
		bootjar + sjme_vmmreadp(sjme_jvmVMem(jvm),
			SJME_VMMTYPE_JAVAINTEGER, &rp, error), error);
	sjme_vmmwrite(sjme_jvmVMem(jvm), SJME_VMMTYPE_JAVAINTEGER, jvm->syscallpool, 0,
		vrambase + sjme_vmmreadp(sjme_jvmVMem(jvm),
			SJME_VMMTYPE_JAVAINTEGER, &rp, error), error);
	
	/* Bootstrap entry arguments. */
	/* (int __rambase, int __ramsize, int __rombase, int __romsize, */
	/* int __confbase, int __confsize) */
	cpu->state.r[SJME_ARGBASE_REGISTER + 0] = jvm->ram->fakeptr;
	cpu->state.r[SJME_ARGBASE_REGISTER + 1] = jvm->ram->size;
	cpu->state.r[SJME_ARGBASE_REGISTER + 2] = jvm->rom->fakeptr;
	cpu->state.r[SJME_ARGBASE_REGISTER + 3] = jvm->rom->size;
	cpu->state.r[SJME_ARGBASE_REGISTER + 4] = jvm->config->fakeptr;
	cpu->state.r[SJME_ARGBASE_REGISTER + 5] = jvm->config->size;
	
#if defined(SJME_DEBUG)
	fprintf(stderr, "RAM=%08x+%d ROM=%08x+%d CFG=%08x+%d\n",
		(int)jvm->ram->fakeptr, (int)jvm->ram->size,
		(int)jvm->rom->fakeptr, (int)jvm->rom->size,
		(int)jvm->config->fakeptr, (int)jvm->config->size);
#endif
	
	/* Address where the BootRAM is read from. */
	rp = bootjar + bootoff;
	
	/* Copy initial base memory bytes, which is pure big endian. */
	n = sjme_vmmreadp(sjme_jvmVMem(jvm), SJME_VMMTYPE_JAVAINTEGER, &rp, error);
	for (i = 0; i < n; i++)
		sjme_vmmwrite(sjme_jvmVMem(jvm), SJME_VMMTYPE_BYTE, vrambase, i,
			sjme_vmmreadp(sjme_jvmVMem(jvm), SJME_VMMTYPE_BYTE, &rp, error), error);
	
	/* Load all seeds, which restores natural byte order. */
	n = sjme_vmmreadp(sjme_jvmVMem(jvm), SJME_VMMTYPE_JAVAINTEGER, &rp, error);
	for (i = 0; i < n; i++)
	{
		/* Read seed information. */
		seedop = sjme_vmmreadp(sjme_jvmVMem(jvm), SJME_VMMTYPE_BYTE, &rp, error);
		seedsize = (seedop >> SJME_JINT_C(4)) & SJME_JINT_C(0xF);
		seedop = (seedop & SJME_JINT_C(0xF));
		seedaddr = sjme_vmmreadp(sjme_jvmVMem(jvm), SJME_VMMTYPE_JAVAINTEGER, &rp,
			error);
		
		/* Wide value. */
		if (seedsize == 8)
		{
			seedvalh = sjme_vmmreadp(sjme_jvmVMem(jvm), SJME_VMMTYPE_JAVAINTEGER, &rp,
				error);
			seedvall = sjme_vmmreadp(sjme_jvmVMem(jvm), SJME_VMMTYPE_JAVAINTEGER, &rp,
				error);
		}
		
		/* Narrow value. */
		else
			seedvalh = sjme_vmmreadp(sjme_jvmVMem(jvm), sjme_vmmsizetojavatype(
				seedsize, error), &rp, error);
		
		/* Make sure the seed types are correct. */
		if ((seedsize != 1 && seedsize != 2 &&
			seedsize != 4 && seedsize != 8) || 
			(seedop != 0 && seedop != 1 && seedop != 2) ||
			(seedsize == 8 && seedop != 0))
		{
			sjme_seterror(error, SJME_ERROR_INVALIDBOOTRAMSEED,
				seedop | (seedsize << SJME_JINT_C(4)));
			
			return 0;
		}
		
		/* Offset value if it is in RAM or JAR ROM. */
		if (seedop == 1)
			seedvalh += vrambase;
		else if (seedop == 2)
			seedvalh += bootjar;
		
		/* Write long value. */
		if (seedsize == 8)
		{
#if defined(SJME_BIG_ENDIAN)
			sjme_vmmwrite(sjme_jvmVMem(jvm), SJME_VMMTYPE_INTEGER,
				vrambase, seedaddr, seedvalh, error);
			sjme_vmmwrite(sjme_jvmVMem(jvm), SJME_VMMTYPE_INTEGER,
				vrambase + 4, seedaddr, seedvall, error);
#else
			sjme_vmmwrite(sjme_jvmVMem(jvm), SJME_VMMTYPE_INTEGER,
				vrambase, seedaddr, seedvall, error);
			sjme_vmmwrite(sjme_jvmVMem(jvm), SJME_VMMTYPE_INTEGER,
				vrambase + 4, seedaddr, seedvalh, error);
#endif
		}
		
		/* Write narrow value. */
		else
			sjme_vmmwrite(sjme_jvmVMem(jvm), sjme_vmmsizetotype(seedsize, error),
				vrambase, seedaddr, seedvalh, error);
			
#if defined(SJME_DEBUG)
		fprintf(stderr, "SEED op=%d sz=%d -> @%08x+%08x (R@%08x) = %d/%08x\n",
			(int)seedop, (int)seedsize, (int)vrambase, (int)seedaddr,
			(int)(vrambase + seedaddr), (int)seedvalh, (int)seedvalh);
#endif
		
		/* Error was reached? */
		if (error->code != SJME_ERROR_NONE)
			return 0;
	}
	
	/* Check end value. */
	if ((qq = sjme_vmmreadp(sjme_jvmVMem(jvm), SJME_VMMTYPE_JAVAINTEGER, &rp, error))
		!= (~SJME_JINT_C(0)))
	{
		sjme_seterror(error, SJME_ERROR_INVALIDBOOTRAMEND, qq);
		
		return 0;
	}
	
	/* Force failure to happen! */
	if (error->code != SJME_ERROR_NONE)
		return 0;
	
	/* Okay! */
	return 1;
#endif
}
