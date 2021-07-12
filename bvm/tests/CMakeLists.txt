# ---------------------------------------------------------------------------
# Multi-Phasic Applications: SquirrelJME
#     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
# ---------------------------------------------------------------------------
# SquirrelJME is under the GNU General Public License v3+, or later.
# See license.mkd for licensing and copyright information.
# ---------------------------------------------------------------------------
# DESCRIPTION: Tests for BootVM

# Build executable just for testing
file(GLOB BUILDVM_TEST_SOURCES "test*.c")
add_executable(BuildVMTests
    mainEntry.c
	${BUILDVM_TEST_SOURCES})

# Additional includes
target_include_directories(BuildVMTests PUBLIC
	"${PROJECT_BINARY_DIR}" "${PROJECT_SOURCE_DIR}/include")

# Link in core SquirrelJME, make it static so there are no dependencies
target_link_libraries(BuildVMTests
	BuildVMCore -static)

# Versioning information
add_compile_definitions(SQUIRRELJME_VERSION="${SQUIRRELJME_VERSION}")
add_compile_definitions(SQUIRRELJME_VERSION_ID="${SQUIRRELJME_VERSION_ID}")

# Used to wrap for QEMU and otherwise
if(DEFINED BUILDVM_TEST_WRAPPER)
	message("Wrapping tests in ${BUILDVM_TEST_WRAPPER}...")
endif()

# Scan for tests
foreach(BUILDVM_TEST_SOURCE ${BUILDVM_TEST_SOURCES})
	# CMake 3.20 has cmake_path, so we can use that.
	# We need to do this because on Windows, CLion+CMake just gives the full
	# path which we really do not want as it will mess up with tests
	if(${CMAKE_VERSION} VERSION_GREATER_EQUAL 3.20)
		cmake_path(GET "${BUILDVM_TEST_SOURCE}" FILENAME ${BUILDVM_TEST_SOURCE})
	else()
		get_filename_component(BUILDVM_TEST_SOURCE "${BUILDVM_TEST_SOURCE}" NAME)
	endif()

	# Remove the C to just get the base test
	string(REPLACE ".c" "" BUILDVM_TEST_SOURCE ${BUILDVM_TEST_SOURCE})

	# Note it and register it accordingly
	message("Adding test ${BUILDVM_TEST_SOURCE}...")
	if(DEFINED BUILDVM_TEST_WRAPPER)
		add_test(NAME ${BUILDVM_TEST_SOURCE}
			COMMAND "${BUILDVM_TEST_WRAPPER}" "$<TARGET_FILE:SquirrelJMETests>" ${BUILDVM_TEST_SOURCE})
	else()
		add_test(NAME ${BUILDVM_TEST_SOURCE}
			COMMAND SquirrelJMETests ${BUILDVM_TEST_SOURCE})
	endif()

	# Code for skipped tests
	set_property(TEST ${BUILDVM_TEST_SOURCE} PROPERTY SKIP_RETURN_CODE 42)
	set_tests_properties(${BUILDVM_TEST_SOURCE} PROPERTIES SKIP_RETURN_CODE 42)
endforeach()
