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
import dev.shadowtail.classfile.pool.InvokeType;
import java.util.Map;
import net.multiphasicapps.classfile.ByteCode;
import net.multiphasicapps.classfile.ClassName;
import net.multiphasicapps.classfile.ConstantValue;
import net.multiphasicapps.classfile.ConstantValueClass;
import net.multiphasicapps.classfile.FieldDescriptor;
import net.multiphasicapps.classfile.FieldReference;
import net.multiphasicapps.classfile.Instruction;
import net.multiphasicapps.classfile.InstructionIndex;
import net.multiphasicapps.classfile.InstructionJumpTarget;
import net.multiphasicapps.classfile.InstructionJumpTargets;
import net.multiphasicapps.classfile.InvalidClassFormatException;
import net.multiphasicapps.classfile.JavaType;
import net.multiphasicapps.classfile.LookupSwitch;
import net.multiphasicapps.classfile.MethodHandle;
import net.multiphasicapps.classfile.MethodReference;
import net.multiphasicapps.classfile.PrimitiveType;
import net.multiphasicapps.classfile.StackMapTable;
import net.multiphasicapps.classfile.StackMapTableState;

/**
 * This class goes through the byte code for a method and then performs stack
 * and instruction work on it.
 *
 * @since 2019/04/06
 */
public final class ByteCodeProcessor
{
	/** The input byte code to be read. */
	protected final ByteCode bytecode;
	
	/** Handle for byte-code operations. */
	protected final ByteCodeHandler handler;
	
	/** The state of the byte code. */
	protected final ByteCodeState state;
	
	/** Stack map table, needed for local wiping. */
	protected final StackMapTable stackmaptable;
	
	/** Reverse jump table. */
	private final Map<Integer, InstructionJumpTargets> _revjumps;
	
	/** Jump targets for this instruction. */
	private InstructionJumpTargets _ijt;
	
	/** Flase is the preprocessor state, otherwise run the handler. */
	private boolean _dohandling;
	
	/** Is an exception possible. */
	private boolean _canexception;
	
	/**
	 * Initializes the byte code processor.
	 *
	 * @param __bc The target byte code.
	 * @param __h Handler for the byte code operations.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/06
	 */
	public ByteCodeProcessor(ByteCode __bc, ByteCodeHandler __h)
		throws NullPointerException
	{
		if (__bc == null || __h == null)
			throw new NullPointerException("NARG");
		
		this.bytecode = __bc;
		this.handler = __h;
		
		// The state is used to share between the processor and the handler
		ByteCodeState state = __h.state();
		this.state = state;
		
		// Load initial Java stack state from the initial stack map
		JavaStackState s;
		StackMapTable stackmaptable = __bc.stackMapTable();
		this.stackmaptable = stackmaptable;
		state.stack = (s = JavaStackState.of(stackmaptable.get(0),
			__bc.writtenLocals()));
		state.stacks.put(0, s);
		
		// Reverse jump table to detect jump backs
		this._revjumps = __bc.reverseJumpTargets();
		
		// Get details and names of the stuff
		state.classname = __bc.thisType();
		state.methodname = __bc.name();
		state.methodtype = __bc.type();
		
		// Store info
		state.exceptionranges = new ExceptionHandlerRanges(__bc);
	}
	
	/**
	 * Processes the byte code and calls the destination handler.
	 *
	 * @since 2019/04/06
	 */
	public final void process()
	{
		// Run process
		try
		{
			this.__aaaProcess();
		}
		
		// {@squirreljme.error JC17 Failed to process the byte code, this may
		// be due to an invalid class or an internal compiler error. (The
		// last processed instruction)}
		catch (InvalidClassFormatException|IllegalArgumentException|
			 IllegalStateException|IndexOutOfBoundsException e)
		{
			throw new InvalidClassFormatException("JC17 " + this.state, e);
		}
	}
	
