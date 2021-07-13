// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.jarfile;

import cc.squirreljme.jvm.summercoat.constants.ClassInfoConstants;
import cc.squirreljme.jvm.summercoat.constants.JarProperty;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import net.multiphasicapps.classfile.InvalidClassFormatException;

/**
 * This represents the header for a minimized Jar file.
 *
 * @since 2019/04/27
 */
public final class MinimizedJarHeader
{
	/** The format version of the JAR. */
	protected final short formatVersion;
	
	/** The properties of the class. */
	private final int[] _properties;
	
	/**
	 * Initializes the JAR header.
	 * 
	 * @param __fV The format version of the JAR header.
	 * @param __properties The properties for the JAR.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/08
	 */
	public MinimizedJarHeader(short __fV, int... __properties)
	{
		this.formatVersion = __fV;
		this._properties = Arrays.copyOf(__properties,
			JarProperty.NUM_JAR_PROPERTIES);
	}
	
	/**
	 * Gets the specified property.
	 * 
	 * @param __property The {@link JarProperty} to get.
	 * @return The property value.
	 * @throws IllegalArgumentException If the property is not valid.
	 * @since 2020/11/29
	 */
	public final int get(int __property)
		throws IllegalArgumentException
	{
		// {@squirreljme.error BC09 Invalid Jar property. (The property)}
		if (__property < 0 ||
			__property >= JarProperty.NUM_JAR_PROPERTIES)
			throw new IllegalArgumentException("BC09 " + __property);
		
		return this._properties[__property];
	}
	
	/** The ClassInfo for {@code byte[]}. */
	public int getBootclassidba()
	{
		throw Debugging.todo();
	}
	
	/** Boot initializer offset. */
	public int getBootoffset()
	{
		return this.get(JarProperty.OFFSET_BOOT_INIT);
	}
	
	/** The boot pool offset. */
	public int getBootpool()
	{
		throw Debugging.todo();
	}
	
	/** Static field basein RAM. */
	public int getBootsfieldbase()
	{
		throw Debugging.todo();
	}
	
	/** Boot initializer size. */
	public int getBootsize()
	{
		return this.get(JarProperty.SIZE_BOOT_INIT);
	}
	
	/** The start method offset. */
	public int getBootstart()
	{
		throw Debugging.todo();
	}
	
	/** Manifest length. */
	public int getManifestlen()
	{
		throw Debugging.todo();
	}
	
	/** Manifest offset. */
	public int getManifestoff()
	{
		throw Debugging.todo();
	}
	
	/** System call handler code address .*/
	public int getSyscallhandler()
	{
		throw Debugging.todo();
	}
	
	/** System call pool address. */
	public int getSyscallpool()
	{
		throw Debugging.todo();
	}
	
	/** System call static field pointer. */
	public int getSyscallsfp()
	{
		throw Debugging.todo();
	}
	
	/**
	 * Decodes the JAR header.
	 *
	 * @param __in The input stream.
	 * @return The resulting header.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/27
	 */
	public static MinimizedJarHeader decode(InputStream __in)
		throws IOException, NullPointerException
	{
		if (__in == null)
			throw new NullPointerException("NARG");
		
		DataInputStream dis = new DataInputStream(__in);
		
		// {@squirreljme.error BC06 Invalid minimized Jar magic number.
		// (The magic number; The expected magic number)}
		int magic;
		if (ClassInfoConstants.JAR_MAGIC_NUMBER != (magic = dis.readInt()))
			throw new InvalidClassFormatException(String.format(
				"BC06 %08x %08x", magic, ClassInfoConstants.JAR_MAGIC_NUMBER));
		
		// {@squirreljme.error BC0g Cannot decode class because the version
		// identifier is not known. (The format version of the class)}
		int formatVersion = dis.readUnsignedShort();
		if (formatVersion != ClassInfoConstants.CLASS_VERSION_20201129)
			throw new RuntimeException("BC0g " + formatVersion);
		
		// Read in all the data
		int numProperties = dis.readUnsignedShort();
		int[] properties = new int[numProperties];
		for (int i = 0; i < numProperties; i++)
			properties[i] = dis.readInt();
		
		// Setup finalized class
		return new MinimizedJarHeader((short)formatVersion, properties);
	}
}

