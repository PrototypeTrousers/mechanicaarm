package mechanicalarms.common.proxy;

import mechanicalarms.MechanicalArms;
import mechanicalarms.client.renderer.TileArmRenderer;
import mechanicalarms.client.renderer.TileBeltRenderer;
import mechanicalarms.common.item.Items;
import mechanicalarms.common.tile.TileArmBasic;
import mechanicalarms.common.tile.TileBeltBasic;
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

    public static final ModelResourceLocation base = new ModelResourceLocation(new ResourceLocation(MechanicalArms.MODID, "models/block/base.obj"), "");
    public static final ModelResourceLocation baseMotor = new ModelResourceLocation(new ResourceLocation(MechanicalArms.MODID, "models/block/basemotor.obj"), "");
    public static final ModelResourceLocation firstArm = new ModelResourceLocation(new ResourceLocation(MechanicalArms.MODID, "models/block/firstarm.obj"), "");
    public static final ModelResourceLocation secondArm = new ModelResourceLocation(new ResourceLocation(MechanicalArms.MODID, "models/block/secondarm.obj"), "");
    public static final ModelResourceLocation hand = new ModelResourceLocation(new ResourceLocation(MechanicalArms.MODID, "models/block/claw.obj"), "");
    public static final ModelResourceLocation belt = new ModelResourceLocation(new ResourceLocation(MechanicalArms.MODID, "models/block/belt.obj"), "");
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(Items.ARM_BASE, 0, new ModelResourceLocation(new ResourceLocation(MechanicalArms.MODID, "models/block/completearm.obj"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Items.BELT_BASE, 0, new ModelResourceLocation(new ResourceLocation(MechanicalArms.MODID, "models/block/belt.obj"), "inventory"));
    }

    @Override
    public void preInit() {
        OBJLoader.INSTANCE.addDomain(MechanicalArms.MODID);
        ClientRegistry.bindTileEntitySpecialRenderer(TileArmBasic.class, new TileArmRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileBeltBasic.class, new TileBeltRenderer());
        super.preInit();
    }

}
