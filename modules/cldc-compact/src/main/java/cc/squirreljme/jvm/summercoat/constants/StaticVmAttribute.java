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
import cc.squirreljme.jvm.summercoat.SystemCall;

/**
 * The static virtual machine metric properties, used with
 * {@link SystemCall#staticVmAttributes()}.
 *
 * @since 2021/01/24
 */
public interface StaticVmAttribute
{
	/** Not valid or used. */
	byte INVALID =
		0;
	
	/** Offset of the array length field. */
	byte OFFSETOF_ARRAY_LENGTH_FIELD =
		1;
	
	/** Offset of the object type field. */
	byte OFFSETOF_OBJECT_TYPE_FIELD =
		2;
	
	/** The size of {@link Object}. */
	byte SIZE_OBJECT =
		3;
	
	/** The type bracket for {@link Class}. */
	byte TYPEBRACKET_CLASS =
		4;
	
	/** Offset of Throwable's trace field. */
	byte OFFSETOF_THROWABLE_TRACE_FIELD =
		5;
	
	/** Offset of {@link Class}'s {@link TypeBracket} field. */
	byte OFFSETOF_CLASS_TYPEBRACKET_FIELD =
		6;
	
	/** The base size of arrays, as {@link JarProperty#SIZE_BASE_ARRAY}. */
	byte SIZE_BASE_ARRAY =
		7;
	
	/** The boot thread handle. */
	byte MEMHANDLE_BOOT_THREAD =
		8;
	
	/** The number of metrics used. */
	byte NUM_METRICS =
		9;
}
