// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.classfile;

import cc.squirreljme.runtime.cldc.debug.Debugging;
import java.util.Objects;

/**
 * This represents the name of a class or array within the virtual machine.
 *
 * This class is immutable.
 *
 * @since 2017/09/27
 */
public class ClassName
	implements Comparable<ClassName>
{
	/** The binary name of the class. */
	protected final BinaryName binary;
	
	/** The field type of the class (for arrays). */
	protected final FieldDescriptor field;
	
	/** Is this considered primitive? */
	protected final boolean isprimitive;
	
	/**
	 * Initializes the class name.
	 *
	 * @param __n The input string.
	 * @throws InvalidClassFormatException If it is not a valid class name.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/09/27
	 */
	public ClassName(String __n)
		throws InvalidClassFormatException, NullPointerException
	{
		if (__n == null)
			throw new NullPointerException("NARG");
		
		// Is an array?
		if (__n.startsWith("["))
		{
			this.binary = null;
			this.field = new FieldDescriptor(__n);
			this.isprimitive = false;
		}
		
		// Not an array
		else
		{
			this.binary = new BinaryName(__n);
			this.field = null;
			
			// Consider this a primitive type
			switch (__n)
			{
				case "boolean":
				case "byte":
				case "short":
				case "char":
				case "int":
				case "long":
				case "float":
				case "double":
					this.isprimitive = true;
					break;
				
				default:
					this.isprimitive = false;
			}
		}
	}
	
	/**
	 * Adds dimensions to the class.
	 *
	 * @param __d The number of dimensions to add.
	 * @return The class with added dimensions.
	 * @throws IllegalArgumentException If the dimensions are negative.
	 * @since 2018/09/15
	 */
	public final ClassName addDimensions(int __d)
		throws IllegalArgumentException
	{
		if (__d == 0)
			return this;
		
		// {@squirreljme.error JC2j Cannot add negative dimensions.}
		if (__d < 0)
			throw new IllegalArgumentException("JC2j");
		
		// Going to be the same here but as a class name instead
		return this.field().addDimensions(__d).className();
	}
	
	/**
	 * Returns the binary name for this class.
	 *
	 * @return The class binary name or {@code null} if this is an array or
	 * primitive type.
	 * @since 2018/03/06
	 */
	public BinaryName binaryName()
	{
		return this.binary;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/09/27
	 */
	@Override
	public int compareTo(ClassName __o)
	{
		BinaryName ab = this.binary;
		if (ab != null)
		{
			BinaryName bb = __o.binary;
			if (bb == null)
				return -1;
			return ab.compareTo(bb);
		}
		
		FieldDescriptor af = this.field,
			bf = __o.field;
		if (bf == null)
			return 1;
		return af.compareTo(bf);
	}
	
	/**
	 * Returns the component type of this class name.
	 *
	 * @return The component type.
	 * @throws IllegalStateException If this is not an array.
	 * @since 2018/09/27
	 */
	public final ClassName componentType()
		throws IllegalStateException
	{
		// {@squirreljme.error JC2k This class is not an array, cannot get
		// the component type. (The name of this class)}
		if (!this.isArray())
			throw new IllegalStateException(String.format("JC2k %s", this));
		
		return this.field().componentType().className();
	}
	
	/**
	 * Returns the number of array dimensions that are used.
	 *
	 * @return The array dimensions, will return {@code 0} if not an array.
	 * @since 2018/09/28
	 */
	public final int dimensions()
	{
		if (!this.isArray())
			return 0;
		return this.field().dimensions();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/09/27
	 */
	@Override
	public boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (!(__o instanceof ClassName))
			return false;
		
		ClassName o = (ClassName)__o;
		return Objects.equals(this.binary, o.binary) &&
			Objects.equals(this.field, o.field);
	}
	
	/**
	 * Returns the field descriptor for this class.
	 *
	 * @return The class field descriptor.
	 * @since 2017/10/10
	 */
	public FieldDescriptor field()
	{
		// If this is a primitive type, treat as one
		BinaryName binary = this.binary;
		if (this.isprimitive)
			switch (binary.toString())
			{
				case "boolean":		return new FieldDescriptor("Z");
				case "byte":		return new FieldDescriptor("B");
				case "short":		return new FieldDescriptor("S");
				case "char":		return new FieldDescriptor("C");
				case "int":			return new FieldDescriptor("I");
				case "long":		return new FieldDescriptor("J");
				case "float":		return new FieldDescriptor("F");
				case "double":		return new FieldDescriptor("D");
				default:
					throw new RuntimeException("TODO");
			}
		
		// If just a binary name, convert
		if (binary != null)
			return new FieldDescriptor("L" + binary + ";");
		return this.field;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/09/27
	 */
	@Override
	public int hashCode()
	{
		BinaryName ab = this.binary;
		return (ab != null ? ab.hashCode() : this.field.hashCode());
	}
	
	/**
	 * Returns the package that this class is within. Primitive types and
	 * arrays are not part of any package.
	 *
	 * @return The package or {@code null} if it is not in the package.
	 * @since 2017/10/09
	 */
	public BinaryName inPackage()
	{
		BinaryName b = this.binary;
		if (b != null)
			return b.inPackage();
		return null;
	}
	
	/**
	 * Does this class refer to an array type?
	 *
	 * @return If this is an array type.
	 * @since 2017/10/08
	 */
	public boolean isArray()
	{
		FieldDescriptor field = this.field;
		return field != null && field.isArray();
	}
	
	/**
	 * Is the other class in the same package as this class?
	 *
	 * @param __b The other class to check.
	 * @return If the other class is in this same package.
	 * @since 2017/10/11
	 */
	public boolean isInSamePackage(ClassName __b)
		throws NullPointerException
	{
		if (__b == null)
			throw new NullPointerException("NARG");
		
		return Objects.equals(this.inPackage(), __b.inPackage());
	}
	
	/**
	 * Checks if this is the object class.
	 * 
	 * @return If this is the object class.
	 * @since 2020/12/21
	 */
	public boolean isObjectClass()
	{
		return this.toString().equals("java/lang/Object");
	}
	
	/**
	 * Does this class refer to a primitive type?
	 *
	 * @return If this is a primitive type.
	 * @since 2017/10/08
	 */
	public boolean isPrimitive()
	{
		FieldDescriptor field = this.field;
		return this.isprimitive || field != null && field.isPrimitive();
	}
	
	/**
	 * Returns the primitive type of this class, if there is one.
	 *
	 * @return The primitive type or {@code null} if it is not one.
	 * @since 2018/09/16
	 */
	public final PrimitiveType primitiveType()
	{
		// If not a binary name but a field, then it can never be a primitive
		// type
		BinaryName binary = this.binary;
		if (binary == null)
			return null;
		
		switch (binary.toString())
		{
			case "boolean":		return PrimitiveType.BOOLEAN;
			case "byte":		return PrimitiveType.BYTE;
			case "short":		return PrimitiveType.SHORT;
			case "char":		return PrimitiveType.CHARACTER;
			case "int":			return PrimitiveType.INTEGER;
			case "long":		return PrimitiveType.LONG;
			case "float":		return PrimitiveType.FLOAT;
			case "double":		return PrimitiveType.DOUBLE;
		}
		
		return null;
	}
	
	/**
	 * Returns the root component type of this class name, that is this class
	 * with all of the dimensions taken away.
	 *
	 * @return The component type.
	 * @throws IllegalStateException If this is not an array.
	 * @since 2021/01/31
	 */
	public final ClassName rootComponentType()
		throws IllegalStateException
	{
		// {@squirreljme.error JC4w This class is not an array, cannot get
		// the root component type. (The name of this class)}
		if (!this.isArray())
			throw new IllegalStateException(String.format("JC4w %s", this));
		
		// Keep going down until we are at the root.
		ClassName rv;
		for (rv = this; rv.isArray();)
			rv = rv.componentType();
		
		return rv;
	}
	
	/**
	 * Returns the simple name of the class.
	 * 
	 * @return The simple name of the class.
	 * @throws IllegalStateException If this is a primitive or array.
	 * @since 2020/11/28
	 */
	public ClassIdentifier simpleName()
		throws IllegalStateException
	{
		// {@squirreljme.error JC0g Cannot get the simple name of a primitive
		// or array type.}
		if (this.isArray() || this.isPrimitive())
			throw new IllegalStateException("JC0g");
		return this.binary.simpleName();
	}
	
	/**
	 * Returns rhe runtime string as if it were called for
	 * {@link Class#getName()}.
	 *
	 * @return The runtime string as if it were called for
	 * {@link Class#getName()}.
	 * @since 2020/06/14
	 */
	public String toRuntimeString()
	{
		BinaryName binary = this.binary;
		if (!this.isArray() && binary != null)
			return binary.toString().replace('/', '.');
		return this.toString().replace('/', '.');
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/09/27
	 */
	@Override
	public String toString()
	{
		BinaryName binary = this.binary;
		if (binary != null)
			return binary.toString();
		return this.field.toString();
	}
	
	/**
	 * Translates the primitive type to the given class type.
	 *
	 * @param __t The type to convert.
	 * @return The name of the class for this primitive.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/09/16
	 */
	public static ClassName fromPrimitiveType(PrimitiveType __t)
		throws NullPointerException
	{
		if (__t == null)
			throw new NullPointerException("NARG");
		
		// Depends on the primitive type
		switch (__t)
		{
			case BOOLEAN:
				return new ClassName("boolean");
			
			case BYTE:
				return new ClassName("byte");
			
			case SHORT:
				return new ClassName("short");
			
			case CHARACTER:
				return new ClassName("char");
			
			case INTEGER:
				return new ClassName("int");
			
			case LONG:
				return new ClassName("long");
			
			case FLOAT:
				return new ClassName("float");
			
			case DOUBLE:
				return new ClassName("double");
			
			default:
				throw Debugging.oops();
		}
	}
	
	/**
	 * Checks if this class name is valid or not.
	 *
	 * @param __cn The class-name to check.
	 * @return If it is valid or not.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/12/22
	 */
	public static boolean isValidClassName(String __cn)
		throws NullPointerException
	{
		if (__cn == null)
			throw new NullPointerException("NARG");
		
		try
		{
			ClassName cn = new ClassName(__cn);
			return true;
		}
		catch (InvalidClassFormatException e)
		{
			return false;
		}
	}
}

