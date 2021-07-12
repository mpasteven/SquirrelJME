// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.springcoat;

import cc.squirreljme.emulator.profiler.ProfiledFrame;
import cc.squirreljme.jdwp.JDWPController;
import cc.squirreljme.jdwp.trips.JDWPGlobalTrip;
import cc.squirreljme.jdwp.trips.JDWPTripThread;
import cc.squirreljme.jdwp.trips.JDWPTripVmState;
import cc.squirreljme.jvm.mle.ThreadShelf;
import cc.squirreljme.jvm.mle.brackets.TracePointBracket;
import cc.squirreljme.jvm.mle.brackets.VMThreadBracket;
import cc.squirreljme.jvm.mle.constants.ThreadModelType;
import cc.squirreljme.jvm.mle.constants.ThreadStatusType;
import cc.squirreljme.runtime.cldc.debug.CallTraceElement;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.vm.springcoat.brackets.TaskObject;
import cc.squirreljme.vm.springcoat.brackets.VMThreadObject;
import cc.squirreljme.vm.springcoat.exceptions.SpringMLECallError;
import net.multiphasicapps.classfile.ClassName;
import net.multiphasicapps.classfile.MethodNameAndType;

/**
 * Functions for {@link ThreadShelf}.
 *
 * @since 2020/06/18
 */
