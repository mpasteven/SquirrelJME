// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm.summercoat.ld.pack;

import cc.squirreljme.jvm.summercoat.ld.mem.ReadableMemory;

/**
 * Represents a {@link TableOfContents} which is stored in the standard
 * format to be read via memory using a memory mapped file that is properly
 * aligned.
 * 
 * Table of contents are in the following format: {@code
 *  - uint16_t entryCount
 *  - uint16_t entrySpan
 * 
 * Then follows (entryCount * entrySpan) int32_t, each span of values
 * corresponds to a single index.
 * }
 *
 * @since 2021/07/11
 */
public final class TableOfContentsMemory
	implements TableOfContents
{
	/** The table of contents data. */
	private final ReadableMemory data;
	
	/** The number of entries in the table. */
	private int _count =
		-1;
	
	/** The number of ints per entry. */
	private int _span =
		-1;
	
	/**
	 * Initializes the table of contents.
	 * 
	 * @param __data The data for the table of contents.
	 * @throws NullPointerException On null arguments.
	 * @since 2021/04/03
	 */
	public TableOfContentsMemory(ReadableMemory __data)
		throws NullPointerException
	{
		if (__data == null)
			throw new NullPointerException("NARG");
		
		this.data = __data;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/03
	 */
	@Override
	public final int count()
	{
		if (this._count < 0)
			this.__init();
		
		return this._count;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2021/04/04
	 */
	@Override
	public final int get(int __dx, int __prop)
		throws IndexOutOfBoundsException
	{
		// Need to initialize this to get the info?
		if (this._count < 0)
			this.__init();
		
		// {@squirreljme.error ZZ50 The entry and/or property is not within
		// bounds. (The index; The property)}
		int span = this._span;
		if (__dx < 0 || __dx >= this._count ||
			__prop < 0 || __prop >= span)
			throw new IndexOutOfBoundsException("ZZ50 " + __dx + " " + __prop);
		
		return this.data.memReadInt(4 +
			(4 * (((long)__dx * span) + __prop)));
	}
	
	/**
	 * Initializes the table of contents fields.
	 * 
	 * @throws InvalidRomException If the table of contents is not valid.
	 * @since 2021/04/03
	 */
	private void __init()
		throws InvalidRomException
	{
		ReadableMemory data = this.data;
		int count = data.memReadShort(0) & 0xFFFF;
		int span = data.memReadShort(2) & 0xFFFF;
		
		// {@squirreljme.error ZZ4v ROM has no span.}
		if (span == 0)
			throw new InvalidRomException("ZZ4v");
		
		// Store for later use;
		this._count = count;
		this._span = span;
	}
}
