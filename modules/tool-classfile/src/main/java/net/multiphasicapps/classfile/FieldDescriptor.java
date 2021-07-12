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
import dev.shadowtail.classfile.xlate.DataType;

/**
 * This represents the type descriptor of a field.
 *
 * @since 2017/06/12
 */
public final class FieldDescriptor
	implements Comparable<FieldDescriptor>, MemberDescriptor
{
	/** The short type. */
	public static final FieldDescriptor SHORT =
		new FieldDescriptor("S");
	
	/** The integer type. */
	public static final FieldDescriptor INTEGER =
		new FieldDescriptor("I");
	
	/** String representation. */
	protected final String string;
	
	/** Is this a primitive type? */
	protected final boolean primitive;
	
	/** Array dimensions. */
	protected final int dimensions;
	
	/** The component type. */
	protected final FieldDescriptor component;
	
	/** The class this refers to. */
	protected final ClassName classname;
	
	/**
	 * Initializes the field descriptor.
	 *
	 * @param __n The field descriptor to decode.
	 * @throws InvalidClassFormatException If it is not a valid field descriptor.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/06/12
	 */
	public FieldDescriptor(String __n)
		throws InvalidClassFormatException, NullPointerException
	{
		// Check
		if (__n == null)
			throw new NullPointerException("NARG");
		
		// Set
		this.string = __n;
		
		// {@squirreljme.error JC2q The field descriptor cannot be blank. (The
		// field descriptor)}
		int n = __n.length();
		if (n <= 0)
			throw new InvalidClassFormatException(
				String.format("JC2q %s", __n));
		
		// Depends on the first character
		char c = __n.charAt(0);
		switch (c)
		{
				// Primitive
			case 'B':
			case 'C':
			case 'D':
			case 'F':
			case 'I':
			case 'J':
			case 'S':
			case 'Z':
				this.primitive = true;
				this.dimensions = 0;
				this.component = null;
				this.classname = null;
				break;
			
				// Array
			case '[':
				this.primitive = false;
				this.classname = null;
				
				// Count dimensions
				int dims = 0;
				for (int i = 0; i < n; i++)
					if (__n.charAt(i) != '[')
						break;
					else
						dims++;
				this.dimensions = dims;
				
				// Parse component
				this.component = new FieldDescriptor(__n.substring(1));
				break;
				
				// Class
			case 'L':
				this.primitive = false;
				this.dimensions = 0;
				this.component = null;
				
				// {@squirreljme.error JC2r The field descriptor for a class
				// must end with a semicolon. (The field descriptor)}
				if (';' != __n.charAt(n - 1))
					throw new InvalidClassFormatException(
						String.format("JC2r %s", __n));
				
				// Decode
				this.classname = new ClassName(__n.substring(1, n - 1));
				break;
				
				// {@squirreljme.error JC2s The field descriptor is not valid.
				// (The field descriptor)}
			default:
				throw new InvalidClassFormatException(
					String.format("JC2s %s", __n));
		}
	}
	
	/**
	 * Adds dimensions to the field descriptor.
	 *
	 * @param __d The number of dimensions to add.
	 * @return The field descriptor with added dimensions.
	 * @throws IllegalArgumentException If the dimensions are negative.
	 * @since 2018/09/15
	 */
	public final FieldDescriptor addDimensions(int __d)
		throws IllegalArgumentException
	{
		if (__d == 0)
			return this;
		
		// {@squirreljme.error JC2t Cannot add negative dimensions.}
		if (__d < 0)
			throw new IllegalArgumentException("JC2t");
		
		// Prepend string with brackets, to declare a new array
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < __d; i++)
			sb.append('[');
		
		// Rebuild field
		sb.append(this.toString());
		return new FieldDescriptor(sb.toString());
	}
	
	/**
	 * Returns the name of the used class.
	 *
	 * @return The used class or {@code null} if a class is not referred to and
	 * this is a primitive type.
	 * @since 2018/09/01
	 */
	public final ClassName className()
	{
		// If this is an array then the class name will be the array descriptor
		if (this.dimensions > 0)
			return new ClassName(this.toString());
		
		// Otherwise as normal class (or primitive representation)
		if (this.primitive)
			return ClassName.fromPrimitiveType(this.primitiveType());
		return this.classname;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/10/02
	 */
	@Override
	public int compareTo(FieldDescriptor __o)
	{
		return this.string.compareTo(__o.string);
	}
	
	/**
	 * Returns the component type of the array if this is one.
	 *
	 * @return The component type or {@code null} if this is not one.
	 * @since 2018/09/27
	 */
	public final FieldDescriptor componentType()
	{
		return this.component;
	}
	
	/**
	 * Returns the data type of this descriptor.
	 * 
	 * @return The data type of this descriptor.
	 * @since 2021/01/12
	 */
	public DataType dataType()
	{
		return DataType.of(this);
	}
	
	/**
	 * Returns the number of dimensions in this class.
	 *
	 * @return The number of dimensions in the class.
	 * @since 2018/09/28
	 */
	public final int dimensions()
	{
		return this.dimensions;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/06/12
	 */
	@Override
	public boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		// Check
		if (!(__o instanceof FieldDescriptor))
			return false;
		
		return this.string.equals(((FieldDescriptor)__o).string);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/06/12
	 */
	@Override
	public int hashCode()
	{
		return this.string.hashCode();
	}
	
	/**
	 * Is this an array type?
	 *
	 * @return {@code true} if an array type.
	 * @since 2017/10/08
	 */
	public boolean isArray()
	{
		return this.component != null;
	}
	
	/**
	 * Is this a primitive type?
	 *
	 * @return {@code true} if this is a primitive type.
	 * @since 2017/07/28
	 */
	public boolean isPrimitive()
	{
		return this.primitive;
	}
	
	/**
	 * Is this an object type?
	 *
	 * @return If this is an object type.
	 * @since 2017/09/16
	 */
	public boolean isObject()
	{
		return !this.isPrimitive();
	}
	
	/**
	 * Returns if this is a wide field or not.
	 *
	 * @return If this is a wide field.
	 * @since 2019/02/05
	 */
	public final boolean isWide()
	{
		if (this.isObject())
			return false;
		switch (this.primitiveType())
		{
			case LONG:
			case DOUBLE:
				return true;
			
			default:
				return false;
		}
	}
	
	/**
	 * Returns the primitive type for this field.
	 *
	 * @return The primitive type to use or {@code null} if there is none.
	 * @since 2017/10/16
	 */
	public PrimitiveType primitiveType()
	{
		// Quick detect
		if (!this.primitive)
			return null;
		
		// Depends on the string
		switch (this.toString())
		{
			case "B": return PrimitiveType.BYTE;
			case "C": return PrimitiveType.CHARACTER;
			case "D": return PrimitiveType.DOUBLE;
			case "F": return PrimitiveType.FLOAT;
			case "I": return PrimitiveType.INTEGER;
			case "J": return PrimitiveType.LONG;
			case "S": return PrimitiveType.SHORT;
			case "Z": return PrimitiveType.BOOLEAN;
			default:
				return null;
		}
	}
	
	/**
	 * Returns the simple storage type of the field.
	 *
	 * @return The simple storage type for this field.
	 * @since 2018/09/15
	 */
	public final SimpleStorageType simpleStorageType()
	{
		// Objects
		if (this.isObject())
			return SimpleStorageType.OBJECT;
		
		// Primitive types, these are promoted
		switch (this.primitiveType())
		{
			case BOOLEAN:
			case BYTE:
			case SHORT:
			case CHARACTER:
			case INTEGER:
				return SimpleStorageType.INTEGER;
			
			case LONG:
				return SimpleStorageType.LONG;
			
			case FLOAT:
				return SimpleStorageType.FLOAT;
			
			case DOUBLE:
				return SimpleStorageType.DOUBLE;
			
				// Should not occur
			default:
				throw Debugging.oops();
		}
	}
	
	/**
	 * Returns the width of this field on the stack.
	 *
	 * @return The width of the field on the stack.
	 * @since 2019/02/05
	 */
	public final int stackWidth()
	{
		return (this.isWide() ? 2 : 1);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/06/12
	 */
	@Override
	public String toString()
	{
		return this.string;
	}
}

