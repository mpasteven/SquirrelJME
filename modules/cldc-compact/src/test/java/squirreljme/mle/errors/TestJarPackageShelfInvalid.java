// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package squirreljme.mle.errors;

import cc.squirreljme.jvm.mle.JarPackageShelf;
import cc.squirreljme.jvm.mle.exceptions.MLECallError;

/**
 * Tests {@link JarPackageShelf}.
 *
 * @since 2020/06/22
 */
public class TestJarPackageShelfInvalid
	extends __BaseMleErrorTest__
{
	/**
	 * {@inheritDoc}
	 * @since 2020/06/22
	 */
	@SuppressWarnings({"resource", "ResultOfMethodCallIgnored"})
	@Override
	public boolean test(int __index)
		throws MLECallError
	{
		switch (__index)
		{
			case 0:
				JarPackageShelf.openResource(null, null);
				break;
			
			case 1:
				JarPackageShelf.openResource(null, "hello");
				break;
			
			case 2:
				JarPackageShelf.openResource(
					JarPackageShelf.classPath()[0], null);
				break;
			
			case 3:
				JarPackageShelf.equals(null, null);
				break;
			
			case 4:
				JarPackageShelf.equals(
					JarPackageShelf.classPath()[0], null);
				break;
			
			case 5:
				JarPackageShelf.equals(
					null, JarPackageShelf.classPath()[0]);
				break;
			
			case 6:
				JarPackageShelf.libraryPath(null);
				break;
			
			default:
				return true;
		}
		
		return false;
	}
}
