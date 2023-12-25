package mechanicalarms.common.proxy;

import com.modularmods.mcgltf.MCglTF;
import mechanicalarms.MechanicalArms;
import mechanicalarms.client.renderer.TileArmRenderer;
import mechanicalarms.common.item.Items;
import mechanicalarms.common.tile.TileArmBasic;
import net.minecraft.client.renderer.block.model.ModelBakery;
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

    public static final ModelResourceLocation base = new ModelResourceLocation(new ResourceLocation(MechanicalArms.MODID, "models/block/arm_base.obj"), "base");
    public static final ModelResourceLocation arm = new ModelResourceLocation(new ResourceLocation(MechanicalArms.MODID, "models/block/arm_arm.obj"), "arm");
    public static final ModelResourceLocation hand = new ModelResourceLocation(new ResourceLocation(MechanicalArms.MODID, "models/block/arm_hand.obj"), "hand");
    public static final ModelResourceLocation claw = new ModelResourceLocation(new ResourceLocation(MechanicalArms.MODID, "models/block/arm_claw.obj"), "claw");
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(Items.ARM_BASE, 0, new ModelResourceLocation(new ResourceLocation(MechanicalArms.MODID, "models/block/arm_basic.obj"), "inventory"));

        ModelBakery.registerItemVariants(Items.ARM_BASE, base);
        ModelBakery.registerItemVariants(Items.ARM_BASE, arm);
        ModelBakery.registerItemVariants(Items.ARM_BASE, hand);
        ModelBakery.registerItemVariants(Items.ARM_BASE, claw);
    }

    @Override
    public void preInit() {
        OBJLoader.INSTANCE.addDomain(MechanicalArms.MODID);
        TileArmRenderer tesr = new TileArmRenderer();
        MCglTF.getInstance().addGltfModelReceiver(tesr);
        ClientRegistry.bindTileEntitySpecialRenderer(TileArmBasic.class, new TileArmRenderer());
        super.preInit();
    }

}
