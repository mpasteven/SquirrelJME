/* ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// --------------------------------------------------------------------------*/

/**
 * SquirrelJME RatufaCoat Source.
 *
 * @since 2019/06/02
 */

#include "sjmerc.h"

/** Default RAM size. */
#if defined(SJME_IS_DOS)
	#define SJME_DEFAULT_RAM_SIZE SJME_JINT_C(65536)
#else
	#define SJME_DEFAULT_RAM_SIZE SJME_JINT_C(16777216)
#endif

/** Virtual memory stuff. */
#if defined(SJME_VIRTUAL_MEM)
	/** Base of virtual memory. */
	#define SJME_VIRTUAL_MEM_BASE SJME_JINT_C(1048576)
	
	/** Rounding of virtual memory. */
	#define SJME_VIRTUAL_MEM_MASK SJME_JINT_C(1023)
#endif

/** Default size of configuration ROM. */
#define SJME_DEFAULT_CONF_SIZE SJME_JINT_C(65536)

/** Magic number for ROMs. */
#define SJME_ROM_MAGIC_NUMBER SJME_JINT_C(0x58455223)

/** Magic number for JARs. */
#define SJME_JAR_MAGIC_NUMBER SJME_JINT_C(0x00456570)

/** Check magic for BootRAM. */
#define SJME_BOOTRAM_CHECK_MAGIC SJME_JINT_C(0xFFFFFFFF)

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

/** Number of available options. */
#define SJME_CONFIG_NUM_OPTIONS SJME_JINT_C(12)

/** Maximum CPU registers. */
#define SJME_MAX_REGISTERS SJME_JINT_C(64)

/** Maximum system call arguments. */
#define SJME_MAX_SYSCALLARGS SJME_JINT_C(8)

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

/** Encoding mask. */
#define SJME_ENC_MASK UINT8_C(0xF0)

/** Math, R=RR, Integer. */
#define SJME_ENC_MATH_REG_INT UINT8_C(0x00)

/** Comparison mask. */
#define SJME_ENC_COMPARE_MASK UINT8_C(0x07)

/** Int comparison, then maybe jump. */
#define SJME_ENC_IF_ICMP UINT8_C(0x10)

/** Math type mask. */
#define SJME_ENC_MATH_MASK UINT8_C(0x0F)

/** Mask for Java order mode. */
#define SJME_ENC_MEMORY_JAVA_MASK UINT8_C(0x10)

/** Memory access, offset is in register. */
#define SJME_ENC_MEMORY_OFF_REG UINT8_C(0x20)

/** Memory access to big endian Java format, offset is in register. */
#define SJME_ENC_MEMORY_OFF_REG_JAVA UINT8_C(0x30)

/** Math, R=RC, Integer. */
#define SJME_ENC_MATH_CONST_INT UINT8_C(0x80)

/** Memory access, offset is a constant. */
#define SJME_ENC_MEMORY_OFF_ICONST UINT8_C(0xA0)

/** Memory access to big endian Java format, offset is a constant. */
#define SJME_ENC_MEMORY_OFF_ICONST_JAVA UINT8_C(0xB0)

/** Special. */
#define SJME_ENC_SPECIAL_A UINT8_C(0xE0)

/** Special.*/
#define SJME_ENC_SPECIAL_B UINT8_C(0xF0)

/** If equal to constant. */
#define SJME_OP_IFEQ_CONST UINT8_C(0xE6)

/** Debug entry to method. */
#define SJME_OP_DEBUG_ENTRY UINT8_C(0xE8)

/** Debug exit from method. */
#define SJME_OP_DEBUG_EXIT UINT8_C(0xE9)

/** Debug single point in method. */
#define SJME_OP_DEBUG_POINT UINT8_C(0xEA)

/** Return. */
#define SJME_OP_RETURN UINT8_C(0xF3)

/** Invoke. */
#define SJME_OP_INVOKE UINT8_C(0xF7)

/** Copy value in register. */
#define SJME_OP_COPY UINT8_C(0xF8)

/** Atomically decrements a memory address and gets the value. */
#define SJME_OP_ATOMIC_INT_DECREMENT_AND_GET UINT8_C(0xF9)

/** Atomically increments a memory address. */
#define SJME_OP_ATOMIC_INT_INCREMENT UINT8_C(0xFA)

/** System call. */
#define SJME_OP_SYSTEM_CALL UINT8_C(0xFB)

/** Load from pool, note that at code gen time this is aliased. */
#define SJME_OP_LOAD_POOL UINT8_C(0xFD)

/** Load from integer array. */
#define SJME_OP_LOAD_FROM_INTARRAY UINT8_C(0xFE)

/** Compare and exchange. */
#define SJME_OP_BREAKPOINT UINT8_C(0xFF)

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

/** Mask for read/write in memory. */
#define SJME_MEM_LOAD_MASK UINT8_C(0x08)

/** Mask for data types in memory. */
#define SJME_MEM_DATATYPE_MASK UINT8_C(0x07)

/** Object. */
#define SJME_DATATYPE_OBJECT UINT8_C(0)

/** Byte. */
#define SJME_DATATYPE_BYTE UINT8_C(1)

/** Short. */
#define SJME_DATATYPE_SHORT UINT8_C(2)

/** Character. */
#define SJME_DATATYPE_CHARACTER UINT8_C(3)

/** Integer. */
#define SJME_DATATYPE_INTEGER UINT8_C(4)

/** Float. */
#define SJME_DATATYPE_FLOAT UINT8_C(5)

/** Long. */
#define SJME_DATATYPE_LONG UINT8_C(6)

/** Double. */
#define SJME_DATATYPE_DOUBLE UINT8_C(7)

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

/** Checks if the system call is supported. */
#define SJME_SYSCALL_QUERY_INDEX SJME_JINT_C(0)

/** Gets the last error state. */
#define SJME_SYSCALL_ERROR_GET SJME_JINT_C(1)

/** Sets the last error state. */
#define SJME_SYSCALL_ERROR_SET SJME_JINT_C(2)

/** Current wall clock milliseconds (low). */
#define SJME_SYSCALL_TIME_LO_MILLI_WALL SJME_JINT_C(3)

/** Current wall clock milliseconds (high). */
#define SJME_SYSCALL_TIME_HI_MILLI_WALL SJME_JINT_C(4)

/** Current monotonic clock nanoseconds (low). */
#define SJME_SYSCALL_TIME_LO_NANO_MONO SJME_JINT_C(5)

/** Current monotonic clock nanoseconds (high). */
#define SJME_SYSCALL_TIME_HI_NANO_MONO SJME_JINT_C(6)

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

/** System call count. */
#define SJME_SYSCALL_NUM_SYSCALLS SJME_JINT_C(26)

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

/** Pipe descriptor for standard output. */
#define SJME_PIPE_FD_STDOUT SJME_JINT_C(1)

/** Pipe descriptor for standard error. */
#define SJME_PIPE_FD_STDERR SJME_JINT_C(2)

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

/** The number of supported items. */
#define SJME_CALLSTACKITEM_NUM_ITEMS SJME_JINT_C(8)

/** Returns the address of the framebuffer. */
#define SJME_FRAMEBUFFER_PROPERTY_ADDRESS SJME_JINT_C(1)

/** Returns the width of the framebuffer. */
#define SJME_FRAMEBUFFER_PROPERTY_WIDTH SJME_JINT_C(2)

/** Returns the height of the framebuffer. */
#define SJME_FRAMEBUFFER_PROPERTY_HEIGHT SJME_JINT_C(3)

/** Returns the scanline length. */
#define SJME_FRAMEBUFFER_PROPERTY_SCANLEN SJME_JINT_C(4)

/** Upper shift value mask, since shifting off the type is undefined. */
static sjme_jint sjme_sh_umask[32] =
{
	SJME_JINT_C(0xFFFFFFFF),
	SJME_JINT_C(0xFFFFFFFE),
	SJME_JINT_C(0xFFFFFFFC),
	SJME_JINT_C(0xFFFFFFF8),
	SJME_JINT_C(0xFFFFFFF0),
	SJME_JINT_C(0xFFFFFFE0),
	SJME_JINT_C(0xFFFFFFC0),
	SJME_JINT_C(0xFFFFFF80),
	SJME_JINT_C(0xFFFFFF00),
	SJME_JINT_C(0xFFFFFE00),
	SJME_JINT_C(0xFFFFFC00),
	SJME_JINT_C(0xFFFFF800),
	SJME_JINT_C(0xFFFFF000),
	SJME_JINT_C(0xFFFFE000),
	SJME_JINT_C(0xFFFFC000),
	SJME_JINT_C(0xFFFF8000),
	SJME_JINT_C(0xFFFF0000),
	SJME_JINT_C(0xFFFE0000),
	SJME_JINT_C(0xFFFC0000),
	SJME_JINT_C(0xFFF80000),
	SJME_JINT_C(0xFFF00000),
	SJME_JINT_C(0xFFE00000),
	SJME_JINT_C(0xFFC00000),
	SJME_JINT_C(0xFF800000),
	SJME_JINT_C(0xFF000000),
	SJME_JINT_C(0xFE000000),
	SJME_JINT_C(0xFC000000),
	SJME_JINT_C(0xF8000000),
	SJME_JINT_C(0xF0000000),
	SJME_JINT_C(0xE0000000),
	SJME_JINT_C(0xC0000000),
	SJME_JINT_C(0x80000000)
};

/** Lower shift value mask, since shifting off the type is undefined. */
static sjme_jint sjme_sh_lmask[32] =
{
	SJME_JINT_C(0xFFFFFFFF),
	SJME_JINT_C(0x7FFFFFFF),
	SJME_JINT_C(0x3FFFFFFF),
	SJME_JINT_C(0x1FFFFFFF),
	SJME_JINT_C(0x0FFFFFFF),
	SJME_JINT_C(0x07FFFFFF),
	SJME_JINT_C(0x03FFFFFF),
	SJME_JINT_C(0x01FFFFFF),
	SJME_JINT_C(0x00FFFFFF),
	SJME_JINT_C(0x007FFFFF),
	SJME_JINT_C(0x003FFFFF),
	SJME_JINT_C(0x001FFFFF),
	SJME_JINT_C(0x000FFFFF),
	SJME_JINT_C(0x0007FFFF),
	SJME_JINT_C(0x0003FFFF),
	SJME_JINT_C(0x0001FFFF),
	SJME_JINT_C(0x0000FFFF),
	SJME_JINT_C(0x00007FFF),
	SJME_JINT_C(0x00003FFF),
	SJME_JINT_C(0x00001FFF),
	SJME_JINT_C(0x00000FFF),
	SJME_JINT_C(0x000007FF),
	SJME_JINT_C(0x000003FF),
	SJME_JINT_C(0x000001FF),
	SJME_JINT_C(0x000000FF),
	SJME_JINT_C(0x0000007F),
	SJME_JINT_C(0x0000003F),
	SJME_JINT_C(0x0000001F),
	SJME_JINT_C(0x0000000F),
	SJME_JINT_C(0x00000007),
	SJME_JINT_C(0x00000003),
	SJME_JINT_C(0x00000001)
};

/** Bit mask for font drawing. */
static sjme_jint sjme_drawcharbitmask[] =
{
	SJME_JINT_C(0x01),
	SJME_JINT_C(0x02),
	SJME_JINT_C(0x04),
	SJME_JINT_C(0x08),
	SJME_JINT_C(0x10),
	SJME_JINT_C(0x20),
	SJME_JINT_C(0x40),
	SJME_JINT_C(0x80),
};

/** SQF Character Widths. */
static sjme_jbyte sjme_fontcharwidths[] =
{
	6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
	6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
	6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
	6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
	6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
	6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
	6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
	6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
	6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6
};

/** SQF Character validity. */
static sjme_jbyte sjme_fontisvalidchar[] =
{
	1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
	1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
	1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
	1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
	1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
	1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
	1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
	1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
	1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1
};

