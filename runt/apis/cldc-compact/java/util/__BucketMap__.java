// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package java.util;

/**
 * This is a bucket map which acts as the raw internal hash table
 * implementation.
 *
 * @see HashMap
 * @see HashSet
 * @see LinkedHashMap
 * @see LinkedHashSet
 * @param <K> The key type.
 * @param <V> The value type.
 * @since 2018/10/07
 */
final class __BucketMap__<K, V>
{
	/** The default capacity. */
	static final int _DEFAULT_CAPACITY =
		16;
	
	/** The default load factor. */
	static final float _DEFAULT_LOAD =
		0.75F;
	
	/** The load factor. */
	protected final float loadfactor;
	
	/** The entry chains for each element. */
	__Entry__<K, V>[][] _buckets;
	
	/** The hashcode divisor for buckets. */
	int _bucketdiv;
	
	/** The number of elements in the map. */
	int _size;
	
	/** The current capacity. */
	int _capacity;
	
	/** The size threshold before a rebuild is done. */
	int _loadthreshold;
	
	/** Modification identifier. */
	int _modid;
	
	/**
	 * Initializes the map with the default capacity and load factor.
	 *
	 * @since 2018/10/07
	 */
	__BucketMap__()
	{
		this(__BucketMap__._DEFAULT_CAPACITY, __BucketMap__._DEFAULT_LOAD);
	}
	
	/**
	 * Initializes the map with the given capacity and the default load factor.
	 *
	 * @param __cap The capacity used.
	 * @throws IllegalArgumentException If the capacity is negative.
	 * @since 2018/10/07
	 */
	__BucketMap__(int __cap)
	{
		this(__cap, __BucketMap__._DEFAULT_LOAD);
	}
	
	/**
	 * Initializes the map with the given capacity and load factor.
	 *
	 * @param __cap The capacity used.
	 * @param __load The load factor used.
	 * @throws IllegalArgumentException If the capacity is negative or the
	 * load factor is not positive.
	 * @since 2018/10/07
	 */
	__BucketMap__(int __cap, float __load)
		throws IllegalArgumentException
	{
		// {@squirreljme.error ZZ2b The initial capacity of the map cannot be
		// negative.}
		if (__cap < 0)
			throw new IllegalArgumentException("ZZ2b");
		
		// {@squirreljme.error ZZ2c The load factor must be a positive value.}
		if (__load <= 0.0F)
			throw new IllegalArgumentException("ZZ2c");
		
		this.loadfactor = __load;
		this._buckets = __BucketMap__.<K, V>__newBucket(__cap);
		this._bucketdiv = __cap;
		this._capacity = __cap;
		this._loadthreshold = (int)(__cap * __load);
	}
	
	/**
	 * Gets the entry for the given key.
	 *
	 * @param __k The key to get.
	 * @return The entry for the given or {@code null} if none exists.
	 * @since 2018/10/08
	 */
	public final __BucketMap__.__Entry__<K, V> get(Object __k)
	{
		// Where to look in the table?
		int hash = (__k == null ? 0 : __k.hashCode());
		int div = (hash & 0x7FFF_FFFF) % this._bucketdiv;
		
		// If the chain does not exist then do not bother at all
		__Entry__<K, V>[] chain = this._buckets[div];
		if (chain == null)
			return null;
		
		// Go through the chain and find the matching entry
		for (__Entry__<K, V> e : chain)
		{
			// Ignore blank entries
			if (e == null)
				continue;
			
			// Has the wrong hashcode
			if (hash != e._keyhash)
				continue;
			
			// If the objects actually match, it is found
			if (Objects.equals(e._key, __k))
				return e;
		}
		
		// Not found
		return null;
	}
	
	/**
	 * Returns the entry set for the map.
	 *
	 * @return The entry set for the map.
	 * @since 2018/10/27
	 */
	public final Set<__BucketMap__.__Entry__<K, V>> entrySet()
	{
		return new __MapEntrySet__<K, V>(this);
	}
	
