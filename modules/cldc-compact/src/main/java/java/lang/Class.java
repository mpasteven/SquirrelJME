// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package java.lang;

import cc.squirreljme.jvm.mle.JarPackageShelf;
import cc.squirreljme.jvm.mle.ObjectShelf;
import cc.squirreljme.jvm.mle.TypeShelf;
import cc.squirreljme.jvm.mle.brackets.JarPackageBracket;
import cc.squirreljme.jvm.mle.brackets.TypeBracket;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import java.io.InputStream;

/**
 * This class is the in-language representation of a Java class, the CLDC
 * allows for minimal reflection via {@link Class#forName(String)} and
 * {@link Class#newInstance()}.
 *
 * @since 2018/12/08
 */
public final class Class<T>
{
	/** This is the prefix that is used for assertion checks. */
	private static final String _ASSERTION_PREFIX =
		"cc.squirreljme.runtime.noassert.";
	
	/** The type shelf for this class. */
	private final TypeBracket _type;
	
	/** Has the assertion status been checked already? */
	private volatile boolean _checkedAssert;
	
	/** Is this class being asserted? */
	private volatile boolean _useAssert;
	
	/**
	 * Initializes the class reference holder.
	 *
	 * @param __type The type shelf to set this as.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/06/07
	 */
	private Class(TypeBracket __type)
		throws NullPointerException
	{
		if (__type == null)
			throw new NullPointerException("NARG");
		
		this._type = __type;
	}
	
	/**
	 * This checks whether the specified input class extends this class or
	 * implements an interface and then returns this class object which is
	 * "cast" to the specified type. Note that this does not change the
	 * returned value.
	 *
	 * @param <U> The sub-class to cast this class object to.
	 * @param __cl A class which is checked to see if it extends or implements
	 * this class.
	 * @return {@code this} except cast to the specified sub-class
	 * @throws ClassCastException If the specified class is not a sub-class of
	 * this class type.
	 * @throws NullPointerException On null arguments.
	 * @see Class#isAssignableFrom(Class)
	 * @since 2016/06/13
	 */
	@SuppressWarnings({"unchecked"})
	public <U> Class<? extends U> asSubclass(Class<U> __cl)
		throws ClassCastException, NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error ZZ0v The specified class is not a sub-class
		// of this class. (The class being checked; The current class)}
		if (!this.isAssignableFrom(__cl))
			throw new ClassCastException("ZZ0v " + __cl + " " + this);
		
