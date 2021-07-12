// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

/**
 * This is a class library resource which is based on class resources.
 *
 * Resource lookup is done on the {@link #actingclass}, all resource
 * accesses are done by having a prefix that is before all accesses of
 * entires. The prefix is so that multiple virtual JARs may exist within a
 * single JAR.
 *
 * The list of resources in the JAR exists in:
 * {@code ${prefix}/META-INF/squirreljme/resources.list}.
 *
 * @since 2018/11/14
 */
public final class ResourceBasedClassLibrary
	implements VMClassLibrary
{
	/** The class to get resources from. */
	protected final Class<?> actingclass;
	
	/** The prefix for entries. */
	protected final String prefix;
	
	/** The name of this library. */
	protected final String name;
	
	/** Resources in this class. */
	private String[] _resources;
	
	/**
	 * Initializes the resource based class library.
	 *
	 * @param __act The class to act on.
	 * @param __pre The prefix for lookup.
	 * @param __name The name of the library.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/15
	 */
	public ResourceBasedClassLibrary(Class<?> __act, String __pre,
		String __name)
		throws NullPointerException
	{
		if (__act == null || __pre == null || __name == null)
			throw new NullPointerException("NARG");
		
		this.actingclass = __act;
		this.prefix = __pre;
		this.name = __name;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 */
	@Override
	public final String[] listResources()
	{
		// Pre-cached?
		String[] rv = this._resources;
		if (rv != null)
			return rv.clone();
		
		// Load in the resource list
		List<String> list = new LinkedList<>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
			this.resourceAsStream("META-INF/squirreljme/resources.list"),
			"utf-8")))
		{
			String ln;
			while ((ln = br.readLine()) != null)
			{
				// Blank lines are not resources
				if (ln.isEmpty())
					continue;
				
				// Add otherwise
				list.add(ln);
			}
		}
		
		// {@squirreljme.error AK09 Could not load resource list.}
		catch (IOException e)
		{
			throw new RuntimeException("AK09");
		}
		
		// Cache it and return
		this._resources = (rv = list.<String>toArray(new String[list.size()]));
		return rv.clone();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/11/14
	 */
	@Override
	public final String name()
	{
		return this.name;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/06/13
	 */
	@Override
	public Path path()
	{
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/11/14
	 */
	@Override
	public final InputStream resourceAsStream(String __rc)
		throws IOException, NullPointerException
	{
		if (__rc == null)
			throw new NullPointerException("NARG");
		
		return this.actingclass.getResourceAsStream(
			this.prefix + __rc);
	}
}

