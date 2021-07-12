// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.jarfile;

import cc.squirreljme.jvm.summercoat.constants.ClassProperty;
import cc.squirreljme.jvm.summercoat.constants.CompilerConstants;
import cc.squirreljme.jvm.summercoat.constants.MemHandleKind;
import cc.squirreljme.jvm.summercoat.constants.StaticClassProperty;
import cc.squirreljme.jvm.summercoat.constants.StaticVmAttribute;
import cc.squirreljme.jvm.summercoat.constants.TaskPropertyType;
import cc.squirreljme.jvm.summercoat.constants.ThreadPropertyType;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.runtime.cldc.util.SortedTreeMap;
import cc.squirreljme.runtime.cldc.util.SortedTreeSet;
import dev.shadowtail.classfile.ClassFileDebug;
import dev.shadowtail.classfile.mini.MinimizedClassFile;
import dev.shadowtail.classfile.mini.MinimizedClassHeader;
import dev.shadowtail.classfile.mini.MinimizedField;
import dev.shadowtail.classfile.mini.MinimizedMethod;
import dev.shadowtail.classfile.mini.Minimizer;
import dev.shadowtail.classfile.pool.AccessedField;
import dev.shadowtail.classfile.pool.BasicPool;
import dev.shadowtail.classfile.pool.BasicPoolEntry;
import dev.shadowtail.classfile.pool.ClassNameHash;
import dev.shadowtail.classfile.pool.ClassPool;
import dev.shadowtail.classfile.pool.DualClassRuntimePool;
import dev.shadowtail.classfile.pool.InvokeType;
import dev.shadowtail.classfile.pool.InvokeXTable;
import dev.shadowtail.classfile.pool.InvokedMethod;
import dev.shadowtail.classfile.pool.QuickCastCheck;
import dev.shadowtail.classfile.pool.TypeBracketPointer;
import dev.shadowtail.classfile.xlate.DataType;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import net.multiphasicapps.classfile.ClassFile;
import net.multiphasicapps.classfile.ClassName;
import net.multiphasicapps.classfile.ClassNames;
import net.multiphasicapps.classfile.FieldNameAndType;
import net.multiphasicapps.classfile.FieldReference;
import net.multiphasicapps.classfile.InvalidClassFormatException;
import net.multiphasicapps.classfile.MethodHandle;
import net.multiphasicapps.classfile.MethodNameAndType;
import net.multiphasicapps.classfile.PrimitiveType;
import net.multiphasicapps.collections.UnmodifiableList;
import net.multiphasicapps.io.ChunkFuture;
import net.multiphasicapps.io.ChunkSection;

/**
 * The boot state of the system.
 *
 * @since 2020/12/13
 */
public final class BootState
{
	/** The length of an array. */
	private static final FieldNameAndType _ARRAY_LENGTH =
		new FieldNameAndType("length", "I");
	
	/** The type bracket in {@link Class}. */
	private static final FieldNameAndType _CLASS_TYPEBRACKET =
		new FieldNameAndType("_type",
			"Lcc/squirreljme/jvm/mle/brackets/TypeBracket;");
	
	/** The default constructor. */
	private static final MethodNameAndType _DEFAULT_CONSTRUCTOR =
		new MethodNameAndType("<init>", "()V");
	
	/** The object class. */
	private static final ClassName _OBJECT_CLASS =
		new ClassName("java/lang/Object");
	
	/** The class for {@link Class}. */
	private static final ClassName _CLASS_CLASS =
		new ClassName("java/lang/Class");
	
	/** Object class info. */
	private static final FieldNameAndType _OBJECT_CLASS_INFO =
		new FieldNameAndType("_classInfo",
			Minimizer.CLASS_INFO_FIELD_DESC);
	
	/** The class static initializer. */
	private static final MethodNameAndType _STATIC_CLINIT = 
		new MethodNameAndType("<clinit>", "()V");
	
	/** The name of the string class. */
	private static final ClassName _STRING_CLASS =
		new ClassName("java/lang/String");
	
	/** The field for the throwable trace. */
	private static final FieldNameAndType _THROWABLE_TRACE =
		new FieldNameAndType("_stack",
			"[Lcc/squirreljme/jvm/mle/brackets/TracePointBracket;");
	
	/** The class data used. */
	private final Map<ClassName, ChunkSection> _rawChunks =
		new HashMap<>();
	
	/** Classes which have been read. */
	private final Map<ClassName, MinimizedClassFile> _readClasses =
		new HashMap<>();
	
	/** The state of all classes. */
	private final Map<ClassName, ClassState> _classStates =
		new HashMap<>();
	
	/** Actions performed on memory. */
	private final MemActions _memActions =
		new MemActions();
	
	/** Memory handles for the boot state, to be written accordingly. */
	private final MemHandles _memHandles =
		new MemHandles(this._memActions, this);
	
	/** Internal strings. */
	private final Map<String, MemHandle> _internStrings =
		new LinkedHashMap<>();
		
	/** The name of the boot class. */
	private ClassName _bootClass;
	
	/** The current boot thread. */
	private PropertyListHandle _bootThread;
	
	/** The current boot task. */
	private PropertyListHandle _bootTask;
	
	/** An empty I2X Table, to save space. */
	private ListValueHandle _emptyI2XTable;
	
	/** The pool references to use for booting. */
	private DualClassRuntimePool _pool;
	
	/** Base array size. */
	private int _baseArraySize =
		-1;
	
	/** Base object size. */
	private int _baseObjectSize =
		-1;
	
	/** The pool chunk. */
	private ChunkSection _poolChunk;
	
	/** The pure virtual handler. */
	private MethodBinder _pureVirtual;
	
	/** The last loaded class, for chain linking. */
	private ClassState _lastClass;
	
	/** The first class in the chain. */
	private ClassState _firstClass;
	
	/**
	 * Adds the specified class to be loaded and handled later.
	 * 
	 * @param __class The class name of this resource.
	 * @param __in The chunk to read from.
	 * @param __isBootClass Is this the boot class?
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/13
	 */
	public void addClass(ClassName __class, ChunkSection __in,
		boolean __isBootClass)
		throws IOException, NullPointerException
	{
		if (__class == null || __in == null)
			throw new NullPointerException("NARG");
		
		// Store the raw chunk reference
		this._rawChunks.put(__class, __in);
		
		// Is this the entry class?
		if (__isBootClass)
			this._bootClass = __class;
	}
	
	/**
	 * Returns all of the interfaces that are implemented by the given class.
	 * 
	 * @param __cl The class to get for.
	 * @return All of the interfaces that the class implements.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/21
	 */
	public ClassNames allInterfaces(ClassName __cl)
		throws IOException, NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException("NARG");
		
		// Was this already calculated?
		ClassState wantState = this.loadClass(__cl);
		ClassNames rv = wantState._allInterfaces;
		if (rv != null)
			return rv;
		
		// All types that are interfaces
		Set<ClassName> result = new SortedTreeSet<>();
		
		// Seed initial queue with the current class
		Deque<ClassName> queue = new LinkedList<>();
		queue.add(__cl);
		
		// Drain the queue and visit all the various classes
		Set<ClassName> visited = new HashSet<>();
		while (!queue.isEmpty())
		{
			// Only visit classes once
			ClassName visit = queue.remove();
			if (visited.contains(visit))
				continue;
			
			// Do not visit again
			visited.add(visit);
			
			// If this is an interface, use that
			ClassState state = this.loadClass(visit);
			if (state.classFile.flags().isInterface())
				result.add(visit);
			
			// Add the interfaces to the queue for processing 
			queue.addAll(state.classFile.interfaceNames());
			
			// Add the super class to go up if there is one
			ClassName parent = state.classFile.superName();
			if (parent != null)
				queue.add(parent);
		}
		
		// Store result for future usage
		rv = new ClassNames(result);
		wantState._allInterfaces = rv;
		
