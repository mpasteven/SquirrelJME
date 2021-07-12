// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.nncc;

import cc.squirreljme.runtime.cldc.debug.Debugging;
import dev.shadowtail.classfile.mini.MinimizedPoolEntryType;
import dev.shadowtail.classfile.pool.NotedString;
import dev.shadowtail.classfile.summercoat.register.ExecutablePointer;
import dev.shadowtail.classfile.summercoat.register.IntValueRegister;
import dev.shadowtail.classfile.summercoat.register.MemHandleRegister;
import dev.shadowtail.classfile.summercoat.register.PlainRegister;
import dev.shadowtail.classfile.summercoat.register.Register;
import dev.shadowtail.classfile.summercoat.register.RuntimePoolPointer;
import dev.shadowtail.classfile.summercoat.register.TypedRegister;
import dev.shadowtail.classfile.summercoat.register.Volatile;
import dev.shadowtail.classfile.summercoat.register.WideRegister;
import dev.shadowtail.classfile.xlate.CompareType;
import dev.shadowtail.classfile.xlate.DataType;
import dev.shadowtail.classfile.xlate.MathType;
import dev.shadowtail.classfile.xlate.StackJavaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.multiphasicapps.classfile.InstructionJumpTarget;

/**
 * This is used to build {@link NativeCode} and add instructions to it.
 *
 * @since 2019/03/16
 */
@SuppressWarnings("UnusedReturnValue")
public final class NativeCodeBuilder
{
	/** Label positions. */
	final Map<NativeCodeLabel, Integer> _labels =
		new LinkedHashMap<>();
	
	/** Temporary instruction layout. */
	final Map<Integer, Point> _points =
		new LinkedHashMap<>();
	
	/** Next address to use. */
	int _nextaddr;
	
	/**
	 * Initializes the code builder at the default start address.
	 *
	 * @since 2019/03/22
	 */
	public NativeCodeBuilder()
	{
		this._nextaddr = 0;
	}
	
	/**
	 * Initializes the code builder at the given start address.
	 *
	 * @param __pc The address to start at.
	 * @since 2019/03/22
	 */
	public NativeCodeBuilder(int __pc)
	{
		this._nextaddr = __pc;
	}
	
	/**
	 * Adds a new instruction.
	 *
	 * @param __op The operation to add.
	 * @param __args The arguments to the operation.
	 * @return The resulting temporary instruction.
	 * @throws IllegalArgumentException If the argument count is incorrect.
	 * @throws NullPointerException On null arguments.
	 * @deprecated Do not call this method to add new instructions as it
	 * does not provide any kind of type safety that could be utilized by the
	 * compiler.
	 * @since 2020/11/24
	 */
	@Deprecated
	public final NativeInstruction add(int __op, Object... __args)
		throws IllegalArgumentException, NullPointerException
	{
		return this.__add(__op, __args);
	}
	
	/**
	 * Adds a marked breakpoint.
	 * 
	 * @param __mark The marker to use.
	 * @param __text Optional marking text.
	 * @return The generated instruction.
	 * @since 2021/01/24
	 */
	public final NativeInstruction addBreakpoint(int __mark, String __text)
	{
		return this.add(NativeInstructionType.BREAKPOINT_MARKED, __mark,
			new NotedString((__text == null ? "" : __text)));
	}
	
	/**
	 * Adds a copy from one register to another.
	 *
	 * @param __from The source.
	 * @param __to The destination.
	 * @return The resulting instruction.
	 * @deprecated Use the type safe {@link #addCopy(Register, Register)}
	 * instead.
	 * @since 2019/04/12
	 */
	@Deprecated
	public final NativeInstruction addCopy(int __from, int __to)
	{
		return this.<PlainRegister>addCopy(
			new PlainRegister(__from), new PlainRegister(__to));
	}
	
	/**
	 * Adds a copy from one register to another.
	 *
	 * @param <R> The register type to copy.
	 * @param __from The source.
	 * @param __to The destination.
	 * @return The resulting instruction.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/28
	 */
	public final <R extends Register> NativeInstruction addCopy(
		R __from, R __to)
		throws NullPointerException
	{
		if (__from == null || __to == null)
			throw new NullPointerException("NARG");
		
		return this.__add(NativeInstructionType.COPY,
			__from, __to);
	}
	
