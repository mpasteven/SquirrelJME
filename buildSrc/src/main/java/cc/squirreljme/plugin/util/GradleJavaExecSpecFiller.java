// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.plugin.util;

import java.util.Collection;
import java.util.Map;
import org.gradle.process.JavaExecSpec;

/**
 * This forwards any sets to the execution spec to be forwarded to
 * {@link JavaExecSpec}.
 *
 * @since 2020/12/26
 */
public class GradleJavaExecSpecFiller
	implements JavaExecSpecFiller
{
	/** The specification to fill. */
	protected final JavaExecSpec spec;
	
	/**
	 * Initializes the filler.
	 * 
	 * @param __spec The specification to fill.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/12/26
	 */
	public GradleJavaExecSpecFiller(JavaExecSpec __spec)
		throws NullPointerException
	{
		if (__spec == null)
			throw new NullPointerException("NARG");
		
		this.spec = __spec;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/12/26
	 */
	@Override
	public void classpath(Collection<Object> __classPath)
	{
		this.spec.classpath(__classPath);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/12/26
	 */
	@Override
	public Iterable<String> getCommandLine()
	{
		return this.spec.getCommandLine();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/12/26
	 */
	@Override
	public void setMain(String __mainClass)
	{
		this.spec.setMain(__mainClass);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/12/26
	 */
	@Override
	public void setArgs(Collection<String> __args)
	{
		this.spec.setArgs(__args);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/06/18
	 */
	@Override
	public void setJvmArgs(Collection<String> __args)
	{
		this.spec.setJvmArgs(__args);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/12/26
	 */
	@Override
	public void systemProperties(Map<String, String> __sysProps)
	{
		this.spec.systemProperties(__sysProps);
	}
}
