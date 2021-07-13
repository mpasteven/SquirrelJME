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
 * Properties for Jar table of contents.
 *
 * @since 2020/12/08
 */
public interface JarTocProperty
{
	/** Flags for the property. */
	byte INT_FLAGS =
		0;
	
	/** Hash code of the entry. */
	byte INT_NAME_HASHCODE =
		1;
	
	/** The name of the resource. */
	byte OFFSET_NAME =
		2;
	
	/** The size of the resource name. */
	byte SIZE_NAME =
		3;
	
	/** Offset to the resource data. */
	byte OFFSET_DATA =
		4;
	
	/** The size of the resource data. */
	byte SIZE_DATA =
		5;
	
	/** The properties available. */
	byte NUM_JAR_TOC_PROPERTIES =
		6;
}
