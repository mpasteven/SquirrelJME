# ---------------------------------------------------------------------------
# Multi-Phasic Applications: SquirrelJME
#     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
# ---------------------------------------------------------------------------
# SquirrelJME is under the GNU General Public License v3+, or later.
# See license.mkd for licensing and copyright information.
# ---------------------------------------------------------------------------
# DESCRIPTION: LibRetro CMake Layer, solely for IDE and quick testing purposes

# Define shared library
add_library(squirreljme_libretro SHARED
	lraudio.c
	lrcheat.c
	lrfreeze.c
	lrinfo.c
	lrjoypad.c
	lrnet.c
	lrvfs.c
	sys_libr.c)

# Remove the "lib" prefix on Windows, it is incorrect
if(CMAKE_SYSTEM_NAME STREQUAL Windows)
	set_target_properties(squirreljme_libretro PROPERTIES PREFIX "")
endif()

# Include all of these
target_include_directories(squirreljme_libretro PUBLIC
	"${PROJECT_BINARY_DIR}"
	"${PROJECT_SOURCE_DIR}/include"
	"${PROJECT_SOURCE_DIR}/libretro")

# Include the SquirrelJME Core as all of the JVM code is there
target_link_libraries(squirreljme_libretro
	SquirrelJMECore)

# If Static ROM is being used, extra RetroArch options are available
if(SQUIRRELJME_HAS_BUILTIN)
	add_compile_definitions(SQUIRRELJME_HAS_BUILTIN=1)
endif()

# Versioning information
add_compile_definitions(SQUIRRELJME_VERSION="${SQUIRRELJME_VERSION}")
add_compile_definitions(SQUIRRELJME_VERSION_ID="${SQUIRRELJME_VERSION_ID}")

# RetroArch must be built with position independent code for any kind of
# library.
set_property(TARGET squirreljme_libretro PROPERTY POSITION_INDEPENDENT_CODE ON)

# Custom launching the core, tries to find RetroArch on the system
## Determine RetroArch directory
if(CMAKE_SYSTEM_NAME STREQUAL Windows)
	set(SQUIRRELJME_LIBRETRO_EXTENSION ".exe")

	if (EXISTS "$ENV{APPDATA}/RetroArch")
		set(SQUIRRELJME_LIBRETRO_DIR "$ENV{APPDATA}/RetroArch")
	endif()
else()
	set(SQUIRRELJME_LIBRETRO_EXTENSION "")
endif()

## Target to run RetroArch with the SquirrelJME Core
if(DEFINED SQUIRRELJME_LIBRETRO_DIR)
	add_custom_target(RetroArch
		DEPENDS squirreljme_libretro
		COMMAND "${SQUIRRELJME_LIBRETRO_DIR}/retroarch${SQUIRRELJME_LIBRETRO_EXTENSION}" "-L" "$<TARGET_FILE:squirreljme_libretro>"
		WORKING_DIRECTORY "${PROJECT_SOURCE_DIR}"
		COMMENT "Starting RetroArch with SquirrelJME")
endif()
