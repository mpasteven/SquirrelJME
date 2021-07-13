// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.summercoat;

import cc.squirreljme.jvm.summercoat.constants.ClassProperty;
import cc.squirreljme.jvm.summercoat.constants.StaticClassProperty;
import cc.squirreljme.jvm.summercoat.constants.StaticVmAttribute;
import cc.squirreljme.jvm.summercoat.ld.mem.ReadableMemoryInputStream;
import cc.squirreljme.jvm.summercoat.ld.mem.WritableMemory;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Extra virtual machine utilities.
 *
 * @since 2021/07/06
 */
public final class VMUtils
{
	/**
	 * Not used.
	 * 
	 * @since 2021/07/06
	 */
	private VMUtils()
	{
	}
	
	/**
	 * Obtains an integer value from the given memory handle.
	 * 
	 * @param __machine The machine used.
	 * @param __handle The handle to read from.
	 * @param __dx The index to read at.
	 * @return The value.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/05/11
	 */
	public static int getHandleInteger(MachineState __machine,
		MemHandle __handle, int __dx)
		throws NullPointerException
	{
		if (__machine == null || __handle == null)
			throw new NullPointerException("NARG");
		
		// Read the list value
		return __handle.memReadInt((4L * __dx) +
			__machine.staticAttribute(StaticVmAttribute.SIZE_BASE_ARRAY));
	}
	
	/**
	 * Returns the given long value from a handle.
	 * 
	 * @param __machine The machine to read from.
	 * @param __handle The handle to read from.
	 * @param __dx The property, this should be the low value as these
	 * properties are in low+high order.
	 * @return The long value.
	 * @since 2021/07/05
	 */
	public static long getHandleLong(MachineState __machine,
		MemHandle __handle, int __dx)
	{
		return (VMUtils.getHandleInteger(__machine,
				__handle, __dx) & 0xFFFFFFFFL) |
			((VMUtils.getHandleInteger(__machine,
				__handle, __dx + 1) & 0xFFFFFFFFL) << 32);
	}
	
	/**
	 * Safely reads a UTF-8 string value.
	 * 
	 * @param __machine The machine used.
	 * @param __addr The address to read from.
	 * @return The read string.
	 * @since 2021/07/05
	 */
	public static String readUtfSafe(MachineState __machine, long __addr)
	{
		if (__machine == null)
			throw new NullPointerException("NARG");
		
		// Read length to figure out how long the string is
		WritableMemory memory = __machine.memory();
		int strlen = -1;
		try
		{
			strlen = memory.memReadShort(__addr) & 0xFFFF;
			
			// Decode string data
			try (DataInputStream dis = new DataInputStream(
				new ReadableMemoryInputStream(memory, __addr,
					strlen + 2)))
			{
				return dis.readUTF();
			}
		}
		
		// Could not read string, use some other string form
		catch (IOException | VMMemoryAccessException e)
		{
			return String.format("@%08x/%d???", __addr, strlen);
		}
	}
	
	/**
	 * Returns the name of the class the type bracket refers to.
	 * 
	 * @param __machine The machine to read memory from.
	 * @param __handle The type bracket handle to read the class name from.
	 * @return The name of the class.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/07/06
	 */
	public static String typeBracketName(MachineState __machine,
		MemHandle __handle)
		throws NullPointerException
	{
		if (__machine == null || __handle == null)
			throw new NullPointerException("NARG");
		
		// Determine where this will be located
		long romAddr = VMUtils.getHandleLong(__machine, __handle,
			ClassProperty.MEMPTR_ROM_CLASS_LO);
		int offEmbName = VMUtils.getHandleInteger(__machine, __handle,
			StaticClassProperty.OFFSETOF_DEBUG_SIGNATURE);
		
		return VMUtils.readUtfSafe(__machine, romAddr + offEmbName);
	}
}
