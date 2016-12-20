// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) 2013-2016 Steven Gawroriski <steven@multiphasicapps.net>
//     Copyright (C) 2013-2016 Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// For more information see license.mkd.
// ---------------------------------------------------------------------------

package net.multiphasicapps.io.streamproc;

import java.io.Closeable;
import java.io.IOException;

/**
 * This is the base class for all stream processor which are used to input
 * and output data.
 *
 * This class is not thread safe.
 *
 * @since 2016/12/20
 */
public abstract class StreamProcessor
	implements Closeable
{
}