	/**
	 * Adds an integer comparison instruction.
	 *
	 * @param __ct The type of comparison to make
	 * @param __a The first register.
	 * @param __b The register to compare against.
	 * @param __jt The target of the jump.
	 * @return The resulting instruction.
	 * @throws NullPointerException On null arguments.
	 * @deprecated Use the type safe {@link #addIfICmp(CompareType,
	 * IntValueRegister, IntValueRegister, NativeCodeLabel)} instead. 
	 * @since 2019/04/10
	 */
	@Deprecated
	public final NativeInstruction addIfICmp(CompareType __ct, int __a,
		int __b, NativeCodeLabel __jt)
		throws NullPointerException
	{
		return this.addIfICmp(__ct, IntValueRegister.of(__a),
			IntValueRegister.of(__b), __jt);
	}
	
	/**
	 * Adds an integer comparison instruction.
	 *
	 * @param __ct The type of comparison to make
	 * @param __a The first register.
	 * @param __b The register to compare against.
	 * @param __jt The target of the jump.
	 * @return The resulting instruction.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/28
	 */
	public final NativeInstruction addIfICmp(CompareType __ct,
		IntValueRegister __a, IntValueRegister __b, NativeCodeLabel __jt)
		throws NullPointerException
	{
		if (__ct == null || __a == null || __b == null || __jt == null)
			throw new NullPointerException("NARG");
		
		// Build operation
		return this.__add(
			NativeInstructionType.IF_ICMP | __ct.ordinal(),
			__a, __b, __jt);
	}
	
	/**
	 * Adds a jump if the given register is negative, that is less than
	 * zero.
	 *
	 * @param __a The register to check.
	 * @param __jt The target of the jump.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/31
	 */
	public final NativeInstruction addIfNegative(IntValueRegister __a,
		NativeCodeLabel __jt)
		throws NullPointerException
	{
		if (__jt == null)
			throw new NullPointerException("NARG");
		
		return this.addIfICmp(CompareType.LESS_THAN, __a,
			IntValueRegister.ZERO, __jt);
	}
	
	/**
	 * Adds a jump if the given register is not zero. No reference clears are
	 * performed by this call.
	 *
	 * @param __a The register to check.
	 * @param __jt The target of the jump.
	 * @throws NullPointerException On null arguments.
	 * @deprecated Use the type-safe {@link #addIfNonZero(IntValueRegister,
	 * NativeCodeLabel)} instead.
	 * @since 2019/04/11
	 */
	@Deprecated
	public final NativeInstruction addIfNonZero(int __a, NativeCodeLabel __jt)
		throws NullPointerException
	{
		return this.addIfNonZero(IntValueRegister.of(__a), __jt);
	}
	
	/**
	 * Adds a jump if the given register is not zero. No reference clears are
	 * performed by this call.
	 *
	 * @param __a The register to check.
	 * @param __jt The target of the jump.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/28
	 */
	public final NativeInstruction addIfNonZero(IntValueRegister __a,
		NativeCodeLabel __jt)
		throws NullPointerException
	{
		if (__a == null || __jt == null)
			throw new NullPointerException("NARG");
		
		return this.addIfICmp(CompareType.NOT_EQUALS,
			__a, IntValueRegister.ZERO, __jt);
	}
	
	/**
	 * Jumps if a memory handle is not {@code null}.
	 * 
	 * @param __ir The register to check.
	 * @param __jump The target to jump to if null.
	 * @return The generated instruction.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/02/09
	 */
	public NativeInstruction addIfNotNull(MemHandleRegister __ir,
		NativeCodeLabel __jump)
		throws NullPointerException
	{
		if (__ir == null || __jump == null)
			throw new NullPointerException("NARG");
		
		return this.addIfNonZero(__ir.asIntValue(), __jump);
	}
	
	/**
	 * Jumps if a memory handle is {@code null}.
	 * 
	 * @param __ir The register to check.
	 * @param __jump The target to jump to if null.
	 * @return The generated instruction.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/28
	 */
	public NativeInstruction addIfNull(MemHandleRegister __ir,
		NativeCodeLabel __jump)
		throws NullPointerException
	{
		if (__ir == null || __jump == null)
			throw new NullPointerException("NARG");
		
		return this.addIfZero(__ir.asIntValue(), __jump);
	}
	
	/**
	 * Adds a jump if the given register is positive.
	 *
	 * @param __a The register to check.
	 * @param __jt The target of the jump.
	 * @throws NullPointerException On null arguments.
	 * @deprecated Use the type safe {@link #addIfPositive(IntValueRegister,
	 * NativeCodeLabel)} instead. 
	 * @since 2019/11/30
	 */
	@Deprecated
	public final NativeInstruction addIfPositive(int __a, NativeCodeLabel __jt)
		throws NullPointerException
	{
		return this.addIfPositive(IntValueRegister.of(__a), __jt);
	}
	
