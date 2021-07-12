/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// --------------------------------------------------------------------------*/

/**
 * SquirrelJME RatufaCoat Source.
 *
 * @since 2019/06/02
 */

#include "sjmerc.h"
#include "sjmecon.h"
#include "corefont.h"
#include "stringies.h"
#include "softmath.h"
#include "error.h"
#include "native.h"
#include "oldstuff.h"
#include "cpu.h"
#include "memory.h"
#include "jvm.h"
#include "debug.h"

/** Draws single character onto the console. */
void sjme_console_drawplate(sjme_jvm* jvm, sjme_jint x, sjme_jint y,
	sjme_jbyte ch, sjme_error* error)
{
	sjme_jint r, c, cv, i, fontw, fonth, xform;
	sjme_vmemptr sp;
	sjme_jbyte* mp;
	sjme_jbyte bits;
	sjme_jint bpp, pq, at, mask;
	
	/* Check. */
	if (jvm == NULL)
		return;
	
	/* Ignore if out of bounds. */
	if (x < 0 || y < 0 || x >= sjme_jvmFramebuffer(jvm)->conw ||
		y >= sjme_jvmFramebuffer(jvm)->conh)
		return;
	
	/* Font dimensions. */
	fontw = sjme_font.charwidths[0];
	fonth = sjme_font.pixelheight;
	
	/* Normalize to screen space. */
	x = x * fontw;
	y = y * fonth;
	
	/* Character data to draw. */
	if (sjme_font.isvalidchar[ch] == 0)
		ch = 0;
	mp = &sjme_font.charbmp[((sjme_jint)ch) * fonth * sjme_font.bytesperscan];
	
	/* Drawing format for the data value? */
	bpp = sjme_jvmFramebuffer(jvm)->bitsperpixel;
	switch (bpp)
	{
		case 1:
		case 2:
		case 4:
		case 8:
			xform = SJME_VMMTYPE_BYTE;
			mask = (SJME_JINT_C(1) << bpp) - 1;
			break;
			
		case 16:
			xform = SJME_VMMTYPE_SHORT;
			mask = SJME_JINT_C(0xFFFF);
			break;
		
		case 32:
			xform = SJME_VMMTYPE_INTEGER;
			mask = SJME_JINT_C(0xFFFFFFFF);
			break;
	}
	
	/* Draw rows. */
	for (r = 0; r < fonth; r++)
	{
		/* Determine screen position. */
		sp = sjme_jvmFramebuffer(jvm)->framebuffer->fakeptr +
			((x * sjme_jvmFramebuffer(jvm)->bitsperpixel) / 8) +
			((y + r) * (sjme_jvmFramebuffer(jvm)->scanlenbytes));
		
		/* Clear pixel queue. */
		pq = 0;
		at = 0;
		
		/* Draw all pixel scans. */
		c = 0;
		for (cv = 0; cv < sjme_font.bytesperscan; cv++, mp++)
		{
			/* Get character bits */
			bits = *mp;
			
			/* Draw all of them. */
			for (i = 0; i < 8 && c < fontw; i++, c++)
			{
				/* Shift the queue up from the last run. */
				pq <<= bpp;
				
				/* Mask it if the color is set? */
				if ((bits & sjme_drawcharbitmask[i]) != 0)
					pq |= mask;
				
				/* Queued bits go up. */
				at += bpp;
				
				/* Only write when there is at least 8! */
				if (at >= 8)
				{
					/* Write. */
					sjme_vmmwritep(sjme_jvmVMem(jvm), xform, &sp, pq, error);
					
					/* Cut down. */
					pq = (((pq & sjme_sh_umask[bpp])) >> bpp);
					at -= bpp;
				}
			}
		}
		
		/* Force draw any pixels left over. */
		if (at >= bpp)
			sjme_vmmwritep(sjme_jvmVMem(jvm), xform, &sp, pq, error);
	}
}