	/**
	 * Processes the byte code and calls the destination handler.
	 *
	 * @since 2019/04/06
	 */
	private void __aaaProcess()
	{
		ByteCode bytecode = this.bytecode;
		ByteCodeState state = this.state;
		ByteCodeHandler handler = this.handler;
		StackMapTable stackmaptable = this.stackmaptable;
		Map<Integer, JavaStackState> stacks = state.stacks;
		Map<Integer, StateOperations> stackpoison = state.stackpoison;
		
		// Go through each operation twice, performing pre-processing first
		// to make things a bit simpler and more well known when it comes
		// to caching.
		for (int pp = 0; pp < 2; pp++)
		{
			// Is handling to be done?
			boolean dohandling = (pp != 0);
			this._dohandling = dohandling;
			
			// Since this is the start, the last address needs to be reset
			// because it will be invalid!
			state.lastaddr = -1;
			state.followaddr = -1;
			
			// Process instruction
			Instruction lastinst = null;
			SimplifiedJavaInstruction lastsji = null;
			for (Instruction inst : bytecode)
			{
				// Translate to simple instruction for easier handling
				SimplifiedJavaInstruction sji =
					new SimplifiedJavaInstruction(inst);
				
				// Debug
				if (__Debug__.ENABLED)
					Debugging.debugNote("%s %s (%s)", (dohandling ? "Handling" :
						"Preprocessing"), sji, inst);
				
				// Current instruction info
				state.instruction = inst;
				state.simplified = sji;
				
				// Store the last processed address
				state.lastaddr = state.addr;
				
				// Current processing this address
				int addr = inst.address();
				state.addr = addr;
				
				// Set line where this code was found
				state.line = bytecode.lineOfAddress(addr);
				
				// Following address, may be used to calculate if the stack
				// needs to be transitioned
				state.followaddr = bytecode.addressFollowing(addr);
				
				// These jump targets are used to map out the state of stacks
				// across various points
				InstructionJumpTargets ijt, rijt;
				this._ijt = (ijt = inst.jumpTargets());
				state.jumptargets = ijt;
				
				// Reverse jump targets are used to detect jumps to previous
				// addresses
				rijt = this._revjumps.get(addr);
				state.reversejumptargets = (rijt != null ? rijt :
					new InstructionJumpTargets());
				
				// Get the stack, which must exist
				JavaStackState stack = stacks.get(addr);
				if (stack == null)
				{
					// Some code generated by the older compilers ends up
					// defining parts of loops or exception handlers which
					// are defined but completely skipped via a goto, but they
					// are then taken back to this point. They should hopefully
					// have a stack map state defined for them, so just
					// initialize a blank state from that instead.
					StackMapTableState smts = stackmaptable.get(addr);
					if (smts != null)
						stack = JavaStackState.of(smts,
							this.bytecode.writtenLocals());
					
					// {@squirreljme.error JC18 No recorded stack state for
					// this position. (The address to check)}
					if (stack == null)
						throw new InvalidClassFormatException("JC18 " + addr);
				}
				
				// Load stack
				state.stack = stack;
				
				// Reset exception possibility, this is used to determine
				// if the stack update should actually accept exception
				// targets even if they are specified
				this._canexception = false;
				state.canexception = false;
				
				// Preprocessing operations
				if (!dohandling)
				{
					// If there is a stack map table, adjust types that are
					// used on the stack along with the removal of locals
					// and such
					StackMapTableState smts = stackmaptable.get(addr);
					if (smts != null)
					{
						// Debug
						if (__Debug__.ENABLED)
							Debugging.debugNote("SMT BEF: %s", stack);
						
						stack = stack.filterByStackMap(smts);
						
						// Debug
						if (__Debug__.ENABLED)
							Debugging.debugNote("SMT AFT: %s", stack);
					}
					
					// If we are jumping back to this instruction at any point
					// we need to flush the stack so that nothing is cached on
					// it. The resulting flushed stack is then used instead
					// Note that if we jump to ourselves we might have entered
					// with something cached and might end up using that when
					// we do not want to
					if (state.reversejumptargets.hasSameOrLaterAddress(addr))
					{
						// Perform a flush of the cache
						JavaStackResult fres = stack.doCacheFlush();
						
						// Set natural flow as poisoned, operations have to
						// be done to match the correct state
						StateOperations sops = fres.operations();
						if (!sops.isEmpty())
							stackpoison.put(addr, sops);
						
						// Use the result of the flush as the state instead so
						// that it propagates ahead from now on
						stack = fres.after();
					}
					
					// Was the stack changed?
					if (state.stack != stack)
						state.stack = stack;
				}
				
				// Handle instruction
				else
				{
					// Call pre-handler
					handler.instructionSetup();
					
					// If the stack has been adjusted for any reason, replace
					// the stored stack for this point
					JavaStackState mns = state.stack;
					if (!stack.equals(mns))
						stacks.put(addr, mns);
				}
				
				// Handle the operation
				switch (sji.operation())
				{
						// Object array load
					case InstructionIndex.AALOAD:
						this.__doArrayLoad(null);
						break;
						
						// Object array store
					case InstructionIndex.AASTORE:
						this.__doArrayStore(null);
						break;
						
						// Null constant
					case InstructionIndex.ACONST_NULL:
						this.__doAConstNull();
						break;
						
						// Allocate new array
					case InstructionIndex.ANEWARRAY:
						this.__doNewArray(sji.<ClassName>argument(0,
							ClassName.class));
						break;
						
						// Length of array
					case InstructionIndex.ARRAYLENGTH:
						this.__doArrayLength();
						break;
						
						// Throw exception
					case InstructionIndex.ATHROW:
						this.__doThrow();
						break;
						
						// Check that object is of a type, or fail
					case InstructionIndex.CHECKCAST:
						this.__doCheckCast(sji.<ClassName>argument(0,
							ClassName.class));
						break;
						
						// Convert data
					case SimplifiedJavaInstruction.CONVERT:
						this.__doConvert(sji.<StackJavaType>argument(0,
								StackJavaType.class),
							sji.<StackJavaType>argument(1,
								StackJavaType.class));
						break;
						
						// Get field
					case InstructionIndex.GETFIELD:
						this.__doFieldGet(sji.<FieldReference>argument(0,
							FieldReference.class));
						break;
						
						// Get static
					case InstructionIndex.GETSTATIC:
						this.__doStaticGet(sji.<FieldReference>argument(0,
							FieldReference.class));
						break;
						
						// Goto
					case InstructionIndex.GOTO:
						this.__doGoto(sji.<InstructionJumpTarget>argument(0,
							InstructionJumpTarget.class));
						break;
						
						// If comparison against zero
					case SimplifiedJavaInstruction.IF:
						this.__doIf(sji.<DataType>argument(0, DataType.class),
							sji.<CompareType>argument(1, CompareType.class),
							sji.<InstructionJumpTarget>argument(2,
								InstructionJumpTarget.class));
						break;
						
						// Compare two values
					case SimplifiedJavaInstruction.IF_CMP:
						this.__doIfCmp(
							sji.<DataType>argument(0, DataType.class),
							sji.<CompareType>argument(1, CompareType.class),
							sji.<InstructionJumpTarget>argument(2,
								InstructionJumpTarget.class));
						break;
						
						// Increment local
					case InstructionIndex.IINC:
						this.__doIInc(sji.intArgument(0), sji.intArgument(1));
						break;
						
						// Invoke interface
					case InstructionIndex.INVOKEINTERFACE:
						this.__doInvoke(InvokeType.INTERFACE,
							sji.<MethodReference>argument(0,
								MethodReference.class));
						break;
					
						// Invoke special
					case InstructionIndex.INVOKESPECIAL:
						this.__doInvoke(InvokeType.SPECIAL,
							sji.<MethodReference>argument(0,
								MethodReference.class));
						break;
					
						// Invoke static
					case InstructionIndex.INVOKESTATIC:
						this.__doInvoke(InvokeType.STATIC,
							sji.<MethodReference>argument(0,
								MethodReference.class));
						break;
						
						// Invoke virtual
					case InstructionIndex.INVOKEVIRTUAL:
						this.__doInvoke(InvokeType.VIRTUAL,
							sji.<MethodReference>argument(0,
								MethodReference.class));
						break;
						
						// Checks that the given class is an instance of an
						// object.
					case InstructionIndex.INSTANCEOF:
						this.__doInstanceOf(sji.<ClassName>argument(0,
							ClassName.class));
						break;
					
						// Load constant
					case InstructionIndex.LDC:
						this.__doLdc(sji.<ConstantValue>argument(0,
							ConstantValue.class));
						break;
					
						// Load local variable to the stack
					case SimplifiedJavaInstruction.LOAD:
						this.__doLoad(sji.<DataType>argument(0,
							DataType.class), sji.intArgument(1));
						break;
						
						// Lookup switch
					case InstructionIndex.LOOKUPSWITCH:
						this.__doLookupSwitch(sji.<LookupSwitch>argument(0,
							LookupSwitch.class));
						break;
						
						// Math
					case SimplifiedJavaInstruction.MATH:
						this.__doMath(sji.<DataType>argument(0,
							DataType.class), sji.<MathType>argument(1,
								MathType.class));
						break;
						
						// Math with constant
					case SimplifiedJavaInstruction.MATH_CONST:
						this.__doMathConst(sji.<DataType>argument(0,
							DataType.class), sji.<MathType>argument(1,
								MathType.class), sji.<Number>argument(2,
									Number.class));
						break;
						
						// Enter monitor
					case InstructionIndex.MONITORENTER:
						this.__doMonitor(true);
						break;
						
						// Exit monitor
					case InstructionIndex.MONITOREXIT:
						this.__doMonitor(false);
						break;
					
						// Multiple new array
					case InstructionIndex.MULTIANEWARRAY:
						this.__doMultiANewArray(
							sji.<ClassName>argument(0, ClassName.class),
							sji.intArgument(1));
						break;
					
						// Create new instance of something
					case InstructionIndex.NEW:
						this.__doNew(sji.<ClassName>argument(0,
							ClassName.class));
						break;
						
						// This literally does nothing so no output code needs to
						// be generated at all
					case InstructionIndex.NOP:
						this.__doNop();
						break;
						
						// Primitive array load
					case SimplifiedJavaInstruction.PALOAD:
						this.__doArrayLoad(sji.<PrimitiveType>argument(0,
							PrimitiveType.class));
						break;
						
						// Primitive array store
					case SimplifiedJavaInstruction.PASTORE:
						this.__doArrayStore(sji.<PrimitiveType>argument(0,
							PrimitiveType.class));
						break;
					
						// Put of instance field
					case InstructionIndex.PUTFIELD:
						this.__doFieldPut(sji.<FieldReference>argument(0,
							FieldReference.class));
						break;
						
						// Put static field
					case InstructionIndex.PUTSTATIC:
						this.__doStaticPut(sji.<FieldReference>argument(0,
							FieldReference.class));
						break;
					
						// Return from method, with no return value
					case InstructionIndex.RETURN:
						this.__doReturn(null);
						break;
					
						// Stack shuffle
					case SimplifiedJavaInstruction.STACKSHUFFLE:
						this.__doStackShuffle(
							sji.<JavaStackShuffleType>argument(0,
								JavaStackShuffleType.class));
						break;
					
						// Place stack variable into local
					case SimplifiedJavaInstruction.STORE:
						this.__doStore(sji.<DataType>argument(0,
							DataType.class), sji.intArgument(1));
						break;
						
						// Return value
					case SimplifiedJavaInstruction.VRETURN:
						this.__doReturn(sji.<DataType>argument(0,
							DataType.class).toJavaType());
						break;
					
						// Not yet implemented
					default:
						throw Debugging.oops(
							sji.toString() + "/" + inst.toString());
				}
				
				// Call post-handler
				if (dohandling)
					handler.instructionFinish();
				
				// Set last
			}
		}
	}
	
