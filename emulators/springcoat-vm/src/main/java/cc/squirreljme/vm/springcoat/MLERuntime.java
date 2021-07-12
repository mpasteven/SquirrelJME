// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.springcoat;

import cc.squirreljme.jvm.mle.RuntimeShelf;
import cc.squirreljme.jvm.mle.constants.BuiltInEncodingType;
import cc.squirreljme.jvm.mle.constants.BuiltInLocaleType;
import cc.squirreljme.jvm.mle.constants.ByteOrderType;
import cc.squirreljme.jvm.mle.constants.MemoryProfileType;
import cc.squirreljme.jvm.mle.constants.VMDescriptionType;
import cc.squirreljme.jvm.mle.constants.VMStatisticType;
import cc.squirreljme.jvm.mle.constants.VMType;
import cc.squirreljme.runtime.cldc.SquirrelJME;
import cc.squirreljme.runtime.cldc.lang.LineEndingUtils;
import cc.squirreljme.vm.springcoat.exceptions.SpringMLECallError;

/**
 * Functions for {@link MLERuntime}.
 *
 * @since 2020/06/18
 */
public enum MLERuntime
	implements MLEFunction
{
	/** {@link RuntimeShelf#byteOrder()}. */
	BYTE_ORDER("byteOrder:()I")
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/02/09
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			// SpringCoat is always big endian
			return ByteOrderType.BIG_ENDIAN;
		}
	},
	
	/** {@link RuntimeShelf#currentTimeMillis()}. */
	CURRENT_TIME_MILLIS("currentTimeMillis:()J")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/18
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			return System.currentTimeMillis();
		}
	},
	
	/** {@link RuntimeShelf#encoding()}. */
	ENCODING("encoding:()I")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/18
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			return BuiltInEncodingType.UTF8;
		}
	},
	
	/** {@link RuntimeShelf#exit(int)}. */
	EXIT("exit:(I)V")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/18
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			__thread.machine.exit((int)__args[0]);
			return null;
		}
	},
	
	/** {@link RuntimeShelf#garbageCollect()}. */
	GARBAGE_COLLECT("garbageCollect:()V")
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/01/04
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			// Use the system GC since it is the only suggestion we can make
			Runtime.getRuntime().gc();
			return null;
		}
	},
	
	/** {@link RuntimeShelf#lineEnding()}. */
	LINE_ENDING("lineEnding:()I")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/18
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			return LineEndingUtils.toType(
				System.getProperty("line.separator"));
		}
	},
	
	/** {@link RuntimeShelf#locale()}. */
	LOCALE("locale:()I")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/18
		 */
		@SuppressWarnings("SwitchStatementWithTooFewBranches")
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			String country = System.getProperty("user.country",
				"unknown");
			String lang = System.getProperty("user.language",
				"unknown");
			
			switch (country)
			{
				case "US":
					switch (lang)
					{
						case "en":
							return BuiltInLocaleType.ENGLISH_US;
					}
					return BuiltInLocaleType.UNSPECIFIED;
				
				default:
					return BuiltInLocaleType.UNSPECIFIED;
			}
		}
	},
	
	/** {@link RuntimeShelf#memoryProfile()}. */
	MEMORY_PROFILE("memoryProfile:()I")
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/02/19
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			// No memory concerns
			return MemoryProfileType.NORMAL;
		}
	},
	
	/** {@link RuntimeShelf#nanoTime()}. */
	NANO_TIME("nanoTime:()J")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/18
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			return System.nanoTime();
		}
	},
	
	/** {@link RuntimeShelf#systemProperty(String)}. */
	SYSTEM_PROPERTY("systemProperty:(Ljava/lang/String;)" +
		"Ljava/lang/String;")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/18
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			String key = __thread
				.<String>asNativeObject(String.class, __args[0]);
			
			if (key == null)
				throw new SpringMLECallError("Null key.");
			
			return __thread.machine._sysproperties.get(key);
		}
	},
	
	/** {@link RuntimeShelf#vmDescription(int)}. */
	VM_DESCRIPTION("vmDescription:(I)Ljava/lang/String;")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/18
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			int index = (int)__args[0];
			if (index < 0 || index >= VMDescriptionType.NUM_TYPES)
				throw new SpringMLECallError(
					"Index out of range: " + index);
			
			switch (index)
			{
				case VMDescriptionType.EXECUTABLE_PATH:
					return null;
				
				case VMDescriptionType.OS_ARCH:
					return "springcoat/" + System.getProperty("os.arch");
					
				case VMDescriptionType.OS_NAME:
					return System.getProperty("os.name");
				
				case VMDescriptionType.OS_VERSION:
					return System.getProperty("os.version");
				
				case VMDescriptionType.VM_EMAIL:
					return "xer@multiphasicapps.net";
					
				case VMDescriptionType.VM_NAME:
					return __thread.machine.tasks.vmName();
				
				case VMDescriptionType.VM_VERSION:
					return __thread.machine.tasks.vmVersion();
				
				case VMDescriptionType.VM_URL:
					return "https://squirreljme.cc/";
					
				case VMDescriptionType.VM_VENDOR:
					return "Stephanie Gawroriski";
			}
			
			return null;
		}
	},
	
	/** {@link RuntimeShelf#vmStatistic(int)}. */
	VM_STATISTIC("vmStatistic:(I)J")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/18
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			int index = (int)__args[0];
			if (index < 0 || index >= VMStatisticType.NUM_STATISTICS)
				throw new SpringMLECallError(
					"Index out of range: " + index);
			
			switch (index)
			{
				case VMStatisticType.MEM_FREE:
					return Runtime.getRuntime().freeMemory();
					
				case VMStatisticType.MEM_MAX:
					return Runtime.getRuntime().maxMemory();
				
				case VMStatisticType.MEM_USED:
					return Runtime.getRuntime().totalMemory();
			}
			
			return 0L;
		}
	},
	
	/** {@link RuntimeShelf#vmType()}. */
	VM_TYPE("vmType:()I")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/18
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			return VMType.SPRINGCOAT;
		}
	},
	
	/* End. */
	;
	
	/** The dispatch key. */
	protected final String key;
	
	/**
	 * Initializes the dispatcher info.
	 *
	 * @param __key The key.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/06/18
	 */
	MLERuntime(String __key)
		throws NullPointerException
	{
		if (__key == null)
			throw new NullPointerException("NARG");
		
		this.key = __key;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2020/06/18
	 */
	@Override
	public String key()
	{
		return this.key;
	}
}
