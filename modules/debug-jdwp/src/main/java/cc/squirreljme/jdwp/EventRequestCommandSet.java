// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jdwp;

import cc.squirreljme.runtime.cldc.debug.Debugging;

/**
 * Event request command set.
 *
 * @since 2021/03/12
 */
public enum EventRequestCommandSet
	implements JDWPCommand
{
	/** Set event requests. */
	SET(1)
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
				__packet.id(), JDWPErrorType.NO_ERROR);
			
			// Which kind of event? If not supported report not implemented
			EventKind eventKind = EventKind.of(__packet.readByte());
			if (eventKind == null)
				throw Debugging.todo();
			
			// How does this suspend?
			SuspendPolicy suspendPolicy =
				SuspendPolicy.of(__packet.readByte());
			
			// Modifier kinds
			int numModifiers = __packet.readInt();
			for (int i = 0; i < numModifiers; i++)
			{
				EventModKind modKind = EventModKind.of(__packet.readByte());
				switch (modKind)
				{
					case OCCURANCE_COUNTDOWN:
						throw Debugging.todo();
					
					case CONDITIONAL:
						throw Debugging.todo();
					
					case ONLY_IN_THREAD:
						throw Debugging.todo();
					
					case ONLY_IN_CLASS:
						throw Debugging.todo();
						
					case ONLY_IN_CLASS_PATTERN:
						throw Debugging.todo();
					
					case NOT_IN_CLASS_PATTERN:
						throw Debugging.todo();
					
					case LOCATION:
						throw Debugging.todo();
					
					case EXCEPTION:
						throw Debugging.todo();
					
					case FIELD:
						throw Debugging.tood();
					
					case CALL_STACK:
						throw Debugging.todo();
					
					case THIS_OBJECT:
						throw Debugging.todo();
					
					case SOURCE_FILENAME_PATTERN:
						throw Debugging.todo();
					
						// Report error on invalid kinds
					default:
						throw Debugging.todo();
				}
			}
			
			return rv;
		}
	},
	
	/* End. */
	;
	
	/** The ID of the packet. */
	public final int id;
	
	/**
	 * Returns the ID used.
	 * 
	 * @param __id The ID used.
	 * @since 2021/03/12
	 */
	EventRequestCommandSet(int __id)
	{
		this.id = __id;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/03/12
	 */
	@Override
	public final int id()
	{
		return this.id;
	}
}
