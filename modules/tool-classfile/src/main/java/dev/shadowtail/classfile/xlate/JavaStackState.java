// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.xlate;

import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.runtime.cldc.util.IntegerArrayList;
import dev.shadowtail.classfile.nncc.NativeCode;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.multiphasicapps.classfile.InvalidClassFormatException;
import net.multiphasicapps.classfile.JavaType;
import net.multiphasicapps.classfile.StackMapTableEntry;
import net.multiphasicapps.classfile.StackMapTableState;

/**
 * This class contains the state of the Java stack, it is mostly used in
 * the generation of the register code as it handles caching as well.
 *
 * This class is immutable, all operations create new copies of this class
 * with the appropriate changes.
 *
 * @since 2019/03/30
 */
public final class JavaStackState
{
	/** The base register where arguments start. */
	public static final int BASE_REGISTER =
		NativeCode.ARGUMENT_REGISTER_BASE;
	
	/** The top of the stack. */
	public final int stacktop;
	
	/** Number of used registers. */
	@Deprecated
	public final int usedregisters;
	
	/** The local variables defined. */
	private final Info[] _locals;
	
	/** The stack variables. */
	private final Info[] _stack;
	
	/** String representation. */
	private Reference<String> _string;
	
	/** Hash code. */
	private int _hash;
	
	/**
	 * Initializes the stack state, the state will be modified to ensure that
	 * it is correct for normalization purposes.
	 *
	 * @param __l The locals.
	 * @param __s The stack.
	 * @param __ss The top of the stack.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/03/31
	 */
	public JavaStackState(Info[] __l, Info[] __s, int __ss)
		throws NullPointerException
	{
		if (__l == null || __s == null)
			throw new NullPointerException("NARG");
		
		for (Info i : (__l = __l.clone()))
			if (i == null)
				throw new NullPointerException("NARG");
		
		for (Info i : (__s = __s.clone()))
			if (i == null)
				throw new NullPointerException("NARG");
		
		// Make sure locals are correct
		for (int i = 0, n = __l.length; i < n; i++)
		{
			Info x = __l[i];
			
			// Checks if there is something here
			if (!x.type.isNothing())
			{
				// {@squirreljme.error JC1e Local variables cannot be an alias
				// of another variable. (The local)}
				if (x.value != x.register)
					throw new InvalidClassFormatException("JC1e " + x);
			}
		}
		
		// Correct pre-stack entries?
		for (int i = 0; i < __ss; i++)
		{
			Info x = __s[i];
			if (x.readonly)
				__s[i] = (x = new Info(x.register, x.type, x.value, false,
					x.nocounting));
			
			// Checks if there is something here
			if (!x.type.isNothing())
			{
				// {@squirreljme.error JC1f Stack variables cannot alias
				// variables at higher indexes. (The stack variable)}
				if (x.value > x.register)
					throw new InvalidClassFormatException("JC1f " + x);
			}
		}
		
		// Correct post-stack entries
		for (int i = __ss, n = __s.length; i < n; i++)
		{
			Info x = __s[i];
			if (!x.type.isNothing() || x.value != -1 || x.readonly ||
				x.nocounting)
				__s[i] = (x = new Info(x.register, JavaType.NOTHING, -1, false,
					false));
		}
		
		// Set
		this._locals = __l;
		this._stack = __s;
		this.stacktop = __ss;
		
		// Determine used registers
		int usedregisters = 0;
		for (Info i : __l)
			usedregisters = Math.max(usedregisters, i.register + 1);
		for (Info i : __s)
			usedregisters = Math.max(usedregisters, i.register + 1);
		this.usedregisters = usedregisters + 1;
	}
	
	/**
	 * Compare two stacks and returns a list of registers which have a
	 * compatible or transferable type but collide in their cached value. This
	 * does not check local variables, only stack entries.
	 *
	 * @param __ts The target stack.
	 * @return A list of registers which collide.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/13
	 */
	public final JavaStackEnqueueList cacheCollision(JavaStackState __ts)
		throws NullPointerException
	{
		if (__ts == null)
			throw new NullPointerException("NARG");
		
		// If the two stacks are e
		if (this.equals(__ts))
			return new JavaStackEnqueueList(0);
			
		// {@squirreljme.error JC1g A collision cannot be made where the
		// length of the stack differs. (The length of the source stack; The
		// length of the target stack)}
		int atop = this.stacktop,
			btop = __ts.stacktop;
		if (atop != btop)
			throw new InvalidClassFormatException("JC1g " + atop + " " + btop);
		
		// Registers which collide
		List<Integer> collides = new ArrayList<>();
		
		// Go through and determine which stack entries collide
		Info[] astk = this._stack,
			bstk = __ts._stack;
		for (int i = 0; i < atop; i++)
		{
			Info a = astk[i],
				b = bstk[i];
			
			// Entries which are the same would never collide
			if (a.equals(b))
				continue;
			
			// If either the source or the target or nothing then there can
			// be a transition, one will either be a set to zero or an uncount
			// if an object
			JavaType at = a.type,
				bt = b.type;
			if (at.isNothing() || bt.isNothing())
				continue;
			
			// The target entry is cached, but it has a value which does not
			// map to this register
			if (b.value != b.register && a.register != b.value)
				collides.add(b.register);
		}
		
		return new JavaStackEnqueueList(0, collides);
	}
	
	/**
	 * Returns the state which would be used if the specified registers were
	 * to have their caches cleared. Only the stack is considered as locals
	 * are never cached.
	 *
	 * @param __enq The registers to clear.
	 * @return The resulting stack.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/13
	 */
	public final JavaStackState cacheClearState(JavaStackEnqueueList __enq)
		throws NullPointerException
	{
		if (__enq == null)
			throw new NullPointerException("NARG");
		
		// If there is nothing to clear 
		if (__enq.isEmpty())
			return this;
		
		// Clear cache from these
		Info[] stack = this._stack.clone();
		int stacktop = this.stacktop;
		
		// Remove any cached slots
		for (int i = 0; i < stacktop; i++)
		{
			Info inf = stack[i];
			
			// This is to be uncached
			if (inf.value != inf.register && __enq.contains(inf.register))
				stack[i] = new Info(inf.register, inf.type, inf.register,
					false, inf.nocounting);
		}
		
		// Build it
		return new JavaStackState(this._locals, stack, stacktop);
	}
	
	/**
	 * Checks if transition can be made to the other state.
	 *
	 * @param __ts The state to check transition to.
	 * @return If it can transition.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/13
	 */
	public final boolean canTransition(JavaStackState __ts)
		throws NullPointerException
	{
		if (__ts == null)
			throw new NullPointerException("NARG");
		
		// Transition is possible if there are no collisions
		return this.cacheCollision(__ts).isEmpty();
	}
	