/** SQF Character Bitmaps. */
static sjme_jbyte sjme_fontcharbmp[] =
{
	12, 30, 51, 45, 55, 63, 55, 30, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 4, 4, 4, 4, 4, 4, 0, 4, 0, 0, 0, 10, 10, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 10, 10, 31, 10, 10, 10, 31, 10, 10, 0, 0, 0, 4,
	14, 21, 5, 14, 20, 20, 21, 14, 4, 0, 0, 18, 21, 10, 8, 4, 2, 10,
	21, 9, 0, 0, 0, 12, 18, 1, 1, 2, 5, 21, 9, 30, 0, 0, 0, 4, 4, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 12, 2, 2, 2, 2, 2, 2, 2, 12, 0, 0, 0,
	6, 8, 8, 8, 8, 8, 8, 8, 6, 0, 0, 0, 21, 14, 31, 14, 21, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 4, 4, 31, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 12, 8, 0, 0, 0, 0, 0, 0, 0, 14, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 12, 0, 0, 0, 0, 16, 16, 8, 8, 4, 4, 2, 2, 0, 0, 0,
	14, 17, 17, 21, 21, 21, 17, 17, 14, 0, 0, 0, 7, 4, 4, 4, 4, 4, 4,
	4, 31, 0, 0, 0, 14, 17, 16, 8, 4, 2, 1, 1, 31, 0, 0, 0, 14, 17, 16,
	16, 14, 16, 16, 17, 14, 0, 0, 0, 17, 17, 17, 17, 31, 16, 16, 16,
	16, 0, 0, 0, 31, 1, 1, 15, 16, 16, 16, 17, 14, 0, 0, 0, 30, 1, 1,
	1, 15, 17, 17, 17, 14, 0, 0, 0, 31, 16, 16, 8, 8, 4, 4, 2, 2, 0,
	0, 0, 14, 17, 17, 17, 14, 17, 17, 17, 14, 0, 0, 0, 14, 17, 17, 17,
	30, 16, 16, 16, 16, 0, 0, 0, 0, 0, 0, 12, 12, 0, 0, 0, 12, 12, 0,
	0, 0, 0, 0, 12, 12, 0, 0, 0, 12, 8, 0, 0, 0, 16, 8, 4, 2, 4, 8, 16,
	0, 0, 0, 0, 0, 0, 0, 0, 14, 0, 14, 0, 0, 0, 0, 0, 0, 2, 4, 8, 16,
	8, 4, 2, 0, 0, 0, 0, 14, 17, 16, 16, 8, 4, 4, 0, 4, 0, 0, 0, 14,
	17, 21, 21, 21, 21, 29, 1, 30, 0, 0, 0, 14, 17, 17, 17, 31, 17, 17,
	17, 17, 0, 0, 0, 15, 17, 17, 17, 15, 17, 17, 17, 15, 0, 0, 0, 30,
	1, 1, 1, 1, 1, 1, 1, 30, 0, 0, 0, 15, 17, 17, 17, 17, 17, 17, 17,
	15, 0, 0, 0, 31, 1, 1, 1, 7, 1, 1, 1, 31, 0, 0, 0, 31, 1, 1, 1, 7,
	1, 1, 1, 1, 0, 0, 0, 30, 1, 1, 1, 29, 17, 17, 17, 14, 0, 0, 0, 17,
	17, 17, 17, 31, 17, 17, 17, 17, 0, 0, 0, 28, 8, 8, 8, 8, 8, 8, 8,
	28, 0, 0, 0, 16, 16, 16, 16, 16, 16, 17, 17, 14, 0, 0, 0, 17, 17,
	9, 5, 3, 5, 9, 17, 17, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 31, 0, 0,
	0, 27, 21, 21, 17, 17, 17, 17, 17, 17, 0, 0, 0, 17, 19, 19, 21, 21,
	21, 25, 25, 17, 0, 0, 0, 14, 17, 17, 17, 17, 17, 17, 17, 14, 0, 0,
	0, 15, 17, 17, 17, 15, 1, 1, 1, 1, 0, 0, 0, 14, 17, 17, 17, 17, 17,
	21, 9, 22, 0, 0, 0, 15, 17, 17, 17, 15, 9, 17, 17, 17, 0, 0, 0, 14,
	17, 1, 1, 14, 16, 16, 17, 14, 0, 0, 0, 31, 4, 4, 4, 4, 4, 4, 4, 4,
	0, 0, 0, 17, 17, 17, 17, 17, 17, 17, 17, 14, 0, 0, 0, 17, 17, 17,
	17, 17, 17, 18, 20, 24, 0, 0, 0, 17, 17, 17, 17, 17, 21, 21, 21,
	10, 0, 0, 0, 17, 17, 10, 4, 4, 10, 17, 17, 17, 0, 0, 0, 17, 17, 17,
	10, 4, 4, 4, 4, 4, 0, 0, 0, 31, 16, 16, 8, 4, 2, 1, 1, 31, 0, 0,
	0, 14, 2, 2, 2, 2, 2, 2, 2, 14, 0, 0, 0, 0, 1, 1, 2, 2, 4, 4, 8,
	8, 0, 0, 0, 14, 8, 8, 8, 8, 8, 8, 8, 14, 0, 0, 0, 4, 10, 17, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 31, 0, 0, 0, 4, 8, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 17, 17, 17, 46, 0, 0,
	0, 1, 1, 1, 1, 15, 17, 17, 17, 15, 0, 0, 0, 0, 0, 0, 0, 28, 2, 2,
	2, 28, 0, 0, 0, 16, 16, 16, 16, 30, 17, 17, 17, 30, 0, 0, 0, 0, 0,
	0, 0, 14, 17, 15, 1, 30, 0, 0, 0, 0, 24, 4, 4, 14, 4, 4, 4, 4, 0,
	0, 0, 0, 0, 0, 0, 30, 17, 17, 17, 30, 16, 15, 0, 0, 1, 1, 1, 15,
	17, 17, 17, 17, 0, 0, 0, 0, 0, 4, 0, 4, 4, 4, 4, 4, 0, 0, 0, 0, 0,
	8, 0, 8, 8, 8, 8, 8, 4, 0, 0, 0, 1, 1, 17, 17, 15, 17, 17, 17, 0,
	0, 0, 0, 4, 4, 4, 4, 4, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 15, 21, 21,
	21, 21, 0, 0, 0, 0, 0, 0, 0, 15, 17, 17, 17, 17, 0, 0, 0, 0, 0, 0,
	0, 14, 17, 17, 17, 14, 0, 0, 0, 0, 0, 0, 0, 15, 17, 17, 17, 15, 1,
	1, 0, 0, 0, 0, 0, 30, 17, 17, 17, 30, 16, 16, 0, 0, 0, 0, 0, 28,
	2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 30, 1, 14, 16, 15, 0, 0, 0, 0, 0,
	4, 4, 14, 4, 4, 4, 24, 0, 0, 0, 0, 0, 0, 0, 17, 17, 17, 17, 14, 0,
	0, 0, 0, 0, 0, 0, 17, 17, 18, 20, 24, 0, 0, 0, 0, 0, 0, 0, 17, 17,
	21, 21, 10, 0, 0, 0, 0, 0, 0, 0, 17, 10, 4, 10, 17, 0, 0, 0, 0, 0,
	0, 0, 17, 17, 17, 30, 16, 16, 15, 0, 0, 0, 0, 0, 31, 8, 4, 2, 31,
	0, 0, 0, 8, 4, 4, 4, 2, 4, 4, 4, 8, 0, 0, 0, 8, 8, 8, 8, 8, 8, 8,
	8, 8, 0, 0, 0, 2, 4, 4, 4, 8, 4, 4, 4, 2, 0, 0, 0, 0, 22, 9, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 12, 30, 51, 45, 55, 63, 55, 30, 12, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 8, 0, 8, 8, 8, 8, 8, 8, 8, 0, 0, 0, 0, 8, 28, 10, 10, 10, 28,
	8, 0, 0, 24, 4, 4, 4, 14, 4, 4, 4, 30, 0, 0, 0, 12, 18, 2, 15, 2,
	15, 2, 18, 12, 0, 0, 0, 17, 17, 10, 4, 4, 14, 4, 14, 4, 0, 0, 0,
	10, 4, 30, 1, 1, 14, 16, 16, 15, 0, 0, 0, 30, 1, 3, 13, 17, 22, 24,
	16, 15, 0, 0, 0, 10, 4, 0, 0, 28, 2, 12, 16, 14, 0, 0, 0, 14, 17,
	29, 19, 19, 19, 29, 17, 14, 0, 0, 0, 4, 10, 10, 20, 0, 30, 0, 0,
	0, 0, 0, 0, 0, 40, 20, 10, 5, 10, 20, 40, 0, 0, 0, 0, 0, 0, 0, 0,
	30, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 0, 0, 0, 0, 0, 0, 0, 30,
	33, 39, 43, 39, 43, 43, 33, 30, 0, 0, 0, 30, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 8, 20, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 31,
	4, 4, 31, 0, 0, 0, 0, 28, 16, 28, 4, 28, 0, 0, 0, 0, 0, 0, 0, 28,
	16, 24, 16, 28, 0, 0, 0, 0, 0, 0, 0, 10, 4, 31, 16, 8, 4, 2, 1, 31,
	0, 0, 0, 0, 0, 0, 0, 0, 17, 17, 17, 15, 1, 1, 0, 22, 23, 23, 22,
	20, 20, 20, 20, 20, 0, 0, 0, 0, 0, 0, 0, 12, 12, 0, 0, 0, 0, 0, 0,
	10, 4, 0, 0, 30, 16, 12, 2, 30, 0, 0, 0, 12, 8, 8, 28, 0, 0, 0, 0,
	0, 0, 0, 0, 8, 20, 8, 0, 28, 0, 0, 0, 0, 0, 0, 0, 0, 5, 10, 20, 40,
	20, 10, 5, 0, 0, 0, 0, 30, 5, 5, 5, 13, 5, 5, 5, 30, 0, 0, 0, 0,
	0, 0, 0, 26, 21, 13, 5, 26, 0, 0, 0, 10, 17, 17, 17, 10, 4, 4, 4,
	4, 0, 0, 0, 0, 0, 4, 0, 4, 4, 2, 1, 17, 17, 14, 0, 2, 4, 0, 14, 17,
	17, 31, 17, 17, 0, 0, 0, 8, 4, 0, 14, 17, 17, 31, 17, 17, 0, 0, 0,
	4, 10, 0, 14, 17, 17, 31, 17, 17, 0, 0, 0, 22, 9, 0, 14, 17, 17,
	31, 17, 17, 0, 0, 0, 10, 0, 0, 14, 17, 17, 31, 17, 17, 0, 0, 0, 4,
	10, 4, 14, 17, 17, 31, 17, 17, 0, 0, 0, 30, 5, 5, 5, 15, 5, 5, 5,
	29, 0, 0, 0, 0, 30, 1, 1, 1, 1, 1, 1, 30, 4, 6, 0, 2, 4, 0, 30, 2,
	14, 2, 2, 30, 0, 0, 0, 4, 2, 0, 30, 2, 14, 2, 2, 30, 0, 0, 0, 4,
	10, 0, 30, 2, 14, 2, 2, 30, 0, 0, 0, 10, 0, 0, 30, 2, 14, 2, 2, 30,
	0, 0, 0, 2, 4, 0, 4, 4, 4, 4, 4, 4, 0, 0, 0, 8, 4, 0, 4, 4, 4, 4,
	4, 4, 0, 0, 0, 4, 10, 0, 4, 4, 4, 4, 4, 4, 0, 0, 0, 10, 0, 0, 4,
	4, 4, 4, 4, 4, 0, 0, 0, 14, 18, 18, 18, 23, 18, 18, 18, 14, 0, 0,
	0, 22, 9, 0, 19, 19, 21, 21, 25, 25, 0, 0, 0, 2, 4, 0, 14, 17, 17,
	17, 17, 14, 0, 0, 0, 8, 4, 0, 14, 17, 17, 17, 17, 14, 0, 0, 0, 4,
	10, 0, 14, 17, 17, 17, 17, 14, 0, 0, 0, 22, 9, 0, 14, 17, 17, 17,
	17, 14, 0, 0, 0, 10, 0, 0, 14, 17, 17, 17, 17, 14, 0, 0, 0, 0, 0,
	0, 17, 10, 4, 10, 17, 0, 0, 0, 0, 14, 25, 25, 21, 21, 21, 19, 19,
	14, 0, 0, 0, 2, 4, 0, 17, 17, 17, 17, 17, 14, 0, 0, 0, 8, 4, 0, 17,
	17, 17, 17, 17, 14, 0, 0, 0, 4, 10, 0, 17, 17, 17, 17, 17, 14, 0,
	0, 0, 10, 0, 0, 17, 17, 17, 17, 17, 14, 0, 0, 0, 8, 4, 0, 17, 17,
	10, 4, 4, 4, 0, 0, 0, 1, 1, 15, 17, 17, 17, 15, 1, 1, 0, 0, 0, 14,
	17, 17, 9, 5, 9, 17, 17, 13, 0, 0, 0, 0, 2, 4, 0, 14, 17, 17, 17,
	46, 0, 0, 0, 0, 8, 4, 0, 14, 17, 17, 17, 46, 0, 0, 0, 0, 4, 10, 0,
	14, 17, 17, 17, 46, 0, 0, 0, 0, 22, 9, 0, 14, 17, 17, 17, 46, 0,
	0, 0, 0, 0, 10, 0, 14, 17, 17, 17, 46, 0, 0, 0, 4, 10, 4, 0, 14,
	17, 17, 17, 46, 0, 0, 0, 0, 0, 0, 0, 11, 20, 14, 5, 26, 0, 0, 0,
	0, 0, 0, 0, 30, 1, 1, 1, 30, 4, 6, 0, 0, 4, 8, 0, 28, 18, 14, 2,
	28, 0, 0, 0, 0, 16, 8, 0, 28, 18, 14, 2, 28, 0, 0, 0, 0, 8, 20, 0,
	28, 18, 14, 2, 28, 0, 0, 0, 0, 0, 20, 0, 28, 18, 14, 2, 28, 0, 0,
	0, 0, 0, 2, 4, 0, 4, 4, 4, 4, 0, 0, 0, 0, 0, 8, 4, 0, 4, 4, 4, 4,
	0, 0, 0, 0, 0, 4, 10, 0, 4, 4, 4, 4, 0, 0, 0, 0, 0, 0, 10, 0, 4,
	4, 4, 4, 0, 0, 0, 0, 0, 24, 7, 10, 17, 17, 17, 14, 0, 0, 0, 0, 0,
	22, 9, 0, 15, 17, 17, 17, 0, 0, 0, 0, 0, 4, 8, 0, 12, 18, 18, 12,
	0, 0, 0, 0, 0, 8, 4, 0, 12, 18, 18, 12, 0, 0, 0, 0, 0, 4, 10, 0,
	12, 18, 18, 12, 0, 0, 0, 0, 0, 20, 10, 0, 12, 18, 18, 12, 0, 0, 0,
	0, 0, 0, 20, 0, 12, 18, 18, 12, 0, 0, 0, 0, 0, 0, 4, 0, 14, 0, 4,
	0, 0, 0, 0, 0, 0, 0, 0, 14, 25, 21, 19, 14, 0, 0, 0, 0, 0, 2, 4,
	0, 17, 17, 17, 14, 0, 0, 0, 0, 0, 8, 4, 0, 17, 17, 17, 14, 0, 0,
	0, 0, 0, 4, 10, 0, 17, 17, 17, 14, 0, 0, 0, 0, 0, 0, 10, 0, 17, 17,
	17, 14, 0, 0, 0, 0, 0, 8, 4, 0, 17, 17, 17, 30, 16, 15, 0, 0, 0,
	1, 1, 15, 17, 17, 17, 15, 1, 1, 0, 0, 0, 0, 10, 0, 17, 17, 17, 30,
	16, 15, 0
};

/** ROM load failure message. */
static sjme_jbyte sjme_romfailmessage[] =
{
	70, 97, 105, 108, 101, 100, 32, 116, 111, 32, 108, 111, 97, 100, 32, 82,
	79, 77, 33, 32, 73, 102, 32, 105, 116, 32, 105, 115, 32, 99, 111, 114,
	114, 117, 112, 116, 47, 109, 105, 115, 115, 105, 110, 103, 44, 32, 118,
	105, 115, 105, 116, 32, 104, 116, 116, 112, 115, 58, 47, 47, 115, 113,
	117, 105, 114, 114, 101, 108, 106, 109, 101, 46, 99, 99, 47, 117, 118,
	47, 100, 111, 119, 110, 108, 111, 97, 100, 46, 109, 107, 100, 32, 97,
	110, 100, 32, 100, 111, 119, 110, 108, 111, 97, 100, 32, 116, 104, 101,
	32, 90, 73, 80, 32, 116, 105, 116, 108, 101, 100, 32, 39, 83, 117, 109,
	109, 101, 114, 67, 111, 97, 116, 32, 82, 79, 77, 39, 46, 13, 10
};

