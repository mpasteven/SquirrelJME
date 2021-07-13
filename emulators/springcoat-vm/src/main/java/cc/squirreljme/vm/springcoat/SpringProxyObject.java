// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.springcoat;

import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.vm.springcoat.brackets.RefLinkHolder;
import net.multiphasicapps.classfile.ClassName;
import net.multiphasicapps.classfile.MethodNameAndType;

/**
 * This is a method that acts as a proxy around for out of VM calls.
 *
 * @since 2021/02/25
 */
public abstract class SpringProxyObject
	implements SpringObject
{
	/** The machine used for the proxy. */
	private final SpringMachine machine;
	
	/** The type this is for. */
	private final ClassName type;
	
	/**
	 * Initializes the proxy object.
	 *
	 * @param __type The class type that this is.
	 * @param __machine The machine used for the proxy.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/02/25
	 */
	public SpringProxyObject(ClassName __type, SpringMachine __machine)
		throws NullPointerException
	{
		if (__machine == null)
			throw new NullPointerException("NARG");
		
		this.type = __type;
		this.machine = __machine;
	}
	
	/**
	 * Invokes the given proxy method.
	 * 
	 * @param __thread The thread is invoke on.
	 * @param __method The method to invoke.
	 * @param __args The arguments of the method.
	 * @return The resultant value, if any.
	 * @since 2021/02/25
	 */
	protected abstract Object invokeProxy(SpringThreadWorker __thread,
		MethodNameAndType __method, Object[] __args);
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/25
	 */
	@Override
	public final SpringMonitor monitor()
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/25
	 */
	@Override
	public final RefLinkHolder refLink()
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/02/25
	 */
	@Override
	public final SpringClass type()
	{
		return this.machine.classLoader().loadClass(this.type);
	}
}
