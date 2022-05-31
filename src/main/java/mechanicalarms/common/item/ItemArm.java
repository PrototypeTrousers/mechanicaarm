package mechanicalarms.common.item;

import mechanicalarms.MechanicalArms;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemArm extends ItemBlock
{
    public ItemArm( Block armBase )
    {
        super( armBase );
        setRegistryName( MechanicalArms.MODID, "arm_base" );
    }


}
