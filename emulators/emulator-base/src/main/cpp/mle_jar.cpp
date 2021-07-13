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
#define JARSHELF_CLASSNAME "cc/squirreljme/emulator/EmulatedJarPackageShelf"

#define JARSHELF_CLASSPATH_DESC "()[Lcc/squirreljme/jvm/mle/brackets/JarPackageBracket;"
#define JARSHELF_LIBRARIES_DESC "()[Lcc/squirreljme/jvm/mle/brackets/JarPackageBracket;"
#define JARSHELF_LIBRARYPATH_DESC "(Lcc/squirreljme/jvm/mle/brackets/JarPackageBracket;)Ljava/lang/String;"
#define JARSHELF_OPENRESOURCE_DESC "(Lcc/squirreljme/jvm/mle/brackets/JarPackageBracket;Ljava/lang/String;)Ljava/io/InputStream;"

JNIEXPORT jobject JNICALL Impl_mle_JarShelf_classPath(JNIEnv* env,
	jclass classy)
{
	return forwardCallStaticObject(env, JARSHELF_CLASSNAME,
		"classPath", JARSHELF_CLASSPATH_DESC);
}

JNIEXPORT jobject JNICALL Impl_mle_JarShelf_libraries(JNIEnv* env,
	jclass classy)
{
	return forwardCallStaticObject(env, JARSHELF_CLASSNAME,
		"libraries", JARSHELF_LIBRARIES_DESC);
}

JNIEXPORT jobject JNICALL Impl_mle_JarShelf_libraryPath(JNIEnv* env,
	jclass classy, jobject jar)
{
	return forwardCallStaticObject(env, JARSHELF_CLASSNAME,
		"libraryPath", JARSHELF_LIBRARYPATH_DESC,
		jar);
}

JNIEXPORT jobject JNICALL Impl_mle_JarShelf_openResource(JNIEnv* env,
	jclass classy, jobject jar, jobject rcName)
{
	return forwardCallStaticObject(env, JARSHELF_CLASSNAME,
		"openResource", JARSHELF_OPENRESOURCE_DESC,
		jar, rcName);
}

static const JNINativeMethod mleJarMethods[] =
{
	{"classPath", JARSHELF_CLASSPATH_DESC, (void*)Impl_mle_JarShelf_classPath},
	{"libraries", JARSHELF_LIBRARIES_DESC, (void*)Impl_mle_JarShelf_libraries},
	{"libraryPath", JARSHELF_LIBRARYPATH_DESC, (void*)Impl_mle_JarShelf_libraryPath},
	{"openResource", JARSHELF_OPENRESOURCE_DESC, (void*)Impl_mle_JarShelf_openResource},
};

jint JNICALL mleJarInit(JNIEnv* env, jclass classy)
{
	return env->RegisterNatives(
		env->FindClass("cc/squirreljme/jvm/mle/JarPackageShelf"),
		mleJarMethods, sizeof(mleJarMethods) / sizeof(JNINativeMethod));
}
