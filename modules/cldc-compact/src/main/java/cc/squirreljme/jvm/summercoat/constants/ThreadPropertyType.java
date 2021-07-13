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
import cc.squirreljme.jvm.mle.brackets.VMThreadBracket;

/**
 * Properties for {@link VMThreadBracket} within SummerCoat.
 *
 * @since 2021/05/08
 */
public interface ThreadPropertyType
{
	/** Not valid. */
	byte INVALID =
		0;
	
	/** The owning {@link TaskBracket} for this thread. */
	byte TASK =
		1;
	
	/** The number of properties. */
	byte NUM_PROPERTIES =
		2;
}
