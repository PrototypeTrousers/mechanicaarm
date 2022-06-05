package mechanicalarms.common.proxy;

import mechanicalarms.MechanicalArms;
import mechanicalarms.client.renderer.TileArmRenderer;
import mechanicalarms.common.item.Items;
import mechanicalarms.common.tile.TileArmBasic;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(Items.ARM_BASE, 0, new ModelResourceLocation(new ResourceLocation(MechanicalArms.MODID, "models/block/arm_basic"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Items.ARM_BASE, 1, new ModelResourceLocation(new ResourceLocation(MechanicalArms.MODID, "models/block/arm_basic_core"), "normal"));
        ModelLoader.setCustomModelResourceLocation(Items.ARM_BASE, 2, new ModelResourceLocation(new ResourceLocation(MechanicalArms.MODID, "models/block/arm_basic_firstarm"), "normal"));
        ModelLoader.setCustomModelResourceLocation(Items.ARM_BASE, 3, new ModelResourceLocation(new ResourceLocation(MechanicalArms.MODID, "models/block/arm_basic_secondarm"), "normal"));
    }

    @Override
    public void preInit() {
        super.preInit();
        ClientRegistry.bindTileEntitySpecialRenderer(TileArmBasic.class, new TileArmRenderer());
    }
}
