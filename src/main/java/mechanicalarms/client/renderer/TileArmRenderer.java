package mechanicalarms.client.renderer;

import mechanicalarms.MechanicalArms;
import mechanicalarms.client.renderer.shaders.Shader;
import mechanicalarms.client.renderer.shaders.ShaderManager;
import mechanicalarms.client.renderer.util.ItemStackRenderToVAO;
import mechanicalarms.client.renderer.util.Quaternion;
import mechanicalarms.common.tile.TileArmBasic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.opengl.*;

import javax.vecmath.Matrix4f;
import javax.vecmath.Tuple4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;


public class TileArmRenderer extends TileEntitySpecialRenderer<TileArmBasic> {

    public static final Shader base_vao = ShaderManager.loadShader(new ResourceLocation(MechanicalArms.MODID, "shaders/arm_shader")).withUniforms(ShaderManager.LIGHTMAP).withUniforms();
    private final ResourceLocation armModelLocation = new ResourceLocation(MechanicalArms.MODID, "models/block/completearm.obj");
    protected static final FloatBuffer MODELVIEW_MATRIX_BUFFER = GLAllocation.createDirectFloatBuffer(16);
    protected static final FloatBuffer PROJECTION_MATRIX_BUFFER = GLAllocation.createDirectFloatBuffer(16);
    private static final Vector3f V3F_ZERO = new Vector3f();
    private static final int[][][] vertexArray = null;
    private final Matrix4f tempModelMatrix = new Matrix4f();
    private final Tuple4f vertexTransformingVec = new Vector4f();
    private final Vector3f V3F_POS = new Vector3f();
    private final Vector3f PIVOT_1 = new Vector3f(.5F, 1 + 7 / 16F, .5F);
    private final Vector3f ANTI_PIVOT_1 = new Vector3f(-.5F, -(1 + 7 / 16F), -.5F);
    private final Vector3f PIVOT_2 = new Vector3f(0.5F, 1 + 7 / 16F, .5F);
    private final Vector3f ANTI_PIVOT_2 = new Vector3f(-0.5F, -(1 + 7 / 16F), -.5F);
    private final Vector3f PIVOT_3 = new Vector3f(0.5F, 1 + 7 / 16F, 0);
    private final Vector3f ANTI_PIVOT_3 = new Vector3f(-0.5F, -(1 + 7 / 16F), 0);
    int totalInstances = 1000;
    Field isShadowField = null;
    Matrix4f transformMatrix = new Matrix4f();
    Quaternion rot = Quaternion.createIdentity();
    FloatBuffer fb = GLAllocation.createDirectFloatBuffer(16 * totalInstances);
    Matrix4f mat;
    Vao vao;

    InstanceableModel item;
    private ItemStackRenderToVAO vao2;

    public TileArmRenderer() {
        super();
    }

    @Override
    public void renderTileEntityFast(TileArmBasic tileArmBasic, double x, double y, double z, float partialTicks, int destroyStage, float partial, BufferBuilder buffer) {

        if (vao == null) {
            vao = new Vao(armModelLocation);
        }

        // Get the current model view matrix and store it in the buffer
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MODELVIEW_MATRIX_BUFFER);

        // Get the current projection matrix and store it in the buffer
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, PROJECTION_MATRIX_BUFFER);


        //renderFirstArm(tileArmBasic, x, y, z, partialTicks);
        renderItemInArm(tileArmBasic, x, y, z, partialTicks);
    }

    void renderFirstArm(TileArmBasic tileArmBasic, double x, double y, double z, float partialTicks) {
        // Get the current model view matrix and store it in the buffer
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MODELVIEW_MATRIX_BUFFER);

        // Get the current projection matrix and store it in the buffer
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, PROJECTION_MATRIX_BUFFER);

        float[] firstArmCurrRot = tileArmBasic.getRotation(0);
        float[] firstArmPrevRot = tileArmBasic.getAnimationRotation(0);

        rot.setIndentity();
        transformMatrix.setIdentity();
        Quaternion rot = Quaternion.createIdentity();
        translate(transformMatrix, (float) x, (float) y + 2, (float) z);


