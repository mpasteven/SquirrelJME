/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

/**
 * CPU Support.
 * 
 * @since 2021/02/27
 */

#ifndef SQUIRRELJME_CPU_H
#define SQUIRRELJME_CPU_H

#include "sjmerc.h"
#include "error.h"
#include "sjmecon.h"

/* Anti-C++. */
#ifdef __cplusplus
#ifndef SJME_CXX_IS_EXTERNED
#define SJME_CXX_IS_EXTERNED
#define SJME_CXX_SQUIRRELJME_CPU_H
extern "C"
{
#endif /* #ifdef SJME_CXX_IS_EXTERNED */
#endif /* #ifdef __cplusplus */

/*--------------------------------------------------------------------------*/

/**
 * This contains the state of a single CPU frame.
 *
 * @since 2019/06/27
 */
typedef struct sjme_cpuframe sjme_cpuframe;

/** Virtual CPU. */
typedef struct sjme_cpu sjme_cpu;

/** Maximum CPU registers. */
#define SJME_MAX_REGISTERS SJME_JINT_C(64)

/** Maximum system call arguments. */
#define SJME_MAX_SYSCALLARGS SJME_JINT_C(8)

struct sjme_cpuframe
{
	/** PC. */
	sjme_vmemptr pc;
	
	/** Registers. */
	sjme_jint r[SJME_MAX_REGISTERS];
	
	/** Debug: Class name. */
	sjme_vmemptr debugclassname;
	
	/** Debug: Method name. */
	sjme_vmemptr debugmethodname;
	
	/** Debug: Method type. */
	sjme_vmemptr debugmethodtype;
	
	/** Debug: Source file. */
	sjme_vmemptr debugsourcefile;
	
	/** Debug: Current line. */
	sjme_jint debugline;
	
	/** Debug: Java Operation. */
	sjme_jint debugjop;
	
	/** Debug: Java Address. */
	sjme_jint debugjpc;
	
	/** Debug: The Task ID. */
	sjme_jint taskid;
	
	/** The parent CPU state. */
	sjme_cpuframe* parent;
};

struct sjme_cpu
{
	/** The state of this thread. */
	sjme_jint threadstate;
	
	/* System call arguments. */
	sjme_jint syscallargs[SJME_MAX_SYSCALLARGS];
	
	/* System call error numbers. */
	sjme_jint syscallerr[SJME_SYSCALL_NUM_SYSCALLS];
	
	/* Supervisor properties. */
	sjme_jint supervisorprops[SJME_SUPERPROP_NUM_PROPERTIES];
	
	/* Current CPU state. */
	sjme_cpuframe state;
	
	/** Stored IPC Exception. */
	sjme_jint ipcexception;
};

/**
 * Metrics for the CPU.
 * 
 * @since 2021/02/28
 */
typedef struct sjme_cpuMetrics
{
	/** Total instruction count. */
	sjme_jint totalinstructions;
} sjme_cpuMetrics;

/*--------------------------------------------------------------------------*/

/* Anti-C++. */
#ifdef __cplusplus
#ifdef SJME_CXX_SQUIRRELJME_CPU_H
}
#undef SJME_CXX_SQUIRRELJME_CPU_H
#undef SJME_CXX_IS_EXTERNED
#endif /* #ifdef SJME_CXX_SQUIRRELJME_CPU_H */
#endif /* #ifdef __cplusplus */

#endif /* SQUIRRELJME_CPU_H */
