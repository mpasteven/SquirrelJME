// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.task;

import cc.squirreljme.jvm.Assembly;

/**
 * This represents a single thread, which is associated with a task.
 *
 * Every thread has a controller thread, this is the thread which is
 * actually executing the given thread even if the IDs are different. This
 * allows other threads to execute within the contexts of other threads
 * accordingly.
 *
 * @since 2019/10/13
 */
public final class TaskThread
{
	/** The owning process ID. */
	protected final int pid;
	
	/** The thread ID. */
	protected final int tid;
	
	/** The logical ID. */
	protected final int lid;
	
	/** The static field pointer for this thread. */
	private int _staticfieldptr;
	
	/**
	 * Initializes the thread.
	 *
	 * @param __pid The owning process ID.
	 * @param __tid The task ID.
	 * @param __lid The logical thread ID.
	 * @since 2019/10/19
	 */
	public TaskThread(int __pid, int __tid, int __lid)
	{
		this.pid = __pid;
		this.tid = __tid;
		this.lid = __lid;
	}
	
	/**
	 * Enters the given frame on the thread. Note that this can only be
	 * done from the current thread where it will be executed.
	 *
	 * This searches for the method and loads any classes as needed.
	 *
	 * @param __cl The class to execute.
	 * @param __mn The method name.
	 * @param __mt The method type.
	 * @param __args The arguments to the thread.
	 * @return The return values of the method call
	 * @throws IllegalStateException If the current thread is being executed
	 * and the current controller thread is not the current thread of
	 * execution.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/10/13
	 */
	public final long execute(String __cl, String __mn, String __mt,
		int... __args)
		throws IllegalStateException, NullPointerException
	{
		if (__cl == null || __mn == null || __mt == null)
			throw new NullPointerException("NARG");
		
		throw new todo.TODO();
	}
	
	/**
	 * Enters the given frame on the thread. Note that this can only be
	 * done from the current thread where it will be executed.
	 *
	 * @param __methpool The combined method pointer to invoke and the
	 * constant pool pointer to load, the method pointer is in the low word
	 * while the pool is in the high word.
	 * @param __args The arguments to the thread.
	 * @return The return values of the method call
	 * @throws IllegalStateException If the current thread is being executed
	 * and the current controller thread is not the current thread of
	 * execution.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/12/08
	 */
	public final long execute(long __methpool, int... __args)
		throws IllegalStateException, NullPointerException
	{
		return this.execute(Assembly.longUnpackLow(__methpool),
			Assembly.longUnpackHigh(__methpool), __args);
	}
	
	/**
	 * Enters the given frame on the thread. Note that this can only be
	 * done from the current thread where it will be executed.
	 *
	 * @param __meth The method pointer to invoke.
	 * @param __pool The constant pool pointer to load.
	 * @param __args The arguments to the thread.
	 * @return The return values of the method call
	 * @throws IllegalStateException If the current thread is being executed
	 * and the current controller thread is not the current thread of
	 * execution.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/12/08
	 */
	public final long execute(int __meth, int __pool, int... __args)
		throws IllegalStateException, NullPointerException
	{
		throw new todo.TODO();
	}
	
	/**
	 * Sets the static field pointer for this thread.
	 *
	 * @param __d The static field pointer.
	 * @since 2019/10/13
	 */
	public final void setStaticFieldPointer(int __d)
	{
		this._staticfieldptr = __d;
	}
}

