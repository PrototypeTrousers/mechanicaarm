package mechanicalarms.common.item;

import mechanicalarms.common.block.Blocks;

public class Items
{
	public static ItemArm ARM_BASE;
	public static ItemBelt BELT_BASE;

	public static void init() {
		ARM_BASE = new ItemArm( Blocks.ARM_BASE );
		BELT_BASE = new ItemBelt( Blocks.BELT_BASE );
	}
}
