// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package squirreljme.compilerbug;

/**
 * Fake linked list.
 *
 * @since 2021/06/16
 */
public class FakeLinkedList
{
	/** The list head. */
	final __Link__ _head =
		new __Link__(null, null, null);
	
	/** The list tail, this gets linked into the head. */
	final __Link__ _tail =
		new __Link__(this._head, null, null);
	
	/** Modification count. */
	protected transient int modCount;
	
	/** List size. */
	protected transient int _size;
}
