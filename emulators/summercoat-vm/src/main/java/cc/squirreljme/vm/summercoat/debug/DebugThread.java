// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.summercoat.debug;

import cc.squirreljme.jdwp.JDWPStepTracker;
import cc.squirreljme.jdwp.JDWPThreadSuspension;
import cc.squirreljme.jdwp.views.JDWPViewThread;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.vm.summercoat.MachineState;
import cc.squirreljme.vm.summercoat.NativeCPU;
import java.lang.ref.Reference;

/**
 * Thread viewer for debugging.
 *
 * @since 2021/07/06
 */
public class DebugThread
	extends DebugBase
	implements JDWPViewThread
{
	/**
	 * Initializes the thread debug viewer.
	 * 
	 * @param __machine The machine used.
	 * @since 2021/07/06
	 */
	public DebugThread(Reference<MachineState> __machine)
	{
		super(__machine);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/07/06
	 */
	@Override
	public Object[] frames(Object __which)
	{
		return DebugThread.__cast(__which).frames();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/07/06
	 */
	@Override
	public Object instance(Object __which)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/07/06
	 */
	@Override
	public void interrupt(Object __which)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/07/06
	 */
	@Override
	public boolean isTerminated(Object __which)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/07/06
	 */
	@Override
	public boolean isValid(Object __which)
	{
		return (__which instanceof NativeCPU);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/07/06
	 */
	@Override
	public String name(Object __which)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/07/06
	 */
	@Override
	public Object parentGroup(Object __which)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/07/06
	 */
	@Override
	public JDWPStepTracker stepTracker(Object __which)
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/07/06
	 */
	@Override
	public JDWPThreadSuspension suspension(Object __which)
	{
		return DebugThread.__cast(__which).debugSuspension();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/07/06
	 */
	@Override
	public int status(Object __which)
	{
		throw Debugging.todo();
	}
	
	/**
	 * Casts the object.
	 * 
	 * @param __which The object to cast.
	 * @return The native CPU.
	 * @since 2021/07/06
	 */
	private static NativeCPU __cast(Object __which)
	{
		return (NativeCPU)__which;
	}
}
