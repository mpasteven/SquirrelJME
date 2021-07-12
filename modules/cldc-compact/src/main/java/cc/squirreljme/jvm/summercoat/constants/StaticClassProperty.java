// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.summercoat.constants;

/**
 * Static class information properties, these are properties that are stored
 * within minimized class files.
 * 
 * Run-time properties are handled in {@link ClassProperty}.
 *
 * @since 2020/11/29
 */
public interface StaticClassProperty
{
	/** The property based version ID. */
	byte INT_CLASS_VERSION_ID =
		0;
	
	/** The index of the method which is the bootstrap entry point. */
	byte INDEX_BOOT_METHOD =
		1;
	
	/**
	 * The data type of the class
	 * {@code dev.shadowtail.classfile.xlate.DataType}.
	 */
	byte INT_DATA_TYPE =
		2;
	
	/** Class flags. */
	byte INT_CLASS_FLAGS =
		3;
	
	/** Name of class. */
	byte SPOOL_THIS_CLASS_NAME =
		4;
	
	/** Super class name. */
	byte SPOOL_SUPER_CLASS_NAME =
		5;
	
	/** Interfaces in class. */
	byte SPOOL_INTERFACES =
		6;
	
	/** Class type ({@code net.multiphasicapps.classfile.ClassType}). */
	byte INT_CLASS_TYPE =
		7;
	
	/** Class version {@code net.multiphasicapps.classfile.ClassVersion}. */
	byte INT_CLASS_VERSION =
		8;
	
	/** Class source filename. */
	byte SPOOL_SOURCE_FILENAME =
		9;
	
	/** Static field count. */
	byte INT_STATIC_FIELD_COUNT =
		10;
	
	/** Static field bytes. */
	byte INT_STATIC_FIELD_BYTES =
		11;
	
	/** Static field objects. */
	byte INT_STATIC_FIELD_OBJECTS =
		12;
	
	/** Static field data offset. */
	byte OFFSET_STATIC_FIELD_DATA =
		13;
	
	/** Static field data size. */
	byte SIZE_STATIC_FIELD_DATA =
		14;
	
	/** Instance field count. */
	byte INT_INSTANCE_FIELD_COUNT =
		15;
	
	/** Instance field bytes. */
	byte INT_INSTANCE_FIELD_BYTES =
		16;
	
	/** Instance field objects. */
	byte INT_INSTANCE_FIELD_OBJECTS =
		17;
	
	/** Instance field data offset. */
	byte OFFSET_INSTANCE_FIELD_DATA =
		18;
	
	/** Instance field data size. */
	byte SIZE_INSTANCE_FIELD_DATA =
		19;
	
	/** Static method count. */
	byte INT_STATIC_METHOD_COUNT =
		20;
	
	/** Static method data offset. */
	byte OFFSET_STATIC_METHOD_DATA =
		21;
	
	/** Static method data size. */
	byte SIZE_STATIC_METHOD_DATA =
		22;
	
	/** Instance method count. */
	byte INT_INSTANCE_METHOD_COUNT =
		23;
	
	/** Instance method data offset. */
	byte OFFSET_INSTANCE_METHOD_DATA =
		24;
	
	/** Instance method data size. */
	byte SIZE_INSTANCE_METHOD_DATA =
		25;
	
	/** High bits for UUID. */
	byte INT_UUID_HI =
		26;
	
	/** Low bits for UUID. */
	byte INT_UUID_LO =
		27;
	
	/** File size. */
	byte INT_FILE_SIZE =
		28;
	
	/** Static constant pool offset. */
	byte OFFSET_STATIC_POOL =
		29;
	
	/** Static constant pool size. */
	byte SIZE_STATIC_POOL =
		30;
	
	/** Runtime constant pool offset. */
	byte OFFSET_RUNTIME_POOL =
		31;
	
	/** Runtime constant pool size. */
	byte SIZE_RUNTIME_POOL =
		32;
	
	/** The offset to the boot class. */
	byte OFFSET_BOOT_METHOD =
		33;
	
	/** The number of dimensions used. */
	byte NUM_DIMENSIONS =
		34;
	
	/** Is the component type the {@link Object} or one of its arrays? */
	byte BOOLEAN_ROOT_IS_OBJECT =
		35;
	
	/** Is this a primitive type? */
	byte BOOLEAN_IS_PRIMITIVE =
		36;
	
	/** Offset to the class name. */
	byte OFFSETOF_DEBUG_SIGNATURE =
		37;
	
	/** Size of the class name. */
	byte SIZEOF_DEBUG_SIGNATURE =
		38;
	
	/** The number of static properties. */
	byte NUM_STATIC_PROPERTIES =
		39;
	
	/** Static field base index. */
	@Deprecated
	byte BASEDX_STATIC_FIELD =
		StaticClassProperty.INT_STATIC_FIELD_COUNT;
	
	/** Instance field base index. */
	@Deprecated
	byte BASEDX_INSTANCE_FIELD =
		StaticClassProperty.INT_INSTANCE_FIELD_COUNT;
	
	/** Static method base index. */
	@Deprecated
	byte BASEDX_STATIC_METHOD =
		StaticClassProperty.INT_STATIC_METHOD_COUNT;
	
	/** Instance method base index. */
	@Deprecated
	byte BASEDX_INSTANCE_METHOD =
		StaticClassProperty.INT_INSTANCE_METHOD_COUNT;
	
	/** Field count (Base index). */
	@Deprecated
	byte BASEDX_INT_X_FIELD_COUNT =
		0;
	
	/** Field bytes (Base index). */
	@Deprecated
	byte BASEDX_INT_X_FIELD_BYTES =
		1;
	
	/** Field objects (Base index). */
	@Deprecated
	byte BASEDX_INT_X_FIELD_OBJECTS =
		2;
	
	/** Field data offset (Base index). */
	@Deprecated
	byte BASEDX_OFFSET_X_FIELD_DATA =
		3;
	
	/** Field data size (Base index). */
	@Deprecated
	byte BASEDX_SIZE_X_FIELD_DATA =
		4;
		
	/** Method count (Base index). */
	@Deprecated
	byte BASEDX_INT_X_METHOD_COUNT =
		0;
	
	/** Method data offset (Base index). */
	@Deprecated
	byte BASEDX_OFFSET_X_METHOD_DATA =
		1;
	
	/** Method data size (Base index). */
	@Deprecated
	byte BASEDX_SIZE_X_METHOD_DATA =
		2;
}
