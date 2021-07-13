// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.summercoat;

import cc.squirreljme.emulator.profiler.ProfilerSnapshot;
import cc.squirreljme.emulator.vm.VMException;
import cc.squirreljme.emulator.vm.VMFactory;
import cc.squirreljme.emulator.vm.VMSuiteManager;
import cc.squirreljme.emulator.vm.VMThreadModel;
import cc.squirreljme.emulator.vm.VirtualMachine;
import cc.squirreljme.jdwp.JDWPFactory;
import cc.squirreljme.jvm.summercoat.SummerCoatUtil;
import cc.squirreljme.jvm.summercoat.constants.BootstrapConstants;
import cc.squirreljme.jvm.summercoat.constants.JarProperty;
import cc.squirreljme.jvm.summercoat.constants.JarTocProperty;
import cc.squirreljme.jvm.summercoat.constants.MemHandleKind;
import cc.squirreljme.jvm.summercoat.constants.PackProperty;
import cc.squirreljme.jvm.summercoat.constants.PackTocProperty;
import cc.squirreljme.jvm.summercoat.constants.StaticClassProperty;
import cc.squirreljme.jvm.summercoat.constants.StaticVmAttribute;
import cc.squirreljme.jvm.summercoat.ld.mem.ByteArrayMemory;
import cc.squirreljme.jvm.summercoat.ld.mem.ReadableMemory;
import cc.squirreljme.jvm.summercoat.ld.mem.ReadableMemoryInputStream;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.runtime.cldc.io.HexDumpOutputStream;
import cc.squirreljme.vm.VMClassLibrary;
import dev.shadowtail.classfile.mini.MinimizedClassHeader;
import dev.shadowtail.jarfile.MinimizedJarHeader;
import dev.shadowtail.jarfile.TableOfContents;
import dev.shadowtail.packfile.MinimizedPackHeader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the factory which is capable of creating instances of the
 * SummerCoat virtual machine.
 *
 * @since 2018/12/29
 */
