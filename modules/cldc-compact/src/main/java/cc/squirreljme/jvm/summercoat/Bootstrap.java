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
import cc.squirreljme.jvm.launch.AvailableSuites;
import cc.squirreljme.jvm.launch.SuiteScanner;
import cc.squirreljme.jvm.mle.RuntimeShelf;
import cc.squirreljme.jvm.mle.TerminalShelf;
import cc.squirreljme.jvm.mle.ThreadShelf;
import cc.squirreljme.jvm.mle.constants.StandardPipeType;
import cc.squirreljme.jvm.mle.constants.ThreadModelType;
import cc.squirreljme.runtime.cldc.SquirrelJME;
import cc.squirreljme.runtime.cldc.debug.CallTraceUtils;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.runtime.cldc.io.ConsoleOutputStream;

/**
 * Main bootstrap entry point.
 *
 * @since 2020/11/27
 */
public final class Bootstrap
{
	/**
	 * Main entry point for the virtual machine.
	 * 
	 * @throws Throwable On any exception.
	 * @since 2020/11/28
	 */
	public static void vmEntry()
		throws Throwable
	{
		// Introduction banner for the virtual machine itself
		Debugging.notice("SquirrelJME %s",
			SquirrelJME.RUNTIME_VERSION);
		Debugging.notice("(C) 2013-2021 Stephanie Gawroriski");
		Debugging.notice("Licensed under the GPLv3!");
		Debugging.notice("E-Mail : xerthesquirrel@gmail.com");
		Debugging.notice("Website: https://squirreljme.cc/");
		Debugging.notice("Donate!: https://patreon.com/SquirrelJME");
		Debugging.notice("");
		
		// Which thread model is being used?
		switch (ThreadShelf.model())
		{
			case ThreadModelType.SINGLE_COOP_THREAD:
				Debugging.notice("Thread model: Single Coop");
				break;
				
			case ThreadModelType.SIMULTANEOUS_MULTI_THREAD:
				Debugging.notice("Thread model: SMT");
				break;
			
			default:
				Debugging.notice("Thread model: Unknown");
				break;
		}
		
		// Perform a scan for every suite, we need to find the launcher!
		Debugging.notice("Performing initial suite scan...");
		try
		{
			AvailableSuites suites = SuiteScanner.scanSuites(null);
		
			Assembly.breakpoint();
			throw Debugging.todo();
		}
		
		// Print a very nasty message regarding this
		catch (Throwable __t)
		{
			// Print trace out
			Debugging.debugNote("************************************");
			Debugging.debugNote("*** CRITICAL BOOT EXCEPTION");
			Debugging.debugNote("Message: %s", __t.getMessage());
			CallTraceUtils.printStackTrace(
				new ConsoleOutputStream(StandardPipeType.STDERR),
				__t, 0);
			TerminalShelf.flush(StandardPipeType.STDERR);
			Debugging.debugNote("************************************");
			
			// Break if we can
			Debugging.debugNote("Sending breakpoint...");
			Assembly.breakpoint();
			
			// Stop execution here
			Debugging.debugNote("Terminating...");
			RuntimeShelf.exit(17);
			throw __t;
		}
	}
}
