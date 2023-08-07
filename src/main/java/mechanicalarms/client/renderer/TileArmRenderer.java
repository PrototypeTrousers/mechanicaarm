package mechanicalarms.client.renderer;

import com.modularmods.mcgltf.IGltfModelReceiver;
import com.modularmods.mcgltf.MCglTF;
import com.modularmods.mcgltf.RenderedGltfModel;
import com.modularmods.mcgltf.RenderedGltfScene;
import de.javagl.jgltf.model.NodeModel;
import mechanicalarms.Tags;
import mechanicalarms.client.util.Quaternion;
import mechanicalarms.common.tile.TileArmBasic;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class TileArmRenderer extends TileEntitySpecialRenderer<TileArmBasic> implements IGltfModelReceiver  {

    protected RenderedGltfScene renderedScene;

    protected NodeModel firstArm;
    protected NodeModel secondArm;
    protected NodeModel hand;

    public TileArmRenderer() {
        super();
    }

    @Override
    public void render(TileArmBasic tileArmBasic, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glTranslated(x, y, z);
        World world = tileArmBasic.getWorld();
        if (world != null) {
            GL11.glTranslatef(0.5F, 0.0F, 0.5F); //Make sure it is in the center of the block
        }

        float[] firstArmCurrRot = tileArmBasic.getCurrentRotation(0);
        float[] firstArmPrevRot = tileArmBasic.getPreviousRotation(0);

        Quaternion rot = Quaternion.createIdentity();

        rot.rotateY(lerp(firstArmPrevRot[1], firstArmCurrRot[1], partialTicks));
        rot.rotateX(lerp(firstArmPrevRot[0], firstArmCurrRot[0], partialTicks));

        firstArm.setRotation(new float[]{rot.x, rot.y, rot.z, rot.w});

        float[] secondArmCurrRot = tileArmBasic.getCurrentRotation(1);
        float[] secondArmPrevRot = tileArmBasic.getPreviousRotation(1);

        rot.setIndentity();
        rot.rotateX(lerp(secondArmPrevRot[0], secondArmCurrRot[0], partialTicks));

        secondArm.setRotation(new float[]{rot.x, rot.y, rot.z, rot.w});

        float[] handRotation = tileArmBasic.getCurrentRotation(2);
        float[] handPrevRot = tileArmBasic.getPreviousRotation(2);

        rot.setIndentity();

        rot.rotateX(lerp(handPrevRot[0], handRotation[0], partialTicks));
        rot.rotateY(lerp(handPrevRot[1], handRotation[1], partialTicks));
        rot.rotateZ(lerp(handPrevRot[2], handRotation[2], partialTicks));




        hand.setRotation(new float[]{rot.x, rot.y, rot.z, rot.w});

        if (MCglTF.getInstance().isShaderModActive()) {
            renderedScene.renderForShaderMod();
        } else {
            renderedScene.renderForVanilla();
        }

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private float lerp(float previous, float current, float partialTick) {
        var diff = Math.abs(previous) - Math.abs(current);
        if (diff > Math.PI) {
            previous = 0;
        } else if (diff < -Math.PI) {
            current = 0;
        }
        return (previous * (1.0F - partialTick)) + (current * partialTick);
    }

    @Override
    public ResourceLocation getModelLocation() {
        return new ResourceLocation(Tags.MODID, "models/block/arm.gltf");
    }

    @Override
    public void onReceiveSharedModel(RenderedGltfModel renderedModel) {
        renderedScene = renderedModel.renderedGltfScenes.get(0);
        for (NodeModel n : renderedModel.gltfModel.getNodeModels()) {
            switch (n.getName()) {
                case "arm1.003" -> firstArm = n;
                case "arm1.002" -> secondArm = n;
                case "hand" -> hand = n;
            }
        }
    }
}
