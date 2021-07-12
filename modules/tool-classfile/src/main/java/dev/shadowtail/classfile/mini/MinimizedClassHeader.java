// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.mini;

import cc.squirreljme.jvm.summercoat.constants.ClassInfoConstants;
import cc.squirreljme.jvm.summercoat.constants.StaticClassProperty;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import net.multiphasicapps.classfile.InvalidClassFormatException;

/**
 * This represents the raw minimized header of a class.
 *
 * @since 2019/04/16
 */
public final class MinimizedClassHeader
{
	/** The format version of the class. */
	protected final short formatVersion;
	
	/** The properties of the class. */
	private final int[] _properties;
	
	/**
	 * Initializes the class header.
	 *
	 * @param __fVer The format version of the class.
	 * @param __props The property values.
	 * @throws IllegalArgumentException If there are too many properties.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/16
	 */
	public MinimizedClassHeader(short __fVer, int... __props)
		throws IllegalArgumentException, NullPointerException
	{
		if (__props == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error JC4t Too many properties were passed to the
		// class file.}
		if (__props.length > StaticClassProperty.NUM_STATIC_PROPERTIES)
			throw new IllegalArgumentException("JC4t " + __props.length);
		
		this.formatVersion = __fVer;
		this._properties = Arrays.copyOf(__props,
			StaticClassProperty.NUM_STATIC_PROPERTIES);
	}
	
	/**
	 * Gets the specified class information property.
	 * 
	 * @param __property The {@link StaticClassProperty} to get.
	 * @return The property value.
	 * @throws IllegalArgumentException If the property is not valid.
	 * @since 2020/11/29
	 */
	public final int get(int __property)
		throws IllegalArgumentException
	{
		// {@squirreljme.error JC4s Invalid class property. (The property)}
		if (__property < 0 ||
			__property >= StaticClassProperty.NUM_STATIC_PROPERTIES)
			throw new IllegalArgumentException("JC4s " + __property);
		
		return this._properties[__property];
	}
	
	/** Class flags. */
	@Deprecated
	public int getClassflags()
	{
		return this.get(StaticClassProperty.INT_CLASS_FLAGS);
	}
	
	/** Interfaces in class. */
	@Deprecated
	public int getClassints()
	{
		return this.get(StaticClassProperty.SPOOL_INTERFACES);
	}
	
	/** Name of class. */
	@Deprecated
	public int getClassname()
	{
		return this.get(StaticClassProperty.SPOOL_THIS_CLASS_NAME);
	}
	
	/** Super class name. */
	@Deprecated
	public int getClasssuper()
	{
		return this.get(StaticClassProperty.SPOOL_SUPER_CLASS_NAME);
	}
	
	/** File size. */
	@Deprecated
	public int getFilesize()
	{
		return this.get(StaticClassProperty.INT_FILE_SIZE);
	}
	
	/** Instance field bytes. */
	@Deprecated
	public int getIfbytes()
	{
		return this.get(StaticClassProperty.INT_INSTANCE_FIELD_BYTES);
	}
	
	/** Instance field count. */
	@Deprecated
	public int getIfcount()
	{
		return this.get(StaticClassProperty.INT_INSTANCE_FIELD_COUNT);
	}
	
	/** Instance field objects. */
	@Deprecated
	public int getIfobjs()
	{
		return this.get(StaticClassProperty.INT_INSTANCE_FIELD_OBJECTS);
	}
	
	/** Instance field data offset. */
	@Deprecated
	public int getIfoff()
	{
		return this.get(StaticClassProperty.OFFSET_INSTANCE_FIELD_DATA);
	}
	
	/** Instance field data size. */
	@Deprecated
	public int getIfsize()
	{
		return this.get(StaticClassProperty.SIZE_INSTANCE_FIELD_DATA);
	}
	
	/** Instance method count. */
	@Deprecated
	public int getImcount()
	{
		return this.get(StaticClassProperty.INT_INSTANCE_METHOD_COUNT);
	}
	
	/** Instance method data offset. */
	@Deprecated
	public int getImoff()
	{
		return this.get(StaticClassProperty.OFFSET_INSTANCE_METHOD_DATA);
	}
	
	/** Instance method data size. */
	@Deprecated
	public int getImsize()
	{
		return this.get(StaticClassProperty.SIZE_INSTANCE_METHOD_DATA);
	}
	
	/** Runtime constant pool offset. */
	@Deprecated
	public int getRuntimepooloff()
	{
		return this.get(StaticClassProperty.OFFSET_RUNTIME_POOL);
	}
	
	/** Runtime constant pool size. */
	@Deprecated
	public int getRuntimepoolsize()
	{
		return this.get(StaticClassProperty.SIZE_RUNTIME_POOL);
	}
	
	/** Static field count. */
	@Deprecated
	public int getSfcount()
	{
		return this.get(StaticClassProperty.INT_STATIC_FIELD_COUNT);
	}
	
	/** Static field data offset. */
	@Deprecated
	public int getSfoff()
	{
		return this.get(StaticClassProperty.OFFSET_STATIC_FIELD_DATA);
	}
	
	/** Static field data size. */
	@Deprecated
	public int getSfsize()
	{
		return this.get(StaticClassProperty.SIZE_STATIC_FIELD_DATA);
	}
	
	/** Static method count. */
	@Deprecated
	public int getSmcount()
	{
		return this.get(StaticClassProperty.INT_STATIC_METHOD_COUNT);
	}
	
	/** Static method data offset. */
	@Deprecated
	public int getSmoff()
	{
		return this.get(StaticClassProperty.OFFSET_STATIC_METHOD_DATA);
	}
	
	/** Static method data size. */
	@Deprecated
	public int getSmsize()
	{
		return this.get(StaticClassProperty.SIZE_STATIC_METHOD_DATA);
	}
	
	/** Static constant pool offset. */
	@Deprecated
	public int getStaticpooloff()
	{
		return this.get(StaticClassProperty.OFFSET_STATIC_POOL);
	}
	
	/** Static constant pool size. */
	@Deprecated
	public int getStaticpoolsize()
	{
		return this.get(StaticClassProperty.SIZE_STATIC_POOL);
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
	 * Decodes the minimized class header.
	 *
	 * @param __is The bytes to decode from.
	 * @return The resulting minimized class header.
	 * @throws InvalidClassFormatException If the class is not formatted
	 * correctly.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/16
	 */
	public static MinimizedClassHeader decode(InputStream __is)
		throws InvalidClassFormatException, IOException, NullPointerException
	{
		if (__is == null)
			throw new NullPointerException("NARG");
		
		DataInputStream dis = new DataInputStream(__is);
		
		// {@squirreljme.error JC04 Invalid minimized class magic number.
		// (The magic number; The expected magic)}
		int magic;
		if (ClassInfoConstants.CLASS_MAGIC_NUMBER != (magic = dis.readInt()))
			throw new InvalidClassFormatException(String.format(
				"JC04 %08x %08x",
				magic, ClassInfoConstants.CLASS_MAGIC_NUMBER));
		
		// {@squirreljme.error JC4u Cannot decode class because the version
		// identifier is not known. (The format version of the class)}
		int formatVersion = dis.readUnsignedShort();
		if (formatVersion != ClassInfoConstants.CLASS_VERSION_20201129)
			throw new RuntimeException("JC4u " + formatVersion);
		
		// Read in all the data
		int numProperties = dis.readUnsignedShort();
		int[] properties = new int[numProperties];
		for (int i = 0; i < numProperties; i++)
			properties[i] = dis.readInt();
		
		// Setup finalized class
		return new MinimizedClassHeader((short)formatVersion, properties);
	}	
}