//        rot.rotateY((float) (-Math.PI/2));
//        rot.rotateY(lerp(firstArmPrevRot[1], firstArmCurrRot[1], partialTicks));
//        rot.rotateX(lerp(firstArmPrevRot[0], firstArmCurrRot[0], partialTicks));
//        Quaternion.rotateMatrix(transformMatrix, rot);

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

        GL30.glBindVertexArray(vao.getVertexArrayBuffer());

        for (int i = 0; i < 1; i++) {
            fb.position(0);
            fb.put(fa, 0, 16);
        }
        fb.rewind();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vao.getModelTransformBuffer());
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, fb, GL15.GL_STATIC_DRAW);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vao.getBlockLightBuffer());
        ByteBuffer byteBuffer = GLAllocation.createDirectByteBuffer(2);
        Chunk c = tileArmBasic.getWorld().getChunk(tileArmBasic.getPos());
        int s = c.getLightFor(EnumSkyBlock.SKY, tileArmBasic.getPos());
        int b = c.getLightFor(EnumSkyBlock.BLOCK, tileArmBasic.getPos());

        byteBuffer.put((byte) s);
        byteBuffer.put((byte) b);
        byteBuffer.rewind();

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, byteBuffer, GL15.GL_DYNAMIC_DRAW);

        int projectionLoc = GL20.glGetUniformLocation(base_vao.getShaderId(), "projection");
        int viewLoc = GL20.glGetUniformLocation(base_vao.getShaderId(), "view");

        GL20.glUniformMatrix4(projectionLoc, false, PROJECTION_MATRIX_BUFFER);
        GL20.glUniformMatrix4(viewLoc, false, MODELVIEW_MATRIX_BUFFER);

        int t = vao.getTexGl();
        if (t == 0) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        } else {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, t);
        }

        GL31.glDrawArraysInstanced(GL11.GL_TRIANGLES, 0, vao.getVertexCount(), 1);

        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        base_vao.release();
    }
    void renderItemInArm(TileArmBasic tileArmBasic, double x, double y, double z, float partialTicks) {
        InstanceRender ir = InstanceRender.INSTANCE;
        if (item == null) {
            item = new ItemStackRenderToVAO(new ItemStack(Blocks.CACTUS));
        }

        ir.schedule(item);

        float[] firstArmCurrRot = tileArmBasic.getRotation(0);
        float[] firstArmPrevRot = tileArmBasic.getAnimationRotation(0);

        rot.setIndentity();
        transformMatrix.setIdentity();
        Quaternion rot = Quaternion.createIdentity();
        translate(transformMatrix, (float) x, (float) y + 2, (float) z);


//        rot.rotateY((float) (-Math.PI/2));
//        rot.rotateY(lerp(firstArmPrevRot[1], firstArmCurrRot[1], partialTicks));
//        rot.rotateX(lerp(firstArmPrevRot[0], firstArmCurrRot[0], partialTicks));
//        Quaternion.rotateMatrix(transformMatrix, rot);

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

        GL30.glBindVertexArray(item.getVertexArrayBuffer());

        for (int i = 0; i < 1; i++) {
            fb.position(0);
            fb.put(fa, 0, 16);
        }
        fb.rewind();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, item.getModelTransformBuffer());
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, fb, GL15.GL_STATIC_DRAW);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, item.getBlockLightBuffer());
        ByteBuffer byteBuffer = GLAllocation.createDirectByteBuffer(2);
        Chunk c = tileArmBasic.getWorld().getChunk(tileArmBasic.getPos());
        int s = c.getLightFor(EnumSkyBlock.SKY, tileArmBasic.getPos());
        int b = c.getLightFor(EnumSkyBlock.BLOCK, tileArmBasic.getPos());

        byteBuffer.put((byte) s);
        byteBuffer.put((byte) b);
        byteBuffer.rewind();

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, byteBuffer, GL15.GL_DYNAMIC_DRAW);

        int projectionLoc = GL20.glGetUniformLocation(base_vao.getShaderId(), "projection");
        int viewLoc = GL20.glGetUniformLocation(base_vao.getShaderId(), "view");

        GL20.glUniformMatrix4(projectionLoc, false, PROJECTION_MATRIX_BUFFER);
        GL20.glUniformMatrix4(viewLoc, false, MODELVIEW_MATRIX_BUFFER);

        int t = item.getTexGl();
        if (t == 0) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        } else {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, t);
        }

        GL31.glDrawArraysInstanced(GL11.GL_TRIANGLES, 0, item.getVertexCount(), 1);

        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        base_vao.release();
    }

    @Override
    public void render(TileArmBasic tileArmBasic, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        int currentProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if (vao == null) {
            vao = new Vao(armModelLocation);
        }

        renderFirstArm(tileArmBasic, x, y, z, partialTicks);
        GL11.glPopAttrib();

        GL20.glUseProgram(currentProgram);
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