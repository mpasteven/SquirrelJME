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
import cc.squirreljme.jvm.summercoat.constants.PackTocProperty;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.vm.PreAddressedClassLibrary;
import cc.squirreljme.vm.SummerCoatJarLibrary;
import cc.squirreljme.vm.VMClassLibrary;
import dev.shadowtail.jarfile.JarMinimizer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.multiphasicapps.classfile.InvalidClassFormatException;
import net.multiphasicapps.io.ChunkDataType;
import net.multiphasicapps.io.ChunkForwardedFuture;
import net.multiphasicapps.io.ChunkSection;
import net.multiphasicapps.io.ChunkWriter;

/**
 * This class is used to pack multiple JAR files into a single packed ROM, so
 * that it is all contained within a single unit.
 *
 * @since 2019/05/29
 */
public class PackMinimizer
{
	/**
	 * Minimizes the class library.
	 *
	 * @param __bootLib The boot JAR used for the entry point.
	 * @param __libs The libraries to minimize.
	 * @return The resulting minimized pack file.
	 * @throws IOException On read/write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/05/29
	 */
	public static byte[] minimize(String __bootLib, VMClassLibrary... __libs)
		throws IOException, NullPointerException
	{
		if (__libs == null)
			throw new NullPointerException("NARG");
		
		// Write into resulting array
		try (ByteArrayOutputStream baos =
			new ByteArrayOutputStream(1048576))
		{
			// Minimize
			PackMinimizer.minimize(baos, __bootLib, __libs);
			
			// Return result
			return baos.toByteArray();
		}
	}
	