/** Writes to the console screen and to the native method as well. */
sjme_jint sjme_console_pipewrite(sjme_jvm* jvm,
	sjme_jint (*writefunc)(sjme_jint b), sjme_jbyte* buf, sjme_jint off,
	sjme_jint len, sjme_error* error)
{
	sjme_jbyte b, donewline;
	sjme_jint i, code;
	
	/* There must be a JVM! */
	if (jvm == NULL)
		return -1;
	
	/* Write all the bytes to the output. */
	for (i = 0; i < len; i++, off++)
	{
		/* Read byte. */
		b = buf[off];
		
		/* Draw to the console in supervisor boot mode or if it was not */
		/* yet squelched. */
		if (1
#if 0
			(jvm->supervisorokay == 0 || jvm->squelchfbconsole == 0) &&
			sjme_jvmFramebuffer(jvm) != NULL
#endif
			)
		{
			/* Carriage return? */
			donewline = 0;
			if (b == '\r')
				sjme_jvmFramebuffer(jvm)->conx = 0;
			
			/* Newline? */
			else if (b == '\n')
				donewline = 1;
			
			/* Draw character? */
			else
			{
				/* Draw it. */
				sjme_console_drawplate(jvm,
					sjme_jvmFramebuffer(jvm)->conx,
					sjme_jvmFramebuffer(jvm)->cony, b, error);
				
				/* Move cursor up. */
				sjme_jvmFramebuffer(jvm)->conx++;
				
				/* New line to print on? */
				if (sjme_jvmFramebuffer(jvm)->conx >=
					sjme_jvmFramebuffer(jvm)->conw)
					donewline = 1;
			}
			
			/* Doing a new line? */
			if (donewline != 0)
			{
				/* Move the cursor to the start of the next line. */
				sjme_jvmFramebuffer(jvm)->conx = 0;
				sjme_jvmFramebuffer(jvm)->cony++;
				
				/* Too much text on the screen? Move it up! */
				if (sjme_jvmFramebuffer(jvm)->cony >=
					sjme_jvmFramebuffer(jvm)->conh)
				{
					/* Move framebuffer up. */
					memmove(
						SJME_POINTER_OFFSET_LONG(sjme_jvmFramebuffer(jvm)->pixels, 0),
						SJME_POINTER_OFFSET_LONG(sjme_jvmFramebuffer(jvm)->pixels,
							sjme_font.pixelheight *
							(sjme_jvmFramebuffer(jvm)->scanlenbytes)),
						(sjme_jvmFramebuffer(jvm)->height - sjme_font.pixelheight) *
							(sjme_jvmFramebuffer(jvm)->scanlenbytes));
					
					/* Wipe bytes at the bottom. */
					memset(
						SJME_POINTER_OFFSET_LONG(sjme_jvmFramebuffer(jvm)->pixels,
							(sjme_jvmFramebuffer(jvm)->height - sjme_font.pixelheight) *
								(sjme_jvmFramebuffer(jvm)->scanlenbytes)), 0,
						sjme_font.pixelheight * (sjme_jvmFramebuffer(jvm)->scanlenbytes));
					
					/* Move the cursor up one line. */
					sjme_jvmFramebuffer(jvm)->cony--;
				}
			}
			
			/* Always flush in debug mode to force screen updates. */
			if (sjme_jvmFramebuffer(jvm)->flush != NULL)
				sjme_jvmFramebuffer(jvm)->flush();
		}
		
		/* Forward to pipe? */
		if (writefunc != NULL)
		{
			code = writefunc(b);
			if (code < 0)
				return (i == 0 ? code : i);
		}
	}
	
	/* Return written bytes. */
	return len;
}

/** Prints the error to the console output. */
void sjme_printerror(sjme_jvm* jvm, sjme_error* error)
{
#define ERROR_SIZE 256
    sjme_jbyte message[ERROR_SIZE];
    sjme_jint messageLen = ERROR_SIZE;
	sjme_jint (*po)(sjme_jint b);
	
	/* Get output console. */
	po = (sjme_jvmNativeFuncs(jvm) != NULL ?
		sjme_jvmNativeFuncs(jvm)->stderr_write : NULL);

	/* Load the JVM error into the target buffer. */
	sjme_describeJvmError(error, message, &messageLen);
	
	/* Write the failure message. */
	sjme_console_pipewrite(jvm, po, message, 0,
		sizeof(messageLen) / sizeof(sjme_jbyte), error);
	
	/* Always flush the screen on error. */
	if (sjme_jvmFramebuffer(jvm)->flush != NULL)
		sjme_jvmFramebuffer(jvm)->flush();

#undef ERROR_SIZE
}