/** BootRAM failed to load. */
static sjme_jbyte sjme_bootfailmessage[] =
{
	70, 97, 105, 108, 101, 100, 32, 116, 111, 32, 105, 110, 105, 116, 105, 97,
	108, 105, 122, 101, 32, 116, 104, 101, 32, 66, 111, 111, 116, 82, 65, 77,
	33, 32, 73, 102, 32, 116, 104, 105, 115, 32, 105, 115, 32, 100, 117, 101,
	32, 116, 111, 32, 97, 32, 99, 111, 114, 114, 117, 112, 116, 32, 82, 79, 77,
	44, 32, 118, 105, 115, 105, 116, 32, 104, 116, 116, 112, 115, 58, 47, 47,
	115, 113, 117, 105, 114, 114, 101, 108, 106, 109, 101, 46, 99, 99, 47, 117,
	118, 47, 100, 111, 119, 110, 108, 111, 97, 100, 46, 109, 107, 100, 32, 97,
	110, 100, 32, 100, 111, 119, 110, 108, 111, 97, 100, 32, 116, 104, 101, 32,
	90, 73, 80, 32, 116, 105, 116, 108, 101, 100, 32, 39, 83, 117, 109, 109,
	101, 114, 67, 111, 97, 116, 32, 82, 79, 77, 39, 46
};

/** SQF Defined Font. */
static sjme_sqf sjme_font =
{
	12,
	9,
	3,
	1,
	sjme_fontcharwidths,
	sjme_fontisvalidchar,
	sjme_fontcharbmp
};

/**
 * Division result.
 *
 * @since 2019/06/20
 */
typedef struct sjme_jint_div
{
	/** Quotient. */
	sjme_jint quot;

	/** Remainder. */
	sjme_jint rem;
} sjme_jint_div;

/** Virtual CPU. */
typedef struct sjme_cpu sjme_cpu;
struct sjme_cpu
{
	/** The state of this thread. */
	sjme_jint state;
	
	/** PC. */
	void* pc;
	
	/** Registers. */
	sjme_jint r[SJME_MAX_REGISTERS];
	
	/* System call arguments. */
	sjme_jint syscallargs[SJME_MAX_SYSCALLARGS];
	
	/* System call error numbers. */
	sjme_jint syscallerr[SJME_SYSCALL_NUM_SYSCALLS];
	
	/** Debug: Class name. */
	void* debugclassname;
	
	/** Debug: Method name. */
	void* debugmethodname;
	
	/** Debug: Method type. */
	void* debugmethodtype;
	
	/** Debug: Current line. */
	sjme_jint debugline;
	
	/** Debug: Java Operation. */
	sjme_jint debugjop;
	
	/** Debug: Java Address. */
	sjme_jint debugjpc;
	
	/** The parent CPU state. */
	sjme_cpu* parent;
};

/** Virtual machine state. */
struct sjme_jvm
{
	/** RAM. */
	void* ram;
	
	/** The size of RAM. */
	sjme_jint ramsize;
	
	/** ROM. */
	void* rom;
	
	/** The size of ROM. */
	sjme_jint romsize;
	
	/** Configuration ROM. */
	void* config;
	
	/** Size of of the configuration ROM. */
	sjme_jint configsize;
	
	/** Preset ROM. */
	void* presetrom;
	
	/** Framebuffer info. */
	sjme_framebuffer* fbinfo;
	
	/** Size of the framebuffer in bytes. */
	sjme_jint fbsize;
	
	/** Native functions. */
	sjme_nativefuncs* nativefuncs;
	
	/** Linearly fair CPU execution engine. */
	sjme_jint fairthreadid;
	
	/** Threads. */
	sjme_cpu threads[SJME_THREAD_MAX];
	
	/* Total instruction count. */
	sjme_jint totalinstructions;
	
	/* Did the supervisor boot okay? */
	sjme_jint supervisorokay;
	
	/* Console X position. */
	sjme_jint conx;
	
	/* Console Y position. */
	sjme_jint cony;
	
	/* Console width. */
	sjme_jint conw;
	
	/* Console height. */
	sjme_jint conh;
	
#if defined(SJME_VIRTUAL_MEM)
	/** Base address for Configuration ROM. */
	sjme_jint configbase;
	
	/** Base address for RAM. */
	sjme_jint rambase;
	
	/** Base address for ROM. */
	sjme_jint rombase;
	
	/** Base address for the framebuffer. */
	sjme_jint fbbase;
#endif
};

/**
 * Allocates the given number of bytes.
 *
 * @param size The number of bytes to allocate.
 * @since 2019/06/07
 */
void* sjme_malloc(sjme_jint size)
{
	void* rv;
	
	/* These will never allocate. */
	if (size <= 0)
		return NULL;
	
	/* Round size and include extra 4-bytes for size storage. */
	size = ((size + SJME_JINT_C(3)) & (~SJME_JINT_C(3))) + SJME_JINT_C(4);
	
	/* Exceeds maximum permitted allocation size? */
	if (sizeof(sjme_jint) > sizeof(size_t) && size > (sjme_jint)SIZE_MAX)
		return NULL;
	
	/* Use standard C function otherwise. */
	rv = calloc(1, size);
	
	/* Address out of range? Only when virtual pointers are not used! */
#if !defined(SJME_VIRTUAL_MEM)
	if (rv != NULL && ((uintptr_t)rv) > (uintptr_t)UINT32_C(0xFFFFFFFF))
	{
		free(rv);
		return NULL;
	}
#endif

	/* Did not allocate? */
	if (rv == NULL)
		return NULL;
	
	/* Store the size into this memory block for later free. */
	*((sjme_jint*)rv) = size;
	
	/* Return the adjusted pointer. */
	return SJME_POINTER_OFFSET_LONG(rv, 4);
}

/**
 * Frees the given pointer.
 *
 * @param p The pointer to free.
 * @since 2019/06/07
 */
void sjme_free(void* p)
{
	void* basep;
	sjme_jint size;
	
	/* Ignore null pointers. */
	if (p == NULL)
		return;
	
	/* Base pointer which is size shifted. */
	basep = SJME_POINTER_OFFSET_LONG(p, -4);
	
	/* Read size. */
	size = *((sjme_jint*)basep);
	
	/* Use Standard C free. */
	free(basep);
}

#if defined(SJME_VIRTUAL_MEM)
/** Returns the fake pointer for the given real pointer. */
void* sjme_fakeptr(sjme_jvm* jvm, void* ptr)
{
	void* oldptr;
	uintptr_t vptr;
	
	/* Pass through memory if no JVM specified. */
	if (jvm == NULL)
		return ptr;
	
	/* Map pointer to pure integer. */
	oldptr = ptr;
	vptr = SJME_POINTER_TO_JMEM(oldptr);
	
	/* Within Configuration ROM region? */
	if (vptr >= SJME_POINTER_TO_JMEM(jvm->config) &&
		vptr < SJME_POINTER_TO_JMEM(SJME_POINTER_OFFSET_LONG(jvm->config,
			jvm->configsize)))
		ptr = SJME_JINT_TO_POINTER(jvm->configbase +
			(sjme_jint)(vptr - SJME_POINTER_TO_JMEM(jvm->config)));
	
	/* Within RAM region? */
	else if (vptr >= SJME_POINTER_TO_JMEM(jvm->ram) &&
		vptr < SJME_POINTER_TO_JMEM(SJME_POINTER_OFFSET_LONG(jvm->ram,
			jvm->ramsize)))
		ptr = SJME_JINT_TO_POINTER(jvm->rambase +
			(sjme_jint)(vptr - SJME_POINTER_TO_JMEM(jvm->ram)));
	
	/* Within ROM region? */
	else if (vptr >= SJME_POINTER_TO_JMEM(jvm->rom) &&
		vptr < SJME_POINTER_TO_JMEM(SJME_POINTER_OFFSET_LONG(jvm->rom,
			jvm->romsize)))
		ptr = SJME_JINT_TO_POINTER(jvm->rombase +
			(sjme_jint)(vptr - SJME_POINTER_TO_JMEM(jvm->rom)));
	
	/* Within Framebuffer region? */
	else if (jvm->fbinfo != NULL && vptr >= SJME_POINTER_TO_JMEM(
		jvm->fbinfo->pixels) && vptr < SJME_POINTER_TO_JMEM(
		SJME_POINTER_OFFSET_LONG(jvm->fbinfo->pixels,
			jvm->fbsize)))
		ptr = SJME_JINT_TO_POINTER(jvm->fbbase +
			(sjme_jint)(vptr - SJME_POINTER_TO_JMEM(jvm->fbinfo->pixels)));
	
	/* No mapping found, pass through. */
	else
		ptr = oldptr;
	
	return ptr;
}

/** Returns the real pointer for the given virtual pointer. */
void* sjme_realptr(sjme_jvm* jvm, void* ptr)
{
	void* oldptr;
	sjme_jint vptr;
	
	/* Pass through memory if no JVM specified. */
	if (jvm == NULL)
		return ptr;
	
	/* Map pointer to pure integer. */
	oldptr = ptr;
	vptr = SJME_POINTER_TO_JINT(oldptr);
	
	/* Within Configuration ROM region? */
	if (vptr >= jvm->configbase && vptr < jvm->configbase + jvm->configsize)
		ptr = SJME_POINTER_OFFSET_LONG(jvm->config, (vptr - jvm->configbase));
	
	/* Within RAM region? */
	else if (vptr >= jvm->rambase && vptr < jvm->rambase + jvm->ramsize)
		ptr = SJME_POINTER_OFFSET_LONG(jvm->ram, (vptr - jvm->rambase));
	
	/* Within ROM region? */
	else if (vptr >= jvm->rombase && vptr < jvm->rombase + jvm->romsize)
		ptr = SJME_POINTER_OFFSET_LONG(jvm->rom, (vptr - jvm->rombase));
	
	/* Within the framebuffer? */
	else if (jvm->fbinfo != NULL && vptr >= jvm->fbbase &&
		vptr < jvm->fbbase + jvm->fbsize)
		ptr = SJME_POINTER_OFFSET_LONG(jvm->fbinfo->pixels,
			(vptr - jvm->fbbase));
	
	/* No mapping found, pass through. */
	else
		ptr = oldptr;
	
	return ptr;
}
#endif

/**
 * Reads a value from memory.
 *
 * @param jvm JVM pointer.
 * @param size The size of the value to read.
 * @param ptr The pointer to read from.
 * @param off The offset.
 * @return The read value.
 * @since 2019/06/08
 */
sjme_jint sjme_memread(sjme_jvm* jvm, sjme_jint size, void* ptr, sjme_jint off)
{
	/* Get the true pointer. */
#if defined(SJME_VIRTUAL_MEM)
	ptr = SJME_POINTER_OFFSET_LONG(sjme_realptr(jvm, ptr), off);
#else
	ptr = SJME_POINTER_OFFSET(ptr, off);
#endif
	
	/* Read value from memory. */
	switch (size)
	{
			/* Byte */
		case 1:
			return *((sjme_jbyte*)ptr);
		
			/* Short */
		case 2:
			return *((sjme_jshort*)ptr);
			
			/* Integer */
		case 4:
		default:
			return *((sjme_jint*)ptr);
	}
}

/**
 * Writes a value to memory.
 *
 * @param jvm JVM pointer.
 * @param size The size of the value to write.
 * @param ptr The pointer to read to.
 * @param off The offset.
 * @param value The value to write.
 * @since 2019/06/08
 */
void sjme_memwrite(sjme_jvm* jvm, sjme_jint size, void* ptr, sjme_jint off,
	sjme_jint value)
{
	/* Get the true pointer. */
#if defined(SJME_VIRTUAL_MEM)
	ptr = SJME_POINTER_OFFSET_LONG(sjme_realptr(jvm, ptr), off);
#else
	ptr = SJME_POINTER_OFFSET(ptr, off);
#endif
	
	/* Check against JVM areas. */
	if (jvm != NULL)
	{
		/* Ignore writes to ROM. */
		if (ptr >= jvm->rom &&
			ptr < SJME_POINTER_OFFSET_LONG(jvm->rom, jvm->romsize))
			return;
	}
	
	/* Write value to memory. */
	switch (size)
	{
			/* Byte */
		case 1:
			*((sjme_jbyte*)ptr) = (sjme_jbyte)value;
			break;
		
			/* Short */
		case 2:
			*((sjme_jshort*)ptr) = (sjme_jshort)value;
			break;
			
			/* Integer */
		case 4:
		default:
			*((sjme_jint*)ptr) = value;
			break;
	}
}

/**
 * Reads a big endian Java value from memory.
 * 
 * @param jvm JVM pointer.
 * @param size The size of the value to read.
 * @param ptr The pointer to read from.
 * @param off The offset.
 * @return The read value.
 * @since 2019/06/08
 */
sjme_jint sjme_memjread(sjme_jvm* jvm, sjme_jint size, void* ptr,
	sjme_jint off)
{
#if !defined(SJME_BIG_ENDIAN)
	sjme_jint rv;
#endif
	
#if defined(SJME_BIG_ENDIAN)
	/* No swapping needed. */
	return sjme_memread(jvm, size, ptr, off);

#else
	/* Read data. */
	rv = sjme_memread(jvm, size, ptr, off);
	
	/* Perform byte swapping. */
	switch (size)
	{
			/* Ignore byte. */
		case 1:
			break;
			
			/* Short */
		case 2:
			rv = (rv & SJME_JINT_C(0xFFFF0000)) |
				(((rv << SJME_JINT_C(8)) & SJME_JINT_C(0xFF00)) |
				((rv >> SJME_JINT_C(8)) & SJME_JINT_C(0x00FF)));
			break;
			
			/* Integer */
		case 4:
		default:
			rv = (((rv >> SJME_JINT_C(24)) & SJME_JINT_C(0x000000FF)) |
				((rv >> SJME_JINT_C(8)) & SJME_JINT_C(0x0000FF00)) |
				((rv << SJME_JINT_C(8)) & SJME_JINT_C(0x00FF0000)) |
				((rv << SJME_JINT_C(24)) & SJME_JINT_C(0xFF000000)));
			break;
	}
	
	/* Use value. */
	return rv;
#endif
}

/**
 * Writes a Java value to memory.
 *
 * @param jvm JVM pointer.
 * @param size The size of the value to write.
 * @param ptr The pointer to read to.
 * @param off The offset.
 * @param value The value to write.
 * @since 2019/06/14
 */
