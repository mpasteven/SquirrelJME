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

import cc.squirreljme.emulator.profiler.ProfiledFrame;
import cc.squirreljme.emulator.profiler.ProfiledThread;
import cc.squirreljme.jdwp.JDWPStepTracker;
import cc.squirreljme.jdwp.JDWPThreadSuspension;
import cc.squirreljme.jvm.mle.constants.ThreadStatusType;
import cc.squirreljme.runtime.cldc.debug.CallTraceElement;
import cc.squirreljme.runtime.cldc.debug.CallTraceUtils;
import cc.squirreljme.vm.springcoat.brackets.VMThreadObject;
import cc.squirreljme.vm.springcoat.exceptions.SpringNullPointerException;
import cc.squirreljme.vm.springcoat.exceptions.SpringVirtualMachineException;
import java.io.PrintStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.multiphasicapps.classfile.ByteCode;
import net.multiphasicapps.classfile.ClassName;
import net.multiphasicapps.classfile.MethodNameAndType;

/**
 * This class contains information about a thread within the virtual machine.
 *
 * @since 2018/09/01
 */
public final class SpringThread
{
	/** Maximum depth of the stack. */
	public static final int MAX_STACK_DEPTH =
		64;
	
	/** The thread ID. */
	protected final int id;
	
	/** Is this a main thread? */
	protected final boolean main;
	
	/** The name of this thread. */
	protected final String name;
	
	/** Profiler information. */
	protected final ProfiledThread profiler;
	
	/** Tracker for debugging suspension. */
	protected final JDWPThreadSuspension debuggerSuspension =
		new JDWPThreadSuspension();
	
	/** The virtual machine reference. */
	protected final Reference<SpringMachine> machineRef;
	
	/** Unique thread ID. */
	protected final int uniqueId;
	
	/** The stack frames. */
	private final List<SpringThread.Frame> _frames =
		new ArrayList<>();
	
	/** The thread status. */
	int _status;
	
	/** The thread's {@link Thread} instance. */
	private SpringObject _threadInstance;
	
	/** The thread's VM Thread instance. */
	private VMThreadObject _vmThread;
		
	/** String representation. */
	private Reference<String> _string;
	
	/** Ran at least one frame (was started)? */
	private volatile boolean _hadoneframe;
	
	/** Is this a daemon thread? */
	volatile boolean _daemon;
	
	/** Did we signal exit? */
	volatile boolean _signaledexit;
	
	/** The current worker for the thread. */
	volatile SpringThreadWorker _worker;
	
	/** Terminate the thread? */
	private volatile boolean _terminate;
	
	/** Step tracker. */
	volatile JDWPStepTracker _stepTracker;
	
	/**
	 * Initializes the thread.
	 *
	 * @param __machRef The machine reference.
	 * @param __id The thread ID.
	 * @param __uniqueId Unique ID for debugging.
	 * @param __main Is this a main thread.
	 * @param __n The name of the thread.
	 * @param __profiler Profiled storage.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/09/01
	 */
	SpringThread(Reference<SpringMachine> __machRef, int __id, int __uniqueId,
		boolean __main, String __n, ProfiledThread __profiler)
		throws NullPointerException
	{
		if (__n == null)
			throw new NullPointerException("NARG");
		
		this.machineRef = __machRef;
		this.id = __id;
		this.uniqueId = __uniqueId;
		this.main = __main;
		this.name = __n;
		this.profiler = __profiler;
	}
	
	/**
	 * Returns the current frame of execution or {@code null} if there is none.
	 *
	 * @return The current frame of execution or {@code null} if there is none.
	 * @since 2018/09/03
	 */
	public final SpringThread.Frame currentFrame()
	{
		List<SpringThread.Frame> frames = this._frames;
		synchronized (this)
		{
			if (frames.isEmpty())
				return null;
			return frames.get(frames.size() - 1);
		}
	}
	