	/**
	 * Performs a flush of the entire state removing all cached values.
	 *
	 * @return The result of the cache flush.
	 * @since 2019/04/11
	 */
	public final JavaStackResult doCacheFlush()
	{
		// This is just a transition to the non-cached state
		return this.doTransition(this.nonCached());
	}
	
	/**
	 * This handles logic used by check cast.
	 *
	 * @param __t The type to cast to.
	 * @return The result of the cast.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/06/12
	 */
	public final JavaStackResult doCheckCast(JavaType __t)
		throws NullPointerException
	{
		if (__t == null)
			throw new NullPointerException("NARG");
			
		// This is nearly the same
		Info[] newstack = this._stack.clone();
		int stacktop = this.stacktop;
		
		// Get item on the 
		Info topitem = newstack[stacktop - 1];
		
		// Same as the top item, but with a new type instead
		newstack[stacktop - 1] = new Info(
			topitem.register,
			__t,
			topitem.value,
			topitem.readonly,
			topitem.nocounting);
		
		// If the top-most item is counting, then enqueue it in the event
		// exceptions happen
		JavaStackEnqueueList enq;
		if (topitem.nocounting)
			enq = new JavaStackEnqueueList(0);
		else
			enq = new JavaStackEnqueueList(0, topitem.register);
		
		// Create the result
		return new JavaStackResult(this,
			new JavaStackState(this._locals, newstack, stacktop),
			enq,
			new StateOperations(),
			JavaStackResult.makeInput(topitem));
	}
	
	/**
	 * Destroys all local variables and stack variables returning the process
	 * that is needed to clear out the entire state.
	 *
	 * Any references that need to be cleared when the code completes will be
	 * placed in the enqueue list.
	 *
	 * @param __rv If true then a return value will be popped before everything
	 * is destroyed, this will be the single input available.
	 * @since 2019/03/30
	 */
	public final JavaStackResult doDestroy(boolean __rv)
	{
		Info inf;
		Info[] locals = this._locals,
			stack = this._stack;
		int stacktop = this.stacktop;
		
		// Setup new locals
		Info[] newlocals = locals.clone();
		
		// Find locals to enqueue
		List<Integer> enqueue = new ArrayList<>();
		List<StateOperation> sops = new ArrayList<>();
		for (int i = 0, n = locals.length; i < n; i++)
		{
			inf = locals[i];
			
			// Enqueue?
			if (inf.canEnqueue())
				enqueue.add(inf.value);
			
			// Clear out
			newlocals[i] = inf.newTypeValue(JavaType.NOTHING, -1, false);
		}
		
		// Return value?
		List<JavaStackResult.InputOutput> io = new ArrayList<>();
		int newstacktop = stacktop;
		Info popped = null;
		if (__rv)
		{
			// Pop top item
			popped = stack[--newstacktop];
			if (popped.type.isTop())
				popped = stack[--newstacktop];
			
			// Add to input
			io.add(JavaStackResult.makeInput(popped));
		}
		
		// Enqueue stack items, they do not need clearing out because setting
		// a limiting top will auto-clear
		// If returning a value do not enqueue what is being returned
		// otherwise it might end up being garbage collected and returned
		int eqss = enqueue.size();
		for (int i = 0; i < newstacktop; i++)
		{
			inf = stack[i];
			
			if (inf.canEnqueue())
				enqueue.add(inf.value);
		}
		
		// If we are popping something, make sure the value that was popped
		// is never enqueued because if it was a cached local or stack entry
		// then it would have been hit by an uncount
		if (popped != null)
			enqueue.remove((Object)popped.value);
		
		// Create result
		return new JavaStackResult(this,
			new JavaStackState(newlocals, stack, 0),
			new JavaStackEnqueueList(eqss, enqueue),
			new StateOperations(sops),
			io.<JavaStackResult.InputOutput>toArray(
				new JavaStackResult.InputOutput[io.size()]));
	}
	
	/**
	 * Removes all stack variables and places a single entry on the stack
	 * for exception handling.
	 *
	 * The input is filled with the entire stack. The output is a single
	 * entry and contains the stack entry to place the value at. The single
	 * stack entry for the exception will never be cached to the exception
	 * register.
	 *
	 * @since 2019/04/13
	 */
	public final JavaStackResult doExceptionHandler()
	{
		return this.doExceptionHandler(JavaType.THROWABLE);
	}
	
	/**
	 * Removes all stack variables and places a single entry on the stack
	 * for exception handling.
	 *
	 * The input is filled with the entire stack. The output is a single
	 * entry and contains the stack entry to place the value at. The single
	 * stack entry for the exception will never be cached to the exception
	 * register.
	 *
	 * @param __t The type to push.
	 * @return The result of the operation.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/03/30
	 */
	public final JavaStackResult doExceptionHandler(JavaType __t)
		throws NullPointerException
	{
		if (__t == null)
			throw new NullPointerException("NARG");
		
		// Enqueues and operations
		List<Integer> enq = new ArrayList<>();
		List<StateOperation> ops = new ArrayList<>();
		
		// Setup new stack
		int oldstacktop = this.stacktop;
		Info[] newstack = this._stack.clone();
		
		// Cleanup everything on the stack!
		for (int i = 0; i < oldstacktop; i++)
		{
			Info sit = newstack[i];
			
			// If there are objects here, then uncount them
			if (sit.canEnqueue())
			{
				enq.add(sit.value);
				ops.add(StateOperation.uncount(sit.value));
			}
		}
		
		// Setup new stack entry
		Info olddest = newstack[0],
			dest = newstack[0].newTypeValue(__t, olddest.register, false);
		newstack[0] = dest;
		
		// Build result, only stack items were enqueued so all entries are
		// stack entries
		return new JavaStackResult(this,
			new JavaStackState(this._locals, newstack, 1),
			new JavaStackEnqueueList(0, enq),
			new StateOperations(ops),
			JavaStackResult.makeOutput(dest));
	}
	
