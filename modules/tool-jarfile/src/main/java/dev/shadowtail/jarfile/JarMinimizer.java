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
import cc.squirreljme.jvm.summercoat.constants.JarTocFlag;
import cc.squirreljme.jvm.summercoat.constants.JarTocProperty;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.vm.VMClassLibrary;
import dev.shadowtail.classfile.mini.DualPoolEncodeResult;
import dev.shadowtail.classfile.mini.DualPoolEncoder;
import dev.shadowtail.classfile.mini.Minimizer;
import dev.shadowtail.classfile.pool.DualClassRuntimePool;
import dev.shadowtail.classfile.pool.DualClassRuntimePoolBuilder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import net.multiphasicapps.classfile.ClassFile;
import net.multiphasicapps.classfile.ClassName;
import net.multiphasicapps.io.ChunkDataType;
import net.multiphasicapps.io.ChunkForwardedFuture;
import net.multiphasicapps.io.ChunkSection;
import net.multiphasicapps.io.ChunkWriter;

/**
 * This class is responsible for creating minimized Jar files which will then
 * be built into a ROM and used by SummerCoat and RatufaCoat.
 *
 * @since 2019/04/27
 */
public final class JarMinimizer
{
	/**
	 * {@squirreljme.property dev.shadowtail.jarfile.debug=boolean
	 * Should debugging text be printed for the JAR minimizer?}
	 */
	@Deprecated
	static final boolean _ENABLE_DEBUG =
		Boolean.getBoolean("dev.shadowtail.jarfile.debug");
	
	/** The boot class. */
	private static final String _BOOT_CLASS_RC =
		"cc/squirreljme/jvm/summercoat/Bootstrap.class";
	
	/** The version to use for the JAR. */
	private static final short _USE_VERSION =
		ClassInfoConstants.CLASS_VERSION_20201129;
	
	/** Is this a boot JAR? */
	protected final boolean boot;
	
	/** The input JAR. */
	protected final VMClassLibrary input;
	
	/** The dual-combined constant pool. */
	protected final DualClassRuntimePoolBuilder dualPool;
	
	/** Are we using our own dual pool? */
	protected final boolean isOurDualPool;
	
	/** The resulting JAR header. */
	private MinimizedJarHeader _jheader;
	
	/**
	 * Initializes the minimizer worker.
	 *
	 * @param __dp The global dual constant pool, may be {@code null} to not
	 * use the pack-file global one.
	 * @param __boot Is this a boot JAR?
	 * @param __in The input library.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/27
	 */
	private JarMinimizer(DualClassRuntimePoolBuilder __dp, boolean __boot,
		VMClassLibrary __in)
		throws NullPointerException
	{
		if (__in == null)
			throw new NullPointerException("NARG");
			
		this.boot = __boot;
		this.input = __in;
		
		// Use the passed pool if it was passed, but otherwise just use one
		// in the event one was not passed through (uses our own pool)
		boolean ownDualPool = (__dp == null);
		this.dualPool = (ownDualPool ?
			new DualClassRuntimePoolBuilder() : __dp);
		this.isOurDualPool = ownDualPool;
	}
	
