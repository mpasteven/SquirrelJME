// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.summercoat.constants;

import cc.squirreljme.jvm.mle.brackets.TypeBracket;

/**
 * This contains properties of class info for {@link TypeBracket}.
 * 
 * Static properties are declared in {@link StaticClassProperty}.
 *
 * @since 2019/11/30
 */
public interface ClassProperty
	extends StaticClassProperty
{
	/** The pointer to the class name (in descriptor form). */
	byte MEMHANDLE_THIS_NAME_DESC =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 1;
	
	/** The allocation size of this class to store all of its fields. */
	byte SIZE_ALLOCATION =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 2;
	
	/** The base offset for instance fields in this class. */
	byte OFFSETBASE_INSTANCE_FIELDS =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 3;
	
	/** The super class data. */
	byte TYPEBRACKET_SUPER =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 4;
	
	/** Interfaces. */
	byte TYPEBRACKETS_INTERFACE_CLASSES =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 5;
	
	/** The component class. */
	byte TYPEBRACKET_COMPONENT =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 6;
	
	/** Pointer to a {@link Class} instance. */
	byte MEMHANDLE_LANG_CLASS_INSTANCE =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 7;
	
	/** The pointer to the constant pool of this class. */
	byte POOLHANDLE_POOL =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 8;
	
	/** The depth of this class. */
	byte INT_CLASSDEPTH =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 9;
	
	/** Memory handle base for static fields. */
	byte MEMHANDLE_STATIC_FIELDS =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 10;
	
	/** The pointer to the class name (in {@link Class#getName()} form). */
	byte MEMHANDLE_THIS_NAME_RUNTIME =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 11;
	
	/** All interfaces that are implemented by this class. */
	byte TYPEBRACKET_ALL_INTERFACECLASSES =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 12;
		
	/** The JAR that this class is within. */
	byte INT_JARDX =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 13;
	
	/** The function pointer for the default new method, if any (low). */
	byte FUNCPTR_DEFAULT_NEW_LO =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 14;
	
	/** The function pointer for the default new method, if any (high). */
	byte FUNCPTR_DEFAULT_NEW_HI =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 15;
	
	/** The memory pointer to the ROM class in memory (low). */
	byte MEMPTR_ROM_CLASS_LO =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 16;
	
	/** The memory pointer to the ROM class in memory (high). */
	byte MEMPTR_ROM_CLASS_HI =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 17;
	
	/** The cell size for array components, if this is an array. */
	byte INT_COMPONENT_CELL_SIZE =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 18;
	
	/** The allocation kind for the memory handle. */
	byte INT_MEMHANDLE_KIND =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 19;
	
	/** The VTable of the class. */
	byte MEMHANDLE_VTABLE =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 20;
	
	/** The interface tables that are available for a class. */
	byte MEMHANDLE_I2XTABLE =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 21;
	
	/** The static invocation table. */
	byte MEMHANDLE_STABLE =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 22;
	
	/** The mask potential for the I2X Table. */
	byte MASK_I2XTABLE =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 23;
	
	/** The root component class information. */
	byte TYPEBRACKET_ROOT_COMPONENT =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 24;
	
	/** Is this class initialized? */
	byte BOOLEAN_IS_INITIALIZED =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 25;
	
	/** The method pointer to the class static initializer (low). */
	byte FUNCPTR_CLINIT_LO =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 26;
	
	/** The method pointer to the class static initializer (high). */
	byte FUNCPTR_CLINIT_HI =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 27;
	
	/** The previous class in the class chain. */
	byte TYPEBRACKET_LINK_CLASS_PREV =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 28;
	
	/** The next class in the class chain. */
	byte TYPEBRACKET_LINK_CLASS_NEXT =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 29;
	
	/** The number of properties available. */
	byte NUM_RUNTIME_PROPERTIES =
		StaticClassProperty.NUM_STATIC_PROPERTIES + 30;
}
