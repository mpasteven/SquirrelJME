// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirrelquarrel.units;

import cc.squirreljme.jvm.manifest.JavaManifest;
import java.io.IOException;
import java.io.InputStream;

/**
 * These are the units which are available in the game and what are actively
 * used within the game.
 *
 * @since 2017/02/14
 */
public enum UnitType
{
	/** Starting location. */
	START_LOCATION,
	
	/** Chlorophid: Garden. */
	CHLOROPHID_GARDEN,
	
	/** End. */
	;
	
	/** Base unit information and its properties. */
	private BaseUnitInfo _info;
	
	/**
	 * Returns the information which is associated with the given unit.
	 *
	 * @return The unit information.
	 * @since 2017/02/14
	 */
	public BaseUnitInfo baseInfo()
	{
		// If the info has not been cached then load it
		BaseUnitInfo rv = this._info;
		if (rv == null)
			try (InputStream in = UnitType.class.getResourceAsStream(
				"/net/multiphasicapps/squirrelquarrel/data/units/" +
				this.name().toLowerCase() + "/info"))
			{
				// {@squirreljme.error BE0b Unit information for the given
				// unit does not exist. (The unit)}
				if (in == null)
					throw new RuntimeException("BE0b " + this);
				
				this._info = (rv = new BaseUnitInfo(this.ordinal(),
					new JavaManifest(in)));
			}
			
			// {@squirreljme.error BE0c Could not load unit information.}
			catch (IOException e)
			{
				throw new RuntimeException("BE0c", e);
			}
		
		return rv;
	}
}

