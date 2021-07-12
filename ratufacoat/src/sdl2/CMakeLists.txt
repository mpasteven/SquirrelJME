# ---------------------------------------------------------------------------
# Multi-Phasic Applications: SquirrelJME
#     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
# ---------------------------------------------------------------------------
# SquirrelJME is under the GNU General Public License v3+, or later.
# See license.mkd for licensing and copyright information.
# ---------------------------------------------------------------------------
# DESCRIPTION: SDL2 front-end to SquirrelJME

add_executable(SquirrelJME
	sys_stdc.c)

target_include_directories(SquirrelJME PUBLIC
	"${PROJECT_BINARY_DIR}"
	"${PROJECT_SOURCE_DIR}/include")

target_link_libraries(SquirrelJME
	SquirrelJMECore)

# Versioning information
add_compile_definitions(SQUIRRELJME_VERSION="${SQUIRRELJME_VERSION}")
add_compile_definitions(SQUIRRELJME_VERSION_ID="${SQUIRRELJME_VERSION_ID}")
