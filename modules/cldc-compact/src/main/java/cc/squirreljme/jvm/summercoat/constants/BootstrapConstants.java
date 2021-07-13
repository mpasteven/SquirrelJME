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
 * Constants used by the bootstrap.
 *
 * @since 2020/12/16
 */
public interface BootstrapConstants
{
	/** Security bits for the memory handles. */
	int HANDLE_SECURITY_BITS =
		0b0101_1001__0000_0000___0000_0000__0000_0000;
	
	/** Mask for the security bits. */
	int HANDLE_SECURITY_MASK =
		0b1111_1111__0000_0000___0000_0000__0000_0000;
	
	/** Mask for the ID. */
	int HANDLE_ID_MASK =
		0b0000_0000__1111_1111___1111_1111__1111_1111;
	
	/** Guard for action sequences. */
	int ACTION_SEQ_GUARD =
		0xE3F4C2B1;
	
	/** Guard for memory sequences. */
	int MEMORY_SEQ_GUARD =
		0xFEFFEFFF;
	
	/** Preload sequence guard. */
	int PRE_SEQ_GUARD =
		0x8651DE91;
	
	/** Flag for memory handle actions. */
	byte ACTION_MEMHANDLE = 
		0x10;
	
	/** Boot Jar Pointer seed, for long and short value. */
	byte ACTION_BOOTJARP_SEED =
		0x20;
	
	/** Boot Jar Pointer action, A (low/high) value. */
	byte ACTION_BOOTJARP_A =
		0x30;
	
	/** Boot Jar Pointer action, B (low/high) value. */
	byte ACTION_BOOTJARP_B =
		0x40;
	
	/** The mask for actions. */
	byte ACTION_MASK =
		0x70;
}
