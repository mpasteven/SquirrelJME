// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.springcoat;

import cc.squirreljme.jvm.mle.callbacks.UIFormCallback;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import net.multiphasicapps.classfile.ClassName;
import net.multiphasicapps.classfile.MethodNameAndType;

/**
 * This is an injector which can inject into {@link UIFormCallback}.
 *
 * @since 2021/02/25
 */
public class UIFormCallbackProxy
	extends SpringProxyObject
{
	/** The to act as a proxy for. */
	private static final ClassName _CLASS =
		new ClassName("cc/squirreljme/jvm/mle/callbacks/UIFormCallback");
	
	/** The callback to call into. */
	private final UIFormCallback callback;
	
	/**
	 * Initializes the form callback wrapper.
	 * 
	 * @param __machine The machine to target.
	 * @param __callback The callback to call into.
	 * @since 2021/02/25
	 */
	public UIFormCallbackProxy(SpringMachine __machine,
		UIFormCallback __callback)
		throws NullPointerException
	{
		super(UIFormCallbackProxy._CLASS, __machine);
		
		if (__callback == null)
			throw new NullPointerException("NARG");
		
		this.callback = __callback;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/25
	 */
	@Override
	protected Object invokeProxy(SpringThreadWorker __thread,
		MethodNameAndType __method, Object[] __args)
	{
		switch (__method.toString())
		{
			case "eventKey:(Lcc/squirreljme/jvm/mle/brackets/" +
				"UIFormBracket;Lcc/squirreljme/jvm/mle/brackets/" +
				"UIItemBracket;III)V":
				this.callback.eventKey(
					MLEUIForm.__form(__args[0]).form,
					MLEUIForm.__item(__args[1]).item,
					(int)__args[2],
					(int)__args[3],
					(int)__args[4]);
				return null;
				
			case "eventMouse:(Lcc/squirreljme/jvm/mle/brackets/" +
				"UIFormBracket;Lcc/squirreljme/jvm/mle/brackets/" +
				"UIItemBracket;IIIII)V":
				this.callback.eventMouse(
					MLEUIForm.__form(__args[0]).form,
					MLEUIForm.__item(__args[1]).item,
					(int)__args[2],
					(int)__args[3],
					(int)__args[4],
					(int)__args[5],
					(int)__args[6]);
				return null;
				
			case "exitRequest:(Lcc/squirreljme/jvm/mle/brackets/" +
				"UIFormBracket;)V":
				this.callback.exitRequest(
					MLEUIForm.__form(__args[0]).form);
				return null;
				
			case "paint:(Lcc/squirreljme/jvm/mle/brackets/" +
				"UIFormBracket;Lcc/squirreljme/jvm/mle/brackets/" +
				"UIItemBracket;IIILjava/lang/Object;I[IIIIII)V":
				SpringArrayObjectInteger pal =
					(SpringArrayObjectInteger)__args[7];
				this.callback.paint(
					MLEUIForm.__form(__args[0]).form,
					MLEUIForm.__item(__args[1]).item,
					(int)__args[2],
					(int)__args[3],
					(int)__args[4],
					((SpringArrayObject)__args[5]).array(),
					(int)__args[6],
					(pal == null ? null : pal.array()),
					(int)__args[8],
					(int)__args[9],
					(int)__args[10],
					(int)__args[11],
					(int)__args[12]);
				return null;
				
			case "propertyChange:(Lcc/squirreljme/jvm/mle/brackets/" +
				"UIFormBracket;Lcc/squirreljme/jvm/mle/brackets/" +
				"UIItemBracket;IIII)V":
				this.callback.propertyChange(
					MLEUIForm.__form(__args[0]).form,
					MLEUIForm.__item(__args[1]).item,
					(int)__args[2],
					(int)__args[3],
					(int)__args[4],
					(int)__args[5]);
				return null;
				
			case "propertyChange:(Lcc/squirreljme/jvm/mle/brackets/" +
				"UIFormBracket;Lcc/squirreljme/jvm/mle/brackets/" +
				"UIItemBracket;IILjava/lang/String;Ljava/lang/String;)V":
				this.callback.propertyChange(
					MLEUIForm.__form(__args[0]).form,
					MLEUIForm.__item(__args[1]).item,
					(int)__args[2],
					(int)__args[3],
					__thread.asNativeObject(String.class, __args[4]),
					__thread.asNativeObject(String.class, __args[5]));
				return null;
				
			default:
				throw Debugging.oops(__method);
		}
	}
}
