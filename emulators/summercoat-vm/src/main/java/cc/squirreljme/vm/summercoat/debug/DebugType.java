// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.summercoat.debug;

import cc.squirreljme.jdwp.JDWPValue;
import cc.squirreljme.jdwp.trips.JDWPTripBreakpoint;
import cc.squirreljme.jdwp.views.JDWPViewType;
import cc.squirreljme.jvm.summercoat.constants.ClassProperty;
import cc.squirreljme.jvm.summercoat.constants.MemHandleKind;
import cc.squirreljme.jvm.summercoat.constants.StaticClassProperty;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.vm.summercoat.MachineState;
import cc.squirreljme.vm.summercoat.MemHandle;
import cc.squirreljme.vm.summercoat.VMUtils;
import java.lang.ref.Reference;

/**
 * Viewer for types.
 *
 * @since 2021/05/11
 */
public class DebugType
	extends DebugBase
	implements JDWPViewType
{
	/**
	 * Initializes the debug base.
	 *
	 * @param __machine The machine used.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/05/11
	 */
	public DebugType(Reference<MachineState> __machine)
		throws NullPointerException
	{
		super(__machine);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/11
	 */
	@Override
	public Object classLoader(Object __which)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/11
	 */
	@Override
	public Object componentType(Object __which)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/11
	 */
	@Override
	public int flags(Object __which)
	{
		MemHandle handle = DebugBase.handleType(__which);
		
		return this.getInteger(handle, StaticClassProperty.INT_CLASS_FLAGS);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/11
	 */
	@Override
	public Object[] interfaceTypes(Object __which)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/11
	 */
	@Override
	public boolean isValid(Object __which)
	{
		return DebugBase.handle(__which, MemHandleKind.UNDEFINED).kind() ==
			MemHandleKind.CLASS_INFO;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/11
	 */
	@Override
	public boolean isValidField(Object __which, int __fieldDx)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/11
	 */
	@Override
	public boolean isValidMethod(Object __which, int __methodDx)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/11
	 */
	@Override
	public int fieldFlags(Object __which, int __fieldDx)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/11
	 */
	@Override
	public String fieldName(Object __which, int __fieldDx)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/11
	 */
	@Override
	public int[] fields(Object __which)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/11
	 */
	@Override
	public String fieldSignature(Object __which, int __fieldDx)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/11
	 */
	@Override
	public void fieldWatch(Object __which, int __fieldDx, boolean __write)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/11
	 */
	@Override
	public Object instance(Object __which)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/11
	 */
	@Override
	public void methodBreakpoint(Object __which, int __methodDx, int __codeDx,
		JDWPTripBreakpoint __trip)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/11
	 */
	@Override
	public byte[] methodByteCode(Object __which, int __methodDx)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/11
	 */
	@Override
	public int methodFlags(Object __which, int __methodDx)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/11
	 */
	@Override
	public int[] methodLineTable(Object __which, int __methodDx)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/11
	 */
	@Override
	public int methodLocationCount(Object __which, int __methodDx)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/11
	 */
	@Override
	public String methodName(Object __which, int __methodDx)
	{
		MemHandle handle = DebugBase.handleType(__which);
		
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/11
	 */
	@Override
	public int[] methods(Object __which)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/11
	 */
	@Override
	public String methodSignature(Object __which, int __methodDx)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/11
	 */
	@Override
	public boolean readValue(Object __which, int __index, JDWPValue __out)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/11
	 */
	@Override
	public String signature(Object __which)
	{
		return VMUtils.typeBracketName(this.__machine(),
			DebugBase.handleType(__which));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/11
	 */
	@Override
	public String sourceFile(Object __which)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/11
	 */
	@Override
	public Object superType(Object __which)
	{
		throw Debugging.todo();
	}
}
