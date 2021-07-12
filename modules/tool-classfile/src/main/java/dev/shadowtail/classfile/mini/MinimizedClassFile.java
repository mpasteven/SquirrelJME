// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.mini;

import cc.squirreljme.jvm.summercoat.constants.ClassInfoConstants;
import dev.shadowtail.classfile.pool.DualClassRuntimePool;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import net.multiphasicapps.classfile.ClassFlags;
import net.multiphasicapps.classfile.ClassName;
import net.multiphasicapps.classfile.ClassNames;
import net.multiphasicapps.classfile.FieldDescriptor;
import net.multiphasicapps.classfile.FieldName;
import net.multiphasicapps.classfile.FieldNameAndType;
import net.multiphasicapps.classfile.InvalidClassFormatException;
import net.multiphasicapps.classfile.MethodDescriptor;
import net.multiphasicapps.classfile.MethodName;
import net.multiphasicapps.classfile.MethodNameAndType;

/**
 * This contains the minimized class file which is a smaller format of the
 * {@link net.multiphasicapps.classfile.ClassFile} that is more optimized to
 * virtual machines for usage.
 *
 * @since 2019/03/10
 */
public final class MinimizedClassFile
{
	/** Header. */
	public final MinimizedClassHeader header;
	
	/** Pool. */
	public final DualClassRuntimePool pool;
	
	/** Static fields. */
	private final MinimizedField[] _sfields;
	
	/** Instance fields. */
	private final MinimizedField[] _ifields;
	
	/** Static methods. */
	private final MinimizedMethod[] _smethods;
	
	/** Instance methods. */
	private final MinimizedMethod[] _imethods;
	
	/** Class flags. */
	private Reference<ClassFlags> _flags;
	
	/**
	 * Initializes the minimized class file.
	 *
	 * @param __h The header.
	 * @param __p The pool.
	 * @param __sf Static fields.
	 * @param __if Instance fields.
	 * @param __sm Static methods.
	 * @param __im Instance methods.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/17
	 */
	public MinimizedClassFile(MinimizedClassHeader __h,
		DualClassRuntimePool __p,
		MinimizedField[] __sf, MinimizedField[] __if,
		MinimizedMethod[] __sm, MinimizedMethod[] __im)
		throws NullPointerException
	{
		if (__h == null || __p == null || __sf == null || __if == null ||
			__sm == null || __im == null)
			throw new NullPointerException("NARG");
		
		// Set
		this.header = __h;
		this.pool = __p;
		this._sfields = (__sf = __sf.clone());
		this._ifields = (__if = __if.clone());
		this._smethods = (__sm = __sm.clone());
		this._imethods = (__im = __im.clone());
		
		// Check for nulls
		for (Object[] va : new Object[][]{__sf, __if, __sm, __im})
			for (Object v : va)
				if (v == null)
					throw new NullPointerException("NARG");
	}
	
	/**
	 * Searches for a field by the given name and type.
	 *
	 * @param __static Search for static field?
	 * @param __n The name.
	 * @param __t The type.
	 * @return The field or {@code null} if not found.
	 * @since 2019/04/22
	 */
	public final MinimizedField field(boolean __static, FieldName __n,
		FieldDescriptor __t)
		throws NullPointerException
	{
		if (__n == null || __t == null)
			throw new NullPointerException("NARG");
		
		return this.field(__static, new FieldNameAndType(__n, __t));
	}
	
	/**
	 * Searches for a field by the given name and type.
	 *
	 * @param __static Search for static field?
	 * @param __nat The name and type.
	 * @return The field or {@code null} if not found.
	 * @since 2019/04/22
	 */
	public final MinimizedField field(boolean __static, FieldNameAndType __nat)
		throws NullPointerException
	{
		if (__nat == null)
			throw new NullPointerException("NARG");
		
		for (MinimizedField mf : (__static ? this._sfields : this._ifields))
			if (mf.name.equals(__nat.name()) && mf.type.equals(__nat.type()))
				return mf;
		
		return null;
	}
	
	/**
	 * Returns the fields in the class.
	 *
	 * @param __static If true then static fields are returned.
	 * @return The fields.
	 * @since 2019/04/17
	 */
	public final MinimizedField[] fields(boolean __static)
	{
		return (__static ? this._sfields.clone() : this._ifields.clone());
	}
	
	/**
	 * The class flags.
	 *
	 * @return The class flags.
	 * @since 2019/04/17
	 */
	public final ClassFlags flags()
	{
		Reference<ClassFlags> ref = this._flags;
		ClassFlags rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._flags = new WeakReference<>((rv =
				new ClassFlags(this.header.getClassflags())));
		
		return rv;
	}
	
