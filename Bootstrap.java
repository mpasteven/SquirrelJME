// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) 2013-2016 Steven Gawroriski <steven@multiphasicapps.net>
//     Copyright (C) 2013-2016 Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// For more information see license.mkd.
// ---------------------------------------------------------------------------

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * This is the bootstrap which builds the bootstrap system and then launches
 * it. It bridges the bootstrap compiler and launcher system to the host
 * Jave SE VM.
 *
 * @since 2016/09/18
 */
public class Bootstrap
	implements Runnable
{
	/** Project root directory. */
	public static final Path PROJECT_ROOT;
	
	/** Source root. */
	public static final Path SOURCE_ROOT;
	
	/** Contrib root. */
	public static final Path CONTRIB_ROOT;
	
	/**
	 * Initializes some details.
	 *
	 * @since 2016/09/18
	 */
	static
	{
		// Get the root and make sure it exists
		PROJECT_ROOT = Paths.get(Objects.<String>requireNonNull(
			System.getProperty("project.root"),
			"The system property `project.root` was not specified."));
		
		// Resolve other paths
		SOURCE_ROOT = PROJECT_ROOT.resolve("src");
		CONTRIB_ROOT = PROJECT_ROOT.resolve("contrib");
	}
	
	/**
	 * Bootstrap starter instance.
	 *
	 * @param __args Program arguments.
	 * @since 2016/09/18
	 */
	public Bootstrap(String... __args)
	{
		// Force
		if (__args == null)
			__args = new String[0];
		
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/09/18
	 */
	@Override
	public void run()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Main entry point for the bootstrap bootstrapper.
	 *
	 * @param __args Program arguments.
	 * @since 2016/09/18
	 */
	public static void main(String... __args)
	{
		// Create and run
		new Bootstrap(__args).run();
	}
	
	/**
	 * This acts as a proxy for the bootstrap system so it has access to the
	 * system's Java compiler.
	 *
	 * @since 2016/09/18
	 */
	private class __CompilerProxy__
		implements InvocationHandler
	{
		/**
		 * {@inheritDoc}
		 * @since 2016/09/18
		 */
		@Override
		public Object invoke(Object __p, Method __m, Object[] __args)
			throws Throwable
		{
			throw new Error("TODO");
		}
	}
	
	/**
	 * This acts as a proxy for the bootstrap launcher system so that JARs
	 * can be loaded and execute natively.
	 *
	 * @since 2016/09/18
	 */
	private class __LauncherProxy__
		implements InvocationHandler
	{
		/**
		 * {@inheritDoc}
		 * @since 2016/09/18
		 */
		@Override
		public Object invoke(Object __p, Method __m, Object[] __args)
			throws Throwable
		{
			throw new Error("TODO");
		}
	}
}

