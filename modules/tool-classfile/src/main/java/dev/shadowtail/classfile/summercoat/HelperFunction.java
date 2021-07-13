// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.summercoat;

import cc.squirreljme.jvm.mle.ObjectShelf;
import cc.squirreljme.jvm.mle.TypeShelf;
import cc.squirreljme.jvm.mle.brackets.TypeBracket;
import cc.squirreljme.jvm.summercoat.LogicHandler;
import cc.squirreljme.jvm.summercoat.lle.LLEObjectShelf;
import cc.squirreljme.jvm.summercoat.lle.LLETypeShelf;
import net.multiphasicapps.classfile.ClassName;
import net.multiphasicapps.classfile.MethodDescriptor;
import net.multiphasicapps.classfile.MethodName;
import net.multiphasicapps.classfile.MethodNameAndType;

/**
 * Helper function for JVM calls, calls into {@link LogicHandler}.
 *
 * @since 2020/11/28
 */
public enum HelperFunction
{
	/** {@link LLEObjectShelf#arrayCheckStore(int, int)}. */
	ARRAY_CHECK_STORE(
		"cc/squirreljme/jvm/summercoat/lle/LLEObjectShelf",
		"arrayCheckStore",
		"(II)Z"),
	
	/** {@link LogicHandler#typeGetProperty(int, int)}. */
	TYPE_GET_PROPERTY(
		"cc/squirreljme/jvm/summercoat/LogicHandler",
		"typeGetProperty",
		"(II)I"),
	
	/** {@link LogicHandler#gcMemHandle(int)}.  */
	GC_MEM_HANDLE("cc/squirreljme/jvm/summercoat/LogicHandler",
		"gcMemHandle", "(I)V"),
	
	/** {@link TypeShelf#initClass(TypeBracket)}.  */
	INIT_CLASS("cc/squirreljme/jvm/summercoat/lle/LLETypeShelf",
		"initClass", "(Lcc/squirreljme/jvm/mle/" +
		"brackets/TypeBracket;)V"),
	
	/** {@link TypeShelf#isClassInit(TypeBracket)}.  */
	IS_CLASS_INIT("cc/squirreljme/jvm/summercoat/lle/LLETypeShelf",
		"isClassInit", 
		"(Lcc/squirreljme/jvm/mle/brackets/TypeBracket;)Z"),
	
	/** {@link ObjectShelf#isArray(Object)}. */
	IS_ARRAY("cc/squirreljme/jvm/mle/ObjectShelf",
		"isArray", "(Ljava/lang/Object;)Z"),
	
	/** {@link LLEObjectShelf#isInstance(int, int)}. */
	IS_INSTANCE("cc/squirreljme/jvm/summercoat/lle/LLEObjectShelf",
		"isInstance", "(II)Z"),
	
	/** {@link LLEObjectShelf#arrayNew(TypeBracket, int)}. */
	NEW_ARRAY("cc/squirreljme/jvm/summercoat/lle/LLEObjectShelf",
		"arrayNew", "(Lcc/squirreljme/jvm/mle/brackets" +
		"/TypeBracket;I)Ljava/lang/Object;"),
	
	/** {@link LLEObjectShelf#newInstance(TypeBracket)}. */
	NEW_INSTANCE("cc/squirreljme/jvm/summercoat/lle/LLEObjectShelf", 
		"newInstance", "(Lcc/squirreljme/jvm/mle/" +
		"brackets/TypeBracket;)Ljava/lang/Object;"),
	
	/** {@link LLETypeShelf#objectType(int)}. */
	OBJECT_TYPE_BRACKET(
		"cc/squirreljme/jvm/summercoat/lle/LLETypeShelf",
		"objectType", "(I)I"),
	
	/** {@link LogicHandler#staticVmAttribute(int)}. */ 
	STATIC_VM_ATTRIBUTE("cc/squirreljme/jvm/summercoat/LogicHandler",
		"staticVmAttribute", "(I)I"),
	
	/** End. */
	;
	
	/** The class to call in. */
	public final ClassName inClass;
	
	/** The member to reference. */
	public final MethodNameAndType member;
	
	/**
	 * Initializes the helper information.
	 * 
	 * @param __name The name of the member.
	 * @param __type The type of the member.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/28
	 */
	HelperFunction(String __class, String __name, String __type)
		throws NullPointerException
	{
		if (__class == null || __name == null || __type == null)
			throw new NullPointerException("NARG");
		
		this.inClass = new ClassName(__class);
		this.member = new MethodNameAndType(new MethodName(__name),
			new MethodDescriptor(__type));
	}
}