	/**
	 * Adds a jump if the given register is positive, that is greater than
	 * zero.
	 *
	 * @param __a The register to check.
	 * @param __jt The target of the jump.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/28
	 */
	public final NativeInstruction addIfPositive(IntValueRegister __a,
		NativeCodeLabel __jt)
		throws NullPointerException
	{
		if (__jt == null)
			throw new NullPointerException("NARG");
		
		return this.addIfICmp(CompareType.GREATER_THAN, __a,
			IntValueRegister.ZERO, __jt);
	}
	
	/**
	 * Adds a jump if the given register is zero. No reference clears are
	 * performed by this call.
	 *
	 * @param __a The register to check.
	 * @param __jt The target of the jump.
	 * @throws NullPointerException On null arguments.
	 * @deprecated Use {@link NativeCodeBuilder#addIfZero(
	 * IntValueRegister, NativeCodeLabel)}. 
	 * @since 2019/04/10
	 */
	@Deprecated
	public final NativeInstruction addIfZero(int __a, NativeCodeLabel __jt)
		throws NullPointerException
	{
		if (__jt == null)
			throw new NullPointerException("NARG");
		
		return this.addIfICmp(CompareType.EQUALS, __a,
			NativeCode.ZERO_REGISTER, __jt);
	}
	
	/**
	 * Adds a jump if the given register is zero. No reference clears are
	 * performed by this call.
	 *
	 * @param __a The register to check.
	 * @param __jt The target of the jump.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/24
	 */
	public final NativeInstruction addIfZero(IntValueRegister __a,
		NativeCodeLabel __jt)
		throws NullPointerException
	{
		if (__jt == null)
			throw new NullPointerException("NARG");
		
		return this.addIfICmp(CompareType.EQUALS, __a,
			IntValueRegister.ZERO, __jt);
	}
	
	/**
	 * Adds a goto which goes to the following location.
	 *
	 * @param __jt The target of the jump.
	 * @return The resulting instruction.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/10
	 */
	public final NativeInstruction addGoto(NativeCodeLabel __jt)
		throws NullPointerException
	{
		if (__jt == null)
			throw new NullPointerException("NARG");
		
		return this.addIfICmp(CompareType.TRUE, NativeCode.ZERO_REGISTER,
			NativeCode.ZERO_REGISTER, __jt);
	}
	
	/**
	 * Loads an integer constant to a register.
	 * 
	 * @param __v The constant to load.
	 * @param __to The target register.
	 * @return The generated instruction.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/28
	 */
	public NativeInstruction addIntegerConst(int __v, IntValueRegister __to)
		throws NullPointerException
	{
		if (__to == null)
			throw new NullPointerException("NARG");
		
		return this.addMathConst(
			DataType.INTEGER.toStackJavaType(), MathType.OR,
			IntValueRegister.ZERO.register, __v, __to.register);
	}
	
	/**
	 * Adds an invocation of a pool and pointer value.
	 * 
	 * @param __exec The executable pointer.
	 * @param __pool The pool pointer.
	 * @param __args Arguments to the call.
	 * @return The created instruction.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/27
	 */
	public NativeInstruction addInvokePoolAndPointer(ExecutablePointer __exec,
		RuntimePoolPointer __pool, Register... __args)
	{
		return this.addInvokePoolAndPointer(__exec, __pool,
			new RegisterList(__args));
	}
	
	/**
	 * Adds an invocation of a pool and pointer value.
	 * 
	 * @param __exec The executable pointer.
	 * @param __pool The pool pointer.
	 * @param __args Arguments to the call.
	 * @return The created instruction.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/27
	 */
	public NativeInstruction addInvokePoolAndPointer(ExecutablePointer __exec,
		RuntimePoolPointer __pool, RegisterList __args)
		throws NullPointerException
	{
		if (__exec == null || __pool == null || __args == null)
			throw new NullPointerException("NARG");
			
		return this.__add(NativeInstructionType.INVOKE_POINTER_AND_POOL,
			__exec, __pool, __args);
	}
	
