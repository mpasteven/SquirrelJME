// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.summercoat;

import cc.squirreljme.emulator.vm.VMSuiteManager;
import cc.squirreljme.jvm.mle.constants.ByteOrderType;
import cc.squirreljme.jvm.summercoat.constants.PackProperty;
import cc.squirreljme.jvm.summercoat.ld.mem.AbstractReadableMemory;
import cc.squirreljme.jvm.summercoat.ld.mem.ByteArrayMemory;
import cc.squirreljme.jvm.summercoat.ld.mem.MemoryAccessException;
import cc.squirreljme.jvm.summercoat.ld.mem.NotRealMemoryException;
import cc.squirreljme.jvm.summercoat.ld.mem.ReadableMemory;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.runtime.cldc.io.HexDumpOutputStream;
import cc.squirreljme.vm.PreAddressedClassLibrary;
import cc.squirreljme.vm.VMClassLibrary;
import dev.shadowtail.packfile.MinimizedPackHeader;
import dev.shadowtail.packfile.PackMinimizer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class contains the memory information for every single suite which
 * exists within the VM.
 *
 * @since 2019/04/21
 */
public final class SuitesMemory
	extends AbstractReadableMemory
{
	/** Size of the ROM header, the maximum permitted. */
	public static final int ROM_HEADER_SIZE =
		1048576;
	
	/** The suite chunk size. */
	public static final int SUITE_CHUNK_SIZE =
		25_165_824;
	
	/** The suite manage to base from. */
	protected final VMSuiteManager suites;
	
	/** Offset. */
	protected final int offset;
	
	/** The size of this memory region. */
	protected final int size;
	
	/** The individual regions of suite memory. */
	private final SuiteMemory[] _suitemem;
	
	/** This is the mapping of suite names to memory. */
	private final Map<String, SuiteMemory> _suitemap;
	
	/** The ROM header information. */
	private volatile ReadableMemory _headerRom;
	
	/** Was the config table initialized? */
	private volatile boolean _didInit;
	
	/**
	 * Initializes the suites memory.
	 *
	 * @param __off The offset of suite memory.
	 * @param __sm The suite manager.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/21
	 */
	public SuitesMemory(int __off, VMSuiteManager __sm)
		throws NullPointerException
	{
		super(ByteOrderType.BIG_ENDIAN);
		
		if (__sm == null)
			throw new NullPointerException("NARG");
		
		// Set suites
		this.suites = __sm;
		
		// All the libraries which are available for usage
		String[] libNames = __sm.listLibraryNames();
		int n = libNames.length;
		
		// Setup suite memory area
		SuiteMemory[] suitemem = new SuiteMemory[n];
		Map<String, SuiteMemory> suitemap = new LinkedHashMap<>();
		
		// Setup memory regions for the various suites
		int off = SuitesMemory.ROM_HEADER_SIZE;
		for (int i = 0; i < n; i++, off += SuitesMemory.SUITE_CHUNK_SIZE)
		{
			// Need the suite name for later lookup on init
			String libname = PackMinimizer.normalizeJarName(libNames[i]);
			
			// Map suite
			SuiteMemory sm;
			suitemem[i] = (sm = new SuiteMemory(off, __sm, libname));
			
			// Also use map for quick access
			suitemap.put(libname, sm);
		}
		
		// {@squirreljme.error AE0q Suite space has exceeded size limit of
		// 2GiB. (The current size; The amount of bytes over)}
		if (off < 0)
			throw new VMMemoryAccessException("AE0q " +
				(((long)off) & 0xFFFFFFFFL) + " " +
				(off - 0x7FFFFFFF));
		
		// Store all the various suite memories
		this._suitemem = suitemem;
		this._suitemap = suitemap;
		
		// Store final memory parameters
		this.offset = __off;
		this.size = off;
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
	 * Finds the specified library.
	 *
	 * @param __name The library to locate.
	 * @return The located library.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/03/01
	 */
	public final SuiteMemory findLibrary(String __name)
		throws NullPointerException
	{
		if (__name == null)
			throw new NullPointerException("NARG");
		
		Map<String, SuiteMemory> suitemap = this._suitemap;
		
		// Direct name match?
		SuiteMemory rv = suitemap.get(__name);
		if (rv != null)
			return rv;
		
		// With JAR attached?
		rv = suitemap.get(__name + ".jar");
		if (rv != null)
			return rv;
		
		// Try one last time with JAR removed
		if (__name.endsWith(".jar"))
			return suitemap.get(__name.substring(
				0, __name.length() - ".jar".length()));
		
		// Not found, give up
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 */
	@Override
	public int memReadByte(long __addr)
	{
		// ROM memory was not initialized, so it is invalid
		if (!this._didInit)
			throw new IllegalStateException("Memory not initialized.");
			
		// Negative address accessed
		if (__addr < 0)
			throw new VMMemoryAccessException(String.format(
				"Negative address byte read: %#08x", __addr));
		
		// Reading from the config table?
		if (__addr < SuitesMemory.ROM_HEADER_SIZE)
			return this._headerRom.memReadByte(__addr);
		
		// Determine the suite index we are wanting to look in memory
		int si = (int)(__addr - SuitesMemory.ROM_HEADER_SIZE) /
			SuitesMemory.SUITE_CHUNK_SIZE;
		
		// Fail if illegal memory is read, this should never happen
		SuiteMemory[] suiteMem = this._suitemem;
		if (si >= suiteMem.length)
			throw new VMMemoryAccessException(String.format(
				"Invalid byte read: %#08x (library: %d of %d)",
				__addr, si, suiteMem.length));
		
		// Read from suite memory
		return suiteMem[si].memReadByte(__addr - suiteMem[si].offset);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 * @return
	 */
	@Override
	public final long memRegionSize()
	{
		return this.size;
	}
	
	/**
	 * Initializes the configuration space.
	 *
	 * @since 2019/04/21
	 * @since 2020/12/12
	 */
	final void __init()
	{
		// Do not initialize twice!
		if (this._didInit)
			return;
		this._didInit = true;
		
		// Initialize the bootstrap
		SuiteMemory bootLib = this.findLibrary("cldc-compact");
		try
		{
			bootLib.__init();
		}
		
		// {@squirreljme.error AE0a Could not initialize the supervisor.}
		catch (IOException e)
		{
			throw new RuntimeException("AE0a", e);
		}
		
		// Get suites and the number of them for processing
		SuiteMemory[] suiteMem = this._suitemem;
		int numSuites = suiteMem.length;
		
		// The base address of this memory
		int base = this.offset;
		
		// Use virtually set predefined addresses so we can recycle the
		// minimizer writing code without needing to duplicate everything
		// This ensures that it works as well.
		VMClassLibrary[] pre = new VMClassLibrary[numSuites];
		for (int i = 0; i < numSuites; i++)
		{
			SuiteMemory from = suiteMem[i];
			pre[i] = new PreAddressedClassLibrary(
				(int)from.absoluteAddress(0),
				(int)from.memRegionSize(), from.libName);
		}
		
		// Write the virtual header
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			// Perform initial minimization
			PackMinimizer.minimize(baos, bootLib.libName, pre);
			byte[] romData = baos.toByteArray();
			
			// Replace a property within the header
			try (InputStream in = new ByteArrayInputStream(romData))
			{
				// Decode and change the header property
				MinimizedPackHeader header = MinimizedPackHeader.decode(in);
				MinimizedPackHeader newHeader =
					header.change(PackProperty.ROM_SIZE, this.size);
				
				// Encode a new header
				byte[] newBytes = newHeader.toByteArray();
				
				// Replace existing header
				System.arraycopy(newBytes, 0,
					romData, 0, newBytes.length);
			}
			
			// Store ROM virtual header
			this._headerRom = new ByteArrayMemory(this.offset, romData);
		}
		
		// Failed to write the virtual header, so fail
		catch (IOException e)
		{
			throw new RuntimeException("Could not write virtual header.", e);
		}
	}
}

