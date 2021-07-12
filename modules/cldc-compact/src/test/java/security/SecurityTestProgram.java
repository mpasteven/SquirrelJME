// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package security;

import cc.squirreljme.jvm.mle.ObjectShelf;
import cc.squirreljme.jvm.mle.TypeShelf;
import cc.squirreljme.jvm.mle.brackets.TypeBracket;
import java.security.Permission;

/**
 * This is the test program that is ran to test that security checks are
 * throwing or not throwing.
 *
 * @since 2020/07/13
 */
public class SecurityTestProgram
{
	/** No exception thrown. */
	public static final int NO_EXCEPTION =
		0;
	
	/** Security exception thrown. */
	public static final int SECURITY_EXCEPTION =
		1;
	
	/** Other exception thrown. */
	public static final int OTHER_EXCEPTION =
		3;
	
	/**
	 * Main entry point.
	 * 
	 * @param __args Program arguments.
	 * @since 2020/07/14
	 */
	public static void main(String... __args)
	{
		try
		{
			String type = __args[0];
			String name = __args[1];
			String action = (__args.length > 2 ? __args[2] : null);
			
			// Create the permission
			Permission p;
			if (action != null)
				p = (Permission)ObjectShelf.newInstance(
					TypeShelf.findType(type),
					new TypeBracket[]{TypeShelf.classToType(String.class),
					TypeShelf.classToType(String.class)}, name, action);
			else 
				p = (Permission)ObjectShelf.newInstance(
					TypeShelf.findType(type),
					new TypeBracket[]{TypeShelf.classToType(String.class)},
					name);
			
			// Test against it
			System.getSecurityManager().checkPermission(p);
			
			// Nothing
			System.exit(SecurityTestProgram.NO_EXCEPTION);
		}
		catch (SecurityException e)
		{
			System.exit(SecurityTestProgram.SECURITY_EXCEPTION);
		}
		catch (Throwable e)
		{
			System.exit(SecurityTestProgram.OTHER_EXCEPTION);
		}
	}
}
