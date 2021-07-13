// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.runtime.launcher.ui;

import cc.squirreljme.jvm.launch.Application;
import cc.squirreljme.jvm.launch.SuiteScanListener;
import cc.squirreljme.jvm.launch.SuiteScanner;
import cc.squirreljme.jvm.mle.brackets.TaskBracket;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;

/**
 * This is the main midlet for the LCDUI based launcher interface.
 *
 * @since 2016/10/11
 */
public class MidletMain
	extends MIDlet
{
	/**
	 * {@squirreljme.property cc.squirreljme.autolaunch=program This specifies
	 * the program that should be auto-launched once the program list has been
	 * processed.}
	 */
	public static final String AUTOLAUNCH_PROPERTY =
		"cc.squirreljme.autolaunch";
	
	/** Command used to exit the launcher and terminate. */
	public static final Command EXIT_COMMAND =
		new Command("Exit", Command.EXIT, 1);
	
	/** The about command. */
	public static final Command ABOUT_COMMAND =
		new Command("About", Command.HELP, 2);
	
	/** Timer used to reschedule things. */
	static final Timer _TIMER =
		new Timer("LauncherRecoverThread");
	
	/** The display that is being used. */
	static volatile Display _MAIN_DISPLAY;
	
	/** The list which contains all of the programs we can run. */
	protected final List programList =
		new List("SquirrelJME Launcher", Choice.IMPLICIT);
	
	/** The active task. */
	private final __ActiveTask__ _activeTask =
		new __ActiveTask__();
	
	/** The suites which are mapped to the list. */
	private final ArrayList<Application> _listedSuites =
		new ArrayList<>();
	
	/** The current end-time for the splash screen. */
	private volatile long _endTime;
	
	/** Automatic launch program. */
	private volatile String _autoLaunch;
	
	/** Lock for loading. */
	private volatile boolean _refreshLock;
	
	{
		// Do not crash if we cannot read properties
		String al = null;
		try
		{
			al = System.getProperty(MidletMain.AUTOLAUNCH_PROPERTY);
		}
		catch (SecurityException ignored)
		{
		}
		
		this._autoLaunch = al;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/10/11
	 */
	@Override
	protected void destroyApp(boolean __uc)
	{
		// This is not used at all
	}
	
	/**
	 * Refreshes the list.
	 *
	 * @since 2018/11/16
	 */
	public void refresh()
	{
		// Prevent double refresh
		synchronized (this)
		{
			if (this._refreshLock)
				return;
			this._refreshLock = true;
		}
		
		// Used to clear the lock when done, always!
		try
		{
			// When a refresh is happening, change the title so that is
			// indicated
			List programList = this.programList;
			programList.setTitle("Querying Suites...");
			
			// Clear the list of all programs
			programList.deleteAll();
			
			// Look for suites to load
			SuiteScanListener handler;
			synchronized (this)
			{
				// Reset the application list
				ArrayList<Application> listedSuites = this._listedSuites;
				listedSuites.clear();
				
				// Used to add suites and indicate progress
				handler = new __ProgressListener__(programList, listedSuites);
			}
			
			// Scan all of the available suites for launching
			SuiteScanner.scanSuites(handler);
			
			// All done so, return the title back
			programList.setTitle("SquirrelJME Launcher");
		}
		
		// Clear the refresh lock
		finally
		{
			synchronized (this)
			{
				this._refreshLock = false;
			}
		}
		
		// If the program list started too quickly then wait until the splash
		// time has expired so it is always shown for a fixed amount of time
		// This is intended for branding and showing credit
		long endTime = this._endTime;
		for (long nowTime = System.nanoTime(); nowTime < endTime;
			nowTime = System.nanoTime())
			try
			{
				Debugging.debugNote("Stalling...");
				Thread.sleep((endTime - nowTime) / 1_000_000L);
			}
			catch (InterruptedException ignored)
			{
			}
		
		// Make sure the program list is showing
		Displayable current = MidletMain._MAIN_DISPLAY.getCurrent();
		if (current == null || (current instanceof SplashScreen))
			MidletMain._MAIN_DISPLAY.setCurrent(this.programList);
		
		// Automatically launch a program?
		String autoLaunch = this._autoLaunch;
		if (autoLaunch != null)
		{
			// Do not try auto-launching whenever there is a refresh since we
			// may just get stuck in a loop here
			this._autoLaunch = null;
			
			// Launch it
			System.err.println("Auto-launching " + autoLaunch + "...");
			this.__launch(autoLaunch);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/11/16
	 */
	@Override
	protected void startApp()
	{
		// We will need to access our own display to build the list of
		// MIDlets that could actually be ran
		Display display = Display.getDisplay(this);
		MidletMain._MAIN_DISPLAY = display;
		
		// Add commands to the list so things can be done with them
		List programList = this.programList;
		programList.addCommand(MidletMain.EXIT_COMMAND);
		programList.addCommand(MidletMain.ABOUT_COMMAND);
		
		// Need to handle commands and such
		__CommandHandler__ ch = new __CommandHandler__();
		programList.setCommandListener(ch);
		
		// Used to ensure the splash screen is visible for at least a second
		this._endTime = System.nanoTime() + 1_000_000_000L;
		
		// Refresh the list in another thread
		Thread refresher = new __Refresher__(this);
		refresher.start();
		
		// Instead of showing the program list early, just show a splash screen
		// with a handsome Lex and the version information
		SplashScreen spl = new SplashScreen(
			display.getWidth(), display.getHeight());
		if (display.getCurrent() == null)
			display.setCurrent(spl);
	}
	
	/**
	 * Launches the specified program.
	 *
	 * @param __p The program to launch.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/14
	 */
	private void __launch(Application __p)
		throws NullPointerException
	{
		if (__p == null)
			throw new NullPointerException("NARG");
		
		// Indication that something is happening
		this.programList.setTitle("Launching " + __p.displayName() + "...");
		
		// Launch this program
		__ActiveTask__ activeTask = this._activeTask;
		synchronized (activeTask)
		{
			activeTask._task = __p.launch();
		}
		
		// We launched, so revert the title
		this.programList.setTitle("SquirrelJME Launcher");
		
		// Set a timer to periodically check if we should show the program
		// list again
		MidletMain._TIMER.schedule(new __ReControlTask__(
			MidletMain._MAIN_DISPLAY, activeTask), 2000, 2000);
	}
	
	/**
	 * Launches the specified program.
	 *
	 * @param __p The program to launch.
	 * @since 2019/04/14
	 */
	private void __launch(int __p)
	{
		Application app;
		synchronized (this)
		{
			ArrayList<Application> programs = this._listedSuites;
			
			// Do nothing if out of bounds
			if (__p < 0 || __p >= programs.size())
				return;
			
			// Launch this one
			app = programs.get(__p);
		}
		
		// Do the actual launching of it
		this.__launch(app);
	}
	
	/**
	 * Launches the specified program.
	 *
	 * @param __p The program to launch.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/14
	 */
	private void __launch(String __p)
		throws NullPointerException
	{
		if (__p == null)
			throw new NullPointerException("NARG");
		
		// Get the applications that are available
		Application[] apps;
		synchronized (this)
		{
			ArrayList<Application> listed = this._listedSuites;
			apps = listed.toArray(new Application[listed.size()]);
		}
		
		// Find all the possible matches for a program with given criteria
		Map<SearchOrder, Application> found = new HashMap<>();
		for (Application app : apps)
		{
			if (Objects.equals(__p, app.displayName()))
				found.put(SearchOrder.DISPLAY_NAME, app);
			else if (Objects.equals(__p, app.entryPoint().entryPoint()))
				found.put(SearchOrder.MAIN_CLASS, app);
			else if (Objects.equals(__p, app.entryPoint().name()))
				found.put(SearchOrder.SUITE_NAME, app);
			else if (Objects.equals(__p, app.squirrelJMEName()))
				found.put(SearchOrder.SQUIRRELJME_NAME, app);
		}
		
		// Use priority based order when finding the application
		for (SearchOrder order : SearchOrder.values())
		{
			Application app = found.get(order);
			if (app != null)
			{
				this.__launch(app);
				return;
			}
		}
		
		// If everything fails, just assume it is an index to a program on the
		// program list
		try
		{
			this.__launch(Integer.parseInt(__p));
		}
		catch (NumberFormatException ignored)
		{
		}
	}
	
	/**
	 * This is the handler for commands.
	 *
	 * @since 2018/11/16
	 */
	private final class __CommandHandler__
		implements CommandListener
	{
		/**
		 * {@inheritDoc}
		 * @since 2018/11/16
		 */
		@Override
		public final void commandAction(Command __c, Displayable __d)
		{
			// Launching a program?
			if (__c == List.SELECT_COMMAND)
			{
				// If the list is empty then do nothing because it will NPE
				// or out of bounds if the selection is off
				List list = (List)__d;
				int selDx = list.getSelectedIndex();
				if (list.size() <= 0 || selDx < 0)
					return;
				
				// Call other launcher
				MidletMain.this.__launch(selDx);
			}
			
			// Exiting the VM?
			else if (__c == MidletMain.EXIT_COMMAND)
			{
				// Indication that something is happening
				MidletMain.this.programList.setTitle("Exiting...");
				
				// We should kill the running task since we exited
				TaskBracket task = MidletMain.this._activeTask._task;
				if (task != null)
					Debugging.todoNote("Kill task on exit.");
				
				// Stop running
				System.exit(0);
			}
			
			// About SquirrelJME
			else if (__c == MidletMain.ABOUT_COMMAND)
			{
				Debugging.todoNote("Show about screen.");
			}
		}
	}
}

