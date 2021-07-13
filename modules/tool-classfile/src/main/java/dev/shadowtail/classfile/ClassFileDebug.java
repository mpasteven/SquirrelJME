// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile;

/**
 * Switch for performing debugging on class compilation/loading.
 *
 * @since 2021/02/07
 */
public interface ClassFileDebug
{
	/**
	 * @squirreljme.property dev.shadowtail.classfile.debug=(bool) Enables
	 * debugging for class files.
	 */
	boolean ENABLE_DEBUG =
		Boolean.getBoolean("dev.shadowtail.classfile.debug");
}