	/**
	 * Loads the specified local variable onto the stack.
	 *
	 * @param __i The local to load from.
	 * @return The result of the operation.
	 * @throws InvalidClassFormatException If the local is not valid or
	 * the stack overflows.
	 * @since 2019/03/30
	 */
	public final JavaStackResult doLocalLoad(int __i)
		throws InvalidClassFormatException
	{
		// {@squirreljme.error JC1h Load of local with no value.
		// (The index; The information about the source)}
		Info from = this._locals[__i];
		if (from.type.isNothing())
			throw new InvalidClassFormatException("JC1h " + __i + " " + from);
		
		// Space needed to be used on the stack
		JavaType type = from.type;
		boolean iswide = type.isWide();
		int space = (iswide ? 2 : 1);
		
		// {@squirreljme.error JC1i Stack would overflow loading local value.}
		Info[] stack = this._stack;
		int stacktop = this.stacktop;
		if (stacktop + space > stack.length)
			throw new InvalidClassFormatException("JC1i");
		
		// Setup new stack
		Info[] newstack = stack.clone();
		Info dest;
		newstack[stacktop] = (dest = stack[stacktop].newTypeValue(type,
			from.value, true));
		
		// Add top entry as well
		if (iswide)
			newstack[stacktop + 1] = stack[stacktop + 1].newTypeValue(
				type.topType(), from.value + 1, true);
		
		// Create resulting state
 		return new JavaStackResult(this,
			new JavaStackState(this._locals, newstack, stacktop + space),
			null,
			JavaStackResult.makeInput(from),
			JavaStackResult.makeOutput(dest));
	}
	
	/**
	 * Sets a local variable.
	 *
	 * @param __jt The type of entry to set.
	 * @param __l The local to set.
	 * @return Return the result of the set.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/06
	 */
	public final JavaStackResult doLocalSet(JavaType __jt, int __l)
		throws NullPointerException
	{
		if (__jt == null)
			throw new NullPointerException("NARG");
			
		Info[] locals = this._locals;
		
		// {@squirreljme.error JC1j Cannot write over a local variable which
		// is read-only. (The local)}
		Info olddest = locals[__l];
		if (olddest.readonly)
			throw new InvalidClassFormatException("JC1j " + olddest);
		
		// If the target local is an object it could be enqueued and it has
		// to be uncounted
		List<Integer> enq = new ArrayList<>();
		List<StateOperation> ops = new ArrayList<>();
		if (olddest.canEnqueue())
			enq.add(olddest.value);
		
		// If we are going to be writing over two locals we need to check
		// the other as well
		if (__jt.isWide())
		{
			// {@squirreljme.error JC1k Cannot write over a local variable
			// which is read-only. (The local)}
			Info wolddest = locals[__l + 1];
			if (wolddest.readonly)
				throw new InvalidClassFormatException("JC1k " + wolddest);
			
			// If the target local is an object it could be enqueued
			if (wolddest.canEnqueue())
				enq.add(wolddest.value);
		}
		
		// Go through the stack and uncache anything which refers to the
		// old destination by value
		Info[] newstack = this._stack.clone();
		int bumpreg = olddest.register,
			stacktop = this.stacktop;
		for (int i = 0; i < stacktop; i++)
		{
			Info ss = newstack[i];
			
			// If the value points to the local register then it is going to
			// be destroyed, so make sure the value is correctly restored and
			// the cached state of the stack is removed
			if (ss.value == bumpreg)
			{
				int ssreg = ss.register;
				JavaType sst = ss.type;
				
				// Copy the value from the local to the stack entry's true
				// register
				ops.add(StateOperation.copy(sst.isWide(), bumpreg, ssreg));
				
				// If the local is counted, then the destination spot on the
				// stack needs to be counted
				if (sst.isObject() && !olddest.nocounting)
					ops.add(StateOperation.count(ssreg));
				
				// Then this slot on the stack becomes just a non-cached direct
				// value
				newstack[i] = new Info(ssreg, sst, ssreg, false,
					olddest.nocounting);
				
				// Also un-cache wide values, remember that longs and doubles
				// are never counted
				if (sst.isWide())
					newstack[i + 1] = new Info(ssreg + 1, sst.topType(),
						ssreg + 1, false, false);
			}
		}
		
		// Setup new base local, remember that locals are never aliased but
		// they might use no counting
		Info[] newlocals = locals.clone();
		Info pushed;
		newlocals[__l] = (pushed = olddest.newTypeValue(__jt,
			olddest.register, false));
		
		// Additionally push top type as well
		if (__jt.isWide())
			newlocals[__l + 1] = newlocals[__l + 1].newTypeValue(
				__jt.topType(), pushed.register + 1, false);
		
		// Create resulting state
 		return new JavaStackResult(this,
			new JavaStackState(newlocals, newstack, stacktop),
			new JavaStackEnqueueList(enq.size(), enq),
			new StateOperations(ops),
			JavaStackResult.makeOutput(pushed));
	}
	
	/**
	 * Writes into the specified local variable from the top-most stack entry.
	 *
	 * Locals which are written to are never cached and are exempt from
	 * any kind caching.
	 *
	 * @param __l The local to store.
	 * @return The result of the store.
	 * @throws InvalidClassFormatException If the local cannot be written to.
	 * @since 2019/04/02
	 */
	public final JavaStackResult doLocalStore(int __l)
		throws InvalidClassFormatException
	{
		// Pop the value to store from the stack
		JavaStackResult stackpop = this.doStack(1);
		JavaStackResult.Input popped = stackpop.in(0);
		
		// Then perform the store from this previous result
		JavaStackResult stacksto = stackpop.after().doLocalSet(
			popped.type, __l);
		
		// Copy old operations over to add more potentially
		List<StateOperation> lsops = new ArrayList<>();
		for (StateOperation sop : stacksto.operations())
			lsops.add(sop);
		
		// If we are storing an object, make sure it is counted
		JavaStackResult.Output out = stacksto.out(0);
		if (popped.isObject() && popped.nocounting)
			lsops.add(0, StateOperation.count(popped.register));
		
		// The two states are nearly combined but most of the result comes
		// from the actual push
		return new JavaStackResult(this,
			stacksto.after,
			stacksto.enqueue,
			new StateOperations(lsops),
			popped, out);
	}
	
	/**
	 * Does nothing, keeping the state exactly the same but producing a
	 * result.
	 *
	 * @return The result of doing nothing.
	 * @since 2019/04/07
	 */
	public final JavaStackResult doNothing()
	{
		return new JavaStackResult(this, this, null);
	}
	
	/**
	 * Pops a certain number of variables and then pushes the given types
	 * to the stack. Note that all results of this operation will treat
	 * all of the target stack operations as new freshly obtained values
	 * with no caching performed on them.
	 *
	 * @param __n The number of locals to pop.
	 * @param __t The types to push.
	 * @return The result of the operation.
	 * @since 2019/03/30
	 */
	public final JavaStackResult doStack(int __n, JavaType... __t)
	{
		return this.doStack(__n, false, __t);
	}
	
