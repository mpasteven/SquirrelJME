// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.summercoat.debug;

import cc.squirreljme.jdwp.views.JDWPViewThreadGroup;
import cc.squirreljme.jvm.summercoat.constants.ClassProperty;
import cc.squirreljme.jvm.summercoat.constants.MemHandleKind;
import cc.squirreljme.jvm.summercoat.constants.TaskPropertyType;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.vm.summercoat.MachineState;
import cc.squirreljme.vm.summercoat.MemHandle;
import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.List;

/**
 * Viewer for thread groups.
 *
 * @since 2021/05/10
 */
public class DebugThreadGroup
	extends DebugBase
	implements JDWPViewThreadGroup
{
	/**
	 * Initializes the thread group debug viewer.
	 * 
	 * @param __machine The machine used.
	 * @since 2021/05/11
	 */
	public DebugThreadGroup(Reference<MachineState> __machine)
	{
		super(__machine);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/10
	 */
	@Override
	public Object[] allTypes(Object __which)
	{
		List<MemHandle> rv = new ArrayList<>(); 
		
		// Debug
		Debugging.debugNote("allTypes(0x%08x)", __which);
		
		// Go through the entire class chain
		MemHandle atClass = this.getHandle(
			DebugBase.handle(__which, MemHandleKind.TASK),
			TaskPropertyType.CLASS_FIRST);
		while (atClass != null)
		{
			// Debug
			Debugging.debugNote("Add Class: %s", atClass);
			
			// Add this class
			rv.add(atClass);
			
			// Go to the next class
			atClass = this.getHandle(atClass,
				ClassProperty.TYPEBRACKET_LINK_CLASS_NEXT);
		}
		
		return rv.toArray(new Object[rv.size()]);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/10
	 */
	@Override
	public void exit(Object __which, int __code)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/10
	 */
	@Override
	public Object findType(Object __which, String __name)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/10
	 */
	@Override
	public boolean isValid(Object __which)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/10
	 */
	@Override
	public String name(Object __which)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/10
	 */
	@Override
	public Object[] threads(Object __which)
	{
		throw Debugging.todo();
	}
}