	/**
	 * Returns the names of all the interfaces.
	 *
	 * @return The interface names.
	 * @since 2019/04/17
	 */
	public final ClassNames interfaceNames()
	{
		int idx = this.header.getClassints();
		if (idx == 0)
			return new ClassNames();
		return this.pool.getByIndex(false, idx).
			<ClassNames>value(ClassNames.class);
	}
	
	/**
	 * Searches for a method by the given name and type.
	 *
	 * @param __n The name.
	 * @param __t The type, if {@code null} the type is ignored.
	 * @return The method or {@code null} if not found.
	 * @since 2019/04/22
	 */
	public final MinimizedMethod method(MethodName __n, MethodDescriptor __t)
		throws NullPointerException
	{
		if (__n == null || __t == null)
			throw new NullPointerException("NARG");
		
		// Static first, then instance methods
		MinimizedMethod rv = this.method(true, __n, __t);
		return (rv == null ? this.method(false, __n, __t) : rv);
	}
	
	/**
	 * Searches for a method by the given name and type.
	 *
	 * @param __static Search for static method?
	 * @param __n The name.
	 * @param __t The type, if {@code null} the type is ignored.
	 * @return The method or {@code null} if not found.
	 * @since 2019/04/22
	 */
	public final MinimizedMethod method(boolean __static, MethodName __n,
		MethodDescriptor __t)
		throws NullPointerException
	{
		if (__n == null)
			throw new NullPointerException("NARG");
		
		for (MinimizedMethod mm : (__static ? this._smethods : this._imethods))
			if (mm.name.equals(__n) && (__t == null || mm.type.equals(__t)))
				return mm;
		
		// Not found
		return null;
	}
	
	/**
	 * Searches for a method by the given name and type.
	 *
	 * @param __static Search for static method?
	 * @param __nat The name and type.
	 * @return The method or {@code null} if not found.
	 * @since 2019/04/22
	 */
	public final MinimizedMethod method(boolean __static,
		MethodNameAndType __nat)
		throws NullPointerException
	{
		if (__nat == null)
			throw new NullPointerException("NARG");
		
		for (MinimizedMethod mm : (__static ? this._smethods : this._imethods))
			if (mm.name.equals(__nat.name()) && mm.type.equals(__nat.type()))
				return mm;
		
		return null;
	}
	
	/**
	 * Returns the methods in the class.
	 *
	 * @param __static If true then static methods are returned.
	 * @return The methods.
	 * @since 2019/04/17
	 */
	public final MinimizedMethod[] methods(boolean __static)
	{
		return (__static ? this._smethods.clone() : this._imethods.clone());
	}
	
	/**
	 * Returns the minimized constant pool.
	 *
	 * @return The minimized constant pool.
	 * @since 2019/04/17
	 */
	public final DualClassRuntimePool pool()
	{
		return this.pool;
	}
	
	/**
	 * Returns the super name of the current class.
	 *
	 * @return The class super name or {@code null} if there is none.
	 * @since 2019/04/17
	 */
	public final ClassName superName()
	{
		int sdx = this.header.getClasssuper();
		if (sdx == 0)
			return null;
		return this.pool.getByIndex(false, sdx).
			<ClassName>value(ClassName.class);
	}
	
	/**
	 * Returns the name of the current class.
	 *
	 * @return The name of the current class.
	 * @since 2019/04/17
	 */
	public final ClassName thisName()
	{
		return this.pool.getByIndex(false, this.header.getClassname()).
			<ClassName>value(ClassName.class);
	}
	
	/**
	 * Decodes and returns the minimized representation of the class file.
	 *
	 * @param __is The stream to decode from.
	 * @return The resulting minimized class.
	 * @throws InvalidClassFormatException If the class is not formatted
	 * correctly.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/09/07
	 */
	public static MinimizedClassFile decode(InputStream __is)
		throws InvalidClassFormatException, IOException, NullPointerException
	{
		return MinimizedClassFile.decode(__is, null);
	}
	
