package mechanicalarms.client.renderer;

import mechanicalarms.MechanicalArms;
import mechanicalarms.client.renderer.util.ItemStackRenderToVAO;
import mechanicalarms.client.renderer.util.Quaternion;
import mechanicalarms.common.proxy.ClientProxy;
import mechanicalarms.common.tile.TileArmBasic;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.nio.FloatBuffer;


public class TileArmRenderer extends TileEntitySpecialRenderer<TileArmBasic> {
    InstanceRender ir = InstanceRender.INSTANCE;

    float[] mtx = new float[16];
    float[] mtx2 = new float[16];

    Matrix4f baseMotorMatrix = new Matrix4f();



    Vao base;
    private final Matrix4f tempModelMatrix = new Matrix4f();
    Matrix4f transformMatrix = new Matrix4f();
    Matrix4f translationMatrix = new Matrix4f();
    byte s;
    byte b;
    Quaternion rot = Quaternion.createIdentity();

    float partialTicks;

    InstanceableModel item;
    private ItemStackRenderToVAO vao2;
    private Vao baseMotor;

    public TileArmRenderer() {
        super();
    }

    @Override
    public void renderTileEntityFast(TileArmBasic tileArmBasic, double x, double y, double z, float partialTicks, int destroyStage, float partial, BufferBuilder buffer) {

        Chunk c = tileArmBasic.getWorld().getChunk(tileArmBasic.getPos());
        s = (byte) c.getLightFor(EnumSkyBlock.SKY, tileArmBasic.getPos());
        b = (byte) c.getLightFor(EnumSkyBlock.BLOCK, tileArmBasic.getPos());
        this.partialTicks = partialTicks;

        translationMatrix.setIdentity();
        translate(translationMatrix, (float) x + 0.5f, (float) y, (float) z + 0.5f);

        renderBase();
        renderBaseMotor(tileArmBasic);
        //renderPart(tileArmBasic, x, y, z, partialTicks, transformMatrix);
    }

    void renderBase() {
        if (base == null) {
            base = new Vao(ClientProxy.base);
        }
        ir.schedule(base);
        matrix4ftofloatarray(translationMatrix, mtx);
        ir.bufferModelMatrixData(mtx);
        ir.bufferLight(s, b);
    }

    void renderBaseMotor(TileArmBasic tileArmBasic) {
        if (baseMotor == null) {
            baseMotor = new Vao(ClientProxy.baseMotor);
        }
        ir.schedule(baseMotor);


        //upload model matrix, light

        float[] firstArmCurrRot = tileArmBasic.getRotation(0);
        float[] firstArmPrevRot = tileArmBasic.getAnimationRotation(0);

        baseMotorMatrix.setIdentity();
        rot.setIndentity();

        translate(baseMotorMatrix, new Vector3f(0.1f,0f,0.0f));
        rot.rotateY(lerp(firstArmPrevRot[1], firstArmCurrRot[1], partialTicks));
        Quaternion.rotateMatrix(baseMotorMatrix, rot);
        translate(translationMatrix, new Vector3f(-0.1f,0f,0f));


        baseMotorMatrix.mul(translationMatrix);

        matrix4ftofloatarray(baseMotorMatrix, mtx);
        ir.bufferModelMatrixData(mtx);
        ir.bufferLight(s, b);
    }

    void matrix4ftofloatarray(Matrix4f matrix4f, float[] floats) {
        floats[0] = matrix4f.m00;
        floats[1] = matrix4f.m10;
        floats[2] = matrix4f.m20;
        floats[3] = matrix4f.m30;
        floats[4] = matrix4f.m01;
        floats[5] = matrix4f.m11;
        floats[6] = matrix4f.m21;
        floats[7] = matrix4f.m31;
        floats[8] = matrix4f.m02;
        floats[9] = matrix4f.m12;
        floats[10] = matrix4f.m22;
        floats[11] = matrix4f.m32;
        floats[12] = matrix4f.m03;
        floats[13] = matrix4f.m13;
        floats[14] = matrix4f.m23;
        floats[15] = matrix4f.m33;
    }

    void renderPart(TileArmBasic tileArmBasic, double x, double y, double z, float partialTicks, Matrix4f parentModelMatrix) {
        InstanceRender ir = InstanceRender.INSTANCE;
        if (item == null) {
            item = new Vao(new ResourceLocation(MechanicalArms.MODID, "models/block/completearm.obj"));
        }

        ir.schedule(item);

        //upload model matrix, light

        float[] firstArmCurrRot = tileArmBasic.getRotation(0);
        float[] firstArmPrevRot = tileArmBasic.getAnimationRotation(0);

        rot.setIndentity();
        Quaternion rot = Quaternion.createIdentity();


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

        ir.bufferModelMatrixData(fa);

        Chunk c = tileArmBasic.getWorld().getChunk(tileArmBasic.getPos());
        int s = c.getLightFor(EnumSkyBlock.SKY, tileArmBasic.getPos());
        int b = c.getLightFor(EnumSkyBlock.BLOCK, tileArmBasic.getPos());

        ir.bufferLight((byte) s, (byte) b);

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