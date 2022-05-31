package mechanicalarms.common.block;

import net.minecraft.block.Block;

public class Blocks
{
	public static Block ARM_BASE = null;

	public static void init() {
		ARM_BASE = new BlockArm();
	}

}
