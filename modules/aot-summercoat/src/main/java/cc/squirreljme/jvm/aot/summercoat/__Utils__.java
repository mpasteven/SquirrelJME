// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.aot.summercoat;

import cc.squirreljme.jvm.summercoat.constants.JarProperty;
import cc.squirreljme.jvm.summercoat.constants.StaticClassProperty;

/**
 * Utilities.
 *
 * @since 2021/05/16
 */
final class __Utils__
{
	/**
	 * Returns the class property as a string.
	 * 
	 * @param __prop The {@link StaticClassProperty}
	 * @return The string form of the property.
	 * @since 2021/05/16
	 */
	public static String classPropertyToString(int __prop)
	{
		switch (__prop)
		{
			case StaticClassProperty.INT_CLASS_VERSION_ID:
				return "classVersionId";
			case StaticClassProperty.INDEX_BOOT_METHOD:
				return "indexOfBootMethod";
			case StaticClassProperty.INT_DATA_TYPE:
				return "dataType";
			case StaticClassProperty.INT_CLASS_FLAGS:
				return "classFlags";
			case StaticClassProperty.SPOOL_THIS_CLASS_NAME:
				return "indexOfThisName";
			case StaticClassProperty.SPOOL_SUPER_CLASS_NAME:
				return "indexOfSuperName";
			case StaticClassProperty.SPOOL_INTERFACES:
				return "indexOfInterfaceNames";
			case StaticClassProperty.INT_CLASS_TYPE:
				return "classType";
			case StaticClassProperty.INT_CLASS_VERSION:
				return "javaClassVersion";
			case StaticClassProperty.SPOOL_SOURCE_FILENAME:
				return "indexOfSourceFileName";
			case StaticClassProperty.INT_STATIC_FIELD_COUNT:
				return "staticFieldCount";
			case StaticClassProperty.INT_STATIC_FIELD_BYTES:
				return "staticFieldBytes";
			case StaticClassProperty.INT_STATIC_FIELD_OBJECTS:
				return "staticFieldObjects";
			case StaticClassProperty.OFFSET_STATIC_FIELD_DATA:
				return "offsetOfStaticFieldData";
			case StaticClassProperty.SIZE_STATIC_FIELD_DATA:
				return "sizeOfStaticFieldData";
			case StaticClassProperty.INT_INSTANCE_FIELD_COUNT:
				return "instanceFieldCount";
			case StaticClassProperty.INT_INSTANCE_FIELD_BYTES:
				return "instanceFieldBytes";
			case StaticClassProperty.INT_INSTANCE_FIELD_OBJECTS:
				return "instanceFieldObjects";
			case StaticClassProperty.OFFSET_INSTANCE_FIELD_DATA:
				return "offsetOfInstanceFieldData";
			case StaticClassProperty.SIZE_INSTANCE_FIELD_DATA:
				return "sizeOfInstanceFieldData";
			case StaticClassProperty.INT_STATIC_METHOD_COUNT:
				return "staticMethodCount";
			case StaticClassProperty.OFFSET_STATIC_METHOD_DATA:
				return "offsetOfStaticMethodData";
			case StaticClassProperty.SIZE_STATIC_METHOD_DATA:
				return "sizeOfStaticMethodData";
			case StaticClassProperty.INT_INSTANCE_METHOD_COUNT:
				return "instanceMethodCount";
			case StaticClassProperty.OFFSET_INSTANCE_METHOD_DATA:
				return "offsetOfInstanceMethodData";
			case StaticClassProperty.SIZE_INSTANCE_METHOD_DATA:
				return "sizeOfInstanceMethodData";
			case StaticClassProperty.INT_UUID_HI:
				return "hiId";
			case StaticClassProperty.INT_UUID_LO:
				return "loId";
			case StaticClassProperty.INT_FILE_SIZE:
				return "fileSize";
			case StaticClassProperty.OFFSET_STATIC_POOL:
				return "offsetOfStaticPool";
			case StaticClassProperty.SIZE_STATIC_POOL:
				return "sizeOfStaticPool";
			case StaticClassProperty.OFFSET_RUNTIME_POOL:
				return "offsetOfRuntimePool";
			case StaticClassProperty.SIZE_RUNTIME_POOL:
				return "sizeOfRuntimePool";
			case StaticClassProperty.OFFSET_BOOT_METHOD:
				return "offsetOfBootMethod";
			case StaticClassProperty.NUM_DIMENSIONS:
				return "numDimensions";
			case StaticClassProperty.BOOLEAN_ROOT_IS_OBJECT:
				return "isRootIsObject";
			case StaticClassProperty.BOOLEAN_IS_PRIMITIVE:
				return "isPrimitive";
			
			default:
				return String.format("UNKNOWN#%d", __prop);
		}
	}
	
	/**
	 * Returns the jar property as a string.
	 * 
	 * @param __prop The {@link JarProperty}
	 * @return The string form of the property.
	 * @since 2021/05/16
	 */
	public static String jarPropertyToString(int __prop)
	{
		switch (__prop)
		{
			case JarProperty.INT_JAR_VERSION_ID:
				return "jarVersionId";
			case JarProperty.COUNT_TOC:
				return "tableCount";
			case JarProperty.OFFSET_TOC:
				return "offsetOfTable";
			case JarProperty.SIZE_TOC:
				return "sizeOfTable";
			case JarProperty.RCDX_MANIFEST:
				return "indexOfManifest";
			case JarProperty.OFFSET_BOOT_INIT:
				return "offsetOfBootInit";
			case JarProperty.SIZE_BOOT_INIT:
				return "sizeOfBootInit";
			case JarProperty.MEMHANDLEID_START_POOL:
				return "memHandleStartPool";
			case JarProperty.RCDX_START_CLASS:
				return "indexOfStartClass";
			case JarProperty.OFFSET_STATIC_POOL:
				return "offsetOfStaticPool";
			case JarProperty.SIZE_STATIC_POOL:
				return "sizeOfStaticPool";
			case JarProperty.OFFSET_RUNTIME_POOL:
				return "offsetOfRuntimePool";
			case JarProperty.SIZE_RUNTIME_POOL:
				return "sizeOfRuntimePool";
			case JarProperty.SIZE_BASE_ARRAY:
				return "sizeBaseArray";
			case JarProperty.MEMHANDLEID_VM_ATTRIBUTES:
				return "memHandleVmAttributes";
			
			default:
				return String.format("UNKNOWN#%d", __prop);
		}
	}
}