	/**
	 * Pushes a null constant to the stack.
	 *
	 * @since 2019/04/16
	 */
	private void __doAConstNull()
	{
		// -> [ref]
		JavaStackResult result = this.state.stack.doStack(0, JavaType.OBJECT);
		this.__update(result);
		
		// Stop pre-processing here
		if (!this._dohandling)
			return;
		
		// Handle
		this.handler.doCopy(JavaStackResult.INPUT_ZERO, result.out(0));
	}
	
	/**
	 * Gets length of array.
	 *
	 * @since 2019/04/06
	 */
	private void __doArrayLength()
	{
		// An exception may be thrown
		this._canexception = true;
		
		// [array] -> [len]
		JavaStackResult result = this.state.stack.doStack(1, JavaType.INTEGER);
		this.__update(result);
		
		// Stop pre-processing here
		if (!this._dohandling)
			return;
		
		// Handle
		this.handler.doArrayLength(result.in(0), result.out(0));
	}
	
	/**
	 * Loads value from value.
	 *
	 * @param __pt The type to load, {@code null} is considered to be an
	 * object.
	 * @since 2019/04/06
	 */
	private void __doArrayLoad(PrimitiveType __pt)
	{
		// An exception may be thrown
		this._canexception = true;
		
		// This is easily determined from the primitive type
		JavaType faketype;
		if (__pt != null)
			faketype = __pt.stackJavaType();
		
		// Otherwise, pop twice and see what the array is and work from
		// that
		else
		{
			// Pop two
			JavaStackResult wouldbe = this.state.stack.doStack(2);
			
			// Get the base type
			ClassName maybecl = wouldbe.in(0).type.type().className();
			
			// If this is an array then get the component type and work
			// from it, otherwise just assume it is Object since we could
			// not derive that info at all
			faketype = (maybecl.isArray() ?
				new JavaType(maybecl.componentType()) : JavaType.OBJECT);
		}
		
		// [array, index] -> [value]
		JavaStackResult result = this.state.stack.doStack(2, faketype);
		this.__update(result);
		
		// Stop pre-processing here
		if (!this._dohandling)
			return;
		
		// Handle
		this.handler.doArrayLoad(DataType.of(__pt), result.in(0),
			result.in(1), result.out(0));
	}
	