		return (Class<? extends U>)this;
	}
	
	/**
	 * Casts the object to this class, checking it.
	 *
	 * @param __o The object to cast.
	 * @return The casted object.
	 * @throws ClassCastException If the type is not matched.
	 * @since 2018/09/29
	 */
	@SuppressWarnings({"unchecked"})
	public T cast(Object __o)
		throws ClassCastException
	{
		// Null always casts OK
		if (__o == null)
			return null;
		
		// {@squirreljme.error ZZ0w The other class cannot be casted to this
		// class. (This class; The other class)}
		Class<?> other = __o.getClass();
		if (!this.isAssignableFrom(other))
			throw new ClassCastException("ZZ0w " + this.getName() + " " +
				other.getName());
		
		return (T)__o;
	}
	
	/**
	 * Returns whether or not assertions should be enabled in the specified
	 * class, this is used internally by the virtual machine to determine if
	 * assertions should fail or not.
	 *
	 * In SquirrelJME, this defaults to returning {@code true}. To disable
	 * assertions for a class or an entire package then the following system
	 * property may be specified to disable them:
	 * {@code cc.squirreljme.noassert.(package)(.class)=true}.
	 *
	 * @return In SquirrelJME this returns by default {@code true}, otherwise
	 * this may return {@code false} if they are disabled for a class.
	 * @since 2016/06/13
	 */
	public boolean desiredAssertionStatus()
	{
		// If assertions have been checked, they do not have to be rechecked
		if (this._checkedAssert)
			return this._useAssert;
		
		// Otherwise check it
		return this.__checkAssertionStatus();
	}
	
	/**
	 * Returns the name of this class.
	 *
	 * @return The name of this class.
	 * @since 2018/09/22
	 */
	public String getName()
	{
		return TypeShelf.runtimeName(this._type);
	}
	
	/**
	 * Obtains a resource from the classpath which exists within a JAR file,
	 * inside of a directory, or in a prepacked resource. If a resource needs
	 * to be obtain from another class which exists in another JAR file then
	 * this method must be called from a class in that JAR.
	 *
	 * In the Java ME environment, one should not rely on getting resources
	 * which are executable class files (files ending in .class). These class
	 * files may be deleted during native compilation. This however should not
	 * be relied upon.
	 *
	 * Using this method on the classes for primitive types ({@code int.class})
	 * and using a relative name will always result in the path being treated
	 * as absolute.
	 *
	 * Relative paths are converted to absolute paths by appending the name
	 * to the binary name of the class package.
	 *
	 * @param __name The name of the resource to find, if this starts with a
	 * forward slash {@code '/'} then it is treated as an absolute path.
	 * Otherwise a resource will be derived from the calling class.
	 * @return A stream to the given resource or {@code null} if one was not
	 * found.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/03/01
	 */
	public InputStream getResourceAsStream(String __name)
		throws NullPointerException
	{
		// Check
		if (__name == null)
			throw new NullPointerException("NARG");
		
		// Do not lookup blank resources
		if (__name.isEmpty())
			return null;
			
		// If a resource starts with slash then it is treated as being an
		// absolute reference, otherwise it will depend on the package the
		// class is in
		String want;
		if (__name.charAt(0) == '/')
			want = __name.substring(1);
		else
		{
			String binName = TypeShelf.binaryPackageName(this._type);
			
			if (binName.isEmpty())
				want = __name;
			else
				want = binName + "/" + __name;
		}
		
		// If our class is within a JAR try to search our own JAR first
		JarPackageBracket inJar = TypeShelf.inJar(this._type);
		if (inJar != null)
		{
			InputStream rv = JarPackageShelf.openResource(inJar, want);
			if (rv != null)
				return rv;
		}
		
		// Otherwise search every JAR in the classpath for the given resource
		for (JarPackageBracket jar : JarPackageShelf.classPath())
		{
			InputStream rv = JarPackageShelf.openResource(jar, want);
			if (rv != null)
				return rv;
		}
		
		// Not found
		return null;
	}
	
	/**
	 * Returns the class which is the superclass of this class.
	 *
	 * @return The superclass or {@code null} if there is none.
	 * @since 2017/03/29
	 */
	public Class<? super T> getSuperclass()
	{
		TypeBracket rv = TypeShelf.superClass(this._type);
		return (rv == null ? null : TypeShelf.typeToClass(rv));
	}
	
	/**
	 * Returns {@code true} if this class represents an array type.
	 *
	 * @return {@code true} if this class represents an array type.
	 * @since 2016/06/16
	 */
	public boolean isArray()
	{
		return TypeShelf.isArray(this._type);
	}
	
	/**
	 * Checks if the given class can be assigned to this one, the check is
	 * in the same order as {@code instanceof Object} that is
	 * {@code b.getClass().isAssignableFrom(a.getClass()) == (a instanceof b)}
	 * and {@code (Class<B>)a} does not throw {@link ClassCastException}.
	 *
	 * @param __cl The other class type.
	 * @return If the other class can be assigned to this one.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/09/27
	 */
	@SuppressWarnings("EqualsBetweenInconvertibleTypes")
	public boolean isAssignableFrom(Class<?> __cl)
		throws NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException("NARG");
		
		// Quick determination if this is the same type
		if (this == __cl)
			return true;
			
		return TypeShelf.isAssignableFrom(this._type,
			TypeShelf.classToType(__cl));
	}
	
	/**
	 * Is this class an interface?
	 *
	 * @return If this is an interface.
	 * @since 2018/11/03
	 */
	public boolean isInterface()
	{
		return TypeShelf.isInterface(this._type);
	}
	
	/**
	 * Checks if the given class is an instance of this class.
	 *
	 * @param __o The object to check.
	 * @return If the given object is an instance of this class.
	 * @since 2018/09/27
	 */
	public boolean isInstance(Object __o)
	{
		// Null will never be an instance
		if (__o == null)
			return false;
		
		// This is in the same form
		return this.isAssignableFrom(__o.getClass());
	}
	
	/**
	 * Constructs a new instance of this class.
	 *
	 * @throws InstantiationException If the default constructor cannot be
	 * accessed by the calling method.
	 * @throws IllegalAccessException If the class or constructor could not
	 * be accessed.
	 * @return The newly created instance.
	 * @since 2018/12/04
	 */
	@SuppressWarnings({"unchecked", "RedundantThrows"})
	public T newInstance()
		throws InstantiationException, IllegalAccessException
	{
		Debugging.todoNote("Implement newInstance() access checks.");
		
		Object rv = ObjectShelf.newInstance(this._type);
		if (rv == null)
			throw new OutOfMemoryError("OOME");
		
		return (T)rv;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/11/03
	 */
	@Override
	public String toString()
	{
		// Arrays and primitive types essentially just use the binary name
		TypeBracket type = this._type;
		if (TypeShelf.isArray(type) || TypeShelf.isPrimitive(type))
			return TypeShelf.binaryName(type);
		
		return (this.isInterface() ? "interface " : "class ") + this.getName();
	}
	
	/**
	 * This checks whether assertions should be **disabled** for this class (or
	 * for the entire package).
	 *
	 * @return The assertions status to use.
	 * @since 2016/10/09
	 */
	private boolean __checkAssertionStatus()
	{
		// Default to true
		boolean rv = true;
		
		// Determine class name
		String cn = this.getName();
		String prop = Class._ASSERTION_PREFIX + cn;
		
		// Disabled for this class?
		if (Boolean.getBoolean(prop))
			rv = false;
		
		// Disabled for this package?
		else
		{
			// Find last dot, if there is none then this is just the default
			// package so never bother checking the package
			int ld = cn.lastIndexOf('.');
			if (ld > 0 && Boolean.getBoolean(prop.substring(0,
				prop.length() - (cn.length() - ld))))
				rv = false;
		}
		
		// Set as marked
		this._checkedAssert = true;
		this._useAssert = rv;
		return rv;
	}
	
	/**
	 * Constructs a new instance of this class.
	 *
	 * @throws InstantiationException If the default constructor cannot be
	 * accessed by the calling method.
	 * @throws IllegalAccessException If the class or constructor could not
	 * be accessed.
	 * @since 2018/12/04
	 */
	@SuppressWarnings("RedundantThrows")
	final Object __newInstance()
		throws InstantiationException, IllegalAccessException
	{
		throw Debugging.todo();
		/*
		// Get class details
		ClassData data = this._data;
		String binaryName = data.binaryName();
		
		// {@squirreljme.error ZZ0x Cannot construct new instance of class
		// because it has no default constructor.}
		StaticMethod sm = data.defaultConstructorMethod();
		if (sm == null)
			throw new InstantiationException("ZZ0x " + binaryName);
		
		// Allocate class instance
		Object rv = ObjectAccess.allocateObject(binaryName);
		
		// {@squirreljme.error ZZ0y Could not allocate new instance.}
		if (rv == null)
			throw new OutOfMemoryError("ZZ0y");
		
		// Call default constructor
		ObjectAccess.invokeStatic(sm, rv);
		
		// All done!
		return rv;
		*/
	}
	
	/**
	 * Locates the class with the given name and returns it, otherwise an
	 * exception is thrown.
	 *
	 * The expected form of the class is a name as is mostly used in the
	 * Java language ({@code some.package.Foo}) and not one that is internal
	 * to the virtual machine except in the case of an array. Inner classes do
	 * not follow dot notation, an inner class is usually separated by a dollar
	 * sign '$'. For example {@code Map.Entry} is {@code java.util.Map$Entry}.
	 *
	 * If an array is requested then it must only be of a primitive type using
	 * a Java internal type descriptor.
	 *
	 * @param __n The name of the class to find.
	 * @return The class with the given name.
	 * @throws ClassNotFoundException If the given class was not found.
	 * @throws NullPointerException If no name was specified.
	 * @since 2016/03/01
	 */
	public static Class<?> forName(String __n)
		throws ClassNotFoundException
	{
		// No class specified
		if (__n == null)
			throw new NullPointerException();
		
		// {@squirreljme.error ZZ0z Could not find the specified class. (The
		// name of the class)}
		TypeBracket found = TypeShelf.findType(
			__n.replace('.', '/'));
		if (found == null)
			throw new ClassNotFoundException("ZZ0z " + __n);
		
		// The name will have to be converted to binary form since that is
		// what is internally used
		return TypeShelf.typeToClass(found);
	}
}