	/**
	 * Enters a blank frame to store data.
	 *
	 * @return The newly created frame.
	 * @since 2018/09/20
	 */
	public final SpringThread.Frame enterBlankFrame()
	{
		// Cannot enter frames when terminated
		if (this.isTerminated())
			throw new SpringVirtualMachineException(
				"Cannot enter frame on terminated thread.");
		
		// Setup blank frame
		List<SpringThread.Frame> frames = this._frames;
		SpringThread.Frame rv = new SpringThread.Frame(frames.size());
		
		// {@squirreljme.error BK1j Stack overflow.}
		if (frames.size() >= SpringThread.MAX_STACK_DEPTH)
			throw new SpringVirtualMachineException("BK1j");
		
		// Profile for this frame
		/*rv._profiler = this.profiler.enterFrame(
			"<blank>", "<blank>", "()V",
			System.nanoTime());*/
		
		// Lock on frames as a new one is added
		synchronized (this)
		{
			frames.add(rv);
		}
		
		// Had one frame (started)
		this._hadoneframe = true;
		
		return rv;
	}
	
	/**
	 * Enters the specified method and sets up a stack frame for it.
	 *
	 * @param __m The method to enter.
	 * @param __args Arguments to the frame entry (method arguments).
	 * @return The used stack frame.
	 * @throws NullPointerException On null arguments.
	 * @throws SpringVirtualMachineException If the method is abstract.
	 * @since 2018/09/03
	 */
	public final SpringThread.Frame enterFrame(SpringMethod __m,
		Object... __args)
		throws NullPointerException, SpringVirtualMachineException
	{
		if (__m == null)
			throw new NullPointerException("NARG");
		
		// Cannot enter frames when terminated
		if (this.isTerminated())
			throw new SpringVirtualMachineException(
				"Cannot enter frame on terminated thread.");
		
		if (__args == null)
			__args = new Object[0];
		
		// Debug
		/*Debugging.debugNote("enterFrame(%s::%s, %s)", __m.inClass(),
			__m.nameAndType(), Arrays.<Object>asList(__args));*/
		
		// {@squirreljme.error BK1k Cannot enter the frame for a method which
		// is abstract. (The class the method is in; The method name and type)}
		if (__m.isAbstract())
			throw new SpringVirtualMachineException(String.format("BK1k %s %s",
				__m.inClass(), __m.nameAndType()));
				
		SpringThreadWorker worker = this._worker;
		
		// Convert all of the object to virtual machine objects if they are
		// not already
		Object[] vmArgs = Arrays.copyOf(__args, __args.length);
		if (worker != null)
			for (int i = 0, n = __args.length; i < n; i++)
				vmArgs[i] = worker.asVMObject(vmArgs[i], true);
		
		// Create new frame
		List<SpringThread.Frame> frames = this._frames;
		Frame rv = new Frame(frames.size(),
			this._worker.loadClass(__m.inClass()), __m, vmArgs);
		
		// Profile for this frame
		rv._profiler = this.profiler.enterFrame(__m.inClass().toString(),
			__m.nameAndType().name().toString(),
			__m.nameAndType().type().toString(), System.nanoTime());
		
		// Lock on frames as a new one is added
		synchronized (this)
		{
			frames.add(rv);
		}
		
		// Had one frame (started)
		this._hadoneframe = true;
		
		// Handle synchronized method
		if (__m.flags().isSynchronized())
		{
			SpringObject monitor;
			
			// Monitor on the class object, needs the worker since we need to
			// load a class
			if (__m.flags().isStatic())
			{
				// {@squirreljme.error BK1l Cannot enter a synchronized static
				// method without a thread working, since we need to load
				// the class object.}
				if (worker == null)
					throw new SpringVirtualMachineException("BK1l");
				
				// Use the class object
				monitor = (SpringObject)worker.asVMObject(
					__m.inClass(), true);
			}
			
			// On this object
			else
			{
				// {@squirreljme.error BK1m Cannot enter a synchronized
				// instance method with no arguments passed.}
				if (__args.length <= 0)
					throw new SpringVirtualMachineException("BK1m");
				
				// {@squirreljme.error BK1n Cannot enter a monitor of nothing
				// or a non-object.}
				Object argzero = __args[0];
				if (!(argzero instanceof SpringObject))
					throw new SpringVirtualMachineException("BK1n");
				
				// Use this as the monitor
				monitor = (SpringObject)argzero;
			}
			
			// Set to unlock later on
			rv._monitor = monitor;
			
			// Enter the monitor and just wait around
			monitor.monitor().enter(this);
		}
		
		return rv;
	}
	
