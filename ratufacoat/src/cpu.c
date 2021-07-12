/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

#include "corefont.h"
#include "cpu.h"
#include "opcode.h"
#include "sjmerc.h"
#include "sjmecon.h"
#include "jvm.h"
#include "softmath.h"
#include "oldstuff.h"
#include "memory.h"

/**
 * Executes single CPU state.
 *
 * @param jvm JVM state.
 * @param cpu CPU state.
 * @param error Execution error.
 * @param cycles The number of cycles to execute, a negative value means
 * forever.
 * @return The number of remaining cycles.
 * @since 2019/06/08
 */
sjme_jint sjme_cpuexec(sjme_jvm* jvm, sjme_cpu* cpu, sjme_error* error,
	sjme_jint cycles)
{
	sjme_jint op, enc;
	sjme_vmemptr nextpc;
	sjme_vmemptr tempp;
	sjme_jint* r;
	sjme_jint ia, ib, ic, id, ie;
	sjme_cpuframe* oldcpu;
	sjme_jlong longcombine;
	
	/* Invalid argument? */
	if (jvm == NULL || cpu == NULL)
	{
		sjme_setError(error, SJME_ERROR_INVALIDARG, 0);
		
		return cycles;
	}
	
	/* Quick register access. */
	r = cpu->state.r;
	
	/* Near-Infinite execution loop. */
	for (;;)
	{
		/* Check if we ran out of cycles. */
		if (cycles >= 0)
		{
			if (cycles == 0)
				break;
			if ((--cycles) <= 0)
				break;
		}
		
		/* Increase total instruction count. */
		sjme_jvmCpuMetrics(jvm)->totalinstructions++;
		
		/* The zero register always must be zero. */
		r[0] = 0;
		
		/* Seed next PC address. */
		nextpc = cpu->state.pc;
		
		/* Read operation and determine encoding. */
		op = (sjme_vmmreadp(sjme_jvmVMem(jvm), SJME_VMMTYPE_BYTE, &nextpc, error) &
			SJME_JINT_C(0xFF));
		enc = ((op >= SJME_ENC_SPECIAL_A) ? op : (op & SJME_ENC_MASK));
		
		/* Temporary debug. */
#if defined(SJME_DEBUG)
		fprintf(stderr,
			"ti=%d tk%d pc=%p op=%X cl=%s mn=%s mt=%s ln=%d jo=%x ja=%d\n",
			jvm->totalinstructions,
			cpu->state.taskid,
			cpu->state.pc,
			(unsigned int)op,
			sjme_vmmresolve(sjme_jvmVMem(jvm), cpu->state.debugclassname, 2, NULL),
			sjme_vmmresolve(sjme_jvmVMem(jvm), cpu->state.debugmethodname, 2, NULL),
			sjme_vmmresolve(sjme_jvmVMem(jvm), cpu->state.debugmethodtype, 2, NULL),
			(int)cpu->state.debugline,
			(unsigned int)cpu->state.debugjop,
			(int)cpu->state.debugjpc);
#endif
		
		/* Depends on the operation. */
		switch (enc)
		{
				/* Compare two register values. */
			case SJME_ENC_IF_ICMP:
				{
					/* Values to compare. */
					ia = r[sjme_opdecodereg(sjme_jvmVMem(jvm), &nextpc, error)];
					ib = r[sjme_opdecodereg(sjme_jvmVMem(jvm), &nextpc, error)];
					
					/* Target PC address. */
					ic = sjme_opdecodejmp(sjme_jvmVMem(jvm), &nextpc, error);
					tempp = cpu->state.pc + ic;
					
					/* Check depends. */
					ic = 0;
					switch (op & SJME_ENC_COMPARE_MASK)
					{
						case SJME_COMPARETYPE_EQUALS:
							if (ia == ib)
								ic = 1;
							break;
							
						case SJME_COMPARETYPE_NOT_EQUALS:
							if (ia != ib)
								ic = 1;
							break;
							
						case SJME_COMPARETYPE_LESS_THAN:
							if (ia < ib)
								ic = 1;
							break;
							
						case SJME_COMPARETYPE_LESS_THAN_OR_EQUALS:
							if (ia <= ib)
								ic = 1;
							break;
							
						case SJME_COMPARETYPE_GREATER_THAN:
							if (ia > ib)
								ic = 1;
							break;
							
						case SJME_COMPARETYPE_GREATER_THAN_OR_EQUALS:
							if (ia >= ib)
								ic = 1;
							break;
							
						case SJME_COMPARETYPE_TRUE:
							ic = 1;
							break;
							
						case SJME_COMPARETYPE_FALSE:
							ic = 0;
							break;
					}
					
					/* Branch success? */
					if (ic != 0)
						nextpc = tempp;
				}
				break;
				
				/* Math. */
			case SJME_ENC_MATH_REG_INT:
			case SJME_ENC_MATH_CONST_INT:
				{
					/* A Value. */
					ia = r[sjme_opdecodereg(sjme_jvmVMem(jvm), &nextpc, error)];
					
					/* B value. */
					if (enc == SJME_ENC_MATH_CONST_INT)
						ib = sjme_opdecodejint(sjme_jvmVMem(jvm), &nextpc, error);
					else
						ib = r[sjme_opdecodereg(sjme_jvmVMem(jvm), &nextpc, error)];
					
					/* Perform the math. */
					switch (op & SJME_ENC_MATH_MASK)
					{
						case SJME_MATH_ADD:
							ic = ia + ib;
							break;
							
						case SJME_MATH_SUB:
							ic = ia - ib;
							break;
							
						case SJME_MATH_MUL:
							ic = ia * ib;
							break;
							
						case SJME_MATH_DIV:
							ic = sjme_divInt(ia, ib).quot;
							break;
							
						case SJME_MATH_REM:
							ic = sjme_divInt(ia, ib).rem;
							break;
							
						case SJME_MATH_NEG:
							ic = -ia;
							break;
							
						case SJME_MATH_SHL:
							/* Shift is truncated. */
							ib = (ib & SJME_JINT_C(0x1F));
							
							/* Shifting values off the type is undefined, */
							/* so only keep the part of the value which is */
							/* not shifted off! */
							if (ib == 0)
								ic = ia;
							else
								ic = ((ia & sjme_sh_lmask[ib]) << ib);
							break;
							
						case SJME_MATH_SHR:
						case SJME_MATH_USHR:
							/* Shift is truncated. */
							ib = (ib & SJME_JINT_C(0x1F));
							
							/* Shifting values off the type is undefined, */
							/* so only keep the part of the value which is */
							/* not shifted off! */
							if (ib == 0)
								ic = ia;
							else
								ic = (((ia & sjme_sh_umask[ib])) >> ib);
							
							/* Mask in or mask out the dragged sign bit. */
							if (((ia & SJME_JINT_C(0x80000000)) != 0) &&
								((op & SJME_ENC_MATH_MASK) == SJME_MATH_SHR))
								ic |= sjme_sh_umask[ib];
							else
								ic &= sjme_sh_lmask[31 - ib];
							break;
							
						case SJME_MATH_AND:
							ic = ia & ib;
							break;
							
						case SJME_MATH_OR:
							ic = ia | ib;
							break;
							
						case SJME_MATH_XOR:
							ic = ia ^ ib;
							break;
							
						case SJME_MATH_SIGNX8:
							if (ia & SJME_JINT_C(0x80))
								ic = (ia | SJME_JINT_C(0xFFFFFF00));
							else
								ic = (ia & SJME_JINT_C(0x000000FF));
							break;
							
						case SJME_MATH_SIGNX16:
							if (ia & SJME_JINT_C(0x8000))
								ic = (ia | SJME_JINT_C(0xFFFF0000));
							else
								ic = (ia & SJME_JINT_C(0x0000FFFF));
							break;
						
						case SJME_MATH_CMPL:
						case SJME_MATH_CMPG:
							ic = (ia < ib ? SJME_JINT_C(-1) :
								(ia > ib ? SJME_JINT_C(1) : SJME_JINT_C(0)));
							break;
					}
					
					/* Store result. */
					r[sjme_opdecodereg(sjme_jvmVMem(jvm), &nextpc, error)] = ic;
				}
				break;
				
				/* Memory (native byte order). */
			case SJME_ENC_MEMORY_OFF_REG:
			case SJME_ENC_MEMORY_OFF_ICONST:
			case SJME_ENC_MEMORY_OFF_REG_JAVA:
			case SJME_ENC_MEMORY_OFF_ICONST_JAVA:
				{
					/* Destination/source register. */
					ic = sjme_opdecodereg(sjme_jvmVMem(jvm), &nextpc, error);
					
					/* The address and offset to access. */
					ia = r[sjme_opdecodereg(sjme_jvmVMem(jvm), &nextpc, error)];
					if (enc == SJME_ENC_MEMORY_OFF_ICONST ||
						enc == SJME_ENC_MEMORY_OFF_ICONST_JAVA)
						ib = sjme_opdecodejint(sjme_jvmVMem(jvm), &nextpc, error);
					else
						ib = r[sjme_opdecodereg(sjme_jvmVMem(jvm), &nextpc, error)];
					tempp = ia;
					
					/* Java types? */
					id = 0;
					if (enc == SJME_ENC_MEMORY_OFF_REG_JAVA ||
						enc == SJME_ENC_MEMORY_OFF_ICONST_JAVA)
						switch (op & SJME_MEM_DATATYPE_MASK)
						{
							case SJME_DATATYPE_BYTE:
								id = SJME_VMMTYPE_BYTE;
								break;
							
							case SJME_DATATYPE_CHARACTER:
							case SJME_DATATYPE_SHORT:
								id = SJME_VMMTYPE_JAVASHORT;
								break;
							
							case SJME_DATATYPE_OBJECT:
							case SJME_DATATYPE_INTEGER:
							case SJME_DATATYPE_FLOAT:
								id = SJME_VMMTYPE_JAVAINTEGER;
								break;
						}
					
					/* Native types? */
					else
						switch (op & SJME_MEM_DATATYPE_MASK)
						{
							case SJME_DATATYPE_BYTE:
								id = SJME_VMMTYPE_BYTE;
								break;
							
							case SJME_DATATYPE_CHARACTER:
							case SJME_DATATYPE_SHORT:
								id = SJME_VMMTYPE_SHORT;
								break;
							
							case SJME_DATATYPE_OBJECT:
							case SJME_DATATYPE_INTEGER:
							case SJME_DATATYPE_FLOAT:
								id = SJME_VMMTYPE_INTEGER;
								break;
						}
						
					/* Load value */
					if ((op & SJME_MEM_LOAD_MASK) != 0)
					{
						/* Read. */
						r[ic] = sjme_vmmread(sjme_jvmVMem(jvm), id, tempp, ib, error);
						
						/* Mask character? */
						if ((op & SJME_MEM_DATATYPE_MASK) ==
							SJME_DATATYPE_CHARACTER)
							r[ic] = r[ic] & SJME_JINT_C(0xFFFF);
							
#if defined(SJME_DEBUG)
						fprintf(stderr, "r[%d] = *(%08x + %d) = %d/%08x\n",
							(int)ic, (int)tempp, (int)ib,
							(int)r[ic], (int)r[ic]);
#endif
					}
					
					/* Store value */
					else
					{
						sjme_vmmwrite(sjme_jvmVMem(jvm), id, tempp, ib, r[ic], error);
						
#if defined(SJME_DEBUG)
						fprintf(stderr, "*(%08x + %d) = r[%d] = %d/%08x\n",
							(int)tempp, (int)ib, (int)ic,
							(int)r[ic], (int)r[ic]);
#endif
					}
				}
				break;
				
				/* Atomic compare, get, and set. */
			case SJME_OP_ATOMIC_COMPARE_GET_AND_SET:
				{
					/* Check. */
					ia = r[sjme_opdecodereg(sjme_jvmVMem(jvm), &nextpc, error)];
					
					/* Get. */
					ib = sjme_opdecodereg(sjme_jvmVMem(jvm), &nextpc, error);
					
					/* Set. */
					ic = r[sjme_opdecodereg(sjme_jvmVMem(jvm), &nextpc, error)];
					
					/* Address. */
					id = r[sjme_opdecodereg(sjme_jvmVMem(jvm), &nextpc, error)];
					
					/* Offset. */
					ie = sjme_opdecodeui(sjme_jvmVMem(jvm), &nextpc, error);
					
					/* Perform the operation. */
					r[ib] = sjme_vmmatomicintcheckgetandset(
						sjme_jvmVMem(jvm), ia, ic, id, ie, error);
				}
				break;
				
				/* Atomic decrement and get. */
			case SJME_OP_ATOMIC_INT_DECREMENT_AND_GET:
				{
					/* Target register. */
					id = sjme_opdecodereg(sjme_jvmVMem(jvm), &nextpc, error);
					
					/* Load address and offset. */
					ia = r[sjme_opdecodereg(sjme_jvmVMem(jvm), &nextpc, error)];
					ib = sjme_opdecodeui(sjme_jvmVMem(jvm), &nextpc, error);
					
					/* Modify the value accordingly. */
					r[id] = sjme_vmmatomicintaddandget(sjme_jvmVMem(jvm), ia, ib,
						SJME_JINT_C(-1), error);
				}
				break;
				
				/* Atomic increment. */
			case SJME_OP_ATOMIC_INT_INCREMENT:
				{
					/* Load address and offset. */
					ia = r[sjme_opdecodereg(sjme_jvmVMem(jvm), &nextpc, error)];
					ib = sjme_opdecodeui(sjme_jvmVMem(jvm), &nextpc, error);
					
					/* Modify the value accordingly. */
					sjme_vmmatomicintaddandget(sjme_jvmVMem(jvm), ia, ib,
						SJME_JINT_C(1), error);
				}
				break;
				
				/* Breakpoint, only if debugging enabled. */
			case SJME_OP_BREAKPOINT:
				if (sjme_jvmIsDebug(jvm) != 0)
				{
					sjme_setError(error, SJME_ERROR_CPUBREAKPOINT,
								  sjme_jvmCpuMetrics(jvm)->totalinstructions);
					
					return cycles;
				}
				
				break;
			
				/* Copy value. */
			case SJME_OP_COPY:
				{
					ia = sjme_opdecodereg(sjme_jvmVMem(jvm), &nextpc, error);
					ib = sjme_opdecodereg(sjme_jvmVMem(jvm), &nextpc, error);
					
					r[ib] = r[ia];
				}
				break;
			
				/* Debug entry. */
			case SJME_OP_DEBUG_ENTRY:
				{
					tempp = r[SJME_POOL_REGISTER];
					
					/* Get pointers to the real values. */
					cpu->state.debugclassname = sjme_vmmread(sjme_jvmVMem(jvm),
						SJME_VMMTYPE_INTEGER, tempp, sjme_opdecodeui(sjme_jvmVMem(jvm),
						&nextpc, error) * SJME_JINT_C(4), error);
					cpu->state.debugmethodname = sjme_vmmread(sjme_jvmVMem(jvm),
						SJME_VMMTYPE_INTEGER, tempp, sjme_opdecodeui(sjme_jvmVMem(jvm),
						&nextpc, error) * SJME_JINT_C(4), error);
					cpu->state.debugmethodtype = sjme_vmmread(sjme_jvmVMem(jvm),
						SJME_VMMTYPE_INTEGER, tempp, sjme_opdecodeui(sjme_jvmVMem(jvm),
						&nextpc, error) * SJME_JINT_C(4), error);
					cpu->state.debugsourcefile = sjme_vmmread(sjme_jvmVMem(jvm),
						SJME_VMMTYPE_INTEGER, tempp, sjme_opdecodeui(sjme_jvmVMem(jvm),
						&nextpc, error) * SJME_JINT_C(4), error);
				}
				break;
				
				/* Exit method. */
			case SJME_OP_DEBUG_EXIT:
				break;
				
				/* Debug point. */
			case SJME_OP_DEBUG_POINT:
				{
					cpu->state.debugline =
						sjme_opdecodeui(sjme_jvmVMem(jvm), &nextpc, error);
					cpu->state.debugjop =
						(sjme_opdecodeui(sjme_jvmVMem(jvm), &nextpc, error) &
						SJME_JINT_C(0xFF));
					cpu->state.debugjpc =
						sjme_opdecodeui(sjme_jvmVMem(jvm), &nextpc, error);
				}
				break;
				
				/* If equal to constant? */
			case SJME_OP_IFEQ_CONST:
				{
					/* A value. */
					ia = r[sjme_opdecodereg(sjme_jvmVMem(jvm), &nextpc, error)];
					
					/* B value. */
					ib = sjme_opdecodejint(sjme_jvmVMem(jvm), &nextpc, error);
					
					/* Target PC address. */
					ic = sjme_opdecodejmp(sjme_jvmVMem(jvm), &nextpc, error);
					tempp = cpu->state.pc + ic;
					
					/* Jump on equals? */
					if (ia == ib)
						nextpc = tempp;
				}
				break;
				
				/* Invoke method. */
			case SJME_OP_INVOKE:
				{
					/* Allocate to store old CPU state. */
					oldcpu = sjme_malloc(sizeof(*oldcpu));
					if (oldcpu == NULL)
					{
						sjme_setError(error, SJME_ERROR_NO_MEMORY,
									  sizeof(*oldcpu));
						
						return cycles;
					}
					
					/* Copy and store state. */
					*oldcpu = cpu->state;
					cpu->state.parent = oldcpu;
					
					/* Setup CPU state for invoke run, move pool up. */
					for (ia = SJME_LOCAL_REGISTER_BASE;
						ia < SJME_MAX_REGISTERS; ia++)
						r[ia] = 0;
					r[SJME_POOL_REGISTER] = oldcpu->r[SJME_NEXT_POOL_REGISTER];
					r[SJME_NEXT_POOL_REGISTER] = 0;
					
					/* Inherit the task ID. */
					cpu->state.taskid = oldcpu->taskid;
					
					/* The address to execute. */
					ia = oldcpu->r[
						sjme_opdecodereg(sjme_jvmVMem(jvm), &nextpc, error)];
					
					/* Load in register list (wide). */
					ib = sjme_vmmreadp(sjme_jvmVMem(jvm), SJME_VMMTYPE_BYTE, &nextpc,
						error);
					if ((ib & SJME_JINT_C(0x80)) != 0)
					{
						/* Skip back and read lower value. */
						nextpc--;
						ib = sjme_opdecodejshort(sjme_jvmVMem(jvm), &nextpc, error);
						
						/* Read values. */
						for (ic = 0; ic < ib; ic++)
							r[SJME_ARGBASE_REGISTER + ic] = oldcpu->r[
								sjme_opdecodejshort(sjme_jvmVMem(jvm), &nextpc,
									error)];
					}
					
					/* Narrow format list. */
					else
					{
						/* Read values. */
						for (ic = 0; ic < ib; ic++)
							r[SJME_ARGBASE_REGISTER + ic] =
								oldcpu->r[sjme_vmmreadp(sjme_jvmVMem(jvm),
									SJME_VMMTYPE_BYTE, &nextpc, error)];
					}
					
#if defined(SJME_DEBUG)
					fprintf(stderr, "Invoke %08x (", (int)ia);
					for (ic = 0; ic < ib; ic++)
					{
						if (ic > 0)
							fprintf(stderr, ", ");
						fprintf(stderr, "%d/%08x",
							(int)r[SJME_ARGBASE_REGISTER + ic],
							(int)r[SJME_ARGBASE_REGISTER + ic]);
					}
					fprintf(stderr, ")\n");
#endif
					
					/* Old PC address resumes where this read ended. */
					oldcpu->pc = nextpc;
					
					/* Our next PC becomes the target address. */
					nextpc = ia;
					cpu->state.pc = nextpc;
				}
				break;
				
				/* Load value from integer array. */
			case SJME_OP_LOAD_FROM_INTARRAY:
				{
					/* Destination register. */
					ic = sjme_opdecodereg(sjme_jvmVMem(jvm), &nextpc, error);
					
					/* Address and index */
					ia = r[sjme_opdecodereg(sjme_jvmVMem(jvm), &nextpc, error)];
					ib = r[sjme_opdecodereg(sjme_jvmVMem(jvm), &nextpc, error)];
					
					/* Load from array. */
					r[ic] = sjme_vmmread(sjme_jvmVMem(jvm), SJME_VMMTYPE_INTEGER,
						ia, SJME_ARRAY_BASE_SIZE + (ib * SJME_JINT_C(4)),
						error);
				}
				break;
				
				/* Load value from constant pool. */
			case SJME_OP_LOAD_POOL:
				{
					/* The index to read from. */
					ia = sjme_opdecodeui(sjme_jvmVMem(jvm), &nextpc, error);
					
					/* Write into destination register. */
					r[(ib = sjme_opdecodereg(sjme_jvmVMem(jvm), &nextpc, error))] = 
						sjme_vmmread(sjme_jvmVMem(jvm), SJME_VMMTYPE_INTEGER,
							r[SJME_POOL_REGISTER], (ia * SJME_JINT_C(4)),
							error);
					
#if defined(SJME_DEBUG)
					fprintf(stderr, "Load pool %d -> %d/%08x\n",
						(int)ia, (int)r[ib], (int)r[ib]);
#endif
				}
				break;
				
				/* Return from method. */
			case SJME_OP_RETURN:
				{
					/* Get parent CPU state. */
					oldcpu = cpu->state.parent;
					
					/* Exit must be done through an exit system call! */
					if (oldcpu == NULL)
					{
						sjme_setError(error, SJME_ERROR_THREADRETURN,
									  sjme_jvmCpuMetrics(
											  jvm)->totalinstructions);
						
						return cycles;
					}
					
					/* Copy global values back. */
					for (ia = 0; ia < SJME_LOCAL_REGISTER_BASE; ia++)
						oldcpu->r[ia] = cpu->state.r[ia];
					
					/* Completely restore the old state. */
					cpu->state = *oldcpu;
					
					/* Restore continuing PC address. */
					nextpc = cpu->state.pc;
					
					/* Free the parent as it is not needed. */
					sjme_free(oldcpu);
					
#if defined(SJME_DEBUG)
					fprintf(stderr, "Return: %d/%08x\n",
						(int)r[SJME_RETURN_REGISTER],
						(int)r[SJME_RETURN_REGISTER]);
#endif
				}
				break;
				
				/* Store to constant pool. */
			case SJME_OP_STORE_POOL:
				{
					/* The index to read from. */
					ia = sjme_opdecodeui(sjme_jvmVMem(jvm), &nextpc, error);
					
					/* Read from destination register. */
					sjme_vmmwrite(sjme_jvmVMem(jvm), SJME_VMMTYPE_INTEGER,
							r[SJME_POOL_REGISTER], (ia * SJME_JINT_C(4)),
							r[(ib = sjme_opdecodereg(sjme_jvmVMem(jvm),
								&nextpc, error))],
							error);
					
#if defined(SJME_DEBUG)
					fprintf(stderr, "Store pool %d <- %d/%08x\n",
						(int)ia, (int)r[ib], (int)r[ib]);
#endif
				}
				break;
				
				/* Store value to integer array. */
			case SJME_OP_STORE_TO_INTARRAY:
				{
					/* Source register. */
					ic = sjme_opdecodereg(sjme_jvmVMem(jvm), &nextpc, error);
					
					/* Address and index */
					ia = r[sjme_opdecodereg(sjme_jvmVMem(jvm), &nextpc, error)];
					ib = r[sjme_opdecodereg(sjme_jvmVMem(jvm), &nextpc, error)];
					
					/* Store to array. */
					sjme_vmmwrite(sjme_jvmVMem(jvm), SJME_VMMTYPE_INTEGER,
						ia, SJME_ARRAY_BASE_SIZE + (ib * SJME_JINT_C(4)),
						r[ic], error);
				}
				break;
				
				/* System call. */
			case SJME_OP_SYSTEM_CALL:
				{
					/* Clear system call arguments. */
					for (ia = 0; ia < SJME_MAX_SYSCALLARGS; ia++)
						cpu->syscallargs[ia] = 0;
					
					/* Load call type. */
					ia = r[sjme_opdecodereg(sjme_jvmVMem(jvm), &nextpc, error)];
					
					/* Load call arguments. */
					ic = sjme_opdecodeui(sjme_jvmVMem(jvm), &nextpc, error);
					for (ib = 0; ib < ic; ib++)
					{
						/* Get value. */
						id = r[sjme_opdecodereg(sjme_jvmVMem(jvm), &nextpc, error)];
						
						/* Set but never exceed the system call limit. */
						if (ib < SJME_MAX_SYSCALLARGS)
							cpu->syscallargs[ib] = id;
					}
					
					/* Call it and place result into the return register. */
					/* IPC Exceptions are not forwarded to supervisor. */
					/* IPC Calls are always virtualized even in supervisor. */
					if ((cpu->state.taskid == 0 ||
						ia == SJME_SYSCALL_EXCEPTION_LOAD ||
						ia == SJME_SYSCALL_EXCEPTION_STORE) &&
						ia != SJME_SYSCALL_IPC_CALL)
					{
						/* Reset */
						longcombine.lo = 0;
						longcombine.hi = 0;
						
						/* Perform the system call. */
						sjme_syscall(jvm, cpu, error, ia, cpu->syscallargs,
							&longcombine);
						
						/* Extract result. */
						r[SJME_RETURN_REGISTER] = longcombine.lo;
						r[SJME_RETURN_REGISTER + 1] = longcombine.hi;
					}
					
					/* Otherwise perform supervisor handling of the call. */
					else
					{
						/* Allocate to store old CPU state. */
						oldcpu = sjme_malloc(sizeof(*oldcpu));
						if (oldcpu == NULL)
						{
							sjme_setError(error, SJME_ERROR_NO_MEMORY,
										  sizeof(*oldcpu));
							
							return cycles;
						}
						
						/* Copy and store state. */
						*oldcpu = cpu->state;
						cpu->state.parent = oldcpu;
						
						/* Old PC address resumes where this ended. */
						oldcpu->pc = nextpc;
						
						/* Go back to the supervisor task. */
						cpu->state.taskid = 0;
						
						/* Setup arguments for this call. */
						r[SJME_POOL_REGISTER] = cpu->supervisorprops[
							SJME_SUPERPROP_TASK_SYSCALL_METHOD_POOL_POINTER];
						r[SJME_STATIC_FIELD_REGISTER] = cpu->supervisorprops[
							SJME_SUPERPROP_TASK_SYSCALL_STATIC_FIELD_POINTER];
						r[SJME_ARGBASE_REGISTER + 0] =
							oldcpu->taskid;
						r[SJME_ARGBASE_REGISTER + 1] =
							oldcpu->r[SJME_STATIC_FIELD_REGISTER];
						r[SJME_ARGBASE_REGISTER + 2] =
							ia;
						
						/* And arguments for it as well. */
						for (ie = 0; ie < SJME_MAX_SYSCALLARGS; ie++)
							r[SJME_ARGBASE_REGISTER + 3 + ie] =
								cpu->syscallargs[ie];
						
						/* Our next PC is the handler address. */
						nextpc = cpu->supervisorprops[
							SJME_SUPERPROP_TASK_SYSCALL_METHOD_HANDLER];
						cpu->state.pc = nextpc;
					}
					
					/* Stop if an error was set. */
					if (error->code != SJME_ERROR_NONE)
						return cycles;
				}
				break;
			
				/* Invalid operation. */
			default:
				sjme_setError(error, SJME_ERROR_INVALIDOP, op);
				
				return cycles;
		}
		
		/* Check for error. */
		if (error != NULL)
			if (error->code != SJME_ERROR_NONE)
				return cycles;
		
		/* Set next PC address. */
		cpu->state.pc = nextpc;
	}
	
	/* Return remaining cycles. */
	return cycles;
}