void sjme_memjwrite(sjme_jvm* jvm, sjme_jint size, void* ptr, sjme_jint off,
	sjme_jint value)
{
#if defined(SJME_LITTLE_ENDIAN)
	/* Perform byte swapping? */
	switch (size)
	{
			/* Byte */
		case 1:
			break;
		
			/* Short */
		case 2:
			value = (value & SJME_JINT_C(0xFFFF0000)) |
				(((value << SJME_JINT_C(8)) & SJME_JINT_C(0xFF00)) |
				((value >> SJME_JINT_C(8)) & SJME_JINT_C(0x00FF)));
			break;
			
			/* Integer */
		case 4:
		default:
			value = (((value >> SJME_JINT_C(24)) & SJME_JINT_C(0x000000FF)) |
				((value >> SJME_JINT_C(8)) & SJME_JINT_C(0x0000FF00)) |
				((value << SJME_JINT_C(8)) & SJME_JINT_C(0x00FF0000)) |
				((value << SJME_JINT_C(24)) & SJME_JINT_C(0xFF000000)));
			break;
	}
#endif

	/* Write to memory. */
	sjme_memwrite(jvm, size, ptr, off, value);
}

/**
 * Write value to memory and increment pointer.
 *
 * @param jvm The JVM.
 * @param size The size of value to write.
 * @param ptr The pointer to read from.
 * @param value The value to write.
 * @since 2019/06/19
 */
void sjme_memwritep(sjme_jvm* jvm, sjme_jint size, void** ptr, sjme_jint value)
{
	/* Write pointer value. */
	sjme_memwrite(jvm, size, *ptr, 0, value);
	
	/* Increment pointer. */
	*ptr = SJME_POINTER_OFFSET_LONG(*ptr, size);
}

/**
 * Read Java value from memory and increment pointer.
 *
 * @param jvm The JVM.
 * @param size The size of value to read.
 * @param ptr The pointer to read from.
 * @return The resulting value.
 * @since 2019/06/08
 */
sjme_jint sjme_memjreadp(sjme_jvm* jvm, sjme_jint size, void** ptr)
{
	sjme_jint rv;
	
	/* Read pointer value. */
	rv = sjme_memjread(jvm, size, *ptr, 0);
	
	/* Increment pointer. */
	*ptr = SJME_POINTER_OFFSET_LONG(*ptr, size);
	
	/* Return result. */
	return rv;
}

/**
 * Write Java value to memory and increment pointer.
 *
 * @param jvm The JVM.
 * @param size The size of value to write.
 * @param ptr The pointer to read from.
 * @param value The value to write.
 * @since 2019/06/14
 */
void sjme_memjwritep(sjme_jvm* jvm, sjme_jint size, void** ptr,
	sjme_jint value)
{
	/* Write pointer value. */
	sjme_memjwrite(jvm, size, *ptr, 0, value);
	
	/* Increment pointer. */
	*ptr = SJME_POINTER_OFFSET_LONG(*ptr, size);
}

/**
 * Decodes an integer value from operations which could be unaligned.
 *
 * @param jvm The JVM.
 * @param ptr The pointer to read from.
 * @return The resulting read value.
 * @since 2019/06/16
 */
sjme_jint sjme_opdecodejint(sjme_jvm* jvm, void** ptr)
{
	sjme_jint rv;
	
	/* Read all values. */
	rv = (sjme_memjreadp(jvm, 1, ptr) & SJME_JINT_C(0xFF)) << 24;
	rv |= (sjme_memjreadp(jvm, 1, ptr) & SJME_JINT_C(0xFF)) << 16;
	rv |= (sjme_memjreadp(jvm, 1, ptr) & SJME_JINT_C(0xFF)) << 8;
	rv |= (sjme_memjreadp(jvm, 1, ptr) & SJME_JINT_C(0xFF));
	
	return rv;
}

/**
 * Decodes a short value from operations which could be unaligned.
 *
 * @param jvm The JVM.
 * @param ptr The pointer to read from.
 * @return The resulting read value.
 * @since 2019/06/16
 */
sjme_jint sjme_opdecodejshort(sjme_jvm* jvm, void** ptr)
{
	sjme_jint rv;
	
	/* Read all values. */
	rv = (sjme_memjreadp(jvm, 1, ptr) & SJME_JINT_C(0xFF)) << 8;
	rv |= (sjme_memjreadp(jvm, 1, ptr) & SJME_JINT_C(0xFF));
	
	/* Sign extend? */
	if (rv & SJME_JINT_C(0x8000))
		rv |= SJME_JINT_C(0xFFFF0000);
	
	return rv;
}

/**
 * Decodes a variable unsigned int operation argument.
 *
 * @param jvm The JVM.
 * @param ptr The pointer to read from.
 * @return The resulting decoded value.
 * @since 2019/06/09
 */
sjme_jint sjme_opdecodeui(sjme_jvm* jvm, void** ptr)
{
	sjme_jint rv;
	
	/* Read single byte value from pointer. */
	rv = (sjme_memjreadp(jvm, 1, ptr) & SJME_JINT_C(0xFF));
	
	/* Encoded as a 15-bit value? */
	if ((rv & SJME_JINT_C(0x80)) != 0)
	{
		rv = (rv & SJME_JINT_C(0x7F)) << SJME_JINT_C(8);
		rv |= (sjme_memjreadp(jvm, 1, ptr) & SJME_JINT_C(0xFF));
	}

#if 0
	fprintf(stderr, "Decode: %d 0x%X\n", (int)rv, (unsigned)rv);
#endif
	
	/* Use read value. */
	return rv;
}

/**
 * Decodes a relative jump offset.
 *
 * @param jvm JVM pointer.
 * @param ptr The pointer to read from.
 * @return The resulting relative jump.
 * @since 2019/06/13
 */
sjme_jint sjme_opdecodejmp(sjme_jvm* jvm, void** ptr)
{
	sjme_jint rv;
	
	/* Decode value. */
	rv = sjme_opdecodeui(jvm, ptr);
	
	/* Negative branch? */
	if ((rv & SJME_JINT_C(0x00004000)) != 0)
		return rv | SJME_JINT_C(0xFFFF8000);
	return rv;
}

/** Draws single character onto the console. */
void sjme_console_drawplate(sjme_jvm* jvm, sjme_jint x, sjme_jint y,
	sjme_jbyte ch)
{
	sjme_jint r, c, cv, i, fontw, fonth;
	void* sp;
	sjme_jbyte* mp;
	sjme_jbyte bits;
	
	/* Check. */
	if (jvm == NULL)
		return;
	
	/* Ignore if out of bounds. */
	if (x < 0 || y < 0 || x >= jvm->conw || y >= jvm->conh)
		return;
	
	/* Font dimensions. */
	fontw = sjme_font.charwidths[0];
	fonth = sjme_font.pixelheight;
	
	/* Normalize to screen space. */
	x = x * fontw;
	y = y * fonth;
	
	/* Character data to draw. */
	if (sjme_font.isvalidchar[ch] == 0)
		ch = 0;
	mp = &sjme_font.charbmp[((sjme_jint)ch) * fonth * sjme_font.bytesperscan];
	
	/* Draw rows. */
	for (r = 0; r < fonth; r++)
	{
		/* Determine screen position. */
		sp = SJME_POINTER_OFFSET_LONG(jvm->fbinfo->pixels,
			(x * SJME_JINT_C(4)) +
			((y + r) * (jvm->fbinfo->scanlen * SJME_JINT_C(4))));
		
		/* Draw all pixel scans. */
		c = 0;
		for (cv = 0; cv < sjme_font.bytesperscan; cv++, mp++)
		{
			/* Get character bits */
			bits = *mp;
			
			/* Draw all of them. */
			for (i = 0; i < 8 && c < fontw; i++, c++)
				sjme_memwritep(jvm, 4, &sp,
					(((bits & sjme_drawcharbitmask[i]) != 0) ?
					SJME_JINT_C(0xFFFFFF) : 0));
		}
	}
}

/** Writes to the console screen and to the native method as well. */
sjme_jint sjme_console_pipewrite(sjme_jvm* jvm,
	sjme_jint (*writefunc)(sjme_jint b), sjme_jbyte* buf, sjme_jint off,
	sjme_jint len)
{
	sjme_jbyte b, donewline;
	sjme_jint i, code;
	
	/* There must be a JVM! */
	if (jvm == NULL)
		return -1;
	
	/* Write all the bytes to the output. */
	for (i = 0; i < len; i++, off++)
	{
		/* Read byte. */
		b = buf[off];
		
		/* Draw to the console in supervisor boot mode. */
		if (jvm->supervisorokay == 0 && jvm->fbinfo != NULL)
		{
			/* Carriage return? */
			donewline = 0;
			if (b == '\r')
				jvm->conx = 0;
			
			/* Newline? */
			else if (b == '\n')
				donewline = 1;
			
			/* Draw character? */
			else
			{
				/* Draw it. */
				sjme_console_drawplate(jvm, jvm->conx, jvm->cony, b);
				
				/* Move cursor up. */
				jvm->conx++;
				
				/* New line to print on? */
				if (jvm->conx >= jvm->conw)
					donewline = 1;
			}
			
			/* Doing a new line? */
			if (donewline != 0)
			{
				/* Move the cursor to the start of the next line. */
				jvm->conx = 0;
				jvm->cony++;
				
				/* Too much text on the screen? Move it up! */
				if (jvm->cony >= jvm->conh)
				{
					/* Move framebuffer up. */
					memmove(
						SJME_POINTER_OFFSET_LONG(jvm->fbinfo->pixels, 0),
						SJME_POINTER_OFFSET_LONG(jvm->fbinfo->pixels,
							sjme_font.pixelheight *
							(jvm->fbinfo->scanlen * SJME_JINT_C(4))),
						(jvm->fbinfo->height - sjme_font.pixelheight) *
							(jvm->fbinfo->scanlen * SJME_JINT_C(4)));
					
					/* Wipe bytes at the bottom. */
					memset(
						SJME_POINTER_OFFSET_LONG(jvm->fbinfo->pixels,
							(jvm->fbinfo->height - sjme_font.pixelheight) *
								(jvm->fbinfo->scanlen * SJME_JINT_C(4))),
						0,
						sjme_font.pixelheight * (jvm->fbinfo->scanlen *
							SJME_JINT_C(4)));
					
					/* Move the cursor up one line. */
					jvm->cony--;
				}
			}
		}
		
		/* Forward to pipe? */
		if (writefunc != NULL)
		{
			code = writefunc(b);
			if (code < 0)
				return (i == 0 ? code : i);
		}
	}
	
	/* Return written bytes. */
	return len;
}

/**
 * Handles system calls.
 *
 * @param jvm The JVM.
 * @param cpu The CPU.
 * @param error Error state.
 * @param callid The system call type.
 * @param args Arguments to the call.
 * @return The result of the call.
 * @since 2019/06/09
 */
