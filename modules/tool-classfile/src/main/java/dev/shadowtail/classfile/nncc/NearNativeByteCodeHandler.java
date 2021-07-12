// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.nncc;

import cc.squirreljme.jvm.Assembly;
import cc.squirreljme.jvm.ClassLoadingAdjustments;
import cc.squirreljme.jvm.SystemCallIndex;
import cc.squirreljme.jvm.summercoat.constants.ClassProperty;
import cc.squirreljme.jvm.summercoat.constants.StaticVmAttribute;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import dev.shadowtail.classfile.pool.AccessedField;
import dev.shadowtail.classfile.pool.ClassNameHash;
import dev.shadowtail.classfile.pool.FieldAccessTime;
import dev.shadowtail.classfile.pool.FieldAccessType;
import dev.shadowtail.classfile.pool.InvokeType;
import dev.shadowtail.classfile.pool.InvokeXTable;
import dev.shadowtail.classfile.pool.InvokedMethod;
import dev.shadowtail.classfile.pool.NotedString;
import dev.shadowtail.classfile.pool.NullPoolEntry;
import dev.shadowtail.classfile.pool.QuickCastCheck;
import dev.shadowtail.classfile.pool.TypeBracketPointer;
import dev.shadowtail.classfile.pool.UsedString;
import dev.shadowtail.classfile.summercoat.HelperFunction;
import dev.shadowtail.classfile.summercoat.register.ExecutablePointer;
import dev.shadowtail.classfile.summercoat.register.IntValueRegister;
import dev.shadowtail.classfile.summercoat.register.MemHandleRegister;
import dev.shadowtail.classfile.summercoat.register.PlainRegister;
import dev.shadowtail.classfile.summercoat.register.Register;
import dev.shadowtail.classfile.summercoat.register.RuntimePoolPointer;
import dev.shadowtail.classfile.summercoat.register.TypedRegister;
import dev.shadowtail.classfile.summercoat.register.Volatile;
import dev.shadowtail.classfile.summercoat.register.WideRegister;
import dev.shadowtail.classfile.xlate.ByteCodeHandler;
import dev.shadowtail.classfile.xlate.ByteCodeState;
import dev.shadowtail.classfile.xlate.CompareType;
import dev.shadowtail.classfile.xlate.DataType;
import dev.shadowtail.classfile.xlate.ExceptionHandlerTransition;
import dev.shadowtail.classfile.xlate.JavaStackEnqueueList;
import dev.shadowtail.classfile.xlate.JavaStackResult;
import dev.shadowtail.classfile.xlate.JavaStackState;
import dev.shadowtail.classfile.xlate.MathType;
import dev.shadowtail.classfile.xlate.SoftRegister;
import dev.shadowtail.classfile.xlate.StackJavaType;
import dev.shadowtail.classfile.xlate.StateOperation;
import dev.shadowtail.classfile.xlate.StateOperations;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.multiphasicapps.classfile.BinaryName;
import net.multiphasicapps.classfile.ByteCode;
import net.multiphasicapps.classfile.ClassIdentifier;
import net.multiphasicapps.classfile.ClassName;
import net.multiphasicapps.classfile.ExceptionHandler;
import net.multiphasicapps.classfile.ExceptionHandlerTable;
import net.multiphasicapps.classfile.FieldReference;
import net.multiphasicapps.classfile.InstructionJumpTarget;
import net.multiphasicapps.classfile.JavaType;
import net.multiphasicapps.classfile.LookupSwitch;
import net.multiphasicapps.classfile.MethodDescriptor;
import net.multiphasicapps.classfile.MethodName;
import net.multiphasicapps.classfile.MethodReference;
import net.multiphasicapps.collections.AutoCloseableList;

/**
 * This contains the handler for the near native byte code.
 *
 * @since 2019/04/06
 */