	/**
	 * Pops a certain number of variables and then pushes the given types
	 * to the stack, this may also force caching on pushed values. Note that
	 * all results of this operation will treat
	 * all of the target stack operations as new freshly obtained values
	 * with no caching performed on them.
	 *
	 * @param __n The number of locals to pop.
	 * @param __nc If true then all the values being pushed will not be
	 * reference countable.
	 * @param __pts The types to push.
	 * @return The result of the operation.
	 * @throws IllegalArgumentException If the local count is negative or an
	 * attempt is made to push a top or nothing type.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/03/30
	 */
	public final JavaStackResult doStack(int __n, boolean __nc,
		JavaType... __pts)
		throws IllegalArgumentException, NullPointerException
	{
		// {@squirreljme.error JC1l Cannot pop a negative number of entries.}
		if (__n < 0)
			throw new IllegalArgumentException("JC1l");
		
		// Force blank types for pushing
		__pts = (__pts == null ? new JavaType[0] : __pts.clone());
		for (JavaType pt : __pts)
			if (pt == null)
				throw new NullPointerException("NARG");
			
			// {@squirreljme.error JC1m Cannot push nothing or top type.}
			else if (pt.isNothing() || pt.isTop())
				throw new IllegalArgumentException("JC1m");
		
		// Only the stack is operated on!
		Info[] stack = this._stack;
		int stacktop = this.stacktop,
			stacklimit = stack.length;
		
		// Enqueues to clear popped entries
		List<Integer> enqs = new ArrayList<>();
		List<StateOperation> ops = new ArrayList<>();
		
		// Pop entries off the stack first
		List<Info> popped = new ArrayList<>();
		for (int i = 0; i < __n; i++)
		{
			// {@squirreljme.error JC1n Stack underflow. (Origin state;
			// The number of items to op; Not reference counted?; The push
			// types.)}
			if (stacktop <= 0)
				throw new IllegalArgumentException(String.format(
					"JC1n %s (%d, %b, %s)",
					this, __n, __nc, Arrays.asList(__pts)));
			
			// Read top most entry, handle tops accordingly
			Info inf = stack[--stacktop];
			if (inf.type.isTop())
				inf = stack[--stacktop];
			
			// Only enqueue objects which are counting and which do not have
			// values of another register
			if (inf.canEnqueue())
				enqs.add(inf.value);
			
			// Was popped, so add to to the pop list
			popped.add(0, inf);
		}
		
		// Setup new stack for pushing
		Info[] newstack = stack.clone();
		
		// Push new entries to the stack
		List<Info> pushed = new ArrayList<>();
		for (JavaType pt : __pts)
		{
			// {@squirreljme.error JC1o Stack overflow. (Top; Limit)}
			if (stacktop >= stacklimit)
				throw new IllegalArgumentException("JC1o " + stacktop + " " +
					stacklimit);
			
			// Setup entry
			Info inf = newstack[stacktop];
			newstack[stacktop] = (inf = inf.newTypeValue(
				pt, inf.register, __nc));
			stacktop++;
			if (pt.isWide())
			{
				newstack[stacktop] = newstack[stacktop].newTypeValue(
					pt.topType(), inf.value + 1, __nc);
				stacktop++;
			}
			
			// Add to pushed set
			pushed.add(inf);
		}
		
		// Convert infos to I/O, note that there is a rare case in the compiler
		// where it nukes stack entries to nothing.
		List<JavaStackResult.InputOutput> ios = new ArrayList<>();
		for (Info i : popped)
			ios.add((i.isNothing() ? JavaStackResult.INPUT_ZERO :
				JavaStackResult.makeInput(i)));
		for (Info o : pushed)
			ios.add(JavaStackResult.makeOutput(o));
		
		// Build result
		return new JavaStackResult(this,
			new JavaStackState(this._locals, newstack, stacktop),
			new JavaStackEnqueueList(enqs.size(), enqs),
			new StateOperations(ops),
			ios.<JavaStackResult.InputOutput>toArray(
				new JavaStackResult.InputOutput[ios.size()]));
	}
	
