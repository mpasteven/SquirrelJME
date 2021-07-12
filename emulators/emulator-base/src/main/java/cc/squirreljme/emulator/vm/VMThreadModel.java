// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.emulator.vm;

import cc.squirreljme.jvm.mle.constants.ThreadModelType;

/**
 * This contains the thread model that will be used.
 *
 * @since 2021/05/07
 */
public enum VMThreadModel
{
	/** Single cooperative thread. */
	SINGLE_COOP_THREAD(ThreadModelType.SINGLE_COOP_THREAD),
	
	/** Simultaneous Multi-Threading. */
	SIMULTANEOUS_MULTI_THREAD(ThreadModelType.SIMULTANEOUS_MULTI_THREAD),
	
	/* End. */
	;
	
	/** The default thread model. */
	public static final VMThreadModel DEFAULT =
		VMThreadModel.SIMULTANEOUS_MULTI_THREAD;
	
	/** The model ID. */
	public final int model;
	
	/**
	 * The thread model to use.
	 * 
	 * @param __model The model ID.
	 * @since 2021/05/07
	 */
	VMThreadModel(int __model)
	{
		this.model = __model;
	}
	
	/**
	 * Returns the thread model from the given string.
	 * 
	 * @param __s The string to use.
	 * @return The thread model, if unknown the default will be used.
	 * @since 2021/05/07
	 */
	public static VMThreadModel of(String __s)
	{
		if (__s == null)
			return VMThreadModel.DEFAULT;
		
		switch (__s.toLowerCase())
		{
			case "single":
			case "coop":
				return VMThreadModel.SINGLE_COOP_THREAD;
			
			case "multi":
			case "smt":
				return VMThreadModel.SIMULTANEOUS_MULTI_THREAD;
			
			default:
				return VMThreadModel.DEFAULT;
		}
	}
}
