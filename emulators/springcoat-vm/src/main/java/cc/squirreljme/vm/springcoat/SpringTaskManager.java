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
import cc.squirreljme.emulator.terminal.TerminalPipeManager;
import cc.squirreljme.emulator.vm.VMSuiteManager;
import cc.squirreljme.jdwp.JDWPBinding;
import cc.squirreljme.jdwp.JDWPController;
import cc.squirreljme.jdwp.JDWPState;
import cc.squirreljme.jdwp.views.JDWPView;
import cc.squirreljme.jdwp.views.JDWPViewKind;
import cc.squirreljme.jvm.mle.constants.StandardPipeType;
import cc.squirreljme.runtime.cldc.SquirrelJME;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.vm.VMClassLibrary;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * This class manages tasks within SpringCoat and is used to launch and
 * provide access to those that are running.
 *
 * @since 2018/11/04
 */
public final class SpringTaskManager
	implements JDWPBinding
{
	/** The manager for suites. */
	protected final VMSuiteManager suites;
	
	/** The profiling information. */
	protected final ProfilerSnapshot profiler;
	
	/** Global state. */
	protected final GlobalState globalState;
	
	/** The machine queue. */
	private final ReferenceQueue<SpringMachine> _machineGc =
		new ReferenceQueue<>(); 
	
	/** Machines that are running on the VM. */
	private final Collection<Reference<SpringMachine>> _machines =
		new LinkedList<>();
	
	/** Controller for JDWP Connections. */
	protected JDWPController jdwpController;
	
	/** Next thread ID, for debugging. */
	private volatile int _nextThreadId;
	
	/**
	 * Initializes the task manager.
	 *
	 * @param __sm The suite manager.
	 * @param __ps The snapshot for profiling.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/04
	 */
	public SpringTaskManager(VMSuiteManager __sm, ProfilerSnapshot __ps)
		throws NullPointerException
	{
		if (__sm == null)
			throw new NullPointerException("NARG");
		
		this.suites = __sm;
		this.profiler = (__ps == null ? new ProfilerSnapshot() : __ps);
		this.globalState = new GlobalState();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/03/14
	 */
	@Override
	public String[] debuggerLibraries()
	{
		return this.suites.listLibraryNames();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/10
	 */
	@Override
	public Object[] debuggerThreadGroups()
	{
		return this.tasks();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/10
	 */
	@Override
	public <V extends JDWPView> V debuggerView(Class<V> __type,
		JDWPViewKind __kind, Reference<JDWPState> __state)
		throws NullPointerException
	{
		// What do we want to view?
		switch (__kind)
		{
			case FRAME:
				return __type.cast(new DebugViewFrame(__state));
			
			case OBJECT:
				return __type.cast(new DebugViewObject(__state));
			
			case THREAD:
				return __type.cast(new DebugViewThread(__state));
			
			case THREAD_GROUP:
				return __type.cast(new DebugViewThreadGroup());
			
			case TYPE:
				return __type.cast(new DebugViewType(__state));
			
			default:
				throw Debugging.oops(__kind);
		}
	}
	
	/**
	 * Return the next thread ID.
	 * 
	 * @return The next thread ID.
	 * @since 2021/03/14
	 */
	protected int nextThreadId()
	{
		synchronized (this)
		{
			return ++this._nextThreadId;
		}
	}
	
	/**
	 * Spawns a new task.
	 * 
	 * @param __classpath The classpath to use.
	 * @param __mainClass The main entry class.
	 * @param __mainArgs The main arguments.
	 * @param __sysProps The system properties.
	 * @param __stdOutMode Standard output mode.
	 * @param __stdErrMode Standard error mode.
	 * @param __forkThread Should the task be started on a new thread?
	 * @param __rootVm Is this the root virtual machine?
	 * @return The spawned machine.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/07/09
	 */
	public SpringMachine startTask(VMClassLibrary[] __classpath,
		String __mainClass, String[] __mainArgs,
		Map<String, String> __sysProps, int __stdOutMode, int __stdErrMode,
		boolean __forkThread, boolean __rootVm)
		throws NullPointerException
	{
		if (__classpath == null || __mainClass == null || __mainArgs == null ||
			__sysProps == null)
			throw new NullPointerException("NARG");
		
		// Setup the pipe manager
		TerminalPipeManager pipes = new TerminalPipeManager();
		
		// Figure out where the pipe ends go
		pipes.registerByType(StandardPipeType.STDOUT, __stdOutMode);
		pipes.registerByType(StandardPipeType.STDERR, __stdErrMode);
		
		// Spawn the machine
		SpringClassLoader classloader = new SpringClassLoader(__classpath);
		SpringMachine machine = new SpringMachine(this.suites,
			classloader, this, __mainClass,
			this.profiler, new LinkedHashMap<>(__sysProps), this.globalState,
			pipes, __rootVm, __mainArgs);
		
		// Register the machine, use garbage collector for the weak references
		synchronized (this)
		{
			this._machines.add(new WeakReference<>(machine, this._machineGc));
		}
		
		// Fork a new thread
		if (__forkThread)
		{
			// Setup new thread
			Thread fork = new Thread(machine, String.format(
				"%s-%s-main", classloader.bootLibrary().name(),
				__mainClass));
			
			// Start executing it
			fork.start();
		}
		
		// Use the resultant machine
		return machine;
	}
	
	/**
	 * Returns the active tasks.
	 * 
	 * @return The active tasks.
	 * @since 2020/07/09
	 */
	public final SpringMachine[] tasks()
	{
		Collection<SpringMachine> result = new ArrayList<>();
		
		Collection<Reference<SpringMachine>> machines = this._machines;
		synchronized (this)
		{
			// See if we can early GC the machines that may have gone away
			ReferenceQueue<SpringMachine> gc = this._machineGc;
			for (Reference<? extends SpringMachine> ref = gc.poll();
				ref != null; ref = gc.poll())
				machines.remove(ref);
			
			// Go through machines to add to the collection
			for (Iterator<Reference<SpringMachine>> it = machines.iterator();
				it.hasNext();)
			{
				Reference<SpringMachine> ref = it.next();
				SpringMachine machine = ref.get();
				
				// Machine was cleared out, so get rid of it
				if (machine == null)
				{
					it.remove();
					continue;
				}
				
				result.add(machine);
			}
		}
		
		return result.<SpringMachine>toArray(new SpringMachine[result.size()]);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/03/13
	 */
	@Override
	public String vmDescription()
	{
		return "SquirrelJME SpringCoat " + this.vmVersion();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/03/13
	 */
	@Override
	public String vmName()
	{
		return "SquirrelJME SpringCoat";
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/03/13
	 */
	@Override
	public String vmVersion()
	{
		return SquirrelJME.RUNTIME_VERSION;
	}
}