	/**
	 * Performs the specified stack shuffling, which may be duplication or
	 * otherwise.
	 *
	 * @param __t The type of shuffle to perform.
	 * @return The result of the shuffle.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/03/30
	 */
	public final JavaStackResult doStackShuffle(JavaStackShuffleType __t)
		throws NullPointerException
	{
		if (__t == null)
			throw new NullPointerException("NARG");
		
		// Input stack properties
		Info[] stack = this._stack;
		int stackTop = this.stacktop;
		
		// Find function
		JavaStackShuffleType.Function func = this.findShuffleFunction(__t);
		
		// Debug
		if (__Debug__.ENABLED)
			Debugging.debugNote("Shuffle with: %s -> %s", __t, func);
		
		// Alternative implementation that uses the existing stack methods much
		// more
		if (true)
		{
			// Debug
			if (__Debug__.ENABLED)
				Debugging.debugNote(">>>>>>>> PRE-DETERMINE");
			
			// Determine all of the types that should be pushed to the target
			// operation
			JavaType[] pushTypes = this.__shuffleCalcType(func,
				this.doStack(func.in.logicalMax));
				
			// Debug
			if (__Debug__.ENABLED)
				Debugging.debugNote("@@PUSHTYPE: %s -> %s",
					func, Arrays.asList(pushTypes));
			
			// Perform the push and pop operation all at once to determine
			// what the resultant state of the stack is
			JavaStackResult pushPop = this.doStack(func.in.logicalMax,
				pushTypes);
				
			// Debug
			if (__Debug__.ENABLED)
				Debugging.debugNote("<<<<<<<< POST-DETERMINE");
			
			// Debug
			if (__Debug__.ENABLED)
				Debugging.debugNote("@@PUSHPOP: %s -> %s",
					func, pushPop);
			
			// Get the original enqueues, if a value is used it will get
			// dropped off this list
			Set<Integer> enqs = new LinkedHashSet<>(
				IntegerArrayList.asList(pushPop.enqueue().registers()));
			Set<Integer> dropEnq = new HashSet<>();
			
			// State operations that will be done first on copies and otherwise
			// when reading/writing, done before copies and after copies
			Set<StateOperation> preStateOps = new LinkedHashSet<>();
			Set<StateOperation> postStateOps = new LinkedHashSet<>();
			
			// Pre-determine soft register ids for temporarily copied inputs
			JavaStackResult.Input[] ins = pushPop.in();
			SoftRegister[] softCopy = new SoftRegister[ins.length];
			for (int i = 0; i < ins.length; i++)
				softCopy[i] = SoftRegister.of(true, i);
			
			// Perform respective copies for the inputs and outputs
			JavaStackResult.Output[] outs = pushPop.out();
			for (int i = 0, n = outs.length; i < n; i++)
			{
				// Ignore any wides in the output
				JavaStackResult.Output out = outs[i];
				if (out.type.isTop())
					continue;
				
				// Get the input variable associated with the given output
				int inSlot = func.in.logicalSlot(func.in.findVariableSlot(
					func.out.logicalVariable(i)));
				JavaStackResult.Input in = ins[inSlot];
				
				// Debug
				if (__Debug__.ENABLED)
					Debugging.debugNote("@@INOUT: %s -> %s", in, out);
				
				// If these point to the same register, we do not need to
				// make a defensive copy at all
				if (in.register == out.register)
				{
					// Drop garbage collection on this register so that it is
					// not destroyed in value at all
					dropEnq.add(in.register);
					
					continue;
				}
				
				// Is this a wide operation?
				boolean isWide = in.type.isWide();
				
				// This register is eligible for garbage collection
				if (enqs.contains(in.register))
				{
					// We are going to copy this register, so add an additional
					// count to it
					preStateOps.add(StateOperation.count(
						SoftRegister.of(false, in.register)));
					
					// Drop garbage collection on this, since it is used
					dropEnq.add(in.register);
				}
				
				// Copy to temporary register
				preStateOps.add(StateOperation.copy(isWide,
					SoftRegister.of(false, in.register),
					softCopy[inSlot]));
				
				// Copy from temporary to target
				postStateOps.add(StateOperation.copy(isWide,
					softCopy[inSlot],
					SoftRegister.of(false, out.register)));
			}
			
			// Combine state operations into one
			List<StateOperation> stateOps = new ArrayList<>();
			stateOps.addAll(preStateOps);
			stateOps.addAll(postStateOps);
			
			// Remove any enqueues we want to drop out
			for (Integer i : dropEnq)
				enqs.remove(i);
			
			// Build resultant stack operation
			return new JavaStackResult(this,
				new JavaStackState(this._locals,
					pushPop.after._stack, pushPop.after.stacktop),
				new JavaStackEnqueueList(enqs.size(), enqs),
				new StateOperations(stateOps));
		}
		
		// Determine stack properties of the pop
		int maxPop = func.in.max;
		int maxPush = func.out.max;
		int baseTop = stackTop - maxPop;
		
		// Load section of stack to be popped 
		List<Info> pops = new ArrayList<>(maxPop);
		for (int i = baseTop; i < stackTop; i++)
			pops.add(stack[i]);
		
		// Debug
		if (__Debug__.ENABLED)
			Debugging.debugNote("Popped: %s", pops);
		
		// Input and output slots
		JavaStackShuffleType.Slots inSlots = func.in;
		JavaStackShuffleType.Slots outSlots = func.out;
		
		// Map virtual variables to entries on the input so we know what is
		// what. Also include the register values are stored at for caching.
		Map<Integer, Info> source = new LinkedHashMap<>();
		Map<Integer, Integer> storedAt = new LinkedHashMap<>();
		for (int ldx = 0; ldx < maxPop; ldx++)
		{
			int var = inSlots._var[ldx];
			if (var >= 0)
			{
				source.put(var, pops.get(ldx));
				storedAt.put(var, -1);
			}
		}
		
		// Debug
		if (__Debug__.ENABLED)
			Debugging.debugNote("Source map: %s", source);
		
		// Number of entries to push
		int pushCount = outSlots.max;
		
		// Initialize new stack
		Info[] newStack = stack.clone();
		int newStackTop = baseTop + pushCount;
		
		// Any enqueues and operations to perform
		List<Integer> enqs = new ArrayList<>();
		List<StateOperation> stateOps = new ArrayList<>();
		
		// For registers which have a value collision, they must be
		// pre-copied to temporary space
		int tempbase = this.usedregisters;
		Map<Integer, Integer> preCopy = new LinkedHashMap<>();
		
		// Setup the new stack by pushing around
		for (int at = baseTop, ldx = 0; ldx < pushCount; at++, ldx++)
		{
			// Pushing a top type?
			int vardx = outSlots._var[ldx];
			if (vardx < 0)
			{
				// Set the current to the appropriate top type of the entry
				// before this one
				Info prev = newStack[at - 1];
				newStack[at] = newStack[at].newTypeValue(prev.type.topType(),
					prev.value + 1, false);
				
				continue;
			}
			
			// Get the source info to use for this slot
			// Also the original destination
			Info ssl = source.get(vardx);
			Info ods = newStack[at];
			
			// Is this type wide?
			boolean isWide = ssl.type.isWide();
			
			// If the value was never used before, try to use the original
			// register for it
			int useVal = storedAt.get(vardx);
			if (useVal < 0)
				useVal = ssl.value;
			
			// Using the value position would violate the strict no-aliasing
			// of future registers
			boolean collision = useVal > ods.register;
			
			if (collision)
			{
				// Debug
				if (__Debug__.ENABLED)
					Debugging.debugNote("Collide " +
					"origUseVal=%d ssl.value=%d useVal=%d ods.register=%d",
					storedAt.get(vardx), ssl.value, useVal, ods.register);
				
				// Try to use an already copied value, if it has not yet had
				// a pre-copy then map it to the copied source instead
				Integer pre = preCopy.get(useVal);
				if (pre == null)
				{
					preCopy.put(useVal,
						(pre = (isWide ? -tempbase : tempbase)));
					tempbase += (isWide ? 2 : 1);
				}
				
				// The value to use is the destination register because it
				// will be copied
				useVal = ods.register;
				StateOperation stateOp = StateOperation.copy(isWide,
					Math.abs(pre), useVal);
				stateOps.add(stateOp);
				
				// Debug
				if (__Debug__.ENABLED)
					Debugging.debugNote("Pre %d -> %d (%s)",
						pre, useVal, stateOp);
			}
			
			// Set value as being stored here
			storedAt.put(vardx, useVal);
			
			// Setup slot
			newStack[at] = newStack[at].newTypeValue(ssl.type, useVal,
				ssl.nocounting);
		}
		
		// Pre-copies which are needed, but make sure that the original
		// link order is maintained, negative pre-maps are treated as
		// being wide
		int vdat = 0;
		for (Map.Entry<Integer, Integer> e : preCopy.entrySet())
			stateOps.add(vdat++, StateOperation.copy(
				e.getValue() < 0, e.getKey(), Math.abs(e.getValue())));
				
		// Debug
		if (__Debug__.ENABLED)
			Debugging.debugNote("Pre-copies: %s", preCopy);
		
		// Build
		return new JavaStackResult(this,
			new JavaStackState(this._locals, newStack, newStackTop),
			new JavaStackEnqueueList(enqs.size(), enqs),
			new StateOperations(stateOps));
	}
	
