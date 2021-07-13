// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.runtime.cldc.lang;

/**
 * This represents the type of operating system SquirrelJME is running on.
 *
 * This enumeration is intended to make OS detection standard.
 *
 * @since 2018/01/13
 */
public interface OperatingSystemType
{
	/** Unknown. */
	int UNKNOWN =
		0;
	
	/** Amiga. */
	int AMIGA =
		1;
	
	/** Linux. */
	int LINUX =
		2;
	
	/** Class Mac OS. */
	int MAC_OS_CLASSIC =
		3;
	
	/** Mac OS X. */
	int MAC_OS_X =
		4;
	
	/** MS-DOS and compatibles. */
	int MS_DOS =
		5;
	
	/** Palm OS. */
	int PALM_OS =
		6;
		
	/** Solaris. */
	int SOLARIS =
		7;
	
	/** Windows 16-bit. */
	int WINDOWS_WIN16 =
		8;
	
	/** Windows CE. */
	int WINDOWS_CE =
		9;
	
	/** Windows 9x. */
	int WINDOWS_9X =
		10;
	
	/** Windows NT. */
	int WINDOWS_NT =
		11;
	
	/** An Unknown windows system. */
	int WINDOWS_UNKNOWN =
		12;
}

