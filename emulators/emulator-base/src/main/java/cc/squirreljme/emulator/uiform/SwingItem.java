// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.emulator.uiform;

import cc.squirreljme.jvm.mle.brackets.UIItemBracket;
import cc.squirreljme.jvm.mle.callbacks.UIFormCallback;
import cc.squirreljme.jvm.mle.constants.UIItemType;
import cc.squirreljme.jvm.mle.constants.UIWidgetProperty;
import cc.squirreljme.jvm.mle.exceptions.MLECallError;
import javax.swing.JComponent;

/**
 * This is the base call for all of the item types that are implementing on
 * Swing.
 *
 * @since 2020/07/18
 */
public abstract class SwingItem
	implements UIItemBracket, SwingWidget
{
	/** The {@link UIItemType}. */
	public final int itemType;
	
	/** The form the item is on. */
	SwingForm _form;
	
	/** Has this item been deleted? */
	boolean _isDeleted;
	
	/**
	 * Initializes the base item.
	 * 
	 * @param __itemType The {@link UIItemType}.
	 * @since 2020/11/14
	 */
	protected SwingItem(int __itemType)
	{
		this.itemType = __itemType;
	}
	
	/**
	 * Returns the component item.
	 * 
	 * @return The component item.
	 * @since 2020/07/18
	 */
	public abstract JComponent component();
	
	/**
	 * Deletes the item.
	 * 
	 * @since 2020/07/18
	 */
	public abstract void deletePost();
	
	/**
	 * This may be overridden by items that need to know when they are added
	 * to forms in the event they need to perform callbacks and otherwise.
	 * 
	 * @param __form The form this was added on.
	 * @param __pos The position of the item.
	 * @since 2020/11/17
	 */
	public void addedOnForm(SwingForm __form, int __pos)
	{
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/10/17
	 */
	@Override
	public final UIFormCallback callback()
	{
		SwingForm form = this._form;
		if (form == null)
			return null;
		
		return form.callback();
	}
	
	/**
	 * Deletes the given item.
	 * 
	 * @throws MLECallError If it could not deleted.
	 * @since 2020/07/19
	 */
	public final void delete()
		throws MLECallError
	{
		synchronized (this)
		{
			if (this._isDeleted)
				throw new MLECallError("Item already deleted.");
			
			if (this._form != null)
				throw new MLECallError("Item is part of a form.");
			
			// Mark as deleted
			this._isDeleted = true;
			
			// Call post deletion handler for cleanup
			this.deletePost();
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/10/17
	 */
	@Override
	public SwingForm form()
	{
		return this._form;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/10/17
	 */
	@Override
	public UIItemBracket item()
	{
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/09/21
	 */
	@Override
	public int propertyInt(int __intProp, int __sub)
		throws MLECallError
	{
		switch (__intProp)
		{
				// Form width
			case UIWidgetProperty.INT_WIDTH:
				return this.component().getWidth();
				
				// Form height
			case UIWidgetProperty.INT_HEIGHT:
				return this.component().getHeight();
			
			default:
				throw new MLECallError("Unknown IntProp: " + __intProp);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/09/21
	 */
	@Override
	public String propertyStr(int __strProp, int __sub)
		throws MLECallError
	{
		switch (__strProp)
		{
			default:
				throw new MLECallError("Unknown IntProp: " + __strProp);
		}
	}
}
