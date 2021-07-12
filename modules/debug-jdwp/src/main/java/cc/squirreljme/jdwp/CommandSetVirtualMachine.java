// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jdwp;

import cc.squirreljme.jdwp.views.JDWPViewThread;
import cc.squirreljme.jdwp.views.JDWPViewThreadGroup;
import cc.squirreljme.jdwp.views.JDWPViewType;
import java.util.LinkedList;
import java.util.List;

/**
 * Virtual machine command set.
 *
 * @since 2021/03/12
 */
public enum CommandSetVirtualMachine
	implements JDWPCommand
{
	/** Version information. */
	VERSION(1)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/03/13
		 */
		@Override
		public JDWPPacket execute(JDWPController __controller,
			JDWPPacket __packet)
			throws JDWPException
		{
			JDWPPacket rv = __controller.__reply(
				__packet.id(), ErrorType.NO_ERROR);
			
			// VM Description
			rv.writeString(__controller.bind().vmDescription());
			
			// JDWP version (assuming Java 7?)
			rv.writeInt(1);
			rv.writeInt(7);
			
			// VM Information
			rv.writeString(__controller.bind().vmVersion());
			rv.writeString(__controller.bind().vmName());
			
			return rv;
		}
	},
	
	/** Class search by signature. */
	CLASSES_BY_SIGNATURE(2)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/03/13
		 */
		@Override
		public JDWPPacket execute(JDWPController __controller,
			JDWPPacket __packet)
			throws JDWPException
		{
			// What are we looking for?
			String wantSig = __packet.readString();
			
			// Search all types for a signature
			List<Object> found = new LinkedList<>();
			JDWPViewType viewType = __controller.viewType();
			for (Object type : __controller.__allTypes(false))
				if (wantSig.equals(viewType.signature(type)))
					found.add(type);
				
			// Write result
			JDWPPacket rv = __controller.__reply(
				__packet.id(), ErrorType.NO_ERROR);
			
			// Record all classes
			rv.writeInt(found.size());
			for (Object type : found)
			{
				// Store type for later grabbing
				__controller.state.items.put(type);
				
				// Write the class type
				rv.writeByte(JDWPUtils.classType(__controller, type).id);
				rv.writeId(System.identityHashCode(type));
				
				// Classes are always loaded
				rv.writeInt(CommandSetVirtualMachine._CLASS_INITIALIZED);
			}
			
			return rv;
		}
	},
	
	/** All Threads. */
	ALL_THREADS(4)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/03/13
		 */
		@Override
		public JDWPPacket execute(JDWPController __controller,
			JDWPPacket __packet)
			throws JDWPException
		{
			Object[] threads = __controller.__allThreads();
			
			// Write result
			JDWPPacket rv = __controller.__reply(
				__packet.id(), ErrorType.NO_ERROR);
			
			// Write all thread references
			rv.writeInt(threads.length);
			for (Object thread : threads)
				rv.writeId(System.identityHashCode(thread));
			
			return rv;
		}
	},
	
	/** Top level thread groups. */
	TOP_LEVEL_THREAD_GROUPS(5)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/03/13
		 */
		@Override
		public JDWPPacket execute(JDWPController __controller,
			JDWPPacket __packet)
			throws JDWPException
		{
			Object[] groups = __controller.__allThreadGroups();
			
			// Write result
			JDWPPacket rv = __controller.__reply(
				__packet.id(), ErrorType.NO_ERROR);
			
			rv.writeInt(groups.length);
			for (Object group : groups)
				rv.writeId(System.identityHashCode(group));
			
			return rv;
		}
	},
	
	/** Dispose of the debugging connection. */
	DISPOSE(6)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/04/30
		 */
		@Override
		public JDWPPacket execute(JDWPController __controller,
			JDWPPacket __packet)
			throws JDWPException
		{
			// Terminate the connection
			try
			{
				// Terminate the connection forcibly
				__controller.close();
			}
			
			// Perform final cleanup always
			finally
			{
				try
				{
					// Force all threads to resume
					JDWPViewThread viewThread = __controller.viewThread();
					for (Object thread : __controller.__allThreads())
					{
						JDWPThreadSuspension suspension =
							viewThread.suspension(thread);
						while (suspension.query() > 0)
							suspension.resume();
					}
				}
				finally
				{
					// Clear all events
					__controller.eventManager.clearAll();
				}
			}
			
			// No result
			return null;
		}
	},
	
	/** Returns the size of variable data. */
	ID_SIZES(7)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/03/12
		 */
		@Override
		public JDWPPacket execute(JDWPController __controller,
			JDWPPacket __packet)
			throws JDWPException
		{
			JDWPPacket rv = __controller.__reply(
				__packet.id(), ErrorType.NO_ERROR);
			
			// field, method, object, reference, frame
			for (int i = 0; i < 5; i++)
				rv.writeInt(JDWPConstants.ID_SIZE);
			
			return rv;
		}
	},
	
	/** Suspend all threads. */
	SUSPEND(8)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/03/13
		 */
		@Override
		public JDWPPacket execute(JDWPController __controller,
			JDWPPacket __packet)
			throws JDWPException
		{
			// Tell all threads to suspend
			JDWPViewThread view = __controller.viewThread();
			for (Object thread : __controller.__allThreads())
				view.suspension(thread).suspend();
			
			return null;
		}
	},
	
	/** Resume all threads. */
	RESUME(9)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/03/14
		 */
		@Override
		public JDWPPacket execute(JDWPController __controller,
			JDWPPacket __packet)
			throws JDWPException
		{
			// Tell all threads to resume
			JDWPViewThread view = __controller.viewThread();
			for (Object thread : __controller.__allThreads())
				view.suspension(thread).resume();
			
			return null;
		}
	},
	
	/** Force exit the virtual machine. */
	EXIT(10)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/04/30
		 */
		@Override
		public JDWPPacket execute(JDWPController __controller,
			JDWPPacket __packet)
			throws JDWPException
		{
			int code = __packet.readInt();
			
			// Our main VM does not have an exit, however we can tell every
			// group that is running to terminate
			JDWPViewThreadGroup view = __controller.viewThreadGroup();
			for (Object threadGroup : __controller.__allThreadGroups())
				view.exit(threadGroup, code);
			
			return null;
		}
	},
	
	/** Capabilities. */
	CAPABILITIES(12)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/03/13
		 */
		@Override
		public JDWPPacket execute(JDWPController __controller,
			JDWPPacket __packet)
			throws JDWPException
		{
			return this.__capabilities(false, __controller, __packet);
		}
	},
	
	/** Class paths. */
	CLASS_PATHS(13)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/03/14
		 */
		@Override
		public JDWPPacket execute(JDWPController __controller,
			JDWPPacket __packet)
			throws JDWPException
		{
			JDWPPacket rv = __controller.__reply(
				__packet.id(), ErrorType.NO_ERROR);
			
			// Base directory is the current directory
			rv.writeString(".");
			
			// There are no non-platform class paths
			rv.writeInt(0);
			
			// However the boot class path is used to refer to everything
			String[] classPaths = __controller.bind().debuggerLibraries();
			rv.writeInt(classPaths.length);
			for (String p : classPaths)
				rv.writeString(p);
			
			return rv;
		}
	},
	
	/** Hold events, keep them queued and not transmit them. */
	HOLD_EVENTS(15)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/03/12
		 */
		@Override
		public JDWPPacket execute(JDWPController __controller,
			JDWPPacket __packet)
			throws JDWPException
		{
			// Hold any events
			__controller._holdEvents = true;
			return null;
		}
	},
	
	/** Release any events. */
	RELEASE_EVENTS(16)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/03/12
		 */
		@Override
		public JDWPPacket execute(JDWPController __controller,
			JDWPPacket __packet)
			throws JDWPException
		{
			// Resume events and let them flow
			__controller._holdEvents = false;
			return null;
		}
	},
	
	/** New Capabilities. */
	CAPABILITIES_NEW(17)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/03/13
		 */
		@Override
		public JDWPPacket execute(JDWPController __controller,
			JDWPPacket __packet)
			throws JDWPException
		{
			return this.__capabilities(true, __controller, __packet);
		}
	},
	
	/** All loaded classes with generic signature included. */ 
	ALL_CLASSES_WITH_GENERIC_SIGNATURE(20)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/03/13
		 */
		@Override
		public JDWPPacket execute(JDWPController __controller,
			JDWPPacket __packet)
			throws JDWPException
		{
			List<Object> allTypes = __controller.__allTypes(false);
			
			// If we have zero classes then JDB will crash since it always
			// expects at least a single class! So we need to go out of our
			// way to find a class!
			if (allTypes.isEmpty())
			{
				// Try to find a type from the first group we find
				JDWPViewThreadGroup viewGroup = __controller.viewThreadGroup();
				for (Object group : __controller.__allThreadGroups())
				{
					// Do we have the object class?
					Object type = viewGroup.findType(group,
						"java/lang/Object");
					if (type != null)
					{
						// Use this type and register it
						allTypes.add(type);
						__controller.state.items.put(type);
						
						// We found one, so we need not try more
						break;
					}
				}
				
				// If we did not find a class still, just say the VM is dead
				if (allTypes.isEmpty())
					throw ErrorType.VM_DEAD.toss(null, 0);
			}
			
			JDWPPacket rv = __controller.__reply(
				__packet.id(), ErrorType.NO_ERROR);
			
			// Write down all the known classes
			JDWPViewType viewType = __controller.viewType();
			rv.writeInt(allTypes.size());
			for (Object type : allTypes)
			{
				// The type ID
				rv.writeByte(JDWPUtils.classType(__controller, type).id);
				rv.writeId(System.identityHashCode(type));
				
				// The signatures, the generic is ignored
				rv.writeString(viewType.signature(type));
				rv.writeString("");
				
				// All classes are considered initialized
				rv.writeInt(CommandSetVirtualMachine._CLASS_INITIALIZED);
			}
			
			return rv;
		}
	},
		
	/* End. */
	;
	
	/** Class is initialized. */
	private static final int _CLASS_INITIALIZED =
		4;
	
	/** The ID of the packet. */
	public final int id;
	
	/**
	 * Returns the ID used.
	 * 
	 * @param __id The ID used.
	 * @since 2021/03/12
	 */
	CommandSetVirtualMachine(int __id)
	{
		this.id = __id;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/03/12
	 */
	@Override
	public final int debuggerId()
	{
		return this.id;
	}
	
	/**
	 * Returns the capabilities of the debugger and virtual machine.
	 * 
	 * @param __new Use new capabilities?
	 * @param __controller The controller used.
	 * @param __packet The packet to read from.
	 * @return The capabilities of the debugger.
	 * @throws JDWPException If the capabilities could not be returned.
	 * @since 2021/04/30
	 */
	JDWPPacket __capabilities(boolean __new, JDWPController __controller,
		JDWPPacket __packet)
		throws JDWPException
	{
		JDWPPacket rv = __controller.__reply(
			__packet.id(), ErrorType.NO_ERROR);
		
		// canWatchFieldModification
		rv.writeBoolean(true);
		
		// canWatchFieldAccess
		rv.writeBoolean(true);
		
		// canGetBytecodes (supported by SquirrelJME)
		rv.writeBoolean(true);
		
		// canGetSyntheticAttribute
		rv.writeBoolean(false);
		
		// canGetOwnedMonitorInfo
		rv.writeBoolean(false);
		
		// canGetCurrentContendedMonitor
		rv.writeBoolean(false);
		
		// canGetMonitorInfo
		rv.writeBoolean(false);
		
		// New Capabilities
		if (__new)
		{
			// canRedefineClasses
			rv.writeBoolean(false);
			
			// canAddMethod
			rv.writeBoolean(false);
			
			// canUnrestrictedlyRedefineClasses
			rv.writeBoolean(false);
			
			// canPopFrames
			rv.writeBoolean(false);
			
			// canUseInstanceFilters
			rv.writeBoolean(false);
			
			// canGetSourceDebugExtension
			rv.writeBoolean(false);
			
			// canRequestVMDeathEvent
			rv.writeBoolean(false);
			
			// canSetDefaultStratum
			rv.writeBoolean(false);
			
			// canGetInstanceInfo
			rv.writeBoolean(false);
			
			// canRequestMonitorEvents
			rv.writeBoolean(false);
			
			// canGetMonitorFrameInfo
			rv.writeBoolean(false);
			
			// canUseSourceNameFilters
			rv.writeBoolean(false);
			
			// canGetConstantPool
			rv.writeBoolean(false);
			
			// canForceEarlyReturn
			rv.writeBoolean(false);
			
			// Reserved
			for (int i = 22; i <= 32; i++)
				rv.writeBoolean(false);
		}
		
		return rv;
	}
}