sjme_jint sjme_syscall(sjme_jvm* jvm, sjme_cpu* cpu, sjme_jint* error,
	sjme_jshort callid, sjme_jint* args)
{
	sjme_jint* syserr;
	sjme_jint ia, ib, ic;
	sjme_jbyte ba;
	void* pa;
	
	/* Called wrong? */
	if (jvm == NULL || cpu == NULL || args == NULL)
	{
		if (error != NULL)
			*error = SJME_ERROR_INVALIDARG;
		
		return 0;
	}
	
	/* Calculate index to set for system call errors. */
	syserr = ((callid < 0 || callid >= SJME_SYSCALL_NUM_SYSCALLS) ?
		&cpu->syscallerr[SJME_SYSCALL_QUERY_INDEX] : &cpu->syscallerr[callid]);
	
	/* Depends on the system call. */
	switch (callid)
	{
			/* Query support for system call. */
		case SJME_SYSCALL_QUERY_INDEX:
			*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
			switch (args[0])
			{
				case SJME_SYSCALL_BYTE_ORDER_LITTLE:
				case SJME_SYSCALL_CALL_STACK_HEIGHT:
				case SJME_SYSCALL_CALL_STACK_ITEM:
				case SJME_SYSCALL_ERROR_GET:
				case SJME_SYSCALL_ERROR_SET:
				case SJME_SYSCALL_FRAMEBUFFER_PROPERTY:
				case SJME_SYSCALL_TIME_HI_MILLI_WALL:
				case SJME_SYSCALL_TIME_HI_NANO_MONO:
				case SJME_SYSCALL_TIME_LO_MILLI_WALL:
				case SJME_SYSCALL_TIME_LO_NANO_MONO:
				case SJME_SYSCALL_MEM_SET:
				case SJME_SYSCALL_MEM_SET_INT:
				case SJME_SYSCALL_PD_OF_STDERR:
				case SJME_SYSCALL_PD_OF_STDOUT:
				case SJME_SYSCALL_PD_WRITE_BYTE:
				case SJME_SYSCALL_SUPERVISOR_BOOT_OKAY:
					return SJME_JINT_C(1);
			}
			return SJME_JINT_C(0);
			
			/* Is the byte order little endian? */
		case SJME_SYSCALL_BYTE_ORDER_LITTLE:
			*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
#if defined(SJME_LITTLE_ENDIAN)
			return 1;
#else
			return 0;
#endif
			
			/* Height of the call stack. */
		case SJME_SYSCALL_CALL_STACK_HEIGHT:
			/* Count trace depth. */
			ia = 0;
			while (cpu != NULL)
			{
				/* Increase the count. */
				ia++;
				
				/* Go to deeper depth. */
				cpu = cpu->parent;
			}
			
			/* Does not generate errors. */
			*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
			return ia;
			
			/* Item within the call stack. */
		case SJME_SYSCALL_CALL_STACK_ITEM:
			/* Find the CPU frame to use. */
			ia = args[0];
			while (ia > 0)
			{
				/* End of CPU? */
				if (cpu == NULL)
				{
					*syserr = SJME_SYSCALL_ERROR_VALUE_OUT_OF_RANGE;
					return 0;
				}
				
				/* Drop down. */
				cpu = cpu->parent;
				ia--;
			}
			
			/* Depends on the requested item.*/
			switch (args[1])
			{
					/* The class name. */
				case SJME_CALLSTACKITEM_CLASS_NAME:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
#if defined(SJME_VIRTUAL_MEM)
					return SJME_POINTER_TO_JINT(
						sjme_fakeptr(jvm, cpu->debugclassname));
#else
					return SJME_POINTER_TO_JINT(cpu->debugclassname);
#endif
					
					/* The method name. */
				case SJME_CALLSTACKITEM_METHOD_NAME:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
#if defined(SJME_VIRTUAL_MEM)
					return SJME_POINTER_TO_JINT(
						sjme_fakeptr(jvm, cpu->debugmethodname));
#else
					return SJME_POINTER_TO_JINT(cpu->debugmethodname);
#endif
					
					/* The method type. */
				case SJME_CALLSTACKITEM_METHOD_TYPE:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
#if defined(SJME_VIRTUAL_MEM)
					return SJME_POINTER_TO_JINT(
						sjme_fakeptr(jvm, cpu->debugmethodtype));
#else
					return SJME_POINTER_TO_JINT(cpu->debugmethodtype);
#endif
					
					/* Source line. */
				case SJME_CALLSTACKITEM_SOURCE_LINE:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
					return cpu->debugline;
					
					/* The PC address. */
				case SJME_CALLSTACKITEM_PC_ADDRESS:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
#if defined(SJME_VIRTUAL_MEM)
					return SJME_POINTER_TO_JINT(sjme_fakeptr(jvm, cpu->pc));
#else
					return SJME_POINTER_TO_JINT(cpu->pc);
#endif
					
					/* Java operation. */
				case SJME_CALLSTACKITEM_JAVA_OPERATION:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
					return cpu->debugjop;
					
					/* Java PC address. */
				case SJME_CALLSTACKITEM_JAVA_PC_ADDRESS:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
					return cpu->debugjpc;
					
					/* Unknown. */
				default:
					*syserr = SJME_SYSCALL_ERROR_VALUE_OUT_OF_RANGE;
					return 0;
			}
			return 0;
			
			/* Get error state. */
		case SJME_SYSCALL_ERROR_GET:
			*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
			
			ia = args[0];
			if (ia < 0 || ia >= SJME_SYSCALL_NUM_SYSCALLS)
				ia = SJME_SYSCALL_QUERY_INDEX;
			
			return cpu->syscallerr[ia];
			
			/* Set error state, return old one. */
		case SJME_SYSCALL_ERROR_SET:
			*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
			
			ia = args[0];
			if (ia < 0 || ia >= SJME_SYSCALL_NUM_SYSCALLS)
				ia = SJME_SYSCALL_QUERY_INDEX;
			
			ib = cpu->syscallerr[ia];
			cpu->syscallerr[ia] = args[1];
			return ib;
			
			/* Gets/sets property of the framebuffer. */
		case SJME_SYSCALL_FRAMEBUFFER_PROPERTY:
			/* No framebuffer is defined? */
			if (jvm->fbinfo == NULL)
			{
				*syserr = SJME_SYSCALL_ERROR_NO_FRAMEBUFFER;
				return 0;
			}
			
			/* Depends on the property. */
			switch (args[0])
			{
					/* Framebuffer address. */
				case SJME_FRAMEBUFFER_PROPERTY_ADDRESS:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
#if defined(SJME_VIRTUAL_MEM)
					return SJME_POINTER_TO_JINT(
						sjme_fakeptr(jvm, jvm->fbinfo->pixels));
#else
					return SJME_POINTER_TO_JINT(jvm->fbinfo->pixels);
#endif
					
					/* Width of the framebuffer. */
				case SJME_FRAMEBUFFER_PROPERTY_WIDTH:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
					return jvm->fbinfo->width;
					
					/* Height of the framebuffer. */
				case SJME_FRAMEBUFFER_PROPERTY_HEIGHT:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
					return jvm->fbinfo->height;
					
					/* Scanline length of the framebuffer. */
				case SJME_FRAMEBUFFER_PROPERTY_SCANLEN:
					*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
					return jvm->fbinfo->scanlen;
				
					/* Unknown property, but there is a framebuffer. */
				default:
					*syserr = SJME_SYSCALL_ERROR_VALUE_OUT_OF_RANGE;
					return 0;
			}
			return 0;
			
			/* Returns the high millisecond wall clock. */
		case SJME_SYSCALL_TIME_HI_MILLI_WALL:
			if (jvm->nativefuncs == NULL ||
				jvm->nativefuncs->millitime == NULL)
			{
				*syserr = SJME_SYSCALL_ERROR_UNSUPPORTED_SYSTEM_CALL;
				return SJME_JINT_C(0);
			}
			else
			{
				*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
				jvm->nativefuncs->millitime(&ia);
				return ia;
			}
			
			/* Returns the low nanosecond wall clock. */
		case SJME_SYSCALL_TIME_HI_NANO_MONO:
			if (jvm->nativefuncs == NULL ||
				jvm->nativefuncs->nanotime == NULL)
			{
				*syserr = SJME_SYSCALL_ERROR_UNSUPPORTED_SYSTEM_CALL;
				return SJME_JINT_C(0);
			}
			else
			{
				*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
				jvm->nativefuncs->nanotime(&ia);
				return ia;
			}
			
			/* Returns the low millisecond wall clock. */
		case SJME_SYSCALL_TIME_LO_MILLI_WALL:
			if (jvm->nativefuncs == NULL ||
				jvm->nativefuncs->millitime == NULL)
			{
				*syserr = SJME_SYSCALL_ERROR_UNSUPPORTED_SYSTEM_CALL;
				return SJME_JINT_C(0);
			}
			else
			{
				*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
				return jvm->nativefuncs->millitime(&ia);
			}
		
			/* Returns the low nanosecond monotonic clock. */
		case SJME_SYSCALL_TIME_LO_NANO_MONO:
			if (jvm->nativefuncs == NULL ||
				jvm->nativefuncs->nanotime == NULL)
			{
				*syserr = SJME_SYSCALL_ERROR_UNSUPPORTED_SYSTEM_CALL;
				return SJME_JINT_C(0);
			}
			else
			{
				*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
				return jvm->nativefuncs->nanotime(&ia);
			}
			
			/* Set memory to byte value. */
		case SJME_SYSCALL_MEM_SET:
			/* Get address to wipe. */
			pa = SJME_JINT_TO_POINTER(args[0]);
			
			/* The value to store. */
			ic = args[1] & SJME_JINT_C(0xFF);
			
			/* Wipe these values! */
			ib = args[2];
			for (ia = 0; ia < ib; ia++)
				sjme_memwritep(jvm, 1, &pa, ic);
			
			/* Is okay. */
			*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
			return 0;
		
			/* Set memory in integer values. */
		case SJME_SYSCALL_MEM_SET_INT:
			/* Get address to wipe. */
			pa = SJME_JINT_TO_POINTER(args[0]);
			
			/* The value to store, is full integer. */
			ic = args[1];
			
			/* Wipe these values! */
			ib = args[2];
			for (ia = 0; ia < ib; ia += 4)
				sjme_memwritep(jvm, 4, &pa, ic);
			
			/* Is okay. */
			*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
			return SJME_JINT_C(0);
			
			/* Pipe descriptor of standard error. */
		case SJME_SYSCALL_PD_OF_STDERR:
			*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
			return SJME_JINT_C(SJME_PIPE_FD_STDERR);
			
			/* Pipe descriptor of standard output. */
		case SJME_SYSCALL_PD_OF_STDOUT:
			*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
			return SJME_JINT_C(SJME_PIPE_FD_STDOUT);
			
			/* Write single byte to a stream. */
		case SJME_SYSCALL_PD_WRITE_BYTE:
			ia = SJME_JINT_C(-1);
			
			/* The byte to write. */
			ba = (sjme_jbyte)args[1];
			
			/* Depends on the pipe target. */
			switch (args[0])
			{
					/* Standard output. */
				case SJME_PIPE_FD_STDOUT:
					ia = sjme_console_pipewrite(jvm,
						(jvm->nativefuncs != NULL &&
						jvm->nativefuncs->stdout_write != NULL ?
						jvm->nativefuncs->stdout_write : NULL), &ba, 0, 1);
					break;
				
					/* Standard error. */
				case SJME_PIPE_FD_STDERR:
					ia = sjme_console_pipewrite(jvm,
						(jvm->nativefuncs != NULL &&
						jvm->nativefuncs->stdout_write != NULL ?
						jvm->nativefuncs->stderr_write : NULL), &ba, 0, 1);
					break;
					
					/* Unknown descriptor. */
				default:
					*syserr = SJME_SYSCALL_ERROR_PIPE_DESCRIPTOR_INVALID;
					return SJME_JINT_C(-1);
			}
			
			/* Write error? */
			if (ia < 0)
			{
				*syserr = SJME_SYSCALL_ERROR_PIPE_DESCRIPTOR_INVALID;
				return SJME_JINT_C(-1);
			}
			
			/* Success. */
			*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
			return SJME_JINT_C(1);
			
			/* The supervisor booted okay! */
		case SJME_SYSCALL_SUPERVISOR_BOOT_OKAY:
			jvm->supervisorokay = 1;
			*syserr = SJME_SYSCALL_ERROR_NO_ERROR;
			return SJME_JINT_C(0);
		
			/* Unknown or unsupported system call. */
		default:
			*syserr = SJME_SYSCALL_ERROR_UNSUPPORTED_SYSTEM_CALL;
			return SJME_JINT_C(0);
	}
}

/** Divides two numbers. */
sjme_jint_div sjme_div(sjme_jint anum, sjme_jint aden)
{
	/* From Wikipedia (http://en.wikipedia.org/wiki/Division_%28digital%29) */
	/* if D == 0 then throw DivisionByZeroException end*/
	/* Q := 0 # initialize quotient and remainder to Zero  */
	/* R := 0                                              */
	/* for i = n-1...0 do  # " where n is no of bits "     */
	/*   R := R << 1       # left-shift R by 1 bit         */
	/*   R(0) := N(i)      # set the least-significant bit */
	/*              # of R equal to bit i of the numerator */
	/*   if R >= D then                                    */
	/*     R = R - D                                       */
	/*     Q(i) := 1                                       */
	/*   end                                               */
	/* end                                                 */
	sjme_jint_div rv = {0, 0};
	struct
	{
		sjme_juint quot;
		sjme_juint rem;
	} interm = {0, 0};
	sjme_juint i;
	sjme_jbyte isneg;
	
	/* Disallow division by zero */
	if (aden == 0)
		return rv;
	
	/* Negative? */
	isneg = 0;
	if ((anum < 0 && aden >= 0) || (anum >= 0 && aden < 0))
		isneg |= 1;
	
	/* Force Positive */
	anum = (anum < 0 ? -anum : anum);
	aden = (aden < 0 ? -aden : aden);
	
	/* Perform Math */
	for (i = SJME_JUINT_C(31);; i--)
	{
		interm.rem <<= SJME_JUINT_C(1);
		interm.rem &= SJME_JUINT_C(0xFFFFFFFE);
		interm.rem |= (((sjme_juint)anum) >> i) & SJME_JUINT_C(1);
		
		if (interm.rem >= (sjme_juint)aden)
		{
			interm.rem -= (sjme_juint)aden;
			interm.quot |= (SJME_JUINT_C(1) << i);
		}
		
		if (i == 0)
			break;
	}
	
	/* Restore Integers */
	rv.quot = interm.quot;
	rv.rem = interm.rem;
	
	/* Make Negative */
	if (isneg & 1)
		rv.quot = -rv.quot;
	
	/* Return */
	return rv;
}

/**
 * Executes single CPU state.
 *
 * @param jvm JVM state.
 * @param cpu CPU state.
 * @param error Execution error.
 * @param cycles The number of cycles to execute, a negative value means
 * forever.
 * @return The number of remaining cycles.
 * @since 2019/06/08
 */
