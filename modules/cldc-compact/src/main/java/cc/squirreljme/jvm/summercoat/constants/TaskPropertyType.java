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

/**
 * Represents the properties for tasks within SummerCoat via
 * {@link TaskBracket}.
 *
 * @since 2021/05/08
 */
public interface TaskPropertyType
{
	/** Not valid. */
	byte INVALID =
		0;
	
	/** The first class. */
	byte CLASS_FIRST =
		1;
	
	/** The last class. */
	byte CLASS_LAST =
		2;
	
	/** The number of properties. */
	byte NUM_PROPERTIES =
		3;
}
