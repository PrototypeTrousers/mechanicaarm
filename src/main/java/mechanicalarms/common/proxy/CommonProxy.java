package mechanicalarms.common.proxy;

import mechanicalarms.MechanicalArms;
import mechanicalarms.common.block.Blocks;
import mechanicalarms.common.item.Items;
import mechanicalarms.common.tile.TileArmBasic;
import mechanicalarms.common.tile.TileBeltBasic;
import mechanicalarms.common.tile.Tiles;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
public class CommonProxy
{
	@SubscribeEvent
	public static void registerBlocks( RegistryEvent.Register<Block> event )
	{
		event.getRegistry().registerAll( Blocks.ARM_BASE );
		event.getRegistry().registerAll( Blocks.BELT_BASE );
	}

	@SubscribeEvent
	public static void registerItems( RegistryEvent.Register<Item> event )
	{
		event.getRegistry().registerAll( Items.ARM_BASE );
		event.getRegistry().registerAll( Items.BELT_BASE );
	}

	@SubscribeEvent
	public static void onRegisterEntities(RegistryEvent.Register<EntityEntry> event)
	{
		GameRegistry.registerTileEntity( TileArmBasic.class, new ResourceLocation( MechanicalArms.MODID, "tilearm") );
		GameRegistry.registerTileEntity( TileBeltBasic.class, new ResourceLocation( MechanicalArms.MODID, "tilebelt") );
	}

	public void preInit()
	{
		Blocks.init();
		Items.init();
		Tiles.init();
	}

	public void init()
	{
	}

	public void postInit()
	{
	}
}

