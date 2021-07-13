// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.aot.summercoat;

import cc.squirreljme.jvm.aot.CompileSettings;
import cc.squirreljme.jvm.aot.LinkGlob;
import cc.squirreljme.vm.InMemoryClassLibrary;
import dev.shadowtail.jarfile.JarMinimizer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This is a linked glob for SummerCoat.
 *
 * @since 2020/11/22
 */
public class SummerCoatLinkGlob
	implements LinkGlob
{
	/** The settings used. */
	protected final CompileSettings settings;
	
	/** The name of the glob. */
	protected final String name;
	
	/** Where the glob writes to. */
	protected final OutputStream out;
	
	/** Resources that are within the glob. */
	private final Map<String, byte[]> _resources =
		new LinkedHashMap<>();
	
	/**
	 * Initializes the glob.
	 * 
	 * @param __settings Compiler settings.
	 * @param __name The name of this glob.
	 * @param __out The output where the glob is to be written.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/11/22
	 */
	public SummerCoatLinkGlob(CompileSettings __settings, String __name,
		OutputStream __out)
		throws NullPointerException
	{
		this.settings = __settings;
		this.name = __name;
		this.out = __out;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/11/22
	 */
	@Override
	public void finish()
		throws IOException
	{
		// Perform bulk minimization, since SummerCoat has to do it all at
		// the same time
		JarMinimizer.minimize(this.settings.isBootLoader,
			new InMemoryClassLibrary(this.name, this._resources), this.out);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/11/22
	 */
	@Override
	public void join(String __name, boolean __isRc, InputStream __data)
		throws IOException, NullPointerException
	{
		if (__name == null || __data == null)
			throw new NullPointerException("NARG");
		
		// Try to find a reasonable buffer size
		int avail = __data.available();
		if (avail == 0)
			avail = 16384;
		
		// Try to load in as much as possible
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream(avail))
		{
			// Copy buffer
			byte[] buf = new byte[16384];
			for (;;)
			{
				int rc = __data.read(buf);
				
				if (rc < 0)
					break;
				
				baos.write(buf, 0, rc);
			}
			
			// The minimizer for SummerCoat does processing of libraries all at
			// once, so we need to just cache for later
			this._resources.put(__name, baos.toByteArray());
		}
	}
}