	/**
	 * Throws a variable from the top of the stack and tosses it.
	 *
	 * @return The result of the throw.
	 * @since 2019/11/30
	 */
	public final JavaStackResult doThrow()
	{
		return this.doStack(1);
	}
	
	/**
	 * Transitions to the given stack state.
	 *
	 * @param __ts The target to transition.
	 * @return The result of the transition and the operations used.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/11
	 */
	public final JavaStackResult doTransition(JavaStackState __ts)
		throws NullPointerException
	{
		if (__ts == null)
			throw new NullPointerException("NARG");
		
		// If this state is exactly the same as this one then no actual
		// transition work needs to be done, so no result is generated
		if (this.equals(__ts))
			return new JavaStackResult(this, this, null);
		
		// Debug
		if (__Debug__.ENABLED)
		{
			Debugging.debugNote("Will transition the stack!");
			Debugging.debugNote("From: %s", this);
			Debugging.debugNote("To  : %s", __ts);
		}
		
		// {@squirreljme.error JC1p A transition cannot be made where the
		// length of the stack differs. (The length of the source stack; The
		// length of the target stack)}
		int atop = this.stacktop,
			btop = __ts.stacktop;
		if (atop != btop)
			throw new InvalidClassFormatException("JC1p " + atop + " " + btop);
		
		// Used to store operations and enqueues
		List<Integer> stackenq = new ArrayList<>(),
			localenq = new ArrayList<>();
		List<StateOperation> ops = new ArrayList<>();
		
		// Source and destination stacks
		Info[] asta = this._stack,
			bsta = __ts._stack,
			aloc = this._locals,
			bloc = __ts._locals;
		
		// Transition both the stack and the locals
		for (int z = 0; z < 2; z++)
		{
			Info[] ainf,
				binf;
			int cap;
			
			// Stack transition
			boolean isstack = (z == 0);
			if (isstack)
			{
				ainf = asta;
				binf = bsta;
				cap = atop;
			}
			
			// Local transition
			else
			{
				ainf = aloc;
				binf = bloc;
				cap = aloc.length;
			}
			
			// Go through and transition the locals
			for (int i = 0, n = cap; i < n; i++)
			{
				Info a = ainf[i],
					b = binf[i];
				
				// They are exactly the same, so nothing needs to be done
				if (a.equals(b))
					continue;
				
				// The source is nothing
				JavaType at = a.type,
					bt = b.type;
				if (at.isNothing())
				{
					// Transition to a non-nothing type, just copy zero to it
					if (!bt.isNothing())
						ops.add(StateOperation.copy(
							bt.isWide(), 0, b.value));
				}
				
				// If the target is transitioning to nothing, then it will be
				// removed
				else if (bt.isNothing())
				{
					// If the A local is an object that is countable, then just
					// uncount it
					if (a.canEnqueue())
					{
						localenq.add(a.value);
						ops.add(StateOperation.uncount(a.value));
					}
					
					// If this is a nothing being put on the stack put a
					// zero value there to wipe it!
					if (isstack)
						ops.add(StateOperation.copy(
							false, a.value, b.register));
				}
				
				// Check if the types and values are compatible
				else
				{
					// Transitioning from no-counting to counting means that A
					// was never counted
					if (!a.canEnqueue() && b.canEnqueue())
						ops.add(StateOperation.count(a.value));
					
					// Transition from non-compatible types means that a copy
					// from zero is performed
					if (at.isObject() != bt.isObject() ||
						(!at.isObject() && !at.equals(bt)))
						ops.add(StateOperation.copy(
							bt.isWide(), 0, b.value));
					
					// Copy to destination, if the values differ
					else if (a.value != b.value)
						ops.add(StateOperation.copy(
							at.isWide(), a.value, b.value));
					
					// Going from counting to no counting means we probably
					// have an extra count somewhere
					if (a.canEnqueue() && !b.canEnqueue())
						ops.add(StateOperation.uncount(b.value));
				}
			}
		}
		
		// Merge the enqueues for locals and the stack
		int eqsat = localenq.size();
		localenq.addAll(stackenq);
		
		// The transition results in the target stack so we do not need to
		// initialize it, however we do need enqueues and operations
		return new JavaStackResult(this, __ts,
			new JavaStackEnqueueList(eqsat, localenq),
			new StateOperations(ops));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/03/30
	 */
	@Override
	public final boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (!(__o instanceof JavaStackState))
			return false;
		
		// Faster to compare hashcodes first since there are lots of values
		JavaStackState o = (JavaStackState)__o;
		if (this.hashCode() != o.hashCode())
			return false;
		
		return this.stacktop == o.stacktop &&
			Arrays.equals(this._locals, o._locals) &&
			Arrays.equals(this._stack, o._stack);
	}
	
	/**
	 * Goes through the stack map and filters the locals and types so that
	 * they match what is in the stack map table.
	 *
	 * @param __smts The source stack map.
	 * @return The resulting stack which has been filtered.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/13
	 */
	public final JavaStackState filterByStackMap(StackMapTableState __smts)
		throws NullPointerException
	{
		if (__smts == null)
			throw new NullPointerException("NARG");
		
		// Debug
		if (__Debug__.ENABLED)
			Debugging.debugNote("SMTS: %s", __smts);
			
		// Input stack properties
		Info[] locals = this._locals.clone(),
			stack = this._stack.clone();
		int stacktop = this.stacktop;
		
		// Used to just drop any changes if nothing changed
		boolean changed = false;
		
		// Filter local variables
		for (int i = 0, n = locals.length; i < n; i++)
		{
			// Get infos and state
			Info inf = locals[i];
			StackMapTableEntry sme = __smts.getLocal(i);
			
			// Type changed?
			if (!inf.type.equals(sme.type()))
			{
				// The local is being wiped away
				if (sme.type().isNothing())
					inf = new Info(inf.register, JavaType.NOTHING,
						inf.register, false, false);
				
				// Use this type instead
				else if (inf.type.isObject())
					inf = new Info(inf.register, sme.type(),
						inf.value, inf.readonly, inf.nocounting);
			}
			
			// Set if changed
			if (changed |= (locals[i] != inf))
				locals[i] = inf;
		}
		
		// Filter stack variables
		for (int i = 0; i < stacktop; i++)
		{
			// Get infos and state
			Info inf = stack[i];
			StackMapTableEntry sme = __smts.getStack(i);
			
			// Use type if the object is different
			if (!inf.type.equals(sme.type()) && inf.type.isObject())
				inf = new Info(inf.register, sme.type(),
					inf.value, inf.readonly, inf.nocounting);
			
			// Set if changed
			if (changed |= (stack[i] != inf))
				stack[i] = inf;
		}
		
		// Only return a new state if it actually changed
		if (changed)
			return new JavaStackState(locals, stack, stacktop);
		return this;
	}
	