	/**
	 * Adds a math via constant operation.
	 *
	 * @param __jt The Java type.
	 * @param __mf The math function.
	 * @param __a Register A.
	 * @param __b Constant B.
	 * @param __c The result.
	 * @return The resulting register.
	 * @throws NullPointerException On null arguments.
	 * @deprecated Use {@link NativeCodeBuilder#addMathConst(StackJavaType,
	 * MathType, IntValueRegister, Number, IntValueRegister)}. 
	 * @since 2019/04/08
	 */
	@Deprecated
	public final NativeInstruction addMathConst(StackJavaType __jt,
		MathType __mf, int __a, Number __b, int __c)
		throws NullPointerException
	{
		return this.addMathConst(__jt, __mf,
			IntValueRegister.of(__a), __b, IntValueRegister.of(__c));
	}
	
	/**
	 * Adds a math via constant operation.
	 *
	 * @param __jt The Java type.
	 * @param __mf The math function.
	 * @param __a Register A.
	 * @param __b Constant B.
	 * @param __c The result.
	 * @return The resulting register.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/26
	 */
	public final NativeInstruction addMathConst(StackJavaType __jt,
		MathType __mf, IntValueRegister __a, Number __b, IntValueRegister __c)
		throws NullPointerException
	{
		if (__jt == null || __mf == null || __b == null)
			throw new NullPointerException("NARG");
		
		int op;
		switch (__jt)
		{
			case INTEGER:
				op = NativeInstructionType.MATH_CONST_INT;
				break;
				
				// {@squirreljme.error JC0t May only do math on integer.}
			case LONG:
			case FLOAT:
			case DOUBLE:
				throw new RuntimeException("JC0t");
			
			default:
				throw Debugging.oops(__jt.name());
		}
		
		// Build operation
		int rop = op | __mf.ordinal();
		return this.__add(rop, __a, __b, __c);
	}
	
	/**
	 * Adds a math via register operation.
	 *
	 * @param __jt The Java type.
	 * @param __mf The math function.
	 * @param __a Register A.
	 * @param __b Register B.
	 * @param __c The result.
	 * @return The resulting register.
	 * @throws NullPointerException On null arguments.
	 * @deprecated Use {@link NativeCodeBuilder#addMathReg(StackJavaType,
	 * MathType, IntValueRegister, IntValueRegister, IntValueRegister)}. 
	 * @since 2019/04/08
	 */
	@Deprecated
	public final NativeInstruction addMathReg(StackJavaType __jt,
		MathType __mf, int __a, int __b, int __c)
		throws NullPointerException
	{
		return this.addMathReg(__jt, __mf, IntValueRegister.of(__a),
			IntValueRegister.of(__b), IntValueRegister.of(__c));
	}
	
	/**
	 * Adds a math via register operation.
	 *
	 * @param __jt The Java type.
	 * @param __mf The math function.
	 * @param __a Register A.
	 * @param __b Register B.
	 * @param __c The result.
	 * @return The resulting register.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/24
	 */
	public final NativeInstruction addMathReg(StackJavaType __jt,
		MathType __mf, IntValueRegister __a, IntValueRegister __b,
		IntValueRegister __c)
		throws NullPointerException
	{
		if (__jt == null || __mf == null)
			throw new NullPointerException("NARG");
		
		int op;
		switch (__jt)
		{
			case INTEGER:
				op = NativeInstructionType.MATH_REG_INT;
				break;
				
				// {@squirreljme.error JC0u May only do math on integer.}
			case LONG:
			case FLOAT:
			case DOUBLE:
				throw new RuntimeException("JC0u");
			
			default:
				throw Debugging.oops(__jt.name());
		}
		
		// Build operation
		int rop = op | __mf.ordinal();
		return this.__add(rop, __a, __b, __c);
	}
	
	/**
	 * Adds counting down of a memory handle.
	 * 
	 * @param __r The register to count.
	 * @param __outCount The output of the register that receives the now
	 * current object count, can be used to determine if GC must be done.
	 * @return The created instruction.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/28
	 */
	public final NativeInstruction addMemHandleCountDown(MemHandleRegister __r,
		IntValueRegister __outCount)
		throws NullPointerException
	{
		if (__r == null || __outCount == null)
			throw new NullPointerException("NARG");
		
		return this.__add(NativeInstructionType.MEM_HANDLE_COUNT_DOWN,
			__r, __outCount);
	}
	
	/**
	 * Adds counting up of a memory handle.
	 * 
	 * @param __r The register to count.
	 * @return The created instruction.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/28
	 */
	public final NativeInstruction addMemHandleCountUp(MemHandleRegister __r)
		throws NullPointerException
	{
		if (__r == null)
			throw new NullPointerException("NARG");
		
		return this.__add(NativeInstructionType.MEM_HANDLE_COUNT_UP,
			__r);
	}
	
