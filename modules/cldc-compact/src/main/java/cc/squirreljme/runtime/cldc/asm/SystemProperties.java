// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.runtime.cldc.asm;

import cc.squirreljme.runtime.cldc.annotation.Api;
import cc.squirreljme.runtime.cldc.lang.ApiLevel;

/**
 * Access to system properties.
 *
 * @since 2018/09/20
 */
@Deprecated
public final class SystemProperties
{
	/**
	 * Not used.
	 *
	 * @since 2018/09/20
	 */
	@Deprecated
	private SystemProperties()
	{
	}
	
	/**
	 * The class to use for a given implementation of something.
	 *
	 * @param __n The class name to lookup.
	 * @return The class that should get its instance created or {@code null}
	 * if there is no implementation.
	 * @since 2018/12/13
	 */
	@Deprecated
	@Api(ApiLevel.LEVEL_SQUIRRELJME_0_2_0_20181225)
	public static native String implementationClass(String __n);
	
	/**
	 * Returns the type of operating SquirrelJME is running on.
	 *
	 * @return The type of operating system SquirrelJME is running on.
	 * @since 2018/10/14
	 */
	@Deprecated
	@Api(ApiLevel.LEVEL_SQUIRRELJME_0_2_0_20181225)
	public static native int operatingSystemType();
}