	/**
	 * Locates the shuffle function that is used to pop from the stack
	 * accordingly to this stack state.
	 *
	 * @param __t The type of shuffle to perform.
	 * @return The matching shuffle function.
	 * @throws InvalidClassFormatException If the shuffle function was not
	 * found.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/04
	 */
	public final JavaStackShuffleType.Function findShuffleFunction(
		JavaStackShuffleType __t)
		throws InvalidClassFormatException, NullPointerException
	{
		if (__t == null)
			throw new NullPointerException("NARG");
		
		// Input stack properties
		Info[] stack = this._stack;
		int stacktop = this.stacktop;
		
		// Working pop list when match is found
		int basetop,
			maxpop;
		
		// Search for the matching function to use for this state
		for (JavaStackShuffleType.Function tryf : __t._functions)
		{
			// Clear for run
			
			// Input slots are used
			JavaStackShuffleType.Slots sls = tryf.in;
			
			// Too little on the stack to pop everything?
			maxpop = sls.max;
			basetop = stacktop - maxpop;
			if (basetop < 0)
				continue;
			
			// Go through slots and see if this is a match or not
			int at = basetop;
			for (int ldx = 0; at < stacktop; ldx++, at++)
			{
				Info i = stack[at];
				JavaType it = i.type;
				
				// Top-ness and wide-ness does not match
				if (it.isTop() != (sls._var[ldx] < 0) ||
					it.isWide() != sls._wide[ldx])
					break;
			}
			
			// If this index was reached then everything was valid
			if (at == stacktop)
				return tryf;
		}
		
		// {@squirreljme.error JC1q Could not find a match for performing
		// shuffled stack operations.}
		throw new InvalidClassFormatException("JC1q");
	}
	
	/**
	 * Obtains the given local.
	 *
	 * @param __i The local to obtain.
	 * @return The information for the local.
	 * @since 2019/03/30
	 */
	public final JavaStackState.Info getLocal(int __i)
	{
		return this._locals[__i];
	}
	
