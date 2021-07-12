// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

import cc.squirreljme.runtime.cldc.debug.Debugging;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.Random;
import net.multiphasicapps.io.Base64Decoder;
import net.multiphasicapps.io.Base64Encoder;
import net.multiphasicapps.io.StringReader;
import net.multiphasicapps.tac.TestSupplier;

/**
 * Test that encoding base64 data encodes then decodes.
 *
 * @since 2021/05/23
 */
public class TestBase64EncodesThenDecodes
	extends TestSupplier<Boolean>
{
	/** Conversion runs. */
	public static final int RUNS =
		64;
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/23
	 */
	@Override
	public Boolean test()
		throws IOException
	{
		// Make a bunch of random data in sequence, will need for padding
		// and otherwise
		Random rand = new Random(0xCAFE_BABE_DEAD_BEEFL);
		for (int i = 0; i < TestBase64EncodesThenDecodes.RUNS; i++)
		{
			// Debug
			Debugging.debugNote("%d: ...", i);
			
			// Generate data
			byte[] data = new byte[i];
			rand.nextBytes(data);
			
			// Encode the data
			StringBuilder sb = new StringBuilder();
			try (Reader enc = new Base64Encoder(
				new ByteArrayInputStream(data)))
			{
				for (;;)
				{
					int c = enc.read();
					if (c < 0)
						break;
					
					sb.append((char)c);
				}
			}
			
			// Debug
			Debugging.debugNote("%d: %s", i, sb);
			
			// Decode the sequence
			byte[] redo;
			try (InputStream dec = new Base64Decoder(
				new StringReader(sb.toString()));
				ByteArrayOutputStream baos = new ByteArrayOutputStream(i))
			{
				// Convert
				byte[] buf = new byte[128];
				for (;;)
				{
					int rc = dec.read(buf);
					if (rc < 0)
						break;
					
					baos.write(buf, 0, rc);
				}
				
				// Make array again
				redo = baos.toByteArray();
			}
			
			for (int j = 0; j < i; j++)
			{
				Debugging.debugNote("%8s ? %8s",
					Integer.toString(data[j] & 0xFF, 2),
					Integer.toString(redo[j] & 0xFF, 2));
			}
			
			// These two must be the same
			if (!Arrays.equals(data, redo))
			{
				this.secondary("data-" + i, data);
				this.secondary("redo-" + i, redo);
				return false;
			}
		}
		
		// Would all be matches
		return true;
	}
}