	/**
	 * Stores value into an array.
	 *
	 * @param __pt The type to store, {@code null} is considered to be an
	 * object.
	 * @since 2019/04/06
	 */
	private void __doArrayStore(PrimitiveType __pt)
	{
		// An exception may be thrown
		this._canexception = true;
		
		// [array, index, value]
		JavaStackResult result = this.state.stack.doStack(3);
		this.__update(result);
		
		// Stop pre-processing here
		if (!this._dohandling)
			return;
		
		// Handle
		this.handler.doArrayStore(DataType.of(__pt), result.in(0),
			result.in(1), result.in(2));
	}
	
	/**
	 * Checks that the object on the stack is of the given type.
	 *
	 * @param __cn The name of the class to check.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/06
	 */
	private void __doCheckCast(ClassName __cn)
		throws NullPointerException
	{
		// An exception may be thrown
		this._canexception = true;
		
		// [object] -> [object]
		JavaStackResult result = this.state.stack.doCheckCast(
			new JavaType(__cn));
		this.__update(result);
		
		// Stop pre-processing here
		if (!this._dohandling)
			return;
		
		// Do check cast
		this.handler.doCheckCast(__cn, result.in(0));
	}
	
	/**
	 * Converts from one Java type to another
	 *
	 * @param __from The source type.
	 * @param __to The destination type.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/16
	 */
	private void __doConvert(StackJavaType __from, StackJavaType __to)
		throws NullPointerException
	{
		if (__from == null || __to == null)
			throw new NullPointerException("NARG");
		
		// [from] -> [to]
		JavaStackResult result = this.state.stack.doStack(1, __to.toJavaType());
		this.__update(result);
		
		// Stop pre-processing here
		if (!this._dohandling)
			return;
		
		// Forward
		this.handler.doConvert(__from, result.in(0), __to, result.out(0));
	}
	
	/**
	 * Reads a value from a field.
	 *
	 * @param __fr The field reference.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/06
	 */
	private void __doFieldGet(FieldReference __fr)
		throws NullPointerException
	{
		if (__fr == null)
			throw new NullPointerException("NARG");
		
		// An exception may be thrown
		this._canexception = true;
		
		// [inst] -> [value]
		ByteCodeState state = this.state;
		JavaStackResult result = state.stack.doStack(1,
			new JavaType(__fr.memberType()));
		this.__update(result);
		
		// Stop pre-processing here
		if (!this._dohandling)
			return;
		
		// Forward
		this.handler.doFieldGet(__fr, result.in(0), result.out(0));
	}
	
