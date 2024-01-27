package mechanicalarms.common.block;

import mechanicalarms.MechanicalArms;
import mechanicalarms.common.tile.TileBeltBasic;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class BlockBelt extends Block implements ITileEntityProvider {

    public BlockBelt() {
        super(Material.IRON);
        setRegistryName(MechanicalArms.MODID, "belt_basic");
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public void onPlayerDestroy(World worldIn, BlockPos pos, IBlockState state) {
        super.onPlayerDestroy(worldIn, pos, state);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileBeltBasic();
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileBeltBasic tileBeltBasic) {
            IItemHandler side = tileBeltBasic.getleftItemHandler();
            for (int s =0 ; s < side.getSlots(); s++) {
                if (side.getStackInSlot(s).isEmpty()) {
                    continue;
                }
                drops.add(side.getStackInSlot(s).copy());
            }
            side = tileBeltBasic.getRightItemHandler();
            for (int s =0 ; s < side.getSlots(); s++) {
                if (side.getStackInSlot(s).isEmpty()) {
                    continue;
                }
                drops.add(side.getStackInSlot(s).copy());
            }
        }
        super.getDrops(drops, world, pos, state, fortune);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!playerIn.getHeldItem(hand).isEmpty()){
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileBeltBasic tbb) {
                tbb.getleftItemHandler().setStackInSlot(0, playerIn.getHeldItem(hand).copy());
            }
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }
}
