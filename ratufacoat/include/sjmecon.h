/* -*- Mode: C; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// --------------------------------------------------------------------------*/

/**
 * SquirrelJME constants.
 *
 * @since 2019/10/06
 */

/** Header guard. */
#ifndef SJME_hGRATUFACOATSJMONHSJMECONH
#define SJME_hGRATUFACOATSJMONHSJMECONH

/* Anti-C++. */
#ifdef __cplusplus
#ifndef SJME_CXX_IS_EXTERNED
#define SJME_CXX_IS_EXTERNED
#define SJME_cXRATUFACOATSJMONHSJMECONH
extern "C"
{
#endif /* #ifdef SJME_CXX_IS_EXTERNED */
#endif /* #ifdef __cplusplus */

/*--------------------------------------------------------------------------*/

/*****************************************************************************
***************************** DEFAULT CONSTANTS ******************************
*****************************************************************************/

/** Default RAM size. */
#define SJME_DEFAULT_RAM_SIZE SJME_JINT_C(16777216)

/** Minimum permitted RAM size. */
#define SJME_MINIMUM_RAM_SIZE SJME_JINT_C(65536)

/** Magic number for ROMs. */
#define SJME_ROM_MAGIC_NUMBER SJME_JINT_C(0x58455223)

/** Magic number for JARs. */
#define SJME_JAR_MAGIC_NUMBER SJME_JINT_C(0x00456570)

/** Check magic for BootRAM. */
#define SJME_BOOTRAM_CHECK_MAGIC SJME_JINT_C(0xFFFFFFFF)

/*****************************************************************************
**************************** CONFIGURATION ITEMS *****************************
*****************************************************************************/

/** End of configuration. */
#define SJME_CONFIG_END SJME_JINT_C(0)

/** Java VM Version. */
#define SJME_CONFIG_JAVA_VM_VERSION SJME_JINT_C(1)

/** Java VM Name. */
#define SJME_CONFIG_JAVA_VM_NAME SJME_JINT_C(2)

/** Java VM Vendor. */
#define SJME_CONFIG_JAVA_VM_VENDOR SJME_JINT_C(3)

/** Java VM E-Mail. */
#define SJME_CONFIG_JAVA_VM_EMAIL SJME_JINT_C(4)

/** Java VM URL. */
#define SJME_CONFIG_JAVA_VM_URL SJME_JINT_C(5)

/** The guest depth. */
#define SJME_CONFIG_GUEST_DEPTH SJME_JINT_C(6)

/** Main class. */
#define SJME_CONFIG_MAIN_CLASS SJME_JINT_C(7)

/** Main program arguments. */
#define SJME_CONFIG_MAIN_ARGUMENTS SJME_JINT_C(8)

/** Is this a MIDlet? */
#define SJME_CONFIG_IS_MIDLET SJME_JINT_C(9)

/** Define system propertly. */
#define SJME_CONFIG_DEFINE_PROPERTY SJME_JINT_C(10)

/** Classpath to use. */
#define SJME_CONFIG_CLASS_PATH SJME_JINT_C(11)

/** System call static field pointer. */
#define SJME_CONFIG_SYSCALL_STATIC_FIELD_POINTER SJME_JINT_C(12)

/** System call method pointer. */
#define SJME_CONFIG_SYSCALL_CODE_POINTER SJME_JINT_C(13)

/** System call pool pointer. */
#define SJME_CONFIG_SYSCALL_POOL_POINTER SJME_JINT_C(14)

/** Number of available options. */
#define SJME_CONFIG_NUM_OPTIONS SJME_JINT_C(15)

/*****************************************************************************
********************************* REGISTERS **********************************
*****************************************************************************/

/** The zero register. */
#define SJME_ZERO_REGISTER SJME_JINT_C(0)

/** The return value register (two slots, 1 + 2). */
#define SJME_RETURN_REGISTER SJME_JINT_C(1)

/** Second return register. */
#define SJME_RETURN_TWO_REGISTER SJME_JINT_C(2)

/** The exception register. */
#define SJME_EXCEPTION_REGISTER SJME_JINT_C(3)

/** The pointer containing static field data. */
#define SJME_STATIC_FIELD_REGISTER SJME_JINT_C(4)

/** Register which represents the current thread of execution. */
#define SJME_THREAD_REGISTER SJME_JINT_C(5)

/** Base for local registers (locals start here). */
#define SJME_LOCAL_REGISTER_BASE SJME_JINT_C(6)

/** The register containing the constant pool. */
#define SJME_POOL_REGISTER SJME_JINT_C(6)

/** The register which contains the next pool pointer to use. */
#define SJME_NEXT_POOL_REGISTER SJME_JINT_C(7)

/** The register of the first argument. */
#define SJME_ARGBASE_REGISTER SJME_JINT_C(8)

/*****************************************************************************
******************************** MATH TYPES **********************************
*****************************************************************************/

/** Add. */
#define SJME_MATH_ADD UINT8_C(0)

/** Subtract. */
#define SJME_MATH_SUB UINT8_C(1)

/** Multiply. */
#define SJME_MATH_MUL UINT8_C(2)

/** Divide. */
#define SJME_MATH_DIV UINT8_C(3)

/** Remainder. */
#define SJME_MATH_REM UINT8_C(4)

/** Negate. */
#define SJME_MATH_NEG UINT8_C(5)

/** Shift left. */
#define SJME_MATH_SHL UINT8_C(6)

/** Shift right. */
#define SJME_MATH_SHR UINT8_C(7)

/** Unsigned shift right. */
#define SJME_MATH_USHR UINT8_C(8)

/** And. */
#define SJME_MATH_AND UINT8_C(9)

/** Or. */
#define SJME_MATH_OR UINT8_C(10)

/** Xor. */
#define SJME_MATH_XOR UINT8_C(11)

/** Compare (Less). */
#define SJME_MATH_CMPL UINT8_C(12)

/** Compare (Greater). */
#define SJME_MATH_CMPG UINT8_C(13)

/** Sign 8-bit. */
#define SJME_MATH_SIGNX8 UINT8_C(14)

/** Sign 16-bit. */
#define SJME_MATH_SIGNX16 UINT8_C(15)

/*****************************************************************************
**************************** MEMORY OPERAND TYPES ****************************
*****************************************************************************/

/** Mask for read/write in memory. */
#define SJME_MEM_LOAD_MASK UINT8_C(0x08)

/** Mask for data types in memory. */
#define SJME_MEM_DATATYPE_MASK UINT8_C(0x07)

/*****************************************************************************
***************************** COMPARISON TYPES *******************************
*****************************************************************************/

/** Equals. */
#define SJME_COMPARETYPE_EQUALS SJME_JINT_C(0)

/** Not equals. */
#define SJME_COMPARETYPE_NOT_EQUALS SJME_JINT_C(1)

/** Less than. */
#define SJME_COMPARETYPE_LESS_THAN SJME_JINT_C(2)

/** Less or equals. */
#define SJME_COMPARETYPE_LESS_THAN_OR_EQUALS SJME_JINT_C(3)

/** Greater than. */
#define SJME_COMPARETYPE_GREATER_THAN SJME_JINT_C(4)

/** Greater or equals. */
#define SJME_COMPARETYPE_GREATER_THAN_OR_EQUALS SJME_JINT_C(5)

/** Always true. */
#define SJME_COMPARETYPE_TRUE SJME_JINT_C(6)

/** Always false. */
#define SJME_COMPARETYPE_FALSE SJME_JINT_C(7)

/*****************************************************************************
******************************* THREAD STATES ********************************
*****************************************************************************/

/** Maximum number of threads. */
#define SJME_THREAD_MAX SJME_JINT_C(32)

/** Mask for CPU threads. */
#define SJME_THREAD_MASK SJME_JINT_C(31)

/** Thread does not exist. */
#define SJME_THREAD_STATE_NONE 0

/** Thread is running. */
#define SJME_THREAD_STATE_RUNNING 1

/** Base size of arrays. */
#define SJME_ARRAY_BASE_SIZE SJME_JINT_C(20)

/*****************************************************************************
**************************** SYSTEM CALL INDEXES *****************************
*****************************************************************************/

/** Checks if the system call is supported. */
#define SJME_SYSCALL_QUERY_INDEX SJME_JINT_C(0)

/** Gets the last error state. */
#define SJME_SYSCALL_ERROR_GET SJME_JINT_C(1)

/** Sets the last error state. */
#define SJME_SYSCALL_ERROR_SET SJME_JINT_C(2)

/** Current wall clock milliseconds. */
#define SJME_SYSCALL_TIME_MILLI_WALL SJME_JINT_C(3)

/** Load cross-IPC exception. */
#define SJME_SYSCALL_EXCEPTION_LOAD SJME_JINT_C(4)

/** Current monotonic clock nanoseconds. */
#define SJME_SYSCALL_TIME_NANO_MONO SJME_JINT_C(5)

/** Store cross-IPC exception. */
#define SJME_SYSCALL_EXCEPTION_STORE SJME_JINT_C(6)

/** VM Information: Free memory in bytes. */
#define SJME_SYSCALL_VMI_MEM_FREE SJME_JINT_C(7)

/** VM Information: Used memory in bytes. */
#define SJME_SYSCALL_VMI_MEM_USED SJME_JINT_C(8)

/** VM Information: Max memory in bytes. */
#define SJME_SYSCALL_VMI_MEM_MAX SJME_JINT_C(9)

/** Invoke the garbage collector. */
#define SJME_SYSCALL_GARBAGE_COLLECT SJME_JINT_C(10)

/** Exit the VM. */
#define SJME_SYSCALL_EXIT SJME_JINT_C(11)

/** The API Level of the VM. */
#define SJME_SYSCALL_API_LEVEL SJME_JINT_C(12)

/** The pipe descriptor for stdin. */
#define SJME_SYSCALL_PD_OF_STDIN SJME_JINT_C(13)

/** The pipe descriptor for stdout. */
#define SJME_SYSCALL_PD_OF_STDOUT SJME_JINT_C(14)

/** The pipe descriptor for stderr. */
#define SJME_SYSCALL_PD_OF_STDERR SJME_JINT_C(15)

/** Pipe descriptor: Write single byte. */
#define SJME_SYSCALL_PD_WRITE_BYTE SJME_JINT_C(16)

/** Set memory. */
#define SJME_SYSCALL_MEM_SET SJME_JINT_C(17)

/** Set memory but in 4-byte pattern. */
#define SJME_SYSCALL_MEM_SET_INT SJME_JINT_C(18)

/** Get the height of the call stack. */
#define SJME_SYSCALL_CALL_STACK_HEIGHT SJME_JINT_C(19)

/** Gets the specified call stack item. */
#define SJME_SYSCALL_CALL_STACK_ITEM SJME_JINT_C(20)

/** Returns the string of the given pointer. */
#define SJME_SYSCALL_LOAD_STRING SJME_JINT_C(21)

/** Fatal ToDo hit. */
#define SJME_SYSCALL_FATAL_TODO SJME_JINT_C(22)

/** The supervisor booted okay! */
#define SJME_SYSCALL_SUPERVISOR_BOOT_OKAY SJME_JINT_C(23)

/** Get property of the framebuffer. */
#define SJME_SYSCALL_FRAMEBUFFER_PROPERTY SJME_JINT_C(24)

/** Is the byte order little endian? */
#define SJME_SYSCALL_BYTE_ORDER_LITTLE SJME_JINT_C(25)

/** Returns the pointer to the option JAR data. */
#define SJME_SYSCALL_OPTION_JAR_DATA SJME_JINT_C(26)

/** Returns the size of the option JAR data. */
#define SJME_SYSCALL_OPTION_JAR_SIZE SJME_JINT_C(27)

/** Loads the specific class (Modified UTF Pointer). */
#define SJME_SYSCALL_LOAD_CLASS_UTF SJME_JINT_C(28)

/** Loads the specified class. */
#define SJME_SYSCALL_LOAD_CLASS_BYTES SJME_JINT_C(29)

/** Set supervisor register value. */
#define SJME_SYSCALL_SUPERVISOR_PROPERTY_SET SJME_JINT_C(30)

/** Get supervisor register value. */
#define SJME_SYSCALL_SUPERVISOR_PROPERTY_GET SJME_JINT_C(31)

/** Set task ID for current frame. */
#define SJME_SYSCALL_FRAME_TASK_ID_SET SJME_JINT_C(32)

/** Get task ID for current frame. */
#define SJME_SYSCALL_FRAME_TASK_ID_GET SJME_JINT_C(33)

/** Perform device feedback. */
#define SJME_SYSCALL_DEVICE_FEEDBACK SJME_JINT_C(34)

/** Sleeps for the given number of millseconds and nanoseconds. */
#define SJME_SYSCALL_SLEEP SJME_JINT_C(35)

/** Squelch the framebuffer console and tell it to not print on-screen. */
#define SJME_SYSCALL_SQUELCH_FB_CONSOLE SJME_JINT_C(36)

/** Inter-Process Call. */
#define SJME_SYSCALL_IPC_CALL SJME_JINT_C(37)

/** System call count. */
#define SJME_SYSCALL_NUM_SYSCALLS SJME_JINT_C(38)

/*****************************************************************************
************************** SYSTEM CALL ERROR CODES ***************************
*****************************************************************************/

/** No error, or success. */
#define SJME_SYSCALL_ERROR_NO_ERROR SJME_JINT_C(0)

/** The system call is not supported. */
#define SJME_SYSCALL_ERROR_UNSUPPORTED_SYSTEM_CALL SJME_JINT_C(-1)

/** The pipe descriptor is not valid. */
#define SJME_SYSCALL_ERROR_PIPE_DESCRIPTOR_INVALID SJME_JINT_C(-2)

/** Write error when writing to the pipe. */
#define SJME_SYSCALL_ERROR_PIPE_DESCRIPTOR_BAD_WRITE SJME_JINT_C(-3)

/** Value out of range. */
#define SJME_SYSCALL_ERROR_VALUE_OUT_OF_RANGE SJME_JINT_C(-4)

/** There is no framebuffer. */
#define SJME_SYSCALL_ERROR_NO_FRAMEBUFFER SJME_JINT_C(-5)

/** Permission denied. */
#define SJME_SYSCALL_ERROR_PERMISSION_DENIED SJME_JINT_C(-6)

/** Interrupted. */
#define SJME_SYSCALL_ERROR_INTERRUPTED SJME_JINT_C(-7)

/** Unknown error. */
#define SJME_SYSCALL_ERROR_UNKNOWN SJME_JINT_C(-8)

/** End of file. */
#define SJME_SYSCALL_ERROR_END_OF_FILE SJME_JINT_C(-9)

/** IPC Error. */
#define SJME_SYSCALL_ERROR_IPC_ERROR SJME_JINT_C(-10)

/*****************************************************************************
***************************** PIPE DESCRIPTORS *******************************
*****************************************************************************/

/** Pipe descriptor for standard output. */
#define SJME_PIPE_FD_STDOUT SJME_JINT_C(1)

/** Pipe descriptor for standard error. */
#define SJME_PIPE_FD_STDERR SJME_JINT_C(2)

/*****************************************************************************
***************************** CALL STACK ITEMS* ******************************
*****************************************************************************/

/** The class name. */
#define SJME_CALLSTACKITEM_CLASS_NAME SJME_JINT_C(0)

/** The method name. */
#define SJME_CALLSTACKITEM_METHOD_NAME SJME_JINT_C(1)

/** The method type. */
#define SJME_CALLSTACKITEM_METHOD_TYPE SJME_JINT_C(2)

/** The current file. */
#define SJME_CALLSTACKITEM_SOURCE_FILE SJME_JINT_C(3)

/** Source line. */
#define SJME_CALLSTACKITEM_SOURCE_LINE SJME_JINT_C(4)

/** The PC address. */
#define SJME_CALLSTACKITEM_PC_ADDRESS SJME_JINT_C(5)

/** Java operation. */
#define SJME_CALLSTACKITEM_JAVA_OPERATION SJME_JINT_C(6)

/** Java PC address. */
#define SJME_CALLSTACKITEM_JAVA_PC_ADDRESS SJME_JINT_C(7)

/** Current Task ID. */
#define SJME_CALLSTACKITEM_TASK_ID SJME_JINT_C(8)

/** The number of supported items. */
#define SJME_CALLSTACKITEM_NUM_ITEMS SJME_JINT_C(9)

/*****************************************************************************
*************************** FRAMEBUFFER PROPERTIES ***************************
*****************************************************************************/

/** The ID used for IPC calls for graphics. */
#define SJME_FB_IPC_ID SJME_JINT_C(0x47665821)

/** Returns the address of the framebuffer. */
#define SJME_FB_CONTROL_ADDRESS SJME_JINT_C(1)

/** Returns the width of the framebuffer. */
#define SJME_FB_CONTROL_WIDTH SJME_JINT_C(2)

/** Returns the height of the framebuffer. */
#define SJME_FB_CONTROL_HEIGHT SJME_JINT_C(3)

/** Returns the scanline length in pixels. */
#define SJME_FB_CONTROL_SCANLEN SJME_JINT_C(4)

/** Flushes the framebuffer. */
#define SJME_FB_CONTROL_FLUSH SJME_JINT_C(5)

/** Pixel format of the screen. */
#define SJME_FB_CONTROL_FORMAT SJME_JINT_C(6)

/** Returns the scanline length in bytes. */
#define SJME_FB_CONTROL_SCANLEN_BYTES SJME_JINT_C(7)

/** Returns the number of bytes per pixel. */
#define SJME_FB_CONTROL_BYTES_PER_PIXEL SJME_JINT_C(8)

/** Number of pixels in the display. */
#define SJME_FB_CONTROL_NUM_PIXELS SJME_JINT_C(9)

/** Number of bits per pixel. */
#define SJME_FB_CONTROL_BITS_PER_PIXEL SJME_JINT_C(10)

/** Get backlight level. */
#define SJME_FB_CONTROL_BACKLIGHT_LEVEL_GET SJME_JINT_C(11)

/** Set backlight level. */
#define SJME_FB_CONTROL_BACKLIGHT_LEVEL_SET SJME_JINT_C(12)

/** Maximum backlight level. */
#define SJME_FB_CONTROL_BACKLIGHT_LEVEL_MAX SJME_JINT_C(13)

/** Upload integer array. */
#define SJME_FB_CONTROL_UPLOAD_ARRAY_INT SJME_JINT_C(14)

/** The backing object for the framebuffer, if there is one. */
#define SJME_FB_CONTROL_BACKING_ARRAY_OBJECT SJME_JINT_C(15)

/** Gets the capabilities of the display. */
#define SJME_FB_CONTROL_GET_CAPABILITIES SJME_JINT_C(16)

/** Query acceleration graphics support. */
#define SJME_FB_CONTROL_ACCEL_FUNC_QUERY SJME_JINT_C(17)

/** Invoke accelerated graphics operation. */
#define SJME_FB_CONTROL_ACCEL_FUNC_INVOKE SJME_JINT_C(18)

/** Request repaint. */
#define SJME_FB_CONTROL_REPAINT_REQUEST SJME_JINT_C(19)

/** Set frame-buffer title. */
#define SJME_FB_CONTROL_SET_TITLE SJME_JINT_C(20)

/** Maximum properties. */
#define SJME_FB_NUM_CONTROLS SJME_JINT_C(21)

/*****************************************************************************
************************** FRAMEBUFFER ACCELERATION **************************
*****************************************************************************/

/** Set color. */
#define SJME_FB_ACCEL_FUNC_SET_COLOR SJME_JINT_C(0)

/** Draw line. */
#define SJME_FB_ACCEL_FUNC_DRAW_LINE SJME_JINT_C(1)

/** Get the X clip. */
#define SJME_FB_ACCEL_FUNC_GET_CLIP_X SJME_JINT_C(2)

/** Get the Y clip. */
#define SJME_FB_ACCEL_FUNC_GET_CLIP_Y SJME_JINT_C(3)

/** Get the width clip. */
#define SJME_FB_ACCEL_FUNC_GET_CLIP_WIDTH SJME_JINT_C(4)

/** Get the height clip. */
#define SJME_FB_ACCEL_FUNC_GET_CLIP_HEIGHT SJME_JINT_C(5)

/** Set the clip. */
#define SJME_FB_ACCEL_FUNC_SET_CLIP SJME_JINT_C(6)

/** Draw rectangle. */
#define SJME_FB_ACCEL_FUNC_DRAW_RECT SJME_JINT_C(7)

/** Get the alpha color. */
#define SJME_FB_ACCEL_FUNC_GET_ALPHA_COLOR SJME_JINT_C(8)

/** Set the alpha color. */
#define SJME_FB_ACCEL_FUNC_SET_ALPHA_COLOR SJME_JINT_C(9)

/** Fill rectangle. */
#define SJME_FB_ACCEL_FUNC_FILL_RECT SJME_JINT_C(10)

/** Sets the fonts for the graphics. */
#define SJME_FB_ACCEL_FUNC_SET_FONT SJME_JINT_C(11)

/** Gets the font to use for drawing. */
#define SJME_FB_ACCEL_FUNC_GET_FONT SJME_JINT_C(12)

/** Draw sub-characters. */
#define SJME_FB_ACCEL_FUNC_DRAW_SUB_CHARS SJME_JINT_C(13)

/** Draw text. */
#define SJME_FB_ACCEL_FUNC_DRAW_TEXT SJME_JINT_C(14)

/** Get stroke style. */
#define SJME_FB_ACCEL_FUNC_GET_STROKE_STYLE SJME_JINT_C(15)

/** Set stroke style. */
#define SJME_FB_ACCEL_FUNC_SET_STROKE_STYLE SJME_JINT_C(16)

/** Copy area. */
#define SJME_FB_ACCEL_FUNC_COPY_AREA SJME_JINT_C(17)

/** Draw arc. */
#define SJME_FB_ACCEL_FUNC_DRAW_ARC SJME_JINT_C(18)

/** Draw ARGB16. */
#define SJME_FB_ACCEL_FUNC_DRAW_ARGB16 SJME_JINT_C(19)

/** Draw character. */
#define SJME_FB_ACCEL_FUNC_DRAW_CHAR SJME_JINT_C(20)

/** Draw characters. */
#define SJME_FB_ACCEL_FUNC_DRAW_CHARS SJME_JINT_C(21)

/** Draw RGB. */
#define SJME_FB_ACCEL_FUNC_DRAW_RGB SJME_JINT_C(22)

/** Draw RGB16. */
#define SJME_FB_ACCEL_FUNC_DRAW_RGB16 SJME_JINT_C(23)

/** Draw round rectangle. */
#define SJME_FB_ACCEL_FUNC_DRAW_ROUND_RECT SJME_JINT_C(24)

/** Fill arc. */
#define SJME_FB_ACCEL_FUNC_FILL_ARC SJME_JINT_C(25)

/** Fill round rectangle. */
#define SJME_FB_ACCEL_FUNC_FILL_ROUND_RECT SJME_JINT_C(26)

/** Fill triangle. */
#define SJME_FB_ACCEL_FUNC_FILL_TRIANGLE SJME_JINT_C(27)

/** Get blending mode. */
#define SJME_FB_ACCEL_FUNC_GET_BLENDING_MODE SJME_JINT_C(28)

/** Get display color. */
#define SJME_FB_ACCEL_FUNC_GET_DISPLAY_COLOR SJME_JINT_C(29)

/** Set blending mode. */
#define SJME_FB_ACCEL_FUNC_SET_BLENDING_MODE SJME_JINT_C(30)

/** Draw region. */
#define SJME_FB_ACCEL_FUNC_DRAW_REGION SJME_JINT_C(31)

/** The number of draw functions. */
#define SJME_FB_NUM_ACCEL_FUNC SJME_JINT_C(32)

/*****************************************************************************
*************************** FRAMEBUFFER CAPABILITY ***************************
*****************************************************************************/

/** Has touch. */
#define SJME_FB_CAPABILITY_TOUCH SJME_JINT_C(0x01)

/** Has keyboard. */
#define SJME_FB_CAPABILITY_KEYBOARD SJME_JINT_C(0x02)

/** The JVM pushes to the IPC handler when events happen. */
#define SJME_FB_CAPABILITY_IPC_EVENTS SJME_JINT_C(0x04)

/** Screen flipping can be detected. */
#define SJME_FB_CAPABILITY_SCREEN_FLIP SJME_JINT_C(0x08)

/** Screen has colors other than a single shade. */
#define SJME_FB_CAPABILITY_COLOR SJME_JINT_C(0x10)

/*****************************************************************************
*************************** SUPERVISOR PROPERTIES ****************************
*****************************************************************************/

/** The static field register of the task syscall handler. */
#define SJME_SUPERPROP_TASK_SYSCALL_STATIC_FIELD_POINTER SJME_JINT_C(1)

/** The method pointer of the task syscall method. */
#define SJME_SUPERPROP_TASK_SYSCALL_METHOD_HANDLER SJME_JINT_C(2)

/** The pool pointer of the task syscall method. */
#define SJME_SUPERPROP_TASK_SYSCALL_METHOD_POOL_POINTER SJME_JINT_C(3)

/** The number of available properties. */
#define SJME_SUPERPROP_NUM_PROPERTIES SJME_JINT_C(4)

/*****************************************************************************
************************************ OTHER ***********************************
*****************************************************************************/

/** The Thread ID for out of bounds IPC events. */
#define SJME_OOB_IPC_THREAD SJME_JINT_C(0xFFFFFFFF)

/*--------------------------------------------------------------------------*/

/* Anti-C++. */
#ifdef __cplusplus
#ifdef SJME_cXRATUFACOATSJMONHSJMECONH
}
#undef SJME_cXRATUFACOATSJMONHSJMECONH
#undef SJME_CXX_IS_EXTERNED
#endif /** #ifdef SJME_cXRATUFACOATSJMONHSJMECONH */
#endif /* #ifdef __cplusplus */

/** Header guard. */
#endif /* #ifndef SJME_hGRATUFACOATSJMONHSJMECONH */

