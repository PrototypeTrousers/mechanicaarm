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

import javax.vecmath.Matrix4f;

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

        float[] firstArmRotation = tileArmBasic.getRotation(0);
        float[] firstArmAnimationAngle = tileArmBasic.getAnimationRotation(0);

        Quaternion rot = Quaternion.ToQuaternion(lerp(firstArmAnimationAngle[0], firstArmRotation[0], partialTicks),
                lerp(firstArmAnimationAngle[1], firstArmRotation[1], partialTicks)
        );

        firstArm.setRotation(new float[]{rot.x, rot.y, rot.z, rot.w});


        float[] secondArmRotation = tileArmBasic.getRotation(1);
        float[] secondArmAnimationAngle = tileArmBasic.getAnimationRotation(1);

        rot =  Quaternion.ToQuaternion(lerp(secondArmAnimationAngle[0], secondArmRotation[0], partialTicks),
                0);

        secondArm.setRotation(new float[]{rot.x, rot.y, rot.z, rot.w});


        float[] handRotation = tileArmBasic.getRotation(2);
        float[] handRotationAnimationAngle = tileArmBasic.getAnimationRotation(2);

        rot =  Quaternion.ToQuaternion(lerp(handRotationAnimationAngle[0], handRotation[0], partialTicks),
                lerp(handRotationAnimationAngle[1], handRotation[1], partialTicks));

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
        return (previous * (1.0F - partialTick)) + (current * partialTick);
    }


    public void rotateX(float[] floats, float radian) {
        radian *= 0.5F;
        float x = (float) Math.sin(radian);
        float w = (float) Math.cos(radian);
        float x0 = floats[0];
        float y0 = floats[1];
        float z0 = floats[2];
        float w0 = floats[3];
        floats[0] =  x0 * w + w0 * x;
        floats[1] =  y0 * w + z0 * x;
        floats[2] = -y0 * x + z0 * w;
        floats[3] = -x0 * x + w0 * w;
    }

    public void rotateY(float[] floats, float radian) {
        radian *= 0.5F;
        float y = (float) Math.sin(radian);
        float w = (float) Math.cos(radian);
        float x0 = floats[0];
        float y0 = floats[1];
        float z0 = floats[2];
        float w0 = floats[3];
        floats[0] =  x0 * w - z0 * y;
        floats[1] =  y0 * w + w0 * y;
        floats[2] = x0 * y + z0 * w;
        floats[3] = -y0 * y + w0 * w;
    }

    public float[] rotateZ(float[] floats, float radian) {
        radian *= 0.5F;
        float z = (float) Math.sin(radian);
        float w = (float) Math.cos(radian);
        float x0 = floats[0];
        float y0 = floats[1];
        float z0 = floats[2];
        float w0 = floats[3];
        floats[0] =  x0 * w + y0 * z;
        floats[1] =  -x0 * w + y0 * w;
        floats[2] = z0 * w + w0 * z;
        floats[3] = -z0 * z + w0 * w;
        return floats;
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
