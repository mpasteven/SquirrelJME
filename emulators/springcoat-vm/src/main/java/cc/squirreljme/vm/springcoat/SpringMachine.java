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
import cc.squirreljme.emulator.vm.VMResourceAccess;
import cc.squirreljme.emulator.vm.VMSuiteManager;
import cc.squirreljme.emulator.vm.VirtualMachine;
import cc.squirreljme.runtime.cldc.debug.CallTraceElement;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.vm.springcoat.exceptions.SpringMachineExitException;
import cc.squirreljme.vm.springcoat.exceptions.SpringVirtualMachineException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import net.multiphasicapps.classfile.ClassName;
import net.multiphasicapps.classfile.ConstantValueString;
import net.multiphasicapps.classfile.MethodDescriptor;
import net.multiphasicapps.classfile.MethodNameAndType;

/**
 * This class contains the instance of the SpringCoat virtual machine and has
 * a classpath along with all the needed storage for variables and such.
 *
 * @since 2018/07/29
 */
@SuppressWarnings("OverlyCoupledClass")
public final class SpringMachine
	implements Runnable, VirtualMachine
{
	/** Exit code indicating bad task things. */
	public static final int EXIT_CODE_FATAL_EXCEPTION =
		123;
	
	/** The class which contains the thread starting point. */
	private static final ClassName _START_CLASS =
		new ClassName("java/lang/__Start__");
	
	/** The method to enter for main threads. */
	private static final MethodNameAndType _MAIN_THREAD_METHOD =
		new MethodNameAndType("__main", "()V");
	
	/** The thread class. */
	private static final ClassName _THREAD_CLASS =
		new ClassName("java/lang/Thread");
	
	/** The new thread instance. */
	private static final MethodDescriptor _THREAD_NEW =
		new MethodDescriptor("(Ljava/lang/String;)V");
	
	/** The next virtual machine ID. */
	private static volatile int _nextVmNumber =
		1; 
	
	/** The class loader. */
	protected final SpringClassLoader classloader;
	
	/** Resources accessor. */
	protected final VMResourceAccess resourceaccessor;
	
	/** The boot class. */
	protected final String bootClass;
	
	/** The manager for suites. */
	protected final VMSuiteManager suites;
	
	/** Task manager. */
	protected final SpringTaskManager tasks;
	
	/** The global VM state. */
	protected final GlobalState globalState;
	
	/** The profiling information. */
	protected final ProfilerSnapshot profiler;
	
	/** The terminal pipe manager. */
	protected final TerminalPipeManager terminalPipes;
	
	/** The virtual machine identifier. */
	protected final String vmId;
	
	/** Is this the root virtual machine? */
	protected final boolean rootVm;
	
	/** State for the callback threader. */
	private final CallbackThreader _cbThreader =
		new CallbackThreader();
	
	/** Threads which are available. */
	private final List<SpringThread> _threads =
		new ArrayList<>();
	
	/** Global strings representing singular constants. */
	private final Map<ConstantValueString, SpringObject> _strings =
		new HashMap<>();
	
	/** Class objects which represent a given class. */
	private final Map<ClassName, SpringObject> _classobjects =
		new HashMap<>();
	
	/** Class names by their objects. */
	private final Map<SpringObject, ClassName> _namesbyclass =
		new HashMap<>();
	
	/** Main entry point arguments. */
	private final String[] _args;
	
	/** System properties. */
	final Map<String, String> _sysproperties;
	
	/** Callback threads that are available for use. */
	private final Collection<CallbackThread> _cbThreads =
		new LinkedList<>();
	
	/** The next thread ID to use. */
	private volatile int _nextthreadid;
	
	/** Is the VM exiting? */
	private volatile boolean _exiting;
	
	/** Exit code of the VM. */
	private int _exitcode;
	
	/** The stored call trace. */
	private CallTraceStorage _storedTrace;
	
	/**
	 * Initializes the virtual machine.
	 *
	 * @param __sm The manager for suites.
	 * @param __cl The class loader.
	 * @param __tm Task manager.
	 * @param __bootcl The boot class.
	 * @param __profiler The profiler to use.
	 * @param __sprops System properties.
	 * @param __gs Global system state.
	 * @param __pipes The terminal pipe manager, may be {@code null} in which
	 * case it is initialized for the caller.
	 * @param __rootVm Is this the root virtual machine?
	 * @param __args Main entry point arguments.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/09/03
	 */
	public SpringMachine(VMSuiteManager __sm, SpringClassLoader __cl,
		SpringTaskManager __tm, String __bootcl, ProfilerSnapshot __profiler,
		Map<String, String> __sprops, GlobalState __gs,
		TerminalPipeManager __pipes, boolean __rootVm, String... __args)
		throws NullPointerException
	{
		if (__cl == null || __sm == null || __pipes == null)
			throw new NullPointerException("NARG");
		
		// Bind to this class class loader
		__cl.__bind(this);
		
		this.rootVm = __rootVm;
		this.suites = __sm;
		this.classloader = __cl;
		this.tasks = __tm;
		this.bootClass = __bootcl;
		this.globalState = __gs;
		this.terminalPipes = __pipes;
		this._args = (__args == null ? new String[0] : __args.clone());
		this.profiler = (__profiler != null ? __profiler :
			new ProfilerSnapshot());
		this._sysproperties = (__sprops == null ?
			new HashMap<String, String>() : new HashMap<>(__sprops));
		
		// Setup resource accessor
		this.resourceaccessor = new VMResourceAccess(__sm);
		
		// Determine an ID for the VM, used for profiler information
		synchronized (SpringMachine.class)
		{
			this.vmId = String.format("%s#%d@%08x",
				__cl.bootLibrary().name(),
				SpringMachine._nextVmNumber++,
				System.identityHashCode(this));
		}
	}
	
	/**
	 * Returns the class loader.
	 *
	 * @return The class loader.
	 * @since 2018/09/08
	 */
	public final SpringClassLoader classLoader()
	{
		return this.classloader;
	}
	
	/**
	 * Creates a new thread within the virtual machine.
	 *
	 * @param __n The name of the thread, may be {@code null} in which case
	 * the thread will just get an ID number.
	 * @param __main Is this a main thread?
	 * @return The newly created thread.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/09/01
	 */
	public final SpringThread createThread(String __n, boolean __main)
	{
		// Store thread
		List<SpringThread> threads = this._threads;
		synchronized (this)
		{
			// The name of the thread to use
			String usedName = (__n != null ? __n :
				"hwThread-" + this.numThreads());
			
			// Initialize new thread
			int v;
			SpringThread rv = new SpringThread(
				new WeakReference<>(this),
				(v = ++this._nextthreadid),
				this.tasks.nextThreadId(),
				__main,
				usedName,
				this.profiler.measureThread(String.format("VM_%s-%d-%s",
				this.vmId, v, usedName)));
			
			// Signal that a major state has changed
			this.notifyAll();
			
			// Store thread
			threads.add(rv);
			return rv;
		}
	}
	
	/**
	 * Exits the virtual machine.
	 *
	 * @param __code The exit code.
	 * @throws SpringMachineExitException To signal virtual machine exit.
	 * @since 2018/10/13
	 */
	public final void exit(int __code)
		throws SpringMachineExitException
	{
		// Should do the same thing
		this.exitNoException(__code);
		
		// Now signal exit
		throw new SpringMachineExitException(__code);
	}
	
	/**
	 * Checks whether the virtual machine is exiting.
	 *
	 * @throws SpringMachineExitException If the VM is exiting.
	 * @since 2018/10/13
	 */
	public final void exitCheck()
		throws SpringMachineExitException
	{
		synchronized (this)
		{
			// Only if exiting
			if (this._exiting)
				throw new SpringMachineExitException(this._exitcode);
		}
	}
	
	/**
	 * Exits the virtual machine without throwing an exception.
	 *
	 * @param __code The exit code.
	 * @since 2018/10/13
	 */
	public final void exitNoException(int __code)
		throws SpringMachineExitException
	{
		synchronized (this)
		{
			// Set as exiting
			this._exitcode = __code;
			this._exiting = true;
			
			// Close all the pipes on exit because otherwise any calling tasks
			// will never be able to finish reading these pipes ever until
			// the process completes
			try
			{
				this.terminalPipes.closeAll();
			}
			catch (IOException e)
			{
				throw new SpringVirtualMachineException(
					"Could not close pipes.", e);
			}
		}
	}
	
	/**
	 * Returns the current exit code.
	 * 
	 * @return The current exit code.
	 * @since 2020/07/08
	 */
	public int getExitCode()
	{
		synchronized (this)
		{
			return this._exitcode;
		}
	}
	
	/**
	 * Returns the main arguments.
	 *
	 * @return The main arguments.
	 * @since 2020/06/17
	 */
	public final String[] getMainArguments()
	{
		return this._args.clone();
	}
	
	/**
	 * Gets the thread by the given ID.
	 *
	 * @param __id The ID of the thread.
	 * @return The thread by this ID.
	 * @throws NoSuchElementException If there is no thread that exists by
	 * that ID.
	 * @since 2018/11/21
	 */
	public final SpringThread getThread(int __id)
		throws NoSuchElementException
	{
		synchronized (this)
		{
			for (SpringThread t : this.getThreads())
				if (t.id == __id)
					return t;
		}
		
		throw new NoSuchElementException("No such thread ID: " + __id);
	}
	
	/**
	 * Returns all of the process threads.
	 *
	 * @return All of the current process threads.
	 * @since 2020/06/17
	 */
	public final SpringThread[] getThreads()
	{
		List<SpringThread> rv = new ArrayList<>();
		
		// Go through threads but also cleanup any that have ended
		List<SpringThread> threads = this._threads;
		synchronized (this)
		{
			for (Iterator<SpringThread> it = threads.iterator(); it.hasNext();)
			{
				SpringThread thread = it.next();
				
				// If the thread is terminating, clean it up
				if (thread.isTerminated())
				{
					// Remove it
					it.remove();
					
					// Signal that a state of a thread has changed
					this.notifyAll();
				}
				
				// Otherwise add it
				else
					rv.add(thread);
			}
			
			// If there are no threads left and we are not yet exiting, exit
			// this task so it finalizes and cleans accordingly
			if (!this.isExiting() && threads.isEmpty())
				this.exitNoException(this.getExitCode());
		}
		
		// Use whatever was found
		return rv.<SpringThread>toArray(new SpringThread[rv.size()]);
	}
	
	/**
	 * Returns the stack trace.
	 * 
	 * @return The trace.
	 * @since 2020/07/08
	 */
	public final CallTraceStorage getTrace()
	{
		synchronized (this)
		{
			return this._storedTrace;
		}
	}
	
	/**
	 * Returns if this task has exited.
	 * 
	 * @return If this task is exited.
	 * @since 2020/07/08
	 */
	public final boolean isExiting()
	{
		synchronized (this)
		{
			return this._exiting;
		}
	}
	
	/**
	 * Returns the number of threads which are currently alive and active.
	 *
	 * @return The number of active and alive threads.
	 * @since 2018/09/03
	 */
	public final int numThreads()
	{
		// Store thread
		List<SpringThread> threads = this._threads;
		synchronized (this)
		{
			return threads.size();
		}
	}
	
	/**
	 * Obtains a temporary callback thread.
	 * 
	 * @return The thread to to be used.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/09/15
	 */
	public final CallbackThread obtainCallbackThread()
		throws NullPointerException
	{
		CallbackThread rv = null;
		
		// This could deadlock on thread initialization, especially if this
		// gets suspended by a debugger!
		Collection<CallbackThread> cbThreads = this._cbThreads;
		synchronized (this)
		{
			// Find the thread that can be opened
			for (CallbackThread thread : cbThreads)
				if (thread.canOpen())
				{
					rv = thread;
					break;
				}
			
			// Use this thread
			if (rv != null)
			{
				rv.open();
				return rv;
			}
		}
			
		// Setup new thread and its worker
		String name = "callback#" + cbThreads.size();
		SpringThread thread = this.createThread(name, false);
		SpringThreadWorker worker = new SpringThreadWorker(this,
			thread, false);
		
		// This always is a daemon thread
		thread.setDaemon();
		
		// Enter blank thread so it is always at the ready
		thread.enterBlankFrame();
		
		// Allocate thread object instance, this will get a VM thread
		// created for it in the constructor and otherwise
		SpringObject jvmThread = worker.newInstance(
			SpringMachine._THREAD_CLASS, SpringMachine._THREAD_NEW,
			worker.asVMObject(name));
		thread.setThreadInstance(jvmThread);
		
		// Register this callback thread since it was initialized
		synchronized (this)
		{
			// Register this
			rv = new CallbackThread(thread);
			cbThreads.add(rv);
			
			// Open and use it
			rv.open();
			return rv;
		}
	}
	
	/**
	 * Returns the access for resources.
	 *
	 * @return The resource access.
	 * @since 2018/10/07
	 */
	public final VMResourceAccess resourceAccess()
	{
		return this.resourceaccessor;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/09/13
	 */
	@Override
	public final void run()
	{
		// Thread that will be used as the main thread of execution, also used
		// to initialize classes when they are requested
		SpringThread mainThread = this.createThread("main", true);
		
		// We will be using the same logic in the thread worker if we need to
		// initialize any objects or arguments
		SpringThreadWorker worker = new SpringThreadWorker(this,
			mainThread, true);
		mainThread._worker = worker;
		
		// Enter the main entry point which handles the thread logic
		mainThread.enterFrame(worker.loadClass(SpringMachine._START_CLASS)
			.lookupMethod(true, SpringMachine._MAIN_THREAD_METHOD));
		
		// Initialize an instance of Thread for this thread, as this is
		// very important, the call to create VM threads will bind the instance
		// object and the vm thread together.
		worker.newInstance(
			new ClassName("java/lang/Thread"),
			new MethodDescriptor("(Ljava/lang/String;)V"),
			worker.asVMObject("main"));
		
		// The main although it executes in this context will always have the
		// same exact logic as other threads running apart from this main
		// thread, so no code is needed to be duplicated at all.
		try
		{
			worker.run();
		}
		
		// Either failed or threw exit exception
		catch (RuntimeException e)
		{
			throw e;
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/11/17
	 */
	@Override
	public final int runVm()
	{
		// Run until the VM terminates
		try
		{
			this.run();
			
			// Debug
			Debugging.debugNote("VM exited normally: %d",
				this.getExitCode());
			
			// Success, maybe
			return this.getExitCode();
		}
		
		// Exit VM with given code
		catch (SpringMachineExitException e)
		{
			// Debug
			Debugging.debugNote("VM Exited via exception: %d", e.code());
			
			return e.code();
		}
		
		// Any other exception is fatal and the task must be made to exit
		// with the error code otherwise the VM will stick trying to wait
		// to exit
		catch (RuntimeException|Error e)
		{
			PrintStream err = System.err;
			
			err.println("****************************");
			
			// Print the real stack trace
			err.println("*** EXTERNAL STACK TRACE ***");
			e.printStackTrace(err);
			err.println();
			
			err.println("****************************");
			
			// Errors are rather bad so rethrow them
			if (e instanceof Error)
				throw (Error)e;
			
			return SpringMachine.EXIT_CODE_FATAL_EXCEPTION;
		}
	}
	
	/**
	 * Sets the current exit code.
	 * 
	 * @param __exitCode The exit code to set.
	 * @since 2020/06/27
	 */
	public final void setExitCode(int __exitCode)
	{
		synchronized (this)
		{
			this._exitcode = __exitCode;
		}
	}
	
	/**
	 * Signals that the given thread terminated.
	 * 
	 * @param __thread The thread that was terminated.
	 * @since 2020/06/29
	 */
	public void signalThreadTerminate(SpringThread __thread)
	{
		// The act of getting all threads will clear out terminated threads
		this.getThreads();
	}
	
	/**
	 * Stores the call trace for this given machine/task.
	 * 
	 * @param __message The message to use.
	 * @param __trace The trace to store.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/07/06
	 */
	public void storeTrace(String __message, CallTraceElement... __trace)
		throws NullPointerException
	{
		if (__message == null || __trace == null)
			throw new NullPointerException("NARG");
		
		synchronized (this)
		{
			this._storedTrace = new CallTraceStorage(__message, __trace); 
		}
	}
	
	/**
	 * Returns the suite manager which is available.
	 *
	 * @return The suite manager that is available.
	 * @since 2018/10/26
	 */
	public final VMSuiteManager suiteManager()
	{
		return this.suites;
	}
	
	/**
	 * Returns the task manager which is used.
	 *
	 * @return The task manager.
	 * @since 2018/11/04
	 */
	public final SpringTaskManager taskManager()
	{
		return this.tasks;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/03/13
	 */
	@Override
	public final String toString()
	{
		return this.vmId;
	}
	
	/**
	 * Splits long to integers.
	 *
	 * @param __dx The index.
	 * @param __v The output integers.
	 * @param __l The input long.
	 * @since 2018/09/29
	 */
	public static final void longToInt(int __dx, int[] __v, long __l)
	{
		__v[__dx] = (int)(__l >>> 32);
		__v[__dx + 1] = (int)__l;
	}
	
	/**
	 * Returns the mapping of class names to {@link Class} instances.
	 *
	 * @return The mapping of class names to object instances.
	 * @since 2018/09/19
	 */
	final Map<ClassName, SpringObject> __classObjectMap()
	{
		return this._classobjects;
	}
	
	/**
	 * Returns the mapping for objects to class names.
	 *
	 * @return The mapping of class objects to names.
	 * @since 2018/09/29
	 */
	final Map<SpringObject, ClassName> __classObjectToNameMap()
	{
		return this._namesbyclass;
	}
	
	/**
	 * Returns the global string map.
	 *
	 * @return The global string map.
	 * @since 2018/09/16
	 */
	final Map<ConstantValueString, SpringObject> __stringMap()
	{
		return this._strings;
	}
}