	/**
	 * Obtains the given stack entry.
	 *
	 * @param __i The stack entry to obtain.
	 * @return The information for the stack entry.
	 * @since 2019/03/30
	 */
	public final JavaStackState.Info getStack(int __i)
	{
		return this._stack[__i];
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/03/30
	 */
	@Override
	public final int hashCode()
	{
		int hash = this._hash;
		if (hash == 0)
			this._hash = (hash = this.stacktop -
				Arrays.asList(this._locals).hashCode() ^
				Arrays.asList(this._stack).hashCode());
		return hash;
	}
	
	/**
	 * Returns the number of local used.
	 *
	 * @return The locals used.
	 * @since 2019/04/06
	 */
	public final int maxLocals()
	{
		return this._locals.length;
	}
	
	/**
	 * Returns the maximum stack size.
	 *
	 * @return The maximum stack size.
	 * @since 2019/04/06
	 */
	public final int maxStack()
	{
		return this._stack.length;
	}
	
	/**
	 * Returns the resulting stack state which would be as if nothing were
	 * cached.
	 *
	 * @return The non-cached stack state.
	 * @since 2019/04/11
	 */
	public final JavaStackState nonCached()
	{
		// Create new copies of the state
		int stacktop = this.stacktop;
		Info[] locals = this._locals.clone(),
			stack = this._stack.clone();
		
		// Un-cache locals
		for (int i = 0, n = locals.length; i < n; i++)
		{
			Info info = locals[i];
			
			// Read-only values are never un-cached
			if (info.readonly)
				continue;
			
			// Map value to the register
			locals[i] = info.newValue(info.register, false);
		}
		
		// Un-cache the stack
		for (int i = 0; i < stacktop; i++)
		{
			Info info = stack[i];
			stack[i] = info.newValue(info.register, false);
		}
		
		// Build, do not return the new object if it ends up being the same
		JavaStackState rv = new JavaStackState(locals, stack, stacktop);
		if (this.equals(rv))
			return this;
		return rv;
	}
	
	/**
	 * Returns all of the enqueues which are possible if the entire stack
	 * and locals were to be flushed.
	 *
	 * @return The maximum possible enqueue list.
	 * @since 2019/03/30
	 */
	public final JavaStackEnqueueList possibleEnqueue()
	{
		// This is the same as a destroy
		return this.doDestroy(false).enqueue();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/03/30
	 */
	@Override
	public final String toString()
	{
		Reference<String> ref = this._string;
		String rv;
		
		if (ref == null || null == (rv = ref.get()))
		{
			StringBuilder sb = new StringBuilder("State:{L=");
			
			// Add locals
			sb.append(Arrays.asList(this._locals));
			
			// Add stack entries
			Info[] stack = this._stack;
			sb.append(", S=[");
			for (int i = 0, n = this.stacktop; i < n; i++)
			{
				if (i > 0)
					sb.append(", ");
				sb.append(stack[i]);
			}
			sb.append("]}");
			
			// Build
			this._string = new WeakReference<>((rv = sb.toString()));
		}
		
		return rv;
	}
	
	/**
	 * Determines the types that are used to push to the stack for when the
	 * stack shuffles are being calculated.
	 * 
	 * @param __func The function used.
	 * @param __popOnly The stack to check.
	 * @return The calculated Java Types of the entries to push.
	 * @since 2021/07/04
	 */
	private JavaType[] __shuffleCalcType(JavaStackShuffleType.Function __func,
		JavaStackResult __popOnly)
		throws NullPointerException
	{
		if (__func == null || __popOnly == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error JC50 Initial stack has output, it should have
		// no output.}
		if (__popOnly.outCount() != 0)
			throw new IllegalArgumentException("JC50");
		
		JavaStackResult.Input[] in = __popOnly.in();
		int inLen = in.length;
		
		// Read the input types
		JavaType[] inType = new JavaType[inLen];
		for (int i = 0; i < inLen; i++)
			inType[i] = in[i].type;
		
		return __func.layerTypes(inType);
	}
	
	/**
	 * Initializes the stack state based off the given stack map table state,
	 * this should only be used for the initial seed of the stack state.
	 *
	 * @param __s The state to base off.
	 * @param __lw Local variables which have been written, this is used to
	 * set flags where locals are cached and can never be written to.
	 * @return The result stack state.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/03/30
	 */
	public static JavaStackState of(StackMapTableState __s, int... __lw)
		throws NullPointerException
	{
		if (__s == null)
			throw new NullPointerException("NARG");
		
		// Optional, might not be specified, but also sort it for searching
		__lw = (__lw == null ? new int[0] : __lw.clone());
		Arrays.sort(__lw);
		
		// Get size of the entries
		int maxlocals = __s.maxLocals(),
			maxstack = __s.maxStack(),
			stacktop = __s.depth();
		
		// Force the stack to have at least a single entry for exception
		// handlers
		if (maxstack == 0)
			maxstack++;
		
		// Setup output infos
		Info[] locals = new Info[maxlocals],
			stack = new Info[maxstack];
		
		// Register position for the slot
		int rpos = JavaStackState.BASE_REGISTER;
		
		// Initialize locals
		for (int i = 0; i < maxlocals; i++)
		{
			StackMapTableEntry from = __s.getLocal(i);
			
			// This local is considered read-only if it is not written to
			boolean ro = !(Arrays.binarySearch(__lw, i) >= 0);
			
			// Is there a type here?
			JavaType t = from.type();
			
			// Setup info here
			locals[i] = new Info(rpos, t, (t.isNothing() ? -1 : rpos), ro, ro);
			rpos++;
		}
		
		// Initialize stack
		for (int i = 0; i < maxstack; i++)
		{
			// Past end of stack?
			if (i >= stacktop)
				stack[i] = new Info(rpos++, JavaType.NOTHING, -1, false,
					false);
			
			// Normal entry
			else
			{
				StackMapTableEntry from = __s.getStack(i);
				
				// Setup info here
				stack[i] = new Info(rpos, from.type(), rpos, false, false);
				rpos++;
			}
		}
		
		// Build it
		return new JavaStackState(locals, stack, stacktop);
	}
	
	/**
	 * Contains information on the individual stack slots.
	 *
	 * @since 2019/03/30
	 */
	public static final class Info
	{
		/** The register position. */
		public final int register;
		
		/** The type. */
		public final JavaType type;
		
		/** The value register. */
		public final int value;
		
		/** Is this read-only? */
		public final boolean readonly;
		
		/** Do not use counting. */
		public final boolean nocounting;
		
		/** String representation. */
		private Reference<String> _string;
		
		/** Hash. */
		private int _hash;
		
		/**
		 * Initializes the information.
		 *
		 * @param __rp The register.
		 * @param __t The type.
		 * @param __rv The value register.
		 * @throws NullPointerException On null arguments.
		 * @since 2019/03/31
		 */
		public Info(int __rp, JavaType __t, int __rv)
			throws NullPointerException
		{
			this(__rp, __t, __rv, false, false);
		}
		
		/**
		 * Initializes the information.
		 *
		 * @param __rp The register.
		 * @param __t The type.
		 * @param __rv The value register.
		 * @param __ro Is this read-only?
		 * @param __nc Is no counting to be used?
		 * @throws NullPointerException On null arguments.
		 * @since 2019/03/31
		 */
		public Info(int __rp, JavaType __t, int __rv, boolean __ro,
			boolean __nc)
			throws NullPointerException
		{
			if (__t == null)
				throw new NullPointerException("NARG");
			
			// If no value was set, just set it to the position
			if (!__t.isNothing() && __rv < 0)
				__rv = __rp;
			
			// Set
			this.register = __rp;
			this.type = __t;
			this.value = (__rv = (__t.isNothing() ? -1 : __rv));
			this.readonly = __ro;
			this.nocounting = __t.isObject() &&
				(__nc || __rp != __rv || __rv < 0);
		}
		
		/**
		 * Can this be enqueued?
		 *
		 * @return If this can be enqueued.
		 * @since 2019/03/31
		 */
		public final boolean canEnqueue()
		{
			if (this.nocounting)
				return false;
			
			return this.type.isObject() &&
				this.register == this.value;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2019/03/30
		 */
		@Override
		public final boolean equals(Object __o)
		{
			if (this == __o)
				return true;
			
			if (!(__o instanceof Info))
				return false;
			
			Info o = (Info)__o;
			if (this.hashCode() != o.hashCode())
				return false;
			
			return this.register == o.register &&
				this.type.equals(o.type) &&
				this.value == o.value &&
				this.readonly == o.readonly &&
				this.nocounting == o.nocounting;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2019/03/30
		 */
		@Override
		public final int hashCode()
		{
			int rv = this._hash;
			if (rv == 0)
				this._hash = (rv = this.register + this.type.hashCode() +
					this.value + (this.readonly ? 12873 : -18723) +
					(this.nocounting ? 987214 : -2143));
			return rv;
		}
		
		/**
		 * Is this the nothing type?
		 *
		 * @return If this is the nothing type.
		 * @since 2019/05/29
		 */
		public final boolean isNothing()
		{
			return this.type.isNothing();
		}
		
		/**
		 * Returns information with a new type and value.
		 *
		 * @param __t The type to use.
		 * @param __v The value to use.
		 * @param __nc Do not count this?
		 * @return The new information.
		 * @throws NullPointerException On null arguments.
		 * @since 2019/03/31
		 */
		public final Info newTypeValue(JavaType __t, int __v, boolean __nc)
			throws NullPointerException
		{
			Info rv = new Info(this.register, __t, __v, false, __nc);
			if (this.equals(rv))
				return this;
			return rv;
		}
		
		/**
		 * Sets up info with a new value, using the same type.
		 *
		 * @param __v The new value.
		 * @param __nc Do not count this?
		 * @return The resulting value.
		 * @since 2019/04/11
		 */
		public final Info newValue(int __v, boolean __nc)
		{
			Info rv = new Info(this.register, this.type, __v, false, __nc);
			if (this.equals(rv))
				return this;
			return rv;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2019/03/30
		 */
		@Override
		public final String toString()
		{
			Reference<String> ref = this._string;
			String rv;
			
			if (ref == null || null == (rv = ref.get()))
			{
				int register = this.register;
				JavaType type = this.type;
				boolean ro = this.readonly,
					nc = this.nocounting;
				
				// Use a compact format for nothing
				if (type.isNothing())
					rv = "--(r" + register + ")";
				
				// Otherwise use a more compact form
				// Previously it was {V=r4 (r4), T=I, F=RONC}, however that
				// takes up too much room and is hard to read
				else
				{
					// If value == register, there is no point in duplicating
					int value = this.value;
					rv = String.format("%s:%s%s%s%s",
						(value != register ?
							("r" + value + "(" + register + ")") :
							("r" + value)),
						this.type, ((ro || nc) ? ":" : ""),
						(ro ? "R" : ""), (nc ? "N" : ""));
				}
				
				this._string = new WeakReference<>(rv);
			}
			
			return rv;
		}
	}
}

