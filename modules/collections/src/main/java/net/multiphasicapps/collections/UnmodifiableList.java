// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.collections;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * This is a list which cannot be modified.
 *
 * @param <V> The type of value the list stores.
 * @since 2016/03/03
 */
public abstract class UnmodifiableList<V>
	extends AbstractList<V>
{
	/** The list to wrap. */
	protected final List<V> wrapped;	
	
	/**
	 * Initializes the list which cannot be modified.
	 *
	 * @param __l The list to wrap.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/03/03
	 */
	private UnmodifiableList(List<V> __l)
		throws NullPointerException
	{
		// Check
		if (__l == null)
			throw new NullPointerException("NARG");
		
		// Set
		this.wrapped = __l;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/05/12
	 */
	@Override
	public boolean contains(Object __o)
	{
		return this.wrapped.contains(__o);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/03/03
	 */
	@Override
	public final V get(int __i)
	{
		return this.wrapped.get(__i);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/05/12
	 */
	@Override
	public int indexOf(Object __o)
	{
		return this.wrapped.indexOf(__o);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/05/12
	 */
	@Override
	public boolean isEmpty()
	{
		return this.wrapped.isEmpty();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/05/12
	 */
	@Override
	public int lastIndexOf(Object __o)
	{
		return this.wrapped.lastIndexOf(__o);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/03/03
	 */
	@Override
	public final int size()
	{
		return this.wrapped.size();
	}
	
	/**
	 * This creates a view of the specified list which cannot be modified.
	 *
	 * @param <V> The type of value stored in the list.
	 * @param __l The list to view.
	 * @return An unmodifiable view of the list.
	 * @since 2016/03/03
	 */
	public static <V> UnmodifiableList<V> of(List<V> __l)
	{
		// If already one, return it
		if (__l instanceof UnmodifiableList)
			return (UnmodifiableList<V>)__l;
		
		// Wrap, make sure that if the list being wrapped can be randomly
		// accessed that it also carries the RandomAccess interface.
		if (__l instanceof RandomAccess)
			return new UnmodifiableList.__Random__<V>(__l);
		return new UnmodifiableList.__Sequential__<V>(__l);
	}
	
	/**
	 * Wraps a list iterator so that it cannot have modifications.
	 *
	 * @param <V> The type of value in the list.
	 * @since 2016/05/12
	 */
	private static final class __ListIterator__<V>
		implements ListIterator<V>
	{
		/** The list iterator to wrap. */
		protected final ListIterator<V> li;
		
		/**
		 * Initializes the wrapped list iterator.
		 *
		 * @param __li The iterator to wrap.
		 * @throws NullPointerException On null arguments.
		 * @since 2016/05/12
		 */
		private __ListIterator__(ListIterator<V> __li)
			throws NullPointerException
		{
			// Check
			if (__li == null)
				throw new NullPointerException("NARG");
			
			// Set
			this.li = __li;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2016/05/12
		 */
		@Override
		public void add(V __e)
		{
			throw new UnsupportedOperationException("RORO");
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2016/05/12
		 */
		@Override
		public boolean hasNext()
		{
			return this.li.hasNext();
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2016/05/12
		 */
		@Override
		public boolean hasPrevious()
		{
			return this.li.hasPrevious();
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2016/05/12
		 */
		@Override
		public V next()
		{
			return this.li.next();
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2016/05/12
		 */
		@Override
		public int nextIndex()
		{
			return this.li.nextIndex();
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2016/05/12
		 */
		@Override
		public V previous()
		{
			return this.li.previous();
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2016/05/12
		 */
		@Override
		public int previousIndex()
		{
			return this.li.previousIndex();
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2016/05/12
		 */
		@Override
		public void remove()
		{
			throw new UnsupportedOperationException("RORO");
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2016/05/12
		 */
		@Override
		public void set(V __v)
		{
			throw new UnsupportedOperationException("RORO");
		}
	}
	
	/**
	 * This is a list which implements {@link RandomAccess} so that the sort
	 * and search operations do not result in an entire copy of the list
	 * before the operation is performed.
	 *
	 * @param <V> The type to contain.
	 * @since 2016/04/28
	 */
	private static final class __Random__<V>
		extends UnmodifiableList<V>
		implements RandomAccess
	{
		/**
		 * Initializes the random access list.
		 *
		 * @param __l The list to wrap.
		 * @since 2016/04/28
		 */
		private __Random__(List<V> __l)
		{
			super(__l);
		}
	}
	
	/**
	 * This is a list which does not implement {@link RandomAccess} and as
	 * such when sort or binary search is done, an intermediate array is used
	 * in place.
	 *
	 * @param <V> The type to contain.
	 * @since 2016/04/28
	 */
	private static final class __Sequential__<V>
		extends UnmodifiableList<V>
	{
		/**
		 * Initializes the sequential access list.
		 *
		 * @param __l The list to wrap.
		 * @since 2016/04/28
		 */
		private __Sequential__(List<V> __l)
		{
			super(__l);
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2016/05/12
		 */
		@Override
		public Iterator<V> iterator()
		{
			return this.listIterator();
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2016/05/12
		 */
		@Override
		public ListIterator<V> listIterator()
		{
			return this.listIterator(0);
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2016/05/12
		 */
		@Override
		public ListIterator<V> listIterator(int __i)
		{
			return new __ListIterator__<V>(this.wrapped.listIterator(__i));
		}
	}
}

