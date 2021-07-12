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
 * Constants for class information.
 *
 * @since 2020/11/29
 */
public interface ClassInfoConstants
{
	/** Legacy class version from a long time ago. */
	short CLASS_VERSION_LEGACY =
		0;
	
	/** Class version from 2020/11/29. */
	short CLASS_VERSION_20201129 =
		1;
	
	/** Magic number for the end of file. */
	int CLASS_END_MAGIC_NUMBER =
		0x42796521;
	
	/** The magic number for the header. */
	int CLASS_MAGIC_NUMBER =
		0x00586572;
	
	/** The maximum header size. */
	short CLASS_MAXIMUM_HEADER_SIZE =
		8 + (StaticClassProperty.NUM_STATIC_PROPERTIES * 4);
	
	/** The maximum size of a JAR header. */
	int JAR_MAXIMUM_HEADER_SIZE =
		8 + (JarProperty.NUM_JAR_PROPERTIES * 4);
	
	/** The maximum size of a Pack header. */
	int PACK_MAXIMUM_HEADER_SIZE =
		8 + (PackProperty.NUM_PACK_PROPERTIES * 4);
	
	/** Magic number for the JAR. */
	int JAR_MAGIC_NUMBER =
		0x00456570;
	
	/** Magic number for the pack file. */
	int PACK_MAGIC_NUMBER =
		0x58455223;
}