	/**
	 * Decodes and returns the minimized representation of the class file.
	 *
	 * @param __is The stream to decode from.
	 * @param __dPool The parent pool, may be {@code null}.
	 * @return The resulting minimized class.
	 * @throws InvalidClassFormatException If the class is not formatted
	 * correctly.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/03/10
	 */
	public static MinimizedClassFile decode(InputStream __is,
		DualClassRuntimePool __dPool)
		throws InvalidClassFormatException, IOException, NullPointerException
	{
		if (__is == null)
			throw new NullPointerException("NARG");
		
		// Initialize byte array with guess to how many bytes can be read
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream(
			Math.min(1048576, __is.available())))
		{
			// Read all the data size bytes
			byte[] buf = new byte[8192];
			for (;;)
			{
				int rc = __is.read(buf);
				
				// EOF?
				if (rc < 0)
					break;
				
				baos.write(buf, 0, rc);
			}
			
			// Finish off
			return MinimizedClassFile.decode(baos.toByteArray(), __dPool);
		}
	}
	
	/**
	 * Decodes and returns the minimized representation of the class file.
	 *
	 * @param __is The bytes to decode from.
	 * @return The resulting minimized class.
	 * @throws InvalidClassFormatException If the class is not formatted
	 * correctly.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/09/07
	 */
	public static MinimizedClassFile decode(byte[] __is)
		throws InvalidClassFormatException, IOException, NullPointerException
	{
		return MinimizedClassFile.decode(__is, null);
	}
	
	/**
	 * Decodes and returns the minimized representation of the class file.
	 *
	 * @param __is The bytes to decode from.
	 * @param __ppool The parent pool, may be {@code null}.
	 * @return The resulting minimized class.
	 * @throws InvalidClassFormatException If the class is not formatted
	 * correctly.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/16
	 */
	public static MinimizedClassFile decode(byte[] __is,
		DualClassRuntimePool __ppool)
		throws InvalidClassFormatException, IOException, NullPointerException
	{
		if (__is == null)
			throw new NullPointerException("NARG");
		
		// Reading could fail
		try
		{
			// Read class header
			MinimizedClassHeader header = MinimizedClassHeader.decode(
				new ByteArrayInputStream(__is));
			
			// {@squirreljme.error JC01 Length of class file does not match
			// length of array. (The file length; The array length)}
			int fsz = header.getFilesize();
			if (fsz != __is.length)
				throw new InvalidClassFormatException("JC01 " + fsz +
					" " + __is.length);
			
			// {@squirreljme.error JC02 End of file magic number is invalid.
			// (The read magic number; The expected value; The file size)}
			int endmagic;
			if (ClassInfoConstants.CLASS_END_MAGIC_NUMBER !=
				(endmagic = (((__is[fsz - 4] & 0xFF) << 24) |
				((__is[fsz - 3] & 0xFF) << 16) |
				((__is[fsz - 2] & 0xFF) << 8) |
				(__is[fsz - 1] & 0xFF))))
				throw new InvalidClassFormatException(
					String.format("JC02 %08x %08x %d", endmagic,
						ClassInfoConstants.CLASS_END_MAGIC_NUMBER, fsz));
			
			// Virtual constant pool which relies on a parent one
			DualClassRuntimePool pool;
			if (header.getStaticpoolsize() < 0 ||
				header.getRuntimepoolsize() < 0)
			{
				// {@squirreljme.error JC4h No parent pool was specified.}
				if (__ppool == null)
					throw new NullPointerException("JC4h");
					
				pool = DualPoolEncoder.decodeLayered(__is,
					header.getStaticpooloff(), -header.getStaticpoolsize(),
					header.getRuntimepooloff(), -header.getRuntimepoolsize(),
					__ppool);
			}
			
			// Decode physical pool within the class
			else
			{
				pool = DualPoolEncoder.decode(__is, header.getStaticpooloff(),
					header.getStaticpoolsize(), header.getRuntimepooloff(),
					header.getRuntimepoolsize());
			}
			
			// Read static and instance fields
			MinimizedField[] sfields = MinimizedField.decodeFields(
				header.getSfcount(), pool, __is, header.getSfoff(),
				header.getSfsize()),
				ifields = MinimizedField.decodeFields(header.getIfcount(), pool, __is,
					header.getIfoff(), header.getIfsize());
			
			// Read static and instance methods
			MinimizedMethod[] smethods = MinimizedMethod.decodeMethods(
				header.getSmcount(), pool, __is, header.getSmoff(),
				header.getSmsize()),
				imethods = MinimizedMethod.decodeMethods(header.getImcount(), pool, __is,
					header.getImoff(), header.getImsize());
			
			// Build final class
			return new MinimizedClassFile(header, pool,
				sfields, ifields, smethods, imethods);
		}
		
		// {@squirreljme.error JC03 End of file reached before parsing the
		// file.}
		catch (EOFException e)
		{
			throw new InvalidClassFormatException("JC03", e); 
		}
	}
}

