package mechanicalarms.common.block;

import com.cleanroommc.modularui.api.UIInfos;
import mechanicalarms.MechanicalArms;
import mechanicalarms.common.tile.TileArmBasic;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
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

import javax.annotation.Nullable;

public class BlockArm extends Block implements ITileEntityProvider {

    public static final PropertyInteger ARM_PART_NUMBER = PropertyInteger.create("arm_part", 0, 3);

    public BlockArm() {
        super(Material.IRON);
        setRegistryName(MechanicalArms.MODID, "arm_basic");
        setDefaultState(this.blockState.getBaseState().withProperty(ARM_PART_NUMBER, 0));
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

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ARM_PART_NUMBER);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(ARM_PART_NUMBER));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(ARM_PART_NUMBER, meta);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(ARM_PART_NUMBER, 0);
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
        return new TileArmBasic();
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileArmBasic) {
            TileArmBasic tileArmBasic = (TileArmBasic) te;
            if (!tileArmBasic.getItemStack().isEmpty()) {
                drops.add(tileArmBasic.getItemStack().copy());
            }
        }
        super.getDrops(drops, world, pos, state, fortune);
    }


    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            UIInfos.TILE_MODULAR_UI.open(playerIn, worldIn, pos);
        }
        return true;
    }
}
