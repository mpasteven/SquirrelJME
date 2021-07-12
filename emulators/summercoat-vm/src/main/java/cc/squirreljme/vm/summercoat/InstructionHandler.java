// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.summercoat;

import cc.squirreljme.emulator.profiler.ProfiledThread;
import cc.squirreljme.emulator.vm.VMException;
import cc.squirreljme.runtime.cldc.debug.Debugging;
import dev.shadowtail.classfile.nncc.NativeInstructionType;

/**
 * Handler for virtual machine instructions.
 *
 * @since 2021/02/14
 */
public enum InstructionHandler
{
	/** Breakpoints. */
	BREAKPOINT(NativeInstructionType.BREAKPOINT, 0,
		NativeInstructionType.BREAKPOINT_MARKED, 0)
	{
		/**
		 * {@inheritDoc}
		 * @since 2021/02/14
		 */
		@Override
		protected void execute(int __op, NativeCPU __cpu, CPUCache __cache)
		{
			int[] argVal = __cache.argValCache;
			
			// Marker for the breakpoint
			String mark = (__op == NativeInstructionType.BREAKPOINT ?
				"Un" : Integer.toString(argVal[0], 16)
					.toUpperCase() + "h");
		
			// If profiling, immediately enter the frame to signal
			// a break point then exit it
			ProfiledThread profiler = __cpu.profiler;
			if (profiler != null)
			{
				String bit = "<breakpoint?" + mark + ">";
				profiler.enterFrame(bit, bit, bit);
				profiler.exitFrame();
			}
			
			// Is there a stored note?
			int noteId = (__op == NativeInstructionType
				.BREAKPOINT_MARKED ? argVal[1] : 0);
			String note = (noteId == 0 ? "" : VMUtils.readUtfSafe(
				__cpu.__state(), __cache.nowFrame.pool(noteId)));
			
			throw new VMException(String.format(
				"Breakpoint Hit (%s: %s)!", mark, note));
		}
	}
	
	/* End. */
	;
	
	/** Operation map. */
	@SuppressWarnings({"CheckForOutOfMemoryOnLargeArrayAllocation", 
		"MagicNumber"})
	private static InstructionHandler[] _OP_MAP =
		new InstructionHandler[256];
	
	/** The operation masks. */
	private final int[] _opMasks;
	
	static
	{
		// Build fully mapped lookup table
		InstructionHandler[] opMap = InstructionHandler._OP_MAP;
		for (InstructionHandler ih : InstructionHandler.values())
		{
			int[] opMasks = ih._opMasks;
			
			// Handle multiple potential operations and masks
			for (int q = 0, qn = opMasks.length; q < qn; q += 2)
				for (int i = opMasks[q], stop = i | opMasks[q + 1];
					i <= stop; i++)
				{
					// Do not allow replacing ever
					if (opMap[i] != null)
						throw Debugging.oops(i, opMap[i]);
					
					opMap[i] = ih;
				}
		}
	}
	
	/**
	 * Initializes the instruction handler.
	 * 
	 * @param __opMasks The operation ID followed by the mask, the mask for the
	 * operation for multiple operations, it
	 * will be noted that {@code 0} means only a single instruction. This must
	 * be a multiple of two.
	 * @since 2021/02/14
	 */
	InstructionHandler(int... __opMasks)
	{
		if (__opMasks.length <= 0 || (__opMasks.length & 1) != 0)
			throw Debugging.oops(this.ordinal(), __opMasks.length);
		
		this._opMasks = __opMasks;
	}
	
	/**
	 * Executes the given instruction.
	 * 
	 * @param __op The operation.
	 * @param __cpu The CPU for execution.
	 * @param __cache The CPU cache.
	 * @since 2021/02/14
	 */
	protected abstract void execute(int __op, NativeCPU __cpu,
		CPUCache __cache);
	
	/**
	 * Handles the given operation.
	 * 
	 * @param __op The operation.
	 * @param __cpu The Native CPU executing this.
	 * @throws VMException If the operation is not valid.
	 * @since 2021/02/14
	 */
	public static void handle(int __op, NativeCPU __cpu)
		throws VMException
	{
		// Make sure this is something that can be executed
		InstructionHandler handler = InstructionHandler._OP_MAP[__op];
		if (handler == null)
			throw new VMException("Invalid operation: " + __op);
		
		// Execute it
		handler.execute(__op, __cpu, __cpu.cache);
	}
	
	/**
	 * Checks if the given operation is valid or not.
	 * 
	 * @param __op The operation to check.
	 * @return If the operation is valid.
	 * @since 2021/02/14
	 */
	protected static boolean isValid(int __op)
	{
		return null != InstructionHandler._OP_MAP[__op];
	}
}