		return rv;
	}
	
	/**
	 * Performs the boot process for the system.
	 * 
	 * @param __pool The pool to use for loading.
	 * @param __lpd The local dual pool section.
	 * @param __outData The output data for the bootstrap.
	 * @param __startPoolHandleId The target handle ID for the pool.
	 * @param __vmAttribHandleId The target handle for the VM attributes.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/16
	 */
	public void boot(DualClassRuntimePool __pool, ChunkSection __lpd,
		ChunkSection __outData, int[] __startPoolHandleId,
		int[] __vmAttribHandleId)
		throws IOException, NullPointerException
	{
		if (__pool == null || __lpd == null || __outData == null ||
			__startPoolHandleId == null)
			throw new NullPointerException("NARG");
		
		// Set the boot pool because we need everything that is inside
		this._pool = __pool;
		this._poolChunk = __lpd;
		
		// Recursively load the boot class and any dependent class
		ClassState boot = this.loadClass(this._bootClass);
		
		// Chain link all the internal strings needed by the bootstrap so that
		// they are consistent. This also needs to handle weak references so
		// these strings can be GCed.
		Debugging.todoNote("Chainlink intern strings.");
		if (false)
			throw Debugging.todo();
		// this._internStrings
		
		// Determine the starting memory handle ID
		__startPoolHandleId[0] = boot._poolMemHandle.id;
		__vmAttribHandleId[0] = this.__buildVmAttributes().id;
		
		// Write the memory handles (and actions) into boot memory
		this._memHandles.chunkOut(__outData);
	}
	
	/**
	 * Loads interface tables for the given class.
	 * 
	 * @param __cl The class to load for.
	 * @param __potentialMask The potential mask output.
	 * @return The loaded interface tables.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/02/06
	 */
	public ListValueHandle interfaceTables(ClassName __cl,
		int[] __potentialMask)
		throws IOException, NullPointerException
	{
		if (__cl == null || __potentialMask == null)
			throw new NullPointerException("NARG");
		
		// Get all the interfaces for direct mapping and usage
		ClassNames classNames = this.allInterfaces(__cl);
		
		// If there are no interfaces there is no point to make the table,
		// although make it a table that always collides
		if (classNames.size() == 0)
		{
			__potentialMask[0] = 0;
			return this.__emptyI2XTable();
		}
		
		// Table size must be a power of two! This is for masking!
		int tableSize = BootState.__bestI2XTableSize(classNames);
		
		// The list is twice the size of interfaces because it has
		// ClassInfo _and_ XTable
		List<Object> working = new ArrayList<>(tableSize * 2);
		for (int i = 0, n = tableSize * 2; i < n; i++)
			working.add(null);
		
		// This is the mapping of taken slots, used for collision detection
		byte[] taken = new byte[tableSize];
		
		// This is the base potential mask, which is used to quickly find
		// an entry or otherwise.
		int mask = tableSize - 1;
		__potentialMask[0] = mask;
		
		// Go through all the class names and build a table for them
		for (ClassName className : classNames)
		{
			// Determine the spot in the table this is at
			int tableSpot = (new ClassNameHash(className)).hashCode() & mask;
			int realSpot = tableSpot * 2;
			
			// Debug
			if (ClassFileDebug.ENABLE_DEBUG)
				Debugging.debugNote(
					"InterfaceSpot: %s [%s] %s -> #%d of %d",
					(taken[tableSpot] != 0 ? "BOOP " : ""), __cl,
					className, tableSpot, tableSize);
			
			// Load the target class info
			Object classInfo = this.loadClass(className)._classInfoHandle;
			Object xTable = this.__buildInterfaceXTable(__cl, className);
			
			// This spot is taken in the table, so it overflows!
			if (taken[tableSpot] != 0)
			{
				// Get what is currently here, since it needs to be moved to
				// the table end
				Object oldClassInfo = working.get(realSpot);
				Object oldXTable = working.get(realSpot + 1);
				
				// This was never shoved over to the end yet
				if (taken[tableSpot] == 1)
				{
					// Debug
					if (ClassFileDebug.ENABLE_DEBUG)
						Debugging.debugNote(
							"InterfaceSpot Collide: [%s] %s -> #%d of %d",
							__cl, className, tableSpot, tableSize);
					
					// Move the old stuff over
					working.add(oldClassInfo);
					working.add(oldXTable);
					
					// Erase the stuff here, this is for overflow
					working.set(realSpot, 0);
					working.set(realSpot + 1, 0);
				}
				
				// Add our information to the end
				working.add(classInfo);
				working.add(xTable);
				
				// Increase the take count here
				taken[tableSpot]++;
				
				// Do not perform normal processing
				continue;
			}
			
			// Set our own data
			working.set(realSpot, classInfo);
			working.set(realSpot + 1, xTable);
			
			// This spot is now taken
			taken[tableSpot]++;
		}
		
		// Map the result, since we need overflows!
		ListValueHandle result = this._memHandles.allocList(
			MemHandleKind.I2X_TABLE, working.size());
		for (int i = 0, y = working.size(); i < y; i++)
		{
			Object value = working.get(i);
			
			// Nothing was stored here, so would be a collision
			if (value == null)
				continue;
			
			// Either an int (erased) or memory handle
			if (value instanceof Integer)
				result.set(i, (Integer)value);
			else
				result.set(i, (MemHandle)value);
		}
		
		return result;
	}
	
	/**
	 * Loads a character array.
	 * 
	 * @param __v The values to store.
	 * @return The loaded character array.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/10
	 */
	public final ChunkMemHandle loadArrayChar(char... __v)
		throws IOException, NullPointerException
	{
		if (__v == null)
			throw new NullPointerException("NARG");
		
		// The class used
		ClassName type = PrimitiveType.CHARACTER
			.toClassName().addDimensions(1);
		
		// Prepare array to store the character data
		int n = __v.length;
		ChunkMemHandle rv = this.prepareArray(type, n);
		
		// Determine the base offset to write to
		int baseOff = this.__baseArraySize();
		
		// Write all the elements to it
		for (int i = 0, off = baseOff; i < n; i++, off += 2)
			rv.write(off, MemoryType.SHORT, (int)__v[i]);
		
		return rv;
	}
	
	/**
	 * Loads the specified class.
	 * 
	 * @param __cl The class to load.
	 * @return The class state for the class.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/24
	 */
	public final ClassState loadClass(String __cl)
		throws IOException, NullPointerException
	{
		return this.loadClass(new ClassName(__cl));
	}
	
	/**
	 * Loads the specified class.
	 * 
	 * @param __cl The class to load.
	 * @return The class state for the class.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/16
	 */
	public final ClassState loadClass(ClassName __cl)
		throws IOException, NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException("NARG");
		
		// Has this class already been loaded?
		Map<ClassName, ClassState> classStates = this._classStates;
		ClassState rv = classStates.get(__cl);
		if (rv != null)
			return rv;
		
		// Debug
		if (ClassFileDebug.ENABLE_DEBUG)
			Debugging.debugNote("Loading %s...", __cl);
		
		// Read the class data as fast as possible and store into the map so
		// we can recursive and recycle classes.
		MinimizedClassFile classFile = this.readClass(__cl);
		rv = new ClassState(__cl, classFile);
		classStates.put(__cl, rv);
		
		// This is the new last class
		ClassState lastClass = this._lastClass;
		this._lastClass = rv;
		
		// If this is the first class in the chain, we need to start somewhere
		if (lastClass == null)
			this._firstClass = rv;
		
		// Header information to extract properties from
		MinimizedClassHeader header = classFile.header;
		
		// Allocate storage for the class information
		MemHandles memHandles = this._memHandles;
		ClassInfoHandle classInfo = memHandles.allocClassInfo(rv);
		rv._classInfoHandle = classInfo;
		
		// Copy all of the static properties since they would not normally
		// be copied
		for (int i = 0; i < StaticClassProperty.NUM_STATIC_PROPERTIES; i++)
			classInfo.set(i, header.get(i));
		
		// Link both classes together
		if (lastClass != null)
		{
			ClassInfoHandle lastHandle = lastClass._classInfoHandle;
			
			// Doubly link together these two classes
			lastHandle.set(ClassProperty.TYPEBRACKET_LINK_CLASS_NEXT,
				classInfo);
			classInfo.set(ClassProperty.TYPEBRACKET_LINK_CLASS_PREV,
				lastHandle);
		}
		
		// Everything is based on the run-time pool, so we need to load
		// everything inside
		BasicPool rtPool = classFile.pool.runtimePool();
		int rtPoolSz = rtPool.size();
		
		// Allocate storage for the constant pool
		PoolHandle pool = memHandles.allocPool(rtPoolSz);
		rv._poolMemHandle = pool;
		
		// The class pool is here
		classInfo.set(ClassProperty.POOLHANDLE_POOL, pool);
		
		// The class static fields have to go somewhere
		ChunkMemHandle staticFieldData = memHandles.allocFields(
			this.__baseArraySize() + header.get(
				StaticClassProperty.SIZE_STATIC_FIELD_DATA));
		classInfo.set(ClassProperty.MEMHANDLE_STATIC_FIELDS,
			staticFieldData);
		
		// Store the pointer to where the Class ROM exists in memory, but this
		// is only valid for non-special classes
		if (!__cl.isArray() && !__cl.isPrimitive())
			classInfo.set(ClassProperty.MEMPTR_ROM_CLASS_LO,
				new BootJarPointer(this._rawChunks
					.get(classFile.thisName()).futureAddress()));
		
		// Need to determine if we are Object or our super class is Object
		// that way there can be shortcuts on resolution
		ClassName superClass = classFile.superName();
		boolean isThisObject = classFile.thisName().isObjectClass();
		
		// This should never happen, hopefully!
		if (!__cl.isPrimitive() && !isThisObject && superClass == null)
			throw Debugging.oops(classFile.thisName(), superClass);
		
		// If this is the object class, then it is well known what the size
		// of the various classes are
		if (isThisObject)
		{
			classInfo.set(ClassProperty.OFFSETBASE_INSTANCE_FIELDS, 0);
			classInfo.set(ClassProperty.SIZE_ALLOCATION,
				header.get(StaticClassProperty.INT_INSTANCE_FIELD_BYTES));
			
			// There is no depth to this class
			classInfo.set(ClassProperty.INT_CLASSDEPTH, 0);
		}
		
		// Primitive types are special cases
		else if (__cl.isPrimitive())
		{
			classInfo.set(ClassProperty.SIZE_ALLOCATION,
				__cl.primitiveType().bytes());
			
			// These do not make sense
			classInfo.set(ClassProperty.OFFSETBASE_INSTANCE_FIELDS, 0);
			classInfo.set(ClassProperty.INT_CLASSDEPTH, 0);
		}
		
		// Determine the kind of memory handle used
		classInfo.set(ClassProperty.INT_MEMHANDLE_KIND,
			this.__handleKind(__cl));
		
		// The cell size, if an array
		if (__cl.isArray())
			classInfo.set(ClassProperty.INT_COMPONENT_CELL_SIZE,
				DataType.of(__cl.componentType().field()).size());
		
		// Is there a super class for this class?
		ClassState superClassState = (superClass == null ? null :
			this.loadClass(superClass));
		if (superClass != null)
			classInfo.set(ClassProperty.TYPEBRACKET_SUPER,
				superClassState._classInfoHandle);
		
		// Store for later
		rv._superClass = superClassState;
		
		// Determine the allocation size of this class, we need to know this
		// as soon as possible but we can only determine this once object
		// is handled.
		if (!isThisObject && !__cl.isPrimitive())
		{
			// This is one deeper than the super class
			try
			{
				classInfo.set(ClassProperty.INT_CLASSDEPTH,
					this.__classDepth(rv));
			}
			catch (IllegalStateException ignored)
			{
				// Has already been set
			}
			
			// Debug super class
			if (ClassFileDebug.ENABLE_DEBUG)
				Debugging.debugNote("SuperClass: %s (from %s)",
					superClassState.thisName, __cl);
			
			// The base of the class is the allocation size of the super
			// class
			int base = this.__allocSize(superClassState);
			this.__classInstanceFieldBase(rv);
			
			// Debug
			if (ClassFileDebug.ENABLE_DEBUG)
				Debugging.debugNote("offsetBase %s = %d (check %d)",
					__cl, base, classInfo.getInteger(
						ClassProperty.OFFSETBASE_INSTANCE_FIELDS));
			
			// The allocation size of this class is the combined base and
			// our storage for fields
			int calcSize = base +
				header.get(StaticClassProperty.INT_INSTANCE_FIELD_BYTES);
			try
			{
				classInfo.set(ClassProperty.SIZE_ALLOCATION, calcSize);
			}
			catch (IllegalStateException ignored)
			{
				// Verify that the value is properly determined
				int val = classInfo.getInteger(ClassProperty.SIZE_ALLOCATION);
				if (calcSize != val)
					throw Debugging.oops("Wrong calc size?",
						calcSize, val);
			}
		}
		
		// Fill in any interfaces the class implements
		ClassNames interfaces = classFile.interfaceNames();
		classInfo.set(ClassProperty.TYPEBRACKETS_INTERFACE_CLASSES,
			memHandles.allocClassInfos(
				this.loadClasses(interfaces.toArray())));
		
		// The name of the current class
		classInfo.set(ClassProperty.MEMHANDLE_THIS_NAME_DESC,
			this.loadString(__cl.toString()));
		classInfo.set(ClassProperty.MEMHANDLE_THIS_NAME_RUNTIME,
			this.loadString(__cl.toRuntimeString()));
		
		// Store in constant values for fields
		int staticFieldBase = this.__baseArraySize();
		for (MinimizedField staticField : classFile.fields(true))
		{
			// Ignore field if there are no constant values
			Object constVal = staticField.value;
			if (constVal == null)
				continue;
			
			// Depends on the type being written
			int offset = staticFieldBase + staticField.offset;
			switch (staticField.datatype)
			{
				case BYTE:
					staticFieldData.write(offset, MemoryType.BYTE,
						((Number)constVal).byteValue());
					break;
					
				case SHORT:
				case CHARACTER:
					staticFieldData.write(offset, MemoryType.SHORT,
						((Number)constVal).shortValue());
					break;
					
				case INTEGER:
					staticFieldData.write(offset, MemoryType.INTEGER,
						((Number)constVal).intValue());
					break;
					
				case LONG:
					staticFieldData.write(offset, MemoryType.LONG,
						((Number)constVal).longValue());
					break;
					
				case FLOAT:
					staticFieldData.write(offset, MemoryType.INTEGER,
						Float.floatToRawIntBits((Float)constVal));
					break;
					
				case DOUBLE:
					staticFieldData.write(offset, MemoryType.LONG,
						Double.doubleToRawLongBits((Double)constVal));
					break;
					
				case OBJECT:
					if (constVal instanceof String)
					{
						staticFieldData.write(offset, MemoryType.INTEGER,
							this.loadString((String)constVal));
						break;
					}
				
				default:
					throw Debugging.oops(staticField);
			}
		}
		
		// Determine the VTable+STable for all virtual+static methods
		this.__getXTable(rv, InvokeType.VIRTUAL);
		this.__getXTable(rv, InvokeType.STATIC);
		
		// Determine all of the interfaces that this class possibly implements.
		// This will be used by instance checks and eventual interface VTable
		// lookup
		ClassNames allInterfaces = this.allInterfaces(__cl);
		classInfo.set(ClassProperty.TYPEBRACKET_ALL_INTERFACECLASSES, 
			memHandles.allocClassInfos(
				this.loadClasses(allInterfaces.toArray())));
		
		// Pre-build all of the interface ITables for every interface that
		// this class implements, a class gets a ITables for all interfaces
		// and inherited interfaces it implements.
		// ex: ArrayList gets ITables for Cloneable, Collection<E>, List<E>,
		// RandomAccess
		// But only if we are not an interface, because it is pointless
		// otherwise because no object will ever be the direct interface class
		// Abstract classes will never be initialized.
		if (!classFile.flags().isInterface() &&
			!classFile.flags().isAbstract())
		{
			int[] potentialMask = new int[1];
			ListValueHandle iTables =
				this.interfaceTables(__cl, potentialMask);
			classInfo.set(ClassProperty.MEMHANDLE_I2XTABLE, iTables);
			classInfo.set(ClassProperty.MASK_I2XTABLE, potentialMask[0]);
		}
		
		// Load the information for the Class<?> instance
		classInfo.set(ClassProperty.MEMHANDLE_LANG_CLASS_INSTANCE,
			this.loadLangClass(__cl));
		
		// Set and initialize all of the entries within the pool
		List<Object> poolSet = new ArrayList<>(rtPoolSz);
		poolSet.add(null);	// null/zero entry
		for (int i = 1; i < rtPoolSz; i++)
		{
			Object pv = this.__loadPool(rv, rtPool, rtPool.byIndex(i),
				poolSet, i);
			poolSet.add(pv);
			
			// Depends on the type of value returned
			if (pv instanceof Integer)
				pool.set(i, (int)pv);
			else if (pv instanceof MemHandle)
				pool.set(i, (MemHandle)pv);
			else if (pv instanceof ChunkFuture)
				pool.set(i, (ChunkFuture)pv);
			else if (pv instanceof HasBootJarPointer)
				pool.set(i, (HasBootJarPointer)pv);
			
			// Do not know what to do with this
			else
				throw Debugging.oops(pv);
		}
		
		// The component class, if this is an array
		if (__cl.isArray())
		{
			// The component
			classInfo.set(ClassProperty.TYPEBRACKET_COMPONENT,
				this.loadClass(__cl.componentType())._classInfoHandle);
			
			// The root component, used for type checking
			classInfo.set(ClassProperty.TYPEBRACKET_ROOT_COMPONENT,
				this.loadClass(__cl.rootComponentType())._classInfoHandle);
		}
		
		// Determine the function pointer to the default new instance, this
		// is needed for Class.newInstance(). This may be null.
		MinimizedMethod defConst = classFile.method(false,
			BootState._DEFAULT_CONSTRUCTOR);
		if (defConst != null)
			classInfo.set(ClassProperty.FUNCPTR_DEFAULT_NEW_LO,
				this.__calcMethodCodeAddr(rv, false, defConst));
		
		// Find the static initializer
		MinimizedMethod clInit = classFile.method(true,
			BootState._STATIC_CLINIT);
		if (clInit != null)
			classInfo.set(ClassProperty.FUNCPTR_CLINIT_LO,
				this.__calcMethodCodeAddr(rv, true, clInit));
		
		// Loading the class is complete!
		return rv;
	}
	
	/**
	 * Loads multiple classes.
	 * 
	 * @param __names The names of the classes to load.
	 * @return The loaded classes.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/21
	 */
	public final ClassState[] loadClasses(ClassName... __names)
		throws IOException, NullPointerException
	{
		if (__names == null)
			throw new NullPointerException("NARG");
		
		int n = __names.length;
		ClassState[] rv = new ClassState[n];
		
		for (int i = 0; i < n; i++)
			rv[i] = this.loadClass(__names[i]);
		
		return rv;
	}
	
	/**
	 * Loads an instance of {@link Class} for a given object.
	 * 
	 * @param __cl The class to load for.
	 * @return The {@link Class} instance.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/21
	 */
	public MemHandle loadLangClass(ClassName __cl)
		throws IOException, NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException("NARG");
			
		// Load the string class and prepare an object that we will be writing
		// into as required
		ChunkMemHandle object = this.prepareObject(BootState._CLASS_CLASS);
		
		// There just needs to be a handle to the class information, note
		// that class information is a kind of TypeBracket at least on
		// SummerCoat.
		this.objectFieldSet(object,
			new FieldNameAndType("_type",
				"Lcc/squirreljme/jvm/mle/brackets/TypeBracket;"),
			this.loadClass(__cl)._classInfoHandle);
		
		return object;
	}
	
	/**
	 * Loads a string into memory and returns the mem handle of where it is
	 * allocated.
	 * 
	 * @param __s The string to load.
	 * @return The memory handle of the string.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/21
	 */
	public final MemHandle loadString(String __s)
		throws IOException, NullPointerException
	{
		if (__s == null)
			throw new NullPointerException("NARG");
		
		// Has this string already been loaded? If so then we need to use
		// the pre-existing string here
		Map<String, MemHandle> intern = this._internStrings;
		MemHandle rv = intern.get(__s);
		if (rv != null)
			return rv;
		
		// Load the string class and prepare an object that we will be writing
		// into as required
		ChunkMemHandle object = this.prepareObject(BootState._STRING_CLASS);
		
		// The only part of string that needs to be set is the character data
		this.objectFieldSet(object,
			new FieldNameAndType("_chars", "[C"),
			this.loadArrayChar(__s.toCharArray()));
		
		return object;
	}
	
	/**
	 * Gets the value for the given instance field.
	 * 
	 * @param <V> The type of value requested.
	 * @param __cl The type of vlaue requested.
	 * @param __object The object to get.
	 * @param __nat The name and type of the field.
	 * @throws IllegalArgumentException If the class does not have that field.
	 * @throws IOException On read/write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/12
	 */
	public  <V> V objectFieldGet(Class<V> __cl, ChunkMemHandle __object,
		FieldNameAndType __nat)
		throws IllegalArgumentException, IOException, NullPointerException
	{
		if (__object == null || __nat == null)
			throw new NullPointerException("NARG");
		
		// Determine the starting point for the object scan
		ClassState start;
		if (__nat.equals(BootState._OBJECT_CLASS_INFO))
			start = this.loadClass(BootState._OBJECT_CLASS);
		else
			start = this.<ClassInfoHandle>objectFieldGet(ClassInfoHandle.class,
				__object, BootState._OBJECT_CLASS_INFO).classState();
		
		// Determine where in the object we will be reading the value
		for (ClassState at = start; at != null; at = at._superClass)
			for (MinimizedField f : at.classFile.fields(false))
				if (__nat.equals(f.nameAndType()))
				{
					// Where is this field located?
					int offset = f.offset + at._classInfoHandle.getInteger(
						ClassProperty.OFFSETBASE_INSTANCE_FIELDS);
					
					if (ClassFileDebug.ENABLE_DEBUG)
						Debugging.debugNote(
							"[%s/%s] <- offset %d = %d + %d",
							__nat, at.thisName,
							offset, f.offset, at._classInfoHandle.getInteger(
							ClassProperty.OFFSETBASE_INSTANCE_FIELDS));
					
					// Set value here
					return __object.<V>read(__cl, offset,
						MemoryType.of(__nat.type().dataType()));
				}
		
		// {@squirreljme.error BC0k Class does not contain the given field.
		// (The class; The field)}
		throw new IllegalArgumentException(String.format("BC0k %s %s",
			start.thisName, __nat));
	}
	
	/**`
	 * Sets the given field for the object instance.
	 * 
	 * @param __object The object to set.
	 * @param __nat The name and type of the field.
	 * @param __v The value to store.
	 * @throws IllegalArgumentException If the class does not have that field.
	 * @throws IOException On read/write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/10
	 */
	public void objectFieldSet(ChunkMemHandle __object, FieldNameAndType __nat,
		Object __v)
		throws IllegalArgumentException, IOException, NullPointerException
	{
		if (__object == null || __nat == null || __v == null)
			throw new NullPointerException("NARG");
		
		// Determine the starting point for the object scan
		ClassState start;
		if (__nat.equals(BootState._OBJECT_CLASS_INFO))
			start = this.loadClass(BootState._OBJECT_CLASS);
		else
			start = this.<ClassInfoHandle>objectFieldGet(ClassInfoHandle.class,
				__object, BootState._OBJECT_CLASS_INFO).classState();
		
		// Determine where in the object we will be writing the value
		for (ClassState at = start; at != null; at = at._superClass)
			for (MinimizedField f : at.classFile.fields(false))
				if (__nat.equals(f.nameAndType()))
				{
					// Where is this field located?
					int offset = f.offset + at._classInfoHandle.getInteger(
						ClassProperty.OFFSETBASE_INSTANCE_FIELDS);
					
					if (ClassFileDebug.ENABLE_DEBUG)
						Debugging.debugNote(
							"[%s/%s] -> offset %d = %d + %d",
							__nat, at.thisName,
							offset, f.offset, at._classInfoHandle.getInteger(
							ClassProperty.OFFSETBASE_INSTANCE_FIELDS));
					
					// Set value here
					__object.write(offset,
						MemoryType.of(__nat.type().dataType()), __v);
					
					// Stop processing, since we set some value
					return;
				}
		
		// {@squirreljme.error BC0j Class does not contain the given field.
		// (The class; The field)}
		throw new IllegalArgumentException(String.format("BC0j %s %s",
			start.thisName, __nat));
	}
	
	/**
	 * Prepares an array instance.
	 * 
	 * @param __cl The class type to use for the array.
	 * @param __len The length of the array.
	 * @return The handle for the array data.
	 * @throws IllegalArgumentException If the class is not an array or the
	 * array length is negative.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/10
	 */
	public ChunkMemHandle prepareArray(ClassName __cl, int __len)
		throws IllegalArgumentException, IOException, NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException("NARG");
		if (__len < 0)
			throw new IllegalArgumentException("NEGV " + __len);
		
		// {@squirreljme.error BC0f Class is not an array. (The class)}
		if (!__cl.isArray())
			throw new IllegalArgumentException("BC0f" + __cl);
		
		// We need to know about the class to work with it
		ClassState state = this.loadClass(__cl);
		
		// Determine the memory handle kind used
		int handleKind = this.__handleKind(__cl);
		
		// Determine the base array size.
		int baseArraySize = this.__baseArraySize();
		
		// Allocate memory needed to store the array handle, this includes
		// room for all of the elements accordingly
		ChunkMemHandle rv = this._memHandles.alloc(
			handleKind, baseArraySize +
				(__len * __cl.componentType().field().dataType().size()));
		
		// Set class kind
		this.objectFieldSet(rv, BootState._OBJECT_CLASS_INFO,
			state._classInfoHandle);
		
		// Debug
		if (ClassFileDebug.ENABLE_DEBUG)
			Debugging.debugNote("array[%d] (sz=%d)", __len, rv.byteSize);
		
		// Store the array size as a hint
		rv._arraySize = __len;
		
		// Set array field information
		this.objectFieldSet(rv, BootState._ARRAY_LENGTH,
			__len);
		
		return rv;
	}
	
	/**
	 * Prepares memory that contains an instance for the given class.
	 * 
	 * @param __cl The class to load.
	 * @return The handle to the class data.
	 * @throws IOException On read errors.
	 * @throws IllegalArgumentException If an object is attempted to be
	 * allocated.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/10
	 */
	public ChunkMemHandle prepareObject(ClassName __cl)
		throws IllegalArgumentException, IOException, NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error BC0i Cannot use this method to initialize a
		// primitive or array type. (The class name)}
		if (__cl.isPrimitive() || __cl.isArray())
			throw new IllegalArgumentException("BC0i " + __cl);
		
		// We need to know about the class to work with it
		ClassState state = this.loadClass(__cl);
		
		// Allocate memory needed to store the object handle
		ChunkMemHandle rv = this._memHandles.allocObject(
			state._classInfoHandle.getInteger(ClassProperty.SIZE_ALLOCATION));
		
		// Set object field information
		this.objectFieldSet(rv, BootState._OBJECT_CLASS_INFO,
			state._classInfoHandle);
		
		return rv;
	}
	
	/**
	 * Reads a minimized class file for the given class.
	 * 
	 * @param __class The class to boot.
	 * @return The read class data.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/16
	 */
	public MinimizedClassFile readClass(ClassName __class)
		throws IOException, NullPointerException
	{
		if (__class == null)
			throw new NullPointerException("NARG");
		
		// Did we already read this class?
		Map<ClassName, MinimizedClassFile> readClasses = this._readClasses;
		MinimizedClassFile rv = readClasses.get(__class);
		if (rv != null)
			return rv;
		
		// Arrays are special and are dynamically created accordingly
		if (__class.isArray() || __class.isPrimitive())
			rv = MinimizedClassFile.decode(Minimizer.minimize(
				ClassFile.special(__class.field())));
		
		// Load in class data
		else
		{
			// {@squirreljme.error JC4x Class does not exist. (The class)}
			ChunkSection chunk = this._rawChunks.get(__class);
			if (chunk == null)
				throw new InvalidClassFormatException("JC4x " + __class);
			
			try (InputStream in = chunk.currentStream())
			{
				rv = MinimizedClassFile.decode(in, this._pool);
				
				// Debug
				if (ClassFileDebug.ENABLE_DEBUG)
					Debugging.debugNote("Read %s...", rv.thisName());
			}
		}
		
		// Cache it and use it
		readClasses.put(__class, rv);
		return rv;
	}
	
	/**
	 * Returns the allocation size of the class.
	 * 
	 * @param __class The class.
	 * @return The allocation size.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/24
	 */
	private int __allocSize(ClassState __class)
		throws IOException, NullPointerException
	{
		if (__class == null)
			throw new NullPointerException("NARG");
		
		ClassInfoHandle classInfo = __class._classInfoHandle;
		
		// See if it is available
		try
		{
			return classInfo.getInteger(ClassProperty.SIZE_ALLOCATION);
		}
		
		// Needs to be calculated
		catch (NoSuchElementException ignored)
		{
			Deque<ClassState> superChain =
				new LinkedList<>(this.__classChain(__class));
			
			// Handle what is in the queue to determine our allocation size
			int allocSize = 0;
			while (!superChain.isEmpty())
			{
				ClassState at = superChain.removeFirst();
				
				// The allocation size is what is going up
				allocSize += at.classFile.header.get(
					StaticClassProperty.INT_INSTANCE_FIELD_BYTES);
			}
			
			// Set it accordingly
			classInfo.set(ClassProperty.SIZE_ALLOCATION, allocSize);
			
			// Use that one
			return allocSize;
		}
	}
	
	/**
	 * Determines the base array size.
	 * 
	 * @return The base array size.
	 * @since 2021/01/20
	 */
	int __baseArraySize()
	{
		// Calculate the base size for arrays
		int baseArraySize = this._baseArraySize;
		if (baseArraySize < 0)
			try
			{
				// Load array file, to get information on it
				MinimizedClassFile arrayFile = this.readClass(
					BootState._OBJECT_CLASS.addDimensions(1));
				
				// Is a base object, but also with the array bytes attached
				baseArraySize = this.__baseObjectSize() +
					arrayFile.header.get(
						StaticClassProperty.INT_INSTANCE_FIELD_BYTES);
				
				this._baseArraySize = baseArraySize;
			}
			catch (IOException e)
			{
				// {@squirreljme.error BC0o Could not read class.}
				throw new InvalidClassFormatException("BC0o", e);
			}
		
		return baseArraySize;
	}
	
	/**
	 * Determines the base array size.
	 * 
	 * @return The base array size.
	 * @since 2021/01/20
	 */
	int __baseObjectSize()
	{
		// Calculate the base size for arrays
		int baseObjectSize = this._baseObjectSize;
		if (baseObjectSize < 0)
			try
			{
				// Load object to get its size
				MinimizedClassFile objectFile = this.readClass(
					BootState._OBJECT_CLASS);
				
				// Is just the field size of the class
				baseObjectSize = objectFile.header.get(
					StaticClassProperty.INT_INSTANCE_FIELD_BYTES);
				
				this._baseObjectSize = baseObjectSize;
			}
			catch (IOException e)
			{
				// {@squirreljme.error BC0p Could not read class.}
				throw new InvalidClassFormatException("BC0p", e);
			}
		
		return baseObjectSize;
	}
	
	/**
	 * Returns the boot task.
	 * 
	 * @return The boot task.
	 * @since 2021/05/11
	 */
	private PropertyListHandle __bootTask()
	{
		// Was it already setup?
		PropertyListHandle rv = this._bootTask;
		if (rv != null)
			return rv;
		
		// Setup task
		rv = this._memHandles.allocList(MemHandleKind.TASK,
			TaskPropertyType.NUM_PROPERTIES);
		this._bootTask = rv;
		
		return rv;
	}
	
	/**
	 * Returns the boot thread handle.
	 * 
	 * @return The boot thread handle.
	 * @since 2021/05/08
	 */
	private PropertyListHandle __bootThread()
	{
		// Was it already setup?
		PropertyListHandle rv = this._bootThread;
		if (rv != null)
			return rv;
		
		// Setup thread
		rv = this._memHandles.allocList(MemHandleKind.VM_THREAD,
			ThreadPropertyType.NUM_PROPERTIES);
		this._bootThread = rv;
		
		// The current task
		rv.set(ThreadPropertyType.TASK, this.__bootTask());
		
		// Use what was created
		return rv;
	}
	
	/**
	 * Builds an interface XTable for the given class.
	 * 
	 * @param __target The target class.
	 * @param __interface The interface this is calling for.
	 * @return The XTable for calling into the given class.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/02/06
	 */
	private ListValueHandle __buildInterfaceXTable(ClassName __target,
		ClassName __interface)
		throws IOException, NullPointerException
	{
		if (__target == null || __interface == null)
			throw new NullPointerException("NARG");
		
		// The methods this will bind into when called.
		List<MethodBinder> targetBinds = this.__classBindsVirtual(
			this.loadClass(__target));
		
		// Interface binds are a hybrid between virtual classes and
		// interfaces, they will all map into here accordingly
		List<MethodNameAndType> interfaceBinds = this.__classMethodsInterface(
			this.loadClass(__interface));
		int numBinds = interfaceBinds.size();
		
		// Go through all of the binds and link them in accordingly
		List<VTableMethod> result = new ArrayList<>(numBinds);
		for (int i = 0; i < numBinds; i++)
		{
			// We want a match for this one
			MethodNameAndType want = interfaceBinds.get(i);
			
			MethodBinder got = null;
			for (int j = targetBinds.size() - 1; j >= 0; j--)
				if (want.equals(targetBinds.get(j).method.nameAndType()))
				{
					got = targetBinds.get(j);
					break;
				}
			
			// {@squirreljme.error BC0n Could not find the method for the
			// given class. (The target class; The interface this is for;
			// The wanted method; The interface methods to look for;
			// The target binds that are available)}
			if (got == null)
				throw new InvalidClassFormatException(String.format(
					"BC0n %s %s %s IB=%s TB=%s", __target, __interface, want,
					interfaceBinds, targetBinds));
			
			// Store into the table
			result.add(i, got.vTable);
		}
		
		return this.__vtMethodsToHandle(result);
	}
	
	/**
	 * Creates the {@link StaticVmAttribute} mapping.
	 * 
	 * @return The memory handle for attributes.
	 * @throws IOException On read errors.
	 * @since 2021/01/24
	 */
	private MemHandle __buildVmAttributes()
		throws IOException
	{
		PropertyListHandle rv = this._memHandles.allocList(
			MemHandleKind.STATIC_VM_ATTRIBUTES, StaticVmAttribute.NUM_METRICS);
		
		// The base size of object
		int objectBase = this.loadClass("java/lang/Object")
			._classInfoHandle.getInteger(ClassProperty.SIZE_ALLOCATION);
		
		rv.set(StaticVmAttribute.SIZE_OBJECT,
			objectBase);
		rv.set(StaticVmAttribute.OFFSETOF_ARRAY_LENGTH_FIELD,
			objectBase + this.loadClass("[I").classFile
				.field(false, BootState._ARRAY_LENGTH).offset);
		rv.set(StaticVmAttribute.OFFSETOF_OBJECT_TYPE_FIELD,
			this.loadClass("java/lang/Object").classFile
				.field(false, BootState._OBJECT_CLASS_INFO).offset);
		
		// The type used for Class<?>
		rv.set(StaticVmAttribute.TYPEBRACKET_CLASS,
			this.loadClass("java/lang/Class")._classInfoHandle);
		
		// Offset to Throwable trace data
		rv.set(StaticVmAttribute.OFFSETOF_THROWABLE_TRACE_FIELD,
			objectBase + this.loadClass("java/lang/Throwable").classFile
				.field(false, BootState._THROWABLE_TRACE).offset);
		
		// Class<?>'s TypeBracket
		rv.set(StaticVmAttribute.OFFSETOF_CLASS_TYPEBRACKET_FIELD,
			objectBase + this.loadClass("java/lang/Class").classFile
				.field(false, BootState._CLASS_TYPEBRACKET).offset);
		
		// Size of arrays
		rv.set(StaticVmAttribute.SIZE_BASE_ARRAY,
			this.__baseArraySize());
		
		// The current boot thread (and task)
		rv.set(StaticVmAttribute.MEMHANDLE_BOOT_THREAD,
			this.__bootThread());
		
		// Any additional needed for the task
		PropertyListHandle bootTask = this.__bootTask();
		
		// The first and last classes for the boot task
		bootTask.set(TaskPropertyType.CLASS_FIRST,
			this._firstClass._classInfoHandle);
		bootTask.set(TaskPropertyType.CLASS_LAST,
			this._lastClass._classInfoHandle);	
		
		return rv;
	}
	
	/**
	 * Calculates the method code address.
	 * 
	 * @param __inClass The class this is in.
	 * @param __static Is this a static method?
	 * @param __method The method to get.
	 * @return The pointer to the method code.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/10/29
	 */
	private BootJarPointer __calcMethodCodeAddr(ClassState __inClass,
		boolean __static, MinimizedMethod __method)
		throws IOException, NullPointerException
	{
		if (__inClass == null || __method == null)
			throw new NullPointerException("NARG");
		
		int base = __inClass.classFile.header.get(__static ?
			StaticClassProperty.OFFSET_STATIC_METHOD_DATA :
			StaticClassProperty.OFFSET_INSTANCE_METHOD_DATA);
		
		// If this is targetting an array or primitive type, just perform
		// lookups on Object instead since it will not be really possible
		// to execute methods otherwise.
		if (__inClass.thisName.isArray() || __inClass.thisName.isPrimitive())
			return this.__calcMethodCodeAddr(this.loadClass(
				BootState._OBJECT_CLASS), __static, __method);
		
		return new BootJarPointer(base + __method.codeoffset,
			this._rawChunks.get(__inClass.thisName).futureAddress());
	}
	
	/**
	 * Calculates the static class call table for a given class.
	 * 
	 * @param __for The class to calculate for.
	 * @return The calculated STable.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/30
	 */
	private ListValueHandle __calcSTable(ClassState __for)
		throws IOException, NullPointerException
	{
		if (__for == null)
			throw new NullPointerException("NARG");
			
		// Get class information and otherwise
		List<MethodBinder> binds = this.__classBindsStatic(__for);
		
		// The static table is a one to one mapping
		List<VTableMethod> methodInfo = new ArrayList<>(binds.size());
		for (MethodBinder bind : binds)
		{
			// Native and abstract methods are purely virtual 
			if (bind.isNative() || bind.isAbstract())
				methodInfo.add(this.__pureVirtualBind().vTable);
			
			// Map 1:1 otherwise
			else
				methodInfo.add(bind.vTable);
		}
		
		// Build VTable
		return this.__vtMethodsToHandle(methodInfo);
	}
	
	/**
	 * Calculates the VTable for the given class, a VTable contains all of
	 * the method references and such. Note that constructors are counted as
	 * virtual calls as well, since you can call a super-class constructor but
	 * still call the correct constructor.
	 * 
	 * @param __class The class to calculate for.
	 * @return The calculated and initialized VTable for a class.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/26
	 */
	private ListValueHandle __calcVTable(ClassState __class)
		throws IOException, NullPointerException
	{
		if (__class == null)
			throw new NullPointerException("NARG");
		
		// Get class information and otherwise
		List<MethodBinder> binds = this.__classBindsVirtual(__class);
		
		// State for the current class, we know how big the table is going to
		// be due to all of the binds
		List<VTableMethod> methodInfo = new ArrayList<>(binds.size());
		
		// Go through all the bindings and determine the methods to use
		for (MethodBinder bind : binds)
		{
			MethodBinder target;
			
			// If this is an interface, then all methods are pure virtual
			if (__class.classFile.flags().isInterface())
				target = this.__pureVirtualBind();
			
			// Private methods refer to the same method
			else if (bind.isPrivate())
				target = bind;
			
			// Otherwise search through everything in backwards order
			else
			{
				// State for method lookup
				MethodBinder found = null;
				ClassState lastClass = null;
				boolean atOurClass = false;
				
				// Search for the given method
				for (ListIterator<MethodBinder> looky =
					binds.listIterator(binds.size()); looky.hasPrevious();)
				{
					MethodBinder look = looky.previous();
					
					// Have we switched classes?
					if (look.inClass != lastClass)
					{
						lastClass = look.inClass;
						
						// If this is the class we are coming from to calculate
						// the base of, we can only lookup methods that
						// either replace or are from the same class
						if (lastClass == bind.inClass)
							atOurClass = true;
						
						// If we were at our class, but are not anymore then
						// stop because we do not want to go further up
						else if (atOurClass)
							break;
					}
					
					// Is a method of a different name and/or type, ignore
					if (!bind.method.nameAndType()
						.equals(look.method.nameAndType()))
						continue;
					
					// Debug
					if (ClassFileDebug.ENABLE_DEBUG)
						Debugging.debugNote("Try? %s:%s -> %s:%s (%s)",
							bind.inClass, bind.method.nameAndType(),
							look.inClass, look.method.nameAndType(),
							look.method.flags());
					
					// Ignore private methods and package private methods if
					// we are not in the same package for the class we are
					// looking at.
					if (look.isPrivate() ||
						(look.isPackagePrivate() && !look.inSamePackage))
						continue;
					
					// Otherwise, we have found our method
					found = look;
					break;
				}
				
				// If the method and class are abstract, or this is a native
				// method then we cannot possibly call the code for it. This
				// becomes a pure virtual call, which will fail. Or we found
				// a native or abstract method.
				if ((found == null && ((bind.isAbstract() &&
						bind.isClassAbstract()) || bind.isNative())) || 
					(found != null &&
						(found.isNative() || found.isAbstract())))
					found = this.__pureVirtualBind();
					
				// {@squirreljme.error BC0r Could not find a method to link to.
				// (The class and method; The flags)}
				else if (found == null)
					throw new InvalidClassFormatException(String.format(
						"BC0r %s:%s (%s)", bind.inClass, bind.method,
							bind.method.flags()));
					
				// Use this
				target = found;
			}
			
			// Debug
			if (ClassFileDebug.ENABLE_DEBUG)
				Debugging.debugNote("Mapped %s:%s -> %s:%s",
					bind.inClass, bind.method.nameAndType(),
					target.inClass, target.method.nameAndType());
			
			// Use target for the given method
			methodInfo.add(target.vTable);
		}
		
		// Build VTable
		return this.__vtMethodsToHandle(methodInfo);
	}
	
	/**
	 * Loads the interface binds for classes.
	 * 
	 * @param __for The class to get the interface binds from.
	 * @return The class binds for interfaces.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/02/06
	 */
	private List<MethodNameAndType> __classMethodsInterface(ClassState __for)
		throws IOException, NullPointerException
	{
		if (__for == null)
			throw new NullPointerException("NARG");
		
		// Was this already calculated? Recycle it
		List<MethodNameAndType> rv = __for._interfaceMethods;
		if (rv != null)
			return rv;
		
		// Determine all of the method name and types for the interface since
		// these do not map to classes, but rather those for interfaces
		Set<MethodNameAndType> methods = new LinkedHashSet<>();
		for (ClassName className : this.allInterfaces(__for.thisName))
			for (MinimizedMethod method : this.loadClass(className)
				.classFile.methods(false))
				methods.add(method.nameAndType());
		
		// Build result from this
		rv = Arrays.asList(methods.toArray(
			new MethodNameAndType[methods.size()]));
		
		// Cache and use this
		__for._interfaceMethods = rv;
		return rv;
	}
	
	/**
	 * Calculates the static method binds for this class, this for the most
	 * part refers to static methods or static methods in a super-class.
	 * 
	 * @param __for The class for static calls.
	 * @return The bound static methods for the class.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/30
	 */
	private List<MethodBinder> __classBindsStatic(ClassState __for)
		throws IOException, NullPointerException
	{
		if (__for == null)
			throw new NullPointerException("NARG");
		
		// Was this already calculated? Recycle it
		List<MethodBinder> rv = __for._staticBinds;
		if (rv != null)
			return rv;
		
		// Get the class chain
		List<ClassState> chain = this.__classChain(__for);
		
		// Setup resultant list
		rv = new ArrayList<>(chain.size());
		__for._staticBinds = UnmodifiableList.<MethodBinder>of(rv);
		
		// Process every class, each name and type can be replaced!
		for (ClassState at : chain)
		{
			// If the class has no pool it has not been loaded yet, so attempt
			// to load it
			if (at._poolMemHandle == null)
				this.loadClass(at.thisName);
			
			// Load in methods
			for (MinimizedMethod method : at.classFile.methods(true))
			{
				// Will we be replacing a method with the same name and type?
				int found = -1;
				for (int i = 0, n = rv.size(); i < n; i++)
					if (method.nameAndType().equals(
						rv.get(i).method.nameAndType()))
					{
						found = i;
						break;
					}
				
				// Calculate the method bind
				MethodBinder bind = new MethodBinder(at, method,
					new VTableMethod(this.__calcMethodCodeAddr(at,
						true, method), at._poolMemHandle),
					__for.thisName.isInSamePackage(at.thisName));
				
				// Adding a fresh new bind?
				if (found < 0)
					rv.add(bind);
				
				// Replacing an existing one, from a lower method point?
				else
					rv.set(found, bind);
			}
		}
		
		return rv;
	}
	
	/**
	 * Calculates the method binds for the class, this refers to all methods
	 * linearly in the entire sequence for processing.
	 * 
	 * @param __for The class doing the resolution for, this is used for package
	 * checks.
	 * @return The bound methods for the class.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/30
	 */
	private List<MethodBinder> __classBindsVirtual(ClassState __for)
		throws IOException, NullPointerException
	{
		if (__for == null)
			throw new NullPointerException("NARG");
		
		// Was this already calculated? Recycle it
		List<MethodBinder> rv = __for._virtualBinds;
		if (rv != null)
			return rv;
		
		// Get the class chain
		List<ClassState> chain = this.__classChain(__for);
		
		// Setup resultant list
		rv = new ArrayList<>(chain.size());
		__for._virtualBinds = UnmodifiableList.<MethodBinder>of(rv);
		
		// Process every class
		Set<MethodNameAndType> declaredMethods = new LinkedHashSet<>();
		for (ClassState at : chain)
		{
			// Is this class abstract ?
			boolean isAtAbstract = at.classFile.flags().isAbstract();
			
			// If the class has no pool it has not been loaded yet, so attempt
			// to load it
			if (at._poolMemHandle == null)
				this.loadClass(at.thisName);
			
			// Calculate where methods are bound to
			for (MinimizedMethod method : at.classFile.methods(false))
			{
				// Setup and register the bind
				MethodBinder bind = new MethodBinder(at, method,
					new VTableMethod(
					this.__calcMethodCodeAddr(at, false, method),
						at._poolMemHandle),
					__for.thisName.isInSamePackage(at.thisName));
				rv.add(bind);
				
				// Used to handle interfaces later, if they are missing
				if (isAtAbstract)
					declaredMethods.add(method.nameAndType());
			}
			
			// No further processing is needed for non-abstract methods
			if (!isAtAbstract)
				continue;
				
			// Map in interfaces for this method accordingly if abstract, since
			// we will need to fill these in
			for (ClassName iFaceName : this.allInterfaces(at.thisName))
			{
				// Handle all methods within the interface if they have not
				// yet been declared
				ClassState iFaceState = this.loadClass(iFaceName);
				for (MinimizedMethod iMethod : iFaceState.classFile
					.methods(false))
					if (!declaredMethods.contains(iMethod.nameAndType()))
					{
						// Debug
						Debugging.debugNote("iMethod: %s:%s",
							iFaceName, iMethod.nameAndType());
						
						// Create a pure virtual bind, but do not use
						// the pure virtual name and type, but only
						// the actual link
						rv.add(new MethodBinder(at, iMethod,
							this.__pureVirtualBind().vTable,
							__for.thisName.isInSamePackage(at.thisName)));
						
						// Is now declared so skip later processing
						declaredMethods.add(iMethod.nameAndType());
					}
			}
		}
		
		return rv;
	}
	
	/**
	 * Returns the class chain, the uppermost class is first in the chain.
	 * 
	 * @param __class The class to get the chain of.
	 * @return The class queue chain.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/24
	 */
	private List<ClassState> __classChain(ClassState __class)
		throws IOException, NullPointerException
	{
		if (__class == null)
			throw new NullPointerException("NARG");
		
		// Already calculated?
		List<ClassState> rv = __class._classChain;
		if (rv != null)
			return rv;
		
		// Store new cache
		rv = new LinkedList<>();
		__class._classChain = UnmodifiableList.<ClassState>of(rv);
		
		// Load the class chain
		for (ClassState at = __class; at != null; at = at._superClass)
		{
			// Process into the queue
			rv.add(0, at);
			
			// If this is not the object/primitive class, and our super class
			// is not yet known then fill it in
			if ((!at.thisName.isObjectClass() && !at.thisName.isPrimitive()) &&
				at._superClass == null)
			{
				// This should be created but it might not have reached
				// the initialization point, due to cyclic super class
				// loading.
				at._superClass = this.loadClass(at.classFile.superName());
			}
		}
		
		return rv;
	}
	
	/**
	 * Returns the class depth for a given class.
	 * 
	 * @param __class The class to get the depth of.
	 * @return The class depth.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/24
	 */
	private int __classDepth(ClassState __class)
		throws IOException, NullPointerException
	{
		if (__class == null)
			throw new NullPointerException("NARG");
		
		ClassInfoHandle classInfo = __class._classInfoHandle;
		
		// See if it is available
		try
		{
			return classInfo.getInteger(ClassProperty.INT_CLASSDEPTH);
		}
		
		// Needs to be calculated
		catch (NoSuchElementException ignored)
		{
			// Load the class chain
			List<ClassState> superChain = this.__classChain(__class);
			
			// The depth is just the size of the class chain minus one, since
			// Object is depth zero
			int depth = superChain.size() - 1;
			
			// Set it accordingly
			classInfo.set(ClassProperty.INT_CLASSDEPTH, depth);
			
			// Use that one
			return depth;
		}
	}
	
	/**
	 * Returns the class instance base of the given class.
	 * 
	 * @param __cl The class to get the base from. 
	 * @return The instance field base of the class.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/30
	 */
	private int __classInstanceFieldBase(ClassState __cl)
		throws IOException, NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException("NARG");
		
		// For the object/primitive class this is always zero.
		if (__cl.thisName.isObjectClass() || __cl.thisName.isPrimitive())
			return 0;
		
		// Get the cached value
		try
		{
			return __cl._classInfoHandle
				.getInteger(ClassProperty.OFFSETBASE_INSTANCE_FIELDS);
		}
		catch (NoSuchElementException ignored)
		{
			// The field base is just the super class allocation size
			int base = this.__allocSize(__cl._superClass);
			__cl._classInfoHandle.set(ClassProperty.OFFSETBASE_INSTANCE_FIELDS,
				base);
			
			return base;
		}
	}
	
	/**
	 * Returns an empty I2X Table.
	 * 
	 * @return An empty I2X Table.
	 * @since 2021/02/06
	 */
	private ListValueHandle __emptyI2XTable()
	{
		ListValueHandle rv = this._emptyI2XTable;
		if (rv != null)
			return rv;
		
		// Setup blank table
		rv = this._memHandles.allocList(MemHandleKind.I2X_TABLE, 2);
		this._emptyI2XTable = rv;
		return rv;
	}
	
	/**
	 * Gets the XTable for the given class.
	 * 
	 * @param __for The class to get for.
	 * @param __type The type of VTable to use.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @return The calculated handle.
	 * @since 2021/01/30
	 */
	private ListValueHandle __getXTable(ClassState __for, InvokeType __type)
		throws IOException, NullPointerException
	{
		if (__for == null || __type == null)
			throw new NullPointerException("NARG");
		
		// Which property is this?
		int prop;
		switch (__type)
		{
			case VIRTUAL:
			case SPECIAL:
				prop = ClassProperty.MEMHANDLE_VTABLE;
				break;
			
			case STATIC:
				prop = ClassProperty.MEMHANDLE_STABLE;
				break;
			
			default:
				throw Debugging.oops(__type);
		}
		
		// Was it already set?
		try
		{
			return (ListValueHandle)__for._classInfoHandle.getMemHandle(prop);
		}
		
		// It is missing, so it needs calculation
		catch (NoSuchElementException ignored)
		{
			// Calculate the appropriate table
			ListValueHandle handle;
			switch (__type)
			{
				case VIRTUAL:
				case SPECIAL:
					handle = this.__calcVTable(__for);
					break;
				
				case STATIC:
					handle = this.__calcSTable(__for);
					break;
				
				default:
					throw Debugging.oops(__type);
			}
			
			// Set it
			__for._classInfoHandle.set(prop, handle);
			
			// Use the handle
			return handle;
		}
	}
	
	/**
	 * Returns the kind of handle used for the class.
	 * 
	 * @param __cl The class to get.
	 * @return The handle kind of the class.
	 * @since 2021/01/24
	 */
	private int __handleKind(ClassName __cl)
	{
		// If not an array is just an object instance
		if (!__cl.isArray())
			return MemHandleKind.OBJECT_INSTANCE;
		
		// Otherwise it is an array type
		PrimitiveType pType = __cl.componentType().primitiveType();
		if (pType == null)
			return MemHandleKind.OBJECT_ARRAY;
			
		switch (pType)
		{
			case BOOLEAN:
			case BYTE:
				return MemHandleKind.BYTE_ARRAY;
			
			case CHARACTER:
				return MemHandleKind.CHARACTER_ARRAY;
				
			case DOUBLE:
				return MemHandleKind.DOUBLE_ARRAY;
				
			case FLOAT:
				return MemHandleKind.FLOAT_ARRAY;
				
			case INTEGER:
				return MemHandleKind.INTEGER_ARRAY;
				
			case LONG:
				return MemHandleKind.LONG_ARRAY;
				
			case SHORT:
				return MemHandleKind.SHORT_ARRAY;
				
			default:
				throw Debugging.oops(__cl);
		}
	}
	
	/**
	 * Loads the specified pool entry into memory and returns the handle.
	 *
	 * @param __rv The class state this is for.
	 * @param __clPool The class runtime pool;
	 * @param __entry The entry to load.
	 * @param __poolSet The pool set used, to refer to older entries.
	 * @param __dx The current index.
	 * @return The loaded pool value.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/29
	 */
	private Object __loadPool(ClassState __rv, BasicPool __clPool,
		BasicPoolEntry __entry, List<Object> __poolSet, int __dx)
		throws IOException, NullPointerException
	{
		if (__clPool == null || __entry == null)
			throw new NullPointerException("NARG");
		
		// Needed pool information
		DualClassRuntimePool dualPool = this._pool;
		BasicPool staticDualPool = dualPool.classPool();
		ChunkSection poolChunk = this._poolChunk;
		
		// Determine where the pool is located
		int poolOff = poolChunk.futureAddress().get();
		
		// Depends on the type used
		switch (__entry.type())
		{
				// Accessed field
			case ACCESSED_FIELD:
				{
					AccessedField access = __entry.<AccessedField>value(
						AccessedField.class);
					FieldReference ref = access.field;
					
					// Is this static?
					boolean isStatic = access.type.isStatic();
					
					// We can refer to fields that are in a super class by
					// using the subclass class.
					// ex: ArrayList.modCount -> AbstractList.modCount
					for (ClassState at = this.loadClass(ref.className());
						at != null; at = at._superClass)
					{
						// Determine the source field
						MinimizedField minField = at.classFile.field(
							isStatic, ref.memberNameAndType());
						if (minField == null)
							continue;
						
						// Static is the direct offset with the array base
						if (isStatic)
							return this.__baseArraySize() + minField.offset;
						
						// Whereas instance is based on the class base
						return this.__classInstanceFieldBase(at) +
							minField.offset;
					}
						
					// {@squirreljme.error BC0q Could not resolve field
					// access entry. (The entry)}
					throw new InvalidClassFormatException(
						"BC0q " + __entry);
				}
			
				// Class information
			case TYPE_BRACKET_POINTER:
				return this.loadClass(__entry.<TypeBracketPointer>value(
					TypeBracketPointer.class).name)._classInfoHandle;
			
				// Class constant pool reference
			case CLASS_POOL:
				return this.loadClass(__entry.<ClassPool>value(
					ClassPool.class).name)._poolMemHandle;
				
				// A static method that has been invoked
			case INVOKED_METHOD:
				InvokedMethod invokedMethod = __entry.<InvokedMethod>value(
					InvokedMethod.class);
				
				// If we are calling an array, treat it as object
				if (invokedMethod.handle.outerClass().isArray())
					invokedMethod = new InvokedMethod(
						invokedMethod.type,
						new ClassName("java/lang/Object"),
						invokedMethod.handle().name(),
						invokedMethod.handle().nameAndType().type());
				
				// Load the target class details
				MethodHandle handle = invokedMethod.handle;
				ClassState inClass = this.loadClass(handle.outerClass());
				
				// If this is an interface, there are no actual binds they
				// are just referred to be index only
				if (invokedMethod.type() == InvokeType.INTERFACE)
				{
					// Get all the methods for the interface
					List<MethodNameAndType> methods =
						this.__classMethodsInterface(inClass);
					
					// Find the index for the method
					for (int i = 0, n = methods.size(); i < n; i++)
						if (handle.nameAndType().equals(methods.get(i)))
							return this.__baseArraySize() +
								((i * 4) * CompilerConstants.VTABLE_SPAN);
					
					// {@squirreljme.error BC0v Could not find an interface
					// index for the given method. (The entry; The methods
					// available)}
					throw new InvalidClassFormatException(String.format(
						"BC0v %s %s", __entry, methods));
				}
				
				// Load the correct binding table for the given invocation
				List<MethodBinder> binds;
				switch (invokedMethod.type())
				{
					case VIRTUAL:
					case SPECIAL:
						binds = this.__classBindsVirtual(inClass);
						break;
						
					case STATIC:
						binds = this.__classBindsStatic(inClass);
						break;
					
					default:
						throw Debugging.oops(__entry);
				}
				
				// Find the linear index for the given bind, which method
				// are we calling here?
				for (int i = 0, n = binds.size(); i < n; i++)
				{
					MethodBinder bind = binds.get(i);
					
					// The wrong method?
					if (!bind.method.nameAndType()
						.equals(handle.nameAndType()))
						continue;
					
					// Return the offset into the XTable
					return this.__baseArraySize() +
						((i * 4) * CompilerConstants.VTABLE_SPAN);
				}
				
				// {@squirreljme.error BC0s Could not link the given method.
				// (The source class; The method to link; The binding table)}
				throw new InvalidClassFormatException(String.format(
					"BC0s %s %s %s", __rv.thisName, __entry, binds));
				
				// Get the XTable for a given class
			case INVOKE_XTABLE:
				InvokeXTable invokeXTable = __entry.<InvokeXTable>value(
					InvokeXTable.class);
				
				ClassState inXClass = this.loadClass(invokeXTable.targetClass);
				switch (invokeXTable.invokeType)
				{
					case VIRTUAL:
					case SPECIAL:
					case STATIC:
						return this.__getXTable(inXClass,
							invokeXTable.invokeType);
					
					case SYSTEM:
						return this.__getXTable(inXClass,
							InvokeType.STATIC);
					
					case INTERFACE:
						// {@squirreljme.error BC0m Cannot get the XTable
						// for an interface. (The entry)}
						throw new InvalidClassFormatException(
							"BC0m " + __entry);
					
					default:
						throw Debugging.oops(__entry);
				}
				
				// A noted string for debugging purposes, this directly points
				// to the ROM for String data
			case NOTED_STRING:
				BasicPoolEntry poolStr =
					staticDualPool.byIndex(__entry.part(0));
				
				// These point to Strings that prefix with [hash$16 len$16]
				return new BootJarPointer(poolOff + poolStr.offset + 4);
			
				// Quickly check whether or not an object can be casted from
				// one type to another, if the types are well known!
			case QUICK_CAST_CHECK:
				QuickCastCheck quickCast = __entry.<QuickCastCheck>value(
					QuickCastCheck.class);
				
				// Allocate whether this is possible or not
				ListValueHandle quickCastState = this._memHandles.allocList(
					MemHandleKind.QUICK_CAST_CHECK, 1);
				
				// Calculate if this is compatible or not
				quickCastState.set(0, this.loadClass(quickCast.from)
					.isAssignableFrom(this,
						this.loadClass(quickCast.to)));
				
				// Debug
				if (ClassFileDebug.ENABLE_DEBUG)
					Debugging.debugNote("Casting?: %s -> %d",
						quickCast, quickCastState.getInteger(0));
				
				return quickCastState;
			
				// String used as a constant value, should be pre-loaded and
				// interned accordingly by the bootstrap.
			case USED_STRING:
				return this.loadString(__entry.value.toString());
			
				// The hash of the class, which is simple to calculate
			case CLASS_NAME_HASH:
				ClassNameHash classNameHash = __entry.<ClassNameHash>value(
					ClassNameHash.class);
				
				return classNameHash.hashCode();
		}
		
		if (false)
			throw Debugging.todo(__entry);
		
		// TODO: Remove fail value
		Debugging.debugNote("Load pool entry: %s", __entry);
		return 0x3E_00_0000 |
			(__entry.index & 0xFFFF) |
			(__entry.type().ordinal() << 16);
	}
	
	/**
	 * Returns the pure virtual method bind.
	 * 
	 * @return The pure virtual method bind.
	 * @throws IOException On read errors.
	 * @since 2021/01/30
	 */
	private MethodBinder __pureVirtualBind()
		throws IOException
	{
		// Has this been determined before?
		MethodBinder rv = this._pureVirtual;
		if (rv != null)
			return rv;
		
		// Lookup the class and method for pure virtual call handler
		ClassState logicHandler = this.loadClass(
			"cc/squirreljme/jvm/summercoat/LogicHandler");
		MinimizedMethod pvMethod = logicHandler.classFile.method(true,
			new MethodNameAndType("pureVirtualCall", "()V"));
		
		// Create information for the pure virtual call method
		rv = new MethodBinder(logicHandler,
			pvMethod, new VTableMethod(this.__calcMethodCodeAddr(logicHandler,
			false, pvMethod), logicHandler._poolMemHandle),
			false);
		
		// Cache for later
		this._pureVirtual = rv;
		return rv;
	}
	
	/**
	 * Creates a VTable handle from the given handles.
	 * 
	 * @param __methodInfo Method information to convert.
	 * @return The allocation handles.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/01/30
	 */
	private ListValueHandle __vtMethodsToHandle(
		List<VTableMethod> __methodInfo)
		throws NullPointerException
	{
		if (__methodInfo == null)
			throw new NullPointerException("NARG");
		
		int numMethods = __methodInfo.size();
		ListValueHandle vTable = this._memHandles.allocList(
			MemHandleKind.VIRTUAL_VTABLE,
			numMethods * CompilerConstants.VTABLE_SPAN);
		
		// Build VTable information into an actual VTable
		for (int i = 0, o = 0; i < numMethods;
			i++, o += CompilerConstants.VTABLE_SPAN)
		{
			VTableMethod method = __methodInfo.get(i);
			
			// Write the pool and the execution pointer (high+low)
			vTable.set(o + CompilerConstants.VTABLE_POOL_INDEX,
				method.poolHandle);
			vTable.set(o + CompilerConstants.VTABLE_METHOD_A_INDEX,
				method.execAddr);
			/*vTable.set(o + CompilerConstants.VTABLE_METHOD_B_INDEX,
				new HighBootJarPointer(method.execAddr));*/
		}
		
		return vTable;
	}
	
	/**
	 * Determines the best I2XTable size with the least collisions.
	 * 
	 * @param __classNames The class names to check.
	 * @return The best I2X Table size.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/02/06
	 */
	private static int __bestI2XTableSize(ClassNames __classNames)
		throws NullPointerException
	{
		if (__classNames == null)
			throw new NullPointerException("NARG");
		
		// Determine the sizes to try
		int baseSize = Integer.highestOneBit(__classNames.size() << 1);
		
		// Use the current size
		Map<Integer, Integer> sizes = new SortedTreeMap<>();
		sizes.put(baseSize, 0);
		
		// Use a bunch of fixed sizes, to get them all a try, but not too
		// large
		for (int i = 1; i <= 32; i <<= 1)
			sizes.put(i, 0);
		
		// Half size but not zero
		if ((baseSize >> 1) > 0)
			sizes.put((baseSize >> 1), 0);
		if ((baseSize << 1) < 64)
			sizes.put((baseSize << 1), 0);
		
		// Calculate for each size
		for (Map.Entry<Integer, Integer> at : sizes.entrySet())
		{
			// Calculate collisions for everything
			int size = at.getKey();
			int mask = size - 1;
			
			// Calculate for names
			boolean[] taken = new boolean[size];
			for (ClassName className : __classNames)
			{
				// Determine the spot in the table this is at
				int spot = (new ClassNameHash(className)).hashCode() & mask;
				
				// Spot was already taken, it counts as a collision
				if (taken[spot])
					at.setValue(at.getValue() + 1);
				
				// Spot is taken now
				else
					taken[spot] = true;
			}
		}
		
		// Find the lowest entry
		Map.Entry<Integer, Integer> lowest = null;
		for (Map.Entry<Integer, Integer> at : sizes.entrySet())
			if (lowest == null || at.getValue() < lowest.getValue())
				lowest = at;
		
		// Debug
		if (ClassFileDebug.ENABLE_DEBUG)
			Debugging.debugNote("__bestI2XTableSize: %s -> %d",
				sizes, lowest.getKey());
		
		return lowest.getKey();
	}
}
