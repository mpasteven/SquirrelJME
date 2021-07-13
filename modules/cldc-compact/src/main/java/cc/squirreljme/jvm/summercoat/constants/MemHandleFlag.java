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
 * Flags for memory handles.
 *
 * @since 2020/12/19
 */
public interface MemHandleFlag
{
	/** Memory handle type mask. */
	byte MASK_TYPE =
		0x0F;
	
	/** Is pool information. */
	byte TYPE_POOL =
		0x01;
}
