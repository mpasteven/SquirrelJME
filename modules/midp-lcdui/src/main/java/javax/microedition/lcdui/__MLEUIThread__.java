// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package javax.microedition.lcdui;

import cc.squirreljme.jvm.mle.UIFormShelf;
import cc.squirreljme.jvm.mle.brackets.UIFormBracket;
import cc.squirreljme.jvm.mle.brackets.UIItemBracket;
import cc.squirreljme.jvm.mle.callbacks.UIDisplayCallback;
import cc.squirreljme.jvm.mle.callbacks.UIFormCallback;
import cc.squirreljme.jvm.mle.constants.NonStandardKey;
import cc.squirreljme.jvm.mle.constants.UIItemPosition;
import cc.squirreljme.jvm.mle.constants.UIKeyEventType;
import cc.squirreljme.jvm.mle.constants.UIKeyModifier;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.runtime.lcdui.mle.DisplayWidget;
import cc.squirreljme.runtime.lcdui.mle.PencilGraphics;
import cc.squirreljme.runtime.lcdui.mle.StaticDisplayState;
import cc.squirreljme.runtime.lcdui.mle.UIBackend;
import cc.squirreljme.runtime.lcdui.mle.UIBackendFactory;
import cc.squirreljme.runtime.midlet.ActiveMidlet;
import com.nokia.mid.ui.FullCanvas;
import java.util.Map;
import javax.microedition.midlet.MIDlet;

/**
 * This thread is responsible for handling graphics operations.
 *
 * @since 2020/09/12
 */
