package squirreljme.summercoat;

import cc.squirreljme.jvm.SystemCallIndex;
import cc.squirreljme.jvm.summercoat.SystemCall;
import net.multiphasicapps.tac.TestRunnable;

/**
 * Tests that error getting and setting works properly.
 *
 * @since 2020/02/26
 */
public class TestErrorGetSet
	extends TestRunnable
{
	/** The error code to use. */
	private static final int _ERROR_CODE =
		12;
	
	/**
	 * {@inheritDoc}
	 * @since 2020/02/26
	 */
	@Override
	public void test() throws Throwable
	{
		int orig = SystemCall.errorGet(SystemCallIndex.GARBAGE_COLLECT);
		
		int fset = SystemCall.errorSet(SystemCallIndex.GARBAGE_COLLECT,
			TestErrorGetSet._ERROR_CODE);
		
		this.secondary("sameasorig", (orig == fset));
		
		int fnow = SystemCall.errorGet(SystemCallIndex.GARBAGE_COLLECT);
		
		this.secondary("isset", (fnow == _ERROR_CODE));
	}
}
