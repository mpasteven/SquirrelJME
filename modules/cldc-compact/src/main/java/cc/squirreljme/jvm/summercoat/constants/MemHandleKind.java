// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.summercoat.constants;

import cc.squirreljme.jvm.mle.brackets.TaskBracket;
import cc.squirreljme.jvm.mle.brackets.VMThreadBracket;

/**
 * Constants to identify the kind of memory handle that an object is.
 *
 * @since 2020/12/19
 */
public interface MemHandleKind
{
	/** Undefined. */
	byte UNDEFINED =
		0;
	
	/** Static field storage. */
	byte STATIC_FIELD_DATA =
		1;
	
	/** Class information. */
	byte CLASS_INFO =
		2;
	
	/** A list of classes. */
	byte CLASS_INFO_LIST =
		3;
	
	/** The constant pool. */
	byte POOL =
		4;
	
	/** Instance of an object. */
	byte OBJECT_INSTANCE =
		5;
	
	/** Virtual method VTable. */
	byte VIRTUAL_VTABLE =
		6;
	
	/** Interface method VTable. */
	byte INTERFACE_VTABLE =
		7;
	
	/** Boolean array (really byte). */
	byte BOOLEAN_ARRAY =
		8;
	
	/** Boolean/byte array. */
	byte BYTE_ARRAY =
		9;
	
	/** Short array. */
	byte SHORT_ARRAY =
		10;
	
	/** Character array. */
	byte CHARACTER_ARRAY =
		11;
	
	/** Integer array. */
	byte INTEGER_ARRAY =
		12;
	
	/** Long array. */
	byte LONG_ARRAY =
		13;
	
	/** Float array. */
	byte FLOAT_ARRAY =
		14;
	
	/** Double array. */
	byte DOUBLE_ARRAY =
		15;
	
	/** Object (memory handle) array. */
	byte OBJECT_ARRAY =
		16;
	
	/** Static virtual Machine Attributes. */
	byte STATIC_VM_ATTRIBUTES =
		17;
	
	/** Quick cast information. */
	byte QUICK_CAST_CHECK =
		18;
	
	/** Interface I2XTable. */
	byte I2X_TABLE =
		19;
	
	/** Interface XTable. */
	byte INTERFACE_XTABLE =
		20;
	
	/** A {@link VMThreadBracket}. */
	byte VM_THREAD =
		21;
	
	/** A {@link TaskBracket}. */
	byte TASK =
		22;
	
	/** The number of kinds used. */
	byte NUM_KINDS =
		23;
}
