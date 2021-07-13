// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.emulator.uiform;

import cc.squirreljme.jvm.mle.callbacks.UIFormCallback;
import cc.squirreljme.jvm.mle.constants.UIItemType;
import cc.squirreljme.jvm.mle.constants.UIKeyEventType;
import cc.squirreljme.jvm.mle.constants.UIListType;
import cc.squirreljme.jvm.mle.constants.UIWidgetProperty;
import cc.squirreljme.jvm.mle.exceptions.MLECallError;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * List.
 *
 * @since 2020/07/18
 */
public class SwingItemList
	extends SwingItem
	implements ListSelectionListener, KeyListener, MouseListener
{
	/** Scrolling for the list. */
	final JScrollPane _scroll;
	
	/** The model for the list. */
	final DefaultListModel<ListEntry> _model;
	
	/** The list used. */
	final JList<ListEntry> _list;
	
	/** Is the enter command being used to select an item? */
	private boolean _enterCommand;
	
	/**
	 * Initializes the item.
	 * 
	 * @since 2020/07/18
	 */
	public SwingItemList()
	{
		super(UIItemType.LIST);
		
		DefaultListModel<ListEntry> model = new DefaultListModel<>();
		this._model = model;
		
		// Use custom renderer for the list
		JList<ListEntry> list = new JList<ListEntry>(model);
		list.setCellRenderer(new ListRenderer());
		
		this._list = list;
		
		// Force the list to show vertically
		list.setLayoutOrientation(JList.VERTICAL);
		
		// Register a listener for selection changes
		list.addListSelectionListener(this);
		
		// Register listener to listen for enter/return to select an item
		list.addKeyListener(this);
		list.addMouseListener(this);
		
		// Setup scrolling for the list
		this._scroll = new JScrollPane(list,
			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/11/17
	 */
	@Override
	public void addedOnForm(SwingForm __form, int __pos)
	{
		// Request that all icons be refreshed accordingly, this requires
		// that a callback be defined for the item which it should be when it
		// is part of the form
		DefaultListModel<ListEntry> model = this._model;
		for (int i = 0, n = model.size(); i < n; i++)
			this.__requestIconUpdate(i, model.get(i)._iconDimension);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/18
	 */
	@Override
	public JScrollPane component()
	{
		// Use the scroll, so that we get that scrolling
		return this._scroll;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/18
	 */
	@Override
	public void deletePost()
	{
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/12/28
	 */
	@Override
	public void keyPressed(KeyEvent __e)
	{
		// Only emit if we desire this behavior and specific keys were typed
		if (this._enterCommand && (__e.getKeyCode() == KeyEvent.VK_ENTER ||
			__e.getKeyCode() == KeyEvent.VK_SPACE))
			this.__activate();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/12/28
	 */
	@Override
	public void keyReleased(KeyEvent __e)
	{
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/12/28
	 */
	@Override
	public void keyTyped(KeyEvent __e)
	{
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/12/28
	 */
	@Override
	public void mouseClicked(MouseEvent __e)
	{
		// Only emit if we desire this behavior and we double clicked
		if (this._enterCommand && __e.getClickCount() >= 2)
			this.__activate();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/12/28
	 */
	@Override
	public void mouseEntered(MouseEvent __e)
	{
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/12/28
	 */
	@Override
	public void mouseExited(MouseEvent __e)
	{
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/12/28
	 */
	@Override
	public void mousePressed(MouseEvent __e)
	{
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/12/28
	 */
	@Override
	public void mouseReleased(MouseEvent __e)
	{
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/18
	 */
	@Override
	public void property(int __intProp, int __sub, int __newValue)
		throws MLECallError
	{
		DefaultListModel<ListEntry> model = this._model;
		JList<ListEntry> list = this._list;
		int n = model.getSize();
		
		try
		{
			ListEntry elem;
			
			// Depends on which action was performed
			switch (__intProp)
			{
				case UIWidgetProperty.INT_NUM_ELEMENTS:
					if (__newValue < 0)
						throw new MLECallError("Negative list size.");
					
					while (__newValue < n)
						model.removeElementAt(--n);
					while (__newValue > n)
						model.add(n++, new ListEntry());
					break;
				
				case UIWidgetProperty.INT_LIST_ITEM_ID_CODE:
					model.get(__sub)._idCode = __newValue;
					break;
				
				case UIWidgetProperty.INT_LIST_ITEM_DISABLED:
					{
						boolean nowDisabled = (__newValue != 0);
						
						elem = model.get(__sub);
						elem._disabled = nowDisabled;
						
						// Disabling an element means it cannot be selected
						if (nowDisabled)
						{
							elem._selected = false;
							list.removeSelectionInterval(__sub, __sub);
						}
					}
					break;
				
				case UIWidgetProperty.INT_LIST_ITEM_SELECTED:
					{
						elem = model.get(__sub);
						
						// Disabled items cannot be selected at all
						boolean sel = (__newValue != 0);
						if (elem._disabled && sel)
							break;
						
						// Update the model
						elem._selected = sel;
						
						// There are two different methods for selections
						if (sel)
							list.addSelectionInterval(__sub,__sub);
						else
							list.removeSelectionInterval(__sub, __sub);
					}
					break;
				
				case UIWidgetProperty.INT_LIST_ITEM_ICON_DIMENSION:
					this.__requestIconUpdate(__sub, __newValue);
					break;
				
				case UIWidgetProperty.INT_LIST_ITEM_FONT:
					model.get(__sub)._fontDescription = __newValue;
					break;
				
					// Change of the list type
				case UIWidgetProperty.INT_LIST_TYPE:
					this.__changeListType(__newValue);
					break;
				
				default:
					throw new MLECallError("" + __intProp);
			}
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new MLECallError("Invalid index: " + __sub, e);
		}
		
		// Always make sure list is updated
		finally
		{
			list.repaint();
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/18
	 */
	@Override
	public void property(int __strProp, int __sub, String __newValue)
	{
		DefaultListModel<ListEntry> model = this._model;
		JList<ListEntry> list = this._list;
		int n = model.getSize();
		
		try
		{
			switch (__strProp)
			{
				case UIWidgetProperty.STRING_LIST_ITEM_LABEL:
					model.get(__sub)._label = __newValue;
					break;
				
				default:
					throw new MLECallError("" + __strProp);
			}
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new MLECallError("Invalid index: " + __sub, e);
		}
		
		// Always make sure list is updated
		finally
		{
			list.repaint();
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/10/31
	 */
	@Override
	public int propertyInt(int __intProp, int __sub)
		throws MLECallError
	{
		DefaultListModel<ListEntry> model = this._model;
		JList<ListEntry> list = this._list;
		
		try
		{
			switch (__intProp)
			{
				case UIWidgetProperty.INT_NUM_ELEMENTS:
					return model.size();
				
				case UIWidgetProperty.INT_LIST_ITEM_ID_CODE:
					return model.get(__sub)._idCode;
				
				case UIWidgetProperty.INT_LIST_ITEM_DISABLED:
					return (model.get(__sub)._disabled ? 1 : 0);
				
				case UIWidgetProperty.INT_LIST_ITEM_SELECTED:
					return (model.get(__sub)._selected ? 1 : 0);
				
				case UIWidgetProperty.INT_LIST_ITEM_ICON_DIMENSION:
					return model.get(__sub)._iconDimension;
				
				case UIWidgetProperty.INT_LIST_ITEM_FONT:
					return model.get(__sub)._fontDescription;
				
				default:
					return super.propertyInt(__intProp, __sub);
			}
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new MLECallError("Invalid index: " + __sub, e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/11/17
	 */
	@Override
	public String propertyStr(int __strProp, int __sub)
		throws MLECallError
	{
		DefaultListModel<ListEntry> model = this._model;
		JList<ListEntry> list = this._list;
		
		try
		{
			switch (__strProp)
			{
				case UIWidgetProperty.STRING_LIST_ITEM_LABEL:
					return model.get(__sub)._label;
				
				default:
					return super.propertyStr(__strProp, __sub);
			}
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new MLECallError("Invalid index: " + __sub, e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/12/28
	 */
	@Override
	public void valueChanged(ListSelectionEvent __e)
	{
		// Do not emit selection changes if it is still being changed, since
		// this could result in a rush of events
		if (__e.getValueIsAdjusting())
			return;
		
		// Can only do something if there is a form and callback
		SwingForm form = this.form();
		UIFormCallback callback = (form != null ? form.callback() : null);
		if (form == null || callback == null)
			return;
		
		// The list selection is being updated
		int keyCode = new Random().nextInt();
		callback.propertyChange(form, this,
			UIWidgetProperty.INT_UPDATE_LIST_SELECTION_LOCK, 0,
			0, keyCode);
		
		// Send updates on all the items which were selected or not
		JList<ListEntry> list = this._list;
		DefaultListModel<ListEntry> model = this._model;
		for (int dx = __e.getFirstIndex(),
			end = Math.min(__e.getLastIndex(), model.size() - 1);
			dx <= end; dx++)
		{
			ListEntry entry = model.get(dx);
			boolean sel = list.isSelectedIndex(dx);
			
			// If this item was somehow disabled, then remove selection from it
			if (sel && entry._disabled)
			{
				entry._selected = false;
				sel = false;
			}
			
			// Update the selection model for the list
			boolean was = entry._selected;
			entry._selected = sel;
			
			// Inform the list of the change, if it actually happened
			if (was != sel)
				callback.propertyChange(form, this,
					UIWidgetProperty.INT_LIST_ITEM_SELECTED, dx,
					keyCode, (sel ? 1 : 0));
		}
		
		// List no longer being updated
		callback.propertyChange(form, this,
			UIWidgetProperty.INT_UPDATE_LIST_SELECTION_LOCK, 0,
			keyCode, 0);
	}
	
	/**
	 * Activates this list.
	 * 
	 * @since 2020/12/28
	 */
	private void __activate()
	{
		// Can only do something if there is a form and callback
		SwingForm form = this.form();
		UIFormCallback callback = (form != null ? form.callback() : null);
		if (form == null || callback == null)
			return;
		
		callback.eventKey(form, this,
			UIKeyEventType.COMMAND_ACTIVATED, 0, 0);
	}
	
	/**
	 * Changes the type of list used.
	 * 
	 * @param __type The type of list to use.
	 * @since 2020/12/28
	 */
	private void __changeListType(int __type)
	{
		// Which model are we using?
		int mode;
		switch (__type)
		{
				// These are both effectively the same, except one always
				// selects the focused item
			case UIListType.IMPLICIT:
			case UIListType.EXCLUSIVE:
				mode = ListSelectionModel.SINGLE_SELECTION;
				break;
			
			case UIListType.MULTIPLE:
				mode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
				break;
			
			default:
				return;
		}
		
		// Use this one
		this._list.setSelectionMode(mode);
		
		// Is the enter command used?
		this._enterCommand = (__type == UIListType.IMPLICIT);
	}
	
	/**
	 * Requests that the icon be updated.
	 * 
	 * @param __sub The item.
	 * @param __dim The dimension to use.
	 * @since 2020/11/17
	 */
	private void __requestIconUpdate(int __sub, int __dim)
	{
		ListEntry entry = this._model.get(__sub);
		entry._iconDimension = __dim;
		
		// Make the icon dirty, to make it update
		entry._dirtyIcon = true;
		
		// Clear the icon?
		if (__dim <= 0)
		{
			entry._icon = null;
			return;
		}
		
		// Can only do something if there is a form and callback
		SwingForm form = this.form();
		UIFormCallback callback = (form != null ? form.callback() : null);
		if (form == null || callback == null)
			return;
		
		// Send in request to update the icon accordingly
		SwingUtilities.invokeLater(new ListIconUpdate(
			callback, form, this, entry, __sub, __dim));
	}
}
