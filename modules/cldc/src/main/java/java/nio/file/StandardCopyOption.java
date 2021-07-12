// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package java.nio.file;

import cc.squirreljme.completion.Completion;
import cc.squirreljme.completion.CompletionState;
import cc.squirreljme.completion.Standard;

@Standard
public enum StandardCopyOption
	implements CopyOption
{
	@Completion(CompletionState.NOTHING)
	ATOMIC_MOVE,
	
	@Completion(CompletionState.NOTHING)
	COPY_ATTRIBUTES,
	
	@Completion(CompletionState.NOTHING)
	REPLACE_EXISTING,
	
	/** End. */
	;
}

