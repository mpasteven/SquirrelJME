/* -*- Mode: C++; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ------------------------------------------------------------------------ */

#include "squirreljme.h"

// The class to forward to
#define SWINGPENCIL_CLASSNAME "cc/squirreljme/emulator/uiform/SwingPencilShelf"

// Descriptors
#define SWINGPENCIL_CAPABILITIES_DESC "(I)J"
#define SWINGPENCIL_HARDWAREGFX_DESC "(IIILjava/lang/Object;I[IIIII)Lcc/squirreljme/jvm/mle/brackets/PencilBracket;"

JNIEXPORT jlong JNICALL Impl_mle_PencilShelf_capabilities(JNIEnv* env,
	jclass classy, jint pixelFormat)
{
	return forwardCallStaticLong(env, SWINGPENCIL_CLASSNAME,
		"capabilities", SWINGPENCIL_CAPABILITIES_DESC,
		pixelFormat);
}

JNIEXPORT jobject JNICALL Impl_mle_PencilShelf_hardwareGraphics(JNIEnv* env,
	jclass classy, jint pf, jint bw, jint bh, jobject buf, jint offset,
	jobject pal, jint sx, jint sy, jint sw, jint sh)
{
	return forwardCallStaticObject(env, SWINGPENCIL_CLASSNAME,
		"hardwareGraphics", SWINGPENCIL_HARDWAREGFX_DESC,
		pf, bw, bh, buf, offset, pal, sx, sy, sw, sh);
}

static const JNINativeMethod mlePencilMethods[] =
{
	{"capabilities", SWINGPENCIL_CAPABILITIES_DESC, (void*)Impl_mle_PencilShelf_capabilities},
	{"hardwareGraphics", SWINGPENCIL_HARDWAREGFX_DESC, (void*)Impl_mle_PencilShelf_hardwareGraphics},
};

jint JNICALL mlePencilInit(JNIEnv* env, jclass classy)
{
	return env->RegisterNatives(
		env->FindClass("cc/squirreljme/jvm/mle/PencilShelf"),
		mlePencilMethods, sizeof(mlePencilMethods) / sizeof(JNINativeMethod));
}
