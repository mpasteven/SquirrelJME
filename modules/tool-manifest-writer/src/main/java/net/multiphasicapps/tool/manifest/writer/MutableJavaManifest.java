// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.tool.manifest.writer;

import cc.squirreljme.jvm.manifest.JavaManifest;
import cc.squirreljme.jvm.manifest.JavaManifestAttributes;
import cc.squirreljme.jvm.manifest.JavaManifestException;
import cc.squirreljme.jvm.manifest.JavaManifestKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * This is a mutable version of {@link JavaManifest}.
 *
 * This class is not thread safe.
 *
 * @since 2016/09/19
 */
public class MutableJavaManifest
	extends AbstractMap<String, MutableJavaManifestAttributes>
{
	/** The maximum number of columns a manifest may have. */
	private static final int _COLUMN_LIMIT =
		71;
	
	/** Main attributes. */
	protected final Map<String, MutableJavaManifestAttributes> attributes =
		new LinkedHashMap<>();
	
	/**
	 * This initializes a new empty manifest.
	 *
	 * @since 2016/09/19
	 */
	public MutableJavaManifest()
	{
		// Always add a main attribute
		this.attributes.put("", new MutableJavaManifestAttributes());
	}
	
	/**
	 * Initializes the mutable manifest using a copy of the data from an
	 * immutable manifest.
	 *
	 * @param __man The immutable manifest.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/12/26
	 */
	public MutableJavaManifest(JavaManifest __man)
		throws NullPointerException
	{
		// Check
		if (__man == null)
			throw new NullPointerException("NARG");
		
		// Go through and add
		for (Map.Entry<String, JavaManifestAttributes> e :
			__man.entrySet())
		{
			// Create new attribute set
			MutableJavaManifestAttributes attr;
			this.put(e.getKey(), (attr = new MutableJavaManifestAttributes()));
			
			// Copy values
			for (Map.Entry<JavaManifestKey, String> f :
				e.getValue().entrySet())
				attr.put(f.getKey(), f.getValue());
		}
		
		// If no main attributes were set then make sure they exist
		if (!this.containsKey(""))
			this.put("", new MutableJavaManifestAttributes());
	}
	
	/**
	 * Initializes the mutable manifest using a copy of the data from the
	 * given mutable manifest.
	 *
	 * @param __man The mutable manifest to copy from.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/12/26
	 */
	public MutableJavaManifest(MutableJavaManifest __man)
		throws NullPointerException
	{
		// Check
		if (__man == null)
			throw new NullPointerException("NARG");
		
		// Go through and add
		for (Map.Entry<String, MutableJavaManifestAttributes> e :
			__man.entrySet())
		{
			// Create new attribute set
			MutableJavaManifestAttributes attr;
			this.put(e.getKey(), (attr = new MutableJavaManifestAttributes()));
			
			// Copy values
			for (Map.Entry<JavaManifestKey, String> f :
				e.getValue().entrySet())
				attr.put(f.getKey(), f.getValue());
		}
		
		// If no main attributes were set then make sure they exist
		if (!this.containsKey(""))
			this.put("", new MutableJavaManifestAttributes());
	}
	
	/**
	 * Builds the specified manifest.
	 *
	 * @return The built manifest.
	 * @throws RuntimeException If the manifest could not be built.
	 * @since 2017/11/17
	 */
	public final JavaManifest build()
		throws RuntimeException
	{
		try
		{
			byte[] bytes;
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
			{
				// Write to output
				this.write(baos);
			
				// Extract array
				bytes = baos.toByteArray();
			}
		
			// Read in manifest
			try (InputStream is = new ByteArrayInputStream(bytes))
			{
				return new JavaManifest(is);
			}
		}
		
		// {@squirreljme.error AB01 Failed to build the immutable manifest
		// from the mutable one.}
		catch (IOException e)
		{
			throw new JavaManifestException("AB01", e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/09/19
	 */
	@Override
	public final Set<Map.Entry<String, MutableJavaManifestAttributes>>
		entrySet()
	{
		return this.attributes.entrySet();
	}
	
	/**
	 * Returns the mapping of main attributes.
	 *
	 * @return The main attribute mapping.
	 * @since 2016/09/19
	 */
	public final MutableJavaManifestAttributes getMainAttributes()
	{
		return this.get("");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/09/19
	 */
	@Override
	public final MutableJavaManifestAttributes put(String __k,
		MutableJavaManifestAttributes __v)
		throws NullPointerException
	{
		// Check
		if (__k == null || __v == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error AB02 The specified value is of the wrong
		// class type.}
		if (!(__v instanceof MutableJavaManifestAttributes))
			throw new ClassCastException("AB02");
		
		// Put
		return this.attributes.put(__k, __v);
	}
	
	/**
	 * Writes the manifest data to the given output stream.
	 *
	 * @param __os The stream to get the manifest data written to.
	 * @return {@code __os}.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/09/19
	 */
	public final OutputStream write(OutputStream __os)
		throws IOException, NullPointerException
	{
		if (__os == null)
			throw new NullPointerException("NARG");
		
		this.write(new OutputStreamWriter(__os, "utf-8"));
		
		return __os;
	}
	
	/**
	 * Writes the manifest data to the given output stream.
	 *
	 * @param __os The stream to get the manifest data written to.
	 * @return {@code __os}.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/09/19
	 */
	public final Appendable write(Appendable __os)
		throws IOException, NullPointerException
	{
		// Check
		if (__os == null)
			throw new NullPointerException("NARG");
		
		// Write main attribute first
		this.__write(__os, this.getMainAttributes());
		
		// Write other attributes
		for (Map.Entry<String, MutableJavaManifestAttributes> e :
			this.attributes.entrySet())
		{
			// Ignore the main attribute
			String k = e.getKey();
			if (k.equals(""))
				continue;
			
			// Sub-attributes are always spaced after the previous one
			__os.append("\r\n");
			
			// Write the name
			this.__write(__os, "Name", k);
			
			// Write values
			this.__write(__os, e.getValue());
		}
		
		// Java ME has no flushable so we only know two classes which are
		if (__os instanceof OutputStream)
			((OutputStream)__os).flush();
		else if (__os instanceof Writer)
			((Writer)__os).flush();
		
		return __os;
	}
	
	/**
	 * Writes attributes to the output.
	 *
	 * @param __w The stream to write to.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/09/19
	 */
	private void __write(Appendable __w, MutableJavaManifestAttributes __a)
		throws IOException, NullPointerException
	{
		// Check
		if (__w == null || __a == null)
			throw new NullPointerException("NARG");
		
		// The attribute version is always first
		JavaManifestKey verk = new JavaManifestKey("MANIFEST-VERSION");
		String ver = __a.get(verk);
		if (ver != null)
			this.__write(__w, "MANIFEST-VERSION", ver);
		else
			this.__write(__w, "MANIFEST-VERSION", "1.0");
		
		// Write all value
		for (Map.Entry<JavaManifestKey, String> e : __a.entrySet())
		{
			// Do not write the version twice
			JavaManifestKey k = e.getKey();
			if (verk.equals(k))
				continue;
			
			// Write pair
			this.__write(__w, k.inputString(), e.getValue());
		}
	}
	
	/**
	 * Writes the given key and value to the output.
	 *
	 * @param __w The stream to write to.
	 * @param __k The key to write.
	 * @param __v The value to write.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/09/19
	 */
	private void __write(Appendable __w, String __k, String __v)
		throws IOException, NullPointerException
	{
		// Check
		if (__w == null || __k == null || __v == null)
			throw new NullPointerException("NARG");
		
		// Write pair
		int col = 0;
		for (int z = 0; z < 2; z++)
		{
			String s = (z == 0 ? __k : __v);
			
			// Print it
			int n = s.length();
			for (int i = 0; i < n; i++)
			{
				// Ignore out of range characters
				char c = s.charAt(i);
				if (c < ' ')
					continue;
				
				// Would be on a new line?
				int nextcol = col + 1;
				boolean newline = false;
				if (nextcol >= MutableJavaManifest._COLUMN_LIMIT)
				{
					// If the current character is a space then it will
					// be lost on the following line.
					if (c == ' ')
						__w.append(' ');
					__w.append("\r\n");
					newline = true;
					
					// Indent next line with space as long as this is not
					// the last character being written
					__w.append(' ');
					
					// Set next column
					nextcol = 1;
				}
				
				// Write the character, but if a space was written early then
				// do not write it
				if ((c == ' ' && !newline) || c != ' ')
					__w.append(c);
				
				// Set new column
				col = nextcol;
			}
			
			// Add spacer
			if (z == 0)
			{
				__w.append(": ");
				col += 2;
			}
		}
		
		// Write newline
		__w.append("\r\n");
	}
}

