// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.springcoat;

import cc.squirreljme.emulator.profiler.ProfilerSnapshot;
import cc.squirreljme.emulator.vm.VMException;
import cc.squirreljme.emulator.vm.VMFactory;
import cc.squirreljme.emulator.vm.VMSuiteManager;
import cc.squirreljme.emulator.vm.VMThreadModel;
import cc.squirreljme.emulator.vm.VirtualMachine;
import cc.squirreljme.jdwp.JDWPFactory;
import cc.squirreljme.jvm.mle.constants.TaskPipeRedirectType;
import cc.squirreljme.vm.VMClassLibrary;
import java.util.Map;

/**
 * Factory which creates instances of the SpringCoat virtual machine.
 *
 * @since 2018/11/17
 */
public class SpringCoatFactory
	extends VMFactory
{
	/**
	 * Initializes the factory.
	 *
	 * @since 2018/11/17
	 */
	public SpringCoatFactory()
	{
		super("springcoat");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/11/17
	 */
	@Override
	protected VirtualMachine createVM(ProfilerSnapshot __ps,
		JDWPFactory __jdwp, VMThreadModel __threadModel, VMSuiteManager __sm,
		VMClassLibrary[] __cp, String __maincl, Map<String, String> __sprops,
		String[] __args)
		throws IllegalArgumentException, NullPointerException, VMException
	{
		// Setup the main task manager which runs everything
		SpringTaskManager tm = new SpringTaskManager(__sm, __ps);
		
		// Bind this to the task manager which is the pure global state
		if (__jdwp != null)
			tm.jdwpController = __jdwp.open(tm); 
		
		// Spawn initial virtual machine task
		return tm.startTask(__cp, __maincl, __args, __sprops,
			TaskPipeRedirectType.TERMINAL, TaskPipeRedirectType.TERMINAL,
			false, true);
	}
}