	/**
	 * Exits all frames in the stack.
	 *
	 * @since 2018/11/17
	 */
	public final void exitAllFrames()
	{
		// Lock on frames as a new one is added
		List<SpringThread.Frame> frames = this._frames;
		synchronized (this)
		{
			frames.clear();
		}
	}
	
	/**
	 * Returns all of the frames which are available.
	 *
	 * @return All of the available stack frames.
	 * @since 2018/09/16
	 */
	public final SpringThread.Frame[] frames()
	{
		// Lock on frames
		List<SpringThread.Frame> frames = this._frames;
		synchronized (this)
		{
			return frames.<SpringThread.Frame>toArray(
				new SpringThread.Frame[frames.size()]);
		}
	}
	
	/**
	 * Returns the stack trace for this thread.
	 * 
	 * @return The stack trace for this thread.
	 * @since 2020/06/13
	 */
	public final CallTraceElement[] getStackTrace()
	{
		// Lock since the frames may change!
		List<SpringThread.Frame> frames = this._frames;
		synchronized (this)
		{
			// Setup target array
			int n = frames.size();
			CallTraceElement[] rv = new CallTraceElement[n];
			
			// The frames at the end are at the top
			for (int i = n - 1, write = 0; i >= 0; i--, write++)
			{
				SpringThread.Frame frame = frames.get(i);
				CallTraceElement trace;
				
				// Blanks are purely virtual standing points so they are
				// regarded as such
				if (frame.isBlank())
				{
					trace = new CallTraceElement(
						"<guard>", "<guard>", null,
						0L, null, -1);
				}
				
				// Print other parts
				else
				{
					SpringMethod inMethod = frame.method();
					int pc = frame.lastExecutedPc();
					
					trace = new CallTraceElement(
						inMethod.inClass().toString(),
						inMethod.name().toString(),
						inMethod.nameAndType().type().toString(),
						0,
						inMethod.infile,
						inMethod.byteCode().lineOfAddress(pc),
						inMethod.byteCode().getByAddress(pc).operation(),
						pc);
				}
				
				// Store trace in top-most order
				rv[write] = trace;
			}
			
			return rv;
		}
	}
	
	/**
	 * Interrupts this thread.
	 * 
	 * @since 2020/06/22
	 */
	public final void hardInterrupt()
	{
		SpringThreadWorker worker = this._worker;
		if (worker == null)
			throw new IllegalStateException(
				"Cannot interrupt thread with no worker.");
		
		// Signal the other thread or something else?
		Thread signal = this._worker.signalinstead;
		if (signal != null)
			signal.interrupt();
		else
			worker.interrupt();
	}
	
	/**
	 * Returns whether or not this thread has an instance.
	 *
	 * @return If this thread has an instance.
	 * @since 2020/06/18
	 */
	public final boolean hasThreadInstance()
	{
		synchronized (this)
		{
			return this._threadInstance != null;
		}
	}
	
	/**
	 * Invokes the given method, this forwards to
	 * {@link SpringThreadWorker#invokeMethod(boolean, ClassName,
	 * MethodNameAndType, Object...)}.
	 * 
	 * @param __static Is the method static?
	 * @param __cl The class to execute from within.
	 * @param __nat The method to be invoked.
	 * @param __args The arguments to the call.
	 * @return The return value from the method.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/09/15
	 */
	public final Object invokeMethod(boolean __static, ClassName __cl,
		MethodNameAndType __nat, Object... __args)
		throws NullPointerException
	{
		if (__cl == null || __nat == null || __args == null)
			throw new NullPointerException("NARG");
		
		return this._worker.invokeMethod(__static, __cl, __nat, __args);
	}
	