	/**
	 * Minimizes the class library.
	 *
	 * @param __out The stream to write the minimized file to.
	 * @param __bootLib The boot JAR used for the entry point.
	 * @param __libs The libraries to minimize.
	 * @throws IOException On read/write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/05/29
	 */
	public static void minimize(OutputStream __out, String __bootLib,
		VMClassLibrary... __libs)
		throws IOException, NullPointerException
	{
		if (__out == null || __libs == null || __bootLib == null)
			throw new NullPointerException("NARG");
		
		// Make sure the boot JAR is normalized
		__bootLib = PackMinimizer.normalizeJarName(__bootLib);
		
		// Write ROM sections
		ChunkWriter out = new ChunkWriter();
		
		// Start the header and table of contents, these will be written to
		// accordingly as needed
		ChunkSection header = out.addSection(
			ChunkWriter.VARIABLE_SIZE, 4);
		
		// Magic number and minimized format, since about November 2020 there
		// is a new version format
		header.writeInt(ClassInfoConstants.PACK_MAGIC_NUMBER);
		header.writeShort(ClassInfoConstants.CLASS_VERSION_20201129);
		
		// The number of properties used, is always constant for now
		ChunkForwardedFuture[] properties = new ChunkForwardedFuture[
			PackProperty.NUM_PACK_PROPERTIES];
		header.writeUnsignedShortChecked(properties.length);
		
		// Initialize empty properties
		for (int i = 0; i < PackProperty.NUM_PACK_PROPERTIES; i++)
		{
			// Initialize the future that will later be used to initialize
			// the value.
			ChunkForwardedFuture future = new ChunkForwardedFuture();
			properties[i] = future;
			
			// This property is written here
			header.writeFuture(ChunkDataType.INTEGER, future);
		}
		
		// The size of the entire pack file
		properties[PackProperty.ROM_SIZE].set(out.futureSize());
		
		// This value is currently meaningless so for now it is always zero
		properties[PackProperty.INT_PACK_VERSION_ID].setInt(0);
		
		// Record timestamp of the packfile
		long created = System.currentTimeMillis();
		properties[PackProperty.TIME_DATE_HIGH].setInt((int)(created >>> 32));
		properties[PackProperty.TIME_DATE_LOW].setInt((int)created);
		
		// Table of contents that represents the JAR
		ChunkSection toc = out.addSection(ChunkWriter.VARIABLE_SIZE, 4);
		properties[PackProperty.COUNT_TOC].setInt(__libs.length);
		properties[PackProperty.OFFSET_TOC].set(toc.futureAddress());
		properties[PackProperty.SIZE_TOC].set(toc.futureSize());
		
		// Start the table of contents off with the number of entries and the
		// ints per entry
		toc.writeUnsignedShortChecked(__libs.length);
		toc.writeUnsignedShortChecked(PackTocProperty.NUM_PACK_TOC_PROPERTIES);
		
		// Go through each library, minimize and write!
		ChunkForwardedFuture[] tocFill = new ChunkForwardedFuture[
			PackTocProperty.NUM_PACK_TOC_PROPERTIES];
		for (int i = 0, n = __libs.length; i < n; i++)
		{
			// Reset the table of contents fill
			for (int q = 0; q < PackTocProperty.NUM_PACK_TOC_PROPERTIES; q++)
				tocFill[q] = new ChunkForwardedFuture();
			
			VMClassLibrary lib = __libs[i];
			
			// No flags are currently defined
			tocFill[PackTocProperty.INT_FLAGS].setInt(0);
			
			// Is this a pre-addressed library?
			boolean isPreAddr = (lib instanceof PreAddressedClassLibrary);
			
			// Determine the normal name of the JAR and if this is a SQC
			String name = PackMinimizer.normalizeJarName(lib.name());
			boolean isSqc = SummerCoatJarLibrary.isSqc(name);
			
			// If this is the boot library, it goes to this one
			boolean isBoot = __bootLib.equals(name);
			if (isBoot)
				properties[PackProperty.INDEX_BOOT_JAR].setInt(i);
			
			// Hash code for the JAR, to find it quicker potentially
			tocFill[PackTocProperty.INT_NAME_HASHCODE].setInt(name.hashCode());
			
			// Spill in name
			ChunkSection utfName = out.addSection(
				ChunkWriter.VARIABLE_SIZE, 4);
			utfName.writeUTF(name);
			tocFill[PackTocProperty.OFFSET_NAME].set(utfName.futureAddress());
			tocFill[PackTocProperty.SIZE_NAME].set(utfName.futureSize());
			
			// Is the offset and size of this JAR pre-addressed? If it is then
			// we do not need to compile or copy the ROM since the address
			// is already known.
			if (isPreAddr)
			{
				PreAddressedClassLibrary pre = (PreAddressedClassLibrary)lib;
				
				tocFill[PackTocProperty.OFFSET_DATA].setInt(pre.offset);
				tocFill[PackTocProperty.SIZE_DATA].setInt(pre.size);
			}
			
			// Is a JAR that needs to be copied in or stored
			else
			{
				// Output JAR data
				ChunkSection jarData = out.addSection(
					ChunkWriter.VARIABLE_SIZE, 4);
				tocFill[PackTocProperty.OFFSET_DATA]
					.set(jarData.futureAddress());
				tocFill[PackTocProperty.SIZE_DATA]
					.set(jarData.futureSize());
			
				// Direct SQC Copy, it is precompiled already and need not be
				// recompiled again.
				if (isSqc)
					try (InputStream rom = lib.resourceAsStream(
						SummerCoatJarLibrary.ROM_CHUNK_RESOURCE))
					{
						byte[] buf = new byte[16384];
						for (;;)
						{
							int rc = rom.read(buf);
							
							if (rc < 0)
								break;
							
							jarData.write(buf, 0, rc);
						}
					}
				
				// Normal JAR to be minimized
				else
					try
					{
						JarMinimizer.minimize(null, isBoot, lib,
							jarData, null);
					}
					catch (InvalidClassFormatException e)
					{
						// {@squirreljme.error BI01 Could not minimize the JAR
						// due to an invalid class file. (The name)}
						throw new InvalidClassFormatException(
							"BI01 " + name, e);
					}
			}
			
			// Store the TOC fill accordingly
			for (int q = 0; q < PackTocProperty.NUM_PACK_TOC_PROPERTIES; q++)
			{
				if (!tocFill[q].isSet())
					throw Debugging.oops(q);
				
				toc.writeFuture(ChunkDataType.INTEGER, tocFill[q]);
			}
		}
		
		// Verify values are set
		for (int i = 0; i < PackProperty.NUM_PACK_PROPERTIES; i++)
			if (!properties[i].isSet())
				throw Debugging.oops(i);
		
		// Write the resultant JAR to the output
		out.writeTo(__out);
	}
	
	/**
	 * Normalizes the name of the JAR.
	 * 
	 * @param __name The JAR name.
	 * @return The normalized name.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/12
	 */
	public static String normalizeJarName(String __name)
		throws NullPointerException
	{
		if (__name == null)
			throw new NullPointerException("NARG");
		
		if (SummerCoatJarLibrary.isSqc(__name))
			return __name.substring(0, __name.length() - 4) + ".sqc";
		else if (!__name.endsWith(".jar"))
			return __name + ".jar";
		return __name;
	}
}

