// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package jdk.dio.counter;

import cc.squirreljme.runtime.cldc.annotation.ApiDefinedDeprecated;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import jdk.dio.DeviceConfig;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;

public final class PulseCounterConfig
	implements DeviceConfig<PulseCounter>, DeviceConfig.HardwareAddressing
{
	public static final int TYPE_FALLING_EDGE_ONLY =
		0;
	
	public static final int TYPE_NEGATIVE_PULSE =
		3;
	
	public static final int TYPE_POSITIVE_PULSE =
		2;
	
	public static final int TYPE_RISING_EDGE_ONLY =
		1;
	
	@ApiDefinedDeprecated
	public PulseCounterConfig(int __a, int __b, int __c)
	{
		throw new todo.TODO();
	}
	
	@ApiDefinedDeprecated
	public PulseCounterConfig(int __a, int __b, int __c, GPIOPinConfig __d)
	{
		throw new todo.TODO();
	}
	
	@ApiDefinedDeprecated
	public PulseCounterConfig(String __a, int __b, int __c)
	{
		throw new todo.TODO();
	}
	
	@ApiDefinedDeprecated
	public PulseCounterConfig(String __a, int __b, int __c, GPIOPinConfig __d
		)
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
	
	@ApiDefinedDeprecated
	public GPIOPin getSource()
	{
		throw new todo.TODO();
	}
	
	public GPIOPinConfig getSourceConfig()
	{
		throw new todo.TODO();
	}
	
	public int getType()
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
	
	public static PulseCounterConfig deserialize(InputStream __a)
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
		
		public PulseCounterConfig build()
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
		
		public Builder setSourceConfig(GPIOPinConfig __a)
		{
			throw new todo.TODO();
		}
		
		public Builder setType(int __a)
		{
			throw new todo.TODO();
		}
	}
}


