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
 * Represents a property of a pack file.
 *
 * @since 2020/12/09
 */
public interface PackProperty
{
	/** The property based version ID. */
	byte INT_PACK_VERSION_ID =
		0;
	
	/** Number of JARs. */
	byte COUNT_TOC =
		1;
	
	/** Table of contents offset. */
	byte OFFSET_TOC =
		2;
	
	/** Table of contents size. */
	byte SIZE_TOC =
		3;
	
	/** The index of the boot JAR. */
	byte INDEX_BOOT_JAR =
		4;
	
	/** Epoch millis of creation date (high). */
	byte TIME_DATE_HIGH =
		5;
	
	/** Epoch millis of creation date (low). */
	byte TIME_DATE_LOW =
		6;
	
	/** The size of the entire ROM. */
	byte ROM_SIZE =
		7;
	
	/** The number of pack properties. */
	byte NUM_PACK_PROPERTIES =
		8;
}
