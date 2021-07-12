// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package squirreljme.mle.errors;

import cc.squirreljme.jvm.mle.ObjectShelf;
import cc.squirreljme.jvm.mle.TypeShelf;
import cc.squirreljme.jvm.mle.brackets.TypeBracket;
import cc.squirreljme.jvm.mle.exceptions.MLECallError;

/**
 * Tests {@link ObjectShelf}.
 *
 * @since 2020/06/22
 */
public class TestObjectShelfInvalid
	extends __BaseMleErrorTest__
{
	/**
	 * {@inheritDoc}
	 * @since 2020/06/22
	 */
	@Override
	public boolean test(int __index)
		throws MLECallError
	{
		switch (__index)
		{
			case 0:
				ObjectShelf.arrayNew(null, -1);
				break;
			
			case 1:
				ObjectShelf.arrayNew(
					TypeShelf.typeOfByte(), 1234);
				break;
			
			case 2:
				ObjectShelf.arrayNew(
					TypeShelf.classToType(byte[].class), -1234);
				break;
			
			case 3:
				ObjectShelf.arrayLength(null);
				break;
			
			case 4:
				ObjectShelf.holdsLock(null, null);
				break;
			
			case 5:
				ObjectShelf.holdsLock(Thread.currentThread(), null);
				break;
			
			case 6:
				ObjectShelf.holdsLock(null, new Object());
				break;
			
			case 7:
				ObjectShelf.identityHashCode(null);
				break;
			
			case 8:
				ObjectShelf.newInstance(null);
				break;
			
			case 9:
				ObjectShelf.newInstance(TypeShelf.classToType(byte[].class));
				break;
			
			case 10:
				ObjectShelf.notify(null, false);
				break;
			
			case 11:
				ObjectShelf.wait(null, -1, -1);
				break;
			
			case 12:
				ObjectShelf.newInstance(null, null, 
					(Object[])null);
				break;
			
			case 13:
				ObjectShelf.newInstance(TypeShelf.classToType(Integer.class),
					null, (Object[])null);
				break;
			
			case 14:
				ObjectShelf.newInstance(TypeShelf.classToType(Integer.class),
					new TypeBracket[]{null}, (Object[])null);
				break;
			
			case 15:
				ObjectShelf.newInstance(TypeShelf.classToType(Integer.class),
					new TypeBracket[]{TypeShelf.typeOfInteger()},
					(Object[])null);
				break;
			
			case 16:
				ObjectShelf.newInstance(TypeShelf.classToType(Integer.class),
					new TypeBracket[]{TypeShelf.typeOfInteger()},
					10, 12);
				break;
			
			case 17:
				ObjectShelf.newInstance(TypeShelf.classToType(Integer.class),
					new TypeBracket[]{TypeShelf.typeOfInteger()},
					"Wrong argument");
				break;
			
			default:
				return true;
		}
		
		return false;
	}
}
