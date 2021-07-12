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
import cc.squirreljme.jvm.manifest.JavaManifestAttributes;
import net.multiphasicapps.squirrelquarrel.util.ConstantFixedPoint;
import net.multiphasicapps.squirrelquarrel.util.Dimension;
import net.multiphasicapps.squirrelquarrel.util.Point;
import net.multiphasicapps.squirrelquarrel.world.Tile;

/**
 * This contains the base information about a unit which is fixed and never
 * changes.
 *
 * @since 2017/02/14
 */
public final class BaseUnitInfo
{
	/** The type ID of the unit. */
	public final int typeid;
	
	/** The name of the unit. */
	public final String name;
	
	/** Unit hitpoints. */
	public final int hp;
	
	/** Unit shields. */
	public final int shields;
	
	/** Armor. */
	public final int armor;
	
	/** Unit size. */
	public final UnitSize size;
	
	/** The cost in salt. */
	public final int salt;
	
	/** The cost in methane. */
	public final int methane;
	
	/** The build time in frames. */
	public final int buildtime;
	
	/** The supply provided. */
	public final int supplyprovided;
	
	/** The supply cost. */
	public final int supplycost;
	
	/** The dimension of the unit in pixels. */
	public final Dimension pixeldimension;
	
	/** The offset. */
	public final Point offset;
	
	/** Center point offset for the unit. */
	public final Point centerpointoffset;
	
	/** The center point offset used for buildings (based on tile grid). */
	public final Point buildingcenterpointoffset;
	
	/** The unit size in tiles (for buildings). */
	public final Dimension tiledimension;
	
	/** The dimenion of the unit in pixels matching the tiled size. */
	public final Dimension pixeltiledimension;
	
	/** The sight range. */
	public final int sight;
	
	/** The score for creating this unit. */
	public final int scorebuild;
	
	/** The score for destroying this unit. */
	public final int scoredestroy;
	
	/** The speed of this unit, in 16.16 fixed point. */
	public final ConstantFixedPoint speed;
	
	/**
	 * Initializes the unit information from the given manifest.
	 *
	 * @param __tid Unit type ID.
	 * @param __m The input unit manifest.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/03/18
	 */
	BaseUnitInfo(int __tid, JavaManifest __m)
		throws NullPointerException
	{
		// Check
		if (__m == null)
			throw new NullPointerException("NARG");
		
		// Type ID is used to modify unit details
		this.typeid = __tid;
		
		// Need the attributes!
		JavaManifestAttributes attr = __m.getMainAttributes();
		
		// Load these values directly
		this.name = attr.getValue("name", "Squirrels are cute!");
		this.hp = Integer.parseInt(
			attr.getValue("hp", "0"));
		this.shields = Integer.parseInt(
			attr.getValue("shields", "0"));
		this.armor = Integer.parseInt(
			attr.getValue("armor", "0"));
		this.salt = Integer.parseInt(
			attr.getValue("salt-cost", "0"));
		this.methane = Integer.parseInt(
			attr.getValue("methane-cost", "0"));
		this.buildtime = Integer.parseInt(
			attr.getValue("build-time", "0"));
		this.supplyprovided = Integer.parseInt(
			attr.getValue("supply-provided", "0"));
		this.supplycost = Integer.parseInt(
			attr.getValue("supply-cost", "0"));
		this.sight = Integer.parseInt(
			attr.getValue("sight", "0"));
		this.scorebuild = Integer.parseInt(
			attr.getValue("score-build", "0"));
		this.scoredestroy = Integer.parseInt(
			attr.getValue("score-destroy", "0"));
		this.speed = new ConstantFixedPoint(
			attr.getValue("speed", "0"));
		
		// Read dimensions
		Dimension pixeldimension;
		this.pixeldimension = (pixeldimension = new Dimension(
			attr.getValue("pixel-dimensions", "[0, 0]")));
		Point offset;
		this.offset = (offset = new Point(
			attr.getValue("pixel-offset", "(0, 0)")));
		
		// Parse size
		String vsize = attr.getValue("size", "small");
		switch (vsize)
		{
			case "small": this.size = UnitSize.SMALL; break;
			case "medium": this.size = UnitSize.MEDIUM; break;
			case "large": this.size = UnitSize.LARGE; break;
			
				// {@squirreljme.error BE09 Unknown unit size. (Unit size)}
			default:
				throw new RuntimeException(String.format("BE09 %s", vsize));
		}
		
		// Center point is just half the dimension
		this.centerpointoffset = new Point(pixeldimension.width / 2,
			pixeldimension.height / 2);
		
		// Get total size of unit in tiles, rounded up to the tile
		int pxw = (pixeldimension.width + offset.x +
				Tile.PIXEL_SIZE) & ~(Tile.PIXEL_MASK),
			pxh = (pixeldimension.height + offset.y +
				Tile.PIXEL_SIZE) & ~(Tile.PIXEL_MASK);
		
		// Determine tile dimension of unit
		Dimension tiledimension = new Dimension(
			pxw / Tile.PIXEL_SIZE,
			pxh / Tile.PIXEL_SIZE);
		this.tiledimension = tiledimension;
		this.pixeltiledimension = new Dimension(
			tiledimension.width / Tile.PIXEL_SIZE,
			tiledimension.height / Tile.PIXEL_SIZE);
		
		// Offset to the center of the building is in the center of
		// the tile dimensions
		this.buildingcenterpointoffset = new Point(pxw / 2, pxh / 2);
	}
	
	/**
	 * This performs the calculation of determining where a building should be
	 * placed. Building placement is aligned to the grid.
	 *
	 * @param __y If {@code true} then {@code __v} is a Y coordinate.
	 * @param __v The coordinate value.
	 * @return The center position of the building.
	 * @since 2017/02/17
	 */
	public int placeBuilding(boolean __y, int __v)
	{
		return __v;
	}
}

