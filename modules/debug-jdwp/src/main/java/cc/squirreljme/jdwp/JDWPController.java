// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jdwp;

import cc.squirreljme.jdwp.event.CallStackStepping;
import cc.squirreljme.jdwp.event.EventFilter;
import cc.squirreljme.jdwp.event.FieldOnly;
import cc.squirreljme.jdwp.trips.JDWPGlobalTrip;
import cc.squirreljme.jdwp.trips.JDWPTrip;
import cc.squirreljme.jdwp.views.JDWPViewFrame;
import cc.squirreljme.jdwp.views.JDWPViewKind;
import cc.squirreljme.jdwp.views.JDWPViewObject;
import cc.squirreljme.jdwp.views.JDWPViewThread;
import cc.squirreljme.jdwp.views.JDWPViewThreadGroup;
import cc.squirreljme.jdwp.views.JDWPViewType;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * This class acts as the main controller interface for JDWP and acts as a kind
 * of polling system to interface with something.
 *
 * @since 2021/03/08
 */
public final class JDWPController
	implements Closeable, Runnable
{
	/** Should debugging be enabled? */
	static final boolean _DEBUG =
		Boolean.getBoolean("cc.squirreljme.jdwp.debug");
	
	/** The communication link. */
	protected final CommLink commLink;
	
	/** Debugger state. */
	protected final JDWPState state;
		
	/** The event manager. */
	protected final EventManager eventManager =
		new EventManager();
	
	/** The binding, which is called to perform any actions. */
	private final Reference<JDWPBinding> _bind;
		
	/** The ID lock. */
	private final Object _nextIdMonitor =
		new Object();
	
	/** Value cache. */
	private final Deque<JDWPValue> _freeValues =
		new LinkedList<>();
	
	/** The global trips that are available. */
	private final JDWPTrip[] _trips =
		new JDWPTrip[JDWPGlobalTrip.values().length];
	
	/** Weak self reference, so there are not 1000 of these. */
	private final Reference<JDWPController> _weakThis =
		new WeakReference<>(this);
	
	/** Held packets. */
	private final Queue<JDWPPacket> _heldPackets =
		new LinkedList<>();
	
	/** Are events to the debugger being held? */
	volatile boolean _holdEvents;
	
	/** Next ID number. */
	private volatile int _nextId;
	
	/** Is this closed? */
	private volatile boolean _closed;
	
	/**
	 * Initializes the controller which manages the communication of JDWP.
	 * 
	 * @param __bind The binding to use.
	 * @param __in The input stream to read from.
	 * @param __out The output stream to write to.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/03/08
	 */
	public JDWPController(JDWPBinding __bind, InputStream __in,
		OutputStream __out)
		throws NullPointerException
	{
		if (__bind == null || __in == null || __out == null)
			throw new NullPointerException("NARG");
		
		this._bind = new WeakReference<>(__bind);
		this.state = new JDWPState(new WeakReference<>(__bind));
		this.commLink = new CommLink(__in, __out);
		
		// Setup Communication Link thread
		Thread thread = new Thread(this, "JDWPController");
		thread.start();
	}
	
	/**
	 * Returns the binding.
	 * 
	 * @return The binding.
	 * @throws JDWPException If the binding has been garbage collected.
	 * @since 2021/05/07
	 */
	public JDWPBinding bind()
		throws JDWPException
	{
		// {@squirreljme.error AG0h The JDWP Binding has been garbage
		// collected.}
		JDWPBinding rv = this._bind.get();
		if (rv == null)
			throw new JDWPException("AG0h");
		
		return rv;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/03/08
	 */
	@Override
	public void close()
	{
		// Only close once
		synchronized (this)
		{
			if (this._closed)
				return;
			this._closed = true;
		}
		
		// Close the communication link
		try
		{
			this.commLink.close();
		}
		
		// Close and remove all held packets.
		finally
		{
			synchronized (this)
			{
				Queue<JDWPPacket> heldPackets = this._heldPackets;
				while (!heldPackets.isEmpty())
					heldPackets.poll().close();
			}
		}
	}
	
	/**
	 * Returns the location of the given thread.
	 * 
	 * @param __thread The thread to get from.
	 * @return The current thread location.
	 * @since 2021/04/25
	 */
	public JDWPLocation locationOf(Object __thread)
	{
		JDWPViewType viewType = this.viewType();
		JDWPViewThread viewThread = this.viewThread();
		JDWPViewFrame viewFrame = this.viewFrame();
		
		// Get the current frames and see if 
		Object[] frames = viewThread.frames(__thread);
		if (frames == null || frames.length == 0)
			return JDWPLocation.BLANK;
		
		// Get frame details
		Object topFrame = frames[0];
		Object type = viewFrame.atClass(topFrame);
		int methodDx = viewFrame.atMethodIndex(topFrame);
		
		// Make sure the types are added!
		JDWPLinker<Object> items = this.state.items;
		if (topFrame != null)
			items.put(topFrame);
		if (type != null)
			items.put(type);
		
		// Build information
		return new JDWPLocation(type,
			methodDx,
			viewFrame.atCodeIndex(topFrame),
			viewType.methodName(type, methodDx),
			viewType.methodSignature(type, methodDx));
	}
	
	/**
	 * Polls for state changes within JDWP and processes any events as
	 * needed.
	 * 
	 * @return If JDWP debugging will continue.
	 * @throws JDWPException If there is an issue with the connection.
	 * @since 2021/03/10
	 */
	public boolean poll()
		throws JDWPException
	{
		// Read in any packets and process them as they come
		for (CommLink commLink = this.commLink;;)
		{
			// Drain any held packets
			for (;;)
			{
				// Is there a packet?
				JDWPPacket packet;
				synchronized (this)
				{
					// If we are closed, do nothing
					if (this._closed)
						return false;
					
					// If we are still holding events, do not drain any
					if (this._holdEvents)
						break;
					
					// Remove the next packet, if there is any
					packet = this._heldPackets.poll();
				}
				
				// If there is no packet stop processing
				if (packet == null)
					break;
				
				// Send this packet and close when done
				try (JDWPPacket ignored = packet)
				{
					commLink.send(packet);
				}
			}
			
			// Normal packet holding
			try (JDWPPacket packet = commLink.poll())
			{
				// No data?
				if (packet == null)
					break;
				
				// Debug
				if (JDWPController._DEBUG)
					Debugging.debugNote("JDWP: <- %s", packet);
				
				// Ignore any reply packet we received
				if (packet.isReply())
					continue;
				
				// Resultant packet, returned as a result
				JDWPPacket result;
				
				// Get the command and if it is unknown, ignore it
				JDWPCommand command = packet.commandSet()
					.command(packet.command());
				if (command == null)
					result = this.__reply(packet.id(),
						ErrorType.NOT_IMPLEMENTED);
				
				// Execute the command normally
				else
				{
					try
					{
						// Execute the command asynchronously
						result = command.execute(this, packet);
						
						// If a result is missing, assume nothing needed
						if (result == null)
							result = this.__reply(packet.id(),
								ErrorType.NO_ERROR);
					}
					
					// There was an error executing this command
					catch (JDWPCommandException e)
					{
						// Print a trace of it
						e.printStackTrace();
						
						// Use this result
						result = this.__reply(
							packet.id(), e.type);
					}
				}
				
				// Send the result to the debugger, close when done
				try (JDWPPacket ignored = result)
				{
					commLink.send(result);
				}
			}
		}
		
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/03/13
	 */
	@Override
	public void run()
	{
		// This runs in a thread, so if this thread ever stops for any reason
		// we terminate the connection
		try (JDWPController ignored = this)
		{
			CommLink commLink = this.commLink;
			while (!commLink._shutdown)
				this.poll();
		}
	}
	
	/**
	 * Signals a JDWP event, this will find any request and perform suspension
	 * as requested by the debugger.
	 * 
	 * @param __thread The thread signaling this, if not known this will be
	 * {@code null}.
	 * @param __kind The kind of event to signal.
	 * @param __args Arguments to the signal packet.
	 * @throws NullPointerException On null arguments.
	 * @return If an event was found and sent for it.
	 * @since 2021/03/16
	 */
	public boolean signal(Object __thread, EventKind __kind, Object... __args)
		throws NullPointerException
	{
		if (__kind == null)
			throw new NullPointerException("NARG");
		
		// Make sure the thread is known
		if (__thread != null)
			this.state.items.put(__thread);
		
		// Is this a special unconditional event
		boolean unconditional = false;
		if (__kind == EventKind.UNCONDITIONAL_BREAKPOINT)
		{
			unconditional = true;
			__kind = EventKind.BREAKPOINT;
		}
		
		// Go through all compatible events for this thread
		boolean hit = false;
		for (EventRequest request : this.eventManager.find(
			this, __thread, unconditional, __kind, __args))
		{
			// Suspend all threads?
			if (request.suspendPolicy == SuspendPolicy.ALL)
				for (Object thread : this.__allThreads())
					this.viewThread().suspension(thread).suspend();
			
			// Suspend only a single thread?
			else if (request.suspendPolicy == SuspendPolicy.EVENT_THREAD)
			{
				if (__thread != null)
					this.viewThread().suspension(__thread).suspend();
			}
			
			// Event was hit
			hit = true;
			
			// Send response to the VM of the event that just occurred
			try (JDWPPacket packet = this.__event(request.suspendPolicy,
				__kind, request.id))
			{
				if (JDWPController._DEBUG)
					Debugging.debugNote("JDWP: Event #%d %s",
						request.id, __kind);
				
				// Write the signal event data
				__kind.write(this, __thread, packet, __args);
				
				// Are we holding events? Save this for later if so
				synchronized (this)
				{
					if (this._holdEvents)
					{
						this._heldPackets.add(this.commLink
							.__getPacket(true).copyOf(packet));
						continue;
					}
				}
				
				// Send it away!
				this.commLink.send(packet);
			}
		}
		
		return hit;
	}
	
	/**
	 * Returns a trip for the given type of global trip.
	 * 
	 * @param <T> The type of trip.
	 * @param __cl The type of trip.
	 * @param __t The type of trip.
	 * @return The trip for the given class.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/04/11
	 */
	public final <T extends JDWPTrip> T trip(Class<T> __cl, JDWPGlobalTrip __t)
		throws NullPointerException
	{
		if (__cl == null || __t == null)
			throw new NullPointerException("NARG");
		
		// Was a trip already created? Use that if so
		JDWPTrip[] trips = this._trips;
		JDWPTrip trip = trips[__t.ordinal()];
		if (trip != null)
			return __cl.cast(trip);
		
		// Otherwise setup a new trip
		Reference<JDWPController> ref = this._weakThis;
		switch (__t)
		{
			case CLASS_STATUS:
				trip = new __TripClassStatus__(ref);
				break;
			
			case FIELD:
				trip = new __TripField__(ref);
				break;
			
			case VM_STATE:
				trip = new __TripVmState__(ref);
				break;
			
			case THREAD:
				trip = new __TripThreadAlive__(ref);
				break;
			
			default:
				throw Debugging.oops(__t);
		}
		
		// Cache and use it
		this._trips[__t.ordinal()] = trip;
		return  __cl.cast(trip);
	}
	
	/**
	 * Injects the given event for trips to occur at later point.
	 * 
	 * @param __request The request being tripped at a later point.
	 * @since 2021/04/17
	 */
	protected void tripRequest(EventRequest __request)
		throws NullPointerException
	{
		if (__request == null)
			throw new NullPointerException("NARG");
		
		Reference<JDWPController> ref = this._weakThis;
		
		// Depends on the event
		EventFilter filter = __request.filter;
		switch (__request.eventKind)
		{
				// Breakpoint at a random position
			case BREAKPOINT:
				{
					// If there is no location, this is a pointless breakpoint
					JDWPLocation location = filter.location;
					if (location == null)
						return;
					
					// Initializes the breakpoint
					this.viewType().methodBreakpoint(location.type,
						location.methodDx, (int)location.codeDx,
						new __TripBreakpoint__(ref));
				}
				break;
				
			case FIELD_ACCESS:
			case FIELD_MODIFICATION:
				{
					// We need to know the field we are watching
					FieldOnly fieldOnly = filter.fieldOnly;
					if (fieldOnly == null)
						return;
					
					// Indicate we want to watch this field
					this.viewType().fieldWatch(
						fieldOnly.type, fieldOnly.fieldDx,
						__request.eventKind ==
							EventKind.FIELD_MODIFICATION);
				}
				break;
				
				// Single stepping
			case SINGLE_STEP:
				{
					// An example single step
					// EventRequest[id=483,kind=SINGLE_STEP,suspend=ALL,left=1,
					// filter=EventFilter{callStackStepping=CallStackStepping[
					// thread=Thread-6: callback#0,depth=0,size=1],
					// exception=null, excludeClass=ClassPattern(kotlin.
					// KotlinNullPointerException), fieldOnly=null,
					// includeClass=null, location=null, thisInstance=null,
					// thread=null, type=null}]
					
					// If there is no stepping what are we going to do?
					CallStackStepping stepping =
						filter.callStackStepping;
					if (stepping == null || stepping.thread == null)
						return;
					
					// Thread was also specified but does not match?
					if (filter.thread != null &&
						filter.thread != stepping.thread)
						return;
					
					// Tell the thread to enter stepping mode
					JDWPStepTracker stepTracker = this.viewThread()
						.stepTracker(stepping.thread);
					stepTracker.steppingSet(this, stepping.thread,
						stepping.size, stepping.depth);
				}
				break;
			
				// Preparing/loading a class, since this could be a class that
				// we already know about, we need to go through all of the
				// classes to find the right one
			case CLASS_PREPARE:
				{
					// If this filter does not have a type match then it is
					// very likely a very generic one
					if (filter == null || !filter.hasTypeMatch())
						return;
					
					// Go through all of our known classes and report ones
					// that we already know about. Note use the cached types
					// so we do not have to ask the VM about it.
					JDWPViewType viewType = this.viewType();
					for (Object type : this.__allTypes(true))
						if (filter.meetsType(viewType, type))
							this.signal(null, __request.eventKind,
								type, JDWPClassStatus.INITIALIZED);
				}
				break;
		}
	}
	
	/**
	 * Returns a value to store data in.
	 * 
	 * @return A value to store data in.
	 * @since 2021/03/19
	 */
	public final JDWPValue value()
	{
		Deque<JDWPValue> freeValues = this._freeValues;
		synchronized (this._freeValues)
		{
			// Use an existing free value for recycling?
			JDWPValue rv = freeValues.poll();
			if (rv != null)
				return rv.__resetToOpen();
			
			// Otherwise make a new one
			//noinspection resource
			return new JDWPValue(freeValues).__resetToOpen();
		}
	}
	
	/**
	 * Returns the frame viewer.
	 * 
	 * @return The frame viewer.
	 * @since 2021/04/11
	 */
	public final JDWPViewFrame viewFrame()
	{
		return this.state.view(JDWPViewFrame.class, JDWPViewKind.FRAME);
	}
	
	/**
	 * Returns the object viewer.
	 * 
	 * @return The object viewer.
	 * @since 2021/04/10
	 */
	public final JDWPViewObject viewObject()
	{
		return this.state.view(JDWPViewObject.class, JDWPViewKind.OBJECT);
	}
	
	/**
	 * Returns the thread viewer.
	 * 
	 * @return The thread viewer.
	 * @since 2021/04/10
	 */
	public final JDWPViewThread viewThread()
	{
		return this.state.view(JDWPViewThread.class, JDWPViewKind.THREAD);
	}
	
	/**
	 * Returns the viewer for thread groups.
	 * 
	 * @return The viewer for thread groups.
	 * @since 2021/04/10
	 */
	public final JDWPViewThreadGroup viewThreadGroup()
	{
		return this.state.view(JDWPViewThreadGroup.class,
			JDWPViewKind.THREAD_GROUP);
	}
	
	/**
	 * Returns the viewer for types.
	 * 
	 * @return The viewer for types.
	 * @since 2021/04/11
	 */
	public final JDWPViewType viewType()
	{
		return this.state.view(JDWPViewType.class,
			JDWPViewKind.TYPE);
	}
	
	/**
	 * Returns all thread groups.
	 * 
	 * @return All thread groups.
	 * @since 2021/04/10
	 */
	final Object[] __allThreadGroups()
	{
		// Get all thread groups
		Object[] groups = this.bind().debuggerThreadGroups();
		
		// Register each one
		JDWPState state = this.state;
		for (Object group : groups)
			state.items.put(group);
		
		return groups;
	}
	
	/**
	 * Returns all threads.
	 * 
	 * @return All threads.
	 * @since 2021/04/10
	 */
	final Object[] __allThreads()
	{
		// Current state
		JDWPState state = this.state;
		
		// All available threads
		ArrayList<Object> allThreads = new ArrayList<>(); 
		
		// Start from the root thread group and get all of the threads
		// under them, since this is a machine to thread linkage
		JDWPViewThreadGroup view = state.view(
			JDWPViewThreadGroup.class, JDWPViewKind.THREAD_GROUP);
		for (Object group : this.bind().debuggerThreadGroups())
		{
			// Register thread group
			state.items.put(group);
			
			// Obtain all threads from this group
			Object[] threads = view.threads(group);
			
			// Register each thread
			for (Object thread : threads)
				state.items.put(thread);
			
			// Store into the list
			allThreads.ensureCapacity(
				allThreads.size() + threads.length);
			allThreads.addAll(Arrays.asList(threads));
		}
		
		return allThreads.toArray(new Object[allThreads.size()]);
	}
	
	
	/**
	 * Returns all of the known types.
	 * 
	 * @param __cached Do we use the type cache?
	 * @return All of the available types.
	 * @since 2021/04/14
	 */
	List<Object> __allTypes(boolean __cached)
	{
		List<Object> allTypes = new LinkedList<>();
		
		// Using all of the known cached types
		if (__cached)
		{
			JDWPViewType viewType = this.viewType();
			for (Object obj : this.state.items.values())
				if (viewType.isValid(obj))
					allTypes.add(obj);
		}
		
		// Get a fresh perspective on all the loaded types
		else
		{
			for (Object group : this.__allThreadGroups())
				allTypes.addAll(this.__allTypes(group));
		}
		
		return allTypes;
	}
	
	/**
	 * Returns all of the types within the given group.
	 * 
	 * @param __group The group to search.
	 * @return All of the types within the group.
	 * @since 2021/04/25
	 */
	private List<Object> __allTypes(Object __group)
		throws NullPointerException
	{
		if (__group == null)
			throw new NullPointerException("NARG");
			
		Object[] types = this.viewThreadGroup().allTypes(__group);
		
		// Register all types so that the debugger knows about their existence
		JDWPLinker<Object> items = this.state.items;
		for (Object type : types)
			items.put(type);
		
		return Arrays.asList(types);
	}
	
	/**
	 * Creates an event packet.
	 * 
	 * @param __policy The suspension policy used.
	 * @param __kind The kind of event to give.
	 * @param __responseId The response identifier.
	 * @return The event packet.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/03/14
	 */
	JDWPPacket __event(SuspendPolicy __policy, EventKind __kind,
		int __responseId)
		throws NullPointerException
	{
		if (__policy == null || __kind == null)
			throw new NullPointerException("NARG");
		
		JDWPPacket rv = this.commLink.__getPacket(true);
		
		// Composite command code
		rv._commandSet = 64;
		rv._command = 100;
		
		// Is just a normal event
		rv._id = this.__nextId();
		rv._errorCode = ErrorType.NO_ERROR;
		rv._flags = 0;
		
		// Write the single event header
		rv.writeByte(__policy.id);
		rv.writeInt(1);
		rv.writeByte(__kind.id);
		rv.writeInt(__responseId);
		
		return rv;
	}
	
	/**
	 * The next ID number.
	 * 
	 * @return Returns a new ID number.
	 * @since 2021/03/13
	 */
	final int __nextId()
	{
		synchronized (this._nextIdMonitor)
		{
			return ++this._nextId;
		}
	}
	
	/**
	 * Creates a reply packet.
	 * 
	 * @param __id The identifier.
	 * @param __error The error code.
	 * @return The packet used.
	 * @since 2021/03/12
	 */
	final JDWPPacket __reply(int __id, ErrorType __error)
	{
		JDWPPacket rv = this.commLink.__getPacket(true);
		
		rv._id = __id;
		rv._errorCode = __error;
		rv._flags = JDWPPacket.FLAG_REPLY;
		
		return rv;
	}
}