final class __MLEUIThread__
	implements Runnable, UIDisplayCallback, UIFormCallback
{
	/** The time to sleep for between periodic checks. */
	private static final long _SLEEP_TIME =
		5_000;
	
	/** Are we within a paint? */
	private boolean _inPaint;
	
	/**
	 * {@inheritDoc}
	 * @since 2020/09/12
	 */
	@Override
	public void eventKey(UIFormBracket __form, UIItemBracket __item,
		int __event, int __keyCode, int __modifiers)
	{
		// Debug
		/*Debugging.debugNote("eventKey(%08x, %08x, %d, %d, %x)",
			System.identityHashCode(__form), System.identityHashCode(__item),
			__event, __keyCode, __modifiers);*/
		
		DisplayWidget widget = StaticDisplayState.locate(__item);
		
		// Get J2ME specific modifiers since we have extra ones undefined by
		// J2ME
		int javaMods = __modifiers & UIKeyModifier.J2ME_MASK;
		
		// If both modifier keys are held down, treat it as the center key
		// This is kind of a shortcut way since SquirrelJME only knows about
		// the Left/Right and there are just not enough buttons available.
		// Note that the F keys count as special menu keys!
		// So this way F1/Soft1 + F2/Soft2 == F3/Soft3!
		if (((__modifiers & UIKeyModifier.MODIFIER_LEFT_RIGHT_COMMANDS) ==
			UIKeyModifier.MODIFIER_LEFT_RIGHT_COMMANDS) &&
			((__keyCode == NonStandardKey.F1 ||
				__keyCode == NonStandardKey.VGAME_COMMAND_LEFT) ||
			(__keyCode == NonStandardKey.F2 ||
				__keyCode == NonStandardKey.VGAME_COMMAND_RIGHT)))
			__keyCode = NonStandardKey.VGAME_COMMAND_CENTER;
		
		// Is this a special activation of command keys? 
		if ((__keyCode >= NonStandardKey.F1 &&
			__keyCode <= NonStandardKey.F24) ||
			__keyCode == NonStandardKey.VGAME_COMMAND_LEFT ||
			__keyCode == NonStandardKey.VGAME_COMMAND_CENTER ||
			__keyCode == NonStandardKey.VGAME_COMMAND_RIGHT)
		{
			// Do not multiply activate these just in case
			if (__event != UIKeyEventType.KEY_PRESSED)
				return;
			
			// Which position will be looked at?
			int pos = UIItemPosition.NOT_ON_FORM;
			switch (__keyCode)
			{
				case NonStandardKey.F1:
				case NonStandardKey.VGAME_COMMAND_LEFT:
					pos = UIItemPosition.LEFT_COMMAND;
					break;
				
				case NonStandardKey.F2:
				case NonStandardKey.VGAME_COMMAND_RIGHT:
					pos = UIItemPosition.RIGHT_COMMAND;
					break;
				
				default:
					break;
			}
			
			// Provided the position is valid, try to find an item to activate
			if (pos != UIItemPosition.NOT_ON_FORM)
			{
				// Locate the item and try to execute the command if it is one
				UIItemBracket item = UIFormShelf.formItemAtPosition(
					__form, pos);
				if (item != null)
				{
					// If this is mapped to a command then activate it
					DisplayWidget attempt = StaticDisplayState.locate(__item);
					if (attempt instanceof __CommandWidget__)
						((__CommandWidget__)attempt).__activate();
					
					// Stop here
					return;
				}
			}
			
			// Nokia exposes these as physical Key IDs, so do the same here
			// Since most software is made for Nokia we pretty much the
			// standard and as such have to support doing it this way.
			switch (__keyCode)
			{
				case NonStandardKey.F1:
				case NonStandardKey.VGAME_COMMAND_LEFT:
					__keyCode = FullCanvas.KEY_SOFTKEY1;
					break;
					
				case NonStandardKey.F2:
				case NonStandardKey.VGAME_COMMAND_RIGHT:
					__keyCode = FullCanvas.KEY_SOFTKEY2;
					break;
					
				case NonStandardKey.F3:
				case NonStandardKey.VGAME_COMMAND_CENTER:
					__keyCode = FullCanvas.KEY_SOFTKEY3;
					break;
			}
		}
		
		// Commands are special key events
		if (__event == UIKeyEventType.COMMAND_ACTIVATED)
		{
			// Command button widgets will activate the given command
			if (widget instanceof __CommandWidget__)
				((__CommandWidget__)widget).__activate();
			
			// List activations will activate the given list item
			else if (widget instanceof List)
				((List)widget).__selectCommand(__keyCode);
		}
		
		// Any Displayable which have standard key access
		else if (widget instanceof Canvas)
			this.__eventKey((Canvas)widget, null,
				__event, __keyCode, javaMods);
		else if (widget instanceof CustomItem)
			this.__eventKey(null, (CustomItem)widget,
				__event, __keyCode, javaMods);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/09/12
	 */
	@Override
	public void eventMouse(UIFormBracket __form, UIItemBracket __item,
		int __event, int __button, int __x, int __y, int __modifiers)
	{
		// Debug
		/*Debugging.debugNote("eventMouse(%08x, %08x, %d, %d, %d, %d, %x)",
			System.identityHashCode(__form), System.identityHashCode(__item),
			__event, __button, __x, __y, __modifiers);*/
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/09/12
	 */
	@Override
	public void exitRequest(UIFormBracket __form)
	{
		// Debug
		/*Debugging.debugNote("exitRequest(%08x) @ %s",
			System.identityHashCode(__form), Thread.currentThread());*/
		
		// Terminate the user interface
		StaticDisplayState.terminate();
		
		// Have the MIDlet destroy itself
		try
		{
			MIDlet midlet = ActiveMidlet.get();
			
			// Destroy the midlet
			midlet.notifyDestroyed();
		}
		
		// No active MIDlet, ignore
		catch (IllegalStateException ignored)
		{
			Debugging.debugNote("No current MIDlet?");
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/10/03
	 */
	@Override
	public void later(int __displayId, int __serialId)
	{
		// Call with the wrong ID? Ignore this
		if (__displayId != System.identityHashCode(this))
			return;
		
		// Look to see if it is a valid call
		Integer key = __serialId;
		synchronized (Display.class)
		{
			Map<Integer, Runnable> serialRuns = Display._SERIAL_RUNS;
			
			// Run it
			Runnable runner = serialRuns.get(key);
			if (runner != null)
				try
				{
					runner.run();
				}
				finally
				{
					// Always clear it, even with failures
					serialRuns.remove(key);
					
					// Notify all the threads that something happened
					Display.class.notifyAll();
				}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/09/12
	 */
	@Override
	public void paint(UIFormBracket __form, UIItemBracket __item, int __pf,
		int __bw, int __bh, Object __buf, int __offset, int[] __pal, int __sx,
		int __sy, int __sw, int __sh, int __special)
	{
		// Debug
		/*Debugging.debugNote("paint(%08x, %08x, %d, " +
			"%d, %d, %s, %d, %s, " +
			"%d, %d, %d, %d)",
			System.identityHashCode(__form), System.identityHashCode(__item),
			__pf, __bw, __bh, __buf, __offset, __pal, __sx, __sy, __sw, __sh);
		*/
		
		DisplayWidget widget = StaticDisplayState.locate(__form);
		if (widget instanceof __CommonWidget__)
		{
			// Ignore widgets which do not want the paint event
			__CommonWidget__ common = (__CommonWidget__)widget;
			if (!common.__isPainted())
				return;
				
			// Since painting is a heavy operation, only allow single paints to
			// happen at a time. This should generally never be called
			// concurrently ever.
			synchronized (this)
			{
				if (this._inPaint)
					return;
				this._inPaint = true;
			}
				
			// The paint flag always needs to be cleared when finished
			try
			{
				// Try to use hardware accelerated graphics where possible
				Graphics gfx = PencilGraphics.hardwareGraphics(__pf,
					__bw, __bh, __buf, __offset, __pal, __sx, __sy,
					__sw, __sh);
				
				// Forward the paint call
				common.__paint(gfx, __sw, __sh, __special);
			}
			finally
			{
				this._inPaint = false;
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/09/12
	 */
	@Override
	public void propertyChange(UIFormBracket __form, UIItemBracket __item,
		int __intProp, int __sub, int __old, int __new)
	{
		// Debug
		/*Debugging.debugNote("propertyChange(%08x, %08x, %d, %d, %d)",
			System.identityHashCode(__form), System.identityHashCode(__item),
			__intProp, __old, __new);*/
			
		// Forward to handler
		DisplayWidget widget = StaticDisplayState.locate(__form);
		if (widget instanceof __CommonWidget__)
			((__CommonWidget__)widget).__propertyChange(__form, __item,
				__intProp, __sub, __old, __new);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/09/12
	 */
	@Override
	public void propertyChange(UIFormBracket __form, UIItemBracket __item,
		int __strProp, int __sub, String __old, String __new)
	{
		// Debug
		/*Debugging.debugNote("propertyChange(%08x, %08x, %d, %s, %s)",
			System.identityHashCode(__form), System.identityHashCode(__item),
			__strProp, __old, __new);*/
		
		// Forward to handler
		DisplayWidget widget = StaticDisplayState.locate(__form);
		if (widget instanceof __CommonWidget__)
			((__CommonWidget__)widget).__propertyChange(__form, __item,
				__strProp, __sub, __old, __new);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/09/12
	 */
	@Override
	public void run()
	{
		// Get the backend calls will occur on
		UIBackend backend = UIBackendFactory.getInstance();
		
		// Infinite UI loop, which performs periodic probes and background
		// actions
		for (;;)
		{
			// Stop if terminating
			if (StaticDisplayState.isTerminating())
				break;
			
			// Wait for the next run
			try
			{
				Thread.sleep(__MLEUIThread__._SLEEP_TIME);
			}
			catch (InterruptedException ignored)
			{
			}
		}
		
		// Debug
		Debugging.debugNote("UI loop terminated.");
		
		// Destroy everything possible
		StaticDisplayState.destroy();
	}
	
	/**
	 * Performs the actual handling of key events with the common canvas and
	 * custom items.
	 * 
	 * @param __canvas The Canvas used
	 * @param __cItem The custom item.
	 * @param __event The {@link UIKeyEventType}.
	 * @param __keyCode The key code, may be a character or special key.
	 * @param __modifiers The modifiers for the key.
	 * @throws IllegalArgumentException If both arguments are null or not
	 * null.
	 * @since 2020/10/16
	 */
	private void __eventKey(Canvas __canvas, CustomItem __cItem, int __event,
		int __keyCode, int __modifiers)
		throws IllegalArgumentException, NullPointerException
	{
		if ((__canvas == null) == (__cItem == null))
			throw new IllegalArgumentException("NARG");
		
		// Potential key listener that is shared between both
		KeyListener defaultKL, customKL;
		if (__canvas != null)
		{
			defaultKL = __canvas.__defaultKeyListener();
			customKL = __canvas._keyListener;
		}
		
		// Otherwise custom items are used
		else
		{
			defaultKL = __cItem.__defaultKeyListener();
			customKL = __cItem._keyListener;
		}
		
		// Forward the event accordingly
		switch (__event)
		{
			case UIKeyEventType.KEY_PRESSED:
				defaultKL.keyPressed(__keyCode, __modifiers);
				if (customKL != null)
					customKL.keyPressed(__keyCode, __modifiers);
				break;
			
			case UIKeyEventType.KEY_RELEASE:
				defaultKL.keyReleased(__keyCode, __modifiers);
				if (customKL != null)
					customKL.keyReleased(__keyCode, __modifiers);
				break;
			
			case UIKeyEventType.KEY_REPEATED:
				defaultKL.keyRepeated(__keyCode, __modifiers);
				if (customKL != null)
					customKL.keyRepeated(__keyCode, __modifiers);
				break;
		}
	}
}