public enum MLEThread
	implements MLEFunction
{
	/** {@link ThreadShelf#aliveThreadCount(boolean, boolean)}. */
	ALIVE_THREAD_COUNT("aliveThreadCount:(ZZ)I")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/18
		 */
		@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			boolean includeMain = ((int)__args[0] != 0);
			boolean includeDaemon = ((int)__args[1] != 0);
			
			// Count every thread
			int count = 0;
			SpringMachine machine = __thread.machine;
			synchronized (machine)
			{
				for (SpringThread thread : machine.getThreads())
				{
					// Ignore any threads that are marked terminated or has not
					// been started as it has no frames
					if (thread.isTerminated() || thread.numFrames() == 0)
						continue;
					
					boolean isMain = thread.isMain();
					boolean isDaemon = thread.isDaemon();
					
					if ((includeMain && isMain) ||
						(includeDaemon && isDaemon) ||
						(!isMain && !isDaemon))
						count++;
				}
			}
			
			return count;
		}
	},
	
	/** {@link ThreadShelf#createVMThread(Thread)}. */
	CREATE_VM_THREAD( "createVMThread:(Ljava/lang/Thread;)Lcc/" +
		"squirreljme/jvm/mle/brackets/VMThreadBracket;")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/18
		 */
		@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			SpringSimpleObject javaThread = MLEThread.__javaThread(__thread,
				__args[0]);
			
			// Find the thread which the given passed object is bound to, this
			// is the target thread
			SpringThread target = null;
			SpringMachine machine = __thread.machine;
			synchronized (machine)
			{
				// Search through every thread
				SpringThread[] threads = machine.getThreads();
				for (SpringThread thread : threads)
				{
					SpringObject instance;
					try
					{
						instance = thread.threadInstance();
					}
					catch (IllegalStateException ignored)
					{
						continue;
					}
					
					// If this is the thread for this, then use that!
					if (javaThread == instance)
					{
						target = thread;
						break;
					}
				}
				
				// If there is exactly one thread, we can rather get into a bit
				// of a loop where our main thread is created outside of normal
				// means by the VM and not by any other thread.. but only if
				// this initial thread has no actual instance
				if (threads.length == 1 && !threads[0].hasThreadInstance())
					target = threads[0];
				
				// No actual thread exists that the object is bound to, so
				// oops! We need to actually create one here and bind it
				// accordingly!
				if (target == null)
					target = machine.createThread(null, false);
			}
			
			// Create object with this attached thread
			VMThreadObject vmThread = new VMThreadObject(machine, target);
			
			// The thread gets these as well
			target.setThreadInstance(javaThread);
			target.setVMThread(vmThread);
			
			// If we are debugging, we are going to need to tell the debugger
			// some important details
			JDWPController jdwp = target.machineRef.get()
				.taskManager().jdwpController;
			if (jdwp != null)
			{
				// If we are debugging, we need to tell the debugger that the
				// virtual machine actually started
				if (target.machine().rootVm && target.isMain())
					jdwp.<JDWPTripVmState>trip(JDWPTripVmState.class,
						JDWPGlobalTrip.VM_STATE).alive(target, true);
				
				// If we are debugging, signal that this thread is in the start
				// state. We need the instance to have been set for this to
				// even properly work!
				jdwp.<JDWPTripThread>trip(JDWPTripThread.class,
					JDWPGlobalTrip.THREAD).alive(target, true);
			}
			
			return vmThread;
		}
	},
	
	/** {@link ThreadShelf#currentExitCode()}. */
	CURRENT_EXIT_CODE("currentExitCode:()I")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/18
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			return __thread.machine.getExitCode();
		}
	},
	
	/** {@link ThreadShelf#currentJavaThread()}. */
	CURRENT_JAVA_THREAD("currentJavaThread:()Ljava/lang/Thread;")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/18
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			return __thread.thread.threadInstance();
		}
	},
	
	/** {@link ThreadShelf#currentVMThread()}. */
	CURRENT_VM_THREAD("currentVMThread:" +
		"()Lcc/squirreljme/jvm/mle/brackets/VMThreadBracket;")
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/05/08
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			return new VMThreadObject(__thread.machine, __thread.thread);
		}
	},
	
	/** {@link ThreadShelf#equals(VMThreadBracket, VMThreadBracket)}. */
	EQUALS("equals:(Lcc/squirreljme/jvm/mle/brackets/VMThreadBracket;" +
		"Lcc/squirreljme/jvm/mle/brackets/VMThreadBracket;)Z")
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/05/08
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			return MLEThread.__vmThread(__args[0]).getThread() ==
				MLEThread.__vmThread(__args[1]).getThread();
		}
	}, 
	
	/** {@link ThreadShelf#javaThreadClearInterrupt(Thread)}. */
	JAVA_THREAD_CLEAR_INTERRUPT("javaThreadClearInterrupt:" +
		"(Ljava/lang/Thread;)Z")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/28
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			SpringFieldStorage field = MLEThread.__javaThread(__thread,
				__args[0]).fieldByNameAndType(false, 
				"_interrupted", "Z");
			
			// Get and clear the field value
			Object old = field.get();
			field.set(false);
			return old;
		}
	}, 
	
	/** {@link ThreadShelf#javaThreadFlagStarted(Thread)}. */
	JAVA_THREAD_FLAG_STARTED("javaThreadFlagStarted:(Ljava/lang/" +
		"Thread;)V")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/18
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			// Just set the started field to true
			MLEThread.__javaThread(__thread, __args[0]).fieldByNameAndType(
				false, "_started", "Z").set(true);
			return null;
		}
	},
	
	/** {@link ThreadShelf#javaThreadIsStarted(Thread)}. */
	JAVA_THREAD_IS_STARTED("javaThreadIsStarted:(Ljava/lang/Thread;)Z")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/18
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			// Just get the state of the given field
			return MLEThread.__javaThread(__thread, __args[0])
				.fieldByNameAndType(false, 
					"_started", "Z").get();
		}
	},
	
	/** {@link ThreadShelf#javaThreadRunnable(Thread)}. */
	JAVA_THREAD_RUNNABLE("javaThreadRunnable:(Ljava/lang/Thread;)" +
		"Ljava/lang/Runnable;")
	{
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			// Just get the state of the given field
			return MLEThread.__javaThread(__thread, __args[0])
				.fieldByNameAndType(
				false, "_runnable",
				"Ljava/lang/Runnable;").get();
		}
	},
	
	/** {@link ThreadShelf#javaThreadSetAlive(Thread, boolean)}. */
	JAVA_THREAD_SET_ALIVE("javaThreadSetAlive:(Ljava/lang/Thread;Z)V")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/18
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			// Just set the started field to true
			MLEThread.__javaThread(__thread, __args[0]).fieldByNameAndType(
				false, "_isAlive", "Z")
				.set((int)__args[1] != 0);
			
			return null;
		}
	},
	
	/** {@link ThreadShelf#javaThreadSetDaemon(Thread)}. */ 
	JAVA_THREAD_SET_DAEMON("javaThreadSetDaemon:(Ljava/lang/Thread;)V")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/09/12
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			SpringThread vmThread = MLEThread.__vmThread(
				MLEThread.TO_VM_THREAD.handle(__thread, __args[0]))
				.getThread();
			
			synchronized (vmThread)
			{
				// Cannot be changed once started
				if (vmThread.isTerminated() || vmThread.numFrames() > 0)
					throw new SpringMLECallError("Thread is started.");
				
				// Set as a daemon thread
				vmThread.setDaemon();
			}
			
			// No value is returned
			return null;
		}
	},
	
	/** {@link ThreadShelf#model()}. */ 
	MODEL("model:()I")
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/05/07
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			// SpringCoat is always multi-threaded
			return ThreadModelType.SIMULTANEOUS_MULTI_THREAD;
		}
	},
	
	/** {@link ThreadShelf#runProcessMain()}. */
	RUN_PROCESS_MAIN("runProcessMain:()V")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/18
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			__thread.runProcessMain();
			return null;
		}
	},
	
	/** {@link ThreadShelf#setCurrentExitCode(int)}. */
	SET_CURRENT_EXIT_CODE("setCurrentExitCode:(I)V")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/27
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			int exitCode = (int)__args[0];
			
			__thread.machine.setExitCode(exitCode);
			
			return null;
		}
	},
	
	/** {@link ThreadShelf#setTrace(String, TracePointBracket[])}. */ 
	SET_TRACE("setTrace:(Ljava/lang/String;[Lcc/squirreljme/" +
		"jvm/mle/brackets/TracePointBracket;)V")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/07/06
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			if (!(__args[1] instanceof SpringArrayObjectGeneric))
				throw new SpringMLECallError("Wrong trace array type.");
			SpringObject[] gen = ((SpringArrayObjectGeneric)__args[1]).array();
			
			// Get the message used
			String message = __thread.<String>asNativeObject(String.class,
				__args[0]);
			if (message == null)
				throw new SpringMLECallError("No message set.");
			
			// Map trace points to the call trace for future get
			int n = gen.length;
			CallTraceElement[] trace = new CallTraceElement[n];
			for (int i = 0; i < n; i++)
				trace[i] = MLEDebug.__trace(gen[i]).getTrace();
			
			// Store the call trace for other tasks to get
			__thread.machine.storeTrace(message, trace);
			return null;
		}
	},
	
	/** {@link ThreadShelf#sleep(int, int)}. */
	SLEEP("sleep:(II)Z")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/18
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			int ms = (int)__args[0];
			int ns = (int)__args[1];
			
			if (ms < 0 || ns < 0 || ns > 1000000000)
				throw new SpringMLECallError("Out of range time.");
			
			// Get the profiler information
			SpringThread.Frame currentFrame = __thread.thread.currentFrame();
			ProfiledFrame profiler = (currentFrame == null ? null :
				currentFrame._profiler);
			
			// We need to restore profiler states
			boolean interrupted = false;
			try
			{
				// Indicate that we are in sleep mode
				__thread.thread.setStatus(ThreadStatusType.SLEEPING);
				
				// Stop counting CPU time for this
				if (profiler != null)
					profiler.sleep(true, System.nanoTime());
				
				// Just giving up CPU time?
				if (ms == 0 && ns == 0)
					Thread.yield();
				
				// Normal sleep
				else
					try
					{
						Thread.sleep(ms, ns);
					}
					catch (InterruptedException ignored)
					{
						interrupted = true;
					}
			}
			finally
			{
				// We have left sleep mode
				__thread.thread.setStatus(ThreadStatusType.RUNNING);
				
				// Continue counting CPU time
				if (profiler != null)
					profiler.sleep(false, System.nanoTime());
			}
			
			return interrupted;
		}
	},
	
	/** {@link ThreadShelf#toJavaThread(VMThreadBracket)}. */
	TO_JAVA_THREAD("toJavaThread:(Lcc/squirreljme/jvm/mle/" +
		"brackets/VMThreadBracket;)Ljava/lang/Thread;")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/29
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			VMThreadObject vmThread = MLEThread.__vmThread(__args[0]);
			
			return vmThread.getThread().threadInstance();
		}
	}, 
	
	/** {@link ThreadShelf#toVMThread(Thread)}. */
	TO_VM_THREAD("toVMThread:(Ljava/lang/Thread;)Lcc/squirreljme/" +
		"jvm/mle/brackets/VMThreadBracket;")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/18
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			return MLEThread.__javaThread(__thread, __args[0]).fieldByField(
				__thread.resolveClass("java/lang/Thread")
				.lookupField(false, "_vmThread",
				"Lcc/squirreljme/jvm/mle/brackets/VMThreadBracket;"))
				.get();
		}
	},
	
	/** {@link ThreadShelf#vmThreadEnd(VMThreadBracket)}. */
	VM_THREAD_END("vmThreadEnd:(Lcc/squirreljme/jvm/mle/brackets/" +
		"VMThreadBracket;)V")
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/03/14
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			SpringThread thread = MLEThread.__vmThread(__args[0]).getThread();
			
			// If debugging, signal that the thread has ended
			JDWPController jdwp = thread.machineRef.get()
				.taskManager().jdwpController;
			if (jdwp != null)
				jdwp.<JDWPTripThread>trip(JDWPTripThread.class,
					JDWPGlobalTrip.THREAD).alive(thread, true);
			
			return null;
		}
	},
	
	/** {@link ThreadShelf#vmThreadId(VMThreadBracket)}. */
	VM_THREAD_ID("vmThreadId:(Lcc/squirreljme/jvm/mle/brackets/" +
		"VMThreadBracket;)I")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/18
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			return MLEThread.__vmThread(__args[0]).getThread().id;
		}
	},
	
	/** {@link ThreadShelf#vmThreadInterrupt(VMThreadBracket)}. */ 
	VM_THREAD_INTERRUPT("vmThreadInterrupt:(Lcc/squirreljme/jvm/mle/" +
		"brackets/VMThreadBracket;)V")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/22
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			VMThreadObject vmThread = MLEThread.__vmThread(__args[0]);
			
			// Send an interrupt to the thread
			vmThread.getThread().hardInterrupt();
			
			return null;
		}
	},
	
	/** {@link ThreadShelf#vmThreadIsMain(VMThreadBracket)}. */
	VM_THREAD_IS_MAIN("vmThreadIsMain:(Lcc/squirreljme/jvm/mle/" +
		"brackets/VMThreadBracket;)Z")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/18
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			return MLEThread.__vmThread(__args[0]).getThread().isMain();
		}
	},
	
	/** {@link ThreadShelf#vmThreadSetPriority(VMThreadBracket, int)}. */
	VM_THREAD_SET_PRIORITY("vmThreadSetPriority:(Lcc/squirreljme/" +
		"jvm/mle/brackets/VMThreadBracket;I)V")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/29
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			SpringThread thread = MLEThread.__vmThread(__args[0]).getThread();
			int priority = (int)__args[1];
			
			if (priority < Thread.MIN_PRIORITY ||
				priority > Thread.MAX_PRIORITY)
				throw new SpringMLECallError(
					"Thread priority out of range.");
			
			// Try to set the priority
			try
			{
				thread._worker.setPriority(priority);
			}
			catch (IllegalArgumentException|SecurityException e)
			{
				throw new SpringMLECallError(
					"Could not set priority.", e);
			}
			
			return null;
		}
	}, 
	
	/** {@link ThreadShelf#vmThreadStart(VMThreadBracket)}. */
	VM_THREAD_START("vmThreadStart:(Lcc/squirreljme/jvm/mle/brackets/" +
		"VMThreadBracket;)Z")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/18
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			SpringThread target = MLEThread.__vmThread(__args[0]).getThread();
			
			// Create worker for thread and start it
			SpringThreadWorker worker = new SpringThreadWorker(
				__thread.machine, target, false);
			
			// Enter the base setup frame
			target.enterFrame(worker.loadClass(MLEThread._START_CLASS)
				.lookupMethod(true, MLEThread._BASE_THREAD_METHOD));
			
			// Try to start it
			try
			{
				worker.start();
				return true;
			}
			catch (IllegalThreadStateException ignored)
			{
				return false;
			}
		}
	},
	
	/** {@link ThreadShelf#vmThreadTask(VMThreadBracket)}. */
	VM_THREAD_TASK("vmThreadTask:(Lcc/squirreljme/jvm/mle/brackets/" +
		"VMThreadBracket;)Lcc/squirreljme/jvm/mle/brackets/TaskBracket;")
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/05/08
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			return new TaskObject(MLEThread.__vmThread(__args[0]).getThread()
				.machine());
		}
	}, 
	
	/** {@link ThreadShelf#waitForUpdate(int)}. */
	WAIT_FOR_UPDATE("waitForUpdate:(I)Z")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/29
		 */
		@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			int ms = (int)__args[0];
			
			if (ms < 0)
				throw new SpringMLECallError("Negative milliseconds");
			
			// Waiting for nothing? just give up our slice
			if (ms == 0)
			{
				Thread.yield();
				return false;
			}
			
			// Wait until the monitor is hit
			SpringMachine machine = __thread.machine;
			synchronized (machine)
			{
				try
				{
					machine.wait(ms);
				}
				catch (InterruptedException e)
				{
					return true;
				}
			}
			
			// Assume not interrupted
			return false;
		}
	}, 
	
	/* End. */
	;
	
	/** The class which contains the thread starting point. */
	static final ClassName _START_CLASS =
		new ClassName("java/lang/__Start__");
	
	/** The method to enter for main threads. */
	static final MethodNameAndType _BASE_THREAD_METHOD =
		new MethodNameAndType("__base", "()V");
	
	/** The dispatch key. */
	protected final String key;
	
	/**
	 * Initializes the dispatcher info.
	 *
	 * @param __key The key.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/06/18
	 */
	MLEThread(String __key)
		throws NullPointerException
	{
		if (__key == null)
			throw new NullPointerException("NARG");
		
		this.key = __key;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/06/18
	 */
	@Override
	public String key()
	{
		return this.key;
	}
	
	/**
	 * Checks if this is a Java thread.
	 * 
	 * @param __thread The context thread.
	 * @param __object The object to check.
	 * @return The verified object.
	 * @throws SpringMLECallError If {@code __object} is {@code null} or is
	 * not an instance of {@link Throwable}.
	 * @since 2020/06/28
	 */
	static SpringSimpleObject __javaThread(SpringThreadWorker __thread,
		Object __object)
		throws SpringMLECallError
	{
		if (__thread == null)
			throw new NullPointerException("NARG");
		
		if (!(__object instanceof SpringSimpleObject))
			throw new SpringMLECallError("Not a Java Thread");
		
		SpringSimpleObject rv = (SpringSimpleObject)__object;
		if (!__thread.resolveClass("java/lang/Thread")
			.isAssignableFrom(rv.type()))
			throw new SpringMLECallError("Not instance of Thread.");
		
		return rv;
	}
	
	/**
	 * Ensures that this is a {@link VMThreadObject}.
	 * 
	 * @param __object The object to check.
	 * @return As a {@link VMThreadObject}.
	 * @throws SpringMLECallError If this is not one.
	 * @since 2020/06/27
	 */
	static VMThreadObject __vmThread(Object __object)
		throws SpringMLECallError
	{
		if (!(__object instanceof VMThreadObject))
			throw new SpringMLECallError("Not a VMThreadObject.");
		
		return (VMThreadObject)__object; 
	}
}
