package mechanicalarms.client.renderer;

import mechanicalarms.MechanicalArms;
import mechanicalarms.client.mixin.interfaces.IBufferBuilderMixin;
import mechanicalarms.client.renderer.shaders.Shader;
import mechanicalarms.client.renderer.shaders.ShaderManager;
import mechanicalarms.common.proxy.ClientProxy;
import mechanicalarms.common.tile.TileArmBasic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
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



    public static int color(int red, int green, int blue) {

        red = MathHelper.clamp(red, 0x00, 0xFF);
        green = MathHelper.clamp(green, 0x00, 0xFF);
        blue = MathHelper.clamp(blue, 0x00, 0xFF);

        final int alpha = 0xFF;

        // 0x alpha red green blue
        // 0xaarrggbb

        // int colorRGBA = 0;
        // colorRGBA |= red << 16;
        // colorRGBA |= green << 8;
        // colorRGBA |= blue << 0;
        // colorRGBA |= alpha << 24;

        return -16777216 | blue << 16 | green << 8 | red;

    }

    /**
     * The render method that gets called for your FastTESR implementation. This is where you render things.
     *
     * @param tileArmBasic your TileEntity instance.
     * @param x            the X position of the TE in view space.
     * @param y            the Y position of the TE in view space.
     * @param z            the Z position of the TE in view space.
     * @param partialTicks the amount of partial ticks escaped. Partial ticks happen when there are multiple frames per tick.
     * @param destroyStage the destroy progress of the TE. You may use it to render the "breaking" animation.
     * @param partial      currently seems to be a 1.0 constant.
     * @param buffer       the BufferBuilder containing arm_shader.vert data for vertices being rendered. It is safe to assume that the format is {@link net.minecraft.client.renderer.vertex.DefaultVertexFormats DefaultVertexFormats}.BLOCK. It is also safe to assume that the GL primitive for drawing is QUADS.
     */
    @Override
    public void renderTileEntityFast(final TileArmBasic tileArmBasic, final double x, final double y, final double z, final float partialTicks, final int destroyStage, final float partial, final BufferBuilder buffer) {
        {
            V3F_POS.x = (float) x;
            V3F_POS.y = (float) y;
            V3F_POS.z = (float) z;

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
        }
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
//        GL11.glTranslated(x + 0.5, y + 4, z + 0.5);


        // Create a FloatBuffer to store the matrix data
        FloatBuffer projectionMatrixBuffer = BufferUtils.createFloatBuffer(16);
        // Get the current projection matrix
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projectionMatrixBuffer);
        // Create a Matrix4f from the FloatBuffer
        mat = new Matrix4f(projectionMatrixBuffer.array());

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


    public void renderQuads(IBufferBuilderMixin buffer, int[][] quadDataList, Vector3f baseOffset, Matrix4f transformMatrix, int brightness, int color) {
        for (int i = 0; i < quadDataList.length; i++) {
            int[] quadData = quadDataList[i];
            for (int k = 0; k < 4; ++k) {
                // Getting the offset for the current arm_shader.vert.
                int vertexIndex = k * 7;
                vertexTransformingVec.x = Float.intBitsToFloat(quadData[vertexIndex]);
                vertexTransformingVec.y = Float.intBitsToFloat(quadData[vertexIndex + 1]);
                vertexTransformingVec.z = Float.intBitsToFloat(quadData[vertexIndex + 2]);
                vertexTransformingVec.w = 1;

                // Transforming it by the model matrix.
                transformMatrix.transform(vertexTransformingVec);

                // Converting the new data to ints.
                int x = Float.floatToRawIntBits((float) (vertexTransformingVec.x + baseOffset.x + buffer.getOffsetX()));
                int y = Float.floatToRawIntBits((float) (vertexTransformingVec.y + baseOffset.y + buffer.getOffsetY()));
                int z = Float.floatToRawIntBits((float) (vertexTransformingVec.z + baseOffset.z + buffer.getOffsetZ()));

                int destIndex = quadCount * 28 + vertexIndex;
                // arm_shader.vert position data
                vertexDataArray[destIndex] = x;
                vertexDataArray[destIndex + 1] = y;
                vertexDataArray[destIndex + 2] = z;

                vertexDataArray[destIndex + 3] = color;

                vertexDataArray[destIndex + 4] = quadData[vertexIndex + 4]; //texture
                vertexDataArray[destIndex + 5] = quadData[vertexIndex + 5];

                // arm_shader.vert brightness
                vertexDataArray[destIndex + 6] = brightness;
            }
            quadCount++;
        }
    }

    public void renderItemQuads(IBufferBuilderMixin buffer, int[][] quadDataList, Vector3f baseOffset, Matrix4f transformMatrix, int brightness, int color) {
        for (int i = 0; i < quadDataList.length; i++) {
            int[] quadData = quadDataList[i];
            for (int k = 0; k < 4; ++k) {
                // Getting the offset for the current arm_shader.vert.
                int vertexIndex = k * 7;
                vertexTransformingVec.x = Float.intBitsToFloat(quadData[vertexIndex]);
                vertexTransformingVec.y = Float.intBitsToFloat(quadData[vertexIndex + 1]);
                vertexTransformingVec.z = Float.intBitsToFloat(quadData[vertexIndex + 2]);
                vertexTransformingVec.w = 1;

                // Transforming it by the model matrix.
                transformMatrix.transform(vertexTransformingVec);

                // Converting the new data to ints.
                int x = Float.floatToRawIntBits((float) (vertexTransformingVec.x + baseOffset.x + buffer.getOffsetX()));
                int y = Float.floatToRawIntBits((float) (vertexTransformingVec.y + baseOffset.y + buffer.getOffsetY()));
                int z = Float.floatToRawIntBits((float) (vertexTransformingVec.z + baseOffset.z + buffer.getOffsetZ()));

                int destIndex = i * 28 + vertexIndex;
                // arm_shader.vert position data
                vertexItemDataArray[destIndex] = x;
                vertexItemDataArray[destIndex + 1] = y;
                vertexItemDataArray[destIndex + 2] = z;

                vertexItemDataArray[destIndex + 3] = color;

                vertexItemDataArray[destIndex + 4] = quadData[vertexIndex + 4]; //texture
                vertexItemDataArray[destIndex + 5] = quadData[vertexIndex + 5];

                // arm_shader.vert brightness
                vertexItemDataArray[destIndex + 6] = brightness;
            }
        }
    }

    private float lerp(float previous, float current, float partialTick) {
        return (previous * (1.0F - partialTick)) + (current * partialTick);
    }


}