package mechanicalarms.common.block;

import mechanicalarms.MechanicalArms;
import mechanicalarms.common.tile.TileArmBasic;
import mechanicalarms.common.tile.TileBeltBasic;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

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
        return EnumBlockRenderType.MODEL;
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
            if (!tileBeltBasic.getItemStack().isEmpty()) {
                drops.add(tileBeltBasic.getItemStack().copy());
            }
        }
        super.getDrops(drops, world, pos, state, fortune);
    }
}
