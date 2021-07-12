// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.springcoat;

import cc.squirreljme.jvm.mle.ObjectShelf;
import cc.squirreljme.jvm.mle.brackets.TypeBracket;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.vm.springcoat.exceptions.SpringMLECallError;
import net.multiphasicapps.classfile.MethodDescriptor;

/**
 * Functions for {@link MLEObject}.
 *
 * @since 2020/06/18
 */
public enum MLEObject
	implements MLEFunction
{
	/** {@link ObjectShelf#arrayCheckStore(Object, Object)}. */
	ARRAY_CHECK_STORE("arrayCheckStore:(Ljava/lang/Object;" +
		"Ljava/lang/Object;)Z")
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/02/07
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			if (__args[0] == null || __args[0] == SpringNullObject.NULL)
				throw new SpringMLECallError("Null object.");
			
			// Object requested is not valid?
			if (!(__args[0] instanceof SpringObject))
				throw new SpringMLECallError(String.format(
					"Object not valid? %s (%s)", __args[0],
					__args[0].getClass()));
				
			SpringObject array = (SpringObject)__args[0];
			
			// Get the type that the array is
			SpringClass arrayType = array.type();
			if (arrayType.dimensions() <= 0)
				throw new SpringMLECallError("Object not an array? " +
					array);
			
			// Storing null is always valid
			if (__args[1] == null || __args[1] == SpringNullObject.NULL)
				return true;
			
			// Must be an object
			if (!(__args[1] instanceof SpringObject))
				throw new SpringMLECallError(
					String.format("Value is not an object? %s [for array %s]",
					__args[1], array));
			
			SpringObject value = (SpringObject)__args[1];
			
			// The component type of the array must be compatible with the
			// target type
			SpringClass targetType = value.type();
			return arrayType.componentType().isAssignableFrom(targetType);
		}
	}, 
	
	/** {@link ObjectShelf#arrayCopy(boolean[], int, boolean[], int, int)}. */
	ARRAY_COPY_BOOLEAN("arrayCopy:([ZI[ZII)V")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/22
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			MLEObject.<SpringArrayObjectBoolean>__arrayCopy(
				SpringArrayObjectBoolean.class,
				__args[0], (int)__args[1],
				__args[2], (int)__args[3], (int)__args[4]);
			return null;
		}
	},
	
	/** {@link ObjectShelf#arrayCopy(byte[], int, byte[], int, int)}. */
	ARRAY_COPY_BYTE("arrayCopy:([BI[BII)V")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/22
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			MLEObject.<SpringArrayObjectByte>__arrayCopy(
				SpringArrayObjectByte.class,
				__args[0], (int)__args[1],
				__args[2], (int)__args[3], (int)__args[4]);
			return null;
		}
	},
	
	/** {@link ObjectShelf#arrayCopy(short[], int, short[], int, int)}. */
	ARRAY_COPY_SHORT("arrayCopy:([SI[SII)V")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/22
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			MLEObject.<SpringArrayObjectShort>__arrayCopy(
				SpringArrayObjectShort.class,
				__args[0], (int)__args[1],
				__args[2], (int)__args[3], (int)__args[4]);
			return null;
		}
	},
	
	/** {@link ObjectShelf#arrayCopy(char[], int, char[], int, int)}. */
	ARRAY_COPY_CHAR("arrayCopy:([CI[CII)V")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/22
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			MLEObject.<SpringArrayObjectChar>__arrayCopy(
				SpringArrayObjectChar.class,
				__args[0], (int)__args[1],
				__args[2], (int)__args[3], (int)__args[4]);
			return null;
		}
	},
	
	/** {@link ObjectShelf#arrayCopy(int[], int, int[], int, int)}. */
	ARRAY_COPY_INTEGER("arrayCopy:([II[III)V")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/22
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			MLEObject.<SpringArrayObjectInteger>__arrayCopy(
				SpringArrayObjectInteger.class,
				__args[0], (int)__args[1],
				__args[2], (int)__args[3], (int)__args[4]);
			return null;
		}
	},
	
	/** {@link ObjectShelf#arrayCopy(long[], int, long[], int, int)}. */
	ARRAY_COPY_LONG("arrayCopy:([JI[JII)V")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/22
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			MLEObject.<SpringArrayObjectLong>__arrayCopy(
				SpringArrayObjectLong.class,
				__args[0], (int)__args[1],
				__args[2], (int)__args[3], (int)__args[4]);
			return null;
		}
	},
	
	/** {@link ObjectShelf#arrayCopy(float[], int, float[], int, int)}. */
	ARRAY_COPY_FLOAT("arrayCopy:([FI[FII)V")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/22
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			MLEObject.<SpringArrayObjectFloat>__arrayCopy(
				SpringArrayObjectFloat.class,
				__args[0], (int)__args[1],
				__args[2], (int)__args[3], (int)__args[4]);
			return null;
		}
	},
	
	/** {@link ObjectShelf#arrayCopy(double[], int, double[], int, int)}. */
	ARRAY_COPY_DOUBLE("arrayCopy:([DI[DII)V")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/22
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			MLEObject.<SpringArrayObjectDouble>__arrayCopy(
				SpringArrayObjectDouble.class,
				__args[0], (int)__args[1],
				__args[2], (int)__args[3], (int)__args[4]);
			return null;
		}
	},
	
	/** {@link ObjectShelf#arrayLength(Object)}. */
	ARRAY_LENGTH("arrayLength:(Ljava/lang/Object;)I")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/18
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			SpringObject object = MLEType.__notNullObject(__args[0]);
			
			if (object instanceof SpringArrayObject)
				return ((SpringArrayObject)object).length();
			
			return -1;
		}
	},
	
	/** {@link ObjectShelf#arrayNew(TypeBracket, int)}. */
	ARRAY_NEW( "arrayNew:(Lcc/squirreljme/jvm/mle/brackets/" +
		"TypeBracket;I)Ljava/lang/Object;")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/18
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			int len = (int)__args[1];
			if (len < 0)
				throw new SpringMLECallError("Negative array size.");
			
			SpringClass type = MLEType.__type(__args[0]).getSpringClass();
			if (!type.isArray())
				throw new SpringMLECallError("Type not an array.");
			
			return __thread.allocateArray(type, len);
		}
	},
	
	/** {@link ObjectShelf#holdsLock(Thread, Object)}. */
	HOLDS_LOCK("holdsLock:(Ljava/lang/Thread;Ljava/lang/Object;)Z")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/27
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			SpringThread vmThread = MLEThread.__vmThread(
				MLEThread.TO_VM_THREAD.handle(__thread, __args[0]))
				.getThread();
			SpringObject target = (SpringObject)__args[1];
			
			// Cannot be null
			if (target == null || target == SpringNullObject.NULL)
				throw new SpringMLECallError("Target object is null.");
			
			return target.monitor().isHeldBy(vmThread);
		}
	}, 
	
	/** {@link ObjectShelf#identityHashCode(Object)}. */
	IDENTITY_HASH_CODE("identityHashCode:(Ljava/lang/Object;)I")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/18
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			SpringObject object = (SpringObject)__args[0];
			return System.identityHashCode(MLEType.__notNullObject(object));
		}
	},
	
	/** {@link ObjectShelf#isArray(Object)}. */
	IS_ARRAY("isInstance:(Ljava/lang/Object;Z")
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/04/07
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			SpringObject object = MLEType.__notNullObject(__args[0]);
			
			if (object instanceof SpringArrayObject)
				return 1;
			
			return 0;
		}
	},
	
	/** {@link ObjectShelf#isInstance(Object, TypeBracket)}. */
	IS_INSTANCE("isInstance:(Ljava/lang/Object;Lcc/squirreljme/" +
		"jvm/mle/brackets/TypeBracket;)Z")
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/02/07
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			throw Debugging.todo();
		}
	},
	
	/** {@link ObjectShelf#newInstance(TypeBracket)}. */
	NEW_INSTANCE("newInstance:(Lcc/squirreljme/jvm/mle/brackets/" +
		"TypeBracket;)Ljava/lang/Object;")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/18
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			SpringClass type = MLEType.__type(__args[0]).getSpringClass();
			
			if (type.isArray())
				throw new SpringMLECallError("Cannot newInstance array");
			
			return __thread.newInstance(type, new MethodDescriptor("()V"));
		}
	},
	
	/** {@link ObjectShelf#notify(Object, boolean)}. */
	NOTIFY("notify:(Ljava/lang/Object;Z)I")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/22
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			SpringObject target = MLEType.__notNullObject(__args[0]);
			boolean notifyAll = (int)__args[1] != 0; 
			
			// Signal the monitor
			return target.monitor().monitorNotify(__thread.thread, notifyAll);
		}
	},
	
	/** {@link ObjectShelf#wait(Object, long, int)}. */
	WAIT("wait:(Ljava/lang/Object;JI)I")
	{
		/**
		 * {@inheritDoc}
		 * @since 2020/06/22
		 */
		@Override
		public Object handle(SpringThreadWorker __thread, Object... __args)
		{
			SpringObject target = MLEType.__notNullObject(__args[0]);
			long ms = (long)__args[1];
			int ns = (int)__args[2];
			
			return target.monitor().monitorWait(__thread.thread, ms, ns);
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
	MLEObject(String __key)
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
	
	/**
	 * Copies the given array.
	 * 
	 * @param <A> The SpringCoat array type.
	 * @param __classy The class type.
	 * @param __src The source array.
	 * @param __srcOff The source offset.
	 * @param __dest The destination array.
	 * @param __destOff The destination offset.
	 * @param __len The length.
	 * @throws SpringMLECallError If the input is not valid.
	 * @since 2020/06/22
	 */
	@SuppressWarnings("SuspiciousSystemArraycopy")
	static <A extends SpringArrayObject> void __arrayCopy(
		Class<A> __classy, Object __src, int __srcOff,
		Object __dest, int __destOff, int __len)
		throws SpringMLECallError
	{
		// Wrong array type
		if (!__classy.isInstance(__src) ||
			!__classy.isInstance(__dest))
			throw new SpringMLECallError("Null array.");
		
		if (__srcOff < 0 || __destOff < 0 || __len < 0)
			throw new SpringMLECallError("Negative offset or length.");
		
		// Try to copy
		try
		{
			System.arraycopy(((SpringArrayObject)__src).array(), __srcOff,
				((SpringArrayObject)__dest).array(), __destOff, __len);
		}
		
		// Not a valid copy
		catch (ArrayStoreException|IndexOutOfBoundsException|
			NullPointerException e)
		{
			throw new SpringMLECallError("Invalid copy.", e);
		}
	}
}