	/**
	 * Returns the machine that created this.
	 * 
	 * @return The machine that created this.
	 * @since 2021/03/16
	 */
	public SpringMachine machine()
	{
		SpringThreadWorker worker = this._worker;
		return (worker == null ? this.machineRef.get() : worker.machine);
	}
	
	/**
	 * Is this a daemon thread?
	 *
	 * @return If this is a daemon thread.
	 * @since 2020/06/17
	 */
	public final boolean isDaemon()
	{
		return this._daemon;
	}
	
	/**
	 * Is exiting the virtual machine okay?
	 *
	 * @return If it is okay to exit.
	 * @since 2018/11/17
	 */
	public final boolean isExitOkay()
	{
		return this._daemon || this._terminate;
	}
	
	/**
	 * If this is a main thread or not.
	 *
	 * @return If this is a main thread.
	 * @since 2020/06/17
	 */
	public final boolean isMain()
	{
		return this.main;
	}
	
	/**
	 * Returns if this thread has terminated.
	 * 
	 * @return If this thread has terminated.
	 * @since 2020/06/29
	 */
	public final boolean isTerminated()
	{
		synchronized (this)
		{
			return this._terminate;
		}
	}
	
	/**
	 * Returns the name of the thread.
	 *
	 * @return The name of the thread.
	 * @since 2018/09/03
	 */
	public final String name()
	{
		return this.name;
	}
	
	/**
	 * Returns the number of frames that are available in this thread.
	 *
	 * @return The number of available frames.
	 * @since 2018/09/03
	 */
	public final int numFrames()
	{
		List<SpringThread.Frame> frames = this._frames;
		synchronized (this)
		{
			return frames.size();
		}
	}
	
	/**
	 * Pops a frame from the thread stack.
	 *
	 * @return The frame which was popped.
	 * @throws SpringVirtualMachineException If there are no stack frames.
	 * @since 2018/09/09
	 */
	public final SpringThread.Frame popFrame()
		throws SpringVirtualMachineException
	{
		// Pop from the stack
		SpringThread.Frame rv;
		List<SpringThread.Frame> frames = this._frames;
		synchronized (this)
		{
			// {@squirreljme.error BK1o No frames to pop.}
			int n;
			if ((n = frames.size()) <= 0)
				throw new SpringVirtualMachineException("BK1o");	
			
			rv = frames.remove(n - 1);
		}
		
		// Exit the frame, if not blank
		if (!rv.isblank)
			this.profiler.exitFrame(System.nanoTime());
		
		// If there is a monitor associated with this then leave it
		SpringObject monitor = rv._monitor;
		if (monitor != null)
			monitor.monitor().exit(this, true);
		
		return rv;
	}
	
	/**
	 * Prints this thread's stack trace.
	 *
	 * @param __ps The stream to write to.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/09/20
	 */
	public final void printStackTrace(PrintStream __ps)
		throws NullPointerException
	{
		if (__ps == null)
			throw new NullPointerException("NARG");
		
		// Use standard SquirrelJME trace printing here
		CallTraceUtils.printStackTrace(__ps,
			String.format("SpringThread #%d: %s", this.id, this.name),
			this.getStackTrace(), null, null,
			0);
	}
	
	/**
	 * Sets this thread as a daemon thread.
	 * 
	 * @since 2020/09/12
	 */
	public void setDaemon()
	{
		synchronized (this)
		{
			this._daemon = true;
		}
	}
	
	/**
	 * Sets the thread status.
	 * 
	 * @param __status The {@link ThreadStatusType} to set.
	 * @since 2021/03/15
	 */
	public void setStatus(int __status)
	{
		synchronized (this)
		{
			this._status = __status;
		}
	}
	
