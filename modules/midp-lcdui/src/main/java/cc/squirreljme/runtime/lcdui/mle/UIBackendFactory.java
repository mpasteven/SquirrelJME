// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.runtime.lcdui.mle;

import cc.squirreljme.jvm.mle.UIFormShelf;
import cc.squirreljme.jvm.mle.constants.UIMetricType;
import cc.squirreljme.jvm.mle.constants.UIPixelFormat;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.runtime.lcdui.mle.fb.FBUIBackend;
import cc.squirreljme.runtime.lcdui.mle.fb.NativeFBAttachment;
import cc.squirreljme.runtime.lcdui.mle.headless.HeadlessAttachment;
import cc.squirreljme.runtime.lcdui.mle.pure.NativeUIBackend;

/**
 * Used to gain access to the {@link UIBackend}.
 *
 * @since 2020/07/19
 */
public final class UIBackendFactory
{
	/**
	 * {@squirreljme.property cc.squirreljme.runtime.lcdui.mle.fallback=bool
	 * Force the usage of the fallback UIForm in the event that native form
	 * handling should NOT be used.}
	 */
	public static final String FORCE_FALLBACK_PROPERTY =
		"cc.squirreljme.runtime.lcdui.mle.fallback";
	
	/**
	 * {@squirreljme.property cc.squirreljme.runtime.lcdui.mle.headless=bool
	 * Force that the headless UIForm be used, this will mean that nothing
	 * will be displayed on the screen.}
	 */
	public static final String FORCE_HEADLESS_PROPERTY =
		"cc.squirreljme.runtime.lcdui.mle.headless";
	
	/** The instance of the form engine to be used. */
	@SuppressWarnings({"StaticVariableMayNotBeInitialized", 
		"NonConstantFieldWithUpperCaseName"})
	private static UIBackend _DEFAULT;
	
	/**
	 * Gets an instance of the UI engine.
	 * 
	 * @return The instance of the engine to use.
	 * @since 2020/06/30
	 */
	@SuppressWarnings("StaticVariableUsedBeforeInitialization")
	public static UIBackend getInstance()
	{
		// If this was already cached, use that
		UIBackend rv = UIBackendFactory._DEFAULT;
		if (rv != null)
			return rv;
		
		// Debug
		Debugging.debugNote("Initializing UIFormEngine...");
		
		// These are properties which determine which kind of engine can be
		// returned
		boolean forceFallback = Boolean.getBoolean(
			UIBackendFactory.FORCE_FALLBACK_PROPERTY);
		boolean forceHeadless = Boolean.getBoolean(
			UIBackendFactory.FORCE_HEADLESS_PROPERTY);
		boolean isForcing = (forceFallback || forceHeadless);
		
		// Use native forms if supported unless we are forcing other options
		if (0 != UIFormShelf.metric(UIMetricType.UIFORMS_SUPPORTED) &&
			!isForcing)
			rv = new NativeUIBackend();
		
		// Otherwise use the fallback implementation (raw framebuffer)
		else
		{
			// Use a headless interface? This is if we have no framebuffer
			// and the only have to have graphics is to fake it
			if (forceHeadless)
			{
				// Emit a notice
				Debugging.notice("Framebuffer either does not exist " +
					"or is disabled, attaching without a head.");
				
				// Create it
				rv = new FBUIBackend(new HeadlessAttachment(
					UIPixelFormat.INT_RGB888, 240, 320));
			}
			
			// Use a method that uses the backing framebuffer here instead
			else
				rv = new FBUIBackend(new NativeFBAttachment());
		}
		
		// Cache and use
		UIBackendFactory._DEFAULT = rv;
		return rv;
	}
}
