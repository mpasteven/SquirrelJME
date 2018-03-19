// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirrelquarrel.player;

/**
 * This represents a game player, where actions may be performed and such.
 *
 * @since 2017/02/13
 */
public class Player
{
	/** The color of this player. */
	protected final PlayerColor color;
	
	/** The vision mask for this player. */
	private volatile int _visionmask;
	
	/**
	 * Initializes the game player.
	 *
	 * @param __g The owning game.
	 * @param __c The player color.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/02/14
	 */
	public Player(PlayerColor __c)
		throws NullPointerException
	{
		// Check
		if (__g == null || __c == null)
			throw new NullPointerException("NARG");
		
		// Set
		this.color = __c;
		
		// The player always has vision to self
		this._visionmask = __c.mask();
	}
	
	/**
	 * Returns the player color.
	 *
	 * @return The player color.
	 * @since 2017/02/14
	 */
	public PlayerColor color()
	{
		return this.color;
	}
	
	/**
	 * This returns the mask that is used to check the vision bits.
	 *
	 * @return The bits used for vision.
	 * @since 2017/02/13
	 */
	public int visionMask()
	{
		return this._vision;
	}
	
	/**
	 * Runs the player logic.
	 *
	 * @param __frame The current frame.
	 * @since 2017/02/14
	 */
	public void run(int __frame)
	{
		throw new todo.TODO();
	}
}