	/**
	 * Processes the input JAR.
	 *
	 * @param __out The output.
	 * @throws IOException On read/write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/27
	 */
	private void __process(OutputStream __out)
		throws IOException, NullPointerException
	{
		if (__out == null)
			throw new NullPointerException("NARG");
		
		// This is processed for all entries
		VMClassLibrary input = this.input;
		
		// Obtain all of the resources in the JAR so we can process them
		// accordingly
		String[] rcNames = input.listResources();
		int numRc = rcNames.length;
		
		// Sort all the resources so that it is faster to find the entries
		Arrays.sort(rcNames);
		
		// Table of the entire JAR for writing
		ChunkWriter out = new ChunkWriter();
		
		// Start the header and table of contents, these will be written to
		// accordingly as needed
		ChunkSection header = out.addSection(
			ChunkWriter.VARIABLE_SIZE, 4);
		
		// Magic number and minimized format, since about November 2020 there
		// is a new version format
		header.writeInt(ClassInfoConstants.JAR_MAGIC_NUMBER);
		header.writeShort(JarMinimizer._USE_VERSION);
		
		// The number of properties used, is always constant for now
		ChunkForwardedFuture[] properties = new ChunkForwardedFuture[
			JarProperty.NUM_JAR_PROPERTIES];
		header.writeUnsignedShortChecked(properties.length);
		
		// Initialize empty properties
		for (int i = 0; i < JarProperty.NUM_JAR_PROPERTIES; i++)
		{
			// Initialize the future that will later be used to initialize
			// the value.
			ChunkForwardedFuture future = new ChunkForwardedFuture();
			properties[i] = future;
			
			// This property is written here
			header.writeFuture(ChunkDataType.INTEGER, future);
		}
		
		// This value is currently meaningless so for now it is always zero
		properties[JarProperty.INT_JAR_VERSION_ID].setInt(0);
		
		// Table of contents that represents the JAR
		ChunkSection toc = out.addSection(ChunkWriter.VARIABLE_SIZE, 4);
		properties[JarProperty.COUNT_TOC].setInt(numRc);
		properties[JarProperty.OFFSET_TOC].set(toc.futureAddress());
		properties[JarProperty.SIZE_TOC].set(toc.futureSize());
		
		// Start the table of contents off with the number of entries and the
		// ints per entry
		toc.writeUnsignedShortChecked(numRc);
		toc.writeUnsignedShortChecked(JarTocProperty.NUM_JAR_TOC_PROPERTIES);
		
		// The global dual-constant pool if one is available, this is used to
		// share pool entries between classes and JARs accordingly
		DualClassRuntimePoolBuilder dualPool = this.dualPool;
		
		// Is this a boot JAR?
		boolean isBoot = this.boot;
		BootState bootState = (isBoot ? new BootState() : null);
		int bootClassDx = -1;
		
		// Buffer for byte copies
		byte[] buf = new byte[16384];
		
		// Go through every resource, process them to be added to the JAR or
		// pipe them directly through
		ChunkForwardedFuture[] tocFill = new ChunkForwardedFuture[
			JarTocProperty.NUM_JAR_TOC_PROPERTIES];
		for (int i = 0; i < numRc; i++)
		{
			// Reset the table of contents fill
			for (int q = 0; q < JarTocProperty.NUM_JAR_TOC_PROPERTIES; q++)
				tocFill[q] = new ChunkForwardedFuture();
			
			// Resource to encode/copy
			String rc = rcNames[i];
			tocFill[JarTocProperty.INT_NAME_HASHCODE].setInt(rc.hashCode());
			
			// Spill in name
			ChunkSection utfName = out.addSection(
				ChunkWriter.VARIABLE_SIZE, 4);
			utfName.writeUTF(rc);
			tocFill[JarTocProperty.OFFSET_NAME].set(utfName.futureAddress());
			tocFill[JarTocProperty.SIZE_NAME].set(utfName.futureSize());
			
			// Is this a class or manifest?
			boolean isClass = (rc.endsWith(".class") &&
				ClassName.isValidClassName(rc.substring(0, rc.length() - 6)));
			boolean isManifest = rc.equals("META-INF/MANIFEST.MF");
			boolean isBootClass = (isBoot && isClass &&
				JarMinimizer._BOOT_CLASS_RC.equals(rc));
			
			// The manifest is at this index
			if (isManifest)
				properties[JarProperty.RCDX_MANIFEST].setInt(i);
			if (isBootClass)
			{
				properties[JarProperty.RCDX_START_CLASS].setInt(i);
				bootClassDx = i;
			}
			
			// Flags for this entry
			tocFill[JarTocProperty.INT_FLAGS].setInt((isClass ?
				JarTocFlag.EXECUTABLE_CLASS : JarTocFlag.RESOURCE) |
				(isManifest ? JarTocFlag.MANIFEST : 0) |
				(isBootClass ? JarTocFlag.BOOT : 0));
			
			// Section to contain the data for this resource
			ChunkSection rcData = out.addSection(
				ChunkWriter.VARIABLE_SIZE, 4);
			tocFill[JarTocProperty.OFFSET_DATA].set(rcData.futureAddress());
			tocFill[JarTocProperty.SIZE_DATA].set(rcData.futureSize());
			
			// Process the resources
			try (InputStream in = input.resourceAsStream(rc))
			{
				// Minimize directly to the JARs
				if (isClass)
				{
					Minimizer.minimize(dualPool, ClassFile.decode(in), rcData);
					
					// If we are setting up the boot state, add this class for
					// later processing
					if (isBoot)
					{
						bootState.addClass(
							new ClassName(rc.substring(0, rc.length() - 6)),
							rcData, isBootClass);
					}
				}
				
				// Otherwise perform a plain copy operation
				else
					for (;;)
					{
						int ll = in.read(buf);
						
						// EOF?
						if (ll < 0)
							break;
						
						rcData.write(buf, 0, ll);
					}
			}
			
			// Store the TOC fill accordingly
			for (int q = 0; q < JarTocProperty.NUM_JAR_TOC_PROPERTIES; q++)
				toc.writeFuture(ChunkDataType.INTEGER, tocFill[q]);
		}
		
		// Class has no manifest?
		if (!properties[JarProperty.RCDX_MANIFEST].isSet())
			properties[JarProperty.RCDX_MANIFEST].setInt(-1);
		
		// We are using our own dual pool, so write it out as if it were
		// in the pack file. It is only local to this JAR.
		ChunkSection lpd = null;
		if (isBoot || this.isOurDualPool)
		{
			// Encode pool into a new section
			lpd = out.addSection(ChunkWriter.VARIABLE_SIZE, 4);
			DualPoolEncodeResult der = DualPoolEncoder.encode(dualPool, lpd);
			
			// Static pool
			properties[JarProperty.OFFSET_STATIC_POOL]
				.set(lpd.futureAddress(der.staticpooloff));
			properties[JarProperty.SIZE_STATIC_POOL]
				.setInt(der.staticpoolsize);
			
			// Run-time pool
			properties[JarProperty.OFFSET_RUNTIME_POOL]
				.set(lpd.futureAddress(der.runtimepooloff));
			properties[JarProperty.SIZE_RUNTIME_POOL]
				.setInt(der.runtimepoolsize);
		}
		
		// We are using the global pack pool, so set special indicators
		// that we are doing as such! The minimized class will use special
		// a special aliased pool for the pack file.
		else
		{
			// Static pool offset and size
			properties[JarProperty.OFFSET_STATIC_POOL].setInt(-1);
			properties[JarProperty.SIZE_STATIC_POOL].setInt(-1);
			
			// Runtime pool offset and size
			properties[JarProperty.OFFSET_RUNTIME_POOL].setInt(-1);
			properties[JarProperty.SIZE_RUNTIME_POOL].setInt(-1);
		}
		
		// Doing bootstrapping?
		if (isBoot)
		{
			// Make sure all the addresses and otherwise are filled so we know
			// our own offsets and otherwise.
			out.undirty();
			
			// Load the pool we just created for the JAR, since we need to
			// use it for processing
			byte[] poolBytes = lpd.currentBytes();
			int lpdStart = lpd.futureAddress().get();
			DualClassRuntimePool decPool = DualPoolEncoder.decode(poolBytes,
				properties[JarProperty.OFFSET_STATIC_POOL].get() -
					lpdStart, properties[JarProperty.SIZE_STATIC_POOL].get(),
				properties[JarProperty.OFFSET_RUNTIME_POOL].get() -
					lpdStart, properties[JarProperty.SIZE_RUNTIME_POOL].get());
			
			// Tell the state to actually boot the system and record the
			// contents into the target bootstrap section
			ChunkSection bootstrap = out.addSection(
				ChunkWriter.VARIABLE_SIZE, 4);
			int[] startPoolHandleId = new int[1];
			int[] vmAttribHandleId = new int[1];
			bootState.boot(decPool, lpd, bootstrap, startPoolHandleId,
				vmAttribHandleId);
			
			// Use the properties of the bootstrap which will be read on
			// virtual machine initialization
			properties[JarProperty.OFFSET_BOOT_INIT]
				.set(bootstrap.futureAddress());
			properties[JarProperty.SIZE_BOOT_INIT]
				.set(bootstrap.futureSize());
			properties[JarProperty.MEMHANDLEID_START_POOL]
				.setInt(startPoolHandleId[0]);
			properties[JarProperty.RCDX_START_CLASS]
				.setInt(bootClassDx);
			properties[JarProperty.MEMHANDLEID_VM_ATTRIBUTES]
				.setInt(vmAttribHandleId[0]);
			
			// Base array size
			properties[JarProperty.SIZE_BASE_ARRAY]
				.setInt(bootState.__baseArraySize());
		}
		
		// No bootstrapping to be done
		else
		{
			// No boot properties are valid here
			properties[JarProperty.OFFSET_BOOT_INIT].setInt(0);
			properties[JarProperty.SIZE_BOOT_INIT].setInt(0);
			properties[JarProperty.MEMHANDLEID_START_POOL].setInt(0);
			properties[JarProperty.RCDX_START_CLASS].setInt(0);
			properties[JarProperty.MEMHANDLEID_VM_ATTRIBUTES].setInt(0);
			
			// No known array base size
			properties[JarProperty.SIZE_BASE_ARRAY].setInt(0);
		}
		
		// Verify values are set
		for (int i = 0; i < JarProperty.NUM_JAR_PROPERTIES; i++)
			if (!properties[i].isSet())
				throw Debugging.oops(i);
		
		// Write the resultant JAR to the output
		out.writeTo(__out);
		
		// We can use the property details and the futures to determine the
		// values that would be read from the JAR header from a byte stream
		int numProps = properties.length;
		int[] headerProps = new int[numProps];
		for (int i = 0; i < numProps; i++)
			headerProps[i] = properties[i].get();
		this._jheader = new MinimizedJarHeader(
			JarMinimizer._USE_VERSION, headerProps);
	}
	
