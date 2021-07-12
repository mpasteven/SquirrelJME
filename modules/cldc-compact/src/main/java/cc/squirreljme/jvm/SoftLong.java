// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm;

/**
 * Software math operations on 64-bit integer types.
 *
 * @since 2019/05/24
 */
@SuppressWarnings("MagicNumber")
public final class SoftLong
{
	/**
	 * Not used.
	 *
	 * @since 2019/05/24
	 */
	private SoftLong()
	{
	}
	
	/**
	 * Adds two values.
	 *
	 * @param __al A low.
	 * @param __ah A high.
	 * @param __bl B low.
	 * @param __bh B high.
	 * @return The result.
	 * @since 2019/05/24
	 */
	public static long add(int __al, int __ah, int __bl, int __bh)
	{
		// Add the higher/lower parts
		int ch = __ah + __bh,
			cl = __al + __bl;
		
		// If the low addition carried a bit over, then set that bit in the
		// high part
		if ((cl + 0x80000000) < (__al + 0x80000000))
			ch++;
		
		// Return result
		return Assembly.longPack(cl, ch);
	}
	
	/**
	 * Ands two values.
	 *
	 * @param __al A low.
	 * @param __ah A high.
	 * @param __bl B low.
	 * @param __bh B high.
	 * @return The result.
	 * @since 2019/05/24
	 */
	public static long and(int __al, int __ah, int __bl, int __bh)
	{
		return Assembly.longPack(__al & __bl, __ah & __bh);
	}
	
	/**
	 * Compares two values.
	 *
	 * @param __al A low.
	 * @param __ah A high.
	 * @param __bl B low.
	 * @param __bh B high.
	 * @return The result.
	 * @since 2019/05/24
	 */
	public static int cmp(int __al, int __ah, int __bl, int __bh)
	{
		// Compare high values firsts
		if (__ah < __bh)
			return -1;
		else if (__ah > __bh)
			return 1;
		
		// Compare low values with unsigned comparison
		__al += Integer.MIN_VALUE;
		__bl += Integer.MIN_VALUE;
		if (__al < __bl)
			return -1;
		else if (__al > __bl)
			return 1;
		return 0;
	}
	
	/**
	 * Divides two values.
	 *
	 * @param __al A low.
	 * @param __ah A high.
	 * @param __bl B low.
	 * @param __bh B high.
	 * @return The result.
	 * @since 2019/05/24
	 */
	public static long div(int __al, int __ah, int __bl, int __bh)
	{
		// Dividing by zero?
		if (__bh == 0 && __bl == 0)
			throw new ArithmeticException();
		
		return SoftLong.__div(false, __al, __ah, __bl, __bh);
	}
	
	/**
	 * Multiplies two long values.
	 *
	 * @param __al A low.
	 * @param __ah A high.
	 * @param __bl B low.
	 * @param __bh B high.
	 * @return The result.
	 * @since 2019/05/24
	 */
	public static long mul(int __al, int __ah, int __bl, int __bh)
	{
		// Are both sides negative?
		if (((__ah & __bh) & 0x8000_0000) != 0)
		{
			// Negate and check for overflow
			__ah = (~__ah);
			__al = (~__al + 1);
			if (__al == 0)
				__ah++;
				
			// Negate and check for overflow
			__bh = (~__bh);
			__bl = (~__bl + 1);
			if (__bl == 0)
				__bh++;
			
			// Return result
			return SoftLong.__mul(__al, __ah, __bl, __bh);
		}
		
		// Perform the calculation
		return SoftLong.__mul(__al, __ah, __bl, __bh);
	}
	
	/**
	 * Negates a value.
	 *
	 * @param __al A low.
	 * @param __ah A high.
	 * @return The result.
	 * @since 2019/05/24
	 */
	public static long neg(int __al, int __ah)
	{
		// Negate and check for overflow
		__ah = (~__ah);
		__al = (~__al + 1);
		if (__al == 0)
			__ah++;
		
		// Return result
		return Assembly.longPack(__al, __ah);
	}
	
	/**
	 * Ors a value.
	 *
	 * @param __al A low.
	 * @param __ah A high.
	 * @param __bl B low.
	 * @param __bh B high.
	 * @return The result.
	 * @since 2019/05/24
	 */
	public static long or(int __al, int __ah, int __bl, int __bh)
	{
		return Assembly.longPack(__al | __bl, __ah | __bh);
	}
	
