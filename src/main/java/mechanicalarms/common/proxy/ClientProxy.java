package mechanicalarms.common.proxy;

import com.modularmods.mcgltf.MCglTF;
import mechanicalarms.MechanicalArms;
import mechanicalarms.Tags;
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

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(Items.ARM_BASE, 0, new ModelResourceLocation(Tags.MODID, "models/block/arm#inventory"));

    }

    @Override
    public void preInit() {
        TileArmRenderer tesr = new TileArmRenderer();
        MCglTF.getInstance().addGltfModelReceiver(tesr);
        ClientRegistry.bindTileEntitySpecialRenderer(TileArmBasic.class, tesr);
        super.preInit();
    }



}