	/**
	 * Puts a value into a field.
	 *
	 * @param __fr The field reference.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/04
	 */
	private void __doFieldPut(FieldReference __fr)
		throws NullPointerException
	{
		if (__fr == null)
			throw new NullPointerException("NARG");
		
		// An exception may be thrown
		this._canexception = true;
		
		// [inst, value] ->
		ByteCodeState state = this.state;
		JavaStackResult result = state.stack.doStack(2);
		this.__update(result);
		
		// Stop pre-processing here
		if (!this._dohandling)
			return;
		
		// Forward
		this.handler.doFieldPut(__fr, result.in(0), result.in(1));
	}
	
	/**
	 * Goes to another address.
	 *
	 * @param __jt The target.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/06
	 */
	private void __doGoto(InstructionJumpTarget __jt)
		throws NullPointerException
	{
		if (__jt == null)
			throw new NullPointerException("NARG");
		
		// Do nothing!
		this.__update(this.state.stack.doNothing());
		
		// Stop pre-processing here
		if (!this._dohandling)
			return;
		
		// Just do an always true comparison on the zero register
		this.handler.doIfICmp(CompareType.TRUE, JavaStackResult.INPUT_ZERO,
			JavaStackResult.INPUT_ZERO, __jt);
	}
	
	/**
	 * Performs if comparison against zero.
	 *
	 * @param __type The type to work with on the stack.
	 * @param __ct The comparison type.
	 * @param __ijt The instruction jump target.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/05
	 */
	private void __doIf(DataType __type, CompareType __ct,
		InstructionJumpTarget __ijt)
		throws NullPointerException
	{
		if (__type == null || __ct == null || __ijt == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error JC19 Cannot compare float or double.}
		if (__type == DataType.FLOAT || __type == DataType.DOUBLE)
			throw new IllegalArgumentException("JC19");
		
		// [val] ->
		JavaStackResult result = this.state.stack.doStack(1);
		this.__update(result);
		
		// Stop pre-processing here
		if (!this._dohandling)
			return;
		
		// Forward
		this.handler.doIfICmp(__ct, result.in(0), JavaStackResult.INPUT_ZERO,
			__ijt);
	}
	
	/**
	 * Performs if comparison of two values against each other.
	 *
	 * @param __type The type to work with on the stack.
	 * @param __ct The comparison type.
	 * @param __ijt The instruction jump target.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/06
	 */
	private void __doIfCmp(DataType __type, CompareType __ct,
		InstructionJumpTarget __ijt)
		throws NullPointerException
	{
		if (__type == null || __ct == null || __ijt == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error JC1a Cannot compare float or double.}
		if (__type == DataType.FLOAT || __type == DataType.DOUBLE)
			throw new IllegalArgumentException("JC1a");
		
		// [a, b] ->
		JavaStackResult result = this.state.stack.doStack(2);
		this.__update(result);
		
		// Stop pre-processing here
		if (!this._dohandling)
			return;
		
		// Forward
		this.handler.doIfICmp(__ct, result.in(0), result.in(1), __ijt);
	}
	
	/**
	 * Increments local variable.
	 *
	 * @param __l The local to increment.
	 * @param __v The value to increment by.
	 * @since 2019/04/06
	 */
	private void __doIInc(int __l, int __v)
	{
		// Just write an integer to the integer so its state is known
		JavaStackResult result = this.state.stack.
			doLocalSet(JavaType.INTEGER, __l);
		this.__update(result);
		
		// Stop pre-processing here
		if (!this._dohandling)
			return;
			
		// There might be items on the stack which were cached and now no
		// longer are because they got pulverized, so de-cache those
		ByteCodeHandler handler = this.handler;
		handler.doStateOperations(result.operations());
		
		// Add value
		handler.doMath(StackJavaType.INTEGER, MathType.ADD,
			result.out(0).asInput(), __v,
			result.out(0));
	}
	
	/**
	 * Checks that the class is the given instance.
	 *
	 * @param __cl The class to check.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/16
	 */
	private void __doInstanceOf(ClassName __cl)
		throws NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException("NARG");
		
		// [object] -> [int]
		JavaStackResult result = this.state.stack.doStack(1, JavaType.INTEGER);
		this.__update(result);
		
		// Stop pre-processing here
		if (!this._dohandling)
			return;
		