/** Initializes the configuration space. */
void sjme_configinit(sjme_jvm* jvm, sjme_jvmoptions* options,
	sjme_nativefuncs* nativefuncs, sjme_error* error)
{
	sjme_todo("sjme_configinit -- DELETE");
	
#if 0
#define SJME_CONFIG_FORMAT_INTEGER SJME_JINT_C(1)
#define SJME_CONFIG_FORMAT_KEYVALUE SJME_JINT_C(2)
#define SJME_CONFIG_FORMAT_STRING SJME_JINT_C(3)
#define SJME_CONFIG_FORMAT_STRINGS SJME_JINT_C(4)
	sjme_vmemptr wp;
	sjme_vmemptr basep;
	sjme_vmemptr sizep;
	sjme_jint opt, format, iv, it, wlen;
	sjme_vmemptr* setpointer;
	char* sa;
	
	(void)options;
	(void)nativefuncs;
	
	/* Write pointer starts at the base area. */
	wp = jvm->config->fakeptr;
	
	/* Go through all possible options to make a value. */
	for (opt = SJME_JINT_C(1); opt < SJME_CONFIG_NUM_OPTIONS; opt++)
	{
		/* Used to specify the format. */
		format = 0;
		
		/* Reset. */
		sa = NULL;
		iv = 0;
		setpointer = NULL;
		
		/* Depends on the option. */
		switch (opt)
		{
			/* Java VM Version. */
			case SJME_CONFIG_JAVA_VM_VERSION:
				format = SJME_CONFIG_FORMAT_STRING;
				sa = "0.3.0";
				break;
			
			/* Java VM Name. */
			case SJME_CONFIG_JAVA_VM_NAME:
				format = SJME_CONFIG_FORMAT_STRING;
				sa = "SquirrelJME RatufaCoat";
				break;
			
			/* Java VM Vendor. */
			case SJME_CONFIG_JAVA_VM_VENDOR:
				format = SJME_CONFIG_FORMAT_STRING;
				sa = "Stephanie Gawroriski";
				break;
			
			/* Java VM E-Mail. */
			case SJME_CONFIG_JAVA_VM_EMAIL:
				format = SJME_CONFIG_FORMAT_STRING;
				sa = "xerthesquirrel@gmail.com";
				break;
			
			/* Java VM URL. */
			case SJME_CONFIG_JAVA_VM_URL:
				format = SJME_CONFIG_FORMAT_STRING;
				sa = "https://squirreljme.cc/";
				break;
			
			/* The guest depth. */
			case SJME_CONFIG_GUEST_DEPTH:
				break;
			
			/* Main class. */
			case SJME_CONFIG_MAIN_CLASS:
				break;
			
			/* Main program arguments. */
			case SJME_CONFIG_MAIN_ARGUMENTS:
				break;
			
			/* Is this a MIDlet? */
			case SJME_CONFIG_IS_MIDLET:
				break;
			
			/* Define system propertly. */
			case SJME_CONFIG_DEFINE_PROPERTY:
				break;
			
			/* Classpath to use. */
			case SJME_CONFIG_CLASS_PATH:
				break;
			
			/* System Call: Static field pointer. */
			case SJME_CONFIG_SYSCALL_STATIC_FIELD_POINTER:
				format = SJME_CONFIG_FORMAT_INTEGER;
				setpointer = &jvm->syscallsfp;
				break;
			
			/* System Call: Code Pointer. */
			case SJME_CONFIG_SYSCALL_CODE_POINTER:
				format = SJME_CONFIG_FORMAT_INTEGER;
				setpointer = &jvm->syscallcode;
				break;
			
			/* System Call: Pool Pointer. */
			case SJME_CONFIG_SYSCALL_POOL_POINTER:
				format = SJME_CONFIG_FORMAT_INTEGER;
				setpointer = &jvm->syscallpool;
				break;
			
				/* Unknown, ignore. */
			default:
				continue;
		}
		
		/* No known way to write this? */
		if (format == 0)
			continue;
		
		/* Write option key. */
		sjme_vmmwritep(sjme_jvmVMem(jvm), SJME_VMMTYPE_JAVASHORT, &wp, opt, error);
		
		/* Store size location for later write. */
		sizep = wp;
		sjme_vmmwritep(sjme_jvmVMem(jvm), SJME_VMMTYPE_JAVASHORT, &wp, 0, error);
		
		/* Base write pointer. */
		basep = wp;
		
		/* Set pointer as needed. */
		if (setpointer != NULL)
			*setpointer = basep;
		
		/* Depends on the format. */
		switch (format)
		{
				/* Integer. */
			case SJME_CONFIG_FORMAT_INTEGER:
				sjme_vmmwritep(sjme_jvmVMem(jvm), SJME_VMMTYPE_JAVAINTEGER, &wp, iv,
					error);
				break;
			
				/* Key/value pair. */
			case SJME_CONFIG_FORMAT_KEYVALUE:
				break;
			
				/* String value. */
			case SJME_CONFIG_FORMAT_STRING:
				{
					// Record string length
					int iv = strlen(sa);
					sjme_vmmwritep(sjme_jvmVMem(jvm), SJME_VMMTYPE_JAVASHORT, &wp, iv,
						error);
					
					// Record characters
					for (it = 0; it < iv; it++)
						sjme_vmmwritep(sjme_jvmVMem(jvm), SJME_VMMTYPE_BYTE, &wp,
							(sjme_jint)sa[it], error);
				}
				break;
			
				/* Multiple strings. */
			case SJME_CONFIG_FORMAT_STRINGS:
				break;
		}
		
		/* Determine length and round it to 4 bytes. */
		wlen = wp - basep;
		while ((wlen & SJME_JINT_C(3)) != 0)
		{
			/* Write padding. */
			sjme_vmmwritep(sjme_jvmVMem(jvm), SJME_VMMTYPE_BYTE, &wp, 0, error);
			wlen++;
		}
		
		/* Write to the actual size! */
		sjme_vmmwrite(sjme_jvmVMem(jvm), SJME_VMMTYPE_JAVASHORT, sizep, 0, wlen,
			error);
	}
	
	/* Write end of config. */
	sjme_vmmwritep(sjme_jvmVMem(jvm), SJME_VMMTYPE_BYTE, &wp, SJME_CONFIG_END, error);
	
#undef SJME_CONFIG_FORMAT_INTEGER
#undef SJME_CONFIG_FORMAT_KEYVALUE
#undef SJME_CONFIG_FORMAT_STRING
#undef SJME_CONFIG_FORMAT_STRINGS
#endif
}
