// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.springcoat;

/**
 * Represents a callback thread.
 *
 * @since 2020/09/15
 */
public class CallbackThread
	implements AutoCloseable
{
	/** The thread to execute under. */
	private final SpringThread _thread;
	
	/** The number of times this has been opened. */
	private int _openCount;
	
	/** The thread this was opened by. */
	private Thread _openBy;
	
	/**
	 * Initializes the callback thread.
	 * 
	 * @param __thread The thread to execute under.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/09/20
	 */
	public CallbackThread(SpringThread __thread)
		throws NullPointerException
	{
		if (__thread == null)
			throw new NullPointerException("NARG");
		
		this._thread = __thread;
	}
	
	/**
	 * Checks if this thread can be opened.
	 * 
	 * @return If this can be opened.
	 * @since 2020/09/20
	 */
	public final boolean canOpen()
	{
		Thread current = Thread.currentThread();
		synchronized (this)
		{
			return this._openBy == current || this._openCount == 0;
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @throws IllegalStateException If not open by this thread.
	 * @since 2020/09/15
	 */
	@Override
	public final void close()
		throws IllegalStateException
	{
		Thread currentThread = Thread.currentThread();
		
		synchronized (this)
		{
			if (this._openBy != currentThread)
				throw new IllegalStateException("Closed by wrong thread.");
			
			// Close it out and clear if count is now zero
			if (--this._openCount == 0)
				this._openBy = null;
		}
	}
	
	/**
	 * Opens the thread.
	 * 
	 * @throws IllegalStateException If this thread is already open.
	 * @since 2020/09/19
	 */
	public final void open()
		throws IllegalStateException
	{
		Thread current = Thread.currentThread();
		synchronized (this)
		{
			// Can only be opened by the same thread, or otherwise if not
			// claimed at all
			Thread openBy = this._openBy;
			if (openBy != null && openBy != current)
				throw new IllegalStateException("Opened by other thread.");
			
			// Mark as open
			this._openCount++;
			this._openBy = current;
		}
	}
	
	/**
	 * Returns the thread to invoke on.
	 * 
	 * @return The used thread.
	 * @throws IllegalStateException If the thread is not owned or opened by
	 * the current thread.
	 * @since 2020/09/15
	 */
	public final SpringThread thread()
		throws IllegalStateException
	{
		synchronized (this)
		{
			if (this._openCount <= 0 || Thread.currentThread() != this._openBy)
				throw new IllegalStateException("Not owned by thread.");
			
			return this._thread;
		}
	}
}