	/**
	 * Minimizes the specified Jar file.
	 *
	 * @param __boot Should pre-created boot memory be created to quickly
	 * initialize the virtual machine?
	 * @param __in The input JAR file.
	 * @return The resulting byte array of minimization.
	 * @throws IOException On read/write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/27
	 */
	public static byte[] minimize(boolean __boot, VMClassLibrary __in)
		throws IOException, NullPointerException
	{
		if (__in == null)
			throw new NullPointerException("NARG");
		
		// Write to a temporary byte array
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream(1048576))
		{
			// Perform minimization
			JarMinimizer.minimize(null, __boot, __in, baos, null);
			
			// Return the generated array
			return baos.toByteArray();
		}
	}
	
	/**
	 * Minimizes the specified Jar file.
	 *
	 * @param __boot Should pre-created boot memory be created to quickly
	 * initialize the virtual machine?
	 * @param __in The input JAR file.
	 * @param __out The stream where JAR data will be placed.
	 * @throws IOException On read/write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/27
	 */
	public static void minimize(boolean __boot, VMClassLibrary __in,
		OutputStream __out)
		throws IOException, NullPointerException
	{
		JarMinimizer.minimize(null, __boot, __in, __out, null);
	}
	
	/**
	 * Minimizes the specified Jar file.
	 *
	 * @param __boot Should pre-created boot memory be created to quickly
	 * initialize the virtual machine?
	 * @param __in The input JAR file.
	 * @param __out The stream where JAR data will be placed.
	 * @param __mjh The output JAR header.
	 * @throws IOException On read/write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/07/17
	 */
	public static void minimize(boolean __boot, VMClassLibrary __in,
		OutputStream __out, MinimizedJarHeader[] __mjh)
		throws IOException, NullPointerException
	{
		JarMinimizer.minimize(null, __boot, __in, __out, __mjh);
	}
	
	/**
	 * Minimizes the specified Jar file.
	 *
	 * @param __dp The dual-pool.
	 * @param __boot Should pre-created boot memory be created to quickly
	 * initialize the virtual machine?
	 * @param __in The input JAR file.
	 * @param __out The stream where JAR data will be placed.
	 * @param __mjh The output JAR header.
	 * @throws IOException On read/write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/05/29
	 */
	public static void minimize(DualClassRuntimePoolBuilder __dp,
		boolean __boot, VMClassLibrary __in, OutputStream __out,
		MinimizedJarHeader[] __mjh)
		throws IOException, NullPointerException
	{
		if (__in == null || __out == null)
			throw new NullPointerException("NARG");
		
		// Use helper class
		JarMinimizer jm = new JarMinimizer(__dp, __boot, __in);
		jm.__process(__out);
		
		// Set header that was generated
		if (__mjh != null && __mjh.length > 0)
			__mjh[0] = jm._jheader;
	}
}