sjme_jint sjme_cpuexec(sjme_jvm* jvm, sjme_cpu* cpu, sjme_jint* error,
	sjme_jint cycles)
{
	sjme_jint op, enc;
	void* nextpc;
	void* tempp;
	sjme_jint* r;
	sjme_jint ia, ib, ic, id;
	sjme_cpu* oldcpu;
	
	/* Invalid argument? */
	if (jvm == NULL || cpu == NULL)
	{
		if (error != NULL)
			*error = SJME_ERROR_INVALIDARG;
		
		return cycles;
	}
	
	/* Quick register access. */
	r = cpu->r;
	
	/* Near-Infinite execution loop. */
	for (;;)
	{
		/* Check if we ran out of cycles. */
		if (cycles >= 0)
		{
			if (cycles == 0)
				break;
			if ((--cycles) <= 0)
				break;
		}
		
		/* Increase total instruction count. */
		jvm->totalinstructions++;
		
		/* The zero register always must be zero. */
		r[0] = 0;
		
		/* Seed next PC address. */
		nextpc = cpu->pc;
		
		/* Read operation and determine encoding. */
		op = (sjme_memjreadp(jvm, 1, &nextpc) & SJME_JINT_C(0xFF));
		enc = ((op >= SJME_ENC_SPECIAL_A) ? op : (op & SJME_ENC_MASK));
		
		/* Temporary debug. */
#if 0
		fprintf(stderr,
			"ti=%d pc=%p op=%X cl=%s mn=%s mt=%s ln=%d jo=%x ja=%d\n",
			jvm->totalinstructions,
			cpu->pc,
			(unsigned int)op,
			(cpu->debugclassname == NULL ? NULL :
				SJME_POINTER_OFFSET_LONG(cpu->debugclassname, 2)),
			(cpu->debugmethodname == NULL ? NULL :
				SJME_POINTER_OFFSET_LONG(cpu->debugmethodname, 2)),
			(cpu->debugmethodtype == NULL ? NULL :
				SJME_POINTER_OFFSET_LONG(cpu->debugmethodtype, 2)),
			(int)cpu->debugline,
			(unsigned int)cpu->debugjop,
			(int)cpu->debugjpc);
#endif
		
		/* Depends on the operation. */
		switch (enc)
		{
				/* Compare two register values. */
			case SJME_ENC_IF_ICMP:
				{
					/* Values to compare. */
					ia = r[sjme_opdecodeui(jvm, &nextpc)];
					ib = r[sjme_opdecodeui(jvm, &nextpc)];
					
					/* Target PC address. */
					ic = sjme_opdecodejmp(jvm, &nextpc);
					tempp = SJME_POINTER_OFFSET(cpu->pc, ic);
					
					/* Check depends. */
					ic = 0;
					switch (op & SJME_ENC_COMPARE_MASK)
					{
						case SJME_COMPARETYPE_EQUALS:
							if (ia == ib)
								ic = 1;
							break;
							
						case SJME_COMPARETYPE_NOT_EQUALS:
							if (ia != ib)
								ic = 1;
							break;
							
						case SJME_COMPARETYPE_LESS_THAN:
							if (ia < ib)
								ic = 1;
							break;
							
						case SJME_COMPARETYPE_LESS_THAN_OR_EQUALS:
							if (ia <= ib)
								ic = 1;
							break;
							
						case SJME_COMPARETYPE_GREATER_THAN:
							if (ia > ib)
								ic = 1;
							break;
							
						case SJME_COMPARETYPE_GREATER_THAN_OR_EQUALS:
							if (ia >= ib)
								ic = 1;
							break;
							
						case SJME_COMPARETYPE_TRUE:
							ic = 1;
							break;
							
						case SJME_COMPARETYPE_FALSE:
							ic = 0;
							break;
					}
					
					/* Branch success? */
					if (ic != 0)
						nextpc = tempp;
				}
				break;
				
				/* Math. */
			case SJME_ENC_MATH_REG_INT:
			case SJME_ENC_MATH_CONST_INT:
				{
					/* A Value. */
					ia = r[sjme_opdecodeui(jvm, &nextpc)];
					
					/* B value. */
					if (enc == SJME_ENC_MATH_CONST_INT)
						ib = sjme_opdecodejint(jvm, &nextpc);
					else
						ib = r[sjme_opdecodeui(jvm, &nextpc)];
					
					/* Perform the math. */
					switch (op & SJME_ENC_MATH_MASK)
					{
						case SJME_MATH_ADD:
							ic = ia + ib;
							break;
							
						case SJME_MATH_SUB:
							ic = ia - ib;
							break;
							
						case SJME_MATH_MUL:
							ic = ia * ib;
							break;
							
						case SJME_MATH_DIV:
							ic = sjme_div(ia, ib).quot;
							break;
							
						case SJME_MATH_REM:
							ic = sjme_div(ia, ib).rem;
							break;
							
						case SJME_MATH_NEG:
							ic = -ia;
							break;
							
						case SJME_MATH_SHL:
							/* Shift is truncated. */
							ib = (ib & SJME_JINT_C(0x1F));
							
							/* Shifting values off the type is undefined, */
							/* so only keep the part of the value which is */
							/* not shifted off! */
							if (ib == 0)
								ic = ia;
							else
								ic = ((ia & sjme_sh_lmask[ib]) << ib);
							break;
							
						case SJME_MATH_SHR:
						case SJME_MATH_USHR:
							/* Shift is truncated. */
							ib = (ib & SJME_JINT_C(0x1F));
							
							/* Shifting values off the type is undefined, */
							/* so only keep the part of the value which is */
							/* not shifted off! */
							if (ib == 0)
								ic = ia;
							else
								ic = (((ia & sjme_sh_umask[ib])) >> ib);
							
							/* Mask in or mask out the dragged sign bit. */
							if (((ia & SJME_JINT_C(0x80000000)) != 0) &&
								((op & SJME_ENC_MATH_MASK) == SJME_MATH_SHR))
								ic |= sjme_sh_umask[ib];
							else
								ic &= sjme_sh_lmask[31 - ib];
							break;
							
						case SJME_MATH_AND:
							ic = ia & ib;
							break;
							
						case SJME_MATH_OR:
							ic = ia | ib;
							break;
							
						case SJME_MATH_XOR:
							ic = ia ^ ib;
							break;
							
						case SJME_MATH_SIGNX8:
							if (ia & SJME_JINT_C(0x80))
								ic = (ia | SJME_JINT_C(0xFFFFFF00));
							else
								ic = (ia & SJME_JINT_C(0x000000FF));
							break;
							
						case SJME_MATH_SIGNX16:
							if (ia & SJME_JINT_C(0x8000))
								ic = (ia | SJME_JINT_C(0xFFFF0000));
							else
								ic = (ia & SJME_JINT_C(0x0000FFFF));
							break;
						
						case SJME_MATH_CMPL:
						case SJME_MATH_CMPG:
							ic = (ia < ib ? SJME_JINT_C(-1) :
								(ia > ib ? SJME_JINT_C(1) : SJME_JINT_C(0)));
							break;
					}
					
					/* Store result. */
					r[sjme_opdecodeui(jvm, &nextpc)] = ic;
				}
				break;
				
				/* Memory (native byte order). */
			case SJME_ENC_MEMORY_OFF_REG:
			case SJME_ENC_MEMORY_OFF_ICONST:
				{
					/* Destination/source register. */
					ic = sjme_opdecodeui(jvm, &nextpc);
					
					/* The address and offset to access. */
					ia = r[sjme_opdecodeui(jvm, &nextpc)];
					if (enc == SJME_ENC_MEMORY_OFF_ICONST)
						ib = sjme_opdecodejint(jvm, &nextpc);
					else
						ib = r[sjme_opdecodeui(jvm, &nextpc)];
					tempp = SJME_JINT_TO_POINTER(ia);
					
					/* Load value */
					if ((op & SJME_MEM_LOAD_MASK) != 0)
					{
						switch (op & SJME_MEM_DATATYPE_MASK)
						{
							case SJME_DATATYPE_BYTE:
								r[ic] = sjme_memread(jvm, 1, tempp, ib);
								break;
								
							case SJME_DATATYPE_CHARACTER:
								r[ic] = (sjme_memread(jvm, 2, tempp, ib) &
									SJME_JINT_C(0xFFFF));
								break;
								
							case SJME_DATATYPE_SHORT:
								r[ic] = sjme_memread(jvm, 2, tempp, ib);
								break;
								
							case SJME_DATATYPE_OBJECT:
							case SJME_DATATYPE_INTEGER:
							case SJME_DATATYPE_FLOAT:
							default:
								r[ic] = sjme_memread(jvm, 4, tempp, ib);
								break;
						}
					}
					
					/* Store value */
					else
					{
						switch (op & SJME_MEM_DATATYPE_MASK)
						{	
							case SJME_DATATYPE_BYTE:
								sjme_memwrite(jvm, 1, tempp, ib, r[ic]);
								break;
								
							case SJME_DATATYPE_CHARACTER:
							case SJME_DATATYPE_SHORT:
								sjme_memwrite(jvm, 2, tempp, ib, r[ic]);
								break;
								
							case SJME_DATATYPE_OBJECT:
							case SJME_DATATYPE_INTEGER:
							case SJME_DATATYPE_FLOAT:
							default:
								sjme_memwrite(jvm, 4, tempp, ib, r[ic]);
								break;
						}
					}
				}
				break;
				
				/* Memory (Java byte order). */
			case SJME_ENC_MEMORY_OFF_REG_JAVA:
			case SJME_ENC_MEMORY_OFF_ICONST_JAVA:
				{
					/* Destination/source register. */
					ic = sjme_opdecodeui(jvm, &nextpc);
					
					/* The address to access. */
					ia = r[sjme_opdecodeui(jvm, &nextpc)];
					if (enc == SJME_ENC_MEMORY_OFF_ICONST_JAVA)
						ib = sjme_opdecodejint(jvm, &nextpc);
					else
						ib = r[sjme_opdecodeui(jvm, &nextpc)];
					tempp = SJME_JINT_TO_POINTER(ia);
					
					/* Load value */
					if ((op & SJME_MEM_LOAD_MASK) != 0)
					{
						switch (op & SJME_MEM_DATATYPE_MASK)
						{
							case SJME_DATATYPE_BYTE:
								r[ic] = sjme_memjread(jvm, 1, tempp, ib);
								break;
								
							case SJME_DATATYPE_CHARACTER:
								r[ic] = (sjme_memjread(jvm, 2, tempp, ib) &
									SJME_JINT_C(0xFFFF));
								break;
								
							case SJME_DATATYPE_SHORT:
								r[ic] = sjme_memjread(jvm, 2, tempp, ib);
								break;
								
							case SJME_DATATYPE_OBJECT:
							case SJME_DATATYPE_INTEGER:
							case SJME_DATATYPE_FLOAT:
							default:
								r[ic] = sjme_memjread(jvm, 4, tempp, ib);
								break;
						}
					}
					
					/* Store value */
					else
					{
						switch (op & SJME_MEM_DATATYPE_MASK)
						{	
							case SJME_DATATYPE_BYTE:
								sjme_memjwrite(jvm, 1, tempp, ib, r[ic]);
								break;
								
							case SJME_DATATYPE_CHARACTER:
							case SJME_DATATYPE_SHORT:
								sjme_memjwrite(jvm, 2, tempp, ib, r[ic]);
								break;
								
							case SJME_DATATYPE_OBJECT:
							case SJME_DATATYPE_INTEGER:
							case SJME_DATATYPE_FLOAT:
							default:
								sjme_memjwrite(jvm, 4, tempp, ib, r[ic]);
								break;
						}
					}
				}
				break;
				
				/* Atomic decrement and get. */
			case SJME_OP_ATOMIC_INT_DECREMENT_AND_GET:
				{
					/* Target register. */
					id = sjme_opdecodeui(jvm, &nextpc);
					
					/* Load address and offset. */
					ia = r[sjme_opdecodeui(jvm, &nextpc)];
					ib = sjme_opdecodeui(jvm, &nextpc);
					
					/* Read value here. */
					ic = sjme_memread(jvm, 4, SJME_JINT_TO_POINTER(ia), ib);
					
					/* Decrement value. */
					ic = ic - 1;
					
					/* Store value. */
					sjme_memwrite(jvm, 4, SJME_JINT_TO_POINTER(ia), ib, ic);
					
					/* Set destination value. */
					r[id] = ic;
				}
				break;
				
				/* Atomic increment. */
			case SJME_OP_ATOMIC_INT_INCREMENT:
				{
					/* Load address and offset. */
					ia = r[sjme_opdecodeui(jvm, &nextpc)];
					ib = sjme_opdecodeui(jvm, &nextpc);
					
					/* Read value here. */
					ic = sjme_memread(jvm, 4, SJME_JINT_TO_POINTER(ia), ib);
					
					/* And write incremented value. */
					sjme_memwrite(jvm, 4, SJME_JINT_TO_POINTER(ia), ib,
						ic + 1);
				}
				break;
				
				/* Breakpoint. */
			case SJME_OP_BREAKPOINT:
				if (error != NULL)
					*error = SJME_ERROR_CPUBREAKPOINT;
				
				return cycles;
			
				/* Copy value. */
			case SJME_OP_COPY:
				{
					ia = sjme_opdecodeui(jvm, &nextpc);
					ib = sjme_opdecodeui(jvm, &nextpc);
					
					r[ib] = r[ia];
				}
				break;
			
				/* Debug entry. */
			case SJME_OP_DEBUG_ENTRY:
				{
					tempp = SJME_JINT_TO_POINTER(r[SJME_POOL_REGISTER]);
					
					/* Get pointers to the real values. */
					ia = sjme_memread(jvm, 4, tempp,
						sjme_opdecodeui(jvm, &nextpc) * SJME_JINT_C(4));
					ib = sjme_memread(jvm, 4, tempp,
						sjme_opdecodeui(jvm, &nextpc) * SJME_JINT_C(4));
					ic = sjme_memread(jvm, 4, tempp,
						sjme_opdecodeui(jvm, &nextpc) * SJME_JINT_C(4));
					
#if defined(SJME_VIRTUAL_MEM)
					/* Map to real memory addresses. */
					cpu->debugclassname = sjme_realptr(jvm,
						SJME_JINT_TO_POINTER(ia));
					cpu->debugmethodname = sjme_realptr(jvm,
						SJME_JINT_TO_POINTER(ib));
					cpu->debugmethodtype = sjme_realptr(jvm,
						SJME_JINT_TO_POINTER(ic));
#else
					/* Just forward the values. */
					cpu->debugclassname = SJME_JINT_TO_POINTER(ia);
					cpu->debugmethodname = SJME_JINT_TO_POINTER(ib);
					cpu->debugmethodtype = SJME_JINT_TO_POINTER(ic);
#endif
				}
				break;
				
				/* Exit method. */
			case SJME_OP_DEBUG_EXIT:
				break;
				
				/* Debug point. */
			case SJME_OP_DEBUG_POINT:
				{
					cpu->debugline = sjme_opdecodeui(jvm, &nextpc);
					cpu->debugjop = (sjme_opdecodeui(jvm, &nextpc) &
						SJME_JINT_C(0xFF));
					cpu->debugjpc = sjme_opdecodeui(jvm, &nextpc);
				}
				break;
				
				/* If equal to constant? */
			case SJME_OP_IFEQ_CONST:
				{
					/* A value. */
					ia = r[sjme_opdecodeui(jvm, &nextpc)];
					
					/* B value. */
					ib = sjme_opdecodejint(jvm, &nextpc);
					
					/* Target PC address. */
					ic = sjme_opdecodejmp(jvm, &nextpc);
					tempp = SJME_POINTER_OFFSET(cpu->pc, ic);
					
					/* Jump on equals? */
					if (ia == ib)
						nextpc = tempp;
				}
				break;
				
				/* Invoke method. */
			case SJME_OP_INVOKE:
				{
					/* Allocate to store old CPU state. */
					oldcpu = sjme_malloc(sizeof(*oldcpu));
					if (oldcpu == NULL)
					{
						if (error != NULL)
							*error = SJME_ERROR_NOMEMORY;
						
						return cycles;
					}
					
					/* Copy and store state. */
					*oldcpu = *cpu;
					cpu->parent = oldcpu;
					
					/* Setup CPU state for invoke run, move pool up. */
					for (ia = SJME_LOCAL_REGISTER_BASE;
						ia < SJME_MAX_REGISTERS; ia++)
						r[ia] = 0;
					r[SJME_POOL_REGISTER] =
						oldcpu->r[SJME_NEXT_POOL_REGISTER];
					r[SJME_NEXT_POOL_REGISTER] = 0;
					
					/* The address to execute. */
					ia = oldcpu->r[sjme_opdecodeui(jvm, &nextpc)];
					
					/* Load in register list (wide). */
					ib = sjme_memjreadp(jvm, 1, &nextpc);
					if ((ib & SJME_JINT_C(0x80)) != 0)
					{
						/* Read lower values. */
						ib &= SJME_JINT_C(0x7F);
						ib <<= 8;
						ib |= (sjme_memjreadp(jvm, 1, &nextpc) &
							SJME_JINT_C(0xFF));
						
						/* Read values. */
						for (ic = 0; ic < ib; ic++)
							r[SJME_ARGBASE_REGISTER + ic] =
								oldcpu->r[sjme_opdecodejshort(jvm, &nextpc)];
					}
					
					/* Narrow format list. */
					else
					{
						/* Read values. */
						for (ic = 0; ic < ib; ic++)
							r[SJME_ARGBASE_REGISTER + ic] =
								oldcpu->r[sjme_memjreadp(jvm, 1, &nextpc)];
					}
					
#if 0
					fprintf(stderr, "Invoke: %08lx\n", (long)ia);
					for (ic = 0; ic < ib; ic++)
						fprintf(stderr, "A%d: %d\n",
							(int)ic, (int)r[SJME_ARGBASE_REGISTER + ic]);
#endif
					
					/* Old PC address resumes where this read ended. */
					oldcpu->pc = nextpc;
					
					/* Our next PC becomes the target address. */
					nextpc = SJME_JINT_TO_POINTER(ia);
					cpu->pc = nextpc;
				}
				break;
				
				/* Load value from integer array. */
			case SJME_OP_LOAD_FROM_INTARRAY:
				{
					/* Destination register. */
					ic = sjme_opdecodeui(jvm, &nextpc);
					
					/* Address and index */
					ia = r[sjme_opdecodeui(jvm, &nextpc)];
					ib = r[sjme_opdecodeui(jvm, &nextpc)];
					
					/* Load from array. */
					r[ic] = sjme_memread(jvm, 4, SJME_JINT_TO_POINTER(ia),
						SJME_ARRAY_BASE_SIZE + (ib * SJME_JINT_C(4)));
				}
				break;
				
				/* Load value from constant pool. */
			case SJME_OP_LOAD_POOL:
				{
					/* The index to read from. */
					ia = sjme_opdecodeui(jvm, &nextpc);
					
					/* Write into destination register. */
					r[sjme_opdecodeui(jvm, &nextpc)] = 
						sjme_memread(jvm, 4,
							SJME_JINT_TO_POINTER(r[SJME_POOL_REGISTER]),
							ia * SJME_JINT_C(4));
				}
				break;
				
				/* Return from method. */
			case SJME_OP_RETURN:
				{
					/* Get parent CPU state. */
					oldcpu = cpu->parent;
					
					/* Copy global values back. */
					for (ia = 0; ia < SJME_LOCAL_REGISTER_BASE; ia++)
						oldcpu->r[ia] = cpu->r[ia];
					
					/* Copy system call errors back. */
					for (ia = 0; ia < SJME_SYSCALL_NUM_SYSCALLS; ia++)
						oldcpu->syscallerr[ia] = cpu->syscallerr[ia];
					
					/* Completely restore the old state. */
					*cpu = *oldcpu;
					
					/* Restore continuing PC address. */
					nextpc = cpu->pc;
					
					/* Free the parent as it is not needed. */
					sjme_free(oldcpu);
				}
				break;
				
				/* System call. */
			case SJME_OP_SYSTEM_CALL:
				{
					/* Clear system call arguments. */
					for (ia = 0; ia < SJME_MAX_SYSCALLARGS; ia++)
						cpu->syscallargs[ia] = 0;
					
					/* Load call type. */
					ia = r[sjme_opdecodeui(jvm, &nextpc)];
					
					/* Load call arguments. */
					ic = sjme_opdecodeui(jvm, &nextpc);
					for (ib = 0; ib < ic; ib++)
					{
						/* Get value. */
						id = r[sjme_opdecodeui(jvm, &nextpc)];
						
						/* Set but never exceed the system call limit. */
						if (ib < SJME_MAX_SYSCALLARGS)
							cpu->syscallargs[ib] = id;
					}
					
					/* Call it and place result into the return register. */
					r[SJME_RETURN_REGISTER] = sjme_syscall(jvm, cpu, error,
						ia, cpu->syscallargs);
					
					/* Stop if an error was set. */
					if (error != NULL && *error != SJME_ERROR_NONE)
						return cycles;
				}
				break;
			
				/* Invalid operation. */
			default:
				if (error != NULL)
					*error = (SJME_ERROR_INVALIDOP + op);
				
				return cycles;
		}
		
		/* Set next PC address. */
		cpu->pc = nextpc;
	}
	
	/* Return remaining cycles. */
	return cycles;
}

