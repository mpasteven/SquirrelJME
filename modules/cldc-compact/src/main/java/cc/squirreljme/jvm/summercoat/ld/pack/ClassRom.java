// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.summercoat.ld.pack;

import cc.squirreljme.jvm.summercoat.constants.ClassInfoConstants;
import cc.squirreljme.jvm.summercoat.constants.StaticClassProperty;
import cc.squirreljme.jvm.summercoat.ld.mem.ReadableMemory;
import cc.squirreljme.jvm.summercoat.ld.mem.ReadableMemoryInputStream;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import java.io.IOException;

/**
 * This represents a class file ROM which is in memory somewhere.
 *
 * @since 2021/07/11
 */
public final class ClassRom
{
	/** The Class data. */
	protected final ReadableMemory data;
	
	/** Header structure, dynamically load. */
	private HeaderStruct _header;
	
	/**
	 * Initializes the class ROM.
	 * 
	 * @param __data The class data.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/07/11
	 */
	public ClassRom(ReadableMemory __data)
		throws NullPointerException
	{
		if (__data == null)
			throw new NullPointerException("NARG");
		
		this.data = __data;
	}
	
	/**
	 * Potentially loads and returns the header.
	 * 
	 * @return The header.
	 * @throws InvalidRomException If the ROM is not valid.
	 * @since 2021/07/11
	 */
	public final HeaderStruct header()
		throws InvalidRomException
	{
		HeaderStruct rv = this._header;
		if (rv != null)
			return rv;
		
		// Decode the header
		try (ReadableMemoryInputStream in =
			new ReadableMemoryInputStream(this.data))
		{
			// {@squirreljme.error ZZ5c Invalid class magic number.}
			rv = HeaderStruct.decode(in,
				StaticClassProperty.NUM_STATIC_PROPERTIES);
			if (rv.magicNumber() != ClassInfoConstants.CLASS_MAGIC_NUMBER)
				throw new InvalidRomException("ZZ5c");
			
			this._header = rv;
		}
		
		// {@squirreljme.error ZZ5b The class header is corrupt.}
		catch (IOException e)
		{
			throw new InvalidRomException("ZZ5b", e);
		}
		
		return rv;
	}
	
	/**
	 * Returns the available methods within the class.
	 * 
	 * @return The methods that are available in the class ROM.
	 * @since 2021/07/12
	 */
	public final ClassRomMethods methods()
	{
		HeaderStruct.
		
		throw Debugging.todo();
	}
}
