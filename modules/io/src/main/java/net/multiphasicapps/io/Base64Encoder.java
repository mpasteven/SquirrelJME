// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.io;

import cc.squirreljme.runtime.cldc.debug.Debugging;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * This encodes binary data to a base64 representation of that data.
 *
 * @since 2021/05/22
 */
public final class Base64Encoder
	extends Reader
{
	/** Character mask. */
	private static final int _CHAR_MASK =
		0b111_111;
	
	/** Character shift. */
	private static final int _BIT_COUNT =
		6;
	
	/** The stream to read from. */
	protected final InputStream in;
	
	/** The alphabet. */
	private final char[] _alphabet;
	
	/** The current bit stream that is left over. */
	int _bitStream;
	
	/** The number of bits in the stream. */
	byte _count;
	
	/** Was EOF hit? */
	boolean _hitEof;
	
	/** Padding that is left to output, when EOF. */
	byte _paddingLeft =
		-1;
	
	/** Bytes converted. */
	int _totalBytes =
		0;
	
	/**
	 * Initializes the base64 encoder, using the basic alphabet.
	 * 
	 * @param __in The stream to read from.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/05/22
	 */
	public Base64Encoder(InputStream __in)
		throws NullPointerException
	{
		this(__in, Base64Alphabet.BASIC);
	}
	
	/**
	 * Initializes the base64 encoder.
	 * 
	 * @param __in The stream to read from.
	 * @param __alphabet The alphabet to encode/decode from.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/05/22
	 */
	public Base64Encoder(InputStream __in, Base64Alphabet __alphabet)
		throws NullPointerException
	{
		if (__in == null || __alphabet == null)
			throw new NullPointerException("NARG");
		
		this.in = __in;
		this._alphabet = __alphabet._alphabet;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/22
	 */
	@Override
	public void close()
		throws IOException
	{
		this.in.close();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/05/22
	 */
	@Override
	public int read(char[] __c, int __o, int __l)
		throws IndexOutOfBoundsException, IOException, NullPointerException
	{
		if (__c == null)
			throw new NullPointerException("NARG");
		if (__o < 0 || __l < 0 || (__o + __l) > __c.length)
			throw new IndexOutOfBoundsException("IOOB");
			
		InputStream in = this.in;
		char[] alphabet = this._alphabet;
		
		int bitStream = this._bitStream;
		int count = this._count;
		boolean hitEof = this._hitEof;
		int paddingLeft = this._paddingLeft;
		int totalBytes = this._totalBytes;
		
		// We want to restore the data fields back when we are done reading
		try
		{
			int converted = 0;
			while (converted < __l)
			{
				// Determine the character to output
				if (count >= Base64Encoder._BIT_COUNT ||
					(hitEof && count > 0))
				{
					// We want the upper bits!
					int downShift = Math.max(0,
						count - Base64Encoder._BIT_COUNT);
					
					// Read in value, if it is too short then shift it up
					// and add zero padding accordingly
					int value = (bitStream >>> downShift) &
						Base64Encoder._CHAR_MASK;
					if (hitEof && count < Base64Encoder._BIT_COUNT)
						value <<= Base64Encoder._BIT_COUNT - count;
					
					// Output encoded character
					__c[__o + (converted++)] = alphabet[value];
					
					// Eat up the bit stream
					count = Math.max(0, count - Base64Encoder._BIT_COUNT);
					bitStream &= ~(-1 << downShift);
				}
				
				// No padding left to read, we stop
				else if (paddingLeft == 0)
					break;
				
				// Is there padding left to put in
				else if (paddingLeft > 0)
				{
					__c[__o + (converted++)] = '=';
					
					// We took this padding
					paddingLeft--;
					continue;
				}
				
				// We could overflow our own storage, so try again
				if (count + 8 > 24 || hitEof)
					continue;
				
				// If EOF hit, then we will pad
				int read = in.read();
				if (read < 0)
				{
					hitEof = true;
					
					// How much padding to place in?
					switch (totalBytes % 3)
					{
						case 0: paddingLeft = 0; break;
						case 1: paddingLeft = 2; break;
						case 2: paddingLeft = 1; break;
					}
				}
				
				// When adding bytes push below
				else
				{
					bitStream <<= 8;
					bitStream |= (read & 0xFF);
					count += 8;
					
					totalBytes++;
				}
			}
			
			return (hitEof && paddingLeft == 0 && converted == 0 ? -1 :
				converted);
		}
		
		// Always store the fields back when done
		finally
		{
			this._bitStream = bitStream;
			this._count = (byte)count;
			this._hitEof = hitEof;
			this._paddingLeft = (byte)paddingLeft;
			this._totalBytes = totalBytes;
		}
	}
}
