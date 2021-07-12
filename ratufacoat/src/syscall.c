/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// --------------------------------------------------------------------------*/

#include "debug.h"
#include "syscall.h"

void sjme_syscall(sjme_jvm* jvm, sjme_cpu* cpu, sjme_error* error,
	sjme_jshort callid, sjme_jint* args, sjme_jlong* rv)
{
	sjme_todo("sjme_syscall()");
#if 0
	
	sjme_jint* syserr;
	sjme_jint ia, ib, ic;
	sjme_jbyte ba;
	sjme_vmemptr pa;
	sjme_cpuframe* cpustate;
	
	/* Called wrong? */
	if (jvm == NULL || cpu == NULL || args == NULL || rv == NULL)
	{
		sjme_seterror(error, SJME_ERROR_INVALIDARG, 0);
		
		return;
	}
	
	/* Start here. */
	cpustate = &cpu->state;
	
	/* Calculate index to set for system call errors. */
	syserr = ((callid < 0 || callid >= SJME_SYSCALL_NUM_SYSCALLS) ?
		&cpu->syscallerr[SJME_SYSCALL_QUERY_INDEX] : &cpu->syscallerr[callid]);
	
	/* Depends on the system call. */
	switch (callid)
	{
			/* Query support for system call. */
		case SJME_SYSCALL_QUERY_INDEX:
			*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
			switch (args[0])
			{
				case SJME_SYSCALL_BYTE_ORDER_LITTLE:
				case SJME_SYSCALL_CALL_STACK_HEIGHT:
				case SJME_SYSCALL_CALL_STACK_ITEM:
				case SJME_SYSCALL_ERROR_GET:
				case SJME_SYSCALL_ERROR_SET:
				case SJME_SYSCALL_EXCEPTION_LOAD:
				case SJME_SYSCALL_EXCEPTION_STORE:
				case SJME_SYSCALL_FRAMEBUFFER_PROPERTY:
				case SJME_SYSCALL_TIME_MILLI_WALL:
				case SJME_SYSCALL_TIME_NANO_MONO:
				case SJME_SYSCALL_MEM_SET:
				case SJME_SYSCALL_MEM_SET_INT:
				case SJME_SYSCALL_OPTION_JAR_DATA:
				case SJME_SYSCALL_OPTION_JAR_SIZE:
				case SJME_SYSCALL_PD_OF_STDERR:
				case SJME_SYSCALL_PD_OF_STDOUT:
				case SJME_SYSCALL_PD_WRITE_BYTE:
				case SJME_SYSCALL_SQUELCH_FB_CONSOLE:
				case SJME_SYSCALL_SUPERVISOR_BOOT_OKAY:
				case SJME_SYSCALL_SUPERVISOR_PROPERTY_GET:
				case SJME_SYSCALL_SUPERVISOR_PROPERTY_SET:
				case SJME_SYSCALL_FRAME_TASK_ID_GET:
				case SJME_SYSCALL_FRAME_TASK_ID_SET:
					rv->lo = SJME_JINT_C(1);
					return;
			}
			
			rv->lo = SJME_JINT_C(0);
			return;
			
			/* Is the byte order little endian? */
		case SJME_SYSCALL_BYTE_ORDER_LITTLE:
			*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
#if defined(SJME_LITTLE_ENDIAN)
			rv->lo = 1;
#else
			rv->lo = 0;
#endif
			break;
			
			/* Height of the call stack. */
		case SJME_SYSCALL_CALL_STACK_HEIGHT:
			/* Count trace depth. */
			ia = 0;
			while (cpustate != NULL)
			{
				/* Increase the count. */
				ia++;
				
				/* Go to deeper depth. */
				cpustate = cpustate->parent;
			}
			
			/* Does not generate errors. */
			*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
			rv->lo = ia;
			return;
			
			/* Item within the call stack. */
		case SJME_SYSCALL_CALL_STACK_ITEM:
			/* Find the CPU frame to use. */
			ia = args[0];
			while (ia > 0)
			{
				/* End of CPU? */
				if (cpustate == NULL)
				{
					*syserr = SJME_SYSCALL_ERROR_VALUE_OUT_OF_RANGE;
					return;
				}
				
				/* Drop down. */
				cpustate = cpustate->parent;
				ia--;
			}
			
			/* Depends on the requested item.*/
			switch (args[1])
			{
					/* The class name. */
				case SJME_CALLSTACKITEM_CLASS_NAME:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
					rv->lo = cpustate->debugclassname;
					break;
					
					/* The method name. */
				case SJME_CALLSTACKITEM_METHOD_NAME:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
					rv->lo = cpustate->debugmethodname;
					break;
					
					/* The method type. */
				case SJME_CALLSTACKITEM_METHOD_TYPE:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
					rv->lo = cpustate->debugmethodtype;
					break;
					
					/* Source file. */
				case SJME_CALLSTACKITEM_SOURCE_FILE:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
					rv->lo = cpustate->debugsourcefile;
					break;
					
					/* Source line. */
				case SJME_CALLSTACKITEM_SOURCE_LINE:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
					rv->lo = cpustate->debugline;
					break;
					
					/* The PC address. */
				case SJME_CALLSTACKITEM_PC_ADDRESS:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
					rv->lo = cpustate->pc;
					break;
					
					/* Java operation. */
				case SJME_CALLSTACKITEM_JAVA_OPERATION:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
					rv->lo = cpustate->debugjop;
					break;
					
					/* Java PC address. */
				case SJME_CALLSTACKITEM_JAVA_PC_ADDRESS:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
					rv->lo = cpustate->debugjpc;
					break;
					
					/* Current Task ID. */
				case SJME_CALLSTACKITEM_TASK_ID:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
					rv->lo = cpustate->taskid;
					break;
					
					/* Unknown. */
				default:
					*syserr = SJME_SYSCALL_ERROR_VALUE_OUT_OF_RANGE;
					return;
			}
			return;
			
			/* Get error state. */
		case SJME_SYSCALL_ERROR_GET:
			*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
			
			ia = args[0];
			if (ia < 0 || ia >= SJME_SYSCALL_NUM_SYSCALLS)
				ia = SJME_SYSCALL_QUERY_INDEX;
			
			rv->lo = cpu->syscallerr[ia];
			return;
			
			/* Set error state, return old one. */
		case SJME_SYSCALL_ERROR_SET:
			*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
			
			ia = args[0];
			if (ia < 0 || ia >= SJME_SYSCALL_NUM_SYSCALLS)
				ia = SJME_SYSCALL_QUERY_INDEX;
			
			ib = cpu->syscallerr[ia];
			cpu->syscallerr[ia] = args[1];
			
			rv->lo = ib;
			return;
			
			/* Load IPC Exception. */
		case SJME_SYSCALL_EXCEPTION_LOAD:
			*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
			rv->lo = cpu->ipcexception;
			return;
		
			/* Store IPC Exception */
		case SJME_SYSCALL_EXCEPTION_STORE:
			*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
			rv->lo = cpu->ipcexception;
			cpu->ipcexception = args[0];
			return;
			
			/* Gets/sets property of the framebuffer. */
		case SJME_SYSCALL_FRAMEBUFFER_PROPERTY:
			/* No framebuffer is defined? */
			if (sjme_jvmFramebuffer(jvm) == NULL)
			{
				*syserr = SJME_SYSCALL_ERROR_NO_FRAMEBUFFER;
				return;
			}
			
			/* Depends on the property. */
			switch (args[0])
			{
					/* Framebuffer address. */
				case SJME_FB_CONTROL_ADDRESS:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
					rv->lo = sjme_jvmFramebuffer(jvm)->framebuffer->fakeptr;
					break;
					
					/* Width of the framebuffer. */
				case SJME_FB_CONTROL_WIDTH:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
					rv->lo = sjme_jvmFramebuffer(jvm)->width;
					break;
					
					/* Height of the framebuffer. */
				case SJME_FB_CONTROL_HEIGHT:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
					rv->lo = sjme_jvmFramebuffer(jvm)->height;
					break;
					
					/* Scanline length of the framebuffer. */
				case SJME_FB_CONTROL_SCANLEN:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
					rv->lo = sjme_jvmFramebuffer(jvm)->scanlen;
					break;
					
					/* Flush the framebuffer. */
				case SJME_FB_CONTROL_FLUSH:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
					if (sjme_jvmFramebuffer(jvm)->flush != NULL)
						sjme_jvmFramebuffer(jvm)->flush();
					break;
					
					/* Frame-buffer format. */
				case SJME_FB_CONTROL_FORMAT:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
					rv->lo = sjme_jvmFramebuffer(jvm)->format;
					break;
					
					/* Scanline length in bytes. */
				case SJME_FB_CONTROL_SCANLEN_BYTES:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
					rv->lo = sjme_jvmFramebuffer(jvm)->scanlenbytes;
					break;
					
					/* Bytes per pixel. */
				case SJME_FB_CONTROL_BYTES_PER_PIXEL:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
					rv->lo = sjme_jvmFramebuffer(jvm)->bitsperpixel / 8;
					break;
					
					/* The number of pixels. */
				case SJME_FB_CONTROL_NUM_PIXELS:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
					rv->lo = sjme_jvmFramebuffer(jvm)->numpixels;
					break;
					
					/* Bits per pixels. */
				case SJME_FB_CONTROL_BITS_PER_PIXEL:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
					rv->lo = sjme_jvmFramebuffer(jvm)->bitsperpixel;
					break;
				
					/* Unknown property, but there is a framebuffer. */
				default:
					*syserr = SJME_SYSCALL_ERROR_VALUE_OUT_OF_RANGE;
					return;
			}
			
			return;
			
			/* Get Task ID. */
		case SJME_SYSCALL_FRAME_TASK_ID_GET:
			*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
			rv->lo = cpustate->taskid;
			return;
			
			/* Set Task ID. */
		case SJME_SYSCALL_FRAME_TASK_ID_SET:
			cpustate->taskid = args[0];
			*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
			rv->lo = SJME_JINT_C(1);
			return;
			
			/* Set memory to byte value. */
		case SJME_SYSCALL_MEM_SET:
			/* Get address to wipe. */
			pa = args[0];
			
			/* The value to store. */
			ic = args[1] & SJME_JINT_C(0xFF);
			
			/* Wipe these values! */
			ib = args[2];
			for (ia = 0; ia < ib; ia++)
				sjme_vmmwrite(sjme_jvmVMem(jvm), SJME_VMMTYPE_BYTE, pa, ia, ic, error);
			
			/* Is okay. */
			*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
			rv->lo = ib;
			return;
		
			/* Set memory in integer values. */
		case SJME_SYSCALL_MEM_SET_INT:
			/* Get address to wipe. */
			pa = args[0];
			
			/* The value to store, is full integer. */
			ic = args[1];
			
			/* Wipe these values! */
			ib = args[2] & ~SJME_JINT_C(3);
			for (ia = 0; ia < ib; ia += 4)
				sjme_vmmwrite(sjme_jvmVMem(jvm), SJME_VMMTYPE_INTEGER, pa, ia, ic,
					error);
			
			/* Is okay. */
			*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
			rv->lo = ib;
			return;
			
			/* Return pointer to the OptionJAR. */
		case SJME_SYSCALL_OPTION_JAR_DATA:
			if (jvm->optionjar == NULL)
			{
				*syserr = SJME_SYSCALL_ERROR_VALUE_OUT_OF_RANGE;
				return;
			}
			
			/* Is available. */
			*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
			rv->lo = jvm->optionjar->fakeptr;
			return;
		
			/* Return size of the OptionJAR. */
		case SJME_SYSCALL_OPTION_JAR_SIZE:
			if (jvm->optionjar == NULL)
			{
				*syserr = SJME_SYSCALL_ERROR_VALUE_OUT_OF_RANGE;
				return;
			}
			
			/* Is available. */
			*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
			rv->lo = jvm->optionjar->size;
			return;
			
			/* Pipe descriptor of standard error. */
		case SJME_SYSCALL_PD_OF_STDERR:
			*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
			rv->lo = SJME_JINT_C(SJME_PIPE_FD_STDERR);
			return;
			
			/* Pipe descriptor of standard output. */
		case SJME_SYSCALL_PD_OF_STDOUT:
			*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
			rv->lo = SJME_JINT_C(SJME_PIPE_FD_STDOUT);
			return;
			
			/* Write single byte to a stream. */
		case SJME_SYSCALL_PD_WRITE_BYTE:
			ia = SJME_JINT_C(-1);
			
			/* The byte to write. */
			ba = (sjme_jbyte)args[1];
			
			/* Depends on the pipe target. */
			switch (args[0])
			{
					/* Standard output. */
				case SJME_PIPE_FD_STDOUT:
					ia = sjme_console_pipewrite(jvm,
						(jvm->nativefuncs != NULL &&
						jvm->nativefuncs->stdout_write != NULL ?
						jvm->nativefuncs->stdout_write : NULL), &ba, 0, 1,
						error);
					break;
				
					/* Standard error. */
				case SJME_PIPE_FD_STDERR:
					ia = sjme_console_pipewrite(jvm,
						(jvm->nativefuncs != NULL &&
						jvm->nativefuncs->stderr_write != NULL ?
						jvm->nativefuncs->stderr_write : NULL), &ba, 0, 1,
						error);
					break;
					
					/* Unknown descriptor. */
				default:
					*syserr = SJME_SYSCALL_ERROR_PIPE_DESCRIPTOR_INVALID;
					rv->lo = SJME_JINT_C(-1);
					return;
			}
			
			/* Write error? */
			if (ia < 0)
			{
				*syserr = SJME_SYSCALL_ERROR_PIPE_DESCRIPTOR_INVALID;
				rv->lo = SJME_JINT_C(-1);
				return;
			}
			
			/* Success. */
			*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
			rv->lo = SJME_JINT_C(1);
			return;
			
			/* Squelch the framebuffer console. */
		case SJME_SYSCALL_SQUELCH_FB_CONSOLE:
			if (jvm->squelchfbconsole == 0)
			{
				jvm->squelchfbconsole = 1;
				*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
				rv->lo = SJME_JINT_C(0);
			}
			return;
			
			/* The supervisor booted okay! */
		case SJME_SYSCALL_SUPERVISOR_BOOT_OKAY:
			if (jvm->supervisorokay == 0)
			{
				jvm->supervisorokay = 1;
				*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
				rv->lo = SJME_JINT_C(0);
			}
			return;
			
			/* Get supervisor property. */
		case SJME_SYSCALL_SUPERVISOR_PROPERTY_GET:
			ia = args[0];
			if (ia < 0 || ia >= SJME_SUPERPROP_NUM_PROPERTIES)
			{
				*syserr = SJME_SYSCALL_ERROR_VALUE_OUT_OF_RANGE;
				return;
			}
			else
			{
				*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
				rv->lo = cpu->supervisorprops[ia];
				return;
			}
			
			/* Set supervisor property. */
		case SJME_SYSCALL_SUPERVISOR_PROPERTY_SET:
			ia = args[0];
			if (ia < 0 || ia >= SJME_SUPERPROP_NUM_PROPERTIES)
			{
				*syserr = SJME_SYSCALL_ERROR_VALUE_OUT_OF_RANGE;
				return;
			}
			else
			{
				*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
				rv->lo = cpu->supervisorprops[ia] = args[1];
				return;
			}
			
			/* Returns the millisecond wall clock. */
		case SJME_SYSCALL_TIME_MILLI_WALL:
			if (jvm->nativefuncs == NULL ||
				jvm->nativefuncs->millitime == NULL)
			{
				*syserr = SJME_SYSCALL_ERROR_UNSUPPORTED_SYSTEM_CALL;
				return;
			}
			else
			{
				*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
				
				rv->lo = jvm->nativefuncs->millitime(&rv->hi);
				return;
			}
			
			/* Returns the nanosecond wall clock. */
		case SJME_SYSCALL_TIME_NANO_MONO:
			if (jvm->nativefuncs == NULL ||
				jvm->nativefuncs->nanotime == NULL)
			{
				*syserr = SJME_SYSCALL_ERROR_UNSUPPORTED_SYSTEM_CALL;
				return;
			}
			else
			{
				*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
				
				rv->lo = jvm->nativefuncs->nanotime(&rv->hi);
				return;
			}
			
			/* Unknown or unsupported system call. */
		default:
			*syserr = SJME_SYSCALL_ERROR_UNSUPPORTED_SYSTEM_CALL;
			return;
	}
#endif
}
