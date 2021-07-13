// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.nncc;

/**
 * This represents the type of argument that is used.
 *
 * @since 2019/04/16
 */
public enum ArgumentFormat
{
	/** Variable unsigned int. */
	VUINT,
	
	/** Variable register (unsigned int). */
	VUREG,
	
	/** Variable pool entry. */
	VPOOL,
	
	/** Variable jump entry. */
	VJUMP,
	
	/** Register list. */
	REGLIST,
	
	/** 32-bit integer. */
	INT32,
	
	/** 64-bit integer. */
	INT64,
	
	/** 32-bit float. */
	FLOAT32,
	
	/** 64-bit float. */
	FLOAT64,
	
	/* End. */
	;
	
	/**
	 * Returns an array of all the formats.
	 *
	 * @param __a The input formats.
	 * @return {@code __a}.
	 * @since 2019/04/16
	 */
	public static InstructionFormat of(ArgumentFormat... __a)
	{
		return new InstructionFormat(__a);
	}
}

