// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.summercoat;

import cc.squirreljme.jvm.Assembly;
import cc.squirreljme.jvm.mle.RuntimeShelf;
import cc.squirreljme.jvm.mle.brackets.TypeBracket;
import cc.squirreljme.jvm.mle.constants.ByteOrderType;
import cc.squirreljme.jvm.mle.exceptions.MLECallError;
import cc.squirreljme.jvm.summercoat.constants.ClassProperty;
import cc.squirreljme.jvm.summercoat.constants.StaticVmAttribute;

/**
 * This handles specific JVM logic that cannot easily be handled in byte code
 * or at the execution layer.
 *
 * @since 2020/11/28
 */
public final class LogicHandler
{
	/**
	 * Not used.
	 * 
	 * @since 2020/11/28
	 */
	private LogicHandler()
	{
	}
	
	/**
	 * Returns the value of the given class property.
	 * 
	 * @param __info The information to get.
	 * @param __p The {@link ClassProperty}.
	 * @return The value of the given property.
	 * @throws MLECallError If {@code __info} is {@code null} or is not a
	 * valid class.
	 * @since 2021/02/07
	 */
	public static int typeGetProperty(int __info, int __p)
		throws MLECallError
	{
		if (__info == 0)
			throw new MLECallError("NARG");
		
		// {@squirreljme.error ZZ4m Invalid class property. (The property)}
		if (__p <= 0 || __p >= ClassProperty.NUM_RUNTIME_PROPERTIES)
			throw new MLECallError("ZZ4m " + __p);
		
		return LogicHandler.listRead(__info, __p);
	}
	
	/**
	 * Returns the value of the given class property.
	 * 
	 * @param __info The information to get.
	 * @param __p The {@link ClassProperty}.
	 * @return The value of the given property.
	 * @throws MLECallError If {@code __info} is {@code null} or is not a
	 * valid class.
	 * @since 2020/11/29
	 */
	public static int typeGetProperty(TypeBracket __info, int __p)
		throws MLECallError
	{
		if (__info == null)
			throw new MLECallError("NARG");
		
		// {@squirreljme.error ZZ4m Invalid class property. (The property)}
		if (__p <= 0 || __p >= ClassProperty.NUM_RUNTIME_PROPERTIES)
			throw new MLECallError("ZZ4m " + __p);
		
		return LogicHandler.listRead(__info, __p);
	}
	
	/**
	 * Returns the value of the given class property as a {@code long}.
	 * 
	 * @param __info The information to get.
	 * @param __p The {@link ClassProperty}.
	 * @return The value of the given property.
	 * @throws MLECallError If {@code __info} is {@code null} or is not a
	 * valid class.
	 * @since 2021/04/07
	 */
	public static long typeGetPropertyLong(TypeBracket __info, int __p)
		throws MLECallError
	{
		int a = LogicHandler.typeGetProperty(__info, __p);
		int b = LogicHandler.typeGetProperty(__info, __p + 1);
		
		// Depends on the system endianess
		if (RuntimeShelf.byteOrder() == ByteOrderType.BIG_ENDIAN)
			return Assembly.longPack(a, b);
		return Assembly.longPack(b, a);
	}
	
	/**
	 * Sets the value of the given class property.
	 * 
	 * @param __info The information to get.
	 * @param __p The {@link ClassProperty}.
	 * @param __v The value to set.
	 * @throws MLECallError If {@code __info} is {@code null} or is not a
	 * valid class.
	 * @since 2021/04/03
	 */
	public static void typeSetProperty(TypeBracket __info, int __p, int __v)
		throws MLECallError
	{
		if (__info == null)
			throw new MLECallError("NARG");
		
		// {@squirreljme.error ZZ4w Invalid class property. (The property)}
		if (__p <= 0 || __p >= ClassProperty.NUM_RUNTIME_PROPERTIES)
			throw new MLECallError("ZZ4w " + __p);
		
		LogicHandler.listWrite(__info, __p, __v);
	}
	
