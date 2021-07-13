/* ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// --------------------------------------------------------------------------*/

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>

#include "jni.h"
#include "cc_squirreljme_emulator_NativeBinding.h"
#include "cc_squirreljme_jvm_Assembly.h"
#include "squirreljme.h"

JNIEXPORT jlong JNICALL longPack(JNIEnv* env, jclass classy, jint al, jint ah)
{
	return (((jlong)al) & UINT64_C(0xFFFFFFFF)) |
		((((jlong)ah) & UINT64_C(0xFFFFFFFF)) << UINT64_C(32));
}

JNIEXPORT jint JNICALL longUnpackHigh(JNIEnv* env, jclass classy, jlong v)
{
	return (jint)(v >> UINT64_C(32));
}

JNIEXPORT jint JNICALL longUnpackLow(JNIEnv* env, jclass classy, jlong v)
{
	return (jint)(v);
}

// Method handler for special functions
JNIEXPORT void JNICALL restrictedFunction(JNIEnv* env, jclass classy);

// Assembly method mappings
static const JNINativeMethod assemblyMethods[] =
{
	{"arrayLength", "(I)I", (void*)restrictedFunction},
	{"arrayLength", "(Ljava/lang/Object;)I", (void*)restrictedFunction},
	{"atomicCompareGetAndSet", "(IIJ)I", (void*)restrictedFunction},
	{"atomicDecrementAndGet", "(J)I", (void*)restrictedFunction},
	{"atomicIncrement", "(J)V", (void*)restrictedFunction},
	{"breakpoint", "()V", (void*)restrictedFunction},
	{"doublePack", "(II)D", (void*)restrictedFunction},
	{"doubleToRawLongBits", "(D)J", (void*)restrictedFunction},
	{"doubleUnpackHigh", "(D)I", (void*)restrictedFunction},
	{"doubleUnpackLow", "(D)I", (void*)restrictedFunction},
	{"exceptionHandle", "()V", (void*)restrictedFunction},
	{"floatToRawIntBits", "(F)I", (void*)restrictedFunction},
	{"intBitsToFloat", "(I)F", (void*)restrictedFunction},
	{"invoke", "(II)V", (void*)restrictedFunction},
	{"invoke", "(III)V", (void*)restrictedFunction},
	{"invoke", "(IIII)V", (void*)restrictedFunction},
	{"invoke", "(IIIII)V", (void*)restrictedFunction},
	{"invoke", "(IIIIII)V", (void*)restrictedFunction},
	{"invoke", "(IIIIIII)V", (void*)restrictedFunction},
	{"invoke", "(IIIIIIII)V", (void*)restrictedFunction},
	{"invoke", "(IIIIIIIII)V", (void*)restrictedFunction},
	{"invoke", "(IIIIIIIIII)V", (void*)restrictedFunction},
	{"invokeV", "(II)I", (void*)restrictedFunction},
	{"invokeV", "(III)I", (void*)restrictedFunction},
	{"invokeV", "(IIII)I", (void*)restrictedFunction},
	{"invokeV", "(IIIII)I", (void*)restrictedFunction},
	{"invokeV", "(IIIIII)I", (void*)restrictedFunction},
	{"invokeV", "(IIIIIII)I", (void*)restrictedFunction},
	{"invokeV", "(IIIIIIII)I", (void*)restrictedFunction},
	{"invokeV", "(IIIIIIIII)I", (void*)restrictedFunction},
	{"invokeV", "(IIIIIIIIII)I", (void*)restrictedFunction},
	{"invokeVL", "(II)J", (void*)restrictedFunction},
	{"invokeVL", "(III)J", (void*)restrictedFunction},
	{"invokeVL", "(IIII)J", (void*)restrictedFunction},
	{"invokeVL", "(IIIII)J", (void*)restrictedFunction},
	{"invokeVL", "(IIIIII)J", (void*)restrictedFunction},
	{"invokeVL", "(IIIIIII)J", (void*)restrictedFunction},
	{"invokeVL", "(IIIIIIII)J", (void*)restrictedFunction},
	{"invokeVL", "(IIIIIIIII)J", (void*)restrictedFunction},
	{"invokeVL", "(IIIIIIIIII)J", (void*)restrictedFunction},
	{"longBitsToDouble", "(J)D", (void*)restrictedFunction},
	{"longPack", "(II)J", (void*)longPack},
	{"longUnpackHigh", "(J)I", (void*)longUnpackHigh},
	{"longUnpackLow", "(J)I", (void*)longUnpackLow},
	{"memReadByte", "(JI)I", (void*)restrictedFunction},
	{"memReadInt", "(JI)I", (void*)restrictedFunction},
	{"memReadLong", "(JI)J", (void*)restrictedFunction},
	{"memReadShort", "(JI)I", (void*)restrictedFunction},
	{"memWriteByte", "(JII)V", (void*)restrictedFunction},
	{"memWriteInt", "(JII)V", (void*)restrictedFunction},
	{"memWriteLong", "(JIJ)V", (void*)restrictedFunction},
	{"memWriteShort", "(JII)V", (void*)restrictedFunction},
	{"monitorCountDecrementAndGetAtomic", "(J)I", (void*)restrictedFunction},
	{"monitorCountDecrementAndGetAtomic", "(Ljava/lang/Object;)I", (void*)restrictedFunction},
	{"monitorCountIncrementAndGetAtomic", "(J)I", (void*)restrictedFunction},
	{"monitorCountIncrementAndGetAtomic", "(Ljava/lang/Object;)I", (void*)restrictedFunction},
	{"monitorCountGetAtomic", "(J)I", (void*)restrictedFunction},
	{"monitorCountGetAtomic", "(Ljava/lang/Object;)I", (void*)restrictedFunction},
	{"monitorCountSetAtomic", "(JI)V", (void*)restrictedFunction},
	{"monitorCountSetAtomic", "(Ljava/lang/Object;I)V", (void*)restrictedFunction},
	{"monitorOwnerCompareGetAndSetAtomic", "(JJJ)Ljava/lang/Thread;", (void*)restrictedFunction},
	{"monitorOwnerCompareGetAndSetAtomic", "(Ljava/lang/Object;JJ)Ljava/lang/Thread;", (void*)restrictedFunction},
	{"monitorOwnerCompareGetAndSetAtomic", "(JLjava/lang/Thread;Ljava/lang/Thread;)Ljava/lang/Thread;", (void*)restrictedFunction},
	{"monitorOwnerCompareGetAndSetAtomic", "(Ljava/lang/Object;Ljava/lang/Thread;Ljava/lang/Thread;)Ljava/lang/Thread;", (void*)restrictedFunction},
	{"monitorOwnerCompareGetAndSetAtomicPointer", "(JJJ)J", (void*)restrictedFunction},
	{"monitorOwnerCompareGetAndSetAtomicPointer", "(Ljava/lang/Object;JJ)J", (void*)restrictedFunction},
	{"monitorOwnerCompareGetAndSetAtomicPointer", "(JLjava/lang/Thread;Ljava/lang/Thread;)J", (void*)restrictedFunction},
	{"monitorOwnerCompareGetAndSetAtomicPointer", "(Ljava/lang/Object;Ljava/lang/Thread;Ljava/lang/Thread;)J", (void*)restrictedFunction},
	{"monitorOwnerGetAtomic", "(J)Ljava/lang/Thread;", (void*)restrictedFunction},
	{"monitorOwnerGetAtomic", "(Ljava/lang/Object;)Ljava/lang/Thread;", (void*)restrictedFunction},
	{"monitorOwnerGetPointerAtomic", "(J)J", (void*)restrictedFunction},
	{"monitorOwnerGetPointerAtomic", "(Ljava/lang/Object;)J", (void*)restrictedFunction},
	{"monitorOwnerSetAtomic", "(JJ)V", (void*)restrictedFunction},
	{"monitorOwnerSetAtomic", "(JLjava/lang/Thread;)V", (void*)restrictedFunction},
	{"monitorOwnerSetAtomic", "(Ljava/lang/Object;J)V", (void*)restrictedFunction},
	{"monitorOwnerSetAtomic", "(Ljava/lang/Object;Ljava/lang/Thread;)V", (void*)restrictedFunction},
	{"objectToPointer", "(Ljava/lang/Object;)I", (void*)restrictedFunction},
	{"objectToPointerRefQueue", "(Ljava/lang/Object;)I", (void*)restrictedFunction},
	{"pointerToObject", "(I)Ljava/lang/Object;", (void*)restrictedFunction},
	{"poolLoad", "(II)I", (void*)restrictedFunction},
	{"poolLoad", "(Ljava/lang/Object;I)I", (void*)restrictedFunction},
	{"refCount", "(I)V", (void*)restrictedFunction},
	{"refCount", "(Ljava/lang/Object;)V", (void*)restrictedFunction},
	{"refGetCount", "(I)I", (void*)restrictedFunction},
	{"refGetCount", "(Ljava/lang/Object;)I", (void*)restrictedFunction},
	{"refSetCount", "(II)V", (void*)restrictedFunction},
	{"refSetCount", "(Ljava/lang/Object;I)V", (void*)restrictedFunction},
	{"refUncount", "(I)V", (void*)restrictedFunction},
	{"refUncount", "(Ljava/lang/Object;)V", (void*)restrictedFunction},
	{"returnFrame", "()V", (void*)restrictedFunction},
	{"returnFrame", "(I)V", (void*)restrictedFunction},
	{"returnFrame", "(II)V", (void*)restrictedFunction},
	{"returnFrameLong", "(J)V", (void*)restrictedFunction},
	{"specialGetExceptionRegister", "()Ljava/lang/Object;", (void*)restrictedFunction},
	{"specialGetExceptionRegisterThrowable", "()Ljava/lang/Throwable;", (void*)restrictedFunction},
	{"specialGetExceptionRegisterPointer", "()J", (void*)restrictedFunction},
	{"specialGetPoolRegister", "()Ljava/lang/Object;", (void*)restrictedFunction},
	{"specialGetPoolRegisterPointer", "()J", (void*)restrictedFunction},
	{"specialGetReturnRegister", "()I", (void*)restrictedFunction},
	{"specialGetReturnRegisterLong", "()J", (void*)restrictedFunction},
	{"specialGetThreadRegister", "()Ljava/lang/Thread;", (void*)restrictedFunction},
	{"specialGetThreadRegisterPointer", "()J", (void*)restrictedFunction},
	{"specialSetExceptionRegister", "(J)V", (void*)restrictedFunction},
	{"specialSetExceptionRegister", "(Ljava/lang/Object;)V", (void*)restrictedFunction},
	{"specialSetPoolRegister", "(J)V", (void*)restrictedFunction},
	{"specialSetPoolRegister", "(Ljava/lang/Object;)V", (void*)restrictedFunction},
	{"specialSetThreadRegister", "(J)V", (void*)restrictedFunction},
	{"specialSetThreadRegister", "(Ljava/lang/Thread;)V", (void*)restrictedFunction},
};

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved)
{
	JNIEnv* env;

	// Used to indicate that something might be happened
	fprintf(stderr, "JNI Sub-Level: Loading Library...\n");

	// Support Java 7!
	return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL restrictedFunction(JNIEnv* env, jclass classy)
{
	// Call the oops method, so we can get a reasonable stack trace
	env->Throw((jthrowable)forwardCallStaticObject(env,
		"cc.squirreljme.runtime.cldc.debug.Debugging",
		"oops", "()java/lang/Error"));
}

JNIEXPORT jint JNICALL Java_cc_squirreljme_emulator_NativeBinding__1_1bindMethods
	(JNIEnv* env, jclass classy)
{
	jint rv = 0;
	
	// It is happening!
	fprintf(stderr, "JNI Sub-Level: Binding Methods...\n");

	rv |= env->RegisterNatives(env->FindClass("cc/squirreljme/jvm/Assembly"),
		assemblyMethods, sizeof(assemblyMethods) / sizeof(JNINativeMethod));
	
	rv |= mleDebugInit(env, classy);
	rv |= mleFormInit(env, classy);
	rv |= mleJarInit(env, classy);
	rv |= mleObjectInit(env, classy);
	rv |= mlePencilInit(env, classy);
	rv |= mleRuntimeInit(env, classy);
	rv |= mleTaskInit(env, classy);
	rv |= mleTerminalInit(env, classy);
	rv |= mleThreadInit(env, classy);
	
	// It happened!
	fprintf(stderr, "JNI Sub-Level: Methods are now bound!\n");

	return rv;
}
