// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package com.nokia.mid.ui;

import cc.squirreljme.jvm.mle.constants.UIMetricType;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.runtime.lcdui.mle.UIBackend;
import cc.squirreljme.runtime.lcdui.mle.UIBackendFactory;

/**
 * This is used to utilize special hardware that exists on the device for
 * user feedback.
 *
 * @since 2019/10/05
 */
public class DeviceControl
{
	/**
	 * Flashes the LED on the device.
	 *
	 * @param __ms The number of milliseconds to flash for.
	 * @throws IllegalArgumentException If the duration is negative.
	 * @since 2019/10/05
	 */
	public static void flashLights(long __ms)
		throws IllegalArgumentException
	{
		// {@squirreljme.error EB2z Cannot blink for a negative duration.}
		if (__ms < 0)
			throw new IllegalArgumentException("EB2z");
		
		throw Debugging.todo();
		/*
		// Blink!
		Assembly.sysCall(SystemCallIndex.DEVICE_FEEDBACK,
			DeviceFeedbackType.BLINK_LED, ((__ms > (long)Integer.MAX_VALUE) ?
				Integer.MAX_VALUE : (int)__ms));*/
	}
	
	/**
	 * Sets the level of the backlight.
	 *
	 * @param __num The light number, this is always zero for the backlight.
	 * @param __lvl The level to set within the range of {@code [0, 100]}
	 * @throws IllegalArgumentException If the light number is not zero or
	 * the level is out of range.
	 * @since 2019/10/05
	 */
	public static void setLights(int __num, int __lvl)
		throws IllegalArgumentException
	{
		// {@squirreljme.error EB31 Only light number zero is supported.
		// (The light number)}
		if (__num != 0)
			throw new IllegalArgumentException("EB31 " + __num);
			
		// {@squirreljme.error EB32 Light level out of range. (The level)}
		if (__lvl < 0 || __lvl > 100)
			throw new IllegalArgumentException("EB32 " + __lvl);
		
		// If controlling the backlight is supported, allow it to be changed
		UIBackend backend = UIBackendFactory.getInstance();
		if (backend.metric(UIMetricType.SUPPORTS_BACKLIGHT_CONTROL) == 0)
			return;
		
		throw Debugging.todo();
		/*
		// Get maximum backlight level, stop if it is zero which means the
		// property is not supported or there is no backlight that can be
		// controlled
		throw Debugging.todo();
		/*
		int max = Assembly.sysCallV(SystemCallIndex.FRAMEBUFFER,
			Framebuffer.CONTROL_BACKLIGHT_LEVEL_MAX);
		if (max == 0)
			return;
		
		// Set the desired level as a percentage of the max
		int val = (max * __lvl) / 100;
		Assembly.sysCall(SystemCallIndex.FRAMEBUFFER,
			Framebuffer.CONTROL_BACKLIGHT_LEVEL_SET,
			(val < 0 ? 0 : (val > max ? max : val)));*/
	}
	
	/**
	 * Starts vibrating at the given frequency for the given duration.
	 *
	 * @param __freq The frequency of the vibration, must be in the range of
	 * {@code [0, 100]}.
	 * @param __ms The length to vibrate for in milliseconds.
	 * @throws IllegalArgumentException If the duration is negative or the
	 * frequency is out of range.
	 * @since 2019/10/05
	 */
	public static void startVibra(int __freq, long __ms)
		throws IllegalArgumentException
	{
		// {@squirreljme.error EB33 Cannot vibrate for a negative duration.}
		if (__ms < 0)
			throw new IllegalArgumentException("EB33");
		
		// {@squirreljme.error EB34 Frequency out of range. (The frequency)}
		if (__freq < 0 || __freq > 100)
			throw new IllegalArgumentException("EB34 " + __freq);
		
		// Vibrate!
		throw Debugging.todo();
		/*
		Assembly.sysCall(SystemCallIndex.DEVICE_FEEDBACK,
			DeviceFeedbackType.VIBRATE, ((__ms > (long)Integer.MAX_VALUE) ?
				Integer.MAX_VALUE : (int)__ms));*/
	}
	
	/**
	 * Stops any vibration that is happening.
	 *
	 * @since 2019/10/05
	 */
	public static void stopVibra()
	{
		// Clear vibration
		throw Debugging.todo();
		/*
		Assembly.sysCall(SystemCallIndex.DEVICE_FEEDBACK,
			DeviceFeedbackType.VIBRATE, 0);*/
	}
}

