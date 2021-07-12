// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm;

/**
 * This interface contains the various error codes for all of the system calls.
 *
 * @since 2019/05/23
 */
public final class SystemCallError
{
	/** No error, or success. */
	public static final byte NO_ERROR =
		0;
	
	/** The system call is not supported. */
	public static final byte UNSUPPORTED_SYSTEM_CALL =
		-1;
	
	/** The pipe descriptor is not valid. */
	public static final byte PIPE_DESCRIPTOR_INVALID =
		-2;
	
	/** Write error when writing to the pipe. */
	public static final byte PIPE_DESCRIPTOR_BAD_WRITE =
		-3;
	
	/** Value out of range. */
	public static final byte VALUE_OUT_OF_RANGE =
		-4;
	
	/** No frame buffer exists. */
	public static final byte NO_FRAMEBUFFER =
		-5;
	
	/** Permission denied. */
	public static final byte PERMISSION_DENIED =
		-6;
	
	/** Interrupted. */
	public static final byte INTERRUPTED =
		-7;
	
	/** Unknown error. */
	public static final byte UNKNOWN =
		-8;
	
	/** End of file reached. */
	public static final byte END_OF_FILE =
		-9;
	
	/** Error with IPC Call. */
	public static final byte IPC_ERROR =
		-10;
	
	/** Unknown class. */
	public static final byte NO_SUCH_CLASS =
		-11;
	
	/** No such thread exists. */
	public static final byte NO_SUCH_THREAD =
		-12;
	
	/** Thread already has a context. */
	public static final byte THREAD_HAS_CONTEXT =
		-13;
	
	/** No such configuration key. */
	public static final byte NO_SUCH_CONFIG_KEY =
		-14;
	
	/** Invalid memory handle kind. */
	public static final byte INVALID_MEMHANDLE_KIND =
		-15;
	
	/** Could not flush the pipe. */
	public static final byte PIPE_DESCRIPTOR_BAD_FLUSH =
		-16;
	
	/**
	 * Not used.
	 *
	 * @since 2019/05/23
	 */
	private SystemCallError()
	{
	}
	
	/**
	 * Converts the error to a string.
	 *
	 * @param __err The input error.
	 * @return The resulting string.
	 * @since 2020/01/12
	 */
	public static String toString(int __err)
	{
		switch (__err)
		{
			case SystemCallError.NO_ERROR:
				return "NoError";
				
			case SystemCallError.UNSUPPORTED_SYSTEM_CALL:
				return "UnsupportedSystemCall";
				
			case SystemCallError.PIPE_DESCRIPTOR_INVALID:
				return "PDInvalid";
				
			case SystemCallError.PIPE_DESCRIPTOR_BAD_WRITE:
				return "PDBadWrite";
				
			case SystemCallError.VALUE_OUT_OF_RANGE:
				return "ValueOutOfRange";
				
			case SystemCallError.NO_FRAMEBUFFER:
				return "NoFramebuffer";
				
			case SystemCallError.PERMISSION_DENIED:
				return "PermissionDenied";
				
			case SystemCallError.INTERRUPTED:
				return "Interrupted";
				
			case SystemCallError.UNKNOWN:
				return "Unknown";
				
			case SystemCallError.END_OF_FILE:
				return "EndOfFile";
				
			case SystemCallError.IPC_ERROR:
				return "IPCError";
			
			case SystemCallError.NO_SUCH_CLASS:
				return "NoSuchClass";
				
			case SystemCallError.NO_SUCH_THREAD:
				return "NoSuchThread";
			
			case SystemCallError.THREAD_HAS_CONTEXT:
				return "ThreadHasContext";
			
			case SystemCallError.NO_SUCH_CONFIG_KEY:
				return "NoSuchConfigKey";
			
				// Some Other ID?
			default:
				return "ERROR" + __err;
		}
	}
}
