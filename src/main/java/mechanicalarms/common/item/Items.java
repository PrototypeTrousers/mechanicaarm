package mechanicalarms.common.item;

import mechanicalarms.common.block.Blocks;

public class Items
{
	public static ItemArm ARM_BASE;

	public static void init() {
		ARM_BASE = new ItemArm( Blocks.ARM_BASE );
	}
}
