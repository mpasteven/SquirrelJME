// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package java.util;

/**
 * The list iterator for this linked list.
 *
 * @param <E> The types of values to store.
 * @since 2018/10/29
 */
final class __LinkedListListIterator__<E>
	implements ListIterator<E>
{
	/** The owning linked list. */
	protected final LinkedList<E> list;
	
	/** The virtualized index for the list (estimated). */
	private int _vdx;
	
	/** Last virtualized index. */
	private int _lastvdx;
	
	/** The current link the list is at. */
	private __Link__<E> _next;
	
	/** The last element, for removal or setting */
	private __Link__<E> _last;
	
	/** The current modification count, to detect modifications. */
	private int _atmod;
	
	/**
	 * Initializes the iterator starting at the given index.
	 *
	 * @param __list The linked list this interfaces with.
	 * @param __i The index to start at.
	 * @throws IndexOutOfBoundsException If the index is outside of the
	 * list bounds.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/10/29
	 */
	__LinkedListListIterator__(LinkedList<E> __list, int __i)
		throws IndexOutOfBoundsException, NullPointerException
	{
		if (__list == null)
			throw new NullPointerException("NARG");
		
		int size = __list._size;
		if (__i < 0 || __i > size)
			throw new IndexOutOfBoundsException("IOOB");
		
		// Closer to the start of the list
		__Link__<E> rover;
		if (__i < (size >> 1))
		{
			rover = __list._head;
			
			for (int i = -1; i < __i; i++)
				rover = rover._next;
		}
		
		// Closer to the end of the list
		else
		{
			rover = __list._tail;
			
			for (int i = size; i > __i; i--)
				rover = rover._prev;
		}
		
		// Store start information
		this.list = __list;
		this._next = rover;
		this._vdx = __i;
		this._atmod = __list.modCount;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/29
	 */
	@Override
	public final void add(E __v)
	{
		// Check modification
		this.__checkConcurrent();
		
		// These will both be adjusted
		int vdx = this._vdx;
		__Link__<E> next = this._next;
		
		// The documentation specifies that the object is inserted
		// before the implicit cursor, which means the next call to
		// previous will return the new element. Next would just return
		// the same element no matter how many elements are added.
		// So it is inserted between the previous and our current next
		new __Link__<E>(next._prev, __v, next);
		
		// Since we inserted an item, vdx goes up
		this._vdx++;
		
		// Size also goes up
		this.list._size++;
		
		// Set list as being modified and update our count to match
		this._atmod = ++this.list.modCount;
		
		// Cannot remove or set
		this._last = null;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/29
	 */
	@Override
	public final boolean hasNext()
	{
		// Check modification
		this.__checkConcurrent();
		
		// There is a next as long as the current element is before the
		// size
		return this._vdx < this.list._size;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/29
	 */
	@Override
	public final boolean hasPrevious()
	{
		// Check modification
		this.__checkConcurrent();
		
		// There is a previous as long as this is not the first element
		return this._vdx > 0;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/28
	 */
	@Override
	public final E next()
		throws NoSuchElementException
	{
		// Check modification
		this.__checkConcurrent();
		
		// We are at the tail node, do nothing
		__Link__<E> next = this._next;
		if (next == this.list._tail)
			throw new NoSuchElementException("NSEE");
		
		// Removal and set can be done on this
		this._last = next;
		
		// Iterate and record for the next element
		this._lastvdx = this._vdx++;
		this._next = next._next;
		
		// Return the current value
		return next._value;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/29
	 */
	@Override
	public final int nextIndex()
	{
		// Check modification
		this.__checkConcurrent();
		
		// Virtual index should match this one
		return this._vdx;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/29
	 */
	@Override
	public final E previous()
	{
		// Check modification
		this.__checkConcurrent();
		
		// We are at the head node, do nothing
		__Link__<E> next = this._next;
		if (next._prev == this.list._head)
			throw new NoSuchElementException("NSEE");
		
		// Move to previous
		__Link__<E> prev = next._prev;
		
		// Removal is done on this
		this._last = prev;
		
		// Move index back
		this._lastvdx = --this._vdx;
		this._next = prev;
		
		// Use previous value
		return prev._value;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/29
	 */
	@Override
	public final int previousIndex()
	{
		// Check modification
		this.__checkConcurrent();
		
		// Should be the previous virtual index
		return this._vdx - 1;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/29
	 */
	@Override
	public final void remove()
		throws IllegalStateException
	{
		// Check modification
		this.__checkConcurrent();
		
		// {@squirreljme.error ZZ2u Cannot remove the element from the
		// linked list because there was no previous call to next or
		// previous, or add was called.}
		__Link__<E> last = this._last;
		if (last == null)
			throw new IllegalStateException("ZZ2u");
		
		// Only removed once
		this._last = null;
		
		// Just link the previous and next entries to the others and
		// drop this link
		last._prev._next = last._next;
		last._next._prev = last._prev;
		
		// If the next entry is after this point then it will needs its
		// index dropped
		int vdx = this._vdx,
			lastvdx = this._lastvdx;
		if (vdx > lastvdx)
			this._vdx = vdx - 1;
		
		// Set list as being modified and update our count to match
		this._atmod = ++this.list.modCount;
		
		// Size goes down
		this.list._size--;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/29
	 */
	@Override
	public final void set(E __v)
		throws IllegalStateException
	{
		// Check modification
		this.__checkConcurrent();
		
		// {@squirreljme.error ZZ2v Cannot set the element from the
		// linked list because there was no previous call to next or
		// previous, or add was called.}
		__Link__<E> last = this._last;
		if (last == null)
			throw new IllegalStateException("ZZ2v");
		
		// Just set it
		last._value = __v;
	}
	
	/**
	 * Checks if the list was concurrently modified.
	 *
	 * @throws ConcurrentModificationException If it was modified.
	 * @since 2018/10/29
	 */
	private final void __checkConcurrent()
		throws ConcurrentModificationException
	{
		// {@squirreljme.error ZZ2w List has been concurrently modified.}
		if (this._atmod != this.list.modCount)
		{
			// Just empty these out so they are never used again
			this._next = null;
			this._last = null;
			
			// Fail
			throw new ConcurrentModificationException("ZZ2w");
		}
	}
}
