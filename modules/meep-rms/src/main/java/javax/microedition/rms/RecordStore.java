// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package javax.microedition.rms;

import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.runtime.rms.SuiteIdentifier;
import cc.squirreljme.runtime.rms.TemporaryVinylRecord;
import cc.squirreljme.runtime.rms.VinylLock;
import cc.squirreljme.runtime.rms.VinylRecord;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.multiphasicapps.collections.IdentityLinkedHashSet;

/**
 * This is a record store which may be used by an application to store
 * information about it in an implementation defined manner.
 *
 * Opened record stores have an open count, as such for every open operation
 * there must be a close operation.
 *
 * Whenever the record store is modified, the version number will be
 * incremented.
 *
 * Record stores may optionally permit other suites to access and potentially
 * write their records, otherwise only the current suite may modify its own
 * records.
 *
 * @since 2017/02/26
 */
public class RecordStore
	implements AutoCloseable
{
	/** Specifies that any suite may access the records. */
	public static final int AUTHMODE_ANY =
		1;
	
	/** Specifies that only record store creator may access the records. */
	public static final int AUTHMODE_PRIVATE =
		0;
	
	/** The vinyl record where everything is stored. */
	static final VinylRecord _VINYL;
	
	/** Existing record stores. */
	static final Map<Integer, RecordStore> _STORE_CACHE =
		new LinkedHashMap<>();
	
	/** Identity map for listeners */
	private final Set<RecordListener> _listeners =
		new IdentityLinkedHashSet<>();
	
	/** The volume ID. */
	private final int _vid;
	
	/** The name. */
	private final String _name;
	
	/** Write to this? */
	private final boolean _write;
	
	/** How many times has this been opened? */
	private volatile int _opens;
	
	/**
	 * Initializes the record store manager.
	 *
	 * @since 2017/02/27
	 */
	static
	{
		// See if there is a service, this will fall back to an implementation
		// that is not shared and will only exist as long as the current
		// program is running
		VinylRecord vr;
		try
		{
			Debugging.todoNote("Implement storage backed RMS.");
			String vclass = null;//Debugging.<String>todoObject();
			vr = (vclass == null ? new TemporaryVinylRecord() :
				(VinylRecord)Class.forName(vclass).newInstance());
		}
		
		// If it fails to initialize, just use a blank one
		catch (ClassNotFoundException|IllegalAccessException|
			InstantiationException e)
		{
			vr = new TemporaryVinylRecord();
		}
		
		// Set
		_VINYL = vr;
	}
	
	/**
	 * Initializes the access to the record store.
	 *
	 * @param __vid The volume ID.
	 * @param __name The name.
	 * @param __w Write to this?
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/14
	 */
	private RecordStore(int __vid, String __name, boolean __w)
		throws NullPointerException
	{
		if (__name == null)
			throw new NullPointerException("NARG");
		
		this._vid = __vid;
		this._name = __name;
		this._write = __w;
		this._opens = 1;
	}
	
	/**
	 * Adds the specified record to the record store and returns the record
	 * id for the newly added record.
	 *
	 * @param __b The data to store.
	 * @param __o The offset into the array.
	 * @param __l The length of the array.
	 * @param __tag The tag to identify the given record with.
	 * @return The record ID of the newly created record.
	 * @throws ArrayIndexOutOfBoundsException If the offset and/or length
	 * are negative or exceed the array bounds.
	 * @throws NullPointerException On null arguments.
	 * @throws RecordStoreNotOpenException If the record store is not open.
	 * @throws RecordStoreException If some other error occurs.
	 * @throws RecordStoreFullException If there is not enough space to store
	 * the data.
	 * @since 2017/02/26
	 */
	public int addRecord(byte[] __b, int __o, int __l, int __tag)
		throws ArrayIndexOutOfBoundsException, NullPointerException,
			RecordStoreNotOpenException, RecordStoreException,
			RecordStoreFullException
	{
		if (__b == null)
			throw new NullPointerException("NARG");
		if (__o < 0 || __l < 0 || (__o + __l) > __b.length)
			throw new ArrayIndexOutOfBoundsException("IOOB");
		
		// {@squirreljme.error DC01 Cannot write record to read-only store.}
		if (!this._write)
			throw new RecordStoreException("DC01");
		
		// Used for later
		int rv;
		RecordListener[] listeners = this.__listeners();
		
		// Lock
		VinylRecord vinyl = RecordStore._VINYL;
		try (VinylLock lock = vinyl.lock())
		{
			// Check open
			this.__checkOpen();
			
			// Add the page
			rv = vinyl.pageAdd(this._vid, __b, __o, __l, __tag);
			RecordStore.__checkError(rv);
		}
		
		// Report to the listeners
		for (RecordListener l : listeners)
			l.recordAdded(this, rv);
		return rv;
	}
	
	/**
	 * Calls {@code addRecord(__b, __o, __l, 0)}.
	 *
	 * @param __b As forwarded.
	 * @param __o As forwarded.
	 * @param __l As forwarded.
	 * @return As forwarded.
	 * @throws ArrayIndexOutOfBoundsException As forwarded.
	 * @throws NullPointerException As forwarded.
	 * @throws RecordStoreNotOpenException As forwarded.
	 * @throws RecordStoreException As forwarded.
	 * @throws RecordStoreFullException As forwarded.
	 * @since 2017/02/26
	 */
	public int addRecord(byte[] __b, int __o, int __l)
		throws ArrayIndexOutOfBoundsException, NullPointerException,
			RecordStoreNotOpenException, RecordStoreException,
			RecordStoreFullException
	{
		return this.addRecord(__b, __o, __l, 0);
	}
	
	/**
	 * Adds a record listener to the given store to notify of when changes
	 * are made to records.
	 *
	 * If the record store is closed then this has no effect.
	 *
	 * @param __l The listener to call for changes, a listener which has
	 * already been added will not be added a second time.
	 * @since 2017/02/26
	 */
	public void addRecordListener(RecordListener __l)
	{
		// Ignore
		if (__l == null)
			return;
		
		// Lock
		VinylRecord vinyl = RecordStore._VINYL;
		try (VinylLock lock = vinyl.lock())
		{
			// No effect if closed
			if (this._opens <= 0)
				return;
			
			// Add listener
			Set<RecordListener> listeners = this._listeners;
			synchronized (listeners)
			{
				listeners.add(__l);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/02/26
	 */
	@Override
	public void close()
		throws RecordStoreNotOpenException, RecordStoreException
	{
		this.closeRecordStore();
	}
	
	/**
	 * Closes the record store.
	 *
	 * Note that due to the ability to have record stores opened multiple times
	 * the open count must reach zero before it is actually closed.
	 *
	 * When the store is fully closed all listeners will be removed.
	 *
	 * @throws RecordStoreNotOpenException If the record is not open.
	 * @throws RecordStoreException If there was an issue closing it.
	 * @since 2017/02/26
	 */
	public void closeRecordStore()
		throws RecordStoreNotOpenException, RecordStoreException
	{
		// Lock the record, so that only a single thread is messing with the
		// open counts and such
		VinylRecord vinyl = RecordStore._VINYL;
		try (VinylLock lock = vinyl.lock())
		{
			// Check open
			this.__checkOpen();
			
			// If closed then remove all the listeners
			if ((--this._opens) <= 0)
				this._listeners.clear();
		}
	}
	
	/**
	 * Deletes the specified record.
	 *
	 * @param __id The record to delete.
	 * @throws InvalidRecordIDException If the record ID is not valid.
	 * @throws RecordStoreNotOpenException If the record store is not open.
	 * @throws RecordStoreException If there was an issue deleting the record.
	 * @throws SecurityException If the record cannot be deleted.
	 * @since 2017/02/26
	 */
	public void deleteRecord(int __id)
		throws InvalidRecordIDException, RecordStoreNotOpenException,
			RecordStoreException, SecurityException
	{
		// Used later
		RecordListener[] listeners = this.__listeners();
		
		// Lock
		VinylRecord vinyl = RecordStore._VINYL;
		try (VinylLock lock = vinyl.lock())
		{
			// Check open
			this.__checkOpen();
			
			// Delete it
			int rv = vinyl.pageDelete(this._vid, __id);
			RecordStore.__checkError(rv);
		}
		
		// Report to the listeners
		for (RecordListener l : listeners)
			l.recordDeleted(this, __id);
	}
	
	/**
	 * Enumerates through the records that exist within this store.
	 *
	 * If a comparator is not specified then the traversal order is not
	 * defined.
	 *
	 * @param __f An optional filter used to filter records, may be
	 * {@code null}.
	 * @param __c An optional comparator used to modify the sort order, may
	 * be {@code null}.
	 * @param __ku If {@code true} then the enumeration is kept up to date.
	 * @param __tags The tags to use for basic filtering, if this is empty then
	 * an empty enumeration will be returned, if this is {@code null} then all
	 * tags will be selected.
	 * @return The enumeration over the records.
	 * @throws RecordStoreNotOpenException If this record store is not open.
	 * @since 2017/02/26
	 */
	public RecordEnumeration enumerateRecords(RecordFilter __f,
		RecordComparator __c, boolean __ku, int[] __tags)
		throws RecordStoreNotOpenException
	{
		// Check open
		this.__checkOpen();
		
		// Build one and perform a rebuild to initialize it
		__VolumeEnumeration__ rv = new __VolumeEnumeration__(this, __f,
			__c, __ku, __tags);
		rv.rebuild();
		
		// Use it
		return rv;
	}
	
	/**
	 * Calls {@code enumerateRecords(__f, __c, __ku, null)}.
	 *
	 * @param __f As forwarded.
	 * @param __c As forwarded.
	 * @param __ku As forwarded.
	 * @return As forwarded.
	 * @throws RecordStoreNotOpenException As forwarded.
	 * @since 2017/02/26
	 */
	public RecordEnumeration enumerateRecords(RecordFilter __f,
		RecordComparator __c, boolean __ku)
		throws RecordStoreNotOpenException
	{
		return this.enumerateRecords(__f, __c, __ku, null);
	}
	
	/**
	 * Returns the last modification date of the record store.
	 *
	 * @return The last modification date of the record store.
	 * @throws RecordStoreNotOpenException If this record store is not open.
	 * @since 2017/02/26
	 */
	public long getLastModified()
		throws RecordStoreNotOpenException
	{
		// Check open
		this.__checkOpen();
		
		// Lock
		VinylRecord vinyl = RecordStore._VINYL;
		try (VinylLock lock = vinyl.lock())
		{
			// Check open
			this.__checkOpen();
			
			long[] time = new long[1];
			int rv = vinyl.volumeModTime(this._vid, time);
			
			try
			{
				RecordStore.__checkError(rv);
			}
			catch (RecordStoreException e)
			{
				if (e instanceof RecordStoreNotOpenException)
					throw (RecordStoreNotOpenException)e;
				
				// {@squirreljme.error DC02 Could not get the record store
				// time.}
				throw new RuntimeException("DC02", e);
			}
			
			return time[0];
		}
	}
	
	/**
	 * Returns the name of the record store.
	 *
	 * @return The name of the record store.
	 * @throws RecordStoreNotOpenException If this record store is not open.
	 * @since 2017/02/26
	 */
	public String getName()
		throws RecordStoreNotOpenException
	{
		// Check open
		this.__checkOpen();
		
		return this._name;
	}
	
	/**
	 * This returns the next record ID which would be used if a new record
	 * were to be added to this record store.
	 *
	 * The returned ID is only valid while the store remains open and before
	 * {@code addRecord()} is called.
	 *
	 * @return The next record ID.
	 * @throws RecordStoreException If there was another issue with the
	 * record store.
	 * @throws RecordStoreNotOpenException If this record store is not open.
	 * @since 2017/02/26
	 */
	public int getNextRecordID()
		throws RecordStoreException, RecordStoreNotOpenException
	{
		// Lock
		VinylRecord vinyl = RecordStore._VINYL;
		try (VinylLock lock = vinyl.lock())
		{
			// Check open
			this.__checkOpen();
			
			// Get next ID as a guess
			int rv = vinyl.pageNextId(this._vid);
			RecordStore.__checkError(rv);
			
			return rv;
		}
	}
	
	/**
	 * Returns the number of records in this store.
	 *
	 * @return The number of records in this store.
	 * @throws RecordStoreNotOpenException If the record store is not open.
	 * @since 2019/05/09
	 */
	public int getNumRecords()
		throws RecordStoreNotOpenException
	{
		// Check open
		this.__checkOpen();
		
		// Lock
		VinylRecord vinyl = RecordStore._VINYL;
		try (VinylLock lock = vinyl.lock())
		{
			// Get record list
			int[] pages = vinyl.pageList(this._vid);
			
			// Check for error
			if (pages.length > 0)
				try
				{
					RecordStore.__checkError(pages[0]);
				}
				catch (RecordStoreNotOpenException e)
				{
					throw e;
				}
				catch (RecordStoreException e)
				{
					// {@squirreljme.error DC03 Error getting list of
					// records.}
					RecordStoreNotOpenException t =
						new RecordStoreNotOpenException("DC03");
					t.initCause(e);
					throw t;
				}
			
			// Return array size
			return pages.length;
		}
	}
	
	/**
	 * Returns a copy of the data which is stored in the given record.
	 *
	 * @param __id The ID of the record to get.
	 * @return A copy of the data stored in this record, if there is no data
	 * then this will return {@code null}.
	 * @throws InvalidRecordIDException If the ID is not valid.
	 * @throws RecordStoreException If there is a problem with the record
	 * store.
	 * @throws RecordStoreNotOpenException If this record store is not open.
	 * @since 2017/02/26
	 */
	public byte[] getRecord(int __id)
		throws InvalidRecordIDException, RecordStoreException,
			RecordStoreNotOpenException
	{
		// Check open
		this.__checkOpen();
		
		// This volume
		int vid = this._vid;
		
		// Lock
		VinylRecord vinyl = RecordStore._VINYL;
		try (VinylLock lock = vinyl.lock())
		{
			// Need to know the size of the record
			int size = vinyl.pageSize(vid, __id);
			RecordStore.__checkError(size);
			
			// Allocate data to read from it
			byte[] rv = new byte[size];
			
			// Read data
			int read = vinyl.pageRead(vid, __id, rv, 0, size);
			RecordStore.__checkError(read);
			
			return rv;
		}
	}
	
	/**
	 * Fills the specified array with a copy of the data within the given
	 * record.
	 *
	 * @param __id The ID of the record to get.
	 * @param __b The array to write data to.
	 * @param __o The offset into the array.
	 * @return The number of bytes copied into the array.
	 * @throws ArrayIndexOutOfBoundsException If the offset is negative or
	 * the record data exceeds the size of the output array.
	 * @throws InvalidRecordIDException If the record ID is not valid.
	 * @throws NullPointerException On null arguments.
	 * @throws RecordStoreException If another problem occurs with the record
	 * store.
	 * @throws RecordStoreNotOpenException If the record store is not open.
	 * @since 2017/02/26
	 */
	public int getRecord(int __id, byte[] __b, int __o)
		throws ArrayIndexOutOfBoundsException, InvalidRecordIDException,
			NullPointerException, RecordStoreException,
			RecordStoreNotOpenException
	{
		if (__b == null)
			throw new NullPointerException("NARG");
		if (__o < 0)
			throw new ArrayIndexOutOfBoundsException("IOOB");
		
		// This volume
		int vid = this._vid;
		
		// Lock
		VinylRecord vinyl = RecordStore._VINYL;
		try (VinylLock lock = vinyl.lock())
		{
			// Check open
			this.__checkOpen();
			
			// Need to know the size of the record
			int size = vinyl.pageSize(vid, __id);
			RecordStore.__checkError(size);
			
			// {@squirreljme.error DC04 The record does not fit into the
			// output.}
			if (size < 0 || (__o + size) > __b.length)
				throw new ArrayIndexOutOfBoundsException("DC04");
			
			// Read data
			int read = vinyl.pageRead(vid, __id, __b, __o, size);
			RecordStore.__checkError(read);
			
			// Size is used as the return value
			return size;
		}
	}
	
	/**
	 * Returns the size of the given record.
	 *
	 * @param __id The record ID to get the size for.
	 * @return The size of the given record.
	 * @throws InvalidRecordIDException If the record ID is not valid.
	 * @throws RecordStoreException If another problem occurs with the record
	 * store.
	 * @throws RecordStoreNotOpenException If the record store is not open.
	 * @since 2016/02/26
	 */
	public int getRecordSize(int __id)
		throws InvalidRecordIDException, RecordStoreException,
			RecordStoreNotOpenException
	{
		// Lock
		VinylRecord vinyl = RecordStore._VINYL;
		try (VinylLock lock = vinyl.lock())
		{
			// Check open
			this.__checkOpen();
			
			// Need to know the size of the record
			int size = vinyl.pageSize(this._vid, __id);
			RecordStore.__checkError(size);
			
			// Return it
			return size;
		}
	}
	
	/**
	 * Returns the record store information.
	 *
	 * @return The record store information.
	 * @throws RecordStoreNotOpenException If the record store is not open.
	 * @since 2016/02/26
	 */
	public RecordStoreInfo getRecordStoreInfo()
		throws RecordStoreNotOpenException
	{
		// Check open
		this.__checkOpen();
		
		// Just quickly create
		return new RecordStoreInfo(this._vid);
	}
	
	/**
	 * Returns the size of the record store.
	 *
	 * @return The record store size, not to exceed {@link Integer#MAX_VALUE}.
	 * @throws RecordStoreNotOpenException If the record store is not open.
	 * @since 2016/02/26
	 */
	@Deprecated
	public int getSize()
		throws RecordStoreNotOpenException
	{
		return (int)Math.min(Integer.MAX_VALUE,
			this.getRecordStoreInfo().getSize());
	}
	
	/**
	 * Returns the available size of the record store.
	 *
	 * @return The available record store size, not to exceed
	 * {@link Integer#MAX_VALUE}.
	 * @throws RecordStoreNotOpenException If the record store is not open.
	 * @since 2016/02/26
	 */
	@Deprecated
	public int getSizeAvailable()
		throws RecordStoreNotOpenException
	{
		return (int)Math.min(Integer.MAX_VALUE,
			this.getRecordStoreInfo().getSizeAvailable());
	}
	
	/**
	 * Returns the tag of the given record.
	 *
	 * @param __id The record ID to get the tag for.
	 * @return The tag of the given record.
	 * @throws InvalidRecordIDException If the record ID is not valid.
	 * @throws RecordStoreException If another problem occurs with the record
	 * store.
	 * @throws RecordStoreNotOpenException If the record store is not open.
	 * @since 2016/02/26
	 */
	public int getTag(int __id)
		throws InvalidRecordIDException, RecordStoreException,
			RecordStoreNotOpenException
	{
		// Lock
		VinylRecord vinyl = RecordStore._VINYL;
		try (VinylLock lock = vinyl.lock())
		{
			// Check open
			this.__checkOpen();
			
			// Get and check tag
			int rv = vinyl.pageTag(this._vid, __id);
			RecordStore.__checkError(rv);
			
			return rv;
		}
	}
	
	/**
	 * Returns the version of the record store, this may be used to quickly
	 * determine if a store has been modified.
	 *
	 * @return The version of this record store.
	 * @throws RecordStoreNotOpenException If the record store is not open.
	 * @since 2016/02/26
	 */
	public int getVersion()
		throws RecordStoreNotOpenException
	{
		// Lock
		VinylRecord vinyl = RecordStore._VINYL;
		try (VinylLock lock = vinyl.lock())
		{
			// Check open
			this.__checkOpen();
			
			int rv = vinyl.volumeModCount(this._vid);
			
			try
			{
				RecordStore.__checkError(rv);
			}
			catch (RecordStoreException e)
			{
				if (e instanceof RecordStoreNotOpenException)
					throw (RecordStoreNotOpenException)e;
				
				// {@squirreljme.error DC05 Could not get the record store
				// version.}
				throw new RuntimeException("DC05", e);
			}
			
			return rv;
		}
	}
	
	/**
	 * Removes the specified record listener, this has no effect if it has
	 * already been removed or was never added.
	 *
	 * @param __l The record listener to remove.
	 * @since 2017/02/26
	 */
	public void removeRecordListener(RecordListener __l)
	{
		// Ignore
		if (__l == null)
			return;
		
		// Lock
		VinylRecord vinyl = RecordStore._VINYL;
		try (VinylLock lock = vinyl.lock())
		{
			// No effect if closed
			if (this._opens <= 0)
				return;
			
			// Remove listener
			Set<RecordListener> listeners = this._listeners;
			synchronized (listeners)
			{
				listeners.remove(__l);
			}
		}
	}
	
	/**
	 * Sets the mode of the record store which permits or denies other suites
	 * access to this record store.
	 *
	 * This may only operate on fully closed record stores and no other
	 * suites must have this record store open when this is called.
	 *
	 * @param __auth The authorization mode to use.
	 * @param __write Whether writing should be permitted.
	 * @throws IllegalArgumentException If the authorization mode is not
	 * valid.
	 * @throws IllegalStateException If the record store is opened by any
	 * application.
	 * @throws RecordStoreException If some other problem occurs with the
	 * record store.
	 * @throws SecurityException If changing the mode is not permitted.
	 * @since 2017/02/26
	 */
	public void setMode(int __auth, boolean __write)
		throws IllegalArgumentException, IllegalStateException,
			RecordStoreException, SecurityException
	{
		// Lock
		VinylRecord vinyl = RecordStore._VINYL;
		try (VinylLock lock = vinyl.lock())
		{
			throw new todo.TODO();
		}
	}
	
	/**
	 * Sets the data for a record.
	 *
	 * @param __id The record ID to set.
	 * @param __b The input data.
	 * @param __o The offset into the array
	 * @param __l The number of bytes to write.
	 * @param __tag The new tag to set for the record, this replaces the
	 * old tag.
	 * @throws ArrayIndexOutOfBoundsException If the offset and/or length are
	 * negative or exceed the array bounds.
	 * @throws InvalidRecordIDException If the record ID is not valid.
	 * @throws NullPointerException On null arguments.
	 * @throws RecordStoreException If another unspecified error happens.
	 * @throws RecordStoreFullException If there is not enough space to store
	 * the data.
	 * @throws RecordStoreNotOpenException If the record store is not open.
	 * @since 2017/02/26
	 */
	public void setRecord(int __id, byte[] __b, int __o, int __l, int __tag)
		throws ArrayIndexOutOfBoundsException, InvalidRecordIDException,
			NullPointerException, RecordStoreException,
			RecordStoreFullException, RecordStoreNotOpenException
	{
		if (__b == null)
			throw new NullPointerException("NARG");
		if (__o < 0 || __l < 0 || (__o + __l) > __b.length)
			throw new ArrayIndexOutOfBoundsException("IOOB");
		
		// {@squirreljme.error DC06 Cannot write record to read-only store.}
		if (!this._write)
			throw new RecordStoreException("DC06");
		
		// Used for later
		RecordListener[] listeners = this.__listeners();
		
		// Lock
		VinylRecord vinyl = RecordStore._VINYL;
		try (VinylLock lock = vinyl.lock())
		{
			// Check open
			this.__checkOpen();
			
			// Set the page
			__id = vinyl.pageSet(this._vid, __id, __b, __o, __l, __tag);
			RecordStore.__checkError(__id);
		}
		
		// Report to the listeners
		for (RecordListener l : listeners)
			l.recordChanged(this, __id);
	}
	
	/**
	 * Calls {@code setRecord(__id, __b, __o, __l, getTag(__id))}.
	 *
	 * @param __id As forwarded.
	 * @param __b As forwarded.
	 * @param __o As forwarded.
	 * @param __l As forwarded.
	 * @throws ArrayIndexOutOfBoundsException As forwarded.
	 * @throws InvalidRecordIDException As forwarded.
	 * @throws NullPointerException As forwarded.
	 * @throws RecordStoreException As forwarded.
	 * @throws RecordStoreFullException As forwarded.
	 * @throws RecordStoreNotOpenException As forwarded.
	 * @since 2017/02/26
	 */
	public void setRecord(int __id, byte[] __b, int __o, int __l)
		throws ArrayIndexOutOfBoundsException, InvalidRecordIDException,
			NullPointerException, RecordStoreException,
			RecordStoreFullException, RecordStoreNotOpenException
	{
		this.setRecord(__id, __b, __o, __l, this.getTag(__id));
	}
	
	/**
	 * Checks that this record store is open.
	 *
	 * @throws RecordStoreNotOpenException If it is not open.
	 * @since 2019/04/15
	 */
	private final void __checkOpen()
		throws RecordStoreNotOpenException
	{
		// {@squirreljme.error DC07 This record store is not open.
		if (this._opens <= 0)
			throw new RecordStoreNotOpenException("DC07");
	}
	
	/**
	 * Lists the pages that exist within this record store.
	 *
	 * @return The page IDs.
	 * @since 2019/05/13
	 */
	final int[] __listPages()
		throws RecordStoreNotOpenException
	{
		// Lock
		VinylRecord vinyl = RecordStore._VINYL;
		try (VinylLock lock = vinyl.lock())
		{
			// Check open
			this.__checkOpen();
			
			return vinyl.pageList(this._vid);
		}
	}
	
	/**
	 * Returns all of the listeners for this record store.
	 *
	 * @return The listeners.
	 * @since 2019/04/15
	 */
	private final RecordListener[] __listeners()
	{
		Set<RecordListener> listeners = this._listeners;
		return listeners.<RecordListener>toArray(
			new RecordListener[listeners.size()]);
	}
	
	/**
	 * Deletes the specified record store.
	 *
	 * Suites may only delete their own record store.
	 *
	 * This will not call
	 * {@link RecordListener#recordDeleted(RecordStore, int)}
	 * listeners associated with the given record store.
	 *
	 * @param __n The name of the record store to delete.
	 * @throws RecordStoreException If the record store cannot be deleted due
	 * to being owned by another suite or deletion is not possible.
	 * @throws RecordStoreNotFoundException If the given record store was not
	 * found.
	 * @since 2017/02/26
	 */
	public static void deleteRecordStore(String __n)
		throws NullPointerException, RecordStoreException,
			RecordStoreNotFoundException
	{
		if (__n == null)
			throw new NullPointerException("NARG");
		
		// Our suite identifier to find our own records
		long mysid = SuiteIdentifier.currentIdentifier();
		
		// Lock
		VinylRecord vinyl = RecordStore._VINYL;
		try (VinylLock lock = vinyl.lock())
		{
			// Try to locate our record
			int got = -1;
			for (int rid : vinyl.volumeList())
			{
				// Another suite's volume
				if (mysid != vinyl.volumeSuiteIdentifier(rid))
					continue;
				
				// Found the record?
				if (__n.equals(vinyl.volumeName(rid)))
				{
					got = -1;
					break;
				}
			}
			
			// {@squirreljme.error DC08 Cannot delete the specified record
			// store because it does not exist. (The name of the store)}
			if (got == -1)
				throw new RecordStoreNotFoundException("DC08 " + __n);
			
			throw new todo.TODO();
		}
	}
	
	/**
	 * Returns the list of record stores that are available and owned by
	 * this suite.
	 *
	 * @return The list of available record stores, the order is unspecified
	 * and implementation dependent. If there are no records then {@code null}
	 * will be returned.
	 * @since 2017/02/26
	 */
	public static String[] listRecordStores()
	{
		// Our suite identifier to find our own records
		long mysid = SuiteIdentifier.currentIdentifier();
		
		// Lock
		VinylRecord vinyl = RecordStore._VINYL;
		try (VinylLock lock = vinyl.lock())
		{
			List<String> rv = new ArrayList<>();
			
			// Go through all IDs and locate record store info
			for (int rid : vinyl.volumeList())
			{
				// Do not add records which belong to another suite
				if (mysid != vinyl.volumeSuiteIdentifier(rid))
					continue;
				
				rv.add(vinyl.volumeName(rid));
			}
			
			return rv.<String>toArray(new String[rv.size()]);
		}
	}
	
	/**
	 * Attempts to open and optionally create the record store for this midlet
	 * with the specified name.
	 *
	 * If a password is specified then the record store will be encrypted to
	 * prevent tampering.
	 *
	 * If the record store has already been opened then it will return a
	 * previously opened record store.
	 *
	 * @param __n The name of the record store, must consist of 1 to 32
	 * Unicode characters.
	 * @param __create If {@code true} then if the record store does not
	 * exist it will be created.
	 * @param __auth The authorization mode of the record which may permit
	 * other applications to access this record. If the record already exists
	 * then this argument will be ignored.
	 * @param __write If {@code true} then the record store may be written to
	 * by other suites. If the record already exists then this argument will be
	 * ignored.
	 * @param __pass The password.
	 * @return The newly opened or created record store, if the record store
	 * is already open then it will return the already open one.
	 * @throws IllegalArgumentException If the name is not valid or the
	 * authorization mode is not valid.
	 * @throws RecordStoreException If it could not be opened for another
	 * reason.
	 * @throws RecordStoreFullException If there is no space remaining.
	 * @throws RecordStoreNotFoundException If the record store could not be
	 * located.
	 * @throws SecureRecordStoreException The secured record could not be
	 * initialized.
	 * @throws SecurityException If the encryption password does not
	 * match an existing password.
	 * @since 2017/02/26
	 */
	public static RecordStore openRecordStore(String __n, boolean __create,
		int __auth, boolean __write, String __pass)
		throws IllegalArgumentException, RecordStoreException,
			RecordStoreFullException, RecordStoreNotFoundException,
			SecureRecordStoreException, SecurityException
	{
		return RecordStore.__openRecordStore(__n,
			SuiteIdentifier.currentVendor(), SuiteIdentifier.currentName(),
			__create, __auth, __write, __pass);
	}
	
	/**
	 * Forwards to {@code openRecordStore(__n, __create, __auth, __write, "")}.
	 *
	 * @param __n As forwarded.
	 * @param __create As forwarded.
	 * @param __auth As forwarded.
	 * @param __write As forwarded.
	 * @return As forwarded.
	 * @throws IllegalArgumentException As forwarded.
	 * @throws RecordStoreException As forwarded.
	 * @throws RecordStoreFullException As forwarded.
	 * @throws RecordStoreNotFoundException As forwarded.
	 * @throws SecureRecordStoreException As forwarded.
	 * @throws SecurityException As forwarded.
	 * @since 2017/02/26
	 */
	public static RecordStore openRecordStore(String __n, boolean __create,
		int __auth, boolean __write)
		throws IllegalArgumentException, RecordStoreException,
			RecordStoreFullException, RecordStoreNotFoundException,
			SecureRecordStoreException, SecurityException
	{
		return RecordStore.openRecordStore(__n, __create, __auth, __write, "");
	}
	
	/**
	 * Forwards to {@code openRecordStore(__n, __create, AUTHMODE_PRIVATE,
	 * true, "")}.
	 *
	 * @param __n As forwarded.
	 * @param __create As forwarded.
	 * @return As forwarded.
	 * @throws IllegalArgumentException As forwarded.
	 * @throws RecordStoreException As forwarded.
	 * @throws RecordStoreFullException As forwarded.
	 * @throws RecordStoreNotFoundException As forwarded.
	 * @throws SecureRecordStoreException As forwarded.
	 * @throws SecurityException As forwarded.
	 * @since 2017/02/26
	 */
	public static RecordStore openRecordStore(String __n, boolean __create)
		throws IllegalArgumentException, RecordStoreException,
			RecordStoreFullException, RecordStoreNotFoundException,
			SecureRecordStoreException, SecurityException
	{
		return RecordStore.openRecordStore(__n, __create,
			RecordStore.AUTHMODE_PRIVATE,
			true, "");
	}
	
	/**
	 * Attempts to open the record store created by another application.
	 *
	 * The record store must have been created with the {@link #AUTHMODE_ANY}
	 * authorization. If it is encrypted then the same password must be
	 * specified.
	 *
	 * If the vendor and suite is our own then this will be the same as
	 * calling: {@code openRecordStore(__n, false, AUTHMODE_PRIVATE, true,
	 * __pass)}.
	 *
	 * @param __n The name of the record store, must consist of 1 to 32
	 * Unicode characters.
	 * @param __vend The vendor of the other suite.
	 * @param __suite The suite.
	 * @param __pass The password to the record store.
	 * @return The opened record store.
	 * @throws IllegalArgumentException If the name, vendor, or suite names
	 * are not valid.
	 * @throws RecordStoreException If it could not be opened for another
	 * reason.
	 * @throws RecordStoreNotFoundException If the record store could not be
	 * located.
	 * @throws SecureRecordStoreException The secured record could not be
	 * initialized.
	 * @throws SecurityException If the encryption password does not
	 * match an existing password.
	 * @since 2017/02/26
	 */
	public static RecordStore openRecordStore(String __n, String __vend,
		String __suite, String __pass)
		throws IllegalArgumentException, RecordStoreException,
			RecordStoreNotFoundException, SecureRecordStoreException,
			SecurityException
	{
		return RecordStore.__openRecordStore(__n, __vend, __suite, false,
			RecordStore.AUTHMODE_ANY, false, __pass);
	}
	
	/**
	 * Calls {@code openRecordStore(__n, __vend, __suite, "")}.
	 *
	 * @param __n As forwarded.
	 * @param __vend As forwarded.
	 * @param __suite As forwarded.
	 * @return As forwarded.
	 * @throws IllegalArgumentException As forwarded.
	 * @throws RecordStoreException As forwarded.
	 * @throws RecordStoreNotFoundException As forwarded.
	 * @throws SecureRecordStoreException As forwarded.
	 * @throws SecurityException As forwarded.
	 * @since 2017/02/26
	 */
	public static RecordStore openRecordStore(String __n, String __vend,
		String __suite)
		throws IllegalArgumentException, RecordStoreException,
			RecordStoreNotFoundException, SecureRecordStoreException,
			SecurityException
	{
		return RecordStore.openRecordStore(__n, __vend, __suite, "");
	}
	
	/**
	 * Checks for an error and throws an exception potentially.
	 *
	 * @param __id The ID to check, negative indicates error.
	 * @throws RecordStoreException If there is an error.
	 * @since 2019/05/01
	 */
	private static final void __checkError(int __id)
		throws RecordStoreException
	{
		// Error was detected
		if (__id < 0)
		{
			// {@squirreljme.error DC09 Could not add the record, there might
			// not be enough free space available.}
			if (__id == VinylRecord.ERROR_NO_MEMORY)
				throw new RecordStoreFullException("DC09");
			
			// {@squirreljme.error DC0a No such record store exists.}
			if (__id == VinylRecord.ERROR_NO_VOLUME)
				throw new RecordStoreNotFoundException("DC0a");
			
			// {@squirreljme.error DC0b No such record exists.}
			if (__id == VinylRecord.ERROR_NO_PAGE)
				throw new InvalidRecordIDException("DC0b");
			
			// {@squirreljme.error DC0c Unknown record store error. (Error)}
			throw new RecordStoreException("DC0c " + __id);
		}
	}
	
	/**
	 * Attempts to open and optionally create the record store for the
	 * specified MIDlet.
	 *
	 * If a password is specified then the record store will be encrypted to
	 * prevent tampering.
	 *
	 * If the record store has already been opened then it will return a
	 * previously opened record store.
	 *
	 * @param __name The name of the record store, must consist of 1 to 32
	 * Unicode characters.
	 * @param __create If {@code true} then if the record store does not
	 * exist it will be created.
	 * @param __auth The authorization mode of the record which may permit
	 * other applications to access this record. If the record already exists
	 * then this argument will be ignored.
	 * @param __write If {@code true} then the record store may be written to
	 * by other suites. If the record already exists then this argument will be
	 * ignored.
	 * @return The newly opened or created record store, if the record store
	 * is already open then it will return the already open one.
	 * @throws IllegalArgumentException If the name is not valid or the
	 * authorization mode is not valid.
	 * @throws NullPointerException On null arguments.
	 * @throws RecordStoreException If it could not be opened for another
	 * reason.
	 * @throws RecordStoreFullException If there is no space remaining.
	 * @throws RecordStoreNotFoundException If the record store could not be
	 * located.
	 * @throws SecureRecordStoreException The secured record could not be
	 * initialized.
	 * @throws SecurityException If the encryption password does not
	 * match an existing password.
	 * @since 2018/12/15
	 */
	private static RecordStore __openRecordStore(String __name, String __vend,
		String __suite, boolean __create, int __auth, boolean __write,
		String __pass)
		throws IllegalArgumentException, NullPointerException,
			RecordStoreException, RecordStoreFullException,
			RecordStoreNotFoundException, SecureRecordStoreException,
			SecurityException
	{
		if (__name == null || __vend == null || __suite == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error DC0d The name is not valid.}
		int namelen = __name.length();
		if (namelen < 1 || namelen > 32)
			throw new IllegalArgumentException("DC0d " + __name);
		
		// Get identifier, used to find the record
		long sid = SuiteIdentifier.identifier(__vend, __suite),
			mysid = SuiteIdentifier.currentIdentifier();
		
		// Lock
		VinylRecord vinyl = RecordStore._VINYL;
		try (VinylLock lock = vinyl.lock())
		{
			// Go through all records and try to find a pre-existing one
			int rv = -1;
			for (int rid : vinyl.volumeList())
			{
				// Belongs to another suite?
				if (sid != vinyl.volumeSuiteIdentifier(rid))
					continue;
				
				// Same name?
				if (__name.equals(vinyl.volumeName(rid)))
				{
					rv = rid;
					break;
				}
			}
			
			// Open a record which already exists
			if (rv >= 0)
			{
				// Use a pre-cached store
				Map<Integer, RecordStore> cache = RecordStore._STORE_CACHE;
				RecordStore rs = cache.get(rv);
				if (rs == null)
					cache.put(rv, (rs = new RecordStore(rv, __name,
						sid == mysid || vinyl.volumeOtherWritable(rv))));
				
				// Increment the open count
				rs._opens++;
				return rs;
			}
			
			// {@squirreljme.error DC0e Could not find the specified record
			// store. (The name; The vendor; The suite)}
			if (!__create)
				throw new RecordStoreNotFoundException(
					String.format("DC0e %s %s %s", __name, __vend, __suite));
			
			// {@squirreljme.error DC0f Could not create the record, it is
			// likely that there is not enough space remaining.}
			rv = vinyl.volumeCreate(sid, __name, __write);
			if (rv < 0)
				throw new RecordStoreFullException("DC0f");
			
			// Since we created it, we can just return the info
			return new RecordStore(rv, __name, sid == mysid || __write);
		}
	}
}

