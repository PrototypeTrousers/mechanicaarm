package mechanicalarms.client.renderer;

import mechanicalarms.MechanicalArms;
import mechanicalarms.client.mixin.interfaces.IBufferBuilderMixin;
import mechanicalarms.client.renderer.shaders.Shader;
import mechanicalarms.client.renderer.shaders.ShaderManager;
import mechanicalarms.common.proxy.ClientProxy;
import mechanicalarms.common.tile.TileArmBasic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import javax.vecmath.Matrix4f;
import javax.vecmath.Tuple4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;

public class TileArmRenderer extends TileEntitySpecialRenderer<TileArmBasic> {

    private static final Vector3f V3F_ZERO = new Vector3f();
    private static int[][][] vertexArray = null;
    private final Matrix4f tempModelMatrix = new Matrix4f();
    private final Tuple4f vertexTransformingVec = new Vector4f();
    private final Vector3f V3F_POS = new Vector3f();
    private final Vector3f PIVOT_1 = new Vector3f(.5F, 1 + 7 / 16F, .5F);
    private final Vector3f ANTI_PIVOT_1 = new Vector3f(-.5F, -(1 + 7 / 16F), -.5F);
    private final Vector3f PIVOT_2 = new Vector3f(0.5F, 1 + 7 / 16F, .5F);
    private final Vector3f ANTI_PIVOT_2 = new Vector3f(-0.5F, -(1 + 7 / 16F), -.5F);

    private final Vector3f PIVOT_3 = new Vector3f(0.5F, 1 + 7 / 16F, 0);
    private final Vector3f ANTI_PIVOT_3 = new Vector3f(-0.5F, -(1 + 7 / 16F), 0);
    private int[] vertexDataArray;
    private int[] vertexItemDataArray;
    private int quadCount = 0;

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
                int size = vertexArray[0].length * 2 + vertexArray[1].length + vertexArray[2].length;
                this.vertexDataArray = new int[size * 28];

            }
            vbo = FixedFunctionVbo.setupVbo(vertexArray);
            vao = Vao.setupVertices(vertexArray);
        }
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        mat = new Matrix4f();
        mat.setIdentity();
        translate(mat, new Vector3f((float) 0, (float) 1, (float) 0));
        rotateX(mat, (float) (Math.PI/4));

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

        mat.setIdentity();

        GL20.glUniformMatrix4(rotationLoc, true, data.asFloatBuffer());

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("mechanicalarms:textures/arm_arm.png"));
        vao.draw();
        base_vao.release();
        //vbo.draw();

        GL11.glPopMatrix();
    }

    void rotateX(Matrix4f matrix, float angle) {
        this.tempModelMatrix.setIdentity();
        this.tempModelMatrix.rotX(angle);
        matrix.mul(this.tempModelMatrix);
    }

    void rotateY(Matrix4f matrix, float angle) {
        this.tempModelMatrix.setIdentity();
        this.tempModelMatrix.rotY(angle);
        matrix.mul(this.tempModelMatrix);
    }

    void rotateZ(Matrix4f matrix, float angle) {
        this.tempModelMatrix.setIdentity();
        this.tempModelMatrix.rotZ(angle);
        matrix.mul(this.tempModelMatrix);
    }

    void translate(Matrix4f matrix, Vector3f translation) {
        this.tempModelMatrix.setIdentity();
        this.tempModelMatrix.setTranslation(translation);
        matrix.mul(this.tempModelMatrix);
    }

    void restoreScale(Matrix4f matrix) {
        this.tempModelMatrix.setIdentity();
        this.tempModelMatrix.setM00(1F);
        this.tempModelMatrix.setM11(1F);
        this.tempModelMatrix.setM22(1F);
        matrix.mul(this.tempModelMatrix);
    }

    void moveToPivot(Matrix4f matrix, Vector3f pivot) {
        this.tempModelMatrix.setIdentity();
        this.tempModelMatrix.setTranslation(pivot);
        matrix.mul(this.tempModelMatrix);
    }

    private float lerp(float previous, float current, float partialTick) {
        return (previous * (1.0F - partialTick)) + (current * partialTick);
    }


}