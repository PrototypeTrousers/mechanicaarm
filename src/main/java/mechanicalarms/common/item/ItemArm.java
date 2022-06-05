package mechanicalarms.common.item;

import mechanicalarms.MechanicalArms;
import mechanicalarms.common.tile.TileArmBasic;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ItemArm extends ItemBlock
{
    private BlockPos sourcePos= null ;
    private BlockPos targetPos = null;

    public ItemArm(Block armBase )
    {
        super( armBase );
        setRegistryName( MechanicalArms.MODID, "arm_basic" );
    }


    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      /*  TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity != null) {
            IItemHandler handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            if (handler != null) {
                if (targetPos == null) {
                    targetPos = pos;
                    MechanicalArms.logger.info("Target pos set to " + targetPos);
                    return EnumActionResult.FAIL;
                } else if (sourcePos == null && targetPos != pos) {
                    this.sourcePos = pos;
                    MechanicalArms.logger.info("Source pos set to " + sourcePos);
                    return EnumActionResult.FAIL;
                }
            }
        }
            if (this.sourcePos != null && this.targetPos != null) {
                return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
            }
        return EnumActionResult.FAIL;*/
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
       /* if (super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)) {
            TileEntity tileEntity1 = world.getTileEntity( pos );
            ((TileArmBasic) tileEntity1).setSource(sourcePos);
            ((TileArmBasic) tileEntity1).setTarget(targetPos);
            return true;
        }
        return false;
    }*/
        return super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
    }
}
