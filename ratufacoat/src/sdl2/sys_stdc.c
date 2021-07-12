/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// --------------------------------------------------------------------------*/

/**
 * SquirrelJME Standard C Program (should run on anything).
 *
 * @since 2019/06/02
 */

#include <stddef.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <errno.h>

#include "sjmerc.h"
#include "error.h"
#include "native.h"
#include "jvm.h"

/** Screen size. */
#define SJME_STDC_WIDTH 240
#define SJME_STDC_HEIGHT 320
#define SJME_STDC_VIDEORAMSIZE 76800

/** RatufaCoat's Video RAM memory. */
static sjme_jint sjme_ratufacoat_videoram[SJME_STDC_VIDEORAMSIZE];

/** File name structure. */
typedef struct sjme_nativefilename
{
	/** Native path. */
	char* filename;
} sjme_nativefilename;

/** Open file. */
typedef struct sjme_nativefile
{
	/** File opened. */
	FILE* file;
} sjme_nativefile;

/** Standard C functions. */
static sjme_nativefuncs stdcfuncs;

/** Returns the native file name. */
sjme_nativefilename* sjme_stdc_nativeromfile(void)
{
	sjme_nativefilename* rv;
	
	/* Allocate. */
	rv = calloc(1, sizeof(*rv));
	if (rv == NULL)
		return NULL;
	
	/* Set with string copy. */
#if defined(SJME_IS_DOS)
	rv->filename = strdup("sjme.sqc");
#else
	rv->filename = strdup("squirreljme.sqc");
#endif
	
	/* Use this one! */
	return rv;
}

/** Opens the specified file. */
sjme_nativefile* sjme_stdc_fileopen(sjme_nativefilename* filename,
	sjme_jint mode, sjme_error* error)
{
	sjme_nativefile* rv;
	int err;
	
	if (filename == NULL)
	{
		sjme_setError(error, SJME_ERROR_INVALIDARG, 0);
		
		return NULL;
	}
	
	/* Allocate returning file. */
	rv = calloc(1, sizeof(*rv));
	if (rv == NULL)
		return NULL;
	
	/* Try to open file! */
	rv->file = fopen(filename->filename,
		(mode == SJME_OPENMODE_READWRITETRUNCATE ? "w+b" :
		(mode == SJME_OPENMODE_READWRITE ? "r+b" : "r")));
	if (rv->file == NULL)
	{
		// Set error?
		if (error != NULL)
		{
			err = errno;
			if (err == ENOENT)
				sjme_setError(error, SJME_ERROR_NOSUCHFILE, 0);
			else
				sjme_setError(error, SJME_ERROR_UNKNOWN, 0);
		}
		
		// Clear out
		free(rv);
		rv = NULL;
	}
	
	/* Use it! */
	return rv;
}

/** Closes the specified file. */
void sjme_stdc_fileclose(sjme_nativefile* file, sjme_error* error)
{
	int rv;
	
	/* Not closing a file? */
	if (file == NULL)
	{
		sjme_setError(error, SJME_ERROR_INVALIDARG, 0);
		
		return;
	}
	
	/* Close and clear pointer. */
	rv = fclose(file->file);
	file->file = NULL;
	
	/* Set error state? */
	if (error != NULL)
		sjme_setError(error, (rv == 0 ? SJME_ERROR_NONE : SJME_ERROR_UNKNOWN),
					  0);
	
	/* Free resources. */
	free(file);
}

/** Returns the size of the file. */
sjme_jint sjme_stdc_filesize(sjme_nativefile* file, sjme_error* error)
{
	long now, size;
	
	/* Not valid? */
	if (file == NULL)
	{
		sjme_setError(error, SJME_ERROR_INVALIDARG, 0);
		
		return SJME_JINT_C(-1);
	}
	
	/* Remember current file size, to go back. */
	now = ftell(file->file);
	
	/* Seek to end. */
	if (fseek(file->file, 0, SEEK_END) != 0)
	{
		sjme_setError(error, SJME_ERROR_UNKNOWN, 0);
		
		return SJME_JINT_C(-1);
	}
	
	/* Set size. */
	size = ftell(file->file);
	if (size < 0)
	{
		sjme_setError(error, SJME_ERROR_UNKNOWN, 0);
		
		return SJME_JINT_C(-1);
	}
	
	/* Seek back. */
	if (fseek(file->file, now, SEEK_SET) != 0)
	{
		sjme_setError(error, SJME_ERROR_UNKNOWN, 0);
		
		return SJME_JINT_C(-1);
	}
	
	/* Return the size. */
	if (size >= SJME_JINT_MAX_VALUE)
		return SJME_JINT_MAX_VALUE;
	return (sjme_jint)size;
}

/** Reads from a file. */
sjme_jint sjme_stdc_fileread(sjme_nativefile* file, void* dest, sjme_jint len,
	sjme_error* error)
{
	size_t rv;
	
	/* Too many bytes to read? */
	if (sizeof(len) > sizeof(rv) && len > (sjme_jint)SIZE_MAX)
		len = (sjme_jint)SIZE_MAX;
	
	/* Invalid argument? */
	if (file == NULL || dest == NULL || len < 0)
	{
		sjme_setError(error, SJME_ERROR_INVALIDARG, 0);
		
		return SJME_JINT_C(-1);
	}
	
	/* Read from file. */
	rv = fread(dest, 1, len, file->file);
	if (rv == 0)
	{
		/* End of file? */
		if (feof(file->file) != 0)
			sjme_setError(error, SJME_ERROR_ENDOFFILE, 0);
		
		/* Another error. */
		else
			sjme_setError(error, SJME_ERROR_UNKNOWN, 0);
		
		return SJME_JINT_C(-1);
	}
	
	/* Return the read bytes. */
	return (sjme_jint)rv;
}