public final class NearNativeByteCodeHandler
	implements ByteCodeHandler
{
	/** The package shelves are in, for remapping. */
	public static final BinaryName MLE_SHELF_PACKAGE =
		new BinaryName("cc/squirreljme/jvm/mle");
		
	/** Target packages for shelves to call instead. */
	public static final BinaryName LLE_SHELF_PACKAGE =
		new BinaryName("cc/squirreljme/jvm/summercoat/lle");
	
	/** State of the byte code. */
	public final ByteCodeState state =
		new ByteCodeState();
	
	/** Used to build native code. */
	protected final NativeCodeBuilder codebuilder =
		new NativeCodeBuilder();
	
	/** Default field access type, to determine how fields are accessed. */
	protected final FieldAccessTime defaultfieldaccesstime;
	
	/** Is this method synchronized? */
	protected final boolean issynchronized;
	
	/** Is this an instance method? */
	protected final boolean isinstance;
	
	/** Returning value? */
	protected final boolean isreturn;
	
	/** Returning wide value? */
	protected final boolean isreturnwide;
	
	/** Monitor target register used. */
	protected final int monitortarget;
	
	/** Volatile registers to use. */
	protected final VolatileRegisterStack volatiles;
	
	/** Standard exception handler table. */
	private final Map<ExceptionHandlerTransition, __EData__> _ehtable =
		new LinkedHashMap<>();
	
	/** Queue for exception handling generation. */
	private final Deque<__TransitingExceptionHandler__> _ehTableQueue =
		new LinkedList<>();
	
	/** Made exception table. */
	private final Map<ClassAndLabel, __EData__> _metable =
		new LinkedHashMap<>();
	
	/** Made exception handling queue. */
	private final Deque<__MakeException__> _meTableQueue =
		new LinkedList<>();
	
	/** The returns which have been performed. */
	private final List<JavaStackEnqueueList> _returns =
		new ArrayList<>();
	
	/** Java transition labels. */
	private final Map<StateOperationsAndTarget, __EData__> _transits =
		new LinkedHashMap<>();
	
	/** State operation transits. */
	private final Deque<__StateOpTransit__> _transitQueue =
		new LinkedList<>();
	
	/** Reference clearing and jumping to label. */
	private final Map<EnqueueAndLabel, __EData__> _refcljumps =
		new LinkedHashMap<>();
	
	/** Reference clear jump list. */
	private final Deque<__RefClearJump__> _refClearJumpQueue =
		new LinkedList<>();
	
	/** The source file used for this class. */
	protected final String sourceFile;
	
	/** The method index. */
	protected final int methodIndex;
	
	/** Last registers enqueued. */
	private JavaStackEnqueueList _lastenqueue;
	
	/** Next reference count/uncount ID number for jump. */
	private int _refclunk;
	
	/**
	 * Initializes the byte code handler.
	 *
	 * @param __bc The byte code.
	 * @param __sourceFile The source file used for this method.
	 * @param __methodIndex The method index.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/11
	 */
	public NearNativeByteCodeHandler(ByteCode __bc, String __sourceFile,
		int __methodIndex)
		throws NullPointerException
	{
		if (__bc == null)
			throw new NullPointerException("NARG");
		
		this.defaultfieldaccesstime = ((__bc.isInstanceInitializer() ||
			__bc.isStaticInitializer()) ? FieldAccessTime.INITIALIZER :
			FieldAccessTime.NORMAL);
		this.issynchronized = __bc.isSynchronized();
		this.isinstance = __bc.isInstance();
		this.sourceFile = __sourceFile;
		this.methodIndex = __methodIndex;
		
		// Determine monitor target register and the volatile base
		int volbase = NativeCode.ARGUMENT_REGISTER_BASE + 2 +
			__bc.maxLocals() + __bc.maxStack();
		this.monitortarget = volbase;
		this.volatiles = new VolatileRegisterStack(volbase + 1);
		
		// Returning values?
		this.isreturn = (__bc.type().returnValue() != null);
		this.isreturnwide = (this.isreturn && __bc.type().returnValue().isWide());
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/12
	 */
	@Override
	public final void doArrayLength(JavaStackResult.Input __in,
		JavaStackResult.Output __len)
	{
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// Push references
		this.__refPush();
		
		// The array used
		MemHandleRegister array = MemHandleRegister.of(__in.register);
		
		// Cannot be null
		this.__basicCheckNPE(array);
		
		// Must be an array
		if (!__in.isArray())
			this.__basicCheckIsArray(array);
		
		// Need volatiles to access the field
		VolatileRegisterStack volatiles = this.volatiles;
		try (Volatile<IntValueRegister> off = volatiles.getIntValue())
		{
			// Determine where the array length is located
			codeBuilder.addIntegerConst(
				StaticVmAttribute.OFFSETOF_ARRAY_LENGTH_FIELD, off.register);
			this.__invokeHelper(HelperFunction.STATIC_VM_ATTRIBUTE,
				off.register);
			
			// Read the offset
			codeBuilder.addCopy(IntValueRegister.RETURN, off.register);
			
			// Read the value
			codeBuilder.addMemHandleAccess(DataType.INTEGER, true,
				IntValueRegister.of(__len.register), array, off.register);
		}
		
		// Clear references
		this.__refClear();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/12
	 */
	@Override
	public final void doArrayLoad(DataType __dt,
		JavaStackResult.Input __array, JavaStackResult.Input __dx,
		JavaStackResult.Output __val)
	{
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// The variables used
		MemHandleRegister array = MemHandleRegister.of(__array.register);
		IntValueRegister index = IntValueRegister.of(__dx.register);
		
		// Push references
		this.__refPush();
		
		// Cannot be null
		this.__basicCheckNPE(array);
		
		// Must be an array
		if (!__array.isArray())
			this.__basicCheckIsArray(array);
		
		// Volatiles are needed
		VolatileRegisterStack volatiles = this.volatiles;
		try (Volatile<IntValueRegister> offset = volatiles.getIntValue())
		{
			// Check array bounds, we use the offset calculated from this
			// to access the memory handle
			this.__basicCheckArrayBound(array, index, __dt,
				offset.register);
			
			// Load value
			if (__dt.isWide())
				codeBuilder.addMemHandleAccess(__dt, true,
					WideRegister.of(__val.register, __val.register + 1),
					array, offset.register);
			else
				codeBuilder.addMemHandleAccess(__dt, true,
					IntValueRegister.of(__val.register),
					array, offset.register);
			
			// Any object being read gets reference counted
			boolean isObject = __dt == DataType.OBJECT;
			if (isObject)
				this.__refCount(MemHandleRegister.of(__val.register));
		}
		
		// Clear references
		this.__refClear();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/12
	 */
	@Override
	public final void doArrayStore(DataType __dt,
		JavaStackResult.Input __array, JavaStackResult.Input __dx,
		JavaStackResult.Input __val)
	{
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// The variables used
		MemHandleRegister array = MemHandleRegister.of(__array.register);
		IntValueRegister index = IntValueRegister.of(__dx.register);
		
		// Push references
		this.__refPush();
		
		// Cannot be null
		this.__basicCheckNPE(array);
		
		// Must be an array
		if (!__array.isArray())
			this.__basicCheckIsArray(array);
		
		// Volatiles are needed
		VolatileRegisterStack volatiles = this.volatiles;
		try (Volatile<IntValueRegister> offset = volatiles.getIntValue();
			Volatile<MemHandleRegister> oldObj = volatiles.getMemHandle())
		{
			// Check array bounds, we use the offset calculated from this
			// to access the memory handle
			this.__basicCheckArrayBound(array, index, __dt,
				offset.register);
			
			// Objects need to be reference count checked
			boolean isObject = __dt == DataType.OBJECT;
			if (isObject)
			{
				// Check if the array type is compatible, if we know the
				// types right now we can skip this check
				if (!__array.isArray() || !__val.type.className().equals(
					__array.type.className().componentType()))
					this.__basicCheckArrayStore(__array.type, array,
						__val.type, MemHandleRegister.of(__val.register));
				
				// Read the old value which may be uncounted
				codeBuilder.addMemHandleAccess(__dt, true,
					IntValueRegister.of(oldObj.register.register),
					array, offset.register);
				
				// Count the object being stored
				this.__refCount(MemHandleRegister.of(__val.register));
			}
			
			// Store value
			if (__dt.isWide())
				codeBuilder.addMemHandleAccess(__dt, false,
					WideRegister.of(__val.register, __val.register + 1),
					array, offset.register);
			else
				codeBuilder.addMemHandleAccess(__dt, false,
					IntValueRegister.of(__val.register),
					array, offset.register);
			
			// Uncount the old value so that it gets GCed
			if (isObject)
				this.__refUncount(oldObj.register);
		}
		
		// Clear references
		this.__refClear();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/12
	 */
	@Override
	public final void doCheckCast(ClassName __cl, JavaStackResult.Input __v)
	{
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// Push reference
		this.__refPush();
		
		// The instance to check
		MemHandleRegister instance = MemHandleRegister.of(__v.register);
		
		// If the value to be checked is null then we do not thrown an
		// exception, we just skip
		NativeCodeLabel nullSkip = new NativeCodeLabel("checkCastNull",
			this._refclunk++);
		codeBuilder.addIfNull(instance, nullSkip);
		
		// Add cast check
		this.__basicCheckCCE(instance, __v.type, __cl);
		
		// Null jump goes here
		codeBuilder.label(nullSkip);
		
		// We already checked the only valid exceptions, so do not perform
		// later handling!
		this.state.canexception = false;
		
		// We do not need to uncount whatever was pushed in because it would
		// be immediately pushed back onto the stack. The counts should only
		// be lowered if ClassCastException is to be thrown. Because otherwise
		// we will just end up collecting things on a normal refclear
		this.__refReset();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/26
	 */
	@Override
	public final void doClassObjectLoad(ClassName __cl,
		JavaStackResult.Output __out)
	{
		this.__loadClassObject(__cl, __out.register);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/16
	 */
	@Override
	public final void doConvert(StackJavaType __as,
		JavaStackResult.Input __a, StackJavaType __bs,
		JavaStackResult.Output __b)
	{
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// Doing just a copy
		if (__as == __bs)
		{
			int a = __a.register,
				b = __b.register;
			
			codeBuilder.addCopy(a, b);
			if (__as.isWide())
				codeBuilder.addCopy(a + 1, b + 1);
		}
		
		// Otherwise a conversion
		else
		{
			// Get the software math class for the source type
			ClassName smc = __as.softwareMathClass();
			
			// Invoke converter method (which might be wide)
			if (__as.isWide())
				this.__invokeStatic(new InvokedMethod(InvokeType.SYSTEM, smc,
					"to" + __bs.boxedType(),
					"(II)" + __bs.signature()),
					__a.register, __a.register + 1);
			else
				this.__invokeStatic(new InvokedMethod(InvokeType.SYSTEM, smc,
					"to" + __bs.boxedType(),
					"(I)" + __bs.signature()),
					__a.register);
			
			// Read out return value
			int a = NativeCode.RETURN_REGISTER,
				b = __b.register;
			
			codeBuilder.addCopy(a, b);
			if (__bs.isWide())
				codeBuilder.addCopy(a + 1, b + 1);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/07
	 */
	@Override
	public final void doCopy(JavaStackResult.Input __in,
		JavaStackResult.Output __out)
	{
		// Push references, the dest may be overwritten
		this.__refPush();
		
		// Perform the copy, make sure to correctly handle wide copies!
		NativeCodeBuilder codebuilder = this.codebuilder;
		
		codebuilder.addCopy(__in.register, __out.register);
		if (__in.type.isWide())
			codebuilder.addCopy(__in.register + 1, __out.register + 1);
		
		// Clear references in the event it was overwritten
		this.__refClear();
	}
	
	/**
	 * Reads a field.
	 *
	 * @param __fr The field reference.
	 * @param __i The instance.
	 * @param __v The output value.
	 * @since 2019/04/12
	 */
	@Override
	public final void doFieldGet(FieldReference __fr,
		JavaStackResult.Input __i, JavaStackResult.Output __v)
	{
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// Push references
		this.__refPush();
		
		// The instance register
		MemHandleRegister instance = MemHandleRegister.of(__i.register);
		MemHandleRegister outValue = MemHandleRegister.of(__v.register);
		
		// Cannot be null!
		this.__basicCheckNPE(instance);
		
		// Must be the given class
		if (!__i.isCompatible(__fr.className()))
			this.__basicCheckCCE(instance, __i.type, __fr.className());
		
		// We already checked the only valid exceptions, so do not perform
		// later handling!
		this.state.canexception = false;
		
		// We need a volatile for the field offset
		try (Volatile<TypedRegister<AccessedField>> fOff =
			this.volatiles.getTyped(AccessedField.class))
		{
			// Read field offset
			codeBuilder.<AccessedField>addPoolLoad(this.__fieldAccess(
				FieldAccessType.INSTANCE, __fr, true), fOff.register);
			
			// Data type used
			DataType dt = DataType.of(__fr.memberType().primitiveType());
			
			// Reading wide value?
			if (dt.isWide())
				codeBuilder.addMemHandleAccess(dt, true,
					WideRegister.of(__v.register, __v.register + 1),
					instance, fOff.register.asIntValue());
			
			// Narrow value
			else
			{
				codeBuilder.addMemHandleAccess(dt, true,
					outValue.asIntValue(),
					instance, fOff.register.asIntValue());
				
				// Need to count up object?
				if (__fr.memberType().isObject())
					this.__refCount(outValue);
			}
		}
			
		// Clear references as needed
		this.__refClear();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/12
	 */
	@Override
	public final void doFieldPut(FieldReference __fr,
		JavaStackResult.Input __i, JavaStackResult.Input __v)
	{
		// Debug
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// Push references
		this.__refPush();
		
		// The instance
		MemHandleRegister instance = MemHandleRegister.of(__i.register);
		IntValueRegister value = IntValueRegister.of(__v.register);
		
		// Cannot be null
		this.__basicCheckNPE(instance);
		
		// Must be the given class
		if (!__i.isCompatible(__fr.className()))
			this.__basicCheckCCE(instance, __i.type, __fr.className());
			
		// If an object, this will need to be uncounted
		boolean isObject = __fr.memberType().isObject();
		
		// We need volatile storage
		VolatileRegisterStack volatiles = this.volatiles;
		try (Volatile<TypedRegister<AccessedField>> fieldOff =
				volatiles.getTyped(AccessedField.class);
			Volatile<MemHandleRegister> old =
				(isObject ? volatiles.getMemHandle() : null))
		{
			// Read field offset
			codeBuilder.addPoolLoad(this.__fieldAccess(
				FieldAccessType.INSTANCE, __fr, false),
				fieldOff.register);
			
			// Data type used
			DataType dt = DataType.of(__fr.memberType().primitiveType());
			
			// If we are storing an object, we need to uncount the value
			// already in this field
			if (isObject)
			{
				// Count the value up before storing it
				this.__refCount(value.asMemHandle());
				
				// Read the value of the field for later uncount
				codeBuilder.addMemHandleAccess(dt, true,
					old.register.asIntValue(),
					instance, fieldOff.register.asIntValue());
			}
			
			// Wide access?
			if (dt.isWide())
				codeBuilder.addMemHandleAccess(dt, false,
					WideRegister.of(value.register, value.register + 1),
					instance, fieldOff.register.asIntValue());
			else
				codeBuilder.addMemHandleAccess(dt, false,
					value,
					instance, fieldOff.register.asIntValue());
			
			// If we stored an object, reference count the field after it has
			// been written to
			if (isObject)
				this.__refUncount(old.register);
		}
			
		// Clear references as needed
		this.__refClear();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/12
	 */
	@Override
	public final void doIfICmp(CompareType __ct, JavaStackResult.Input __a,
		JavaStackResult.Input __b, InstructionJumpTarget __ijt)
	{
		// Push references if needed
		this.__refPush();
		
		// Add comparison
		this.codebuilder.addIfICmp(__ct, __a.register, __b.register,
			this.__labelJava(__ijt));
		
		// Clear references as needed
		this.__refClear();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/16
	 */
	@Override
	public final void doInstanceOf(ClassName __cl,
		JavaStackResult.Input __v, JavaStackResult.Output __o)
	{
		// Push reference
		this.__refPush();
		
		// Perform the common instance check
		this.__isInstance(MemHandleRegister.of(__v.register), __v.type, __cl,
			IntValueRegister.of(__o.register));
		
		// This cannot cause exceptions
		this.state.canexception = false;
		
		// Clear references in the event it was overwritten
		this.__refClear();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/10
	 */
	@Override
	public final void doInvoke(InvokeType __t, MethodReference __r,
		JavaStackResult.Output __out, JavaStackResult.Input... __in)
	{
		// Target class
		ClassName targetClass = __r.handle().outerClass();
		
		// Remap method to LLE implementation of classes?
		if (NearNativeByteCodeHandler.MLE_SHELF_PACKAGE
			.equals(targetClass.inPackage()) &&
			targetClass.toString().endsWith("Shelf"))
		{
			// Map to new class
			__r = new MethodReference(NearNativeByteCodeHandler
				.LLE_SHELF_PACKAGE.resolve(new ClassIdentifier(
					"LLE" + __r.className().simpleName())).toClass(),
				__r.memberName(), __r.memberType(), __r.isInterface());
			
			// Needs to be recycled
			targetClass = __r.handle().outerClass();
		}
		
		// Invoking a helped system call
		else if ("cc/squirreljme/jvm/summercoat/SystemCall".equals(
			targetClass.toString()))
		{
			this.__invokeSysCallHelped(__r, __out, __in);
				
			// Do nothing else
			return;
		}
		
		// Invocation of assembly method?
		else if ("cc/squirreljme/jvm/Assembly".equals(
			targetClass.toString()))
		{
			// Forward
			this.__invokeAssembly(__r.handle().name(),
				__r.handle().descriptor(), __out, __in);
			
			// Do nothing else
			return;
		}
		
		// What is being called?
		InvokedMethod invokedMethod = new InvokedMethod(__t, __r.handle());
		
		// Code generator
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// Push references
		this.__refPush();
		
		// Fill in call arguments
		List<Integer> callArgs = new ArrayList<>(__in.length * 2);
		for (int i = 0, n = __in.length; i < n; i++)
		{
			// Add the input register
			JavaStackResult.Input in = __in[i];
			callArgs.add(in.register);
			
			// But also if it is wide, we need to pass the other one or
			// else the value will be clipped
			if (in.type.isWide())
				callArgs.add(in.register + 1);
		}
		
		// Actual arguments to the call
		RegisterList regList = new RegisterList(callArgs);
		
		// If invoking static method, use our helper method
		if (__t == InvokeType.STATIC)
		{
			this.__invokeStatic(invokedMethod, regList);
		}
		
		// Interface, special, or virtual
		else
		{
			JavaStackResult.Input object = __in[0];
			MemHandleRegister instance = MemHandleRegister.of(object.register);
			
			// Check that the object is of the given class type and is not null
			this.__basicCheckNPE(instance);
			
			// Check types if this is not compatible
			if (!object.isCompatible(__r.handle().outerClass()))
				this.__basicCheckCCE(instance, object.type,
					__r.handle().outerClass());
			
			// Invoking interface method
			if (__t == InvokeType.INTERFACE)
				this.__invokeInterface(invokedMethod, __in[0].type, regList);
			
			// Special or virtual method
			else
				this.__invokeInstance(invokedMethod, regList);
		}
		
		// Check if exception occurred, before copying the return value!
		if (this.state.canexception)
		{
			// Exception check
			codeBuilder.addIfNonZero(NativeCode.EXCEPTION_REGISTER,
				this.__labelException());
			
			// We did the exception handling, so do not handle later
			this.state.canexception = false;
		}
		
		// Read in return value, it is just a copy
		if (__out != null)
		{
			int a = NativeCode.RETURN_REGISTER,
				b = __out.register;
			
			if (__out.type.isWide())
				codeBuilder.addCopy(a + 1, b + 1);
			codeBuilder.addCopy(a, b);
		}
		
		// Clear references
		this.__refClear();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/16
	 */
	@Override
	public final void doLookupSwitch(JavaStackResult.Input __key,
		LookupSwitch __ls)
	{
		NativeCodeBuilder codebuilder = this.codebuilder;
		
		// The key register
		int keyreg = __key.register;
		
		// Generate checks for all keys
		int[] keys = __ls.keys();
		InstructionJumpTarget[] jumps = __ls.jumps();
		for (int i = 0, n = keys.length; i < n; i++)
			codebuilder.add(NativeInstructionType.IFEQ_CONST,
				keyreg, keys[i], this.__labelJava(jumps[i]));
		
		// Final case is the default jump
		codebuilder.addGoto(this.__labelJava(__ls.defaultJump()));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/07
	 */
	@Override
	public final void doMath(StackJavaType __dt, MathType __mt,
		JavaStackResult.Input __a, JavaStackResult.Input __b,
		JavaStackResult.Output __c)
	{
		// Is this wide?
		boolean isWide = __dt.isWide();
		
		// The first value
		IntValueRegister al = IntValueRegister.of(__a.register);
		IntValueRegister ah = (isWide ?
			IntValueRegister.of(__a.register + 1) : null);
	
		// The second value
		IntValueRegister bl = IntValueRegister.of(__b.register);
		IntValueRegister bh = (isWide ?
			IntValueRegister.of(__b.register + 1) : null);
		
		// The destination register
		IntValueRegister cl = IntValueRegister.of(__c.register);
		IntValueRegister ch = (isWide ?
			IntValueRegister.of(__c.register + 1) : null);
		
		// Perform the math operation
		this.__math(__dt, __mt,
			al, ah, bl, bh, cl, ch);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/07
	 */
	@Override
	public final void doMath(StackJavaType __dt, MathType __mt,
		JavaStackResult.Input __a, Number __b, JavaStackResult.Output __c)
	{
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// Is this wide?
		boolean isWide = __dt.isWide();
		
		// Is this just a constant value load?
		boolean isConst = (__a == JavaStackResult.INPUT_ZERO &&
			__mt == MathType.OR);
		
		// The first value
		IntValueRegister al = IntValueRegister.of(__a.register);
		IntValueRegister ah = (isWide ?
			IntValueRegister.of(__a.register + 1) : null);
		
		// The resultant registers
		IntValueRegister cl = IntValueRegister.of(__c.register);
		IntValueRegister ch = (isWide ?
			IntValueRegister.of(__c.register + 1) : null);
		
		// We need volatiles to do math with
		VolatileRegisterStack volatiles = this.volatiles;
		try (Volatile<IntValueRegister> vl = (!isConst ?
				volatiles.getIntValue() : null);
			Volatile<IntValueRegister> vh = (isWide && !isConst ?
				volatiles.getIntValue() : null))
		{
			// Obtain the raw value for constant for loading
			long constVal;
			if (__b instanceof Integer)
				constVal = __b.intValue();
			else if (__b instanceof Long)
				constVal = __b.longValue(); 
			else if (__b instanceof Float)
				constVal = Float.floatToRawIntBits(__b.floatValue());
			else
				constVal = Double.doubleToRawLongBits(__b.doubleValue());
			
			// The target register, if not const
			IntValueRegister bl = (isConst ? cl : vl.register);
			IntValueRegister bh = (isWide ?
				(isConst ? ch : vh.register) : null);
			
			// Lower value of the integer/float?
			codeBuilder.addMathConst(StackJavaType.INTEGER, MathType.OR,
				IntValueRegister.ZERO, (int)constVal,
				bl);
			
			// Upper value of the long/double
			if (isWide)
				codeBuilder.addMathConst(StackJavaType.INTEGER, MathType.OR,
					IntValueRegister.ZERO, (int)(constVal >>> 32),
					bh);
			
			// Perform the actual math if not constant
			if (!isConst)
				this.__math(__dt, __mt,
					al, ah, bl, bh, cl, ch);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/16
	 */
	@Override
	public final void doMonitor(boolean __enter, JavaStackResult.Input __o)
	{
		// Push reference
		this.__refPush();
		
		// Generate instruction
		this.__monitor(__enter, __o.register);
		
		// Clear reference
		this.__refClear();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/05/04
	 */
	@Override
	public final void doMultiANewArray(ClassName __cl, int __numdims,
		JavaStackResult.Output __o, JavaStackResult.Input... __dims)
	{
		NativeCodeBuilder codebuilder = this.codebuilder;
		
		// Need volatiles
		VolatileRegisterStack volatiles = this.volatiles;
		int volclassobj = volatiles.getUnmanaged();
		
		// Load the class we want to allocate
		this.__loadClassObject(__cl, volclassobj);
		
		// Determine the number of integer arguments to use, since we
		// are passing multiple arguments to multianewarray
		StringBuilder sb = new StringBuilder(__numdims);
		for (int i = 0; i < __numdims; i++)
			sb.append('I');
			
		// Build arguments to the method call (class, skip, args);
		List<Integer> rl = new ArrayList<>();
		rl.add(volclassobj);
		rl.add(0);
		for (int i = 0; i < __numdims; i++)
			rl.add(__dims[i].register);
		
		// Invoke array utility
		this.__invokeStatic(new InvokedMethod(InvokeType.SYSTEM,
			"cc/squirreljme/runtime/cldc/lang/ArrayUtils",
			"multiANewArray",
			"(Ljava/lang/Class;I" + sb + ")Ljava/lang/Object;"),
			new RegisterList(rl));
		
		// Use this result
		codebuilder.addCopy(NativeCode.RETURN_REGISTER, __o.register);
		
		// Not needed anymore
		volatiles.removeUnmanaged(volclassobj);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/11
	 */
	@Override
	public final void doNew(ClassName __cn, JavaStackResult.Output __out)
	{
		NativeCodeBuilder codebuilder = this.codebuilder;
		
		// Need result register
		VolatileRegisterStack volatiles = this.volatiles;
		int volresult = volatiles.getUnmanaged();
		
		// Perform new invocation
		this.__invokeNew(__cn, volresult);
		
		// Check for out of memory
		this.__basicCheckOOM(volresult);
		
		// All the exceptions were checked
		this.state.canexception = false;
		
		// Copy to result
		codebuilder.addCopy(volresult, __out.register);
		
		// Not needed
		volatiles.removeUnmanaged(volresult);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/12
	 */
	@Override
	public final void doNewArray(ClassName __at,
		JavaStackResult.Input __len, JavaStackResult.Output __out)
	{
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// Check if the array is negative, fail here if so
		this.__basicCheckNAS(__len.register);
		
		// Need volatiles
		VolatileRegisterStack volatiles = this.volatiles;
		try (Volatile<TypedRegister<TypeBracketPointer>> classInfo =
			volatiles.getTyped(TypeBracketPointer.class))
		{
			// Load the class information for the register
			this.__loadClassInfo(__at, classInfo.register);
			
			// Call the helper for the method
			this.__invokeHelper(HelperFunction.NEW_ARRAY,
				classInfo.register, PlainRegister.of(__len.register));
			
			// Copy the result to the output
			codeBuilder.<MemHandleRegister>addCopy(
				MemHandleRegister.RETURN,
				MemHandleRegister.of(__out.register));
		}
		
		// Negative array and invocation exceptions were checked.
		this.state.canexception = false;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/12
	 */
	@Override
	public final void doPoolLoadString(String __v,
		JavaStackResult.Output __out)
	{
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// Load the string from the pool, it is already a String object here
		// and we do not need to do anything for it at all. It will be interned
		// here always.
		TypedRegister<UsedString> out = TypedRegister.<UsedString>of(
			UsedString.class, __out.register);
		codeBuilder.addPoolLoad(new UsedString(__v), out);
		
		// Count it up so it is not instantly GCed, unless it is marked as
		// not being counted
		if (!__out.nocounting)
			this.__refCount(out.asMemHandle());
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/11
	 */
	@Override
	public final void doReturn(JavaStackResult.Input __in)
	{
		NativeCodeBuilder codebuilder = this.codebuilder;
		
		// If we are returning an object, we need to reference count it up
		// so it does not get garbage collected as a return is happening
		if (__in != null && __in.isObject())
			this.__refCount(__in.register);
		
		// Uncount all references that need to be cleared out as we return
		for (int q : this.state.result.enqueue())
			this.__refUncount(q);
		
		// Copy the returning value at the end to increase it's lifetime
		// as much as possible. Otherwise if we copy too early to the return
		// register then future uncounts can completely trash the value
		if (__in != null)
		{
			int a = __in.register,
				b = NativeCode.RETURN_REGISTER;
			
			// Copy value to return register
			if (__in.type.isWide())
				codebuilder.addCopy(a + 1, b + 1);
			codebuilder.addCopy(a, b);
		}
		
		// Do the return
		this.__generateReturn();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/11
	 */
	@Override
	public final void doStateOperations(StateOperations __ops)
		throws NullPointerException
	{
		if (__ops == null)
			throw new NullPointerException("NARG");
		
		// If there are no operations, just do not bother
		if (__ops.isEmpty())
			return;
		
		// We need volatiles to process these
		VolatileRegisterStack volatiles = this.volatiles;
		try (@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
			AutoCloseableList<Volatile<?>> closed = new AutoCloseableList<>())
		{
			// Load register values into the map
			Map<SoftRegister, Register> mapping =
				new LinkedHashMap<>();
			for (StateOperation op : __ops)
				for (SoftRegister sr : new SoftRegister[]{op.a, op.b})
					if (!mapping.containsKey(sr))
					{
						// Build register value
						Register r;
						if (!sr.isTemporary)
						{
							if (op.type.isWide())
								r = WideRegister.of(sr.register,
									sr.register + 1);
							else
								r = IntValueRegister.of(sr.register);
						}
						
						// Temporaries need to be cleaned up
						else
						{
							Volatile<? extends Register> vol;
							if (op.type.isWide())
								vol = volatiles.getWide();
							else
								vol = volatiles.getIntValue();
							
							// Get the base register to use
							r = vol.register;
							
							// Close for later
							closed.add(vol);
						}
						
						// Store for the mapping	
						mapping.put(sr, r);
					}
			
			// Generate code for the operations, using the appropriate
			// registers and such
			NativeCodeBuilder codeBuilder = this.codebuilder;
			for (StateOperation op : __ops)
			{
				// Get the A and B sides
				Register a = mapping.get(op.a);
				Register b = mapping.get(op.b);
				
				// Do the action
				switch (op.type)
				{
					case UNCOUNT:
						this.__refUncount(a.asMemHandle());
						break;
					
					case COUNT:
						this.__refCount(a.asMemHandle());
						break;
					
					case COPY:
						codeBuilder.addCopy(a, b);
						break;
					
					case WIDE_COPY:
						WideRegister wa = (WideRegister)a;
						WideRegister wb = (WideRegister)b;
						
						codeBuilder.addCopy(wa.low, wb.low);
						codeBuilder.addCopy(wa.high, wb.high);
						break;
					
					default:
						throw Debugging.oops();
				}
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/12
	 */
	@Override
	public final void doStaticGet(FieldReference __fr,
		JavaStackResult.Output __v)
	{
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// Push references
		this.__refPush();
		
		// Which field is being accessed?
		AccessedField fieldAccess = this.__fieldAccess(
			FieldAccessType.STATIC, __fr, true);
		
		// Need volatiles
		VolatileRegisterStack volatiles = this.volatiles;
		try (Volatile<MemHandleRegister> staticStore = 
				volatiles.getMemHandle();
			Volatile<IntValueRegister> offset = 
				volatiles.getIntValue())
		{
			// Determine where we are writing
			this.__doStaticAccess(__fr, fieldAccess, staticStore, offset);
			
			// Perform the read
			DataType dataType = DataType.of(__fr.memberType());
			if (dataType.isWide())
				codeBuilder.addMemHandleAccess(dataType,
					true, WideRegister.of(
						__v.register, __v.register + 1),
					staticStore.register, offset.register);
			else
				codeBuilder.addMemHandleAccess(dataType,
					true, IntValueRegister.of(__v.register),
					staticStore.register, offset.register);
			
			// If this is an object, we must count up!
			if (dataType == DataType.OBJECT)
				this.__refCount(MemHandleRegister.of(__v.register));
		}
			
		// Clear references as needed
		this.__refClear();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/13
	 */
	@Override
	public final void doStaticPut(FieldReference __fr,
		JavaStackResult.Input __v)
	{
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// Push references
		this.__refPush();
		
		// Which field is being accessed?
		AccessedField fieldAccess = this.__fieldAccess(
			FieldAccessType.STATIC, __fr, true);
		
		// Need volatiles
		VolatileRegisterStack volatiles = this.volatiles;
		try (Volatile<MemHandleRegister> staticStore = 
				volatiles.getMemHandle();
			Volatile<IntValueRegister> offset = volatiles.getIntValue();
			Volatile<MemHandleRegister> old = volatiles.getMemHandle())
		{
			// Determine where we are writing
			this.__doStaticAccess(__fr, fieldAccess, staticStore, offset);
			
			// If we are storing an object, we need to handle reference
			// counts for these
			DataType dataType = DataType.of(__fr.memberType());
			if (dataType == DataType.OBJECT)
			{
				// Read the old object
				codeBuilder.addMemHandleAccess(dataType,
					true, old.register.asIntValue(),
					staticStore.register, offset.register);
				
				// Count up the value we are storing
				this.__refCount(MemHandleRegister.of(__v.register));
			}
			
			// Perform the write
			if (dataType.isWide())
				codeBuilder.addMemHandleAccess(dataType,
					false, WideRegister.of(
						__v.register, __v.register + 1),
					staticStore.register, offset.register);
			else
				codeBuilder.addMemHandleAccess(dataType,
					false, IntValueRegister.of(__v.register),
					staticStore.register, offset.register);
			
			// We read the old value to count it down
			if (dataType == DataType.OBJECT)
				this.__refUncount(old.register);
		}
			
		// Clear references as needed
		this.__refClear();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/12
	 */
	@Override
	public final void doThrow(JavaStackResult.Input __in)
	{
		NativeCodeBuilder codebuilder = this.codebuilder;
		
		// Push references
		this.__refPush();
		
		// Cannot be null
		this.__basicCheckNPE(__in.register);
		
		// Copy into the exception register
		codebuilder.addCopy(__in.register, NativeCode.EXCEPTION_REGISTER);
		
		// Clear the reference queue because this results in a net reference
		// count if this point is reached, however this is still needed for
		// the NPE check even though null values will never be counted
		this.__refReset();
		
		// Do not jump at this point, just return the exception check will be
		// flagged which will start the exception handling
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/07
	 */
	@Override
	public final void instructionFinish()
	{
		ByteCodeState state = this.state;
		
		// {@squirreljme.error JC12 Enqueues were not cleared, this is an
		// internal compiler error.}
		if (this._lastenqueue != null)
			throw new IllegalStateException("JC12");
		
		// An exception check was requested, do a check on the exception
		// register and jump if there is something there
		if (state.canexception)
			this.codebuilder.addIfNonZero(NativeCode.EXCEPTION_REGISTER,
				this.__labelException());
		
		// If this instruction naturally flows into another, determine we
		// need to transition to that stack state in order to work properly
		// with any cached values
		if (state.instruction.hasNaturalFlow())
		{
			// Get following stack
			int followaddr = state.followaddr;
			JavaStackState nowstack = state.stack,
				followstack = state.stacks.get(followaddr);
			
			// If our current stack is not compatible with the target stack
			// then we need to transition to that state. However the Java label
			// code already handles transition, so do this to remove duplicate
			// code with just a minor jump around.
			// However if the next state is poisoned we do not do this because
			// we will just naturally transition to a pure de-cache at the
			// start of everything anyway.
			if (!nowstack.equals(followstack) &&
				!state.stackpoison.containsKey(followaddr))
				this.codebuilder.addGoto(
					this.__labelJava(new InstructionJumpTarget(followaddr)));
		}
		
		// Clear volatiles
		this.volatiles.clear();
	}
	
	/**
	 * Sets up before processing the instruction.
	 *
	 * @since 2019/04/07
	 */
	@Override
	public final void instructionSetup()
	{
		NativeCodeBuilder codebuilder = this.codebuilder;
		ByteCodeState state = this.state;
		int addr = state.addr;
		
		// Clear volatiles
		this.volatiles.clear();
		
		// Entry point debugging
		if (addr == 0)
		{
			// Debug entry point
			codebuilder.add(NativeInstructionType.DEBUG_ENTRY,
				new TypeBracketPointer(state.classname),
				new NotedString(state.methodname.toString()),
				new NotedString(state.methodtype.toString()),
				new NullPoolEntry(),
				this.methodIndex);
			
			// Setup monitor entry
			if (this.issynchronized)
			{
				// Copy instance to monitor target
				if (this.isinstance)
					codebuilder.addCopy(state.stack.getLocal(0).register,
						this.monitortarget);
				
				// Load class object to monitor
				else
					this.__loadClassObject(state.classname,
						this.monitortarget);
				
				// Enter monitor on this
				this.__refCount(this.monitortarget);
				this.__monitor(true, this.monitortarget);
			}
		}
		
		// Check if there are operations that need to be performed to make
		// sure the stack state is morphed into correctly
		StateOperations poison = state.stackpoison.get(addr);
		if (poison != null)
			this.doStateOperations(poison);
		
		// Setup a label for this current position, this is done after
		// potential flushing because it is assumed that the current state
		// is always valid even after a flush
		codebuilder.label("java", addr);
		
		// Debug single instruction point, place after the Java label so
		// that it is always set regardless of program flow
		codebuilder.add(NativeInstructionType.DEBUG_POINT,
			state.line & 0x7FFF, state.instruction.operation() & 0xFF,
			state.instruction.address() & 0x7FFF);
	}
	
	/**
	 * Returns the result of the translation.
	 *
	 * @return The translation result.
	 * @since 2019/04/07
	 */
	public final NativeCode result()
	{
		NativeCodeBuilder codebuilder = this.codebuilder;
		List<JavaStackEnqueueList> returns = this._returns;
		VolatileRegisterStack volatiles = this.volatiles;
		
		// Queues for various exception handlers
		Deque<__TransitingExceptionHandler__> ehQueue = this._ehTableQueue;
		Deque<__RefClearJump__> refClQueue = this._refClearJumpQueue;
		Deque<__MakeException__> makeExQueue = this._meTableQueue;
		Deque<__StateOpTransit__> transitQueue = this._transitQueue;
		
		// Keep generating these late label transitions and otherwise until
		// they are completely drained out. It is possible some exception
		// handlers may generated special handling calls, in which case they
		// can happen while they are being handled.
		int cycleCount = 0;
		do
		{
			// Extra cycles processed?
			if (++cycleCount > 1)
			{
				// Note the cycle
				Debugging.debugNote("Generation Cycle: %d", cycleCount);
				
				// Dump what is remaining in the cycles, for debugging
				Debugging.debugNote("ehQueue: %s", ehQueue);
				Debugging.debugNote("refClQueue: %s", refClQueue);
				Debugging.debugNote("makeExQueue: %s", makeExQueue);
				Debugging.debugNote("transitQueue: %s", transitQueue);
			}
			
			// Generate reference clear jumps
			while (!refClQueue.isEmpty())
			{
				__RefClearJump__ refClearJump = refClQueue.remove();
				
				EnqueueAndLabel eql = refClearJump._enqueueAndLabel;
				__EData__ eData = refClearJump._eData;
				
				// Set label target for this one
				codebuilder.label(this.__useEDataAndGetLabel(eData));
				this.__useEDataDebugPoint(eData, 256);
				
				// Clear references
				JavaStackEnqueueList enq = eql.enqueue;
				for (int i = 0, n = enq.size(); i < n; i++)
					this.__refUncount(enq.get(i));
				
				// Then go to the target
				codebuilder.addGoto(eql.label);
			}
			
			// Generate make exception code
			while (!makeExQueue.isEmpty())
			{
				__MakeException__ makeException = makeExQueue.remove();
				
				ClassAndLabel csl = makeException._classAndLabel;
				__EData__ eData = makeException._eData;
				
				// Set label target for this one
				codebuilder.label(this.__useEDataAndGetLabel(eData));
				this.__useEDataDebugPoint(eData, 257);
				
				// Allocate class object, make sure it is not done in the exception
				// register because the method we call may end up just clearing it
				// and stopping if a make exception is done (because there is an
				// exception here).
				int exinst = volatiles.getUnmanaged();
				this.__invokeNew(csl.classname, exinst);
				
				// Initialize the exception
				this.__invokeInstance(new InvokedMethod(InvokeType.SPECIAL,
					csl.classname, "<init>", "()V"),
					new RegisterList(exinst));
				
				// Copy result into the exception register
				codebuilder.addCopy(exinst, NativeCode.EXCEPTION_REGISTER);
				
				// Done with this
				volatiles.removeUnmanaged(exinst);
				
				// Generate jump to exception handler
				codebuilder.addGoto(csl.label);
			}
			
			// Generate exception handler tables
			while (!ehQueue.isEmpty())
			{
				__TransitingExceptionHandler__ transit = ehQueue.remove();
				
				ExceptionHandlerTransition ehtran = transit._transition;
				__EData__ eData = transit._eData;
				
				StateOperations sops = ehtran.handled;
				JavaStackEnqueueList enq = ehtran.nothandled;
				ExceptionHandlerTable ehtable = ehtran.table;
				
				// Label used for the jump target
				NativeCodeLabel lab = this.__useEDataAndGetLabel(eData);
				this.__useEDataDebugPoint(eData, 258);
				
				// If the table is empty, just return
				if (ehtable.isEmpty())
				{
					// If we never saw a cleanup for this return yet we can
					// generate one here to be used for later points
					int rdx = returns.indexOf(enq);
					if (rdx < 0)
						codebuilder.label(lab,
							codebuilder.labelTarget(this.__generateReturn(enq)));
					
					// We can just alias this exception to the return point to
					// cleanup everything
					else
						codebuilder.label(lab,
							codebuilder.labelTarget("return", rdx));
					
					// Generate the next handler
					continue;
				}
				
				// Set label target for this one
				codebuilder.label(lab);
				
				// Go through and build the exception handler table
				for (ExceptionHandler eh : ehtable)
					try (Volatile<IntValueRegister> isInstance =
						volatiles.getIntValue())
					{
						// Do common instance check, assume that thrown
						// exceptions extend Throwable
						this.__isInstance(MemHandleRegister.EXCEPTION,
							JavaType.THROWABLE, eh.type(),
							isInstance.register);
						
						// If the return value is non-zero then it is an instance, in
						// which case we jump to the handler address.
						codebuilder.addIfNonZero(isInstance.register,
							this.__labelJavaTransition(sops,
								new InstructionJumpTarget(
									eh.handlerAddress())));
					}
				
				// No exception handler is available so, just fall through to the
				// caller as needed
				this.__generateReturn(enq);
			}
			
			// Generate transition labels
			while (!transitQueue.isEmpty())
			{
				__StateOpTransit__ transit = transitQueue.remove();
				
				StateOperationsAndTarget sot = transit._stateOpAndTarget;
				StateOperations ops = sot.operations;
				InstructionJumpTarget target = sot.target;
				
				__EData__ eData = transit._eData;
				
				// Set label target for this one
				codebuilder.label(this.__useEDataAndGetLabel(eData));
				this.__useEDataDebugPoint(eData, 259);
				
				// Generate operations
				this.doStateOperations(ops);
				
				// Then just jump to the Java target
				codebuilder.addGoto(new NativeCodeLabel("java",
					target.target()));
			}
		} while (!ehQueue.isEmpty() || !refClQueue.isEmpty() ||
			!makeExQueue.isEmpty() || !transitQueue.isEmpty());
		
		return codebuilder.build();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/06
	 */
	@Override
	public final ByteCodeState state()
	{
		return this.state;
	}
	
	/**
	 * Checks if an array access is within bounds.
	 *
	 * @param __ir The instance register.
	 * @param __dxr The index register.
	 * @param __dt The data type used.
	 * @param __outOffset The output offset.
	 * @deprecated Use {@link
	 * NearNativeByteCodeHandler#__basicCheckArrayBound(MemHandleRegister,
	 * IntValueRegister, DataType, IntValueRegister)}
	 * @since 2019/04/27
	 */
	@Deprecated
	private void __basicCheckArrayBound(int __ir, int __dxr, DataType __dt,
		IntValueRegister __outOffset)
	{
		this.__basicCheckArrayBound(MemHandleRegister.of(__ir),
			IntValueRegister.of(__dxr), __dt, __outOffset);
	}
	
	/**
	 * Checks if an array access is within bounds.
	 *
	 * @param __array The instance register.
	 * @param __dx The index register.
	 * @param __dt The data type used.
	 * @param __outOffset The output offset.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/27
	 */
	private void __basicCheckArrayBound(MemHandleRegister __array,
		IntValueRegister __dx, DataType __dt, IntValueRegister __outOffset)
		throws NullPointerException
	{
		if (__array == null || __dx == null || __dt == null ||
			__outOffset == null)
			throw new NullPointerException("NARG");
		
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// In the event the array is negative, this is thrown
		NativeCodeLabel failAIOOB = this.__labelMakeException(
			"java/lang/ArrayIndexOutOfBoundsException");
		
		// Out of bounds if the index is less than zero
		codeBuilder.addIfICmp(CompareType.LESS_THAN,
			__dx, IntValueRegister.ZERO, failAIOOB);
		
		// Need volatiles
		VolatileRegisterStack volatiles = this.volatiles;
		try (Volatile<IntValueRegister> arrayBase = volatiles.getIntValue();
			Volatile<IntValueRegister> cellSize = volatiles.getIntValue();
			Volatile<IntValueRegister> offset = volatiles.getIntValue())
		{
			// We need to know the base for array types, there is a system
			// call for this
			this.__invokeSysCallV(SystemCallIndex.ARRAY_ALLOCATION_BASE,
				arrayBase.register);
			
			// Cell size of indexes
			codeBuilder.addIntegerConst(__dt.size(), cellSize.register);
			
			// Calculate the base before the offset
			codeBuilder.addMathReg(StackJavaType.INTEGER, MathType.MUL,
				__dx, cellSize.register, offset.register);
			
			// Calculate the true offset used
			codeBuilder.addMathReg(StackJavaType.INTEGER, MathType.ADD,
				offset.register, arrayBase.register, offset.register);
			
			// Check that it is in range of the memory handle
			this.__invokeSysCallV(SystemCallIndex.MEM_HANDLE_IN_BOUNDS,
				arrayBase.register,
				__array, offset.register, cellSize.register);
			
			// If not in bounds, this fails
			codeBuilder.addIfZero(arrayBase.register, failAIOOB);
			
			// Before we go away, copy the calculated offset to the output
			// so we do not need to recalculate it again
			codeBuilder.addCopy(offset.register, __outOffset);
		}
	}
	
	/**
	 * Checks if the target array can store this value.
	 *
	 * @param __arrayType The array type.
	 * @param __array The instance register.
	 * @param __valType The value type.
	 * @param __val The value register.
	 * @since 2019/04/27
	 */
	private void __basicCheckArrayStore(
		JavaType __arrayType, MemHandleRegister __array,
		JavaType __valType, MemHandleRegister __val)
		throws NullPointerException
	{
		if (__array == null || __val == null)
			throw new NullPointerException("NARG");
		
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// Call helper class
		this.__invokeHelper(HelperFunction.ARRAY_CHECK_STORE,
			__array, __val);
		
		// Was it invalid?
		codeBuilder.addIfZero(IntValueRegister.RETURN,
			this.__labelMakeException("java/lang/ArrayStoreException"));
	}
	
	/**
	 * Basic check if the instance is of the given class.
	 *
	 * @param __ir The register to check.
	 * @param __jType The type used on instance.
	 * @param __cl The class that this will be cast to.
	 * @deprecated Use the type-safe
	 * {@link #__basicCheckNPE(MemHandleRegister)} instead. 
	 * @since 2019/04/22
	 */
	@Deprecated
	private void __basicCheckCCE(int __ir, JavaType __jType, ClassName __cl)
		throws NullPointerException
	{
		this.__basicCheckCCE(MemHandleRegister.of(__ir), __jType, __cl);
	}
	
	/**
	 * Basic check if the instance is of the given class.
	 *
	 * @param __instance The register to check.
	 * @param __jType The type used on instance.
	 * @param __cl The class that this will be cast to.
	 * @throws NullPointerException On null arguments, except for
	 * {@code __jType}.
	 * @since 2020/11/28
	 */
	private void __basicCheckCCE(MemHandleRegister __instance,
		JavaType __jType, ClassName __cl)
		throws NullPointerException
	{
		if (__instance == null || __cl == null)
			throw new NullPointerException("NARG");
		
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// Need volatiles
		VolatileRegisterStack volatiles = this.volatiles;
		try (Volatile<IntValueRegister> result = volatiles.getIntValue())
		{
			// Perform the calculation and store it into the given result
			this.__isInstance(__instance, __jType, __cl, result.register);
			
			// If the resulting method call returns zero then it is not an
			// instance of the given class. The return register is checked
			// because the value of that method will be placed there.
			codeBuilder.addIfZero(result.register,
				this.__labelRefClearJump(this.__labelMakeException(
					"java/lang/ClassCastException")));
		}
	}
	
	/**
	 * Checks for divide by zero.
	 *
	 * @param __br The B register.
	 * @since 2019/06/24
	 */
	private void __basicCheckDBZ(IntValueRegister __br)
	{
		// If the B register is zero, then we throw the exception
		this.codebuilder.addIfZero(__br, this.__labelMakeException(
			"java/lang/ArithmeticException"));
	}
	
	/**
	 * Checks that the given object is an array.
	 *
	 * @param __object The type to check.
	 * @deprecated Use {@link NearNativeByteCodeHandler#
	 * __basicCheckIsArray(MemHandleRegister)}. 
	 * @since 2019/04/27
	 */
	@Deprecated
	private void __basicCheckIsArray(int __object)
	{
		this.__basicCheckIsArray(MemHandleRegister.of(__object));
	}
	
	/**
	 * Checks that the given object is an array.
	 *
	 * @param __object The object to check.
	 * @since 2021/01/24
	 */
	private void __basicCheckIsArray(MemHandleRegister __object)
		throws NullPointerException
	{
		// Call internal helper
		this.__invokeHelper(HelperFunction.IS_ARRAY, __object);
		
		// If this is not an array, throw a class cast exception
		this.codebuilder.addIfZero(IntValueRegister.RETURN,
			this.__labelRefClearJump(this.__labelMakeException(
			"java/lang/ClassCastException")));
	}
	
	/**
	 * Checks if the requested array allocation is negative.
	 *
	 * @param __lr The length register.
	 * @since 2019/06/28
	 */
	private void __basicCheckNAS(int __lr)
	{
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// Check against less than zero
		codeBuilder.addIfICmp(CompareType.LESS_THAN,
			__lr, NativeCode.ZERO_REGISTER, this.__labelRefClearJump(
			this.__labelMakeException(
				"java/lang/NegativeArraySizeException")));
	}
	
	/**
	 * Basic check if the instance is null.
	 *
	 * @param __ir The register to check.
	 * @deprecated Use the type safe {@link #__basicCheckNPE(
	 * MemHandleRegister)}. 
	 * @since 2019/04/22
	 */
	@Deprecated
	private void __basicCheckNPE(int __ir)
	{
		this.__basicCheckNPE(MemHandleRegister.of(__ir));
	}
	
	/**
	 * Basic check if the instance is null.
	 *
	 * @param __ir The register to check.
	 * @since 2020/11/28
	 */
	private void __basicCheckNPE(MemHandleRegister __ir)
		throws NullPointerException
	{
		if (__ir == null)
			throw new NullPointerException("NARG");
		
		this.codebuilder.addIfNull(__ir, this.__labelRefClearJump(
			this.__labelMakeException("java/lang/NullPointerException"))); 
	}
	
	/**
	 * Checks if the given allocation ran out of memory.
	 *
	 * @param __ir The register to check.
	 * @since 2019/06/28
	 */
	private void __basicCheckOOM(int __ir)
	{
		NativeCodeBuilder codebuilder = this.codebuilder;
		
		// Just a plain zero check
		codebuilder.addIfZero(__ir, this.__labelRefClearJump(
			this.__labelMakeException("java/lang/OutOfMemoryError")));
	}
	
	/**
	 * Accesses a static field.
	 * 
	 * @param __fr The field reference.
	 * @param __fieldAccess The accessor for the pool.
	 * @param __staticStore The output static storage area.
	 * @param __offset The output offset to the field.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/24
	 */
	private void __doStaticAccess(FieldReference __fr,
		AccessedField __fieldAccess,
		Volatile<MemHandleRegister> __staticStore,
		Volatile<IntValueRegister> __offset)
		throws NullPointerException
	{
		if (__fr == null || __fieldAccess == null || __staticStore == null ||
			__offset == null)
			throw new NullPointerException("NARG");
		
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// Will need storage to get classes
		VolatileRegisterStack volatiles = this.volatiles;
		try (Volatile<TypedRegister<TypeBracketPointer>> classInfo =
			volatiles.getTyped(TypeBracketPointer.class))
		{
			// Load the class information, since it has the static storage
			// area of the field... we also might need to initialize the
			// class if it has not been initialized
			this.__loadClassInfo(__fr.className(), classInfo.register);
			
			// Get the static field storage for the class
			codeBuilder.addIntegerConst(
				ClassProperty.MEMHANDLE_STATIC_FIELDS,
				__staticStore.register.asIntValue());
			this.__invokeHelper(HelperFunction.TYPE_GET_PROPERTY,
				classInfo.register, __staticStore.register);
			
			// Use this as the base
			codeBuilder.addCopy(IntValueRegister.RETURN,
				__staticStore.register);
		}
		
		// Load the offset to the field
		codeBuilder.addPoolLoad(__fieldAccess,
			TypedRegister.of(AccessedField.class,
				__offset.register.register));
	}
	
	/**
	 * Makes an EData for the current position and label.
	 *
	 * @param __lab The stored label.
	 * @return The made EData.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/26
	 */
	private __EData__ __eData(NativeCodeLabel __lab)
		throws NullPointerException
	{
		if (__lab == null)
			throw new NullPointerException("NARG");
		
		ByteCodeState state = this.state;
		return new __EData__(state.addr, state.line,
			state.instruction.address(), state.instruction.operation(), __lab);
	}
	
	/**
	 * Generates an access to a field.
	 *
	 * @param __at The type of access to perform.
	 * @param __fr The reference to the field.
	 * @param __read Is a read being performed?
	 * @return The accessed field.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/03/24
	 */
	private AccessedField __fieldAccess(FieldAccessType __at,
		FieldReference __fr, boolean __read)
		throws NullPointerException
	{
		if (__at == null || __fr == null)
			throw new NullPointerException("NARG");
		
		// Accessing final fields of another class will always be treated as
		// normal despite being in the constructor of a class
		if (!this.state.classname.equals(__fr.className()))
			return new AccessedField((__read ? FieldAccessTime.READ :
				FieldAccessTime.NORMAL), __at, __fr);
		return new AccessedField((__read ? FieldAccessTime.READ :
			this.defaultfieldaccesstime), __at, __fr);
	}
	
	/**
	 * Generates or jumps to another return point.
	 *
	 * @return The label to this return point.
	 * @since 2019/04/11
	 */
	private NativeCodeLabel __generateReturn()
	{
		return this.__generateReturn(this.state.stack.possibleEnqueue());
	}
	
	/**
	 * Generates or jumps to another return point for the given enqueue.
	 *
	 * @param __eq The enqueue to return for.
	 * @throws NullPointerException On null arguments.
	 * @return The label to this return point.
	 * @since 2019/04/11
	 */
	private NativeCodeLabel __generateReturn(JavaStackEnqueueList __eq)
		throws NullPointerException
	{
		if (__eq == null)
			throw new NullPointerException("NARG");
		
		// Will be used to generate safe spots
		VolatileRegisterStack volatiles = this.volatiles;
		int ssh = -1,
			ssl = -1;
		
		// Find unique return point
		boolean freshdx;
		List<JavaStackEnqueueList> returns = this._returns;
		int dx = returns.indexOf(__eq);
		if ((freshdx = (dx < 0)))
			returns.add((dx = returns.size()), __eq);
		
		// Label used for return
		NativeCodeLabel lb = new NativeCodeLabel("return", dx);
		
		// If this was never added here, make sure a label exists
		if (freshdx)
			this.codebuilder.label(lb);
		
		// If the enqueue list is empty then the only thing we need to do
		// is generate a return instruction
		NativeCodeBuilder codebuilder = this.codebuilder;
		if (__eq.isEmpty())
		{
			// If this is synchronized, we need to exit the monitor
			if (this.issynchronized)
			{
				// Protect return value, if there is one
				if (this.isreturn)
				{
					ssh = volatiles.getUnmanaged();
					codebuilder.addCopy(NativeCode.RETURN_REGISTER, ssh);
				}
				
				// And wide value, if any
				if (this.isreturnwide)
				{
					ssl = volatiles.getUnmanaged();
					codebuilder.addCopy(NativeCode.RETURN_REGISTER + 1, ssl);
				}
				
				// Uncount and clear out
				this.__monitor(false, this.monitortarget);
				this.__refUncount(this.monitortarget);
				
				// Recover value, if any
				if (this.isreturn)
				{
					codebuilder.addCopy(ssh, NativeCode.RETURN_REGISTER);
					volatiles.removeUnmanaged(ssh);
				}
				
				// Recover wide, if any
				if (this.isreturnwide)
				{
					codebuilder.addCopy(ssl, NativeCode.RETURN_REGISTER + 1);
					volatiles.removeUnmanaged(ssl);
				}
			}
			
			// Debug exit
			codebuilder.add(NativeInstructionType.DEBUG_EXIT);
			
			// Since there is nothing to uncount, just return
			codebuilder.add(NativeInstructionType.RETURN);
			
			return lb;
		}
		
		// If we are not making a fresh index there more things to clear out
		// then just jump to the pre-existing return point
		if (!freshdx && __eq.size() > 1)
		{
			// Jump to label
			codebuilder.addGoto(lb);
			
			return lb;
		}
		
		// Protect return value, if there is one
		if (this.isreturn)
		{
			ssh = volatiles.getUnmanaged();
			codebuilder.addCopy(NativeCode.RETURN_REGISTER, ssh);
		}
		
		// And wide value, if any
		if (this.isreturnwide)
		{
			ssl = volatiles.getUnmanaged();
			codebuilder.addCopy(NativeCode.RETURN_REGISTER + 1, ssl);
		}
		
		// Since the enqueue list is not empty, we can just trim a register
		// from the top and recursively go down
		// So uncount the top
		this.__refUncount(__eq.top());
		
		// Recover value, if any
		if (this.isreturn)
		{
			codebuilder.addCopy(ssh, NativeCode.RETURN_REGISTER);
			volatiles.removeUnmanaged(ssh);
		}
		
		// Recover wide, if any
		if (this.isreturnwide)
		{
			codebuilder.addCopy(ssl, NativeCode.RETURN_REGISTER + 1);
			volatiles.removeUnmanaged(ssl);
		}
		
		// Recursively go down since the enqueues may possibly be shared, if
		// any of these enqueues were previously made then the recursive
		// call will just make a goto
		this.__generateReturn(__eq.trimTop());
		
		// Note that we do not return the recursive result because that
		// will be for another enqueue state
		return lb;
	}
	
	/**
	 * Invokes an {@link Assembly} method.
	 *
	 * @param __name The method name.
	 * @param __type The method type.
	 * @param __out The result.
	 * @param __in The input.
	 * @throws NullPointerException If no name or type were specified.
	 * @since 2019/05/24
	 */
	private void __invokeAssembly(MethodName __name,
		MethodDescriptor __type, JavaStackResult.Output __out,
		JavaStackResult.Input... __in)
		throws NullPointerException
	{
		if (__name == null || __type == null)
			throw new NullPointerException("NARG");
		
		// Code generator
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// Force exception cancel for these operations
		this.state.canexception = false;
		
		// Depends on the assembly function
		String asmfunc;
		switch ((asmfunc = __name.toString()))
		{
				// Read length of array
			case "arrayLength":
				this.doArrayLength(__in[0], __out);
				break;
				
				// Atomic compare, get and set
			case "atomicCompareGetAndSet":
				codeBuilder.add(
					NativeInstructionType.ATOMIC_COMPARE_GET_AND_SET,
						__in[0].register,
						__out.register,
						__in[1].register,
						__in[2].register,
						0);
				break;
				
				// Atomic decrement and get
			case "atomicDecrementAndGet":
				codeBuilder.add(
					NativeInstructionType.ATOMIC_INT_DECREMENT_AND_GET,
						__out.register,
						__in[0].register,
						0);
				break;
				
				// Atomic increment
			case "atomicIncrement":
				codeBuilder.add(
					NativeInstructionType.ATOMIC_INT_INCREMENT,
						__in[0].register,
						0);
				break;
				
				// Breakpoint (with current line)
			case "breakpoint":
				codeBuilder.addBreakpoint(this.state.line & 0x7FFF,
					String.format("%s:%s %s(%s:%d)",
						this.state.methodname,
						this.state.methodtype,
						this.state.classname.toRuntimeString(),
						this.sourceFile,
						this.state.line));
				break;
				
				// Ping (with current line)
			case "ping":
				codeBuilder.addPing(this.state.line & 0x7FFF,
					String.format("%s:%s %s(%s:%d)",
						this.state.methodname,
						this.state.methodtype,
						this.state.classname.toRuntimeString(),
						this.sourceFile,
						this.state.line));
				break;
				
				// Long/Double bits
			case "doubleToRawLongBits":
			case "longBitsToDouble":
				if (__in[0].register != __out.register)
				{
					int a = __in[0].register,
						b = __out.register;
					
					codeBuilder.addCopy(a, b);
					codeBuilder.addCopy(a + 1, b + 1);
				}
				break;
				
				// Exception handling
			case "exceptionHandle":
				// This generates no actual codes to check the exception,
				// it just makes the exception check run so that they are
				// checked
				this.state.canexception = true;
				break;
				
				// Integer/Float bits
			case "floatToRawIntBits":
			case "intBitsToFloat":
				if (__in[0].register != __out.register)
					codeBuilder.addCopy(__in[0].register, __out.register);
				break;
				
				// object -> pointer
			case "objectToPointer":
			case "objectToPointerWide":
				if (__in[0].register != __out.register)
					codeBuilder.addCopy(__in[0].register, __out.register);
				break;
				
				// Invoke method (possibly return a value)
			case "invoke":
			case "invokeV":
			case "invokeVL":
				{
					// Invoked methods can thrown an exception, so do
					// checks! Otherwise the behavior we expect might not
					// happen
					this.state.canexception = true;
					
					// Build the register List
					List<Integer> args = new ArrayList<>();
					int n = __in.length;
					for (int i = 2; i < n; i++)
						args.add(__in[i].register);
					
					// Before we invoke we need to set the next pool so
					// execution is correct!
					codeBuilder.addCopy(__in[1].register,
						NativeCode.NEXT_POOL_REGISTER);
					
					// Invoke pointer with arguments
					codeBuilder.add(NativeInstructionType.INVOKE,
						__in[0].register, new RegisterList(args));
					
					// Copy return value?
					switch (asmfunc)
					{
						case "invokeV":
							codeBuilder.addCopy(NativeCode.RETURN_REGISTER,
									__out.register);
							break;
							
						case "invokeVL":
							codeBuilder.addCopy(NativeCode.RETURN_REGISTER,
									__out.register);
							codeBuilder.addCopy(NativeCode.RETURN_REGISTER + 1,
								__out.register + 1);
							break;
					}
				}
				break;
				
				// Double/Long pack, low + high
			case "doublePack":
			case "longPack":
				if (__in[0].register != __out.register)
					codeBuilder.addCopy(__in[0].register, __out.register);
				if (__in[1].register != __out.register + 1)
					codeBuilder.addCopy(__in[1].register, __out.register + 1);
				break;
				
				// Long unpack high
			case "longUnpackHigh":
				if (__in[0].register + 1 != __out.register)
					codeBuilder.addCopy(__in[0].register + 1, __out.register);
				break;
			
				// Long unpack low
			case "longUnpackLow":
				if (__in[0].register != __out.register)
					codeBuilder.addCopy(__in[0].register, __out.register);
				break;
				
				// Read from memory handle
			case "memHandleReadFloat":
			case "memHandleReadInt":
			case "memHandleReadObject":
				codeBuilder.addMemHandleAccess(DataType.INTEGER, true,
					IntValueRegister.of(__out.register),
					MemHandleRegister.of(__in[0].register),
					IntValueRegister.of(__in[1].register));
				break; 
				
				// Write to memory handle
			case "memHandleWriteFloat":
			case "memHandleWriteInt":
			case "memHandleWriteObject":
				codeBuilder.addMemHandleAccess(DataType.INTEGER, false,
					IntValueRegister.of(__in[2].register),
					MemHandleRegister.of(__in[0].register),
					IntValueRegister.of(__in[1].register));
				break; 
				
				// Read byte memory
			case "memReadByte":
				codeBuilder.addMemoryAccess(DataType.BYTE, true,
					IntValueRegister.of(__out.register),
					WideRegister.of(__in[0].register, __in[0].register + 1),
					IntValueRegister.of(__in[1].register));
				break;
				
				// Read short memory
			case "memReadCharacter":
			case "memReadShort":
				codeBuilder.addMemoryAccess(DataType.SHORT, true,
					IntValueRegister.of(__out.register),
					WideRegister.of(__in[0].register, __in[0].register + 1),
					IntValueRegister.of(__in[1].register));
				break;
				
				// Read int memory
			case "memReadFloat":
			case "memReadInt":
			case "memReadObject":
				codeBuilder.addMemoryAccess(DataType.INTEGER, true,
					IntValueRegister.of(__out.register),
					WideRegister.of(__in[0].register, __in[0].register + 1),
					IntValueRegister.of(__in[1].register));
				break;
				
				// Read long memory
			case "memReadDouble":
			case "memReadLong":
				codeBuilder.addMemoryAccess(DataType.LONG, true,
					WideRegister.of(__out.register, __out.register + 1),
					WideRegister.of(__in[0].register, __in[0].register + 1),
					IntValueRegister.of(__in[1].register));
				break;
				
				// Write byte memory
			case "memWriteByte":
				codeBuilder.addMemoryAccess(DataType.BYTE, false,
					IntValueRegister.of(__in[2].register),
					WideRegister.of(__in[0].register, __in[0].register + 1),
					IntValueRegister.of(__in[1].register));
				break;
				
				// Write short memory
			case "memWriteCharacter":
			case "memWriteShort":
				codeBuilder.addMemoryAccess(DataType.SHORT, false,
					IntValueRegister.of(__in[2].register),
					WideRegister.of(__in[0].register, __in[0].register + 1),
					IntValueRegister.of(__in[1].register));
				break;
				
				// Write int memory
			case "memWriteFloat":
			case "memWriteInt":
			case "memWriteObject":
				codeBuilder.addMemoryAccess(DataType.INTEGER, false,
					IntValueRegister.of(__in[2].register),
					WideRegister.of(__in[0].register, __in[0].register + 1),
					IntValueRegister.of(__in[1].register));
				break;
				
				// Write long memory
			case "memWriteDouble":
			case "memWriteLong":
				codeBuilder.addMemoryAccess(DataType.LONG, false,
					WideRegister.of(__in[2].register, __in[2].register + 1),
					WideRegister.of(__in[0].register, __in[0].register + 1),
					IntValueRegister.of(__in[1].register));
				break;
			
			// object -> pointer, with ref clear
			case "objectToPointerRefQueue":
				// Push references
				this.__refPush();
				
				// Do the copy
				if (__in[0].register != __out.register)
					codeBuilder.addCopy(__in[0].register, __out.register);
				
				// Clear references
				this.__refClear();
				break;
			
				// pointer -> object (and variants)
			case "pointerToObject":
			case "pointerToTypeBracket":
				if (__in[0].register != __out.register)
					codeBuilder.addCopy(__in[0].register, __out.register);
				
				// The returned object is electable for reference
				// counting so we need to count it up otherwise it will
				// be just freed (this is just a plain copy)
				this.__refCount(__out.register);
				break;
				
				// Reference count up
			case "refCount":
				this.__refCount(__in[0].register);
				break;
				
				// Reference count down
			case "refUncount":
				this.__refUncount(__in[0].register);
				break;
				
				// Return from frame
			case "returnFrame":
				// This may be a variant which returns multiple values
				switch (__type.toString())
				{
					case "(II)V":
						codeBuilder.addCopy(__in[0].register,
							NativeCode.RETURN_REGISTER);
						codeBuilder.addCopy(__in[1].register,
							NativeCode.RETURN_REGISTER + 1);
						break;
						
					case "(I)V":
						codeBuilder.addCopy(__in[0].register,
							NativeCode.RETURN_REGISTER);
						break;
				}
				
				// Always return at the end
				this.__generateReturn();
				break;
				
				// Get the exception register
			case "specialGetExceptionRegister":
				codeBuilder.addCopy(NativeCode.EXCEPTION_REGISTER,
					__out.register);
				break;
				
				// Gets the pool register
			case "specialGetPoolRegister":
				codeBuilder.addCopy(NativeCode.POOL_REGISTER,
					__out.register);
				break;
				
				// Read return register
			case "specialGetReturnRegister":
			case "specialGetReturnHighRegister":
				codeBuilder.addCopy(NativeCode.RETURN_REGISTER,
					__out.register);
				break;
				
				// Read return register (low value)
			case "specialGetReturnLowRegister":
				codeBuilder.addCopy(NativeCode.RETURN_REGISTER + 1,
					__out.register);
				break;
				
				// Get static field register
			case "specialGetStaticFieldRegister":
				codeBuilder.addCopy(NativeCode.STATIC_FIELD_REGISTER,
					__out.register);
				break;
				
				// Get thread register
			case "specialGetThreadRegister":
				codeBuilder.addCopy(NativeCode.THREAD_REGISTER,
					__out.register);
				break;
				
				// Set the exception register
			case "specialSetExceptionRegister":
				codeBuilder.addCopy(__in[0].register,
					NativeCode.EXCEPTION_REGISTER);
				break;
				
				// Set pool register
			case "specialSetPoolRegister":
				codeBuilder.addCopy(__in[0].register,
					NativeCode.POOL_REGISTER);
				break;
				
				// Set static field register
			case "specialSetStaticFieldRegister":
				codeBuilder.addCopy(__in[0].register,
					NativeCode.STATIC_FIELD_REGISTER);
				break;
				
				// Set thread register
			case "specialSetThreadRegister":
				codeBuilder.addCopy(__in[0].register,
					NativeCode.THREAD_REGISTER);
				break;
			
			default:
				throw Debugging.oops(asmfunc);
		}
	}
	
	/**
	 * Invokes the given helper function.
	 * 
	 * @param __func The function to execute.
	 * @param __r The values to use.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/28
	 */
	private void __invokeHelper(HelperFunction __func, Register... __r)
		throws NullPointerException
	{
		if (__func == null || __r == null)
			throw new NullPointerException("NARG");
		
		// Call the given static method instead
		this.__invokeStatic(new InvokedMethod(InvokeType.SYSTEM,
			__func.inClass, __func.member.name(),
			__func.member.type()), new RegisterList(__r));
		
		// Check if the invocation generated an exception
		this.codebuilder.addIfNonZero(NativeCode.EXCEPTION_REGISTER,
			this.__labelException());
	}
	
	/**
	 * Invokes instance method, doing the needed pool loading and all the
	 * complicated stuff in a simple point of code.
	 *
	 * @param __invoked The method being invoked.
	 * @param __args The arguments to the call.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/05/24
	 */
	private void __invokeInstance(InvokedMethod __invoked, RegisterList __args)
		throws NullPointerException
	{
		if (__invoked == null || __args == null)
			throw new NullPointerException("NARG");
		
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// Is the invocation special?
		boolean isSpecial = (__invoked.type == InvokeType.SPECIAL);
		
		// The instance handle, used for any instance relative invocations
		MemHandleRegister instance = MemHandleRegister.of(__args.get(0));
		
		// Need volatiles to work with
		VolatileRegisterStack volatiles = this.volatiles;
		try (Volatile<TypedRegister<TypeBracketPointer>> targetClass =
				volatiles.getTyped(TypeBracketPointer.class);
			 Volatile<TypedRegister<InvokeXTable>> vTable =
				volatiles.getTyped(InvokeXTable.class))
		{
			// Use the exactly specified method if:
			//  * It is a special invocation
			//  * It is an initializer, we want to call that exact one
			//  * The target class is the same class of the current class
			//    being processed (private method)
			boolean exactSpecial = __invoked.handle.name()
				.isInstanceInitializer() ||
				__invoked.handle.outerClass().equals(this.state.classname);
			if (isSpecial && exactSpecial)
				this.__loadClassInfo(__invoked.handle.outerClass(),
					targetClass.register);
			
			// Load the current class of the instance
			else
			{
				// Load ClassInfo for the instance
				this.__invokeHelper(HelperFunction.OBJECT_TYPE_BRACKET,
					instance);
				
				// Move this over
				codeBuilder.addCopy(MemHandleRegister.RETURN,
					targetClass.register);
			}
			
			// Otherwise, we will be calling a super method so we need to
			// load the super class of our current class. We would then be
			// using its VTable accordingly
			if (isSpecial && !exactSpecial)
				try (Volatile<IntValueRegister> superProp =
					volatiles.getIntValue())
				{
					// Get the super class of the instance
					codeBuilder.addIntegerConst(ClassProperty.TYPEBRACKET_SUPER,
						superProp.register);
					this.__invokeHelper(HelperFunction.TYPE_GET_PROPERTY,
						targetClass.register, superProp.register);
					
					// Move over
					codeBuilder.addCopy(MemHandleRegister.RETURN,
						targetClass.register);
				}
			
			// Get the VTable for the class
			try (Volatile<IntValueRegister> vTableProp =
					volatiles.getIntValue())
			{
				codeBuilder.addIntegerConst(ClassProperty.MEMHANDLE_VTABLE,
					vTableProp.register);
				this.__invokeHelper(HelperFunction.TYPE_GET_PROPERTY,
					targetClass.register, vTableProp.register);
					
				// Move over
				codeBuilder.addCopy(MemHandleRegister.RETURN,
					vTable.register);
			}
			
			// Perform standardized XTable invocation with the XTable we
			// obtained, since we do not want automatic getting
			this.__invokeXTable(vTable.register, __invoked, __args);
		}
	}
	
	/**
	 * Invokes an interface method from a given instance.
	 *
	 * @param __invokedMethod The method being invoked.
	 * @param __jType The type that the instance is.
	 * @param __args The arguments to the call.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/31
	 */
	private void __invokeInterface(InvokedMethod __invokedMethod,
		JavaType __jType, RegisterList __args)
		throws NullPointerException
	{
		if (__invokedMethod == null || __jType == null || __args == null)
			throw new NullPointerException("NARG");
		
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// The instance object
		MemHandleRegister instance = MemHandleRegister.of(__args.get(0));
		
		// This is jumped to when we have our XTable and we do not need
		// to perform a linear scan for the right interface
		NativeCodeLabel obtained = new NativeCodeLabel("obtained",
			this._refclunk++);
		
		// Need temporary variables to store values in
		VolatileRegisterStack volatiles = this.volatiles;
		try (Volatile<TypedRegister<InvokeXTable>> xTable =
				volatiles.getTyped(InvokeXTable.class))
		{
			// Need the I2X Table to find methods and such
			try (Volatile<MemHandleRegister> itxTable =
					volatiles.getMemHandle();
				Volatile<IntValueRegister> mask =
					volatiles.getIntValue())
			{
				// We need information on the class to make this work properly
				try (Volatile<MemHandleRegister> classInfo =
						volatiles.getMemHandle())
				{
					// Load the class information from the given instance
					this.__invokeHelper(HelperFunction.OBJECT_TYPE_BRACKET,
						instance);
					codeBuilder.addCopy(MemHandleRegister.RETURN,
						classInfo.register);
						
					// Obtain the I2XTable, which is used to quickly map to a
					// hashed index and if that fails it will linearly scan for
					// the interface.
					try (Volatile<IntValueRegister> itxProp =
						volatiles.getIntValue())
					{
						// Need the constant for the table
						codeBuilder.addIntegerConst(
							ClassProperty.MEMHANDLE_I2XTABLE,
							itxProp.register);
						
						// Load the I2X Table for the class
						this.__invokeHelper(HelperFunction.TYPE_GET_PROPERTY,
							classInfo.register, itxProp.register);
						codeBuilder.addCopy(MemHandleRegister.RETURN,
							itxTable.register);
					}
					
					// Load the I2XTable base potential mask from the class
					// information, this will be used to determine if we can
					// quickly index an interface table via a hashcode. This
					// must _always_ be a power of two minus one!
					codeBuilder.addIntegerConst(ClassProperty.MASK_I2XTABLE,
						mask.register);
					
					// Load the mask
					this.__invokeHelper(HelperFunction.TYPE_GET_PROPERTY,
						classInfo.register, mask.register);
					codeBuilder.addCopy(IntValueRegister.RETURN,
						mask.register);
				}
				
				// Clear this register since it is expected to not change
				codeBuilder.addIntegerConst(0,
					xTable.register.asIntValue());
				
				// Perform hash-based lookup, use that if successful
				this.__invokeInterfaceGenHash(__invokedMethod.outerClass(),
					xTable.register, itxTable.register, mask.register);
				codeBuilder.addIfNotNull(xTable.register.asMemHandle(),
					obtained);
				
				// Otherwise perform linear scan to find the given class
				this.__invokeInterfaceGenLinear(__invokedMethod.outerClass(),
					xTable.register, itxTable.register, mask.register);
			}
			
			// We did a hash lookup for the interface, so this is not required
			codeBuilder.label(obtained);
			
			// Once we have the XTable, we can perform an XTable invoke like
			// how other static and virtual methods are invoked.
			this.__invokeXTable(xTable.register, __invokedMethod, __args);
		}
	}
	
	/**
	 * Invoke interface generation: Linear scan.
	 * 
	 * @param __outerClass The outer class.
	 * @param __xTable The target XTable.
	 * @param __itxTable The input I2XTable.
	 * @param __mask The mask used for entry lookup.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/02/09
	 */
	private void __invokeInterfaceGenLinear(ClassName __outerClass,
		TypedRegister<InvokeXTable> __xTable, MemHandleRegister __itxTable,
		IntValueRegister __mask)
		throws NullPointerException
	{
		if (__outerClass == null || __xTable == null || __itxTable == null ||
			__mask == null)
			throw new NullPointerException("NARG");
		
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// Load the class information for the target class to call
		// into, we need this to map values
		codeBuilder.addBreakpoint(0x7CAA,
			"Interface " + __outerClass);
		if (false)
			throw Debugging.todo();
		
		// Start from the I2XTable base potential mask + 1 so that we
		// are at the very end of the base potential table
		codeBuilder.addBreakpoint(0x7CAB,
			"Interface " + __outerClass);
		if (false)
			throw Debugging.todo();
		
		// Scan through the I2XTable and find the class that matches
		// the one we want to execute for, we should get an XTable
		// from this
		codeBuilder.addBreakpoint(0x7CAC,
			"Interface " + __outerClass);
		if (false)
			throw Debugging.todo();
	}
	
	/**
	 * Invoke interface generation: Hash based lookup.
	 * 
	 * @param __outerClass The outer class.
	 * @param __xTable The target XTable.
	 * @param __itxTable The input I2XTable.
	 * @param __mask The mask used for entry lookup.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/02/09
	 */
	private void __invokeInterfaceGenHash(ClassName __outerClass,
		TypedRegister<InvokeXTable> __xTable, MemHandleRegister __itxTable,
		IntValueRegister __mask)
		throws NullPointerException
	{
		if (__outerClass == null || __xTable == null || __itxTable == null ||
			__mask == null)
			throw new NullPointerException("NARG");
		
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// Perform hash based lookup
		VolatileRegisterStack volatiles = this.volatiles;
		try (Volatile<IntValueRegister> trueOffset =
				volatiles.getIntValue())
		{
			// Determine the spot to look into the table with
			try (Volatile<TypedRegister<ClassNameHash>> nameHash =
				volatiles.getTyped(ClassNameHash.class))
			{
				// Obtain the interface class target hash from the constant
				// pool, although many different classes will
				codeBuilder.addPoolLoad(new ClassNameHash(__outerClass),
					nameHash.register);
				
				// AND the hash with the I2XTable base potential mask, this
				// will give us the index into the table
				codeBuilder.addMathReg(StackJavaType.INTEGER, 
					MathType.AND, nameHash.register.asIntValue(), __mask, 
					trueOffset.register);
			}
			
			// The I2XTable consists of multiple cells with [class+xtable]
			codeBuilder.addMathConst(StackJavaType.INTEGER,
				MathType.MUL, trueOffset.register, 8,
				trueOffset.register);
			
			// Determine the true physical offset
			try (Volatile<IntValueRegister> arrayBase =
					volatiles.getIntValue())
			{
				// Load the array base, used for any list type
				this.__invokeSysCallV(
					SystemCallIndex.ARRAY_ALLOCATION_BASE,
					arrayBase.register);
				
				// Shift the offset here accordingly
				codeBuilder.addMathReg(StackJavaType.INTEGER,
					MathType.ADD,
					trueOffset.register, arrayBase.register,
					trueOffset.register);
			}
			
			// Used when the check fails, to jump head and do nothing
			NativeCodeLabel checkFail = new NativeCodeLabel("checkFail",
				this._refclunk++);
		
			// Need to check against a value to determine if it has a valid
			// interface defined for it
			try (Volatile<MemHandleRegister> checkCl =
					volatiles.getMemHandle())
			{
				// Read the value here
				codeBuilder.addMemHandleAccess(DataType.INTEGER,
					true, checkCl.register.asIntValue(),
					__itxTable, trueOffset.register);
					
				// Zero indicates collision and a scan must be performed
				codeBuilder.addIfZero(checkCl.register.asIntValue(),
					checkFail);
			}
			
			// The XTable is just one entry off!
			codeBuilder.addMathConst(StackJavaType.INTEGER,
				MathType.ADD, trueOffset.register, 4,
				trueOffset.register);
				
			// Read in the XTable
			codeBuilder.addMemHandleAccess(DataType.INTEGER,
				true, __xTable.asIntValue(),
				__itxTable, trueOffset.register);
			
			// Failure point
			codeBuilder.label(checkFail);
		}
	}
	
	/**
	 * Allocates a new object.
	 *
	 * @param __cl The class to create.
	 * @param __out The output register.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/05/24
	 */
	private void __invokeNew(ClassName __cl, int __out)
		throws NullPointerException
	{
		this.__invokeNew(__cl, MemHandleRegister.of(__out));
	}
	
	/**
	 * Allocates a new object.
	 *
	 * @param __cl The class to create.
	 * @param __out The output register.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/29
	 */
	private void __invokeNew(ClassName __cl, MemHandleRegister __out)
		throws NullPointerException
	{
		if (__cl == null || __out == null)
			throw new NullPointerException("NARG");
		
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// We need a volatile for the class to allocate!
		try (Volatile<TypedRegister<TypeBracketPointer>> classInfo =
			this.volatiles.getTyped(TypeBracketPointer.class))
		{
			// Load class data
			this.__loadClassInfo(__cl, classInfo.register);
			
			// Call allocator
			this.__invokeHelper(HelperFunction.NEW_INSTANCE,
				classInfo.register);
			
			// Handle result
			codeBuilder.<MemHandleRegister>addCopy(
				MemHandleRegister.RETURN, __out);
		}
	}
	
	/**
	 * Invokes static method, doing the needed pool loading and all the
	 * complicated stuff in a simple point of code.
	 *
	 * @param __invoked The method being invoked.
	 * @param __args The arguments to the call.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/05/24
	 */
	private void __invokeStatic(InvokedMethod __invoked, int... __args)
		throws NullPointerException
	{
		this.__invokeStatic(__invoked, new RegisterList(__args));
	}
	
	/**
	 * Invokes static method, doing the needed pool loading and all the
	 * complicated stuff in a simple point of code.
	 *
	 * @param __invoked The method being invoked.
	 * @param __args The arguments to the call.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/05/24
	 */
	private void __invokeStatic(InvokedMethod __invoked, RegisterList __args)
		throws NullPointerException
	{
		if (__invoked == null || __args == null)
			throw new NullPointerException("NARG");
		
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// Is this a system static call, where we need to
		boolean isSystem = (__invoked.type == InvokeType.SYSTEM);
		if (isSystem)
			__invoked = new InvokedMethod(InvokeType.STATIC, __invoked.handle);
		
		// Need volatiles
		VolatileRegisterStack volatiles = this.volatiles;
		try (Volatile<TypedRegister<MemHandleRegister>> oldEx = (isSystem ?
			volatiles.<MemHandleRegister>getTyped(MemHandleRegister.class) :
			null))
		{			
			// Create a backup of the exception register (system mode)
			if (isSystem)
			{
				codeBuilder.addCopy(MemHandleRegister.EXCEPTION,
					oldEx.register);
				codeBuilder.addCopy(MemHandleRegister.NULL,
					MemHandleRegister.EXCEPTION);
			}
			
			// Invoke using the standard XTable method, if this is a system
			// invocation force it to be static
			this.__invokeXTable(__invoked, __args);
			
			// If the system invoke (which could be from special code) threw an
			// exception just replace our current exception with that one since
			// it definitely would be worse!
			if (isSystem)
			{
				// This is just a jump over restoring the old exception one
				// so that the system one takes priority
				NativeCodeLabel doubleFault = new NativeCodeLabel(
					"doubleFault", this._refclunk++);
				codeBuilder.addIfNonZero(NativeCode.EXCEPTION_REGISTER,
					doubleFault);
				
				// Restore our old exception register
				codeBuilder.addCopy(oldEx.register,
					MemHandleRegister.EXCEPTION);
				
				// Target point for double fault
				codeBuilder.label(doubleFault);
			}
		}
	}
	
	/**
	 * Invokes a helped system call.
	 * 
	 * @param __r The reference to call.
	 * @param __out The output register.
	 * @param __in The input arguments.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/29
	 */
	public final void __invokeSysCallHelped(MethodReference __r,
		JavaStackResult.Output __out, JavaStackResult.Input... __in)
		throws NullPointerException
	{
		if (__r == null || __in == null)
			throw new NullPointerException("NARG");
		
		// Determine the system call index to call into
		int id;
		switch (__r.memberName().toString())
		{
			case "arrayAllocationBase":
				id = SystemCallIndex.ARRAY_ALLOCATION_BASE;
				break;
			
			case "callStackHeight":
				id = SystemCallIndex.CALL_STACK_HEIGHT;
				break;
			
			case "callStackItem":
				id = SystemCallIndex.CALL_STACK_ITEM;
				break;
			
			case "errorGet":
				id = SystemCallIndex.ERROR_GET;
				break;
			
			case "errorSet":
				id = SystemCallIndex.ERROR_SET;
				break;
			
			case "memHandleNew":
				id = SystemCallIndex.MEM_HANDLE_NEW;
				break;
			
			case "memHandleMove":
				id = SystemCallIndex.MEM_HANDLE_MOVE;
				break;
			
			case "pdFlush":
				id = SystemCallIndex.PD_FLUSH;
				break;
			
			case "pdOfStdErr":
				id = SystemCallIndex.PD_OF_STDERR;
				break;
				
			case "pdOfStdIn":
				id = SystemCallIndex.PD_OF_STDIN;
				break;
				
			case "pdOfStdOut": 
				id = SystemCallIndex.PD_OF_STDOUT;
				break;
			
			case "pdWriteByte":
				id = SystemCallIndex.PD_WRITE_BYTE;
				break;
			
			case "runtimeVmAttribute":
				id = SystemCallIndex.RUNTIME_VM_ATTRIBUTE;
				break;
			
			case "staticVmAttributes":
				id = SystemCallIndex.STATIC_VM_ATTRIBUTES;
				break;
			
			case "verbose":
				id = SystemCallIndex.VERBOSE;
				break;
				
				// {@squirreljme.error JC4r The specified system call method
				// is not known. (The method)}
			default:
				throw new IllegalArgumentException("JC4r " + __r); 
		}
		
		// We definitely need volatiles for the index and other parts!
		NativeCodeBuilder codeBuilder = this.codebuilder;
		try (Volatile<IntValueRegister> idReg = this.volatiles.getIntValue())
		{
			// Load integer constant
			codeBuilder.addIntegerConst(id, idReg.register);
			
			// Perform the call
			codeBuilder.addSysCall(idReg.register, new RegisterList(__in));
				
			// Is there a return value?
			if (__out != null)
			{
				codeBuilder.addCopy(IntValueRegister.RETURN,
					IntValueRegister.of(__out.register));
				
				// Wide value?
				if (__out.type.isWide())
					codeBuilder.addCopy(IntValueRegister.RETURN_TWO,
						IntValueRegister.of(__out.register + 1));
			}
			
			// The error get/set are always successful, otherwise bad things
			// will happen, so check to see if an error occurred!
			if (id != SystemCallIndex.ERROR_GET &&
				id != SystemCallIndex.ERROR_SET)
				try (Volatile<IntValueRegister> getErrId =
					this.volatiles.getIntValue())
				{
					// Load the get error index in
					codeBuilder.addIntegerConst(SystemCallIndex.ERROR_GET,
						getErrId.register);
					
					// Perform the call
					codeBuilder.addSysCall(getErrId.register,
						idReg.register);
					
					// Did we fail the system call?
					NativeCodeLabel fail = this.__labelMakeException(
						"cc/squirreljme/jvm/mle/exceptions/MLECallError");
					codeBuilder.addIfNonZero(IntValueRegister.RETURN, fail);
				}
		}
	}
	
	/**
	 * Invokes the given pure system call that returns an integer value.
	 * 
	 * @param __sysCall The {@link SystemCallIndex}.
	 * @param __rv The return value.
	 * @param __regs The registers to call.
	 * @throws IllegalArgumentException If the system call is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/28
	 */
	private void __invokeSysCallV(int __sysCall, IntValueRegister __rv,
		Register... __regs)
		throws IllegalArgumentException, NullPointerException
	{
		if (__rv == null || __regs == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error JC4q The system call is not valid. (The system
		// call ID)}
		if (__sysCall < 0 || __sysCall >= SystemCallIndex.NUM_SYSCALLS)
			throw new IllegalArgumentException("JC4q " + __sysCall);
		
		// Invoked methods can thrown an exception, so do
		// checks! Otherwise the behavior we expect might not
		// happen
		this.state.canexception = true;
		
		// We need volatiles
		NativeCodeBuilder codeBuilder = this.codebuilder;
		try (Volatile<IntValueRegister> callId = this.volatiles.getIntValue())
		{
			// Load integer constant
			codeBuilder.addIntegerConst(__sysCall, callId.register);
			
			// Perform the call
			codeBuilder.addSysCall(callId.register, __regs);
			
			// Copy return value
			codeBuilder.addCopy(IntValueRegister.RETURN, __rv);
		}
	}
	
	/**
	 * Invoke a method from it's XTable.
	 * 
	 * @param __method The method to invoke.
	 * @param __args The arguments to the call.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/30
	 */
	private void __invokeXTable(InvokedMethod __method, RegisterList __args)
		throws NullPointerException
	{
		if (__method == null || __args == null)
			throw new NullPointerException("NARG");
		
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// Get the VTable for the target method
		VolatileRegisterStack volatiles = this.volatiles;
		try (Volatile<TypedRegister<InvokeXTable>> xTable =
			volatiles.getTyped(InvokeXTable.class))
		{
			// Determine and load the appropriate XTable
			codeBuilder.addPoolLoad(new InvokeXTable(__method.type,
				__method.handle.outerClass()), xTable.register);
			
			// Forward to the other method, which has the known XTable
			this.__invokeXTable(xTable.register, __method, __args);
		}
	}
	
	/**
	 * Invoke a method from it's XTable, which was pre-determined.
	 * 
	 * @param __xTable The XTable this is being invoked with.
	 * @param __method The method to invoke.
	 * @param __args The arguments to the call.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/30
	 */
	private void __invokeXTable(TypedRegister<InvokeXTable> __xTable,
		InvokedMethod __method, RegisterList __args)
		throws NullPointerException
	{
		if (__xTable == null || __method == null || __args == null)
			throw new NullPointerException("NARG");
		
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// Need room for the method and pool references
		VolatileRegisterStack volatiles = this.volatiles;
		try (Volatile<ExecutablePointer> methodAddr =
				volatiles.getExecutablePointer();
			Volatile<RuntimePoolPointer> poolRef =
				volatiles.getRuntimePoolPointer())
		{
			// Read the method and pool address accordingly
			try (Volatile<TypedRegister<InvokedMethod>> methodIndex =
				volatiles.getTyped(InvokedMethod.class))
			{
				// Load the index to read from
				codeBuilder.addPoolLoad(__method,
					methodIndex.register);
					
				// Load the method pointer
				codeBuilder.addMemHandleAccess(DataType.INTEGER, true,
					methodAddr.register.asIntValue(),
					__xTable.asMemHandle(), methodIndex.register.asIntValue());
				
				// The pool pointer will be in the next position, so add to
				// get there
				codeBuilder.addMathConst(StackJavaType.INTEGER, MathType.ADD,
					methodIndex.register.asIntValue(),
					DataType.INTEGER.size(),
					methodIndex.register.asIntValue());
				
				// Load the pool pointer
				codeBuilder.addMemHandleAccess(DataType.INTEGER, true,
					poolRef.register.asIntValue(),
					__xTable.asMemHandle(), methodIndex.register.asIntValue());
			}
			
			// Invoke the method
			codeBuilder.addInvokePoolAndPointer(
				methodAddr.register, poolRef.register, __args);
		}
	}
	
	/**
	 * Basic check if the instance is of the given class.
	 *
	 * @param __instance The register to check.
	 * @param __jType The type used on instance.
	 * @param __cl The class that this will be cast to.
	 * @param __result The result if this is an instance or not.
	 * @throws NullPointerException On null arguments, except for
	 * {@code __jType}.
	 * @since 2021/01/31
	 */
	private void __isInstance(MemHandleRegister __instance, JavaType __jType,
		ClassName __cl, IntValueRegister __result)
		throws NullPointerException
	{
		if (__instance == null || __cl == null || __result == null)
			throw new NullPointerException("NARG");
		
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// If the target class is object, just do nothing because it is
		// pointless to check casts to object.
		// Additionally if the two types are the same, do nothing
		if (__cl.isObjectClass() || __cl.equals(__jType.className()))
		{
			// Whatever is calling this is wanting a result, so we give one
			codeBuilder.addIntegerConst(1, __result);
			
			// We do not need to generate anymore code however
			return;
		}
		
		// Jumping point if we are quick compatible
		NativeCodeLabel didQuickCompat = null;
		
		// If we are quick compatible, then we avoid the instance check.
		// However if we are coming from Object, we need to check everything
		// so it would be rather pointless to use quick cast checks when it
		// will always fail
		boolean possibleQuick = (__jType.className() != null &&
			!__jType.className().isObjectClass());
		if (possibleQuick)
			didQuickCompat = new NativeCodeLabel(
				"isQuickCompat", this._refclunk++);
		
		// Perform a cached quick cast check
		VolatileRegisterStack volatiles = this.volatiles;
		try (Volatile<TypedRegister<QuickCastCheck>> quickCast =
			 (possibleQuick ? volatiles.getTyped(QuickCastCheck.class) : null))
		{
			// If quick casting is possible, then
			if (possibleQuick)
				try (Volatile<IntValueRegister> arrayBase =
						volatiles.getIntValue();
					Volatile<IntValueRegister> quickVal =
						volatiles.getIntValue())
				{
					// Get the base for the array, since we will be reading a
					// value from the quick cast object.
					this.__invokeSysCallV(
						SystemCallIndex.ARRAY_ALLOCATION_BASE,
						arrayBase.register);
						
					// Get the existing quick cast cache
					codeBuilder.addPoolLoad(new QuickCastCheck(
						__jType.className(), __cl), quickCast.register);
					
					// Read the value stored here
					codeBuilder.addMemHandleAccess(DataType.INTEGER,
						true, quickVal.register,
						quickCast.register.asMemHandle(), arrayBase.register);
					
					// If the value is zero (not cached), then an instance
					// check will be performed
					NativeCodeLabel doInstanceCheck = new NativeCodeLabel(
						"doInstanceCheck", this._refclunk++);
					codeBuilder.addIfZero(quickVal.register, doInstanceCheck);
					
					// Store zero into the result register, if this is
					// positive then it will change
					codeBuilder.addIntegerConst(0, __result);
					
					// We calculated this already and it is known that this
					// will always fail!
					codeBuilder.addIfNegative(quickVal.register,
						didQuickCompat);
					
					// If we are not negative then we are positive, so the
					// check was successful
					codeBuilder.addIntegerConst(1, __result);
					
					// We calculated the quick compatibility, unless the
					// cache returned zero for unknown
					codeBuilder.addGoto(didQuickCompat);
					
					// Will be doing a normal instance check, this only
					// happens if no value was ever cached or it is unknown
					codeBuilder.label(doInstanceCheck);
				}
			
			// Perform the actual instance checking logic
			try (Volatile<TypedRegister<TypeBracketPointer>> classInfo =
				volatiles.getTyped(TypeBracketPointer.class))
			{
				// Load desired target class type
				this.__loadClassInfo(__cl, classInfo.register);
				
				// Call helper class, this will check. Since this will always
				// be a runtime check, we cannot pass the quick check
				this.__invokeHelper(HelperFunction.IS_INSTANCE,
					__instance, classInfo.register);
				
				// Store the result into the output
				codeBuilder.addCopy(IntValueRegister.RETURN, __result);
			}
		}
		
		// If quick compatibility was performed, skip the method based
		// instance check
		if (possibleQuick)
			codeBuilder.label(didQuickCompat);
	}
	
	/**
	 * Creates and stores an exception.
	 *
	 * @return The label to the exception.
	 * @since 2019/04/09
	 */
	private NativeCodeLabel __labelException()
	{
		// Get both states for when an exception is handled (transition) and
		// for where it is not handled (full cleanup)
		ByteCodeState state = this.state;
		JavaStackResult handled = state.stack.doExceptionHandler(),
			nothandled = state.stack.doDestroy(false);
		
		// The byte code handler does not know about exception registers so
		// we need to add this operation in so it is handled correctly
		List<StateOperation> newsop = new ArrayList<>();
		for (StateOperation o : handled.operations())
			newsop.add(o);
		newsop.add(StateOperation.copy(false,
			NativeCode.EXCEPTION_REGISTER, handled.out(0).register));
		
		// Clear the exception register when it is handled, otherwise it
		// will just cause an exception fall off when the register is next
		// checked
		newsop.add(StateOperation.copy(false, NativeCode.ZERO_REGISTER,
			NativeCode.EXCEPTION_REGISTER));
		
		// Setup key
		ExceptionHandlerTransition key = new ExceptionHandlerTransition(
			new StateOperations(newsop), nothandled.enqueue(),
			state.exceptionranges.tableOf(state.addr));
		
		// Try to use an already existing point
		Map<ExceptionHandlerTransition, __EData__> ehtable = this._ehtable;
		__EData__ rv = ehtable.get(key);
		if (rv != null)
			return rv.label;
			
		// Build new data to record this point
		rv = this.__eData(new NativeCodeLabel("exception", ehtable.size()));
		ehtable.put(key, rv);
		
		// This exception should be unique, so we need to process it
		this._ehTableQueue.addLast(
			new __TransitingExceptionHandler__(key, rv));
		
		// Return the created label (where the caller jumps to)
		return rv.label;
	}
	
	/**
	 * Makes a lable which goes from this address to the target address. If
	 * the target address is poisoned then the current stack will be adapted
	 * to the target state through a transition method. Otherwise if the target
	 * is not poisoned it will be a normal jump.
	 *
	 * @param __jt The jump target.
	 * @return The label of the jump.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/12
	 */
	private NativeCodeLabel __labelJava(InstructionJumpTarget __jt)
		throws NullPointerException
	{
		if (__jt == null)
			throw new NullPointerException("NARG");
		
		ByteCodeState state = this.state;
		JavaStackState sourcestack = state.stack;
		
		// If the target stack state matches the current state then no
		// adapting is required at all
		int target = __jt.target();
		JavaStackState targetstack = state.stacks.get(target);
		if (sourcestack.equals(targetstack))
			return new NativeCodeLabel("java", target);
		
		// Do a transition to the target stack
		return this.__labelJavaTransition(sourcestack.
			doTransition(targetstack).operations(), __jt);
	}
	
	/**
	 * Creates the actual label which is used for the state transition.
	 *
	 * @param __sops The operation to use.
	 * @param __jt The jump target in the Java address space.
	 * @return The label for this transition.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/13
	 */
	private NativeCodeLabel __labelJavaTransition(StateOperations __sops,
		InstructionJumpTarget __jt)
		throws NullPointerException
	{
		if (__sops == null || __jt == null)
			throw new NullPointerException("NARG");
		
		// If no operations were generated then just use a normal jump since
		// there is no point in transitioning anyway
		if (__sops.isEmpty())
			return new NativeCodeLabel("java", __jt.target());
		
		// Setup key
		StateOperationsAndTarget key =
			new StateOperationsAndTarget(__sops, __jt);
		
		// Determine if such a transition was already done, since if the
		// transition is exactly the same we do not need to actually do
		// anything
		Map<StateOperationsAndTarget, __EData__> transits = this._transits;
		__EData__ rv = transits.get(key);
		if (rv != null)
			return rv.label;
		
		// Setup transition for later
		ByteCodeState state = this.state;
		rv = this.__eData(new NativeCodeLabel("transit", transits.size()));
		transits.put(key, rv);
		
		// Queue for later
		this._transitQueue.add(new __StateOpTransit__(key, rv));
		
		return rv.label;
	}
	
	/**
	 * Makes a label which creates the given exception then throws that
	 * exception.
	 *
	 * @param __cl The class to create.
	 * @return The label for that target.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/10
	 */
	private NativeCodeLabel __labelMakeException(String __cl)
		throws NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException("NARG");
		
		// Setup key, the label is the target to jump to after the exception
		// has been generated and a throw is performed
		ByteCodeState state = this.state;
		ClassAndLabel key = new ClassAndLabel(new ClassName(__cl),
			this.__labelException());
		
		// Look in the table to see if we made it before
		Map<ClassAndLabel, __EData__> metable = this._metable;
		__EData__ rv = metable.get(key);
		if (rv != null)
			return rv.label;
		
		// Build new data to record this point
		rv = this.__eData(new NativeCodeLabel("makeexception",
			metable.size()));
		metable.put(key, rv);
		
		// Add to queue for final processing
		this._meTableQueue.add(new __MakeException__(key, rv));
		
		// Return the created label (where the caller jumps to)
		return rv.label;
	}
	
	/**
	 * Creates a label to potentially reference clear and jumps to the given
	 * label. This uses the current ref queue table.
	 *
	 * @param __tl The target label.
	 * @return The transition label for reference clearing or {@code __tl}
	 * if there are no references to clear.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/24
	 */
	private NativeCodeLabel __labelRefClearJump(NativeCodeLabel __tl)
		throws NullPointerException
	{
		if (__tl == null)
			throw new NullPointerException("NARG");
		
		return this.__labelRefClearJump(this._lastenqueue, __tl);
	}
	
	/**
	 * Creates a label to potentially reference clear and jumps to the given
	 * label.
	 *
	 * @param __eql The reference queue label.
	 * @param __tl The target label.
	 * @return The transition label for reference clearing or {@code __tl}
	 * if there are no references to clear.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/24
	 */
	private NativeCodeLabel __labelRefClearJump(
		JavaStackEnqueueList __eql, NativeCodeLabel __tl)
		throws NullPointerException
	{
		if (__tl == null)
			throw new NullPointerException("NARG");
		
		// If not clearing anything, just return the target label
		if (__eql == null || __eql.isEmpty())
			return __tl;
		
		// Setup key
		EnqueueAndLabel key = new EnqueueAndLabel(__eql, __tl);
		
		// Did we make this before?
		Map<EnqueueAndLabel, __EData__> refcljumps = this._refcljumps;
		__EData__ rv = refcljumps.get(key);
		if (rv != null)
			return rv.label;
		
		// Setup new record
		ByteCodeState state = this.state;
		rv = this.__eData(new NativeCodeLabel("refclear", refcljumps.size()));
		refcljumps.put(key, rv);
		
		// Add to the processing queue for later handling
		this._refClearJumpQueue.add(new __RefClearJump__(key, rv));
		
		// Use the created label
		return rv.label;
	}
	
	/**
	 * Loads the class information for a class.
	 *
	 * @param __cl The class to load.
	 * @param __r The output register.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/01/19
	 */
	private void __loadClassInfo(String __cl, int __r)
		throws NullPointerException
	{
		this.__loadClassInfo(new ClassName(__cl), __r);
	}
	
	/**
	 * Loads the class information for a class.
	 *
	 * @param __cl The class to load.
	 * @param __r The output register.
	 * @throws NullPointerException On null arguments.
	 * @deprecated Use {@link NearNativeByteCodeHandler#
	 * __loadClassInfo(ClassName, TypedRegister)}. 
	 * @since 2019/12/15
	 */
	@Deprecated
	private void __loadClassInfo(ClassName __cl, int __r)
		throws NullPointerException
	{
		this.__loadClassInfo(__cl, new TypedRegister<TypeBracketPointer>(
			TypeBracketPointer.class, __r));
	}
	
	/**
	 * Loads the class information for a class.
	 *
	 * @param __cl The class to load.
	 * @param __r The output register.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/28
	 */
	private void __loadClassInfo(ClassName __cl,
		TypedRegister<TypeBracketPointer> __r)
		throws NullPointerException
	{
		if (__cl == null || __r == null)
			throw new NullPointerException("NARG");
		
		// Used for loading code
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// Load cached pool entry
		codeBuilder.<TypeBracketPointer>addPoolLoad(
			new TypeBracketPointer(__cl), __r);
		
		// There are classes that must always exist within the VM in an
		// always loaded state, otherwise there can be a very bad infinite
		// recursive trying to load these important classes.
		if (!ClassLoadingAdjustments.isDeferredLoad(
			this.state.classname.toString(), __cl.toString()))
			return;
		
		// Check if the class is initialized or not
		NativeCodeLabel isLoadPt = new NativeCodeLabel("classInfoLoaded",
			this._refclunk++);
		try (Volatile<IntValueRegister> isInitBool =
			this.volatiles.getIntValue())
		{
			// If the class is already initialized do not try loading
			this.__invokeHelper(HelperFunction.IS_CLASS_INIT, __r);
			codeBuilder.addIfNonZero(IntValueRegister.RETURN, isLoadPt);
			
			// Otherwise try to initialize the class
			this.__invokeHelper(HelperFunction.INIT_CLASS, __r);
			
			// End point is here
			codeBuilder.label(isLoadPt);
		}
	}
	
	/**
	 * Loads the Class object for the given class into the given register.
	 *
	 * @param __cl The class to load.
	 * @param __r The register to place it in.
	 * @deprecated Do not use.
	 * @since 2019/04/26
	 */
	@Deprecated
	private void __loadClassObject(ClassName __cl, int __r)
	{
		this.codebuilder.addBreakpoint(0x7E0D,
			"load Class<?>: " + __cl);
		/*
		VolatileRegisterStack volatiles = this.volatiles;
		
		// Load the class info for the class
		int volcdvt = volatiles.getUnmanaged();
		this.__loadClassInfo(__cl, volcdvt);
		
		// Call internal class object loader
		this.__invokeStatic(__invoked, InvokeType.SYSTEM,
			NearNativeByteCodeHandler.JVMFUNC_CLASS,
			"jvmLoadClass", "(I)Ljava/lang/Class;", volcdvt);
		
		// Cleanup
		volatiles.removeUnmanaged(volcdvt);
		
		// Copy return value to the output register
		this.codebuilder.addCopy(NativeCode.RETURN_REGISTER, __r);*/
	}
	
	/**
	 * Performs math.
	 * 
	 * @param __dt The data type.
	 * @param __mt The math to perform.
	 * @param __al A low.
	 * @param __ah A high.
	 * @param __bl B low.
	 * @param __bh B high.
	 * @param __cl C low.
	 * @param __ch C high.
	 * @since 2021/04/03
	 */
	private void __math(StackJavaType __dt, MathType __mt,
		IntValueRegister __al, IntValueRegister __ah,
		IntValueRegister __bl, IntValueRegister __bh,
		IntValueRegister __cl, IntValueRegister __ch)
	{
		NativeCodeBuilder codeBuilder = this.codebuilder;
		
		// Integer math is supported natively
		if (__dt == StackJavaType.INTEGER)
		{
			// Check for division by zero, only the integer type can have this
			// done in code because the long can be handled by the software
			// math library code. Otherwise we would need to add more code to
			// the generator to handle this.
			if (__mt == MathType.DIV || __mt == MathType.REM)
			{
				// Perform divide by zero check
				this.__basicCheckDBZ(__bl);
				
				// We already checked the only valid exceptions, so do not
				// perform later handling!
				this.state.canexception = false;
			}
			
			// Add math operation
			codeBuilder.addMathReg(__dt, __mt,
				__al, __bl,
				__cl);
			
			// Do not perform any software math
			return;
		}
		
		// Other kinds of math are done in software
		// Get the software math class for the type
		ClassName smc = __dt.softwareMathClass();
		
		// The function to call is just the lowercase enum
		String func = __mt.name().toLowerCase();
		
		// Remove the L/G from compare as that is only for float/double
		if (__dt == StackJavaType.LONG && func.startsWith("cmp"))
			func = "cmp";
		
		// Handling wide math?
		boolean isWide = __dt.isWide();
		
		// Determine the call signature
		String type = __mt.signature(__dt);
		RegisterList args;
		switch (__mt)
		{
			case NEG:
			case SIGNX8:
			case SIGNX16:
				if (isWide)
					args = new RegisterList(__al, __ah);
				else
					args = new RegisterList(__al);
				break;
			
			case SHL:
			case SHR:
			case USHR:
				if (isWide)
					args = new RegisterList(__al, __ah, __bl);
				else
					args = new RegisterList(__al, __bl);
				break;
			
			default:
				if (isWide)
					args = new RegisterList(__al, __ah, __bl, __bh);
				else
					args = new RegisterList(__al, __bl);
				break;
		}
		
		// Perform the call
		this.__invokeStatic(new InvokedMethod(InvokeType.SYSTEM,
			smc.toString(), func, type), args);
		
		// Read out return value
		codeBuilder.addCopy(IntValueRegister.RETURN, __cl);
		if (isWide)
			codeBuilder.addCopy(IntValueRegister.RETURN_TWO, __ch);
	}
	
	/**
	 * Enters the monitor.
	 *
	 * @param __enter Is the monitor being entered? 
	 * @param __r The register to enter a monitor for.
	 * @since 2019/04/26
	 */
	private void __monitor(boolean __enter, int __r)
	{
		this.codebuilder.addBreakpoint(0x7E0A,
			"Monitor " + __enter);
		/*// Call helper method
		this.__invokeStatic(__invoked, InvokeType.SYSTEM,
			NearNativeByteCodeHandler.JVMFUNC_CLASS,
			(__enter ? "jvmMonitorEnter" : "jvmMonitorExit"), "(I)V", __r);*/
	}
	
	/**
	 * If anything has been previously pushed then generate code to clear it.
	 *
	 * @since 2019/03/30
	 */
	private void __refClear()
	{
		// Do nothing if nothing has been enqueued
		JavaStackEnqueueList lastenqueue = this._lastenqueue;
		if (lastenqueue == null)
			return;
		
		// No need to clear anymore
		this.__refReset();
		
		// Un-count all of them accordingly
		VolatileRegisterStack volatiles = this.volatiles;
		for (int i = 0, n = lastenqueue.size(); i < n; i++)
		{
			// Get the volatile to clear
			int v = lastenqueue.get(i);
			
			// Uncount this one
			this.__refUncount(v);
			
			// Free it for later usage
			volatiles.removeUnmanaged(v);
		}
	}
	
	/**
	 * Generates code to reference count the given register.
	 *
	 * @param __r The register to reference to count.
	 * @deprecated Use the type safe {@link #__refCount(MemHandleRegister)}. 
	 * @since 2019/04/25
	 */
	@Deprecated
	private void __refCount(int __r)
	{
		this.__refCount(MemHandleRegister.of(__r));
	}
	
	/**
	 * Generates code to reference count up the given register.
	 *
	 * @param __r The register reference to count up.
	 * @since 2019/04/25
	 */
	private void __refCount(MemHandleRegister __r)
		throws NullPointerException
	{
		if (__r == null)
			throw new NullPointerException("NARG");
		
		// If the object is null then it will not be counted, this is skipped
		NativeCodeLabel ncj = new NativeCodeLabel("refnocount",
			this._refclunk++);
		
		// Do not do any counting if this is a NULL reference
		NativeCodeBuilder codebuilder = this.codebuilder;
		codebuilder.addIfZero(__r.register, ncj);
		
		// Count this handle directly up
		codebuilder.addMemHandleCountUp(__r);
		
		// No count is jumped here
		codebuilder.label(ncj);
	}
	
	/**
	 * Generates code to enqueue registers, if there are any. This implicitly
	 * uses the registers from the state.
	 *
	 * @return True if the push list was not empty.
	 * @since 2019/04/10
	 */
	private boolean __refPush()
		throws NullPointerException
	{
		return this.__refPush(this.state.result.enqueue());
	}
	
	/**
	 * Generates code to enqueue registers, if there are any.
	 *
	 * @param __r The registers to push.
	 * @return True if the push list was not empty.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/03/30
	 */
	private boolean __refPush(JavaStackEnqueueList __r)
		throws NullPointerException
	{
		if (__r == null)
			throw new NullPointerException("NARG");
		
		// Nothing to enqueue?
		if (__r.isEmpty())
		{
			this.__refReset();
			return false;
		}
		
		// Place anything that is referenced into volatile registers
		VolatileRegisterStack volatiles = this.volatiles;
		NativeCodeBuilder codebuilder = this.codebuilder;
		
		// Get a volatile register and copy into it, store the volatile
		int n = __r.size();
		int[] use = new int[n];
		for (int i = 0; i < n; i++)
		{
			// Get the volatile register to copy into
			int v = volatiles.getUnmanaged();
			
			// Copy to the volatile
			codebuilder.addCopy(__r.get(i), v);
			
			// Use this volatile register
			use[i] = v;
		}
		
		// We completely lose our original registers and instead use our new
		// set of volatiles for later clearing and removing
		// Note that we keep the same stack offset because it will match
		// exactly as the input (needed for exception handlers), even though
		// the registers might be all over the place.
		this._lastenqueue = new JavaStackEnqueueList(__r.stackstart, use);
		
		// Did enqueue something
		return true;
	}
	
	/**
	 * Resets the reference queue so nothing uses it.
	 *
	 * @since 2019/11/24
	 */
	private void __refReset()
	{
		this._lastenqueue = null;
	}
	
	/**
	 * Generates code to reference uncount the given register.
	 *
	 * @param __r The reference to uncount.
	 * @deprecated Use the type safe {@link #__refUncount(MemHandleRegister)}.  
	 * @since 2019/04/25
	 */
	@Deprecated
	private void __refUncount(int __r)
	{
		this.__refUncount(MemHandleRegister.of(__r));
	}
	
	/**
	 * Generates code to reference uncount the given register.
	 *
	 * @param __r The reference to uncount.
	 * @since 2020/11/28
	 */
	private void __refUncount(MemHandleRegister __r)
		throws NullPointerException
	{
		if (__r == null)
			throw new NullPointerException("NARG");
		
		// If the object is null then it will not be uncounted, this is skipped
		NativeCodeLabel ncj = new NativeCodeLabel("RefNoUnCount",
			this._refclunk++);
		
		// Do not do any un-counting if this is null
		NativeCodeBuilder codeBuilder = this.codebuilder;
		codeBuilder.addIfZero(__r.register, ncj);
		
		// We need a register to determine if we should do garbage collection
		try (Volatile<IntValueRegister> count = this.volatiles.getIntValue())
		{
			// Perform the uncount, store the count value somewhere we can
			// check to ensure that we do need to GC
			codeBuilder.addMemHandleCountDown(__r, count.register); 
			
			// If the count is still positive, we do not GC
			codeBuilder.addIfPositive(count.register, ncj);
			
			// Call garbage collect on object via helper
			this.__invokeHelper(HelperFunction.GC_MEM_HANDLE,
				__r);
			
			// Reset the variable to zero to prevent it from being used again
			codeBuilder.addCopy(MemHandleRegister.NULL, __r);
			
			// Null reference or no GC performed
			codeBuilder.label(ncj);
		}
	}
	
	/**
	 * Uses the given EData and returns the used label.
	 *
	 * @param __ed The data to use.
	 * @return The label.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/25
	 */
	private NativeCodeLabel __useEDataAndGetLabel(__EData__ __ed)
		throws NullPointerException
	{
		if (__ed == null)
			throw new NullPointerException("NARG");
		
		// Setup state
		ByteCodeState state = this.state;
		state.addr = __ed.addr;
		state.line = __ed.line;
		
		// And return the label
		return __ed.label;
	}
	
	/**
	 * Sets debug point information for edata.
	 *
	 * @param __ed The EData used.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/06/16
	 */
	private void __useEDataDebugPoint(__EData__ __ed, int __jop)
		throws NullPointerException
	{
		if (__ed == null)
			throw new NullPointerException("NARG");
		
		// Add debug point (operation becomes debug point 1)
		this.codebuilder.add(NativeInstructionType.DEBUG_POINT,
			__ed.line & 0x7FFF, __jop,
			__ed.jpc & 0x7FFF);
	}
}

