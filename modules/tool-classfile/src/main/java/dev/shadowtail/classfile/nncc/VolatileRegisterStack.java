// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.nncc;

import dev.shadowtail.classfile.summercoat.register.ExecutablePointer;
import dev.shadowtail.classfile.summercoat.register.IntValueRegister;
import dev.shadowtail.classfile.summercoat.register.MemHandleRegister;
import dev.shadowtail.classfile.summercoat.register.RuntimePoolPointer;
import dev.shadowtail.classfile.summercoat.register.TypedRegister;
import dev.shadowtail.classfile.summercoat.register.Volatile;
import dev.shadowtail.classfile.summercoat.register.WideRegister;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.multiphasicapps.classfile.InvalidClassFormatException;

/**
 * This is a stack which is used to manage which volatile registers are used.
 *
 * @since 2019/05/24
 */
public final class VolatileRegisterStack
{
	/** The base register. */
	protected final int base;
	
	/** Registers being used. */
	private final Collection<Integer> _used =
		new ArrayList<>();
	
	/**
	 * Initializes the volatile stack.
	 *
	 * @param __b The base register.
	 * @since 2019/05/25
	 */
	public VolatileRegisterStack(int __b)
	{
		this.base = __b;
	}
	
	/**
	 * Clears all of the used volatile registers.
	 *
	 * @since 2019/05/25
	 */
	public final void clear()
	{
		this._used.clear();
	}
	
	/**
	 * Returns a volatile executable pointer.
	 * 
	 * @return The executable pointer.
	 * @since 2020/11/24
	 */
	public Volatile<ExecutablePointer> getExecutablePointer()
	{
		return new Volatile<>(this,
			new ExecutablePointer(this.getUnmanaged()));
	}
	
	/**
	 * Obtains an integer value register.
	 * 
	 * @return An integer value register.
	 * @since 2020/11/28
	 */
	public Volatile<IntValueRegister> getIntValue()
	{
		return new Volatile<>(this,
			new IntValueRegister(this.getUnmanaged()));
	}
	
	/**
	 * Returns a memory handle register.
	 * 
	 * @return The memory handle register.
	 * @since 2021/01/24
	 */
	protected Volatile<MemHandleRegister> getMemHandle()
	{
		return new Volatile<>(this,
			new MemHandleRegister(this.getUnmanaged()));
	}
	
	/**
	 * Returns a volatile to represent a runtime pool pointer.
	 * 
	 * @return A register to store the runtime pool pointer.
	 * @since 2020/11/24
	 */
	public Volatile<RuntimePoolPointer> getRuntimePoolPointer()
	{
		return new Volatile<>(this,
			new RuntimePoolPointer(this.getUnmanaged()));
	}
	
	/**
	 * Returns a volatile typed volatile register.
	 * 
	 * @param <T> The typed register type.
	 * @param __cl The typed register type.
	 * @return A volatile typed register.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/24
	 */
	public final <T> Volatile<TypedRegister<T>> getTyped(Class<T> __cl)
		throws NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException("NARG");
		
		return new Volatile<>(this,
			new TypedRegister<T>(__cl, this.getUnmanaged()));
	}
	
	/**
	 * Returns the next volatile register.
	 *
	 * @return The next volatile register.
	 * @throws IllegalStateException If no registers are available.
	 * @since 2019/05/24
	 */
	public final int getUnmanaged()
		throws IllegalStateException
	{
		// Find next register to use from the base, use any register which
		// was not previously recorded
		int at = this.base;
		Collection<Integer> used = this._used;
		while (used.contains(at))
			at++;
		
		// {@squirreljme.error JC4l Exceeded maximum permitted registers.
		// (The base register; The current count)}
		if (at >= NativeCode.MAX_REGISTERS)
			throw new InvalidClassFormatException(
				String.format("JC4l %d %d", this.base, at));
		
		// Record it
		used.add(at);
		return at;
	}
	
	/**
	 * Returns a wide register.
	 * 
	 * @return A wide register.
	 * @since 2021/04/08
	 */
	public final Volatile<WideRegister> getWide()
	{
		// Get a few registers to find a linked pair
		List<Integer> ints = new ArrayList<>();
		for (int i = 0; i < 3; i++)
			ints.add(this.getUnmanaged());
		
		// Sort in register order
		Collections.sort(ints);
		
		// Try to find a register set
		WideRegister rv = null;
		for (int i = 0, n = ints.size() - 1; i < n; i++)
		{
			int a = ints.get(i);
			int b = ints.get(i + 1);
			
			if (a + 1 == b)
				rv = new WideRegister(IntValueRegister.of(a),
					IntValueRegister.of(b));
		}
		
		// {@squirreljme.error JC4y Could not find wide register allocation.}
		if (rv == null)
			throw new IllegalStateException("JC4y");
		
		// Clear other unused registers
		for (Iterator<Integer> it = ints.iterator(); it.hasNext();)
		{
			int reg = it.next();
			
			// If not used at all, clear it
			if (rv.low.register != reg && rv.high.register != reg)
				this.removeUnmanaged(reg);
			
			it.remove();
		}
		
		return new Volatile<>(this, rv);
	}
	
	/**
	 * Removes the given volatile register from usage.
	 *
	 * @param __r The register to remove.
	 * @throws IllegalStateException If it was never used.
	 * @since 2019/05/24
	 */
	public final void removeUnmanaged(int __r)
		throws IllegalStateException
	{
		// {@squirreljme.error JC13 Register to remove was never previously
		// returned or was removed multiple times. (The register)}
		if (!this._used.remove(__r))
			throw new IllegalStateException("JC13 " + __r);
	}
}

