// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.manifest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * This contains decoders for the standard Java manifest format.
 *
 * This class is immutable.
 *
 * @since 2016/05/20
 */
public final class JavaManifest
	extends AbstractMap<String, JavaManifestAttributes>
{
	/** The attributes defined in this manifest file. */
	private final Map<String, JavaManifestAttributes> _attributes;
	
	/**
	 * Initializes a blank manifest.
	 *
	 * @since 2018/02/10
	 */
	public JavaManifest()
	{
		// Initialize a blank set of main attributes
		Map<String, JavaManifestAttributes> backing = new HashMap<>();
		backing.put("", new JavaManifestAttributes());
		
		// Lock in the backing map
		this._attributes = backing;
	}
	
	/**
	 * Decodes the manifest from the given input stream, it is treated as
	 * UTF-8 as per the JAR specification.
	 *
	 * @param __is The input stream for the manifest data.
	 * @throws IOException On read errors.
	 * @throws JavaManifestException If the manifest is malformed.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/05/20
	 */
	public JavaManifest(InputStream __is)
		throws IOException, JavaManifestException, NullPointerException
	{
		this(new BufferedReader(
			new InputStreamReader(__is, "utf-8")));
	}
	
	/**
	 * Decodes the manifest from the given reader.
	 *
	 * @param __r The characters which make up the manifest.
	 * @throws IOException On read errors.
	 * @throws JavaManifestException If the manifest is malformed.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/22
	 */
	public JavaManifest(Reader __r)
		throws IOException, JavaManifestException, NullPointerException
	{
		// Check
		if (__r == null)
			throw new NullPointerException("NARG");
			
		// The backing map and temporary key/value pairs for each
		// attribute set
		String curname = "";
		Map<String, JavaManifestAttributes> backing = new LinkedHashMap<>();
		Map<JavaManifestKey, String> working = new LinkedHashMap<>();
		
		// Read input file line by line, since it is more efficient than
		// character by character
		StringBuilder vsb = new StringBuilder(128);
		BufferedReader br = new BufferedReader(__r);
		for (String pln = null;;)
		{
			// End of EOF
			String ln = (pln != null ? pln : br.readLine());
			pln = null;
			if (ln == null)
				break;
			
			// If the line is blank, it starts a new attribute block
			if (ln.isEmpty())
			{
				// Store the current working set
				if (working != null)
				{
					backing.put(curname, new JavaManifestAttributes(working));
					
					// It was stored in the map, so forget it
					curname = null;
					working = null;
				}
				
				// Skip blank line
				continue;
			}
			
			// This will be a name: value line, so find the colon on it
			// {@squirreljme.error BB01 Expected colon to appear on the
			// manifest line, to split a name/value pair.}
			int col = ln.indexOf(':');
			if (col < 0)
				throw new JavaManifestException("BB01");
			
			// Read key and value
			String key = ln.substring(0, col);
			
			// {@squirreljme.error BB02 Manifest key contains an invalid
			// character.}
			for (int i = 0, n = key.length(); i < n; i++)
				if (!JavaManifest.__isKeyChar(key.charAt(i)))
					throw new JavaManifestException(
						String.format("BB02 %s", key));
			
			// Need to skip the actual colon
			col++;
			
			// Skip spaces at the start of the value line
			int n = ln.length();
			while (col < n && ln.charAt(col) == ' ')
				col++;
			
			// Place value as it is now into a temporary buffer
			vsb.append(ln, col, n);
			
			// Read the next line to determine if it is a continuation
			for (;;)
			{
				// Stop at EOF
				pln = br.readLine();
				if (pln == null)
					break;
				
				// If this is a non-continuation line, it will be a blank
				// line or some other value, so redo the loop
				if (!pln.startsWith(" "))
					break;
				
				// Stop at the first non-space
				int nsp = 1;
				for (n = pln.length(); nsp < n; nsp++)
					if (pln.charAt(nsp) != ' ')
						break;
				
				// Append split into the buffer
				vsb.append(pln, nsp, n);
				
				// Clear the line, so it is not repeated
				pln = null;
			}
			
			// Build key and value
			JavaManifestKey ak = new JavaManifestKey(key);
			String av = vsb.toString();
			
			// Was this the start of a new section?
			if (curname == null)
			{
				// {@squirreljme.error BB03 New section must start with
				// {@code Name: value}. (The input section)}
				if (!"name".equals(ak.string))
					throw new JavaManifestException("BB03 " + ak);
				
				// The current name becomes the value
				curname = av;
				
				// Empty map to store values into
				working = new HashMap<>();
			}
			
			// Otherwise, add to the map
			else
				working.put(ak, av);
			
			// Clear the value
			vsb.setLength(0);
		}
		
		// Make sure the attribute is added to the set
		if (working != null)
			backing.put(curname, new JavaManifestAttributes(working));
		
		// Lock in the backing map
		this._attributes = backing;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/05/20
	 */
	@Override
	public boolean containsKey(Object __k)
	{
		return this._attributes.containsKey(__k);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/05/20
	 */
	@Override
	public Set<Map.Entry<String, JavaManifestAttributes>> entrySet()
	{
		return this._attributes.entrySet();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/05/20
	 */
	@Override
	public JavaManifestAttributes get(Object __k)
	{
		return this._attributes.get(__k);
	}
	
	/**
	 * Returns the mapping of main attributes.
	 *
	 * @return The main attribute mapping.
	 * @since 2016/05/29
	 */
	public JavaManifestAttributes getMainAttributes()
	{
		return this._attributes.get("");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/05/20
	 */
	@Override
	public int size()
	{
		return this._attributes.size();
	}
	
	/**
	 * Returns {@code true} if the specified character is an alpha-numeric
	 * character.
	 *
	 * @param __c The character to check.
	 * @return {@code true} if an alpha-numeric character.
	 * @since 2016/05/29
	 */
	private static boolean __isAlphaNum(char __c)
	{
		return (__c >= 'A' && __c <= 'Z') ||
			(__c >= 'a' && __c <= 'z') ||
			(__c >= '0' && __c <= '9');
	}
	
	/**
	 * Returns {@code true} if the character is part of a character which would
	 * be used for key values.
	 *
	 * @param __c The character to check.
	 * @return {@code true} if a key character.
	 * @since 2016/05/29
	 */
	private static boolean __isKeyChar(char __c)
	{
		return JavaManifest.__isAlphaNum(__c) || __c == '_' || __c == '-';
	}
}