	/**
	 * Adds memory handle accessing.
	 * 
	 * @param __dt The data type for access.
	 * @param __load Is this a load?
	 * @param __inOut The input/output register.
	 * @param __mh The memory handle used.
	 * @param __offset The offset of the access.
	 * @return The generated instruction.
	 * @throws IllegalArgumentException If this is a wide access, use the
	 * wide variant of this method.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/28
	 */
	public final NativeInstruction addMemHandleAccess(DataType __dt,
		boolean __load, IntValueRegister __inOut, MemHandleRegister __mh,
		IntValueRegister __offset)
		throws IllegalArgumentException, NullPointerException
	{
		if (__dt == null || __inOut == null || __mh == null ||
			__offset == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error JC30 Cannot use wide values with this method.
		// (The data type)}
		if (__dt.isWide())
			throw new IllegalArgumentException("JC30 " + __dt);
		
		return this.__add(NativeInstructionType.MEM_HANDLE_OFF_REG |
			(__load ? 0b1000 : 0) | __dt.ordinal(),
			__inOut, __mh, __offset);
	}
	
	/**
	 * Adds memory handle accessing.
	 * 
	 * @param __dt The data type for access.
	 * @param __load Is this a load?
	 * @param __inOut The input/output register.
	 * @param __mh The memory handle used.
	 * @param __offset The offset of the access.
	 * @return The generated instruction.
	 * @throws IllegalArgumentException If this is a wide access, use the
	 * wide variant of this method.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/28
	 */
	public final NativeInstruction addMemHandleAccess(DataType __dt,
		boolean __load, IntValueRegister __inOut, MemHandleRegister __mh,
		int __offset)
		throws IllegalArgumentException, NullPointerException
	{
		if (__dt == null || __inOut == null || __mh == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error JC0i Cannot use wide values with this method.
		// (The data type)}
		if (__dt.isWide())
			throw new IllegalArgumentException("JC0i " + __dt);
		
		return this.__add(NativeInstructionType.MEM_HANDLE_OFF_ICONST |
			(__load ? 0b1000 : 0) | __dt.ordinal(),
			__inOut, __mh, __offset);
	}
	
	/**
	 * Adds memory handle accessing.
	 * 
	 * @param __dt The data type for access.
	 * @param __load Is this a load?
	 * @param __inOut The input/output register.
	 * @param __mh The memory handle used.
	 * @param __offset The offset of the access.
	 * @return The generated instruction.
	 * @throws IllegalArgumentException If this is a wide access, use the
	 * wide variant of this method.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/28
	 */
	public final NativeInstruction addMemHandleAccess(DataType __dt,
		boolean __load, WideRegister __inOut, MemHandleRegister __mh,
		IntValueRegister __offset)
		throws IllegalArgumentException, NullPointerException
	{
		if (__dt == null || __inOut == null || __mh == null ||
			__offset == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error JC4o Cannot use narrow values with this method.
		// (The data type)}
		if (!__dt.isWide())
			throw new IllegalArgumentException("JC4o " + __dt);
		
		return this.__add(NativeInstructionType.MEM_HANDLE_OFF_REG |
			(__load ? 0b1000 : 0) | __dt.ordinal(),
			__inOut.low, __inOut.high, __mh, __offset);
	}
	
	/**
	 * Adds memory handle accessing.
	 * 
	 * @param __dt The data type for access.
	 * @param __load Is this a load?
	 * @param __inOut The input/output register.
	 * @param __mh The memory handle used.
	 * @param __offset The offset of the access.
	 * @return The generated instruction.
	 * @throws IllegalArgumentException If this is a wide access, use the
	 * wide variant of this method.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/28
	 */
	public final NativeInstruction addMemHandleAccess(DataType __dt,
		boolean __load, WideRegister __inOut, MemHandleRegister __mh,
		int __offset)
		throws IllegalArgumentException, NullPointerException
	{
		if (__dt == null || __inOut == null || __mh == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error JC4n Cannot use narrow values with this method.
		// (The data type)}
		if (!__dt.isWide())
			throw new IllegalArgumentException("JC4n " + __dt);
		
		return this.__add(NativeInstructionType.MEM_HANDLE_OFF_ICONST |
			(__load ? 0b1000 : 0) | __dt.ordinal(),
			__inOut.low, __inOut.high, __mh, __offset);
	}
	
	/**
	 * Adds memory offset by constant.
	 *
	 * @param __dt The data type used.
	 * @param __load Is this a load operation?
	 * @param __v The value to store.
	 * @param __p The pointer.
	 * @param __off The offset.
	 * @return The generated instruction.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/12
	 */
	public final NativeInstruction addMemoryAccess(DataType __dt,
		boolean __load, IntValueRegister __v, WideRegister __p, int __off)
		throws NullPointerException
	{
		if (__dt == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error JC0v Must use wide version of this.}
		if (__dt.isWide())
			throw new IllegalArgumentException("JC0v");
		
		return this.__add(NativeInstructionType.MEMORY_OFF_ICONST |
			(__load ? 0b1000 : 0) | __dt.ordinal(),
			__v, __p.low, __p.high, __off);
	}
	
	/**
	 * Adds memory offset by constant.
	 *
	 * @param __dt The data type used.
	 * @param __load Is this a load operation?
	 * @param __v The value to store.
	 * @param __p The pointer.
	 * @param __off The offset.
	 * @return The generated instruction.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/02/14
	 */
	public final NativeInstruction addMemoryAccess(DataType __dt,
		boolean __load, WideRegister __v, WideRegister __p, int __off)
		throws NullPointerException
	{
		if (__dt == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error JC0y Must use narrow version of this.}
		if (!__dt.isWide())
			throw new IllegalArgumentException("JC0y");
		
		return this.__add(NativeInstructionType.MEMORY_OFF_ICONST |
			(__load ? 0b1000 : 0) | __dt.ordinal(),
			__v.low, __v.high, __p.low, __p.high, __off);
	}
	
	/**
	 * Adds memory offset by register.
	 *
	 * @param __dt The data type used.
	 * @param __load Is this a load operation?
	 * @param __v The value to load/store.
	 * @param __p The pointer.
	 * @param __off The offset.
	 * @return The generated instruction.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/12
	 */
	public final NativeInstruction addMemoryAccess(DataType __dt,
		boolean __load, IntValueRegister __v, WideRegister __p,
		IntValueRegister __off)
		throws NullPointerException
	{
		if (__dt == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error JC0x Must use wide version of this method.}
		if (__dt.isWide())
			throw new IllegalArgumentException("JC0x");
		
		// Generate
		return this.__add(NativeInstructionType.MEMORY_OFF_REG |
			(__load ? 0b1000 : 0) | __dt.ordinal(),
			__v, __p.low, __p.high, __off);
	}
	
	/**
	 * Adds memory offset by register.
	 *
	 * @param __dt The data type used.
	 * @param __load Is this a load operation?
	 * @param __v The value to load/store.
	 * @param __p The pointer.
	 * @param __off The offset.
	 * @return The generated instruction.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/02/14
	 */
	public final NativeInstruction addMemoryAccess(DataType __dt,
		boolean __load, WideRegister __v, WideRegister __p,
		IntValueRegister __off)
		throws NullPointerException
	{
		if (__dt == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error JC0w Must use narrow version of this method.}
		if (!__dt.isWide())
			throw new IllegalArgumentException("JC0w");
		
		// Generate
		return this.__add(NativeInstructionType.MEMORY_OFF_REG |
			(__load ? 0b1000 : 0) | __dt.ordinal(),
			__v.low, __p.high, __p.low, __p.high, __off);
	}
	
	/**
	 * Adds no operation.
	 * 
	 * @return The generated instruction.
	 * @since 2021/01/24
	 */
	public NativeInstruction addNop()
	{
		return this.addMathReg(StackJavaType.INTEGER, MathType.ADD,
			0, 0, 0);
	}
	
	/**
	 * Adds a marked ping
	 * 
	 * @param __mark The marker to use.
	 * @param __text Optional marking text.
	 * @return The generated instruction.
	 * @since 2021/01/24
	 */
	public final NativeInstruction addPing(int __mark, String __text)
	{
		return this.add(NativeInstructionType.PING, __mark,
			new NotedString((__text == null ? "" : __text)));
	}
	
	/**
	 * Loads from the constant pool.
	 * 
	 * @param <P> The pool type.
	 * @param __poolRef The pool reference.
	 * @param __register The target register.
	 * @return The generated instruction.
	 * @throws IllegalArgumentException If the types are not compatible.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/28
	 */
	public <P> NativeInstruction addPoolLoad(P __poolRef,
		TypedRegister<P> __register)
		throws IllegalArgumentException, NullPointerException
	{
		if (__poolRef == null || __register == null)
			throw new NullPointerException("NARG");
		
		// Check to make sure the type is compatible
		// {@squirreljme.error JC4p (The register; The defined pool type;
		// The pool reference type)}
		MinimizedPoolEntryType poolType = MinimizedPoolEntryType.ofClass(
			__register.type);
		if (__register.type != __poolRef.getClass() ||
			!poolType.isClass(__poolRef.getClass()))
			throw new IllegalArgumentException(String.format("JC4p %s %s %s",
				__register, poolType, __poolRef.getClass()));
		
		return this.__add(NativeInstructionType.LOAD_POOL,
			__poolRef, __register);
	}
	
	/**
	 * Adds a system call instruction.
	 * 
	 * @param __id The system call ID.
	 * @param __regs The registers to pass to the call.
	 * @return The generated instruction.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/28
	 */
	public NativeInstruction addSysCall(IntValueRegister __id,
		Register... __regs)
		throws NullPointerException
	{
		return this.addSysCall(__id, new RegisterList(__regs));
	}
	
	/**
	 * Adds a system call instruction.
	 * 
	 * @param __id The system call ID.
	 * @param __regs The registers to pass to the call.
	 * @return The generated instruction.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/29
	 */
	public NativeInstruction addSysCall(IntValueRegister __id,
		RegisterList __regs)
		throws NullPointerException
	{
		if (__id == null || __regs == null)
			throw new NullPointerException("NARG");
		
		return this.__add(NativeInstructionType.SYSTEM_CALL,
			__id, __regs);
	}
	
	/**
	 * Builds the register code, all references to other portions in the
	 * code itself are resolved.
	 *
	 * @return The built register code.
	 * @since 2019/03/22
	 */
	public final NativeCode build()
	{
		// Labels which point to addresses
		Map<NativeCodeLabel, Integer> labels = this._labels;
		
		// If there are any jump points which refer to the instruction index
		// directly following it, then remove the jump.
		// Also possibly perform other modifications
		List<Point> in = new ArrayList<>(this._points.values());
		for (int i = in.size() - 1; i >= 0; i--)
		{
			// Get the instruction and its various properties
			Point point = in.get(i);
			NativeInstruction ri = point.instruction;
			int rie = ri.encoding();
			
			NativeCodeLabel jt;
			
			// Depends on the encoding
			switch (rie)
			{
				case NativeInstructionType.IF_ICMP:
					jt = (NativeCodeLabel)ri.argument(2);
					break;
					
					// Not a jump
				default:
					continue;
			}
			
			// Check if this points to the instruction directly following this.
			boolean ptonext = (jt != null && (i + 1) == labels.get(jt));
			
			// If it does point to the next instruction, we either delete it
			// or replace the instruction with another depending on ref_clear
			// and such
			if (ptonext)
			{
				// Remove this instruction, it is pointless
				in.remove(i);
				
				// Move all of the label values down
				for (Map.Entry<NativeCodeLabel, Integer> e :
					labels.entrySet())
				{
					int val = e.getValue();
					if (val > i)
						e.setValue(val - 1);
				}
			}
		}
		
		// Resulting tables of instructions
		int n = in.size();
		NativeInstruction[] tabni = new NativeInstruction[n];
		
		// Go through input instructions and map them to real instructions
		for (int i = 0; i < n; i++)
		{
			// Get input point
			Point point = in.get(i);
			
			// The instruction is re-processed potentially
			NativeInstruction inst = point.instruction;
			
			// Used to detect if the instruction actually changed
			boolean didchange = false;
			
			// Arguments may be re-translated if they contain jumps
			Object[] args = inst.arguments().toArray();
			for (int j = 0, jn = args.length; j < jn; j++)
			{
				Object a = args[j];
				
				// Map any labels to indexes
				if ((didchange |= (a instanceof NativeCodeLabel)))
				{
					// {@squirreljme.error JC0z The specified label was
					// never defined. (The label)}
					Integer rlp = labels.get(a);
					if (rlp == null)
						throw new IllegalArgumentException("JC0z " + a);
					
					args[j] = new InstructionJumpTarget(rlp);
				}
			}
			
			// If the instruction changed, use the new one
			tabni[i] = (didchange ? new NativeInstruction(inst.op, args) :
				inst);
		}
		
		// Build
		return new NativeCode(tabni);
	}
	
	/**
	 * Adds a label at the current position.
	 *
	 * @param __lo The locality.
	 * @param __dx The index.
	 * @return The added label.
	 * @since 2019/03/22
	 */
	public final NativeCodeLabel label(String __lo, int __dx)
	{
		return this.label(new NativeCodeLabel(__lo, __dx), this._nextaddr);
	}
	
	/**
	 * Adds a label.
	 *
	 * @param __lo The locality.
	 * @param __dx The index.
	 * @param __pc The address to target.
	 * @return The added label.
	 * @since 2019/03/22
	 */
	public final NativeCodeLabel label(String __lo, int __dx, int __pc)
	{
		return this.label(new NativeCodeLabel(__lo, __dx), __pc);
	}
	
	/**
	 * Adds a label at the current position.
	 *
	 * @param __l The label to add.
	 * @return The added label.
	 * @since 2019/03/22
	 */
	public final NativeCodeLabel label(NativeCodeLabel __l)
	{
		return this.label(__l, this._nextaddr);
	}
	
	/**
	 * Adds a label.
	 *
	 * @param __pc The address to target.
	 * @return The added label.
	 * @since 2019/03/22
	 */
	public final NativeCodeLabel label(NativeCodeLabel __l, int __pc)
	{
		// Debug
		if (__Debug__.ENABLED)
			Debugging.debugNote("Label %s -> @%d", __l, __pc);
		
		// Add
		this._labels.put(__l, __pc);
		return __l;
	}
	
	/**
	 * Returns the target of the specified label.
	 *
	 * @param __n The label name.
	 * @param __dx The label index.
	 * @return The index of the instruction or {@code -1} if the label is not
	 * valid or the position is not yet known.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/04
	 */
	public final int labelTarget(String __n, int __dx)
		throws NullPointerException
	{
		if (__n == null)
			throw new NullPointerException("NARG");
		
		return this.labelTarget(new NativeCodeLabel(__n, __dx));
	}
	
	/**
	 * Returns the target of the specified label.
	 *
	 * @param __l The label to get the target of.
	 * @return The index of the instruction or {@code -1} if the label is not
	 * valid or the position is not yet known.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/03/22
	 */
	public final int labelTarget(NativeCodeLabel __l)
		throws NullPointerException
	{
		if (__l == null)
			throw new NullPointerException("NARG");
		
		Integer rv = this._labels.get(__l);
		return (rv == null ? -1 : rv);
	}
	
	
	/**
	 * Adds a new instruction.
	 *
	 * @param __op The operation to add.
	 * @param __args The arguments to the operation.
	 * @return The resulting temporary instruction.
	 * @throws IllegalArgumentException If the argument count is incorrect.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/03/16
	 */
	private NativeInstruction __add(int __op, Object... __args)
		throws IllegalArgumentException, NullPointerException
	{
		// Needed for argument format check
		InstructionFormat afmt = NativeInstruction.argumentFormat(__op);
		int fnar = afmt.size();
		
		// Build instruction
		NativeInstruction rv = new NativeInstruction(__op, __args);
		
		// {@squirreljme.error JC0q Operation has an incorrect number of
		// arguments. (The instruction)}
		if (fnar != __args.length)
			throw new IllegalArgumentException("JC0q " + rv);
		
		// Check format
		for (int i = 0; i < fnar; i++)
		{
			// Cannot be null
			Object o = __args[i];
			if (o == null)
				throw new NullPointerException("NARG");
			
			// Referencing a volatile register?
			int oi;
			if (o instanceof Volatile)
				oi = ((Volatile<?>)o).register.register;
			
			// Easily obtainable integer value?
			else
				oi = ((o instanceof Register) ? ((Register)o).register :
					((o instanceof Number) ? ((Number)o).intValue() : -1));
			
			// Make sure values are good
			switch (afmt.get(i))
			{
					// {@squirreljme.error JC0r Use of register which is out
					// of range of the maximum register count.
					// (The instruction; The register specified)}
				case VUREG:
					if (oi < 0 || oi >= NativeCode.MAX_REGISTERS)
						throw new IllegalArgumentException("JC0r " + rv +
							" " + oi);
					break;
				
					// {@squirreljme.error JC0s Cannot jump to a non-label.
					// (The instruction)}
				case VJUMP:
					if (!(o instanceof NativeCodeLabel))
						throw new IllegalArgumentException("JC0s " + rv);
					break;
				
					// Check that this is a valid pool type
				case VPOOL:
					MinimizedPoolEntryType.ofClass(o.getClass());
					break;
			}
		}
		
		// Create instruction
		int atdx = this._nextaddr++;
		
		// Debug
		if (__Debug__.ENABLED)
			Debugging.debugNote("@%d -> %s %s", atdx,
				NativeInstruction.mnemonic(__op), Arrays.asList(__args));
			
		// Store all information
		this._points.put(atdx, new Point(rv));
		return rv;
	}
}

