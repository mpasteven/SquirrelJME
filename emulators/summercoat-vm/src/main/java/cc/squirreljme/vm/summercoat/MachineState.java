// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.summercoat;

import cc.squirreljme.emulator.profiler.ProfilerSnapshot;
import cc.squirreljme.emulator.vm.VMThreadModel;
import cc.squirreljme.jdwp.JDWPBinding;
import cc.squirreljme.jdwp.JDWPController;
import cc.squirreljme.jdwp.JDWPState;
import cc.squirreljme.jdwp.views.JDWPView;
import cc.squirreljme.jdwp.views.JDWPViewKind;
import cc.squirreljme.jvm.summercoat.constants.MemHandleKind;
import cc.squirreljme.jvm.summercoat.constants.StaticVmAttribute;
import cc.squirreljme.jvm.summercoat.constants.ThreadPropertyType;
import cc.squirreljme.jvm.summercoat.ld.mem.WritableMemory;
import cc.squirreljme.runtime.cldc.SquirrelJME;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.runtime.cldc.util.SortedTreeMap;
import cc.squirreljme.vm.summercoat.debug.DebugBase;
import cc.squirreljme.vm.summercoat.debug.DebugFrame;
import cc.squirreljme.vm.summercoat.debug.DebugThread;
import cc.squirreljme.vm.summercoat.debug.DebugThreadGroup;
import cc.squirreljme.vm.summercoat.debug.DebugType;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * This contains the machine state and stores everything about SummerCoat.
 *
 * @since 2019/06/19
 */
