// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.summercoat;

import cc.squirreljme.runtime.cldc.debug.CallTraceElement;
import dev.shadowtail.classfile.nncc.NativeCode;
import dev.shadowtail.classfile.nncc.NativeInstruction;
import java.io.PrintStream;
import net.multiphasicapps.classfile.InstructionMnemonics;

/**
 * This represents a single slice of execution.
 *
 * @since 2019/10/27
 */
public final class ExecutionSlice
{
	/** The tracing information. */
	protected final CallTraceElement trace;
	
	/** The operand executed. */
	protected final int op;
	
	/** Operand arguments. */
	private final int[] _opargs;
	
	/** CPU registers. */
	private final int[] _cpuregs;
	
	/** Method call arguments (optional). */
	private final int[] _callargs;
	
	/**
	 * Creates an execution slice of the given frame information.
	 *
	 * @param __cte Call trace information.
	 * @param __rreg CPU registers stored here.
	 * @param __op The operand.
	 * @param __args The arguments.
	 * @param __argslen Number of arguments available.
	 * @param __reglist The register list for method calls.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/11/03
	 */
	public ExecutionSlice(CallTraceElement __cte, int[] __rreg,
		int __op, int[] __args, int __argslen, int[] __reglist)
		throws NullPointerException
	{
		if (__cte == null || __rreg == null || __args == null)
			throw new NullPointerException("NARG");
		
		this.trace = __cte;
		this._cpuregs = __rreg.clone();
		this.op = __op;
		this._callargs = (__reglist != null ? __reglist.clone() : null);
		
		int[] args = new int[__argslen];
		for (int i = 0; i < __argslen; i++)
			args[i] = __args[i];
		this._opargs = args;
	}
	
	/**
	 * Prints the information about this slice.
	 *
	 * @since 2019/10/27
	 */
	public final void print()
	{
		this.print(System.err);
	}
	