/** Executes code running within the JVM. */
sjme_jint sjme_jvmexec(sjme_jvm* jvm, sjme_jint* error, sjme_jint cycles)
{
	sjme_jint threadid;
	sjme_cpu* cpu;
	sjme_jint xerror;
	
	/* Fallback error state. */
	if (error == NULL)
		error = &xerror;
	
	/* Clear error always. */
	if (error != NULL)
		*error = SJME_ERROR_NONE;
	
	/* Do nothing. */
	if (jvm == NULL)
	{
		if (error != NULL)
			*error = SJME_ERROR_INVALIDARG;
		
		return 0;
	}
	
	/* Run cooperatively threaded style CPU. */
	for (threadid = jvm->fairthreadid;;
		threadid = ((threadid + 1) & SJME_THREAD_MASK))
	{
		/* Have we used all our execution cycles? */
		if (cycles >= 0)
		{
			if (cycles == 0)
				break;
			if ((--cycles) <= 0)
				break;
		}
		
		/* Ignore CPUs which are not turned on. */
		cpu = &jvm->threads[threadid];
		if (cpu->state == SJME_THREAD_STATE_NONE)
			continue;
		
		/* Execute CPU engine. */
		cycles = sjme_cpuexec(jvm, cpu, error, cycles);
		
		/* CPU fault, stop! */
		if (error != NULL)
		{
			if (*error != SJME_ERROR_NONE)
				break;
		}
	}
	
	/* Start next run on the CPU that was last executing. */
	jvm->fairthreadid = (threadid & SJME_THREAD_MASK);
	
	/* Returning remaining number of cycles. */
	return cycles;
}

/**
 * Attempts to load a built-in ROM file.
 *
 * @param nativefuncs Native functions.
 * @param outromsize Output ROM size.
 * @param error Error flag.
 * @return The loaded ROM data or {@code NULL} if no ROM was loaded.
 * @since 2019/06/07
 */
void* sjme_loadrom(sjme_nativefuncs* nativefuncs, sjme_jint* outromsize,
	sjme_jint* error)
{
	void* rv;
	sjme_nativefilename* fn;
	sjme_nativefile* file;
	sjme_jint romsize, xerror, readat, readcount;
	
	/* Need native functions. */
	if (nativefuncs == NULL || nativefuncs->nativeromfile == NULL ||
		nativefuncs->fileopen == NULL || nativefuncs->filesize == NULL ||
		nativefuncs->fileread == NULL)
	{
		if (error != NULL)
			*error = SJME_ERROR_NOFILES;
		
		return NULL;
	}
	
	/* Load file name used for the native ROM. */
	fn = nativefuncs->nativeromfile();
	if (fn == NULL)
	{
		if (error != NULL)
			*error = SJME_ERROR_NONATIVEROM;
		
		return NULL;
	}
	
	/* Set to nothing. */
	rv = NULL;
	
	/* Open ROM. */
	file = nativefuncs->fileopen(fn, SJME_OPENMODE_READ, error);
	if (file != NULL)
	{
		/* Need ROM size. */
		romsize = nativefuncs->filesize(file, error);
		
		/* Allocate ROM into memory. */
		rv = sjme_malloc(romsize);
		if (rv != NULL)
		{
			/* Read whatever is possible. */
			for (readat = 0; readat < romsize;)
			{
				/* Read into raw memory. */
				readcount = nativefuncs->fileread(file,
					SJME_POINTER_OFFSET_LONG(rv, readat), romsize - readat,
					&xerror);
				
				/* EOF or error? */
				if (readcount < 0)
				{
					/* End of file reached? */
					if (xerror == SJME_ERROR_ENDOFFILE)
					{
						/* Reached early EOF?? */
						if (readat < romsize)
						{
							if (error != NULL)
								*error = SJME_ERROR_EARLYEOF;
							
							/* Failed */
							sjme_free(rv);
							return NULL;
						}
						
						break;
					}
					
					/* Otherwise fail */
					else
					{
						/* Force error to be set. */
						if (xerror == SJME_ERROR_NONE)
							xerror = SJME_ERROR_READERROR;
						
						/* Copy error over. */
						*error = xerror;
						
						/* Free resources. */
						sjme_free(rv);
						rv = NULL;
						break;
					}
				}
				
				/* Read count goes up. */
				readat += readcount;
			}
		}
		
		/* Just set error. */
		else
		{
			if (error != NULL)
				*error = SJME_ERROR_NOMEMORY;
		}
		
		/* Close when done. */
		if (nativefuncs->fileclose != NULL)
			nativefuncs->fileclose(file, NULL);
	}
	
	/* Free file name when done. */
	if (nativefuncs->freefilename != NULL)
		nativefuncs->freefilename(fn);
	
	/* Output ROM size? */
	if (outromsize != NULL)
		*outromsize = romsize;
	
	/* Whatever value was used, if possible */
	return rv;
}

/**
 * Initializes the BootRAM, loading it from ROM.
 *
 * @param rom The ROM.
 * @param ram The RAM.
 * @param ramsize The size of RAM.
 * @param jvm The Java VM to initialize.
 * @param error Error flag.
 * @return Non-zero on success.
 * @since 2019/06/07
 */
sjme_jint sjme_initboot(void* rom, void* ram, sjme_jint ramsize, sjme_jvm* jvm,
	sjme_jint* error)
{
	void* rp;
	void* bootjar;
	void* byteram;
	sjme_jint bootoff, i, n, seedop, seedaddr, seedvalh, seedvall, seedsize;
	sjme_jint bootjaroff, vbootjarbase, vrambase, vrombase, vconfigbase;
	sjme_cpu* cpu;
	
	/* Invalid arguments. */
	if (rom == NULL || ram == NULL || ramsize <= 0 || jvm == NULL)
		return 0;
	
	/* Determine the address the VM sees for some memory types. */
#if defined(SJME_VIRTUAL_MEM)
	vrambase = jvm->rambase;
	vrombase = jvm->rombase;
	vconfigbase = jvm->configbase;
#else
	vrambase = SJME_POINTER_TO_JINT(jvm->ram);
	vrombase = SJME_POINTER_TO_JINT(jvm->rom);
	vconfigbase = SJME_POINTER_TO_JINT(jvm->config);
#endif
	
	/* Set initial CPU (the first). */
	cpu = &jvm->threads[0];
	cpu->state = SJME_THREAD_STATE_RUNNING;
	
	/* Set boot pointer to start of ROM. */
	rp = rom;
	
	/* Check ROM magic number. */
	if (sjme_memjreadp(jvm, 4, &rp) != SJME_ROM_MAGIC_NUMBER)
	{
		if (error != NULL)
			*error = SJME_ERROR_INVALIDROMMAGIC;
		
		return 0;
	}
	
	/* Ignore JAR count and BootJAR index. */
	sjme_memjreadp(jvm, 4, &rp);
	sjme_memjreadp(jvm, 4, &rp);
	
	/* Read and calculate BootJAR position. */
	bootjaroff = sjme_memjreadp(jvm, 4, &rp);
	vbootjarbase = vrombase + bootjaroff;
	rp = bootjar = SJME_POINTER_OFFSET_LONG(rom, bootjaroff);
	
	/* Check JAR magic number. */
	if (sjme_memjreadp(jvm, 4, &rp) != SJME_JAR_MAGIC_NUMBER)
	{
		if (error != NULL)
			*error = SJME_ERROR_INVALIDJARMAGIC;
		
		return 0;
	}
	
	/* Ignore numrc, tocoffset, manifestoff, manifestlen. */
	sjme_memjreadp(jvm, 4, &rp);
	sjme_memjreadp(jvm, 4, &rp);
	sjme_memjreadp(jvm, 4, &rp);
	sjme_memjreadp(jvm, 4, &rp);
	
	/* Read boot offset for later. */
	bootoff = sjme_memjreadp(jvm, 4, &rp);
	
	/* Ignore bootsize. */
	sjme_memjreadp(jvm, 4, &rp);
	
	/* Seed initial CPU state. */
	cpu->r[SJME_POOL_REGISTER] =
		vrambase + sjme_memjreadp(jvm, 4, &rp);
	cpu->r[SJME_STATIC_FIELD_REGISTER] =
		vrambase + sjme_memjreadp(jvm, 4, &rp);
	cpu->pc = SJME_JINT_TO_POINTER(vbootjarbase + sjme_memjreadp(jvm, 4, &rp));
	
	/* Bootstrap entry arguments. */
	/* (int __rambase, int __ramsize, int __rombase, int __romsize, */
	/* int __confbase, int __confsize) */
	cpu->r[SJME_ARGBASE_REGISTER + 0] = vrambase;
	cpu->r[SJME_ARGBASE_REGISTER + 1] = ramsize;
	cpu->r[SJME_ARGBASE_REGISTER + 2] = vrombase;
	cpu->r[SJME_ARGBASE_REGISTER + 3] = jvm->romsize;
	cpu->r[SJME_ARGBASE_REGISTER + 4] = vconfigbase;
	cpu->r[SJME_ARGBASE_REGISTER + 5] = jvm->configsize;
	
	/* Address where the BootRAM is read from. */
	rp = SJME_POINTER_OFFSET_LONG(bootjar, bootoff);
	
	/* Copy initial base memory bytes, which is pure big endian. */
	byteram = SJME_JINT_TO_POINTER(vrambase);
	n = sjme_memjreadp(jvm, 4, &rp);
	for (i = 0; i < n; i++)
		sjme_memjwritep(jvm, 1, &byteram, sjme_memjreadp(jvm, 1, &rp));
	
	/* Load all seeds, which restores natural byte order. */
	n = sjme_memjreadp(jvm, 4, &rp);
	for (i = 0; i < n; i++)
	{
		/* Read seed information. */
		seedop = sjme_memjreadp(jvm, 1, &rp);
		seedsize = (seedop >> SJME_JINT_C(4)) & SJME_JINT_C(0xF);
		seedop = (seedop & SJME_JINT_C(0xF));
		seedaddr = sjme_memjreadp(jvm, 4, &rp);
		
		/* Wide value. */
		if (seedsize == 8)
		{
			seedvalh = sjme_memjreadp(jvm, 4, &rp);
			seedvall = sjme_memjreadp(jvm, 4, &rp);
		}
		
		/* Narrow value. */
		else
			seedvalh = sjme_memjreadp(jvm, seedsize, &rp);
		
		/* Make sure the seed types are correct. */
		if ((seedsize != 1 && seedsize != 2 &&
			seedsize != 4 && seedsize != 8) || 
			(seedop != 0 && seedop != 1 && seedop != 2) ||
			(seedsize == 8 && seedop != 0))
		{
			if (error != NULL)
				*error = SJME_ERROR_INVALIDBOOTRAMSEED;
			
			return 0;
		}
		
		/* Offset value if it is in RAM or JAR ROM. */
		if (seedop == 1)
			seedvalh += vrambase;
		else if (seedop == 2)
			seedvalh += vbootjarbase;
		
		/* Write long value. */
		if (seedsize == 8)
		{
#if defined(SJME_BIG_ENDIAN)
			sjme_memwrite(jvm, 4, ram, seedaddr, seedvalh);
			sjme_memwrite(jvm, 4, ram, seedaddr + SJME_JINT_C(4), seedvall);
#else
			sjme_memwrite(jvm, 4, ram, seedaddr, seedvall);
			sjme_memwrite(jvm, 4, ram, seedaddr + SJME_JINT_C(4), seedvalh);
#endif
		}
		
		/* Write narrow value. */
		else
			sjme_memwrite(jvm, seedsize, ram, seedaddr, seedvalh);
	}
	
	/* Check end value. */
	if (sjme_memjreadp(jvm, 4, &rp) != (~SJME_JINT_C(0)))
	{
		if (error != NULL)
			*error = SJME_ERROR_INVALIDBOOTRAMEND;
		
		return 0;
	}
	
	/* Okay! */
	return 1;
}

