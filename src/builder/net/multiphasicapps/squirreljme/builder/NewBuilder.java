// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) 2013-2016 Steven Gawroriski <steven@multiphasicapps.net>
//     Copyright (C) 2013-2016 Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// For more information see license.mkd.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirreljme.builder;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import net.multiphasicapps.sjmepackages.PackageInfo;
import net.multiphasicapps.sjmepackages.PackageList;
import net.multiphasicapps.squirreljme.basicassets.BasicAsset;
import net.multiphasicapps.squirreljme.jit.JITNamespaceProcessor;
import net.multiphasicapps.squirreljme.jit.JITNamespaceProcessorProgress;
import net.multiphasicapps.squirreljme.jit.JITOutputConfig;
import net.multiphasicapps.zip.streamwriter.ZipStreamWriter;
import net.multiphasicapps.zip.ZipCompressionType;

/**
 * This builds the given target configuration and generates a binary.
 *
 * @since 2016/07/22
 */
public class NewBuilder
	implements BuilderCacheHelper, JITNamespaceProcessorProgress
{
	/** Logging output. */
	protected final PrintStream out;
	
	/** The configuration. */
	protected final BuildConfig config;
	
	/** The target builder. */
	protected final TargetBuilder targetbuilder;
	
	/** The package list. */
	protected final PackageList packagelist;
	
	/** The temporary output directory. */
	protected final Path tempdir;
	
	/** The compiled set of namespaces. */
	protected final Set<String> compiled =
		new LinkedHashSet<>();
	
	/** The projects to use in the boot classpath (the JVM itself). */
	private volatile Set<PackageInfo> _bootclasspath;
	
	/** The cache creator. */
	private volatile BuilderCache _cache;
	
	/**
	 * Initializes the new builder code.
	 *
	 * @param __out The console output.
	 * @param __conf The build configuration.
	 * @param __tb The target builder.
	 * @param __pl The package list.
	 * @param __td The temporary cache storage directory.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/07/22
	 */
	public NewBuilder(PrintStream __out, BuildConfig __conf,
		TargetBuilder __tb, PackageList __pl, Path __td)
		throws NullPointerException
	{
		// Check
		if (__out == null || __conf == null || __tb == null || __pl == null ||
			__td == null)
			throw new NullPointerException("NARG");
		
		// Set
		this.out = __out;
		this.config = __conf;
		this.targetbuilder = __tb;
		this.packagelist = __pl;
		this.tempdir = __td;
	}
	
	/**
	 * Performs the actual build.
	 *
	 * @throws IOException On read/write errors.
	 * @since 2016/07/22
	 */
	public void build()
		throws IOException
	{
		PrintStream out = this.out;
		
		// Determine the packages to build
		out.println("Selecting projects...");
		Set<PackageInfo> buildprojects = __selectProjects();
		Set<PackageInfo> bootclasspath = this._bootclasspath;
		
		// Print them all
		out.printf("Will compile %d project(s)...%n", buildprojects.size());
		out.printf("JVM Classpath contains %d project(s)...%n",
			bootclasspath.size());
		System.err.printf("DEBUG -- %s %s%n", buildprojects, bootclasspath);
		
		// Get the JIT output configuration to use when compiling for the
		// target
		TargetBuilder targetbuilder = this.targetbuilder;
		BuildConfig config = this.config;
		JITOutputConfig mutjitconf = new JITOutputConfig();
		targetbuilder.outputConfig(mutjitconf, config);
		
		// Add the triplet
		mutjitconf.setTriplet(config.triplet());
		
		// Setup cache creator
		BuilderCache cache = new BuilderCache(this);
		this._cache = cache;
		mutjitconf.setCacheCreator(cache);
		
		// Lock
		JITOutputConfig.Immutable jitconf = mutjitconf.immutable();
		mutjitconf = null;
		
		// {@squirreljme.error DW0l Could not configure the JIT for compilation
		// to the target. (The build configuration)}
		if (jitconf == null)
			throw new IllegalStateException(String.format("DW0l %s", config));
		
		// Setup namespace processor
		JITNamespaceProcessor jnp = new JITNamespaceProcessor(jitconf, cache,
			this);
		
		// Process all packages to be built
		Set<String> compiled = this.compiled;
		for (PackageInfo pi : buildprojects)
		{
			// Add namespace to compilation set
			String na = pi.name() + ".jar";
			compiled.add(na);
			
			// Process
			jnp.processNamespace(na);
		}
	}
	
	/**
	 * Links together all of the namespaces and generates a distributable
	 * package to be passed along to others.
	 *
	 * @param __os The stream where the ZIP should be written to.
	 * @throws IOException On read/write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/07/25
	 */
	public void linkAndGeneratePackage(OutputStream __os)
		throws IOException, NullPointerException
	{
		// Set
		PrintStream out = this.out;
		
		// Start writing the output ZIP
		try (ZipStreamWriter zsw = new ZipStreamWriter(__os))
		{
			// Write the triplet to the ZIP
			try (OutputStream os = zsw.nextEntry("target",
				ZipCompressionType.DEFAULT_COMPRESSION);
				PrintStream ps = new PrintStream(os, true, "utf-8"))
			{
				ps.println(this.config.triplet().toString());
			}
			
			// Create linked binary and store it in the output
			Set<String> compiled = this.compiled;
			int n = compiled.size();
			InputStream[] wns = new InputStream[n];
			try
			{
				// Load in names and resources
				String[] nsns = new String[n];
				Iterator<String> nit = compiled.iterator();
				BuilderCache cache = this._cache;
				for (int i = 0; i < n; i++)
				{
					// Get and store name
					String name = nit.next();
					nsns[i] = name;
					
					// Open cached data
					wns[i] = cache.openCache(name);
				}
				
				// Generate
				this.targetbuilder.linkBinary(zsw, nsns, wns, this.config);
			}
			
			// Close everything
			finally
			{
				// Failing exception?
				IOException fail = null;
				
				// Close then all
				for (InputStream os : wns)
					if (os != null)
						try
						{
							os.close();
						}
						
						// Failed to close
						catch (IOException e)
						{
							// {@squirreljme.error DW0n Failed to close the
							// opened namespace cache output streams.}
							if (fail == null)
								fail = new IOException("DW0n");
							
							// Suppress it
							fail.addSuppressed(e);
						}
				
				// Failed?
				if (fail != null)
					throw fail;
			}
			
			// Include basic assets
			byte[] buf = new byte[64];
			for (BasicAsset b : BasicAsset.getAssets())
			{
				String an = b.name();
			
				// Create entry
				try (InputStream is = b.open();
					OutputStream os = zsw.nextEntry(an,
						ZipCompressionType.DEFAULT_COMPRESSION))
				{
					for (;;)
					{
						int rc = is.read(buf);
						
						// EOF?
						if (rc < 0)
							break;
						
						// Write
						os.write(buf, 0, rc);
					}
				}
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/07/22
	 */
	@Override
	public PackageList packageList()
	{
		return this.packagelist;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/07/23
	 */
	@Override
	public void progressClass(String __cl)
	{
		// Print it
		if (__cl != null)
			this.out.println(__cl);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/07/23
	 */
	@Override
	public void progressNamespace(String __ns)
	{
		// Print it
		if (__ns != null)
			this.out.println(__ns);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/07/23
	 */
	@Override
	public void progressResource(String __rs)
	{
		// Print it
		if (__rs != null)
			this.out.println(__rs);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/07/22
	 */
	@Override
	public Path temporaryDirectory()
	{
		return this.tempdir;
	}
	
	/**
	 * Selects the projects that should be built.
	 *
	 * @throws IOException On read/write errors.
	 * @since 2016/07/22
	 */
	private final Set<PackageInfo> __selectProjects()
		throws IOException
	{
		// Setup
		Set<PackageInfo> rv = new LinkedHashSet<>();
		PackageList packagelist = this.packagelist;
		
		// Always add the JVM
		// {@squirreljme.error DW0k Cannot build the target because the Java
		// Virtual Machine project could not be found.}
		PackageInfo jvmproj = packagelist.get("jvm");
		if (jvmproj == null)
			throw new IllegalStateException("DW0k");
		rv.addAll(jvmproj.recursiveDependencies());
		
		// Tests do not need to be a part of the boot classpath, so the
		// boot classpath is just what is currently in the build list
		Set<PackageInfo> bootclasspath = new LinkedHashSet<>(rv);
		this._bootclasspath = bootclasspath;
		
		// Include any test possible?
		BuildConfig config = this.config;
		if (config.includeTests())
		{
			// Only include if they were actually found
			PackageInfo taproj = packagelist.get("test-all");
			if (taproj != null)
				for (PackageInfo pi : taproj.dependencies(true))
					rv.addAll(pi.recursiveDependencies());
		}
		
		// Return
		return rv;
	}
}

