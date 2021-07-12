// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.mle;

import cc.squirreljme.jvm.mle.brackets.TaskBracket;
import cc.squirreljme.jvm.mle.brackets.TracePointBracket;
import cc.squirreljme.jvm.mle.brackets.VMThreadBracket;
import cc.squirreljme.jvm.mle.constants.ThreadModelType;
import cc.squirreljme.jvm.mle.exceptions.MLECallError;

/**
 * This shelf handles everything regarding threading and otherwise.
 *
 * @see VMThreadBracket
 * @since 2020/06/17
 */
public final class ThreadShelf
{
	/**
	 * Returns the number of alive threads.
	 *
	 * @param __includeMain Include main threads?
	 * @param __includeDaemon Include daemon threads?
	 * @return The number of alive threads.
	 * @since 2020/06/17
	 */
	public static native int aliveThreadCount(boolean __includeMain,
		boolean __includeDaemon);
	
	/**
	 * Creates a virtual machine thread for the given Java thread.
	 *
	 * @param __javaThread The Java thread to create under.
	 * @return The virtual machine thread.
	 * @throws MLECallError If {@code __javaThread} is null.
	 * @since 2020/06/17
	 */
	public static native VMThreadBracket createVMThread(Thread __javaThread)
		throws MLECallError;
	
	/**
	 * Returns the exit code for the current process.
	 *
	 * @return The exit code for the current process.
	 * @since 2020/06/17
	 */
	public static native int currentExitCode();
	
	/**
	 * Returns the current Java thread.
	 *
	 * @return The current {@link Thread}.
	 * @since 2020/06/17
	 */
	public static native Thread currentJavaThread();
	
	/**
	 * Returns the current virtual machine thread.
	 * 
	 * @return The current virtual machine thread.
	 * @since 2021/05/08
	 */
	public static native VMThreadBracket currentVMThread();
	
	/**
	 * Checks if these two threads are the same.
	 * 
	 * @param __a The first thread.
	 * @param __b The second thread.
	 * @return If these are the same thread.
	 * @throws MLECallError If either arguments are null.
	 * @since 2021/05/08
	 */
	public static native boolean equals(VMThreadBracket __a,
		VMThreadBracket __b)
		throws MLECallError;
	
	/**
	 * Returns whether the interrupt flag was raised and clears it.
	 *
	 * @param __javaThread The Java thread.
	 * @return If the thread was interrupted.
	 * @throws MLECallError If {@code __javaThread} is null.
	 * @since 2020/06/17
	 */
	public static native boolean javaThreadClearInterrupt(Thread __javaThread)
		throws MLECallError;
	
	/**
	 * Marks the thread as being started.
	 *
	 * @param __javaThread The thread to mark started.
	 * @throws MLECallError If {@code __javaThread} is null.
	 * @since 2020/06/17
	 */
	public static native void javaThreadFlagStarted(Thread __javaThread)
		throws MLECallError;
	
	/**
	 * Has this Java thread been started?
	 *
	 * @param __javaThread The Java thread.
	 * @return If this thread has been started.
	 * @throws MLECallError If {@code __javaThread} is null.
	 * @since 2020/06/17
	 */
	public static native boolean javaThreadIsStarted(Thread __javaThread)
		throws MLECallError;
	
	/**
	 * Returns the runnable for the given Java thread.
	 *
	 * @param __javaThread The Java thread.
	 * @return The {@link Runnable} for the given thread.
	 * @throws MLECallError If {@code __javaThread} is null.
	 * @since 2020/06/17
	 */
	public static native Runnable javaThreadRunnable(Thread __javaThread)
		throws MLECallError;
	
	/**
	 * Sets if the thread is alive or not.
	 *
	 * @param __javaThread The Java thread.
	 * @param __set If this is to be alive or not. If this is {@code true}
	 * then the active count goes up, otherwise it shall go down.
	 * @throws MLECallError If {@code __javaThread} is null.
	 * @since 2020/06/17
	 */
	public static native void javaThreadSetAlive(Thread __javaThread,
		boolean __set)
		throws MLECallError;
	
	/**
	 * Sets the thread to be a daemon thread, it cannot be started.
	 * 
	 * @param __javaThread The thread to set as a daemon thread.
	 * @throws MLECallError If {@code __javaThread} is null or is already
	 * started.
	 * @since 2020/09/12
	 */
	public static native void javaThreadSetDaemon(Thread __javaThread)
		throws MLECallError;
	
	/**
	 * Returns the {@link ThreadModelType} of the virtual machine.
	 * 
	 * @return The {@link ThreadModelType} of the virtual machine.
	 * @since 2021/05/07
	 */
	public static native int model();
	
	/**
	 * Runs the main entry point for the current process and gives it all of
	 * the arguments that were specified on program initialization.
	 *
	 * @since 2020/06/17
	 */
	public static native void runProcessMain();
	
	/**
	 * Sets the current process exit code.
	 *
	 * @param __code The exit code to use.
	 * @since 2020/06/17
	 */
	public static native void setCurrentExitCode(int __code);
	