	/**
	 * Sets the {@link Thread} instance.
	 *
	 * @param __object The object to set the instance to.
	 * @throws IllegalStateException If this thread already has an instance.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/06/17
	 */
	public final void setThreadInstance(SpringObject __object)
		throws IllegalStateException, NullPointerException
	{
		if (__object == null)
			throw new NullPointerException("NARG");
		
		synchronized (this)
		{
			// Only a single instance is permitted
			if (this._threadInstance != null)
				throw new IllegalStateException("Thread has an instance.");
			
			this._threadInstance = __object;
		}
	}
	
	/**
	 * Sets the virtual machine thread of this thread.
	 *
	 * @param __vmThread The thread to set.
	 * @throws IllegalStateException If this thread already has one.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/06/17
	 */
	public final void setVMThread(VMThreadObject __vmThread)
		throws IllegalStateException, NullPointerException
	{
		if (__vmThread == null)
			throw new NullPointerException("NARG");
		
		synchronized (this)
		{
			if (this._vmThread != null)
				throw new IllegalStateException("Thread has VM Thread.");
			
			this._vmThread = __vmThread;
		}
	}
	
	/**
	 * Terminates this thread.
	 * 
	 * @since 2020/06/29
	 */
	public final void terminate()
	{
		// Terminates this thread
		synchronized (this)
		{
			// Set as terminated
			this._terminate = true;
		}
		
		// Signal to the machine that this thread terminated
		SpringThreadWorker worker = this._worker;
		if (worker != null)
			worker.machine.signalThreadTerminate(this);
	}
	
