// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.xlate;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * This represents an operation that needs to be performed during stack
 * transitions.
 *
 * @since 2019/04/11
 */
public final class StateOperation
{
	/** The type of operation to perform. */
	public final StateOperation.Type type;
	
	/** The A register. */
	public final SoftRegister a;
	
	/** The B register. */
	public final SoftRegister b;
	
	/** Hashcode. */
	private int _hash;
	
	/** String. */
	private Reference<String> _string;
	
	/**
	 * Initializes the state operation.
	 *
	 * @param __t The type.
	 * @param __a Register A.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/13
	 */
	private StateOperation(StateOperation.Type __t, SoftRegister __a)
		throws NullPointerException
	{
		this(__t, __a, __a);
	}
	
	/**
	 * Initializes the state operation.
	 *
	 * @param __t The type.
	 * @param __a Register A.
	 * @param __b Register B.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/13
	 */
	private StateOperation(StateOperation.Type __t,
		SoftRegister __a, SoftRegister __b)
		throws NullPointerException
	{
		if (__t == null)
			throw new NullPointerException("NARG");
		
		this.type = __t;
		this.a = __a;
		this.b = __b;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/11
	 */
	@Override
	public final boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (this.hashCode() != __o.hashCode())
			return false;
		
		if (!(__o instanceof StateOperation))
			return false;
		
		StateOperation o = (StateOperation)__o;
		return this.type.equals(o.type) &&
			Objects.equals(this.a, o.a) &&
			Objects.equals(this.b, o.b);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/11
	 */
	@Override
	public final int hashCode()
	{
		int rv = this._hash;
		if (rv == 0)
			this._hash = (rv = this.type.hashCode() +
				this.a.hashCode() + this.b.hashCode());
		return rv;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/11
	 */
	@Override
	public final String toString()
	{
		Reference<String> ref = this._string;
		String rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._string = new WeakReference<>((rv = this.type + "(" +
				this.a + ", " + this.b + ")"));
		
		return rv;
	}
	
	/**
	 * Creates a copy operation.
	 *
	 * @param __w Is this wide?
	 * @param __a The source.
	 * @param __b The destination.
	 * @return The operation.
	 * @deprecated Use {@link #copy(boolean, SoftRegister, SoftRegister)}. 
	 * @since 2019/04/13
	 */
	@Deprecated
	public static StateOperation copy(boolean __w, int __a, int __b)
	{
		return StateOperation.copy(__w,
			SoftRegister.of(false, __a),
			SoftRegister.of(false, __b));
	}
	
	/**
	 * Creates a copy operation.
	 *
	 * @param __w Is this wide?
	 * @param __a The source.
	 * @param __b The destination.
	 * @return The operation.
	 * @since 2021/06/19
	 */
	public static StateOperation copy(boolean __w,
		SoftRegister __a, SoftRegister __b)
	{
		return new StateOperation((__w ? Type.WIDE_COPY : Type.COPY),
			__a, __b);
	}
	
	/**
	 * Creates a counting operation.
	 *
	 * @param __a The register to count.
	 * @return The operation.
	 * @deprecated Use {@link #count(SoftRegister)}. 
	 * @since 2019/04/13
	 */
	@Deprecated
	public static StateOperation count(int __a)
	{
		return StateOperation.count(SoftRegister.of(false, __a));
	}
	
	/**
	 * Creates a counting operation.
	 *
	 * @param __a The register to count.
	 * @return The operation.
	 * @since 2021/06/19
	 */
	public static StateOperation count(SoftRegister __a)
	{
		return new StateOperation(Type.COUNT, __a);
	}
	
	/**
	 * Creates a uncounting operation.
	 *
	 * @param __a The register to uncount.
	 * @return The operation.
	 * @deprecated Use {@link #uncount(SoftRegister)}.
	 * @since 2019/04/13
	 */
	@Deprecated
	public static StateOperation uncount(int __a)
	{
		return StateOperation.uncount(SoftRegister.of(false, __a));
	}
	
	/**
	 * Creates a uncounting operation.
	 *
	 * @param __a The register to uncount.
	 * @return The operation.
	 * @since 2021/06/19
	 */
	public static StateOperation uncount(SoftRegister __a)
	{
		return new StateOperation(Type.UNCOUNT, __a);
	}
	
	/**
	 * Represents the type of operation to perform.
	 *
	 * @since 2019/04/11
	 */
	public enum Type
	{
		/** Count. */
		COUNT,
		
		/** Un-count. */
		UNCOUNT,
		
		/** Copy. */
		COPY,
		
		/** Wide copy. */
		WIDE_COPY,
		
		/* End. */
		;
		
		/**
		 * Is this wide?
		 * 
		 * @return If this is wide.
		 * @since 2021/06/19
		 */
		public final boolean isWide()
		{
			return this == Type.WIDE_COPY;
		}
	}
}

