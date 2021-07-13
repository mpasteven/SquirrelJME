// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.springcoat;

import cc.squirreljme.vm.VMClassLibrary;
import cc.squirreljme.vm.springcoat.exceptions.SpringClassFormatException;
import cc.squirreljme.vm.springcoat.exceptions.SpringIncompatibleClassChangeException;
import cc.squirreljme.vm.springcoat.exceptions.SpringNoSuchFieldException;
import cc.squirreljme.vm.springcoat.exceptions.SpringNoSuchMethodException;
import cc.squirreljme.vm.springcoat.exceptions.SpringVirtualMachineException;
import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.multiphasicapps.classfile.ClassFile;
import net.multiphasicapps.classfile.ClassFlags;
import net.multiphasicapps.classfile.ClassName;
import net.multiphasicapps.classfile.Field;
import net.multiphasicapps.classfile.FieldDescriptor;
import net.multiphasicapps.classfile.FieldName;
import net.multiphasicapps.classfile.FieldNameAndType;
import net.multiphasicapps.classfile.HasAccessibleFlags;
import net.multiphasicapps.classfile.Method;
import net.multiphasicapps.classfile.MethodDescriptor;
import net.multiphasicapps.classfile.MethodName;
import net.multiphasicapps.classfile.MethodNameAndType;

/**
 * This is a representation of a class file as it is seen by the virtual
 * machine, it is intended to remain simple and only refer to what is needed
 * for the machine to run.
 *
 * @since 2018/07/21
 */
