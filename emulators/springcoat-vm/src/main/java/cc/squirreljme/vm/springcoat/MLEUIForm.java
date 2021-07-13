// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.springcoat;

import cc.squirreljme.jvm.mle.UIFormShelf;
import cc.squirreljme.jvm.mle.brackets.UIDisplayBracket;
import cc.squirreljme.jvm.mle.brackets.UIFormBracket;
import cc.squirreljme.jvm.mle.brackets.UIItemBracket;
import cc.squirreljme.jvm.mle.brackets.UIWidgetBracket;
import cc.squirreljme.jvm.mle.callbacks.UIFormCallback;
import cc.squirreljme.vm.springcoat.brackets.UIDisplayObject;
import cc.squirreljme.vm.springcoat.brackets.UIFormObject;
import cc.squirreljme.vm.springcoat.brackets.UIItemObject;
import cc.squirreljme.vm.springcoat.brackets.UIWidgetObject;
import cc.squirreljme.vm.springcoat.exceptions.SpringMLECallError;

/**
 * SpringCoat support for {@link UIFormShelf}.
 *
 * @since 2020/06/30
 */
public enum MLEUIForm
	implements MLEFunction
{
	/** Registers a display callback. */
	CALLBACK_DISPLAY("callback:" +
		"(Ljava/lang/Object;Lcc/squirreljme/jvm/mle/callbacks/" +
		"UIDisplayCallback;)V")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/10/03
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			SpringObject ref = (SpringObject)__args[0];
			if (ref == null || ref == SpringNullObject.NULL)
				throw new SpringMLECallError("Null reference.");
			
			SpringObject callback = (SpringObject)__args[1];
			if (callback == null || callback == SpringNullObject.NULL)
				throw new SpringMLECallError("Null callback.");
			
			// The callback needs to be wrapped accordingly
			UIFormShelf.callback(ref,
				new UIDisplayCallbackAdapter(__thread.machine, callback));
			
			return null;
		}
	},
	
	/** {@link UIFormShelf#callback(UIFormBracket, UIFormCallback)}. */
	CALLBACK_FORM("callback:(Lcc/squirreljme/jvm/mle/brackets/" +
		"UIFormBracket;Lcc/squirreljme/jvm/mle/callbacks/UIFormCallback;)V")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/09/13
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			UIFormObject form = MLEUIForm.__form(__args[0]);
			
			SpringObject callback = (SpringObject)__args[1];
			if (callback == null || callback == SpringNullObject.NULL)
				throw new SpringMLECallError("Null callback.");
			
			// Since the callback is in SpringCoat, it has to be wrapped to
			// forward calls into, there also needs to be threads to do work
			// in
			UIFormShelf.callback(form.form,
				new UIFormCallbackAdapter(__thread.machine, callback));
			return null;
		}
	}, 
	
	/** {@link UIFormShelf#displays()}. */
	DISPLAYS("displays:()[Lcc/squirreljme/jvm/mle/brackets/" +
		"UIDisplayBracket;")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/07/01
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			UIDisplayBracket[] natives = UIFormShelf.displays();
			
			// Wrap the array
			int n = natives.length;
			SpringObject[] result = new SpringObject[n];
			for (int i = 0; i < n; i++)
				result[i] = new UIDisplayObject(__thread.machine, natives[i]);
			
			// Use array as result
			return __thread.asVMObjectArray(__thread.resolveClass(
				"[Lcc/squirreljme/jvm/mle/brackets/UIDisplayBracket;"),
				result);
		}
	},
	
	/** {@link UIFormShelf#displayCurrent(UIDisplayBracket)}. */ 
	DISPLAY_CURRENT("displayCurrent:(Lcc/squirreljme/jvm/mle/" +
		"brackets/UIDisplayBracket;)Lcc/squirreljme/jvm/mle/brackets/" +
		"UIFormBracket;")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/10/04
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			UIFormBracket form = UIFormShelf.displayCurrent(
				MLEUIForm.__display(__args[0]).display);
			
			return (form == null ? null : new UIFormObject(__thread.machine,
				form));
		}
	},
	
	/** {@link UIFormShelf#displayShow(UIDisplayBracket, UIFormBracket)}. */ 
	DISPLAY_SHOW("displayShow:(Lcc/squirreljme/jvm/mle/brackets/" +
		"UIDisplayBracket;Lcc/squirreljme/jvm/mle/brackets/UIFormBracket;)V")
	{
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			SpringObject form = (SpringObject)__args[1];
			
			UIFormShelf.displayShow(MLEUIForm.__display(__args[0]).display,
				(form == null || form == SpringNullObject.NULL ? null :
				MLEUIForm.__form(__args[1]).form));
			return null;
		}
	},
	
	/** {@link UIFormShelf#equals(UIDisplayBracket, UIDisplayBracket)}. */
	EQUALS_DISPLAY("equals:(Lcc/squirreljme/jvm/mle/brackets/" +
		"UIDisplayBracket;Lcc/squirreljme/jvm/mle/brackets/" +
		"UIDisplayBracket;)Z")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/09/20
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			return UIFormShelf.equals(MLEUIForm.__display(__args[0]).display,
				MLEUIForm.__display(__args[1]).display);
		}
	},
	
	/** {@link UIFormShelf#equals(UIFormBracket, UIFormBracket)}. */
	EQUALS_FORM("equals:(Lcc/squirreljme/jvm/mle/brackets/" +
		"UIFormBracket;Lcc/squirreljme/jvm/mle/brackets/" +
		"UIFormBracket;)Z")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/09/20
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			return UIFormShelf.equals(MLEUIForm.__form(__args[0]).form,
				MLEUIForm.__form(__args[1]).form);
		}
	},
	
	/** {@link UIFormShelf#equals(UIItemBracket, UIItemBracket)}. */
	EQUALS_ITEM("equals:(Lcc/squirreljme/jvm/mle/brackets/" +
		"UIItemBracket;Lcc/squirreljme/jvm/mle/brackets/" +
		"UIItemBracket;)Z")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/09/20
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			return UIFormShelf.equals(MLEUIForm.__item(__args[0]).item,
				MLEUIForm.__item(__args[1]).item);
		}
	},
	
	/** {@link UIFormShelf#equals(UIWidgetBracket, UIWidgetBracket)}. */
	EQUALS_WIDGET("equals:(Lcc/squirreljme/jvm/mle/brackets/" +
		"UIWidgetBracket;Lcc/squirreljme/jvm/mle/brackets/" +
		"UIWidgetBracket;)Z")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/09/20
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			return UIFormShelf.equals(MLEUIForm.__widget(__args[0]).widget(),
				MLEUIForm.__widget(__args[1]).widget());
		}
	},
	
	/** {@link UIFormShelf#flushEvents()}. */
	FLUSH_EVENTS("flushEvents:()V")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/10/03
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			UIFormShelf.flushEvents();
			return null;
		}
	},
	
	/** {@link UIFormShelf#formDelete(UIFormBracket)}. */
	FORM_DELETE("formDelete:(Lcc/squirreljme/jvm/mle/brackets/" +
		"UIFormBracket;)V")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/07/01
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			UIFormShelf.formDelete(MLEUIForm.__form(__args[0]).form);
			return null;
		}
	},
	
	/** {@link UIFormShelf#formItemAtPosition(UIFormBracket, int)}. */
	FORM_ITEM_AT_POSITION("formItemAtPosition:(Lcc/squirreljme/jvm/" +
		"mle/brackets/UIFormBracket;I)Lcc/squirreljme/jvm/mle/brackets/" +
		"UIItemBracket;")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/10/03
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			UIItemBracket rv = UIFormShelf.formItemAtPosition(
				MLEUIForm.__form(__args[0]).form, (int)__args[1]);
			
			return (rv == null ? null : new UIItemObject(__thread.machine, rv));
		}
	},
	
	/** {@link UIFormShelf#formItemCount(UIFormBracket)}. */ 
	FORM_ITEM_COUNT("formItemCount:(Lcc/squirreljme/jvm/mle/" +
		"brackets/UIFormBracket;)I")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/10/09
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			return UIFormShelf.formItemCount(MLEUIForm.__form(__args[0]).form);
		}
	},
	
	/** {@link UIFormShelf#formItemPosition(UIFormBracket, UIItemBracket)}. */
	FORM_ITEM_POSITION_GET("formItemPosition:(Lcc/squirreljme/jvm/mle/" +
		"brackets/UIFormBracket;Lcc/squirreljme/jvm/mle/brackets/" +
		"UIItemBracket;)I")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/07/18
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			return UIFormShelf.formItemPosition(
				MLEUIForm.__form(__args[0]).form,
				MLEUIForm.__item(__args[1]).item);
		}
	},
	
	/**
	 * {@link
	 * UIFormShelf#formItemPosition(UIFormBracket, UIItemBracket, int)}.
	 */
	FORM_ITEM_POSITION_SET("formItemPosition:(Lcc/squirreljme/jvm/" +
		"mle/brackets/UIFormBracket;Lcc/squirreljme/jvm/mle/brackets/" +
		"UIItemBracket;I)V")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/07/18
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			UIFormShelf.formItemPosition(
				MLEUIForm.__form(__args[0]).form,
				MLEUIForm.__item(__args[1]).item,
				(int)__args[2]);
			return null;
		}
	},
	
	/** {@link UIFormShelf#formItemRemove(UIFormBracket, int)}. */ 
	FORM_ITEM_REMOVE("formItemRemove:(Lcc/squirreljme/jvm/mle/" +
		"brackets/UIFormBracket;I)Lcc/squirreljme/jvm/mle/brackets/" +
		"UIItemBracket;")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/10/09
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			return new UIItemObject(__thread.machine, UIFormShelf.formItemRemove(
				MLEUIForm.__form(__args[0]).form, (int)__args[1]));
		}
	},
	
	/** {@link UIFormShelf#formNew()}. */
	FORM_NEW("formNew:()Lcc/squirreljme/jvm/mle/brackets/UIFormBracket;")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/07/01
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			return new UIFormObject(__thread.machine, UIFormShelf.formNew());
		}
	},
	
	/** {@link UIFormShelf#injector()}. */
	INJECTOR("injector:()Lcc/squirreljme/jvm/mle/callbacks/" +
		"UIFormCallback;")
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/02/25
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			// Use a proxy to call from SpringCoat to the real machine
			return new UIFormCallbackProxy(__thread.machine,
				UIFormShelf.injector());
		}
	},
	
	/** {@link UIFormShelf#itemDelete(UIItemBracket)}. */
	ITEM_DELETE("itemDelete:(Lcc/squirreljme/jvm/mle/brackets/" +
		"UIItemBracket;)V")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/07/18
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			UIFormShelf.itemDelete(MLEUIForm.__item(__args[0]).item);
			return null;
		}
	},
	
	/** {@link UIFormShelf#itemForm(UIItemBracket)}. */
	ITEM_FORM("itemForm:(Lcc/squirreljme/jvm/mle/brackets/" +
		"UIItemBracket;)Lcc/squirreljme/jvm/mle/brackets/UIFormBracket;")
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/01/03
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			UIFormBracket form = UIFormShelf.itemForm(
				MLEUIForm.__item(__args[0]).item);
			return (form == null ? null : new UIFormObject(__thread.machine,
				form));
		}
	}, 
	
	/** {@link UIFormShelf#itemNew(int)}. */  
	ITEM_NEW("itemNew:(I)Lcc/squirreljme/jvm/mle/brackets/" +
		"UIItemBracket;")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/07/18
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			return new UIItemObject(__thread.machine, UIFormShelf.itemNew((int)__args[0]));
		}
	},
	
	/** {@link UIFormShelf#later(int, int)}. */ 
	LATER("later:(II)V")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/10/03
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			UIFormShelf.later((int)__args[0], (int)__args[1]);
			return null;
		}
	},
	
	/** {@link UIFormShelf#metric(int)}. */
	METRIC("metric:(I)I")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/30
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			int metric = (int)__args[0];
			
			return UIFormShelf.metric(metric);
		}
	},
	
	/** {@link UIFormShelf#widgetPropertyInt(UIWidgetBracket, int, int)}. */
	WIDGET_PROPERTY_GET_INT("widgetPropertyInt:(Lcc/squirreljme/jvm/" +
		"mle/brackets/UIWidgetBracket;II)I")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/09/21
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			return UIFormShelf.widgetPropertyInt(
				MLEUIForm.__widget(__args[0]).widget(),
				(int)__args[1], (int)__args[2]);
		}
	},
	
	/** {@link UIFormShelf#widgetPropertyStr(UIWidgetBracket, int, int)}. */
	WIDGET_PROPERTY_GET_SET("widgetPropertyStr:(Lcc/squirreljme/jvm/" +
		"mle/brackets/UIWidgetBracket;II)Ljava/lang/String;")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/09/21
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			return UIFormShelf.widgetPropertyStr(
				MLEUIForm.__widget(__args[0]).widget(),
				(int)__args[1], (int)__args[2]);
		}
	},
	
	/** {@link UIFormShelf#widgetProperty(UIWidgetBracket, int, int, int)}. */ 
	WIDGET_PROPERTY_SET_INT("widgetProperty:(Lcc/squirreljme/jvm/mle/" +
		"brackets/UIWidgetBracket;III)V")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/09/13
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			UIFormShelf.widgetProperty(
				MLEUIForm.__widget(__args[0]).widget(),
				(int)__args[1], (int)__args[2], (int)__args[3]);
			return null;
		}
	},
	
	/** {@link UIFormShelf#widgetProperty(UIWidgetBracket, int, int, int)}. */ 
	WIDGET_PROPERTY_SET_STR("widgetProperty:(Lcc/squirreljme/jvm/mle/" +
		"brackets/UIWidgetBracket;IILjava/lang/String;)V")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/09/13
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			UIFormShelf.widgetProperty(
				MLEUIForm.__widget(__args[0]).widget(),
				(int)__args[1], (int)__args[2],
				__thread.<String>asNativeObject(String.class, __args[3]));
			return null;
		}
	},
	
	/* End. */
	;
	
	/** The dispatch key. */
	protected final String key;
	
	/**
	 * Initializes the dispatcher info.
	 *
	 * @param __key The key.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/06/30
	 */
	MLEUIForm(String __key)
		throws NullPointerException
	{
		if (__key == null)
			throw new NullPointerException("NARG");
		
		this.key = __key;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/06/30
	 */
	@Override
	public String key()
	{
		return this.key;
	}
	
	/**
	 * Gets the object as a {@link UIDisplayObject}.
	 * 
	 * @param __o The object.
	 * @return As the desired {@link UIDisplayObject}.
	 * @throws SpringMLECallError If the object is not this type.
	 * @since 2020/07/01
	 */
	static UIDisplayObject __display(Object __o)
		throws SpringMLECallError
	{
		if (!(__o instanceof UIDisplayObject))
			throw new SpringMLECallError("Not a UIDisplayObject.");
		
		return (UIDisplayObject)__o;
	}
	
	/**
	 * Gets the object as a {@link UIFormObject}.
	 * 
	 * @param __o The object.
	 * @return As the desired {@link UIFormObject}.
	 * @throws SpringMLECallError If the object is not this type.
	 * @since 2020/07/01
	 */
	static UIFormObject __form(Object __o)
		throws SpringMLECallError
	{
		if (!(__o instanceof UIFormObject))
			throw new SpringMLECallError("Not a UIFormObject.");
		
		return (UIFormObject)__o;
	}
	
	/**
	 * Gets the object as a {@link UIItemObject}.
	 * 
	 * @param __o The object.
	 * @return As the desired {@link UIItemObject}.
	 * @throws SpringMLECallError If the object is not this type.
	 * @since 2020/07/01
	 */
	static UIItemObject __item(Object __o)
		throws SpringMLECallError
	{
		if (!(__o instanceof UIItemObject))
			throw new SpringMLECallError("Not a UIItemObject.");
		
		return (UIItemObject)__o;
	}
	
	/**
	 * Gets the widget from this object.
	 * 
	 * @param __o The object to get from.
	 * @throws SpringMLECallError If not one.
	 * @return The widget.
	 * @since 2020/09/20
	 */
	static UIWidgetObject __widget(Object __o)
		throws SpringMLECallError
	{
		if (!(__o instanceof UIWidgetObject))
			throw new SpringMLECallError("Not a UIWidgetObject.");
		
		return (UIWidgetObject)__o;
	}
}
