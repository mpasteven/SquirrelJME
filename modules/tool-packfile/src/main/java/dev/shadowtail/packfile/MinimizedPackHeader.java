// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.packfile;

import cc.squirreljme.jvm.summercoat.constants.ClassInfoConstants;
import cc.squirreljme.jvm.summercoat.constants.PackProperty;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import net.multiphasicapps.classfile.InvalidClassFormatException;

/**
 * This represents the header for a minimized pack file, it mostly just
 * represents a number of JAR files which are combined into one.
 *
 * @since 2019/05/28
 */
public final class MinimizedPackHeader
{
	/** The format version of the JAR. */
	protected final short formatVersion;
	
	/** The properties of the class. */
	private final int[] _properties;
	
	/**
	 * Initializes the Pack header.
	 * 
	 * @param __fV The format version of the JAR header.
	 * @param __properties The properties for the JAR.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/09
	 */
	public MinimizedPackHeader(short __fV, int... __properties)
	{
		this.formatVersion = __fV;
		this._properties = Arrays.copyOf(__properties,
			PackProperty.NUM_PACK_PROPERTIES);
	}
	
	/**
	 * Changes a property of the pack header and returns a new one.
	 * 
	 * @param __property The property to change.
	 * @param __val The new value.
	 * @return A new header with the changed property.
	 * @throws IllegalArgumentException If the property is not valid.
	 * @since 2021/02/21
	 */
	public final MinimizedPackHeader change(int __property, int __val)
		throws IllegalArgumentException
	{
		// {@squirreljme.error BI02 Invalid Pack property. (The property)}
		if (__property < 0 ||
			__property >= PackProperty.NUM_PACK_PROPERTIES)
			throw new IllegalArgumentException("BI02 " + __property);
		
		// Build new properties
		int[] newProperties = this._properties.clone();
		newProperties[__property] = __val;
		
		// Create a new one that is changed
		return new MinimizedPackHeader(this.formatVersion, newProperties);
	}
	
	/**
	 * Gets the specified property.
	 * 
	 * @param __property The {@link PackProperty} to get.
	 * @return The property value.
	 * @throws IllegalArgumentException If the property is not valid.
	 * @since 2020/12/09
	 */
	public final int get(int __property)
		throws IllegalArgumentException
	{
		// {@squirreljme.error BI03 Invalid Pack property. (The property)}
		if (__property < 0 ||
			__property >= PackProperty.NUM_PACK_PROPERTIES)
			throw new IllegalArgumentException("BI03 " + __property);
		
		return this._properties[__property];
	}
	
	/**
	 * Returns this header as a byte array.
	 * 
	 * @return The byte array of the given header.
	 * @since 2021/02/21
	 */
	public final byte[] toByteArray()
	{
		// Where does it go?
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream(
			ClassInfoConstants.PACK_MAXIMUM_HEADER_SIZE))
		{
			// Use standard writing
			this.writeTo(baos);
			
			return baos.toByteArray();
		}
		
		// {@squirreljme.error BI06 Could not write the pack header.}
		catch (IOException __e)
		{
			throw new RuntimeException("Bi06", __e);
		}
	}
	
	/**
	 * Writes the header to the given output.
	 * 
	 * @param __out The stream to write to.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/02/21
	 */
	@SuppressWarnings("resource")
	public final void writeTo(OutputStream __out)
		throws IOException, NullPointerException
	{
		if (__out == null)
			throw new NullPointerException("NARG");
		
		// We need to write specific data
		DataOutputStream out = new DataOutputStream(__out);
		int[] properties = this._properties;
		
		// Write header
		out.writeInt(ClassInfoConstants.PACK_MAGIC_NUMBER);
		out.writeShort(ClassInfoConstants.CLASS_VERSION_20201129);
		
		// Write property count
		int n = properties.length;
		out.writeShort(n);
		
		// Write all the various properties
		for (int i = 0; i < n; i++)
			out.writeInt(properties[i]);
	}
	
	/**
	 * Decodes the input pack header.
	 * 
	 * @param __in The stream to decode from.
	 * @return The resultant header.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/09
	 */
	public static MinimizedPackHeader decode(InputStream __in)
		throws IOException, NullPointerException
	{
		if (__in == null)
			throw new NullPointerException("NARG");
		
		DataInputStream dis = new DataInputStream(__in);
		
		// {@squirreljme.error BI04 Invalid minimized Jar magic number.
		// (The magic number)}
		int magic;
		if (ClassInfoConstants.PACK_MAGIC_NUMBER != (magic = dis.readInt()))
			throw new InvalidClassFormatException(String.format("BI04 %08x",
				magic));
		
		// {@squirreljme.error BI05 Cannot decode pack file because the version
		// identifier is not known. (The format version of the pack file)}
		int formatVersion = dis.readUnsignedShort();
		if (formatVersion != ClassInfoConstants.CLASS_VERSION_20201129)
			throw new RuntimeException("BI05 " + formatVersion);
		
		// Read in all the data
		int numProperties = dis.readUnsignedShort();
		int[] properties = new int[numProperties];
		for (int i = 0; i < numProperties; i++)
			properties[i] = dis.readInt();
		
		// Setup finalized pack
		return new MinimizedPackHeader((short)formatVersion, properties);
	}
}