	/**
	 * Prints the information about this slice.
	 *
	 * @param __ps The stream to print to.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/10/27
	 */
	public final void print(PrintStream __ps)
		throws NullPointerException
	{
		if (__ps == null)
			throw new NullPointerException("NARG");
		
		// We need all of these!
		CallTraceElement trace = this.trace;
		int op = this.op;
		int[] opargs = this._opargs,
			cpuregs = this._cpuregs,
			callargs = this._callargs;
		
		// Print CPU operation state
		__ps.printf("$$$$$$$$ @%08x (%s/%s)%n", trace.address(),
			NativeInstruction.mnemonic(op),
			InstructionMnemonics.toString(trace.byteCodeInstruction()));
		
		// Print normal trace info
		__ps.printf("  - InFrm: %s%n", trace.toString());
		
		// Print instruction arguments
		__ps.print("  - OpArg: (");
		for (int i = 0, n = opargs.length; i < n; i++)
		{
			if (i > 0)
				__ps.print(", ");
			
			__ps.printf("%d [%xh]", opargs[i], opargs[i]);
		}
		__ps.println(")");
		
		// Do not print the large number of final registers that are zero
		int maxregister = cpuregs.length - 1;
		while (maxregister > 0 && cpuregs[maxregister] == 0)
			maxregister--;
		
		// Print CPU registers
		__ps.print("  - CPU+g:[");
		int didonline = 0;
		for (int i = 0; i <= maxregister; i++)
		{
			// Put local registers on a new line
			if (i == NativeCode.LOCAL_REGISTER_BASE)
			{
				__ps.printf("]%n  - CPU+l:[");
				didonline = 0;
			}
			
			// Start the arguments as well
			else if (i == NativeCode.ARGUMENT_REGISTER_BASE)
			{
				__ps.printf("]%n  - CPU+a:[");
				didonline = 0;
			}
			
			// Other formatting
			else if (i > 0)
			{
				// Comma these
				if (didonline < 6)
					__ps.print(", ");
					
				// Reset count
				else
				{
					__ps.printf("%n  -   ...:.");
					didonline = 0;
				}
			}
			
			// Known register name?
			String knownreg = null;
			switch (i)
			{
				case NativeCode.ZERO_REGISTER:
					knownreg = "zer";
					break;
				
				case NativeCode.RETURN_REGISTER:
					knownreg = "rv1";
					break;
				
				case NativeCode.RETURN_REGISTER + 1:
					knownreg = "rv2";
					break;
				
				case NativeCode.EXCEPTION_REGISTER:
					knownreg = "exc";
					break;
				
				case NativeCode.STATIC_FIELD_REGISTER:
					knownreg = "sfp";
					break;
				
				case NativeCode.THREAD_REGISTER:
					knownreg = "thr";
					break;
				
				case NativeCode.POOL_REGISTER:
					knownreg = "cpl";
					break;
				
				case NativeCode.NEXT_POOL_REGISTER:
					knownreg = "npl";
					break;
				
				case NativeCode.ARGUMENT_REGISTER_BASE:
					knownreg = "a0t";
					break;
			}
			
			// Show a value
			int val = cpuregs[i];
			
			// Print out
			__ps.printf("%3s=%8xh", (knownreg != null ? knownreg :
			String.format("r%02d", i)), cpuregs[i]);
			
			// Count line up
			didonline++;
		}
		__ps.println("]");
		
		// Print call arguments, if any
		if (callargs != null)
		{
			__ps.print("  - CallA: [");
			for (int i = 0, n = callargs.length; i < n; i++)
			{
				if (i > 0)
					__ps.print(", ");
				
				__ps.printf("%xh (%d/%#x)", callargs[i],
					cpuregs[callargs[i]], cpuregs[callargs[i]]);
			}
			__ps.println("]");
		}
		
		/*
		PrintStream out = System.err;
		
		// Limit class name
		CallTraceElement trace = this.trace(__nf);
		String cname = "" + trace.className();
		int nl;
		if ((nl = cname.length()) > 20)
			cname = cname.substring(nl - 20, nl);
		
		// Print Header (with location info)
		out.printf("***** @%08x %-19.19s/%10.10s | L%-4d/J%-3d %20.20s::%s %n",
			__nf._pc,
			NativeInstruction.mnemonic(__op),
			InstructionMnemonics.toString(trace.byteCodeInstruction()),
			trace.line(),
			trace.byteCodeAddress(),
			cname,
			trace.methodName() + ":" + trace.methodDescriptor());
		
		// Is this an invoke?
		boolean isinvoke = (__op == NativeInstructionType.INVOKE ||
			__op == NativeInstructionType.SYSTEM_CALL);
		
		// Arguments to print, invocations get 1 (pc) + register list
		int naf = (isinvoke ? 1 + __reglist.length:
			__af.length);
		
		// Used to modify some calls
		int encoding = NativeInstruction.encoding(__op);
		
		// Print out arguments to the call
		out.printf("  A:[");
		for (int i = 0, n = naf; i < n; i++)
		{
			int iv = (isinvoke ? (i == 0 ? __args[i] : __reglist[i - 1]) :
				__args[i]);
			
			// Comma
			if (i > 0)
				out.print(", ");
			
			// Can be special?
			boolean canspec = true;
			if (encoding == NativeInstructionType.DEBUG_ENTRY ||
				encoding == NativeInstructionType.DEBUG_POINT ||
				(encoding == NativeInstructionType.IF_ICMP &&
					i == 2) ||
				(encoding == NativeInstructionType.MATH_CONST_INT &&
					i == 1) ||
				(encoding == NativeInstructionType.IFEQ_CONST &&
					i == 1) ||
				(encoding == NativeInstructionType.ATOMIC_INT_INCREMENT &&
					i == 1) ||
				(encoding == NativeInstructionType.
					ATOMIC_INT_DECREMENT_AND_GET && i == 2) ||
				(encoding == NativeInstructionType.MEMORY_OFF_ICONST &&
					i == 2) ||
				(encoding == NativeInstructionType.LOAD_POOL && i == 0))
				canspec = false;
			
			// Is this a special register?
			String spec = null;
			if (canspec)
				switch (iv)
				{
					case NativeCode.ZERO_REGISTER:
						spec = "zero";
						break;
					
					case NativeCode.RETURN_REGISTER:
						spec = "return1";
						break;
					
					case NativeCode.RETURN_REGISTER + 1:
						spec = "return2";
						break;
					
					case NativeCode.EXCEPTION_REGISTER:
						spec = "exception";
						break;
					
					case NativeCode.STATIC_FIELD_REGISTER:
						spec = "sfieldptr";
						break;
					
					case NativeCode.THREAD_REGISTER:
						spec = "thread";
						break;
					
					case NativeCode.POOL_REGISTER:
						spec = "pool";
						break;
					
					case NativeCode.NEXT_POOL_REGISTER:
						spec = "nextpool";
						break;
					
					case NativeCode.ARGUMENT_REGISTER_BASE:
						spec = "a0/this";
						break;
				}
			
			// Print special register
			if (spec != null)
				out.printf("%10.10s", spec);
			else
				out.printf("%10d", iv);
		}
		out.print("] | ");
		
		// And register value
		out.printf("V:[");
		int[] registers = __nf._registers;
		for (int i = 0, n = naf; i < n; i++)
		{
			int iv = (isinvoke ? (i == 0 ? __args[i] : __reglist[i - 1]) :
				__args[i]);
				
			if (i > 0)
				out.print(", ");
			
			// Load register value
			if (iv < 0 || iv >= registers.length)
				out.print("----------");
			else
				out.printf("%+10d", registers[iv]);
		}
		out.println("]");
		*/
	}
	
	/**
	 * Creates an execution slice of the given frame information.
	 *
	 * @param __cte The call trace information.
	 * @param __nf The native frame.
	 * @param __op The operand.
	 * @param __args The arguments.
	 * @param __argslen Arguments available.
	 * @param __reglist The register list for method calls.
	 * @return The execution slice.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/10/27
	 */
	public static final ExecutionSlice of(CallTraceElement __cte,
		CPUFrame __nf, int __op, int[] __args, int __argslen,
		int[] __reglist)
		throws NullPointerException
	{
		if (__nf == null || __args == null)
			throw new NullPointerException("NARG");
		
		return new ExecutionSlice(__cte, __nf.getRegisters(),
			__op,
			__args,
			__argslen,
			__reglist);
	}
}

