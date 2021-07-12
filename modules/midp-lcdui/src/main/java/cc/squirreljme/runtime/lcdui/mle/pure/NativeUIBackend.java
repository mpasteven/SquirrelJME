// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.runtime.lcdui.mle.pure;

import cc.squirreljme.jvm.mle.UIFormShelf;
import cc.squirreljme.jvm.mle.brackets.UIDisplayBracket;
import cc.squirreljme.jvm.mle.brackets.UIFormBracket;
import cc.squirreljme.jvm.mle.brackets.UIItemBracket;
import cc.squirreljme.jvm.mle.brackets.UIWidgetBracket;
import cc.squirreljme.jvm.mle.callbacks.UIDisplayCallback;
import cc.squirreljme.jvm.mle.callbacks.UIFormCallback;
import cc.squirreljme.jvm.mle.exceptions.MLECallError;
import cc.squirreljme.runtime.lcdui.mle.UIBackend;

/**
 * This is a form engine which is used when UI Forms are supported by the
 * native implementation.
 *
 * @since 2020/06/30
 */
public class NativeUIBackend
	implements UIBackend
{
	/**
	 * {@inheritDoc}
	 * @since 2020/07/19
	 */
	@Override
	public void callback(Object __ref, UIDisplayCallback __dc)
		throws MLECallError
	{
		UIFormShelf.callback(__ref, __dc);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/19
	 */
	@Override
	public void callback(UIFormBracket __form, UIFormCallback __callback)
		throws MLECallError
	{
		UIFormShelf.callback(__form, __callback);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/19
	 */
	@Override
	public UIDisplayBracket[] displays()
		throws MLECallError
	{
		return UIFormShelf.displays();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/19
	 */
	@Override
	public UIFormBracket displayCurrent(UIDisplayBracket __display)
		throws MLECallError
	{
		return UIFormShelf.displayCurrent(__display);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/19
	 */
	@Override
	public void displayShow(UIDisplayBracket __display, UIFormBracket __form)
		throws MLECallError
	{
		UIFormShelf.displayShow(__display, __form);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/19
	 */
	@Override
	public boolean equals(UIDisplayBracket __a, UIDisplayBracket __b)
		throws MLECallError
	{
		return UIFormShelf.equals(__a, __b);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/19
	 */
	@Override
	public boolean equals(UIFormBracket __a, UIFormBracket __b)
		throws MLECallError
	{
		return UIFormShelf.equals(__a, __b);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/19
	 */
	@Override
	public boolean equals(UIItemBracket __a, UIItemBracket __b)
		throws MLECallError
	{
		return UIFormShelf.equals(__a, __b);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/09/20
	 */
	@Override
	public boolean equals(UIWidgetBracket __a, UIWidgetBracket __b)
		throws MLECallError
	{
		return UIFormShelf.equals(__a, __b);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/19
	 */
	@Override
	public void flushEvents()
		throws MLECallError
	{
		UIFormShelf.flushEvents();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/19
	 */
	@Override
	public void formDelete(UIFormBracket __form)
		throws MLECallError
	{
		UIFormShelf.formDelete(__form);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/19
	 */
	@Override
	public UIItemBracket formItemAtPosition(UIFormBracket __form, int __pos)
		throws MLECallError
	{
		return UIFormShelf.formItemAtPosition(__form, __pos);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/19
	 */
	@Override
	public int formItemCount(UIFormBracket __form)
		throws MLECallError
	{
		return UIFormShelf.formItemCount(__form);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/19
	 */
	@Override
	public int formItemPosition(UIFormBracket __form, UIItemBracket __item)
		throws MLECallError
	{
		return UIFormShelf.formItemPosition(__form, __item);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/19
	 */
	@Override
	public void formItemPosition(UIFormBracket __form, UIItemBracket __item,
		int __pos)
		throws MLECallError
	{
		UIFormShelf.formItemPosition(__form, __item, __pos);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/19
	 */
	@Override
	public UIItemBracket formItemRemove(UIFormBracket __form, int __pos)
		throws MLECallError
	{
		return UIFormShelf.formItemRemove(__form, __pos);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/19
	 */
	@Override
	public UIFormBracket formNew()
		throws MLECallError
	{
		return UIFormShelf.formNew();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/19
	 */
	@Override
	public UIFormCallback injector()
		throws MLECallError
	{
		return UIFormShelf.injector();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/19
	 */
	@Override
	public void itemDelete(UIItemBracket __item)
		throws MLECallError
	{
		UIFormShelf.itemDelete(__item);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/01/03
	 */
	@Override
	public UIFormBracket itemForm(UIItemBracket __item)
		throws MLECallError
	{
		return UIFormShelf.itemForm(__item);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/19
	 */
	@Override
	public UIItemBracket itemNew(int __type)
		throws MLECallError
	{
		return UIFormShelf.itemNew(__type);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/19
	 */
	@Override
	public void later(int __displayId, int __serialId)
		throws MLECallError
	{
		UIFormShelf.later(__displayId, __serialId);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/19
	 */
	@Override
	public int metric(int __metric)
		throws MLECallError
	{
		return UIFormShelf.metric(__metric);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/19
	 */
	@Override
	public void widgetProperty(UIWidgetBracket __item, int __intProp,
		int __sub, int __newValue)
	{
		UIFormShelf.widgetProperty(__item, __intProp, __sub, __newValue);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/19
	 */
	@Override
	public void widgetProperty(UIWidgetBracket __item, int __strProp,
		int __sub, String __newValue)
	{
		UIFormShelf.widgetProperty(__item, __strProp, __sub, __newValue);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/19
	 */
	@Override
	public int widgetPropertyInt(UIWidgetBracket __widget, int __intProp,
		int __sub)
		throws MLECallError
	{
		return UIFormShelf.widgetPropertyInt(__widget, __intProp, __sub);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/07/19
	 */
	@Override
	public String widgetPropertyStr(UIWidgetBracket __widget, int __strProp,
		int __sub)
		throws MLECallError
	{
		return UIFormShelf.widgetPropertyStr(__widget, __strProp, __sub);
	}
}
