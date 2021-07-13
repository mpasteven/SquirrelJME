// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.runtime.lcdui.mle;

import cc.squirreljme.jvm.mle.UIFormShelf;
import cc.squirreljme.jvm.mle.brackets.UIDisplayBracket;
import cc.squirreljme.jvm.mle.brackets.UIFormBracket;
import cc.squirreljme.jvm.mle.brackets.UIItemBracket;
import cc.squirreljme.jvm.mle.brackets.UIWidgetBracket;
import cc.squirreljme.jvm.mle.callbacks.UIDisplayCallback;
import cc.squirreljme.jvm.mle.callbacks.UIFormCallback;
import cc.squirreljme.jvm.mle.constants.UIItemPosition;
import cc.squirreljme.jvm.mle.constants.UIItemType;
import cc.squirreljme.jvm.mle.constants.UIMetricType;
import cc.squirreljme.jvm.mle.constants.UIWidgetProperty;
import cc.squirreljme.jvm.mle.exceptions.MLECallError;

/**
 * This interface is used as a wrapper around {@link UIFormShelf}, it is
 * implemented by a class and essentially is used to forward calls to the
 * true backing implementation. This is here so that in the event that
 * {@link UIFormShelf} is not supported, that there is a fall-back.
 *
 * @since 2020/06/30
 */
public interface UIBackend
{
	/**
	 * Registers a display callback that is to be called when information about
	 * displays changes.
	 * 
	 * @param __ref The object this refers to, if it gets garbage collected
	 * then this becomes invalidated.
	 * @param __dc The display callback to use.
	 * @throws MLECallError On null arguments.
	 * @since 2020/10/03
	 */
	void callback(Object __ref, UIDisplayCallback __dc)
		throws MLECallError;
	
	/**
	 * This is used to register the callback which is called with the user
	 * interface events and otherwise.
	 * 
	 * @param __form The form that the callback will act under.
	 * @param __callback The callback to register.
	 * @throws MLECallError If {@code __form} is {@code null}.
	 * @since 2020/07/03
	 */
	void callback(UIFormBracket __form, UIFormCallback __callback)
		throws MLECallError;
	
	/**
	 * Returns the displays which are attached to the system.
	 * 
	 * @return The display heads which are attached to a system, these objects
	 * here represent physical displays.
	 * @throws MLECallError If there are no displays.
	 * @since 2020/07/01
	 */
	UIDisplayBracket[] displays()
		throws MLECallError;
	
	/**
	 * Returns the form that is currently being shown on the display.
	 * 
	 * @param __display The display to query.
	 * @return The form that is currently shown or {@code null}.
	 * @throws MLECallError On null arguments.
	 * @since 2020/07/01
	 */
	UIFormBracket displayCurrent(UIDisplayBracket __display)
		throws MLECallError;
	
	/**
	 * Show the given form on the display.
	 * 
	 * @param __display The form to display on screen.
	 * @param __form The form to display, if {@code null} then no item is
	 * shown on the display.
	 * @throws MLECallError On {@code __display} is {@code null}.
	 * @since 2020/07/01
	 */
	void displayShow(UIDisplayBracket __display, UIFormBracket __form)
		throws MLECallError;
	
	/**
	 * Checks if the two displays represent the same {@link UIDisplayBracket}.
	 * 
	 * @param __a The first.
	 * @param __b The second.
	 * @return If these are the same display.
	 * @throws MLECallError If either is {@code null}.
	 * @since 2020/07/01
	 */
	boolean equals(UIDisplayBracket __a, UIDisplayBracket __b)
		throws MLECallError;
	
	/**
	 * Checks if the two displays represent the same {@link UIFormBracket}.
	 * 
	 * @param __a The first.
	 * @param __b The second.
	 * @return If these are the same form.
	 * @throws MLECallError If either is {@code null}.
	 * @since 2020/07/01
	 */
	boolean equals(UIFormBracket __a, UIFormBracket __b)
		throws MLECallError;
	
