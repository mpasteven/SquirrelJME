// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.summercoat.brackets;

import cc.squirreljme.jvm.mle.annotation.GhostObject;

/**
 * This is used to represent store information on whether a quick cast is
 * possible or not.
 * 
 * Internally the values have the given meaning:
 *  * {@code -1}: Calculated, but cast will never be successful.
 *  * {@code  0}: Not yet calculated.
 *  * {@code +1}: Successful cast, is always okay. 
 *
 * @since 2021/01/31
 */
@GhostObject
public interface QuickCastCheckBracket
{
}
