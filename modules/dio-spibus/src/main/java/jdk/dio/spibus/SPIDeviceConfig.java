// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package jdk.dio.spibus;

import cc.squirreljme.runtime.cldc.annotation.ApiDefinedDeprecated;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import jdk.dio.DeviceConfig;

public final class SPIDeviceConfig
	implements DeviceConfig<SPIDevice>, DeviceConfig.HardwareAddressing
{
	public static final int CS_ACTIVE_HIGH =
		0;
	
	public static final int CS_ACTIVE_LOW =
		1;
	
	public static final int CS_NOT_CONTROLLED =
		2;
	
	@ApiDefinedDeprecated
	public SPIDeviceConfig(int __a, int __b, int __c, int __d, int __e, int 
		__f)
	{
		throw new todo.TODO();
	}
	
	@ApiDefinedDeprecated
	public SPIDeviceConfig(int __a, int __b, int __c, int __d, int __e, int 
		__f, int __g)
	{
		throw new todo.TODO();
	}
	
	@ApiDefinedDeprecated
	public SPIDeviceConfig(String __a, int __b, int __c, int __d, int __e, 
		int __f)
	{
		throw new todo.TODO();
	}
	
	@ApiDefinedDeprecated
	public SPIDeviceConfig(String __a, int __b, int __c, int __d, int __e, 
		int __f, int __g)
	{
		throw new todo.TODO();
	}
	
	@Override
	public boolean equals(Object __a)
	{
		throw new todo.TODO();
	}
	
	public int getAddress()
	{
		throw new todo.TODO();
	}
	
	public int getBitOrdering()
	{
		throw new todo.TODO();
	}
	
	public int getCSActiveLevel()
	{
		throw new todo.TODO();
	}
	
	public int getClockFrequency()
	{
		throw new todo.TODO();
	}
	
	public int getClockMode()
	{
		throw new todo.TODO();
	}
	
	@Override
	public String getControllerName()
	{
		throw new todo.TODO();
	}
	
	@Override
	public int getControllerNumber()
	{
		throw new todo.TODO();
	}
	
	public int getInputBufferSize()
	{
		throw new todo.TODO();
	}
	
	public int getOutputBufferSize()
	{
		throw new todo.TODO();
	}
	
	public int getWordLength()
	{
		throw new todo.TODO();
	}
	
	@Override
	public int hashCode()
	{
		throw new todo.TODO();
	}
	
	@Override
	public int serialize(OutputStream __a)
		throws IOException
	{
		if (false)
			throw new IOException();
		throw new todo.TODO();
	}
	
	public static SPIDeviceConfig deserialize(InputStream __a)
		throws IOException
	{
		if (false)
			throw new IOException();
		throw new todo.TODO();
	}
	
	public static final class Builder
	{
		public Builder()
		{
			throw new todo.TODO();
		}
		
		public SPIDeviceConfig build()
		{
			throw new todo.TODO();
		}
		
		public Builder setAddress(int __a)
		{
			throw new todo.TODO();
		}
		
		public Builder setBitOrdering(int __a)
		{
			throw new todo.TODO();
		}
		
		public Builder setCSActiveLevel(int __a)
		{
			throw new todo.TODO();
		}
		
		public Builder setClockFrequency(int __a)
		{
			throw new todo.TODO();
		}
		
		public Builder setClockMode(int __a)
		{
			throw new todo.TODO();
		}
		
		public Builder setControllerName(String __a)
		{
			throw new todo.TODO();
		}
		
		public Builder setControllerNumber(int __a)
		{
			throw new todo.TODO();
		}
		
		public Builder setInputBufferSize(int __a)
		{
			throw new todo.TODO();
		}
		
		public Builder setOutputBufferSize(int __a)
		{
			throw new todo.TODO();
		}
		
		public Builder setWordLength(int __a)
		{
			throw new todo.TODO();
		}
	}
}


