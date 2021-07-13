// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package java.lang;

import java.security.BasicPermission;

/**
 * This is a permission which may be available at runtime.
 *
 * The following permissions are available:
 * {@code "exitVM.(n)"} -- Exiting the VM with the given error code, if
 * an asterisk {@code '*'} is used then any exit code is specified.
 * {@code "setSecurityManager"} -- Setting a new security manager.
 * {@code "createSecurityManager"} -- Creating a new security manager.
 * {@code "setIO"} -- Setting {@link System#out} and {@link System#err}.
 * {@code "modifyThread"} -- Allows one to interrupt threads, set priority,
 * and rename them.
 *
 * @since 2018/09/18
 */
public final class RuntimePermission
	extends BasicPermission
{
	/**
	 * Initializes the permission.
	 *
	 * @param __name The name to check.
	 * @throws IllegalArgumentException If name is empty.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/09/18
	 */
	public RuntimePermission(String __name)
		throws IllegalArgumentException, NullPointerException
	{
		this(__name, null);
	}
	
	/**
	 * Invokes {@code RuntimePermission(__name)}, the action is not used.
	 *
	 * @param __name The name to check.
	 * @param __act This is ignored.
	 * @throws IllegalArgumentException If name is empty.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/09/18
	 */
	public RuntimePermission(String __name, String __act)
		throws IllegalArgumentException, NullPointerException
	{
		super(__name, __act);
	}
}