	/**
	 * Checks if the two displays represent the same {@link UIItemBracket}.
	 * 
	 * @param __a The first.
	 * @param __b The second.
	 * @return If these are the same item.
	 * @throws MLECallError If either is {@code null}.
	 * @since 2020/07/01
	 */
	boolean equals(UIItemBracket __a, UIItemBracket __b)
		throws MLECallError;
	
	/**
	 * Checks if the two displays represent the same {@link UIWidgetBracket}.
	 * 
	 * @param __a The first.
	 * @param __b The second.
	 * @return If these are the same item.
	 * @throws MLECallError If either is {@code null}.
	 * @since 2020/09/20
	 */
	boolean equals(UIWidgetBracket __a, UIWidgetBracket __b)
		throws MLECallError;
	
	/**
	 * Flushes all of the events and forces them to be processed.
	 * 
	 * @throws MLECallError If events could not be flushed.
	 * @since 2020/07/26
	 */
	void flushEvents()
		throws MLECallError;
	
	/**
	 * Deletes the given form.
	 * 
	 * @param __form The form to delete.
	 * @throws MLECallError On null arguments or the form could not be deleted.
	 * @since 2020/07/01
	 */
	void formDelete(UIFormBracket __form)
		throws MLECallError;
	
	/**
	 * Returns the item at the given location.
	 * 
	 * @param __form The form to check.
	 * @param __pos The position to check, may be {@link UIItemPosition}.
	 * @return The item at the given position or {@code null} if it is not
	 * there.
	 * @throws MLECallError If the form is not valid or if the position is
	 * not valid.
	 * @since 2020/07/19
	 */
	UIItemBracket formItemAtPosition(UIFormBracket __form, int __pos)
		throws MLECallError;
	
	/**
	 * Returns the number of items on the form.
	 * 
	 * @param __form The form to get the count from.
	 * @return The number of normal items on the form.
	 * @throws MLECallError If the form is null or not valid.
	 * @since 2020/07/19
	 */
	int formItemCount(UIFormBracket __form)
		throws MLECallError;
	
	/**
	 * Returns the position of the item on the given form, or if it is not
	 * on the form.
	 * 
	 * @param __form The form to get from.
	 * @param __item The item being queried.
	 * @return The position of the item, may be one of
	 * {@link UIItemPosition} or
	 * {@link UIItemPosition#NOT_ON_FORM} if not on the form.
	 * @throws MLECallError If the form and item are null.
	 * @since 2020/07/18
	 */
	int formItemPosition(UIFormBracket __form, UIItemBracket __item)
		throws MLECallError;
	
	/**
	 * Sets the position of a form's item.
	 * 
	 * @param __form The form to set on.
	 * @param __item The item to set.
	 * @param __pos The position to set the item at, may be one of
	 * {@link UIItemPosition}.
	 * @throws MLECallError If the form or item are null, or the position
	 * is not valid.
	 * @since 2020/07/18
	 */
	void formItemPosition(UIFormBracket __form, UIItemBracket __item,
		int __pos)
		throws MLECallError;
	
	/**
	 * Removes the item at the given position on the form.
	 * 
	 * @param __form The form to remove from.
	 * @param __pos The item to be removed.
	 * @return The item that was removed.
	 * @throws MLECallError If the form or item are null, the position is
	 * not valid, or there was no item at the position.
	 * @since 2020/07/18
	 */
	UIItemBracket formItemRemove(UIFormBracket __form, int __pos)
		throws MLECallError;
	
	/**
	 * Creates a new form.
	 * 
	 * @return The newly created form.
	 * @throws MLECallError If the form could not be created.
	 * @since 2020/07/01
	 */
	UIFormBracket formNew()
		throws MLECallError;
	
	/**
	 * Returns the injector for forms, which is used for testing purposes.
	 * 
	 * @return The injector for the shelf.
	 * @throws MLECallError If injecting is not supported.
	 * @since 2020/07/26
	 */
	UIFormCallback injector()
		throws MLECallError;
	
