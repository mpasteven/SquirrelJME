// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package javax.microedition.media.protocol;

import java.io.IOException;
import javax.microedition.media.Controllable;

public abstract class DataSource
	implements Controllable
{
	public DataSource(String __a)
	{
		throw new todo.TODO();
	}
	
	public abstract void connect()
		throws IOException;
	
	public abstract void disconnect();
	
	public abstract String getContentType();
	
	public abstract SourceStream[] getStreams();
	
	public abstract void start()
		throws IOException;
	
	public abstract void stop()
		throws IOException;
	
	public String getLocator()
	{
		throw new todo.TODO();
	}
}