/** Destroys the virtual machine instance. */
sjme_jint sjme_jvmdestroy(sjme_jvm* jvm, sjme_jint* error)
{
	sjme_cpu* cpu;
	sjme_cpu* oldcpu;
	sjme_jint i;
	
	/* Missing this? */
	if (jvm == NULL)
	{
		if (error != NULL)
			*error = SJME_ERROR_INVALIDARG;
		
		return 0;
	}
	
	/* Reset error. */
	if (error != NULL)
		*error = SJME_ERROR_NONE;
	
	/* Go through and cleanup CPUs. */
	for (i = 0; i < SJME_THREAD_MAX; i++)
	{
		/* Get CPU here. */
		cpu = &jvm->threads[i];
		
		/* Recursively clear CPU stacks. */
		while (cpu->parent != NULL)
		{
			/* Keep for later free. */
			oldcpu = cpu->parent;
			
			/* Copy down. */
			*cpu = *oldcpu;
			
			/* Free CPU state. */
			sjme_free(oldcpu);
		}
	}
	
	/* Delete major JVM data areas. */
	sjme_free(jvm->ram);
	sjme_free(jvm->config);
	if (jvm->presetrom == NULL)
		sjme_free(jvm->rom);
	
	/* Destroyed okay. */
	return 1;
}

/** Initializes the configuration space. */
void sjme_configinit(void* conf, sjme_jint confsize, sjme_jvm* jvm,
	sjme_jvmoptions* options, sjme_nativefuncs* nativefuncs, sjme_jint* error)
{
#define SJME_CONFIG_FORMAT_INTEGER SJME_JINT_C(1)
#define SJME_CONFIG_FORMAT_KEYVALUE SJME_JINT_C(2)
#define SJME_CONFIG_FORMAT_STRING SJME_JINT_C(3)
#define SJME_CONFIG_FORMAT_STRINGS SJME_JINT_C(4)
	void* wp;
	void* basep;
	void* sizep;
	sjme_jint opt, format, iv, it, wlen;
	char* sa;
	char* sb;
	
	/* Write pointer starts at the base area. */
	wp = conf;
	
	/* Go through all possible options to make a value. */
	for (opt = SJME_JINT_C(1); opt < SJME_CONFIG_NUM_OPTIONS; opt++)
	{
		/* Used to specify the format. */
		format = 0;
		
		/* Reset. */
		sa = NULL;
		sb = NULL;
		iv = 0;
		
		/* Depends on the option. */
		switch (opt)
		{
			/* Java VM Version. */
			case SJME_CONFIG_JAVA_VM_VERSION:
				format = SJME_CONFIG_FORMAT_STRING;
				sa = "0.3.0";
				break;
			
			/* Java VM Name. */
			case SJME_CONFIG_JAVA_VM_NAME:
				format = SJME_CONFIG_FORMAT_STRING;
				sa = "SquirrelJME RatufaCoat";
				break;
			
			/* Java VM Vendor. */
			case SJME_CONFIG_JAVA_VM_VENDOR:
				format = SJME_CONFIG_FORMAT_STRING;
				sa = "Stephanie Gawroriski";
				break;
			
			/* Java VM E-Mail. */
			case SJME_CONFIG_JAVA_VM_EMAIL:
				format = SJME_CONFIG_FORMAT_STRING;
				sa = "xerthesquirrel@gmail.com";
				break;
			
			/* Java VM URL. */
			case SJME_CONFIG_JAVA_VM_URL:
				format = SJME_CONFIG_FORMAT_STRING;
				sa = "https://squirreljme.cc/";
				break;
			
			/* The guest depth. */
			case SJME_CONFIG_GUEST_DEPTH:
				break;
			
			/* Main class. */
			case SJME_CONFIG_MAIN_CLASS:
				break;
			
			/* Main program arguments. */
			case SJME_CONFIG_MAIN_ARGUMENTS:
				break;
			
			/* Is this a MIDlet? */
			case SJME_CONFIG_IS_MIDLET:
				break;
			
			/* Define system propertly. */
			case SJME_CONFIG_DEFINE_PROPERTY:
				break;
			
			/* Classpath to use. */
			case SJME_CONFIG_CLASS_PATH:
				break;
			
				/* Unknown, ignore. */
			default:
				continue;
		}
		
		/* No known way to write this? */
		if (format == 0)
			continue;
		
		/* Write option key. */
		sjme_memjwritep(jvm, 2, &wp, opt);
		
		/* Store size location for later write. */
		sizep = wp;
		sjme_memjwritep(jvm, 2, &wp, 0);
		
		/* Base write pointer. */
		basep = wp;
		
		/* Depends on the format. */
		switch (format)
		{
				/* Integer. */
			case SJME_CONFIG_FORMAT_INTEGER:
				sjme_memjwritep(jvm, 4, &wp, iv);
				break;
			
				/* Key/value pair. */
			case SJME_CONFIG_FORMAT_KEYVALUE:
				break;
			
				/* String value. */
			case SJME_CONFIG_FORMAT_STRING:
				{
					// Record string length
					int iv = strlen(sa);
					sjme_memjwritep(jvm, 2, &wp, iv);
					
					// Record characters
					for (it = 0; it < iv; it++)
						sjme_memjwritep(jvm, 1, &wp, (sjme_jint)sa[it]);
				}
				break;
			
				/* Multiple strings. */
			case SJME_CONFIG_FORMAT_STRINGS:
				break;
		}
		
		/* Determine length and round it to 4 bytes. */
		wlen = SJME_POINTER_TO_JINT(wp) - SJME_POINTER_TO_JINT(basep);
		while ((wlen & SJME_JINT_C(3)) != 0)
		{
			/* Write padding. */
			sjme_memjwritep(jvm, 1, &wp, 0);
			wlen++;
		}
		
		/* Write to the actual size! */
		sjme_memjwrite(jvm, 2, sizep, 0, wlen);
	}
	
	/* Write end of config. */
	sjme_memjwritep(jvm, 2, &wp, SJME_CONFIG_END);
	
#undef SJME_CONFIG_FORMAT_INTEGER
#undef SJME_CONFIG_FORMAT_KEYVALUE
#undef SJME_CONFIG_FORMAT_STRING
#undef SJME_CONFIG_FORMAT_STRINGS
}

/** Creates a new instance of the JVM. */
sjme_jvm* sjme_jvmnew(sjme_jvmoptions* options, sjme_nativefuncs* nativefuncs,
	sjme_jint* error)
{
	sjme_jvmoptions nulloptions;
	void* ram;
	void* rom;
	void* conf;
	sjme_jvm* rv;
	sjme_jint i, l, romsize;
	sjme_framebuffer* fbinfo;
#if defined(SJME_VIRTUAL_MEM)
	sjme_jint vmem = SJME_VIRTUAL_MEM_BASE;
#endif
	
	/* We need native functions. */
	if (nativefuncs == NULL)
		return NULL;
	
	/* Allocate VM state. */
	rv = sjme_malloc(sizeof(*rv));
	conf = sjme_malloc(SJME_DEFAULT_CONF_SIZE);
	if (rv == NULL || conf == NULL)
	{
		if (error != NULL)
			*error = SJME_ERROR_NOMEMORY;
		
		sjme_free(rv);
		sjme_free(conf);
		
		return NULL;
	}

#if defined(SJME_VIRTUAL_MEM)
	/* Virtual map config. */
	rv->configbase = vmem;
	vmem = (vmem + SJME_DEFAULT_CONF_SIZE + SJME_VIRTUAL_MEM_MASK) &
		(~SJME_VIRTUAL_MEM_MASK);
#endif
	
	/* If there were no options specified, just use a null set. */
	if (options == NULL)
	{
		memset(&nulloptions, 0, sizeof(nulloptions));
		options = &nulloptions;
	}
	
	/* If no RAM size was specified then use the default. */
	if (options->ramsize <= 0)
		options->ramsize = SJME_DEFAULT_RAM_SIZE;
	
	/* Allocate RAM. */
	ram = sjme_malloc(options->ramsize);
	if (ram == NULL)
	{
		if (error != NULL)
			*error = SJME_ERROR_NOMEMORY;
			
		sjme_free(rv);
		sjme_free(conf);
		
		return NULL;
	}
	
#if defined(SJME_VIRTUAL_MEM)
	/* Virtual map RAM. */
	rv->rambase = vmem;
	vmem = (vmem + options->ramsize + SJME_VIRTUAL_MEM_MASK) &
		(~SJME_VIRTUAL_MEM_MASK);
#endif
	
	/* Needed by the VM. */
	rv->ram = ram;
	rv->ramsize = options->ramsize;
	rv->nativefuncs = nativefuncs;
	
	/* Initialize the framebuffer info, if available. */
	fbinfo = NULL;
	if (nativefuncs->framebuffer != NULL)
		fbinfo = nativefuncs->framebuffer();
	
	/* Initialize framebuffer, done a bit early to show errors. */
	rv->fbinfo = fbinfo;
	if (fbinfo != NULL)
	{
		/* Framebuffer size, needed by vptrs. */
		rv->fbsize = fbinfo->numpixels * SJME_JINT_C(4);
		
		/* Console positions and size. */
		rv->conx = 0;
		rv->cony = 0;
		rv->conw = fbinfo->width / sjme_font.charwidths[0];
		rv->conh = fbinfo->height / sjme_font.pixelheight;
	}
	
#if defined(SJME_VIRTUAL_MEM)
	/* Virtual map framebuffer, if available. */
	if (fbinfo != NULL)
	{
		rv->fbbase = vmem;
		vmem = (vmem + rv->fbsize + SJME_VIRTUAL_MEM_MASK) &
			(~SJME_VIRTUAL_MEM_MASK);
	}
#endif
	
	/* Needed by destruction later. */
	rv->presetrom = options->presetrom;
	
	/* Load the ROM? */
	rom = options->presetrom;
	if (rom == NULL)
	{
		/* Call sub-routine which can load the ROM. */
		rom = sjme_loadrom(nativefuncs, &romsize, error);
		
		/* Could not load the ROM? */
		if (rom == NULL)
		{
			/* Write the ROM failure message! */
			sjme_console_pipewrite(rv, (nativefuncs != NULL ?
				nativefuncs->stderr_write : NULL), sjme_romfailmessage, 0,
				sizeof(sjme_romfailmessage) / sizeof(sjme_jbyte));
			
			/* Clear resources */
			sjme_free(rv);
			sjme_free(ram);
			sjme_free(conf);
			
			/* Fail */
			return NULL;
		}
	}
	
	/* If we are copying from the preset ROM, duplicate it. */
	if (options->presetrom != NULL && options->copyrom != 0)
	{
		/* Use this ROM size. */
		romsize = options->romsize;
		
		/* Allocate space to fit ROM. */
		rom = sjme_malloc(options->romsize);
		if (rom == NULL)
		{
			if (error != NULL)
				*error = SJME_ERROR_NOMEMORY;
			
			sjme_free(ram);
			sjme_free(conf);
			
			return NULL;
		}
		
		/* Copy large chunks at a time. */
		for (i = 0; i < options->romsize;)
		{
			/* Byte left to move? */
			l = options->romsize - i;
			
			/* Function uses a size_t which may be limited on this platform. */
			if (sizeof(sjme_jint) > sizeof(size_t) && l > (sjme_jint)SIZE_MAX)
				l = (sjme_jint)SIZE_MAX;
			
			/* Copy the data. */
			memmove(SJME_POINTER_OFFSET(rom, i),
				SJME_POINTER_OFFSET(options->presetrom, i), l);
			
			/* Offset up. */
			i += l;
		}
		
		/* We copied it, so never make a preset ROM. */
		rv->presetrom = NULL;
	}
	
#if defined(SJME_VIRTUAL_MEM)
	/* Virtual map ROM. */
	rv->rombase = vmem;
	vmem = (vmem + romsize + SJME_VIRTUAL_MEM_MASK) &
		(~SJME_VIRTUAL_MEM_MASK);
#endif
	
	/* Set JVM rom space. */
	rv->rom = rom;
	rv->romsize = romsize;
	
	/* Initialize configuration space. */
	rv->config = conf;
	rv->configsize = SJME_DEFAULT_CONF_SIZE;
	sjme_configinit(conf, SJME_DEFAULT_CONF_SIZE, rv, options, nativefuncs,
		error);
	
	/* Initialize the BootRAM and boot the CPU. */
	if (sjme_initboot(rom, ram, options->ramsize, rv, error) == 0)
	{
		/* Write the Boot failure message! */
		sjme_console_pipewrite(rv, (nativefuncs != NULL ?
			nativefuncs->stderr_write : NULL), sjme_bootfailmessage, 0,
			sizeof(sjme_bootfailmessage) / sizeof(sjme_jbyte));
		
		/* Cleanup. */
		sjme_free(rv);
		sjme_free(ram);
		sjme_free(conf);
		
		/* If a pre-set ROM is not being used, make sure it gets cleared. */
		if (options->presetrom)
			sjme_free(rom);
		
		return NULL;
	}
	
	/* The JVM is ready to use. */
	return rv;
}
