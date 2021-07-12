/* -*- Mode: C++; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

#ifndef __SQUIRRELJME_H__
#define __SQUIRRELJME_H__

#include "jni.h"

/** Initializing methods. */
jint JNICALL mleDebugInit(JNIEnv* env, jclass classy);
jint JNICALL mleFormInit(JNIEnv* env, jclass classy);
jint JNICALL mleJarInit(JNIEnv* env, jclass classy);
jint JNICALL mleObjectInit(JNIEnv* env, jclass classy);
jint JNICALL mlePencilInit(JNIEnv* env, jclass classy);
jint JNICALL mleRuntimeInit(JNIEnv* env, jclass classy);
jint JNICALL mleTaskInit(JNIEnv* env, jclass classy);
jint JNICALL mleTerminalInit(JNIEnv* env, jclass classy);
jint JNICALL mleThreadInit(JNIEnv* env, jclass classy);

/** Useful macros, structures, and functions for forwarding. */
// Stores forwarded information
typedef struct forwardMethod
{
	jclass xclass;
	jmethodID xmeth;	
} forwardMethod;

// Find forwarded method
forwardMethod JNICALL findForwardMethod(JNIEnv* env,
	const char* const classy, const char* const name, const char* const type);

// Call static methods
void JNICALL forwardCallStaticVoid(JNIEnv* env,
	const char* const classy, const char* const name, const char* const type,
	...);
jint JNICALL forwardCallStaticInteger(JNIEnv* env,
	const char* const classy, const char* const name, const char* const type,
	...);
jlong JNICALL forwardCallStaticLong(JNIEnv* env,
	const char* const classy, const char* const name, const char* const type,
	...);
jobject JNICALL forwardCallStaticObject(JNIEnv* env,
	const char* const classy, const char* const name, const char* const type,
	...);
jboolean JNICALL forwardCallStaticBoolean(JNIEnv* env,
	const char* const classy, const char* const name, const char* const type,
	...);

#endif /* __SQUIRRELJME_H__ */
