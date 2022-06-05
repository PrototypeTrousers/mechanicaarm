package mechanicalarms.common.block;

import mechanicalarms.MechanicalArms;
import mechanicalarms.common.tile.TileArmBasic;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockArm extends Block implements ITileEntityProvider
{

	public BlockArm()
	{
		super( Material.IRON );
		setRegistryName( MechanicalArms.MODID, "arm_basic" );

	}

	@Override
	public void onBlockPlacedBy( World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack )
	{
		super.onBlockPlacedBy( worldIn, pos, state, placer, stack );
	}

	@Override
	public void onPlayerDestroy( World worldIn, BlockPos pos, IBlockState state )
	{
		super.onPlayerDestroy( worldIn, pos, state );
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer( this, BlockDirectional.FACING );
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return (state.getValue( BlockDirectional.FACING )).getIndex();
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty( BlockDirectional.FACING, EnumFacing.byIndex( meta ) );
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return this.getDefaultState().withProperty( BlockDirectional.FACING, EnumFacing.UP );
	}

	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity( World worldIn, int meta )
	{
		return new TileArmBasic();
	}
}
