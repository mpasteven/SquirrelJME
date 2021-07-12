// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package jdk.dio.uart;

import cc.squirreljme.runtime.cldc.annotation.ApiDefinedDeprecated;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import jdk.dio.DeviceConfig;

public final class UARTConfig
	implements DeviceConfig<UART>, DeviceConfig.HardwareAddressing
{
	public static final int DATABITS_5 =
		5;
	
	public static final int DATABITS_6 =
		6;
	
	public static final int DATABITS_7 =
		7;
	
	public static final int DATABITS_8 =
		8;
	
	public static final int DATABITS_9 =
		9;
	
	public static final int FLOWCONTROL_NONE =
		0;
	
	public static final int FLOWCONTROL_RTSCTS_IN =
		1;
	
	public static final int FLOWCONTROL_RTSCTS_OUT =
		2;
	
	public static final int FLOWCONTROL_XONXOFF_IN =
		4;
	
	public static final int FLOWCONTROL_XONXOFF_OUT =
		8;
	
	public static final int PARITY_EVEN =
		2;
	
	public static final int PARITY_MARK =
		3;
	
	public static final int PARITY_NONE =
		0;
	
	public static final int PARITY_ODD =
		1;
	
	public static final int PARITY_SPACE =
		4;
	
	public static final int STOPBITS_1 =
		1;
	
	public static final int STOPBITS_1_5 =
		2;
	
	public static final int STOPBITS_2 =
		3;
	
	@ApiDefinedDeprecated
	public UARTConfig(int __a, int __b, int __c, int __d, int __e, int __f, 
		int __g)
	{
		throw new todo.TODO();
	}
	
	@ApiDefinedDeprecated
	public UARTConfig(int __a, int __b, int __c, int __d, int __e, int __f, 
		int __g, int __h, int __i)
	{
		throw new todo.TODO();
	}
	
	@ApiDefinedDeprecated
	public UARTConfig(String __a, int __b, int __c, int __d, int __e, int __f
		, int __g)
	{
		throw new todo.TODO();
	}
	
	@ApiDefinedDeprecated
	public UARTConfig(String __a, int __b, int __c, int __d, int __e, int __f
		, int __g, int __h, int __i)
	{
		throw new todo.TODO();
	}
	
	@Override
	public boolean equals(Object __a)
	{
		throw new todo.TODO();
	}
	
	public int getBaudRate()
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
	
	public int getDataBits()
	{
		throw new todo.TODO();
	}
	
	public int getFlowControlMode()
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
	
	public int getParity()
	{
		throw new todo.TODO();
	}
	
	public int getStopBits()
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
	
	public static UARTConfig deserialize(InputStream __a)
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
		
		public UARTConfig build()
		{
			throw new todo.TODO();
		}
		
		public Builder setBaudRate(int __a)
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
		
		public Builder setDataBits(int __a)
		{
			throw new todo.TODO();
		}
		
		public Builder setFlowControlMode(int __a)
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
		
		public Builder setParity(int __a)
		{
			throw new todo.TODO();
		}
		
		public Builder setStopBits(int __a)
		{
			throw new todo.TODO();
		}
	}
}


