// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.plugin.multivm;

import javax.inject.Inject;
import org.gradle.api.DefaultTask;

/**
 * Task for running the full-suite of SquirrelJME.
 *
 * @since 2020/10/17
 */
public class VMFullSuite
	extends DefaultTask
	implements VMExecutableTask
{
	/** The source set used. */
	public final String sourceSet;
	
	/** The virtual machine type. */
	public final VMSpecifier vmType;
	
	/**
	 * Initializes the full suite task.
	 * 
	 * @param __sourceSet The source set used.
	 * @param __vmType The virtual machine type.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/10/17
	 */
	@Inject
	public VMFullSuite(String __sourceSet, VMSpecifier __vmType)
		throws NullPointerException
	{
		if (__vmType == null || __sourceSet == null)
			throw new NullPointerException("NARG");
		
		this.sourceSet = __sourceSet;
		this.vmType = __vmType;
		
		// Runs the entire API/Library suite of SquirrelJME to run a given
		// application
		this.setGroup("squirreljme");
		this.setDescription("Runs the full suite of SquirrelJME Modules.");
		
		// This always runs, no matter what
		this.onlyIf(new AlwaysTrue());
		this.getOutputs().upToDateWhen(new AlwaysFalse());
		
		// This depends on everything!
		this.dependsOn(
			new VMFullSuiteDepends(this, __sourceSet, __vmType),
			new VMEmulatorDependencies(this, __vmType));
		
		// Actual running of everything
		this.doLast(new VMFullSuiteTaskAction(__sourceSet, __vmType));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/03/08
	 */
	@Override
	public String getSourceSet()
	{
		return this.sourceSet;
	}
}