	/**
	 * Returns the instance of the {@link Thread} object for this thread.
	 *
	 * @return The instance of {@link Thread}.
	 * @throws IllegalStateException If the thread has no instance.
	 * @since 2020/06/17
	 */
	public final SpringObject threadInstance()
		throws IllegalStateException
	{
		SpringObject rv;
		synchronized (this)
		{
			rv = this._threadInstance;
		}
		
		if (rv == null)
			throw new IllegalStateException("Thread has no instance.");
		
		return rv;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/09/15
	 */
	@Override
	public final String toString()
	{
		Reference<String> ref = this._string;
		String rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._string = new WeakReference<>((rv = String.format(
				"Thread-%d: %s", this.id, this.name)));
		
		return rv;
	}
	
	/**
	 * This class represents the stack frame and is used to store local
	 * variables and other such things.
	 *
	 * @since 2018/09/03
	 */
	public static class Frame
	{
		/** The frame level. */
		public final int level;
		
		/** The current method in this frame. */
		protected final SpringMethod method;
		
		/** The method code for execution. */
		protected final ByteCode code;
		
		/** The current object of the current frame. */
		protected final SpringObject thisobject;
		
		/** Is this frame blank? */
		protected final boolean isblank;
		
		/** The class this is for. */
		protected final SpringClass springClass;
		
		/** Local variables. */
		private final Object[] _locals;
		
		/** The stack. */
		private final Object[] _stack;
		
		/** Profiled frame. */
		volatile ProfiledFrame _profiler;
		
		/** The top of the stack. */
		private volatile int _stacktop;
		
		/** The current program counter. */
		private volatile int _pc;
		
		/** Last executed PC address. */
		private volatile int _lastexecpc;
		
		/** Frame execution count. */
		private volatile int _execcount;
		
		/** Exception which was tossed into this frame. */
		private SpringObject _tossedexception;
		
		/** Object which has a monitor for quicker unlock. */
		volatile SpringObject _monitor;
		
		/**
		 * Initializes a blank guard frame.
		 *
		 * @param __level The frame depth.
		 * @since 2018/09/20
		 */
		Frame(int __level)
		{
			this.level = __level;
			this.method = null;
			this.springClass = null;
			this.code = null;
			this.thisobject = null;
			this.isblank = true;
			this._locals = new Object[0];
			this._stack = new Object[2];
		}
		
		/**
		 * Initializes the frame.
		 *
		 * @param __level The frame depth.
		 * @param __cl The class used.
		 * @param __m The method used for the frame.
		 * @param __args The frame arguments.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/09/03
		 */
		Frame(int __level, SpringClass __cl, SpringMethod __m,
			Object... __args)
			throws NullPointerException
		{
			if (__cl == null || __m == null)
				throw new NullPointerException("NARG");
			
			__args = (__args == null ? new Object[0] : __args.clone());
			
			this.level = __level;
			this.isblank = false;
			this.method = __m;
			this.springClass = __cl;
			
			// We will need to initialize the local and stack data from the
			// information the byte code gives
			ByteCode code;
			this.code = (code = __m.byteCode());
			
			// Initialize variable storage
			Object[] locals;
			this._locals = (locals = new Object[code.maxLocals()]);
			this._stack = new Object[code.maxStack()];
			
			// Copy arguments passed to the method
			for (int i = 0, n = __args.length, o = 0; i < n; i++)
			{
				Object av;
				locals[o++] = (av = __args[i]);
				
				// Add additional top for long/double
				if (av instanceof Long || av instanceof Double)
					locals[o++] = SpringStackTop.TOP;
			}
			
			// Store the this object, if needed
			this.thisobject = (__m.flags().isStatic() ? null :
				(SpringObject)__args[0]);
			
			// Debug
			/*todo.DEBUG.note("Frame has %d locals, %d stack", locals.length,
				this._stack.length);*/
		}
		
		/**
		 * Returns the byte code to execute.
		 *
		 * @return The byte code to execute.
		 * @since 2018/09/03
		 */
		public final ByteCode byteCode()
		{
			return this.code;
		}
		
		/**
		 * Clears the stack.
		 *
		 * @since 2018/10/13
		 */
		public final void clearStack()
		{
			this._stacktop = 0;
		}
		
		/**
		 * Increments the execution counter for this frame.
		 *
		 * @return The original execution count.
		 * @since 2018/10/12
		 */
		public final int incrementExecCount()
		{
			return this._execcount++;
		}
		
		/**
		 * Is this a blank frame?
		 *
		 * @return If this is a blank frame.
		 * @since 2018/09/20
		 */
		public final boolean isBlank()
		{
			return this.isblank;
		}
		
		/**
		 * Returns the last executed PC address.
		 *
		 * @return The last executed PC address.
		 * @since 2018/09/20
		 */
		public final int lastExecutedPc()
		{
			return this._lastexecpc;
		}
		
		/**
		 * Returns the source line of the last executed address.
		 *
		 * @return The source line for the last executed address.
		 * @since 2018/09/24
		 */
		public final int lastExecutedPcSourceLine()
		{
			ByteCode code = this.code;
			if (code == null)
				return -1;
			return code.lineOfAddress(this._lastexecpc);
		}
		
		/**
		 * Loads a value from local variables.
		 *
		 * @param <C> The type to return.
		 * @param __cl The type to return.
		 * @param __dx The index to load from.
		 * @return The read value.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/09/16
		 */
		public final <C> C loadLocal(Class<C> __cl, int __dx)
			throws NullPointerException
		{
			return __cl.cast(this._locals[__dx]);
		}
		
		/**
		 * Loads the specified value from a local variable and pushes it to
		 * the stack.
		 *
		 * @param __cl The expected class type.
		 * @param __dx The index to load from.
		 * @since 2018/09/08
		 */
		public final void loadToStack(Class<?> __cl, int __dx)
		{
			Object[] locals = this._locals;
			
			// {@squirreljme.error BK1p Cannot push local variable to the stack
			// because it of the incorrect type. (The varible to push; The
			// index to load from; The expected class; The value to push;
			// The type of value to push)}
			Object pushy = locals[__dx];
			if (!__cl.isInstance(pushy))
				throw new SpringVirtualMachineException(String.format(
					"BK1p %s %d %s %s %s", pushy, __dx, __cl, pushy,
					(pushy == null ? "null" : pushy.getClass())));
			
			// Just copy to the stack
			this.pushToStack(pushy);
		}
		
		/**
		 * Returns the method that this frame represents.
		 *
		 * @return The current method the frame is in.
		 * @since 2018/09/08
		 */
		public final SpringMethod method()
		{
			return this.method;
		}
		
		/**
		 * Returns the number of locals.
		 * 
		 * @return The number of locals.
		 * @since 2021/04/14
		 */
		public final int numLocals()
		{
			return this._locals.length;
		}
		
		/**
		 * Returns the instruction counter.
		 *
		 * @return The instruction counter.
		 * @since 2018/09/03
		 */
		public final int pc()
		{
			return this._pc;
		}
		
		/**
		 * Returns the PC index.
		 * 
		 * @return The PC index.
		 * @since 2021/04/11
		 */
		public final int pcIndex()
		{
			ByteCode code = this.code;
			if (code == null)
				return this._pc;
			
			// These just use indexes, not true addresses
			return code.addressToIndex(this._pc);
		}
		
		/**
		 * Returns the line of code the program counter is on.
		 *
		 * @return The program counter line of code.
		 * @since 2018/09/16
		 */
		public final int pcSourceLine()
		{
			return this.code.lineOfAddress(this._pc);
		}
		
		/**
		 * Pops from the stack.
		 *
		 * @return The popped value.
		 * @throws SpringVirtualMachineException If the stack underflows.
		 * @since 2018/09/09
		 */
		public final Object popFromStack()
			throws SpringVirtualMachineException
		{
			Object[] stack = this._stack;
			int stacktop = this._stacktop;
			
			// {@squirreljme.error BK1q Stack underflow. (The current top of
			// the stack; The stack limit)}
			if (stacktop <= 0)
				throw new SpringVirtualMachineException(String.format(
					"BK1q %d %d", stacktop, stack.length));
			
			// Read value and clear the value that was there
			Object rv = stack[--stacktop];
			stack[stacktop] = null;
			this._stacktop = stacktop;
			
			// {@squirreljme.error BK1r Popped a null value of the stack, which
			// should not occur.}
			if (rv == null)
				throw new SpringVirtualMachineException("BK1r");
			
			// Is top, so pop again to read the actual desired value
			if (rv == SpringStackTop.TOP)
			{
				rv = this.popFromStack();
				
				// {@squirreljme.error BK1s Expected long or double below
				// top entry in stack. (The current top of the stack; The
				// stack limit)}
				if (!(rv instanceof Long || rv instanceof Double))
					throw new SpringVirtualMachineException(String.format(
						"BK1s %d %d", stacktop, stack.length));
			}
			
			// Debug
			/*todo.DEBUG.note("popped(%s) <- %d", rv, stacktop);*/
			
			return rv;
		}
		
		/**
		 * Pops from the stack, checking the type.
		 *
		 * @param <C> The type to pop.
		 * @param __cl The type to pop.
		 * @return The popped data.
		 * @throws NullPointerException On null arguments.
		 * @throws SpringVirtualMachineException If the type does not match or
		 * the stack underflows.
		 * @since 2018/09/15
		 */
		public final <C> C popFromStack(Class<C> __cl)
			throws NullPointerException, SpringVirtualMachineException
		{
			if (__cl == null)
				throw new NullPointerException("NARG");
			
			// {@squirreljme.error BK1t Popped the wrong kind of value from the
			// stack. (The popped type; The expected type)}
			Object rv = this.popFromStack();
			if (!__cl.isInstance(rv))
				throw new SpringVirtualMachineException(
					String.format("BK1t %s %s",
						(rv == null ? null : rv.getClass()), __cl));
			
			return __cl.cast(rv);
		}
		
		/**
		 * Pops the given class from the stack and throws an exception if it
		 * is null.
		 *
		 * @param <C> The type to pop.
		 * @param __cl The type to pop.
		 * @return The popped data.
		 * @throws NullPointerException On null arguments.
		 * @throws SpringNullPointerException If the popped item is null.
		 * @throws SpringVirtualMachineException If the type does not match or
		 * the stack underflows.
		 * @since 2018/12/11
		 */
		public final <C> C popFromStackNotNull(Class<C> __cl)
			throws NullPointerException, SpringNullPointerException,
				SpringVirtualMachineException
		{
			if (__cl == null)
				throw new NullPointerException("NARG");
			
			// Pop from the stack
			Object rv = this.popFromStack();
			
			// {@squirreljme.error BK1u Did not expect a null value to be
			// popped from the stack.}
			if (rv == SpringNullObject.NULL ||
				(rv instanceof SpringNullObject))
				throw new SpringNullPointerException("BK1u");
			
			// {@squirreljme.error BK1v Popped the wrong kind of value from the
			// stack. (The popped type; The expected type)}
			if (!__cl.isInstance(rv))
				throw new SpringVirtualMachineException(
					String.format("BK1v %s %s",
						(rv == null ? null : rv.getClass()), __cl));
			
			return __cl.cast(rv);
		}
		
		/**
		 * Pushes the specified value to the stack.
		 *
		 * @param __v The value to push.
		 * @throws NullPointerException On null arguments.
		 * @throws SpringVirtualMachineException If the stack overflows or is
		 * not valid.
		 * @since 2018/09/08
		 */
		public final void pushToStack(Object __v)
			throws NullPointerException, SpringVirtualMachineException
		{
			if (__v == null)
				throw new NullPointerException("NARG");
			
			Object[] stack = this._stack;
			int stacktop = this._stacktop;
			
			// Debug
			/*todo.DEBUG.note("push(%s) -> %d", __v, stacktop);*/
			
			// {@squirreljme.error BK1w Stack overflow pushing value. (The
			// value; The current top of the stack; The stack limit)}
			if (stacktop >= stack.length)
				throw new SpringVirtualMachineException(String.format(
					"BK1w %s %d %d", __v, stacktop, stack.length));
			
			// Store
			stack[stacktop++] = __v;
			this._stacktop = stacktop;
			
			// Push an extra top for long and double
			if (__v instanceof Long || __v instanceof Double)
				this.pushToStack(SpringStackTop.TOP);
		}
		
		/**
		 * Sets the last executed PC address.
		 *
		 * @param __pc The last executed PC address.
		 * @since 2018/09/20
		 */
		public final void setLastExecutedPc(int __pc)
		{
			this._lastexecpc = __pc;
		}
		
		/**
		 * Sets the program counter.
		 *
		 * @param __pc The program counter to set.
		 * @since 2018/09/03
		 */
		public final void setPc(int __pc)
		{
			this._pc = __pc;
		}
		
		/**
		 * Stores the specified value at the given local variable index.
		 *
		 * @param __dx The index to store into.
		 * @param __v The value to store.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/09/15
		 */
		public final void storeLocal(int __dx, Object __v)
			throws NullPointerException
		{
			if (__v == null)
				throw new NullPointerException("NARG");
			
			this._locals[__dx] = __v;
		}
		
		/**
		 * Returns the {@code this} of the current frame.
		 *
		 * @return The current this,
		 * @since 2018/09/09
		 */
		public final SpringObject thisObject()
		{
			return this.thisobject;
		}
		
		/**
		 * Returns the tossed exception.
		 *
		 * @return The exception being tossed.
		 * @since 2018/10/13
		 */
		public final SpringObject tossedException()
		{
			return this._tossedexception;
		}
		
		/**
		 * Sets the tossed exception for this frame.
		 *
		 * @param __o The exception tossed for this frame.
		 * @since 2018/10/13
		 */
		public final void tossException(SpringObject __o)
		{
			this._tossedexception = __o;
		}
	}
}

