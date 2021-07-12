// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package jdk.dio.pwm;

import cc.squirreljme.runtime.cldc.annotation.ApiDefinedDeprecated;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import jdk.dio.DeviceConfig;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;

public final class PWMChannelConfig
	implements DeviceConfig<PWMChannel>, DeviceConfig.HardwareAddressing
{
	public static final int ALIGN_CENTER =
		0;
	
	public static final int ALIGN_LEFT =
		1;
	
	public static final int ALIGN_RIGHT =
		2;
	
	public static final int IDLE_STATE_HIGH =
		0;
	
	public static final int IDLE_STATE_LOW =
		1;
	
	@ApiDefinedDeprecated
	public PWMChannelConfig(int __a, int __b, int __c, int __d, int __e)
	{
		throw new todo.TODO();
	}
	
	@ApiDefinedDeprecated
	public PWMChannelConfig(int __a, int __b, int __c, int __d, int __e, 
		GPIOPinConfig __f)
	{
		throw new todo.TODO();
	}
	
	@ApiDefinedDeprecated
	public PWMChannelConfig(String __a, int __b, int __c, int __d, int __e)
	{
		throw new todo.TODO();
	}
	
	@ApiDefinedDeprecated
	public PWMChannelConfig(String __a, int __b, int __c, int __d, int __e, 
		GPIOPinConfig __f)
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
	
	public int getIdleState()
	{
		throw new todo.TODO();
	}
	
	@ApiDefinedDeprecated
	public GPIOPin getOutput()
	{
		throw new todo.TODO();
	}
	
	public int getOutputBufferSize()
	{
		throw new todo.TODO();
	}
	
	public GPIOPinConfig getOutputConfig()
	{
		throw new todo.TODO();
	}
	
	public int getPulseAlignment()
	{
		throw new todo.TODO();
	}
	
	public int getPulsePeriod()
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
	
	public static PWMChannelConfig deserialize(InputStream __a)
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
		
		public PWMChannelConfig build()
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
		
		public Builder setIdleState(int __a)
		{
			throw new todo.TODO();
		}
		
		public Builder setOutputBufferSize(int __a)
		{
			throw new todo.TODO();
		}
		
		public Builder setOutputConfig(GPIOPinConfig __a)
		{
			throw new todo.TODO();
		}
		
		public Builder setPulseAlignment(int __a)
		{
			throw new todo.TODO();
		}
		
		public Builder setPulsePeriod(int __a)
		{
			throw new todo.TODO();
		}
		
		public Builder setScaleFactor(double __a)
		{
			throw new todo.TODO();
		}
	}
}


