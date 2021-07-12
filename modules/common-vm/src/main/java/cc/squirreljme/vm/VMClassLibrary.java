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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * This class represents a class library which represents a single JAR file
 * whether it exists on the disk or is virtually provided.
 *
 * @since 2018/09/13
 */
public interface VMClassLibrary
{
	/**
	 * Lists the names of the resources in this library.
	 *
	 * @return The resource names.
	 * @since 2019/04/21
	 */
	String[] listResources();
	
	/**
	 * Returns the name of this library.
	 *
	 * @return The library name.
	 * @since 2018/09/13
	 */
	String name();
	
	/**
	 * Returns the file system path if it is known and valid. 
	 * 
	 * @return The file system path this uses on the disk, this will be
	 * {@code null} if not valid.
	 * @since 2021/06/13
	 */
	Path path();
	
	/**
	 * Opens the specified resource as a stream.
	 *
	 * @param __rc The name of the resource to open.
	 * @return The stream to the resource data or {@code null} if it is not
	 * valid.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/09/13
	 */
	InputStream resourceAsStream(String __rc)
		throws IOException, NullPointerException;
}
