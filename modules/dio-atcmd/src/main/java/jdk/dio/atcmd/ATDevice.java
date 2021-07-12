// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package jdk.dio.atcmd;

import cc.squirreljme.runtime.cldc.annotation.ApiDefinedDeprecated;
import java.io.IOException;
import jdk.dio.ClosedDeviceException;
import jdk.dio.Device;
import jdk.dio.UnavailableDeviceException;

public interface ATDevice
	extends Device<ATDevice>
{
	@ApiDefinedDeprecated
	void abortCommand(String __a)
		throws IOException, UnavailableDeviceException, 
			ClosedDeviceException;
	
	@ApiDefinedDeprecated
	void escapeToCommandMode()
		throws IOException, UnavailableDeviceException, 
			ClosedDeviceException;
	
	int getMaxCommandLength()
		throws IOException, UnavailableDeviceException, 
			ClosedDeviceException;
	
	boolean isConnected()
		throws IOException, UnavailableDeviceException, 
			ClosedDeviceException;
	
	boolean isInCommandMode()
		throws IOException, UnavailableDeviceException, 
			ClosedDeviceException;
	
	DataConnection openDataConnection(String __a, CommandResponseHandler __b,
		DataConnectionHandler __c)
		throws IOException, UnavailableDeviceException, 
			ClosedDeviceException;
	
	String sendCommand(String __a)
		throws IOException, UnavailableDeviceException, 
			ClosedDeviceException;
	
	void sendCommand(String __a, CommandResponseHandler __b)
		throws IOException, UnavailableDeviceException, 
			ClosedDeviceException;
	
	void setUnsolicitedResponseHandler(UnsolicitedResponseHandler __a)
		throws IOException, ClosedDeviceException;
	
	String tryAbortCommand(String __a)
		throws IOException, UnavailableDeviceException, 
			ClosedDeviceException;
	
	String tryEscapeToCommandMode()
		throws IOException, UnavailableDeviceException, 
			ClosedDeviceException;
}


