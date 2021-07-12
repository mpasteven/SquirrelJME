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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import net.multiphasicapps.collections.UnmodifiableArrayList;
import net.multiphasicapps.io.SizeLimitedInputStream;

/**
 * This represents a class file which was most likely derived from the binary
 * class file format of a class.
 *
 * @since 2017/09/26
 */
public final class ClassFile
	implements Annotated, HasAccessibleFlags
{
	/** The magic number of the class file. */
	private static final int _MAGIC_NUMBER =
		0xCAFEBABE;
	
	/** The version of this class. */
	protected final ClassVersion version;
	
	/** The flags for this class. */
	protected final ClassFlags classflags;
	
	/** The name of this class. */
	protected final ClassName thisname;
	
	/** The class this extends. */
	protected final ClassName supername;
	
	/** The annotation table of the class. */
	protected final AnnotationTable annotations;
	
	/** The referenced inner classes. */
	protected final InnerClasses innerclasses;
	
	/** The source file the class is in. */
	protected final String sourcefilename;
	
	/** The interfaces this class implements. */
	protected final ClassNames interfaces;
	
	/** The fields within this class. */
	private final Field[] _fields;
	
	/** The methods within this class. */
	private final Method[] _methods;
	
	/**
	 * Initializes the class file.
	 *
	 * @param __ver The version of the class.
	 * @param __cf The flags for this class.
	 * @param __tn The name of this class.
	 * @param __sn The class this class extends, may be {@code null}.
	 * @param __in The interfaces this class implements.
	 * @param __fs The fields in this class.
	 * @param __ms The methods in this class.
	 * @param __icl Defined inner classes.
	 * @param __at Annotations that are declared on the class.
	 * @param __sfn Source file name.
	 * @throws InvalidClassFormatException If the class is not valid.
	 * @throws NullPointerException On null arguments, except for {@code __sn}.
	 * @since 2017/09/26
	 */
	ClassFile(ClassVersion __ver, ClassFlags __cf, ClassName __tn,
		ClassName __sn, ClassName[] __in, Field[] __fs, Method[] __ms,
		InnerClasses __icl, AnnotationTable __at, String __sfn)
		throws InvalidClassFormatException, NullPointerException
	{
		if (__ver == null || __cf == null || __tn == null ||
			__in == null || __fs == null || __ms == null || __icl == null ||
			__at == null)
			throw new NullPointerException("NARG");
		
		// Check sub-arrays for null
		for (Object[] foo : new Object[][]{(__in = __in.clone()),
			(__fs = __fs.clone()), (__ms = __ms.clone())})
			for (Object f : foo)
				if (f == null)
					throw new NullPointerException("NARG");
		
		// {@squirreljme.error JC29 Either Object has a superclass which it
		// cannot extend any class or any other class does not have a super
		// class. Additionally primitive types cannot have a super class.
		// (The current class name; The super class name; Object class name;
		// Is this primitive?)}
		ClassName objectcn = new ClassName("java/lang/Object");
		if ((__tn.isPrimitive() ||
			__tn.equals(objectcn)) != (__sn == null))
			throw new InvalidClassFormatException(String.format(
				"JC29 %s %s %s %s",
				__tn, __sn, objectcn, __tn.isPrimitive()));
		
		// Set
		this.version = __ver;
		this.classflags = __cf;
		this.thisname = __tn;
		this.supername = __sn;
		this.innerclasses = __icl;
		this.annotations = __at;
		this.interfaces = new ClassNames(__in);
		this._fields = __fs;
		this._methods = __ms;
		this.sourcefilename = __sfn;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/03/06
	 */
	@Override
	public final AnnotationTable annotationTable()
	{
		return this.annotations;
	}
	
	/**
	 * Returns the fields within this class.
	 *
	 * @return The class fields.
	 * @since 2017/10/12
	 */
	public final List<Field> fields()
	{
		return UnmodifiableArrayList.<Field>of(this._fields);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/10/09
	 */
	@Override
	public final ClassFlags flags()
	{
		return this.classflags;
	}
	
	/**
	 * Returns the inner classes.
	 *
	 * @return The inner classes.
	 * @since 2018/06/16
	 */
	public final InnerClasses innerClasses()
	{
		return this.innerclasses;
	}
	
	/**
	 * Returns the names of implemented interfaces.
	 *
	 * @return The implemented interface names.
	 * @since 2017/10/09
	 */
	public final ClassNames interfaceNames()
	{
		return this.interfaces;
	}
	
	/**
	 * Methods which exist within this class.
	 *
	 * @return The class methods.
	 * @since 2017/10/09
	 */
	public final Method[] methods()
	{
		return this._methods.clone();
	}
	
	/**
	 * Returns the source file the class is in.
	 *
	 * @return The source file the class is in or {@code null} if it is not
	 * in one.
	 * @since 2018/09/08
	 */
	public final String sourceFile()
	{
		return this.sourcefilename;
	}
	
	/**
	 * Returns the name of the super class.
	 *
	 * @return The name of the super class.
	 * @since 2017/10/09
	 */
	public final ClassName superName()
	{
		return this.supername;
	}
	
	/**
	 * Returns the name of the current class.
	 *
	 * @return The current class name.
	 * @since 2017/10/02
	 */
	public final ClassName thisName()
	{
		return this.thisname;
	}
	
	/**
	 * Returns the type of class this is.
	 *
	 * @return The class type.
	 * @since 2018/05/14
	 */
	public final ClassType type()
	{
		ClassFlags flags = this.classflags;
		if (flags.isEnum())
			return ClassType.ENUM;
		else if (flags.isAnnotation())
			return ClassType.ANNOTATION;
		else if (flags.isInterface())
			return ClassType.INTERFACE;
		return ClassType.CLASS;
	}
	
	/**
	 * Returns the class version.
	 *
	 * @return The class version.
	 * @since 2019/04/14
	 */
	public final ClassVersion version()
	{
		return this.version;
	}
	
	/**
	 * Initializes a class file which is a special representation of the
	 * following field descriptor which is either an array or a primitive
	 * type.
	 *
	 * @param __d The descriptor to create a special class for.
	 * @return The generated class file from the specified descriptor.
	 * @throws IllegalArgumentException If the descriptor is not an array
	 * or a pritimive type.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/10/09
	 */
	public static ClassFile special(FieldDescriptor __d)
		throws IllegalArgumentException, NullPointerException
	{
		if (__d == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error JC2a Cannot create a special class because it
		// is not an array or primitive type. (The descriptor)}
		if (!__d.isArray() && !__d.isPrimitive())
			throw new IllegalArgumentException(String.format("JC2a %s", __d));
		
		// Pre-composed parts to fake things
		ClassFlags cflags = new ClassFlags(ClassFlag.PUBLIC, ClassFlag.FINAL,
			ClassFlag.SUPER, ClassFlag.SYNTHETIC);
		Method[] methods = new Method[0];
		
		// Use the names of the types in the language
		ClassName name;
		boolean isPrimitive = __d.isPrimitive();
		if (isPrimitive)
			name = ClassName.fromPrimitiveType(__d.primitiveType());
		
		// Something else
		else
			name = __d.className();
		
		// Build
		return new ClassFile(ClassVersion.MAX_VERSION, cflags, name,
			(isPrimitive ? null : new ClassName("java/lang/Object")),
			new ClassName[0], new Field[0], methods, new InnerClasses(),
			new AnnotationTable(), "<special>");
	}
	
	/**
	 * This parses the input stream as a class file and returns the
	 * representation of that class file.
	 *
	 * @param __is The input stream to source classes from.
	 * @return The decoded class file.
	 * @throws InvalidClassFormatException If the class file is not formatted
	 * correctly.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/09/26
	 */
	public static ClassFile decode(InputStream __is)
		throws InvalidClassFormatException, IOException, NullPointerException
	{
		// Check
		if (__is == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error JC2b The magic number for the class is not
		// valid. (The read magic number; The expected magic number)}
		DataInputStream in = new DataInputStream(__is);
		int magic = in.readInt();
		if (magic != ClassFile._MAGIC_NUMBER)
			throw new InvalidClassFormatException(String.format(
				"JC2b %08x %08x", magic, ClassFile._MAGIC_NUMBER));
		
		// {@squirreljme.error JC2c The version number of the input class
		// file is not valid. (The version number)}
		int cver = in.readShort() | (in.readShort() << 16);
		ClassVersion version = ClassVersion.findVersion(cver);
		if (version == null)
			throw new InvalidClassFormatException(String.format("JC2c %d.%d",
				cver >>> 16, (cver & 0xFFFF)));
		
		// Decode the constant pool
		Pool pool = Pool.decode(in);
		
		// Decode flags
		ClassFlags classflags = new ClassFlags(in.readUnsignedShort());
		
		// Name of the current class
		ClassName thisname = pool.<ClassName>require(ClassName.class,
			in.readUnsignedShort());
		
		// Read super class
		ClassName supername = pool.<ClassName>get(ClassName.class,
			in.readUnsignedShort());
		
		// Read interfaces
		int icount = in.readUnsignedShort();
		ClassName[] interfaces = new ClassName[icount];
		for (int i = 0; i < icount; i++)
			interfaces[i] = pool.<ClassName>require(ClassName.class,
				in.readUnsignedShort());
		
		// Read fields
		Field[] fields = Field.decode(version, thisname, classflags, pool, in);
		
		// Read methods
		Method[] methods = Method.decode(version, thisname, classflags, pool,
			in);
		
		// Read in attributes
		AttributeTable attrs = AttributeTable.parse(pool, in);
		
		// Read annotation table, if it is valid and exists
		AnnotationTable annotations = AnnotationTable.parse(pool, attrs);
		
		// Parse inner classes
		InnerClasses innerclasses = InnerClasses.parse(pool, attrs);
		
		// {@squirreljme.error JC2d Expected end of the class to follow the
		// attributes in the class. (The name of this class)}
		if (in.read() >= 0)
			throw new InvalidClassFormatException(
				String.format("JC2d %s", thisname));
		
		// Source file name, if any
		Attribute attr = attrs.get("SourceFile");
		String sourcefilename = null;
		if (attr != null)
			try (DataInputStream ai = attr.open())
			{
				sourcefilename = pool.<UTFConstantEntry>require(
					UTFConstantEntry.class, ai.readUnsignedShort()).toString();
			}
		
		// Build
		return new ClassFile(version, classflags, thisname, supername,
			interfaces, fields, methods, innerclasses, annotations,
			sourcefilename);
	}
	
	/**
	 * Reads the next attribute from the class.
	 *
	 * @param __in The input stream where bytes come from.
	 * @param __pool The constant pool.
	 * @param __aname The output name of the attribute which was just read.
	 * @param __alens The length of the attribute.
	 * @return The stream to the attribute which just has been read.
	 * @throws IOException On read errors.
	 * @throws InvalidClassFormatException If the attribute is not correct.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/04/09
	 */
	@Deprecated
	static DataInputStream __nextAttribute(DataInputStream __in,
		Pool __pool, String[] __aname, int[] __alens)
		throws InvalidClassFormatException, IOException, NullPointerException
	{
		// Check
		if (__aname == null || __alens == null)
			throw new NullPointerException("NARG");
		
		// The name is not parsed here
		__aname[0] = __pool.<UTFConstantEntry>require(UTFConstantEntry.class,
			__in.readUnsignedShort()).toString();
		
		// {@squirreljme.error JC2e Attribute exceeds 2GiB in length. (The
		// size of the attribute)}
		int len = __in.readInt();
		if (len < 0)
			throw new InvalidClassFormatException(String.format("JC2e %d",
				len & 0xFFFFFFFFL));
		
		// Used as a hint
		__alens[0] = len;
		
		// Setup reader
		return new DataInputStream(new SizeLimitedInputStream(__in, len, true,
			false));
	}
}