	/**
	 * Sets the trace of the current task so that it can be requested by
	 * another or launching program.
	 * 
	 * @param __message The message of the trace.
	 * @param __trace The trace to set.
	 * @throws MLECallError If {@code __message} is {@code null}, or
	 * {@code __trace} or any element within is {@code null}.
	 * @since 2020/07/02
	 */
	public static native void setTrace(String __message,
		TracePointBracket[] __trace)
		throws MLECallError;
	
	/**
	 * Sleeps the current thread for the given amount of time.
	 *
	 * If both times are zero, this means to yield the thread (give up its
	 * current execution context).
	 * 
	 * If SquirrelJME is running in cooperative
	 * single threaded mode, this will relinquish control of the current
	 * thread.
	 *
	 * @param __ms The number of milliseconds.
	 * @param __ns The number of nanoseconds.
	 * @return {@code true} if the thread was interrupted.
	 * @throws MLECallError If either value is negative or the nanoseconds is
	 * out of range.
	 * @since 2020/06/17
	 */
	public static native boolean sleep(int __ms, int __ns)
		throws MLECallError;
	
	/**
	 * Returns the Java thread for the VM thread.
	 *
	 * @param __vmThread The VM thread.
	 * @return The Java thread which belongs to this thread.
	 * @throws MLECallError If {@code __thread} is null.
	 * @since 2020/06/17
	 */
	public static native Thread toJavaThread(VMThreadBracket __vmThread)
		throws MLECallError;
	
	/**
	 * Returns the virtual machine thread from the given Java thread.
	 *
	 * @param __thread The Java thread.
	 * @return The VM thread for this thread.
	 * @throws MLECallError If {@code __thread} is null.
	 * @since 2020/06/17
	 */
	public static native VMThreadBracket toVMThread(Thread __thread)
		throws MLECallError;
	
	/**
	 * Signals that the thread has ended and is no longer considered to be
	 * alive.
	 * 
	 * @param __vmThread The virtual machine thread.
	 * @throws MLECallError If {@code __vmThread} is null.
	 * @since 2021/03/14
	 */
	public static native void vmThreadEnd(VMThreadBracket __vmThread)
		throws MLECallError;
	
	/**
	 * Returns the thread ID for the given thread.
	 *
	 * @param __vmThread The virtual machine thread.
	 * @return The thread ID.
	 * @throws MLECallError If {@code __vmThread} is null.
	 * @since 2020/06/17
	 */
	public static native int vmThreadId(VMThreadBracket __vmThread)
		throws MLECallError;
	
	/**
	 * Performs a hardware interrupt on the thread.
	 *
	 * @param __vmThread The virtual machine thread.
	 * @throws MLECallError If {@code __vmThread} is null.
	 * @since 2020/06/17
	 */
	public static native void vmThreadInterrupt(VMThreadBracket __vmThread)
		throws MLECallError;
	
	/**
	 * Checks if the given thread is a main thread.
	 *
	 * @param __vmThread The thread to check.
	 * @return {@code true} if the given thread is a main thread.
	 * @throws MLECallError If {@code __vmThread} is null.
	 * @since 2020/06/17
	 */
	public static native boolean vmThreadIsMain(VMThreadBracket __vmThread)
		throws MLECallError;
	
	/**
	 * Sets the thread priority in the same manner as
	 * {@link Thread#setPriority(int)} if this is supported by the hardware.
	 * 
	 * This may or may not be supported and should only be used as a hint and
	 * not a guarantee.
	 *
	 * @param __vmThread The virtual machine thread.
	 * @param __p The priority to set, this will be the same as
	 * {@link Thread#setPriority(int)}.
	 * @throws MLECallError If {@code __vmThread} is null or {@code __p} is
	 * not within {@link Thread#MIN_PRIORITY} and {@link Thread#MAX_PRIORITY}
	 * inclusive.
	 * @since 2020/06/17
	 */
	public static native void vmThreadSetPriority(VMThreadBracket __vmThread,
		int __p)
		throws MLECallError;
	
	/**
	 * Performs the actual start of the given thread.
	 *
	 * @param __vmThread The thread to start.
	 * @return If the start of the thread succeeded.
	 * @throws MLECallError If {@code __vmThread} is null.
	 * @since 2020/06/17
	 */
	public static native boolean vmThreadStart(VMThreadBracket __vmThread)
		throws MLECallError;
	
	/**
	 * Returns the task that owns the given thread.
	 * 
	 * @param __vmThread The thread to get the task of.
	 * @return The task for the given thread.
	 * @throws MLECallError If the thread is not valid.
	 * @since 2021/05/08
	 */
	public static native TaskBracket vmThreadTask(VMThreadBracket __vmThread)
		throws MLECallError;
	
	/**
	 * Waits for the state of threads to be updated, or just times out.
	 *
	 * A thread update is when another thread becomes alive, becomes dead,
	 * or is started.
	 * 
	 * If waiting and SquirrelJME is running in cooperative
	 * single threaded mode, this will relinquish control of the current
	 * thread.
	 *
	 * @param __ms The amount of time to wait for.
	 * @return If the thread was interrupted while waiting.
	 * @throws MLECallError If {@code __ms} is negative.
	 * @since 2020/06/17
	 */
	public static native boolean waitForUpdate(int __ms)
		throws MLECallError;
}
