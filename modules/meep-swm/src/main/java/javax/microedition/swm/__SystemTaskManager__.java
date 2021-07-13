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
import cc.squirreljme.runtime.swm.ExtendedTaskManager;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the task manager which interfaces with the CLDC system support
 * methods to provide access to tasks and such.
 *
 * @since 2017/12/07
 */
final class __SystemTaskManager__
	implements ExtendedTaskManager, TaskManager
{
	/** Mapping of task IDs to tasks. */
	static final Map<Integer, Task> _TASKS =
		new HashMap<>();
	
	/** Internal lock for chore management. */
	protected final Object lock =
		new Object();
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/07
	 */
	@Override
	public void addTaskListener(TaskListener __tl)
	{
		throw new todo.TODO();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/07
	 */
	@Override
	public Task getCurrentTask()
	{
		throw new todo.TODO();
		/*
		return this.__byId(APIAccessor.chores().currentId());
		*/
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/07
	 */
	@Override
	public List<Task> getTaskList(boolean __incsys)
	{
		throw new todo.TODO();
		/*
		// Lock so that the task list is always up to date
		Task[] rv;
		synchronized (this.lock)
		{
			IntegerArray tids = SystemCall.EASY.taskList(__incsys);
			int n = tids.length();
			
			// Wrap all the tasks
			rv = new Task[n];
			for (int i = 0; i < n; i++)
				rv[i] = this.__ofTask(new WrappedTask(tids.get(i)));
		}
		
		// Wrap array instead of creating a new list for speed
		return Arrays.<Task>asList(rv);
		*/
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/07
	 */
	@Override
	public void removeTaskListener(TaskListener __tl)
	{
		throw new todo.TODO();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/07
	 */
	@Override
	public boolean setForeground(Task __t)
		throws IllegalArgumentException
	{
		throw new todo.TODO();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/07
	 */
	@Override
	public boolean setPriority(Task __t, TaskPriority __p)
		throws IllegalArgumentException
	{
		throw new todo.TODO();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/29
	 */
	@Override
	public Task startTask(Suite __s, String __cn)
		throws IllegalArgumentException, IllegalStateException,
			NullPointerException
	{
		// Forward to the extended task start
		return this.startTask(__s, __cn, null, null, null, null);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/02/02
	 */
	@Override
	public final Task startTask(Suite __s, String __cn,
		Map<String, String> __sprops, String[] __args, OutputStream __stdout,
		OutputStream __stderr)
		throws IllegalArgumentException, IllegalStateException,
			NullPointerException
	{
		if (__s == null || __cn == null)
			throw new NullPointerException("NARG");
		
		// Defensive copies
		__args = (__args == null ? new String[0] : __args.clone());
		__sprops = (__sprops == null ? new HashMap<String, String>() :
			new HashMap<String, String>(__sprops));
		
		// Make sure values are actually valid
		for (int i = 0, n = __args.length; i < n; i++)
			if (__args[i] == null)
				throw new NullPointerException("NARG");
		for (Map.Entry<String, String> e : __sprops.entrySet())
		{
			String k = e.getKey(),
				v = e.getValue();
			if (k == null || v == null)
				throw new NullPointerException("NARG");
		}
		
		// {@squirreljme.error DG0v Cannot start a non-application suite.}
		if (__s.getSuiteType() != SuiteType.APPLICATION)
			throw new IllegalArgumentException("DG0v");
		
		// Get all the suites that are available, since we need to determine
		// dependencies and such
		List<Suite> all = __SystemSuiteManager__.__allSuites();
		
		// Determine the suites which need to be loaded into the classpath
		// in order to run the given suite
		Suite[] depends = __SystemSuiteManager__.__matchDependencies(
			__s.__suiteInfo().dependencies(), false);
		
		// Load suite names since we need to build the class path
		int n = depends.length;
		String[] names = new String[n + 1];
		for (int i = 0; i < n; i++)
			names[i] = depends[i]._name;
		
		// Add our boot suite to the last entry
		names[n] = __s._name;
		
		// Debug
		todo.DEBUG.note("Suites: %s", Arrays.<String>asList(names));
		
		throw Debugging.todo();
		/*
		// SquirrelJME 0.3.0 allows system properties and alternative output
		// streams to be specified as well
		int tid;
		if (ApiLevel.minimumLevel(ApiLevel.LEVEL_SQUIRRELJME_0_3_0_DEV))
		{
			// Copy system properties to key/value pair array
			int spn = __sprops.size();
			String[] xprops = new String[spn * 2];
			Iterator<Map.Entry<String, String>> eit = __sprops.entrySet().
				iterator();
			for (int i = 0, o = 0; i < spn; i++, o += 2)
			{
				Map.Entry<String, String> e = eit.next();
				
				xprops[o] = e.getKey();
				xprops[o + 1] = e.getValue();
			}
			
			// Forward launch
			tid = TaskAccess.startTask(names, __cn, __args, xprops,
				(__stdout == null ? null : new __CCWrapper__(__stdout)),
				(__stderr == null ? null : new __CCWrapper__(__stderr)));
		}
		else
			tid = TaskAccess.startTask(names, __cn, __args);
		
		// It did not work
		if (tid < 0)
		{
			// {@squirreljme.error DG0w Invalid entry point was specified
			// when starting task. (The entry point)}
			if (tid == TaskAccess.ERROR_INVALID_ENTRY)
				throw new IllegalArgumentException("DG0w " + __cn);
			
			// {@squirreljme.error DG0x Could not launch the task because of
			// an unspecified error. (The error)}
			throw new IllegalArgumentException("DG0x " + tid);
		}
		
		// Debug
		todo.DEBUG.note("Created task with TID %d", tid);
		
		// Otherwise use cached form
		return this.__getTask(tid, __s, __cn);
		 */
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/07
	 */
	@Override
	public boolean stopTask(Task __t)
		throws IllegalArgumentException, IllegalStateException
	{
		throw new todo.TODO();
	}
	
	/**
	 * Returns a task mapped to the given ID.
	 *
	 * @param __tid The task ID.
	 * @param __s The suite used.
	 * @param __n The starting class of the task, gives its name.
	 * @return The task for the task.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/04
	 */
	static final Task __getTask(int __tid, Suite __s, String __n)
		throws NullPointerException
	{
		if (__s == null || __n == null)
			throw new NullPointerException("NARG");
		
		// Lock on the tasks
		Map<Integer, Task> tasks = __SystemTaskManager__._TASKS;
		synchronized (tasks)
		{
			// There should not be duplicates
			Integer k = __tid;
			Task rv = tasks.get(k);
			if (rv != null)
				throw new todo.OOPS();
			
			tasks.put(k, (rv = new Task(__tid, __s, __n)));
			return rv;
		}
	}
}