public class SummerCoatFactory
	extends VMFactory
{
	/** The starting address for RAM. */
	public static final int RAM_START_ADDRESS =
		0x0010_0000;
	
	/** Default size of RAM. */
	@Deprecated
	public static final int DEFAULT_RAM_SIZE =
		33554432;
	
	/** Base address for configuration data. */
	@Deprecated
	public static final int CONFIG_BASE_ADDR =
		0x1000_0000;
	
	/** The base address for suites. */
	public static final int SUITE_BASE_ADDR =
		0x2000_0000;
	
	/** Size of the configuration area. */
	@Deprecated
	public static final int CONFIG_SIZE =
		65536;
	
	/**
	 * Initializes the factory.
	 *
	 * @since 2018/12/29
	 */
	public SummerCoatFactory()
	{
		super("summercoat");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/12/29
	 */
	@Override
	protected VirtualMachine createVM(ProfilerSnapshot __ps,
		JDWPFactory __jdwp, VMThreadModel __threadModel, VMSuiteManager __sm,
		VMClassLibrary[] __cp,
		String __maincl, Map<String, String> __sysProps, String[] __args)
		throws IllegalArgumentException, NullPointerException, VMException
	{
		// Virtual memory which provides access to many parts of memory
		VirtualMemory vMem = new VirtualMemory();
		
		// The ROM always starts here
		int romBase = SummerCoatFactory.SUITE_BASE_ADDR;
		
		// Setup non-cpu VM state
		MachineState ms = new MachineState(vMem, __ps, romBase, __threadModel);
		MemHandleManager memHandles = ms.memHandles;
		
		// Load ROM file or generate dynamically for loaded classes
		ReadableMemory romMemory = this.__loadRom(romBase, __sm, __sysProps);
		vMem.mapRegion(romMemory);
		
		// Read in the appropriate ROM header
		TableOfContents<MinimizedPackHeader>[] packTocOut =
			new TableOfContents[1];
		MinimizedPackHeader packHeader =
			SummerCoatFactory.__loadRomHeader(romMemory, packTocOut);
		TableOfContents<MinimizedPackHeader> packToc = packTocOut[0];
		
		// The index of the JAR we are booting into
		int bootDx = packHeader.get(PackProperty.INDEX_BOOT_JAR);
		
		// Debug
		if (NativeCPU.ENABLE_DEBUG)
			Debugging.debugNote("PackToc: %s%n", packToc);
		
		// Load the boot JAR information
		MinimizedJarHeader bootJarHeader;
		TableOfContents<MinimizedJarHeader> bootJarToc;
		int bootJarOff;
		try (InputStream packIn = new ReadableMemoryInputStream(romMemory,
			(bootJarOff = packToc.get(bootDx, PackTocProperty.OFFSET_DATA)),
			packToc.get(bootDx, PackTocProperty.SIZE_DATA)))
		{
			// Read the pack header
			bootJarHeader = MinimizedJarHeader.decode(packIn);
			
			// Parse the table of contents
			try (InputStream bootJarTocIn = new ReadableMemoryInputStream(
				romMemory,
				bootJarOff + bootJarHeader.get(JarProperty.OFFSET_TOC),
				bootJarHeader.get(JarProperty.SIZE_TOC)))
			{
				bootJarToc = TableOfContents.<MinimizedJarHeader>decode(
					MinimizedJarHeader.class, bootJarTocIn);
			}
		}
		catch (IOException e)
		{
			throw new VMException("Could not read boot JAR headers.", e);
		}
		
		// Initialize RAM
		vMem.mapRegion(new ByteArrayMemory(SummerCoatFactory.RAM_START_ADDRESS,
			new byte[SummerCoatFactory.DEFAULT_RAM_SIZE], true));
		
		// Load the bootstrap JAR header
		int bootRamOff = bootJarOff + bootJarHeader.getBootoffset(),
			bootRamLen = bootJarHeader.getBootsize();
			
		// BootRAM memory handle IDs to 
		Map<Integer, MemHandle> virtHandles = new HashMap<>();
		
		// Debug
		if (NativeCPU.ENABLE_DEBUG)
			Debugging.debugNote("Loading BootRAM!");
		
		// Load the boot RAM
		try (DataInputStream dis = new DataInputStream(
			new ReadableMemoryInputStream(romMemory, bootRamOff, bootRamLen)))
		{
			// Base array size, needed for array allocation
			int arrayBase = bootJarHeader.get(JarProperty.SIZE_BASE_ARRAY);
			
			// Pre-load handles to determine all of their IDs and sizes
			// accordingly before the later load logic happens 
			for (;;)
			{
				// Determine the handle ID for this
				int handleId = dis.readInt();
				
				// No more remain?
				if (handleId == 0)
					break;
				
				// Invalid security bits
				if ((handleId & BootstrapConstants.HANDLE_SECURITY_MASK) !=
					BootstrapConstants.HANDLE_SECURITY_BITS)
					throw new VMException(
						"Invalid security bits: 0b" +
						Integer.toString(handleId, 2));
				
				// Read handle information
				int initCount = dis.readUnsignedShort();
				int byteSize = dis.readUnsignedShort();
				
				// Determine if the kind is valid
				int handleKind = dis.readUnsignedByte();
				if (handleKind <= 0 || handleKind >= MemHandleKind.NUM_KINDS)
					throw new VMException("Invalid MemHandle Kind: " +
						handleKind);
				
				// If this is an array, read the array length
				int arrayLen = -1;
				if (SummerCoatUtil.isArrayKind(handleKind))
					arrayLen = dis.readUnsignedShort();
				
				// Setup memory handle for this
				MemHandle memHandle;
				if (SummerCoatUtil.isArrayKind(handleKind))
					memHandle = memHandles.allocArray(handleKind, arrayBase,
						arrayLen);
				else
					memHandle = memHandles.alloc(handleKind, byteSize);
				
				// Store it for later usage
				virtHandles.put(handleId, memHandle);
				
				// Set initial count since it would have gone up on
				// initialization
				memHandle.setCount(initCount);
			}
			
			// Check if the guards are valid or not
			int preSeqGuard = dis.readInt();
			if (preSeqGuard != BootstrapConstants.PRE_SEQ_GUARD)
				throw new VMException(String.format("Invalid Pre: %08x",
					preSeqGuard));
			
			// Load handle data writing information
			for (;;)
			{
				// Read the handle we will be writing
				int handleId = dis.readInt();
				if (handleId == 0)
					break;
				
				// Ensure the handle is actually valid!
				MemHandle handle = virtHandles.get(handleId);
				if (handle == null)
					throw new VMException("Invalid handle: " + handleId);
				
				// Read and initialize big data?
				boolean hasBigData = (dis.readByte() != 0);
				if (hasBigData)
				{
					// Read it in
					byte[] bigData = new byte[handle.size];
					dis.readFully(bigData);
					
					// Write it into the memory handle completely
					handle.memWriteBytes(0,
						bigData, 0, bigData.length);
				}
				
				// Boot Jar pointer seed
				int bootJarSeed = 0;
				
				// Read in all the handle actions
				for (;;)
				{
					// Read the next action
					int type = dis.readByte();
					if (type == 0)
						break;
					
					// Is there an action mask?
					int terp;
					int action = (type < 0 ? -type : type) &
						BootstrapConstants.ACTION_MASK;
					
					// Interpret value as action or the given type
					if (action != 0)
						terp = action;
					else
						terp = type;
					
					// Read the address it occurs at
					int addr = dis.readUnsignedShort();
					
					// Determine what is being written to the memory
					int readValue = -1;
					switch (terp)
					{
							// Ignore Byte swaps
						case -1:
						case -2:
						case -4:
						case -8:
							continue;
						
							// Byte
						case 1:
							readValue = dis.readByte();
							handle.memWriteByte(addr, readValue);
							break;
							
							// Short
						case 2:
							readValue = dis.readShort();
							handle.memWriteShort(addr, readValue);
							break;
							
							// Integer
						case 4:
							readValue = dis.readInt();
							handle.memWriteInt(addr, readValue);
							break;
							
							// Long
						case 8:
							handle.memWriteLong(addr, dis.readLong());
							break;
						
							// Memory handle
						case BootstrapConstants.ACTION_MEMHANDLE:
							// Read the desired handle
							int wantId = dis.readInt();
							
							// Invalid security bits
							if ((handleId &
								BootstrapConstants.HANDLE_SECURITY_MASK) !=
								BootstrapConstants.HANDLE_SECURITY_BITS)
								throw new VMException(
									"Invalid security bits: 0b" +
									Integer.toString(wantId, 2));
							
							// Get the actual handle
							MemHandle target = virtHandles.get(wantId);
							if (target == null)
								throw new VMException(
									"Invalid handle: " + wantId);
							
							handle.memWriteHandle(addr, target.reference());
							readValue = target.id;
							break;
							
							// Seed the Boot Jar Pointer Value, used for the
							// low/high
						case BootstrapConstants.ACTION_BOOTJARP_SEED:
							bootJarSeed = dis.readInt();
							readValue = bootJarSeed;
							break;
							
							// Boot Jar Pointer (lo/hi)
						case BootstrapConstants.ACTION_BOOTJARP_A:
						case BootstrapConstants.ACTION_BOOTJARP_B:
							// Load the value from the BootJarSeed
							int baseOff = bootJarSeed;
							readValue = baseOff;
							
							// Write where it should belong, SquirrelJME is
							// always big endian so the lower value is always
							// first
							long desire = (long)romBase + (long)bootJarOff +
								(long)baseOff;
							if (terp == BootstrapConstants.ACTION_BOOTJARP_A)
								handle.memWriteInt(addr,
									(int)desire);
							else
								handle.memWriteInt(addr,
									(int)(desire >>> 32));
							break;
						
						default:
							throw new VMException("Invalid type: " + type);
					}
					
					// Debug
					if (NativeCPU.ENABLE_DEBUG)
						Debugging.debugNote(
							"%10x (%#10x t%d): %#04x @ %#010x = %d/%#x",
							handle.id, handleId, handle.kind, type, addr,
							readValue, readValue);
				}
				
				// Ensure the guard is valid
				int actGuard = dis.readInt();
				if (actGuard != BootstrapConstants.ACTION_SEQ_GUARD)
					throw new VMException(String.format("Invalid Act: %08x",
						actGuard));
			}
			
			// Check if the guards are valid or not
			int memSeqGuard = dis.readInt();
			if (memSeqGuard != BootstrapConstants.MEMORY_SEQ_GUARD)
				throw new VMException(String.format("Invalid End: %08x",
					memSeqGuard));
		}
		
		// {@squirreljme.error AE0h Could not initialize the boot RAM for
		// the virtual machine.}
		catch (IOException e)
		{
			throw new VMException("AE0h", e);
		}
		
		// The starting address
		int startAddress;
		
		// Determine the entry class and the entry method address
		int bootClassDx = bootJarHeader.get(JarProperty.RCDX_START_CLASS);
		int bootClassOff = bootJarOff +
			bootJarToc.get(bootClassDx, JarTocProperty.OFFSET_DATA);
		try (InputStream classIn = new ReadableMemoryInputStream(romMemory,
			bootClassOff,
			bootJarToc.get(bootClassDx, JarTocProperty.SIZE_DATA)))
		{
			// Dump the boot class?
			if (NativeCPU.ENABLE_DEBUG)
				try (InputStream copy = new ReadableMemoryInputStream(
					romMemory, bootClassOff, bootJarToc.get(bootClassDx,
					JarTocProperty.SIZE_DATA)))
				{
					HexDumpOutputStream.dump(System.err, copy);
				}
			
			// Read the pack header
			MinimizedClassHeader bootClassHeader =
				MinimizedClassHeader.decode(classIn);
			
			// Determine the start address
			int bootMethodOff = bootClassHeader.get(
				StaticClassProperty.OFFSET_BOOT_METHOD);
			startAddress = romBase + bootClassOff + bootMethodOff;
			
			// Debug
			if (NativeCPU.ENABLE_DEBUG)
			{
				Debugging.debugNote("BootJar RC Count: %d",
					bootJarHeader.get(JarProperty.COUNT_TOC));
				Debugging.debugNote("Boot Class Dx: %d", bootClassDx);
				Debugging.debugNote("ROM Base: %#010x", romBase);
				Debugging.debugNote("BootJar Off: %#010x", bootJarOff);
				Debugging.debugNote("BootClass Off: %#010x",
					bootClassOff);
				Debugging.debugNote("BootMethod Dx: %d",
					bootClassHeader.get(StaticClassProperty.INDEX_BOOT_METHOD));
				Debugging.debugNote("BootMethod Off: %#010x",
					bootMethodOff);
				Debugging.debugNote("Start Address: %#010x",
					startAddress);
			}
		}
		catch (IOException e)
		{
			throw new VMException("Could not read Boot Class Header.", e);
		}
		
		// Which memory handle contains the pool?
		int bootPool = bootJarHeader.get(JarProperty.MEMHANDLEID_START_POOL);
		
		// Debug
		if (NativeCPU.ENABLE_DEBUG)
			Debugging.debugNote("bootPool: %d/%#08x (%#08x)",
				bootPool, bootPool, virtHandles.get(bootPool).id);
		
		// Where are the static attributes?
		int staticAttrib = bootJarHeader.get(
			JarProperty.MEMHANDLEID_VM_ATTRIBUTES);
		int arrayBase = bootJarHeader.get(JarProperty.SIZE_BASE_ARRAY);
		ms.setStaticAttributes(virtHandles.get(staticAttrib),
			arrayBase);
		
		// Find the initial thread and task
		MemHandle initThread = memHandles.get(
			ms.staticAttribute(StaticVmAttribute.MEMHANDLE_BOOT_THREAD));
		
		// Setup virtual execution CPU
		NativeCPU cpu = ms.createVmCpu(initThread);
		cpu.enterFrame(false, startAddress,
			virtHandles.get(bootPool).id);
		
		// Open debugging connection, if we are debugging
		// We open it here since we know about our threads and otherwise and
		// also the debugger will not ask us about things we do not yet know
		// about as well.
		if (__jdwp != null)
			ms._jdwp = __jdwp.open(ms);
		
		// Setup virtual machine with initial thread
		return new SummerCoatVirtualMachine(ms, cpu);
	}
	
	/**
	 * Loads the ROM data.
	 * 
	 * @param __romBase The base of the ROM.
	 * @param __sm The suite memory to load from.
	 * @param __sysProps System properties used.
	 * @return The memory that makes up the ROM.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/09
	 */
	private ReadableMemory __loadRom(int __romBase, VMSuiteManager __sm,
		Map<String, String> __sysProps)
		throws NullPointerException
	{
		if (__sm == null || __sysProps == null)
			throw new NullPointerException("NARG");
		
		// Try to load a specific ROM file instead of the dynamically
		// generate one?
		String romFile = __sysProps.get("cc.squirreljme.romfile");
		if (romFile == null)
			try
			{
				romFile = System.getProperty("cc.squirreljme.romfile");
			}
			catch (SecurityException ignored)
			{
			}
		
		// Load existing ROM file
		if (romFile != null)
		{
			// Debug
			if (NativeCPU.ENABLE_DEBUG)
				Debugging.debugNote("Using ROM %s", romFile);
			
			// Resultant ROM memory
			ByteArrayMemory romMemory;
			
			// Copy all of the file data
			Path p = Paths.get(romFile);
			try (InputStream in = Files.newInputStream(p,
					StandardOpenOption.READ);
				ByteArrayOutputStream baos = new ByteArrayOutputStream(
					(int)Files.size(p)))
			{
				// Read data
				byte[] buf = new byte[16384];
				for (;;)
				{
					int rc = in.read(buf);
					
					if (rc < 0)
						break;
					
					baos.write(buf, 0, rc);
				}
				
				// Initialize memory with the ROM data
				romMemory = new ByteArrayMemory(__romBase, baos.toByteArray());
			}
			
			// {@squirreljme.error AE0c Could not load SummerCoat ROM. (File)}
			catch (IOException e)
			{
				throw new RuntimeException("AE0c " + romFile, e);
			}
			
			return romMemory;
		}
		
		// Dynamically initialized suite memory
		else
		{
			// Create and map dynamic suite region
			SuitesMemory sm = new SuitesMemory(__romBase, __sm);
			
			// Initialize suite memory explicitly since we need it!
			sm.__init();
			
			return sm;
		}
	}
	
	/**
	 * Loads the ROM header.
	 * 
	 * @param __rom The ROM to read from.
	 * @param __packToc The output table of contents.
	 * @return The read pack header.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/12
	 */
	private static MinimizedPackHeader __loadRomHeader(ReadableMemory __rom,
		TableOfContents<MinimizedPackHeader>[] __packToc)
		throws NullPointerException
	{
		if (__rom == null || __packToc == null)
			throw new NullPointerException("NARG");
		
		// Read header data from memory
		try (InputStream packIn = new ReadableMemoryInputStream(__rom))
		{
			// Read the pack header
			MinimizedPackHeader header = MinimizedPackHeader.decode(packIn);
			
			// Parse the table of contents
			try (InputStream packTocIn = new ReadableMemoryInputStream(
				__rom, header.get(PackProperty.OFFSET_TOC),
				header.get(PackProperty.SIZE_TOC)))
			{
				__packToc[0] = TableOfContents.<MinimizedPackHeader>decode(
					MinimizedPackHeader.class, packTocIn);
			}
			
			return header;
		}
		catch (IOException e)
		{
			throw new VMException("Could not read pack headers.", e);
		}
	}
}