	/**
	 * Remainders a value.
	 *
	 * @param __al A low.
	 * @param __ah A high.
	 * @param __bl B low.
	 * @param __bh B high.
	 * @return The result.
	 * @since 2019/05/24
	 */
	public static long rem(int __al, int __ah, int __bl, int __bh)
	{
		// Dividing by zero?
		if (__bh == 0 && __bl == 0)
			throw new ArithmeticException();
		
		return SoftLong.__div(true, __al, __ah, __bl, __bh);
	}
	
	/**
	 * Shifts value left by bits.
	 *
	 * @param __al A low.
	 * @param __ah A high.
	 * @param __s Shift amount.
	 * @return The result.
	 * @since 2019/05/24
	 */
	public static long shl(int __al, int __ah, int __s)
	{
		// Mask the shift amount
		__s &= 0x3F;
		
		// Doing nothing?
		if (__s == 0)
			return Assembly.longPack(__al, __ah);
		
		// Shifting all the low bits to the high bits
		else if (__s >= 32)
			return Assembly.longPack(0, __al << (__s - 32));
		
		// Merge of bits (shift in range of 1-31)
		return Assembly.longPack((__al << __s),
			(__ah << __s) | (__al >>> (32 - __s)));
	}
	
	/**
	 * Shifts value right by bits.
	 *
	 * @param __al A low.
	 * @param __ah A high.
	 * @param __s Shift amount.
	 * @return The result.
	 * @since 2019/05/24
	 */
	public static long shr(int __al, int __ah, int __s)
	{
		// Mask the shift amount
		__s &= 0x3F;
		
		// Doing nothing?
		if (__s == 0)
			return Assembly.longPack(__al, __ah);
		
		// Shifting all the high bits low
		else if (__s >= 32)
			return Assembly.longPack(__ah >> (__s - 32),
				(__ah & 0x80000000) >> 31);
		
		// Merge of bits (shift in range of 1-31)
		return Assembly.longPack((__ah << (32 - __s)) | (__al >>> __s),
			(__ah >> __s));
	}
	
	/**
	 * Subtracts values.
	 *
	 * @param __al A low.
	 * @param __ah A high.
	 * @param __bl B low.
	 * @param __bh B high.
	 * @return The result.
	 * @since 2019/05/24
	 */
	public static long sub(int __al, int __ah, int __bl, int __bh)
	{
		// The same as add, but the second operand is negated
		// Negate and check for overflow
		__bh = (~__bh);
		__bl = (~__bl + 1);
		if (__bl == 0)
			__bh++;
		
		// Add the higher/lower parts
		int ch = __ah + __bh;
		int cl = __al + __bl;
		
		// If the low addition carried a bit over, then set that bit in the
		// high part
		if ((cl + 0x80000000) < (__al + 0x80000000))
			ch++;
		
		// Return result
		return Assembly.longPack(cl, ch);
	}
	
	/**
	 * Converts to double.
	 *
	 * @param __al Low value.
	 * @param __ah High value.
	 * @return The result.
	 * @since 2019/05/24
	 */
	public static double toDouble(int __al, int __ah)
	{
		Assembly.breakpoint();
		throw new todo.TODO();
	}
	
	/**
	 * Converts to float.
	 *
	 * @param __al A low.
	 * @param __ah A high.
	 * @return The result.
	 * @since 2019/05/24
	 */
	public static float toFloat(int __al, int __ah)
	{
		Assembly.breakpoint();
		throw new todo.TODO();
	}
	
	/**
	 * Converts to integer.
	 *
	 * @param __al A low.
	 * @param __ah A high.
	 * @return The result.
	 * @since 2019/05/24
	 */
	public static int toInteger(int __al, @SuppressWarnings("unused") int __ah)
	{
		// Just return the low order bits
		return __al;
	}
	
