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
 * Flags for the table of content entries within Jars.
 *
 * @since 2020/12/08
 */
public interface JarTocFlag
{
	/** A class that can be executed. */
	byte EXECUTABLE_CLASS =
		1;
	
	/** A resource entry. */
	byte RESOURCE =
		2;
	
	/** Manifest resource. */
	byte MANIFEST =
		4;
	
	/** Is this a boot class? */
	byte BOOT =
		8;
}
