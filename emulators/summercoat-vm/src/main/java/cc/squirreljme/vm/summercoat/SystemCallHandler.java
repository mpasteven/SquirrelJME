// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.summercoat;

import cc.squirreljme.jvm.CallStackItem;
import cc.squirreljme.jvm.SystemCallError;
import cc.squirreljme.jvm.SystemCallIndex;
import cc.squirreljme.jvm.mle.constants.BuiltInEncodingType;
import cc.squirreljme.jvm.mle.constants.BuiltInLocaleType;
import cc.squirreljme.jvm.mle.constants.ByteOrderType;
import cc.squirreljme.jvm.mle.constants.MemoryProfileType;
import cc.squirreljme.jvm.mle.constants.PipeErrorType;
import cc.squirreljme.jvm.mle.constants.StandardPipeType;
import cc.squirreljme.jvm.summercoat.constants.MemHandleKind;
import cc.squirreljme.jvm.summercoat.constants.RuntimeVmAttribute;
import cc.squirreljme.jvm.summercoat.constants.StaticVmAttribute;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.runtime.cldc.lang.OperatingSystemType;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Handler for system calls.
 *
 * @since 2021/01/24
 */
public enum SystemCallHandler
{
	/** {@link SystemCallIndex#ARRAY_ALLOCATION_BASE}. */
	ARRAY_ALLOCATION_BASE(SystemCallIndex.ARRAY_ALLOCATION_BASE)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/01/24
		 */
		@Override
		public long handle(NativeCPU __cpu, int... __args)
			throws VMSystemCallException
		{
			return __cpu.__state().staticAttribute(StaticVmAttribute.SIZE_BASE_ARRAY);
		}
	},
	
	/** {@link SystemCallIndex#CALL_STACK_HEIGHT}. */
	CALL_STACK_HEIGHT(SystemCallIndex.CALL_STACK_HEIGHT)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/04/03
		 */
		@Override
		public long handle(NativeCPU __cpu, int... __args)
			throws VMSystemCallException
		{
			return __cpu.countFrames();
		}
	},
	
	/** {@link SystemCallIndex#CALL_STACK_ITEM}. */
	CALL_STACK_ITEM(SystemCallIndex.CALL_STACK_ITEM)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/04/03
		 */
		@Override
		public long handle(NativeCPU __cpu, int... __args)
			throws VMSystemCallException
		{
			// Check validity of this frame
			CPUFrame[] frames = __cpu.frames();
			int frameId = (frames.length - __args[0]) - 1;
			if (frameId < 0 || frameId >= frames.length)
				return 0;
			
			// Get specific frame details
			CPUFrame frame = frames[frameId];
			switch (__args[1])
			{
				case CallStackItem.CLASS_NAME:
					return frame._inClassP;
				
				case CallStackItem.METHOD_NAME:
					return frame._inMethodNameP;
				
				case CallStackItem.METHOD_TYPE:
					return frame._inMethodTypeP;
				
				case CallStackItem.SOURCE_FILE:
					return frame._inSourceFileP;
				
				case CallStackItem.SOURCE_LINE:
					return frame._inline;
				
				case CallStackItem.PC_ADDRESS:
					return frame._pc;
				
				case CallStackItem.JAVA_OPERATION:
					return frame._injop;
				
				case CallStackItem.JAVA_PC_ADDRESS:
					return frame._injpc;
				
				case CallStackItem.TASK_ID:
					return frame._taskid;
				
					// Unknown, ignore
				default:
					return 0;
			}
		}
	},
	
	/** {@link SystemCallIndex#ERROR_GET}. */
	ERROR_GET(SystemCallIndex.ERROR_GET)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/01/24
		 */
		@Override
		public long handle(NativeCPU __cpu, int... __args)
			throws VMSystemCallException
		{
			// Normalize ID number
			SystemCallHandler handler = SystemCallHandler.of(__args[0]);
			int normalId = (handler == null ? SystemCallIndex.QUERY_INDEX :
				handler.id);
			
			// Obtain system call error
			int[] sysCallErrors = __cpu._sysCallErrors;
			synchronized (__cpu)
			{
				return sysCallErrors[normalId];
			}
		}
	},
	
	/** {@link SystemCallIndex#ERROR_SET}. */
	ERROR_SET(SystemCallIndex.ERROR_SET)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/01/24
		 */
		@Override
		public long handle(NativeCPU __cpu, int... __args)
			throws VMSystemCallException
		{
			// Normalize ID number
			SystemCallHandler handler = SystemCallHandler.of(__args[0]);
			int normalId = (handler == null ? SystemCallIndex.QUERY_INDEX :
				handler.id);
			
			// Obtain system call error
			int[] sysCallErrors = __cpu._sysCallErrors;
			synchronized (__cpu)
			{
				sysCallErrors[normalId] = __args[1];
			}
			
			return 0;
		}
	},
	
	/** {@link SystemCallIndex#MEM_HANDLE_IN_BOUNDS}. */
	MEM_HANDLE_IN_BOUNDS(SystemCallIndex.MEM_HANDLE_IN_BOUNDS)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/01/24
		 */
		@Override
		public long handle(NativeCPU __cpu, int... __args)
			throws VMSystemCallException
		{
			MemHandle handle = __cpu.__state().memHandles.get(__args[0]);
			int off = __args[1];
			int len = __args[2];
			
			// Debug
			if (NativeCPU.ENABLE_DEBUG)
			{
				String handleStr = handle.toString();
				Debugging.debugNote(
					"MEM_HANDLE_IN_BOUNDS(%s, %d, %d) in %d",
					handleStr, off, len, handle.size);
			}
			
			// Check if in bounds
			int size = handle.size;
			if (off < 0 || len < 0 || off + len > size)
				return 0;
			return 1;
		}
	},
	
	/** {@link SystemCallIndex#MEM_HANDLE_MOVE}. */
	MEM_HANDLE_MOVE(SystemCallIndex.MEM_HANDLE_MOVE)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/02/19
		 */
		@Override
		public long handle(NativeCPU __cpu, int... __args)
			throws VMSystemCallException
		{
			// Get all the details for the system call
			MemHandle src = __cpu.__state().memHandles.get(__args[0]);
			int srcOff = __args[1];
			MemHandle dest = __cpu.__state().memHandles.get(__args[2]);
			int destOff = __args[3];
			int length = __args[4];
			
			// Copy all the data around
			for (int i = 0; i < length; i++)
				dest.memWriteByte(destOff + i,
					src.memReadByte(srcOff + i));
			
			return 0;
		}
	},
	
	/** {@link SystemCallIndex#MEM_HANDLE_NEW}. */
	MEM_HANDLE_NEW(SystemCallIndex.MEM_HANDLE_NEW)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/01/24
		 */
		@Override
		public long handle(NativeCPU __cpu, int... __args)
			throws VMSystemCallException
		{
			int kind = __args[0];
			int size = __args[1];
			
			// Are these not valid?
			if (kind <= 0 || kind >= MemHandleKind.NUM_KINDS)
				throw new VMSystemCallException(
					SystemCallError.INVALID_MEMHANDLE_KIND);
			else if (size < 0)
				throw new VMSystemCallException(
					SystemCallError.VALUE_OUT_OF_RANGE);
			
			// Allocate handle and count up so it is not instantly GCed
			MemHandle handle = __cpu.__state().memHandles.alloc(kind, size);
			handle.count(true);
			 
			return handle.id;
		}
	},
	
	/** {@link SystemCallIndex#RUNTIME_VM_ATTRIBUTE}. */
	RUNTIME_VM_ATTRIBUTE(SystemCallIndex.RUNTIME_VM_ATTRIBUTE)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/01/30
		 */
		@Override
		public long handle(NativeCPU __cpu, int... __args)
			throws VMSystemCallException
		{
			switch (__args[0])
			{
					// The address of the system ROM
				case RuntimeVmAttribute.ROM_ADDR_LOW:
					return __cpu.__state().romBase;
					
					// SummerCoat is purely a 32-bit system so there is no
					// high address for the ROM.
				case RuntimeVmAttribute.ROM_ADDR_HIGH:
					return 0x0;
				
					// The current operating system
				case RuntimeVmAttribute.OPERATING_SYSTEM:
					String systemOs = System.getProperty("os.name")
						.toLowerCase();
					
					if (systemOs.contains("windows"))
						return OperatingSystemType.WINDOWS_UNKNOWN;
					else if (systemOs.contains("linux"))
						return OperatingSystemType.LINUX;
					else if (systemOs.contains("solaris"))
						return OperatingSystemType.SOLARIS;
					else if (systemOs.contains("mac os"))
						return OperatingSystemType.MAC_OS_X;
					
					return OperatingSystemType.UNKNOWN;
					
					// The memory profile of the system
				case RuntimeVmAttribute.MEMORY_PROFILE:
					return MemoryProfileType.NORMAL;
				
					// Always big endian
				case RuntimeVmAttribute.BYTE_ORDER:
					return ByteOrderType.BIG_ENDIAN;
					
					// The encoding: Always UTF-8
				case RuntimeVmAttribute.ENCODING:
					return BuiltInEncodingType.UTF8;
					
					// The locale: Always English US
				case RuntimeVmAttribute.LOCALE:
					return BuiltInLocaleType.ENGLISH_US;
					
					// The thread model
				case RuntimeVmAttribute.THREAD_MODEL:
					return __cpu.__state().threadingModel.model;
				
					// Unknown
				default:
					return 0;
			}
		}
	},
	
	/** {@link SystemCallIndex#PD_FLUSH}. */
	PD_FLUSH(SystemCallIndex.PD_FLUSH)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/04/03
		 */
		@Override
		public long handle(NativeCPU __cpu, int... __args)
			throws VMSystemCallException
		{
			int pd = __args[0];
		
			// Determine where we are writing to
			PrintStream target;
			switch (pd)
			{
				case StandardPipeType.STDOUT:
					target = System.out;
					break;
					
				case StandardPipeType.STDERR:
					target = System.err;
					break;
				
				default:
					throw new VMSystemCallException(
						SystemCallError.PIPE_DESCRIPTOR_INVALID);
			}
			
			// Try to flush
			target.flush();
			
			// Did we fail the flush?
			if (target.checkError())
				throw new VMSystemCallException(
					SystemCallError.PIPE_DESCRIPTOR_BAD_FLUSH);
			
			// Write okay
			return PipeErrorType.NO_ERROR;
		}
	},
	
	/** {@link SystemCallIndex#PD_OF_STDERR}. */
	PD_OF_STDERR(SystemCallIndex.PD_OF_STDERR)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/01/24
		 */
		@Override
		public long handle(NativeCPU __cpu, int... __args)
			throws VMSystemCallException
		{
			return StandardPipeType.STDERR;
		}
	},
	
	/** {@link SystemCallIndex#PD_WRITE_BYTE}. */
	PD_WRITE_BYTE(SystemCallIndex.PD_WRITE_BYTE)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/01/24
		 */
		@Override
		public long handle(NativeCPU __cpu, int... __args)
			throws VMSystemCallException
		{
			int pd = __args[0];
			int val = __args[1];
		
			// Determine where we are writing to
			PrintStream target;
			switch (pd)
			{
				case StandardPipeType.STDOUT:
					target = System.out;
					break;
					
				case StandardPipeType.STDERR:
					target = System.err;
					break;
				
				default:
					throw new VMSystemCallException(
						SystemCallError.PIPE_DESCRIPTOR_INVALID);
			}
			
			// Try to write
			target.write(val);
			
			// Did we fail the write?
			if (target.checkError())
				throw new VMSystemCallException(
					SystemCallError.PIPE_DESCRIPTOR_BAD_WRITE);
			
			// Write okay
			return 1;
		}
	},
	
	/** {@link SystemCallIndex#QUERY_INDEX}. */
	QUERY_INDEX(SystemCallIndex.QUERY_INDEX)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/01/24
		 */
		@Override
		public long handle(NativeCPU __cpu, int... __args)
			throws VMSystemCallException
		{
			// Just checks to see if the system call is implemented
			if (SystemCallHandler.of(__args[0]) != null)
				return 1;
			return 0;
		}
	},
	
	/** {@link SystemCallIndex#VERBOSE}. */
	VERBOSE(SystemCallIndex.VERBOSE)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/02/20
		 */
		@Override
		public long handle(NativeCPU __cpu, int... __args)
			throws VMSystemCallException
		{
			// TODO: Implement this verbosity.
			Debugging.todoNote("Implement VERBOSE.");
			return 0;
		}
	},
	
	/** {@link SystemCallIndex#STATIC_VM_ATTRIBUTES}. */
	STATIC_VM_ATTRIBUTES(SystemCallIndex.STATIC_VM_ATTRIBUTES)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/01/24
		 */
		@Override
		public long handle(NativeCPU __cpu, int... __args)
			throws VMSystemCallException
		{
			return __cpu.__state().staticAttributes().id;
		}
	},
	
	/* End. */
	;
	
	/** Lookup for system call handlers by ID. */
	private static final SystemCallHandler[] _LOOKUP =
		SystemCallHandler.__buildLookupTable();
	
	/** The system call ID. */
	protected final int id;
	
	/**
	 * Initializes the handler instance.
	 * 
	 * @param __id The identifier for the system call.
	 * @since 2021/01/24
	 */
	SystemCallHandler(int __id)
	{
		this.id = __id;
	}
	
	/**
	 * Handles the given system call.
	 * 
	 * @param __cpu The CPU used.
	 * @param __args The arguments to the call.
	 * @return The resultant system call value.
	 * @throws VMSystemCallException On any error.
	 * @since 2021/01/24
	 */
	public abstract long handle(NativeCPU __cpu, int... __args)
		throws VMSystemCallException;
	
	/**
	 * Returns the handler for the system call.
	 * 
	 * @param __id The ID to look for.
	 * @return The handler for the given ID or {@code null} if not supported.
	 * @since 2021/01/24
	 */
	public static SystemCallHandler of(int __id)
	{
		if (__id < 0)
			return null;
		
		// Out of bounds?
		SystemCallHandler[] lookup = SystemCallHandler._LOOKUP;
		if (__id >= lookup.length)
			return null;
		
		return lookup[__id];
	}
	
	/**
	 * Builds a lookup table for system calls.
	 * 
	 * @return The built lookup table.
	 * @since 2021/01/24
	 */
	private static SystemCallHandler[] __buildLookupTable()
	{
		// Build lookup table which maps indexes to handlers
		List<SystemCallHandler> lookup = new ArrayList<>(
			SystemCallIndex.NUM_SYSCALLS);
		for (SystemCallHandler handler : SystemCallHandler.values())
		{
			int id = handler.id;
			
			// Grow the list until it can fit this ID
			while (lookup.size() <= id)
				lookup.add(null);
			
			// Set lookup at position
			if (lookup.set(id, handler) != null)
				throw Debugging.oops("Duplicate ID: " + handler);
		}
		
		// Store into the lookup table
		return lookup.<SystemCallHandler>toArray(
			new SystemCallHandler[lookup.size()]);
	}
}
