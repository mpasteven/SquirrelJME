// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.pool;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import net.multiphasicapps.classfile.ClassName;
import net.multiphasicapps.classfile.MethodDescriptor;
import net.multiphasicapps.classfile.MethodHandle;
import net.multiphasicapps.classfile.MethodName;

/**
 * Represents a method to be invoked.
 *
 * @since 2019/03/21
 */
public final class InvokedMethod
{
	/** The type of method to invoke. */
	public final InvokeType type;
	
	/** The handle of the method being invoke. */
	public final MethodHandle handle;
	
	/** String representation. */
	private Reference<String> _string;
	
	/**
	 * Initializes the invoked method.
	 *
	 * @param __t The type of method to invoke.
	 * @param __cl The class.
	 * @param __mn The method name.
	 * @param __mt The method type.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/24
	 */
	public InvokedMethod(InvokeType __t, String __cl, String __mn, String __mt)
		throws NullPointerException
	{
		this(__t, new MethodHandle(new ClassName(__cl),
			new MethodName(__mn), new MethodDescriptor(__mt)));
	}
	
	/**
	 * Initializes the invoked method.
	 *
	 * @param __t The type of method to invoke.
	 * @param __cl The class.
	 * @param __mn The method name.
	 * @param __mt The method type.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/09/11
	 */
	public InvokedMethod(InvokeType __t, ClassName __cl,
		String __mn, String __mt)
		throws NullPointerException
	{
		this(__t, new MethodHandle(__cl,
			new MethodName(__mn), new MethodDescriptor(__mt)));
	}
	
	/**
	 * Initializes the invoked method.
	 *
	 * @param __t The type of method to invoke.
	 * @param __cl The class.
	 * @param __mn The method name.
	 * @param __mt The method type.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/09/11
	 */
	public InvokedMethod(InvokeType __t, ClassName __cl,
		String __mn, MethodDescriptor __mt)
		throws NullPointerException
	{
		this(__t, new MethodHandle(__cl,
			new MethodName(__mn), __mt));
	}
	
	/**
	 * Initializes the invoked method.
	 *
	 * @param __t The type of method to invoke.
	 * @param __cl The class.
	 * @param __mn The method name.
	 * @param __mt The method type.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/09/11
	 */
	public InvokedMethod(InvokeType __t, ClassName __cl, MethodName __mn,
		MethodDescriptor __mt)
		throws NullPointerException
	{
		this(__t, new MethodHandle(__cl, __mn, __mt));
	}
	
	/**
	 * Initializes the invoked method.
	 *
	 * @param __t The type of method to invoke.
	 * @param __h The handle of the method.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/03/21
	 */
	public InvokedMethod(InvokeType __t, MethodHandle __h)
		throws NullPointerException
	{
		if (__t == null || __h == null)
			throw new NullPointerException("NARG");
		
		this.type = __t;
		this.handle = __h;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/03/24
	 */
	@Override
	public final boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (!(__o instanceof InvokedMethod))
			return false;
		
		InvokedMethod o = (InvokedMethod)__o;
		return this.type.equals(o.type) &&
			this.handle.equals(o.handle);
	}
	
	/**
	 * Returns the method handle.
	 *
	 * @return The method handle.
	 * @since 2019/03/24
	 */
	public final MethodHandle handle()
	{
		return this.handle;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/03/24
	 */
	@Override
	public final int hashCode()
	{
		return this.type.hashCode() ^ this.handle.hashCode();
	}
	
	/**
	 * Returns the outer class.
	 * 
	 * @return The outer class.
	 * @since 2021/02/04
	 */
	public final ClassName outerClass()
	{
		return this.handle.outerClass();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/03/21
	 */
	@Override
	public final String toString()
	{
		Reference<String> ref = this._string;
		String rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._string = new WeakReference<>(
				(rv = this.type + "+" + this.handle));
		
		return rv;
	}
	
	/**
	 * Returns the invocation type.
	 *
	 * @return The invocation type.
	 * @since 2019/04/14
	 */
	public final InvokeType type()
	{
		return this.type;
	}
}

