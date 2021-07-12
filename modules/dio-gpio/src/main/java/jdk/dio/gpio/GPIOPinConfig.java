// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package jdk.dio.gpio;

import cc.squirreljme.runtime.cldc.annotation.ApiDefinedDeprecated;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import jdk.dio.DeviceConfig;

public final class GPIOPinConfig
	implements DeviceConfig<GPIOPin>, DeviceConfig.HardwareAddressing
{
	public static final int DIR_BOTH_INIT_INPUT =
		2;
	
	public static final int DIR_BOTH_INIT_OUTPUT =
		3;
	
	public static final int DIR_INPUT_ONLY =
		0;
	
	public static final int DIR_OUTPUT_ONLY =
		1;
	
	public static final int MODE_INPUT_PULL_DOWN =
		2;
	
	public static final int MODE_INPUT_PULL_UP =
		1;
	
	public static final int MODE_OUTPUT_OPEN_DRAIN =
		8;
	
	public static final int MODE_OUTPUT_PUSH_PULL =
		4;
	
	public static final int TRIGGER_BOTH_EDGES =
		3;
	
	public static final int TRIGGER_BOTH_LEVELS =
		6;
	
	public static final int TRIGGER_FALLING_EDGE =
		1;
	
	public static final int TRIGGER_HIGH_LEVEL =
		4;
	
	public static final int TRIGGER_LOW_LEVEL =
		5;
	
	public static final int TRIGGER_NONE =
		0;
	
	public static final int TRIGGER_RISING_EDGE =
		2;
	
	@ApiDefinedDeprecated
	public GPIOPinConfig(int __a, int __b, int __c, int __d, int __e, boolean
		__f)
	{
		throw new todo.TODO();
	}
	
	@ApiDefinedDeprecated
	public GPIOPinConfig(String __a, int __b, int __c, int __d, int __e, 
		boolean __f)
	{
		throw new todo.TODO();
	}
	
	@Override
	public boolean equals(Object __a)
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
	
	public int getDirection()
	{
		throw new todo.TODO();
	}
	
	public int getDriveMode()
	{
		throw new todo.TODO();
	}
	
	public boolean getInitValue()
	{
		throw new todo.TODO();
	}
	
	public int getPinNumber()
	{
		throw new todo.TODO();
	}
	
	public int getTrigger()
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
	
	public static GPIOPinConfig deserialize(InputStream __a)
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
		
		public GPIOPinConfig build()
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
		
		public Builder setDirection(int __a)
		{
			throw new todo.TODO();
		}
		
		public Builder setDriveMode(int __a)
		{
			throw new todo.TODO();
		}
		
		public Builder setInitValue(boolean __a)
		{
			throw new todo.TODO();
		}
		
		public Builder setPinNumber(int __a)
		{
			throw new todo.TODO();
		}
		
		public Builder setTrigger(int __a)
		{
			throw new todo.TODO();
		}
	}
}