public final class SpringClass
	implements HasAccessibleFlags
{
	/** The name of this class. */
	protected final ClassName name;
	
	/** The class file data. */
	protected final ClassFile file;
	
	/** The super class. */
	protected final SpringClass superclass;
	
	/** The number of instance fields that exist. */
	protected final int instanceFieldCount;
	
	/** The dimentions of this class. */
	protected final int dimensions;
	
	/** The component type. */
	protected final SpringClass component;
	
	/** The JAR this class is in. */
	protected final VMClassLibrary inJar;
	
	/** Interface classes. */
	private final SpringClass[] _interfaceclasses;
	
	/** Methods which exist in this class, includes statics for this only. */
	private final Map<MethodNameAndType, SpringMethod> _methods =
		new HashMap<>();
	
	/** Non-virtual instance methods. */
	private final Map<MethodNameAndType, SpringMethod> _nonvirtmethods =
		new HashMap<>();
	
	/** Fields which exist in this class, only includes this class fields */
	private final Map<FieldNameAndType, SpringField> _fields =
		new HashMap<>();
	
	/** The table of fields defined in this class, includes super classes. */
	private final SpringField[] _fieldtable;
	
	/** Field lookup. */
	private final SpringField[] _fieldLookup;
	
	/** Method index table. */
	private final SpringMethod[] _methodLookup;
	
	/** Base method indexes. */
	final int _methodLookupBase;
	
	/** The class loader which loaded this class. */
	private final Reference<SpringClassLoader> _classLoader;
	
	/** Static field storage. */
	final SpringFieldStorage[] _staticFields;
	
	/** The base index for static fields. */
	final int _staticFieldBase;
	
	/** The base index for our own instance fields. */
	final int _fieldLookupBase;
	
	/** The class instance. */
	SpringObject _instance;
	
	/** Has this class been initialized? */
	private volatile boolean _initialized;
	
	/**
	 * Initializes the spring class.
	 *
	 * @param __super The super class of this class.
	 * @param __interfaces The the interfaces this class implements.
	 * @param __cf The class file for this class.
	 * @param __ct The component type.
	 * @param __inJar The JAR this class is in.
	 * @param __loader The class loader which loaded this class.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/07/21
	 */
	SpringClass(SpringClass __super, SpringClass[] __interfaces,
		ClassFile __cf, SpringClass __ct, VMClassLibrary __inJar,
		Reference<SpringClassLoader> __loader)
		throws NullPointerException
	{
		if (__interfaces == null || __cf == null || __loader == null)
			throw new NullPointerException("NARG");
		
		ClassName name = __cf.thisName();
		this.name = name;
		this.inJar = __inJar;
		this.file = __cf;
		this.superclass = __super;
		this.component = __ct;
		this.dimensions = name.dimensions();
		this._classLoader = __loader;
		
		// Check
		this._interfaceclasses = (__interfaces = __interfaces.clone());
		for (SpringClass x : __interfaces)
			if (x == null)
				throw new NullPointerException("NARG");
		
		// Used for method location
		String filename = __cf.sourceFile();
		
		// Method index lookup for this class, used for debugging
		int baseMethods = (__super == null ? 0 : __super._methodLookup.length);
		int numMethods = baseMethods + __cf.methods().length;
		SpringMethod[] methodLookup = (__super == null ?
			new SpringMethod[numMethods] :
			Arrays.copyOf(__super._methodLookup, numMethods));
		this._methodLookup = methodLookup;
		
		// Base method index where entries go
		this._methodLookupBase = baseMethods;
		
		// Go through and initialize methods declared in this class
		int atMethodDx = baseMethods;
		Map<MethodNameAndType, SpringMethod> nvmeths = this._nonvirtmethods;
		Map<MethodNameAndType, SpringMethod> methods = this._methods;
		for (Method m : __cf.methods())
		{
			// Determine index
			int atIndex = atMethodDx++;
			
			// Setup method
			SpringMethod sm;
			if (null != methods.put(m.nameAndType(),
				(sm = new SpringMethod(name, m, filename, atIndex))))
			{
				// {@squirreljme.error BK0t Duplicated method in class. (The
				// method)}
				throw new SpringClassFormatException(name, String.format(
					"BK0t %s", m.nameAndType()));
			}
			
			// Store method in the lookup table
			methodLookup[atIndex] = sm;
			
			// Store only instance methods which are not static
			if (!m.flags().isStatic())
				nvmeths.put(m.nameAndType(), sm);
		}
		
		// Fields that are defined in super classes must be allocated, stored,
		// and indexed appropriately so that way casting between types and
		// accessing other fields is actually valid
		int superFieldCount = (__super == null ? 0 :
			__super.instanceFieldCount);
		int instanceFieldCount = superFieldCount;
		
		// The base of the instance fields are here, which are used to obtain
		// everything
		this._fieldLookupBase = superFieldCount;
		
		// Field index lookup for this class, used for debugging
		// We only base on the instance fields of the super class because
		// our statics do not extend because they are part of the class type
		// and not the instance.
		int numFields = superFieldCount + __cf.fields().size();
		SpringField[] fieldLookup = (__super == null ?
			new SpringField[numFields] :
			Arrays.copyOf(__super._fieldLookup, numFields));
		this._fieldLookup = fieldLookup;
		
		// Keep static fields at the very end
		int staticFieldAt = numFields;
		
		// Static field area
		SpringFieldStorage[] staticFields = new SpringFieldStorage[numFields];
		this._staticFields = staticFields;
		
		// Initialize all of the fields as needed
		Map<FieldNameAndType, SpringField> fields = this._fields;
		List<SpringField> instFields = new ArrayList<>(fields.size());
		for (Field f : __cf.fields())
		{
			boolean isinstance = f.flags().isInstance();
			
			// Where is this field located? At which index?
			int atDx = (isinstance ? instanceFieldCount++ : --staticFieldAt);
			
			// {@squirreljme.error BK0u Duplicated field in class. (The field)}
			SpringField sf;
			if (null != fields.put(f.nameAndType(),
				(sf = new SpringField(name, f, atDx))))
				throw new SpringClassFormatException(name, String.format(
					"BK0u %s", f.nameAndType()));
			
			// Store field lookup
			fieldLookup[atDx] = sf;
			
			// Setup storage for this field if static
			if (f.flags().isStatic())
				staticFields[atDx] = new SpringFieldStorage(sf, sf.index);
			
			// Used to build our part of the field table
			if (isinstance)
				instFields.add(sf);
		}
		
		// The position of the last static field is the one that is written
		this._staticFieldBase = staticFieldAt;
		
		// Each field is referenced by an index rather than a map, this is
		// more efficient for instances and additionally still allows for
		// sub-classes to declare fields as needed.
		SpringField[] fieldtable = new SpringField[instanceFieldCount];
		this._fieldtable = fieldtable;
		
		// Copy the super class field table, since technically all of the
		// fields in the super class are a part of this class.
		if (__super != null)
		{
			SpringField[] supertable = __super._fieldtable;
			for (int i = 0; i < superFieldCount; i++)
				fieldtable[i] = supertable[i];
		}
		
		// Store all of the instance fields
		for (int i = superFieldCount, p = 0, pn = instFields.size();
			p < pn; i++, p++)
			fieldtable[i] = instFields.get(p);
		
		// Used to quickly determine how big to set storage for a class
		this.instanceFieldCount = instanceFieldCount;
		
		// Go through super and interfaces and add non-static methods which
		// exist in sub-classes
		for (int i = 0, n = __interfaces.length; i <= n; i++)
		{
			// The class to look within
			SpringClass lookIn = (i == 0 ? __super : __interfaces[i - 1]);
			if (lookIn == null)
				continue;
			
			// Go through class methods
			for (Map.Entry<MethodNameAndType, SpringMethod> e :
				lookIn._methods.entrySet())
			{
				MethodNameAndType k = e.getKey();
				SpringMethod v = e.getValue();
				
				// Ignore initializer methods, and private methods
				if (v.isInstanceInitializer() ||
					v.isStaticInitializer() || v.flags().isPrivate())
					continue;
				
				// If the method does not exist in the table then it gets added
				// otherwise it is effectively replaced
				if (!methods.containsKey(k))
					methods.put(k, v);
			}
		}
		
		// Debug
		/*todo.DEBUG.note("Class %s (size=%d, fields=%d, methods=%d).", name,
			instancefieldcount,
			fields.size(),
			methods.size());*/
	}
	
	/**
	 * Returns the class loader which loaded this class.
	 * 
	 * @return The class loader which loaded this.
	 * @throws IllegalStateException If it was not set or GCed.
	 * @since 2021/03/15
	 */
	public final SpringClassLoader classLoader()
		throws IllegalStateException
	{
		synchronized (this)
		{
			if (this._classLoader == null)
				throw new IllegalStateException("Owner not set.");
			
			SpringClassLoader rv = this._classLoader.get();
			if (rv == null)
				throw new IllegalStateException("Owner GCed.");
			
			return rv;
		}
	}
	
	/**
	 * Returns the {@link Class} object.
	 * 
	 * @return The {@link Class} object.
	 * @since 2021/04/19
	 */
	public final SpringObject classObject()
	{
		synchronized (this)
		{
			SpringObject rv = this._instance;
			if (rv == null)
				throw new IllegalStateException("No Class<?> for " +
					this.name);
			
			return rv;
		}
	}
	
	/**
	 * Returns the component type of this class.
	 *
	 * @return The component type.
	 * @since 2018/09/27
	 */
	public final SpringClass componentType()
	{
		return this.component;
	}
	
	/**
	 * Returns the number of array dimensions.
	 *
	 * @return The number of dimensions.
	 * @since 2018/09/28
	 */
	public final int dimensions()
	{
		return this.name.dimensions();
	}
	
	/**
	 * Returns the field lookup.
	 * 
	 * @return The field lookup.
	 * @since 2021/04/16
	 */
	public final SpringField[] fieldLookup()
	{
		return this._fieldLookup.clone();
	}
	
	/**
	 * Returns the fields which are only declared in this class.
	 *
	 * @return The fields only declared in this class.
	 * @since 2018/09/09
	 */
	public final SpringField[] fieldsOnlyThisClass()
	{
		Map<FieldNameAndType, SpringField> fields = this._fields;
		return fields.values().<SpringField>toArray(
			new SpringField[fields.size()]);
	}
	
	/**
	 * Returns the table of fields used for this class which includes the
	 * super classes.
	 *
	 * @return The field table used for this class.
	 * @since 2018/09/16
	 */
	public final SpringField[] fieldTable()
	{
		return this._fieldtable.clone();
	}
	
	/**
	 * Returns the associated class file.
	 *
	 * @return The class file.
	 * @since 2018/09/08
	 */
	public final ClassFile file()
	{
		return this.file;
	}
	
	/**
	 * Returns the method index of the given method.
	 * 
	 * @param __method The method to look for.
	 * @return The method index of the given method.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/04/11
	 */
	public int findMethodIndex(SpringMethod __method)
		throws NullPointerException
	{
		throw cc.squirreljme.runtime.cldc.debug.Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/09/09
	 */
	@Override
	public final ClassFlags flags()
	{
		return this.file.flags();
	}
	
	/**
	 * Returns the JAR this class is in.
	 *
	 * @return The JAR this class is in.
	 * @since 2018/10/07
	 */
	public final VMClassLibrary inJar()
	{
		return this.inJar;
	}
	
	/**
	 * Returns the number of instance fields this class stores. This is for
	 * the most part the size of the given class.
	 *
	 * @return The number of instance fields this class stores.
	 * @since 2018/09/08
	 */
	public final int instanceFieldCount()
	{
		return this.instanceFieldCount;
	}
	
	/**
	 * Returns the interfaces that this class implements.
	 *
	 * @return The implemented interfaces.
	 * @since 2018/09/08
	 */
	public final SpringClass[] interfaceClasses()
	{
		return this._interfaceclasses.clone();
	}
	
	/**
	 * Is this an array?
	 *
	 * @return If this is an array.
	 * @since 2018/09/27
	 */
	public final boolean isArray()
	{
		return this.name.isArray();
	}
	
	/**
	 * Checks if this class can be assigned from the target class, that is
	 * {@code this = (ThisClass)__o}.
	 *
	 * This is the same as {@link Class#isInstance(Object)} except it works
	 * only on class representations.
	 *
	 * @param __o The other class to check.
	 * @return If the other class can be assigned as this class.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/09/15
	 */
	public final boolean isAssignableFrom(SpringClass __o)
		throws NullPointerException
	{
		if (__o == null)
			throw new NullPointerException("NARG");
		
		// Go through target superclasses to find this class
		for (SpringClass r = __o; r != null; r = r.superclass)
		{
			if (r == this)
				return true;
		
			// Go through interfaces for the class to find this class
			for (SpringClass i : r._interfaceclasses)
				if (this.isAssignableFrom(i))
					return true;
		}
		
		// Need to cast from one array type to another
		int thisDims = this.dimensions();
		int otherDims = __o.dimensions();
		if (thisDims > 0 || otherDims > 0)
		{
			// If this is an array and the other type is an array with the same
			// number of dimensions, then compare the base type so that say
			// Number[] is assignable from Integer[].
			if (otherDims == thisDims)
				return this.__rootType().isAssignableFrom(__o.__rootType());
			
			// We can cast down to Object array types if there are less
			// dimensions ([[[[Integer -> [Object)
			return this.__rootType().isObjectClass() && thisDims < otherDims;
		}
		
		// Not compatible
		return false;
	}
	
	/**
	 * Checks if the given value is compatible with this class.
	 *
	 * @param __v The value to check.
	 * @return If it is compatible or not.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/09/16
	 */
	public final boolean isCompatible(Object __v)
		throws NullPointerException
	{
		if (__v == null)
			throw new NullPointerException("NARG");
		
		// Primitive must match standard promoted type
		ClassName name = this.name;
		if (name.isPrimitive())
			switch (name.toString())
			{
				case "boolean":
				case "byte":
				case "short":
				case "char":
				case "int":
					return (__v instanceof Integer);
				
				case "long":
					return (__v instanceof Long);
				
				case "float":
					return (__v instanceof Float);
					
				case "double":
					return (__v instanceof Double);
				
				default:
					throw new todo.OOPS();
			}
			
		// Would be an assignable reference
		else if (__v instanceof SpringNullObject)
			return true;
		
		// Not primitive type, must be assignable
		else if (__v instanceof SpringObject)
			return this.isAssignableFrom(((SpringObject)__v).type());
		
		// Unknown
		else
			throw new todo.OOPS();
	}
	
	/**
	 * Checks if this is an enumeration.
	 * 
	 * @return If this is an enumeration.
	 * @since 2020/06/28
	 */
	public final boolean isEnum()
	{
		return this.flags().isEnum();
	}
	
	/**
	 * Has this class been initialized?
	 *
	 * @return If the class has been initialized.
	 * @since 2018/09/08
	 */
	public final boolean isInitialized()
	{
		synchronized (this)
		{
			return this._initialized;
		}
	}
	
	/**
	 * Is this the object class?
	 *
	 * @return If this is the object class.
	 * @since 2018/11/04
	 */
	public final boolean isObjectClass()
	{
		return this.name.toString().equals("java/lang/Object");
	}
	
	/**
	 * Checks if the given class is a super class of the this class.
	 *
	 * @param __cl The class to check.
	 * @return {@code true} if it is a superclass.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/09/09
	 */
	public final boolean isSuperClass(SpringClass __cl)
		throws NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException("NARG");
		
		for (SpringClass r = this.superclass; r != null; r = r.superClass())
			if (r == __cl)
				return true;
		
		return true;
	}
	
	/**
	 * Looks up the method which acts as the default constructor for instance
	 * objects.
	 *
	 * @return The default constructor for the object or {@code null} if there
	 * is none.
	 * @since 2018/09/08
	 */
	public final SpringMethod lookupDefaultConstructor()
	{
		try
		{
			return this.lookupMethodNonVirtual(new MethodNameAndType("<init>",
				"()V"));
		}
		catch (SpringNoSuchMethodException e)
		{
			return null;
		}
	}
	
	/**
	 * Locates the given field in this class.
	 *
	 * @param __static Is the field static?
	 * @param __name The name of the field.
	 * @param __desc The type of the field.
	 * @return The field.
	 * @throws NullPointerException On null arguments.
	 * @throws SpringNoSuchFieldException If the field does not exist.
	 * @since 2018/11/19
	 */
	public final SpringField lookupField(boolean __static, String __name,
		String __desc)
		throws NullPointerException, SpringNoSuchFieldException
	{
		if (__name == null || __desc == null)
			throw new NullPointerException("NARG");
		
		return this.lookupField(__static,
			new FieldName(__name), new FieldDescriptor(__desc));
	}
	
	/**
	 * Locates the given field in this class.
	 *
	 * @param __static Is the field static?
	 * @param __name The name of the field.
	 * @param __desc The type of the field.
	 * @return The field.
	 * @throws NullPointerException On null arguments.
	 * @throws SpringNoSuchFieldException If the field does not exist.
	 * @since 2018/09/09
	 */
	public final SpringField lookupField(boolean __static, FieldName __name,
		FieldDescriptor __desc)
		throws NullPointerException, SpringNoSuchFieldException
	{
		if (__name == null || __desc == null)
			throw new NullPointerException("NARG");
		
		return this.lookupField(__static,
			new FieldNameAndType(__name, __desc));
	}
	
	/**
	 * Locates the given field in this class.
	 *
	 * @param __static Is the field static?
	 * @param __nat The name and type of the field.
	 * @return The field.
	 * @throws NullPointerException On null arguments.
	 * @throws SpringNoSuchFieldException If the field does not exist.
	 * @since 2018/09/09
	 */
	public final SpringField lookupField(boolean __static,
		FieldNameAndType __nat)
		throws NullPointerException, SpringNoSuchFieldException
	{
		if (__nat == null)
			throw new NullPointerException("NARG");
		
		// Debug
		/*todo.DEBUG.note("Looking up field %s::%s (static=%b)", this.name,
			__nat, __static);*/
		
		// Field lookup starts at the current class, but also goes to the
		// super class for non-statics
		SpringField rv = this._fields.get(__nat);
		if (rv == null)
		{
			// Lookup non-static fields in super class
			if (!__static)
			{
				SpringClass sc = this.superclass;
				if (sc != null)
					return sc.lookupField(__static, __nat);
			}
			
			// {@squirreljme.error BK0v The specified field does not exist.
			// (The class which was looked in; The name and type of the field)}
			throw new SpringNoSuchFieldException(String.format("BK0v %s %s",
				this.name, __nat));
		}
		
		// {@squirreljme.error BK0w The specified field exists in the class
		// however it does not match being static. (The class the field is in;
		// The name and type of the method; If a static field was requested)}
		if (rv.isStatic() != __static)
			throw new SpringIncompatibleClassChangeException(String.format(
				"BK0w %s %s %s", this.name, __nat, __static));
		
		return rv;
	}
	
	/**
	 * Returns a field for the given index.
	 * 
	 * @param __fieldDx The field index.
	 * @return The field for the given index.
	 * @throws SpringNoSuchFieldException If the field does not exist.
	 * @since 2021/04/16
	 */
	public final SpringField lookupField(int __fieldDx)
		throws SpringNoSuchFieldException
	{
		SpringField[] lookup = this._fieldLookup;
		if (__fieldDx < 0 || __fieldDx >= lookup.length)
			throw new SpringNoSuchFieldException("No field: " + __fieldDx);
		
		return lookup[__fieldDx];
	}
	
	/**
	 * Locates the given method in the class.
	 *
	 * @param __static Is the method static?
	 * @param __name The name of the method.
	 * @param __desc The descriptor of the method.
	 * @return The method which was found.
	 * @throws NullPointerException On null arguments.
	 * @throws SpringIncompatibleClassChangeException If the target method
	 * does not match static-ness.
	 * @throws SpringNoSuchMethodException If the specified method does not
	 * exist.
	 * @since 2018/09/03
	 */
	public final SpringMethod lookupMethod(boolean __static, MethodName __name,
		MethodDescriptor __desc)
		throws NullPointerException, SpringIncompatibleClassChangeException,
			SpringNoSuchMethodException
	{
		if (__name == null || __desc == null)
			throw new NullPointerException("NARG");
		
		return this.lookupMethod(__static, new MethodNameAndType(__name,
			__desc));
	}
	
	/**
	 * Locates the given method in the class.
	 *
	 * @param __static Is the method static?
	 * @param __nat The name and type of the method.
	 * @return The method which was found.
	 * @throws NullPointerException On null arguments.
	 * @throws SpringIncompatibleClassChangeException If the target method
	 * does not match staticness.
	 * @throws SpringNoSuchMethodException If the specified method does not
	 * exist.
	 * @since 2018/09/03
	 */
	public final SpringMethod lookupMethod(boolean __static,
		MethodNameAndType __nat)
		throws NullPointerException, SpringIncompatibleClassChangeException,
			SpringNoSuchMethodException
	{
		if (__nat == null)
			throw new NullPointerException("NARG");
		
		// Debug
		/*todo.DEBUG.note("Looking up method %s::%s (static=%b)", this.name,
			__nat, __static);*/
		
		// {@squirreljme.error BK0x The specified method does not exist.
		// (The class which was looked in; The name and type of the method)} 
		SpringMethod rv = this._methods.get(__nat);
		if (rv == null)
			throw new SpringNoSuchMethodException(String.format("BK0x %s %s",
				this.name, __nat));
		
		// {@squirreljme.error BK0y The specified method exists in the class
		// however it does not match being static. (The class the method is in;
		// The name and type of the method; If a static method was requested)}
		if (rv.isStatic() != __static)
			throw new SpringIncompatibleClassChangeException(String.format(
				"BK0y %s %s %b", this.name, __nat, __static));
		
		return rv;
	}
	
	/**
	 * Looks up a method with the given index.
	 * 
	 * @param __methodDx The method index.
	 * @return The given method.
	 * @throws SpringNoSuchMethodException If no method by the given index
	 * exists.
	 * @since 2021/04/15
	 */
	public final SpringMethod lookupMethod(int __methodDx)
		throws SpringNoSuchMethodException
	{
		SpringMethod[] lookup = this._methodLookup;
		if (__methodDx < 0 || __methodDx >= lookup.length)
			throw new SpringNoSuchMethodException("No method: " + __methodDx);
		
		return lookup[__methodDx];
	}
	
	/**
	 * Looks up the specified method non-virtually.
	 *
	 * @param __nat The name and type.
	 * @return The target method.
	 * @throws NullPointerException On null arguments.
	 * @throws SpringIncompatibleClassChangeException If the target method
	 * is static.
	 * @throws SpringNoSuchMethodException If no method exists.
	 * @since 2018/09/09
	 */
	public final SpringMethod lookupMethodNonVirtual(MethodNameAndType __nat)
		throws NullPointerException, SpringIncompatibleClassChangeException,
			SpringNoSuchMethodException
	{
		if (__nat == null)
			throw new NullPointerException("NARG");
		
		// Debug
		/*todo.DEBUG.note("Looking up non-virtual method %s::%s.", this.name,
			__nat);*/
		
		// {@squirreljme.error BK0z The specified method does not exist, when
		// non-virtual lookup is used. (The class which was looked in; The
		// name and type of the method)} 
		SpringMethod rv = this._nonvirtmethods.get(__nat);
		if (rv == null)
			throw new SpringNoSuchMethodException(String.format("BK0z %s %s",
				this.name, __nat));
		
		// {@squirreljme.error BK10 Non-virtual method lookup found a static
		// method. (The class being looked in; The name and type requested)}
		if (rv.flags().isStatic())
			throw new SpringIncompatibleClassChangeException(String.format(
				"BK10 %s %s", this.name, __nat));
		
		return rv;
	}
	
	/**
	 * Returns the method lookup table.
	 * 
	 * @return The method lookup table.
	 * @since 2021/04/15
	 */
	public final SpringMethod[] methodLookup()
	{
		return this._methodLookup.clone();
	}
	
	/**
	 * Returns the name of this class.
	 *
	 * @return The name of this class.
	 * @since 2018/09/08
	 */
	public final ClassName name()
	{
		return this.name;
	}
	
	/**
	 * Sets the class as initialized.
	 *
	 * @throws SpringVirtualMachineException If the class has already been
	 * initialized.
	 * @since 2018/09/08
	 */
	public final void setInitialized()
		throws SpringVirtualMachineException
	{
		synchronized (this)
		{
			// {@squirreljme.error BK11 Class attempted to be initialized
			// twice. (This class)}
			if (this._initialized)
				throw new SpringVirtualMachineException(String.format(
					"BK11 %s", this.name));
			
			this._initialized = true;
		}
	}
	
	/**
	 * Returns the super class of this class.
	 *
	 * @return The super class of this class.
	 * @since 2018/09/08
	 */
	public final SpringClass superClass()
	{
		return this.superclass;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/09/15
	 */
	@Override
	public final String toString()
	{
		return this.name.toString();
	}
	
	/**
	 * Returns the root type, the base of the component.
	 *
	 * @return The root type of this type.
	 * @since 2018/09/27
	 */
	private SpringClass __rootType()
	{
		SpringClass rv = this;
		for (SpringClass r = this; r != null; r = r.component)
			rv = r;
		return rv;
	}
}