	/**
	 * Deletes the specified item.
	 * 
	 * @param __item The item to delete.
	 * @throws MLECallError On null arguments, if the item could not be
	 * deleted, if the item was already deleted, or if the item is currently
	 * active within a form.
	 * @since 2020/07/18
	 */
	void itemDelete(UIItemBracket __item)
		throws MLECallError;
	
	/**
	 * Returns the form the item is on.
	 * 
	 * @param __item The item to get.
	 * @return The form the item is on.
	 * @throws MLECallError If {@code __item} is {@code null}.
	 * @since 2021/01/03
	 */
	UIFormBracket itemForm(UIItemBracket __item)
		throws MLECallError;
	
	/**
	 * Creates a new item.
	 * 
	 * @param __type The {@link UIItemType} to create.
	 * @return The newly created item.
	 * @throws MLECallError If the item could not be created or the type was
	 * not valid.
	 * @since 2020/07/17
	 */
	UIItemBracket itemNew(int __type)
		throws MLECallError;
	
	/**
	 * Calls the given method serially within the main event handler.
	 * 
	 * @param __displayId The display identifier.
	 * @param __serialId The serial identifier.
	 * @throws MLECallError If the call is not valid.
	 * @since 2020/10/03
	 */
	void later(int __displayId, int __serialId)
		throws MLECallError;
	
	/**
	 * Returns a metric which describes something about the user interface
	 * forms implementation or other details about the system.
	 * 
	 * @param __metric One of {@link UIMetricType}. The metric
	 * {@link UIMetricType#UIFORMS_SUPPORTED} is always a valid metric and
	 * must be supported, even if the implementation lacks forms.
	 * @return The value of the metric.
	 * @throws MLECallError If the metric is out of range or forms are not
	 * supported and the metric is not {@link UIMetricType#UIFORMS_SUPPORTED}.
	 * @since 2020/06/30
	 */
	int metric(int __metric)
		throws MLECallError;
	
	/**
	 * Sets the given item property.
	 * 
	 * @param __item The item to set.
	 * @param __intProp The {@link UIWidgetProperty}.
	 * @param __sub The sub-index.
	 * @param __newValue The new value to set.
	 * @throws MLECallError If the item is not valid or the property is not
	 * valid or not an integer property.
	 * @since 2020/09/13
	 */
	void widgetProperty(UIWidgetBracket __item, int __intProp, int __sub,
		int __newValue);
	
	/**
	 * Sets the given item property.
	 * 
	 * @param __item The item to set.
	 * @param __strProp The {@link UIWidgetProperty}.
	 * @param __sub The sub-index.
	 * @param __newValue The new value to set.
	 * @throws MLECallError If the item is not valid or the property is not
	 * valid or not a string property.
	 * @since 2020/09/13
	 */
	void widgetProperty(UIWidgetBracket __item, int __strProp, int __sub,
		String __newValue);
	
	/**
	 * Gets a property of the given widget.
	 * 
	 * @param __widget The widget to get from.
	 * @param __intProp The {@link UIWidgetProperty}.
	 * @param __sub The sub-index.
	 * @return The value of the property.
	 * @throws MLECallError If the widget or property is not valid.
	 * @since 2020/09/21
	 */
	int widgetPropertyInt(UIWidgetBracket __widget, int __intProp, int __sub)
		throws MLECallError;
	
	/**
	 * Gets a property of the given widget.
	 * 
	 * @param __widget The widget to get from.
	 * @param __strProp The {@link UIWidgetProperty}.
	 * @param __sub The sub-index.
	 * @return The value of the property.
	 * @throws MLECallError If the widget or property is not valid.
	 * @since 2020/09/21
	 */
	String widgetPropertyStr(UIWidgetBracket __widget, int __strProp,
		int __sub)
		throws MLECallError;
}