public final class MachineState
	implements JDWPBinding
{
	/** The virtual machine memory. */
	protected final WritableMemory memory;
	
	/** The profiler snapshot to write to. */
	protected final ProfilerSnapshot profiler;
	
	/** The memory handle manager. */
	protected final MemHandleManager memHandles =
		new MemHandleManager();
	
	/** The base address for the system ROM. */
	protected final int romBase;
	
	/** The threading model to use. */
	protected final VMThreadModel threadingModel;
	
	/** The threads which are known. */
	private final Map<Integer, NativeCPU> _vCpus =
		new SortedTreeMap<>();
	
	/** The JDWP Controller. */
	volatile JDWPController _jdwp;
	
	/** Attributes for {@link StaticVmAttribute}. */
	private volatile MemHandle _staticAttributes;
	
	/** The array base. */
	private volatile int _arrayBase;
	
	/** Was the supervisor okay? */
	private volatile boolean _superVisorOkay;
	
	/** The next virtual CPU id. */
	private volatile int _nextVCpuId;
	
	/**
	 * Initializes the machine state.
	 *
	 * @param __mem The memory state.
	 * @param __pf The profiler, this is optional.
	 * @param __romBase The ROM base.
	 * @param __threadModel The threading model.
	 * @throws NullPointerException If no memory was specified.
	 * @since 2019/12/28
	 */
	public MachineState(WritableMemory __mem, ProfilerSnapshot __pf,
		int __romBase, VMThreadModel __threadModel)
		throws NullPointerException
	{
		if (__mem == null)
			throw new NullPointerException("NARG");
		
		this.memory = __mem;
		this.profiler = __pf;
		this.romBase = __romBase;
		this.threadingModel = (__threadModel == null ?
			VMThreadModel.DEFAULT : __threadModel);
	}
	
	/**
	 * Returns all of the available CPUs.
	 * 
	 * @return All of the available CPUs.
	 * @since 2021/05/08
	 */
	protected NativeCPU[] cpus()
	{
		synchronized (this)
		{
			Collection<NativeCPU> rv = this._vCpus.values();
			return rv.toArray(new NativeCPU[rv.size()]);
		}
	}
	
	/**
	 * Creates a new virtual machine CPU.
	 * 
	 * @param __threadInfo Information on the current thread.
	 * @return A new virtual machine CPU.
	 * @since 2021/05/07
	 */
	protected NativeCPU createVmCpu(MemHandle __threadInfo)
		throws NullPointerException
	{
		if (__threadInfo == null)
			throw new NullPointerException("NARG");
		
		// Create new CPU
		NativeCPU rv = new NativeCPU(this, this.__nextVCpuId(),
			__threadInfo);
		
		// Store into the mapping
		synchronized (this)
		{
			this._vCpus.put(rv.vCpuId, rv);
		}
		
		return rv;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/07
	 */
	@Override
	public String[] debuggerLibraries()
	{
		throw Debugging.todo();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/07
	 */
	@Override
	public Object[] debuggerThreadGroups()
	{
		// Go through all threads and get the task handles
		Set<MemHandle> rv = new LinkedHashSet<>();
		for (NativeCPU cpu : this.cpus())
		{
			MemHandle threadInfo = DebugBase.handle(cpu.threadInfo,
				MemHandleKind.VM_THREAD);
			
			// Make sure this is an actual task
			rv.add(DebugBase.handle(DebugBase.getHandle(this,
				threadInfo, ThreadPropertyType.TASK), MemHandleKind.TASK));
		}
		
		return rv.toArray(new Object[rv.size()]);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/07
	 */
	@Override
	public <V extends JDWPView> V debuggerView(Class<V> __type,
		JDWPViewKind __kind, Reference<JDWPState> __state)
		throws NullPointerException
	{
		if (__type == null || __kind == null || __state == null)
			throw new NullPointerException("NARG");
		
		WeakReference<MachineState> self = new WeakReference<>(this);
		switch (__kind)
		{
			case FRAME:
				return __type.cast(new DebugFrame(self));
			
			case THREAD:
				return __type.cast(new DebugThread(self));
			
			case THREAD_GROUP:
				return __type.cast(new DebugThreadGroup(self));
			
			case TYPE:
				return __type.cast(new DebugType(self));
			
			default:
				throw Debugging.oops(__kind);
		}
	}
	
	/**
	 * Flags that the supervisor booted okay.
	 *
	 * @since 2019/06/19
	 */
	public final void flagSupervisorOkay()
	{
		synchronized (this)
		{
			this._superVisorOkay = true;
		}
	}
	
	/**
	 * Has the supervisor initialized correctly?
	 *
	 * @return If the supervisor initialized it was okay.
	 * @since 2019/06/19
	 */
	public final boolean isSupervisorOkay()
	{
		synchronized (this)
		{
			return this._superVisorOkay;
		}
	}
	
	/**
	 * Returns the memory handle manager.
	 * 
	 * @return The memory handles.
	 * @since 2021/05/11
	 */
	public final MemHandleManager memHandles()
	{
		return this.memHandles;
	}
	
	/**
	 * Returns the system memory.
	 * 
	 * @return The memory within the system.
	 * @since 2021/07/05
	 */
	public final WritableMemory memory()
	{
		return this.memory;
	}
	
	/**
	 * Sets the static attributes handle.
	 * 
	 * @param __memHandle The handle where static attributes are.
	 * @param __arrayBase The array base to use for accesses.
	 * @throws IllegalStateException If this has already been set.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/05/07
	 */
	protected final void setStaticAttributes(MemHandle __memHandle,
		int __arrayBase)
		throws IllegalStateException, NullPointerException
	{
		if (__memHandle == null)
			throw new NullPointerException("NARG");
		
		synchronized (this)
		{
			if (this._staticAttributes != null)
				throw new IllegalStateException("StaticAttrs set already.");
			
			this._staticAttributes = __memHandle;
			this._arrayBase = __arrayBase;
		}
	}
	
	/**
	 * Returns the value of the static attribute.
	 * 
	 * @param __id One of {@link StaticVmAttribute}. 
	 * @return The value of the given attribute.
	 * @throws IllegalStateException If the attributes were not set.
	 * @throws IllegalArgumentException If the ID is not valid.
	 */
	public final int staticAttribute(int __id)
		throws IllegalStateException, IllegalArgumentException
	{
		if (__id <= 0 || __id >= StaticVmAttribute.NUM_METRICS)
			throw new IllegalArgumentException("Invalid attribute: " + __id);
		
		return this.staticAttributes()
			.memReadInt(this._arrayBase + (4 * __id));
	}
	
	/**
	 * Returns the static attributes handle.
	 * 
	 * @return The static attributes handle.
	 * @throws IllegalStateException If these were not set.
	 * @since 2021/05/07
	 */
	public final MemHandle staticAttributes()
		throws IllegalStateException
	{
		MemHandle rv;
		synchronized (this)
		{
			rv = this._staticAttributes;
		}
		
		if (rv == null)
			throw new IllegalStateException("No attributes set.");
		
		return rv;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/07
	 */
	@Override
	public String vmDescription()
	{
		return "SquirrelJME SummerCoat " + this.vmVersion();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/07
	 */
	@Override
	public String vmName()
	{
		return "SquirrelJME SummerCoat";
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/07
	 */
	@Override
	public String vmVersion()
	{
		return SquirrelJME.RUNTIME_VERSION;
	}
	
	/**
	 * Returns the next virtual CPU id.
	 * 
	 * @return The next virtual CPU id.
	 * @since 2021/05/07
	 */
	private int __nextVCpuId()
	{
		synchronized (this)
		{
			return this._nextVCpuId++;
		}
	}
}