	/**
	 * Garbage collects the given handle.
	 * 
	 * @param __p The pointer to clear.
	 * @since 2020/11/28
	 */
	public static void gcMemHandle(int __p)
	{
		// TODO: Implement Garbage Collection of Memory Handles
		//Assembly.ping();
		/*Assembly.breakpoint();
		throw Debugging.todo();*/
	}
	
	/**
	 * Returns the length of the given list memory handle.
	 * 
	 * @param __o The list based memory handle to get the length of.
	 * @return The length of the list.
	 * @throws MLECallError On null arguments.
	 * @since 2021/02/07
	 */
	public static int listLength(int __o)
		throws MLECallError
	{
		if (__o == 0)
			throw new MLECallError("NARG");
			
		return Assembly.memHandleReadInt(__o, LogicHandler.staticVmAttribute(
			StaticVmAttribute.OFFSETOF_ARRAY_LENGTH_FIELD));
	}
	
	/**
	 * Reads from the given list based handle.
	 * 
	 * @param __o The handle to read from.
	 * @param __dx The index to read from.
	 * @return The value of the given index.
	 * @throws MLECallError If the handle is {@code null}.
	 * @since 2021/02/07
	 */
	public static int listRead(int __o, int __dx)
		throws MLECallError
	{
		if (__o == 0)
			throw new MLECallError("NARG");
		
		int arrayBase = SystemCall.arrayAllocationBase();
		return Assembly.memHandleReadInt(__o, arrayBase + (__dx * 4));
	}
	
	/**
	 * Reads from the given list based handle.
	 * 
	 * @param __o The handle to read from.
	 * @param __dx The index to read from.
	 * @return The value of the given index.
	 * @throws MLECallError If the handle is {@code null}.
	 * @since 2021/02/07
	 */
	public static int listRead(Object __o, int __dx)
		throws MLECallError
	{
		return LogicHandler.listRead(Assembly.objectToPointer(__o), __dx);
	}
	
	/**
	 * Reads from the given list based handle.
	 * 
	 * @param __o The handle to write to.
	 * @param __dx The index to write to.
	 * @param __v The value to write.
	 * @throws MLECallError If the handle is {@code null}.
	 * @since 2021/02/07
	 */
	public static void listWrite(int __o, int __dx, int __v)
		throws MLECallError
	{
		if (__o == 0)
			throw new MLECallError("NARG");
		
		int arrayBase = SystemCall.arrayAllocationBase();
		Assembly.memHandleWriteInt(__o, arrayBase + (__dx * 4), __v);
	}
	
	/**
	 * Reads from the given list based handle.
	 * 
	 * @param __o The handle to write to.
	 * @param __dx The index to write to.
	 * @param __v The value to write.
	 * @throws MLECallError If the handle is {@code null}.
	 * @since 2021/02/07
	 */
	public static void listWrite(Object __o, int __dx, int __v)
		throws MLECallError
	{
		LogicHandler.listWrite(Assembly.objectToPointer(__o), __dx, __v);
	}
	
	/**
	 * This is the method that is called for all native and abstract methods
	 * within the virtual machine so that every method leads somewhere.
	 * 
	 * @throws PureVirtualMethodCallError Always.
	 * @since 2021/01/30
	 */
	public static void pureVirtualCall()
		throws PureVirtualMethodCallError
	{
		// {@squirreljme.error ZZ40 Pure virtual method call.}
		throw new PureVirtualMethodCallError("ZZ4o");
	}
	
	/**
	 * Returns the static virtual machine attribute.
	 * 
	 * @param __attr The {@link StaticVmAttribute} to get.
	 * @return The value of the metric.
	 * @throws IllegalArgumentException If the metric is not valid.
	 * @since 2021/01/24
	 */
	public static int staticVmAttribute(int __attr)
		throws IllegalArgumentException
	{
		// {@squirreljme.error ZZ4n Invalid Vm Metric, (Metric Id)}
		if (__attr <= 0 || __attr >= StaticVmAttribute.NUM_METRICS)
			throw new IllegalArgumentException("ZZ4n " + __attr);
		
		return LogicHandler.listRead(SystemCall.staticVmAttributes(), __attr);
	}
}
