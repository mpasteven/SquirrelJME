// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Jar Library for SummerCoat.
 *
 * @since 2020/11/23
 */
public class SummerCoatJarLibrary
	implements VMClassLibrary
{
	/** Special name for SummerCoat ROM chunk. */
	public static final String ROM_CHUNK_RESOURCE =
		"$$SQUIRRELJME$SUMMERCOAT$$";
	
	/** The path to the ROM. */
	protected final Path path;
	
	/**
	 * Initializes the ROM library.
	 * 
	 * @param __path The ROM path.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/23
	 */
	public SummerCoatJarLibrary(Path __path)
		throws NullPointerException
	{
		if (__path == null)
			throw new NullPointerException("NARG");
		
		this.path = __path;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/11/23
	 */
	@Override
	public String[] listResources()
	{
		// There is only ever a single resource
		return new String[]{SummerCoatJarLibrary.ROM_CHUNK_RESOURCE};
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/11/23
	 */
	@Override
	public String name()
	{
		return this.path.getFileName().toString();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/06/13
	 */
	@Override
	public Path path()
	{
		return this.path;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/11/23
	 */
	@Override
	public InputStream resourceAsStream(String __rc)
		throws IOException, NullPointerException
	{
		if (__rc == null)
			throw new NullPointerException("NARG");
		
		// Not our ROM chunk?
		if (!SummerCoatJarLibrary.ROM_CHUNK_RESOURCE.equals(__rc))
			return null;
		
		return Files.newInputStream(this.path, StandardOpenOption.READ);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/11/27
	 */
	@Override
	public String toString()
	{
		return this.name();
	}
	
	/**
	 * Checks if this is a SQC or not.
	 * 
	 * @param __s The file name.
	 * @return If this is a SQC ROM.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/27
	 */
	public static boolean isSqc(Path __s)
		throws NullPointerException
	{
		if (__s == null)
			throw new NullPointerException("NARG");
		
		return SummerCoatJarLibrary.isSqc(__s.toString());
	}
	
	/**
	 * Checks if this is a SQC or not.
	 * 
	 * @param __s The file name.
	 * @return If this is a SQC ROM.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/27
	 */
	public static boolean isSqc(String __s)
		throws NullPointerException
	{
		if (__s == null)
			throw new NullPointerException("NARG");
		
		return __s.endsWith(".sqc") || __s.endsWith(".SQC");
	}
}