	/**
	 * Returns the chain that the hashed object is within for the bucket.
	 *
	 * @param __k The key.
	 * @return The key for the given entry.
	 * @since 2018/10/07
	 */
	public final __BucketMap__.__Entry__<K, V> put(K __k)
	{
		__Entry__<K, V>[][] buckets = this._buckets;
		int bucketdiv = this._bucketdiv;
		
		// Used to determine if we rebuild
		int size = this._size,
			nextsize = size + 1;
		
		// Hypothetically putting a new entry could cause the threshold to be
		// hit, so just in this case a new entry would be put so rebuild
		// the hash table.
		if (nextsize >= this._loadthreshold)
		{
			todo.TODO.note("Re-balance bucket map.");
			if (false)
				throw new todo.TODO();
		}
		
		// Where to look in the table?
		int hash = (__k == null ? 0 : __k.hashCode());
		int div = (hash & 0x7FFF_FFFF) % bucketdiv;
		
		// This will be set depending on the situation
		__Entry__<K, V> rv;
		
		// No entries exist in the chain, we can just create one
		__Entry__<K, V>[] chain = buckets[div];
		if (chain == null)
		{
			// Setup chain
			chain = __BucketMap__.<K, V>__newChain(1);
			buckets[div] = chain;
			
			// Fill
			chain[0] = (rv = new __Entry__<K, V>(__k));
			return rv;
		}
		
		// Go through and find if there was a pre-existing item
		int nulldx = -1,
			n = chain.length;
		for (int i = 0; i < n; i++)
		{
			__Entry__<K, V> e = chain[i];
			
			// If no entry is here remember this blank spot in the event
			// nothing is ever found
			if (e == null)
			{
				if (nulldx < 0)
					nulldx = i;
				continue;
			}
			
			// Has the wrong hashcode
			if (hash != e._keyhash)
				continue;
			
			// If the objects actually match, it is found
			if (Objects.equals(__k, e._key))
				return e;
		}
		
		// Found a blank spot, we can just put the entry here
		if (nulldx >= 0)
			chain[nulldx] = (rv = new __Entry__<K, V>(__k));
		
		// Otherwise, increase the chain and use that instead
		else
		{
			// Copy the old chain over
			__Entry__<K, V>[] dup = __BucketMap__.<K, V>__newChain(n + 1);
			for (int i = 0; i < n; i++)
				dup[i] = chain[i];
			
			// Set at end
			dup[n] = (rv = new __Entry__<K, V>(__k));
			
			// Use this chain again
			buckets[div] = dup;
		}
		
		// Map has been modified
		this._modid++;
		
		// Size would have been increased at this point
		this._size = nextsize;
		
		return rv;
	}
	
	/**
	 * Returns the number of entries in the map.
	 *
	 * @return The number of entries in the map.
	 * @since 2018/10/08
	 */
	public final int size()
	{
		return this._size;
	}
	
	/**
	 * Creates a new bucket array.
	 *
	 * @param <K> Key type.
	 * @param <V> Value type.
	 * @param __n The length.
	 * @return The array.
	 * @since 2018/10/08
	 */
	@SuppressWarnings({"unchecked"})
	private static <K, V> __Entry__<K, V>[][] __newBucket(int __n)
	{
		return (__Entry__<K, V>[][])((Object)new __Entry__[__n][]);
	}
	
	/**
	 * Creates a new chain array.
	 *
	 * @param <K> Key type.
	 * @param <V> Value type.
	 * @param __n The length.
	 * @return The array.
	 * @since 2018/10/08
	 */
	@SuppressWarnings({"unchecked"})
	private static <K, V> __Entry__<K, V>[] __newChain(int __n)
	{
		return (__Entry__<K, V>[])((Object)new __Entry__[__n]);
	}
	
