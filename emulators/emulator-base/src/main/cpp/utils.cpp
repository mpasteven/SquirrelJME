/* -*- Mode: C++; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// -------------------------------------------------------------------------*/

#include "squirreljme.h"

#include <stdarg.h>

forwardMethod JNICALL findForwardMethod(JNIEnv* env,
	const char* const classy, const char* const name, const char* const type)
{
	forwardMethod result;
	
	result.xclass = env->FindClass(classy);
	result.xmeth = env->GetStaticMethodID(result.xclass, name, type);
	
	return result;
}

// Preface
#define SQUEAK_PREF \
	forwardMethod call; \
	va_list vaArgs; \
	\
	call = findForwardMethod(env, classy, name, type);\
	\
	va_start(vaArgs, type);

// Post handling
#define SQUEAK_POST \
	va_end(vaArgs);

// Debugging?
#if defined(_DEBUG) || defined(DEBUG)
	#define DEBUG_CALL fprintf(stderr, "JNI Forward: %s:%s\n", classy, name);
#else
	#define DEBUG_CALL
#endif

void JNICALL forwardCallStaticVoid(JNIEnv* env,
	const char* const classy, const char* const name, const char* const type,
	...)
{
	SQUEAK_PREF;
	DEBUG_CALL;
	env->CallStaticVoidMethodV(call.xclass, call.xmeth, vaArgs);
	SQUEAK_POST;
}

jint JNICALL forwardCallStaticInteger(JNIEnv* env,
	const char* const classy, const char* const name, const char* const type,
	...)
{
	jint rv;
	
	SQUEAK_PREF;
	DEBUG_CALL;
	rv = env->CallStaticIntMethodV(call.xclass, call.xmeth, vaArgs);
	SQUEAK_POST;
	
	return rv;
}

jlong JNICALL forwardCallStaticLong(JNIEnv* env,
	const char* const classy, const char* const name, const char* const type,
	...)
{
	jlong rv;
	
	SQUEAK_PREF;
	DEBUG_CALL;
	rv = env->CallStaticLongMethodV(call.xclass, call.xmeth, vaArgs);
	SQUEAK_POST;
	
	return rv;
}

jobject JNICALL forwardCallStaticObject(JNIEnv* env,
	const char* const classy, const char* const name, const char* const type,
	...)
{
	jobject rv;
	
	SQUEAK_PREF;
	DEBUG_CALL;
	rv = env->CallStaticObjectMethodV(call.xclass, call.xmeth, vaArgs);
	SQUEAK_POST;
	
	return rv;
}

jboolean JNICALL forwardCallStaticBoolean(JNIEnv* env,
	const char* const classy, const char* const name, const char* const type,
	...)
{
	jboolean rv;
	
	SQUEAK_PREF;
	DEBUG_CALL;
	rv = env->CallStaticBooleanMethodV(call.xclass, call.xmeth, vaArgs);
	SQUEAK_POST;
	
	return rv;
}
	
