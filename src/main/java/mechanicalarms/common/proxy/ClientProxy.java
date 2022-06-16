package mechanicalarms.common.proxy;

import mechanicalarms.MechanicalArms;
import mechanicalarms.client.renderer.TileArmRenderer;
import mechanicalarms.common.item.Items;
import mechanicalarms.common.tile.TileArmBasic;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(Items.ARM_BASE, 0, new ModelResourceLocation(new ResourceLocation(MechanicalArms.MODID, "models/block/arm_basic.obj"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Items.ARM_BASE, 1, new ModelResourceLocation(new ResourceLocation(MechanicalArms.MODID, "models/block/arm_base.obj"), "arm_part=1"));
        ModelLoader.setCustomModelResourceLocation(Items.ARM_BASE, 2, new ModelResourceLocation(new ResourceLocation(MechanicalArms.MODID, "models/block/arm_arm.obj"), "arm_part=2"));
        ModelLoader.setCustomModelResourceLocation(Items.ARM_BASE, 3, new ModelResourceLocation(new ResourceLocation(MechanicalArms.MODID, "models/block/arm_arm.obj"), "arm_part=3"));
        ModelLoader.setCustomModelResourceLocation(Items.ARM_BASE, 4, new ModelResourceLocation(new ResourceLocation(MechanicalArms.MODID, "models/block/arm_hand.obj"), "arm_part=4"));
        ModelLoader.setCustomModelResourceLocation(Items.ARM_BASE, 5, new ModelResourceLocation(new ResourceLocation(MechanicalArms.MODID, "models/block/arm_claw.obj"), "arm_part=5"));
    }

    @Override
    public void preInit() {
        OBJLoader.INSTANCE.addDomain(MechanicalArms.MODID);
        ClientRegistry.bindTileEntitySpecialRenderer(TileArmBasic.class, new TileArmRenderer());
        super.preInit();
    }
}
