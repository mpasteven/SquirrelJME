// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.springcoat;

import cc.squirreljme.jvm.mle.PencilShelf;
import cc.squirreljme.jvm.mle.constants.UIPixelFormat;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.vm.springcoat.exceptions.SpringMLECallError;

/**
 * Handling for {@link PencilShelf}.
 *
 * @since 2020/09/26
 */
public enum MLEPencil
	implements MLEFunction
{
	/** {@link PencilShelf#capabilities(int)}. */
	CAPABILITIES("capabilities:(I)J")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/09/26
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			int pf = (int)__args[0];
			if (pf < 0 || pf >= UIPixelFormat.NUM_PIXEL_FORMATS)
				throw new SpringMLECallError("Invalid pixel format: " +
					pf);
			
			return PencilShelf.capabilities(pf);
		}
	}, 
	
	/* End. */
	;
	
	/** The dispatch key. */
	protected final String key;
	
	/**
	 * Initializes the dispatcher info.
	 *
	 * @param __key The key.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/09/26
	 */
	MLEPencil(String __key)
		throws NullPointerException
	{
		if (__key == null)
			throw new NullPointerException("NARG");
		
		this.key = __key;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/09/26
	 */
	@Override
	public String key()
	{
		return this.key;
	}
}