	/**
	 * Shifts value bits right unsigned.
	 *
	 * @param __al A low.
	 * @param __ah A high.
	 * @param __s Shift amount.
	 * @return The result.
	 * @since 2019/05/24
	 */
	public static long ushr(int __al, int __ah, int __s)
	{
		// Mask the shift amount
		__s &= 0x3F;
		
		// Doing nothing?
		if (__s == 0)
			return Assembly.longPack(__al, __ah);
		
		// Shifting all the high bits low
		else if (__s >= 32)
			return Assembly.longPack(__ah >>> (__s - 32), 0);
		
		// Merge of bits (shift in range of 1-31)
		return Assembly.longPack((__ah << (32 - __s)) | (__al >>> __s),
			(__ah >>> __s));
	}
	
	/**
	 * Xors two values.
	 *
	 * @param __al A low.
	 * @param __ah A high.
	 * @param __bl B low.
	 * @param __bh B high.
	 * @return The result.
	 * @since 2019/05/24
	 */
	public static long xor(int __al, int __ah, int __bl, int __bh)
	{
		return Assembly.longPack(__al ^ __bl, __ah ^ __bh);
	}
	
	/**
	 * Divides and remainders two values.
	 *
	 * @param __doRem Return the remainder?
	 * @param __nl The numerator, low.
	 * @param __nh The numerator, high.
	 * @param __dl The denominator, low.
	 * @param __dh The denominator, high.
	 * @return The result.
	 * @since 2019/05/24
	 */
	@SuppressWarnings("CommentedOutCode")
	private static long __div(boolean __doRem, int __nl, int __nh,
		int __dl, int __dh)
	{
		// {@squirreljme.error ZZ4z Divide by zero.}
		if (__dl == 0 && __dh == 0)
			throw new ArithmeticException("ZZ4z");
		
		// Wikipedia (http://en.wikipedia.org/wiki/Division_%28digital%29)
		// if D == 0 then throw DivisionByZeroException end
		// Q := 0 # initialize quotient and remainder to zero
		// R := 0
		// for i = n-1...0 do  # " where n is no of bits "
		//   R := R << 1       # left-shift R by 1 bit
		//   R(0) := N(i)      # set the least-significant bit
		//              # of R equal to bit i of the numerator
		//   if R >= D then
		//     R = R - D
		//     Q(i) := 1
		//   end
		// end
		// long qx = 0, rx = 0;
		
		// High and low resultant values
		int ql = 0;
		int qh = 0;
		int rl = 0;
		int rh = 0;
		
		// Disallow division by zero
		if (__dl == 0)
			return 0;
		
		// Results in a negative? Only results in such if either side is
		// a negative value
		boolean negNum = ((__nh & 0x8000_0000) != 0);
		boolean negDem = ((__dh & 0x8000_0000) != 0);
		boolean isNeg = (negNum != negDem);
		
		// Make the numerator positive, if negative
		if (negNum)
		{
			// Negate and check for overflow
			__nh = (~__nh);
			__nl = (~__nl + 1);
			if (__nl == 0)
				__nh++;
		}
		
		// Make the denominator positive, if negative
		if (negDem)
		{
			// Negate and check for overflow
			__dh = (~__dh);
			__dl = (~__dl + 1);
			if (__dl == 0)
				__dh++;
		}
		
		// __dl and __dh for unsigned compare
		int dlUnsigned = __dl + Integer.MIN_VALUE;
		int dhUnsigned = __dh + Integer.MIN_VALUE;
		
		// Perform Math
		for (int i = 63, hMask = 0xFFFF_FFFF, lMask = 0;
			i >= 0;
			i--, hMask >>>= 1, lMask = ((lMask >> 1) | 0x8000_0000))
		{
			// rx <<= 1;
			// rx &= 0xFFFFFFFFFFFFFFFEL;
			rh <<= 1;
			rh |= (rl >>> 31);
			rl <<= 1;
			
			// rx |= ((__nx >>> i) & 1L); ... only take the lowest bit!
			// branching:
			// ! if (i >= 32)
			// !     rl |= ((__nh >>> (i - 32)) & 0x1);
			// ! else
			// !     rl |= ((__nl >>> i) & 0x1);
			// faster using masking:
			// ! rl |= (((__nh & hMask) >>> i) & 0x1);
			// ! rl |= (((__nl & lMask) >>> i) & 0x1);
			rl |= ((((__nh & hMask) | (__nl & lMask)) >>> i) & 0x1);
			
			// Unsigned comparison (shift by 0x8000_0000__0000_0000L)
			// if ((rx + Long.MIN_VALUE) >= (__dx + Long.MIN_VALUE))
			/*if (SoftLong.cmp(rl, rh + Integer.MIN_VALUE,
				__dl, __dh + Integer.MIN_VALUE) >= 0)*/
			int cmp = (rh + Integer.MIN_VALUE) - dhUnsigned;
			if (cmp >= 0 &&
				(cmp > 0 ||	// Is just a bigger number overall? 
				rl + Integer.MIN_VALUE >= dlUnsigned))
			{
				// rx -= __dx;
				// The same as add, but the second operand is negated
				// Negate and check for overflow
				int bh = (~__dh);
				int bl = (~__dl + 1);
				if (bl == 0)
					bh++;
				
				// Add the higher/lower parts
				rh = rh + bh;
				int cl = rl + bl;
				
				// If the low addition carried a bit over, then set that bit in
				// the high part
				if ((cl + 0x80000000) < (rl + 0x80000000))
					rh++;
				
				// Use result
				rl = cl;
				
				// qx |= (1L << i);
				if (i >= 32)
					qh |= (1 << (i - 32));
				else
					ql |= (1 << i);
			}
		}
		
		// Return the remainder if needed, note the remainder is negative only
		// if the numerator is negative
		if (__doRem)
			return (negNum ? SoftLong.neg(rl, rh) :
				Assembly.longPack(rl, rh));
		
		// Return, normalize negative if needed
		return (isNeg ? SoftLong.neg(ql, qh) : Assembly.longPack(ql, qh));
	}
	
