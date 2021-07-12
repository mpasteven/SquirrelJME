// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.summercoat.ld.pack;

import cc.squirreljme.jvm.mle.brackets.JarPackageBracket;
import cc.squirreljme.jvm.summercoat.constants.ClassInfoConstants;
import cc.squirreljme.jvm.summercoat.constants.JarProperty;
import cc.squirreljme.jvm.summercoat.constants.JarTocProperty;
import cc.squirreljme.jvm.summercoat.ld.mem.MemoryUtils;
import cc.squirreljme.jvm.summercoat.ld.mem.ReadableMemory;
import cc.squirreljme.jvm.summercoat.ld.mem.ReadableMemoryInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This represents a Jar, or some other abstract file, within the ROM.
 *
 * @since 2021/04/04
 */
public class JarRom
	implements JarPackageBracket
{
	/** The JAR data. */
	protected final ReadableMemory data;
	
	/** The JAR flags. */
	protected final int flags;
	
	/** The JAR name. */
	protected final String name;
	
	/** Header structure, dynamically load. */
	private HeaderStruct _header;
	
	/** Table of contents, dynamically loaded. */
	private TableOfContents _toc;
	
	/** The names of the entries, dynamically loaded. */
	private String[] _entries;
	
	/**
	 * Initializes the Jar ROM.
	 * 
	 * @param __flags The flags for the JAR.
	 * @param __name The name of the JAR.
	 * @param __data The data for the JAR.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/04/04
	 */
	public JarRom(int __flags, String __name, ReadableMemory __data)
		throws NullPointerException
	{
		if (__name == null || __data == null)
			throw new NullPointerException("NARG");
		
		this.flags = __flags;
		this.name = __name;
		this.data = __data;
	}
	
	/**
	 * Returns all of the entries within the JAR.
	 * 
	 * @return All of the entries within the JAR.
	 * @since 2021/05/16
	 */
	public final String[] entries()
	{
		// Already loaded?
		String[] rv = this._entries;
		if (rv != null)
			return rv.clone();
		
		// Look through the TOC
		TableOfContents toc = this.__toc();
		ReadableMemory data = this.data;
		rv = new String[toc.count()];
		for (int i = 0, n = toc.count(); i < n; i++)
		{
			rv[i] = MemoryUtils.loadString(data,
				toc.get(i, JarTocProperty.OFFSET_NAME));
		}
		
		// Cache it for later and use the calculated result
		this._entries = rv;
		return rv.clone();
	}
	
	/**
	 * Potentially loads and returns the header.
	 * 
	 * @return The header.
	 * @throws InvalidRomException If the ROM is not valid.
	 * @since 2021/04/07
	 */
	public HeaderStruct header()
		throws InvalidRomException
	{
		HeaderStruct rv = this._header;
		if (rv != null)
			return rv;
		
		// Decode the header
		try (ReadableMemoryInputStream in =
			new ReadableMemoryInputStream(this.data))
		{
			// {@squirreljme.error Invalid JAR magic number.}
			rv = HeaderStruct.decode(in, JarProperty.NUM_JAR_PROPERTIES);
			if (rv.magicNumber != ClassInfoConstants.JAR_MAGIC_NUMBER)
				throw new InvalidRomException("ZZ57");
			
			this._header = rv;
		}
		
		// {@squirreljme.error ZZ56 The JAR header is corrupt.}
		catch (IOException e)
		{
			throw new InvalidRomException("ZZ56", e);
		}
		
		return rv;
	}
	
	/**
	 * Returns the index of the given resource
	 * 
	 * @param __rc The entry to look for.
	 * @return The index of the given resource or a negative value if not
	 * found.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/05/16
	 */
	public final int indexOf(String __rc)
		throws NullPointerException
	{
		if (__rc == null)
			throw new NullPointerException("NARG");
		
		ReadableMemory data = this.data;
		
		// We need the hash code to do a quicker lookup of entries
		int hash = __rc.hashCode();
		
		// Look through the TOC
		TableOfContents toc = this.__toc();
		for (int i = 0, n = toc.count(); i < n; i++)
		{
			// Not this hash code?
			if (hash != toc.get(i, JarTocProperty.INT_NAME_HASHCODE))
				continue;
			
			// This is not the entry we are looking for?
			if (0 != MemoryUtils.strCmp(__rc, data, 
					toc.get(i, JarTocProperty.OFFSET_NAME)))
				continue;
			
			return i;
		}
		
		// Not found
		return -1;
	}
	
	/**
	 * Opens the resource by the given index.
	 * 
	 * @param __dx The entry to open.
	 * @return The memory which makes up the given resource.
	 * @throws IndexOutOfBoundsException If the index is not valid.
	 * @since 2021/05/16
	 */
	public final ReadableMemory openResourceAsMemory(int __dx)
		throws IndexOutOfBoundsException
	{
		// {@squirreljme.error ZZ59 Jar index out of bounds. (The index)}
		TableOfContents toc = this.__toc();
		if (__dx < 0 || __dx >= toc.count())
			throw new IndexOutOfBoundsException("ZZ59 " + __dx);
			
		// Return a stream over the entry data
		return this.data.subSection(
			toc.get(__dx, JarTocProperty.OFFSET_DATA),
			toc.get(__dx, JarTocProperty.SIZE_DATA));
	}
	
	/**
	 * Opens the resource by the given index.
	 * 
	 * @param __dx The entry to open.
	 * @return An input stream for the given resource.
	 * @throws IndexOutOfBoundsException If the index is not valid.
	 * @since 2021/05/16
	 */
	public final InputStream openResourceAsStream(int __dx)
		throws IndexOutOfBoundsException
	{
		return new ReadableMemoryInputStream(this.openResourceAsMemory(__dx));
	}
	
	/**
	 * Opens a stream to the resource data.
	 * 
	 * @param __rc The resource to load.
	 * @return The input stream over the resource or {@code null} if not found.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/04/07
	 */
	public final InputStream openResourceAsStream(String __rc)
		throws NullPointerException
	{
		if (__rc == null)
			throw new NullPointerException("NARG");
		
		// Make sure the entry exists
		int dx = this.indexOf(__rc);
		if (dx < 0)
			return null;
		
		// Open the data
		return this.openResourceAsStream(dx);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/06
	 */
	@Override
	public final String toString()
	{
		return this.name;
	}
	
	/**
	 * Potentially loads and returns the table of contents.
	 * 
	 * @return The table of contents.
	 * @throws InvalidRomException If the ROM is not valid.
	 * @since 2021/04/07
	 */
	private TableOfContents __toc()
	{
		TableOfContents rv = this._toc;
		if (rv != null)
			return rv;
		
		// Load the header
		HeaderStruct header = this.header();
		
		// Load in the table of contents
		rv = new TableOfContents(this.data.subSection(
			header.getProperty(JarProperty.OFFSET_TOC),
			header.getProperty(JarProperty.SIZE_TOC)));
		
		// Cache and use it
		this._toc = rv;
		return rv;
	}
}