		// Handle
		this.handler.doInstanceOf(__cl, result.in(0), result.out(0));
	}
	
	/**
	 * Handles invocation of other methods.
	 *
	 * @param __t The type of invocation to perform.
	 * @param __r The method to invoke.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/03
	 */
	private void __doInvoke(InvokeType __t, MethodReference __r)
		throws NullPointerException
	{
		if (__t == null || __r == null)
			throw new NullPointerException("NARG");
		
		// An exception may be thrown
		this._canexception = true;
		
		// Return value type, if any
		MethodHandle mf = __r.handle();
		FieldDescriptor rv = mf.descriptor().returnValue();
		boolean hasrv = (rv != null);
		
		// The number of arguments to pop is the instance (if non-static) and
		// the number of arguments taken
		int popcount = (__t.hasInstance() ? 1 : 0) +
			mf.descriptor().argumentCount();
		
		// Perform stack operation
		ByteCodeState state = this.state;
		JavaStackResult result = (!hasrv ? state.stack.doStack(popcount) :
			state.stack.doStack(popcount, new JavaType(rv)));
		this.__update(result);
		
		// Stop pre-processing here
		if (!this._dohandling)
			return;
		
		// Forward
		this.handler.doInvoke(__t, __r, (!hasrv ? null :
			result.out(0)), result.in());
	}
	
	/**
	 * Loads constant value onto the stack.
	 *
	 * @param __v The value to push.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/03
	 */
	private void __doLdc(ConstantValue __v)
		throws NullPointerException
	{
		if (__v == null)
			throw new NullPointerException("NARG");
		
		// Get push properties
		JavaType jt = __v.type().javaType();
		
		// An exception is only throwable on classes, because the class
		// could potentially fail to initialize properly
		this._canexception = (__v instanceof ConstantValueClass);
		
		// Push to the stack this type, the result is always cached
		JavaStackResult result = this.state.stack.doStack(0,
			true, jt);
		this.__update(result);
		
		// Do not call generator, we just want the stack result
		if (!this._dohandling)
			return;
		
		// Call the appropriate handler
		ByteCodeHandler handler = this.handler;
		switch (__v.type())
		{
			case INTEGER:
				handler.doMath(StackJavaType.INTEGER, MathType.OR,
					JavaStackResult.INPUT_ZERO, (Integer)__v.boxedValue(),
					result.out(0));
				break;
				
			case FLOAT:
				handler.doMath(StackJavaType.FLOAT, MathType.OR,
					JavaStackResult.INPUT_ZERO, (Float)__v.boxedValue(),
					result.out(0));
				break;
			
			case LONG:
				handler.doMath(StackJavaType.LONG, MathType.OR,
					JavaStackResult.INPUT_ZERO, (Long)__v.boxedValue(),
					result.out(0));
				break;
				
			case DOUBLE:
				handler.doMath(StackJavaType.DOUBLE, MathType.OR,
					JavaStackResult.INPUT_ZERO, (Double)__v.boxedValue(),
					result.out(0));
				break;
			
			case STRING:
				handler.doPoolLoadString(__v.boxedValue().toString(),
					result.out(0));
				break;
				
			case CLASS:
				handler.doClassObjectLoad((ClassName)__v.boxedValue(),
					result.out(0));
				break;
			
			default:
				throw Debugging.oops();
		}
	}
	
	/**
	 * Loads from a local and puts to the stack.
	 *
	 * @param __jt The type to push.
	 * @param __from The source local.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/03
	 */
	private void __doLoad(DataType __jt, int __from)
		throws NullPointerException
	{
		if (__jt == null)
			throw new NullPointerException("NARG");
		
		// Load from local variable
		JavaStackResult result = this.state.stack.doLocalLoad(__from);
		this.__update(result);
		
		// Stop pre-processing here
		if (!this._dohandling)
			return;
		
		// Only perform the copy if the value is different, because otherwise
		// it would have just been cached
		if (result.in(0).register != result.out(0).register)
			this.handler.doCopy(result.in(0), result.out(0));
	}
	
	/**
	 * Handles lookup switch.
	 *
	 * @param __ls The switch.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/16
	 */
	private void __doLookupSwitch(LookupSwitch __ls)
		throws NullPointerException
	{
		if (__ls == null)
			throw new NullPointerException("NARG");
		
		// [key] ->
		JavaStackResult result = this.state.stack.doStack(1);
		this.__update(result);
		
		// Stop pre-processing here
		if (!this._dohandling)
			return;
		
		// Handle
		this.handler.doLookupSwitch(result.in(0), __ls);
	}
	
	/**
	 * Performs math operation.
	 *
	 * @param __pt The primitive type.
	 * @param __mot The math operation type.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/06
	 */
	private void __doMath(DataType __pt, MathType __mot)
		throws NullPointerException
	{
		if (__pt == null || __mot == null)
			throw new NullPointerException("NARG");
		
		// We may throw an exception here if we divide by zero!
		if (__pt == DataType.INTEGER || __pt == DataType.LONG)
			if (__mot == MathType.DIV || __mot == MathType.REM)
				this._canexception = true;
		
		// [a, b] -> [result]
		JavaStackResult result = this.state.stack.doStack(2,
			__pt.toJavaType());
		this.__update(result);
		
		// Stop pre-processing here
		if (!this._dohandling)
			return;
		
		// Perform the math
		this.handler.doMath(__pt.toStackJavaType(), __mot, result.in(0),
			result.in(1), result.out(0));
	}
	
	/**
	 * Performs math operation with constant.
	 *
	 * @param __pt The primitive type.
	 * @param __mot The math operation type.
	 * @param __c The constant.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/06
	 */
	private void __doMathConst(DataType __pt, MathType __mot, Number __c)
		throws NullPointerException
	{
		if (__pt == null || __mot == null || __c == null)
			throw new NullPointerException("NARG");
		
		// [a, b] -> [result]
		JavaStackResult result = this.state.stack.doStack(1,
			__pt.toJavaType());
		this.__update(result);
		
		// Stop pre-processing here
		if (!this._dohandling)
			return;
		
		// Monitor operation
		this.handler.doMath(__pt.toStackJavaType(), __mot, result.in(0),
			__c, result.out(0));
	}
	
	/**
	 * Enters or exits the monitor.
	 *
	 * @param __enter If the monitor is to be entered.
	 * @since 2019/04/16
	 */
	private void __doMonitor(boolean __enter)
	{
		// Can toss exception
		this._canexception = true;
		
		// [object] ->
		JavaStackResult result = this.state.stack.doStack(1);
		this.__update(result);
		
		// Stop pre-processing here
		if (!this._dohandling)
			return;
		
		// Monitor operation
		this.handler.doMonitor(__enter, result.in(0));
	}
	
	/**
	 * Allocate multi-dimensional array.
	 *
	 * @param __cl The class to allocate.
	 * @param __dims The number of dimensions to allocate.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/05/04
	 */
	private void __doMultiANewArray(ClassName __cl, int __dims)
		throws NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException("NARG");
			
		// Can toss exception
		this._canexception = true;
		
		// [__dims, ...] -> [object]
		JavaStackResult result = this.state.stack.doStack(__dims,
			new JavaType(__cl));
		this.__update(result);
		
		// Stop pre-processing here
		if (!this._dohandling)
			return;
		
		// Handle array creation
		this.handler.doMultiANewArray(__cl, __dims, result.out(0),
			result.in());
	}
	
	/**
	 * Creates a new instance of the given class.
	 *
	 * @param __cn The class to create.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/04
	 */
	private void __doNew(ClassName __cn)
		throws NullPointerException
	{
		if (__cn == null)
			throw new NullPointerException("NARG");
		
		// An exception may be thrown
		this._canexception = true;
		
		// Just the type is pushed to the stack
		JavaStackResult result = this.state.stack.
			doStack(0, new JavaType(__cn));
		this.__update(result);
		
		// Stop pre-processing here
		if (!this._dohandling)
			return;
		
		// Forward
		this.handler.doNew(__cn, result.out(0));
	}
	
	/**
	 * Allocates a new array.
	 *
	 * @param __cn The component type of the array.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/05
	 */
	private void __doNewArray(ClassName __cn)
		throws NullPointerException
	{
		if (__cn == null)
			throw new NullPointerException("NARG");
		
		// An exception may be thrown
		this._canexception = true;
		
		// Add dimension to the class since it lacks it
		__cn = __cn.addDimensions(1);
		
		// [len] -> [array]
		JavaStackResult result = this.state.stack.
			doStack(1, new JavaType(__cn));
		this.__update(result);
		
		// Stop pre-processing here
		if (!this._dohandling)
			return;
		
		// Generate
		this.handler.doNewArray(__cn, result.in(0), result.out(0));
	}
	
	/**
	 * Do nothing.
	 *
	 * @since 2019/04/07
	 */
	private void __doNop()
	{
		// Just do nothing
		this.__update(this.state.stack.doNothing());
	}
	
	/**
	 * Handles returning.
	 *
	 * @param __rt The type to return, {@code null} means nothing is to be
	 * returned.
	 * @since 2019/04/03
	 */
	private void __doReturn(JavaType __rt)
	{
		ByteCodeState state = this.state;
		
		// Destroy everything and bring destruction to the method!!!
		JavaStackResult result = state.stack.doDestroy(__rt != null);
		this.__update(result);
		
		// Stop pre-processing here
		if (!this._dohandling)
			return;
		
		// Call handler
		this.handler.doReturn((__rt != null ? result.in(0) : null));
	}
	
	/**
	 * Performs shuffling of the stack.
	 *
	 * @param __st The type of shuffle to do.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/04
	 */
	private void __doStackShuffle(JavaStackShuffleType __st)
		throws NullPointerException
	{
		if (__st == null)
			throw new NullPointerException("NARG");
		
		// Shuffle the stack
		JavaStackResult result = this.state.stack.doStackShuffle(__st);
		this.__update(result);
		
		// Stop pre-processing here
		if (!this._dohandling)
			return;
		
		// Potentially run state operations as needed
		StateOperations ops = result.operations();
		if (!ops.isEmpty())
			this.handler.doStateOperations(ops);
	}
	
	/**
	 * Reads a value from a static field.
	 *
	 * @param __fr The field reference.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/06
	 */
	private void __doStaticGet(FieldReference __fr)
		throws NullPointerException
	{
		if (__fr == null)
			throw new NullPointerException("NARG");
		
		// An exception may be thrown
		this._canexception = true;
		
		// [] -> [value]
		ByteCodeState state = this.state;
		JavaStackResult result = state.stack.doStack(0,
			new JavaType(__fr.memberType()));
		this.__update(result);
		
		// Stop pre-processing here
		if (!this._dohandling)
			return;
		
		// Forward
		this.handler.doStaticGet(__fr, result.out(0));
	}
	
	/**
	 * Writes to static field.
	 *
	 * @param __fr The field reference.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/13
	 */
	private void __doStaticPut(FieldReference __fr)
		throws NullPointerException
	{
		if (__fr == null)
			throw new NullPointerException("NARG");
		
		// An exception may be thrown
		this._canexception = true;
		
		// [value] ->
		ByteCodeState state = this.state;
		JavaStackResult result = state.stack.doStack(1);
		this.__update(result);
		
		// Stop pre-processing here
		if (!this._dohandling)
			return;
		
		// Forward
		this.handler.doStaticPut(__fr, result.in(0));
	}
	
	/**
	 * Stores an entry on the stack.
	 *
	 * @param __jt The type to pop.
	 * @param __to The destination local.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/06
	 */
	private void __doStore(DataType __jt, int __to)
		throws NullPointerException
	{
		if (__jt == null)
			throw new NullPointerException("NARG");
		
		// Store onto the stack, locals are never cached
		JavaStackResult result = this.state.stack.doLocalStore(__to);
		this.__update(result);
		
		// Stop pre-processing here
		if (!this._dohandling)
			return;
		
		// There might be items on the stack which were cached and now no
		// longer are because they got pulverized, so de-cache those
		ByteCodeHandler handler = this.handler;
		handler.doStateOperations(result.operations());
		
		// Perform plain copy
		handler.doCopy(result.in(0), result.out(0));
	}
	
	/**
	 * Performs a throw of an exception on the stack.
	 *
	 * @since 2019/04/05
	 */
	private void __doThrow()
	{
		// An exception will be thrown
		this._canexception = true;
		
		// Pop item from the stack
		JavaStackResult result = this.state.stack.doStack(1);
		this.__update(result);
		
		// Stop pre-processing here
		if (!this._dohandling)
			return;
		
		// Handle
		this.handler.doThrow(result.in(0));
	}
	
	/**
	 * Updates the stack state and result.
	 *
	 * @param __jsr The stack result.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/07
	 */
	private void __update(JavaStackResult __jsr)
		throws NullPointerException
	{
		if (__jsr == null)
			throw new NullPointerException("NARG");
		
		// The new stack state
		JavaStackState newstack = __jsr.after();
		
		// Needed for processing
		ByteCodeState state = this.state;
		Map<Integer, JavaStackState> stacks = state.stacks;
		int addr = state.addr;
		
		// Store result and the new stack
		state.result = __jsr;
		state.stack = newstack;
		
		// Can an exception handler be called?
		boolean canexception = this._canexception;
		state.canexception = canexception;
		
		// Target stack states are not touched in the normal handling state
		// because collisions and transitioning of states is handled in the
		// pre-processing step
		if (this._dohandling)
			return;
		
		// The result of the jump calculations may result in the stack
		// being poisoned potentially
		Map<Integer, StateOperations> stackpoison = state.stackpoison;
		Map<Integer, JavaStackEnqueueList> stackcollides = state.stackcollides;
		
		// Set target stack states for destinations of this instruction
		// Calculate the exception state only if it is needed
		JavaStackState hypoex = null;
		InstructionJumpTargets ijt = this._ijt;
		if (ijt != null && !ijt.isEmpty())
			for (int i = 0, n = ijt.size(); i < n; i++)
			{
				int jta = ijt.get(i).target();
				
				// If an exception is never thrown by the instruction being
				// processed then just ignore any exception points which may
				// be defined since they will have no effect
				boolean isexception = ijt.isException(i);
				if (!canexception && isexception)
					continue;
				
				// Lazily calculate the exception handler since it might
				// not always be needed
				if (isexception && hypoex == null)
					hypoex = newstack.doExceptionHandler(new JavaType(
						new ClassName("java/lang/Throwable"))).after();
				
				// The type of stack to target
				JavaStackState use = (isexception ? hypoex : newstack);
				
				// Is empty state, use this state because we defined it first
				JavaStackState dss = stacks.get(jta);
				if (dss == null)
					stacks.put(jta, use);
				
				// For later addresses which do not have an exact stack state
				// match, a partial un-cache will have to be used.
				// Note that jump backs are ignored here since those were
				// processed and we cannot adjust the states anymore
				else if (jta > addr && !use.canTransition(dss))
				{
					// Debug
					if (__Debug__.ENABLED)
					{
						Debugging.debugNote("Transition is required! %d -> %d",
							addr, jta);
						Debugging.debugNote("From: %s", use);
						Debugging.debugNote("To  : %s", dss);
					}
					
					// Get pre-existing collision state here, if any
					JavaStackEnqueueList preq = stackcollides.get(jta);
					if (preq == null)
						preq = new JavaStackEnqueueList(0);
					
					// Merge these two register lists
					JavaStackEnqueueList mcol = JavaStackEnqueueList.merge(
						preq, use.cacheCollision(dss));
					
					// Store the resulting collision
					stacks.put(jta, (dss = dss.cacheClearState(mcol)));
					
					// Debug
					if (__Debug__.ENABLED)
						Debugging.debugNote("Coll: %s", dss);
				}
			}
	}
}