	/**
	 * This represents a single entry within the map.
	 *
	 * @param <K> The key type.
	 * @param <V> The value type.
	 * @since 2018/10/07
	 */
	static final class __Entry__<K, V>
		implements Map.Entry<K, V>
	{
		/** The key. */
		final K _key;
		
		/** The key hashcode. */
		final int _keyhash;
		
		/** The value here. */
		V _value;		
		
		/**
		 * Initializes the entry.
		 *
		 * @param __k The key.
		 * @since 2018/10/08
		 */
		__Entry__(K __k)
		{
			this._key = __k;
			this._keyhash = (__k == null ? 0 : __k.hashCode());
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2018/10/08
		 */
		@Override
		public final boolean equals(Object __o)
		{
			if (__o == this)
				return true;
			
			if (!(__o instanceof Map.Entry))
				return false;
			
			Map.Entry<?, ?> o = (Map.Entry<?, ?>)__o;
			return Objects.equals(this._key, o.getKey()) &&
				Objects.equals(this._value, o.getValue());
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2018/10/08
		 */
		@Override
		public final K getKey()
		{
			return this._key;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2018/10/08
		 */
		@Override
		public final V getValue()
		{
			return this._value;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2018/10/08
		 */
		@Override
		public final int hashCode()
		{
			throw new todo.TODO();
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2018/10/08
		 */
		@Override
		public final V setValue(V __v)
		{
			V rv = this._value;
			this._value = __v;
			return rv;
		}
	}
	
	/**
	 * Iterator over the entries in this map.
	 *
	 * @since 2018/10/13
	 */
	final class __EntryIterator__
		implements Iterator<__BucketMap__.__Entry__<K, V>>
	{
		/** The mod init this iterator is at, to detect modifications. */
		int _atmodinit =
			__BucketMap__.this._modid;
		
		/** The current bucket this is at. */
		int _bucketat;
		
		/** The current chain link this is at. */
		int _chainat;
		
		/** The cached next entry. */
		__BucketMap__.__Entry__<K, V> _next;
		
		/**
		 * {@inheritDoc}
		 * @since 2018/10/13
		 */
		@Override
		public final boolean hasNext()
		{
			// Check modification
			this.__checkModified();
			
			// Already cached, do not need to check anything more
			if (this._next != null)
				return true;
			
			// No more buckets remain?
			int bucketat = this._bucketat,
				bucketdiv = __BucketMap__.this._bucketdiv;
			if (bucketat >= bucketdiv)
				return false;
			
			// Get the current chain
			__Entry__<K, V>[][] buckets = __BucketMap__.this._buckets;
			
			// We can store the current location parameters at the end rather
			// than every time (keeps everything in locals)
			int chainat = this._chainat;
			try
			{
				// We might try looking at the next bucket if we reach the end
				// of this chain.
				for (;;)
				{
					__Entry__<K, V>[] chain = buckets[bucketat];
					
					// No more chain links remain? Or there is no chain?
					int chaindiv = (chain == null ? -1 : chain.length);
					if (chainat >= chaindiv)
					{
						// Reset to start of next bucket
						bucketat++;
						chainat = 0;
						
						// No more buckets to look in
						if (bucketat >= bucketdiv)
							return false;
						
						// Try again
						continue;
					}
					
					// Will use the next chain
					int oldchainat = chainat++;
					
					// If no link was here try again
					__Entry__<K, V> link = chain[oldchainat];
					if (link == null)
						continue;
					
					// Cache that link for returning
					this._next = link;
					return true;
				}
			}
			
			// Store properties
			finally
			{
				this._bucketat = bucketat;
				this._chainat = chainat;
			}
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2018/10/13
		 */
		@Override
		public final __BucketMap__.__Entry__<K, V> next()
			throws NoSuchElementException
		{
			// {@squirreljme.error ZZ2p Map has no more entries remaining.}
			if (!this.hasNext())
				throw new NoSuchElementException("ZZ2p");
			
			// hasNext() caches this
			__BucketMap__.__Entry__<K, V> rv = this._next;
			this._next = null;
			return rv;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2018/10/13
		 */
		@Override
		public final void remove()
		{
			// Check modification
			this.__checkModified();
			
			throw new todo.TODO();
		}
		
		/**
		 * Checks if the map's internal structure modification count has
		 * changed.
		 *
		 * @throws ConcurrentModificationException If the map was modified.
		 * @since 2018/10/13
		 */
		private final void __checkModified()
			throws ConcurrentModificationException
		{
			// {@squirreljme.error ZZ2o Backing map has been modified.}
			if (this._atmodinit != __BucketMap__.this._modid)
				throw new ConcurrentModificationException("ZZ2o");
		}
	}
}

