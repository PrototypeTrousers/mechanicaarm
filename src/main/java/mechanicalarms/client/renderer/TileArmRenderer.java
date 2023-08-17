package mechanicalarms.client.renderer;

import akka.io.DirectByteBufferPool;
import com.modularmods.mcgltf.IGltfModelReceiver;
import com.modularmods.mcgltf.MCglTF;
import com.modularmods.mcgltf.RenderedGltfModel;
import com.modularmods.mcgltf.RenderedGltfScene;
import de.javagl.jgltf.model.MathUtils;
import de.javagl.jgltf.model.NodeModel;
import mechanicalarms.Tags;
import mechanicalarms.client.util.Quaternion;
import mechanicalarms.common.tile.TileArmBasic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.vector.Matrix4f;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class TileArmRenderer extends TileEntitySpecialRenderer<TileArmBasic> implements IGltfModelReceiver {

    protected RenderedGltfScene renderedScene;

    protected NodeModel firstArm;
    protected NodeModel secondArm;
    protected NodeModel hand;

    protected FloatBuffer fb = ByteBuffer.allocateDirect(32 * 4).asFloatBuffer();

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

        //rotate(rot, item);

        hand.setRotation(new float[]{rot.x, rot.y, rot.z, rot.w});


        ItemStack inHand = tileArmBasic.getItemStack();

        GL11.glPushMatrix();
        float[] origin = new float[3];
        MathUtils.transformPoint3D(hand.computeGlobalTransform(null), new float[]{(float) 0, (float) 0, -1.0F}, origin);

        GL11.glTranslatef(origin[0], origin[1], origin[2]);

        GL11.glRotatef((float) (((handRotation[1]) * 180) / Math.PI), 0, 1, 0);
        GL11.glRotatef((float) ((handRotation[0]) * 180 / Math.PI), 1, 0, 0);

        GL11.glScalef(0.5F, 0.5F, 0.5F);
        Minecraft.getMinecraft().getRenderItem().renderItem(inHand, ItemCameraTransforms.TransformType.NONE);

        GL11.glPopMatrix();

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

    public void rotate(Quaternion quaternion, Matrix4f matrix4f) {
        // setup rotation matrix
        float xx = 2.0F * quaternion.x * quaternion.x;
        float yy = 2.0F * quaternion.y * quaternion.y;
        float zz = 2.0F * quaternion.z * quaternion.z;
        float xy = quaternion.x * quaternion.y;
        float yz = quaternion.y * quaternion.z;
        float zx = quaternion.z * quaternion.x;
        float xw = quaternion.x * quaternion.w;
        float yw = quaternion.y * quaternion.w;
        float zw = quaternion.z * quaternion.w;

        float r00 = 1.0F - yy - zz;
        float r11 = 1.0F - zz - xx;
        float r22 = 1.0F - xx - yy;
        float r10 = 2.0F * (xy + zw);
        float r01 = 2.0F * (xy - zw);
        float r20 = 2.0F * (zx - yw);
        float r02 = 2.0F * (zx + yw);
        float r21 = 2.0F * (yz + xw);
        float r12 = 2.0F * (yz - xw);

        // multiply matrices
        float f00 = matrix4f.m00;
        float f01 = matrix4f.m01;
        float f02 = matrix4f.m02;
        float f10 = matrix4f.m10;
        float f11 = matrix4f.m11;
        float f12 = matrix4f.m12;
        float f20 = matrix4f.m20;
        float f21 = matrix4f.m21;
        float f22 = matrix4f.m22;
        float f30 = matrix4f.m30;
        float f31 = matrix4f.m31;
        float f32 = matrix4f.m32;

        matrix4f.m00 = f00 * r00 + f01 * r10 + f02 * r20;
        matrix4f.m01 = f00 * r01 + f01 * r11 + f02 * r21;
        matrix4f.m02 = f00 * r02 + f01 * r12 + f02 * r22;
        matrix4f.m10 = f10 * r00 + f11 * r10 + f12 * r20;
        matrix4f.m11 = f10 * r01 + f11 * r11 + f12 * r21;
        matrix4f.m12 = f10 * r02 + f11 * r12 + f12 * r22;
        matrix4f.m20 = f20 * r00 + f21 * r10 + f22 * r20;
        matrix4f.m21 = f20 * r01 + f21 * r11 + f22 * r21;
        matrix4f.m22 = f20 * r02 + f21 * r12 + f22 * r22;
        matrix4f.m30 = f30 * r00 + f31 * r10 + f32 * r20;
        matrix4f.m31 = f30 * r01 + f31 * r11 + f32 * r21;
        matrix4f.m32 = f30 * r02 + f31 * r12 + f32 * r22;
    }
}
