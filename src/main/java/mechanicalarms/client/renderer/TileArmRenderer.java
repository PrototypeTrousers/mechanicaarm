package mechanicalarms.client.renderer;

import mechanicalarms.MechanicalArms;
import mechanicalarms.client.renderer.shaders.Shader;
import mechanicalarms.client.renderer.shaders.ShaderManager;
import mechanicalarms.client.renderer.util.Quaternion;
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
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import javax.vecmath.Matrix4f;
import javax.vecmath.Tuple4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.nio.FloatBuffer;
import java.util.List;

import static mechanicalarms.client.renderer.Vao.ebo;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL31.glDrawArraysInstanced;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;


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

    int totalInstances = 10000;

    protected static final FloatBuffer MODELVIEW_MATRIX_BUFFER = GLAllocation.createDirectFloatBuffer(16);;
    protected static final FloatBuffer PROJECTION_MATRIX_BUFFER = GLAllocation.createDirectFloatBuffer(16);


    Matrix4f transformMatrix = new Matrix4f();
    Quaternion rot = Quaternion.createIdentity();

    FloatBuffer fb = GLAllocation.createDirectFloatBuffer(16 * totalInstances);

    Matrix4f mat;

    public static final Shader base_vao = ShaderManager.loadShader(new ResourceLocation(MechanicalArms.MODID, "shaders/arm_shader"))
            .withUniforms(ShaderManager.LIGHTMAP).withUniforms();

    Vao vao;

    public TileArmRenderer() {
        super();

    }

    @Override
    public void renderTileEntityFast(TileArmBasic tileArmBasic, double x, double y, double z, float partialTicks, int destroyStage, float partial, BufferBuilder buffer) {

        if (vao == null) {
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
            vao = Vao.setupVertices(vertexArray);
        }

        renderFirstArm(tileArmBasic, x, y, z, partialTicks);
        //renderSecondArm(tileArmBasic, x, y, z, partialTicks);
    }

    void renderFirstArm(TileArmBasic tileArmBasic, double x, double y, double z, float partialTicks){

        GL11.glPushMatrix();
        GL11.glEnable(GL_DEPTH_TEST);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MODELVIEW_MATRIX_BUFFER);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, PROJECTION_MATRIX_BUFFER);

        float[] firstArmCurrRot = tileArmBasic.getRotation(0);
        float[] firstArmPrevRot = tileArmBasic.getAnimationRotation(0);

        rot.setIndentity();
        transformMatrix.setIdentity();
        Quaternion rot = Quaternion.createIdentity();
        translate(transformMatrix, (float) x, (float) y, (float) z);
        moveToPivot(transformMatrix, PIVOT_1);
        rot.rotateY(lerp(firstArmPrevRot[1], firstArmCurrRot[1], partialTicks));
        rot.rotateX(lerp(firstArmPrevRot[0], firstArmCurrRot[0], partialTicks));
        Quaternion.rotateMatrix(transformMatrix, rot);
        moveToPivot(transformMatrix, ANTI_PIVOT_1);

        float[] fa = new float[]{
                transformMatrix.m00,
                transformMatrix.m10,
                transformMatrix.m20,
                transformMatrix.m30,
                transformMatrix.m01,
                transformMatrix.m11,
                transformMatrix.m21,
                transformMatrix.m31,
                transformMatrix.m02,
                transformMatrix.m12,
                transformMatrix.m22,
                transformMatrix.m32,
                transformMatrix.m03,
                transformMatrix.m13,
                transformMatrix.m23,
                transformMatrix.m33};

        base_vao.use();

        GL30.glBindVertexArray(vao.vaoId);

        glBindBuffer(GL_ARRAY_BUFFER, Vao.vboInstance);

        for (int i=0;i < totalInstances; i++) {
            fb.position(i*16);
            fb.put(fa, 0, 16);
        }
        fb.rewind();

        glBufferData(GL_ARRAY_BUFFER, fb, GL_STATIC_DRAW);

        int projectionLoc = GL20.glGetUniformLocation(base_vao.getShaderId(), "projection");
        int viewLoc = GL20.glGetUniformLocation(base_vao.getShaderId(), "view");

        GL20.glUniformMatrix4(projectionLoc, false, PROJECTION_MATRIX_BUFFER);
        GL20.glUniformMatrix4(viewLoc, false, MODELVIEW_MATRIX_BUFFER);

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("mechanicalarms:textures/arm_arm.png"));
        GL15.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

        glDrawElementsInstanced(GL_TRIANGLES, 120, GL_UNSIGNED_INT, 0 ,1);
        GL30.glBindVertexArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        GL15.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);


        base_vao.release();
        glPopMatrix();
    }
    @Override
    public void render(TileArmBasic tileArmBasic, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

    }

    Matrix4f fbToM4f(FloatBuffer fb, Matrix4f mat) {
        mat.m00 = fb.get();
        mat.m01 = fb.get();
        mat.m02 = fb.get();
        mat.m03 = fb.get();
        mat.m10 = fb.get();
        mat.m11 = fb.get();
        mat.m12 = fb.get();
        mat.m13 = fb.get();
        mat.m20 = fb.get();
        mat.m21 = fb.get();
        mat.m22 = fb.get();
        mat.m23 = fb.get();
        mat.m30 = fb.get();
        mat.m31 = fb.get();
        mat.m32 = fb.get();
        mat.m33 = fb.get();
        fb.rewind();
        return mat;
    }

    public Matrix4f createTranslateMatrix(float x, float y, float z) {
        Matrix4f matrix = new Matrix4f();
        matrix.m00 = 1.0F;
        matrix.m11 = 1.0F;
        matrix.m22 = 1.0F;
        matrix.m33 = 1.0F;
        matrix.m03 = x;
        matrix.m13 = y;
        matrix.m23 = z;
        return matrix;
    }

    public void translate(Matrix4f mat, float x, float y, float z) {
        mat.m03 += mat.m00 * x + mat.m01 * y + mat.m02 * z;
        mat.m13 += mat.m10 * x + mat.m11 * y + mat.m12 * z;
        mat.m23 += mat.m20 * x + mat.m21 * y + mat.m22 * z;
        mat.m33 += mat.m30 * x + mat.m31 * y + mat.m32 * z;
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
        var diff = Math.abs(previous) - Math.abs(current);
        if (diff > Math.PI) {
            previous = 0;
        } else if (diff < -Math.PI) {
            current = 0;
        }
        return (previous * (1.0F - partialTick)) + (current * partialTick);
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