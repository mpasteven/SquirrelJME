# ---------------------------------------------------------------------------
# Multi-Phasic Applications: SquirrelJME
#     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
# ---------------------------------------------------------------------------
# SquirrelJME is under the GNU General Public License v3+, or later.
# See license.mkd for licensing and copyright information.
# ---------------------------------------------------------------------------
# DESCRIPTION: Core BootVM Source

# Base core library
add_library(BuildVMCore STATIC
	entry.c)
target_include_directories(BuildVMCore PUBLIC
	"${PROJECT_BINARY_DIR}" "${PROJECT_SOURCE_DIR}/include")

# Versioning information
add_compile_definitions(SQUIRRELJME_VERSION="${SQUIRRELJME_VERSION}")
add_compile_definitions(SQUIRRELJME_VERSION_ID="${SQUIRRELJME_VERSION_ID}")

# Make this position independent so it can be linked into shared libraries
set_property(TARGET BuildVMCore PROPERTY POSITION_INDEPENDENT_CODE ON)

# Are we building Java for Windows or everything else?
if(WIN32)
	add_subdirectory(win32)
else()
	add_subdirectory(unix)
endif()