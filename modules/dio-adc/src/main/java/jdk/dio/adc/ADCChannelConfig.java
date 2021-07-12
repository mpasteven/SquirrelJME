// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package jdk.dio.adc;

import cc.squirreljme.runtime.cldc.annotation.ApiDefinedDeprecated;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import jdk.dio.DeviceConfig;

public final class ADCChannelConfig
	implements DeviceConfig<ADCChannel>, DeviceConfig.HardwareAddressing
{
	@ApiDefinedDeprecated
	public ADCChannelConfig(int __a, int __b, int __c, int __d, int __e)
	{
		throw new todo.TODO();
	}
	
	@ApiDefinedDeprecated
	public ADCChannelConfig(String __a, int __b, int __c, int __d, int __e)
	{
		throw new todo.TODO();
	}
	
	@Override
	public boolean equals(Object __a)
	{
		throw new todo.TODO();
	}
	
	public int getChannelNumber()
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
	
	public int getResolution()
	{
		throw new todo.TODO();
	}
	
	public int getSamplingInterval()
	{
		throw new todo.TODO();
	}
	
	public int getSamplingTime()
	{
		throw new todo.TODO();
	}
	
	public double getScaleFactor()
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
	
	public static ADCChannelConfig deserialize(InputStream __a)
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
		
		public ADCChannelConfig build()
		{
			throw new todo.TODO();
		}
		
		public Builder setChannelNumber(int __a)
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
		
		public Builder setResolution(int __a)
		{
			throw new todo.TODO();
		}
		
		public Builder setSamplingInterval(int __a)
		{
			throw new todo.TODO();
		}
		
		public Builder setSamplingTime(int __a)
		{
			throw new todo.TODO();
		}
		
		public Builder setScaleFactor(double __a)
		{
			throw new todo.TODO();
		}
	}
}


