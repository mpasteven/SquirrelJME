// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.summercoat.ld.pack;

import cc.squirreljme.jvm.summercoat.constants.ClassInfoConstants;
import java.io.DataInput;
import java.io.IOException;

/**
 * Common header structure that is shared for all ROM formats.
 *
 * @since 2021/04/07
 */
public final class HeaderStruct
{
	/** The ROM Magic Number. */
	protected final int magicNumber;
	
	/** The format version. */
	protected final int formatVersion;
	
	/** The properties used. */
	private final int[] _properties;
	
	/**
	 * Initializes the header structure.
	 * 
	 * @param __romMagic The ROM Magic number.
	 * @param __formatVersion The format version.
	 * @param __properties The properties used.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/04/07
	 */
	private HeaderStruct(int __romMagic, int __formatVersion,
		int[] __properties)
		throws NullPointerException
	{
		if (__properties == null)
			throw new NullPointerException("NARG");
		
		this.magicNumber = __romMagic;
		this.formatVersion = __formatVersion;
		this._properties = __properties;
	}
	
	/**
	 * Obtains the given property.
	 * 
	 * @param __prop The property to get.
	 * @return The value of the given property.
	 * @throws IndexOutOfBoundsException If the property is not within bounds.
	 * @since 2021/04/07
	 */
	public int getInteger(int __prop)
		throws IndexOutOfBoundsException
	{
		// {@squirreljme.error ZZ55 Invalid property. (The property)}
		int[] properties = this._properties;
		if (__prop < 0 || __prop >= properties.length)
			throw new IndexOutOfBoundsException("ZZ55 " + __prop);
		
		return properties[__prop];
	}
	
	/**
	 * Gets the long valued property.
	 * 
	 * @param __prop The property to get, the low value is first while the
	 * high value is the next property.
	 * @return The long value.
	 * @throws IndexOutOfBoundsException If the property is not within bounds.
	 * @since 2021/07/13
	 */
	@SuppressWarnings("MagicNumber")
	public long getLong(int __prop)
		throws IndexOutOfBoundsException
	{
		return (this.getInteger(__prop) & 0xFFFFFFFFL) |
			((long)this.getInteger(__prop) << 32);
	}
	
	/**
	 * Returns the magic number of the structure.
	 * 
	 * @return The structure's magic number.
	 * @since 2021/07/11
	 */
	public int magicNumber()
	{
		return this.magicNumber;
	}
	
	/**
	 * Returns the number of properties.
	 * 
	 * @return The number of properties.
	 * @since 2021/05/16
	 */
	public final int numProperties()
	{
		return this._properties.length;
	}
	
	/**
	 * Decodes the header.
	 * 
	 * @param __in The input to decode from.
	 * @param __maxProps The maximum number of valid properties.
	 * @return The header structure.
	 * @throws IOException On read errors.
	 * @since 2021/04/07
	 */
	public static HeaderStruct decode(DataInput __in, int __maxProps)
		throws IOException
	{
		// Read the magic number
		int romMagic = __in.readInt();
			
		// Read the format version
		int formatVersion = __in.readUnsignedShort();
		
		// {@squirreljme.error ZZ44 Cannot decode header because the
		// version identifier is not known. (The format version of the header
		// file)}
		if (formatVersion != ClassInfoConstants.CLASS_VERSION_20201129)
			throw new InvalidRomException("ZZ44 " + formatVersion);
		
		// Read in all the data
		int numProperties = Math.min(__in.readUnsignedShort(), __maxProps);
		int[] properties = new int[numProperties];
		for (int i = 0; i < numProperties; i++)
			properties[i] = __in.readInt();
		
		return new HeaderStruct(romMagic, formatVersion, properties);
	}
}