	/**
	 * Multiplies two long values, note that this will fail if both A and B
	 * are negative. The values must either be both positive or only one is
	 * negative.
	 *
	 * @param __al A low.
	 * @param __ah A high.
	 * @param __bl B low.
	 * @param __bh B high.
	 * @return The result.
	 * @since 2019/05/24
	 */
	private static long __mul(int __al, int __ah, int __bl, int __bh)
	{
		// First value
		int a = (__ah >>> 16);
		int b = (__ah & 0xFFFF);
		int c = (__al >>> 16);
		int d = (__al & 0xFFFF);
		
		// Second value
		int w = (__bh >>> 16);
		int x = (__bh & 0xFFFF);
		int y = (__bl >>> 16);
		int z = (__bl & 0xFFFF);
		
		// Effectively this is long multiplication
		// Multiplication in two's complement form is the same in both
		// signed and unsigned, so we need not worry about signs and just treat
		// this all as unsigned. Each fragment is 16 of the 64 bits.
		//             |  a.  b,  c.  d
		// *           |  w.  x,  y.  z
		// ============|===.===,===.===
		//             |d_w.d_x,d_y.d_z
		//          c_w|c_x.c_y,c_z.
		//      b_w b_x|b_y.b_z,   .
		//  a_w a_x a_y|a_z.   ,   .
		// ============|===.===,===.===
		//  DISCARD       m.  n,  o.  p
		
		// Overflow storage, since we want a temporary and we do not want to
		// make any allocations or otherwise for 64-bit values. This keeps it
		// all in 32-bit and handles the various situations.
		int over;
		
		// Multiply and add all the parts together
		// int p = (d * z);
		int p = (d * z);
		over = (p >>> 16);
		
		// int o = (d * y) + (c * z) + (p >>> 16);
		int o = over + (d * y);
		over = (o >>> 16);
		o = (o & 0xFFFF) + (c * z);
		over += (o >>> 16);
		
		// int n = (d * x) + (c * y) + (b * z) + (o >>> 16);
		int n = over + (d * x);
		over = (n >>> 16);
		n = (n & 0xFFFF) + (c * y);
		over += (n >>> 16);
		n = (n & 0xFFFF) + (b * z);
		over += (n >>> 16);
		
		// int m = (d * w) + (c * x) + (b * y) + (a * z) + (n >>> 16);
		// We need not care about overflow here, because it will all be
		// discarded anyway!
		int m = over + (d * w) + (c * x) + (b * y) + (a * z);
		
		// Combine the resultant parts
		return Assembly.longPack((o << 16) | (p & 0xFFFF),
			(m << 16) | (n & 0xFFFF));
	}
}