/** Writes single byte to standard output. */
sjme_jint sjme_stdc_stdout_write(sjme_jint b)
{
	sjme_jbyte v;
	
	/* Write. */
	v = (sjme_jbyte)b;
	fwrite(&v, 1, 1, stdout);
	
	/* EOF? */
	if (feof(stdout))
		return SJME_JINT_C(0);
	
	/* Error? */
	if (ferror(stdout))
		return SJME_JINT_C(-1);
	
	return SJME_JINT_C(1);
}

/** Writes single byte to standard error. */
sjme_jint sjme_stdc_stderr_write(sjme_jint b)
{
	sjme_jbyte v;
	
	/* Write. */
	v = (sjme_jbyte)b;
	fwrite(&v, 1, 1, stderr);
	
	/* EOF? */
	if (feof(stderr))
		return SJME_JINT_C(0);
	
	/* Error? */
	if (ferror(stderr))
		return SJME_JINT_C(-1);
	
	return SJME_JINT_C(1);
}

/** Returns a framebuffer structure. */
sjme_framebuffer* sjme_stdc_framebuffer(void)
{
	sjme_framebuffer* rv;
	
	/* Allocate one. */
	rv = calloc(1, sizeof(*rv));
	if (rv == NULL)
		return NULL;
	
	/* Fill information out. */
	rv->pixels = sjme_ratufacoat_videoram;
	rv->width = SJME_STDC_WIDTH;
	rv->height = SJME_STDC_HEIGHT;
	rv->scanlen = SJME_STDC_WIDTH;
	rv->format = SJME_PIXELFORMAT_INTEGER_RGB888;
	rv->numpixels = SJME_STDC_VIDEORAMSIZE;
	
	return rv;
}

/**
 * Main entry point.
 *
 * @param argc Argument count.
 * @param argv Arguments.
 * @since 2019/06/02
 */
int main(int argc, char** argv)
{
	sjme_jvmoptions options;
	sjme_jvm* jvm;
	sjme_error error;
	
	/* Clear error. */
	memset(&error, 0, sizeof(error));
	
	/* Wipe options because it will get set and such. */
	memset(&options, 0, sizeof(options));
	
	/* Setup arguments. */
	options.args.format = SJME_JVMARG_FORMAT_STDC;
	options.args.args.stdc.argc = argc;
	options.args.args.stdc.argv = argv;
	
	/* Setup native functions. */
	stdcfuncs.nativeromfile = sjme_stdc_nativeromfile;
	stdcfuncs.fileopen = sjme_stdc_fileopen;
	stdcfuncs.fileclose = sjme_stdc_fileclose;
	stdcfuncs.filesize = sjme_stdc_filesize;
	stdcfuncs.fileread = sjme_stdc_fileread;
	stdcfuncs.stdout_write = sjme_stdc_stdout_write;
	stdcfuncs.stderr_write = sjme_stdc_stderr_write;
	stdcfuncs.framebuffer = sjme_stdc_framebuffer;
	
	/* Create VM. */
	jvm = NULL;
	if (sjme_jvmNew(&jvm, &options, &stdcfuncs, &error))
	{
		fprintf(stderr,
			"Failed to create the JVM! (Error %d/0x%X %d/0x%X)\n",
			(int)error.code, (unsigned int)error.code,
			(int)error.value, (unsigned int)error.value);
		return EXIT_FAILURE;
	}
	
	/* Execute until termination. */
	for (;;)
	{
		/* Just execute the VM and disregard nay cycles that remain. */
		sjme_jvmexec(jvm, &error, SJME_JINT_C(1048576));
		
		/* The JVM hit some kind of error? */
		if (error.code != SJME_ERROR_NONE)
		{
			/* Normal JVM exit, not considered a true error. */
			if (error.code == SJME_ERROR_JVMEXIT_SUV_OKAY)
				break;
			
			/* Message on it! */
			fprintf(stderr, "JVM execution fault! (Error %d/0x%X %d/0x%X)\n",
				(int)error.code, (unsigned int)error.code,
				(int)error.value, (unsigned int)error.value);
			
			/* Destroy the JVM to free resources. */
			if (sjme_jvmDestroy(jvm, &error))
				fprintf(stderr,
					"JVM destruction error! (Error %d/0x%X %d/0x%X)\n",
					(int)error.code, (unsigned int)error.code,
					(int)error.value, (unsigned int)error.value);
			
			return EXIT_FAILURE;
		}
		
		/* Keep going! */
		continue;
	}
	
	/* Destroy the VM so it uses no memory. */
	if (sjme_jvmDestroy(jvm, &error))
		fprintf(stderr, "JVM destruction error! (Error %d/0x%X %d/0x%X)\n",
			(int)error.code, (unsigned int)error.code,
			(int)error.value, (unsigned int)error.value);
	
	return EXIT_SUCCESS;
}
