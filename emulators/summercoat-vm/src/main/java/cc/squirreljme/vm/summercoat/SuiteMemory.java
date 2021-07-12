// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.summercoat;

import cc.squirreljme.emulator.vm.VMException;
import cc.squirreljme.emulator.vm.VMSuiteManager;
import cc.squirreljme.jvm.mle.constants.ByteOrderType;
import cc.squirreljme.jvm.summercoat.constants.ClassInfoConstants;
import cc.squirreljme.jvm.summercoat.ld.mem.AbstractReadableMemory;
import cc.squirreljme.jvm.summercoat.ld.mem.ByteArrayMemory;
import cc.squirreljme.jvm.summercoat.ld.mem.MemoryAccessException;
import cc.squirreljme.jvm.summercoat.ld.mem.NotRealMemoryException;
import cc.squirreljme.jvm.summercoat.ld.mem.ReadableMemory;
import cc.squirreljme.jvm.summercoat.ld.mem.ReadableMemoryInputStream;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.vm.SummerCoatJarLibrary;
import cc.squirreljme.vm.VMClassLibrary;
import dev.shadowtail.jarfile.MinimizedJarHeader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This represents the single virtual memory space for suite memory.
 *
 * @since 2019/04/21
 */
public final class SuiteMemory
	extends AbstractReadableMemory
	implements ReadableMemory
{
	/** The suite manager. */
	protected final VMSuiteManager suites;
	
	/** The library this uses. */
	protected final String libName;
	
	/** The offset to this memory region. */
	protected final int offset;
	
	/** Was this initialized? */
	private volatile boolean _didinit;
	
	/** Memory for this suite. */
	private volatile ReadableMemory _memory;
	
	/** Header for this JAR. */
	volatile MinimizedJarHeader _jarheader;
	
	/**
	 * Initializes the suite memory.
	 *
	 * @param __off The memory offset for this suite.
	 * @param __sm The suite manager.
	 * @param __ln The library name.
	 * @throws NullPointerException On null arguments.
	 */
	public SuiteMemory(int __off, VMSuiteManager __sm, String __ln)
		throws NullPointerException
	{
		super(ByteOrderType.BIG_ENDIAN);
		
		if (__sm == null || __ln == null)
			throw new NullPointerException("NARG");
		
		this.suites = __sm;
		this.libName = __ln;
		this.offset = __off;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/03
	 */
	@Override
	public long absoluteAddress(long __addr)
		throws MemoryAccessException, NotRealMemoryException
	{
		return this.offset + __addr;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 */
	@Override
	public int memReadByte(long __addr)
	{
		// Initialize?
		if (!this._didinit)
			try
			{
				this.__init();
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		
		// Forward
		return this._memory.memReadByte(__addr);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 */
	@Override
	public void memReadBytes(long __addr, byte[] __b, int __o, int __l)
		throws IndexOutOfBoundsException, NullPointerException
	{
		// Initialize?
		if (!this._didinit)
			try
			{
				this.__init();
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		
		// Forward
		this._memory.memReadBytes(__addr, __b, __o, __l);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 */
	@Override
	public int memReadInt(long __addr)
	{
		// Initialize?
		if (!this._didinit)
			try
			{
				this.__init();
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		
		// Forward
		return this._memory.memReadInt(__addr);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 */
	@Override
	public int memReadShort(long __addr)
	{
		// Initialize?
		if (!this._didinit)
			try
			{
				this.__init();
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		
		// Forward
		return this._memory.memReadShort(__addr);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 * @return
	 */
	@Override
	public final long memRegionSize()
	{
		return SuitesMemory.SUITE_CHUNK_SIZE;
	}
	
	/**
	 * Initializes all the memory and suites here.
	 *
	 * @throws IOException On read/write errors.
	 * @since 2019/04/21
	 */
	final void __init()
		throws IOException
	{
		// Do not initialize twice!
		if (this._didinit)
			return;
		this._didinit = true;
		
		// Load the class library
		String libname = this.libName;
		VMClassLibrary clib = this.__loadLibrary(libname);
		
		// Debug
		todo.DEBUG.note("Initialize suite %s @%08d",
			libname, this.offset);
		
		// Since the new build system using Gradle, JARs are already
		// compiled and minimized producing much faster and cached runs
		// rather than recompiling everything over and over.
		byte[] jf;
		try (InputStream in = clib.resourceAsStream(
				SummerCoatJarLibrary.ROM_CHUNK_RESOURCE);
			ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			// No ROM actually exists?
			if (in == null)
				throw new VMException("JAR has no SummerCoat ROM: " + libname);
			
			// Copy ROM data
			byte[] buf = new byte[4096];
			for (;;)
			{
				int rc = in.read(buf);
				
				if (rc < 0)
					break;
				
				baos.write(buf, 0, rc);
			}
			
			// Use finalized ROM data
			jf = baos.toByteArray();
		}
		
		// {@squirreljme.error AE09 Suite chunk size limit was exceeded.
		// (The required chunk size; The limit; More space that is needed)}
		if (jf.length > SuitesMemory.SUITE_CHUNK_SIZE)
			throw new RuntimeException("AE09 " + jf.length + " " +
				SuitesMemory.SUITE_CHUNK_SIZE + " " +
				(jf.length - SuitesMemory.SUITE_CHUNK_SIZE));
		
		// Set memory using this byte array
		ReadableMemory rm;
		this._memory = (rm = new ByteArrayMemory(this.offset, jf));
		
		// Load the JAR header
		this._jarheader = MinimizedJarHeader.decode(
			new ReadableMemoryInputStream(rm, 0,
				ClassInfoConstants.JAR_MAXIMUM_HEADER_SIZE));
	}
	
	/**
	 * Attempts to load the given library.
	 *
	 * @param __libname The library to load.
	 * @return The loaded library.
	 * @throws NullPointerException On null arguments.
	 * @throws VMException If it was not found.
	 * @since 2019/11/28
	 */
	private final VMClassLibrary __loadLibrary(String __libname)
		throws NullPointerException, VMException
	{
		if (__libname == null)
			throw new NullPointerException("NARG");
			
		VMSuiteManager suites = this.suites;
		
		// Try as it is requested
		VMClassLibrary rv = suites.loadLibrary(__libname);
		if (rv != null)
			return rv;
		
		// Try with an extension added
		rv = suites.loadLibrary(__libname + ".jar");
		if (rv != null)
			return rv;
		
		// Try to remove the JAR extension
		if (__libname.endsWith(".jar"))
		{
			rv = suites.loadLibrary(__libname.substring(0,
				__libname.length() - 4));
			if (rv != null)
				return rv;
		}
		
		// {@squirreljme.error AE0p Could not find library. (The library)}
		throw new VMException("AE0p " + __libname);
	}
}

