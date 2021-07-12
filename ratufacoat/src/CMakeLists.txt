# ---------------------------------------------------------------------------
# Multi-Phasic Applications: SquirrelJME
#     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
# ---------------------------------------------------------------------------
# SquirrelJME is under the GNU General Public License v3+, or later.
# See license.mkd for licensing and copyright information.
# ---------------------------------------------------------------------------
# DESCRIPTION: Main project source code

# Base core library
add_library(SquirrelJMECore STATIC
	accelgfx.c
	atomic.c
	audio.c
	bootrom.c
	btooth.c
	cellfunc.c
	corefont.c
	cpu.c
	datatype.c
	debug.c
	error.c
	gpio.c
	handles.c
	ipnet.c
	jdwp.c
	jvm.c
	memory.c
	oldstuff.c
	opcode.c
	random.c
	sjmerc.c
	softmath.c
	stringies.c
	syscall.c
	video.c
	xbuiltin.c
	${SQUIRRELJME_BUILTIN_FILE})
target_include_directories(SquirrelJMECore PUBLIC
	"${PROJECT_BINARY_DIR}" "${PROJECT_SOURCE_DIR}/include")

# Does the static ROM file exist? If it does we can pack it into the binary
message("Built-In? ${SQUIRRELJME_HAS_BUILTIN} ${SQUIRRELJME_BUILTIN_FILE}")
if(SQUIRRELJME_HAS_BUILTIN)
	add_compile_definitions(SQUIRRELJME_HAS_BUILTIN=1)
endif()

# Versioning information
add_compile_definitions(SQUIRRELJME_VERSION="${SQUIRRELJME_VERSION}")
add_compile_definitions(SQUIRRELJME_VERSION_ID="${SQUIRRELJME_VERSION_ID}")

# Make this position independent so it can be linked into shared libraries
set_property(TARGET SquirrelJMECore PROPERTY POSITION_INDEPENDENT_CODE ON)

# Add the SDL2 Implementation
add_subdirectory(sdl2)

# And the libRetro Implementation (Testing)
add_subdirectory(libretro)
