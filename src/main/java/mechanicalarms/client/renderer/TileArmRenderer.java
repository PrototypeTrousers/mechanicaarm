package mechanicalarms.client.renderer;

import mechanicalarms.MechanicalArms;
import mechanicalarms.client.renderer.shaders.Shader;
import mechanicalarms.client.renderer.shaders.ShaderManager;
import mechanicalarms.common.proxy.ClientProxy;
import mechanicalarms.common.tile.TileArmBasic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.nio.ByteBuffer;
import java.util.List;

public class TileArmRenderer extends TileEntitySpecialRenderer<TileArmBasic> {

    private static int[][][] vertexArray = null;

    Matrix4f mat;

    public static final Shader base_vao = ShaderManager.loadShader(new ResourceLocation(MechanicalArms.MODID, "shaders/arm_shader"))
            .withUniforms(ShaderManager.LIGHTMAP).withUniforms();

    FixedFunctionVbo vbo;
    Vao vao;

    public TileArmRenderer() {
        super();

    }

    @Override
    public void render(TileArmBasic te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (vbo == null) {
            BlockRendererDispatcher blockRendererDispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            ModelManager modelManager = blockRendererDispatcher.getBlockModelShapes().getModelManager();
            if (vertexArray == null) {
                vertexArray = new int[3][][];

                ModelResourceLocation[] mrl = new ModelResourceLocation[]{
                        ClientProxy.arm,
                        ClientProxy.hand,
                        ClientProxy.claw
                };

                for (int i = 0; i < mrl.length; i++) {
                    ModelResourceLocation m = mrl[i];
                    List<BakedQuad> quads = modelManager.getModel(m).getQuads(null, null, 0);
                    vertexArray[i] = new int[quads.size()][];
                    for (int j = 0; j < quads.size(); j++) {
                        vertexArray[i][j] = quads.get(j).getVertexData();
                    }
                }

            }
            vbo = FixedFunctionVbo.setupVbo(vertexArray);
            vao = Vao.setupVertices(vertexArray);
        }
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        mat = new Matrix4f();
        mat.setIdentity();
        //mat.translate(new Vector3f((float) x, (float) y, (float) z));

        base_vao.use();

        int rotationLoc = GL20.glGetUniformLocation(base_vao.getShaderId(), "rotation");

        ByteBuffer data = GLAllocation.createDirectByteBuffer(16 * 4);
        data.asFloatBuffer().put(new float[]{
                mat.m00,
                mat.m01,
                mat.m02,
                mat.m03,
                mat.m10,
                mat.m11,
                mat.m12,
                mat.m13,
                mat.m20,
                mat.m21,
                mat.m22,
                mat.m23,
                mat.m30,
                mat.m31,
                mat.m32,
                mat.m33}
        );

        GL20.glUniformMatrix4(rotationLoc, true, data.asFloatBuffer());

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("mechanicalarms:textures/arm_arm.png"));
        vao.draw();
        base_vao.release();
        //vbo.draw();

        GL11.glPopMatrix();
    }

    private float lerp(float previous, float current, float partialTick) {
        return (previous * (1.0F - partialTick)) + (current * partialTick);
    }


}