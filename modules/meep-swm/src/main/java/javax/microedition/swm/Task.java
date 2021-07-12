// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package javax.microedition.swm;

import cc.squirreljme.runtime.cldc.debug.Debugging;
import java.util.Objects;

/**
 * This describes a task which is currently running on the system. Each task
 * has a starting point which is a {@link javax.microedition.midlet.MIDlet}.
 *
 * System tasks cannot be started or stopped.
 *
 * @since 2016/06/24
 */
public final class Task
{
	/** The ID of the task. */
	final int _tid;
	
	/** The suite of this task. */
	final Suite _suite;
	
	/** The entry class of the task. */
	final String _entry;
	
	/**
	 * Initializes the task.
	 *
	 * @param __tid The task ID, zero is the system task.
	 * @param __s The suite used.
	 * @param __e The entry name of the task.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/06/24
	 */
	Task(int __tid, Suite __s, String __e)
		throws NullPointerException
	{
		if (__s == null || __e == null)
			throw new NullPointerException("NARG");
		
		this._tid = __tid;
		this._suite = __s;
		this._entry = __e;
	}
	
	/**
	 * Checks if this task is the same as another task, tasks are equal if
	 * they share the same name.
	 *
	 * @param __o The other object.
	 * @return {@code true} if this is the same task.
	 * @since 2016/06/24
	 */
	@Override
	public boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		// Must be another task
		if (!(__o instanceof Task))
			return false;
		
		// Check
		Task o = (Task)__o;
		return Objects.equals(this.getSuite(), o.getSuite()) &&
			Objects.equals(this.getName(), o.getName());
	}
	
	/**
	 * Returns the estimated number of bytes that a task is using.
	 *
	 * @return The number of bytes the task is estimated to be using.
	 * @since 2016/06/24
	 */
	public int getHeapUse()
	{
		throw new todo.TODO();
		/*
		// Make sure the amount of memory used does not overflow ever
		long rv = this._task.metric(SystemTaskMetric.MEMORY_USED);
		if (rv < 0L)
			return 0;
		else if (rv > Integer.MAX_VALUE)
			return Integer.MAX_VALUE;
		return (int)rv;
		*/
	}
	
	/**
	 * Returns the name of the current task.
	 *
	 * @return The name of the task, system tasks always return {@code null}.
	 * @since 2016/06/24
	 */
	public String getName()
	{
		// System tasks have no name
		if (this.isSystemTask())
			return null;
		
		return this._entry;
	}
	
	/**
	 * Returns the priority of this task.
	 *
	 * @return The task priority.
	 * @since 2016/06/24
	 */
	public TaskPriority getPriority()
	{
		throw new todo.TODO();
		/*
		long rv = this._task.metric(SystemTaskMetric.PRIORITY);
		if (rv < 0L)
			return TaskPriority.MAX;
		else if (rv > 0L)
			return TaskPriority.MIN;
		return TaskPriority.NORM;
		*/
	}
	
	/**
	 * Returns the status of this task.
	 *
	 * @return The task status.
	 * @since 2016/06/24
	 */
	public TaskStatus getStatus()
	{
		// If the TID is negative then it failed to start
		int tid = this._tid;
		if (tid < 0)
			return TaskStatus.START_FAILED;
		
		return TaskStatus.__of(Debugging.<Integer>todoObject(tid));
	}
	
	/**
	 * Returns the suite that this task belongs to.
	 *
	 * @return The owning suite.
	 * @since 2016/06/24
	 */
	public Suite getSuite()
	{
		// All system tasks are hidden behind the system suite
		if (this.isSystemTask())
			return Suite.SYSTEM_SUITE;
		
		return this._suite;
	}
	
	/**
	 * Returns the hash code of this task, the hashcode is derived from the
	 * suite and the name.
	 *
	 * @return The hash code.
	 * @since 2016/06/24
	 */
	@Override
	public int hashCode()
	{
		return Objects.hashCode(this.getSuite()) ^
			Objects.hashCode(this.getName());
	}
	
	/**
	 * Returns {@code true} if this is a system task.
	 *
	 * @return {@code true} if a system task.
	 * @since 2016/06/24
	 */
	public boolean isSystemTask()
	{
		// System task is always zero
		return this._tid == 0;
	}
}

